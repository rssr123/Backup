package com.maven.rms.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.maven.rms.models.FMSAPIA;
import com.maven.rms.models.FMSAPIAJson;
import com.maven.rms.models.GenericValue;
import com.maven.rms.models.PaymentItemDetails;
import com.maven.rms.repositories.FMSAPIARepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FMSAPIAService {

    private final FMSAPIARepository fmsAPIARepo;

    @Value("${fmsapia.api.url}")
    private String api_url;

    @Value("${fmsapia.api.name}")
    private String api_name;

    @Value("${fms.ibm-client-id}")
    private String IBMClientID;

    @Autowired
    private CommonService commonSvc;

    public FMSAPIAService(FMSAPIARepository fmsAPIARepo) {
        this.fmsAPIARepo = fmsAPIARepo;
    }

    public List<FMSAPIA> sp_getrefunddetails() {
        List<FMSAPIA> result = new ArrayList<>();
        List<Object[]> objects = fmsAPIARepo.sp_getrefunddetails();
        List<FMSAPIA> fmsAPIAList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSAPIA fmsAPIA = new FMSAPIA();

            fmsAPIA.setEnt_nm((String) obj[0]);
            fmsAPIA.setId_ty((String) obj[1]);
            fmsAPIA.setId_no((String) obj[2]);
            fmsAPIA.setRtt_app_no((String) obj[3]);
            fmsAPIA.setCust_email((String) obj[4]);
            fmsAPIA.setRefund_slip_no((String) obj[5]);
            fmsAPIA.setRefund_total_amt((String) obj[6]);

            fmsAPIAList.add(fmsAPIA);
        }
        result = fmsAPIAList;
        return result;
    }

    public Integer sp_insfmsapia(FMSAPIA fmsapia) {
        Integer fmsApiaIH_ID = 0;
        Integer fmsApiaID_ID = 0;
        String finalResult = "";
        // Integer result = null;
        try {
            fmsApiaIH_ID = fmsAPIARepo.sp_insfmsapia(fmsapia);
            System.out.println("Generated fmsApiaIH_ID: " + fmsApiaIH_ID);

            if (fmsApiaIH_ID > 0) {
                List<PaymentItemDetails> itemDetailsList = fmsapia.getPayment_item_details();

                for (PaymentItemDetails item : itemDetailsList) {
                    System.out.println("Inserting refund item: " + item);

                    fmsApiaID_ID = fmsAPIARepo.sp_insertInvoiceItem(item, fmsApiaIH_ID, fmsapia.getRtt_app_no());

                    if (fmsApiaID_ID < 1) {
                        finalResult = "RTT Item table insert failed for item_ref_no: " + item.getItem_ref_no();
                        System.out.println(finalResult);
                        throw new Exception(finalResult);
                    }
                    System.out.println("Successfully inserted item_ref_no: " + item.getItem_ref_no());
                }

                finalResult = "Insert successful";
                System.out.println(finalResult);
            } else {
                finalResult = fmsApiaID_ID < 0 ? "Insert FMS APIA V & IH successful" : "Insert FMS APIA ID Failed";
                System.out.println(finalResult);
                throw new Exception(finalResult);
            }
        } catch (Exception e) {
            log.error("FMSAPIA ERROR: " + e.getMessage(), e);
        }

        return fmsApiaIH_ID;
    }

    public List<FMSAPIA> sp_getfmsapia() {
        List<FMSAPIA> result = new ArrayList<>();
        List<Object[]> objects = fmsAPIARepo.sp_getfmsapia();
        List<FMSAPIA> fmsAPIAList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSAPIA fmsAPIA = new FMSAPIA();

            fmsAPIA.setFms_apia_v_id((String) obj[0]);
            fmsAPIA.setExt_sys((String) obj[1]);
            fmsAPIA.setVendor_id((String) obj[2]);
            fmsAPIA.setVendor_nm((String) obj[3]);
            fmsAPIA.setId_ty((String) obj[4]);
            fmsAPIA.setId_no((String) obj[5]);
            fmsAPIA.setPm((String) obj[6]);
            fmsAPIA.setP_desc((String) obj[7]);
            fmsAPIA.setP_id((String) obj[8]);
            fmsAPIA.setP_value((String) obj[9]);
            fmsAPIA.setAddr1((String) obj[10]);
            fmsAPIA.setAddr2((String) obj[11]);
            fmsAPIA.setAddr3((String) obj[12]);
            fmsAPIA.setCity((String) obj[13]);
            fmsAPIA.setCountry((String) obj[14]);
            fmsAPIA.setPostcode((String) obj[15]);
            fmsAPIA.setState((String) obj[16]);
            fmsAPIA.setEmail((String) obj[17]);
            fmsAPIA.setPhone((String) obj[18]);
            fmsAPIA.setFms_ext_sys((String) obj[19]);
            fmsAPIA.setFms_ref_no((String) obj[20]);
            fmsAPIA.setFms_ven_ref((String) obj[21]);
            fmsAPIA.setFms_status((String) obj[22]);
            fmsAPIA.setFms_msg((String) obj[23]);
            fmsAPIA.setFms_date((Date) obj[24]);
            fmsAPIA.setIh_amt((String) obj[25]);
            fmsAPIA.setIh_date((Date) obj[26]);
            fmsAPIA.setIh_desc((String) obj[27]);
            fmsAPIA.setIh_hold((String) obj[28]);
            fmsAPIA.setIh_type((String) obj[29]);
            fmsAPIA.setVendor_ref((String) obj[30]);
            fmsAPIA.setFms_apia_ih_id((String) obj[31]);
            fmsAPIA.setAcct((String) obj[32]);
            fmsAPIA.setAmt((String) obj[33]);
            fmsAPIA.setBranch((String) obj[34]);
            fmsAPIA.setEx_cost((String) obj[35]);
            fmsAPIA.setQty((String) obj[36]);
            fmsAPIA.setSub_acct((String) obj[37]);
            fmsAPIA.setTax_ca((String) obj[38]);
            fmsAPIA.setTxn_desc((String) obj[39]);
            fmsAPIA.setUnit((String) obj[40]);
            fmsAPIA.setUom((String) obj[41]);
            //new column
            fmsAPIA.setDescription1((String) obj[42]);
            fmsAPIA.setPaymentinstructionsid1((String) obj[43]);
            fmsAPIA.setPaymentmethod1((String) obj[44]);
            fmsAPIA.setValue1((String) obj[45]);

            fmsAPIA.setDescription2((String) obj[46]);
            fmsAPIA.setPaymentinstructionsid2((String) obj[47]);
            fmsAPIA.setPaymentmethod2((String) obj[48]);
            fmsAPIA.setValue2((String) obj[49]);

            fmsAPIA.setDescription3((String) obj[50]);
            fmsAPIA.setPaymentinstructionsid3((String) obj[51]);
            fmsAPIA.setPaymentmethod3((String) obj[52]);
            fmsAPIA.setValue3((String) obj[53]);

            fmsAPIAList.add(fmsAPIA);
        }
        result = fmsAPIAList;

        return result;
    }

    public Integer sp_updfmsapia(FMSAPIA fmsapia) {
        Integer result = null;
        result = fmsAPIARepo.sp_updfmsapia(fmsapia);
        return result;
    }

    public FMSAPIAJson generateStringBody(FMSAPIAJson inputObject) {
        // 1. Set up Custom -> Document -> AttributeSYSNAME
        FMSAPIAJson.Custom custom = new FMSAPIAJson.Custom();
        FMSAPIAJson.Document document = new FMSAPIAJson.Document();
        FMSAPIAJson.Attribute attributeSYSNAME = new FMSAPIAJson.Attribute();
        attributeSYSNAME.setType("CustomStringField");
        attributeSYSNAME.setValue("RMS");
        document.setAttributeSYSNAME(attributeSYSNAME);
        custom.setDocument(document);
        inputObject.setCustom(custom);

        // 2. Set up VendorInfo and CurrentVendor
        FMSAPIAJson.VendorInfo vendorInfo = new FMSAPIAJson.VendorInfo();
        vendorInfo.setVendor(new GenericValue<>("B21108"));
        vendorInfo.setVendorName(new GenericValue<>("20.000")); // VendorName with a numeric value

        FMSAPIAJson.CurrentVendor currentVendor = new FMSAPIAJson.CurrentVendor();
        FMSAPIAJson.Attribute usrIdentityType = new FMSAPIAJson.Attribute();
        usrIdentityType.setType("CustomStringField");
        usrIdentityType.setValue("Business Registration Number");
        currentVendor.setUsrIdentityType(usrIdentityType);

        FMSAPIAJson.Attribute usrIdentityNbr = new FMSAPIAJson.Attribute();
        usrIdentityNbr.setType("CustomStringField");
        usrIdentityNbr.setValue("200501034355");
        currentVendor.setUsrIdentityNbr(usrIdentityNbr);

        FMSAPIAJson.VendorCustom vendorCustom = new FMSAPIAJson.VendorCustom();
        vendorCustom.setCurrentVendor(currentVendor);
        vendorInfo.setCustom(vendorCustom);

        // 3. Add Payment Instructions
        List<FMSAPIAJson.PaymentInstruction> paymentInstructionsList = new ArrayList<>();
        paymentInstructionsList.add(createPaymentInstruction("12346", "1", "EFT", "5643242291571"));
        paymentInstructionsList.add(createPaymentInstruction("Beneficiary Name", "2", "EFT", "RMS VENDOR SDN BHD"));
        paymentInstructionsList.add(createPaymentInstruction("Bank Code", "3", "EFT", "MBBEMYKL"));
        vendorInfo.setPaymentInstructions(paymentInstructionsList);

        // 4. Set up MainContact and Address
        FMSAPIAJson.MainContact mainContact = new FMSAPIAJson.MainContact();
        FMSAPIAJson.Address address = new FMSAPIAJson.Address();
        address.setAddressLine1(new GenericValue<>("72 Jalan BK 9/3B"));
        address.setAddressLine2(new GenericValue<>("Bandar Kinrara"));
        address.setAddressLine3(new GenericValue<>("11"));
        address.setState(new GenericValue<>("Selangor"));
        address.setPostalCode(new GenericValue<>("47180"));
        address.setCity(new GenericValue<>("PUCHONG"));
        address.setCountry(new GenericValue<>("MY"));
        mainContact.setAddress(address);
        mainContact.setEmail(new GenericValue<>("test@gmail.com"));
        vendorInfo.setMainContact(mainContact);

        // Add VendorInfo to the list
        List<FMSAPIAJson.VendorInfo> vendorInfoList = new ArrayList<>();
        vendorInfoList.add(vendorInfo);
        inputObject.setVendorInfo(vendorInfoList);

        // 5. Set up InvoiceHeader
        List<FMSAPIAJson.InvoiceHeader> invoiceHeaderList = new ArrayList<>();
        FMSAPIAJson.InvoiceHeader invoiceHeader = new FMSAPIAJson.InvoiceHeader();
        invoiceHeader.setType(new GenericValue<>("Bill"));
        invoiceHeader.setBranchID(new GenericValue<>("H"));
        invoiceHeader.setDate(new GenericValue<>("2023-09-09"));
        invoiceHeader.setDescription(new GenericValue<>("RMS Refund Slip"));
        invoiceHeader.setHold(new GenericValue<>(false));
        invoiceHeader.setVendorRef(new GenericValue<>("RMS-12345"));
        invoiceHeader.setAmount(new GenericValue<>("100.0"));

        // Add Details to InvoiceHeader
        List<FMSAPIAJson.Detail> detailsList = new ArrayList<>();
        FMSAPIAJson.Detail details = new FMSAPIAJson.Detail();
        details.setAccount(new GenericValue<>("L25106"));
        details.setAmount(new GenericValue<>("100.0000"));
        details.setBranch(new GenericValue<>("H"));
        details.setQty(new GenericValue<>("10.000000"));
        details.setSubaccount(new GenericValue<>("P1H000"));
        details.setTransactionDescription(new GenericValue<>("Invoice Refund Line 1"));
        details.setUnitCost(new GenericValue<>("10.000000"));
        details.setUom(new GenericValue<>("EACH"));
        detailsList.add(details);
        invoiceHeader.setDetails(detailsList);
        invoiceHeaderList.add(invoiceHeader);

        inputObject.setInvoiceHeader(invoiceHeaderList);

        // Return the updated input object
        return inputObject;
    }

    // Utility method to create PaymentInstruction
    private FMSAPIAJson.PaymentInstruction createPaymentInstruction(
            String description, String id, String method, String value) {
        FMSAPIAJson.PaymentInstruction paymentInstruction = new FMSAPIAJson.PaymentInstruction();
        paymentInstruction.setDescription(new GenericValue<>(description));
        paymentInstruction.setPaymentInstructionsID(new GenericValue<>(id));
        paymentInstruction.setPaymentMethod(new GenericValue<>(method));
        paymentInstruction.setValue(new GenericValue<>(value));
        return paymentInstruction;
    }

    @SuppressWarnings("deprecation")
    public List<FMSAPIA> fms_api_apia(String stringBody, FMSAPIA existingFmsApia) {
        List<FMSAPIA> result = new ArrayList<>();

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
            // System.out.println("Response Body: " + response.toString());
            // Close the connection
            connection.disconnect();

            try {
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("FMSAPIA");
                extAudit.setI_request_body(stringBody);
                extAudit.setI_response_body(response.toString());
                extAudit.setI_rms_batch_no(null);
                extAudit.setI_direction("Outgoing");
                extAudit.setI_remark(null);
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for FMS APPIA: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }

            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            FMSAPIA fmsapia = new FMSAPIA();

            fmsapia.setFms_apia_v_id(existingFmsApia.getFms_apia_v_id());
            // fmsapia.setArv_reason(existingFmsApia.getArv_reason());

            // Utility method to extract fields dynamically

            // resp_attr_ext_sys;
            // resp_ref_no;
            // resp_vendor_ref;
            // resp_status;
            // resp_msg;
            // resp_date;

            fmsapia.setResp_attr_ext_sys(extractField(jsonResponse, "AttributeEXTSYSTEM"));
            fmsapia.setFms_ref_no(extractField(jsonResponse, "ReferenceNbr"));
            fmsapia.setVendor_ref(extractField(jsonResponse, "VendorRef"));
            fmsapia.setResp_status(extractField(jsonResponse, "Status"));
            fmsapia.setResp_msg(extractField(jsonResponse, "Message"));

            // Handle "Date" field
            if (jsonResponse.has("Date")) {
                String dateString = jsonResponse.get("Date").getAsString();
                try {
                    Date date = dateFormat.parse(dateString);
                    fmsapia.setResp_date(date);
                } catch (ParseException e) {
                    log.error("FMSAPIA ERROR: " + e.getMessage(), e);

                }
            }

            result.add(fmsapia);

            this.sp_updfmsapia(fmsapia);

        } catch (Exception e) {
            log.error("FMSAPIA ERROR: " + e.getMessage(), e);
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

    public List<FMSAPIA> fms_apia_sch() throws JsonProcessingException {
        List<FMSAPIA> result = new ArrayList<>();

        // Get all FMSAPIA data from stored procedure
        List<FMSAPIA> fmsAPIA = this.sp_getfmsapia();

        if (fmsAPIA.size() > 0) {
            // Group entities by vendor_ref (invoice header identifier)
            Map<String, List<FMSAPIA>> groupedByVendorRef = fmsAPIA.stream()
                    .collect(Collectors.groupingBy(
                            entity -> entity.getVendor_ref() != null ? entity.getVendor_ref()
                                    : "default-" + System.identityHashCode(entity)));

            // Process each group (each unique invoice header) separately
            for (Map.Entry<String, List<FMSAPIA>> entry : groupedByVendorRef.entrySet()) {
                String vendorRef = entry.getKey();
                List<FMSAPIA> headerEntities = entry.getValue();

                try {

                    // Create JSON for this specific invoice header and its details
                    FMSAPIAJson fmsAPIAJson = this.mapToJson(headerEntities);

                    if (fmsAPIAJson != null) {
                        ObjectMapper mapper = new ObjectMapper();
                        String stringBody = mapper.writeValueAsString(fmsAPIAJson);

                        // Make API call for this invoice header
                        List<FMSAPIA> apiResult = this.fms_api_apia(stringBody, headerEntities.get(0));
                        result.addAll(apiResult);

                        // Optional: Add delay between API calls to avoid overwhelming the server
                        try {
                            Thread.sleep(1000); // 1 second delay
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            log.warn("FMSAPIA WARN: Thread interrupted during API call delay", e);
                        }
                    }
                } catch (Exception e) {
                    // Log the error and continue with the next group
                    log.error("FMSAPIA ERROR: Failed to process vendor_ref {}: {}", vendorRef, e.getMessage(), e);
                }
            }
        }

        return result;
    }

    // public List<FMSAPIA> fms_apia_sch() throws JsonProcessingException {
    // List<FMSAPIA> result = new ArrayList<>();

    // // Get all FMSAPIA data from stored procedure
    // List<FMSAPIA> fmsAPIA = this.sp_getfmsapia();

    // if (fmsAPIA.size() > 0) {
    // // Group entities by vendor_ref (invoice header identifier)
    // Map<String, List<FMSAPIA>> groupedByVendorRef = fmsAPIA.stream()
    // .collect(Collectors.groupingBy(
    // entity -> entity.getVendor_ref() != null ? entity.getVendor_ref() :
    // "default"));

    // // Process each group (each unique invoice header) separately
    // for (Map.Entry<String, List<FMSAPIA>> entry : groupedByVendorRef.entrySet())
    // {
    // String vendorRef = entry.getKey();
    // List<FMSAPIA> headerEntities = entry.getValue();

    // try {
    // log.error("FMSAPIA INFO: Processing vendor_ref: {} with {} entities",
    // vendorRef,
    // headerEntities.size());

    // // Create JSON for this specific invoice header and its details
    // FMSAPIAJson fmsAPIAJson = this.mapToJson(headerEntities);

    // if (fmsAPIAJson != null) {
    // ObjectMapper mapper = new ObjectMapper();
    // String stringBody = mapper.writeValueAsString(fmsAPIAJson);

    // log.error("FMSAPIA DEBUG: JSON body for vendor_ref {}: {}", vendorRef,
    // stringBody);

    // // Make API call for this invoice header
    // // Use the first entity as representative for the API call response handling
    // List<FMSAPIA> apiResult = this.fms_api_apia(stringBody,
    // headerEntities.get(0));
    // result.addAll(apiResult);

    // // Optional: Add delay between API calls to avoid overwhelming the server
    // try {
    // Thread.sleep(5000); // 5 second delay
    // } catch (InterruptedException e) {
    // Thread.currentThread().interrupt();
    // log.warn("FMSAPIA WARN: Thread interrupted during API call delay", e);
    // }
    // }
    // } catch (Exception e) {
    // // Log the error and continue with the next record
    // log.error("FMSAPIA ERROR: Failed to process vendor_ref {}: {}", vendorRef,
    // e.getMessage(), e);
    // }
    // }
    // }

    // return result;
    // }

    // public List<FMSAPIA> fms_apia_sch() throws JsonProcessingException {
    // List<FMSAPIA> result = new ArrayList<>();
    // List<FMSAPIA> fmsAPIA = this.sp_getfmsapia();

    // if (!fmsAPIA.isEmpty()) {
    // Map<String, List<FMSAPIA>> groupedByVendorRef = fmsAPIA.stream()
    // .collect(Collectors.groupingBy(
    // entity -> entity.getVendor_ref() != null ? entity.getVendor_ref() :
    // "default"));

    // for (Map.Entry<String, List<FMSAPIA>> entry : groupedByVendorRef.entrySet())
    // {
    // String vendorRef = entry.getKey();
    // List<FMSAPIA> headerEntities = entry.getValue();

    // try {
    // log.info("Processing vendor_ref [{}] with {} records", vendorRef,
    // headerEntities.size());

    // FMSAPIAJson fmsAPIAJson = this.mapToJson(headerEntities);
    // if (fmsAPIAJson != null) {
    // String stringBody = new ObjectMapper().writeValueAsString(fmsAPIAJson);

    // log.info("Sending payload to API for vendor_ref [{}]: {}", vendorRef,
    // stringBody);

    // List<FMSAPIA> apiResult = this.fms_api_apia(stringBody,
    // headerEntities.get(0));
    // result.addAll(apiResult);
    // }

    // // Optional: Add delay between API calls to avoid overwhelming the server
    // try {
    // Thread.sleep(5000); // 5 second delay
    // } catch (InterruptedException e) {
    // Thread.currentThread().interrupt();
    // log.warn("FMSAPIA WARN: Thread interrupted during API call delay", e);
    // }

    // } catch (Exception e) {
    // log.error("Failed to process vendor_ref [{}]: {}", vendorRef, e.getMessage(),
    // e);
    // // Continue with next group even if current one fails
    // }
    // }
    // }

    // return result;
    // }

    // Mapper

    /**
     * Maps FMSAPIA entity to FMSAPIAJson request
     * 
     * @param entity the entity to map from
     * @return mapped FMSAPIAJson request
     */
    public FMSAPIAJson mapToJson(FMSAPIA entity) {
        if (entity == null) {
            return null;
        }

        FMSAPIAJson json = new FMSAPIAJson();

        // Map custom attributes
        json.setCustom(mapCustomAttributes(entity));

        // Map vendor information
        json.setVendorInfo(Arrays.asList(mapVendorInfo(entity)));

        // Map invoice header (single header for this entity)
        json.setInvoiceHeader(Arrays.asList(mapInvoiceHeader(entity)));

        return json;
    }

    // Mapper
    /**
     * Maps list of FMSAPIA entities to FMSAPIAJson request
     * Groups entities by invoice header and aggregates details
     * 
     * @param entities list of entities (may contain multiple details for same
     *                 invoice header)
     * @return mapped FMSAPIAJson request
     */
    public FMSAPIAJson mapToJson(List<FMSAPIA> entities) {
        if (entities == null || entities.isEmpty()) {
            return null;
        }

        // Use the first entity as the base for common fields
        FMSAPIA baseEntity = entities.get(0);
        FMSAPIAJson json = new FMSAPIAJson();

        // Map custom attributes from base entity
        json.setCustom(mapCustomAttributes(baseEntity));

        // Map vendor information from base entity
        json.setVendorInfo(Arrays.asList(mapVendorInfo(baseEntity)));

        // Group entities by fms_apia_ih_id (invoice header ID) and map to invoice
        // headers with details
        Map<String, List<FMSAPIA>> groupedByInvoiceHeader = entities.stream()
                .collect(Collectors.groupingBy(
                        entity -> entity.getFms_apia_ih_id() != null ? entity.getFms_apia_ih_id() : "default"));

        List<FMSAPIAJson.InvoiceHeader> invoiceHeaders = new ArrayList<>();

        for (Map.Entry<String, List<FMSAPIA>> entry : groupedByInvoiceHeader.entrySet()) {
            String invoiceHeaderId = entry.getKey();
            List<FMSAPIA> headerEntities = entry.getValue();

            // Filter entities that have actual detail data (non-null account info)
            List<FMSAPIA> entitiesWithDetails = headerEntities.stream()
                    .filter(entity -> entity.getAcct() != null && !entity.getAcct().trim().isEmpty())
                    .collect(Collectors.toList());

            FMSAPIAJson.InvoiceHeader invoiceHeader;

            if (!entitiesWithDetails.isEmpty()) {
                // Create invoice header with details
                invoiceHeader = mapInvoiceHeaderWithDetails(entitiesWithDetails);
            } else {
                // Create invoice header without details (use first entity for header info)
                invoiceHeader = mapInvoiceHeaderWithoutDetails(headerEntities.get(0));
            }

            if (invoiceHeader != null) {
                invoiceHeaders.add(invoiceHeader);
            }
        }

        json.setInvoiceHeader(invoiceHeaders);
        return json;
    }

    /**
     * Maps invoice header without details (for headers that don't have detail
     * records)
     */
    private FMSAPIAJson.InvoiceHeader mapInvoiceHeaderWithoutDetails(FMSAPIA entity) {
        if (entity == null) {
            return null;
        }

        FMSAPIAJson.InvoiceHeader invoiceHeader = new FMSAPIAJson.InvoiceHeader();

        invoiceHeader.setType(createGenericValue(entity.getIh_type()));
        invoiceHeader.setBranchID(createGenericValue(entity.getBranch()));
        invoiceHeader.setDate(createGenericValue(formatDate(entity.getIh_date())));
        invoiceHeader.setDescription(createGenericValue(entity.getIh_desc()));
        invoiceHeader.setHold(createGenericValue(parseBoolean(entity.getIh_hold())));
        invoiceHeader.setVendorRef(createGenericValue(entity.getVendor_ref()));
        invoiceHeader.setAmount(createGenericValue(entity.getIh_amt()));

        // Set empty details list for headers without details
        invoiceHeader.setDetails(new ArrayList<>());

        return invoiceHeader;
    }

    private FMSAPIAJson.Custom mapCustomAttributes(FMSAPIA entity) {
        FMSAPIAJson.Custom custom = new FMSAPIAJson.Custom();
        FMSAPIAJson.Document document = new FMSAPIAJson.Document();
        FMSAPIAJson.Attribute attribute = new FMSAPIAJson.Attribute();

        attribute.setType("system");
        attribute.setValue(entity.getExt_sys());

        document.setAttributeSYSNAME(attribute);
        custom.setDocument(document);

        return custom;
    }

    private FMSAPIAJson.VendorInfo mapVendorInfo(FMSAPIA entity) {
        FMSAPIAJson.VendorInfo vendorInfo = new FMSAPIAJson.VendorInfo();

        // Map basic vendor fields
        vendorInfo.setVendor(createGenericValue(entity.getVendor_id()));
        vendorInfo.setVendorName(createGenericValue(entity.getVendor_nm()));

        // Map custom vendor fields
        vendorInfo.setCustom(mapVendorCustom(entity));

        // // Map payment instructions
        // vendorInfo.setPaymentInstructions(Arrays.asList(mapPaymentInstruction(entity)));

        // New Mapping PaymentInstructions
        List<FMSAPIAJson.PaymentInstruction> paymentInstructions = new ArrayList<>();
        paymentInstructions.add(mapPaymentInstruction(entity.getDescription1(), entity.getPaymentinstructionsid1(), entity.getPaymentmethod1(), entity.getValue1()));
        paymentInstructions.add(mapPaymentInstruction(entity.getDescription2(), entity.getPaymentinstructionsid2(), entity.getPaymentmethod2(), entity.getValue2()));
        paymentInstructions.add(mapPaymentInstruction(entity.getDescription3(), entity.getPaymentinstructionsid3(), entity.getPaymentmethod3(), entity.getValue3()));

        vendorInfo.setPaymentInstructions(paymentInstructions);

        // Map main contact
        vendorInfo.setMainContact(mapMainContact(entity));

        return vendorInfo;
    }

    private FMSAPIAJson.VendorCustom mapVendorCustom(FMSAPIA entity) {
        FMSAPIAJson.VendorCustom vendorCustom = new FMSAPIAJson.VendorCustom();
        FMSAPIAJson.CurrentVendor currentVendor = new FMSAPIAJson.CurrentVendor();

        // Map identity type
        FMSAPIAJson.Attribute identityType = new FMSAPIAJson.Attribute();
        identityType.setType("identity");
        identityType.setValue(entity.getId_ty());
        currentVendor.setUsrIdentityType(identityType);

        // Map identity number
        FMSAPIAJson.Attribute identityNumber = new FMSAPIAJson.Attribute();
        identityNumber.setType("identity");
        identityNumber.setValue(entity.getId_no());
        currentVendor.setUsrIdentityNbr(identityNumber);

        vendorCustom.setCurrentVendor(currentVendor);
        return vendorCustom;
    }

    // private FMSAPIAJson.PaymentInstruction mapPaymentInstruction(FMSAPIA entity) {
    //     FMSAPIAJson.PaymentInstruction paymentInstruction = new FMSAPIAJson.PaymentInstruction();

    //     paymentInstruction.setDescription(createGenericValue(entity.getDescription1()));
    //     paymentInstruction.setPaymentInstructionsID(createGenericValue(entity.getPaymentInstructionsID1()));
    //     paymentInstruction.setPaymentMethod(createGenericValue(entity.getPaymentMethod1()));
    //     paymentInstruction.setValue(createGenericValue(entity.getValue1()));

    //     return paymentInstruction;
    // }

    private FMSAPIAJson.PaymentInstruction mapPaymentInstruction(String description, String paymentInstructionsID, String paymentMethod, String value) {
        FMSAPIAJson.PaymentInstruction paymentInstruction = new FMSAPIAJson.PaymentInstruction();

        paymentInstruction.setDescription(createGenericValue(description));
        paymentInstruction.setPaymentInstructionsID(createGenericValue(paymentInstructionsID));
        paymentInstruction.setPaymentMethod(createGenericValue(paymentMethod));
        paymentInstruction.setValue(createGenericValue(value));

        return paymentInstruction;
    }

    private FMSAPIAJson.MainContact mapMainContact(FMSAPIA entity) {
        FMSAPIAJson.MainContact mainContact = new FMSAPIAJson.MainContact();

        // Map email
        mainContact.setEmail(createGenericValue(entity.getEmail()));

        // Map address
        FMSAPIAJson.Address address = new FMSAPIAJson.Address();
        address.setAddressLine1(createGenericValue(entity.getAddr1()));
        address.setAddressLine2(createGenericValue(entity.getAddr2()));
        address.setAddressLine3(createGenericValue(entity.getAddr3()));
        address.setCity(createGenericValue(entity.getCity()));
        address.setCountry(createGenericValue(entity.getCountry()));
        address.setPostalCode(createGenericValue(entity.getPostcode()));
        address.setState(createGenericValue(entity.getState()));

        mainContact.setAddress(address);
        return mainContact;
    }

    private FMSAPIAJson.InvoiceHeader mapInvoiceHeader(FMSAPIA entity) {
        FMSAPIAJson.InvoiceHeader invoiceHeader = new FMSAPIAJson.InvoiceHeader();

        invoiceHeader.setType(createGenericValue(entity.getIh_type()));
        invoiceHeader.setBranchID(createGenericValue(entity.getBranch()));
        invoiceHeader.setDate(createGenericValue(formatDate(entity.getIh_date())));
        invoiceHeader.setDescription(createGenericValue(entity.getIh_desc()));
        invoiceHeader.setHold(createGenericValue(parseBoolean(entity.getIh_hold())));
        invoiceHeader.setVendorRef(createGenericValue(entity.getVendor_ref()));
        invoiceHeader.setAmount(createGenericValue(entity.getIh_amt()));

        // Map single detail for this entity
        invoiceHeader.setDetails(Arrays.asList(mapDetail(entity)));

        return invoiceHeader;
    }

    private FMSAPIAJson.InvoiceHeader mapInvoiceHeaderWithDetails(List<FMSAPIA> entities) {
        if (entities == null || entities.isEmpty()) {
            return null;
        }

        // Use first entity for header information
        FMSAPIA headerEntity = entities.get(0);
        FMSAPIAJson.InvoiceHeader invoiceHeader = new FMSAPIAJson.InvoiceHeader();

        invoiceHeader.setType(createGenericValue(headerEntity.getIh_type()));
        invoiceHeader.setBranchID(createGenericValue(headerEntity.getBranch()));
        invoiceHeader.setDate(createGenericValue(formatDate(headerEntity.getIh_date())));
        invoiceHeader.setDescription(createGenericValue(headerEntity.getIh_desc()));
        invoiceHeader.setHold(createGenericValue(parseBoolean(headerEntity.getIh_hold())));
        invoiceHeader.setVendorRef(createGenericValue(headerEntity.getVendor_ref()));
        invoiceHeader.setAmount(createGenericValue(headerEntity.getIh_amt()));

        // Map all details for this invoice header
        List<FMSAPIAJson.Detail> details = entities.stream()
                .map(this::mapDetail)
                .collect(Collectors.toList());

        invoiceHeader.setDetails(details);

        return invoiceHeader;
    }

    private FMSAPIAJson.Detail mapDetail(FMSAPIA entity) {
        FMSAPIAJson.Detail detail = new FMSAPIAJson.Detail();

        detail.setAccount(createGenericValue(entity.getAcct()));
        detail.setAmount(createGenericValue(entity.getAmt()));
        detail.setBranch(createGenericValue(entity.getBranch()));
        detail.setQty(createGenericValue(entity.getQty()));
        detail.setSubaccount(createGenericValue(entity.getSub_acct()));
        detail.setTransactionDescription(createGenericValue(entity.getTxn_desc()));
        detail.setUnitCost(createGenericValue(entity.getUnit())); // assuming unitCost is the correct field
        detail.setUom(createGenericValue(entity.getUom()));

        return detail;
    }

    private <T> GenericValue<T> createGenericValue(T value) {
        if (value == null) {
            return null;
        }
        return new GenericValue<>(value);
    }

    private String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    private Boolean parseBoolean(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Boolean.parseBoolean(value.trim());
    }

}
