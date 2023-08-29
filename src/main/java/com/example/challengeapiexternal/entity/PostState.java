package com.example.challengeapiexternal.entity;

import lombok.Getter;

@Getter
public enum PostState {
    CREATED("CREATED"),
    POST_FIND("POST_FIND"),
    POST_OK("POST_OK"),
    COMMENT_FIND("COMMENT_FIND"),
    COMMENT_OK("COMMENT_OK"),
    ENABLED("ENABLED"),
    UPDATING("UPDATING"),
    DISABLED("DISABLED"),
    FAILED("FAILED");

    private final String name;


    PostState(String name) {
        this.name = name;
    }


}
