package com.maven.rms.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.FMSARR;
import com.maven.rms.models.FMSARRJson;
import com.maven.rms.models.GenericValue;
import com.maven.rms.repositories.FMSARROTCRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FMSARROTCService {

    private final FMSARROTCRepository fmsARROTCRepo;

    @Value("${fmsarr.api.url}")
    private String api_url;

    @Value("${fmsarr.api.name}")
    private String api_name;

    @Value("${fms.ibm-client-id}")
    private String IBMClientID;

    @Autowired
    private CommonService commonSvc;

    public FMSARROTCService(FMSARROTCRepository fmsARROTCRepo) {
        this.fmsARROTCRepo = fmsARROTCRepo;
    }

    public List<FMSARR> fms_arr_otc_sch() {
        List<FMSARR> result = new ArrayList<>();
        List<String> ariList = new ArrayList<>();

        try {
            // Get FMS OTC ARI
            ariList = fmsARROTCRepo.sp_getotcfmsarirefno();
        } catch (Exception e) {
            log.error("Error fetching FMS ARI ref numbers", e);
        }

        try {
            // Insert into FMS OTC ARR
            if (ariList != null && !ariList.isEmpty()) {
                for (String refNo : ariList) {
                    fmsARROTCRepo.sp_insotcfmsarr(refNo);
                }
            }
        } catch (Exception e) {
            log.error("Error inserting into FMS OTC ARR", e);
        }

        try {
            // get fms otc ARR Physical
            List<FMSARR> fmsARRM = this.sp_getotcfmsarr("m");
            if (fmsARRM != null && !fmsARRM.isEmpty()) {
                result = convertFMSARROTCBody(fmsARRM);
            }
        } catch (Exception e) {
            log.error("Error processing FMS OTC ARR M", e);
        }

        try {
            // get fms otc ARR EMV
            List<FMSARR> fmsARRE = this.sp_getotcfmsarr("e");
            if (fmsARRE != null && !fmsARRE.isEmpty()) {
                result = convertFMSARROTCBody(fmsARRE);
            }
        } catch (Exception e) {
            log.error("Error processing FMS OTC ARR E", e);
        }

        return result;
    }

    public List<FMSARR> sp_getotcfmsarr(String i_otc_type){

        List<Object[]> objects = fmsARROTCRepo.sp_getotcfmsarr(i_otc_type);
        List<FMSARR> fmsARRList = new ArrayList<>();

        //Get FMS ARR
        if(objects != null && objects.size() > 0){
            for (Object[] obj : objects) {
                FMSARR fmsARR = new FMSARR();
                fmsARR.setArr_hid((BigInteger) obj[0]);
                fmsARR.setType((String) obj[1]);
                fmsARR.setBranch((String) obj[2]);
                fmsARR.setDt_sent((Date) obj[3]);
                fmsARR.setCash_acct((String) obj[4]);
                fmsARR.setPymt_amt((BigDecimal) obj[5]);
                fmsARR.setPymt_method((String) obj[6]);
                fmsARR.setCust((String) obj[7]);
                fmsARR.setRms_batch_no((String) obj[8]);
                fmsARR.setFms_ref_no((String) obj[9]);
                fmsARR.setDoc_type_b((String) obj[10]);
                fmsARR.setAmt((BigDecimal) obj[11]);
                fmsARR.setDoc_type_c((String) obj[12]);
                fmsARR.setEntity_type((String) obj[13]);
                fmsARR.setOffset_subacct((String) obj[14]);
                fmsARR.setAttribute_doc_no((String) obj[15]);
                fmsARR.setAmt_paid((BigDecimal) obj[16]);
                fmsARR.setAcct_id("");
                fmsARRList.add(fmsARR);
            }
        }

        return fmsARRList;
    }

    public List<FMSARR> convertFMSARROTCBody(List<FMSARR> fmsARR) {
        List<FMSARR> result = new ArrayList<>();

        // stringbody n fms_api_arr
        String stringBody = "";
        FMSARRJson fmsARRJson = new FMSARRJson();

        // if size > 0
        if (fmsARR.size() > 0) {

            for (int i = 0; i < fmsARR.size(); i++) {
                FMSARR currentItem = fmsARR.get(i);
                String currentBatchNo = currentItem.getRms_batch_no();

                boolean isFirst = (i == 0) || !fmsARR.get(i - 1).getRms_batch_no().equals(currentBatchNo);
                boolean isLast = (i == fmsARR.size() - 1)
                        || !fmsARR.get(i + 1).getRms_batch_no().equals(currentBatchNo);

                // only has 1 body or is first
                if ((isFirst && isLast) || (isFirst && !isLast)) {
                    fmsARRJson = new FMSARRJson();
                    fmsARRJson = this.generateStringBody(currentItem.getType(), currentItem.getBranch(),
                            currentItem.getDt_sent(),
                            currentItem.getCash_acct(), currentItem.getPymt_amt(), currentItem.getPymt_method(),
                            currentItem.getCust(), currentItem.getRms_batch_no(), currentItem.getFms_ref_no(),
                            currentItem.getDoc_type_b(), currentItem.getAmt(), currentItem.getDoc_type_c(),
                            currentItem.getEntity_type(), currentItem.getOffset_subacct(), isFirst, isLast,
                            fmsARRJson, currentItem.getAttribute_doc_no(), currentItem.getAmt_paid(),
                            currentItem.getAcct_id());

                    // if it's last, call api
                    if (isFirst && isLast) {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            stringBody = mapper.writeValueAsString(fmsARRJson);
                            // Send to FMS
                            result.addAll(this.fms_api_arr(stringBody, currentItem.getArr_hid()));
                        } catch (JsonProcessingException e) {
                            log.error("Exception in " + this.getClass().toString() + " " + e);
                        }

                    }
                }
                // middle or last
                else if ((!isFirst && !isLast) || (!isFirst && isLast)) {
                    fmsARRJson = this.generateStringBody(currentItem.getType(), currentItem.getBranch(),
                            currentItem.getDt_sent(),
                            currentItem.getCash_acct(), currentItem.getPymt_amt(), currentItem.getPymt_method(),
                            currentItem.getCust(), currentItem.getRms_batch_no(), currentItem.getFms_ref_no(),
                            currentItem.getDoc_type_b(), currentItem.getAmt(), currentItem.getDoc_type_c(),
                            currentItem.getEntity_type(), currentItem.getOffset_subacct(), isFirst, isLast,
                            fmsARRJson, currentItem.getAttribute_doc_no(), currentItem.getAmt_paid(),
                            currentItem.getAcct_id());
                    // if it's last, call api
                    if (!isFirst && isLast) {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            stringBody = mapper.writeValueAsString(fmsARRJson);
                            // Send to FMS
                            result.addAll(this.fms_api_arr(stringBody, currentItem.getArr_hid()));
                        } catch (JsonProcessingException e) {
                            log.error("Exception in " + this.getClass().toString() + " " + e);
                        }
                    }
                }
            }
        }
        return result;
    }

    public FMSARRJson generateStringBody(String type, String branch, Date dt_sent, String cash_acct,
            BigDecimal pymt_amt, String pymt_method, String cust, String rms_batch_no,
            String fms_ref_no, String doc_type_b, BigDecimal amt, String doc_type_c,
            String entity_type, String offset_subacct, boolean isFirst, boolean isLast,
            FMSARRJson inputObject, String attr_doc_no, BigDecimal amt_paid, String acct_id) {
        if (isFirst) {
            inputObject.setType(new GenericValue<>(type));
            inputObject.setBranch(new GenericValue<>(branch));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dtSentFormatted = sdf.format(dt_sent);
            inputObject.setApplicationDate(new GenericValue<>(dtSentFormatted));
            inputObject.setCashAccount(new GenericValue<>(cash_acct));
            inputObject.setCustomerID(new GenericValue<>(cust));
            inputObject.setDescription(new GenericValue<>("PAYMENT REF RMS"));
            inputObject.setPaymentAmount(new GenericValue<>(pymt_amt));
            inputObject.setPaymentMethod(new GenericValue<>(pymt_method));
            inputObject.setPaymentRef(new GenericValue<>(rms_batch_no));

            FMSARRJson.Custom custom = new FMSARRJson.Custom();
            FMSARRJson.CurrentDocument currentDocument = new FMSARRJson.CurrentDocument();
            FMSARRJson.Attribute attribute = new FMSARRJson.Attribute();
            attribute.setType("CustomStringField");
            attribute.setValue("RMS");
            currentDocument.setAttributeSYSNAME(attribute);
            FMSARRJson.Attribute attributeDoc = new FMSARRJson.Attribute();
            attributeDoc.setType("CustomStringField");
            attributeDoc.setValue(attr_doc_no);
            currentDocument.setAttributeDOCNO(attributeDoc);
            custom.setCurrentDocument(currentDocument);
            inputObject.setCustom(custom);

            List<FMSARRJson.Charge> charges = new ArrayList<>();
            FMSARRJson.Charge charge = new FMSARRJson.Charge();
            charge.setAccountID(new GenericValue<>(acct_id));
            charge.setAmount(new GenericValue<>(amt));
            charge.setDocType(new GenericValue<>(doc_type_c));
            charge.setEntityType(new GenericValue<>(entity_type));
            charge.setOffsetSubaccount(new GenericValue<>(offset_subacct));
            charges.add(charge);
            inputObject.setCharges(charges);

            inputObject.setDocumentsToApply(new ArrayList<>());
        }

        List<FMSARRJson.DocumentToApply> docs = inputObject.getDocumentsToApply();
        if (fms_ref_no != null && !fms_ref_no.trim().isEmpty()) {
            FMSARRJson.DocumentToApply doc = new FMSARRJson.DocumentToApply();
            doc.setDocType(new GenericValue<>(doc_type_b));
            doc.setReferenceNbr(new GenericValue<>(fms_ref_no));
            doc.setAmt_paid(new GenericValue<>(amt_paid));
            docs.add(doc);
        }

        if (isLast) {
            inputObject.setDocumentsToApply(docs);
        }

        return inputObject;
    }

    @SuppressWarnings("deprecation")
    public List<FMSARR> fms_api_arr(String stringBody, BigInteger arr_hid) {
        log.error("FMSARROTC Service Line 277- " + stringBody);
        List<FMSARR> result = new ArrayList<>();

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
            log.error("Response Body: " + response.toString());

            // Close the connection
            connection.disconnect();

            try {
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("FMSARROTC");
                extAudit.setI_request_body(stringBody);
                extAudit.setI_response_body(response.toString());
                extAudit.setI_rms_batch_no(null);
                extAudit.setI_direction("Outgoing");
                extAudit.setI_remark(null);
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for FMS ARR OTC: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");

            FMSARR fmsarr = new FMSARR();
            fmsarr.setResp_attr_ext_sys(
                    jsonResponse.has("AttributeEXTSYSTEM") ? jsonResponse.get("AttributeEXTSYSTEM").getAsString()
                            : null);
            fmsarr.setFms_ref_no(
                    jsonResponse.has("ReferenceNbr") ? jsonResponse.get("ReferenceNbr").getAsString() : null);
            fmsarr.setResp_co(
                    jsonResponse.has("CustomerOrder") ? jsonResponse.get("CustomerOrder").getAsString() : null);
            fmsarr.setResp_status(jsonResponse.has("Status") ? jsonResponse.get("Status").getAsString() : null);
            fmsarr.setResp_msg(jsonResponse.has("Message") ? jsonResponse.get("Message").getAsString() : null);
            fmsarr.setPayment_ref(jsonResponse.has("PaymentRef") ? jsonResponse.get("PaymentRef").getAsString() : null);
            if (jsonResponse.has("Date")) {
                String dateString = jsonResponse.get("Date").getAsString();
                try {
                    Date date = dateFormat.parse(dateString);
                    fmsarr.setResp_dt(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            result.add(fmsarr);

            this.sp_updfmsarr(fmsarr);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public Integer sp_updfmsarr(FMSARR fmsarr) {
        Integer result = 0;
        result = fmsARROTCRepo.sp_updfmsarr(fmsarr);
        return result;
    }

}
