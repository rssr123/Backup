package com.maven.rms.interfaces;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import com.maven.rms.models.FMSCRMemo;
import com.maven.rms.models.FMSDRMemo;

public interface IFMSCRMemoInterface {

    public List<Object[]> sp_getfmsrcpgmtt();
    List<Object[]> sp_getfmscreditdebit();
    public List<Object[]> sp_getfmscnbr();
    public Integer sp_insfmsrcpgmtt_b(BigInteger crmemo_hid, FMSCRMemo fmscrMemo, Integer flag);
    public List<Object[]> sp_getfmscrmemo();
    public Integer sp_updfmscrmemo(BigInteger crmemo_hid, FMSCRMemo fmscrMemo);
    public BigInteger sp_insfmsrcpgmtt_h(BigDecimal pg_pymt_amt, Integer flag);
    Integer sp_insfmsrcpgmtt_f(BigInteger crmemo_hid, BigInteger mtt_pg_id, Integer i_is_drmemo);
    public Integer creditNonBilCrMemo(String orn_no);
    Integer sp_insfmsrcpgmtt_b(BigInteger crmemo_hid, FMSDRMemo fmsdrMemo, Integer flag);
}
