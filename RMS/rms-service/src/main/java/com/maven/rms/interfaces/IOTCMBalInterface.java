package com.maven.rms.interfaces;

import java.sql.Date;
import java.util.List;

public interface IOTCMBalInterface {
    List<Object[]> sp_getotcmdetails(String i_branch_cd, Date i_bal_date);
    
    List<Object[]> sp_getotcmrc(String i_branch_cd, Date i_bal_date);

    List<Object[]> sp_getotcmemvcol(String i_branch_cd, Date i_bal_date, Integer i_page, Integer i_size);

    List<Object[]> sp_getotcmcashcol(String i_branch_cd, Date i_bal_date);
    
    List<Object[]> sp_getotcmphyinfo(String i_branch_cd, Date i_bal_date);

    List<Object[]> sp_getotcmbaldoclist(String i_branch_cd, Date i_bal_date);
}
