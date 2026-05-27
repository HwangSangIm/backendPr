package com.mapleinfo.nexonapi.controller;

import com.mapleinfo.nexonapi.dtos.NexonCharacterResponseDTO;
import com.mapleinfo.nexonapi.dtos.NexonEquipmentResponseDTO;
import com.mapleinfo.nexonapi.dtos.NexonLevelResponseDTO;
import com.mapleinfo.nexonapi.service.NexonApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maple")
@RequiredArgsConstructor
public class ApiController {
    private final NexonApiService nexonApiService;

    @GetMapping("/character-all")
    public Mono<ResponseEntity<?>> getCharacterAllData(@RequestParam("name") String nickName) {
        if (nickName == null || nickName.trim().isEmpty()) {
            return Mono.just(ResponseEntity.badRequest().body("캐릭터 이름을 입력해주세요."));
        }
        return nexonApiService.getOcid(nickName)
                .flatMap(ocid -> {
                    Mono<NexonCharacterResponseDTO> infoMono = nexonApiService.getCharacterInfo(ocid);
                    Mono<Object> statInfoMono = nexonApiService.getCharacterStat(ocid);
                    Mono<NexonEquipmentResponseDTO> equipmentInfoMono = nexonApiService.getCharacterEquipment(ocid);
                    Mono<List<NexonLevelResponseDTO>> levelInfoMono = nexonApiService.getCharacterLevel(ocid);
                    return Mono.zip(infoMono, statInfoMono, equipmentInfoMono, levelInfoMono)
                            .map(tuple -> {
                                Map<String, Object> responseData = new HashMap<>();
                                responseData.put("character", tuple.getT1());
                                responseData.put("stat", tuple.getT2());
                                responseData.put("equipment", tuple.getT3());
                                responseData.put("level", tuple.getT4());
                                return ResponseEntity.ok((Object)responseData);
                            });
                })
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().<Object>body("존재하지 않거나 OCID를 조회할 수 없는 캐릭터입니다.")))
                .onErrorResume(e -> Mono.just(ResponseEntity.internalServerError()
                        .body("캐릭터 통합 정보를 가져오는 데 실패했습니다: " + e.getMessage())))
                .map(response -> (ResponseEntity<?>) response);
    }
}