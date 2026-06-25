package com.maven.rms.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.FMSARIImmediateRequest;
import com.maven.rms.models.FMSARV;
import com.maven.rms.models.FMSARVJson;
import com.maven.rms.models.GenericValue;
import com.maven.rms.repositories.FMSARVRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FMSARVService {

    private final FMSARVRepository fmsARVRepo;

    @Value("${fmsarv.api.url}")
    private String api_url;

    @Value("${fmsarv.api.name}")
    private String api_name;

    @Value("${fms.ibm-client-id}")
    private String IBMClientID;

    @Autowired
    private CommonService commonSvc;

    public FMSARVService(FMSARVRepository fmsARVRepo) {
        this.fmsARVRepo = fmsARVRepo;
    }

    public List<FMSARV> sp_getfmsrefno() {
        List<FMSARV> result = new ArrayList<>();
        List<Object[]> objects = fmsARVRepo.sp_getfmsrefno();
        List<FMSARV> fmsARVList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSARV fmsARV = new FMSARV();
            fmsARV.setRcpt_no((String) obj[0]);
            fmsARV.setRms_batch_no((String) obj[1]);
            fmsARV.setFms_ref_no((String) obj[2]);
            fmsARV.setArv_reason((String) obj[3]);

            fmsARVList.add(fmsARV);
        }
        result = fmsARVList;
        return result;
    }

    public Integer sp_insfmsarv(FMSARV fmsarv) {
        Integer result = null;
        result = fmsARVRepo.sp_insfmsarv(fmsarv);
        return result;
    }

    public List<FMSARV> sp_getfmsarv() {
        List<FMSARV> result = new ArrayList<>();
        List<Object[]> objects = fmsARVRepo.sp_getfmsarv();
        List<FMSARV> fmsARVList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSARV fmsARV = new FMSARV();

            fmsARV.setFms_arv_id((BigInteger) obj[0]);
            fmsARV.setRef_no((String) obj[1]);
            fmsARV.setArv_type((String) obj[2]);
            fmsARV.setArv_hold((String) obj[3]);
            fmsARV.setArv_reason((String) obj[4]);
            fmsARV.setExt_sys((String) obj[5]);
            fmsARV.setStatus((String) obj[6]);
            fmsARV.setMessage((String) obj[7]);
            fmsARV.setFms_date((Date) obj[8]);
            fmsARV.setDt_created((Date) obj[9]);
            fmsARV.setDt_modified((Date) obj[10]);
            fmsARV.setCreated_by((String) obj[11]);
            fmsARV.setModified_by((String) obj[12]);

            fmsARVList.add(fmsARV);
        }
        result = fmsARVList;

        return result;
    }

    // 250310: Added new method for FMSARV Immediate send posting
    public List<FMSARV> sp_getfmsarvimmediate(FMSARIImmediateRequest fmsARIImmediateRequest) {
        List<FMSARV> result = new ArrayList<>();
        List<Object[]> objects = fmsARVRepo.sp_getfmsarvimmediate(fmsARIImmediateRequest);
        List<FMSARV> fmsARVList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSARV fmsARV = new FMSARV();

            fmsARV.setFms_arv_id((BigInteger) obj[0]);
            fmsARV.setRef_no((String) obj[1]);
            fmsARV.setArv_type((String) obj[2]);
            fmsARV.setArv_hold((String) obj[3]);
            fmsARV.setArv_reason((String) obj[4]);
            fmsARV.setExt_sys((String) obj[5]);
            fmsARV.setStatus((String) obj[6]);
            fmsARV.setMessage((String) obj[7]);
            fmsARV.setFms_date((Date) obj[8]);
            fmsARV.setDt_created((Date) obj[9]);
            fmsARV.setDt_modified((Date) obj[10]);
            fmsARV.setCreated_by((String) obj[11]);
            fmsARV.setModified_by((String) obj[12]);

            fmsARVList.add(fmsARV);
        }
        result = fmsARVList;

        return result;
    }

    public void postFMSAVImmediate(FMSARIImmediateRequest fmsARIImmediateRequest) throws JsonProcessingException {
        List<FMSARV> result = new ArrayList<>();
        // Call FMS Post Accounting API
        List<FMSARV> fmsARV = sp_getfmsarvimmediate(fmsARIImmediateRequest);

        // stringbody n fms_api_arr
        String stringBody = "";
        FMSARVJson fmsARVJson = new FMSARVJson();

        // if size > 0
        if (fmsARV.size() > 0) {

            for (int i = 0; i < fmsARV.size(); i++) {
                FMSARV currentItem = fmsARV.get(i);
                String currentBatchNo = currentItem.getRms_batch_no();

                log.info("FMSARV Sent on: " + new Date() + "for fms_ref_no " + fmsARV.get(0).getFms_ref_no());
                fmsARVJson = new FMSARVJson();
                fmsARVJson = this.generateStringBody(currentItem.getRef_no(), currentItem.getArv_type(),
                        currentItem.getArv_reason(), fmsARVJson);

                ObjectMapper mapper = new ObjectMapper();
                stringBody = mapper.writeValueAsString(fmsARVJson);
                System.out.print(stringBody);
                result.addAll(this.fms_api_arv(stringBody, currentItem));
            }
        }
    }

    public Integer sp_updfmsarv(FMSARV fmsarv) {
        Integer result = null;
        result = fmsARVRepo.sp_updfmsarv(fmsarv);
        return result;
    }

    public FMSARVJson generateStringBody(String ref_no, String type, String arvReason, FMSARVJson inputObject) {

        FMSARVJson.Entity entity = new FMSARVJson.Entity();
        entity.setReferenceNbr(new GenericValue<>(ref_no));
        entity.setType(new GenericValue<>(type));
        entity.setHold(new GenericValue<>("False")); // Assuming hold is always false

        inputObject.setEntity(entity);

        FMSARVJson.Custom custom = new FMSARVJson.Custom();
        FMSARVJson.Document document = new FMSARVJson.Document();
        FMSARVJson.UsrVoidReason usrVoidReason = new FMSARVJson.UsrVoidReason();
        usrVoidReason.setType("CustomStringField"); // =harcoded
        usrVoidReason.setValue(arvReason);

        document.setUsrVoidReason(usrVoidReason);
        custom.setDocument(document);

        inputObject.setCustom(custom);

        return inputObject;
    }

    @SuppressWarnings("deprecation")
    public List<FMSARV> fms_api_arv(String stringBody, FMSARV existingFmsArv) {

        List<FMSARV> result = new ArrayList<>();

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

            URL url = new URL(api_url + "/" + api_name);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("X-IBM-Client-Id", IBMClientID);

            // Enable output for the connection
            connection.setDoOutput(true);

            // Write the request body
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = stringBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            // System.out.println("GET Response Code :: " + responseCode);
            log.debug("GET Response Code :: " + responseCode);

            // String responseBody = connection.getResponseMessage();
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

            // Close the connection
            connection.disconnect();

            try {
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("FMSARV");
                extAudit.setI_request_body(stringBody);
                extAudit.setI_response_body(response.toString());
                extAudit.setI_rms_batch_no(existingFmsArv.getRms_batch_no());
                extAudit.setI_direction("Outgoing");
                extAudit.setI_remark(null);
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for FMS ARV: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            FMSARV fmsarv = new FMSARV();

            fmsarv.setFms_arv_id(existingFmsArv.getFms_arv_id());
            fmsarv.setArv_reason(existingFmsArv.getArv_reason());

            // Utility method to extract fields dynamically
            fmsarv.setResp_attr_ext_sys(extractField(jsonResponse, "Type", "AttributeEXTSYSTEM"));
            fmsarv.setFms_ref_no(extractField(jsonResponse, "ReferenceNbr"));
            fmsarv.setResp_msg(extractField(jsonResponse, "Message"));
            fmsarv.setResp_status(extractField(jsonResponse, "Status"));

            // Handle "Date" field
            if (jsonResponse.has("Date")) {
                String dateString = jsonResponse.get("Date").getAsString();
                try {
                    Date date = dateFormat.parse(dateString);
                    fmsarv.setResp_dt(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            result.add(fmsarv);

            this.sp_updfmsarv(fmsarv);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // Utility method to extract either a top-level field or a nested "value" field
    private String extractField(JsonObject jsonResponse, String... fieldNames) {
        for (String fieldName : fieldNames) {
            if (jsonResponse.has(fieldName)) {
                JsonElement element = jsonResponse.get(fieldName);
                if (element.isJsonObject() && element.getAsJsonObject().has("value")) {
                    return element.getAsJsonObject().get("value").getAsString();
                } else if (element.isJsonPrimitive()) {
                    return element.getAsString();
                }
            }
        }
        return null;
    }

    public List<FMSARV> fms_arv_sch() throws JsonProcessingException {
        List<FMSARV> result = new ArrayList<>();
        // int insSuccess = 0;
        Integer insSuccess = 0;

        // getfmsrcbank
        List<FMSARV> fmsRefNo = this.sp_getfmsrefno();

        // loop n insfmsarr
        for (FMSARV item : fmsRefNo) {

            insSuccess = this.sp_insfmsarv(item);
        }

        // getfmsarr
        List<FMSARV> fmsARV = this.sp_getfmsarv();

        // stringbody n fms_api_arr
        String stringBody = "";
        FMSARVJson fmsARVJson = new FMSARVJson();

        // if size > 0
        if (fmsARV.size() > 0) {

            for (int i = 0; i < fmsARV.size(); i++) {
                FMSARV currentItem = fmsARV.get(i);
                String currentBatchNo = currentItem.getRms_batch_no();

                fmsARVJson = new FMSARVJson();
                fmsARVJson = this.generateStringBody(currentItem.getRef_no(), currentItem.getArv_type(),
                        currentItem.getArv_reason(), fmsARVJson);

                ObjectMapper mapper = new ObjectMapper();
                stringBody = mapper.writeValueAsString(fmsARVJson);
                result.addAll(this.fms_api_arv(stringBody, currentItem));

            }
        }

        return result;
    }
}
