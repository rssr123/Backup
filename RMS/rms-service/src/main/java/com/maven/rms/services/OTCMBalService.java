package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IOTCMBalService;
import com.maven.rms.models.OTCBalCash;
import com.maven.rms.models.OTCBalEMV;
import com.maven.rms.models.OTCBalInfo;
import com.maven.rms.models.OTCBalRC;
import com.maven.rms.models.OTCCtrBalCol;
import com.maven.rms.models.OTCCtrBalPhy;
import com.maven.rms.repositories.OTCMBalRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OTCMBalService implements IOTCMBalService{
     private final OTCMBalRepository otcMBalRepository;

    public OTCMBalService(OTCMBalRepository otcMBalRepository)
    {
        this.otcMBalRepository = otcMBalRepository;
    }

    @Override
    public List<OTCBalInfo> sp_getotcmdetails(String i_branch_cd, Date i_bal_date){
       List<OTCBalInfo> getotcdetails = Collections.emptyList();

       List<Object[]> objects = otcMBalRepository.sp_getotcmdetails(i_branch_cd, i_bal_date);

       getotcdetails = convertOTCMBalInfo(objects);

       return getotcdetails;
    }
    
    @Override
    public List<OTCBalRC> sp_getotcmrc(String i_branch_cd, Date i_bal_date){
        List<OTCBalRC> getotcrc = Collections.emptyList();

        List<Object[]> objects = otcMBalRepository.sp_getotcmrc(i_branch_cd, i_bal_date);
 
        getotcrc = convertOTCMBalRC(objects);
 
        return getotcrc;
    }

    @Override
    public List<OTCCtrBalCol> sp_getotcmemvcol(String i_branch_cd, Date i_bal_date, Integer i_page, Integer i_size){
        List<OTCCtrBalCol> getotcemvcol = Collections.emptyList();

        List<Object[]> objects = otcMBalRepository.sp_getotcmemvcol(i_branch_cd, i_bal_date, i_page, i_size);
 
        getotcemvcol = convertOTCMBalEMV(objects);
 
        return getotcemvcol;
    }

    @Override
    public List<OTCBalCash> sp_getotcmcashcol(String i_branch_cd, Date i_bal_date){
        List<OTCBalCash> getotccashcol = Collections.emptyList();

        List<Object[]> objects = otcMBalRepository.sp_getotcmcashcol(i_branch_cd, i_bal_date);
 
        getotccashcol = convertOTCMBalCash(objects);
 
        return getotccashcol;
    }

    @Override
    public List<OTCCtrBalPhy> sp_getotcmphyinfo(String i_branch_cd, Date i_bal_date){
        List<OTCCtrBalPhy> getotcphyinfo = Collections.emptyList();

        List<Object[]> objects = otcMBalRepository.sp_getotcmphyinfo(i_branch_cd, i_bal_date);
 
        getotcphyinfo = convertOTCMBalPhy(objects);
 
        return getotcphyinfo;
    }

    @Override
    public  List<OTCBalEMV> sp_getotcmbaldoclist(String i_branch_cd, Date i_bal_date){
        List<OTCBalEMV> getotcbaldoclist = Collections.emptyList();

        List<Object[]> objects = otcMBalRepository.sp_getotcmbaldoclist(i_branch_cd, i_bal_date);
 
        getotcbaldoclist = convertOTCMBalDocList(objects);
 
        return getotcbaldoclist;
    }

    //Convert Balancing Info
    private List<OTCBalInfo> convertOTCMBalInfo(List<Object[]> objects){

        List<OTCBalInfo> OTCBalList = new ArrayList<>();

        for (Object[] obj : objects){
            OTCBalInfo OTCBalListing = new OTCBalInfo();
            OTCBalListing.setBranch_cd((String) obj[0]);
            OTCBalListing.setDt_bal((Date) obj[1]);
            OTCBalListing.setNo_of_counters((Integer) obj[2]);
            OTCBalListing.setNo_of_txn((Integer) obj[3]);
            OTCBalListing.setTotal((BigDecimal) obj[4]);
            OTCBalListing.setTotal_emv((BigDecimal) obj[5]);
            OTCBalListing.setTotal_phy((BigDecimal) obj[6]);
            OTCBalListing.setTotal_cash((BigDecimal) obj[7]);
            OTCBalListing.setTotal_che((BigDecimal) obj[8]);
            OTCBalListing.setTotal_bd((BigDecimal) obj[9]);
            OTCBalListing.setTotal_mo((BigDecimal) obj[10]);
            OTCBalListing.setNo_of_rcpt_can((Integer) obj[11]);
            OTCBalListing.setStatus((String) obj[12]);
            OTCBalList.add(OTCBalListing);
        }

        return OTCBalList;
    }

    //Convert Balancing Receipt Cancellation
    private List<OTCBalRC> convertOTCMBalRC(List<Object[]> objects){

        List<OTCBalRC> OTCBalList = new ArrayList<>();

        for (Object[] obj : objects){
            OTCBalRC OTCBalListing = new OTCBalRC();
            OTCBalListing.setColl_slip_no((String) obj[0]);
            OTCBalListing.setOrn_no((String) obj[1]);
            OTCBalListing.setRcpt_no((String) obj[2]);
            OTCBalListing.setTotalAmount((BigDecimal) obj[3]);
            OTCBalListing.setOtc_pymt_mode((String) obj[4]);
            OTCBalListing.setRequested_by((String) obj[5]);
            OTCBalListing.setApproved_by((String) obj[6]);
            OTCBalListing.setRemark((String) obj[7]);
            OTCBalListing.setMtt_id((BigInteger) obj[8]);
            OTCBalListing.setOtc_id((Integer) obj[9]);
            OTCBalList.add(OTCBalListing);
        }

        return OTCBalList;
    }

    //Convert Balancing EMV Collection
    private List<OTCCtrBalCol> convertOTCMBalEMV(List<Object[]> objects){

        List<OTCCtrBalCol> OTCBalList = new ArrayList<>();

        for (Object[] obj : objects){
            OTCCtrBalCol OTCBalListing = new OTCCtrBalCol();
            OTCBalListing.setCol_slip_no((String) obj[0]);
            OTCBalListing.setOrn_no((String) obj[1]);
            OTCBalListing.setTrans_trace((String) obj[2]);
            OTCBalListing.setBatch_no((String) obj[3]);
            OTCBalListing.setHost_no((String) obj[4]);
            OTCBalListing.setT_id((String) obj[5]);
            OTCBalListing.setAmount((BigDecimal) obj[6]);
            OTCBalListing.setTotal((Integer) obj[7]);            
            OTCBalList.add(OTCBalListing);
        }

        return OTCBalList;
    }
    
    //Convert Balancing Cash Collection
    private List<OTCBalCash> convertOTCMBalCash(List<Object[]> objects){

        List<OTCBalCash> OTCBalList = new ArrayList<>();

        for (Object[] obj : objects){
            OTCBalCash OTCBalListing = new OTCBalCash();
            OTCBalListing.setParam_cd((String) obj[0]);
            OTCBalListing.setDenomination((String) obj[1]);
            OTCBalListing.setQuantity((Integer) obj[2]);
            OTCBalListing.setTotal((BigDecimal) obj[3]);                      
            OTCBalList.add(OTCBalListing);
        }

        return OTCBalList;
    }

    //Convert Balancing Physical Collection
    private List<OTCCtrBalPhy> convertOTCMBalPhy(List<Object[]> objects){

        List<OTCCtrBalPhy> OTCBalList = new ArrayList<>();

        for (Object[] obj : objects){
            OTCCtrBalPhy OTCBalListing = new OTCCtrBalPhy();
            OTCBalListing.setTotal_cash_amt((BigDecimal) obj[0]);
            OTCBalListing.setCol_slip_no((String) obj[1]);
            OTCBalListing.setOrn_no((String) obj[2]);
            OTCBalListing.setChe_bank_nm((String) obj[3]);
            OTCBalListing.setChe_payer_nm((String) obj[4]);
            OTCBalListing.setChe_ba_acct_no((String) obj[5]);
            OTCBalListing.setChe_no((String) obj[6]);
            OTCBalListing.setChe_date((Date) obj[7]);
            OTCBalListing.setChe_amt((BigDecimal) obj[8]);
            OTCBalListing.setBd_bank_nm((String) obj[9]);
            OTCBalListing.setBd_no((String) obj[10]);
            OTCBalListing.setBd_date((Date) obj[11]);
            OTCBalListing.setBd_amt((BigDecimal) obj[12]);
            OTCBalListing.setMo_rm_no((String) obj[13]);
            OTCBalListing.setMo_date((Date) obj[14]);
            OTCBalListing.setMo_payer_nm((String) obj[15]);
            OTCBalListing.setMo_id_no((String) obj[16]);
            OTCBalListing.setMo_contact_no((String) obj[17]);
            OTCBalListing.setMo_amt((BigDecimal) obj[18]);
            OTCBalListing.setDetail_type((String) obj[19]);
            OTCBalListing.setId((BigInteger) obj[20]);
            OTCBalListing.setOtc_id((BigInteger) obj[21]);                                
            OTCBalList.add(OTCBalListing);
        }

        return OTCBalList;
    }

    private List<OTCBalEMV> convertOTCMBalDocList(List<Object[]> objects){

        List<OTCBalEMV> OTCBalDoc = new ArrayList<>();

        for (Object[] obj : objects){
            OTCBalEMV OTCBalEmv = new OTCBalEMV();
            OTCBalEmv.setDocID((BigInteger) obj[0]);
            OTCBalEmv.setFileNm((String) obj[1]);
            OTCBalEmv.setTerminalId((String) obj[2]);
            OTCBalEmv.setDtSettlement((Date) obj[3]);
            OTCBalEmv.setBatchNo((String) obj[4]);
            OTCBalEmv.setBatchCount((String) obj[5]);            
            OTCBalEmv.setBatchAmt((String) obj[6]);
            OTCBalEmv.setTotal((BigDecimal) obj[7]);                                  
            OTCBalDoc.add(OTCBalEmv);
        }

        return OTCBalDoc;
    }

}
