package com.example.challengeapiexternal.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Data
@Setter
@Getter

@Entity(name = "post")
public class Post {
    @Id
    private Long id;
    private String title;
    private String body;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name ="post_id")
    private List<Comment> comments = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name ="post_id")
    private List<History> history = new ArrayList<>();

    @JsonIgnore
    private Boolean isEnabled;

}
