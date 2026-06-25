package com.maven.rms.services;

import static org.quartz.JobBuilder.newJob;

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
import com.maven.rms.repositories.FMSARRRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FMSARRService {

    private final FMSARRRepository fmsARRRepo;

    @Value("${fmsarr.api.url}")
    private String api_url;

    @Value("${fmsarr.api.name}")
    private String api_name;

    @Value("${fms.ibm-client-id}")
    private String IBMClientID;

    @Autowired
    private CommonService commonSvc;

    public FMSARRService(FMSARRRepository fmsARRRepo) {
        this.fmsARRRepo = fmsARRRepo;
    }

    // public List<FMSARR> sp_getfmsrcbank() {
    // List<FMSARR> result = new ArrayList<>();

    // try {
    // List<Object[]> objects = fmsARRRepo.sp_getfmsrcbank();
    // List<FMSARR> fmsARRList = new ArrayList<>();
    // for (Object[] obj : objects) {
    // FMSARR fmsARR = new FMSARR();
    // fmsARR.setRc_bank_id((BigInteger) obj[0]);
    // fmsARR.setRc_pg_id((BigInteger) obj[1]);
    // fmsARR.setCredit((BigDecimal) obj[2]);
    // fmsARR.setMdr_amt((BigDecimal) obj[3]);
    // fmsARR.setMtt_pg_id((BigInteger) obj[4]);
    // fmsARR.setAcct_cd((String) obj[5]);
    // fmsARR.setCust((String) obj[6]);
    // fmsARRList.add(fmsARR);
    // }
    // result = fmsARRList;
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    public List<FMSARR> sp_getfmsrcbank() {
        List<FMSARR> result = new ArrayList<>();
        List<Object[]> objects = fmsARRRepo.sp_getfmsrcbank();
        List<FMSARR> fmsARRList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSARR fmsARR = new FMSARR();
            fmsARR.setRc_bank_id((BigInteger) obj[0]);
            fmsARR.setRc_pg_id((BigInteger) obj[1]);
            fmsARR.setCredit((BigDecimal) obj[2]);
            fmsARR.setMdr_amt((BigDecimal) obj[3]);
            // fmsARR.setMtt_pg_id((BigInteger) obj[4]);
            fmsARR.setAcct_cd((String) obj[4]);
            fmsARR.setCust((String) obj[5]);
            fmsARRList.add(fmsARR);
        }
        result = fmsARRList;
        return result;
    }

    public List<FMSARR> sp_getfmsarrdebit(BigInteger rc_pg_id) {
        List<FMSARR> result = new ArrayList<>();
        List<Object[]> objects = fmsARRRepo.sp_getfmsarrdebit(rc_pg_id);
        List<FMSARR> fmsARRList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSARR fmsARR = new FMSARR();
            fmsARR.setCredit((BigDecimal) obj[0]);
            fmsARR.setMdr_amt((BigDecimal) obj[1]);
            fmsARR.setAcct_cd((String) obj[2]);
            fmsARR.setCust((String) obj[3]);
            fmsARR.setFms_ref_no((String) obj[4]);
            fmsARRList.add(fmsARR);
        }
        result = fmsARRList;
        return result;
    }

    // public Integer sp_insfmsarr(BigInteger rc_bank_id, BigInteger rc_pg_id,
    // BigDecimal credit, BigDecimal mdr_amt,
    // BigInteger mtt_pg_id,
    // String acct_cd, String cust) {
    // Integer result = null;

    // try {
    // result = fmsARRRepo.sp_insfmsarr(rc_bank_id, rc_pg_id, credit, mdr_amt,
    // mtt_pg_id, acct_cd, cust);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    // public Integer sp_insfmsarr(FMSARR fmsarr) {
    // Integer result = null;

    // try {
    // result = fmsARRRepo.sp_insfmsarr(fmsarr);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    public Integer sp_insfmsarr(FMSARR fmsarr, Integer hid, Integer flag) {
        Integer result = 0;
        result = fmsARRRepo.sp_insfmsarr(fmsarr, hid, flag);
        return result;
    }

    // public List<FMSARR> sp_getfmsarr() {
    // List<FMSARR> result = new ArrayList<>();

    // try {
    // List<Object[]> objects = fmsARRRepo.sp_getfmsarr();
    // List<FMSARR> fmsARRList = new ArrayList<>();
    // for (Object[] obj : objects) {
    // FMSARR fmsARR = new FMSARR();
    // fmsARR.setArr_hid((BigInteger) obj[0]);
    // fmsARR.setType((String) obj[1]);
    // fmsARR.setBranch((String) obj[2]);
    // fmsARR.setDt_sent((Date) obj[3]);
    // fmsARR.setCash_acct((String) obj[4]);
    // fmsARR.setPymt_amt((BigDecimal) obj[5]);
    // fmsARR.setPymt_method((String) obj[6]);
    // fmsARR.setCust((String) obj[7]);
    // fmsARR.setRms_batch_no((String) obj[8]);
    // fmsARR.setFms_ref_no((String) obj[9]);
    // fmsARR.setDoc_type_b((String) obj[10]);
    // fmsARR.setAmt((BigDecimal) obj[11]);
    // fmsARR.setDoc_type_c((String) obj[12]);
    // fmsARR.setEntity_type((String) obj[13]);
    // fmsARR.setOffset_subacct((String) obj[14]);
    // fmsARRList.add(fmsARR);
    // }
    // result = fmsARRList;
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    public List<FMSARR> sp_getfmsarr() {
        List<Object[]> objects = fmsARRRepo.sp_getfmsarr();
        List<FMSARR> fmsARRList = new ArrayList<>();
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
            fmsARR.setAcct_id((String) obj[17]);
            fmsARRList.add(fmsARR);
        }

        return fmsARRList;
    }

    // public Integer sp_updfmsarr(BigInteger arr_hid, String resp_attr_ext_sys,
    // String fms_ref_no, String resp_co,
    // String resp_status, String resp_msg, Date resp_dt) {
    // Integer result = null;

    // try {
    // result = fmsARRRepo.sp_updfmsarr(arr_hid, resp_attr_ext_sys, fms_ref_no,
    // resp_co, resp_status, resp_msg,
    // resp_dt);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    // public Integer sp_updfmsarr(FMSARR fmsarr) {
    // Integer result = null;

    // try {
    // result = fmsARRRepo.sp_updfmsarr(fmsarr);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    public Integer sp_updfmsarr(FMSARR fmsarr) {
        Integer result = 0;
        result = fmsARRRepo.sp_updfmsarr(fmsarr);
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
            connection.setConnectTimeout(420000); // in ms
            connection.setReadTimeout(420000);
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
                extAudit.setI_module_nm("FMSARR");
                extAudit.setI_request_body(stringBody);
                extAudit.setI_response_body(response.toString());
                extAudit.setI_rms_batch_no(null);
                extAudit.setI_direction("Outgoing");
                extAudit.setI_remark(null);
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for FMS ARR: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }

            // OkHttpClient client = new OkHttpClient().newBuilder()
            // .build();
            // MediaType mediaType = MediaType.parse("application/json");

            // RequestBody body = RequestBody.create(mediaType, stringBody);
            // Request request = new Request.Builder()
            // .url(api_url +"/"+ api_name)
            // .method("POST", body)
            // .addHeader("Content-Type", "application/json")
            // .build();
            // Response response = client.newCall(request).execute();
            // String responseBody = response.body().string();

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

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

            // this.sp_updfmsarr(arr_hid,
            // fmsarr.getResp_attr_ext_sys(),
            // fmsarr.getFms_ref_no(),
            // fmsarr.getResp_co(),
            // fmsarr.getResp_status(),
            // fmsarr.getResp_msg(),
            // fmsarr.getResp_dt());

            this.sp_updfmsarr(fmsarr);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    // use this
    public List<FMSARR> fms_arr_sch() {
        List<FMSARR> result = new ArrayList<>();
        List<FMSARR> fmsRcBank = this.sp_getfmsrcbank();
        List<FMSARR> fmsARRDN = new ArrayList<>();
        List<FMSARR> fmsARRDNAlone = new ArrayList<>();
        //List<FMSARR> fmsARRDN = new ArrayList<>();

        Integer hid = 0;

        boolean safeCompare = fmsRcBank != null && !fmsRcBank.isEmpty() &&
                fmsARRDN != null && !fmsARRDN.isEmpty() &&
                fmsRcBank.get(0).getCust() != null &&
                fmsARRDN.get(0).getCust() != null;

        //if (!safeCompare || !fmsRcBank.get(0).getCust().equals(fmsARRDN.get(0).getCust())) {
        //} else {
            for (FMSARR item : fmsRcBank) {
                hid = this.sp_insfmsarr(item, null, 0); // Save header

                fmsARRDN = this.sp_getfmsarrdebit(item.getRc_pg_id());
                if(fmsARRDN.size() > 0) {
                    for (FMSARR itemDN : fmsARRDN) {
                        this.sp_insfmsarr(itemDN, hid, 0); // Append to same header
                    }
                }
            }

            fmsARRDNAlone = this.sp_getfmsarrdebit(BigInteger.valueOf(0));
            if(fmsARRDNAlone.size() > 0) {
                // for (FMSARR item : fmsRcBank) {
                //     this.sp_insfmsarr(item, null, 1); // New header
                // }

                for (FMSARR item : fmsARRDNAlone) {
                    this.sp_insfmsarr(item, null, 1); // New header (even for debit)
                }
            }


            // for (FMSARR item : fmsARRDN) {
            //     this.sp_insfmsarr(item, hid, 0); // Append to same header
            // }
        //}

        List<FMSARR> fmsARR = this.sp_getfmsarr();

        if (fmsARR != null && !fmsARR.isEmpty()) {
            result = convertFMSARRBody(fmsARR);
        }

        return result;
    }

    // public List<FMSARR> fms_arr_sch() {
    // List<FMSARR> result = new ArrayList<>();
    // // Integer insSuccess = 0;

    // // Normal ARReceipt call
    // List<FMSARR> fmsRcBank = this.sp_getfmsrcbank();
    // // ARReceipt for Debit Memo call
    // List<FMSARR> fmsARRDN = this.sp_getfmsarrdebit();

    // Integer hid = 0;

    // for (FMSARR item : fmsRcBank) {
    // hid = this.sp_insfmsarr(item, null);
    // }

    // for (FMSARR item : fmsARRDN) {
    // log.error("Line 443- FMSARR" + hid.toString());
    // this.sp_insfmsarr(item, hid);
    // }

    // // getfmsarr
    // List<FMSARR> fmsARR = this.sp_getfmsarr();

    // if (fmsARR != null && fmsARR.size() > 0) {
    // result = convertFMSARRBody(fmsARR);
    // }

    // return result;
    // }

    public List<FMSARR> convertFMSARRBody(List<FMSARR> fmsARR) {
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


    public Integer sp_insfmsarrnonrmsrecon(FMSARR fmsarr) {
        Integer result = 0;
        result = fmsARRRepo.sp_insfmsarrnonrmsrecon(fmsarr);
        return result;
    }
}
