package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.util.List;

import com.maven.rms.models.OTCReceiptRpMTTOrderStatusRequest;
import com.maven.rms.models.ReprintRcptRequest;

public interface IReprintReceiptInterface {

    
    List<Object[]> sp_getreprintreceipt(ReprintRcptRequest reprintRcptRequest);
    List<Object[]> sp_getorderinfo_rr(ReprintRcptRequest reprintRcptRequest);
    List<Object[]> sp_getpaymentitems_rr(ReprintRcptRequest reprintRcptRequest);
    List<Object[]> sp_getpaymentinfo_rr(ReprintRcptRequest reprintRcptRequest);
    List<Object[]> sp_getpaymentinfo_rr_v2(ReprintRcptRequest reprintRcptRequest);
    List<Object[]> sp_getreceiptinfo_rr(ReprintRcptRequest reprintRcptRequest);
    List<Object[]> sp_gethistorytable_rr(ReprintRcptRequest reprintRcptRequest);
    List<Object[]> sp_gethistorytable_rr_v2(ReprintRcptRequest reprintRcptRequest);
    List<Object[]> sp_getjustification_rr(ReprintRcptRequest reprintRcptRequest);
    Integer sp_updrcptcount_rr (ReprintRcptRequest reprintRcptRequest);
    Integer sp_updrcptjust_rr (ReprintRcptRequest reprintRcptRequest, int i_otc_rc_rp_id, int i_otc_rcpt_id, String i_modified_by, String i_justication);
    Integer sp_updotcrcpt(Integer i_otc_rcpt_id, String i_ver_id, String i_ssdocref_id, String i_file_nm);
    Integer sp_updmtt_orderstatus(OTCReceiptRpMTTOrderStatusRequest mttOrderStatusRequest);
    Object[] sp_getotcreceiptrp(OTCReceiptRpMTTOrderStatusRequest mttOrderStatusRequest);
    Object[] sp_getmttrcptinfo(BigInteger mtt_id);
    Integer sp_checkrcptcl(OTCReceiptRpMTTOrderStatusRequest mttOrderStatusRequest);
    List<Object[]> sp_getmttrcptrp(ReprintRcptRequest reprintRcptRequest);
    Integer sp_updrcptcount_mtt (ReprintRcptRequest reprintRcptRequest);
  




}
