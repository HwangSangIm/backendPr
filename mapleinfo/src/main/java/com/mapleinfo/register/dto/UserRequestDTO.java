package com.mapleinfo.register.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserRequestDTO {
    private String id;
    private String password;
    private List<String> nickname;
}
