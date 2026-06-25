package com.maven.rms.services;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IBillingRefundAdjustmentSSServiceInterface;
import com.maven.rms.models.FMSLedgerDocRequest;
import com.maven.rms.models.TaxCdRequest;
import com.maven.rms.models.Billing.BillAdjUpdReq;
import com.maven.rms.models.Billing.BillDocReq;
import com.maven.rms.models.Billing.BillDocWithoutFile;
import com.maven.rms.models.Billing.BillGetItem;
import com.maven.rms.models.Billing.BillGetItemReq;
import com.maven.rms.models.Billing.BillLOAAGM;
import com.maven.rms.models.Billing.BillListing;
import com.maven.rms.models.Billing.BillListingRequest;
import com.maven.rms.models.Billing.BillSearch;
import com.maven.rms.models.Billing.BillSearchRequest;
import com.maven.rms.models.Billing.BillingAdjustment;
import com.maven.rms.models.Billing.BillingAdjustmentRequest;
import com.maven.rms.models.Billing.BillingHistory;
import com.maven.rms.repositories.BillingRefundAdjustmentSSRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillingRefundAdjustmentSSService implements IBillingRefundAdjustmentSSServiceInterface {
    
    private final BillingRefundAdjustmentSSRepo billRefundAdjRepo;
    @Autowired
    private AuthService authService;

    public BillingRefundAdjustmentSSService(BillingRefundAdjustmentSSRepo billRefundAdjRepo) {
        this.billRefundAdjRepo = billRefundAdjRepo;
    }

    @Override
    public List<BillSearch> sp_getbillsearch(BillSearchRequest req) {
        List<BillSearch> result = Collections.emptyList();
        List<Object[]> objects = billRefundAdjRepo.sp_getbillsearch(req);
        result = convertBillingCASS(objects);
        return result;
    }

    @Override
    public List<BillListing> sp_getbillcancellisting(BillListingRequest req) {
        List<BillListing> result = Collections.emptyList();
        List<Object[]> objects = billRefundAdjRepo.sp_getbillcancellisting(req);
        result = convertBillingCASSList(objects);
        return result;
    }

    private List<BillListing> convertBillingCASSList(List<Object[]> objects) {
        List<BillListing> billSearchs = new ArrayList<>();

        for (Object[] obj : objects) {
            BillListing billSearch = new BillListing();
            billSearch.setBil_id((Integer) obj[0]);
            billSearch.setBltc_id((Integer) obj[1]);
            billSearch.setBilcust_id((Integer) obj[2]);
            billSearch.setReq_name((String) obj[3]);
            billSearch.setReq_email((String) obj[4]);
            billSearch.setSs_cd((String) obj[5]);
            billSearch.setBilling_no((String) obj[6]);
            billSearch.setBilling_desc((String) obj[7]);
            billSearch.setAction((String) obj[8]);
            billSearch.setDps_amt((BigDecimal) obj[9]);
            billSearch.setBilling_cnt((Integer) obj[10]);
            billSearch.setBilling_freq((String) obj[11]);
            billSearch.setLoa_id((String) obj[12]);
            billSearch.setDt_loa_start((Date) obj[13]);
            billSearch.setDt_loa_end((Date) obj[14]);
            billSearch.setAgm_id((String) obj[15]);
            billSearch.setDt_agm_start((Date) obj[16]);
            billSearch.setDt_agm_end((Date) obj[17]);
            billSearch.setBil_wf_status((String) obj[18]);
            billSearch.setPickup_by((String) obj[19]);
            billSearch.setDt_pick((Date) obj[20]);
            billSearch.setDt_created((Date) obj[21]);
            billSearch.setDt_modified((Date) obj[22]);
            billSearch.setCreated_by((String) obj[23]);
            billSearch.setModified_by((String) obj[24]);
            billSearch.setStatus((String) obj[25]);
            billSearch.setCust_id((String) obj[26]);
            billSearch.setCust_nm((String) obj[27]);
            billSearch.setCust_email((String) obj[28]);
            billSearch.setCust_phone((String) obj[29]);
            billSearch.setCust_addr1((String) obj[30]);
            billSearch.setCust_addr2((String) obj[31]);
            billSearch.setCust_addr3((String) obj[32]);
            billSearch.setCust_postcode((String) obj[33]);
            billSearch.setCust_city((String) obj[34]);
            billSearch.setCust_state((String) obj[35]);
            billSearch.setEnt_nm((String) obj[36]);
            billSearch.setEnt_no((String) obj[37]);
            billSearch.setEnt_ty((String) obj[38]);
            billSearch.setBt_cd((String) obj[39]);
            billSearch.setBt_desc((String) obj[40]);
            billSearch.setAmount((BigDecimal) obj[41]);
            billSearch.setBilling_mthd((String) obj[42]);
            billSearch.setTotal((Integer) obj[43]);
            billSearchs.add(billSearch);
        }

        return billSearchs;
    }

    private List<BillSearch> convertBillingCASS(List<Object[]> objects) {
        List<BillSearch> billSearchs = new ArrayList<>();

        for (Object[] obj : objects) {
            BillSearch billSearch = new BillSearch();
            billSearch.setBil_id((Integer) obj[0]);
            billSearch.setBltc_id((Integer) obj[1]);
            billSearch.setBilcust_id((Integer) obj[2]);
            billSearch.setReq_name((String) obj[3]);
            billSearch.setReq_email((String) obj[4]);
            billSearch.setSs_cd((String) obj[5]);
            billSearch.setBilling_no((String) obj[6]);
            billSearch.setBilling_desc((String) obj[7]);
            billSearch.setAction((String) obj[8]);
            billSearch.setDps_amt((BigDecimal) obj[9]);
            billSearch.setBilling_cnt((Integer) obj[10]);
            billSearch.setBilling_freq((String) obj[11]);
            billSearch.setLoa_id((String) obj[12]);
            billSearch.setDt_loa_start((Date) obj[13]);
            billSearch.setDt_loa_end((Date) obj[14]);
            billSearch.setAgm_id((String) obj[15]);
            billSearch.setDt_agm_start((Date) obj[16]);
            billSearch.setDt_agm_end((Date) obj[17]);
            billSearch.setBil_wf_status((String) obj[18]);
            billSearch.setPickup_by((String) obj[19]);
            billSearch.setDt_pick((Date) obj[20]);
            billSearch.setDt_created((Date) obj[21]);
            billSearch.setDt_modified((Date) obj[22]);
            billSearch.setCreated_by((String) obj[23]);
            billSearch.setModified_by((String) obj[24]);
            billSearch.setStatus((String) obj[25]);
            billSearch.setCust_id((String) obj[26]);
            billSearch.setCust_nm((String) obj[27]);
            billSearch.setCust_email((String) obj[28]);
            billSearch.setCust_phone((String) obj[29]);
            billSearch.setCust_addr1((String) obj[30]);
            billSearch.setCust_addr2((String) obj[31]);
            billSearch.setCust_addr3((String) obj[32]);
            billSearch.setCust_postcode((String) obj[33]);
            billSearch.setCust_city((String) obj[34]);
            billSearch.setCust_state((String) obj[35]);
            billSearch.setEnt_nm((String) obj[36]);
            billSearch.setEnt_no((String) obj[37]);
            billSearch.setEnt_ty((String) obj[38]);
            billSearch.setBt_cd((String) obj[39]);
            billSearch.setBt_desc((String) obj[40]);
            billSearch.setBilling_mthd((String) obj[41]);
            billSearch.setTotal((Integer) obj[42]);
            billSearchs.add(billSearch);
        }

        return billSearchs;
    }

    @Override
    public List<BillGetItem> sp_getbillitem(BillGetItemReq req) {
        List<BillGetItem> result = Collections.emptyList();
        List<Object[]> objects = billRefundAdjRepo.sp_getbillitem(req);
        result = convertBillingCAItem(objects);
        return result;
    }

    private List<BillGetItem> convertBillingCAItem(List<Object[]> objects) {
        List<BillGetItem> billItems = new ArrayList<>();

        for (Object[] obj : objects) {
            BillGetItem billItem = new BillGetItem();
            billItem.setBil_item_id((Integer) obj[0]);
            billItem.setBil_id((Integer) obj[1]);
            billItem.setMft_pk((Integer) obj[2]);
            billItem.setFee_detail_nm_e((String) obj[3]);
            billItem.setUnit_fee((BigDecimal) obj[4]);
            billItem.setQty((Integer) obj[5]);
            billItem.setTax_pct((BigDecimal) obj[6]);
            billItem.setTax_amt((BigDecimal) obj[7]);
            billItem.setFinal_amt((BigDecimal) obj[8]);
            billItem.setBil_wf_id((Integer) obj[9]);
            billItem.setTotal((Integer) obj[10]);
            billItems.add(billItem);
        }

        return billItems;
    }


    @Override
    public List<BillDocWithoutFile> sp_getbilsuppdoc(BillDocReq fmsLedgerDocRequest) {
        List<BillDocWithoutFile> result = Collections.emptyList();
        try {
            List<Object[]> objects = billRefundAdjRepo.sp_getbilsuppdoc(fmsLedgerDocRequest);
            result = convertToGetBillDoc(objects);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    private List<BillDocWithoutFile> convertToGetBillDoc(List<Object[]> objects) {
        List<BillDocWithoutFile> billDocList = new ArrayList<>();

        for (Object[] obj : objects) {
            BillDocWithoutFile billDocWithoutFile = new BillDocWithoutFile();
            billDocWithoutFile.setBil_doc_id((Integer) obj[0]);
            billDocWithoutFile.setBil_wf_id((Integer) obj[1]);
            billDocWithoutFile.setBil_id((Integer) obj[2]);
            billDocWithoutFile.setFile_nm((String) obj[3]);
            billDocWithoutFile.setFile_type((String) obj[4]);
            billDocWithoutFile.setFile_size((Integer) obj[5]);
            billDocWithoutFile.setFile_category((String) obj[6]);
            billDocWithoutFile.setDt_created((Date) obj[7]);
            billDocWithoutFile.setDt_modified((Date) obj[8]);
            billDocWithoutFile.setCreated_by((String) obj[9]);
            billDocWithoutFile.setModified_by((String) obj[10]);
            billDocWithoutFile.setTotal((Integer) obj[11]);
            billDocList.add(billDocWithoutFile);
        }
        return billDocList;
    }


    @Override
    public String sp_getbillsuppfilecontent(BillDocReq req) {

        String result = "";

        try {
            Blob blob = (Blob) billRefundAdjRepo.sp_getbillsuppfilecontent(req);
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
    public Integer sp_updbillcancel(BillDocReq updateRequest) {
        Integer result = 0;

            result = billRefundAdjRepo.sp_updbillcancel(updateRequest);

        return result;
    }

    @Override
    public List<BillingAdjustment> sp_getbilladjustment(BillingAdjustmentRequest req) {
        List<BillingAdjustment> result = Collections.emptyList();
        List<Object[]> objects = billRefundAdjRepo.sp_getbilladjustment(req);
        result = convertBillingCAAdjustmemtList(objects);
        return result;
    }

    private List<BillingAdjustment> convertBillingCAAdjustmemtList(List<Object[]> objects) {
        List<BillingAdjustment> billSearchs = new ArrayList<>();

        for (Object[] obj : objects) {
            BillingAdjustment billSearch = new BillingAdjustment();
            billSearch.setBil_id((Integer) obj[0]);
            billSearch.setBltc_id((Integer) obj[1]);
            billSearch.setBilcust_id((Integer) obj[2]);
            billSearch.setReq_name((String) obj[3]);
            billSearch.setReq_email((String) obj[4]);
            billSearch.setSs_cd((String) obj[5]);
            billSearch.setBilling_no((String) obj[6]);
            billSearch.setBilling_desc((String) obj[7]);
            billSearch.setAction((String) obj[8]);
            billSearch.setDps_amt((BigDecimal) obj[9]);
            billSearch.setBilling_cnt((Integer) obj[10]);
            billSearch.setBilling_freq((String) obj[11]);
            billSearch.setLoa_id((String) obj[12]);
            billSearch.setDt_loa_start((Date) obj[13]);
            billSearch.setDt_loa_end((Date) obj[14]);
            billSearch.setAgm_id((String) obj[15]);
            billSearch.setDt_agm_start((Date) obj[16]);
            billSearch.setDt_agm_end((Date) obj[17]);
            billSearch.setBil_wf_status((String) obj[18]);
            billSearch.setPickup_by((String) obj[19]);
            billSearch.setDt_pick((Date) obj[20]);
            billSearch.setDt_created((Date) obj[21]);
            billSearch.setDt_modified((Date) obj[22]);
            billSearch.setCreated_by((String) obj[23]);
            billSearch.setModified_by((String) obj[24]);
            billSearch.setStatus((String) obj[25]);
            billSearch.setCust_id((String) obj[26]);
            billSearch.setCust_nm((String) obj[27]);
            billSearch.setCust_email((String) obj[28]);
            billSearch.setCust_phone((String) obj[29]);
            billSearch.setCust_addr1((String) obj[30]);
            billSearch.setCust_addr2((String) obj[31]);
            billSearch.setCust_addr3((String) obj[32]);
            billSearch.setCust_postcode((String) obj[33]);
            billSearch.setCust_city((String) obj[34]);
            billSearch.setCust_state((String) obj[35]);
            billSearch.setEnt_nm((String) obj[36]);
            billSearch.setEnt_no((String) obj[37]);
            billSearch.setEnt_ty((String) obj[38]);
            billSearch.setBt_cd((String) obj[39]);
            billSearch.setBt_desc((String) obj[40]);
            billSearch.setAmount((BigDecimal) obj[41]);
            billSearch.setBill_type((String) obj[42]);
            billSearch.setTotal((Integer) obj[43]);
            billSearchs.add(billSearch);
        }

        return billSearchs;
    }

    @Override
    public Integer sp_updbilladjust(List<BillAdjUpdReq> updateRequest) {
        Integer result = 0;

            result = billRefundAdjRepo.sp_updbilladjust(updateRequest);

        return result;
    }


    @Override
    public List<BillingHistory> sp_getbillhist(BillAdjUpdReq req) {
        List<BillingHistory> result = Collections.emptyList();
        List<Object[]> objects = billRefundAdjRepo.sp_getbillhist(req);
        result = convertBillingCAHistList(objects);
        return result;
    }

    private List<BillingHistory> convertBillingCAHistList(List<Object[]> objects) {
        List<BillingHistory> billSearchs = new ArrayList<>();

        for (Object[] obj : objects) {
            BillingHistory billSearch = new BillingHistory();
            billSearch.setBil_wf_hist_id((Integer) obj[0]);
            billSearch.setBil_wf_id((Integer) obj[1]);
            billSearch.setBltc_id((Integer) obj[2]);
            billSearch.setBilcust_id((Integer) obj[3]);
            billSearch.setReq_name((String) obj[4]);
            billSearch.setReq_email((String) obj[5]);
            billSearch.setSs_cd((String) obj[6]);
            billSearch.setBilling_no((String) obj[7]);
            billSearch.setBilling_desc((String) obj[8]);
            billSearch.setAction((String) obj[9]);
            billSearch.setDps_amt((BigDecimal) obj[10]);
            billSearch.setBilling_cnt((Integer) obj[11]);
            billSearch.setBilling_freq((String) obj[12]);
            billSearch.setLoa_id((String) obj[13]);
            billSearch.setDt_loa_start((Date) obj[14]);
            billSearch.setDt_loa_end((Date) obj[15]);
            billSearch.setAgm_id((String) obj[16]);
            billSearch.setDt_agm_start((Date) obj[17]);
            billSearch.setDt_agm_end((Date) obj[18]);
            billSearch.setBil_wf_status((String) obj[19]);
            billSearch.setPickup_by((String) obj[20]);
            billSearch.setDt_pick((Date) obj[21]);
            billSearch.setDt_created((Date) obj[22]);
            billSearch.setDt_modified((Date) obj[23]);
            billSearch.setCreated_by((String) obj[24]);
            billSearch.setModified_by((String) obj[25]);
            billSearch.setStatus((String) obj[26]);
            billSearch.setBil_id((Integer) obj[27]);
            billSearch.setBilling_mthd((String) obj[28]);
            billSearch.setMsg((String) obj[29]);
            billSearch.setTotal((Integer) obj[30]);
            billSearchs.add(billSearch);
        }

        return billSearchs;
    }


    @Override
    public List<BillLOAAGM> sp_getbillingloaagm(BillDocReq req) {
        List<BillLOAAGM> result = Collections.emptyList();
        List<Object[]> objects = billRefundAdjRepo.sp_getbillingloaagm(req);
        result = convertBillLOAAGM(objects);
        return result;
    }

    private List<BillLOAAGM> convertBillLOAAGM(List<Object[]> objects) {
        List<BillLOAAGM> billLOAAGMs = new ArrayList<>();

        for (Object[] obj : objects) {
            BillLOAAGM billLOAAGM = new BillLOAAGM();
            billLOAAGM.setBil_child_id((Integer) obj[0]);
            billLOAAGM.setBil_child_date((Date) obj[1]);
            billLOAAGM.setBil_child_status((String) obj[2]);
            billLOAAGM.setDt_created((Date) obj[3]);
            billLOAAGM.setDt_modified((Date) obj[4]);
            billLOAAGM.setCreated_by((String) obj[5]);
            billLOAAGM.setModified_by((String) obj[6]);
            billLOAAGM.setStatus((String) obj[7]);
            billLOAAGM.setBil_wf_id((Integer) obj[8]);
            billLOAAGM.setBil_no((String) obj[9]);
            billLOAAGM.setBil_id((Integer) obj[10]);
            billLOAAGM.setBil_status((String) obj[11]);
            billLOAAGM.setTotal((Integer) obj[12]);
            billLOAAGMs.add(billLOAAGM);
        }

        return billLOAAGMs;
    }   
}