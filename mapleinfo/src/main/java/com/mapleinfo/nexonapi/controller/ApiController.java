package com.mapleinfo.nexonapi.controller;

import com.mapleinfo.nexonapi.dtos.NexonCharacterResponseDTO;
import com.mapleinfo.nexonapi.dtos.NexonEquipmentResponseDTO;
import com.mapleinfo.nexonapi.dtos.NexonLevelResponseDTO;
import com.mapleinfo.nexonapi.service.NexonApiService;
import com.mapleinfo.rank.service.CharacterSearchRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maple")
@RequiredArgsConstructor
public class ApiController {
    private final NexonApiService nexonApiService;
    private final CharacterSearchRankService rankService;

    @GetMapping("/character-all")
    public ResponseEntity<?> getCharacterAllData(@RequestParam("name") String nickName) {
        if (nickName == null || nickName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("캐릭터 이름을 입력해주세요.");
        }

        try {
            Map<String, Object> responseData = nexonApiService.getOcid(nickName)
                    .flatMap(ocid -> {
                        Mono<NexonCharacterResponseDTO> infoMono = nexonApiService.getCharacterInfo(ocid);
                        Mono<Object> statInfoMono = nexonApiService.getCharacterStat(ocid);
                        Mono<NexonEquipmentResponseDTO> equipmentInfoMono = nexonApiService.getCharacterEquipment(ocid);
                        Mono<List<NexonLevelResponseDTO>> levelInfoMono = nexonApiService.getCharacterLevel(ocid);

                        return Mono.zip(infoMono, statInfoMono, equipmentInfoMono, levelInfoMono)
                                .map(tuple -> {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("character", tuple.getT1());
                                    data.put("stat", tuple.getT2());
                                    data.put("equipment", tuple.getT3());
                                    data.put("level", tuple.getT4());
                                    return data;
                                });
                    })
                    .block();
            if (responseData == null) {
                return ResponseEntity.badRequest().body("존재하지 않거나 OCID를 조회할 수 없는 캐릭터입니다.");
            }
            Mono.fromRunnable(() -> rankService.incrementSearchCount(nickName))
                    .subscribeOn(Schedulers.boundedElastic())
                    .subscribe();

            return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("캐릭터 통합 정보를 가져오는 데 실패했습니다: " + e.getMessage());
        }
    }

}