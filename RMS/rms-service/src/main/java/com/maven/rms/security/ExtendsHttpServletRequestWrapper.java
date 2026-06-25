package com.maven.rms.security;

import java.io.*;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.*;
import javax.servlet.http.*;

import org.jfree.util.Log;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExtendsHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private byte[] cachedBody;
    private Map<String, String[]> cachedFormParameters;
    private boolean isFormUrlEncoded;

    public ExtendsHttpServletRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        String contentType = request.getContentType();
        this.isFormUrlEncoded = "application/x-www-form-urlencoded".equalsIgnoreCase(contentType);

        if (isFormUrlEncoded) {
            // For form URL encoded requests, cache parameters
            cacheFormParameters(request);
        } else {
            // For other content types, cache the body
            cacheRequestBody(request);
        }
    }

    private void cacheRequestBody(HttpServletRequest request) throws IOException {
        try {
            InputStream requestInputStream = request.getInputStream();
            if (requestInputStream != null) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = requestInputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                this.cachedBody = output.toByteArray();
            } else {
                this.cachedBody = new byte[0];
            }
        } catch (IOException e) {
            // Log error and initialize empty body
            this.cachedBody = new byte[0];
            log.error("Error in ExtendsHttpServletRequestWrapper > cacheRequestBody", e);
        }
    }

    private void cacheFormParameters(HttpServletRequest request) {
        try {
            // Cache the form parameters
            Map<String, String[]> parameterMap = request.getParameterMap();
            if (parameterMap != null) {
                cachedFormParameters = new HashMap<>(parameterMap);
            } else {
                cachedFormParameters = new HashMap<>();
            }
        } catch (Exception e) {
            // Initialize empty map if there's an error
            cachedFormParameters = new HashMap<>();
            log.error("Error in ExtendsHttpServletRequestWrapper > cacheFormParameters", e);
        }
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (isFormUrlEncoded) {
            // For form URL encoded, return empty stream as parameters are handled
            // separately
            return new CachedBodyServletInputStream(new byte[0]);
        } else {
            // For other content types, return cached body stream
            return new CachedBodyServletInputStream(this.cachedBody != null ? this.cachedBody : new byte[0]);
        }
    }

    @Override
    public BufferedReader getReader() throws IOException {
        if (isFormUrlEncoded) {
            // For form URL encoded, return empty reader as parameters are handled
            // separately
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(new byte[0]);
            return new BufferedReader(new InputStreamReader(byteArrayInputStream, getCharacterEncodingSafe()));
        } else {
            // For other content types, return cached body reader
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
                    this.cachedBody != null ? this.cachedBody : new byte[0]);
            return new BufferedReader(new InputStreamReader(byteArrayInputStream, getCharacterEncodingSafe()));
        }
    }

    private String getCharacterEncodingSafe() {
        try {
            String encoding = getCharacterEncoding();
            return encoding != null ? encoding : "UTF-8";
        } catch (Exception e) {
            return "UTF-8";
        }
    }

    @Override
    public String getParameter(String name) {
        if (isFormUrlEncoded && cachedFormParameters != null) {
            String[] values = cachedFormParameters.get(name);
            return values != null && values.length > 0 ? values[0] : null;
        } else {
            // For non-form requests, delegate to parent
            return super.getParameter(name);
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        if (isFormUrlEncoded && cachedFormParameters != null) {
            return Collections.unmodifiableMap(cachedFormParameters);
        } else {
            // For non-form requests, delegate to parent
            return super.getParameterMap();
        }
    }

    @Override
    public Enumeration<String> getParameterNames() {
        if (isFormUrlEncoded && cachedFormParameters != null) {
            return Collections.enumeration(cachedFormParameters.keySet());
        } else {
            // For non-form requests, delegate to parent
            return super.getParameterNames();
        }
    }

    @Override
    public String[] getParameterValues(String name) {
        if (isFormUrlEncoded && cachedFormParameters != null) {
            return cachedFormParameters.get(name);
        } else {
            // For non-form requests, delegate to parent
            return super.getParameterValues(name);
        }
    }

    /**
     * Get the cached body content as byte array.
     * 
     * @return cached body content, or empty array if not cached or if form URL
     *         encoded
     */
    public byte[] getContentAsByteArray() {
        return this.cachedBody != null ? this.cachedBody : new byte[0];
    }

    /**
     * Get the cached form parameters.
     * 
     * @return cached form parameters map, or null if not form URL encoded
     */
    public Map<String, String[]> getCachedFormParameters() {
        return isFormUrlEncoded && cachedFormParameters != null ? Collections.unmodifiableMap(cachedFormParameters)
                : null;
    }

    /**
     * Check if this is a form URL encoded request.
     * 
     * @return true if the request is form URL encoded
     */
    public boolean isFormUrlEncoded() {
        return isFormUrlEncoded;
    }

    private static class CachedBodyServletInputStream extends ServletInputStream {
        private final ByteArrayInputStream inputStream;

        public CachedBodyServletInputStream(byte[] cachedBody) {
            this.inputStream = new ByteArrayInputStream(cachedBody != null ? cachedBody : new byte[0]);
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
            throw new RuntimeException("Not implemented");
        }
    }
}