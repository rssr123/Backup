package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IOTCCtrBalInfoService;
import com.maven.rms.repositories.OTCCtrBalRepository;
import com.maven.rms.models.OTCBalCash;
import com.maven.rms.models.OTCCtrBalCol;
import com.maven.rms.models.OTCCtrBalInfo;
import com.maven.rms.models.OTCCtrBalPhy;
import com.maven.rms.models.OTCCtrBalRMS;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OTCCtrBalService implements IOTCCtrBalInfoService{
    private final OTCCtrBalRepository otcCtrBalRepository;

    public OTCCtrBalService(OTCCtrBalRepository otcCtrBalRepository)
    {
        this.otcCtrBalRepository = otcCtrBalRepository;
    }

    @Override
    public List<OTCCtrBalInfo> sp_getotcbalctrinfo(String i_counter_id, BigInteger i_otc_counter_id) 
    {
       List<OTCCtrBalInfo> ctrBalInfoList = Collections.emptyList();

       List<Object[]> objects = otcCtrBalRepository.sp_getotcbalctrinfo(i_counter_id, i_otc_counter_id);

       ctrBalInfoList = convertCtrBalInfoList(objects);

       return ctrBalInfoList;
    }

    @Override
    public List<OTCCtrBalRMS> sp_getotcrmscol(Integer i_page, Integer i_size, String i_counter_id, BigInteger i_otc_counter_id) 
    {
       List<OTCCtrBalRMS> ctrBalRMSList = Collections.emptyList();

       List<Object[]> objects = otcCtrBalRepository.sp_getotcrmscol(i_page, i_size, i_counter_id, i_otc_counter_id);

       ctrBalRMSList = convertCtrBalRMSList(objects);

       return ctrBalRMSList;
    }

    @Override
    public List<OTCCtrBalCol> sp_getotcctrcol(Integer i_page, Integer i_size, String i_counter_id, BigInteger i_otc_counter_id) 
    {
       List<OTCCtrBalCol> ctrBalColList = Collections.emptyList();

       List<Object[]> objects = otcCtrBalRepository.sp_getotcctrcol(i_page, i_size, i_counter_id, i_otc_counter_id);

       ctrBalColList = convertCtrBalColList(objects);

       return ctrBalColList;
    }

    @Override
    public List<OTCCtrBalPhy> sp_getotcphyinfo(String i_counter_id, BigInteger i_otc_counter_id) 
    {
       List<OTCCtrBalPhy> ctrBalPhyList = Collections.emptyList();

       List<Object[]> objects = otcCtrBalRepository.sp_getotcphyinfo(i_counter_id, i_otc_counter_id);

       ctrBalPhyList = convertCtrBalPhyList(objects);

       return ctrBalPhyList;
    }

    @Override
    public List<OTCBalCash> sp_getotccashinfo(BigInteger i_otc_counter_id) 
    {
       List<OTCBalCash> cashList = Collections.emptyList();

       List<Object[]> objects = otcCtrBalRepository.sp_getotccashinfo(i_otc_counter_id);

       cashList = convertCtrBalCashList(objects);

       return cashList;
    }

    private List<OTCCtrBalInfo> convertCtrBalInfoList(List<Object[]> objects) {
        List<OTCCtrBalInfo> ctrBalInfoList = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCCtrBalInfo ctrBalInfo = new OTCCtrBalInfo();
            ctrBalInfo.setCounter_id((String) obj[0]);
            ctrBalInfo.setCheck_in((String) obj[1]);
            ctrBalInfo.setUser_id((String) obj[2]);
            ctrBalInfo.setBranch_cd((String) obj[3]);
            ctrBalInfo.setOrders_paid((Integer) obj[4]);
            ctrBalInfo.setTotal((BigDecimal) obj[5]);
            ctrBalInfo.setTotal_emv((BigDecimal) obj[6]);
            ctrBalInfo.setTotal_phy((BigDecimal) obj[7]);
            ctrBalInfo.setTotal_col((BigDecimal) obj[8]);
            ctrBalInfo.setTotal_che((BigDecimal) obj[9]);
            ctrBalInfo.setTotal_mo((BigDecimal) obj[10]);
            ctrBalInfo.setTotal_bd((BigDecimal) obj[11]);
            ctrBalInfo.setStatus((String) obj[12]);
            ctrBalInfoList.add(ctrBalInfo);
        }

        return ctrBalInfoList;
    }

    
    private List<OTCCtrBalRMS> convertCtrBalRMSList(List<Object[]> objects){
        List<OTCCtrBalRMS> ctrBalRMSList = new ArrayList<>();

        for(Object[] obj : objects){
            OTCCtrBalRMS ctrBalRMS = new OTCCtrBalRMS();
            ctrBalRMS.setCol_slip_no((String) obj[0]);
            ctrBalRMS.setOrn_no((String) obj[1]);
            ctrBalRMS.setGtotal((BigDecimal) obj[2]);
            ctrBalRMS.setOtc_pymt_mode((String) obj[3]);
            ctrBalRMS.setEmv_amt((BigDecimal) obj[4]);
            ctrBalRMS.setCash_amt((BigDecimal) obj[5]);
            ctrBalRMS.setChe_amt((BigDecimal) obj[6]);
            ctrBalRMS.setBd_amt((BigDecimal) obj[7]);
            ctrBalRMS.setMo_amt((BigDecimal) obj[8]);
            ctrBalRMS.setTotal((Integer) obj[9]);
            ctrBalRMSList.add(ctrBalRMS);
        }

        return ctrBalRMSList;
    }

    private List<OTCCtrBalCol> convertCtrBalColList(List<Object[]> objects){
        List<OTCCtrBalCol> ctrBalColList = new ArrayList<>();

        for(Object[] obj : objects){
            OTCCtrBalCol ctrBalCol = new OTCCtrBalCol();
            ctrBalCol.setCol_slip_no((String) obj[0]);
            ctrBalCol.setOrn_no((String) obj[1]);
            ctrBalCol.setTrans_trace((String) obj[2]);
            ctrBalCol.setBatch_no((String) obj[3]);
            ctrBalCol.setHost_no((String) obj[4]);
            ctrBalCol.setT_id((String) obj[5]);
            ctrBalCol.setAmount((BigDecimal) obj[6]);
            ctrBalCol.setTotal((Integer) obj[7]);
            ctrBalColList.add(ctrBalCol);
        }

        return ctrBalColList;
    }

    private List<OTCCtrBalPhy> convertCtrBalPhyList(List<Object[]> objects){
        List<OTCCtrBalPhy> ctrBalPhyList = new ArrayList<>();

        for(Object[] obj : objects){
            OTCCtrBalPhy ctrBalPhy = new OTCCtrBalPhy();
            ctrBalPhy.setTotal_cash_amt((BigDecimal) obj[0]);
            ctrBalPhy.setCol_slip_no((String) obj[1]);
            ctrBalPhy.setOrn_no((String) obj[2]);
            ctrBalPhy.setChe_bank_nm((String) obj[3]);
            ctrBalPhy.setChe_payer_nm((String) obj[4]);
            ctrBalPhy.setChe_ba_acct_no((String) obj[5]);
            ctrBalPhy.setChe_no((String) obj[6]);
            ctrBalPhy.setChe_date((Date) obj[7]);
            ctrBalPhy.setChe_amt((BigDecimal) obj[8]);
            ctrBalPhy.setBd_bank_nm((String) obj[9]);
            ctrBalPhy.setBd_no((String) obj[10]);
            ctrBalPhy.setBd_date((Date) obj[11]);
            ctrBalPhy.setBd_amt((BigDecimal) obj[12]);
            ctrBalPhy.setMo_rm_no((String) obj[13]);
            ctrBalPhy.setMo_date((Date) obj[14]);
            ctrBalPhy.setMo_payer_nm((String) obj[15]);
            ctrBalPhy.setMo_id_no((String) obj[16]);
            ctrBalPhy.setMo_contact_no((String) obj[17]);
            ctrBalPhy.setMo_amt((BigDecimal) obj[18]);
            ctrBalPhy.setDetail_type((String) obj[19]);
            ctrBalPhy.setId((BigInteger) obj[20]);
            ctrBalPhy.setOtc_id((BigInteger) obj[21]);
            ctrBalPhyList.add(ctrBalPhy);
        }

        return ctrBalPhyList;
    }

    private List<OTCBalCash> convertCtrBalCashList(List<Object[]> objects){
        List<OTCBalCash> cashList = new ArrayList<>();

        for(Object[] obj : objects){
            OTCBalCash ctrBalCash = new OTCBalCash();
            ctrBalCash.setParam_cd((String) obj[0]);
            ctrBalCash.setDenomination((String) obj[1]);
            ctrBalCash.setQuantity((Integer) obj[2]);
            ctrBalCash.setTotal((BigDecimal) obj[3]);
            cashList.add(ctrBalCash);
        }

        return cashList;
    }

}
