package com.maven.rms.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Custom Log4j2 appender that uses Spring JdbcTemplate for database logging
 * This bypasses the Informix setNString() compatibility issues
 * 
 * Configuration is read from Log4j2 properties for better maintainability
 */
@Plugin(name = "SpringJdbcAppender", category = "Core", elementType = "appender", printObject = true)
public class SpringJdbcAppender extends AbstractAppender {

    private static JdbcTemplate jdbcTemplate;
    private final DatabaseConfig dbConfig;

    protected SpringJdbcAppender(String name, String tableName) {
        super(name, null, null, false, null);
        this.dbConfig = new DatabaseConfig(tableName);
    }

    @Override
    public void append(LogEvent event) {
        if (jdbcTemplate == null) {
            return; // JdbcTemplate not yet initialized
        }

        try {
            // Try database logging first
            logToDatabase(event);
        } catch (Exception e) {
            // If database fails, failover to file logging
            try {
                logToFailoverFile(event, e);
            } catch (Exception fileException) {
                // If both fail, log to system err to avoid recursion
                System.err.println("SpringJdbcAppender: Both database and file logging failed!");
                System.err.println("Database error: " + e.getMessage());
                System.err.println("File error: " + fileException.getMessage());
            }
        }
    }

    private void logToDatabase(LogEvent event) {
        // Build dynamic SQL using configuration
        String insertSql = buildInsertSql();

        // Extract values matching original XML patterns exactly

        // %5p - 5 character padded level
        String level = String.format("%-5s", event.getLevel().toString());

        // %C - Full class name (not just logger name)
        String source = event.getSource() != null ? event.getSource().getClassName() : event.getLoggerName();

        // %throwable - Exception stack trace
        String stacktrace = null;
        if (event.getThrown() != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(event.getThrown().toString());
            for (StackTraceElement ste : event.getThrown().getStackTrace()) {
                sb.append("\n\tat ").append(ste.toString());
            }
            if (event.getThrown().getCause() != null) {
                sb.append("\nCaused by: ").append(event.getThrown().getCause().toString());
            }
            stacktrace = sb.toString();
        }

        // Extract MDC values (%X{}) - correlationId, apiPath
        String correlationId = event.getContextData().getValue("correlationId");
        String apiPath = event.getContextData().getValue("apiPath");

        // Ensure correlation ID exists (clean UUID only)
        if (correlationId == null || correlationId.isEmpty()) {
            correlationId = java.util.UUID.randomUUID().toString();
            // Set default remark for logger-generated IDs
            if (apiPath == null || apiPath.isEmpty()) {
                apiPath = "LOGGER: " + event.getLoggerName();
            }
        }

        // Extract ThreadContext values (%K{}) - client_ip, client_browser, login_nm
        String clientIp = getContextValue(event, "client_ip");
        String clientBrowser = getContextValue(event, "client_browser");
        String loginUser = getContextValue(event, "login_nm");

        // [%X{correlationId}]- %msg - exact format from original XML
        String message = event.getMessage().getFormattedMessage();
        String formattedMessage = "[" + correlationId + "]- " + message;

        // %X{apiPath} - use apiPath as remark
        String remark = apiPath != null ? apiPath : dbConfig.defaultRemark;

        // %K{login_nm} - use login_nm for all user fields
        String effectiveLoginUser = loginUser != null ? loginUser : dbConfig.defaultUser;

        jdbcTemplate.update(insertSql,
                level,
                clientIp,
                clientBrowser,
                formattedMessage,
                stacktrace,
                source,
                effectiveLoginUser,
                remark,
                effectiveLoginUser,
                effectiveLoginUser);
    }

    /**
     * Get context value from both MDC and ThreadContext (Log4j2 ContextData)
     */
    private String getContextValue(LogEvent event, String key) {
        String value = event.getContextData().getValue(key);
        if (value != null && !value.isEmpty()) {
            return value;
        }

        // Fallback values for background tasks that don't have web context
        switch (key) {
            case "client_ip":
                return getMachineIdentifier();
            case "client_browser":
                return "BackgroundTask";
            case "login_nm":
                return "System";
            default:
                return "unknown";
        }
    }

    private void logToFailoverFile(LogEvent event, Exception dbException) throws IOException {
        // Read failover configuration from XML properties
        boolean failoverEnabled = Boolean.parseBoolean(System.getProperty("log4j2.failover.enabled", "true"));
        if (!failoverEnabled) {
            return; // Failover disabled
        }

        String failoverLogPath = System.getProperty("log4j2.failover.logPath");
        if (failoverLogPath == null) {
            // Fallback to default path
            String tempDir = System.getProperty("java.io.tmpdir");
            String separator = System.getProperty("file.separator");
            failoverLogPath = tempDir + separator + "rms" + separator + "rms_database_failover.log";
        }

        // Create failover directory if it doesn't exist
        File logFile = new File(failoverLogPath);
        File parentDir = logFile.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        // Check file size and rotate if needed
        String maxFileSizeStr = System.getProperty("log4j2.failover.maxFileSize", "5MB");
        long maxFileSize = parseFileSize(maxFileSizeStr);

        if (logFile.exists() && logFile.length() > maxFileSize) {
            int maxFiles = Integer.parseInt(System.getProperty("log4j2.failover.maxFiles", "10"));
            rotateFailoverFiles(logFile, maxFiles);
        }

        // Format log entry
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String timestamp = dateFormat.format(new Date(event.getTimeMillis()));
        String level = event.getLevel().toString();
        String message = event.getMessage().getFormattedMessage();
        String source = event.getLoggerName();
        String stacktrace = event.getThrown() != null ? event.getThrown().toString() : "";

        // Write to failover file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true))) {
            writer.write(String.format("[%s] %s [%s] %s - %s%n",
                    level, timestamp, Thread.currentThread().getName(), source, message));
            if (!stacktrace.isEmpty()) {
                writer.write(stacktrace + "\n");
            }
            writer.write("DATABASE_ERROR: " + dbException.getMessage() + "\n");
            writer.write("---\n");
        }
    }

    private long parseFileSize(String sizeStr) {
        sizeStr = sizeStr.toUpperCase();
        if (sizeStr.endsWith("MB")) {
            return Long.parseLong(sizeStr.replace("MB", "")) * 1024 * 1024;
        } else if (sizeStr.endsWith("KB")) {
            return Long.parseLong(sizeStr.replace("KB", "")) * 1024;
        } else {
            return Long.parseLong(sizeStr); // Assume bytes
        }
    }

    private void rotateFailoverFiles(File currentLogFile, int maxFiles) {
        String basePath = currentLogFile.getAbsolutePath();
        String basePathWithoutExt = basePath.substring(0, basePath.lastIndexOf('.'));
        String extension = basePath.substring(basePath.lastIndexOf('.'));

        // Rotate files: .log -> .log.1 -> .log.2 -> ... -> .log.(maxFiles-1)
        for (int i = maxFiles - 1; i >= 1; i--) {
            File oldFile = new File(basePathWithoutExt + "." + i + extension);
            if (i == maxFiles - 1) {
                oldFile.delete(); // Delete oldest file
            } else {
                File newFile = new File(basePathWithoutExt + "." + (i + 1) + extension);
                oldFile.renameTo(newFile);
            }
        }
        // Rename current log to .log.1
        File firstBackup = new File(basePathWithoutExt + ".1" + extension);
        currentLogFile.renameTo(firstBackup);
    }

    private String buildInsertSql() {
        return String.format(
                "INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT, CURRENT, ?, ?)",
                dbConfig.tableName,
                dbConfig.levelColumn,
                dbConfig.clientIpColumn,
                dbConfig.clientBrowserColumn,
                dbConfig.messageColumn,
                dbConfig.stacktraceColumn,
                dbConfig.sourceColumn,
                dbConfig.loginUserColumn,
                dbConfig.remarkColumn,
                dbConfig.createdDateColumn,
                dbConfig.modifiedDateColumn,
                dbConfig.createdByColumn,
                dbConfig.modifiedByColumn);
    }

    @PluginFactory
    public static SpringJdbcAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginAttribute("tableName") String tableName) {

        if (name == null) {
            LogManager.getLogger().error("No name provided for SpringJdbcAppender");
            return null;
        }

        if (tableName == null) {
            tableName = "rms_error_log";
        }

        return new SpringJdbcAppender(name, tableName);
    }

    /**
     * Called by Spring to inject JdbcTemplate
     */
    public static void setJdbcTemplate(JdbcTemplate template) {
        jdbcTemplate = template;
    }

    /**
     * Database configuration class to hold column mappings and defaults
     */
    private static class DatabaseConfig {
        final String tableName;
        final String defaultUser;
        final String defaultRemark;

        // Column names
        final String levelColumn;
        final String clientIpColumn;
        final String clientBrowserColumn;
        final String messageColumn;
        final String stacktraceColumn;
        final String sourceColumn;
        final String loginUserColumn;
        final String remarkColumn;
        final String createdDateColumn;
        final String modifiedDateColumn;
        final String createdByColumn;
        final String modifiedByColumn;

        DatabaseConfig(String configuredTableName) {
            // Read from Log4j2 system properties (set by Log4j2JdbcAppenderInitializer)
            this.tableName = System.getProperty("log4j2.db.tableName",
                    configuredTableName != null ? configuredTableName : "rms_error_log");
            this.defaultUser = System.getProperty("log4j2.db.defaultUser", "System");
            this.defaultRemark = System.getProperty("log4j2.db.defaultRemark", "");

            // Column mappings
            this.levelColumn = System.getProperty("log4j2.db.columns.level", "level");
            this.clientIpColumn = System.getProperty("log4j2.db.columns.clientIp", "client_ip");
            this.clientBrowserColumn = System.getProperty("log4j2.db.columns.clientBrowser", "client_browser");
            this.messageColumn = System.getProperty("log4j2.db.columns.message", "msg");
            this.stacktraceColumn = System.getProperty("log4j2.db.columns.stacktrace", "stacktrace");
            this.sourceColumn = System.getProperty("log4j2.db.columns.source", "source");
            this.loginUserColumn = System.getProperty("log4j2.db.columns.loginUser", "login_nm");
            this.remarkColumn = System.getProperty("log4j2.db.columns.remark", "remark");
            this.createdDateColumn = System.getProperty("log4j2.db.columns.createdDate", "dt_created");
            this.modifiedDateColumn = System.getProperty("log4j2.db.columns.modifiedDate", "dt_modified");
            this.createdByColumn = System.getProperty("log4j2.db.columns.createdBy", "created_by");
            this.modifiedByColumn = System.getProperty("log4j2.db.columns.modifiedBy", "modified_by");
        }
    }

    /**
     * Get machine identifier instead of localhost
     * Returns hostname or hostname(IP) format for better identification
     */
    private String getMachineIdentifier() {
        try {
            String hostname = java.net.InetAddress.getLocalHost().getHostName();
            // Try to get the machine's actual IP
            try {
                java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
                String actualIp = localHost.getHostAddress();
                if (!isLocalAddress(actualIp)) {
                    return hostname + " (" + actualIp + ")";
                } else {
                    return hostname + " (localhost)";
                }
            } catch (Exception ex) {
                return hostname + " (localhost)";
            }
        } catch (Exception e) {
            return "UnknownHost (localhost)";
        }
    }

    /**
     * Check if the IP address is a local/loopback address
     */
    private boolean isLocalAddress(String ipAddress) {
        return ipAddress != null &&
                (ipAddress.equals("127.0.0.1") ||
                        ipAddress.equals("localhost") ||
                        ipAddress.equals("0.0.0.0") ||
                        ipAddress.equals("::1"));
    }
}
