package com.example.challengeapiexternal.service;

import com.example.challengeapiexternal.dto.ResponseDTO;
import com.example.challengeapiexternal.entity.History;
import com.example.challengeapiexternal.entity.Post;
import com.example.challengeapiexternal.entity.PostState;
import com.example.challengeapiexternal.repository.PostRepository;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public record PostService (PostRepository postRepository, HistoryService historyService, ExternalApiService externalApiService) {

    public Post validateProcessPost(long postId) {
        if (postId < 1 || postId > 100){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "An error occurred while fetching post, resource not found: Post 1 - 100");
        }
        return processPost(postId);
    }

    public Post validateDisablePost(Long postId) {
        Post post = getPostByIdOrException(postId);
        if (!post.getIsEnabled()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Disables a post that is in the ENABLED state."); }
        return disablePost(postId);
    }

    public Post validateReprocessPost(long postId){
        Post post = getPostByIdOrException(postId);
        historyService.saveStatusInHistory(post, PostState.UPDATING);
        return reprocessPost(post.getId());
    }

    public ResponseDTO queryPosts(int pageNo, int pageSize) {
        return mapToPageableQueryPosts(PageRequest.of(pageNo, pageSize));
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

    private Post reprocessPost(long postId) {
        Post reprocessPost = externalApiService.fetchPostById(postId);
        Post post = savePostInLocal(reprocessPost);
        historyService.deleteHistoriesWithNullPostId();
        return postRepository.save(post);
    }

    private Post getPostByIdOrException(long postId) {
         return postRepository.findById(postId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post not found/exist in database"));
    }

    private Post savePostInLocal(Post post) {
        try {
            fetchPost(post);
        } catch (Exception e) {
            historyService.saveStatusInHistory(post, PostState.FAILED);
            historyService.saveStatusInHistory(post, PostState.DISABLED);
            post.setIsEnabled(false);
        }
        return postRepository.save(post);
    }

    private Post newPost(long postId) {
        Post post = new Post();
        post.setId(postId);
        return post;
    }

    private void fetchPost(Post post){
        historyService.saveStatusInHistory(post, PostState.CREATED);
        historyService.saveStatusInHistory(post, PostState.POST_FIND);

        post.setTitle(externalApiService.fetchPostById(post.getId()).getTitle());
        post.setBody(externalApiService.fetchPostById(post.getId()).getBody());

        historyService.saveStatusInHistory(post, PostState.POST_OK);

        fetchComments(post);
    }

    private void fetchComments(Post post) {
        historyService.saveStatusInHistory(post, PostState.COMMENT_FIND);

        post.getComments().addAll(externalApiService.fetchCommentsForPost(post.getId()));

        historyService.saveStatusInHistory(post, PostState.COMMENT_OK);
        historyService.saveStatusInHistory(post, PostState.ENABLED);
        post.setIsEnabled(true);
    }

    private ResponseDTO mapToPageableQueryPosts( Pageable pageable){
        Page<Post> posts = postRepository.findAll(pageable);

        List<Post> listOfPost = posts.getContent();

        List<Post> content = new ArrayList<>(listOfPost);

        return mapToResponseDTO(posts, content);
    }

    private ResponseDTO mapToResponseDTO(Page<Post> posts, List<Post> post) {
        ResponseDTO postResponse = new ResponseDTO();
        postResponse.setPosts(post);
        postResponse.setPageNo(posts.getNumber());
        postResponse.setPageSize(posts.getSize());
        postResponse.setTotalElements(posts.getTotalElements());
        postResponse.setTotalPages(posts.getTotalPages());
        postResponse.setLast(posts.isLast());
        return postResponse;
    }

}

