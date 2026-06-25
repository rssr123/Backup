package com.maven.rms.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test to verify that our logging regression captures all required context
 * compared to the original log4j.xml configuration
 */
@SpringBootTest
@ActiveProfiles("local")
public class LoggingRegressionTest {

    private static final Logger logger = LoggerFactory.getLogger(LoggingRegressionTest.class);

    @Test
    public void testLoggingContextCapture() {
        // Set up context exactly like original log4j.xml patterns

        // MDC values for %X{} patterns - UUID only in correlationId, URL in apiPath
        MDC.put("correlationId", "test-correlation-12345");
        MDC.put("apiPath", "/rmsrest/notifications");

        // ThreadContext values for %K{} patterns
        ThreadContext.put("client_ip", "192.168.1.100");
        ThreadContext.put("client_browser", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) Chrome/91.0");
        ThreadContext.put("login_nm", "testuser");

        try {
            // Test error logging - should match original format exactly
            logger.error("Test error log entry with original XML format");

            // Test with exception to verify enhanced class name extraction
            try {
                throw new RuntimeException("Test exception for logging regression");
            } catch (Exception e) {
                logger.error("Test error with exception - should show proper stack trace format", e);
            }

            logger.info("Test info log entry - should capture all context like original");

        } finally {
            MDC.clear();
            ThreadContext.clearAll();
        }
    }

    @Test
    public void testBackgroundTaskLogging() {
        Logger logger = LoggerFactory.getLogger(LoggingRegressionTest.class);
        logger.info("Testing background task logging format:");

        // Simulate background task logging
        LoggingContextUtil.setupBackgroundTaskContext("TestTask");

        try {
            logger.info("Background task started - should have UUID and task info in remark");
            logger.warn("Background task warning - testing format");

            try {
                throw new RuntimeException("Background task exception");
            } catch (Exception e) {
                logger.error("Background task error - should show proper correlation ID format", e);
            }

        } finally {
            LoggingContextUtil.clearContext();
        }
    }
}
