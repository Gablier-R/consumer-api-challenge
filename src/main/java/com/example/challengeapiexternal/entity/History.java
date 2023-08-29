package com.example.challengeapiexternal.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
@NoArgsConstructor
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
        this.setFormattedTimestamp(LocalDateTime.now());
    }

    public void setFormattedTimestamp(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        this.timestamp = LocalDateTime.parse(dateTime.format(formatter), formatter);
    }

}
