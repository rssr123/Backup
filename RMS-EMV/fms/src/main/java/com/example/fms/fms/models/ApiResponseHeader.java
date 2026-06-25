package com.example.fms.fms.models;


import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponseHeader {
    private LocalDateTime requestTimestamp;
    private LocalDateTime responseTimestamp;
    private String statusCode;
    private String Message;

}
