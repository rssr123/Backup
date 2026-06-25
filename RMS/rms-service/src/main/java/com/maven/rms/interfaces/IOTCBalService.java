package com.maven.rms.interfaces;

import java.sql.SQLException;
import java.text.ParseException;
import java.sql.Date;
import java.util.List;

import com.maven.rms.models.OTCBalCash;
import com.maven.rms.models.OTCBalEMV;
import com.maven.rms.models.OTCBalInfo;
import com.maven.rms.models.OTCBalRC;
import com.maven.rms.models.OTCBalancingDocRequest;
import com.maven.rms.models.OTCBalancingRequest;
import com.maven.rms.models.OTCCtrBalCol;
import com.maven.rms.models.OTCCtrBalPhy;

public interface IOTCBalService {
    List<OTCBalInfo> sp_getotcdetails(String i_branch_cd, Date i_bal_date);
    
    List<OTCBalRC> sp_getotcrc(String i_branch_cd, Date i_bal_date);

    List<OTCCtrBalCol> sp_getotcemvcol(String i_branch_cd, Date i_bal_date, Integer i_page, Integer i_size);

    List<OTCBalCash> sp_getotccashcol(String i_branch_cd, Date i_bal_date);
    
    List<OTCCtrBalPhy> sp_getotcphyinfo(String i_branch_cd, Date i_bal_date);

    List<OTCBalEMV> sp_getotcbaldoclist(String i_branch_cd, Date i_bal_date);

    String sp_getotcbaldoc(OTCBalancingDocRequest bodyRequest) throws SQLException, ParseException;

    Integer sp_insotcbaldoc(OTCBalancingDocRequest bodyRequest) throws SQLException, ParseException ;

    Integer sp_insotcdbalcashbytotal(List<OTCBalancingRequest> ListBodyRequest, OTCBalancingRequest BodyRequest);

    Integer sp_insotcbalcashbytotal(List<OTCBalancingRequest> ListBodyRequest, OTCBalancingRequest BodyRequest);

    Integer sp_updotcbalcashbytotal(OTCBalancingRequest BodyRequest);

    Integer sp_insotccashgrandtotal(OTCBalancingRequest bodyRequest);

    Integer sp_updotcdailybalstatus(OTCBalancingRequest bodyRequest);
}
