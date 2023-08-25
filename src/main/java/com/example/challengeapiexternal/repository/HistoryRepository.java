package com.example.challengeapiexternal.repository;

import com.example.challengeapiexternal.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryRepository extends JpaRepository<History, Long> {
}
