package com.mapleinfo.nexonapi.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NexonLevelResponseDTO {
    private String date;

    @JsonProperty("character_level")
    private int characterLevel;

    @JsonProperty("character_exp")
    private Long characterExp;

    @JsonProperty("character_exp_rate")
    private String characterExpRate;
}
