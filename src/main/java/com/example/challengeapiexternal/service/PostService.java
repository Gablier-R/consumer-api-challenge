package com.example.challengeapiexternal.service;

import com.example.challengeapiexternal.dto.ResponseDTO;
import com.example.challengeapiexternal.entity.Post;
import com.example.challengeapiexternal.entity.PostState;
import com.example.challengeapiexternal.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private PostRepository postRepository;
    private final HistoryService historyService;

    private ExternalApiService externalApiService;

    @Autowired

    public PostService(PostRepository postRepository, HistoryService historyService, ExternalApiService externalApiService) {
        this.postRepository = postRepository;
        this.historyService = historyService;
        this.externalApiService = externalApiService;
    }

    public Optional<Post> getPostById(Long postId) {
        return postRepository.findById(postId);
    }

    public Post processPost(long postId) {
        return postRepository.findById(postId)
                .orElseGet(() -> {
                    var post = postAlreadyExists(postId);
            return savePostInLocal(postId, post);
                });
    }
    public ResponseDTO queryPosts(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        return mapToQueryPosts(pageable);
    }

    private ResponseDTO mapToQueryPosts( Pageable pageable){
        Page<Post> postsPage = postRepository.findAll(pageable);

        List<Post> content = postsPage.getContent();
        int currentPage = postsPage.getNumber();
        int pageSize = postsPage.getSize();
        long totalElements = postsPage.getTotalElements();
        int totalPages = postsPage.getTotalPages();
        boolean isLast = postsPage.isLast();

        return new ResponseDTO(content, pageSize, currentPage, totalPages,  totalElements,  isLast);
    }

    public void disablePost(Long postId) {
    }

    public Post reprocessPost(Long postId) {

        return null;
    }




    //Mets for help main mets
    public Post savePostInLocal(long postId, Post post) {
        try {
            fetchPost(postId, post);
            fetchComments(post, postId);
        } catch (Exception e) {
            historyService.saveStatusInHistory(post, PostState.FAILED);
            historyService.saveStatusInHistory(post, PostState.DISABLED);
        }
        return postRepository.save(post);
    }

    private Post postAlreadyExists(long postId) {
        Post post = new Post();
        post.setId(postId);
        historyService.saveStatusInHistory(post, PostState.CREATED);
        return post;
    }

    private void fetchPost(long postId, Post post){
        historyService.saveStatusInHistory(post, PostState.POST_FIND);
        historyService.saveStatusInHistory(post, PostState.POST_OK);

        post.setTitle(externalApiService.fetchPostById(postId).getTitle());
        post.setBody(externalApiService.fetchPostById(postId).getBody());
    }

    private void fetchComments(Post post, long postId) {
        historyService.saveStatusInHistory(post, PostState.COMMENT_FIND);

        post.getComments().addAll(externalApiService.fetchCommentsForPost(postId));

        historyService.saveStatusInHistory(post, PostState.COMMENT_OK);
        historyService.saveStatusInHistory(post, PostState.ENABLED);
    }

}
