package com.maven.rms.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    // GENERAL_ERROR ("System error", "999"),
    // DEMO_INVALID_KEYWORD_LENGTH("Keyword is too long, size=%s", "001"),
    // COM_GENERAL_ERROR("Keyword is too long, size=%s", "001"),

    // INVALID_FORMAT("Invalid Format", "400"),
    // INTERNAL_SERVER_ERROR("Internal Server Error", "500"),
    // NO_DATA_FOUND("No Data Found", "200");

    GENERAL_ERROR("System error"),
    DEMO_INVALID_KEYWORD_LENGTH("Keyword is too long, size=%s"),
    COM_GENERAL_ERROR("Keyword is too long, size=%s"),

    INVALID_FORMAT("Invalid Format"),
    INTERNAL_SERVER_ERROR("Internal Server Error"),
    NO_DATA_FOUND("No Data Found");

    // private final String message;
    // private final String errorNumber;

    private final String message;
    // private final String errorNumber;
}
