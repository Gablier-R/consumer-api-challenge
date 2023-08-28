package com.example.challengeapiexternal.service;

import com.example.challengeapiexternal.dto.ResponseDTO;
import com.example.challengeapiexternal.entity.History;
import com.example.challengeapiexternal.entity.Post;
import com.example.challengeapiexternal.entity.PostState;
import com.example.challengeapiexternal.repository.PostRepository;
import org.modelmapper.ValidationException;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public record PostService (PostRepository postRepository, HistoryService historyService, ExternalApiService externalApiService) {
    public Post validateprocessPost(long postId) {
        if (postId < 0 || postId > 100){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "An error occurred while fetching post, resource not found: Post 1 - 100");
        }
        return processPost(postId);
    }

    public Post validateDisablePost(Long postId) {
        Post post = getPostByIdOrException(postId);

        if (!post.getIsEnabled()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Disables a post that is in the ENABLED state.");
        }
        return disablePost(postId) ;
    }

    public Post validatereprocessPost(long postId){
        Post post = getPostByIdOrException(postId);

        return reprocessPost(post.getId());
    }

    public ResponseDTO queryPosts(int pageNo, int pageSize) {
        return mapToPageableQueryPosts(PageRequest.of(pageNo, pageSize));
    }



//    public Post processPost(long postId) {
//
//        Optional<Post> post = postRepository.findById(postId);
//
//        if (post.isEmpty()) {
//            return savePostInLocal(postId, postAlreadyNotExists(postId));
//        }
//        return post.get();
//    }

    public Post processPost(long postId) {
        return postRepository.findById(postId)
                .orElseGet(() -> savePostInLocal(postId, postAlreadyNotExists(postId)));
    }

    private Post disablePost(long postId) {
        Post post = getPostByIdOrException(postId);
        historyService.saveStatusInHistory(post, PostState.DISABLED);
        return postRepository.save(post);
    }

    private Post reprocessPost(long postId) {
        Post post = getPostByIdOrException(postId);
        try {
            historyService.saveStatusInHistory(post, PostState.UPDATING);
            externalApiService.fetchPostById(postId);

            post.setReprocessed(true);

        } catch (Exception e) {
            historyService.saveStatusInHistory(post, PostState.FAILED);
            historyService.saveStatusInHistory(post, PostState.DISABLED);
        }
        return postRepository.save(post);
    }

    //Mets for help main mets

    private Post getPostByIdOrException(long postId) {
         return postRepository.findById(postId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Post not found/exist in database"));
    }

    private Post savePostInLocal(long postId, Post post) {
        try {
            fetchPost(postId, post);
        } catch (Exception e) {
            historyService.saveStatusInHistory(post, PostState.FAILED);
            historyService.saveStatusInHistory(post, PostState.DISABLED);
        }
        return postRepository.save(post);
    }

    private Post postAlreadyNotExists(long postId) {
        Post post = new Post();
        post.setId(postId);
        return post;
    }

    private void fetchPost(long postId, Post post){
        historyService.saveStatusInHistory(post, PostState.CREATED);
        historyService.saveStatusInHistory(post, PostState.POST_FIND);

        post.setTitle(externalApiService.fetchPostById(postId).getTitle());
        post.setBody(externalApiService.fetchPostById(postId).getBody());

        historyService.saveStatusInHistory(post, PostState.POST_OK);

        fetchComments(post, postId);
    }

    private void fetchComments(Post post, long postId) {
        historyService.saveStatusInHistory(post, PostState.COMMENT_FIND);

        post.getComments().addAll(externalApiService.fetchCommentsForPost(postId));

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

