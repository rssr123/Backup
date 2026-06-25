package com.maven.rms.utils;

public enum SystemMessage {
    SUCCESS("Success"),
    SERVERERROR("Server Error"),
    RETRIEVED_SUCCESSFULLY("Retrieved Successfully"),
    NO_DATA_FOUND("No Data Found"),
    INVALID_FORMAT("Invalid Format"),
    INTERNAL_SERVER_ERROR("Internal Server Error"),
    RECORD_IN_USED("Record In Used"),
    NO_PERMISSION("User has no permission"),
    DUPLICATE_DATA("Duplicated Data Found"),
    SYSTEM_RULE_VIOLATION("System Rule Violation");

    private final String message;

    SystemMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
