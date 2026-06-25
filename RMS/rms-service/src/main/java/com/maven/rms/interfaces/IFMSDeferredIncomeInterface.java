package com.maven.rms.interfaces;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.maven.rms.models.FMSDeferredIncome;

public interface IFMSDeferredIncomeInterface {
    List<Object[]> sp_getfmsdia();

    // BigInteger sp_insfmsdia(BigInteger i_audit_id, BigInteger i_di_id, Date i_dt_txn,
    //                         BigDecimal i_bal_di_amt_af, BigDecimal i_unit_fee, String i_action_type);

    BigInteger sp_insfmsdia(FMSDeferredIncome fmsDeferredIncome);
}
