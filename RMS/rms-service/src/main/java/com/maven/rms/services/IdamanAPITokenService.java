package com.maven.rms.services;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maven.rms.interfaces.IIdamanApiTokenService;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.IdamanAPITokenReq;
import com.maven.rms.repositories.IdamanApiTokenRepository;

import okhttp3.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class IdamanAPITokenService implements IIdamanApiTokenService {

    @Autowired
    IdamanApiTokenRepository itRepo;

    @Value("${idaman.api.tokenurl}")
    private String tokenUrl;

    @Value("${idaman.api.xibmclientid}")
    private String clientId;

    @Value("${idaman.api.clientSecret}")
    private String clientSecret;

    @Value("${idaman.api.scope}")
    private String scope;

    @Value("${idaman.api.grant_type}")
    private String grant_type;

    @Autowired
    private CommonService commonSvc;

    @Override
    public String getOAuth2Token() throws IOException {

        String credentials = clientId + ":" + clientSecret;
        String basicAuth = "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes());

        // Unsafe for dev only!
        OkHttpClient client = getUnsafeOkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("grant_type", grant_type)
                .add("scope", scope)
                .build();

        Request request = new Request.Builder()
                .url(tokenUrl)
                .addHeader("Authorization", basicAuth)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Error: HTTP " + response.code() + " - " + response.message());
                return "Error: HTTP " + response.code() + " - " + response.message();
            }

            ResponseBody responseBody = response.body();
            String rawResponse = responseBody != null ? responseBody.string() : null;

            if (rawResponse == null) {
                log.error("Error: Empty response body");
                return "Error: Empty response body";
            }

            try {
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("IdamanToken");
                extAudit.setI_request_body("{"+ "\"grant_type\":\"" + grant_type + "\","+ "\"scope\":\"" + scope + "\""+"}");
                extAudit.setI_response_body(rawResponse);
                extAudit.setI_rms_batch_no(null);
                extAudit.setI_direction("Outgoing");
                extAudit.setI_remark(null);
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for IdamanToken: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }

            // The regex pattern for extracting access_token value
            String pattern = "\"access_token\":\"([^\"]+)\"";

            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(rawResponse);
            if (m.find()) {
                // The value of access_token is in group 1
                String accessToken = m.group(1);
                return accessToken;
            } else {
                log.error("Idaman Token Access token not found.");
                return "Idaman Token Access token not found.";
            }

        } catch (Exception e) {
            log.error("Idaman Token " + e.getMessage());
            return "Exception occurred: " + e.getMessage();
        }
    }

    @Override
    // @Transactional
    public Integer updidamantoken(IdamanAPITokenReq bodyReq) {
        Integer result = 0;

        // result = itRepo.updidamantoken(bodyReq);
        try {
            result = itRepo.updidamantoken(bodyReq);
        } catch (IllegalStateException | InvalidDataAccessApiUsageException ex) {
            if (ex.getMessage().contains("Session/EntityManager is closed")) {
                // Optionally log to console but NOT to your error log table
                // System.out.println("Skipped: EntityManager is closed (likely expired
                // session).");
            } else {
                // rethrow or handle other unexpected exceptions
                log.error("Idaman Token Update " + ex.getMessage());
                throw ex;
            }
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public String getidamantoken() {
        String result = "";

        result = itRepo.getidamantoken();

        return result;
    }

    private OkHttpClient getUnsafeOkHttpClient() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] chain, String authType) {
                        }

                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
