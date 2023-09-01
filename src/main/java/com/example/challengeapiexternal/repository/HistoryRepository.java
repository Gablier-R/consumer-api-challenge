package com.example.challengeapiexternal.repository;

import com.example.challengeapiexternal.entity.History;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {}
