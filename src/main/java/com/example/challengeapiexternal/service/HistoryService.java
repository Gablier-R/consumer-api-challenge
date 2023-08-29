package com.example.challengeapiexternal.service;

import com.example.challengeapiexternal.entity.History;
import com.example.challengeapiexternal.entity.Post;
import com.example.challengeapiexternal.entity.PostState;
import com.example.challengeapiexternal.repository.HistoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public record HistoryService(HistoryRepository historyRepository) {
    public void saveStatusInHistory(Post post, PostState status){
        post.getHistory().add(new History(status));
    }
}
