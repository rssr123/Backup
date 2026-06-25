package com.maven.rms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Database Connection Tester
 * Tests database connectivity at application startup
 */
@Component
@Slf4j
public class DatabaseConnectionTester {

    private final DataSource dataSource;

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String username;

    public DatabaseConnectionTester(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void testDatabaseConnection() {
        log.info("Testing database connection...");
        log.info("Database URL: {}", databaseUrl);
        log.info("Username: {}", username);

        try (Connection connection = dataSource.getConnection()) {
            log.info("✅ Database connection successful!");
            log.info("Connection URL: {}", connection.getMetaData().getURL());
            log.info("Database Product: {}", connection.getMetaData().getDatabaseProductName());
            log.info("Database Version: {}", connection.getMetaData().getDatabaseProductVersion());

            // Test a simple query
            try (Statement stmt = connection.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as cnt FROM systables WHERE tabid = 1")) {
                if (rs.next()) {
                    log.info("✅ Database query test successful! Result: {}", rs.getInt("cnt"));
                }
            }

        } catch (Exception e) {
            log.error("❌ Database connection failed: {}", e.getMessage());
            log.error("Error details:", e);
        }
    }
}
