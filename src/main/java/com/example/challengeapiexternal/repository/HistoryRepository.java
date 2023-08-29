package com.example.challengeapiexternal.repository;

import com.example.challengeapiexternal.entity.History;
import com.example.challengeapiexternal.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {}
