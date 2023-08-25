package com.example.challengeapiexternal.repository;

import com.example.challengeapiexternal.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {


}
