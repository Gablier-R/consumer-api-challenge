package com.example.challengeapiexternal.controller;

import com.example.challengeapiexternal.dto.ResponseDTO;
import com.example.challengeapiexternal.entity.Post;
import com.example.challengeapiexternal.service.PostService;
import com.example.challengeapiexternal.utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("posts")
public class PostController {

    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Post> disablePost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.disablePost(postId));
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> reprocessPost(@PathVariable Long postId) {
        return new ResponseEntity<>(postService.reprocessPost(postId), HttpStatus.OK);
    }

    //http://localhost:8080/posts?pageNo=0&pageSize=5
    @GetMapping
    public ResponseEntity<ResponseDTO> queryPosts(@RequestParam( defaultValue = AppConstants.DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                  @RequestParam( defaultValue = AppConstants.DEFAULT_PAGE_SIZE, required = false) int pageSize){
        return ResponseEntity.ok(postService.queryPosts(pageNo, pageSize));
    }

    @PostMapping("/{postId}")
    public ResponseEntity<Post> getPost(@PathVariable long postId) {
        return ResponseEntity.ok(postService.processPost(postId));
    }




}
