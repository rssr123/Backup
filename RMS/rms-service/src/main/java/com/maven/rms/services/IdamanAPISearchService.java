package com.maven.rms.services;

import com.maven.rms.interfaces.IIdamanApiSearchService;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.IdamanAPISearch;
import com.maven.rms.models.IdamanAPISearchRequest;
import com.maven.rms.models.IdamanAPISearch.DocFields;
import com.maven.rms.models.IdamanAPISearch.DocList;

import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
@Slf4j
public class IdamanAPISearchService implements IIdamanApiSearchService {

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

    @Value("${idaman.profile.name}")
    private String profile_nm;

    @Value("${idaman.max.record}")
    private Integer max_rec;

    @Value("${idaman.search.field}")
    private String search_field;

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

    @SuppressWarnings("deprecation")
    public List<IdamanAPISearch> idaman_api_searchDoc(IdamanAPISearchRequest idamanAPISearchRequest)
            throws IOException {

        if (group == 0) {
            String authToken = itService.getidamantoken();
            List<IdamanAPISearch> result = new ArrayList<>();

            // Unsafe for dev only!
            OkHttpClient client = getUnsafeOkHttpClient();
            // OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");

            String stringBody = "{\r\n    \"RequestorID\":\"" + requestorId + "\"," +
                    "\r\n    \"ProfileName\": \"" + profile_nm + "\"," +
                    "\r\n    \"MaxRecord\":" + max_rec + "," +
                    "\r\n    \"RefNo1\": \"" + idamanAPISearchRequest.getRefNo1() + "\"," +
                    "\r\n    \"SearchFields\": \"" + search_field + "\"," +
                    "\r\n    \"SearchKeywords\": \"" + idamanAPISearchRequest.getRefNo1() + "\"\r\n}";

            RequestBody body = RequestBody.create(mediaType,
                    stringBody);
            Request request = new Request.Builder()
                    .url(api_url + "/" + api_name + "searchProfile")
                    .method("POST", body)
                    .addHeader("APIKey", api_key)
                    .addHeader("ApiToken", api_tk)
                    .addHeader("X-IBM-Client-Id", xibmclientid)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + authToken)
                    .build();

            // Execute the request
            Response response = client.newCall(request).execute();

            // Request was successful
            String responseBody = response.body() != null ? response.body().string() : "";

            try {
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("IdamanSearch");
                extAudit.setI_request_body(stringBody);
                extAudit.setI_response_body(responseBody);
                extAudit.setI_rms_batch_no(null);
                extAudit.setI_direction("Outgoing");
                extAudit.setI_remark(null);
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for IdamanSearch: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }

            Gson gson = new Gson();
            IdamanAPISearch up = gson.fromJson(responseBody, IdamanAPISearch.class);

            if (Integer.parseInt(up.getCode()) == 1 && up.getData() != null) {
                IdamanAPISearch upl = new IdamanAPISearch();
                upl.setId(up.getId());
                upl.setCode(up.getCode());
                upl.setDesc(up.getDesc());
                for (DocList documentList : up.getData().getDocumentlist()) {
                    upl.setFileName(documentList.getFileName());
                    upl.setFileSize(documentList.getFileSize());
                    upl.setVerID(documentList.getVerID());
                    upl.setClassID(documentList.getVerID());
                    upl.setProfileID(documentList.getProfileID());
                    upl.setDateCreated(documentList.getDateCreated());
                    result.add(upl);
                    for (DocFields fields : documentList.getFields()) {
                        upl.setFname(fields.getName());
                        upl.setFdescription(fields.getDescription());
                        upl.setFvalue(fields.getValue());
                        result.add(upl);
                    }
                }
            } else {
                IdamanAPISearch upl = new IdamanAPISearch();
                upl.setId(up.getId());
                upl.setCode(up.getCode());
                upl.setDesc(up.getDesc());
                result.add(upl);
            }

            return result;
        } else {
            List<IdamanAPISearch> result = new ArrayList<>();

            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("application/json");

            RequestBody body = RequestBody.create(mediaType,
                    "{\r\n    \"RequestorID\":\"" + requestorId + "\"," +
                            "\r\n    \"ProfileName\": \"" + profile_nm + "\"," +
                            "\r\n    \"MaxRecord\":" + max_rec + "," +
                            "\r\n    \"RefNo1\": \"" + idamanAPISearchRequest.getRefNo1() + "\"," +
                            "\r\n    \"SearchFields\": \"" + search_field + "\"," +
                            "\r\n    \"SearchKeywords\": \"" + idamanAPISearchRequest.getRefNo1() + "\"\r\n}");
            Request request = new Request.Builder()
                    .url(api_url + "/" + api_name + "profileSearch")
                    .method("POST", body)
                    .addHeader("APIKey", api_key)
                    .addHeader("ApiToken", api_tk)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + bearer_tk)
                    .build();

            // Execute the request
            Response response = client.newCall(request).execute();
            // Request was successful
            String responseBody = response.body().string();
            Gson gson = new Gson();
            IdamanAPISearch up = gson.fromJson(responseBody, IdamanAPISearch.class);

            if (Integer.parseInt(up.getCode()) == 1 && up.getData() != null) {
                IdamanAPISearch upl = new IdamanAPISearch();
                upl.setId(up.getId());
                upl.setCode(up.getCode());
                upl.setDesc(up.getDesc());
                for (DocList documentList : up.getData().getDocumentlist()) {
                    upl.setFileName(documentList.getFileName());
                    upl.setFileSize(documentList.getFileSize());
                    upl.setVerID(documentList.getVerID());
                    upl.setClassID(documentList.getVerID());
                    upl.setProfileID(documentList.getProfileID());
                    upl.setDateCreated(documentList.getDateCreated());
                    result.add(upl);
                    for (DocFields fields : documentList.getFields()) {
                        upl.setFname(fields.getName());
                        upl.setFdescription(fields.getDescription());
                        upl.setFvalue(fields.getValue());
                        result.add(upl);
                    }
                }
            } else {
                IdamanAPISearch upl = new IdamanAPISearch();
                upl.setId(up.getId());
                upl.setCode(up.getCode());
                upl.setDesc(up.getDesc());
                result.add(upl);
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
                    .build();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
