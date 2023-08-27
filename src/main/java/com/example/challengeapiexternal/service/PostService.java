package com.example.challengeapiexternal.service;

import com.example.challengeapiexternal.dto.ResponseDTO;
import com.example.challengeapiexternal.entity.History;
import com.example.challengeapiexternal.entity.Post;
import com.example.challengeapiexternal.entity.PostState;
import com.example.challengeapiexternal.repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

//    public Post validatePost(long postId){
//
//        if (postId > 100)
//        {
//            throw new NotFoundExceptionExternal("Post", "id", postId);
//        }
//        else if (postRepository.findById(postId).isPresent())
//        {
//            throw new NotFoundException("Post", "id", postId);
//        }
//
//        return processPost(postId);
//    }

    public Post processPost(long postId) {
        return postRepository.findById(postId)
                .orElseGet(() -> savePostInLocal(postId, postAlreadyExists(postId)));
    }

    public ResponseDTO queryPosts(int pageNo, int pageSize) {
        return mapToPageableQueryPosts(PageRequest.of(pageNo, pageSize));
    }

    public Post disablePost(long postId) {
        Post post = getPostByIdOrException(postId);

        historyService.saveStatusInHistory(post, PostState.DISABLED);
        return postRepository.save(post);
    }

    public Post reprocessPost(long postId) {
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
        return postRepository.findById(postId).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Post not found/exist in database"
                )
        );
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

    private Post postAlreadyExists(long postId) {
        Post post = new Post();
        post.setId(postId);
        historyService.saveStatusInHistory(post, PostState.CREATED);
        return post;
    }

    private void fetchPost(long postId, Post post){
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

