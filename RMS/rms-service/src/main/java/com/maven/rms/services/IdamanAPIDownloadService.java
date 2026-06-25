package com.maven.rms.services;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IIdamanApiDownloadService;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.IdamanAPIDownload;
import com.maven.rms.models.IdamanAPIDownloadRequest;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.gson.Gson;

@Service
@Slf4j
public class IdamanAPIDownloadService implements IIdamanApiDownloadService {

    @Autowired
    IdamanAPITokenService itService;

    @Value("${idaman.api.url}")
    private String api_url;

    @Value("${idaman.api.name}")
    private String api_name;

    @Value("${idaman.requestor.id}")
    private String requestorId;

    @Value("${idaman.profile.name}")
    private String profile_nm;

    @Value("${idaman.bearer.token}")
    private String bearer_tk;

    @Value("${idaman.api.key}")
    private String api_key;

    @Value("${idaman.api.token}")
    private String api_tk;

    @Value("${idaman.api.xibmclientid}")
    private String xibmclientid;

    @Value("${idaman.api.public}")
    private Integer group;

    @Autowired
    private CommonService commonSvc;

    @SuppressWarnings("deprecation")
    public List<IdamanAPIDownload> idaman_api_downloadDoc(IdamanAPIDownloadRequest idamanAPIDownloadRequest)
            throws IOException {
        // 20251105, added error handling for Gson/Idaman API failures
        return idaman_api_downloadDoc_v2(idamanAPIDownloadRequest);
    }

    // Original version - preserved for rollback
    @SuppressWarnings("deprecation")
    public List<IdamanAPIDownload> idaman_api_downloadDoc_v1(IdamanAPIDownloadRequest idamanAPIDownloadRequest)
            throws IOException {
        if (group == 0) {
            Response response = null;
            try {
                String authToken = itService.getidamantoken();
                List<IdamanAPIDownload> result = new ArrayList<>();

                // Unsafe for dev only!
                OkHttpClient client = getUnsafeOkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");

                String stringBody = "{\r\n    \"RequestorID\": \"" + requestorId + "\",\r\n" +
                        "    \"ProfileName\": \"" + profile_nm + "\",\r\n" +
                        "    \"RefNo1\": \"" + idamanAPIDownloadRequest.getRefNo1() + "\",\r\n" +
                        "    \"VerID\": \"" + idamanAPIDownloadRequest.getVerID() + "\",\r\n" +
                        "    \"SourceSysDocRefID\":\"" + idamanAPIDownloadRequest.getSourceSysDocRefID() + "\"\r\n}";
                log.debug("DEBUG: download requestBody-----------\n" + stringBody + "\n-----------");

                RequestBody body = RequestBody.create(mediaType, stringBody);
                Request request = new Request.Builder()
                        .url(api_url + "/" + api_name + "downloadDocument")
                        .method("POST", body)
                        .addHeader("ApiKey", api_key)
                        .addHeader("ApiToken", api_tk)
                        .addHeader("X-IBM-Client-Id", xibmclientid)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer " + authToken)
                        .build();
                response = client.newCall(request).execute();
                String responseBody = response.body().string();
                log.debug("DEBUG: download responseBody----------- rspcd: " + response.code() + "\n"
                        + responseBody + "\n-----------");

                try {
                    ExtAudit extAudit = new ExtAudit();
                    extAudit.setI_module_nm("IdamanDownload");
                    extAudit.setI_request_body(stringBody);
                    extAudit.setI_response_body(responseBody);
                    extAudit.setI_rms_batch_no(null);
                    extAudit.setI_direction("Outgoing");
                    extAudit.setI_remark(null);
                    commonSvc.sp_insextaudit(extAudit);
                } catch (Exception e) {
                    log.error("Error in sp_insextaudit for IdamanDownload: " + e.getMessage() + ", "
                            + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
                }

                Gson gson = new Gson();
                IdamanAPIDownload dld = gson.fromJson(responseBody, IdamanAPIDownload.class);

                // Check the response and handle errors
                if (dld.getDesc() != null && dld.getDesc().contains("Successful")) {
                    log.debug("Idaman status: " + dld.getDesc());
                } else if (dld.getDesc() != null && dld.getDesc().contains("No item found")) {
                    log.error("No item found for RefNo1: " + idamanAPIDownloadRequest.getRefNo1() +
                            " and VerID: " + idamanAPIDownloadRequest.getVerID());
                } else {
                    log.error("Idaman Code: " + dld.getCode() + " - Idaman Unknown error: " + dld.getDesc());
                }

                if ("1".equals(dld.getCode()) && dld.getData() != null) {
                    IdamanAPIDownload dl = new IdamanAPIDownload();
                    dl.setId(dld.getId());
                    dl.setCode(dld.getCode());
                    dl.setDesc(dld.getDesc());
                    dl.setFile_content(dld.getData().getFileContent());
                    dl.setFile_nm(dld.getData().getFileName());
                    result.add(dl);
                } else {
                    // Request failed
                    log.error("DL Request failed with code: " + response.code() + " | Desc: " + dld.getDesc());
                    IdamanAPIDownload dl = new IdamanAPIDownload();
                    dl.setId(dld.getId());
                    dl.setCode(dld.getCode());
                    dl.setDesc(dld.getDesc());
                    result.add(dl);
                }

                return result;
            } finally {
                if (response != null) {
                    response.close();
                }
            }
        } else {
            // Group != 0 logic (public API)
            List<IdamanAPIDownload> result = new ArrayList<>();
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");

            String stringBody = "{\r\n    \"RequestorID\": \"" + requestorId + "\",\r\n" +
                    "    \"ProfileName\": \"" + profile_nm + "\",\r\n" +
                    "    \"RefNo1\": \"" + idamanAPIDownloadRequest.getRefNo1() + "\",\r\n" +
                    "    \"VerID\": \"" + idamanAPIDownloadRequest.getVerID() + "\",\r\n" +
                    "    \"SourceSysDocRefID\":\"" + idamanAPIDownloadRequest.getSourceSysDocRefID() + "\"\r\n}";
            log.debug("DEBUG: download requestBody-----------\n" + stringBody + "\n-----------");

            RequestBody body = RequestBody.create(mediaType, stringBody);
            Request request = new Request.Builder()
                    .url(api_url + "/" + api_name + "download")
                    .method("POST", body)
                    .addHeader("ApiKey", api_key)
                    .addHeader("ApiToken", api_tk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + bearer_tk)
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();
            log.debug("DEBUG: download responseBody----------- rspcd: " + response.code() + "\n"
                    + responseBody + "\n-----------");

            try {
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("IdamanDownload");
                extAudit.setI_request_body(stringBody);
                extAudit.setI_response_body(responseBody);
                extAudit.setI_rms_batch_no(null);
                extAudit.setI_direction("Outgoing");
                extAudit.setI_remark(null);
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for IdamanDownload: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }

            Gson gson = new Gson();
            IdamanAPIDownload dld = gson.fromJson(responseBody, IdamanAPIDownload.class);

            if (dld.getDesc() != null && dld.getDesc().contains("Successful")) {
                log.debug("Idaman status: " + dld.getDesc());
            } else if (dld.getDesc() != null && dld.getDesc().contains("No item found")) {
                log.error("No item found for RefNo1: " + idamanAPIDownloadRequest.getRefNo1() +
                        " and VerID: " + idamanAPIDownloadRequest.getVerID());
            } else {
                log.error("Idaman Code: " + dld.getCode() + " - Idaman Unknown error: " + dld.getDesc());
            }

            if ("1".equals(dld.getCode()) && dld.getData() != null) {
                IdamanAPIDownload dl = new IdamanAPIDownload();
                dl.setId(dld.getId());
                dl.setCode(dld.getCode());
                dl.setDesc(dld.getDesc());
                dl.setFile_content(dld.getData().getFileContent());
                dl.setFile_nm(dld.getData().getFileName());
                result.add(dl);
            } else {
                log.error("Request failed with code: " + response.code() + " | Desc: " + dld.getDesc());
                IdamanAPIDownload dl = new IdamanAPIDownload();
                dl.setId(dld.getId());
                dl.setCode(dld.getCode());
                dl.setDesc(dld.getDesc());
                result.add(dl);
            }

            return result;
        }
    }

    // New version with error handling for Gson parsing
    @SuppressWarnings("deprecation")
    public List<IdamanAPIDownload> idaman_api_downloadDoc_v2(IdamanAPIDownloadRequest idamanAPIDownloadRequest)
            throws IOException {
        if (group == 0) {
            Response response = null;
            try {
                String authToken = itService.getidamantoken();
                List<IdamanAPIDownload> result = new ArrayList<>();

                OkHttpClient client = getUnsafeOkHttpClient();
                MediaType mediaType = MediaType.parse("application/json");

                String stringBody = "{\r\n    \"RequestorID\": \"" + requestorId + "\",\r\n" +
                        "    \"ProfileName\": \"" + profile_nm + "\",\r\n" +
                        "    \"RefNo1\": \"" + idamanAPIDownloadRequest.getRefNo1() + "\",\r\n" +
                        "    \"VerID\": \"" + idamanAPIDownloadRequest.getVerID() + "\",\r\n" +
                        "    \"SourceSysDocRefID\":\"" + idamanAPIDownloadRequest.getSourceSysDocRefID() + "\"\r\n}";
                log.debug("DEBUG: download requestBody-----------\n" + stringBody + "\n-----------");

                RequestBody body = RequestBody.create(mediaType, stringBody);
                Request request = new Request.Builder()
                        .url(api_url + "/" + api_name + "downloadDocument")
                        .method("POST", body)
                        .addHeader("ApiKey", api_key)
                        .addHeader("ApiToken", api_tk)
                        .addHeader("X-IBM-Client-Id", xibmclientid)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Authorization", "Bearer " + authToken)
                        .build();
                response = client.newCall(request).execute();
                String responseBody = response.body().string();
                log.debug("DEBUG: download responseBody----------- rspcd: " + response.code() + "\n"
                        + responseBody + "\n-----------");

                // ✅ Audit logging
                try {
                    ExtAudit extAudit = new ExtAudit();
                    extAudit.setI_module_nm("IdamanDownload");
                    extAudit.setI_request_body(stringBody);
                    extAudit.setI_response_body(responseBody);
                    extAudit.setI_rms_batch_no(null);
                    extAudit.setI_direction("Outgoing");
                    extAudit.setI_remark(null);
                    commonSvc.sp_insextaudit(extAudit);
                } catch (Exception e) {
                    log.error("Error in sp_insextaudit for IdamanDownload: " + e.getMessage() + ", "
                            + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
                }

                Gson gson = new Gson();
                IdamanAPIDownload dld = null;

                // ✅ Handle Gson parsing errors
                try {
                    dld = gson.fromJson(responseBody, IdamanAPIDownload.class);
                } catch (com.google.gson.JsonSyntaxException e) {
                    log.error(
                            "Idaman API returned non-JSON response for RefNo1: {} VerID: {} | Error: {} | Response: {}",
                            idamanAPIDownloadRequest.getRefNo1(), idamanAPIDownloadRequest.getVerID(),
                            e.getMessage(), responseBody, e);

                    // ✅ Return error response instead of throwing exception
                    IdamanAPIDownload errorResponse = new IdamanAPIDownload();
                    errorResponse.setCode("0");
                    errorResponse.setDesc("API returned invalid response format: " + responseBody);
                    result.add(errorResponse);
                    return result;
                }

                // Check the response and handle errors
                if (dld.getDesc() != null && dld.getDesc().contains("Successful")) {
                    log.debug("Idaman download successful for RefNo1: {} VerID: {}",
                            idamanAPIDownloadRequest.getRefNo1(), idamanAPIDownloadRequest.getVerID());
                } else if (dld.getDesc() != null && dld.getDesc().contains("No item found")) {
                    log.error("No item found for RefNo1: {} VerID: {}",
                            idamanAPIDownloadRequest.getRefNo1(), idamanAPIDownloadRequest.getVerID());
                } else {
                    log.error("Idaman download error for RefNo1: {} VerID: {} | Code: {} Desc: {}",
                            idamanAPIDownloadRequest.getRefNo1(), idamanAPIDownloadRequest.getVerID(),
                            dld.getCode(), dld.getDesc());
                }

                if ("1".equals(dld.getCode()) && dld.getData() != null) {
                    IdamanAPIDownload dl = new IdamanAPIDownload();
                    dl.setId(dld.getId());
                    dl.setCode(dld.getCode());
                    dl.setDesc(dld.getDesc());
                    dl.setFile_content(dld.getData().getFileContent());
                    dl.setFile_nm(dld.getData().getFileName());
                    result.add(dl);
                } else {
                    // Request failed
                    log.error("Download failed - HTTP code: {} | Idaman code: {} | Desc: {}",
                            response.code(), dld.getCode(), dld.getDesc());
                    IdamanAPIDownload dl = new IdamanAPIDownload();
                    dl.setId(dld.getId());
                    dl.setCode(dld.getCode());
                    dl.setDesc(dld.getDesc());
                    result.add(dl);
                }

                return result;

            } catch (IOException e) {
                // ✅ Handle IO exceptions (network failures, timeouts)
                log.error("IOException during Idaman download for RefNo1: {} VerID: {} | Error: {}",
                        idamanAPIDownloadRequest.getRefNo1(), idamanAPIDownloadRequest.getVerID(),
                        e.getMessage(), e);
                throw e; // Re-throw to be handled by controller

            } catch (Exception e) {
                // ✅ Handle unexpected exceptions
                log.error("Unexpected error during Idaman download for RefNo1: {} VerID: {} | Error: {}",
                        idamanAPIDownloadRequest.getRefNo1(), idamanAPIDownloadRequest.getVerID(),
                        e.getMessage(), e);

                List<IdamanAPIDownload> result = new ArrayList<>();
                IdamanAPIDownload errorResponse = new IdamanAPIDownload();
                errorResponse.setCode("0");
                errorResponse.setDesc("System error: " + e.getMessage());
                result.add(errorResponse);
                return result;

            } finally {
                if (response != null) {
                    response.close();
                }
            }
        } else {
            // ✅ Group != 0 (public API) - apply same error handling
            return idaman_api_downloadDoc_v2_public(idamanAPIDownloadRequest);
        }
    }

    // Helper method for public API (group != 0)
    private List<IdamanAPIDownload> idaman_api_downloadDoc_v2_public(IdamanAPIDownloadRequest idamanAPIDownloadRequest)
            throws IOException {
        Response response = null;
        try {
            List<IdamanAPIDownload> result = new ArrayList<>();
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");

            String stringBody = "{\r\n    \"RequestorID\": \"" + requestorId + "\",\r\n" +
                    "    \"ProfileName\": \"" + profile_nm + "\",\r\n" +
                    "    \"RefNo1\": \"" + idamanAPIDownloadRequest.getRefNo1() + "\",\r\n" +
                    "    \"VerID\": \"" + idamanAPIDownloadRequest.getVerID() + "\",\r\n" +
                    "    \"SourceSysDocRefID\":\"" + idamanAPIDownloadRequest.getSourceSysDocRefID() + "\"\r\n}";
            log.debug("DEBUG: download requestBody-----------\n" + stringBody + "\n-----------");

            RequestBody body = RequestBody.create(mediaType, stringBody);
            Request request = new Request.Builder()
                    .url(api_url + "/" + api_name + "download")
                    .method("POST", body)
                    .addHeader("ApiKey", api_key)
                    .addHeader("ApiToken", api_tk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + bearer_tk)
                    .build();
            response = client.newCall(request).execute();
            String responseBody = response.body().string();
            log.debug("DEBUG: download responseBody----------- rspcd: " + response.code() + "\n"
                    + responseBody + "\n-----------");

            try {
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("IdamanDownload");
                extAudit.setI_request_body(stringBody);
                extAudit.setI_response_body(responseBody);
                extAudit.setI_rms_batch_no(null);
                extAudit.setI_direction("Outgoing");
                extAudit.setI_remark(null);
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for IdamanDownload: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }

            Gson gson = new Gson();
            IdamanAPIDownload dld = null;

            // ✅ Handle Gson parsing errors
            try {
                dld = gson.fromJson(responseBody, IdamanAPIDownload.class);
            } catch (com.google.gson.JsonSyntaxException e) {
                log.error(
                        "Idaman API (public) returned non-JSON response for RefNo1: {} VerID: {} | Error: {} | Response: {}",
                        idamanAPIDownloadRequest.getRefNo1(), idamanAPIDownloadRequest.getVerID(),
                        e.getMessage(), responseBody, e);

                IdamanAPIDownload errorResponse = new IdamanAPIDownload();
                errorResponse.setCode("0");
                errorResponse.setDesc("API returned invalid response format: " + responseBody);
                result.add(errorResponse);
                return result;
            }

            if (dld.getDesc() != null && dld.getDesc().contains("Successful")) {
                log.debug("Idaman download (public) successful for RefNo1: {} VerID: {}",
                        idamanAPIDownloadRequest.getRefNo1(), idamanAPIDownloadRequest.getVerID());
            } else if (dld.getDesc() != null && dld.getDesc().contains("No item found")) {
                log.error("No item found (public) for RefNo1: {} VerID: {}",
                        idamanAPIDownloadRequest.getRefNo1(), idamanAPIDownloadRequest.getVerID());
            } else {
                log.error("Idaman download error (public) for RefNo1: {} VerID: {} | Code: {} Desc: {}",
                        idamanAPIDownloadRequest.getRefNo1(), idamanAPIDownloadRequest.getVerID(),
                        dld.getCode(), dld.getDesc());
            }

            if ("1".equals(dld.getCode()) && dld.getData() != null) {
                IdamanAPIDownload dl = new IdamanAPIDownload();
                dl.setId(dld.getId());
                dl.setCode(dld.getCode());
                dl.setDesc(dld.getDesc());
                dl.setFile_content(dld.getData().getFileContent());
                dl.setFile_nm(dld.getData().getFileName());
                result.add(dl);
            } else {
                log.error("Download failed (public) - HTTP code: {} | Idaman code: {} | Desc: {}",
                        response.code(), dld.getCode(), dld.getDesc());
                IdamanAPIDownload dl = new IdamanAPIDownload();
                dl.setId(dld.getId());
                dl.setCode(dld.getCode());
                dl.setDesc(dld.getDesc());
                result.add(dl);
            }

            return result;

        } finally {
            if (response != null) {
                response.close();
            }
        }
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
                    .connectTimeout(70, TimeUnit.SECONDS)
                    .readTimeout(70, TimeUnit.SECONDS)
                    .writeTimeout(70, TimeUnit.SECONDS)
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
