package com.example.challengeapiexternal.service;

import com.example.challengeapiexternal.entity.Comment;
import com.example.challengeapiexternal.entity.Post;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import java.util.List;
import java.util.Arrays;
import java.util.Objects;

@Service
public class ExternalApiService {
    private static final String EXTERNAL_API_URL = "https://jsonplaceholder.typicode.com/posts/";

    public Post fetchPostById(Long postId) {
        String postUrl = EXTERNAL_API_URL + postId;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Post> response = restTemplate.getForEntity(postUrl, Post.class);
        return response.getBody();
    }

    public List<Comment> fetchCommentsForPost(Long postId) {
        String commentsUrl = EXTERNAL_API_URL + postId + "/comments";
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Comment[]> response = restTemplate.getForEntity(commentsUrl, Comment[].class);
        return Arrays.asList(Objects.requireNonNull(response.getBody()));
    }


}
