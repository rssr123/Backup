package com.maven.rms.logging;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.UUID;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

@Component
public class MDCFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Wrap request with ContentCachingRequestWrapper for audit logging
        // This allows the request body to be read multiple times
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(httpRequest);

        // Extract correlation ID and API path
        String correlationId = wrappedRequest.getHeader("X-Correlation-ID");
        String apiPath = wrappedRequest.getRequestURI();

        if (correlationId == null || correlationId.isEmpty()) {
            // Generate clean UUID only (URL goes to remark column)
            correlationId = UUID.randomUUID().toString();
        }

        // Extract client information
        String clientIp = getClientIpAddress(wrappedRequest);
        String clientBrowser = wrappedRequest.getHeader("User-Agent");

        // Extract authentication information
        String loginUser = getCurrentUsername();

        // Use LoggingContextUtil for consistent context setup
        LoggingContextUtil.setupWebRequestContext(correlationId, apiPath, clientIp, clientBrowser, loginUser);

        // Add the correlationId to the response headers
        httpResponse.setHeader("X-Correlation-ID", correlationId);

        try {
            // Pass the wrapped request down the filter chain
            chain.doFilter(wrappedRequest, response);
        } finally {
            // Clear context using utility
            LoggingContextUtil.clearContext();
        }
    }

    /**
     * Extract the real client IP address, handling proxies and load balancers
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        String xRealIp = request.getHeader("X-Real-IP");
        String xClusterClientIp = request.getHeader("X-Cluster-Client-IP");

        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            // X-Forwarded-For can contain multiple IPs, get the first one
            return xForwardedFor.split(",")[0].trim();
        }

        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        if (xClusterClientIp != null && !xClusterClientIp.isEmpty() && !"unknown".equalsIgnoreCase(xClusterClientIp)) {
            return xClusterClientIp;
        }

        // Fallback to standard remote address
        String remoteAddr = request.getRemoteAddr();

        // If it's localhost, 127.0.0.1, or 0.0.0.0, try to get machine name instead
        if (isLocalAddress(remoteAddr)) {
            try {
                String hostname = java.net.InetAddress.getLocalHost().getHostName();
                // Return hostname-IP format for better identification
                return hostname + " (" + remoteAddr + ")";
            } catch (Exception e) {
                // If hostname lookup fails, try to get the machine's actual IP
                try {
                    java.net.InetAddress localHost = java.net.InetAddress.getLocalHost();
                    String actualIp = localHost.getHostAddress();
                    if (!isLocalAddress(actualIp)) {
                        return localHost.getHostName() + " (" + actualIp + ")";
                    }
                } catch (Exception ex) {
                    // Fall back to original address
                }
            }
        }

        return remoteAddr;
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

    /**
     * Get the current authenticated username
     */
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() &&
                    !"anonymousUser".equals(authentication.getName())) {
                return authentication.getName();
            }
        } catch (Exception e) {
            // Ignore authentication errors, return null for fallback
        }
        return null;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code, if necessary
    }

    @Override
    public void destroy() {
        // Cleanup code, if necessary
    }
}
