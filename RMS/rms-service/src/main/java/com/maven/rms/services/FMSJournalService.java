package com.maven.rms.services;

import java.math.BigDecimal;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.*;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IFMSJournalService;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.FMSJournal;
import com.maven.rms.repositories.FMSJournalRepository;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Service
@Slf4j
public class FMSJournalService implements IFMSJournalService {
    private final FMSJournalRepository fmsJournalRepository;

    @Value("${fmsjournal.api.url}")
    private String fmsJournalURL;

    @Value("${fmsjournal.api.name}")
    private String fmsJournalName;

    @Value("${fms.ibm-client-id}")
    private String IBMClientID;

    @Autowired
    private CommonService commonSvc;

    public FMSJournalService(FMSJournalRepository fmsJournalRepository) {
        this.fmsJournalRepository = fmsJournalRepository;
    };

    @SuppressWarnings("deprecation")
    public List<FMSJournal> fms_journal_entry(FMSJournal fmsJournal) {

        List<FMSJournal> result = new ArrayList<>();

        try {
            // Trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] {
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }
            };

            // Install the all-trusting trust manager
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            // Create an OkHttpClient that uses the all-trusting trust manager
            OkHttpClient client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();

            // OkHttpClient client = new OkHttpClient().newBuilder()
            // .build();
            MediaType mediaType = MediaType.parse("application/json");
            String requestJson = "{"
                + "\"BranchID\":{\"value\":\"" + fmsJournal.getBranch_id() + "\"},"
                + "\"Description\":{\"value\":\"" + fmsJournal.getDesc() + "\"},"
                + "\"LedgerID\":{\"value\":\"" + fmsJournal.getLedger_id() + "\"},"
                + "\"Module\":{\"value\":\"" + fmsJournal.getModule() + "\"},"
                + "\"TransactionDate\":{\"value\":\"" + fmsJournal.getDt_txn() + "\"},"
                + "\"custom\":{"
                + "\"BatchModule\":{"
                + "\"AttributeEXTREFNBR\":{\"type\":\"CustomStringField\",\"value\":\"" + fmsJournal.getAttr_ext_ref_no() + "\""
                + "},"
                + "\"AttributeSYSNAME\":{\"type\":\"CustomStringField\",\"value\":\"" + fmsJournal.getAttr_ext_sys() + "\"}"
                + "}"
                + "},"
                + "\"Details\":[{"
                + "\"Account\":{\"value\":\"" + fmsJournal.getAcct1() + "\"},"
                + "\"BranchID\":{\"value\":\"" + fmsJournal.getBranch1() + "\"},"
                + "\"CreditAmount\":{\"value\":" + fmsJournal.getCredit1() + "},"
                + "\"DebitAmount\":{\"value\":" + fmsJournal.getDebit1() + "},"
                + "\"Subaccount\":{\"value\":\"" + fmsJournal.getSub_acct1() + "\"},"
                + "\"TransactionDescription\":{\"value\":\"" + fmsJournal.getTxn_desc1() + "\"}"
                + "},"
                + "{"
                + "\"Account\":{\"value\":\"" + fmsJournal.getAcct2() + "\"},"
                + "\"BranchID\":{\"value\":\"" + fmsJournal.getBranch2() + "\"},"
                + "\"CreditAmount\":{\"value\":" + fmsJournal.getCredit2() + "},"
                + "\"DebitAmount\":{\"value\":" + fmsJournal.getDebit2() + "},"
                + "\"Subaccount\":{\"value\":\"" + fmsJournal.getSub_acct2() + "\"},"
                + "\"TransactionDescription\":{\"value\":\"" + fmsJournal.getTxn_desc2() + "\"}"
                + "}]"
                + "}";

            RequestBody body = RequestBody.create(mediaType, requestJson);

            Request request = new Request.Builder()
                    .url(fmsJournalURL + "/" + fmsJournalName)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .addHeader("X-IBM-Client-Id", IBMClientID)
                    .build();
            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            try {
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("FMSJournal");
                extAudit.setI_request_body(requestJson.toString());
                extAudit.setI_response_body(responseBody);
                extAudit.setI_rms_batch_no(null);
                extAudit.setI_direction("Outgoing");
                extAudit.setI_remark(null);
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for FMS Journal: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }

            if (response.isSuccessful()) {
                //String responseBody = response.body().string();
                Gson gson = new Gson();
                FMSJournal apiResp = gson.fromJson(responseBody, FMSJournal.class);
                
                if (apiResp.getStatus().equals("200"))
                {
                    for (int i = 0; i < 1; i++) {
                        FMSJournal apiMap = new FMSJournal();
                        apiMap.setAttributeEXTSYSTEM(apiResp.getAttributeEXTSYSTEM());
                        apiMap.setBatchNbr(apiResp.getBatchNbr());
                        apiMap.setExtRefNbr(apiResp.getExtRefNbr());
                        apiMap.setStatus(apiResp.getStatus());
                        apiMap.setMessage(apiResp.getMessage());
                        apiMap.setDate(apiResp.getDate());
                        result.add(apiMap);
                    }
                }
                else {
                    // Request failed
                    log.error("Request failed with code: " + response.code());
                
                    for (int i = 0; i < 1; i++) {
                        FMSJournal apiMap = new FMSJournal();
                        apiMap.setAttributeEXTSYSTEM(apiResp.getAttributeEXTSYSTEM());
                        apiMap.setBatchNbr(apiResp.getBatchNbr());
                        apiMap.setExtRefNbr(apiResp.getExtRefNbr());
                        apiMap.setStatus(apiResp.getStatus());
                        apiMap.setMessage(apiResp.getMessage());
                        apiMap.setDate(apiResp.getDate());
                        result.add(apiMap);
                    }
                }
            } else {
                // HTTP request itself failed (not 2xx)
                String message = "Request failed";

                try {
                    JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();
                    if (json.has("Message")) {
                        message = json.get("Message").getAsString();
                    }
                } catch (Exception parseEx) {
                    log.error("Failed to parse error body as JSON", parseEx);
                }

                LocalDateTime currentDateTime = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDateTime = currentDateTime.format(formatter);
                FMSJournal apiMap = new FMSJournal();
                apiMap.setAttributeEXTSYSTEM(null);
                apiMap.setBatchNbr(null);
                apiMap.setExtRefNbr(null);
                apiMap.setStatus(Integer.toString(response.code()));
                apiMap.setMessage(message);
                apiMap.setDate(formattedDateTime);
                result.add(apiMap);
            }
        } catch (Exception e) {
            e.printStackTrace();

            LocalDateTime currentDateTime = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDateTime = currentDateTime.format(formatter);
            FMSJournal apiMap = new FMSJournal();
            apiMap.setAttributeEXTSYSTEM(null);
            apiMap.setBatchNbr(null);
            apiMap.setExtRefNbr(null);
            apiMap.setStatus("500");
            apiMap.setMessage("Server Error");
            apiMap.setDate(formattedDateTime);
            result.add(apiMap);

            log.error("Exception in " + this.getClass().toString() + " An error was encountered in "
                    + FMSJournal.class.getName() + "!", e);
        }

        return result;
    };

    public Integer fmsJnSch() {

        List<Object[]> listResult = new ArrayList<>();

        // Step 1: get FMS Journal list from Database
        listResult = fmsJournalRepository.sp_getfmsjn();

        if (listResult.size() > 0) {
            List<FMSJournal> respResult = new ArrayList<>();
            Integer result;
            Integer counter = 0;

            for (Object[] obj : listResult) {
                FMSJournal fmsJN = new FMSJournal();
                fmsJN.setBranch_id((String) obj[0]);
                fmsJN.setDesc((String) obj[1]);
                fmsJN.setLedger_id((String) obj[2]);
                fmsJN.setModule((String) obj[3]);
                fmsJN.setDt_txn((java.sql.Date) obj[4]);
                fmsJN.setAttr_ext_ref_no((String) obj[5]);
                fmsJN.setAttr_ext_sys((String) obj[6]);
                fmsJN.setAcct1((String) obj[7]);
                fmsJN.setBranch1((String) obj[8]);
                fmsJN.setCredit1((BigDecimal) obj[9]);
                fmsJN.setDebit1((BigDecimal) obj[10]);
                fmsJN.setSub_acct1((String) obj[11]);
                fmsJN.setTxn_desc1((String) obj[12]);
                fmsJN.setAcct2((String) obj[13]);
                fmsJN.setBranch2((String) obj[14]);
                fmsJN.setCredit2((BigDecimal) obj[15]);
                fmsJN.setDebit2((BigDecimal) obj[16]);
                fmsJN.setSub_acct2((String) obj[17]);
                fmsJN.setTxn_desc2((String) obj[18]);

                respResult = fms_journal_entry(fmsJN);

                fmsJN.setAttr_ext_ref_no(fmsJN.getAttr_ext_ref_no());
                fmsJN.setAttributeEXTSYSTEM(respResult.get(0).getAttributeEXTSYSTEM());
                fmsJN.setBatchNbr(respResult.get(0).getBatchNbr());
                fmsJN.setExtRefNbr(respResult.get(0).getExtRefNbr());
                fmsJN.setStatus(respResult.get(0).getStatus());
                fmsJN.setMessage(respResult.get(0).getMessage());
                fmsJN.setDate(respResult.get(0).getDate());

                result = fmsJournalRepository.sp_updfmsjn(fmsJN);

                if (result > 0) {
                    counter = counter + 1;
                }
            }

            if (listResult.size() == counter) {
                return 1;
            } else {
                return 0;
            }
        } else {
            return -1;
        }
    }
}
