package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.util.List;

import com.maven.rms.models.FMSRILTJN;
import com.maven.rms.models.FMSRIPL;

public interface IFMSRIPLInterface {
    List<Object[]> sp_getfmsripla();

    // BigInteger sp_insfmsripla(BigInteger i_audit_id, BigInteger i_ripl_id, Date i_dt_txn,
    //                         BigDecimal i_accr_amt_af, String i_action_type);
    
    BigInteger sp_insfmsripla(FMSRIPL fmsripl);

    List<Object[]> sp_getfmsjnrilt();
    Integer sp_insfmsjnrilt(FMSRILTJN request);


}
