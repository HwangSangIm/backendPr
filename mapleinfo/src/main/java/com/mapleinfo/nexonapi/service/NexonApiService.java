package com.mapleinfo.nexonapi.service;

import com.mapleinfo.nexonapi.dtos.NexonCharacterResponseDTO;
import com.mapleinfo.nexonapi.dtos.NexonEquipmentResponseDTO;
import com.mapleinfo.nexonapi.dtos.NexonLevelResponseDTO;
import com.mapleinfo.nexonapi.dtos.NexonOcidResponseDTO;
import com.mapleinfo.nexonapi.entity.NexonLevelHistory;
import com.mapleinfo.nexonapi.repository.NexonApiRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class NexonApiService {

    @Value("${key}")
    private String apiKey;

    private WebClient webClient;

    // 1. 주입 누락 오류를 완전히 막기 위해 final 필드로 설정합니다.
    private final NexonApiRepository apiRepository;

    // 2. 명시적 생성자 주입으로 스프링 실행 시 apiRepository가 null이 되지 않도록 고정합니다.
    public NexonApiService(NexonApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl("https://open.api.nexon.com")
                .defaultHeader("x-nxopen-api-key", apiKey)
                .build();
    }

    public Mono<String> getOcid(String nickName){
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maplestory/v1/id")
                        .queryParam("character_name", nickName)
                        .build())
                .retrieve()
                .bodyToMono(NexonOcidResponseDTO.class)
                .map(NexonOcidResponseDTO::getOcid)
                .onErrorMap(e -> new RuntimeException("OCID 조회 중 오류 발생", e));
    }

    public Mono<NexonCharacterResponseDTO> getCharacterInfo(String ocid){
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maplestory/v1/character/basic")
                        .queryParam("ocid", ocid)
                        .build())
                .retrieve()
                .bodyToMono(NexonCharacterResponseDTO.class);
    }

    public Mono<Object> getCharacterStat(String ocid){
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maplestory/v1/character/stat")
                        .queryParam("ocid", ocid)
                        .build())
                .retrieve()
                .bodyToMono(Object.class);
    }

    public Mono<NexonEquipmentResponseDTO> getCharacterEquipment(String ocid){
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maplestory/v1/character/item-equipment")
                        .queryParam("ocid", ocid)
                        .build())
                .retrieve()
                .bodyToMono(NexonEquipmentResponseDTO.class)
                .delayElement(Duration.ofMillis(100))
                .retryWhen(Retry.fixedDelay(3, Duration.ofMillis(250))
                        .filter(throwable -> throwable instanceof Exception));
    }
    public Mono<List<NexonLevelResponseDTO>> getCharacterLevel(String ocid) {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();
        String todayStr = today.format(format);
        NexonLevelHistory history = apiRepository.findById(ocid).orElse(null);
        if (history != null && todayStr.equals(history.getLastUpdatedDate())) {
            List<NexonLevelResponseDTO> cachedList = new ArrayList<>();
            var savedList = history.getHistoryList();
            for (int i = 0; i < savedList.size(); i++) {
                String targetDate = (i == 0) ? null : today.minusDays(i).format(format);
                String displayDate = (i == 0) ? today.format(format) : targetDate;

                NexonLevelResponseDTO dto = new NexonLevelResponseDTO();
                dto.setDate(displayDate);
                dto.setCharacterLevel(savedList.get(i).getLevel());
                dto.setCharacterExp(savedList.get(i).getExp());
                dto.setCharacterExpRate(savedList.get(i).getExpRate());
                cachedList.add(dto);
            }
            return Mono.just(cachedList);
        }
        return Flux.range(0, 7)
                .delayElements(Duration.ofMillis(450))
                .flatMap(i -> {
                    String targetDate = (i == 0) ? null : today.minusDays(i).format(format);
                    String displayDate = (i == 0) ? today.format(format) : targetDate;
                    return this.webClient.get()
                            .uri(uriBuilder -> {
                                uriBuilder.path("/maplestory/v1/character/basic")
                                        .queryParam("ocid", ocid);
                                if (targetDate != null) uriBuilder.queryParam("date", targetDate);
                                return uriBuilder.build();
                            })
                            .retrieve()
                            .bodyToMono(NexonLevelResponseDTO.class)
                            .doOnNext(response -> response.setDate(displayDate));
                })
                .collectList()
                .doOnNext(list -> {
                    if (list != null && !list.isEmpty()) {
                        List<NexonLevelHistory.HistoryData> historyList = new ArrayList<>();
                        for (NexonLevelResponseDTO d : list) {
                            historyList.add(new NexonLevelHistory.HistoryData(
                                    d.getCharacterLevel(),
                                    d.getCharacterExp(),
                                    d.getCharacterExpRate()
                            ));
                        }
                        apiRepository.save(new NexonLevelHistory(ocid, todayStr, historyList));
                    }
                });
    }
}