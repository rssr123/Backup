package com.maven.rms.config;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Custom database logging solution that works around Informix JDBC driver
 * limitations
 * Uses Spring JdbcTemplate with custom Log4j2 appender
 */
@Slf4j
@Component
@Order(10) // Run after database is initialized
public class Log4j2JdbcAppenderInitializer implements CommandLineRunner {

    @Value("${spring.datasource.url}")
    private String datasourceUrl;

    @Value("${spring.datasource.username}")
    private String datasourceUsername;

    @Value("${spring.datasource.password}")
    private String datasourcePassword;

    @Value("${quartz.enable.tasks:false}")
    private boolean quartzEnabled;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("=== Custom Database Logging Initializer ===");
        System.out.println("Datasource URL: " + datasourceUrl);
        System.out.println("Datasource Username: " + datasourceUsername);

        try {
            // Verify log directory exists (should be created by main method)
            verifyLogDirectories();

            // Read database configuration from Log4j2 XML
            loadDatabaseConfiguration();

            // Initialize custom Spring JDBC appender
            SpringJdbcAppender.setJdbcTemplate(jdbcTemplate);

            // Test database connection (non-fatal) -- with server start info
            testDatabaseLogging();

            // Add custom appender to Log4j2
            addCustomAppenderToLog4j2();

            System.out.println("✓ Database logging is ready - using custom Spring JdbcTemplate solution");
            System.out.println("✓ Bypasses Informix setNString() compatibility issues");

            // Test SLF4J logging after everything is setup
            testSlf4jLogging();

        } catch (Exception e) {
            System.err.println("✗ Database logging setup failed: " + e.getMessage());
            System.err.println("⚠ Application will continue without database logging");
            System.err.println("⚠ Logs will only be written to files");
            // Don't rethrow the exception - let the application continue
        }
    }

    /**
     * Verify log directories exist (should be created by main method)
     */
    private void verifyLogDirectories() {
        String logDir = System.getProperty("rms.log.dir");

        File dir = new File(logDir);
        if (!dir.exists()) {
            System.err.println("⚠ Warning: Log directory does not exist: " + logDir);
            System.err.println("⚠ Attempting to create it now...");
            boolean created = dir.mkdirs();
            if (created) {
                System.out.println("✓ Created log directory: " + logDir);
            } else {
                System.err.println("✗ Failed to create log directory: " + logDir);
            }
        } else {
            System.out.println("✓ Log directory verified: " + logDir);
        }
    }

    private void testDatabaseLogging_v2() {
        try {
            // Get configuration from system properties (loaded from XML)
            String tableName = System.getProperty("log4j2.db.tableName", "rms_error_log");
            String defaultUser = System.getProperty("log4j2.db.defaultUser", "System");
            String defaultRemark = System.getProperty("log4j2.db.defaultRemark", "Log4J Is working properly");

            // Get server information
            String serverName = getServerName();
            String serverIP = getServerIP();

            // Create initialization message with server info
            String initMessage = String.format("Server Name: %s, Server IP: %s is initialized", serverName, serverIP);

            // Test insert to verify table structure and connectivity
            String insertSql = "INSERT INTO " + tableName
                    + " (level, msg, source, login_nm, remark, dt_created, dt_modified, created_by, modified_by) VALUES (?, ?, ?, ?, ?, CURRENT, CURRENT, ?, ?)";

            jdbcTemplate.update(insertSql,
                    "INFO",
                    initMessage,
                    "HealthCheck",
                    defaultUser,
                    defaultRemark,
                    defaultUser,
                    defaultUser);

            System.out.println("✓ Successfully inserted test log entry to " + tableName + " table");
            System.out.println("✓ Log message: " + initMessage);

            // Test database connectivity without inserting a log entry
            String testSql = "SELECT COUNT(*) FROM " + tableName + " WHERE 1=0";
            jdbcTemplate.queryForObject(testSql, Integer.class);

            System.out.println("✓ Database connectivity verified for " + tableName + " table");
            System.out.println("✓ No test log entry inserted - table ready for actual application logs");
        } catch (Exception e) {
            System.err.println("⚠ Database logging test failed: " + e.getMessage());
            System.err.println("⚠ Will proceed with failover-only mode - logs will go to file when needed");
        }
    }

    private void testDatabaseLogging() {
        try {
            // Get configuration from system properties (loaded from XML)
            String tableName = System.getProperty("log4j2.db.tableName", "rms_error_log");

            // Test database connectivity without inserting a log entry
            String testSql = "SELECT COUNT(*) FROM " + tableName + " WHERE 1=0";
            jdbcTemplate.queryForObject(testSql, Integer.class);

            System.out.println("✓ Database connectivity verified for " + tableName + " table");
            System.out.println("✓ No test log entry inserted - table ready for actual application logs");
        } catch (Exception e) {
            System.err.println("⚠ Database logging test failed: " + e.getMessage());
            System.err.println("⚠ Will proceed with failover-only mode - logs will go to file when needed");
        }
    }

    private void addCustomAppenderToLog4j2() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        // Create custom database appender
        SpringJdbcAppender customDatabaseAppender = SpringJdbcAppender.createAppender("databaseAppender",
                "rms_error_log");
        customDatabaseAppender.start();
        config.addAppender(customDatabaseAppender);

        // Add database appender directly to root logger
        // The SpringJdbcAppender handles its own failover internally (database -> file)
        // The existing primaryFailover handles console -> file
        ctx.getRootLogger().addAppender(customDatabaseAppender);
        ctx.updateLoggers();

        System.out.println("✓ Added custom JDBC appender to Log4j2 configuration");
        System.out.println("✓ Logging sequence: Database (with internal failover) + Console -> File");
        System.out.println("✓ Database failover is handled internally by SpringJdbcAppender");

        // Show the actual resolved failover path
        String resolvedPath = System.getProperty("log4j2.failover.logPath");
        if (resolvedPath != null) {
            System.out.println("✓ Failed database logs will be written to: " + resolvedPath);
        } else {
            System.out.println("✓ Failed database logs will be written to: " + System.getProperty("java.io.tmpdir")
                    + System.getProperty("file.separator") + "rms" + System.getProperty("file.separator")
                    + "rms_database_failover.log");
        }
    }

    /**
     * Load database configuration from Log4j2 XML properties and set as system
     * properties
     * for the SpringJdbcAppender to use
     */
    private void loadDatabaseConfiguration() {
        LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        Configuration config = ctx.getConfiguration();

        // Read properties from Log4j2 configuration and set as system properties
        setSystemPropertyFromConfig(config, "log4j2.db.tableName", "db.tableName");
        setSystemPropertyFromConfig(config, "log4j2.db.defaultUser", "db.defaultUser");
        setSystemPropertyFromConfig(config, "log4j2.db.defaultRemark", "db.defaultRemark");

        // Column mappings
        setSystemPropertyFromConfig(config, "log4j2.db.columns.level", "db.columns.level");
        setSystemPropertyFromConfig(config, "log4j2.db.columns.clientIp", "db.columns.clientIp");
        setSystemPropertyFromConfig(config, "log4j2.db.columns.clientBrowser", "db.columns.clientBrowser");
        setSystemPropertyFromConfig(config, "log4j2.db.columns.message", "db.columns.message");
        setSystemPropertyFromConfig(config, "log4j2.db.columns.stacktrace", "db.columns.stacktrace");
        setSystemPropertyFromConfig(config, "log4j2.db.columns.source", "db.columns.source");
        setSystemPropertyFromConfig(config, "log4j2.db.columns.loginUser", "db.columns.loginUser");
        setSystemPropertyFromConfig(config, "log4j2.db.columns.remark", "db.columns.remark");
        setSystemPropertyFromConfig(config, "log4j2.db.columns.createdDate", "db.columns.createdDate");
        setSystemPropertyFromConfig(config, "log4j2.db.columns.modifiedDate", "db.columns.modifiedDate");
        setSystemPropertyFromConfig(config, "log4j2.db.columns.createdBy", "db.columns.createdBy");
        setSystemPropertyFromConfig(config, "log4j2.db.columns.modifiedBy", "db.columns.modifiedBy");

        // Failover configuration
        setSystemPropertyFromConfig(config, "log4j2.failover.enabled", "failover.enabled");
        setSystemPropertyFromConfig(config, "log4j2.failover.maxFiles", "failover.maxFiles");
        setSystemPropertyFromConfig(config, "log4j2.failover.maxFileSize", "failover.maxFileSize");

        // Set OS-specific failover path programmatically
        String osSpecificLogPath = getOSSpecificLogPath();
        System.setProperty("log4j2.failover.logPath", osSpecificLogPath);

        // Debug: Print the resolved paths
        System.out.println("✓ Failover log path set to: " + osSpecificLogPath);
        System.out.println("✓ Temp folder is: " + System.getProperty("tempfolder"));

        System.out.println("✓ Database configuration loaded from Log4j2 XML");
    }

    /**
     * Get OS-specific log path with proper file separators
     * Windows: C:\temp\rms\rms_database_failover.log
     * Linux/Unix: /tmp/rms/rms_database_failover.log
     */
    private String getOSSpecificLogPath() {
        String logDir = System.getProperty("rms.log.dir");
        return logDir + File.separator + "rms_database_failover.log";
    }

    /**
     * Helper method to read property from Log4j2 config and set as system property
     */
    private void setSystemPropertyFromConfig(Configuration config, String systemPropName, String configPropName) {
        String value = config.getProperties().get(configPropName);
        if (value != null) {
            System.setProperty(systemPropName, value);
        }
    }

    /**
     * Test SLF4J logging after everything is setup
     */
    private void testSlf4jLogging() {
        try {
            String machineIp = getMachineIpAddress();
            String machineName = getMachineName();
            String loggingPath = getLoggingPath();
            String schedulerStatus = quartzEnabled ? "ENABLED" : "DISABLED";

            System.out.println("=== SLF4J Logging Test ===");

            log.debug("Logging is initiated: {} ({}) - {} | Scheduler: {}", machineName, machineIp, loggingPath,
                    schedulerStatus);
            log.info("Logging is initiated: {} ({}) - {} | Scheduler: {}", machineName, machineIp, loggingPath,
                    schedulerStatus);
            log.warn("Logging is initiated: {} ({}) - {} | Scheduler: {}", machineName, machineIp, loggingPath,
                    schedulerStatus);
            log.error("Logging is initiated: {} ({}) - {} | Scheduler: {}", machineName, machineIp, loggingPath,
                    schedulerStatus);

            System.out.println("✓ SLF4J logging test completed successfully");
            System.out.println("✓ Check database and log files for the test messages");

        } catch (Exception e) {
            System.err.println("✗ SLF4J logging test failed: " + e.getMessage());
            log.error("SLF4J logging test failed", e);
        }
    }

    /**
     * Get the actual machine IP address (not localhost, 127.0.0.1, or 0.0.0.0)
     */
    private String getMachineIpAddress() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                // Skip loopback and inactive interfaces
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();

                    // Skip loopback addresses and IPv6 addresses
                    if (!address.isLoopbackAddress() &&
                            !address.isLinkLocalAddress() &&
                            address.getHostAddress().indexOf(':') == -1) {

                        String ip = address.getHostAddress();
                        // Make sure it's not localhost variants
                        if (!ip.equals("127.0.0.1") && !ip.equals("0.0.0.0") && !ip.startsWith("127.")) {
                            return ip;
                        }
                    }
                }
            }

            // Fallback: try to get host address by hostname
            return InetAddress.getLocalHost().getHostAddress();

        } catch (Exception e) {
            System.err.println("⚠ Could not determine machine IP: " + e.getMessage());
            return "UNKNOWN_IP";
        }
    }

    /**
     * Get the machine name/hostname
     */
    private String getMachineName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            System.err.println("⚠ Could not determine machine name: " + e.getMessage());
            // Try environment variables as fallback
            String computerName = System.getenv("COMPUTERNAME"); // Windows
            if (computerName != null) {
                return computerName;
            }

            String hostname = System.getenv("HOSTNAME"); // Linux/Unix
            if (hostname != null) {
                return hostname;
            }

            return "UNKNOWN_MACHINE";
        }
    }

    /**
     * Get the logging path that will be used for this machine
     */
    private String getLoggingPath() {
        String logDir = System.getProperty("rms.log.dir");
        String failoverPath = System.getProperty("log4j2.failover.logPath");

        StringBuilder pathInfo = new StringBuilder();
        pathInfo.append("Log Directory: ").append(logDir != null ? logDir : "NOT_SET");

        if (failoverPath != null) {
            pathInfo.append(", Failover Path: ").append(failoverPath);
        }

        return pathInfo.toString();
    }

    private String getServerName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    /**
     * Get server IP address (excluding localhost and 0.0.0.0)
     */
    private String getServerIP() {
        try {
            // Get all network interfaces
            for (NetworkInterface networkInterface : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                // Skip loopback and inactive interfaces
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }

                // Get all IP addresses for this interface
                for (InetAddress inetAddress : Collections.list(networkInterface.getInetAddresses())) {
                    // Skip IPv6, loopback, and link-local addresses
                    if (!inetAddress.isSiteLocalAddress() || inetAddress.isLoopbackAddress()) {
                        continue;
                    }

                    String ip = inetAddress.getHostAddress();
                    // Skip localhost and 0.0.0.0
                    if (!"127.0.0.1".equals(ip) && !"0.0.0.0".equals(ip) && !ip.startsWith("127.")) {
                        return ip;
                    }
                }
            }

            // Fallback: try getLocalHost but filter out localhost
            InetAddress localhost = InetAddress.getLocalHost();
            String ip = localhost.getHostAddress();
            if (!"127.0.0.1".equals(ip) && !"0.0.0.0".equals(ip) && !ip.startsWith("127.")) {
                return ip;
            }

            return "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }
}
