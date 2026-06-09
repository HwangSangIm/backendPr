package com.mapleinfo.rank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchRankResponse {
    private String characterName;
    private int searchCount;
}