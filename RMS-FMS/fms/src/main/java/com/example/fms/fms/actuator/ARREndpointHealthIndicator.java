package com.example.fms.fms.actuator;

import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.example.fms.fms.models.FMSARRJson;
import com.example.fms.fms.models.GenericValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("arrEndpoint")
public class ARREndpointHealthIndicator implements HealthIndicator {

    private final ObjectMapper objectMapper;

    public ARREndpointHealthIndicator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Health health() {
        
        RestTemplate restTemplate = new RestTemplateBuilder()
                                        .setConnectTimeout(Duration.ofSeconds(30))
                                        .setReadTimeout(Duration.ofSeconds(30))
                                        .build();

        String url = "https://192.168.1.186:8080/api/fms/v1/arr";

        FMSARRJson inputObject = preparePayload();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String requestEntityString;
        try {
            requestEntityString = objectMapper.writeValueAsString(inputObject);
        } catch (JsonProcessingException e) {
            return Health.down().withDetail("FMS ARR Endpoint", "Error creating request JSON").build();
        }

        HttpEntity<String> requestEntity = new HttpEntity<>(requestEntityString, headers);

        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                return Health.up().withDetail("FMS ARR Endpoint", "Available").build();
            } else {
                String responseBody = response.getBody();
                return Health.down().withDetail("FMS ARR Endpoint", "Unavailable")
                            .withDetail("Error", response.getStatusCode() + " : " + responseBody).build();
            }
        } catch (HttpStatusCodeException e) {
            String responseBody = e.getResponseBodyAsString();
            return Health.down().withDetail("FMS ARR Endpoint", "Unavailable")
                        .withDetail("Error", e.getStatusCode() + " : " + responseBody).build();
        } catch (Exception e) {
            return Health.down().withDetail("FMS ARR Endpoint", "Unavailable")
                        .withDetail("Error", e.getMessage()).build();
        }
    }

    private FMSARRJson preparePayload() {
        FMSARRJson inputObject = new FMSARRJson();
        Date today = new Date();

        inputObject.setType(new GenericValue<>("Payment"));
        inputObject.setBranch(new GenericValue<>("0"));
        inputObject.setApplicationDate(new GenericValue<>(today));
        inputObject.setCashAccount(new GenericValue<>("SRHB-02"));
        inputObject.setCustomerID(new GenericValue<>("PG0069783"));
        inputObject.setDescription(new GenericValue<>("PAYMENT REF RMS"));
        inputObject.setPaymentAmount(new GenericValue<>(100.0));
        inputObject.setPaymentMethod(new GenericValue<>("BDR"));
        inputObject.setPaymentRef(new GenericValue<>("RMS TEST 123"));

        FMSARRJson.Custom custom = new FMSARRJson.Custom();
        FMSARRJson.CurrentDocument currentDocument = new FMSARRJson.CurrentDocument();
        FMSARRJson.Attribute attribute = new FMSARRJson.Attribute();
        attribute.setType("CustomStringField");
        attribute.setValue("RMS");
        currentDocument.setAttributeSYSNAME(attribute);
        custom.setCurrentDocument(currentDocument);
        inputObject.setCustom(custom);

        // List<FMSARRJson.Charge> charges = new ArrayList<>();
        // FMSARRJson.Charge charge = new FMSARRJson.Charge();
        // charge.setAmount(new GenericValue<>(0.0));
        // charge.setDocType(new GenericValue<>("doc_type_c"));
        // charge.setEntityType(new GenericValue<>("entity_type"));
        // charge.setOffsetSubaccount(new GenericValue<>("offset_subacct"));
        // charges.add(charge);
        // inputObject.setCharges(charges);

        List<FMSARRJson.DocumentToApply> docs = new ArrayList<>();
        FMSARRJson.DocumentToApply doc = new FMSARRJson.DocumentToApply();
        doc.setDocType(new GenericValue<>("doc_type_b"));
        doc.setReferenceNbr(new GenericValue<>("fms_ref_no"));
        docs.add(doc);
        inputObject.setDocumentsToApply(docs);

        return inputObject;
    }
}
