package com.maven.rms.interfaces;

import java.util.List;
import java.math.BigInteger;
import com.maven.rms.models.DeferredIncome;
import com.maven.rms.models.DeferredIncomeTermination;

public interface IDeferredIncomeService {

    // BigInteger sp_insdi(String i_fee_detail_id,String i_txn_type, String
    // i_entity_no, String i_entity_type, Date i_dt_effective,
    // Date i_dt_expiry, String i_item_ref_no, String i_approval_status, Date
    // i_dt_approval);

    // BigInteger sp_insdi_tmn_log(String i_txn_type, String i_entity_no, String
    // i_entity_type,
    // Date i_dt_termination, Date i_dt_approval);

    // List<DeferredIncome> sp_getdi(Integer i_page, Integer i_size, BigInteger
    // i_di_id, String i_fee_detail_id,
    // String i_txn_type, String i_entity_type, String i_entity_no, Date
    // i_dt_effective, Date i_dt_expiry,
    // String i_item_ref_no, String i_approval_status, Date i_dt_approval, String
    // i_status);
    BigInteger sp_insdi(DeferredIncome recognitionRequest);

    BigInteger sp_insdi_tmn_log(DeferredIncomeTermination terminationRequest);

    List<DeferredIncome> sp_getdi(DeferredIncome deferredIncome);
}