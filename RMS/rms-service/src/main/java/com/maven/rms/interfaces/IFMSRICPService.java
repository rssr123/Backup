package com.maven.rms.interfaces;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.maven.rms.models.FMSRICP;

public interface IFMSRICPService {
    List<Object[]> sp_getfmsricpa();

    // BigInteger sp_insfmsricpa(BigInteger i_audit_id, BigInteger i_ricp_id, Date i_dt_txn,
    //                         BigDecimal i_accr_amt_af, String i_action_type);

    BigInteger sp_insfmsricpa(FMSRICP fmsricp);
}
