package com.maven.rms.config;

import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.zaxxer.hikari.HikariDataSource;

import lombok.extern.slf4j.Slf4j;

/**
 * Manages the lifecycle of the HikariCP DataSource to prevent memory leaks
 * and ensure proper shutdown of connection pool threads
 */
@Component
@Slf4j
public class HikariDataSourceLifecycleManager {

    @Autowired
    private DataSource dataSource;

    /**
     * Properly close the HikariCP DataSource when the application shuts down
     * This prevents memory leaks from the housekeeper thread
     */
    @PreDestroy
    public void closeDataSource() {
        if (dataSource instanceof HikariDataSource) {
            HikariDataSource hikariDataSource = (HikariDataSource) dataSource;

            if (!hikariDataSource.isClosed()) {
                log.info("Closing HikariCP DataSource: {}", hikariDataSource.getPoolName());

                try {
                    // Close the datasource which will shutdown the housekeeper thread
                    hikariDataSource.close();
                    log.info("HikariCP DataSource closed successfully");
                } catch (Exception e) {
                    log.error("Error closing HikariCP DataSource", e);
                }
            } else {
                log.info("HikariCP DataSource is already closed");
            }
        }
    }
}
