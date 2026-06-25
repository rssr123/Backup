package com.maven.rms.exceptionhandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.maven.rms.config.RMSProperties;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.models.payload.responses.ApiResponseHeader;
import com.maven.rms.utils.ErrorCode;
import com.maven.rms.utils.ModelEnum;
import com.maven.rms.utils.SystemMessage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalControllerExceptionHandler {

    private final ErrorMessage errorMessage;

    @Autowired
    private RMSProperties apiEndpointConfig;

    private static final String PREFIX_SYSTEM_MSG = "Immediate Action needed, Unhandled ";
    private static final String DEFAULT_ERROR_STATUS = "99";
    private static final String VALIDATION_ERROR_STATUS = "02";
    private static final String CLIENT_ERROR_STATUS = "400";
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("through reference chain: ([\\w\\.]+)\\[.*\\]");

    @ExceptionHandler(ApplicationException.class)
    public <T> ResponseEntity<ApiResponse<T>> handleApplicationException(ApplicationException e) {
        log.error("Business Flow Exception", e);

        String className = extractClassNameEnhanced(e);
        ModelEnum model = resolveModelEnumSafely(className);

        ApiResponseHeader header = createResponseHeader(DEFAULT_ERROR_STATUS,
                errorMessage.getMessageExternalMessage(e.getErrorCode(), model));

        ApiResponse<T> response = createErrorResponse(header);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public <T> ResponseEntity<ApiResponse<T>> handleException(Exception e) {
        String className = extractClassNameEnhanced(e);
        boolean isRuntime = e instanceof RuntimeException;

        log.error(PREFIX_SYSTEM_MSG + (isRuntime ? "RuntimeException" : "Exception") +
                ": class name > " + className, e);

        String message = getGenericErrorMessage(className);
        ApiResponseHeader header = createResponseHeader(DEFAULT_ERROR_STATUS, message);

        ApiResponse<T> response = createErrorResponse(header);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public <T> ResponseEntity<ApiResponse<T>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e) {

        try {
            log.error(PREFIX_SYSTEM_MSG + "MethodArgumentNotValidException", e);

            String statusCode = determineStatusCode();
            logValidationErrors(e);

            ApiResponseHeader header = createResponseHeader(statusCode, SystemMessage.INVALID_FORMAT.getMessage());
            ApiResponse<T> response = createErrorResponse(header);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);

        } catch (Exception ex) {
            log.error(PREFIX_SYSTEM_MSG + "MethodArgumentNotValidException in Catch", ex);
            return handleValidationExceptionFallback(e);
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public <T> ResponseEntity<ApiResponse<T>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e) throws IOException {

        Throwable mostSpecificCause = e.getMostSpecificCause();

        if (mostSpecificCause instanceof InvalidFormatException) {
            log.error(PREFIX_SYSTEM_MSG + "InvalidFormatException", e);
            return handleInvalidFormatException((InvalidFormatException) mostSpecificCause);
        }

        log.error(PREFIX_SYSTEM_MSG + "HttpMessageNotReadableException", e);
        String className = extractClassName(e.getMessage());
        String message = getGenericErrorMessage(className);

        ApiResponseHeader header = createResponseHeader(DEFAULT_ERROR_STATUS, message);
        ApiResponse<T> response = createErrorResponse(header);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(InvalidFormatException.class)
    public <T> ResponseEntity<ApiResponse<T>> handleInvalidFormatException(InvalidFormatException e) {
        log.error(PREFIX_SYSTEM_MSG + "InvalidFormatException", e);

        String className = extractClassName(e.getMessage());
        String statusCode = determineStatusCode();
        ModelEnum model = resolveModelEnumSafely(className);
        String message = errorMessage.getMessageExternalMessage(ErrorCode.INVALID_FORMAT, model);

        ApiResponseHeader header = createResponseHeader(statusCode, message);
        ApiResponse<T> response = createErrorResponse(header);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Helper methods for better code organization
    private ApiResponseHeader createResponseHeader(String statusCode, String message) {
        ApiResponseHeader header = new ApiResponseHeader();
        LocalDateTime now = LocalDateTime.now();
        header.setRequestTimestamp(now);
        header.setResponseTimestamp(now);
        header.setStatusCode(statusCode);
        header.setMessage(message);
        return header;
    }

    private <T> ApiResponse<T> createErrorResponse(ApiResponseHeader header) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setHeader(header);
        response.setData((T) Collections.emptyList());
        return response;
    }

    private String extractClassName(String errorMessage) {
        if (errorMessage == null || errorMessage.isEmpty()) {
            return "";
        }

        Matcher matcher = CLASS_NAME_PATTERN.matcher(errorMessage);
        if (matcher.find()) {
            String fullClassName = matcher.group(1);
            return fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
        }
        return "";
    }

    /**
     * Enhanced class name extraction that captures more context like the original
     * logging
     */
    private String extractClassNameEnhanced(Exception e) {
        if (e == null) {
            return "UnknownException";
        }

        // First, try the original method for JSON parsing errors
        String originalClassName = extractClassName(e.getMessage());
        if (!originalClassName.isEmpty()) {
            return originalClassName;
        }

        // Get the actual exception class name
        String exceptionClassName = e.getClass().getSimpleName();

        // Try to get the source class from stack trace (like original %C pattern)
        if (e.getStackTrace() != null && e.getStackTrace().length > 0) {
            StackTraceElement firstElement = e.getStackTrace()[0];
            String sourceClassName = firstElement.getClassName();
            String sourceMethodName = firstElement.getMethodName();
            int lineNumber = firstElement.getLineNumber();

            // Extract just the class name (not package)
            String simpleSourceClassName = sourceClassName.substring(sourceClassName.lastIndexOf('.') + 1);

            // Return enhanced format: ExceptionType@SourceClass.method:line
            return String.format("%s@%s.%s:%d",
                    exceptionClassName, simpleSourceClassName, sourceMethodName, lineNumber);
        }

        // Fallback to just exception class name
        return exceptionClassName;
    }

    private ModelEnum resolveModelEnumSafely(String className) {
        if (className == null || className.isEmpty()) {
            return ModelEnum.NOT_FOUND;
        }

        try {
            return ModelEnum.valueOf(className.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException e) {
            log.debug("Could not resolve ModelEnum for className: {}", className);
            return ModelEnum.NOT_FOUND;
        }
    }

    private String getGenericErrorMessage(String className) {
        try {
            ModelEnum model = resolveModelEnumSafely(className);
            return errorMessage.getMessageExternalMessage(ErrorCode.COM_GENERAL_ERROR, model);
        } catch (Exception ex) {
            log.warn("Failed to get error message for className: {}", className, ex);
            return errorMessage.getMessageExternalMessage(ErrorCode.COM_GENERAL_ERROR, ModelEnum.NOT_FOUND);
        }
    }

    private String determineStatusCode() {
        try {
            HttpServletRequest request = getCurrentRequest();
            String url = request.getRequestURI();
            List<String> apiEndpoints = apiEndpointConfig.getApiEndpoints();
            return apiEndpoints.contains(url) ? CLIENT_ERROR_STATUS : VALIDATION_ERROR_STATUS;
        } catch (Exception e) {
            log.warn("Could not determine status code, using default", e);
            return VALIDATION_ERROR_STATUS;
        }
    }

    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes();
        return attributes.getRequest();
    }

    private void logValidationErrors(MethodArgumentNotValidException e) {
        e.getBindingResult().getFieldErrors()
                .forEach(error -> log.error("Field error in object '{}', on field '{}': {}",
                        error.getObjectName(), error.getField(), error.getDefaultMessage()));
    }

    private <T> ResponseEntity<ApiResponse<T>> handleValidationExceptionFallback(MethodArgumentNotValidException e) {
        String className = extractClassNameEnhanced(e);
        String message = getGenericErrorMessage(className);

        ApiResponseHeader header = createResponseHeader(VALIDATION_ERROR_STATUS, message);
        ApiResponse<T> response = createErrorResponse(header);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}