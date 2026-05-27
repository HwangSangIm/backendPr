package com.mapleinfo.nexonapi.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NexonCharacterResponseDTO {
    @JsonProperty("character_name")
    private String characterName;

    @JsonProperty("world_name")
    private String worldName;

    @JsonProperty("character_class")
    private String characterClass;

    @JsonProperty("character_level")
    private String characterLevel;

    @JsonProperty("character_exp_rate")
    private String characterExpRate;

    @JsonProperty("character_image")
    private String characterImage;

    @JsonProperty("character_guild_name")
    private String characterGuildName;
}
