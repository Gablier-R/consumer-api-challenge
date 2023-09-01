package com.example.challengeapiexternal.dto;

import com.example.challengeapiexternal.entity.Post;
import java.util.List;

public record ResponseDTO(
        List<Post> posts,
        int pageNo,
        int pageSize,
        int totalPages,
        long totalElements,
        boolean last
) {}
