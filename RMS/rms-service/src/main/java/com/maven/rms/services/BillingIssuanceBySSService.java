package com.maven.rms.services;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IBillingIssuanceBySSService;
import com.maven.rms.models.BillingIssuanceBySBillingDoc;
import com.maven.rms.models.BillingIssuanceBySBillingDocRequest;
import com.maven.rms.models.BillingIssuanceBySSBilCustomerRequest;
import com.maven.rms.models.BillingIssuanceBySSBilStatusRequest;
import com.maven.rms.models.BillingIssuanceBySSBillingChildDetails;
import com.maven.rms.models.BillingIssuanceBySSBillingDetails;
import com.maven.rms.models.BillingIssuanceBySSBillingDetailsRequest;
import com.maven.rms.models.BillingIssuanceBySSBillingItemDetails;
import com.maven.rms.models.BillingIssuanceBySSBillingMethod;
import com.maven.rms.models.BillingIssuanceBySSHistory;
import com.maven.rms.models.BillingIssuanceBySSListOfIssuance;
import com.maven.rms.models.BillingIssuanceBySSListing;
import com.maven.rms.models.BillingIssuanceBySSListingRequest;
import com.maven.rms.models.BillingIssuanceBySSListofBilItems;
import com.maven.rms.models.BillingIssuanceBySSPaymentDetails;
import com.maven.rms.models.BillingIssuanceBySSRunnoRequest;
import com.maven.rms.models.BillingTypeCode;
import com.maven.rms.models.BillingTypeCodeRequest;
import com.maven.rms.models.Email;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.SubmitBillingChild;
import com.maven.rms.models.SubmitBillingCust;
import com.maven.rms.models.SubmitBillingItem;
import com.maven.rms.models.SubmitBillingRequest;
import com.maven.rms.repositories.BillingIssuanceBySSRepository;
import com.maven.rms.utils.RMSLogger;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillingIssuanceBySSService implements IBillingIssuanceBySSService {

    @Autowired
    private MTTService mttSvc;
    @Autowired
    private EmailService emailSvc;
    @Autowired
    private BillingService bSvc;
    @Autowired
    private AuthService authService;

    @Value("${rms.application.backPortalURL}")
    private String onlinePortalUrl;
    @Value("${rms.application.onlinePortalURL}")
    private String publicPortalUrl;

    private final BillingIssuanceBySSRepository bilIssBySSRepository;

    public BillingIssuanceBySSService(BillingIssuanceBySSRepository bilIssBySSRepository) {
        this.bilIssBySSRepository = bilIssBySSRepository;
    }

    @Override
    public List<BillingTypeCode> sp_getbibssbiltypecode(BillingTypeCodeRequest billingTypeCodeRequest) {

        List<BillingTypeCode> result = Collections.emptyList();

        List<Object[]> objects = bilIssBySSRepository.sp_getbibssbiltypecode(billingTypeCodeRequest);

        result = convertToGetBillingItemDetails(objects);

        return result;
    }

    private List<BillingTypeCode> convertToGetBillingItemDetails(List<Object[]> objects) {
        List<BillingTypeCode> billingTypeCodesList = new ArrayList<>();

        for (Object[] obj : objects) {
            BillingTypeCode billingTypeCode = new BillingTypeCode();

            billingTypeCode.setBltc_id((Integer) obj[0]);
            billingTypeCode.setBt_cd((String) obj[1]);
            billingTypeCode.setBt_ty((String) obj[2]);
            billingTypeCode.setBt_desc((String) obj[3]);
            billingTypeCode.setClass_id((String) obj[4]);
            billingTypeCode.setSs_cd((String) obj[5]);
            billingTypeCode.setBltc_item_id((Integer) obj[6]);
            billingTypeCode.setMft_pk((Integer) obj[7]);
            billingTypeCode.setMft_id((String) obj[8]);
            billingTypeCode.setDps_mft_pk((Integer) obj[9]);
            billingTypeCode.setDps_mft_id((String) obj[10]);
            billingTypeCode.setFee_detail_nm_e((String) obj[11]);
            billingTypeCode.setUnit_fee((BigDecimal) obj[12]);
            billingTypeCode.setTax_cd_id((Integer) obj[13]);
            billingTypeCode.setTax_pct((BigDecimal) obj[14]);
            billingTypeCode.setSs_nm((String) obj[15]);
            billingTypeCode.setTotal((Integer) obj[16]);

            billingTypeCodesList.add(billingTypeCode);
        }
        return billingTypeCodesList;
    }

    @Override
    public Integer sp_insbilissbyssbilcust(BillingIssuanceBySSBilCustomerRequest bilCustRequest, String username,
            String custIP) {

        Integer bilId = 0;
        Integer bil_item_id = 0;
        Integer bil_child_id = 0;
        String finalResult = "";
        // String rmsType="Online";
        // Integer mtt_id = 0;

        try {

            // BigInteger result = BigInteger.ZERO;

            bilId = bilIssBySSRepository.sp_insbilissbyssbilcust(bilCustRequest);

            // loop insert billing item
            if (bilId > 0) {

                List<BillingIssuanceBySSBillingItemDetails> billingItemDetailsList = bilCustRequest
                        .getI_billingItemDetails();
                List<BillingIssuanceBySSBillingChildDetails> billingChildDetailsList = bilCustRequest
                        .getI_billingChildDetails();

                for (BillingIssuanceBySSBillingItemDetails bilItemDets : billingItemDetailsList) {

                    bil_item_id = bilIssBySSRepository.sp_insbilissbyssbilitem(bilItemDets, bilId, username);

                    if (bil_item_id < 1) {
                        bilIssBySSRepository.sp_removebilissbyss(bilId);
                        finalResult = "Billing Item table insert failed";
                        throw new Exception(finalResult);
                    }
                }

                for (BillingIssuanceBySSBillingChildDetails bilChildDets : billingChildDetailsList) {

                    bil_child_id = bilIssBySSRepository.sp_insbilissbyssbilchild(bilChildDets, bilId, username);

                    if (bil_child_id < 1) {
                        bilIssBySSRepository.sp_removebilissbyss(bilId);
                        finalResult = "Billing Item table insert failed";
                        throw new Exception(finalResult);
                    }
                }

                finalResult = "insert successful";

                if (bilCustRequest.getI_billing_mthd().equals("O")) {

                    // Integer statusCode = sp_confirmnewbill(bilCustRequest.getI_billing_no(),
                    // "system");
                    Integer statusCode = bSvc.confirmBill(bilCustRequest.getI_billing_no(),
                            authService.getLoginUserName(), true);

                    if (statusCode < 1) {
                        log.error("Exception in " + this.getClass().toString()
                                + "insbilissbyssbilcust func - confirmBill failed! Code: " + statusCode.toString());
                        // RMSLogger.error("BillingIssuance Scheduler job error: " +
                        // bilCustRequest.getI_billing_no()
                        // + " failed to issue billing due to statusCode: " +
                        // Integer.toString(statusCode));
                        finalResult = "Billing Item table insert failed";
                        // throw new Exception(finalResult); //the throw new exception is remove because
                        // should not block the flow, the image can get later
                    }
                }

            }
            // else if (bilId < 0) {

            // finalResult = "update successful";

            // }
            else {
                finalResult = "Billing table insert failed";
                // bilId = 0;
                throw new Exception(finalResult);
            }

        } catch (NumberFormatException e) {
            if (bilId > 0) {
                bilIssBySSRepository.sp_removebilissbyss(bilId);
            }
            bilId = -1;
            finalResult = e.getMessage();
            log.error("Exception in " + this.getClass().toString(), e);
        } catch (EmptyResultDataAccessException e) {
            if (bilId > 0) {
                bilIssBySSRepository.sp_removebilissbyss(bilId);
            }
            bilId = -1;
            finalResult = e.getMessage();
            log.error("Exception in " + this.getClass().toString(), e);
        } catch (Exception e) {
            if (bilId > 0) {
                bilIssBySSRepository.sp_removebilissbyss(bilId);
            }
            bilId = -1;
            finalResult = e.getMessage();
            log.error("Exception in " + this.getClass().toString(), e);
        }

        return bilId;
    }

    // private Integer sp_insertPaymentMTT(BillingIssuanceBySSBilCustomerRequest
    // bilCustRequest, String username, String custIP) {
    // //List<TaxCode> result = Collections.emptyList(); // Define a default return
    // value

    // Integer mtt_id =0;
    // Integer mtt_item_id=0;

    // //temp hardcode rms_type to online for now
    // String rmsType="Online";

    // String finalResult="";

    // try {
    // // Perform the operation
    // /*
    // mtt_id =
    // opRep.sp_insertPaymentMTT(rmsType,paymentRequest.getSs_cd(),paymentRequest.getOrn_no(),paymentRequest.getOrn_dt(),custIP,paymentRequest.getCust_nm(),
    // paymentRequest.getCust_addr_1(),paymentRequest.getCust_addr_2(),paymentRequest.getCust_addr_3(),paymentRequest.getCust_postcode(),
    // paymentRequest.getCust_city(),paymentRequest.getCust_state(),paymentRequest.getCust_email(),paymentRequest.getCust_phone(),paymentRequest.getTotal_amt(),
    // paymentRequest.getSs_return_url(),username,username);
    // */
    // mtt_id =
    // bilIssBySSRepository.sp_insertPaymentMTT(bilCustRequest.getI_paymentRequest(),
    // rmsType, custIP, username, username);

    // //loop insert mtt_item
    // if(mtt_id > 0){
    // List<PaymentItemDetails> itemDetailsList =
    // bilCustRequest.getI_paymentRequest().getPayment_item_details();
    // for (PaymentItemDetails item : itemDetailsList) {
    // /*
    // mtt_item_id =
    // opRep.sp_insertPaymentMTTItem(mtt_id,item.getFee_detail_id(),item.getItem_ref_no(),item.getItem_desc(),item.getLine_no(),item.getQty(),item.getUnit_fee(),
    // item.getGross_amt(),item.getGrant_cd(),item.getDisc_amt(),item.getTax_pct(),item.getTax_amt(),item.getNet_amt(),item.getEntity_type(),
    // item.getEntity_no(),item.getEntity_nm(),item.getCp_no(),item.getCp_tier(),item.getCp_tier_amt(),item.getCp_tier_disc_pct(),
    // username,username);
    // */
    // mtt_item_id = bilIssBySSRepository.sp_insertPaymentMTTItem(item, mtt_id,
    // username, username);

    // if(mtt_item_id<1){
    // finalResult="MTT Item table insert failed";
    // //revert back MTT table
    // throw new Exception(finalResult);
    // }
    // }

    // finalResult="insert successful";

    // }else if(mtt_id < 0){

    // finalResult="update successful";

    // }else{
    // finalResult = "MTT table insert failed";
    // mtt_id = 0;
    // throw new Exception(finalResult);
    // }

    // // if(result!=""){
    // // finalResult="insert failed";
    // // }
    // // else{
    // // finalResult="insert successful";
    // // }
    // } catch (NumberFormatException e) {
    // // Handle the exception if feeGrpId is not a valid Long
    // mtt_id = -1;
    // finalResult = e.getMessage();
    // log.error("Exception in " + this.getClass().toString(), e);
    // } catch (Exception e) {
    // // Handle other exceptions here
    // mtt_id = -1;
    // finalResult = e.getMessage();
    // log.error("Exception in " + this.getClass().toString(), e);
    // }

    // return mtt_id;
    // }

    @Override
    public String sp_getbibssrunno() {

        String result = "";

        result = bilIssBySSRepository.sp_getbibssrunno();

        // result = convertToGetBillingItemDetails(objects);

        return result;
    }

    @Override
    public String sp_getandreservebillrunno(BillingIssuanceBySSRunnoRequest runnoRequest) {

        String result = "";

        result = bilIssBySSRepository.sp_getandreservebillrunno(runnoRequest);

        // result = convertToGetBillingItemDetails(objects);

        return result;
    }

    @Override
    public String sp_getbilstatus(BillingIssuanceBySSBilStatusRequest bilStatusRequest) {

        String result = "";

        result = bilIssBySSRepository.sp_getbilstatus(bilStatusRequest);

        // result = convertToGetBillingItemDetails(objects);

        return result;
    }

    @Override
    public List<BillingIssuanceBySSPaymentDetails> sp_getbibsspaymentdetails(
            BillingIssuanceBySSBilStatusRequest bilStatusRequest) {

        List<BillingIssuanceBySSPaymentDetails> result = Collections.emptyList();

        List<Object[]> objects = bilIssBySSRepository.sp_getbibsspaymentdetails(bilStatusRequest);

        result = convertToGetBillingPaymentDetails(objects);

        return result;
    }

    private List<BillingIssuanceBySSPaymentDetails> convertToGetBillingPaymentDetails(List<Object[]> objects) {
        List<BillingIssuanceBySSPaymentDetails> billingPaymentDetailsList = new ArrayList<>();

        for (Object[] obj : objects) {
            BillingIssuanceBySSPaymentDetails billingPaymentDetails = new BillingIssuanceBySSPaymentDetails();

            billingPaymentDetails.setBil_id((Integer) obj[0]);
            billingPaymentDetails.setBilling_no((String) obj[1]);
            billingPaymentDetails.setBilling_desc((String) obj[2]);
            billingPaymentDetails.setBil_wf_status((String) obj[3]);
            billingPaymentDetails.setBilcust_id((Integer) obj[4]);
            billingPaymentDetails.setCust_id((String) obj[5]);
            billingPaymentDetails.setCust_nm((String) obj[6]);
            billingPaymentDetails.setCust_email((String) obj[7]);
            billingPaymentDetails.setCust_phone((String) obj[8]);
            billingPaymentDetails.setCust_addr1((String) obj[9]);
            billingPaymentDetails.setCust_addr2((String) obj[10]);
            billingPaymentDetails.setCust_addr3((String) obj[11]);
            billingPaymentDetails.setCust_postcode((String) obj[12]);
            billingPaymentDetails.setCust_city((String) obj[13]);
            billingPaymentDetails.setCust_state((String) obj[14]);
            billingPaymentDetails.setEnt_nm((String) obj[15]);
            billingPaymentDetails.setEnt_no((String) obj[16]);
            billingPaymentDetails.setEnt_ty((String) obj[17]);

            billingPaymentDetailsList.add(billingPaymentDetails);
        }
        return billingPaymentDetailsList;
    }

    @Override
    public Integer sp_uploadDoc(BillingIssuanceBySBillingDocRequest bilDocRequest)
            throws SerialException, SQLException {
        // Decode Base64 content
        byte[] decodedBytes = decodeBase64(bilDocRequest.getI_file_content());
        Blob blob = new SerialBlob(decodedBytes);

        // Call the repository method
        Integer result = bilIssBySSRepository.sp_uploadDoc(bilDocRequest, blob);
        if (result <= 0 && bilDocRequest.getI_bil_id() > 0) {//
            bilIssBySSRepository.sp_removebilissbyss(bilDocRequest.getI_bil_id());
        }
        return result;
    }

    private byte[] decodeBase64(String base64String) {
        if (base64String.startsWith("data:")) {
            base64String = base64String.substring(base64String.indexOf(',') + 1);
        }
        base64String = base64String.replaceAll("\\s", "").replace(":", "");
        return Base64.getDecoder().decode(base64String);
    }

    @Override
    public List<BillingIssuanceBySSListing> sp_getbibsslisting(
            BillingIssuanceBySSListingRequest billingListingRequest) {

        List<BillingIssuanceBySSListing> result = Collections.emptyList();

        List<Object[]> objects = bilIssBySSRepository.sp_getbibsslisting(billingListingRequest);

        result = convertToGetBillingListing(objects);

        return result;
    }

    private List<BillingIssuanceBySSListing> convertToGetBillingListing(List<Object[]> objects) {
        List<BillingIssuanceBySSListing> billingList = new ArrayList<>();

        for (Object[] obj : objects) {
            BillingIssuanceBySSListing billing = new BillingIssuanceBySSListing();

            billing.setCust_id((String) obj[0]);
            billing.setEnt_nm((String) obj[1]);
            billing.setEnt_no((String) obj[2]);
            billing.setBilling_no((String) obj[3]);
            billing.setBil_id((Integer) obj[4]);
            billing.setAmount((BigDecimal) obj[5]);
            billing.setBilling_method((String) obj[6]);
            billing.setBil_wf_status((String) obj[7]);
            billing.setRcpt_no((String) obj[8]);
            billing.setReq_name((String) obj[9]);
            billing.setBil_child_status((String) obj[10]);
            billing.setTotal((Integer) obj[11]);

            billingList.add(billing);
        }
        return billingList;
    }

    @Override
    public List<BillingIssuanceBySSBillingDetails> sp_getbibssbillingdetails(
            BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest) {

        List<BillingIssuanceBySSBillingDetails> result = Collections.emptyList();

        List<Object[]> objects = bilIssBySSRepository.sp_getbibssbillingdetails(bilDetailsRequest);

        result = convertToGetBillingDetails(objects);

        return result;
    }

    private List<BillingIssuanceBySSBillingDetails> convertToGetBillingDetails(List<Object[]> objects) {
        List<BillingIssuanceBySSBillingDetails> billingDetailsList = new ArrayList<>();

        for (Object[] obj : objects) {
            BillingIssuanceBySSBillingDetails billingDetails = new BillingIssuanceBySSBillingDetails();

            billingDetails.setReq_name((String) obj[0]);
            billingDetails.setReq_email((String) obj[1]);
            billingDetails.setSs_cd((String) obj[2]);
            billingDetails.setBilling_no((String) obj[3]);
            billingDetails.setBilling_desc((String) obj[4]);
            billingDetails.setAction((String) obj[5]);
            billingDetails.setDps_amt((BigDecimal) obj[6]);
            billingDetails.setBilling_cnt((Integer) obj[7]);
            billingDetails.setBilling_freq((String) obj[8]);
            billingDetails.setLoa_id((String) obj[9]);
            billingDetails.setDt_loa_start((Date) obj[10]);
            billingDetails.setDt_loa_end((Date) obj[11]);
            billingDetails.setAgm_id((String) obj[12]);
            billingDetails.setDt_agm_start((Date) obj[13]);
            billingDetails.setDt_agm_end((Date) obj[14]);
            billingDetails.setBil_wf_status((String) obj[15]);
            billingDetails.setPickup_by((String) obj[16]);
            billingDetails.setDt_pick((Date) obj[17]);
            billingDetails.setCust_id((String) obj[18]);
            billingDetails.setCust_nm((String) obj[19]);
            billingDetails.setCust_email((String) obj[20]);
            billingDetails.setCust_phone((String) obj[21]);
            billingDetails.setCust_addr1((String) obj[22]);
            billingDetails.setCust_addr2((String) obj[23]);
            billingDetails.setCust_addr3((String) obj[24]);
            billingDetails.setCust_postcode((String) obj[25]);
            billingDetails.setCust_city((String) obj[26]);
            billingDetails.setCust_state((String) obj[27]);
            billingDetails.setEnt_nm((String) obj[28]);
            billingDetails.setEnt_no((String) obj[29]);
            billingDetails.setEnt_ty((String) obj[30]);

            billingDetailsList.add(billingDetails);
        }
        return billingDetailsList;
    }

    @Override
    public List<BillingIssuanceBySSListofBilItems> sp_getbibsslistofbillingitems(
            BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest) {

        List<BillingIssuanceBySSListofBilItems> result = Collections.emptyList();

        List<Object[]> objects = bilIssBySSRepository.sp_getbibsslistofbillingitems(bilDetailsRequest);

        result = convertToGetlistOfBilItems(objects);

        return result;
    }

    private List<BillingIssuanceBySSListofBilItems> convertToGetlistOfBilItems(List<Object[]> objects) {
        List<BillingIssuanceBySSListofBilItems> billingListofItemsList = new ArrayList<>();

        for (Object[] obj : objects) {
            BillingIssuanceBySSListofBilItems billingListofItems = new BillingIssuanceBySSListofBilItems();

            billingListofItems.setMft_pk((Integer) obj[0]);
            billingListofItems.setUnit_fee((BigDecimal) obj[1]);
            billingListofItems.setQty((Integer) obj[2]);
            billingListofItems.setTax_pct((BigDecimal) obj[3]);
            billingListofItems.setTax_amt((BigDecimal) obj[4]);
            billingListofItems.setFinal_amt((BigDecimal) obj[5]);
            billingListofItems.setFee_detail_id((String) obj[6]);
            billingListofItems.setFee_detail_nm_e((String) obj[7]);
            billingListofItems.setTotal((Integer) obj[8]);

            billingListofItemsList.add(billingListofItems);
        }
        return billingListofItemsList;
    }

    @Override
    public List<BillingIssuanceBySSListOfIssuance> sp_getbibsslistofbillingissuance(
            BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest) {

        List<BillingIssuanceBySSListOfIssuance> result = Collections.emptyList();

        List<Object[]> objects = bilIssBySSRepository.sp_getbibsslistofbillingissuance(bilDetailsRequest);

        result = convertToGetlistOfBilIss(objects);

        return result;
    }

    private List<BillingIssuanceBySSListOfIssuance> convertToGetlistOfBilIss(List<Object[]> objects) {
        List<BillingIssuanceBySSListOfIssuance> billingIssuanceList = new ArrayList<>();

        for (Object[] obj : objects) {
            BillingIssuanceBySSListOfIssuance billingIssuance = new BillingIssuanceBySSListOfIssuance();

            billingIssuance.setBil_child_date((Date) obj[0]);
            billingIssuance.setBil_child_status((String) obj[1]);
            billingIssuance.setBil_wf_id((Integer) obj[2]);
            billingIssuance.setBil_no((String) obj[3]);
            billingIssuance.setBil_status((String) obj[4]);
            billingIssuance.setTotal((Integer) obj[5]);

            billingIssuanceList.add(billingIssuance);
        }
        return billingIssuanceList;
    }

    @Override
    public List<BillingIssuanceBySBillingDoc> sp_getbibsslistofdoc(
            BillingIssuanceBySSBillingDetailsRequest bilRequest) {

        List<BillingIssuanceBySBillingDoc> result = Collections.emptyList();

        List<Object[]> objects = bilIssBySSRepository.sp_getbibsslistofdoc(bilRequest);

        result = convertToGetBillingIssuanceDoc(objects);

        return result;
    }

    private List<BillingIssuanceBySBillingDoc> convertToGetBillingIssuanceDoc(List<Object[]> objects) {
        List<BillingIssuanceBySBillingDoc> bilDocList = new ArrayList<>();

        for (Object[] obj : objects) {
            BillingIssuanceBySBillingDoc bilDoc = new BillingIssuanceBySBillingDoc();

            bilDoc.setBil_wf_id((Integer) obj[0]);
            bilDoc.setBil_id((Integer) obj[1]);
            bilDoc.setFile_nm((String) obj[2]);
            bilDoc.setFile_type((String) obj[3]);
            bilDoc.setFile_size((Integer) obj[4]);
            bilDoc.setFile_category((String) obj[5]);
            bilDoc.setDt_created((Date) obj[6]);
            bilDoc.setDt_modified((Date) obj[7]);
            bilDoc.setCreated_by((String) obj[8]);
            bilDoc.setModified_by((String) obj[9]);
            bilDoc.setTotal((Integer) obj[10]);

            bilDocList.add(bilDoc);
        }
        return bilDocList;
    }

    @Override
    public String sp_getbibssdocfilecontent(BillingIssuanceBySSBillingDetailsRequest bilRequest) throws SQLException {

        String result = "";

        Blob blob = (Blob) bilIssBySSRepository.sp_getbibssdocfilecontent(bilRequest);

        // Convert Blob to byte array
        byte[] bytes = blob.getBytes(1, (int) blob.length());

        // Convert byte array to Base64-encoded string
        String base64Content = Base64.getEncoder().encodeToString(bytes);
        result = base64Content;

        return result;
    }

    @Override
    public List<BillingIssuanceBySSHistory> sp_getbibsshistory(
            BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest) {

        List<BillingIssuanceBySSHistory> result = Collections.emptyList();

        List<Object[]> objects = bilIssBySSRepository.sp_getbibsshistory(bilDetailsRequest);

        result = convertToGetBilHist(objects);

        return result;
    }

    private List<BillingIssuanceBySSHistory> convertToGetBilHist(List<Object[]> objects) {
        List<BillingIssuanceBySSHistory> billingHistList = new ArrayList<>();

        for (Object[] obj : objects) {
            BillingIssuanceBySSHistory billingHist = new BillingIssuanceBySSHistory();

            billingHist.setAction((String) obj[0]);
            billingHist.setBil_wf_status((String) obj[1]);
            billingHist.setPickup_by((String) obj[2]);
            billingHist.setDt_pick((Date) obj[3]);
            billingHist.setDt_created((Date) obj[4]);
            billingHist.setDt_modified((Date) obj[5]);
            billingHist.setCreated_by((String) obj[6]);
            billingHist.setModified_by((String) obj[7]);
            billingHist.setMsg((String) obj[8]);
            billingHist.setTotal((Integer) obj[9]);

            billingHistList.add(billingHist);
        }
        return billingHistList;
    }

    public Integer sp_confirmnewbill(String billingNo, String username) {
        /*
         * //FMS API (AR Invoice API) goes here
         * return 0; // if AR Invoice API fail
         */

        // If AR Invoice API Success:
        // Integer statusCode = bilIssBySSRepository.sp_confirmnewbill(billingNo,
        // username);
        Integer statusCode = bilIssBySSRepository.sp_confirmnewbill(billingNo, username);
        if (statusCode < 1) {
            RMSLogger.error("Exception in " + this.getClass().toString() + " (" + statusCode.toString() + ") "
                    + "confirmBill func - sp_confirmnewbill failed!");
            return -1;
            // return APIResponse.InternalServerErrorExternal();
        }

        OnlinePayment mtt = mttSvc.getMttFromOrderNo(billingNo).orElse(null);
        if (mtt == null) {
            RMSLogger.error("Exception in " + this.getClass().toString()
                    + "confirmBill func - getMttFromOrderNo failed!");
            return -2;
            // return APIResponse.InternalServerErrorExternal();
        }

        BillingIssuanceBySSBillingMethod billingMethod = sp_getbibssbillingmethod(billingNo);
        // String billingMethod = sp_getbibssbillingmethod(billingNo);

        if (billingMethod == null) {
            RMSLogger.error("Exception in " + this.getClass().toString() +
                    "getBillingMethod func - sp_getbibssbillingmethod failed!");
            return -3;
        }

        String redirect = onlinePortalUrl + "/payment-page?pr=" + billingNo;
        String body = "";
        String subject = "";

        subject = "Payment Notification for Billing";

        body = "Entity Name: " + billingMethod.getEnt_nm()
                + "<br>Billing No.: " + billingMethod.getBil_no();

        if (!billingMethod.getBilling_mthd().equals("O")) {
            body += "<br>Billing Period: "
                    + new SimpleDateFormat("MMMM yyyy").format(billingMethod.getBil_child_date());
        }

        body += "<br>Billing Amount: RM" + billingMethod.getSum_amt()
                + "<br>Order Summary: " + billingMethod.getBt_desc()
                + "<br>Billing Description: " + billingMethod.getBilling_desc()
                + "<br><br>Dear Sir/Madam,<br>Tuan/Puan,"
                + "<br><br>We wish to inform you that a billing payment is pending in our system. Please review and complete "
                + "the transactions using the payment link provided below within 30 days from this e-mail issuance. "
                + "<br>Kami ingin memaklumkan bahawa terdapat pembayaran bil yang masih tertunggak dalam sistem kami. "
                + "Kami memohon Tuan/Puan untuk melakukan semakan dan menyelesaikan transaksi "
                + "<br>menggunakan pautan pembayaran yang disediakan di bawah dalam tempoh 30 hari daripada tarikh e-mel dikeluarkan."
                + "<br><br><a href='" + redirect + "'>CLICK HERE</a> to proceed with the payment."
                + "<br><a href='" + redirect + "'>KLIK DI SINI</a> untuk tujuan pemprosesan pembayaran."
                + "<br><br>For further information or to access other services, you may also visit our RMS Public Portal: "
                + "<br><a href='" + publicPortalUrl + "'>RMS Public Portal link.</a>"
                + "<br>Untuk maklumat lanjut atau untuk mengakses perkhidmatan lain, anda juga boleh melayari Portal "
                + "<br>Awam RMS kami: <a href='" + publicPortalUrl + "'>Pautan Portal Awam RMS.</a>"
                + "<br><br>**PLEASE IGNORE THIS EMAIL IF YOUR PAYMENT HAS ALREADY BEEN PROCESSED*** ***MOHON "
                + "<br>ABAIKAN EMAIL INI SEKIRANYA PEMBAYARAN TELAH DILAKUKAN***"
                + "<br><br>Thank you for using our services."
                + "<br>Terima kasih kerana menggunakan perkhidmatan kami."
                + "<br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
                + "DO NOT REPLY DIRECTLY TO THIS EMAIL] \n";

        emailSvc.saveEmailDets(new Email("Notification", mtt.getCust_email(), "", "",
                "PENDING PAYMENT", body, billingNo, null));

        return 1;
    }

    public BillingIssuanceBySSBillingMethod sp_getbibssbillingmethod(String billingNo) {
        Object[] objects = bilIssBySSRepository.sp_getbibssbillingmethod(billingNo);

        if (objects != null) {
            return convertToGetBilMethod(objects);
        }
        return null; // Return null if no data is found
    }

    private BillingIssuanceBySSBillingMethod convertToGetBilMethod(Object[] objects) {
        BillingIssuanceBySSBillingMethod billingMethod = new BillingIssuanceBySSBillingMethod();

        // Assuming objects contains a single row of data
        if (objects != null && objects.length > 0) {
            billingMethod.setBilling_mthd((String) objects[0]);
            billingMethod.setBil_no((String) objects[1]);
            billingMethod.setEnt_nm((String) objects[2]);
            billingMethod.setBil_child_date((Date) objects[3]);
            billingMethod.setSum_amt((BigDecimal) objects[4]);
            billingMethod.setBt_desc((String) objects[5]);
            billingMethod.setBilling_desc((String) objects[6]);

        }

        return billingMethod;
    }

    // @Override
    public List<SubmitBillingCust> sp_callbacksubmitbilling(SubmitBillingRequest submitBillingRequest) {

        List<SubmitBillingCust> result = Collections.emptyList();

        List<Object[]> objects = bilIssBySSRepository.sp_callbacksubmitbilling(submitBillingRequest);

        result = convertToCallbackSubmitBil(objects);

        return result;
    }

    private List<SubmitBillingCust> convertToCallbackSubmitBil(List<Object[]> objects) {
        Map<String, SubmitBillingCust> billingCustMap = new HashMap<>();
        Map<String, SubmitBillingChild> billingChildMap = new HashMap<>();

        for (Object[] obj : objects) {
            // Parse SubmitBillingCust level fields
            String custId = (String) obj[0];
            String custNm = (String) obj[1];
            String custEmail = (String) obj[2];
            String custPhone = (String) obj[3];
            String custAddr1 = (String) obj[4];
            String custAddr2 = (String) obj[5];
            String custAddr3 = (String) obj[6];
            String custPostcode = (String) obj[7];
            String custCity = (String) obj[8];
            String custState = (String) obj[9];
            String custStateNm = (String) obj[10];
            String entNm = (String) obj[11];
            String entNo = (String) obj[12];
            String entTy = (String) obj[13];
            String entTyNm = (String) obj[14];
            String reqName = (String) obj[15];
            String reqEmail = (String) obj[16];
            String ssCd = (String) obj[17];
            String ssCdNm = (String) obj[18];
            String btTy = (String) obj[19];
            String btTyNm = (String) obj[20];
            String billingDesc = (String) obj[21];
            Date dtStart = (Date) obj[22];
            Date dtEnd = (Date) obj[23];
            String msg = (String) obj[24];
            // Parse SubmitBillingChild level fields
            String bilNo = (String) obj[25];
            Date bilChildDate = (Date) obj[26];
            String bilStatus = (String) obj[27];
            String bilStatusNm = (String) obj[28];

            // Parse SubmitBillingItem level fields
            String itemDesc = (String) obj[29];
            BigDecimal unitFee = (BigDecimal) obj[30];
            Integer qty = (Integer) obj[31];
            BigDecimal finalAmt = (BigDecimal) obj[32];

            // Create or update SubmitBillingCust
            SubmitBillingCust billingCust = billingCustMap.computeIfAbsent(
                    custId,
                    k -> {
                        SubmitBillingCust newCust = new SubmitBillingCust();
                        newCust.setCust_id(custId);
                        newCust.setCust_nm(custNm);
                        newCust.setCust_email(custEmail);
                        newCust.setCust_phone(custPhone);
                        newCust.setCust_addr1(custAddr1);
                        newCust.setCust_addr2(custAddr2);
                        newCust.setCust_addr3(custAddr3);
                        newCust.setCust_postcode(custPostcode);
                        newCust.setCust_city(custCity);
                        newCust.setCust_state(custState);
                        newCust.setCust_state_nm(custStateNm);
                        newCust.setEnt_nm(entNm);
                        newCust.setEnt_no(entNo);
                        newCust.setEnt_ty(entTy);
                        newCust.setEnt_ty_nm(entTyNm);
                        newCust.setReq_name(reqName);
                        newCust.setReq_email(reqEmail);
                        newCust.setSs_cd(ssCd);
                        newCust.setSs_cd_nm(ssCdNm);
                        newCust.setBt_ty(btTy);
                        newCust.setBt_ty_nm(btTyNm);
                        newCust.setBilling_desc(billingDesc);
                        newCust.setDt_start(dtStart);
                        newCust.setDt_end(dtEnd);
                        newCust.setMsg(msg);
                        newCust.setBil_list(new ArrayList<>());
                        return newCust;
                    });

            // Create or update SubmitBillingChild
            String childKey = custId + "_" + bilNo; // Unique key for each child
            SubmitBillingChild billingChild = billingChildMap.computeIfAbsent(
                    childKey,
                    k -> {
                        SubmitBillingChild newChild = new SubmitBillingChild();
                        newChild.setBil_no(bilNo);
                        newChild.setBil_child_date(bilChildDate);
                        newChild.setBil_status(bilStatus);
                        newChild.setBil_status_nm(bilStatusNm);
                        newChild.setBil_item(new ArrayList<>());
                        billingCust.getBil_list().add(newChild);
                        return newChild;
                    });

            // Add SubmitBillingItem
            SubmitBillingItem billingItem = new SubmitBillingItem();
            billingItem.setItem_desc(itemDesc);
            billingItem.setUnit_fee(unitFee);
            billingItem.setQty(qty);
            billingItem.setFinal_amt(finalAmt);
            billingChild.getBil_item().add(billingItem);
        }

        return new ArrayList<>(billingCustMap.values());
    }

    // //@Override
    // public List<BillingIssuanceBySSBilItemDets>
    // sp_getbibssbillingitemdetails(BillingIssuanceBySSBilItemDetsRequest
    // billingItemDetsRequest) {

    // List<BillingIssuanceBySSBilItemDets> result = Collections.emptyList();

    // List<Object[]> objects =
    // bilIssBySSRepository.sp_getbibssbillingitemdetails(billingItemDetsRequest);

    // result = convertToGetBillingIssuanceBySSBilItemDets(objects);

    // return result;
    // }

    // private List<BillingIssuanceBySSBilItemDets>
    // convertToGetBillingIssuanceBySSBilItemDets(List<Object[]> objects) {
    // List<BillingIssuanceBySSBilItemDets> billingIssuanceBySSBilItemDetsList = new
    // ArrayList<>();

    // for (Object[] obj : objects) {
    // BillingIssuanceBySSBilItemDets billingIssuanceBySSBilItemDets = new
    // BillingIssuanceBySSBilItemDets();

    // billingIssuanceBySSBilItemDets.setBltc_item_id((Integer) obj[0]);
    // billingIssuanceBySSBilItemDets.setMft_pk((Integer) obj[1]);
    // billingIssuanceBySSBilItemDets.setMft_id((String) obj[2]);
    // billingIssuanceBySSBilItemDets.setDps_mft_pk((Integer) obj[3]);
    // billingIssuanceBySSBilItemDets.setDps_mft_id((String) obj[4]);
    // billingIssuanceBySSBilItemDets.setFee_detail_nm_e((String) obj[5]);
    // billingIssuanceBySSBilItemDets.setTax_cd_id((Integer) obj[6]);
    // billingIssuanceBySSBilItemDets.setTax_pct((BigDecimal) obj[7]);
    // billingIssuanceBySSBilItemDets.setTotal((Integer) obj[8]);

    // billingIssuanceBySSBilItemDetsList.add(billingIssuanceBySSBilItemDets);
    // }
    // return billingIssuanceBySSBilItemDetsList;
    // }

}
