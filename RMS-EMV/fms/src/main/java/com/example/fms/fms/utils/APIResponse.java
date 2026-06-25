package com.example.fms.fms.utils;

import java.time.LocalDateTime;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.fms.fms.models.ApiResponse;
import com.example.fms.fms.models.ApiResponseHeader;

public class APIResponse {
            public static <T> ResponseEntity<ApiResponse<T>> SuccessResponse(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();

        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("00");
        header.setMessage(SystemMessage.RETRIEVED_SUCCESSFULLY.getMessage());
        response.setHeader(header);
        response.setData(data);

        return ResponseEntity.ok(response);
    }

        public static <T> ResponseEntity<ApiResponse<T>> InternalServerError() {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();

        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("99");
        header.setMessage(SystemMessage.INTERNAL_SERVER_ERROR.getMessage());

        response.setHeader(header);
        response.setData((T) Collections.emptyList()); // or Collections.emptyList() based on your need

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
