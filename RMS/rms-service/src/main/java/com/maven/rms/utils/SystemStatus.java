package com.maven.rms.utils;

public enum SystemStatus {
    Active("A"),
    Deleted("D"),
    Inactive("D");

    private final String message;

    SystemStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return message;
    }
}
