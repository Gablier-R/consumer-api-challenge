package com.example.challengeapiexternal.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity(name = "comment")
public class Comment {
    @Id
    private Integer id;
    private String name;
    private String email;
    private String body;
}
