package com.maven.rms.security;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExtendsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Any initialization code can go here
        // Initialization for both OriginalUrlCaptureFilter and CustomCachingBodyFilter
        // functionality
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        try {
            log.info("CombinedUrlCachingFilter filter is started.");

            if (request instanceof HttpServletRequest) {
                HttpServletRequest httpRequest = (HttpServletRequest) request;

                // OriginalUrlCaptureFilter functionality - capture original URL
                captureOriginalUrl(httpRequest);

                // ExtendsFilter functionality - handle request wrapping
                ServletRequest wrappedRequest = wrapRequest(httpRequest);

                // Continue with the wrapped request
                chain.doFilter(wrappedRequest, response);

            } else {
                // For non-HTTP requests, just continue the chain without modification
                log.info("Non-HTTP request detected, continuing without modification.");
                chain.doFilter(request, response);
            }

        } catch (Exception e) {

            log.error("Error in CombinedUrlCachingFilter: " + e.getMessage(), e);
            chain.doFilter(request, response);
        }
    }

    private void captureOriginalUrl(HttpServletRequest httpRequest) {
        try {
            String originalRequestUrl = httpRequest.getRequestURL().toString();
            String originalUrl = httpRequest.getParameter("originalUrl");

            // Save the original URL in the session
            HttpSession session = httpRequest.getSession();
            if (session != null) {
                session.setAttribute("originalRequestUrl", originalRequestUrl);
                log.info("originalRequestUrl: " + originalRequestUrl);
                log.info("originalUrl: " + originalUrl);
            } else {
                log.info("Unable to get session for storing original URL");
            }
        } catch (Exception e) {
            log.error("Error capturing original URL: " + e.getMessage(), e);
        }
    }

    private ServletRequest wrapRequest(HttpServletRequest httpRequest) {
        try {
            String contentType = httpRequest.getContentType();
            log.info("Wrapping request with content type: " + contentType + " using ExtendsHttpServletRequestWrapper");
            
            // ExtendsHttpServletRequestWrapper automatically handles different content
            // types internally
            return new ExtendsHttpServletRequestWrapper(httpRequest);

        } catch (Exception e) {
            log.error("Error wrapping request based on content type: " + e.getMessage(), e);
            // Return original request if wrapping fails
            return httpRequest;
        }
    }

    @Override
    public void destroy() {
        // Any cleanup code can go here
        // Cleanup for both OriginalUrlCaptureFilter and CustomCachingBodyFilter
        // functionality
        log.info("CombinedUrlCachingFilter is being destroyed.");
    }
}