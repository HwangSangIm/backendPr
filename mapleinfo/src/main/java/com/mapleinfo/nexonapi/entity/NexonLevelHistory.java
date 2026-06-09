package com.mapleinfo.nexonapi.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NexonLevelHistory {
    @Id
    private String ocid;
    private String lastUpdatedDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "character_history_days", joinColumns = @JoinColumn(name = "ocid"))
    private List<HistoryData> historyList = new ArrayList<>();

    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryData {
        private int level;
        private long exp;
        private String expRate;
    }
}