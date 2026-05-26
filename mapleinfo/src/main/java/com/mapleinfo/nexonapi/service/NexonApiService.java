package com.mapleinfo.nexonapi.service;

import com.mapleinfo.nexonapi.DTO.NexonCharacterResponseDTO;
import com.mapleinfo.nexonapi.DTO.NexonEquipmentResponseDTO;
import com.mapleinfo.nexonapi.DTO.NexonLevelResponseDTO;
import com.mapleinfo.nexonapi.DTO.NexonOcidResponseDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class NexonApiService {

    @Value("${key}")
    private String apiKey;

    private WebClient webClient;

    public NexonApiService(){
    }

    @PostConstruct
    public void init() {
        this.webClient = WebClient.builder()
                .baseUrl("https://open.api.nexon.com")
                .defaultHeader("x-nxopen-api-key", apiKey)
                .build();
    }

    public String getOcid(String nickName){
        try{
            NexonOcidResponseDTO response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/maplestory/v1/id")
                            .queryParam("character_name", nickName)
                            .build())
                    .retrieve()
                    .bodyToMono(NexonOcidResponseDTO.class)
                    .block();
            return response != null ? response.getOcid() : null;
        } catch (Exception e){
            throw new RuntimeException("OCID 조회 중 오류 발생");
        }
    }

    public NexonCharacterResponseDTO getCharacterInfo(String ocid){
        return  webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maplestory/v1/character/basic")
                        .queryParam("ocid",ocid)
                        .build())
                .retrieve()
                .bodyToMono(NexonCharacterResponseDTO.class)
                .block();
    }



    public Object getCharacterStat(String ocid){
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maplestory/v1/character/stat")
                        .queryParam("ocid", ocid)
                        .build())
                .retrieve()
                .bodyToMono(Object.class)
                .block();
    }

    public NexonEquipmentResponseDTO getCharacterEquipment(String ocid){
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/maplestory/v1/character/item-equipment")
                        .queryParam("ocid", ocid)
                        .build())
                .retrieve()
                .bodyToMono(NexonEquipmentResponseDTO.class)
                .delayElement(Duration.ofMillis(100))
                .retryWhen(Retry.fixedDelay(3 , Duration.ofMillis(250))
                        .filter(throwable -> throwable instanceof Exception))
                .block();
    }

    public List<NexonLevelResponseDTO> getCharacterLevel(String ocid){
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate today = LocalDate.now();

        return Flux.range(0, 7)
                .delayElements(Duration.ofMillis(250))
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
                .block();
        }
    }

