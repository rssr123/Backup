package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IRIPLService;
import com.maven.rms.models.RIPLRealizedRequest;
import com.maven.rms.models.RIPLRecognitionRequest;
import com.maven.rms.models.RIPLRequest;
import com.maven.rms.models.RIPLResponse;
import com.maven.rms.repositories.RIPLRepository;

@Service
@Slf4j
public class RIPLService implements IRIPLService {

    //private static final Logger logger = LoggerFactory.getLogger(StoreProcedureService.class);
    private final RIPLRepository riplRepository;

    public RIPLService(RIPLRepository riplRepository) {
        this.riplRepository = riplRepository;
    }

    @Override
    // public BigInteger sp_insRIPL(String i_txn_type, String i_entity_type, String i_entity_no, String i_calendar_yr, 
    //                             String i_dt_due, String i_ripl_ctype, String i_created_by, String i_modified_by) {
    public BigInteger sp_insRIPL(RIPLRecognitionRequest riplRecognitionRequest) {

        BigInteger result=null;

        // result = riplRepository.sp_insRIPL(  i_txn_type,  i_entity_type,  i_entity_no,  i_calendar_yr, i_dt_due,  i_ripl_ctype,  i_created_by,  i_modified_by);
        result = riplRepository.sp_insRIPL(riplRecognitionRequest);

        return result;
    }

    @Override
    // public BigInteger sp_updRIPL(String i_txn_type, String i_entity_type, String i_entity_no, String i_calendar_yr, 
    //                       String i_rcpt_no, String i_modified_by) {
    public BigInteger sp_updRIPL(RIPLRealizedRequest riplRealizedRequest) {
        BigInteger result=null;

            result = riplRepository.sp_updRIPL(riplRealizedRequest);

        return result;
    }

    @Override
    // public List<RIPLResponse> sp_getRIPL(Integer i_page,Integer i_size,BigInteger i_ripl_id,String i_txn_type, String i_entity_type, String i_entity_no, String i_calendar_yr, 
    //                       String i_dt_due, String i_ripl_ctype, String i_status){
    public List<RIPLResponse> sp_getRIPL(RIPLRequest RIPLRequest){
        List<RIPLResponse> result = Collections.emptyList();


            // List<Object[]> objects = riplRepository.sp_getRIPL(i_page, i_size,i_ripl_id, i_txn_type, i_entity_type, i_entity_no, i_calendar_yr, i_dt_due, i_ripl_ctype, i_status); 
            List<Object[]> objects = riplRepository.sp_getRIPL(RIPLRequest); 
            result = convertToRIPLResponseList(objects);
            
        return result;
    }

    private List<RIPLResponse> convertToRIPLResponseList(List<Object[]> objects) {
    List<RIPLResponse> riplResponseList = new ArrayList<>();

    for (Object[] obj : objects) {
        RIPLResponse riplResponse = new RIPLResponse();
        riplResponse.setI_ripl_id((BigInteger) obj[0]);
        riplResponse.setI_txn_type((String) obj[1]);
        riplResponse.setI_entity_type((String) obj[2]);
        riplResponse.setI_entity_no((String) obj[3]);
        riplResponse.setI_calendar_yr((String) obj[4]);
        riplResponse.setI_dt_due((String) obj[5]);
        riplResponse.setI_dt_impair((String) obj[6]);
        riplResponse.setI_dt_writeoff((String) obj[7]);
        riplResponse.setI_ripl_ctype((String) obj[8]);
        riplResponse.setI_accr_amt((BigDecimal) obj[9]);
        riplResponse.setI_rcpt_no((String) obj[10]);
        riplResponse.setI_dt_created((String) obj[11]);
        riplResponse.setI_status((String) obj[12]);
        riplResponse.setTotal((Integer) obj[13]);
        riplResponseList.add(riplResponse);
    }

    return riplResponseList;
}
    
    @Override
    public BigInteger sp_updRIPLImpairStatus() {
        BigInteger result=null;

            result = riplRepository.sp_updRIPLImpairStatus();
            
        return result;
    }

    @Override
    public BigInteger sp_updRIPLWriteOffStatus() {
        BigInteger result=null;

            result = riplRepository.sp_updRIPLWriteOffStatus();

        return result;
    }

//#region old
//     @Override
//     public List<BigInteger>  sp_getRIPLID() {

//         List<BigInteger> result = Collections.emptyList();

//         try {
//             result = convertToStringList(riplRepository.sp_getRIPLID());
            
//         } catch (Exception e) {
//             e.printStackTrace();
//         }

//         return result;
//     }

//     private List<BigInteger> convertToStringList(List<Object[]> objects) {
//     List<BigInteger> riplID = new ArrayList<>();

//     for (Object[] obj : objects) {
//         BigInteger riplResponse = (BigInteger) obj[0];
//         riplID.add(riplResponse);
//     }

//     return riplID;
// }

//#endregion

    @Override
    public Integer sp_checkExist(String entityType,String entity,String txnType, String calendarYr){
        Integer result=null;

        result = riplRepository.sp_checkExist(entityType,entity,txnType,calendarYr);
        
        return result;
    }

}
