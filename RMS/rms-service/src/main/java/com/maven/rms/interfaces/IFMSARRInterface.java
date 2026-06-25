package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.util.List;

import com.maven.rms.models.FMSARR;

public interface IFMSARRInterface {

    public List<Object[]> sp_getfmsrcbank();

    List<Object[]> sp_getfmsarrdebit(BigInteger rc_pg_id);

    // public Integer sp_insfmsarr(BigInteger rc_bank_id, BigInteger rc_pg_id,
    // BigDecimal credit, BigDecimal mdr_amt,
    // BigInteger mtt_pg_id, String acct_cd, String cust);

    Integer sp_insfmsarr(FMSARR fmsarr, Integer hid, Integer flag);

    public List<Object[]> sp_getfmsarr();

    // public Integer sp_updfmsarr(BigInteger arr_hid, String resp_attr_ext_sys,
    // String fms_ref_no,
    // String resp_co, String resp_status, String resp_msg, Date resp_dt);

    public Integer sp_updfmsarr(FMSARR fmsarr);
    Integer sp_insfmsarrnonrmsrecon(FMSARR fmsarr);
}
