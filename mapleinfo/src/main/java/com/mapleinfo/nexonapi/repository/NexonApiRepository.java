package com.mapleinfo.nexonapi.repository;

import com.mapleinfo.nexonapi.entity.NexonLevelHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NexonApiRepository extends JpaRepository<NexonLevelHistory, String> {

}