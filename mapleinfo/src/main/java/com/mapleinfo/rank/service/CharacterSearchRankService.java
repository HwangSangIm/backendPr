package com.mapleinfo.rank.service;

import com.mapleinfo.rank.dto.SearchRankResponse;
import com.mapleinfo.rank.handler.LiveRankWebSocketHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CharacterSearchRankService {

    private final StringRedisTemplate redisTemplate;
    private final LiveRankWebSocketHandler webSocketHandler;

    private static final String KEY_PREFIX = "maple:search:minute:";
    private static final String RECENT_RANK_KEY = "maple:search:recent:1hour";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

    private static final List<String> DEFAULT_CHARACTERS = List.of(
            "아델", "강은호", "동원참치", "쨍알", "늙으음", "슈퍼스타", "쯔단", "에델", "StarK1ng", "불독혀노"
    );

    @EventListener(ApplicationReadyEvent.class)
    public void initializeRedisRepository() {
        String currentMinuteKey = KEY_PREFIX + LocalDateTime.now().format(TIME_FORMATTER);
        Boolean hasKey = redisTemplate.hasKey(currentMinuteKey);
        if (Boolean.FALSE.equals(hasKey)) {
            log.info("▶ Redis 실시간 랭킹 데이터가 비어있어 기본 캐릭터 10명을 0회로 초기화합니다.");
            DEFAULT_CHARACTERS.forEach(name -> {
                redisTemplate.opsForZSet().add(currentMinuteKey, name, 0);
            });
            redisTemplate.expire(currentMinuteKey, 60, TimeUnit.MINUTES);

            updateAndBroadcastRank();
        }
    }
    public void incrementSearchCount(String characterName) {
        if (characterName == null || characterName.trim().isEmpty()) return;

        String currentMinuteKey = KEY_PREFIX + LocalDateTime.now().format(TIME_FORMATTER);
        String cleanedName = characterName.trim();

        redisTemplate.opsForZSet().incrementScore(currentMinuteKey, cleanedName, 1);
        redisTemplate.expire(currentMinuteKey, 65, TimeUnit.MINUTES);

        updateAndBroadcastRank();
    }

    @Scheduled(cron = "0 * * * * *")
    public void scheduledRankUpdate() {
        updateAndBroadcastRank();
    }

    public void updateAndBroadcastRank() {
        List<String> last60MinutesKeys = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 60; i++) {
            last60MinutesKeys.add(KEY_PREFIX + now.minusMinutes(i).format(TIME_FORMATTER));
        }

        if (!last60MinutesKeys.isEmpty()) {
            String firstKey = last60MinutesKeys.get(0);
            List<String> otherKeys = last60MinutesKeys.subList(1, last60MinutesKeys.size());
            redisTemplate.opsForZSet().unionAndStore(firstKey, otherKeys, RECENT_RANK_KEY);
            redisTemplate.expire(RECENT_RANK_KEY, 2, TimeUnit.HOURS);
        }
        webSocketHandler.broadcastRankList(getTopSearchRank(15));
    }

    public List<SearchRankResponse> getTopSearchRank(int limit) {
        Set<ZSetOperations.TypedTuple<String>> typedTuples =
                redisTemplate.opsForZSet().reverseRangeWithScores(RECENT_RANK_KEY, 0, limit - 1);

        if (typedTuples == null || typedTuples.isEmpty()) {
            return DEFAULT_CHARACTERS.stream()
                    .limit(limit)
                    .map(name -> new SearchRankResponse(name, 0))
                    .collect(Collectors.toList());
        }

        return typedTuples.stream()
                .map(tuple -> new SearchRankResponse(tuple.getValue(), tuple.getScore().intValue()))
                .collect(Collectors.toList());
    }
}