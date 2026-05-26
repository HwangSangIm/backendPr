package com.mapleinfo.nexonapi.controller;

import com.mapleinfo.nexonapi.DTO.NexonCharacterResponseDTO;
import com.mapleinfo.nexonapi.DTO.NexonEquipmentResponseDTO;
import com.mapleinfo.nexonapi.DTO.NexonLevelResponseDTO;
import com.mapleinfo.nexonapi.service.NexonApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/maple")
@RequiredArgsConstructor
public class ApiController {
    private final NexonApiService nexonApiService;

    // 💡 [통합 엔드포인트] 기존 4개의 GetMapping을 하나로 묶어 프론트엔드로 한방에 보냅니다.
    @GetMapping("/character-all")
    public ResponseEntity<?> getCharacterAllData(@RequestParam("name") String nickName) {
        if (nickName == null || nickName.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("캐릭터 이름을 입력해주세요.");
        }

        try {
            String ocid = nexonApiService.getOcid(nickName);
            if (ocid == null) {
                return ResponseEntity.badRequest().body("존재하지 않거나 OCID를 조회할 수 없는 캐릭터입니다.");
            }
            NexonCharacterResponseDTO info = nexonApiService.getCharacterInfo(ocid);
            Object statInfo = nexonApiService.getCharacterStat(ocid);
            NexonEquipmentResponseDTO equipmentInfo = nexonApiService.getCharacterEquipment(ocid);
            List<NexonLevelResponseDTO> levelInfo = nexonApiService.getCharacterLevel(ocid);

            // 3. 한 장의 묶음(Map)으로 포장합니다.
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("character", info);
            responseData.put("stat", statInfo);
            responseData.put("equipment", equipmentInfo);
            responseData.put("level", levelInfo);

            return ResponseEntity.ok(responseData);

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("캐릭터 통합 정보를 가져오는 데 실패했습니다: " + e.getMessage());
        }
    }
}