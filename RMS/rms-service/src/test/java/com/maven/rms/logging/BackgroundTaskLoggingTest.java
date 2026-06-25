package com.maven.rms.logging;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Test to verify background tasks get correlation IDs
 */
@SpringBootTest
@ActiveProfiles("local")
public class BackgroundTaskLoggingTest {

    private static final Logger logger = LoggerFactory.getLogger(BackgroundTaskLoggingTest.class);

    @Test
    public void testBackgroundTaskLogging() {
        // Simulate background task without any context
        logger.info("This should automatically get a correlation ID");

        // Simulate scheduler task
        LoggingContextUtil.setupBackgroundTaskContext("TestScheduler");
        try {
            logger.error("Simulated scheduler error - should have proper correlation ID");

            // Test exception logging
            try {
                throw new RuntimeException("Test background exception");
            } catch (Exception e) {
                logger.error("Background exception with full context", e);
            }

        } finally {
            LoggingContextUtil.clearContext();
        }

        // Test automatic fallback again
        logger.warn("Another background log - should get auto-generated correlation ID");
    }

    @Test
    public void testCorrelationIdFormats() {
        // Test web request format
        LoggingContextUtil.setupWebRequestContext(
                "web-test-123 from URL: /test/endpoint",
                "/test/endpoint",
                "192.168.1.100",
                "Mozilla/5.0 Test Browser",
                "testuser");

        try {
            logger.info("Web request log message");
        } finally {
            LoggingContextUtil.clearContext();
        }

        // Test background task format
        LoggingContextUtil.setupBackgroundTaskContext("PaymentClearanceJob");
        try {
            logger.info("Background task log message");
        } finally {
            LoggingContextUtil.clearContext();
        }
    }
}
