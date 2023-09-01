package com.example.challengeapiexternal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Setter
@Getter
@Entity(name = "history")
public class History {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private LocalDateTime timestamp;
    private PostState status;

    public History(PostState status){
        this.setStatus(status);
        this.setTimestamp(LocalDateTime.now());
    }

    public History(){
        this.setTimestamp(LocalDateTime.now());
    }

}
