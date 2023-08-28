package com.example.challengeapiexternal.dto;

import java.time.LocalDateTime;
import java.util.List;

public record  ErrorDetailsDTO(
        LocalDateTime timestamp,
        List<String> message
)
{
    public ErrorDetailsDTO(List<String> message) {
        this(LocalDateTime.now(), message);
    }
}
