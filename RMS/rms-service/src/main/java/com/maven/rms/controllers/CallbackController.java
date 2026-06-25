package com.maven.rms.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.utils.APIResponse;

@RestController
@RequestMapping("/api/rms/v1/callback")
public class CallbackController {

    private static final Logger log = LoggerFactory.getLogger(CallbackController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Receive any JSON body and log it directly to the database table.
     * The JSON body will be inserted into the remark column of the error log table.
     */
    @PostMapping("/receive")
    public ResponseEntity<?> receiveCallback(@RequestBody Map<String, Object> payload, HttpServletRequest request) {
        try {
            String bodyString = objectMapper.writeValueAsString(payload);

            // Direct database logging: insert incoming request body into remark column
            String clientIp = request != null ? request.getRemoteAddr() : "unknown";
            String clientBrowser = request != null ? request.getHeader("User-Agent") : "unknown";
            String message = "Callback received";
            String source = this.getClass().getName();
            String loginUser = "System";

            // insertIntoErrorLog("INFO", clientIp, clientBrowser, message, null, source,
            // loginUser, bodyString,
            // loginUser, loginUser);

            return APIResponse.SuccessResponse("receive callback and return to caller successfully");
        } catch (Exception e) {
            log.error("Failed to log callback payload", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.InternalServerError().getBody());
        }
    }

    /**
     * Call another endpoint supplied in the request. The request must include an
     * "endpoint" field; all other fields will be forwarded as JSON payload.
     * Example input:
     * {
     * "endpoint":"https://example.com/target",
     * "orn_no":"EPN20250514000044",
     * "pymt_status":"P",
     * "rcpt_no":"OR202350967500001",
     * "rcpt_dt":"2024-10-23T12:00:050"
     * }
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendCallback(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        try {
            if (requestBody == null || !requestBody.containsKey("endpoint")) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.InvalidFormat("Missing 'endpoint' field").getBody());
            }

            Object endpointObj = requestBody.get("endpoint");
            if (endpointObj == null) {
                return ResponseEntity.badRequest()
                        .body(APIResponse.InvalidFormat("'endpoint' value is null").getBody());
            }

            String endpoint = endpointObj.toString();

            // Prepare payload by removing endpoint key
            Map<String, Object> forwardPayload = new HashMap<>(requestBody);
            forwardPayload.remove("endpoint");

            String jsonPayload = objectMapper.writeValueAsString(forwardPayload);

            // First log: what we received (direct database insert)
            String receivedString = objectMapper.writeValueAsString(requestBody);
            String clientIp = request != null ? request.getRemoteAddr() : "unknown";
            String clientBrowser = request != null ? request.getHeader("User-Agent") : "unknown";
            // insertIntoErrorLog("INFO", clientIp, clientBrowser, "", null,
            // this.getClass().getName(), "System", receivedString, "System", "System");

            // Also log the forwarded payload (without endpoint) before sending
            insertIntoErrorLog("INFO", clientIp, clientBrowser, "sending to callback url", null,
                    this.getClass().getName(), "System", jsonPayload, "System", "System");

            // Use HTTPS-capable RestTemplate for external APIs
            RestTemplate restTemplate = createHttpsRestTemplate(endpoint);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, entity, String.class);

            // Log response status and headers first
            String responseHeaders = response.getHeaders() != null ? response.getHeaders().toString() : "";
            String responseStatus = String.format("status=%d, headers=%s", response.getStatusCodeValue(),
                    responseHeaders);
            insertIntoErrorLog("INFO", clientIp, clientBrowser, "response status and headers", null,
                    this.getClass().getName(), "System", responseStatus, "System", "System");

            // Log response body separately to avoid truncation
            String responseBody = response.getBody();
            insertIntoErrorLog("INFO", clientIp, clientBrowser, "response body from callback endpoint", null,
                    this.getClass().getName(), "System", responseBody != null ? responseBody : "null", "System",
                    "System");

            Map<String, Object> result = new HashMap<>();
            result.put("statusCode", response.getStatusCodeValue());
            result.put("responseBody", response.getBody());

            return APIResponse.SuccessResponse(result);
        } catch (Exception e) {
            log.error("Failed to call endpoint", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(APIResponse.InternalServerError().getBody());
        }
    }

    /**
     * Insert a row into the configured error log table using JdbcTemplate.
     * Column order matches SpringJdbcAppender.buildInsertSql()
     */
    private void insertIntoErrorLog(String level,
            String clientIp,
            String clientBrowser,
            String message,
            String stacktrace,
            String source,
            String loginUser,
            String remark,
            String createdBy,
            String modifiedBy) {
        try {
            String tableName = System.getProperty("log4j2.db.tableName", "rms_error_log");

            String insertSql = "INSERT INTO " + tableName +
                    " (level, client_ip, client_browser, msg, stacktrace, source, login_nm, remark, dt_created, dt_modified, created_by, modified_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT, CURRENT, ?, ?)";

            jdbcTemplate.update(insertSql,
                    level,
                    clientIp,
                    clientBrowser,
                    message,
                    stacktrace,
                    source,
                    loginUser,
                    remark,
                    createdBy,
                    modifiedBy);
        } catch (Exception e) {
            log.error("Failed to insert into rms_error_log", e);
        }
    }

    // Direct database logging replaces appender-based logging for explicit control

    private RestTemplate createHttpsRestTemplate(String endpoint) {
        try {
            if (endpoint == null) {
                return new RestTemplate();
            }

            java.net.URI uri = new java.net.URI(endpoint);
            String scheme = uri.getScheme();
            String host = uri.getHost();

            // For HTTPS endpoints (both localhost testing and external APIs)
            if ("https".equalsIgnoreCase(scheme)) {

                // For localhost testing - use relaxed SSL (trust all certs)
                if ("localhost".equalsIgnoreCase(host) || "127.0.0.1".equals(host)) {
                    log.info("Creating relaxed SSL RestTemplate for localhost testing: {}", endpoint);
                    TrustStrategy acceptingTrustStrategy = (x509Certificates, s) -> true;
                    SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
                    sslContextBuilder.loadTrustMaterial(null, acceptingTrustStrategy);
                    javax.net.ssl.SSLContext sslContext = sslContextBuilder.build();

                    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext,
                            NoopHostnameVerifier.INSTANCE);
                    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
                    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
                            httpClient);
                    return new RestTemplate(requestFactory);
                }

                // For external HTTPS APIs - use proper SSL with system truststore
                else {
                    log.info("Creating secure SSL RestTemplate for external API: {}", endpoint);
                    // Use HttpComponents with proper SSL context for external APIs
                    SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
                    // Load default truststore (contains CA certificates)
                    sslContextBuilder.loadTrustMaterial((java.security.KeyStore) null, (TrustStrategy) null);
                    javax.net.ssl.SSLContext sslContext = sslContextBuilder.build();

                    SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);
                    CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
                    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(
                            httpClient);
                    return new RestTemplate(requestFactory);
                }
            }

            // For HTTP endpoints - use default RestTemplate
            log.info("Creating default RestTemplate for HTTP endpoint: {}", endpoint);
            return new RestTemplate();

        } catch (Exception e) {
            log.error("Failed to create HTTPS RestTemplate for endpoint: {}", endpoint, e);
            // Fallback to default RestTemplate
            return new RestTemplate();
        }
    }

}
