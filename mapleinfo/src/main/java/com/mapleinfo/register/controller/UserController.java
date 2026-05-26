package com.mapleinfo.register.controller;

import com.mapleinfo.register.dto.LoginRequestDTO;
import com.mapleinfo.register.dto.LoginResponseDTO;
import com.mapleinfo.register.dto.UserRequestDTO;
import com.mapleinfo.register.entity.CharacterInfo;
import com.mapleinfo.register.entity.User;
import com.mapleinfo.register.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserRequestDTO requestDTO){
        try{
            userService.singUp(requestDTO);
            return ResponseEntity.ok("회원 가입이 완료되었습니다.");
        } catch (IllegalStateException | IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO requestDTO){
        try {
            User user = userService.login(requestDTO);
            List<String> nicknames = user.getCharacterInfoList().stream()
                    .map(CharacterInfo::getCharacterName)
                    .toList();
            LoginResponseDTO responseDTO = new LoginResponseDTO(user.getId(), nicknames);
            return ResponseEntity.ok(responseDTO);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
