package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.maven.rms.models.RIPLRealizedRequest;
import com.maven.rms.models.RIPLRecognitionRequest;
import com.maven.rms.models.RIPLRequest;
import com.maven.rms.models.RIPLResponse;

public interface IRIPLService {

    // BigInteger sp_insRIPL(String i_txn_type, String i_entity_type, String i_entity_no, String i_calendar_yr, 
    //     String i_dt_due, String i_ripl_ctype, String i_created_by, String i_modified_by);

    BigInteger sp_insRIPL(RIPLRecognitionRequest riplRecognitionRequest);

    // List<RIPLResponse> sp_getRIPL(Integer i_page,Integer i_size, BigInteger i_ripl_id, String i_txn_type, String i_entity_type, String i_entity_no, String i_calendar_yr, 
    //     String i_dt_due, String i_ripl_ctype, String i_status);
    List<RIPLResponse> sp_getRIPL(RIPLRequest RIPLRequest);

    // BigInteger sp_updRIPL(String i_txn_type, String i_entity_type, String i_entity_no, String i_calendar_yr, 
    //     String i_rcpt_no, String i_modified_by);
    BigInteger sp_updRIPL(RIPLRealizedRequest riplRealizedRequest);
    
    // List<BigInteger> sp_getRIPLID();

    BigInteger sp_updRIPLImpairStatus();

    BigInteger sp_updRIPLWriteOffStatus();

    Integer sp_checkExist(String entityType,String entity,String txnType, String calendarYr);
    
}
