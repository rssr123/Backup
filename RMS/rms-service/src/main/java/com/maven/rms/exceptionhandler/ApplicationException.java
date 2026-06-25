package com.maven.rms.exceptionhandler;

import java.util.IllegalFormatException;

import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.maven.rms.utils.ErrorCode;

@RestControllerAdvice
public class ApplicationException extends Exception {
    private ErrorCode errorCode;

    public String[] getParams() {
        return params;
    }

    private String[] params;

    // 240806- Code added by Wei Ern based on the Code Review section 2.2
    public ApplicationException() {
    }

    public ApplicationException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ApplicationException(ErrorCode errorCode, String... params) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.params = params;
    }

    public ApplicationException(Throwable cause, ErrorCode errorCode, String... params) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.params = params;
    }

    @Override
    public String getMessage() {
        String baseMessage = super.getMessage();
        if (params != null) {
            try {
                // Cast to Object[] so String.format treats the array as varargs
                baseMessage = String.format(baseMessage, (Object[]) params);
            } catch (IllegalFormatException e) {
                // Fallback to the unformatted base message if formatting fails
                baseMessage = baseMessage + " (formatting error)";
            }
        }

        return baseMessage;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
