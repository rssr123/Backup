package com.maven.rms.services;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maven.rms.interfaces.IIdamanApiUploadService;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.IdamanAPIUpload;
import com.maven.rms.models.IdamanAPIUploadReq;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@Slf4j
public class IdamanAPIUploadService implements IIdamanApiUploadService {

    @Autowired
    private CommonService commonSvc;

    @Autowired
    IdamanAPITokenService itService;

    @Value("${idaman.api.url}")
    private String api_url;

    @Value("${idaman.api.name}")
    private String api_name;

    @Value("${idaman.requestor.id}")
    private String requestorId;

    @Value("${idaman.class.id}")
    private String classId;

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

    @Transactional
    public List<IdamanAPIUpload> idaman_api_uploadDoc(IdamanAPIUploadReq req) throws IOException {

        // v1 is version at before 20250805
        // return idaman_api_uploadDoc_v1(req);

        return idaman_api_uploadDoc_v2(req);
    }

    @SuppressWarnings("deprecation")
    @Transactional
    public List<IdamanAPIUpload> idaman_api_uploadDoc_v1(IdamanAPIUploadReq req) throws IOException {

        if (group == 0) {
            String authToken = itService.getidamantoken();
            List<IdamanAPIUpload> result = new ArrayList<>();

            if (req.getFileContent().startsWith("data:"))
                req.setFileContent(req.getFileContent().substring(req.getFileContent().indexOf(',') + 1));

            req.setFileContent(req.getFileContent().replaceAll("\\s", "").replace(":", ""));

            // Unsafe for dev only!
            OkHttpClient client = getUnsafeOkHttpClient();
            // OkHttpClient client = new OkHttpClient().newBuilder().build();

            MediaType mediaType = MediaType.parse("application/json");
            String stringBody = "{\r\n    \"RequestorID\":\"" + requestorId + "\"," +
                    "\r\n    \"ClassID\":\"" + classId + "\"," +
                    "\r\n    \"ProfileName\": \"" + profile_nm + "\"," +
                    "\r\n    \"ProfileFields\": \"SourceSystem=" + req.getSourceSystem() + ";RefNo1=" + req.getRefNo1()
                    + ";FormCode=" + req.getFormCode() +
                    ";DocumentDate=" + req.getDocDate() + ";ReceivedDate=" + req.getReceivedDate() + ";TotalPage="
                    + req.getTotalPage() + ";StateCode=" + req.getStateCode() +
                    ";SourceData=" + req.getSourceData() + ";DateFiler=" + req.getDateFiler() + ";SourceSysTRXNo="
                    + req.getSourceSysTRXNo() + ";SourceSysDocRefID=" + req.getSourceSysDocRefID() +
                    ";RefNo2=" + req.getRefNo2() + ";RefNo3=" + req.getRefNo3() + ";RefNo4=" + req.getRefNo4()
                    + ";RefNo5=" + req.getRefNo5() + ";RefNo6=" + req.getRefNo6() + ";RefNo7=" + req.getRefNo7()
                    + ";RefNo8=" + req.getRefNo8() + "\"," +
                    "\r\n    \"FileContent\":\"" + req.getFileContent() + "\"," +
                    "\r\n    \"FileName\": \"" + req.getFileName() + "\"\r\n}";
            RequestBody body = RequestBody.create(mediaType, stringBody);
            Request request = new Request.Builder()
                    .url(api_url + "/" + api_name + "uploadDocument")
                    .method("POST", body)
                    .addHeader("ApiKey", api_key)
                    .addHeader("ApiToken", api_tk)
                    .addHeader("X-IBM-Client-Id", xibmclientid)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + authToken)
                    .build();
            log.debug("DEBUG: upload requestBody-----------\n" + stringBody + "\n-----------");
            // Execute the request
            Response response = client.newCall(request).execute();
            // Request was successful
            String responseBody = response.body() != null ? response.body().string() : "";
            log.debug("DEBUG: upload responseBody----------- rspcd: " + response.code() + "\n"
                    + responseBody + "\n-----------");

            try {
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("IdamanUpload");
                extAudit.setI_request_body(stringBody);
                extAudit.setI_response_body(responseBody);
                extAudit.setI_rms_batch_no(null);
                extAudit.setI_direction("Outgoing");
                extAudit.setI_remark(null);
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for IdamanUpload: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }

            Gson gson = new Gson();
            IdamanAPIUpload up = gson.fromJson(responseBody, IdamanAPIUpload.class);

            if ("1".equals(up.getCode()) && up.getData() != null)
                for (int i = 0; i < 1; i++) {
                    IdamanAPIUpload upl = new IdamanAPIUpload();
                    upl.setId(up.getId());
                    upl.setCode(up.getCode());
                    upl.setDesc(up.getDesc());
                    upl.setVerid(up.getData().getVerID());
                    result.add(upl);
                }
            else {
                // Request failed
                log.error("UL Request failed with code: " + response.code() + " | Desc: " + up.getDesc()
                        + " | RequestBody: "
                        + stringBody + " | ResponseBody: " + responseBody);
                for (int i = 0; i < 1; i++) {
                    IdamanAPIUpload upl = new IdamanAPIUpload();
                    upl.setId(up.getId());
                    upl.setCode(up.getCode());
                    upl.setDesc(up.getDesc());
                    result.add(upl);
                }
            }

            return result;
        } else {
            List<IdamanAPIUpload> result = new ArrayList<>();

            if (req.getFileContent().startsWith("data:"))
                req.setFileContent(req.getFileContent().substring(req.getFileContent().indexOf(',') + 1));

            req.setFileContent(req.getFileContent().replaceAll("\\s", "").replace(":", ""));

            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            String stringBody = "{\r\n    \"RequestorID\":\"" + requestorId + "\"," +
                    "\r\n    \"ClassID\":\"" + classId + "\"," +
                    "\r\n    \"ProfileName\": \"" + profile_nm + "\"," +
                    "\r\n    \"ProfileFields\": \"SourceSystem=" + req.getSourceSystem() + ";RefNo1=" + req.getRefNo1()
                    + ";FormCode=" + req.getFormCode() +
                    ";DocumentDate=" + req.getDocDate() + ";ReceivedDate=" + req.getReceivedDate() + ";TotalPage="
                    + req.getTotalPage() + ";StateCode=" + req.getStateCode() +
                    ";SourceData=" + req.getSourceData() + ";DateFiler=" + req.getDateFiler() + ";SourceSysTRXNo="
                    + req.getSourceSysTRXNo() + ";SourceSysDocRefID=" + req.getSourceSysDocRefID() +
                    ";RefNo2=" + req.getRefNo2() + ";RefNo3=" + req.getRefNo3() + ";RefNo4=" + req.getRefNo4()
                    + ";RefNo5=" + req.getRefNo5() + ";RefNo6=" + req.getRefNo6() + ";RefNo7=" + req.getRefNo7()
                    + ";RefNo8=" + req.getRefNo8() + "\"," +
                    "\r\n    \"FileContent\":\"" + req.getFileContent() + "\"," +
                    "\r\n    \"FileName\": \"" + req.getFileName() + "\"\r\n}";
            RequestBody body = RequestBody.create(mediaType, stringBody);
            Request request = new Request.Builder()
                    .url(api_url + "/" + api_name + "upload")
                    .method("POST", body)
                    .addHeader("ApiKey", api_key)
                    .addHeader("ApiToken", api_tk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + bearer_tk)
                    .build();
            log.debug("DEBUG: upload requestBody-----------\n" + stringBody + "\n-----------");
            // Execute the request
            Response response = client.newCall(request).execute();
            // Request was successful
            String responseBody = response.body().string();
            log.debug("DEBUG: upload responseBody----------- rspcd: " + response.code() + "\n"
                    + responseBody + "\n-----------");

            try {
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("IdamanUpload");
                extAudit.setI_request_body(stringBody);
                extAudit.setI_response_body(responseBody);
                extAudit.setI_rms_batch_no(null);
                extAudit.setI_direction("Outgoing");
                extAudit.setI_remark(null);
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for IdamanUpload: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }

            Gson gson = new Gson();
            IdamanAPIUpload up = gson.fromJson(responseBody, IdamanAPIUpload.class);
            if ("1".equals(up.getCode()) && up.getData() != null)
                for (int i = 0; i < 1; i++) {
                    IdamanAPIUpload upl = new IdamanAPIUpload();
                    upl.setId(up.getId());
                    upl.setCode(up.getCode());
                    upl.setDesc(up.getDesc());
                    upl.setVerid(up.getData().getVerID());
                    result.add(upl);
                }
            else {
                // Request failed
                log.error("Request failed with code: " + response.code() + " | Desc: " + up.getDesc());
                for (int i = 0; i < 1; i++) {
                    IdamanAPIUpload upl = new IdamanAPIUpload();
                    upl.setId(up.getId());
                    upl.setCode(up.getCode());
                    upl.setDesc(up.getDesc());
                    result.add(upl);
                }
            }

            return result;
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

    // ver2
    @SuppressWarnings("deprecation")
    @Transactional
    public List<IdamanAPIUpload> idaman_api_uploadDoc_v2(IdamanAPIUploadReq req) throws IOException {
        List<IdamanAPIUpload> result = new ArrayList<>();

        String stringBody = "";
        Response response = null;
        // Execute API call - don't block execution if it fails
        try {

            // Clean file content
            cleanFileContent(req);

            // Build request
            stringBody = buildRequestBody(req);

            //Log before calling the API
            LocalDateTime requestTime = LocalDateTime.now();

            response = executeApiCall(stringBody);
            String responseBody = getResponseBody(response);

            //Log after receiving the response
            LocalDateTime responseTime = LocalDateTime.now();
            Duration duration = Duration.between(requestTime, responseTime);

            String InOutTime = "Outgoing: " + requestTime + " | Incoming: " + responseTime + " | Duration: " + duration.toMillis() + " ms";

            String stringBodyAudit = buildRequestBody_auditUse(req);

            // Log audit (don't fail if audit fails)
            logAudit(stringBodyAudit, responseBody, InOutTime);

            // Process response
            return processResponse(response, responseBody, stringBody);

        } catch (Exception e) {
            log.error("IdamanUpload API call failed, but continuing execution: " + e.getMessage(), e);

            // Log audit for the failed attempt
            logAudit(stringBody, "API_CALL_FAILED: " + e.getMessage(), null);

            // Return empty result or default response instead of blocking
            List<IdamanAPIUpload> result2 = new ArrayList<>();
            IdamanAPIUpload failedUpload = new IdamanAPIUpload();
            failedUpload.setCode("0");
            failedUpload.setDesc("API call failed: " + e.getMessage());
            result2.add(failedUpload);

            return result2;
        }
        finally{
            // Closed the response
            if (response != null){
                response.close();
            }
        }
    }

    private void cleanFileContent(IdamanAPIUploadReq req) {
        if (req.getFileContent().startsWith("data:")) {
            req.setFileContent(req.getFileContent().substring(req.getFileContent().indexOf(',') + 1));
        }
        req.setFileContent(req.getFileContent().replaceAll("\\s", "").replace(":", ""));
    }

    private String buildRequestBody(IdamanAPIUploadReq req) {
        return "{\r\n    \"RequestorID\":\"" + requestorId + "\"," +
                "\r\n    \"ClassID\":\"" + classId + "\"," +
                "\r\n    \"ProfileName\": \"" + profile_nm + "\"," +
                "\r\n    \"ProfileFields\": \"SourceSystem=" + req.getSourceSystem() +
                ";RefNo1=" + req.getRefNo1() + ";FormCode=" + req.getFormCode() +
                ";DocumentDate=" + req.getDocDate() + ";ReceivedDate=" + req.getReceivedDate() +
                ";TotalPage=" + req.getTotalPage() + ";StateCode=" + req.getStateCode() +
                ";SourceData=" + req.getSourceData() + ";DateFiler=" + req.getDateFiler() +
                ";SourceSysTRXNo=" + req.getSourceSysTRXNo() + ";SourceSysDocRefID=" + req.getSourceSysDocRefID() +
                ";RefNo2=" + req.getRefNo2() + ";RefNo3=" + req.getRefNo3() + ";RefNo4=" + req.getRefNo4() +
                ";RefNo5=" + req.getRefNo5() + ";RefNo6=" + req.getRefNo6() + ";RefNo7=" + req.getRefNo7() +
                ";RefNo8=" + req.getRefNo8() + "\"," +
                "\r\n    \"FileContent\":\"" + req.getFileContent() + "\"," +
                "\r\n    \"FileName\": \"" + req.getFileName() + "\"\r\n}";
    }

    private String buildRequestBody_auditUse(IdamanAPIUploadReq req) {
        return "{\r\n    \"RequestorID\":\"" + requestorId + "\"," +
                "\r\n    \"ClassID\":\"" + classId + "\"," +
                "\r\n    \"ProfileName\": \"" + profile_nm + "\"," +
                "\r\n    \"ProfileFields\": \"SourceSystem=" + req.getSourceSystem() +
                ";RefNo1=" + req.getRefNo1() + ";FormCode=" + req.getFormCode() +
                ";DocumentDate=" + req.getDocDate() + ";ReceivedDate=" + req.getReceivedDate() +
                ";TotalPage=" + req.getTotalPage() + ";StateCode=" + req.getStateCode() +
                ";SourceData=" + req.getSourceData() + ";DateFiler=" + req.getDateFiler() +
                ";SourceSysTRXNo=" + req.getSourceSysTRXNo() + ";SourceSysDocRefID=" + req.getSourceSysDocRefID() +
                ";RefNo2=" + req.getRefNo2() + ";RefNo3=" + req.getRefNo3() + ";RefNo4=" + req.getRefNo4() +
                ";RefNo5=" + req.getRefNo5() + ";RefNo6=" + req.getRefNo6() + ";RefNo7=" + req.getRefNo7() +
                ";RefNo8=" + req.getRefNo8() + "\"," +
                "\r\n    \"FileContent\":\"" + "[File Content]" + "\"," +
                "\r\n    \"FileName\": \"" + req.getFileName() + "\"\r\n}";
    }

    private Response executeApiCall(String stringBody) throws IOException {
        OkHttpClient client = (group == 0) ? getUnsafeOkHttpClient() : new OkHttpClient().newBuilder().connectTimeout(70, TimeUnit.SECONDS).readTimeout(70, TimeUnit.SECONDS).writeTimeout(70, TimeUnit.SECONDS).build();
        String authToken = (group == 0) ? itService.getidamantoken() : bearer_tk;
        String endpoint = (group == 0) ? "uploadDocument" : "upload";

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, stringBody);

        Request request = new Request.Builder()
                .url(api_url + "/" + api_name + endpoint)
                .method("POST", body)
                .addHeader("ApiKey", api_key)
                .addHeader("ApiToken", api_tk)
                .addHeader("X-IBM-Client-Id", xibmclientid)
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer " + authToken)
                .build();

        log.debug("DEBUG: upload requestBody-----------\n" + stringBody + "\n-----------");
        return client.newCall(request).execute();
    }

    private String getResponseBody(Response response) throws IOException {
        if (response == null) {
            log.debug("DEBUG: upload responseBody----------- rspcd: 0\n\n-----------");

            return "";
        }

        String responseBody = response.body() != null ? response.body().string() : "";
        responseBody = responseBody != null ? responseBody : ""; // Handle if .string() returns null

        log.debug("DEBUG: upload responseBody----------- rspcd: " + response.code() + "\n" + responseBody
                + "\n-----------");

        return responseBody;
    }

    private void logAudit(String requestBody, String responseBody, String InOutTime) {
        try {
            ExtAudit extAudit = new ExtAudit();
            extAudit.setI_module_nm("IdamanUpload");
            extAudit.setI_request_body(requestBody);
            extAudit.setI_response_body(responseBody);
            extAudit.setI_rms_batch_no(null);
            extAudit.setI_direction("Outgoing");
            extAudit.setI_remark(InOutTime);
            commonSvc.sp_insextaudit(extAudit);
        } catch (Exception e) {
            // Don't fail the main operation if audit fails
            // log.warn("Failed to log audit for IdamanUpload: " + e.getMessage());
            log.error("Error in sp_insextaudit for IdamanUpload: " + e.getMessage() + ", "
                    + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
        }
    }

    private List<IdamanAPIUpload> processResponse(Response response, String responseBody, String requestBody) {
        List<IdamanAPIUpload> result = new ArrayList<>();

        try {
            Gson gson = new Gson();
            IdamanAPIUpload up = gson.fromJson(responseBody, IdamanAPIUpload.class);

            IdamanAPIUpload upl = new IdamanAPIUpload();
            upl.setId(up.getId());
            upl.setCode(up.getCode());
            upl.setDesc(up.getDesc());

            if ("1".equals(up.getCode()) && up.getData() != null) {
                upl.setVerid(up.getData().getVerID());
            } else {
                log.error("Request failed with code: " + response.code() + " | Desc: " + up.getDesc() +
                        " | RequestBody: " + requestBody + " | ResponseBody: " + responseBody);
            }

            result.add(upl);

        } catch (Exception e) {
            log.error("Failed to parse JSON response: " + e.getMessage() + " | ResponseBody: " + responseBody, e);

            // Return error response instead of crashing
            IdamanAPIUpload errorUpload = new IdamanAPIUpload();
            errorUpload.setCode("0");
            errorUpload.setDesc("JSON parsing failed: " + e.getMessage());
            result.add(errorUpload);
        }

        return result;
    }

    // ver2
}
