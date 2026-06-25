package com.maven.rms.services;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.FMSARIImmediateRequest;
import com.maven.rms.models.FMSARIModel;
import com.maven.rms.models.FMSARIReplyJSONV2;
import com.maven.rms.models.FMSARIRequest;
import com.maven.rms.repositories.BillingRepository;
import com.maven.rms.repositories.FMSARIRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Service
@Slf4j
public class FMSARIService {

    // private static final Logger logger =
    // LoggerFactory.getLogger(FMSARIService.class);

    private final FMSARIRepository fmsARIRepo;
    private final BillingRepository bRepo;

    @Value("${fmsari.api.url}")
    private String api_url;

    @Value("${fmsari.api.name}")
    private String api_name;

    @Value("${fmsari.api.test.customerid}")
    private Boolean isFakeCustomerId;

    @Value("${fms.ibm-client-id}")
    private String IBMClientID;

    @Value("${rms.env.javaUrl}")
    private String javaUrl;

    @Autowired
    private CommonService commonSvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    public FMSARIService(FMSARIRepository fmsARIRepo, BillingRepository bRepo) {
        this.fmsARIRepo = fmsARIRepo;
        this.bRepo = bRepo;
    }

    // public List<FMSARIModel> sp_getfmsmtt() {
    // List<FMSARIModel> result = new ArrayList<>();

    // try {
    // List<Object[]> objects = fmsARIRepo.sp_getfmsmtt();
    // List<FMSARIModel> fmsARIList = new ArrayList<>();
    // for (Object[] obj : objects) {
    // FMSARIModel fmsARI = new FMSARIModel();
    // fmsARI.setMtt_pg_id((BigInteger) obj[0]);
    // fmsARI.setPg_pymt_amt((BigDecimal) obj[1]);
    // fmsARI.setQty((int) obj[2]);
    // fmsARI.setItem_desc((String) obj[3]);
    // fmsARI.setUnit_fee((BigDecimal) obj[4]);
    // fmsARI.setRcpt_no((String) obj[5]);
    // fmsARI.setCust_nm((String) obj[6]);
    // fmsARI.setEntity_nm((String) obj[7]);
    // fmsARI.setEntity_no((String) obj[8]);
    // fmsARI.setEntity_type((String) obj[9]);
    // fmsARI.setGross_amt((BigDecimal) obj[10]);
    // fmsARI.setFee_detail_id((String) obj[11]);
    // fmsARI.setPg_pymt_method((String) obj[12]);
    // fmsARI.setTax_amt((BigDecimal) obj[13]);
    // fmsARI.setCustomer((String) obj[14]);
    // fmsARI.setItem_ref_no((String) obj[15]);
    // fmsARI.setCp_no((String) obj[16]);
    // fmsARIList.add(fmsARI);
    // }
    // result = fmsARIList;
    // } catch (Exception e) {
    // log.error("sp_getfmsrcbank", e);
    // e.printStackTrace();
    // }

    // return result;
    // }

    public List<FMSARIModel> sp_getfmsmtt() {
        List<FMSARIModel> result = new ArrayList<>();

        List<Object[]> objects = fmsARIRepo.sp_getfmsmtt();
        List<FMSARIModel> fmsARIList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSARIModel fmsARI = new FMSARIModel();
            fmsARI.setMtt_pg_id((BigInteger) obj[0]);
            fmsARI.setPg_pymt_amt((BigDecimal) obj[1]);
            fmsARI.setQty((int) obj[2]);
            fmsARI.setItem_desc((String) obj[3]);
            fmsARI.setUnit_fee((BigDecimal) obj[4]);
            fmsARI.setRcpt_no((String) obj[5]);
            fmsARI.setCust_nm((String) obj[6]);
            fmsARI.setEnt_nm((String) obj[7]);
            fmsARI.setEnt_no((String) obj[8]);
            fmsARI.setEnt_ty((String) obj[9]);
            fmsARI.setGross_amt((BigDecimal) obj[10]);
            // fmsARI.setFee_detail_id((String) obj[11]); // change to use fee_detail_pk
            fmsARI.setFee_detail_pk((BigInteger) obj[11]);
            fmsARI.setPg_pymt_method((String) obj[12]);
            fmsARI.setTax_amt((BigDecimal) obj[13]);
            fmsARI.setCustomer((String) obj[14]);
            fmsARI.setItem_ref_no((String) obj[15]);
            fmsARI.setCp_no((String) obj[16]);
            // 241010- Added new fields
            fmsARI.setDiscAmt((BigDecimal) obj[17]);
            fmsARI.setDepositID((String) obj[18]);
            fmsARI.setDepositTask((String) obj[19]);
            fmsARI.setNet_amt((BigDecimal) obj[20]);
            fmsARI.setInv_dt(obj[21] != null ? ((java.sql.Timestamp)obj[21]).toLocalDateTime() : null); // 251010- Add invoice date
            fmsARI.setLit_item_ref((String) obj[22]);
            fmsARIList.add(fmsARI);
        }
        result = fmsARIList;

        return result;
    }

    public String sp_getfmsarirespcodebyarihid(Integer i_ari_hid) {
        return fmsARIRepo.sp_getfmsarirespcodebyarihid(i_ari_hid);
    }

    public String sp_getfmsarirespcodebybilchildid(Integer i_bil_child_id) {
        return fmsARIRepo.sp_getfmsarirespcodebybilchildid(i_bil_child_id);
    }

    public Integer sp_deactivatefmsaribyarihid(Integer i_ari_hid) {
        return fmsARIRepo.sp_deactivatefmsaribyarihid(i_ari_hid);
    }

    public Integer sp_getfmsarihidbyaribodybilchildid(Integer i_bil_child_id) {
        return fmsARIRepo.sp_getfmsarihidbyaribodybilchildid(i_bil_child_id);
    }

    // public BigInteger sp_insfmsmtth(String customer) {
    // // public BigInteger sp_insfmsmtth(BigInteger mtt_pg_id, BigDecimal
    // pg_pymt_amt,
    // // int qty, String item_desc,BigDecimal unit_fee, String rcpt_no, String
    // // cust_nm, String entity_nm, String entity_no, String entity_type,
    // BigDecimal
    // // gross_amt, String fee_detail_id, String pg_pymt_method, BigDecimal
    // tax_amt,
    // // String customer) {
    // BigInteger result = null;

    // try {
    // result = fmsARIRepo.sp_insfmsmtth(customer);
    // // result = fmsARIRepo.sp_insfmsmtth(mtt_pg_id, pg_pymt_amt, qty,
    // // item_desc,unit_fee, rcpt_no, cust_nm, entity_nm, entity_no,entity_type,
    // // gross_amt, fee_detail_id, pg_pymt_method, tax_amt, customer);
    // } catch (Exception e) {
    // log.error("sp_insfmsmtth", e);
    // e.printStackTrace();
    // }

    // return result;
    // }

    public BigInteger sp_insfmsmtth(String customer, LocalDateTime inv_dt) {
        // public BigInteger sp_insfmsmtth(BigInteger mtt_pg_id, BigDecimal pg_pymt_amt,
        // int qty, String item_desc,BigDecimal unit_fee, String rcpt_no, String
        // cust_nm, String entity_nm, String entity_no, String entity_type, BigDecimal
        // gross_amt, String fee_detail_id, String pg_pymt_method, BigDecimal tax_amt,
        // String customer) {
        BigInteger result = null;
        result = fmsARIRepo.sp_insfmsmtth(customer, inv_dt);
        // result = fmsARIRepo.sp_insfmsmtth(mtt_pg_id, pg_pymt_amt, qty,
        // item_desc,unit_fee, rcpt_no, cust_nm, entity_nm, entity_no,entity_type,
        // gross_amt, fee_detail_id, pg_pymt_method, tax_amt, customer);
        return result;
    }

    public BigInteger sp_insfmsari_h(FMSARIModel ari) {
        BigInteger result = null;
        result = fmsARIRepo.sp_insfmsari_h(ari);
        return result;
    }

    // public BigInteger sp_insfmsmttb(BigInteger mtt_pg_id, BigDecimal pg_pymt_amt,
    // int qty, String item_desc,
    // BigDecimal unit_fee, String rcpt_no, String cust_nm, String entity_nm, String
    // entity_no,
    // String entity_type, BigDecimal gross_amt, String fee_detail_id, String
    // pg_pymt_method, BigDecimal tax_amt,
    // String customer, BigInteger hid, String item_ref_no, String cp_no) {
    // BigInteger result = null;

    // try {
    // result = fmsARIRepo.sp_insfmsmttb(mtt_pg_id, pg_pymt_amt, qty, item_desc,
    // unit_fee, rcpt_no, cust_nm,
    // entity_nm, entity_no, entity_type, gross_amt, fee_detail_id, pg_pymt_method,
    // tax_amt, customer, hid, item_ref_no, cp_no);
    // } catch (Exception e) {
    // logger.error("sp_insfmsmttb", e);
    // e.printStackTrace();
    // }

    // return result;
    // }

    // public BigInteger sp_insfmsmttb(FMSARIModel fmsariModel, BigInteger hid) {
    // BigInteger result = null;

    // try {
    // result = fmsARIRepo.sp_insfmsmttb(fmsariModel, hid);
    // } catch (Exception e) {
    // log.error("sp_insfmsmttb", e);
    // e.printStackTrace();
    // }

    // return result;
    // }

    public BigInteger sp_insfmsmttb(FMSARIModel fmsariModel, BigInteger hid, Integer flag) {
        BigInteger result = null;
        result = fmsARIRepo.sp_insfmsmttb(fmsariModel, hid, flag);
        return result;
    }

    // public List<FMSARIModel> sp_getfmsari() {
    // List<FMSARIModel> result = new ArrayList<>();

    // try {
    // List<Object[]> objects = fmsARIRepo.sp_getfmsari();
    // List<FMSARIModel> fmsARIList = new ArrayList<>();
    // for (Object[] obj : objects) {
    // FMSARIModel fmsARI = new FMSARIModel();
    // fmsARI.setType((String) obj[0]);
    // fmsARI.setLink_branch((String) obj[1]);
    // fmsARI.setAmt((BigDecimal) obj[2]);
    // fmsARI.setCust((String) obj[3]);
    // fmsARI.setRms_batch_no((String) obj[4]);
    // fmsARI.setDt_sent((Date) obj[5]);
    // fmsARI.setDesc((String) obj[6]);
    // fmsARI.setAttr_ext_sys((String) obj[7]);
    // fmsARI.setCoa1((String) obj[8]);
    // fmsARI.setCoa2((String) obj[9]);
    // fmsARI.setBranch((String) obj[10]);
    // fmsARI.setQty((int) obj[11]);
    // fmsARI.setSub_acct((String) obj[12]);
    // fmsARI.setTxn_desc((String) obj[13]);
    // fmsARI.setUnit_price((BigDecimal) obj[14]);
    // fmsARI.setRcpt_no((String) obj[15]);
    // fmsARI.setPayee_info((String) obj[16]);
    // fmsARI.setEnt_nm((String) obj[17]);
    // fmsARI.setEnt_no((String) obj[18]);
    // fmsARI.setEnt_ty((String) obj[19]);
    // fmsARI.setItem_amt((BigDecimal) obj[20]);
    // fmsARI.setPymt_mode((String) obj[21]);
    // fmsARI.setItem_tax_amt((BigDecimal) obj[22]);
    // fmsARIList.add(fmsARI);
    // }
    // result = fmsARIList;

    // } catch (Exception e) {
    // log.error("sp_getfmsarr", e);
    // e.printStackTrace();
    // }

    // return result;
    // }

    public List<FMSARIModel> sp_getfmsari() {
        List<FMSARIModel> result = new ArrayList<>();
        List<Object[]> objects = fmsARIRepo.sp_getfmsari();
        List<FMSARIModel> fmsARIList = new ArrayList<>();
        if (objects.size() > 0) {

            for (Object[] obj : objects) {
                FMSARIModel fmsARI = new FMSARIModel();
                fmsARI.setType((String) obj[0]);
                fmsARI.setLink_branch((String) obj[1]);
                fmsARI.setAmt((BigDecimal) obj[2]);
                fmsARI.setCust((String) obj[3]);
                fmsARI.setRms_batch_no((String) obj[4]);
                fmsARI.setDt_sent((Date) obj[5]);
                fmsARI.setDesc((String) obj[6]);
                fmsARI.setAttr_ext_sys((String) obj[7]);
                fmsARI.setCoa1((String) obj[8]);
                fmsARI.setCoa2((String) obj[9]);
                fmsARI.setBranch((String) obj[10]);
                fmsARI.setQty((int) obj[11]);
                fmsARI.setSub_acct((String) obj[12]);
                fmsARI.setTxn_desc((String) obj[13]);
                fmsARI.setUnit_price((BigDecimal) obj[14]);
                fmsARI.setRcpt_no((String) obj[15]);
                fmsARI.setPayee_info((String) obj[16]);
                fmsARI.setEnt_nm((String) obj[17]);
                fmsARI.setEnt_no((String) obj[18]);
                fmsARI.setEnt_ty((String) obj[19]);
                fmsARI.setItem_amt((BigDecimal) obj[20]);
                fmsARI.setPymt_mode((String) obj[21]);
                fmsARI.setItem_tax_amt((BigDecimal) obj[22]);
                // 241010: Added fields
                fmsARI.setLineNbr((Integer) obj[23]);
                fmsARI.setDiscAmt((BigDecimal) obj[24]);
                fmsARI.setDepositID((String) obj[25]);
                fmsARI.setDepositTask((String) obj[26]);
                fmsARI.setGeneratePDF((Integer) obj[27]);
                fmsARI.setInv_dt(obj[28] != null ? ((java.sql.Timestamp)obj[28]).toLocalDateTime() : null);
                fmsARIList.add(fmsARI);
            }

        }

        result = fmsARIList;

        return result;
    }

    public List<FMSARIModel> sp_getfmsaribybilchildid(Integer bil_child_id) {
        List<FMSARIModel> result = new ArrayList<>();
        List<Object[]> objects = fmsARIRepo.sp_getfmsaribybilchildid(bil_child_id);
        List<FMSARIModel> fmsARIList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSARIModel fmsARI = new FMSARIModel();
            fmsARI.setType((String) obj[0]);
            fmsARI.setLink_branch((String) obj[1]);
            fmsARI.setAmt((BigDecimal) obj[2]);
            fmsARI.setCust((String) obj[3]);
            fmsARI.setRms_batch_no((String) obj[4]);
            fmsARI.setDt_sent((Date) obj[5]);
            fmsARI.setDesc((String) obj[6]);
            fmsARI.setAttr_ext_sys((String) obj[7]);
            fmsARI.setCoa1((String) obj[8]);
            fmsARI.setCoa2((String) obj[9]);
            fmsARI.setBranch((String) obj[10]);
            fmsARI.setQty((int) obj[11]);
            fmsARI.setSub_acct((String) obj[12]);
            fmsARI.setTxn_desc((String) obj[13]);
            fmsARI.setUnit_price((BigDecimal) obj[14]);
            fmsARI.setRcpt_no((String) obj[15]);
            fmsARI.setPayee_info((String) obj[16]);
            fmsARI.setEnt_nm((String) obj[17]);
            fmsARI.setEnt_no((String) obj[18]);
            fmsARI.setEnt_ty((String) obj[19]);
            fmsARI.setItem_amt((BigDecimal) obj[20]);
            fmsARI.setPymt_mode((String) obj[21]);
            fmsARI.setItem_tax_amt((BigDecimal) obj[22]);
            // 241010: Added fields
            fmsARI.setLineNbr((Integer) obj[23]);
            fmsARI.setDiscAmt((BigDecimal) obj[24]);
            fmsARI.setDepositID((String) obj[25]);
            fmsARI.setDepositTask((String) obj[26]);
            fmsARI.setFms_ref_no((String) obj[27]);
            fmsARI.setGeneratePDF((Integer) obj[28]);
            fmsARI.setInv_dt(obj[29] != null ? ((java.sql.Timestamp)obj[29]).toLocalDateTime() : null);
            fmsARI.setBil_child_id(bil_child_id);
            fmsARIList.add(fmsARI);
        }
        result = fmsARIList;

        return result;
    }

    // 250307: Added new method for FMSARI Immediate send posting
    public List<FMSARIModel> sp_getfmsariimmediate(FMSARIImmediateRequest fmsARIImmediateRequest) {
        List<FMSARIModel> result = new ArrayList<>();

        List<Object[]> objects = fmsARIRepo.sp_getfmsariimmediate(fmsARIImmediateRequest);

        List<FMSARIModel> fmsARIList = new ArrayList<>();

        for (Object[] obj : objects) {
            FMSARIModel fmsARI = new FMSARIModel();
            fmsARI.setType((String) obj[0]);
            fmsARI.setLink_branch((String) obj[1]);
            fmsARI.setAmt((BigDecimal) obj[2]);
            fmsARI.setCust((String) obj[3]);
            fmsARI.setRms_batch_no((String) obj[4]);
            fmsARI.setDt_sent((Date) obj[5]);
            fmsARI.setDesc((String) obj[6]);
            fmsARI.setAttr_ext_sys((String) obj[7]);
            fmsARI.setCoa1((String) obj[8]);
            fmsARI.setCoa2((String) obj[9]);
            fmsARI.setBranch((String) obj[10]);
            fmsARI.setQty((int) obj[11]);
            fmsARI.setSub_acct((String) obj[12]);
            fmsARI.setTxn_desc((String) obj[13]);
            fmsARI.setUnit_price((BigDecimal) obj[14]);
            fmsARI.setRcpt_no((String) obj[15]);
            fmsARI.setPayee_info((String) obj[16]);
            fmsARI.setEnt_nm((String) obj[17]);
            fmsARI.setEnt_no((String) obj[18]);
            fmsARI.setEnt_ty((String) obj[19]);
            fmsARI.setItem_amt((BigDecimal) obj[20]);
            fmsARI.setPymt_mode((String) obj[21]);
            fmsARI.setItem_tax_amt((BigDecimal) obj[22]);
            fmsARI.setLineNbr((Integer) obj[23]);
            fmsARI.setDiscAmt((BigDecimal) obj[24]);
            fmsARI.setDepositID((String) obj[25]);
            fmsARI.setDepositTask((String) obj[26]);
            fmsARI.setInv_dt(obj[27] != null ? ((java.sql.Timestamp)obj[27]).toLocalDateTime() : null);
            fmsARIList.add(fmsARI);
        }
        result = fmsARIList;

        return result;
    }

    // 250307: Added new method for FMSARI Immediate send posting
    public List<FMSARIModel> sp_getfmsarijson(String i_rms_batch_no) {
        List<FMSARIModel> result = new ArrayList<>();

        List<Object[]> objects = fmsARIRepo.sp_getfmsarijson(i_rms_batch_no);

        List<FMSARIModel> fmsARIList = new ArrayList<>();

        for (Object[] obj : objects) {
            FMSARIModel fmsARI = new FMSARIModel();
            fmsARI.setType((String) obj[0]);
            fmsARI.setLink_branch((String) obj[1]);
            fmsARI.setAmt((BigDecimal) obj[2]);
            fmsARI.setCust((String) obj[3]);
            fmsARI.setRms_batch_no((String) obj[4]);
            fmsARI.setDt_sent((Date) obj[5]);
            fmsARI.setDesc((String) obj[6]);
            fmsARI.setAttr_ext_sys((String) obj[7]);
            fmsARI.setCoa1((String) obj[8]);
            fmsARI.setCoa2((String) obj[9]);
            fmsARI.setBranch((String) obj[10]);
            fmsARI.setQty((int) obj[11]);
            fmsARI.setSub_acct((String) obj[12]);
            fmsARI.setTxn_desc((String) obj[13]);
            fmsARI.setUnit_price((BigDecimal) obj[14]);
            fmsARI.setRcpt_no((String) obj[15]);
            fmsARI.setPayee_info((String) obj[16]);
            fmsARI.setEnt_nm((String) obj[17]);
            fmsARI.setEnt_no((String) obj[18]);
            fmsARI.setEnt_ty((String) obj[19]);
            fmsARI.setItem_amt((BigDecimal) obj[20]);
            fmsARI.setPymt_mode((String) obj[21]);
            fmsARI.setItem_tax_amt((BigDecimal) obj[22]);
            fmsARI.setLineNbr((Integer) obj[23]);
            fmsARI.setDiscAmt((BigDecimal) obj[24]);
            fmsARI.setDepositID((String) obj[25]);
            fmsARI.setDepositTask((String) obj[26]);
            fmsARI.setGeneratePDF((Integer) obj[27]);
            fmsARI.setInv_dt(obj[28] != null ? ((java.sql.Timestamp)obj[28]).toLocalDateTime() : null);
            fmsARIList.add(fmsARI);
        }
        result = fmsARIList;

        return result;
    }

    public void postFMSARIImmediate(FMSARIImmediateRequest fmsARIImmediateRequest) {

        // Call FMS Post Accounting API
        List<FMSARIModel> fmsARI = sp_getfmsariimmediate(fmsARIImmediateRequest);

        if (fmsARI.size() > 0) {
            log.info("FMSARI Sent on: " + new Date() + "for RMS Batch No " + fmsARI.get(0).getRms_batch_no());
            String body = generateStringBody(fmsARI);
        }
    }

    // public Integer sp_updfmsari(String resp_attr_ext_sys, String fms_ref_no,
    // String resp_co,
    // String resp_status, String resp_msg, String resp_dt) {
    // Integer result = null;

    // try {
    // result = fmsARIRepo.sp_updfmsari(resp_attr_ext_sys, fms_ref_no, resp_co,
    // resp_status, resp_msg, resp_dt);
    // } catch (Exception e) {
    // logger.error("sp_updfmsari", e);
    // e.printStackTrace();
    // }

    // return result;
    // }

    // public Integer sp_updfmsari(FMSARIModel fmsariModel) {
    // Integer result = null;

    // try {
    // result = fmsARIRepo.sp_updfmsari(fmsariModel);
    // } catch (Exception e) {
    // log.error("sp_updfmsari", e);
    // e.printStackTrace();
    // }

    // return result;
    // }

    public Integer sp_updfmsari(FMSARIModel fmsariModel) {
        Integer result = null;
        result = fmsARIRepo.sp_updfmsari(fmsariModel);
        return result;
    }

    // public String generateStringBody(List<FMSARIModel> fmsARI) {

    // String stringBody = "";
    // Boolean header = false;
    // Boolean first = true;

    // for (int i = 0; i < fmsARI.size(); i++) {
    // // System.out.println(i);
    // log.debug(String.valueOf(i));
    // FMSARIModel fmsariModel = fmsARI.get(i);
    // String rms_batch_no = fmsariModel.getRms_batch_no() == null ? "" :
    // fmsariModel.getRms_batch_no();
    // String nextBatchNo = "";
    // if ((i + 1) < fmsARI.size()) {
    // String tmp = fmsARI.get(i + 1).getRms_batch_no();
    // nextBatchNo = tmp == null ? "" : tmp;
    // }
    // if (header == false) {
    // stringBody = this.header(stringBody, fmsariModel);
    // stringBody = stringBody + "\"Details\": [\r\n";
    // stringBody = this.body(stringBody, fmsariModel);
    // header = true;
    // }
    // if (rms_batch_no.equals(nextBatchNo)) { // same batch
    // if (i + 1 == fmsARI.size()) {
    // if (!first) {
    // stringBody = stringBody + ",\r\n";
    // stringBody = this.body(stringBody, fmsariModel);
    // }
    // stringBody = stringBody + "\r\n]\r\n}";
    // // send api
    // this.fms_api_ari(stringBody, fmsariModel);
    // } else {
    // if (!first) {
    // stringBody = stringBody + ",\r\n";
    // stringBody = this.body(stringBody, fmsariModel);
    // }
    // }

    // } else { // next not same batch
    // if (!first) {
    // stringBody = stringBody + ",\r\n";
    // stringBody = this.body(stringBody, fmsariModel);
    // }
    // stringBody = stringBody + "\r\n]\r\n}";
    // // send api
    // System.out.print(stringBody);
    // log.error(stringBody);
    // this.fms_api_ari(stringBody, fmsariModel);
    // header = false;
    // stringBody = "";
    // first = true;
    // }
    // first = false;
    // }

    // return stringBody;
    // }

    // // Commented on 26/5/2025
    // // 241010- Wei Ern modified based on latest ISD
    // public String header(String stringBody, FMSARIModel fmsariModel) {
    // stringBody += "{\r\n \"Type\": {\r\n \"value\": \"" + fmsariModel.getType() +
    // "\"\r\n }," +
    // "\r\n \"LinkBranch\": {\r\n \"value\": \"" + fmsariModel.getLink_branch() +
    // "\"\r\n }," +
    // "\r\n \"Amount\": {\r\n \"value\": " + fmsariModel.getAmt() + "\r\n }," +
    // "\r\n \"CustomerID\": {\r\n \"value\": \"" + (isFakeCustomerId ? "C000001" :
    // fmsariModel.getCust()) + "\"\r\n }," +
    // // Changed to customerID
    // "\r\n \"CustomerOrder\": {\r\n \"value\": \"" + fmsariModel.getRms_batch_no()
    // + "\"\r\n },"
    // +
    // "\r\n \"Date\": {\r\n \"value\": \"" + fmsariModel.getDt_sent() + "\"\r\n },"
    // +
    // "\r\n \"Description\": {\r\n \"value\": \"" + fmsariModel.getDesc() + "\"\r\n
    // }," +
    // "\r\n \"custom\": {\r\n \"CurrentDocument\": {\r\n" +
    // " \"AttributeSYSNAME\": {\r\n \"type\": \"CustomStringField\",\r\n \"value\":
    // \""
    // + fmsariModel.getAttr_ext_sys()
    // + "\"\r\n },\r\n \"AttributeGENPDF\": {\r\n \"type\": \"CustomIntField\","
    // + "\r\n \"value\": "
    // + (fmsariModel.getGeneratePDF() != null ? (fmsariModel.getGeneratePDF() > 0 ?
    // "true" : "false")
    // : "false")
    // + "\r\n }}},\r\n";
    // return stringBody;
    // }

    // public String body(String stringBody, FMSARIModel fmsariModel) {
    // stringBody += "{\r\n \"LineNbr\": {\r\n \"value\": \"" +
    // fmsariModel.getLineNbr() + "\"\r\n }," +
    // "\r\n \"ChartofAccount1\": {\r\n \"value\": \"" + fmsariModel.getCoa1() +
    // "\"\r\n }," +
    // "\r\n \"ChartofAccount2\": {\r\n \"value\": \"" + fmsariModel.getCoa2() +
    // "\"\r\n }," +
    // "\r\n \"Branch\": {\r\n \"value\": \"" + fmsariModel.getBranch() + "\"\r\n
    // }," +
    // "\r\n \"Qty\": {\r\n \"value\": " + fmsariModel.getQty() + "\r\n }," +
    // "\r\n \"Subaccount\": {\r\n \"value\": \"" + fmsariModel.getSub_acct() +
    // "\"\r\n }," +
    // "\r\n \"TransactionDescription\": {\r\n \"value\": \"" +
    // fmsariModel.getTxn_desc()
    // + "\"\r\n }," +
    // "\r\n \"UnitPrice\": {\r\n \"value\": " + fmsariModel.getUnit_price() + "\r\n
    // }," +
    // "\r\n \"ReceiptNumber\": {\r\n \"value\": \"" + fmsariModel.getRcpt_no() +
    // "\"\r\n }," + // v1
    // // only
    // "\r\n \"PayeeInfo\": {\r\n \"value\": \"" + fmsariModel.getPayee_info() +
    // "\"\r\n }," + // v1
    // // only
    // "\r\n \"EntityName\": {\r\n \"value\": \"" + fmsariModel.getEnt_nm() +
    // "\"\r\n }," + // v1
    // // only
    // "\r\n \"EntityNumber\": {\r\n \"value\": \"" + fmsariModel.getEnt_no() +
    // "\"\r\n }," + // v1
    // // only
    // "\r\n \"EntityType\": {\r\n \"value\": \"" + fmsariModel.getEnt_ty() +
    // "\"\r\n }," + // v1
    // // only
    // "\r\n \"ItemAmount\": {\r\n \"value\": \"" + fmsariModel.getItem_amt() +
    // "\"\r\n }," + // v1
    // // only
    // "\r\n \"PaymentMode\": {\r\n \"value\": \"" + fmsariModel.getPg_pymt_method()
    // + "\"\r\n },"
    // + // v1 only
    // "\r\n \"ItemTaxAmount\": {\r\n \"value\": \"" + fmsariModel.getItem_tax_amt()
    // + "\"\r\n },"
    // + // v1 only
    // "\r\n \"DiscountAmount\": {\r\n \"value\": \"" + fmsariModel.getDiscAmt() +
    // "\"\r\n }," +
    // "\r\n \"DepositID\": {\r\n \"value\": \"" + fmsariModel.getDepositID() +
    // "\"\r\n }," +
    // "\r\n \"DepositTask\": {\r\n \"value\": \"" + fmsariModel.getDepositTask()
    // + "\"\r\n }\r\n}";
    // return stringBody;
    // }

    public String generateStringBodyJSON(List<FMSARIModel> fmsARI) {
        List<FMSARIRequest> requests = new ArrayList<>();

        for (int i = 0; i < fmsARI.size(); i++) {
            FMSARIModel fmsariModel = fmsARI.get(i);
            String rms_batch_no = fmsariModel.getRms_batch_no() == null ? "" : fmsariModel.getRms_batch_no();

            // Find or create request for this batch (it will be only 1 batch)
            FMSARIRequest currentRequest = findOrCreateRequest(requests, rms_batch_no, fmsariModel);

            // Add detail to the request
            FMSARIRequest.Detail detail = createDetail(fmsariModel);
            currentRequest.getDetails().add(detail);
        }

        try {
            objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
            // Always return the first object (not array)
            return objectMapper.writeValueAsString(requests.get(0));
        } catch (JsonProcessingException e) {
            log.error("Error converting to JSON", e);
            return "{}"; // return empty object if serialization fails
        }
    }

    // New method to generate JSON using proper object structure
    public String generateStringBody(List<FMSARIModel> fmsARI) {
        List<FMSARIRequest> requests = new ArrayList<>();

        for (int i = 0; i < fmsARI.size(); i++) {
            FMSARIModel fmsariModel = fmsARI.get(i);
            String rms_batch_no = fmsariModel.getRms_batch_no() == null ? "" : fmsariModel.getRms_batch_no();
            String nextBatchNo = "";

            if ((i + 1) < fmsARI.size()) {
                String tmp = fmsARI.get(i + 1).getRms_batch_no();
                nextBatchNo = tmp == null ? "" : tmp;
            }

            // Find or create request for this batch
            FMSARIRequest currentRequest = findOrCreateRequest(requests, rms_batch_no, fmsariModel);

            // Add detail to the request
            FMSARIRequest.Detail detail = createDetail(fmsariModel);
            currentRequest.getDetails().add(detail);

            // If this is the last item in the batch or the last item overall, send the API
            boolean isLastInBatch = !rms_batch_no.equals(nextBatchNo);
            boolean isLastOverall = (i + 1) == fmsARI.size();

            if (isLastInBatch || isLastOverall) {
                try {
                    objectMapper.setTimeZone(TimeZone.getTimeZone("Asia/Kuala_Lumpur"));
                    String jsonBody = objectMapper.writeValueAsString(currentRequest);
                    System.out.print(jsonBody);
                    this.fms_api_ari(jsonBody, fmsariModel);

                    // Remove the completed request from the list
                    requests.remove(currentRequest);
                } catch (JsonProcessingException e) {
                    log.error("Error converting to JSON", e);
                }
            }
        }

        return ""; // Maintain compatibility with original method signature
    }

    // Helper method to find existing request or create new one
    private FMSARIRequest findOrCreateRequest(List<FMSARIRequest> requests, String batchNo, FMSARIModel fmsariModel) {
        for (FMSARIRequest request : requests) {
            if (request.getCustomerOrder().getValue().equals(batchNo)) {
                return request;
            }
        }

        // Create new request
        FMSARIRequest newRequest = createRequest(fmsariModel);
        requests.add(newRequest);
        return newRequest;
    }

    // Helper method to create request header
    private FMSARIRequest createRequest(FMSARIModel fmsariModel) {
        FMSARIRequest request = new FMSARIRequest();

        // Set Type
        FMSARIRequest.Type type = new FMSARIRequest.Type();
        type.setValue(fmsariModel.getType());
        request.setType(type);

        // Set LinkBranch
        FMSARIRequest.LinkBranch linkBranch = new FMSARIRequest.LinkBranch();
        linkBranch.setValue(fmsariModel.getLink_branch());
        request.setLinkBranch(linkBranch);

        // Set Amount
        FMSARIRequest.Amount amount = new FMSARIRequest.Amount();
        amount.setValue(fmsariModel.getAmt());
        request.setAmount(amount);

        // Set CustomerID
        FMSARIRequest.CustomerID customerID = new FMSARIRequest.CustomerID();
        // customerID.setValue(isFakeCustomerId ? "C000001" : fmsariModel.getCust());
        customerID.setValue(fmsariModel.getCust());
        request.setCustomerID(customerID);

        // Set CustomerOrder
        FMSARIRequest.CustomerOrder customerOrder = new FMSARIRequest.CustomerOrder();
        customerOrder.setValue(fmsariModel.getRms_batch_no());
        request.setCustomerOrder(customerOrder);

        // Set Date
        FMSARIRequest.Date date = new FMSARIRequest.Date();
        date.setValue(fmsariModel.getDt_sent());
        request.setDate(date);
        
        // Set InvDate
        FMSARIRequest.InvoiceDate Invdate = new FMSARIRequest.InvoiceDate();
        Invdate.setValue(fmsariModel.getInv_dt() != null ? 
        		Date.from(fmsariModel.getInv_dt().atZone(ZoneId.systemDefault()).toInstant())
        		: new Date());
        request.setInvoiceDate(Invdate);

        // Set Description
        FMSARIRequest.Description description = new FMSARIRequest.Description();
        description.setValue(fmsariModel.getDesc());
        request.setDescription(description);

        // Set Custom
        FMSARIRequest.Custom custom = new FMSARIRequest.Custom();
        FMSARIRequest.Custom.CurrentDocument currentDocument = new FMSARIRequest.Custom.CurrentDocument();

        // Set AttributeSYSNAME
        FMSARIRequest.Custom.CurrentDocument.AttributeSYSNAME attributeSYSNAME = new FMSARIRequest.Custom.CurrentDocument.AttributeSYSNAME();
        attributeSYSNAME.setType("CustomStringField");
        attributeSYSNAME.setValue(fmsariModel.getAttr_ext_sys());
        currentDocument.setAttributeSYSNAME(attributeSYSNAME);

        // Set AttributeGENPDF
        FMSARIRequest.Custom.CurrentDocument.AttributeGENPDF attributeGENPDF = new FMSARIRequest.Custom.CurrentDocument.AttributeGENPDF();
        attributeGENPDF.setType("CustomIntField");
        attributeGENPDF.setValue(
                fmsariModel.getGeneratePDF() != null ? (fmsariModel.getGeneratePDF() > 0 ? true : false) : false);
        currentDocument.setAttributeGENPDF(attributeGENPDF);

        custom.setCurrentDocument(currentDocument);
        request.setCustom(custom);

        // Initialize Details list
        request.setDetails(new ArrayList<>());

        return request;
    }

    // Helper method to create detail object
    private FMSARIRequest.Detail createDetail(FMSARIModel fmsariModel) {
        FMSARIRequest.Detail detail = new FMSARIRequest.Detail();

        if (fmsariModel.getCust().contains("OTC")) {
            // Set LineNbr
            FMSARIRequest.Detail.LineNbr lineNbr = new FMSARIRequest.Detail.LineNbr();
            lineNbr.setValue(fmsariModel.getLineNbr());
            detail.setLineNbr(lineNbr);

            // Set ChartofAccount1
            FMSARIRequest.Detail.ChartofAccount1 coa1 = new FMSARIRequest.Detail.ChartofAccount1();
            coa1.setValue(fmsariModel.getCoa1());
            detail.setChartofAccount1(coa1);

            // Set ChartofAccount2
            FMSARIRequest.Detail.ChartofAccount2 coa2 = new FMSARIRequest.Detail.ChartofAccount2();
            coa2.setValue(fmsariModel.getCoa2());
            detail.setChartofAccount2(coa2);

            // Set Branch
            FMSARIRequest.Detail.Branch branch = new FMSARIRequest.Detail.Branch();
            branch.setValue(fmsariModel.getBranch());
            detail.setBranch(branch);

            // Set Qty
            FMSARIRequest.Detail.Qty qty = new FMSARIRequest.Detail.Qty();
            qty.setValue(fmsariModel.getQty());
            detail.setQty(qty);

            // Set Subaccount
            FMSARIRequest.Detail.Subaccount subaccount = new FMSARIRequest.Detail.Subaccount();
            subaccount.setValue(fmsariModel.getSub_acct());
            detail.setSubaccount(subaccount);

            // Set TransactionDescription
            FMSARIRequest.Detail.TransactionDescription txnDesc = new FMSARIRequest.Detail.TransactionDescription();
            txnDesc.setValue(fmsariModel.getTxn_desc());
            detail.setTransactionDescription(txnDesc);

            // Set UnitPrice
            FMSARIRequest.Detail.UnitPrice unitPrice = new FMSARIRequest.Detail.UnitPrice();
            unitPrice.setValue(fmsariModel.getUnit_price());
            detail.setUnitPrice(unitPrice);

            // Set DiscountAmount
            FMSARIRequest.Detail.DiscountAmount discountAmount = new FMSARIRequest.Detail.DiscountAmount();
            discountAmount.setValue(fmsariModel.getDiscAmt());
            detail.setDiscountAmount(discountAmount);

            // Set DepositID
            FMSARIRequest.Detail.DepositID depositID = new FMSARIRequest.Detail.DepositID();
            String depositIdValue = fmsariModel.getDepositID();
            if (depositIdValue == null || depositIdValue.trim().isEmpty()) {
                depositID.setValue("X");
            } else {
                depositID.setValue(depositIdValue);
            }
            detail.setDepositID(depositID);

            // Set DepositTask
            FMSARIRequest.Detail.DepositTask depositTask = new FMSARIRequest.Detail.DepositTask();
            String depositTaskValue = fmsariModel.getDepositTask();
            if (depositTaskValue == null || depositTaskValue.trim().isEmpty()) {
                depositTask.setValue(null);
            } else {
                depositTask.setValue(depositTaskValue);
            }
            detail.setDepositTask(depositTask);
            return detail;
        } else {
            // Set LineNbr
            FMSARIRequest.Detail.LineNbr lineNbr = new FMSARIRequest.Detail.LineNbr();
            lineNbr.setValue(fmsariModel.getLineNbr());
            detail.setLineNbr(lineNbr);

            // Set ChartofAccount1
            FMSARIRequest.Detail.ChartofAccount1 coa1 = new FMSARIRequest.Detail.ChartofAccount1();
            coa1.setValue(fmsariModel.getCoa1());
            detail.setChartofAccount1(coa1);

            // Set ChartofAccount2
            FMSARIRequest.Detail.ChartofAccount2 coa2 = new FMSARIRequest.Detail.ChartofAccount2();
            coa2.setValue(fmsariModel.getCoa2());
            detail.setChartofAccount2(coa2);

            // Set Branch
            FMSARIRequest.Detail.Branch branch = new FMSARIRequest.Detail.Branch();
            branch.setValue(fmsariModel.getBranch());
            detail.setBranch(branch);

            // Set Qty
            FMSARIRequest.Detail.Qty qty = new FMSARIRequest.Detail.Qty();
            qty.setValue(fmsariModel.getQty());
            detail.setQty(qty);

            // Set Subaccount
            FMSARIRequest.Detail.Subaccount subaccount = new FMSARIRequest.Detail.Subaccount();
            subaccount.setValue(fmsariModel.getSub_acct());
            detail.setSubaccount(subaccount);

            // Set TransactionDescription
            FMSARIRequest.Detail.TransactionDescription txnDesc = new FMSARIRequest.Detail.TransactionDescription();
            txnDesc.setValue(fmsariModel.getTxn_desc());
            detail.setTransactionDescription(txnDesc);

            // Set UnitPrice
            FMSARIRequest.Detail.UnitPrice unitPrice = new FMSARIRequest.Detail.UnitPrice();
            unitPrice.setValue(fmsariModel.getUnit_price());
            detail.setUnitPrice(unitPrice);

            // Set ReceiptNumber
            FMSARIRequest.Detail.ReceiptNumber receiptNumber = new FMSARIRequest.Detail.ReceiptNumber();
            receiptNumber.setValue(fmsariModel.getRcpt_no());
            detail.setReceiptNumber(receiptNumber);

            // Set PayeeInfo
            FMSARIRequest.Detail.PayeeInfo payeeInfo = new FMSARIRequest.Detail.PayeeInfo();
            payeeInfo.setValue(fmsariModel.getPayee_info());
            detail.setPayeeInfo(payeeInfo);

            // Set EntityName
            FMSARIRequest.Detail.EntityName entityName = new FMSARIRequest.Detail.EntityName();
            entityName.setValue(fmsariModel.getEnt_nm());
            detail.setEntityName(entityName);

            // Set EntityNumber
            FMSARIRequest.Detail.EntityNumber entityNumber = new FMSARIRequest.Detail.EntityNumber();
            entityNumber.setValue(fmsariModel.getEnt_no());
            detail.setEntityNumber(entityNumber);

            // Set EntityType
            FMSARIRequest.Detail.EntityType entityType = new FMSARIRequest.Detail.EntityType();
            entityType.setValue(fmsariModel.getEnt_ty());
            detail.setEntityType(entityType);

            // Set ItemAmount
            FMSARIRequest.Detail.ItemAmount itemAmount = new FMSARIRequest.Detail.ItemAmount();
            itemAmount.setValue(fmsariModel.getItem_amt());
            detail.setItemAmount(itemAmount);

            // Set PaymentMode
            FMSARIRequest.Detail.PaymentMode paymentMode = new FMSARIRequest.Detail.PaymentMode();
            paymentMode.setValue(fmsariModel.getPg_pymt_method());
            detail.setPaymentMode(paymentMode);

            // Set ItemTaxAmount
            FMSARIRequest.Detail.ItemTaxAmount itemTaxAmount = new FMSARIRequest.Detail.ItemTaxAmount();
            itemTaxAmount.setValue(fmsariModel.getItem_tax_amt());
            detail.setItemTaxAmount(itemTaxAmount);

            // Set DiscountAmount
            FMSARIRequest.Detail.DiscountAmount discountAmount = new FMSARIRequest.Detail.DiscountAmount();
            discountAmount.setValue(fmsariModel.getDiscAmt());
            detail.setDiscountAmount(discountAmount);

            // Set DepositID
            FMSARIRequest.Detail.DepositID depositID = new FMSARIRequest.Detail.DepositID();
            String depositIdValue = fmsariModel.getDepositID();
            if (depositIdValue == null || depositIdValue.trim().isEmpty()) {
                depositID.setValue("X");
            } else {
                depositID.setValue(depositIdValue);
            }
            detail.setDepositID(depositID);

            // Set DepositTask
            FMSARIRequest.Detail.DepositTask depositTask = new FMSARIRequest.Detail.DepositTask();
            String depositTaskValue = fmsariModel.getDepositTask();
            if (depositTaskValue == null || depositTaskValue.trim().isEmpty()) {
                depositTask.setValue(null);
            } else {
                depositTask.setValue(depositTaskValue);
            }
            detail.setDepositTask(depositTask);
            return detail;
        }
    }

    @SuppressWarnings("deprecation")
    public Integer fms_api_ari(String stringBody, FMSARIModel fmsariModel) {
        // List<FMSARIModel> result = new ArrayList<>();
        Integer result2 = -1;

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
            // Close the connection
            connection.disconnect();

            /*
              log.info("FMSARI JSON Request: " + stringBody.substring(0,450));
              for(int i = 449; i < stringBody.length(); i=i+499) {
              if(i+500 < stringBody.length())
              log.info(stringBody.substring(i, i+500));
              else
              log.info(stringBody.substring(i, stringBody.length()-1));
              }
              
              log.info("FMSARI Response Body: ");
              for(int i = 0; i < response.toString().length(); i+=500) {
              if(i+500 < response.toString().length())
              log.info(response.toString().substring(i, i+500));
              else
              log.info(response.toString().substring(i, response.toString().length()-1));
              }
             */

            String data = response.toString();
            Gson gson = new Gson();

            try {
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("FMSARI");

                // Check if request body is too large
                String requestBodyToLog;
                if (stringBody != null && stringBody.length() >= 24999) {
                    requestBodyToLog = javaUrl + "api/fms/v1/getfmsarijson?rms_batch_no="
                            + fmsariModel.getRms_batch_no();
                } else {
                    requestBodyToLog = stringBody;
                }

                extAudit.setI_request_body(requestBodyToLog);
                extAudit.setI_response_body(data);
                extAudit.setI_rms_batch_no(fmsariModel.getRms_batch_no());
                extAudit.setI_direction("Outgoing");
                extAudit.setI_remark(null);

                commonSvc.sp_insextaudit(extAudit);

            } catch (Exception e) {
                log.error("Error in sp_insextaudit for FMS ARI: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }

            FMSARIModel fmsari = new FMSARIModel();
            // Parse response to JSON
            // JsonObject json = JsonParser.parseString(data).getAsJsonObject();
            // if(fmsariModel.getGeneratePDF() != null && fmsariModel.getGeneratePDF() > 0)
            // {
            
            if (response.toString().contains("\"Status\"")) {
                FMSARIModel fmsarijson = gson.fromJson(data, FMSARIModel.class);
                fmsari.setResp_attr_ext_sys(fmsarijson.getAttributeEXTSYSTEM());
                fmsari.setFms_ref_no(fmsarijson.getReferenceNbr());
                if (response.toString().contains("\"CustomerOrder\"")) {
                    fmsari.setResp_co(fmsarijson.getCustomerOrder());
                } else {
                    fmsari.setResp_co(fmsarijson.getCustomerOder());
                }
                fmsari.setResp_status(fmsarijson.getStatus());
                fmsari.setResp_msg(fmsarijson.getMessage());
                fmsari.setResp_dt(fmsarijson.getDate());
            } else {
                data = data.replace("files:put", "filesput");
                FMSARIReplyJSONV2 fmsarijson = gson.fromJson(data, FMSARIReplyJSONV2.class);

                fmsari.setResp_attr_ext_sys(
                        fmsarijson.getCustom().getCurrentDocument().getAttributeSYSNAME().getValue());
                fmsari.setFms_ref_no(fmsarijson.getReferenceNbr().getValue());
                fmsari.setResp_co(fmsarijson.getCustomerOrder().getValue());
                fmsari.setResp_status("200");
                if (fmsarijson.getStatus() != null)
                    fmsari.setResp_msg(
                            fmsarijson.getStatus().getValue() != null ? fmsarijson.getStatus().getValue() : "");
                fmsari.setResp_dt(fmsarijson.getDate().getValue().substring(0, 19).replace("T", " "));
                
                // System.out.println("DEBUG: doc in FMS JSON: " +
                // fmsarijson.getDocument().getValue());
                if (fmsarijson.getDocument() != null && (fmsarijson.getDocument().getValue() != null
                        || !fmsarijson.getDocument().getValue().isEmpty())) {
                    Integer statusCode = 0;
                    if (fmsariModel.getBil_child_id() != null && fmsariModel.getBil_child_id() > 0) {
                        bRepo.sp_insbilchildimg(fmsariModel.getBil_child_id(), fmsarijson.getDocument().getValue());
                        statusCode = bRepo.sp_checkbilchildimg(fmsariModel.getBil_child_id());
                    }
                    if (statusCode < 1) {
                        if (statusCode == 0)
                            log.error("Exception in " + this.getClass().toString()
                                    + "fms_api_ari func - failed to insert bill img due to: "
                                    + "Bad bil_child_id in fmsarimodel: "
                                    + Integer.toString(fmsariModel.getBil_child_id()));
                        else if (statusCode < 0)
                            log.error("Exception in " + this.getClass().toString()
                                    + "fms_api_ari func - failed to insert bill img due to: "
                                    + "sp_insbilchildimg - Param(" + Integer.toString(fmsariModel.getBil_child_id())
                                    + ", " + fmsarijson.getDocument().getValue().substring(0, 50) + "...)");
                    }
                }
            }

            // result.add(fmsari);
            // result2 = this.sp_updfmsari(fmsari.getResp_attr_ext_sys(),
            // fmsari.getFms_ref_no(),
            // fmsari.getResp_co(),
            // fmsari.getResp_status(),
            // fmsari.getResp_msg(),
            // fmsari.getResp_dt());
            /*
              System.out.println("DEBUG: ");
              System.out.println(fmsari.getResp_attr_ext_sys());
              System.out.println(fmsari.getFms_ref_no());
              System.out.println(fmsari.getResp_co());
              System.out.println(fmsari.getResp_status());
              System.out.println(fmsari.getResp_msg());
              System.out.println(fmsari.getResp_dt());
             */
            result2 = this.sp_updfmsari(fmsari);

        } catch (Exception e) {
            log.error("fms_api_ari", e);
            e.printStackTrace();
        }

        return result2;
    }

}
