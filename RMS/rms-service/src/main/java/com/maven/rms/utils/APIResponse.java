package com.maven.rms.utils;

import java.time.LocalDateTime;
import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.models.payload.responses.ApiResponseHeader;


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

    public static <T> ResponseEntity<ApiResponse<T>> NoDataFound(ControllersEnum controllersEnum) {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();

        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("01");
        header.setMessage(ErrorCode.NO_DATA_FOUND.getMessage() + " (" + controllersEnum.getValue() + ")");

        response.setHeader(header);
        response.setData((T) Collections.emptyList()); // or Collections.emptyList() based on your need

        return ResponseEntity.status(HttpStatus.OK).body(response);
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

    public static <T> ResponseEntity<ApiResponse<T>> InvalidFormat(String errorMessage) {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();
    
        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("02");
        header.setMessage(errorMessage); // Pass the detailed error message
    
        response.setHeader(header);
        response.setData((T) Collections.emptyList());
    
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

    // public static <T> ResponseEntity<ApiResponse<T>> InternalServerError() {
    // ApiResponse<T> response = new ApiResponse<>();
    // ApiResponseHeader header = new ApiResponseHeader();

    // header.setRequestTimestamp(LocalDateTime.now());
    // header.setResponseTimestamp(LocalDateTime.now());
    // header.setStatusCode("99");
    // // header.setErrorNumber(ErrorCode.INTERNAL_SERVER_ERROR.getErrorNumber());
    // header.setMessage(new
    // ErrorMessage(null).getMessageExternalMessage(ErrorCode.INTERNAL_SERVER_ERROR));

    // response.setHeader(header);
    // response.setData((T) Collections.emptyList()); // or Collections.emptyList()
    // based on your need

    // return
    // ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    // }

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

    public static <T> ResponseEntity<ApiResponse<T>> SuccessResponseExternal(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();

        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("200");
        header.setMessage(SystemMessage.SUCCESS.getMessage());

        response.setHeader(header);
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> DuplicateDataExternal(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();

        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("402");
        header.setMessage(SystemMessage.DUPLICATE_DATA.getMessage());

        response.setHeader(header);
        response.setData(data);

        return ResponseEntity.ok(response);
    }

    public static <T> ResponseEntity<ApiResponse<T>> InternalServerErrorExternal(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();

        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("500");
        header.setMessage(SystemMessage.SERVERERROR.getMessage());

        response.setHeader(header);
        response.setData(data);
        // response.setData((T) Collections.emptyList()); // or Collections.emptyList()
        // based on your need

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> InternalServerErrorExternal() {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();

        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("500");
        header.setMessage(SystemMessage.SERVERERROR.getMessage());

        response.setHeader(header);
        // response.setData((T) Collections.emptyList()); // or Collections.emptyList()
        // based on your need

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    public static <T> ResponseEntity<ApiResponse<T>> InvalidFormatExternal() {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();

        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("400");
        header.setMessage(SystemMessage.INVALID_FORMAT.getMessage());

        response.setHeader(header);
        response.setData((T) Collections.emptyList()); // or Collections.emptyList() based on your need

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        // return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    public static <T> ResponseEntity<ApiResponse<T>> NoDataFoundExternal() {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();

        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("401");
        header.setMessage(SystemMessage.NO_DATA_FOUND.getMessage());

        response.setHeader(header);
        response.setData((T) Collections.emptyList()); // or Collections.emptyList() based on your need

        return ResponseEntity.ok(response);
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
        // return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    public static <T> ResponseEntity<ApiResponse<T>> DuplicateDataExternal() {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();

        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("402");
        header.setMessage(SystemMessage.DUPLICATE_DATA.getMessage());

        response.setHeader(header);
        response.setData((T) Collections.emptyList()); // or Collections.emptyList() based on your need

        return ResponseEntity.ok(response);
    }
    
    public static <T> ResponseEntity<ApiResponse<T>> SystemRuleViolationExternal() {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();

        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode("403");
        header.setMessage(SystemMessage.SYSTEM_RULE_VIOLATION.getMessage());

        response.setHeader(header);
        response.setData((T) Collections.emptyList()); // or Collections.emptyList() based on your need

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    public static ResponseEntity<ApiResponse<String>> ErrorResponse(String string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'ErrorResponse'");
    }
    public static <T> ResponseEntity<ApiResponse<T>> CustomErrorResponse(String message, String statusCode, HttpStatus httpStatus) {
        ApiResponse<T> response = new ApiResponse<>();
        ApiResponseHeader header = new ApiResponseHeader();
    
        header.setRequestTimestamp(LocalDateTime.now());
        header.setResponseTimestamp(LocalDateTime.now());
        header.setStatusCode(statusCode);
        header.setMessage(message);
    
        response.setHeader(header);
        response.setData((T) Collections.emptyList()); // or null, depending on your preference
    
        return ResponseEntity.status(httpStatus).body(response);
    }
    
}
