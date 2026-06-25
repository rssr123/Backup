package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Date;
import java.util.List;

import com.maven.rms.models.OTCBalancingDocRequest;
import com.maven.rms.models.OTCBalancingRequest;

public interface IOTCBalInterface {

    List<Object[]> sp_getotcdetails(String i_branch_cd, Date i_bal_date);
    
    List<Object[]> sp_getotcrc(String i_branch_cd, Date i_bal_date);

    List<Object[]> sp_getotcemvcol(String i_branch_cd, Date i_bal_date, Integer i_page, Integer i_size);

    List<Object[]> sp_getotccashcol(String i_branch_cd, Date i_bal_date);
    
    List<Object[]> sp_getotcphyinfo(String i_branch_cd, Date i_bal_date);

    List<Object[]> sp_getotcbaldoclist(String i_branch_cd, Date i_bal_date);

    Blob sp_getotcbaldoc(OTCBalancingDocRequest bodyRequest);

    BigInteger sp_insotcbaldoc(OTCBalancingDocRequest bodyRequest);

    BigInteger sp_insotcdbalcashbytotal(OTCBalancingRequest bodyRequest);

    BigInteger sp_insotcbalcashbytotal(OTCBalancingRequest bodyRequest);

    Integer sp_updotcbalcashbytotal(OTCBalancingRequest bodyRequest);

    Integer sp_insotccashgrandtotal(OTCBalancingRequest bodyRequest);

    Integer sp_updotcdailybalstatus(OTCBalancingRequest bodyRequest);

}
