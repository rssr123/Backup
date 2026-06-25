package com.example.fms.fms.utils;

public enum SystemMessage {
    SUCCESS("Success"),
    SERVERERROR("Server Error"),
    RETRIEVED_SUCCESSFULLY("Retrieved Successfully"),
    NO_DATA_FOUND("No data found"),
    INVALID_FORMAT("Invalid Format"),
    INTERNAL_SERVER_ERROR("Internal Server Error"),
    RECORD_IN_USED("Record In Used"),
    NO_PERMISSION("User has no permission"),
    DUPLICATE_DATA("Duplicate Data");

    private final String message;

    SystemMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
