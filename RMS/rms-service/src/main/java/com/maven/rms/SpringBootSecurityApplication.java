package com.maven.rms;

import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.sql.DataSource;

// import javax.naming.NamingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jndi.JndiTemplate;
import org.springframework.scheduling.annotation.EnableAsync;

// HikariCP imports
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
// import org.springframework.scheduling.annotation.EnableAsync;
// import org.springframework.transaction.annotation.EnableTransactionManagement;
// import org.springframework.web.client.RestTemplate;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories
@EnableEncryptableProperties
@EnableAsync(proxyTargetClass = true)
@Slf4j
public class SpringBootSecurityApplication {

    @Autowired
    private Environment env;

    @Bean(name = "dataSource")
    public DataSource dataSource() {
        String jndiName = env.getProperty("spring.datasource.jndi-name");
        if (jndiName != null && !jndiName.isEmpty()) {
            try {
                return new JndiTemplate().lookup(jndiName, DataSource.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to lookup JNDI DataSource", e);
            }
        } else {
            // Use HikariCP for robust connection pooling
            HikariConfig config = new HikariConfig();

            // Basic connection settings
            config.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
            config.setJdbcUrl(env.getProperty("spring.datasource.url"));
            config.setUsername(env.getProperty("spring.datasource.username"));
            config.setPassword(env.getProperty("spring.datasource.password"));

            // Connection pool settings - use environment properties with fallbacks
            // config.setPoolName(env.getProperty("spring.datasource.hikari.pool-name",
            // "RMS-HikariCP"));

            String poolName = getPoolNameWithHostInfo();
            config.setPoolName(poolName);

            config.setMinimumIdle(Integer.parseInt(env.getProperty("spring.datasource.hikari.minimum-idle", "10")));
            config.setMaximumPoolSize(
                    Integer.parseInt(env.getProperty("spring.datasource.hikari.maximum-pool-size", "75")));
            config.setConnectionTimeout(
                    Long.parseLong(env.getProperty("spring.datasource.hikari.connection-timeout", "30000")));
            config.setIdleTimeout(Long.parseLong(env.getProperty("spring.datasource.hikari.idle-timeout", "600000")));
            config.setMaxLifetime(Long.parseLong(env.getProperty("spring.datasource.hikari.max-lifetime", "1800000")));
            config.setLeakDetectionThreshold(
                    Long.parseLong(env.getProperty("spring.datasource.hikari.leak-detection-threshold", "60000")));

            // Connection validation settings - Use default isValid() method
            // config.setConnectionTestQuery(
            // env.getProperty("spring.datasource.hikari.connection-test-query",
            // "SELECT 1 FROM systables WHERE tabid=1"));
            config.setValidationTimeout(
                    Long.parseLong(env.getProperty("spring.datasource.hikari.validation-timeout", "5000")));

            // Informix-specific optimizations
            config.addDataSourceProperty("OPTOFC",
                    env.getProperty("spring.datasource.hikari.data-source-properties.OPTOFC", "1"));
            config.addDataSourceProperty("FET_BUF_SIZE",
                    env.getProperty("spring.datasource.hikari.data-source-properties.FET_BUF_SIZE", "65536"));
            config.addDataSourceProperty("CONTIME",
                    env.getProperty("spring.datasource.hikari.data-source-properties.CONTIME", "10"));
            config.addDataSourceProperty("ISOLATION_LEVEL",
                    env.getProperty("spring.datasource.hikari.data-source-properties.ISOLATION_LEVEL", "2"));

            // Additional HikariCP optimizations for stability
            config.setAutoCommit(true);
            config.setReadOnly(false);
            config.setRegisterMbeans(false); // Disable MBean registration to avoid conflicts

            // Thread management settings to prevent memory leaks
            config.setInitializationFailTimeout(-1); // Don't fail fast on startup
            config.setAllowPoolSuspension(false); // Disable pool suspension

            log.info("Initializing HikariCP DataSource with pool settings:");
            log.info("  - Pool Name: {}", config.getPoolName());
            log.info("  - Minimum Idle: {}", config.getMinimumIdle());
            log.info("  - Maximum Pool Size: {}", config.getMaximumPoolSize());
            log.info("  - Connection Timeout: {}ms", config.getConnectionTimeout());
            log.info("  - Idle Timeout: {}ms", config.getIdleTimeout());
            log.info("  - Max Lifetime: {}ms", config.getMaxLifetime());
            log.info("  - JDBC URL: {}", config.getJdbcUrl());

            System.out.println("  - Pool Name: " + config.getPoolName());

            HikariDataSource dataSource = new HikariDataSource(config);

            // Add shutdown hook to properly close the datasource
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("Shutting down HikariCP DataSource...");
                try {
                    if (dataSource != null && !dataSource.isClosed()) {
                        dataSource.close();
                        log.info("HikariCP DataSource closed successfully");
                    }
                } catch (Exception e) {
                    log.warn("Error closing HikariCP DataSource", e);
                }
            }));

            return dataSource;
        }
    }

    public static void main(String[] args) {
        try {
            // Set OS-specific log directory before Spring Boot initializes Log4j2
            initializeLogDirectories();

            System.out.println(">>> Starting RMS Application...");
            SpringApplication.run(SpringBootSecurityApplication.class, args);

            System.out.println(">>> RMS Application Started Successfully.");

        } catch (Exception e) {
            System.err.println(">>> FATAL: Application failed to start!");
            e.printStackTrace();
        }
    }

    /**
     * Initialize log directories for both standalone and Tomcat deployment
     * Public method that can be called from ServletInitializer
     */
    public static void initializeLogDirectories() {
        setOSSpecificLogDirectory();
    }

    /**
     * Set OS-specific log directory system property before Log4j2 loads
     * This must be called before SpringApplication.run() to ensure Log4j2 can
     * resolve the property. Reads paths from application.properties.
     */
    private static void setOSSpecificLogDirectory() {
        String osName = System.getProperty("os.name").toLowerCase();
        String logDir;

        // Default fallback paths
        String defaultWinPath = "C:/temp/rms";
        String defaultOtherPath = "/var/log/rms";

        try {
            // Load properties from application.properties
            java.util.Properties props = new java.util.Properties();
            java.io.InputStream inputStream = SpringBootSecurityApplication.class
                    .getClassLoader().getResourceAsStream("application.properties");

            if (inputStream != null) {
                props.load(inputStream);
                inputStream.close();

                // Read paths from properties with fallback to defaults
                String winPath = props.getProperty("log.win.path", defaultWinPath);
                String otherPath = props.getProperty("log.other.path", defaultOtherPath);

                if (osName.contains("windows")) {
                    logDir = winPath.replace("\\\\", "/"); // Convert Windows path separators
                } else {
                    // Linux, Unix, Mac, etc.
                    logDir = otherPath;
                }

                System.out.println(">>> Log paths loaded from application.properties:");
                System.out.println("    log.win.path = " + winPath);
                System.out.println("    log.other.path = " + otherPath);
            } else {
                System.out.println(">>> Warning: application.properties not found, using default paths");
                if (osName.contains("windows")) {
                    logDir = defaultWinPath;
                } else {
                    logDir = defaultOtherPath;
                }
            }
        } catch (Exception e) {
            System.out.println(">>> Error reading application.properties: " + e.getMessage());
            System.out.println(">>> Using default log directory paths");
            if (osName.contains("windows")) {
                logDir = defaultWinPath;
            } else {
                logDir = defaultOtherPath;
            }
        }

        // Create the directory if it doesn't exist
        java.io.File dir = new java.io.File(logDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            System.out.println(">>> Log directory created: " + logDir + " (success: " + created + ")");
        } else {
            System.out.println(">>> Log directory already exists: " + logDir);
        }

        System.setProperty("rms.log.dir", logDir);
        System.out.println(">>> Set system property rms.log.dir = " + logDir);
    }

    private String getPoolNameWithHostInfo() {
        try {
            InetAddress inetAddress = InetAddress.getLocalHost();
            String hostname = inetAddress.getHostName();
            String ip = inetAddress.getHostAddress();

            // Clean hostname (remove domain suffix if present)
            String cleanHostname = hostname.contains(".")
                    ? hostname.substring(0, hostname.indexOf("."))
                    : hostname;

            // Build pool name
            String poolName = String.format("RMS-HikariCP-%s-%s", cleanHostname, ip);

            log.info("Generated HikariCP pool name: {}", poolName);
            return poolName;

        } catch (UnknownHostException e) {
            log.warn("Could not determine hostname/IP, using default pool name", e);
            return env.getProperty("spring.datasource.hikari.pool-name", "RMS-HikariCP-Unknown");
        }
    }

}
