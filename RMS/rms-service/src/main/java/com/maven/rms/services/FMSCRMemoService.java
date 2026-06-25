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
import com.maven.rms.models.FMSCRMemoJson;
import com.maven.rms.models.FMSDRMemo;
import com.maven.rms.models.GenericValue;
import com.maven.rms.repositories.BillingRepository;
import com.maven.rms.repositories.FMSCRMemoRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FMSCRMemoService {

    private final FMSCRMemoRepository fmsCRMemoRepo;
    private final BillingRepository bRepo;

    @Value("${fmscrmemo.api.url}")
    private String api_url;

    @Value("${fmscrmemo.api.name}")
    private String api_name;

    @Value("${fms.ibm-client-id}")
    private String IBMClientID;

    @Autowired
    private FMSDRMemoService fmsDRMemoService;

    @Autowired
    private CommonService commonSvc;

    public FMSCRMemoService(FMSCRMemoRepository fmsCRMemoRepo, BillingRepository bRepo) {
        this.fmsCRMemoRepo = fmsCRMemoRepo;
        this.bRepo = bRepo;
    }

    // public List<FMSCRMemo> sp_getfmsrcpgmtt() {
    // List<FMSCRMemo> result = new ArrayList<>();

    // try {
    // List<Object[]> objects = fmsCRMemoRepo.sp_getfmsrcpgmtt();
    // List<FMSCRMemo> fmsCRMemoList = new ArrayList<>();
    // for (Object[] obj : objects) {
    // FMSCRMemo fmsCRMemo = new FMSCRMemo();
    // fmsCRMemo.setRc_pg_id((BigInteger) obj[0]);
    // fmsCRMemo.setPg_pymt_method((String) obj[1]);
    // fmsCRMemo.setMtt_pg_id((BigInteger) obj[2]);
    // fmsCRMemo.setQty((int) obj[3]);
    // fmsCRMemo.setItem_desc((String) obj[4]);
    // fmsCRMemo.setUnit_fee((BigDecimal) obj[5]);
    // fmsCRMemo.setEnt_nm((String) obj[6]);
    // fmsCRMemo.setEnt_no((String) obj[7]);
    // fmsCRMemo.setEnt_ty((String) obj[8]);
    // fmsCRMemo.setGross_amt((BigDecimal) obj[9]);
    // fmsCRMemo.setFee_detail_id((String) obj[10]);
    // fmsCRMemo.setTax_amt((BigDecimal) obj[11]);
    // fmsCRMemo.setRcpt_no((String) obj[12]);
    // fmsCRMemo.setCust_nm((String) obj[13]);
    // fmsCRMemo.setPg_pymt_amt((BigDecimal) obj[14]);
    // fmsCRMemoList.add(fmsCRMemo);
    // }
    // result = fmsCRMemoList;
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    public List<FMSCRMemo> sp_getfmsrcpgmtt() {
        List<FMSCRMemo> result = new ArrayList<>();
        List<Object[]> objects = fmsCRMemoRepo.sp_getfmsrcpgmtt();
        List<FMSCRMemo> fmsCRMemoList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSCRMemo fmsCRMemo = new FMSCRMemo();
            fmsCRMemo.setRc_pg_id((BigInteger) obj[0]);
            fmsCRMemo.setPg_pymt_method((String) obj[1]);
            fmsCRMemo.setMtt_pg_id((BigInteger) obj[2]);
            fmsCRMemo.setQty((int) obj[3]);
            fmsCRMemo.setItem_desc((String) obj[4]);
            fmsCRMemo.setUnit_fee((BigDecimal) obj[5]);
            fmsCRMemo.setEnt_nm((String) obj[6]);
            fmsCRMemo.setEnt_no((String) obj[7]);
            fmsCRMemo.setEnt_ty((String) obj[8]);
            fmsCRMemo.setGross_amt((BigDecimal) obj[9]);
            fmsCRMemo.setFee_detail_id((String) obj[10]);
            fmsCRMemo.setTax_amt((BigDecimal) obj[11]);
            fmsCRMemo.setDepositID((String) obj[12]);
            fmsCRMemo.setDepositTask((String) obj[13]);
            fmsCRMemo.setRcpt_no((String) obj[14]);
            fmsCRMemo.setCust_nm((String) obj[15]);
            fmsCRMemo.setPg_pymt_amt((BigDecimal) obj[16]);
            fmsCRMemo.setRc_pgmtt_id((BigInteger) obj[17]);
            fmsCRMemoList.add(fmsCRMemo);
        }
        result = fmsCRMemoList;

        return result;
    }

    public List<FMSCRMemo> sp_getfmscreditdebit() {
        List<FMSCRMemo> result = new ArrayList<>();
        List<Object[]> objects = fmsCRMemoRepo.sp_getfmscreditdebit();

        for (Object[] obj : objects) {
            FMSCRMemo fmsCRMemo = new FMSCRMemo();
            fmsCRMemo.setDrmemo_hid((Integer) obj[0]); // drmemo_hid
            fmsCRMemo.setType((String) obj[1]); // type
            fmsCRMemo.setLink_branch((String) obj[2]); // link_branch
            fmsCRMemo.setAmt((BigDecimal) obj[3]); // amt
            fmsCRMemo.setDesc((String) obj[4]); // desc
            fmsCRMemo.setAttr_ext_sys((String) obj[5]); // attr_ext_sys
            fmsCRMemo.setFms_ref_no((String) obj[6]); // fms_ref_no
            fmsCRMemo.setGenPdf((Integer) obj[7]); // genpdf
            fmsCRMemo.setMtt_pg_id((BigInteger) obj[8]); // mtt_pg_id
            fmsCRMemo.setQty((Integer) obj[9]); // qty
            fmsCRMemo.setTxn_desc((String) obj[10]); // txn_desc
            fmsCRMemo.setUnit_fee((BigDecimal) obj[11]); // unit_price
            fmsCRMemo.setRcpt_no((String) obj[12]); // rcpt_no
            fmsCRMemo.setPayee_info((String) obj[13]); // payee_info
            fmsCRMemo.setEnt_nm((String) obj[14]); // ent_nm
            fmsCRMemo.setEnt_no((String) obj[15]); // ent_no
            fmsCRMemo.setEnt_ty((String) obj[16]); // ent_ty
            fmsCRMemo.setGross_amt((BigDecimal) obj[17]); // item_amt
            fmsCRMemo.setPymt((String) obj[18]); // pymt
            fmsCRMemo.setTax_amt((BigDecimal) obj[19]); // item_tax_amt
            fmsCRMemo.setCoa1((String) obj[20]); // coa1
            fmsCRMemo.setCoa2((String) obj[21]); // coa2
            fmsCRMemo.setDepositID((String) obj[22]); // dps_id
            fmsCRMemo.setDepositTask((String) obj[23]); // dps_task
            fmsCRMemo.setBil_child_id((Integer) obj[24]); // bil_child_id
            fmsCRMemo.setAcct((String) obj[25]); // acct
            fmsCRMemo.setBranch((String) obj[26]); // branch
            fmsCRMemo.setSub_acct((String) obj[27]); // sub_acct
            fmsCRMemo.setDoc_ty((String) obj[28]); // doc_ty
            fmsCRMemo.setAcct_cd((String) obj[29]); // acct_cd
            fmsCRMemo.setCust((String) obj[30]); // cust
            result.add(fmsCRMemo);
        }

        return result;
    }

    public List<FMSCRMemo> sp_getfmscnbr() {
        List<FMSCRMemo> result = new ArrayList<>();
        List<Object[]> objects = fmsCRMemoRepo.sp_getfmscnbr();
        List<FMSCRMemo> fmsCRMemoList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSCRMemo fmsCRMemo = new FMSCRMemo();
            fmsCRMemo.setRc_pg_id((BigInteger) obj[0]);
            fmsCRMemo.setPg_pymt_method((String) obj[1]);
            fmsCRMemo.setMtt_pg_id((BigInteger) obj[2]);
            fmsCRMemo.setQty((int) obj[3]);
            fmsCRMemo.setItem_desc((String) obj[4]);
            fmsCRMemo.setUnit_fee((BigDecimal) obj[5]);
            fmsCRMemo.setEnt_nm((String) obj[6]);
            fmsCRMemo.setEnt_no((String) obj[7]);
            fmsCRMemo.setEnt_ty((String) obj[8]);
            fmsCRMemo.setGross_amt((BigDecimal) obj[9]);
            fmsCRMemo.setFee_detail_id((String) obj[10]);
            fmsCRMemo.setTax_amt((BigDecimal) obj[11]);
            fmsCRMemo.setDepositID((String) obj[12]);
            fmsCRMemo.setDepositTask((String) obj[13]);
            fmsCRMemo.setRcpt_no((String) obj[14]);
            fmsCRMemo.setCust_nm((String) obj[15]);
            fmsCRMemo.setPg_pymt_amt((BigDecimal) obj[16]);
            fmsCRMemo.setRc_pgmtt_id((BigInteger) obj[17]);
            fmsCRMemoList.add(fmsCRMemo);
        }
        result = fmsCRMemoList;

        return result;
    }

    // public BigInteger sp_insfmsrcpgmtt_h(BigDecimal pg_pymt_amt) {
    // BigInteger result = null;

    // try {
    // result = fmsCRMemoRepo.sp_insfmsrcpgmtt_h(pg_pymt_amt);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    public BigInteger sp_insfmsrcpgmtt_h(BigDecimal pg_pymt_amt, Integer flag) {
        BigInteger result = null;

        result = fmsCRMemoRepo.sp_insfmsrcpgmtt_h(pg_pymt_amt, flag);

        return result;
    }

    public BigInteger sp_insfmscrmemo_h(FMSCRMemo fmscrMemo) {
        return fmsCRMemoRepo.sp_insfmscrmemo_h(fmscrMemo);
    }

    public BigInteger sp_insfmscrmemo_h2(FMSCRMemo fmscrMemo) {
        return fmsCRMemoRepo.sp_insfmscrmemo_h2(fmscrMemo);
    }

    // public Integer sp_insfmsrcpgmtt_b(BigInteger crmemo_hid, String
    // pg_pymt_method, BigInteger mtt_pg_id, int qty,
    // String item_desc, BigDecimal unit_fee, String entity_nm, String entity_no,
    // String entity_type, BigDecimal gross_amt,
    // String fee_detail_id, BigDecimal tax_amt, String rcpt_no, String cust_nm) {
    // Integer result = null;

    // try {
    // result = fmsCRMemoRepo.sp_insfmsrcpgmtt_b(crmemo_hid, pg_pymt_method,
    // mtt_pg_id, qty, item_desc, unit_fee, entity_nm, entity_no, entity_type,
    // gross_amt, fee_detail_id, tax_amt, rcpt_no, cust_nm);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    // public Integer sp_insfmsrcpgmtt_b(BigInteger crmemo_hid, FMSCRMemo fmscrMemo)
    // {
    // Integer result = null;

    // try {
    // result = fmsCRMemoRepo.sp_insfmsrcpgmtt_b(crmemo_hid, fmscrMemo);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    public Integer sp_insfmsrcpgmtt_b(BigInteger crmemo_hid, FMSCRMemo fmscrMemo, Integer flag) {
        Integer result = null;
        result = fmsCRMemoRepo.sp_insfmsrcpgmtt_b(crmemo_hid, fmscrMemo, flag);

        return result;
    }

    public Integer sp_insfmsrcpgmtt_b(BigInteger crmemo_hid, FMSDRMemo fmsdrMemo, Integer flag) {
        Integer result = null;
        result = fmsCRMemoRepo.sp_insfmsrcpgmtt_b(crmemo_hid, fmsdrMemo, flag);

        return result;
    }

    public Integer sp_insfmscrmemo_b(BigInteger crmemo_hid, FMSCRMemo fmscrMemo, Integer flag) {
        Integer result = null;
        result = fmsCRMemoRepo.sp_insfmscrmemo_b(crmemo_hid, fmscrMemo, flag);

        return result;
    }

    // public Integer sp_insfmsrcpgmtt_f(BigInteger crmemo_hid, BigInteger
    // mtt_pg_id) {
    // Integer result = null;

    // try {
    // result = fmsCRMemoRepo.sp_insfmsrcpgmtt_f(crmemo_hid, mtt_pg_id);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    public Integer sp_insfmsrcpgmtt_f(BigInteger crmemo_hid, BigInteger mtt_pg_id, Integer i_is_drmemo) {
        Integer result = null;
        result = fmsCRMemoRepo.sp_insfmsrcpgmtt_f(crmemo_hid, mtt_pg_id, i_is_drmemo);
        return result;
    }

    public Integer sp_insfmscrmemo_f(BigInteger crmemo_hid, String fms_ref_no, String doc_type) {
        Integer result = null;
        result = fmsCRMemoRepo.sp_insfmscrmemo_f(crmemo_hid, fms_ref_no, doc_type);
        return result;
    }

    // public List<FMSCRMemo> sp_getfmscrmemo() {
    // List<FMSCRMemo> result = new ArrayList<>();

    // try {
    // List<Object[]> objects = fmsCRMemoRepo.sp_getfmscrmemo();
    // List<FMSCRMemo> fmsCRMemoList = new ArrayList<>();
    // for (Object[] obj : objects) {
    // FMSCRMemo fmsCRMemo = new FMSCRMemo();
    // fmsCRMemo.setCrmemo_hid((BigInteger) obj[0]);
    // fmsCRMemo.setType((String) obj[1]);
    // fmsCRMemo.setLink_branch((String) obj[2]);
    // fmsCRMemo.setAmt((BigDecimal) obj[3]);
    // fmsCRMemo.setCust((String) obj[4]);
    // fmsCRMemo.setRms_batch_no((String) obj[5]);
    // fmsCRMemo.setDt_sent((Date) obj[6]);
    // fmsCRMemo.setDesc((String) obj[7]);
    // fmsCRMemo.setAttr_ext_sys((String) obj[8]);
    // fmsCRMemo.setFms_ref_no((String) obj[9]);
    // fmsCRMemo.setDoc_ty((String) obj[10]);
    // fmsCRMemo.setAcct((String) obj[11]);
    // fmsCRMemo.setBranch((String) obj[12]);
    // fmsCRMemo.setQty_crmemo((int) obj[13]);
    // fmsCRMemo.setSub_acct((String) obj[14]);
    // fmsCRMemo.setTxn_desc((String) obj[15]);
    // fmsCRMemo.setUnit_price((BigDecimal) obj[16]);
    // // fmsCRMemo.setRcpt_no_crmemo((String) obj[17]);
    // // fmsCRMemo.setPayee_info((String) obj[18]);
    // fmsCRMemo.setEnt_nm((String) obj[17]);
    // fmsCRMemo.setEnt_no((String) obj[18]);
    // fmsCRMemo.setEnt_ty((String) obj[19]);
    // // fmsCRMemo.setItem_amt((BigDecimal) obj[22]);
    // // fmsCRMemo.setPymt((BigDecimal) obj[23]);
    // // fmsCRMemo.setItem_tax_amt((BigDecimal) obj[24]);
    // fmsCRMemo.setCoa1((String) obj[20]);
    // fmsCRMemo.setCoa2((String) obj[21]);
    // fmsCRMemoList.add(fmsCRMemo);
    // }
    // result = fmsCRMemoList;
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    public List<FMSCRMemo> sp_getfmscrmemo() {
        List<FMSCRMemo> result = new ArrayList<>();
        List<Object[]> objects = fmsCRMemoRepo.sp_getfmscrmemo();
        List<FMSCRMemo> fmsCRMemoList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSCRMemo fmsCRMemo = new FMSCRMemo();
            fmsCRMemo.setCrmemo_hid((BigInteger) obj[0]);
            fmsCRMemo.setType((String) obj[1]);
            fmsCRMemo.setLink_branch((String) obj[2]);
            fmsCRMemo.setAmt((BigDecimal) obj[3]);
            fmsCRMemo.setCust((String) obj[4]);
            fmsCRMemo.setRms_batch_no((String) obj[5]);
            fmsCRMemo.setDt_sent((Date) obj[6]);
            fmsCRMemo.setDesc((String) obj[7]);
            fmsCRMemo.setAttr_ext_sys((String) obj[8]);
            fmsCRMemo.setFms_ref_no((String) obj[9]);
            fmsCRMemo.setDoc_ty((String) obj[10]);
            fmsCRMemo.setAcct((String) obj[11]);
            fmsCRMemo.setBranch((String) obj[12]);
            fmsCRMemo.setQty_crmemo((Integer) obj[13]);
            fmsCRMemo.setSub_acct((String) obj[14]);
            fmsCRMemo.setTxn_desc((String) obj[15]);
            fmsCRMemo.setUnit_price((BigDecimal) obj[16]);
            // fmsCRMemo.setRcpt_no_crmemo((String) obj[17]);
            // fmsCRMemo.setPayee_info((String) obj[18]);
            fmsCRMemo.setEnt_nm((String) obj[17]);
            fmsCRMemo.setEnt_no((String) obj[18]);
            fmsCRMemo.setEnt_ty((String) obj[19]);
            // fmsCRMemo.setItem_amt((BigDecimal) obj[22]);
            // fmsCRMemo.setPymt((BigDecimal) obj[23]);
            // fmsCRMemo.setItem_tax_amt((BigDecimal) obj[24]);
            fmsCRMemo.setCoa1((String) obj[20]);
            fmsCRMemo.setCoa2((String) obj[21]);
            fmsCRMemo.setDepositID((String) obj[22]);
            fmsCRMemo.setDepositTask((String) obj[23]);
            fmsCRMemo.setGenPdf((Integer) obj[24]);
            fmsCRMemo.setBil_child_id((Integer) obj[25]);
            fmsCRMemoList.add(fmsCRMemo);
        }
        result = fmsCRMemoList;

        return result;
    }

    public List<FMSCRMemo> sp_getfmscrmemobyhid(BigInteger hid) {
        List<Object[]> objects = fmsCRMemoRepo.sp_getfmscrmemobyhid(hid);
        List<FMSCRMemo> fmsCRMemoList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSCRMemo fmsCRMemo = new FMSCRMemo();
            fmsCRMemo.setCrmemo_hid((BigInteger) obj[0]);
            fmsCRMemo.setType((String) obj[1]);
            fmsCRMemo.setLink_branch((String) obj[2]);
            fmsCRMemo.setAmt((BigDecimal) obj[3]);
            fmsCRMemo.setCust((String) obj[4]);
            fmsCRMemo.setRms_batch_no((String) obj[5]);
            fmsCRMemo.setDt_sent((Date) obj[6]);
            fmsCRMemo.setDesc((String) obj[7]);
            fmsCRMemo.setAttr_ext_sys((String) obj[8]);
            fmsCRMemo.setFms_ref_no((String) obj[9]);
            fmsCRMemo.setDoc_ty((String) obj[10]);
            fmsCRMemo.setAcct((String) obj[11]);
            fmsCRMemo.setBranch((String) obj[12]);
            fmsCRMemo.setQty_crmemo((int) obj[13]);
            fmsCRMemo.setSub_acct((String) obj[14]);
            fmsCRMemo.setTxn_desc((String) obj[15]);
            fmsCRMemo.setUnit_price((BigDecimal) obj[16]);
            // fmsCRMemo.setRcpt_no_crmemo((String) obj[17]);
            // fmsCRMemo.setPayee_info((String) obj[18]);
            fmsCRMemo.setEnt_nm((String) obj[17]);
            fmsCRMemo.setEnt_no((String) obj[18]);
            fmsCRMemo.setEnt_ty((String) obj[19]);
            // fmsCRMemo.setItem_amt((BigDecimal) obj[22]);
            // fmsCRMemo.setPymt((BigDecimal) obj[23]);
            // fmsCRMemo.setItem_tax_amt((BigDecimal) obj[24]);
            fmsCRMemo.setCoa1((String) obj[20]);
            fmsCRMemo.setCoa2((String) obj[21]);
            fmsCRMemo.setDepositID((String) obj[22]);
            fmsCRMemo.setDepositTask((String) obj[23]);
            fmsCRMemo.setGenPdf((Integer) obj[24]);
            fmsCRMemo.setBil_child_id((Integer) obj[25]);
            fmsCRMemoList.add(fmsCRMemo);
        }
        return fmsCRMemoList;
    }

    public FMSCRMemo sp_getfmscrmemobyarifmsrefno(String ari_fms_ref_no) {
        Object[] obj = fmsCRMemoRepo.sp_getfmscrmemobyarifmsrefno(ari_fms_ref_no);

        FMSCRMemo fmsCRMemo = new FMSCRMemo();
        fmsCRMemo.setCrmemo_hid((BigInteger) obj[0]);
        fmsCRMemo.setType((String) obj[1]);
        fmsCRMemo.setLink_branch((String) obj[2]);
        fmsCRMemo.setAmt((BigDecimal) obj[3]);
        fmsCRMemo.setCust((String) obj[4]);
        fmsCRMemo.setRms_batch_no((String) obj[5]);
        fmsCRMemo.setDt_sent((Date) obj[6]);
        fmsCRMemo.setDesc((String) obj[7]);
        fmsCRMemo.setAttr_ext_sys((String) obj[8]);
        fmsCRMemo.setFms_ref_no((String) obj[9]);
        fmsCRMemo.setDoc_ty((String) obj[10]);
        fmsCRMemo.setAcct((String) obj[11]);
        fmsCRMemo.setBranch((String) obj[12]);
        fmsCRMemo.setQty_crmemo((int) obj[13]);
        fmsCRMemo.setSub_acct((String) obj[14]);
        fmsCRMemo.setTxn_desc((String) obj[15]);
        fmsCRMemo.setUnit_price((BigDecimal) obj[16]);
        fmsCRMemo.setEnt_nm((String) obj[17]);
        fmsCRMemo.setEnt_no((String) obj[18]);
        fmsCRMemo.setEnt_ty((String) obj[19]);
        fmsCRMemo.setCoa1((String) obj[20]);
        fmsCRMemo.setCoa2((String) obj[21]);
        fmsCRMemo.setDepositID((String) obj[22]);
        fmsCRMemo.setDepositTask((String) obj[23]);
        fmsCRMemo.setGenPdf((Integer) obj[24]);
        fmsCRMemo.setBil_child_id((Integer) obj[25]);

        return fmsCRMemo;
    }

    // public Integer sp_updfmscrmemo(BigInteger crmemo_hid, String
    // resp_attr_ext_sys, String fms_ref_no, String resp_co,
    // String resp_status, String resp_msg, Date resp_dt) {
    // Integer result = null;

    // try {
    // result = fmsCRMemoRepo.sp_updfmscrmemo(crmemo_hid, resp_attr_ext_sys,
    // fms_ref_no, resp_co, resp_status,
    // resp_msg, resp_dt);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    // public Integer sp_updfmscrmemo(BigInteger crmemo_hid, FMSCRMemo fmscrMemo) {
    // Integer result = null;

    // try {
    // result = fmsCRMemoRepo.sp_updfmscrmemo(crmemo_hid, fmscrMemo);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }

    // return result;
    // }

    public Integer sp_updfmscrmemo(BigInteger crmemo_hid, FMSCRMemo fmscrMemo) {
        Integer result = null;
        result = fmsCRMemoRepo.sp_updfmscrmemo(crmemo_hid, fmscrMemo);

        return result;
    }

    public FMSCRMemoJson generateStringBody(FMSCRMemo memo, boolean isFirst, boolean isLast, FMSCRMemoJson inputObject)
            throws JsonProcessingException {
        /*
         * String type, String linkBranch, BigDecimal amt, String cust,
         * String rmsBatchNo,
         * Date dtSent, String desc, String attrExtSys, String acct, String branch,
         * String subAcct, String txnDesc, int qtyCrmemo, BigDecimal unitPrice, String
         * entNm,
         * String entNo, String entTy, String coa1, String coa2, String fmsRefNo, String
         * docTy,
         * boolean isFirst, boolean isLast, FMSCRMemoJson inputObject,
         * String depositID, String depositTask) throws JsonProcessingException {
         */
        // Added 2 new parameters, depositID and depositTask

        if (isFirst) {
            // Initialize the FMSCRMemoJson object with general information
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

            // Setting custom attributes
            FMSCRMemoJson.Custom custom = new FMSCRMemoJson.Custom();
            FMSCRMemoJson.CurrentDocument currentDocument = new FMSCRMemoJson.CurrentDocument();
            FMSCRMemoJson.Attribute attribute = new FMSCRMemoJson.Attribute();
            FMSCRMemoJson.Attribute2 attribute2 = new FMSCRMemoJson.Attribute2();
            attribute.setValue(memo.getAttr_ext_sys());
            attribute2.setValue(memo.getGenPdf() == null || memo.getGenPdf() == 0 ? false : true);
            currentDocument.setAttribute(attribute);
            currentDocument.setAttribute2(attribute2);
            custom.setCurrentDocument(currentDocument);
            inputObject.setCustom(custom);

            // Initialize details and credit memo lists
            inputObject.setDetails(new ArrayList<>());

            List<FMSCRMemoJson.CreditMemo> creditMemos = new ArrayList<>();
            FMSCRMemoJson.CreditMemo creditMemo = new FMSCRMemoJson.CreditMemo();
            creditMemo.setReferenceNbr(new GenericValue<>(memo.getFms_ref_no()));
            creditMemo.setAmountPaid(new GenericValue<>(memo.getAmt()));
            creditMemo.setDocType(new GenericValue<>(memo.getDoc_ty()));
            creditMemos.add(creditMemo);
            inputObject.setApplicationsCreditMemo(creditMemos);
        }

        // Adding detail to the list
        if (hasNonNullDetailValues(memo)) {
        List<FMSCRMemoJson.Detail> details = inputObject.getDetails();
        FMSCRMemoJson.Detail detail = new FMSCRMemoJson.Detail();
        detail.setAccount(new GenericValue<>(memo.getAcct()));
        detail.setLineNbr(new GenericValue<>(String.valueOf(details.size() + 1)));
        detail.setBranch(new GenericValue<>(memo.getBranch()));
        detail.setChartOfAccount1(new GenericValue<>(memo.getCoa1()));
        detail.setChartOfAccount2(new GenericValue<>(memo.getCoa2()));
        detail.setSubaccount(new GenericValue<>(memo.getSub_acct()));
        detail.setTransactionDescription(new GenericValue<>(memo.getTxn_desc()));
        detail.setQty(new GenericValue<>(memo.getQty_crmemo()));
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
                new GenericValue<>(memo.getDiscount_amt() != null ? memo.getDiscount_amt() : new BigDecimal(0)));

        details.add(detail);
        }

        // If this is the last detail, finalize the inputObject (if needed)
        // if (isLast) {
        //     inputObject.setDetails(details);
        // }

        if (isLast) {
            List<FMSCRMemoJson.Detail> details = inputObject.getDetails();

            // If no valid details were added, set Details to null to exclude it from JSON
            if (details.isEmpty()) {
                inputObject.setDetails(null);
            } else {
                inputObject.setDetails(details);
            }
        }

        return inputObject;
    }

    @SuppressWarnings("deprecation")
    public List<FMSCRMemo> fms_api_crmemo(String stringBody, BigInteger crmemo_hid, FMSCRMemo memo) {

        List<FMSCRMemo> result = new ArrayList<>();
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
                extAudit.setI_module_nm("FMSCreditMemo");
                extAudit.setI_request_body(stringBody);
                extAudit.setI_response_body(response.toString());
                extAudit.setI_rms_batch_no(memo.getRms_batch_no());
                extAudit.setI_direction("Outgoing");
                extAudit.setI_remark(null);
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for FMS Credit Memo: " + e.getMessage() + ", "
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

            FMSCRMemo fmscrmemo = new FMSCRMemo();

            if (memo.getGenPdf() != null && memo.getGenPdf() > 0) {
                fmscrmemo.setFms_ref_no(jsonResponse.has("ReferenceNbr")
                        ? jsonResponse.get("ReferenceNbr").getAsJsonObject().get("value").getAsString()
                        : null);
                fmscrmemo.setResp_co(jsonResponse.has("CustomerOrder")
                        ? jsonResponse.get("CustomerOrder").getAsJsonObject().get("value").getAsString()
                        : null);
                fmscrmemo.setResp_status("200");
                if (jsonResponse.has("Date")) {
                    String dateString = jsonResponse.get("Date").getAsJsonObject().get("value").getAsString()
                            .substring(0, 19).replace("T", " ");
                    // fmscrmemo.setResp_dt(dateString);
                    try {
                        Date date = dateFormat.parse(dateString);
                        fmscrmemo.setResp_dt(date);
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

                fmscrmemo.setResp_attr_ext_sys(
                        jsonResponse.has("AttributeEXTSYSTEM") ? jsonResponse.get("AttributeEXTSYSTEM").getAsString()
                                : null);
                fmscrmemo.setFms_ref_no(
                        jsonResponse.has("ReferenceNbr") ? jsonResponse.get("ReferenceNbr").getAsString() : null);
                fmscrmemo.setResp_co(
                        jsonResponse.has("CustomerOrder") ? jsonResponse.get("CustomerOrder").getAsString() : null);
                fmscrmemo.setResp_status(jsonResponse.has("Status") ? jsonResponse.get("Status").getAsString() : null);
                fmscrmemo.setResp_msg(jsonResponse.has("Message") ? jsonResponse.get("Message").getAsString() : null);
                // Parse the date string to Date object
                if (jsonResponse.has("Date")) {
                    String dateString = jsonResponse.get("Date").getAsString();
                    // fmscrmemo.setResp_dt(dateString);
                    try {
                        Date date = dateFormat.parse(dateString);
                        fmscrmemo.setResp_dt(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
            result.add(fmscrmemo);

            this.sp_updfmscrmemo(crmemo_hid, fmscrmemo);

            // this.sp_updfmscrmemo(crmemo_hid,
            // fmscrmemo.getResp_attr_ext_sys(),
            // fmscrmemo.getFms_ref_no(),
            // fmscrmemo.getResp_co(),
            // fmscrmemo.getResp_status(),
            // fmscrmemo.getResp_msg(),
            // fmscrmemo.getResp_dt());

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public List<FMSCRMemo> fms_crmemo_sch() throws JsonProcessingException {
        List<FMSCRMemo> result = new ArrayList<>();
        BigInteger crmemo_hid = null;
        BigInteger drmemo_hid = null;

        // getfmsrcpg // for pgrecon
        List<FMSCRMemo> fmsRCPG = this.sp_getfmsrcpgmtt();

        // loop n insfmsrcpg
        try {
            while(CollectionUtils.size(fmsRCPG) > 0) {
            for (int i = 0; i < fmsRCPG.size(); i++) {
                FMSCRMemo item = fmsRCPG.get(i);

                BigInteger current_rc_pg_mtt_id = fmsRCPG.get(i).getRc_pgmtt_id();
                Integer flag = 0;

                // If this is not the last record, compare with the next record's mtt_pg_id
                if (i < CollectionUtils.size(fmsRCPG) - 1) {
                    BigInteger next_rc_pg_mtt_id = fmsRCPG.get(i + 1).getRc_pgmtt_id();
                    if (!current_rc_pg_mtt_id.equals(next_rc_pg_mtt_id)) {
                        flag = 1; // Set the flag if current mtt_pg_id is different from the next one
                    }
                } else {
                    // If it's the last record, set flag to 1 (assuming there's no next record)
                    flag = 1;
                }

                if (i == 0) {
                    crmemo_hid = this.sp_insfmsrcpgmtt_h(item.getPg_pymt_amt(), 0); 
                    drmemo_hid = fmsDRMemoService.sp_insfmsrcpgtxn_h(item.getPg_pymt_amt(), 1);
                    // this.sp_insfmsrcpgmtt_b(crmemo_hid,
                    // item.getPg_pymt_method(),
                    // item.getMtt_pg_id(),
                    // item.getQty(),
                    // item.getItem_desc(),
                    // item.getUnit_fee(),
                    // item.getEnt_nm(),
                    // item.getEnt_no(),
                    // item.getEnt_ty(),
                    // item.getGross_amt(),
                    // item.getFee_detail_id(),
                    // item.getTax_amt(),
                    // item.getRcpt_no(),
                    // item.getCust_nm());

                    this.sp_insfmsrcpgmtt_b(crmemo_hid, item, flag);
                    fmsDRMemoService.sp_insfmsrcpgtxn_b(drmemo_hid, item, flag);

                } else if (i > 0) {
                    // this.sp_insfmsrcpgmtt_b(crmemo_hid,
                    // item.getPg_pymt_method(),
                    // item.getMtt_pg_id(),
                    // item.getQty(),
                    // item.getItem_desc(),
                    // item.getUnit_fee(),
                    // item.getEnt_nm(),
                    // item.getEnt_no(),
                    // item.getEnt_ty(),
                    // item.getGross_amt(),
                    // item.getFee_detail_id(),
                    // item.getTax_amt(),
                    // item.getRcpt_no(),
                    // item.getCust_nm());
                    this.sp_insfmsrcpgmtt_b(crmemo_hid, item, flag);
                    fmsDRMemoService.sp_insfmsrcpgtxn_b(drmemo_hid, item, flag);
                }

                if (i == fmsRCPG.size() - 1) {
                    this.sp_insfmsrcpgmtt_f(crmemo_hid,
                            item.getMtt_pg_id(), 0);

                    fmsDRMemoService.sp_insfmsrcpgtxn_f(drmemo_hid, item);
                }
            }
            fmsRCPG = this.sp_getfmsrcpgmtt();
            }
        } catch (Exception e) {
            log.error("Exception in fms_crmemo_sch() loop: {}", e.getMessage(), e);
            e.printStackTrace();
        }

        // getfmscrmemo
        List<FMSCRMemo> fmsCRMemo = this.sp_getfmscrmemo();

        // stringbody n fms_api_crmemo
        String stringBody = "";
        FMSCRMemoJson fmsCRMemoJson = new FMSCRMemoJson();

        // if size > 0
        if (fmsCRMemo.size() > 0) {
            for (int i = 0; i < fmsCRMemo.size(); i++) {
                FMSCRMemo currentItem = fmsCRMemo.get(i);
                String currentBatchNo = currentItem.getRms_batch_no();

                boolean isFirst = (i == 0) || !fmsCRMemo.get(i - 1).getRms_batch_no().equals(currentBatchNo);
                boolean isLast = (i == fmsCRMemo.size() - 1)
                        || !fmsCRMemo.get(i + 1).getRms_batch_no().equals(currentBatchNo);

                // only has 1 body or is first
                if ((isFirst && isLast) || (isFirst && !isLast)) {
                    try {
                        fmsCRMemoJson = new FMSCRMemoJson();
                        fmsCRMemoJson = this.generateStringBody(currentItem, isFirst, isLast, fmsCRMemoJson);

                        /*
                         * currentItem.getType(), currentItem.getLink_branch(),
                         * currentItem.getAmt(),
                         * currentItem.getCust(), currentItem.getRms_batch_no(),
                         * currentItem.getDt_sent(),
                         * currentItem.getDesc(), currentItem.getAttr_ext_sys(), currentItem.getAcct(),
                         * currentItem.getBranch(), currentItem.getSub_acct(),
                         * currentItem.getTxn_desc(),
                         * currentItem.getQty_crmemo(), currentItem.getUnit_price(),
                         * currentItem.getEnt_nm(),
                         * currentItem.getEnt_no(), currentItem.getEnt_ty(), currentItem.getCoa1(),
                         * currentItem.getCoa2(), currentItem.getFms_ref_no(), currentItem.getDoc_ty(),
                         * isFirst, isLast, fmsCRMemoJson,
                         * currentItem.getDepositID(), currentItem.getDepositTask());
                         */
                        // 241010: Added 2 new parameters, depositID and depositTask
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                        e.printStackTrace();
                    }
                    // if it's last, call api
                    if (isFirst && isLast) {
                        ObjectMapper mapper = new ObjectMapper();
                        stringBody = mapper.writeValueAsString(fmsCRMemoJson);
                        result.addAll(this.fms_api_crmemo(stringBody, currentItem.getCrmemo_hid(), currentItem));
                    }
                }
                // middle or last
                else if ((!isFirst && !isLast) || (!isFirst && isLast)) {
                    try {
                        fmsCRMemoJson = this.generateStringBody(currentItem, isFirst, isLast, fmsCRMemoJson);
                        /*
                         * currentItem.getType(), currentItem.getLink_branch(),
                         * currentItem.getAmt(),
                         * currentItem.getCust(), currentItem.getRms_batch_no(),
                         * currentItem.getDt_sent(),
                         * currentItem.getDesc(), currentItem.getAttr_ext_sys(), currentItem.getAcct(),
                         * currentItem.getBranch(), currentItem.getSub_acct(),
                         * currentItem.getTxn_desc(),
                         * currentItem.getQty_crmemo(), currentItem.getUnit_price(),
                         * currentItem.getEnt_nm(),
                         * currentItem.getEnt_no(), currentItem.getEnt_ty(), currentItem.getCoa1(),
                         * currentItem.getCoa2(), currentItem.getFms_ref_no(), currentItem.getDoc_ty(),
                         * isFirst, isLast, fmsCRMemoJson,
                         * currentItem.getDepositID(), currentItem.getDepositTask());
                         */
                        // 241010: Added 2 new parameters, depositID and depositTask
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage(), e);
                        e.printStackTrace();
                    }
                    // if it's last, call api
                    if (!isFirst && isLast) {
                        ObjectMapper mapper = new ObjectMapper();
                        stringBody = mapper.writeValueAsString(fmsCRMemoJson);
                        result.addAll(this.fms_api_crmemo(stringBody, currentItem.getCrmemo_hid(), currentItem));
                    }
                }

            }
        }
        // testing
        // else {
        // Date today = new Date();
        // try {
        // fmsCRMemoJson = this.generateStringBody("", "2", 0.0, "", "", today,
        // "", "", "", "", "", "",
        // 0, 0.0, "", "0", "", "",
        // "", "", "", true, true, fmsCRMemoJson);
        // } catch (JsonProcessingException e) {
        // e.printStackTrace();
        // }
        // BigInteger bigint = new BigInteger("1");
        // ObjectMapper mapper = new ObjectMapper();
        // stringBody = mapper.writeValueAsString(fmsCRMemoJson);
        // result.addAll(this.fms_api_crmemo(stringBody, bigint));
        // }
        return result;
    }

    public Integer newCrMemo(List<FMSCRMemo> memos) {
        BigInteger hid = sp_insfmscrmemo_h(memos.get(0));
        if (hid.intValue() < 1) {
            log.error("Exception in " + this.getClass().toString()
                    + "newBillingCrmemo func - sp_insfmscrmemo_h failed with code " + Integer.toString(hid.intValue())
                    + " !");
            return -1;
        }

        for (FMSCRMemo memo : memos) {
            if (memo.getCrmemo_hid() == null)
                memo.setCrmemo_hid(hid);

            Integer statusCode = sp_insfmscrmemo_b(hid, memo, 0);

            if (statusCode < 1) {
                log.error("Exception in " + this.getClass().toString()
                        + "newBillingCrmemo func - sp_insfmscrmemo_b failed with code " + Integer.toString(statusCode)
                        + " !");
                statusCode = sp_rollbackfmscrmemohbh(hid);
                if (statusCode < 1)
                    log.error("Exception in " + this.getClass().toString()
                            + "newBillingCrmemo func - sp_rollbackfmscrmemohbh @ sp_insfmscrmemo_b failed with code "
                            + Integer.toString(statusCode) + " !");
                return -2;
            }
            statusCode = sp_insfmscrmemo_f(hid, memo.getFms_ref_no(), memo.getDoc_ty());
            if (statusCode < 1) {
                log.error("Exception in " + this.getClass().toString()
                        + "newBillingCrmemo func - sp_insfmscrmemo_f failed with code " + Integer.toString(statusCode)
                        + " !");
                statusCode = sp_rollbackfmscrmemohbh(hid);
                if (statusCode < 1)
                    log.error("Exception in " + this.getClass().toString()
                            + "newBillingCrmemo func - sp_rollbackfmscrmemohbh @ sp_insfmscrmemo_f failed with code "
                            + Integer.toString(statusCode) + " !");
                return -3;
            }
        }
        return hid.intValue();
    }

    public Integer crMemoCallAPI(BigInteger hid) throws JsonProcessingException {
        List<FMSCRMemo> fmsCRMemo = sp_getfmscrmemobyhid(hid);

        // stringbody n fms_api_crmemo
        String stringBody = "";
        FMSCRMemoJson fmsCRMemoJson = new FMSCRMemoJson();

        // if size > 0
        if (fmsCRMemo.size() > 0) {
            for (int i = 0; i < fmsCRMemo.size(); i++) {
                FMSCRMemo currentItem = fmsCRMemo.get(i);
                String currentBatchNo = currentItem.getRms_batch_no();

                boolean isFirst = (i == 0) || !fmsCRMemo.get(i - 1).getRms_batch_no().equals(currentBatchNo);
                boolean isLast = (i == fmsCRMemo.size() - 1)
                        || !fmsCRMemo.get(i + 1).getRms_batch_no().equals(currentBatchNo);

                // only has 1 body or is first
                if ((isFirst && isLast) || (isFirst && !isLast)) {
                    try {
                        fmsCRMemoJson = new FMSCRMemoJson();
                        fmsCRMemoJson = this.generateStringBody(currentItem, isFirst, isLast, fmsCRMemoJson);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    // if it's last, call api
                    if (isFirst && isLast) {
                        ObjectMapper mapper = new ObjectMapper();
                        stringBody = mapper.writeValueAsString(fmsCRMemoJson);
                        this.fms_api_crmemo(stringBody, currentItem.getCrmemo_hid(), currentItem);
                    }
                }
                // middle or last
                else if ((!isFirst && !isLast) || (!isFirst && isLast)) {
                    try {
                        fmsCRMemoJson = this.generateStringBody(currentItem, isFirst, isLast, fmsCRMemoJson);
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    // if it's last, call api
                    if (!isFirst && isLast) {
                        ObjectMapper mapper = new ObjectMapper();
                        stringBody = mapper.writeValueAsString(fmsCRMemoJson);
                        this.fms_api_crmemo(stringBody, currentItem.getCrmemo_hid(), currentItem);
                    }
                }
            }
        }
        return 1;
    }

    public Integer sp_rollbackfmscrmemohbh(BigInteger hid) {
        return fmsCRMemoRepo.sp_rollbackfmscrmemohbh(hid);
    }

    public Integer creditNonBilCrMemo(String orn_no) {
        return fmsCRMemoRepo.creditNonBilCrMemo(orn_no);
        // return fmsCRMemoRepo.sp_rollbackfmscrmemohbh(hid);
    }

    private boolean hasNonNullDetailValues(FMSCRMemo memo) {
        return memo.getAcct() != null ||
                memo.getBranch() != null ||
                memo.getCoa1() != null ||
                memo.getCoa2() != null ||
                memo.getSub_acct() != null ||
                memo.getTxn_desc() != null ||
                memo.getQty_crmemo() != null ||
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