package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IOTCBalService;
import com.maven.rms.models.OTCBalCash;
import com.maven.rms.models.OTCBalEMV;
import com.maven.rms.models.OTCBalInfo;
import com.maven.rms.models.OTCBalRC;
import com.maven.rms.models.OTCBalancingDocRequest;
import com.maven.rms.models.OTCBalancingRequest;
import com.maven.rms.models.OTCCtrBalCol;
import com.maven.rms.models.OTCCtrBalPhy;
import com.maven.rms.repositories.OTCBalRespository;
import com.maven.rms.repositories.OTCBalancingRequestRepository;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class OTCBalService implements IOTCBalService{
    private final OTCBalRespository otcBalRepository;
    private final OTCBalancingRequestRepository otcBalancingRequestRepository;

    public OTCBalService(OTCBalRespository otcBalRepository, OTCBalancingRequestRepository otcBalancingRequestRepository)
    {
        this.otcBalRepository = otcBalRepository;
        this.otcBalancingRequestRepository = otcBalancingRequestRepository;
    }

    @Override
    public List<OTCBalInfo> sp_getotcdetails(String i_branch_cd, Date i_bal_date){
       List<OTCBalInfo> getotcdetails = Collections.emptyList();

       List<Object[]> objects = otcBalRepository.sp_getotcdetails(i_branch_cd, i_bal_date);

       getotcdetails = convertOTCBalInfo(objects);

       return getotcdetails;
    }
    
    @Override
    public List<OTCBalRC> sp_getotcrc(String i_branch_cd, Date i_bal_date){
        List<OTCBalRC> getotcrc = Collections.emptyList();

        List<Object[]> objects = otcBalRepository.sp_getotcrc(i_branch_cd, i_bal_date);
 
        getotcrc = convertOTCBalRC(objects);
 
        return getotcrc;
    }

    @Override
    public List<OTCCtrBalCol> sp_getotcemvcol(String i_branch_cd, Date i_bal_date, Integer i_page, Integer i_size){
        List<OTCCtrBalCol> getotcemvcol = Collections.emptyList();

        List<Object[]> objects = otcBalRepository.sp_getotcemvcol(i_branch_cd, i_bal_date, i_page, i_size);
 
        getotcemvcol = convertOTCBalEMV(objects);
 
        return getotcemvcol;
    }

    @Override
    public List<OTCBalCash> sp_getotccashcol(String i_branch_cd, Date i_bal_date){
        List<OTCBalCash> getotccashcol = Collections.emptyList();

        List<Object[]> objects = otcBalRepository.sp_getotccashcol(i_branch_cd, i_bal_date);
 
        getotccashcol = convertOTCBalCash(objects);
 
        return getotccashcol;
    }

    @Override
    public List<OTCCtrBalPhy> sp_getotcphyinfo(String i_branch_cd, Date i_bal_date){
        List<OTCCtrBalPhy> getotcphyinfo = Collections.emptyList();

        List<Object[]> objects = otcBalRepository.sp_getotcphyinfo(i_branch_cd, i_bal_date);
 
        getotcphyinfo = convertOTCBalPhy(objects);
 
        return getotcphyinfo;
    }

    @Override
    public  List<OTCBalEMV> sp_getotcbaldoclist(String i_branch_cd, Date i_bal_date){
        List<OTCBalEMV> getotcbaldoclist = Collections.emptyList();

        List<Object[]> objects = otcBalRepository.sp_getotcbaldoclist(i_branch_cd, i_bal_date);
 
        getotcbaldoclist = convertOTCBalDocList(objects);
 
        return getotcbaldoclist;
    }

    @Override
    public String sp_getotcbaldoc(OTCBalancingDocRequest bodyRequest) throws SQLException, ParseException{
        String file_content = "";
        Blob file_content_b = null;

        file_content_b = otcBalRepository.sp_getotcbaldoc(bodyRequest);

        file_content = convertOTCBalDoc(file_content_b);

        return file_content;
    }

    @Override
    public Integer sp_insotcbaldoc(OTCBalancingDocRequest bodyRequest) throws SQLException, ParseException {
        BigInteger result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd yyyy HH:mm:ss 'GMT'Z '('z')'");

        if(bodyRequest.getBal_date() != null){
            java.sql.Date sqlDate = java.sql.Date.valueOf(
                new java.text.SimpleDateFormat("yyyy-MM-dd").format(bodyRequest.getBal_date())
            );

            bodyRequest.setI_bal_date(sqlDate);
        }

        if(bodyRequest.getDtSettlement() != null && bodyRequest.getDtSettlement().trim() != "")
        {
            // Step 1: Parse String into java.util.Date
            java.util.Date utilDate = sdf.parse(bodyRequest.getDtSettlement());

            // Step 2: Convert java.util.Date to java.sql.Date
            Date sqlDate = new Date(utilDate.getTime());

            // Step 3: set into bodyRequest
            bodyRequest.setI_dtSettlement(sqlDate);
        }

        // Decode Base64 content
        byte[] decodedBytes = decodeBase64(bodyRequest.getFileContent());

        if(decodedBytes.length != 0 ){
            Blob blob = new SerialBlob(decodedBytes);

            bodyRequest.setI_file_content(blob);
        }

        result = otcBalRepository.sp_insotcbaldoc(bodyRequest);

        if (result.signum() > 0) {
            return 1;
        } else {
            return 0;
        }
    }

    @Override
    public Integer sp_insotcdbalcashbytotal(List<OTCBalancingRequest> ListBodyRequest, OTCBalancingRequest bodyRequest){
        Integer result = 0;

        for(OTCBalancingRequest request : ListBodyRequest){

            if(request.getBal_date() != null){
                java.sql.Date sqlDate = java.sql.Date.valueOf(
                    new java.text.SimpleDateFormat("yyyy-MM-dd").format(request.getBal_date())
                );

                request.setBal_date(sqlDate);
            }

            request.setSsm4uuserrefno(bodyRequest.getSsm4uuserrefno());

            otcBalRepository.sp_insotcdbalcashbytotal(request);
        }

        return result;
    }

    @Override
    public Integer sp_insotcbalcashbytotal(List<OTCBalancingRequest> ListBodyRequest, OTCBalancingRequest bodyRequest){
        Integer result = 0;

        for(OTCBalancingRequest request : ListBodyRequest){

            if(request.getBal_date() != null){
                java.sql.Date sqlDate = java.sql.Date.valueOf(
                    new java.text.SimpleDateFormat("yyyy-MM-dd").format(request.getBal_date())
                );

                request.setBal_date(sqlDate);
            }

            request.setSsm4uuserrefno(bodyRequest.getSsm4uuserrefno());

            otcBalRepository.sp_insotcbalcashbytotal(request);
        }

        return result;
    }

    @Override
    public Integer sp_updotcbalcashbytotal(OTCBalancingRequest bodyRequest){
        Integer result = 1;

        // if(ListBodyRequest.size() == 0){
        //     return 0;
        // }

        // for(OTCBalancingRequest request : ListBodyRequest){

        //     if(request.getBal_date() != null){
        //         java.sql.Date sqlDate = java.sql.Date.valueOf(
        //             new java.text.SimpleDateFormat("yyyy-MM-dd").format(request.getBal_date())
        //         );

        //         request.setBal_date(sqlDate);
        //     }

        //     request.setSsm4uuserrefno(bodyRequest.getSsm4uuserrefno());
        //     Integer count = otcBalRepository.sp_updotcbalcashbytotal(request);
        //     result += count; 
        // }

        // if(result > 0){
            otcBalRepository.sp_insotccashgrandtotal(bodyRequest);
            bodyRequest.setDetail_type("cash");
            otcBalancingRequestRepository.sp_insfmsotcbalphysum(bodyRequest);
        // }

        return result;
    }

    @Override 
    public Integer sp_insotccashgrandtotal(OTCBalancingRequest bodyRequest){
        Integer result = 0;

        result = otcBalRepository.sp_insotccashgrandtotal(bodyRequest);

        return result;
    }

    @Override
    public Integer sp_updotcdailybalstatus(OTCBalancingRequest bodyRequest){
        Integer result = 0;

        result = otcBalRepository.sp_updotcdailybalstatus(bodyRequest);

        return result;
    }

    //Convert Balancing Info
    private List<OTCBalInfo> convertOTCBalInfo(List<Object[]> objects){

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
    private List<OTCBalRC> convertOTCBalRC(List<Object[]> objects){

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
            OTCBalListing.setOtc_counter_id((Integer) obj[10]);
            OTCBalListing.setCounter_id((String) obj[11]);
            OTCBalList.add(OTCBalListing);
        }

        return OTCBalList;
    }

    //Convert Balancing EMV Collection
    private List<OTCCtrBalCol> convertOTCBalEMV(List<Object[]> objects){

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
    private List<OTCBalCash> convertOTCBalCash(List<Object[]> objects){

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
    private List<OTCCtrBalPhy> convertOTCBalPhy(List<Object[]> objects){

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

    private List<OTCBalEMV> convertOTCBalDocList(List<Object[]> objects){

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

    private String convertOTCBalDoc(Blob file) throws SQLException{

        // Convert Blob to byte array
        byte[] bytes = file.getBytes(1, (int) file.length());

        // Convert byte array to Base64-encoded string
        String base64Content = Base64.getEncoder().encodeToString(bytes);

        return base64Content;
    }

    private byte[] decodeBase64(String base64String) {
        if (base64String.startsWith("data:")) {
            base64String = base64String.substring(base64String.indexOf(',') + 1);
        }
        base64String = base64String.replaceAll("\\s", "").replace(":", "");
        return Base64.getDecoder().decode(base64String);
    }
}
