package com.maven.rms.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maven.rms.models.ARCRequest;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.FMSARC;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FMSARCService {
    @Value("${fmsarc.api.url}")
    private String apiUrl;

    @Value("${fmsarc.api.name}")
    private String apiName;

    @Value("${fms.ibm-client-id}")
    private String IBMClientID;

    @Autowired
    private CommonService commonSvc;

    public FMSARC sendCustomerRequest(ARCRequest request) {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HostnameVerifier allHostsValid = (hostname, session) -> true;
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

            URL url = new URL(apiUrl + "/" + apiName);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-IBM-Client-Id", IBMClientID);
            connection.setDoOutput(true);

            // String requestBody = new com.google.gson.Gson().toJson(request);
            String requestBody = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(request);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            log.debug("POST Response Code :: {}", responseCode);

            BufferedReader br;
            if (responseCode >= 200 && responseCode < 300) {
                br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream(), "utf-8"));
            }

            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }

            log.debug("Response Body: {}", response.toString());
            connection.disconnect();

            try {
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("FMSCreditMemo");
                extAudit.setI_request_body(requestBody);
                extAudit.setI_response_body(response.toString());
                extAudit.setI_rms_batch_no(null);
                extAudit.setI_direction("Outgoing");
                extAudit.setI_remark(null);
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for FMS Credit Memo: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }

            JsonObject root = JsonParser.parseString(response.toString()).getAsJsonObject();

            // Map root to FMSARC
            FMSARC fmsarc = new FMSARC();
            fmsarc.setUsrIdentityNbr(root.get("UsrIdentityNbr").getAsString());
            fmsarc.setCustomerID(root.get("CustomerID").getAsString());
            if (root.has("CustomerName")) {
                fmsarc.setCustomerName(root.get("CustomerName").getAsString());
            }
            // fmsarc.setCustomerName(root.get("CustomerName").getAsString());
            fmsarc.setStatus(root.get("Status").getAsString());
            fmsarc.setMessage(root.get("Message").getAsString());
            fmsarc.setDate(root.get("Date").getAsString());

            return fmsarc;

        } catch (Exception e) {
            log.error("Error sending customer request", e);
            return null;
        }
    }
}