package com.maven.rms.interfaces;

import java.sql.Date;
import java.util.List;

import com.maven.rms.models.OTCBalCash;
import com.maven.rms.models.OTCBalEMV;
import com.maven.rms.models.OTCBalInfo;
import com.maven.rms.models.OTCBalRC;
import com.maven.rms.models.OTCCtrBalCol;
import com.maven.rms.models.OTCCtrBalPhy;

public interface IOTCMBalService {

    List<OTCBalInfo> sp_getotcmdetails(String i_branch_cd, Date i_bal_date);
    
    List<OTCBalRC> sp_getotcmrc(String i_branch_cd, Date i_bal_date);

    List<OTCCtrBalCol> sp_getotcmemvcol(String i_branch_cd, Date i_bal_date, Integer i_page, Integer i_size);

    List<OTCBalCash> sp_getotcmcashcol(String i_branch_cd, Date i_bal_date);
    
    List<OTCCtrBalPhy> sp_getotcmphyinfo(String i_branch_cd, Date i_bal_date);

    List<OTCBalEMV> sp_getotcmbaldoclist(String i_branch_cd, Date i_bal_date);

}
