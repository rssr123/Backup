package com.maven.rms.utils;

import java.sql.SQLException;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for handling database lock retries
 * Specifically designed to handle Informix ISAM lock errors and connection
 * issues
 */
@Component
@Slf4j
public class DatabaseRetryUtils {

    // Default retry configuration
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final long DEFAULT_BASE_DELAY_MS = 100;
    private static final long DEFAULT_MAX_DELAY_MS = 2000;

    /**
     * Execute a database operation with retry logic for lock errors
     * 
     * @param operation     The database operation to execute
     * @param operationName Name of the operation for logging
     * @return Result of the operation
     * @throws Exception If operation fails after all retries
     */
    public <T> T executeWithRetry(Supplier<T> operation, String operationName) throws Exception {
        return executeWithRetry(operation, operationName, DEFAULT_MAX_RETRIES, DEFAULT_BASE_DELAY_MS,
                DEFAULT_MAX_DELAY_MS);
    }

    /**
     * Execute a database operation with custom retry configuration
     * 
     * @param operation     The database operation to execute
     * @param operationName Name of the operation for logging
     * @param maxRetries    Maximum number of retry attempts
     * @param baseDelayMs   Base delay between retries in milliseconds
     * @param maxDelayMs    Maximum delay between retries in milliseconds
     * @return Result of the operation
     * @throws Exception If operation fails after all retries
     */
    public <T> T executeWithRetry(Supplier<T> operation, String operationName,
            int maxRetries, long baseDelayMs, long maxDelayMs) throws Exception {

        Exception lastException = null;

        for (int attempt = 0; attempt <= maxRetries; attempt++) {
            try {
                log.debug("Executing {} - Attempt {}/{}", operationName, attempt + 1, maxRetries + 1);
                return operation.get();

            } catch (Exception e) {
                lastException = e;

                // Check if this is a retryable error (lock or connection error)
                if (isRetryableError(e) && attempt < maxRetries) {
                    long delay = calculateDelay(attempt, baseDelayMs, maxDelayMs);

                    log.warn("Database error detected in {} - Attempt {}/{}. Retrying in {}ms. Error: {}",
                            operationName, attempt + 1, maxRetries + 1, delay, e.getMessage());

                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted during retry delay", ie);
                    }
                } else {
                    // Either not a retryable error or max retries reached
                    if (isRetryableError(e)) {
                        log.error("Database error in {} - Max retries ({}) exceeded. Giving up.",
                                operationName, maxRetries);
                    }
                    throw e;
                }
            }
        }

        // This should never happen, but just in case
        if (lastException != null) {
            throw lastException;
        } else {
            throw new RuntimeException("Operation failed after " + maxRetries + " retries with no recorded exception");
        }
    }

    /**
     * Check if the exception is a retryable database error (locks, connection
     * issues, etc.)
     */
    private boolean isRetryableError(Exception e) {
        if (e == null)
            return false;

        String message = e.getMessage();
        if (message == null)
            message = "";
        message = message.toLowerCase();

        // Informix lock error patterns
        boolean isLockError = message.contains("isam error") && message.contains("record is locked") ||
                message.contains("lock timeout") ||
                message.contains("deadlock") ||
                message.contains("record locked") ||
                message.contains("table locked") ||
                // Generic SQL lock errors
                message.contains("lock wait timeout") ||
                message.contains("resource busy");

        // Informix connection/read errors
        boolean isConnectionError = message.contains("could not read next physical row") ||
                message.contains("connection lost") ||
                message.contains("connection closed") ||
                message.contains("connection reset") ||
                message.contains("network error") ||
                message.contains("socket closed") ||
                message.contains("connection timeout") ||
                message.contains("broken pipe") ||
                message.contains("connection refused");

        // Check SQL state codes for retryable errors
        boolean isSqlStateRetryable = (e instanceof SQLException &&
                isRetryableSqlState(((SQLException) e).getSQLState()));

        return isLockError || isConnectionError || isSqlStateRetryable;
    }

    /**
     * Check if SQL state indicates a retryable error
     */
    private boolean isRetryableSqlState(String sqlState) {
        if (sqlState == null)
            return false;

        // Common SQL state codes for retryable errors
        return sqlState.equals("40001") || // Serialization failure (deadlock)
                sqlState.equals("40P01") || // Deadlock detected (PostgreSQL)
                sqlState.equals("HY000") || // General error (could be lock timeout)
                sqlState.startsWith("IX"); // Informix specific codes
    }

    /**
     * Calculate delay with exponential backoff and jitter
     */
    private long calculateDelay(int attempt, long baseDelayMs, long maxDelayMs) {
        // Exponential backoff: baseDelay * 2^attempt
        long delay = baseDelayMs * (1L << attempt);

        // Cap at maximum delay
        delay = Math.min(delay, maxDelayMs);

        // Add jitter (random variation) to prevent thundering herd
        // Jitter is ±25% of the calculated delay
        long jitter = (long) (delay * 0.25 * (ThreadLocalRandom.current().nextDouble() - 0.5) * 2);
        delay += jitter;

        // Ensure delay is positive
        return Math.max(delay, 10);
    }

    /**
     * Execute a void operation with retry logic
     */
    public void executeVoidWithRetry(Runnable operation, String operationName) throws Exception {
        executeWithRetry(() -> {
            operation.run();
            return null;
        }, operationName);
    }

    /**
     * Check if current thread should continue retrying based on interrupt status
     */
    public boolean shouldContinueRetrying() {
        return !Thread.currentThread().isInterrupted();
    }

    /**
     * Log retry statistics for monitoring
     */
    public void logRetryStatistics(String operationName, int attemptsMade, long totalTimeMs) {
        if (attemptsMade > 1) {
            log.info("Operation '{}' completed after {} attempts in {}ms",
                    operationName, attemptsMade, totalTimeMs);
        }
    }
}
