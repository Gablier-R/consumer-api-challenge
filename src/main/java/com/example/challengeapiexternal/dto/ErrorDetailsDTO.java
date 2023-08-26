package com.example.challengeapiexternal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErrorDetailsDTO {

    private Date timeStamp;
    private String message;
    private String details;

}
