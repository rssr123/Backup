package com.maven.rms.models.payload.responses;

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
    //// private String errorNumber;

    //// public String getErrorNumber() {
    ////     return errorNumber;
    //// }

    // public LocalDateTime getRequestTimestamp() {
    //     return requestTimestamp;
    // }

    // public void setRequestTimestamp(LocalDateTime requestTimestamp) {
    //     this.requestTimestamp = requestTimestamp;
    // }

    // public LocalDateTime getResponseTimestamp() {
    //     return responseTimestamp;
    // }

    // public void setResponseTimestamp(LocalDateTime responseTimestamp) {
    //     this.responseTimestamp = responseTimestamp;
    // }

    // public String getStatusCode() {
    //     return statusCode;
    // }

    // public void setStatusCode(String statusCode) {
    //     this.statusCode = statusCode;
    // }

    // public String getMessage() {
    //     return Message;
    // }

    // public void setMessage(String Message) {
    //     this.Message = Message;
    // }

    // public void setErrorNumber(String errorNumber) {
    //     this.errorNumber = errorNumber;
    // }
}
