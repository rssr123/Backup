package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

public interface IOTCBankInSlipInterface {
    
    List<Object[]> sp_getotcbisinfo(String i_branch_cd, Date i_bal_date);

    List<Object[]> sp_getotcbiscash(String i_branch_cd, Date i_bal_date);

    List<Object[]> sp_getotcbisphy(String i_branch_cd, Date i_bal_date);

    BigInteger sp_insbankinslip(String i_branch_cd, Date i_bal_date, String i_ssm4uuserrefno);

    List<Object[]> sp_getotcfmsari(String i_otc_type, Date i_dt_balancing);


}
