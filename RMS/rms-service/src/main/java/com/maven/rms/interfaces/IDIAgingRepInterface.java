package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.util.List;

import com.maven.rms.models.DeferredIncomeAgingRequest;

public interface IDIAgingRepInterface {
    
    BigInteger sp_insdiagingrpt(DeferredIncomeAgingRequest DIRequest, String i_p_email, String i_created_by,
            String i_modified_by);

    List<Object[]> sp_getdiaginglistingrpt(DeferredIncomeAgingRequest DIRequest);

    Integer sp_upddiagingrpt(DeferredIncomeAgingRequest DIRequest, String i_modified_by);

    List<Object[]> sp_getdiagingrpt(BigInteger i_rpt_di_age_id);

    Integer sp_getdiagequeuerpt();

    List<Object[]> sp_getpendingdiagingrpt();

    Integer sp_getpendingdiagingrptbyid(BigInteger i_rpt_di_age_id);
}
