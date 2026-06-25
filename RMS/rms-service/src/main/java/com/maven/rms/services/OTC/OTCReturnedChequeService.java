package com.maven.rms.services.OTC;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maven.rms.models.MTTEmailExpiry;
import com.maven.rms.models.OTC.NBLDocInsRequest;
import com.maven.rms.models.OTC.NBLInsRequest;
import com.maven.rms.models.OTC.NBLItem;
import com.maven.rms.models.OTC.NBLItemInsRequest;
import com.maven.rms.models.OTC.NBLItemRequest;
import com.maven.rms.models.OTC.NBLTC;
import com.maven.rms.models.OTC.NonBilHist;
import com.maven.rms.models.OTC.NonBilResult;
import com.maven.rms.models.OTC.NonBillDoc;
import com.maven.rms.models.OTC.NonBillingItems;
import com.maven.rms.models.OTC.NonBillingListing;
import com.maven.rms.models.OTC.NonBillingListingRequest;
import com.maven.rms.models.OTC.OTCReturnedCheque;
import com.maven.rms.models.OTC.OTCReturnedChequeRequest;
import com.maven.rms.repositories.OTC.OTCReturnedChequeRepository;
// import com.maven.rms.services.AuthService;
import com.maven.rms.models.NonBillRCEmail;
import com.maven.rms.models.ServiceProviderRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OTCReturnedChequeService implements IOTCReturnedChequeServiceInterface {

    private final OTCReturnedChequeRepository otcReturnedChequeRepository;

    // @Autowired
    // private AuthService authService;

    public OTCReturnedChequeService(OTCReturnedChequeRepository otcReturnedChequeRepository) {
        this.otcReturnedChequeRepository = otcReturnedChequeRepository;
    }

    @Override
    public List<OTCReturnedCheque> sp_getchequeinfo(OTCReturnedChequeRequest otcReturnedChequeRequest) {
        List<OTCReturnedCheque> result = Collections.emptyList();
        List<Object[]> objects = otcReturnedChequeRepository.sp_getchequeinfo(otcReturnedChequeRequest);
        result = convertOTCReturnedChequeList(objects);
        return result;
    }

    private List<OTCReturnedCheque> convertOTCReturnedChequeList(List<Object[]> objects) {
        List<OTCReturnedCheque> otcReturnedCheques = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCReturnedCheque otcReturnedCheque = new OTCReturnedCheque();
            otcReturnedCheque.setMtt_id((Integer) obj[0]);
            otcReturnedCheque.setOtc_id((Integer) obj[1]);
            otcReturnedCheque.setChe_amt((BigDecimal) obj[2]);
            otcReturnedCheque.setChe_date((Date) obj[3]);
            otcReturnedCheque.setChe_bank_nm((String) obj[4]);
            otcReturnedCheque.setChe_payer_nm((String) obj[5]);
            otcReturnedCheque.setChe_no((String) obj[6]);
            otcReturnedCheque.setChe_status((String) obj[7]);
            otcReturnedCheque.setChe_ba_acct_no((String) obj[8]);
            otcReturnedCheque.setChe_id((String) obj[9]);
            otcReturnedCheque.setCounter_id((String) obj[10]);
            otcReturnedCheque.setBranch_cd((String) obj[11]);
            otcReturnedCheque.setRcpt_no((String) obj[12]);
            otcReturnedCheque.setOrn_no((String) obj[13]);
            otcReturnedCheque.setColl_slip_no((String) obj[14]);
            otcReturnedCheque.setSs_cd((String) obj[15]);
            otcReturnedCheque.setCust_nm((String) obj[16]);
            otcReturnedCheque.setCust_phone((String) obj[17]);
            otcReturnedCheque.setCust_email((String) obj[18]);
            otcReturnedCheque.setCust_addr1((String) obj[19]);
            otcReturnedCheque.setCust_addr2((String) obj[20]);
            otcReturnedCheque.setCust_addr3((String) obj[21]);
            otcReturnedCheque.setCust_postcode((String) obj[22]);
            otcReturnedCheque.setCust_city((String) obj[23]);
            otcReturnedCheque.setCust_state((String) obj[24]);
            otcReturnedCheque.setTotal_amt((BigDecimal) obj[25]);
            otcReturnedCheque.setOrder_status((String) obj[26]);
            otcReturnedCheque.setTotal((Integer) obj[27]);
            otcReturnedCheques.add(otcReturnedCheque);
        }
        return otcReturnedCheques;
    }

    @Override
    public List<NBLTC> sp_getnbltc() {
        List<NBLTC> result = Collections.emptyList();
        try {

            List<Object[]> objects = otcReturnedChequeRepository.sp_getnbltc();
            result = convertToGetNBLTC(objects);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }

    private List<NBLTC> convertToGetNBLTC(List<Object[]> objects) {
        List<NBLTC> nbltcs = new ArrayList<>();

        for (Object[] obj : objects) {
            // Create a new sourceSystemCode instance using the extracted values
            NBLTC nbltc = new NBLTC();
            nbltc.setBt_cd((String) obj[0]);
            nbltc.setBt_desc((String) obj[1]);
            nbltc.setClass_id((String) obj[2]);
            nbltcs.add(nbltc);
        }
        return nbltcs;
    }

    @Override
    public List<NBLItem> sp_getnblitem(NBLItemRequest nblItemRequest) {
        List<NBLItem> result = Collections.emptyList();
        try {

            List<Object[]> objects = otcReturnedChequeRepository.sp_getnblitem(nblItemRequest);
            result = convertToGetNBLTCItem(objects);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }

    private List<NBLItem> convertToGetNBLTCItem(List<Object[]> objects) {
        List<NBLItem> nbltcs = new ArrayList<>();

        for (Object[] obj : objects) {
            // Create a new sourceSystemCode instance using the extracted values
            NBLItem nbltcItem = new NBLItem();
            nbltcItem.setBt_cd((String) obj[0]);
            nbltcItem.setBt_ty((String) obj[1]);
            nbltcItem.setBt_desc((String) obj[2]);
            nbltcItem.setClass_id((String) obj[3]);
            nbltcItem.setSs_cd((String) obj[4]);
            nbltcItem.setMft_pk((Integer) obj[5]);
            nbltcItem.setMft_id((String) obj[6]);
            nbltcItem.setDps_mft_pk((Integer) obj[7]);
            nbltcItem.setDps_mft_id((String) obj[8]);
            nbltcItem.setFee_detail_pk((Integer) obj[9]);
            nbltcItem.setFee_detail_id((String) obj[10]);
            nbltcItem.setFee_detail_nm_e((String) obj[11]);
            nbltcItem.setFee_detail_nm_b((String) obj[12]);
            nbltcItem.setUnit_fee((BigDecimal) obj[13]);
            nbltcItem.setTax_pct((BigDecimal) obj[14]);
            nbltcs.add(nbltcItem);
        }
        return nbltcs;
    }

    @Override
    public String sp_getnbrunno() {
        String result = "";
        try {

            result = otcReturnedChequeRepository.sp_getnbrunno();

        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }

    // @Override
    // public List<Integer> sp_insnonbill(NBLInsRequest insertRequest) {
    //     List<Integer> resultList = new ArrayList<>();
    //     resultList = otcReturnedChequeRepository.sp_insnonbill(insertRequest);
    //     return resultList;
    // }

    @Override
    public List<NonBilResult> sp_insnonbill(NBLInsRequest insertRequest) {
        List<NonBilResult> resultList = new ArrayList<>();
        resultList = otcReturnedChequeRepository.sp_insnonbill(insertRequest);
        return resultList;
    }

    @Override
    public Integer sp_insnonbillitem(List<NBLItemInsRequest> insertRequest) {
        Integer result = 0;
        result = otcReturnedChequeRepository.sp_insnonbillitem(insertRequest);
        return result;
    }

    // @Override
    // public Integer sp_insnonbilldoc(List<NBLDocInsRequest> insertRequest) {
    // Integer result = 0;
    // result = otcReturnedChequeRepository.sp_insnonbilldoc(insertRequest);
    // return result;
    // }

    @Override
    public Integer sp_insnonbilldoc(NBLDocInsRequest insertRequest) throws SerialException, SQLException {
        // Decode Base64 content
        byte[] decodedBytes = decodeBase64(insertRequest.getI_file_content());
        Blob blob = new SerialBlob(decodedBytes);

        // Call the repository method
        Integer result = otcReturnedChequeRepository.sp_insnonbilldoc(insertRequest, blob);
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
    public List<NonBillingListing> sp_getnonbilllisting(NonBillingListingRequest req) {
        List<NonBillingListing> result = Collections.emptyList();
        try {

            List<Object[]> objects = otcReturnedChequeRepository.sp_getnonbilllisting(req);
            result = convertToGetNBListing(objects);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }

    private List<NonBillingListing> convertToGetNBListing(List<Object[]> objects) {
        List<NonBillingListing> nbltcs = new ArrayList<>();

        for (Object[] obj : objects) {
            // Create a new sourceSystemCode instance using the extracted values
            NonBillingListing nbltcItem = new NonBillingListing();
            nbltcItem.setNon_bil_id((Integer) obj[0]);
            nbltcItem.setReq_name((String) obj[1]);
            nbltcItem.setReq_email((String) obj[2]);
            nbltcItem.setNon_bil_no((String) obj[3]);
            nbltcItem.setNon_bil_desc((String) obj[4]);
            nbltcItem.setRet_che_no((String) obj[5]);
            nbltcItem.setTotal_bil_amt((BigDecimal) obj[6]);
            nbltcItem.setRemark((String) obj[7]);
            nbltcItem.setBil_status((String) obj[8]);
            nbltcItem.setFms_admin_email((String) obj[9]);
            nbltcItem.setFms_admin_nm((String) obj[10]);
            nbltcItem.setDt_created((Date) obj[11]);
            nbltcItem.setDt_modified((Date) obj[12]);
            nbltcItem.setCreated_by((String) obj[13]);
            nbltcItem.setModified_by((String) obj[14]);
            nbltcItem.setStatus((String) obj[15]);
            nbltcItem.setBil_action((String) obj[16]);
            nbltcItem.setDt_action((Date) obj[17]);
            nbltcItem.setPerformed_by((String) obj[18]);
            nbltcItem.setOtc_counter_id((Integer) obj[19]);
            nbltcItem.setOtc_body_id((Integer) obj[20]);
            nbltcItem.setNon_bilcust_id((Integer) obj[21]);
            nbltcItem.setCust_id((String) obj[22]);
            nbltcItem.setCust_nm((String) obj[23]);
            nbltcItem.setCust_email((String) obj[24]);
            nbltcItem.setCust_phone((String) obj[25]);
            nbltcItem.setCust_addr_1((String) obj[26]);
            nbltcItem.setCust_addr_2((String) obj[27]);
            nbltcItem.setCust_addr_3((String) obj[28]);
            nbltcItem.setEnt_nm((String) obj[29]);
            nbltcItem.setEnt_no((String) obj[30]);
            nbltcItem.setEnt_ty((String) obj[31]);
            nbltcItem.setCust_postcode((String) obj[32]);
            nbltcItem.setCust_city((String) obj[33]);
            nbltcItem.setCust_state((String) obj[34]);
            nbltcItem.setChe_no((String) obj[35]);
            nbltcItem.setChe_id((String) obj[36]);
            nbltcItem.setTotal((Integer) obj[37]);
            nbltcs.add(nbltcItem);
        }
        return nbltcs;
    }

    @Override
    public List<NonBillingItems> sp_getnonbillitem(NonBillingListingRequest req) {
        List<NonBillingItems> result = Collections.emptyList();
        try {

            List<Object[]> objects = otcReturnedChequeRepository.sp_getnonbillitem(req);
            result = convertNonBillItems(objects);

        } catch (Exception e) {
            e.printStackTrace();

        }
        return result;
    }

    private List<NonBillingItems> convertNonBillItems(List<Object[]> objects) {
        List<NonBillingItems> nbltcs = new ArrayList<>();

        for (Object[] obj : objects) {
            // Create a new sourceSystemCode instance using the extracted values
            NonBillingItems nbltcItem = new NonBillingItems();
            nbltcItem.setNon_bilitem_id((Integer) obj[0]);
            nbltcItem.setNon_bil_id((Integer) obj[1]);
            nbltcItem.setMft_pk((Integer) obj[2]);
            nbltcItem.setFee_detail_nm_e((String) obj[3]);
            nbltcItem.setUnit_fee((BigDecimal) obj[4]);
            nbltcItem.setQty((Integer) obj[5]);
            nbltcItem.setTax_pct((BigDecimal) obj[6]);
            nbltcItem.setTax_amt((BigDecimal) obj[7]);
            nbltcItem.setItem_total_amt((BigDecimal) obj[8]);
            nbltcItem.setItem_ref_no((String) obj[9]);
            nbltcItem.setTotal((Integer) obj[10]);

            nbltcs.add(nbltcItem);
        }
        return nbltcs;
    }

    @Override
    public List<NonBillDoc> sp_getnonbildoc(NonBillingListingRequest req) {
        List<NonBillDoc> result = Collections.emptyList();
        try {
            List<Object[]> objects = otcReturnedChequeRepository.sp_getnonbildoc(req);
            result = convertToGetBillDoc(objects);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<NonBillDoc> convertToGetBillDoc(List<Object[]> objects) {
        List<NonBillDoc> billDocList = new ArrayList<>();

        for (Object[] obj : objects) {
            NonBillDoc billDocWithoutFile = new NonBillDoc();
            billDocWithoutFile.setNon_bil_doc_id((Integer) obj[0]);
            billDocWithoutFile.setNon_bil_id((Integer) obj[1]);
            billDocWithoutFile.setFile_nm((String) obj[2]);
            billDocWithoutFile.setFile_type((String) obj[3]);
            billDocWithoutFile.setFile_size((Integer) obj[4]);
            billDocWithoutFile.setFile_category((String) obj[5]);
            billDocWithoutFile.setDt_created((Date) obj[6]);
            billDocWithoutFile.setDt_modified((Date) obj[7]);
            billDocWithoutFile.setCreated_by((String) obj[8]);
            billDocWithoutFile.setModified_by((String) obj[9]);
            billDocWithoutFile.setTotal((Integer) obj[10]);
            billDocList.add(billDocWithoutFile);
        }
        return billDocList;
    }

    @Override
    public String sp_getnonbildoccontent(NonBillingListingRequest req) {

        String result = "";

        try {
            Blob blob = (Blob) otcReturnedChequeRepository.sp_getnonbildoccontent(req);
            try {
                // Convert Blob to byte array
                byte[] bytes = blob.getBytes(1, (int) blob.length());

                // Convert byte array to Base64-encoded string
                String base64Content = Base64.getEncoder().encodeToString(bytes);
                result = base64Content;

            } catch (SQLException e) {
                e.printStackTrace();
                result = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<NonBilHist> sp_getnonbilhist(NonBillingListingRequest req) {
        List<NonBilHist> result = Collections.emptyList();
        try {
            List<Object[]> objects = otcReturnedChequeRepository.sp_getnonbilhist(req);
            result = convertToGetBillHist(objects);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<NonBilHist> convertToGetBillHist(List<Object[]> objects) {
        List<NonBilHist> bilHists = new ArrayList<>();

        for (Object[] obj : objects) {
            NonBilHist bitHist = new NonBilHist();
            bitHist.setNon_bil_a_id((Integer) obj[0]);
            bitHist.setNon_bil_id((Integer) obj[1]);
            bitHist.setReq_name((String) obj[2]);
            bitHist.setReq_email((String) obj[3]);
            bitHist.setNon_bil_no((String) obj[4]);
            bitHist.setNon_bil_desc((String) obj[5]);
            bitHist.setRet_che_no((String) obj[6]);
            bitHist.setTotal_bil_amt((BigDecimal) obj[7]);
            bitHist.setRemark((String) obj[8]);
            bitHist.setBil_status((String) obj[9]);
            bitHist.setFms_admin_email((String) obj[10]);
            bitHist.setFms_admin_nm((String) obj[11]);
            bitHist.setDt_created(((Timestamp) obj[12]).toLocalDateTime());
            bitHist.setDt_modified(((Timestamp) obj[13]).toLocalDateTime());
            bitHist.setCreated_by((String) obj[14]);
            bitHist.setModified_by((String) obj[15]);
            bitHist.setStatus((String) obj[16]);
            bitHist.setBil_action((String) obj[17]);
            bitHist.setPerformed_by((String) obj[18]);
            bitHist.setOtc_counter_id((Integer) obj[19]);
            bitHist.setOtc_body_id((Integer) obj[20]);
            bitHist.setTotal((Integer) obj[21]);

            bilHists.add(bitHist);
        }
        return bilHists;
    }

    // scheduler
    @Override
    public List<NonBillRCEmail> sp_getnonbillreturnche(OTCReturnedChequeRequest getRequest) {
        List<NonBillRCEmail> result = Collections.emptyList();

        List<Object[]> objects = otcReturnedChequeRepository.sp_getnonbillreturnche(getRequest);
        result = convertNonBillReturnCheList(objects);

        return result;
    }

    private List<NonBillRCEmail> convertNonBillReturnCheList(List<Object[]> objects) {
        List<NonBillRCEmail> nonBillRCEmailList = new ArrayList<>();

        for (Object[] obj : objects) {
            NonBillRCEmail nonBillRCEmail = new NonBillRCEmail();

            nonBillRCEmail.setNon_bil_id((String) obj[0]);
            nonBillRCEmail.setMtt_id((String) obj[1]);
            nonBillRCEmail.setOrn_no((String) obj[2]);
            nonBillRCEmail.setPayer_email((String) obj[3]);
            nonBillRCEmail.setFms_admin_email((String) obj[4]);
            nonBillRCEmail.setReq_name((String) obj[5]);
            nonBillRCEmail.setNon_bil_no((String) obj[6]);
            nonBillRCEmail.setTotal_bil_amt((BigDecimal) obj[7]);
            nonBillRCEmail.setRet_che_no((String) obj[8]);
            nonBillRCEmail.setReason((String) obj[9]);
            nonBillRCEmail.setOrder_status((String) obj[10]);

            nonBillRCEmailList.add(nonBillRCEmail);
        }

        return nonBillRCEmailList;
    }

    @Override
    public List<MTTEmailExpiry> sp_getmttemaildtexpiry(OTCReturnedChequeRequest getRequest) {
        List<MTTEmailExpiry> result = Collections.emptyList();

        // Assuming the repository returns List<Object> instead of List<Object[]>
        List<Object> objects = otcReturnedChequeRepository.sp_getmttemaildtexpiry(getRequest);
        result = convertMTTEmailExpiryList(objects);

        return result;
    }

    private List<MTTEmailExpiry> convertMTTEmailExpiryList(List<Object> objects) {
        List<MTTEmailExpiry> mttEmailExpiryList = new ArrayList<>();

        for (Object obj : objects) {
            MTTEmailExpiry mttEmailExpiry = new MTTEmailExpiry();

            // Cast the object to the correct type, e.g., Timestamp
            if (obj instanceof java.sql.Timestamp) {
                mttEmailExpiry.setDt_email_expiry(new Date(((java.sql.Timestamp) obj).getTime()));
            }

            mttEmailExpiryList.add(mttEmailExpiry);
        }

        return mttEmailExpiryList;
    }

    @Override
    public Integer sp_updnonbillinsa(NBLInsRequest insertRequest) {
        Integer result = 0;
        result = otcReturnedChequeRepository.sp_updnonbillinsa(insertRequest);
        log.error("Update Non-Bill Ins Result: " + result);
        return result;
    }

    ///////
    @Override
    public Integer sp_updsp(ServiceProviderRequest insertRequest) {
        Integer result = 0;
        result = otcReturnedChequeRepository.sp_updsp(insertRequest);
        return result;
    }



}
