package com.mapleinfo.nexonapi.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NexonLevelResponseDTO {
    private String date;

    @JsonProperty("character_level")
    private int characterLevel;

    @JsonProperty("character_exp")
    private Long characterExp;

    @JsonProperty("character_exp_rate")
    private String characterExpRate;
}
