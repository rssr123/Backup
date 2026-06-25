package com.example.fms.fms.actuator;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Endpoint(id = "endpointsHealth")
public class CustomEndpointsHealthEndpoint {

    @ReadOperation
    public CustomHealthResponse endpointsHealth() {
        RestTemplate restTemplate = new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(10))
            .setReadTimeout(Duration.ofSeconds(10))
            .build();

        CustomHealthResponse response = new CustomHealthResponse();

        response.addEndpointHealth("arr", checkEndpointHealth(restTemplate, "https://192.168.1.186:8080/api/fms/v1/arr"));

        return response;
    }

    private EndpointHealth checkEndpointHealth(RestTemplate restTemplate, String url) {
        try {
            restTemplate.getForEntity(url, String.class);
            return new EndpointHealth("UP", "Available");
        } catch (Exception e) {
            return new EndpointHealth("DOWN", "Unavailable");
        }
    }

    static class CustomHealthResponse {
        private String status;
        private Map<String, EndpointHealth> endpoints = new HashMap<>();

        void addEndpointHealth(String endpoint, EndpointHealth health) {
            endpoints.put(endpoint, health);
            if (health.getStatus().equals("DOWN")) {
                status = "DOWN";
            } else {
                status = "UP";
            }
        }

        public String getStatus() {
            return status;
        }

        public Map<String, EndpointHealth> getEndpoints() {
            return endpoints;
        }
    }

    static class EndpointHealth {
        private String status;
        private String details;

        EndpointHealth(String status, String details) {
            this.status = status;
            this.details = details;
        }

        public String getStatus() {
            return status;
        }

        public String getDetails() {
            return details;
        }
    }
}

