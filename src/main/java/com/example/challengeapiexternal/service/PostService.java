package com.example.challengeapiexternal.service;

import com.example.challengeapiexternal.dto.ResponseDTO;
import com.example.challengeapiexternal.entity.Post;
import com.example.challengeapiexternal.entity.PostState;
import com.example.challengeapiexternal.repository.PostRepository;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public record PostService (PostRepository postRepository, HistoryService historyService, ExternalApiService externalApiService) {
    public ResponseDTO queryPosts(int pageNo, int pageSize) {
        return mapToPageableQueryPosts(PageRequest.of(pageNo, pageSize));
    }

    public Post validateProcessPost(long postId) {
        if (postId < 1 || postId > 100){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An error occurred while fetching post, resource not found: Post 1 - 100");
        }
        return processPost(postId);
    }

    public Post validateDisablePost(long postId) {
        if (!getPostByIdOrException(postId).getIsEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Disables a post that is in the ENABLED state."); }
        return disablePost(postId);
    }

    public Post validateReprocessPost(long postId){
        Post post = getPostByIdOrException(postId);
        return reprocessPost(post);
    }

    private Post processPost(long postId) {
        return postRepository.findById(postId)
                .orElseGet(() -> savePostInLocal(newPost(postId)));
    }

    private Post disablePost(long postId) {
        Post post = getPostByIdOrException(postId);
        historyService.saveStatusInHistory(post, PostState.DISABLED);
        post.setIsEnabled(false);
        return postRepository.save(post);
    }

    private Post reprocessPost(Post post) {
        historyService.saveStatusInHistory(post, PostState.UPDATING);
        return savePostInLocal(post);
    }

    private Post savePostInLocal(Post post) {
        return postRepository.save(fetchPost(post));
    }

    private Post getPostByIdOrException(long postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post not found/exist in database"));
    }

    private Post newPost(long postId) {
        Post post = new Post();
        post.setId(postId);
        historyService.saveStatusInHistory(post, PostState.CREATED);
        return post;
    }

    private Post fetchPost(Post post){
        try {
            historyService.saveStatusInHistory(post, PostState.POST_FIND);

            post.setTitle(externalApiService.fetchPostById(post.getId()).getTitle());
            post.setBody(externalApiService.fetchPostById(post.getId()).getBody());

            historyService.saveStatusInHistory(post, PostState.POST_OK);

        } catch (Exception e) {
            historyService.saveStatusInHistory(post, PostState.FAILED);
            historyService.saveStatusInHistory(post, PostState.DISABLED);
            post.setIsEnabled(false);
        }
        return fetchComments(post);
    }

    private Post fetchComments(Post post) {
        try {
            historyService.saveStatusInHistory(post, PostState.COMMENT_FIND);

            post.getComments().clear();
            post.getComments().addAll(externalApiService.fetchCommentsForPost(post.getId()));

            historyService.saveStatusInHistory(post, PostState.COMMENT_OK);
            historyService.saveStatusInHistory(post, PostState.ENABLED);
            post.setIsEnabled(true);
        }catch (Exception e){
            historyService.saveStatusInHistory(post, PostState.FAILED);
            historyService.saveStatusInHistory(post, PostState.DISABLED);
            post.setIsEnabled(false);
        }
        return post;
    }

    private ResponseDTO mapToPageableQueryPosts(Pageable pageable){
        Page<Post> posts = postRepository.findAll(pageable);
        return mapToResponseDTO(posts);
    }

    private ResponseDTO mapToResponseDTO(Page<Post> posts) {
        return new ResponseDTO(
                posts.getContent(),
                posts.getNumber(),
                posts.getSize(),
                posts.getTotalPages(),
                posts.getTotalElements(),
                posts.isLast()
        );
    }


}

