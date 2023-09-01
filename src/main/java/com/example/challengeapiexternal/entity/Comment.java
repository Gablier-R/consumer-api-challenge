package com.example.challengeapiexternal.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "comment")
public class Comment {
    @Id
    private Integer id;
    @Column(columnDefinition = "TEXT")
    private String body;
}
