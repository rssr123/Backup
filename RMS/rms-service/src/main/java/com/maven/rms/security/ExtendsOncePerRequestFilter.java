package com.maven.rms.security;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.function.Function;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.savedrequest.RequestCache;

import com.maven.rms.utils.RMSLogger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExtendsOncePerRequestFilter extends OncePerRequestFilter {

    private RequestCache requestCache;
    private RequestMatcher uriMatcher;
    
    // Constructor for DevFilter functionality only
    public ExtendsOncePerRequestFilter() {
        this.requestCache = new HttpSessionRequestCache();
        this.uriMatcher = null;
    }

    // Constructor for SavedRequestLoggerFilter functionality
    public ExtendsOncePerRequestFilter(RequestCache requestCache) {
        this.requestCache = requestCache != null ? requestCache : new HttpSessionRequestCache();
        this.uriMatcher = null;
    }

    // Constructor for SaveRefererFilter functionality
    public ExtendsOncePerRequestFilter(String antPath) {
        this.requestCache = new HttpSessionRequestCache();
        try {
            this.uriMatcher = new AntPathRequestMatcher(antPath, HttpMethod.GET.name());
        } catch (Exception e) {
            log.error("Error creating AntPathRequestMatcher for path: " + antPath, e);
            this.uriMatcher = null;
        }
    }

    // Constructor for all functionalities
    public ExtendsOncePerRequestFilter(RequestCache requestCache, String antPath) {
        this.requestCache = requestCache != null ? requestCache : new HttpSessionRequestCache();
        try {
            this.uriMatcher = antPath != null ? new AntPathRequestMatcher(antPath, HttpMethod.GET.name()) : null;
        } catch (Exception e) {
            log.error("Error creating AntPathRequestMatcher for path: " + antPath, e);
            this.uriMatcher = null;
        }
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            // DevFilter functionality - logging request details
            logRequestDetails(request);

            // SavedRequestLoggerFilter functionality - log saved request
            logSavedRequest(request, response);

            // SaveRefererFilter functionality - save referer URL if matcher is configured
            if (uriMatcher != null && uriMatcher.matches(request)) {
                saveRefererUrl(request);
            }        

        } catch (Exception e) {
            log.error("Error in CombinedSecurityFilter processing", e);
            // Continue with filter chain even if logging fails
        }

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("Error in filter chain execution", e);
            throw e; // Re-throw to maintain proper error handling
        }
    }

    
    private void logRequestDetails(HttpServletRequest request) {
        try {
            StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
            String queryString = request.getQueryString();
            String fullURL = queryString != null ? requestURL.append('?').append(queryString).toString()
                    : requestURL.toString();
            String username = "";

            String ipAddress = getClientIpAddress(request);

            // Log headers
            logHeaders(request);

            // Log cookies
            logCookies(request);

            if (username.equals("")) {
                log.debug("Anonymous user from IP Address: " + ipAddress + " accessed resource at: " + fullURL);
                username = "Anonymous";
            }

            // Log redirect information
            logRedirectInfo(request);

        } catch (Exception e) {
            log.error("Error logging request details", e);
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        try {
            String ipAddress = request.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = request.getRemoteAddr();
            } else {
                ipAddress = ipAddress.contains(",") ? ipAddress.split(",")[0] : ipAddress;
            }
            return ipAddress;
        } catch (Exception e) {
            log.error("Error getting client IP address", e);
            return "unknown";
        }
    }

    private void logHeaders(HttpServletRequest request) {
        try {
            Map<String, List<String>> headersMap = Collections.list(request.getHeaderNames())
                    .stream()
                    .collect(Collectors.toMap(
                            Function.identity(),
                            h -> Collections.list(request.getHeaders(h))));
            Iterator<Map.Entry<String, List<String>>> itr = headersMap.entrySet().iterator();

            log.info("Headers");
            while (itr.hasNext()) {
                Map.Entry<String, List<String>> entry = itr.next();
                log.debug("Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }
        } catch (Exception e) {
            log.error("Error logging headers", e);
        }
    }

    private void logCookies(HttpServletRequest request) {
        try {
            log.info("Cookies");
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (int i = 0; i < cookies.length; i++) {
                    log.debug("Key = " + cookies[i].getName() + ", Value = " + cookies[i].getValue());
                }
            }
        } catch (Exception e) {
            log.error("Error logging cookies", e);
        }
    }

    private void logRedirectInfo(HttpServletRequest request) {
        try {
            log.info("Redirect Stuff:");
            HttpSession session = request.getSession(false);
            if (session != null) {
                SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, null);
                if (savedRequest != null) {
                    log.debug("Saved Request: " + savedRequest.getRedirectUrl());
                }
            }
            log.debug("Context Path: " + request.getContextPath() + "/");
        } catch (Exception e) {
            log.error("Error logging redirect info", e);
        }
    }

    private void logSavedRequest(HttpServletRequest request, HttpServletResponse response) {
        try {
            SavedRequest savedRequest = requestCache.getRequest(request, response);
            if (savedRequest != null) {
                // log the original request URL
                RMSLogger.info("Original request URL: " + savedRequest.getRedirectUrl());
            } else {
                RMSLogger.info("Unable to read saved URL");
            }
        } catch (Exception e) {
            log.error("Error logging saved request", e);
        }
    }

    private void saveRefererUrl(HttpServletRequest request) {
        try {
            StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
            String queryString = request.getQueryString();
            String fullURL = queryString != null ? requestURL.append('?').append(queryString).toString()
                    : requestURL.toString();

            log.info("Referer URL: " + fullURL);
            log.info("Origin: " + request.getHeader("Origin"));
            log.info("Query: " + request.getQueryString());
            log.info("Param: " + request.getParameter("redirectUrl"));
            log.info("Size: " + request.getParameterMap().size());

            HttpSession session = request.getSession(true);
            session.setAttribute("redirectUrl", fullURL);
        } catch (Exception e) {
            log.error("Error saving referer URL", e);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        try {
            if (uriMatcher != null) {
                RequestMatcher matcher = new NegatedRequestMatcher(uriMatcher);
                return matcher.matches(request);
            }
            return false;
        } catch (Exception e) {
            log.error("Error in shouldNotFilter method", e);
            return false; // Default to filtering if there's an error
        }
    }
}