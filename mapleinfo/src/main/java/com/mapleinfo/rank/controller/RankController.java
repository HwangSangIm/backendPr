package com.mapleinfo.rank.controller;

import com.mapleinfo.rank.dto.SearchRankResponse;
import com.mapleinfo.rank.service.CharacterSearchRankService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rank")
@RequiredArgsConstructor
public class RankController {

    private final CharacterSearchRankService rankService;
    @GetMapping("/current")
    public List<SearchRankResponse> getCurrentRank() {
        return rankService.getTopSearchRank(15);
    }
}