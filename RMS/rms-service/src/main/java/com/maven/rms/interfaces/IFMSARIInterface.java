package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.maven.rms.models.FMSARIImmediateRequest;
import com.maven.rms.models.FMSARIModel;

public interface IFMSARIInterface {

    public List<Object[]> sp_getfmsmtt();

BigInteger sp_insfmsmtth(String customer, LocalDateTime inv_dt);

    // public BigInteger sp_insfmsmtth(BigInteger mtt_pg_id, BigDecimal pg_pymt_amt, int qty, String item_desc,
    // BigDecimal unit_fee, String rcpt_no, String cust_nm, String entity_nm, String entity_no, 
    // String entity_type, BigDecimal gross_amt, String fee_detail_id, String pg_pymt_method, BigDecimal tax_amt, String customer);

    // public BigInteger sp_insfmsmttb(BigInteger mtt_pg_id, BigDecimal pg_pymt_amt, int qty, String item_desc,
    // BigDecimal unit_fee, String rcpt_no, String cust_nm, String entity_nm, String entity_no, 
    // String entity_type, BigDecimal gross_amt, String fee_detail_id, String pg_pymt_method, BigDecimal tax_amt, 
    // String customer, BigInteger hid, String item_ref_no, String cp_no);

    public BigInteger sp_insfmsmttb(FMSARIModel fmsariModel, BigInteger hid, Integer flag);

    public List<Object[]> sp_getfmsari();
    public List<Object[]> sp_getfmsariimmediate(FMSARIImmediateRequest fmsARIImmediateRequest);

    // public Integer sp_updfmsari(String resp_attr_ext_sys, String fms_ref_no, String resp_co,
    // String resp_status, String resp_msg, String resp_dt);

    public Integer sp_updfmsari(FMSARIModel fmsariModel);
    List<Object[]> sp_getfmsarijson(String i_rms_batch_no);
}
