package com.mapleinfo.register.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class LoginResponseDTO {
    private String id;
    private List<String> nickname;
    private String token;
}
