package com.maven.rms.logging;

import org.slf4j.MDC;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Utility to ensure all threads (web requests, background tasks, schedulers)
 * have proper logging context with correlation IDs matching original log4j.xml
 * format
 */
@Component
public class LoggingContextUtil {

    private static final String CORRELATION_ID_KEY = "correlationId";
    private static final String API_PATH_KEY = "apiPath";
    private static final String CLIENT_IP_KEY = "client_ip";
    private static final String CLIENT_BROWSER_KEY = "client_browser";
    private static final String LOGIN_USER_KEY = "login_nm";

    /**
     * Set up logging context for background tasks/schedulers
     * 
     * @param taskName The name of the background task (e.g., "QuartzScheduler",
     *                 "PaymentClearance")
     */
    public static void setupBackgroundTaskContext(String taskName) {
        // Generate clean UUID only (task info goes to remark column)
        String correlationId = UUID.randomUUID().toString();

        // Get current authenticated user (if any)
        String loginUser = getCurrentUsername();

        // Set MDC values for %X{} patterns
        MDC.put(CORRELATION_ID_KEY, correlationId);
        MDC.put(API_PATH_KEY, "TASK: " + taskName); // This goes to remark column

        // Set ThreadContext values for %K{} patterns
        ThreadContext.put(CLIENT_IP_KEY, getMachineIdentifier());
        ThreadContext.put(CLIENT_BROWSER_KEY, "BackgroundTask-" + taskName);
        ThreadContext.put(LOGIN_USER_KEY, loginUser != null ? loginUser : "System");
    }

    /**
     * Set up logging context for web requests (used by MDCFilter)
     */
    public static void setupWebRequestContext(String correlationId, String apiPath,
            String clientIp, String clientBrowser, String loginUser) {
        // Set MDC values for %X{} patterns
        MDC.put(CORRELATION_ID_KEY, correlationId);
        MDC.put(API_PATH_KEY, apiPath);

        // Set ThreadContext values for %K{} patterns
        ThreadContext.put(CLIENT_IP_KEY, clientIp != null ? clientIp : "unknown");
        ThreadContext.put(CLIENT_BROWSER_KEY, clientBrowser != null ? clientBrowser : "unknown");
        ThreadContext.put(LOGIN_USER_KEY, loginUser != null ? loginUser : "System");
    }

    /**
     * Clear all logging context
     */
    public static void clearContext() {
        MDC.clear();
        ThreadContext.clearAll();
    }

    /**
     * Get current authenticated username
     */
    private static String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !"anonymousUser".equals(authentication.getName())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            // Ignore authentication errors in background tasks
        }
        return null;
    }

    /**
     * Ensure current thread has correlation ID (create one if missing)
     */
    public static void ensureCorrelationId(String source) {
        String existingId = MDC.get(CORRELATION_ID_KEY);
        if (existingId == null || existingId.isEmpty()) {
            setupBackgroundTaskContext(source);
        }
    }

    /**
     * Get machine identifier instead of localhost
     * Returns hostname or hostname(IP) format for better identification
     */
    private static String getMachineIdentifier() {
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
    private static boolean isLocalAddress(String ipAddress) {
        return ipAddress != null &&
                (ipAddress.equals("127.0.0.1") ||
                        ipAddress.equals("localhost") ||
                        ipAddress.equals("0.0.0.0") ||
                        ipAddress.equals("::1"));
    }
}
