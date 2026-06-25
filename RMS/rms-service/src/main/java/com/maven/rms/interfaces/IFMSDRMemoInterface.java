package com.maven.rms.interfaces;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.maven.rms.models.FMSCRMemo;
import com.maven.rms.models.FMSDRMemo;

public interface IFMSDRMemoInterface {

    // public List<Object[]> sp_getfmsrcpgtxn();

    // public BigInteger sp_insfmsrcpgtxn_h(BigDecimal pg_pymt_amt);

    // public Integer sp_insfmsrcpgtxn_b(BigInteger drmemo_hid, String pg_pymt_method, BigInteger mtt_pg_id, int qty,
    //                             String item_desc, BigDecimal unit_fee, String entity_nm, String entity_no, String entity_type, BigDecimal gross_amt,
    //                             String fee_detail_id, BigDecimal tax_amt, String rcpt_no, String cust_nm);

    // public Integer sp_insfmsrcpgtxn_f(BigInteger drmemo_hid, BigInteger mtt_pg_id);

    // public List<Object[]> sp_getfmsdrmemo();

    // public Integer sp_updfmsdrmemo(BigInteger drmemo_hid, String resp_attr_ext_sys, String fms_ref_no,
    //                                 String resp_co, String resp_status, String resp_msg, Date resp_dt);

    public List<Object[]> sp_getfmsrcpgtxn();

    public BigInteger sp_insfmsrcpgtxn_h(BigDecimal pg_pymt_amt, Integer flag);

    public Integer sp_insfmsrcpgtxn_b(BigInteger drmemo_hid, FMSDRMemo fmsdrMemo, Integer flag);

    public Integer sp_insfmsrcpgtxn_f(BigInteger drmemo_hid, FMSDRMemo fmsdrMemo);

    public List<Object[]> sp_getfmsdrmemo();

    public Integer sp_updfmsdrmemo(BigInteger drmemo_hid, FMSDRMemo fmsdrMemo);
    Integer sp_insfmsrcpgtxn_b(BigInteger drmemo_hid, FMSCRMemo fmscrMemo, Integer flag);
    Integer sp_insfmsrcpgtxn_f(BigInteger drmemo_hid, FMSCRMemo fmscrMemo);
    Integer sp_updfmsdrmemohid(BigInteger drmemo_hid, BigInteger crmemo_hid);
}
