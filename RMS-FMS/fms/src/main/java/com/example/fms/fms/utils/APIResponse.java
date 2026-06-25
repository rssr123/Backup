package com.example.fms.fms.utils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.example.fms.fms.models.payload.responses.ApiResponse;
import com.example.fms.fms.models.payload.responses.ApiResponseHeader;

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

    public static <T> ResponseEntity<ApiResponse<T>> NoDataFound() {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();

        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("01");
        header.setMessage(SystemMessage.NO_DATA_FOUND.getMessage());

        response.setHeader(header);
        response.setData((T) Collections.emptyList()); // or Collections.emptyList() based on your need

        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> InvalidFormat() {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();

        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("02");
        header.setMessage(SystemMessage.INVALID_FORMAT.getMessage());

        response.setHeader(header);
        response.setData((T) Collections.emptyList()); // or Collections.emptyList() based on your need

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
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

    public static <T> ResponseEntity<ApiResponse<T>> RecordInUsed(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();

        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("03");
        header.setMessage(SystemMessage.RECORD_IN_USED.getMessage());

        response.setHeader(header);
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> DuplicateData() {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();

        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("04");
        header.setMessage(SystemMessage.DUPLICATE_DATA.getMessage());

        response.setHeader(header);
        response.setData((T) Collections.emptyList()); // or Collections.emptyList() based on your need

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // public static <T> ResponseEntity<ApiResponse<T>> SuccessResponseExternal(T data) {
    //     ApiResponse<T> response = new ApiResponse<>();
    //     ApiResponseHeader header = new ApiResponseHeader();

    //     header.setRequestTimestamp(LocalDateTime.now());
    //     header.setResponseTimestamp(LocalDateTime.now());
    //     header.setStatusCode("200");
    //     header.setMessage(SystemMessage.SUCCESS.getMessage());

    //     response.setHeader(header);
    //     response.setData(data);

    //     return ResponseEntity.ok(response);
    // }

    public static ResponseEntity<Map<String, Object>> SuccessResponseExternal(Map<String, Object> data) {
        return ResponseEntity.ok(data);
    }
    
    // public static <T> ResponseEntity<ApiResponse<T>> InternalServerErrorExternal() {
    //     ApiResponse<T> response = new ApiResponse<>();
    //     ApiResponseHeader header = new ApiResponseHeader();

    //     header.setRequestTimestamp(LocalDateTime.now());
    //     header.setResponseTimestamp(LocalDateTime.now());
    //     header.setStatusCode("500");
    //     header.setMessage(SystemMessage.SERVERERROR.getMessage());

    //     response.setHeader(header);
    //     //response.setData((T) Collections.emptyList()); // or Collections.emptyList() based on your need

    //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    //     //return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    // }

    public static ResponseEntity<Map<String, Object>> InternalServerErrorExternal(Map<String, Object> data) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(data);
    }

    // public static <T> ResponseEntity<ApiResponse<T>> InvalidFormatExternal() {
    //     ApiResponse<T> response = new ApiResponse<>();
    //     ApiResponseHeader header = new ApiResponseHeader();

    //     header.setRequestTimestamp(LocalDateTime.now());
    //     header.setResponseTimestamp(LocalDateTime.now());
    //     header.setStatusCode("400");
    //     header.setMessage(SystemMessage.INVALID_FORMAT.getMessage());

    //     response.setHeader(header);
    //     response.setData((T) Collections.emptyList()); // or Collections.emptyList() based on your need

    //     return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    //     //return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    // }

    public static ResponseEntity<Map<String, Object>> InvalidFormatExternal(Map<String, Object> data) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(data);
    }

    public static <T> ResponseEntity<ApiResponse<T>> NoPermission(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();

        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("200");
        header.setMessage(SystemMessage.NO_PERMISSION.getMessage());

        response.setHeader(header); // or Collections.emptyList() based on your need
        response.setData(data);

        return ResponseEntity.ok(response);
        //return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}
