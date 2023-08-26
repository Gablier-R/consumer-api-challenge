package com.example.challengeapiexternal.service;

import com.example.challengeapiexternal.entity.Comment;
import com.example.challengeapiexternal.entity.Post;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Arrays;
import java.util.Objects;

@Service
public class ExternalApiService {
    private static final String EXTERNAL_API_URL = "https://jsonplaceholder.typicode.com/posts/";

    public Post fetchPostById(Long postId) {
        String postUrl = EXTERNAL_API_URL + postId;
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Post> response = restTemplate.getForEntity(postUrl, Post.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new ResponseStatusException(response.getStatusCode(), "Error fetching post");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching post", e);
        }
    }

    public List<Comment> fetchCommentsForPost(Long postId) {
        String commentsUrl = EXTERNAL_API_URL + postId + "/comments";
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<Comment[]> response = restTemplate.getForEntity(commentsUrl, Comment[].class);
            if (response.getStatusCode() == HttpStatus.OK) {
                Comment[] comments = response.getBody();
                if (comments != null) {
                    return Arrays.asList(Objects.requireNonNull(response.getBody()));
                } else {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Empty response body");
                }
            } else {
                throw new ResponseStatusException(response.getStatusCode(), "Error fetching comments");
            }
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching comments", e);
        }
    }


}
