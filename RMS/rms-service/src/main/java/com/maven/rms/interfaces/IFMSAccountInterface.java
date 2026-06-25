package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.maven.rms.models.FMSAccountRequest;

public interface IFMSAccountInterface {

    // public List<Object[]> sp_getfmsaccount(Integer i_page, Integer i_size,
    // String i_acct_nm, String i_acct_type, String i_acct_cd,
    // String i_modified_by, Date i_dt_modified);
    // public List<Object[]> sp_getfmsaccount(Integer i_page, Integer i_size,
    // String i_acct_nm, String i_acct_type, String i_acct_cd,
    // String i_modified_by, Date i_dt_modified, Date i_dt_modified_fr, Date
    // i_dt_modified_to);

    // public Integer sp_updfmsaccount(Integer i_fms_acct_id, String i_acct_nm,
    // String i_acct_type,
    // String i_acct_cd, String i_modified_by, String i_status) ;
    public List<Object[]> sp_getfmsaccount(FMSAccountRequest fmsAccountRequest);

    public Integer sp_updfmsaccount(FMSAccountRequest fmsAccountRequest);
}
