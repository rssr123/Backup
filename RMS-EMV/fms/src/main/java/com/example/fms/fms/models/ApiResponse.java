package com.example.fms.fms.models;


import java.time.LocalDateTime;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {

    private ApiResponseHeader header;
    private T data;

    // public ApiResponseHeader getHeader() {
    //     return header;
    // }

    // public void setHeader(ApiResponseHeader header) {
    //     this.header = header;
    // }

    // public T getData() {
    //     return data;
    // }

    // public void setData(T data) {
    //     this.data = data;
    // }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ApiResponse[");
        sb.append("header=").append(header); // This will call the toString() of the T type
        sb.append("data=").append(data); // This will call the toString() of the T type
        sb.append("]");
        return sb.toString();
    }


}

