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

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.FMSCRMemo;
import com.maven.rms.models.FMSDRMemo;
import com.maven.rms.models.FMSDRMemoJson;
import com.maven.rms.models.GenericValue;
import com.maven.rms.repositories.BillingRepository;
import com.maven.rms.repositories.FMSDRMemoRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FMSDRMemoService {

    private final FMSDRMemoRepository fmsDRMemoRepo;
    private final BillingRepository bRepo;

    @Value("${fmsdrmemo.api.url}")
    private String api_url;

    @Value("${fmsdrmemo.api.name}")
    private String api_name;

    @Value("${fms.ibm-client-id}")
    private String IBMClientID;

    @Autowired
    private FMSCRMemoService fmsCRMemoService;

    @Autowired
    private CommonService commonSvc;

    public FMSDRMemoService(FMSDRMemoRepository fmsDRMemoRepo, BillingRepository bRepo) {
        this.fmsDRMemoRepo = fmsDRMemoRepo;
        this.bRepo = bRepo;
    }

    // public List<FMSDRMemo> sp_getfmsrcpgtxn() {
    // List<FMSDRMemo> result = new ArrayList<>();

    // try {
    // List<Object[]> objects = fmsDRMemoRepo.sp_getfmsrcpgtxn();
    // List<FMSDRMemo> fmsDRMemoList = new ArrayList<>();
    // for (Object[] obj : objects) {
    // FMSDRMemo fmsDRMemo = new FMSDRMemo();
    // fmsDRMemo.setRc_pg_id((BigInteger) obj[0]);
    // fmsDRMemo.setPg_pymt_method((String) obj[1]);
    // fmsDRMemo.((BigInteger) obj[2]);
    // fmsDRMemo.setQty((int) obj[3]);
    // fmsDRMemo.setItem_desc((String) obj[4]);
    // fmsDRMemo.setUnit_fee((BigDecimal) obj[5]);
    // fmsDRMemo.setEnt_nm((String) obj[6]);
    // fmsDRMemo.setEnt_no((String) obj[7]);
    // fmsDRMemo.setEnt_ty((String) obj[8]);
    // fmsDRMemo.setGross_amt((BigDecimal) obj[9]);
    // fmsDRMemo.setFee_detail_id((String) obj[10]);
    // fmsDRMemo.setTax_amt((BigDecimal) obj[11]);
    // fmsDRMemo.setRcpt_no((String) obj[12]);
    // fmsDRMemo.setCust_nm((String) obj[13]);
    // fmsDRMemo.setPg_pymt_amt((BigDecimal) obj[14]);
    // fmsDRMemoList.add(fmsDRMemo);
    // }
    // result = fmsDRMemoList;
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    public List<FMSDRMemo> sp_getfmsrcpgtxn() {
        List<FMSDRMemo> result = new ArrayList<>();
        List<Object[]> objects = fmsDRMemoRepo.sp_getfmsrcpgtxn();
        List<FMSDRMemo> fmsDRMemoList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSDRMemo fmsDRMemo = new FMSDRMemo();
            fmsDRMemo.setRc_pg_id((BigInteger) obj[0]);
            fmsDRMemo.setPg_pymt_method((String) obj[1]);
            fmsDRMemo.setMtt_pg_id((BigInteger) obj[2]);
            fmsDRMemo.setQty((Integer) obj[3]);
            fmsDRMemo.setItem_desc((String) obj[4]);
            fmsDRMemo.setUnit_fee((BigDecimal) obj[5]);
            fmsDRMemo.setEnt_nm((String) obj[6]);
            fmsDRMemo.setEnt_no((String) obj[7]);
            fmsDRMemo.setEnt_ty((String) obj[8]);
            fmsDRMemo.setGross_amt((BigDecimal) obj[9]);
            fmsDRMemo.setFee_detail_id((String) obj[10]);
            fmsDRMemo.setTax_amt((BigDecimal) obj[11]);
            fmsDRMemo.setDepositID((String) obj[12]); // 241010: Added 2 new parameters
            fmsDRMemo.setDepositTask((String) obj[13]); // 241010: Added 2 new parameters
            fmsDRMemo.setRcpt_no((String) obj[14]);
            fmsDRMemo.setCust_nm((String) obj[15]);
            fmsDRMemo.setPg_pymt_amt((BigDecimal) obj[16]);
            fmsDRMemo.setRc_pgtxn_id((BigInteger) obj[17]);
            fmsDRMemoList.add(fmsDRMemo);
        }
        result = fmsDRMemoList;
        return result;
    }

    // public BigInteger sp_insfmsrcpgtxn_h(BigDecimal pg_pymt_amt) {
    // BigInteger result = null;

    // try {
    // result = fmsDRMemoRepo.sp_insfmsrcpgtxn_h(pg_pymt_amt);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    public BigInteger sp_insfmsrcpgtxn_h(BigDecimal pg_pymt_amt, Integer flag) {
        BigInteger result = null;
        result = fmsDRMemoRepo.sp_insfmsrcpgtxn_h(pg_pymt_amt, flag);
        return result;
    }

    public BigInteger sp_insfmsdrmemo_h(FMSDRMemo fmsdrMemo) {
        return fmsDRMemoRepo.sp_insfmsdrmemo_h(fmsdrMemo);
    }

    // public Integer sp_insfmsrcpgtxn_b(BigInteger drmemo_hid, String
    // pg_pymt_method, BigInteger mtt_pg_id, int qty,
    // String item_desc, BigDecimal unit_fee, String entity_nm, String entity_no,
    // String entity_type, BigDecimal gross_amt,
    // String fee_detail_id, BigDecimal tax_amt, String rcpt_no, String cust_nm) {
    // Integer result = null;

    // try {
    // result = fmsDRMemoRepo.sp_insfmsrcpgtxn_b(drmemo_hid, pg_pymt_method,
    // mtt_pg_id, qty, item_desc, unit_fee, entity_nm, entity_no, entity_type,
    // gross_amt, fee_detail_id, tax_amt, rcpt_no, cust_nm);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    // public Integer sp_insfmsrcpgtxn_b(BigInteger drmemo_hid, FMSDRMemo fmsdrMemo)
    // {
    // Integer result = null;

    // try {
    // result = fmsDRMemoRepo.sp_insfmsrcpgtxn_b(drmemo_hid, fmsdrMemo);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    public Integer sp_insfmsrcpgtxn_b(BigInteger drmemo_hid, FMSDRMemo fmsdrMemo, Integer flag) {
        Integer result = null;
        result = fmsDRMemoRepo.sp_insfmsrcpgtxn_b(drmemo_hid, fmsdrMemo, flag);
        return result;
    }

    public Integer sp_insfmsdrmemo_b(BigInteger drmemo_hid, FMSDRMemo fmsdrMemo, Integer flag) {
        return fmsDRMemoRepo.sp_insfmsdrmemo_b(drmemo_hid, fmsdrMemo, flag);
    }

    public Integer sp_insfmsrcpgtxn_b(BigInteger drmemo_hid, FMSCRMemo fmscrMemo, Integer flag) {
        Integer result = null;
        result = fmsDRMemoRepo.sp_insfmsrcpgtxn_b(drmemo_hid, fmscrMemo, flag);
        return result;
    }

    // public Integer sp_insfmsrcpgtxn_f(BigInteger drmemo_hid, BigInteger
    // mtt_pg_id) {
    // Integer result = null;

    // try {
    // result = fmsDRMemoRepo.sp_insfmsrcpgtxn_f(drmemo_hid, mtt_pg_id);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    // public Integer sp_insfmsrcpgtxn_f(BigInteger drmemo_hid, FMSDRMemo fmsdrMemo)
    // {
    // Integer result = null;

    // try {
    // result = fmsDRMemoRepo.sp_insfmsrcpgtxn_f(drmemo_hid, fmsdrMemo);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    public Integer sp_insfmsrcpgtxn_f(BigInteger drmemo_hid, FMSDRMemo fmsdrMemo) {
        Integer result = null;
        result = fmsDRMemoRepo.sp_insfmsrcpgtxn_f(drmemo_hid, fmsdrMemo);
        return result;
    }

    public Integer sp_insfmsdrmemo_f(BigInteger drmemo_hid, String fms_ref_no, String doc_type) {
        return fmsDRMemoRepo.sp_insfmsdrmemo_f(drmemo_hid, fms_ref_no, doc_type);
    }

    public Integer sp_insfmsrcpgtxn_f(BigInteger drmemo_hid, FMSCRMemo fmscrMemo) {
        Integer result = null;
        result = fmsDRMemoRepo.sp_insfmsrcpgtxn_f(drmemo_hid, fmscrMemo);
        return result;
    }

    // public List<FMSDRMemo> sp_getfmsdrmemo() {
    // List<FMSDRMemo> result = new ArrayList<>();

    // try {
    // List<Object[]> objects = fmsDRMemoRepo.sp_getfmsdrmemo();
    // List<FMSDRMemo> fmsDRMemoList = new ArrayList<>();
    // for (Object[] obj : objects) {
    // FMSDRMemo fmsDRMemo = new FMSDRMemo();
    // fmsDRMemo.setDrmemo_hid((BigInteger) obj[0]);
    // fmsDRMemo.setType((String) obj[1]);
    // fmsDRMemo.setLink_branch((String) obj[2]);
    // fmsDRMemo.setAmt((BigDecimal) obj[3]);
    // fmsDRMemo.setCust((String) obj[4]);
    // fmsDRMemo.setRms_batch_no((String) obj[5]);
    // fmsDRMemo.setDt_sent((Date) obj[6]);
    // fmsDRMemo.setDesc((String) obj[7]);
    // fmsDRMemo.setAttr_ext_sys((String) obj[8]);
    // fmsDRMemo.setFms_ref_no((String) obj[9]);
    // fmsDRMemo.setDoc_ty((String) obj[10]);
    // fmsDRMemo.setAcct((String) obj[11]);
    // fmsDRMemo.setBranch((String) obj[12]);
    // fmsDRMemo.setQty_drmemo((int) obj[13]);
    // fmsDRMemo.setSub_acct((String) obj[14]);
    // fmsDRMemo.setTxn_desc((String) obj[15]);
    // fmsDRMemo.setUnit_price((BigDecimal) obj[16]);
    // // fmsDRMemo.setRcpt_no_drmemo((String) obj[17]);
    // // fmsDRMemo.setPayee_info((String) obj[18]);
    // fmsDRMemo.setEnt_nm((String) obj[17]);
    // fmsDRMemo.setEnt_no((String) obj[18]);
    // fmsDRMemo.setEnt_ty((String) obj[19]);
    // // fmsDRMemo.setItem_amt((BigDecimal) obj[22]);
    // // fmsDRMemo.setPymt((BigDecimal) obj[23]);
    // // fmsDRMemo.setItem_tax_amt((BigDecimal) obj[24]);
    // fmsDRMemo.setCoa1((String) obj[20]);
    // fmsDRMemo.setCoa2((String) obj[21]);
    // fmsDRMemoList.add(fmsDRMemo);
    // }
    // result = fmsDRMemoList;
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    public List<FMSDRMemo> sp_getfmsdrmemo() {
        List<FMSDRMemo> result = new ArrayList<>();
        List<Object[]> objects = fmsDRMemoRepo.sp_getfmsdrmemo();
        List<FMSDRMemo> fmsDRMemoList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSDRMemo fmsDRMemo = new FMSDRMemo();
            fmsDRMemo.setDrmemo_hid((BigInteger) obj[0]);
            fmsDRMemo.setType((String) obj[1]);
            fmsDRMemo.setLink_branch((String) obj[2]);
            fmsDRMemo.setAmt((BigDecimal) obj[3]);
            fmsDRMemo.setCust((String) obj[4]);
            fmsDRMemo.setRms_batch_no((String) obj[5]);
            fmsDRMemo.setDt_sent((Date) obj[6]);
            fmsDRMemo.setDesc((String) obj[7]);
            fmsDRMemo.setAttr_ext_sys((String) obj[8]);
            fmsDRMemo.setFms_ref_no((String) obj[9]);
            fmsDRMemo.setDoc_ty((String) obj[10]);
            fmsDRMemo.setAcct((String) obj[11]);
            fmsDRMemo.setBranch((String) obj[12]);
            fmsDRMemo.setQty_drmemo((Integer) obj[13]);
            fmsDRMemo.setSub_acct((String) obj[14]);
            fmsDRMemo.setTxn_desc((String) obj[15]);
            fmsDRMemo.setUnit_price((BigDecimal) obj[16]);
            // fmsDRMemo.setRcpt_no_drmemo((String) obj[17]);
            // fmsDRMemo.setPayee_info((String) obj[18]);
            fmsDRMemo.setEnt_nm((String) obj[17]);
            fmsDRMemo.setEnt_no((String) obj[18]);
            fmsDRMemo.setEnt_ty((String) obj[19]);
            // fmsDRMemo.setItem_amt((BigDecimal) obj[22]);
            // fmsDRMemo.setPymt((BigDecimal) obj[23]);
            // fmsDRMemo.setItem_tax_amt((BigDecimal) obj[24]);
            fmsDRMemo.setCoa1((String) obj[20]);
            fmsDRMemo.setCoa2((String) obj[21]);
            fmsDRMemo.setDepositID((String) obj[22]); // 241010: Added 2 new parameters
            fmsDRMemo.setDepositTask((String) obj[23]); // 241010: Added 2 new parameters
            fmsDRMemo.setGenPdf((Integer) obj[24]);
            fmsDRMemo.setBil_child_id((Integer) obj[25]);
            fmsDRMemo.setCrmemo_hid((BigInteger) obj[26]);
            fmsDRMemo.setMtt_pg_id((BigInteger)obj[27]);
            fmsDRMemoList.add(fmsDRMemo);
        }
        result = fmsDRMemoList;
        return result;
    }

    public List<FMSDRMemo> sp_getfmsdrmemobyhid(BigInteger hid) {
        List<FMSDRMemo> result = new ArrayList<>();
        List<Object[]> objects = fmsDRMemoRepo.sp_getfmsdrmemobyhid(hid);
        List<FMSDRMemo> fmsDRMemoList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSDRMemo fmsDRMemo = new FMSDRMemo();
            fmsDRMemo.setDrmemo_hid((BigInteger) obj[0]);
            fmsDRMemo.setType((String) obj[1]);
            fmsDRMemo.setLink_branch((String) obj[2]);
            fmsDRMemo.setAmt((BigDecimal) obj[3]);
            fmsDRMemo.setCust((String) obj[4]);
            fmsDRMemo.setRms_batch_no((String) obj[5]);
            fmsDRMemo.setDt_sent((Date) obj[6]);
            fmsDRMemo.setDesc((String) obj[7]);
            fmsDRMemo.setAttr_ext_sys((String) obj[8]);
            fmsDRMemo.setFms_ref_no((String) obj[9]);
            fmsDRMemo.setDoc_ty((String) obj[10]);
            fmsDRMemo.setAcct((String) obj[11]);
            fmsDRMemo.setBranch((String) obj[12]);
            fmsDRMemo.setQty_drmemo((Integer) obj[13]);
            fmsDRMemo.setSub_acct((String) obj[14]);
            fmsDRMemo.setTxn_desc((String) obj[15]);
            fmsDRMemo.setUnit_price((BigDecimal) obj[16]);
            fmsDRMemo.setEnt_nm((String) obj[17]);
            fmsDRMemo.setEnt_no((String) obj[18]);
            fmsDRMemo.setEnt_ty((String) obj[19]);
            fmsDRMemo.setCoa1((String) obj[20]);
            fmsDRMemo.setCoa2((String) obj[21]);
            fmsDRMemo.setDepositID((String) obj[22]);
            fmsDRMemo.setDepositTask((String) obj[23]);
            fmsDRMemo.setGenPdf((Integer) obj[24]);
            fmsDRMemo.setBil_child_id((Integer) obj[25]);
            fmsDRMemo.setH_fms_ref_no((String) obj[26]);
            fmsDRMemoList.add(fmsDRMemo);
        }
        return fmsDRMemoList;
    }

    public FMSDRMemo sp_getfmsdrmemobyarifmsrefno(String ari_fms_ref_no) {
        Object[] obj = fmsDRMemoRepo.sp_getfmsdrmemobyarifmsrefno(ari_fms_ref_no);
        FMSDRMemo fmsDRMemo = new FMSDRMemo();
        fmsDRMemo.setDrmemo_hid((BigInteger) obj[0]);
        fmsDRMemo.setType((String) obj[1]);
        fmsDRMemo.setLink_branch((String) obj[2]);
        fmsDRMemo.setAmt((BigDecimal) obj[3]);
        fmsDRMemo.setCust((String) obj[4]);
        fmsDRMemo.setRms_batch_no((String) obj[5]);
        fmsDRMemo.setDt_sent((Date) obj[6]);
        fmsDRMemo.setDesc((String) obj[7]);
        fmsDRMemo.setAttr_ext_sys((String) obj[8]);
        fmsDRMemo.setFms_ref_no((String) obj[9]);
        fmsDRMemo.setDoc_ty((String) obj[10]);
        fmsDRMemo.setAcct((String) obj[11]);
        fmsDRMemo.setBranch((String) obj[12]);
        fmsDRMemo.setQty_drmemo((Integer) obj[13]);
        fmsDRMemo.setSub_acct((String) obj[14]);
        fmsDRMemo.setTxn_desc((String) obj[15]);
        fmsDRMemo.setUnit_price((BigDecimal) obj[16]);
        fmsDRMemo.setEnt_nm((String) obj[17]);
        fmsDRMemo.setEnt_no((String) obj[18]);
        fmsDRMemo.setEnt_ty((String) obj[19]);
        fmsDRMemo.setCoa1((String) obj[20]);
        fmsDRMemo.setCoa2((String) obj[21]);
        fmsDRMemo.setDepositID((String) obj[22]);
        fmsDRMemo.setDepositTask((String) obj[23]);
        fmsDRMemo.setGenPdf((Integer) obj[24]);
        fmsDRMemo.setBil_child_id((Integer) obj[25]);

        return fmsDRMemo;
    }

    // public Integer sp_updfmsdrmemo(BigInteger drmemo_hid, String
    // resp_attr_ext_sys, String fms_ref_no, String resp_co,
    // String resp_status, String resp_msg, Date resp_dt) {
    // Integer result = null;

    // try {
    // result = fmsDRMemoRepo.sp_updfmsdrmemo(drmemo_hid, resp_attr_ext_sys,
    // fms_ref_no, resp_co, resp_status,
    // resp_msg, resp_dt);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    // public Integer sp_updfmsdrmemo(BigInteger drmemo_hid, FMSDRMemo fmsdrMemo) {
    // Integer result = null;

    // try {
    // result = fmsDRMemoRepo.sp_updfmsdrmemo(drmemo_hid, fmsdrMemo);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    public Integer sp_updfmsdrmemo(BigInteger drmemo_hid, FMSDRMemo fmsdrMemo) {
        Integer result = null;
        result = fmsDRMemoRepo.sp_updfmsdrmemo(drmemo_hid, fmsdrMemo);
        return result;
    }

    public Integer sp_updfmsdrmemohid(BigInteger drmemo_hid, BigInteger crmemo_hid) {
        Integer result = null;
        result = fmsDRMemoRepo.sp_updfmsdrmemohid(drmemo_hid, crmemo_hid);
        return result;
    }


    /*
     * public FMSDRMemoJson generateStringBody(String type, String linkBranch,
     * BigDecimal amt, String cust,
     * String rmsBatchNo,
     * Date dtSent, String desc, String attrExtSys, String acct, String branch,
     * String subAcct, String txnDesc, int qtyDrmemo, BigDecimal unitPrice, String
     * entNm,
     * String entNo, String entTy, String coa1, String coa2, String fmsRefNo, String
     * docTy,
     * BigDecimal pgPymtAmt, boolean isFirst, boolean isLast, FMSDRMemoJson
     * inputObject,
     * String depositID, String depositTask)
     */

    public FMSDRMemoJson generateStringBody(FMSDRMemo memo, boolean isFirst, boolean isLast, FMSDRMemoJson inputObject)
            throws JsonProcessingException {
        // 241010: Added 2 new parameters, depositID and depositTask
        // is first
        if (isFirst) {
            inputObject.setType(new GenericValue<>(memo.getType()));
            inputObject.setLinkBranch(new GenericValue<>(memo.getLink_branch()));
            inputObject.setAmount(new GenericValue<>(memo.getAmt()));
            inputObject.setCustomer(new GenericValue<>(memo.getCust()));
            inputObject.setCustomerOrder(new GenericValue<>(memo.getRms_batch_no()));
            inputObject.setProject(new GenericValue<>("RMS"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dtSentFormatted = sdf.format(memo.getDt_sent());
            inputObject.setDate(new GenericValue<>(dtSentFormatted));
            inputObject.setDescription(new GenericValue<>(memo.getDesc()));
            inputObject.setHold(new GenericValue<>(true));

            FMSDRMemoJson.Custom custom = new FMSDRMemoJson.Custom();
            FMSDRMemoJson.CurrentDocument currentDocument = new FMSDRMemoJson.CurrentDocument();
            FMSDRMemoJson.Attribute attribute = new FMSDRMemoJson.Attribute();
            FMSDRMemoJson.Attribute2 attribute2 = new FMSDRMemoJson.Attribute2();
            attribute.setValue(memo.getAttr_ext_sys());
            attribute2.setValue(memo.getGenPdf() == null || memo.getGenPdf() == 0 ? false : true);
            currentDocument.setAttribute(attribute);
            currentDocument.setAttribute2(attribute2);
            custom.setCurrentDocument(currentDocument);
            inputObject.setCustom(custom);

            inputObject.setDetails(new ArrayList<>());

            List<FMSDRMemoJson.DebitMemo> debitMemos = new ArrayList<>();
            FMSDRMemoJson.DebitMemo debitMemo = new FMSDRMemoJson.DebitMemo();
            debitMemo.setReferenceNbr(new GenericValue<>(memo.getFms_ref_no()));
            debitMemo.setAmountPaid(new GenericValue<>(memo.getAmt()));
            debitMemo.setDocType(new GenericValue<>(memo.getDoc_ty()));
            debitMemos.add(debitMemo);
            inputObject.setApplicationsDebitMemo(debitMemos);
        }

        // Adding detail to the list
        List<FMSDRMemoJson.Detail> details = inputObject.getDetails();

        if (memo.getFee_detail_id() == null || memo.getFee_detail_id().isEmpty()) {
            if (hasNonNullSimpleDetailValues(memo)) {
                FMSDRMemoJson.Detail detail = new FMSDRMemoJson.Detail();
                detail.setSubaccount(new GenericValue<>(memo.getSub_acct()));
                detail.setChartOfAccount1(new GenericValue<>(memo.getCoa1()));
                detail.setQty(new GenericValue<>(1));
                detail.setUnitPrice(
                        new GenericValue<>(memo.getUnit_price() != null ? memo.getUnit_price() : new BigDecimal(0)));
                details.add(detail);
            }

            if (isLast) {
                inputObject.setDetails(details);
            }
            return inputObject;
        }

        else {
            if (hasNonNullFullDetailValues(memo)) {
                FMSDRMemoJson.Detail detail = new FMSDRMemoJson.Detail();
                detail.setAccount(new GenericValue<>(memo.getAcct()));
                detail.setLineNbr(new GenericValue<>(String.valueOf(details.size() + 1)));
                detail.setBranch(new GenericValue<>(memo.getBranch()));
                detail.setChartOfAccount1(new GenericValue<>(memo.getCoa1()));
                detail.setChartOfAccount2(new GenericValue<>(memo.getCoa2()));
                detail.setSubaccount(new GenericValue<>(memo.getSub_acct()));
                detail.setTransactionDescription(new GenericValue<>(memo.getTxn_desc()));
                detail.setQty(new GenericValue<>(memo.getQty_drmemo()));
                detail.setUnitPrice(
                        new GenericValue<>(memo.getUnit_price() != null ? memo.getUnit_price() : new BigDecimal(0)));
                detail.setEntityName(new GenericValue<>(memo.getEnt_nm()));
                detail.setEntityNumber(new GenericValue<>(memo.getEnt_no()));
                detail.setEntityType(new GenericValue<>(memo.getEnt_ty()));
                detail.setDepositID(new GenericValue<>(memo.getDepositID()));
                detail.setDepositTask(new GenericValue<>(memo.getDepositTask()));

                detail.setRcptNo(new GenericValue<>(memo.getRcpt_no()));
                detail.setPayeeInfo(new GenericValue<>(memo.getCust_nm()));
                detail.setPymtMd(new GenericValue<>(memo.getPg_pymt_method()));
                detail.setItemAmt(new GenericValue<>(memo.getQty()));
                detail.setItmTaxAmt(new GenericValue<>(memo.getTax_amt() != null && memo.getQty() != 0
                        ? memo.getTax_amt().divide(new BigDecimal(memo.getQty()))
                        : new BigDecimal(0)));
                detail.setDiscAmt(
                        new GenericValue<>(
                                memo.getDiscount_amt() != null ? memo.getDiscount_amt() : new BigDecimal(0)));

                details.add(detail);
            }

            // If this is the last detail, finalize the inputObject (if needed)
            // if (isLast) {
            // inputObject.setDetails(details);
            // }
            if (isLast) {
                // If no valid details were added, set Details to null to exclude it from JSON
                if (details.isEmpty()) {
                    inputObject.setDetails(null);
                } else {
                    inputObject.setDetails(details);
                }
            }
            return inputObject;
        }
    }

    @SuppressWarnings("deprecation")
    public List<FMSDRMemo> fms_api_drmemo(String stringBody, BigInteger drmemo_hid, FMSDRMemo memo) {
        List<FMSDRMemo> result = new ArrayList<>();

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
            // System.out.println("Response Body: " + response.toString());

            // Close the connection
            connection.disconnect();

            try {
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("FMSDebitMemo");
                extAudit.setI_request_body(stringBody);
                extAudit.setI_response_body(response.toString());
                extAudit.setI_rms_batch_no(memo.getRms_batch_no());
                extAudit.setI_direction("Outgoing");
                extAudit.setI_remark(null);
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for FMS Debit Memo: " + e.getMessage() + ", "
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

            // Parse the JSON response manually
            JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // Create FMSDRMemo object and set the values
            FMSDRMemo fmsdrmemo = new FMSDRMemo();

            if (memo.getGenPdf() != null && memo.getGenPdf() > 0) {
                fmsdrmemo.setFms_ref_no(jsonResponse.has("ReferenceNbr")
                        ? jsonResponse.get("ReferenceNbr").getAsJsonObject().get("value").getAsString()
                        : null);
                fmsdrmemo.setResp_co(jsonResponse.has("CustomerOrder")
                        ? jsonResponse.get("CustomerOrder").getAsJsonObject().get("value").getAsString()
                        : null);
                fmsdrmemo.setResp_status("200");
                if (jsonResponse.has("Date")) {
                    String dateString = jsonResponse.get("Date").getAsJsonObject().get("value").getAsString()
                            .substring(0, 19).replace("T", " ");
                    // fmsdrmemo.setResp_dt(dateString);
                    try {
                        Date date = dateFormat.parse(dateString);
                        fmsdrmemo.setResp_dt(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                if ((jsonResponse.has("document") && jsonResponse.get("document").getAsJsonObject().has("value"))
                        && (jsonResponse.get("document").getAsJsonObject().get("value").getAsString() != null
                                || !jsonResponse.get("document").getAsJsonObject().get("value").getAsString()
                                        .isEmpty())) {
                    Integer statusCode = 0;
                    String data = jsonResponse.get("document").getAsJsonObject().get("value").getAsString();
                    // System.out.println("DEBUG: bil child id: " +
                    // Integer.toString(fmsariModel.getBil_child_id()));
                    if (memo.getBil_child_id() != null && memo.getBil_child_id() > 0) {
                        bRepo.sp_insbilchildimg(memo.getBil_child_id(), data);
                        statusCode = bRepo.sp_checkbilchildimg(memo.getBil_child_id());
                    }
                    if (statusCode < 1) {
                        if (statusCode == 0)
                            log.error("Exception in " + this.getClass().toString()
                                    + "fms_api_cr func - failed to insert bill img due to: "
                                    + "Bad bil_child_id in fmsarimodel: " + Integer.toString(memo.getBil_child_id()));
                        else if (statusCode < 0)
                            log.error("Exception in " + this.getClass().toString()
                                    + "fms_api_cr func - failed to insert bill img due to: "
                                    + "sp_insbilchildimg - Param(" + Integer.toString(memo.getBil_child_id())
                                    + ", " + data.substring(0, 50) + "...)");
                    }
                }

            } else {
                fmsdrmemo.setResp_attr_ext_sys(
                        jsonResponse.has("AttributeEXTSYSTEM") ? jsonResponse.get("AttributeEXTSYSTEM").getAsString()
                                : null);
                fmsdrmemo.setFms_ref_no(
                        jsonResponse.has("ReferenceNbr") ? jsonResponse.get("ReferenceNbr").getAsString() : null);
                fmsdrmemo.setResp_co(
                        jsonResponse.has("CustomerOrder") ? jsonResponse.get("CustomerOrder").getAsString() : null);
                fmsdrmemo.setResp_status(jsonResponse.has("Status") ? jsonResponse.get("Status").getAsString() : null);
                fmsdrmemo.setResp_msg(jsonResponse.has("Message") ? jsonResponse.get("Message").getAsString() : null);
                // Parse the date string to Date object
                if (jsonResponse.has("Date")) {
                    String dateString = jsonResponse.get("Date").getAsString();
                    // fmsdrmemo.setResp_dt(dateString);
                    try {
                        Date date = dateFormat.parse(dateString);
                        fmsdrmemo.setResp_dt(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }

            result.add(fmsdrmemo);

            this.sp_updfmsdrmemo(drmemo_hid, fmsdrmemo);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<FMSDRMemo> fms_drmemo_sch() throws JsonProcessingException {
        List<FMSDRMemo> result = new ArrayList<>();
        BigInteger drmemo_hid = null;
        BigInteger crmemo_hid = null;

        // getfmsrcpg
        List<FMSDRMemo> fmsRCPG = this.sp_getfmsrcpgtxn();

        // loop n insfmsrcpg
        try {
            while(CollectionUtils.size(fmsRCPG) > 0) {
            for (int i = 0; i < fmsRCPG.size(); i++) {

                FMSDRMemo item = fmsRCPG.get(i);
                BigInteger current_rc_pg_txn_id = fmsRCPG.get(i).getRc_pgtxn_id();
                Integer flag = 0;

                // If this is not the last record, compare with the next record's mtt_pg_id
                if (i < CollectionUtils.size(fmsRCPG) - 1) {
                    BigInteger next_rc_pg_txn_id = fmsRCPG.get(i + 1).getRc_pgtxn_id();
                    if (!current_rc_pg_txn_id.equals(next_rc_pg_txn_id)) {
                        flag = 1; // Set the flag if current mtt_pg_id is different from the next one
                    }
                } else {
                    // If it's the last record, set flag to 1 (assuming there's no next record)
                    flag = 1;
                }

                if (i == 0) {
                    if (item.getFee_detail_id() == null || item.getFee_detail_id().isEmpty()) {
                        drmemo_hid = this.sp_insfmsrcpgtxn_h(item.getPg_pymt_amt(), 0);
                    } else {
                        drmemo_hid = this.sp_insfmsrcpgtxn_h(item.getPg_pymt_amt(), 0);
                    }
                    // drmemo_hid = this.sp_insfmsrcpgtxn_h(item.getPg_pymt_amt(), 0);
                    this.sp_insfmsrcpgtxn_b(drmemo_hid, item, flag);

                    if (item.getRcpt_no() != null && !item.getRcpt_no().isEmpty()) {
                        crmemo_hid = fmsCRMemoService.sp_insfmsrcpgmtt_h(item.getPg_pymt_amt(), 1);
                        fmsCRMemoService.sp_insfmsrcpgmtt_b(crmemo_hid, item, flag);

                        this.sp_updfmsdrmemohid(drmemo_hid, crmemo_hid);
                    }

                } else if (i > 0) {
                    this.sp_insfmsrcpgtxn_b(drmemo_hid, item, flag);
                    if (item.getRcpt_no() != null && !item.getRcpt_no().isEmpty()) {
                        fmsCRMemoService.sp_insfmsrcpgmtt_b(crmemo_hid, item, flag);
                    }
                }

                if (i == fmsRCPG.size() - 1) {
                    this.sp_insfmsrcpgtxn_f(drmemo_hid, item);
                    if (item.getRcpt_no() != null && !item.getRcpt_no().isEmpty()) {
                        // fmsCRMemoService.sp_insfmsrcpgmtt_f(crmemo_hid, item.getMtt_pg_id(), 1);
                    }

                }
            }
            fmsRCPG = this.sp_getfmsrcpgtxn();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }

        // getfmsdrmemo
        List<FMSDRMemo> fmsDRMemo = this.sp_getfmsdrmemo();

        // stringbody n fms_api_drmemo
        String stringBody = "";
        FMSDRMemoJson fmsDRMemoJson = new FMSDRMemoJson();

        // if size > 0
        if (fmsDRMemo.size() > 0) {
            for (int i = 0; i < fmsDRMemo.size(); i++) {
                FMSDRMemo currentItem = fmsDRMemo.get(i);
                String currentBatchNo = currentItem.getRms_batch_no();

                boolean isFirst = (i == 0) || !fmsDRMemo.get(i - 1).getRms_batch_no().equals(currentBatchNo);
                boolean isLast = (i == fmsDRMemo.size() - 1)
                        || !fmsDRMemo.get(i + 1).getRms_batch_no().equals(currentBatchNo);

                // only has 1 body or is first
                if ((isFirst && isLast) || (isFirst && !isLast)) {
                    try {
                        fmsDRMemoJson = new FMSDRMemoJson();
                        fmsDRMemoJson = this.generateStringBody(currentItem, isFirst, isLast, fmsDRMemoJson);
                        /*
                         * fmsDRMemoJson = this.generateStringBody(currentItem.getType(),
                         * currentItem.getLink_branch(),
                         * currentItem.getAmt(),
                         * currentItem.getCust(), currentItem.getRms_batch_no(),
                         * currentItem.getDt_sent(),
                         * currentItem.getDesc(), currentItem.getAttr_ext_sys(), currentItem.getAcct(),
                         * currentItem.getBranch(), currentItem.getSub_acct(),
                         * currentItem.getTxn_desc(),
                         * currentItem.getQty_drmemo(), currentItem.getUnit_price(),
                         * currentItem.getEnt_nm(),
                         * currentItem.getEnt_no(), currentItem.getEnt_ty(), currentItem.getCoa1(),
                         * currentItem.getCoa2(), currentItem.getFms_ref_no(), currentItem.getDoc_ty(),
                         * currentItem.getPg_pymt_amt(), isFirst, isLast, fmsDRMemoJson,
                         * currentItem.getDepositID(), currentItem.getDepositTask()); //241010: Added 2
                         * new parameters
                         */
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    // if it's last, call api
                    if (isFirst && isLast) {
                        ObjectMapper mapper = new ObjectMapper();
                        stringBody = mapper.writeValueAsString(fmsDRMemoJson);
                        result.addAll(this.fms_api_drmemo(stringBody, currentItem.getDrmemo_hid(), currentItem));

                        if(currentItem.getCrmemo_hid() != null){
                            fmsCRMemoService.sp_insfmsrcpgmtt_f(currentItem.getCrmemo_hid(), currentItem.getMtt_pg_id(), 1);
                        }
                    }
                }
                // middle or last
                else if ((!isFirst && !isLast) || (!isFirst && isLast)) {
                    try {
                        fmsDRMemoJson = this.generateStringBody(currentItem, isFirst, isLast, fmsDRMemoJson);
                        /*
                         * fmsDRMemoJson = this.generateStringBody(currentItem.getType(),
                         * currentItem.getLink_branch(),
                         * currentItem.getAmt(),
                         * currentItem.getCust(), currentItem.getRms_batch_no(),
                         * currentItem.getDt_sent(),
                         * currentItem.getDesc(), currentItem.getAttr_ext_sys(), currentItem.getAcct(),
                         * currentItem.getBranch(), currentItem.getSub_acct(),
                         * currentItem.getTxn_desc(),
                         * currentItem.getQty_drmemo(), currentItem.getUnit_price(),
                         * currentItem.getEnt_nm(),
                         * currentItem.getEnt_no(), currentItem.getEnt_ty(), currentItem.getCoa1(),
                         * currentItem.getCoa2(), currentItem.getFms_ref_no(), currentItem.getDoc_ty(),
                         * currentItem.getPg_pymt_amt(), isFirst, isLast, fmsDRMemoJson,
                         * currentItem.getDepositID(), currentItem.getDepositTask());
                         */
                        // 241010: Added 2 new parameters
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    // if it's last, call api
                    if (!isFirst && isLast) {
                        ObjectMapper mapper = new ObjectMapper();
                        stringBody = mapper.writeValueAsString(fmsDRMemoJson);
                        result.addAll(this.fms_api_drmemo(stringBody, currentItem.getDrmemo_hid(), currentItem));

                        if(currentItem.getCrmemo_hid() != null){
                            fmsCRMemoService.sp_insfmsrcpgmtt_f(currentItem.getCrmemo_hid(), currentItem.getMtt_pg_id(), 1);
                        }
                    }
                }
            }
        }
        // testing
        // else {
        // Date today = new Date();
        // stringBody = this.generateStringBody("", "0", 0.0, "", "", today,
        // "", "", "", "", "", "",
        // 0, 0.0, "", "0", "", "",
        // "", "", true, true, "");
        // BigInteger bigint = new BigInteger("1");
        // result.addAll(this.fms_api_drmemo(stringBody, bigint));
        // }
        return result;
    }

    public Integer newDrMemo(List<FMSDRMemo> memos) {
        BigInteger hid = sp_insfmsdrmemo_h(memos.get(0));
        if (hid.intValue() < 1) {
            log.error("Exception in " + this.getClass().toString()
                    + "newBillingDrmemo func - sp_insfmsdrmemo_h failed with code " + Integer.toString(hid.intValue())
                    + " !");
            return -1;
        }
        for (FMSDRMemo memo : memos) {
            if (memo.getDrmemo_hid() == null)
                memo.setDrmemo_hid(hid);

            Integer statusCode = sp_insfmsdrmemo_b(hid, memo, 0);
            if (statusCode < 1) {
                log.error("Exception in " + this.getClass().toString()
                        + "newBillingDrmemo func - sp_insfmsdrmemo_b failed with code " + Integer.toString(statusCode)
                        + " !");
                statusCode = sp_rollbackfmsdrmemohbh(hid);
                if (statusCode < 1)
                    log.error("Exception in " + this.getClass().toString()
                            + "newBillingDrmemo func - sp_rollbackfmsdrmemohbh @ sp_insfmsdrmemo_b failed with code "
                            + Integer.toString(statusCode) + " !");
                return -2;
            }
            statusCode = sp_insfmsdrmemo_f(hid, memo.getFms_ref_no(), memo.getDoc_ty());
            if (statusCode < 1) {
                log.error("Exception in " + this.getClass().toString()
                        + "newBillingDrmemo func - sp_insfmsdrmemo_f failed with code " + Integer.toString(statusCode)
                        + " !");
                statusCode = sp_rollbackfmsdrmemohbh(hid);
                if (statusCode < 1)
                    log.error("Exception in " + this.getClass().toString()
                            + "newBillingDrmemo func - sp_rollbackfmsdrmemohbh @ sp_insfmsdrmemo_f failed with code "
                            + Integer.toString(statusCode) + " !");
                return -3;
            }
        }
        return hid.intValue();
    }

    public Integer drMemoCallAPI(BigInteger hid) throws JsonProcessingException {
        List<FMSDRMemo> fmsDRMemo = sp_getfmsdrmemobyhid(hid);

        // stringbody n fms_api_drmemo
        String stringBody = "";
        FMSDRMemoJson fmsDRMemoJson = new FMSDRMemoJson();

        // if size > 0
        if (fmsDRMemo.size() > 0) {
            for (int i = 0; i < fmsDRMemo.size(); i++) {
                FMSDRMemo currentItem = fmsDRMemo.get(i);
                String currentBatchNo = currentItem.getRms_batch_no();

                boolean isFirst = (i == 0) || !fmsDRMemo.get(i - 1).getRms_batch_no().equals(currentBatchNo);
                boolean isLast = (i == fmsDRMemo.size() - 1)
                        || !fmsDRMemo.get(i + 1).getRms_batch_no().equals(currentBatchNo);

                // only has 1 body or is first
                if ((isFirst && isLast) || (isFirst && !isLast)) {
                    try {
                        fmsDRMemoJson = new FMSDRMemoJson();
                        fmsDRMemoJson = this.generateStringBody(currentItem, isFirst, isLast, fmsDRMemoJson);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    // if it's last, call api
                    if (isFirst && isLast) {
                        ObjectMapper mapper = new ObjectMapper();
                        stringBody = mapper.writeValueAsString(fmsDRMemoJson);
                        this.fms_api_drmemo(stringBody, currentItem.getDrmemo_hid(), currentItem);
                    }
                }
                // middle or last
                else if ((!isFirst && !isLast) || (!isFirst && isLast)) {
                    try {
                        fmsDRMemoJson = this.generateStringBody(currentItem, isFirst, isLast, fmsDRMemoJson);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    if (!isFirst && isLast) {
                        ObjectMapper mapper = new ObjectMapper();
                        stringBody = mapper.writeValueAsString(fmsDRMemoJson);
                        this.fms_api_drmemo(stringBody, currentItem.getDrmemo_hid(), currentItem);
                    }
                }
            }
        }
        return 1;
    }

    public Integer sp_rollbackfmsdrmemohbh(BigInteger hid) {
        return fmsDRMemoRepo.sp_rollbackfmsdrmemohbh(hid);
    }


    private boolean hasNonNullSimpleDetailValues(FMSDRMemo memo) {
        return memo.getSub_acct() != null ||
                memo.getCoa1() != null ||
                memo.getUnit_price() != null;
        // Note: qty is always set to 1, so we don't check it
    }

    private boolean hasNonNullFullDetailValues(FMSDRMemo memo) {
        return memo.getAcct() != null ||
                memo.getBranch() != null ||
                memo.getCoa1() != null ||
                memo.getCoa2() != null ||
                memo.getSub_acct() != null ||
                memo.getTxn_desc() != null ||
                memo.getQty_drmemo() != null ||
                memo.getUnit_price() != null ||
                memo.getEnt_nm() != null ||
                memo.getEnt_no() != null ||
                memo.getEnt_ty() != null ||
                memo.getDepositID() != null ||
                memo.getDepositTask() != null ||
                memo.getRcpt_no() != null ||
                memo.getCust_nm() != null ||
                memo.getPg_pymt_method() != null ||
                memo.getQty() != 0 ||
                memo.getTax_amt() != null ||
                memo.getDiscount_amt() != null;
    }
}
