package com.example.challengeapiexternal.controller;

import com.example.challengeapiexternal.dto.ResponseDTO;
import com.example.challengeapiexternal.entity.Post;
import com.example.challengeapiexternal.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static com.example.challengeapiexternal.utils.AppConstants.DEFAULT_PAGE_NUMBER;
import static com.example.challengeapiexternal.utils.AppConstants.DEFAULT_PAGE_SIZE;

@RestController
        @RequestMapping("posts")
public record PostController (PostService postService) {

    //http://localhost:8080/posts/{postId}
    @DeleteMapping("/{postId}")
    public ResponseEntity<Post> disablePost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.validateDisablePost(postId));
    }

    //http://localhost:8080/posts/{postId}
    @PutMapping("/{postId}")
    public ResponseEntity<Post> reprocessPost(@PathVariable Long postId) {
        return new ResponseEntity<>(postService.validateReprocessPost(postId), HttpStatus.OK);
    }

    //http://localhost:8080/posts?pageNo=0&pageSize=5
    @GetMapping
    public ResponseEntity<ResponseDTO> queryPosts(@RequestParam( defaultValue = DEFAULT_PAGE_NUMBER, required = false) int pageNo,
                                                  @RequestParam( defaultValue = DEFAULT_PAGE_SIZE, required = false) int pageSize){
        return ResponseEntity.ok(postService.queryPosts(pageNo, pageSize));
    }

    //http://localhost:8080/posts/{postId}
    @PostMapping("/{postId}")
    public ResponseEntity<Post> processPost(@PathVariable long postId) {
        return ResponseEntity.ok(postService.validateProcessPost(postId));
    }




}
