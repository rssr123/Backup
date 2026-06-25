package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.OTC.OTCEMVPaymentReq;
import com.maven.rms.models.OTC.OTCEMVRequest;
import com.maven.rms.models.OTC.OTCHistReq;
import com.maven.rms.models.OTC.OTCPaymentRequest;
import com.maven.rms.models.OTC.OTCRcptRequest;
import com.maven.rms.models.OTC.OTCollectionReceiptingRequest;

public interface IOTCCollectionReceiptingRepositoryInterface {
    List<Object[]> sp_getcollectioninfo(OTCollectionReceiptingRequest otCollectionReceiptingRequest);
    List<Object[]> sp_otccrpymtitem(OTCollectionReceiptingRequest otCollectionReceiptingRequest);
    List<Object[]> sp_otccrpymtitembymtt(OTCollectionReceiptingRequest otCollectionReceiptingRequest);
    Integer sp_insotcpymt(OTCPaymentRequest insRequest);
    Integer sp_insotcpymtbody(List<OTCPaymentRequest> insRequests);
    Integer sp_insotchistupdmtt(OTCHistReq insRequests);
    Integer sp_insotchist(OTCHistReq insRequests);
    List<Object[]> sp_otccrhist(OTCPaymentRequest otcHistReq);
    List<Object[]> sp_getotccrpaymentdetails(OTCPaymentRequest otCollectionReceiptingRequest);
    List<Object[]> sp_getotccrpaymentheader(OTCPaymentRequest otCollectionReceiptingRequest);
    Object[] sp_insotcrcpt(OTCRcptRequest insRequest);
    Object[] sp_getotcorder(Integer i_mtt_id);
    Object[] sp_getotcorderemv(Integer i_mtt_id);
    Integer sp_updotcrcpt(Integer i_otc_rcpt_id, String i_ver_id, String i_ssdocref_id, String i_file_nm);
    Integer sp_insemvsale(OTCEMVRequest insRequest);
    Integer sp_insotcpymtemv(OTCEMVPaymentReq insRequest);
    List<Object[]> sp_getotcrcpt(OTCPaymentRequest otcRcptReq);
    Object[] sp_getotcemvsales(OTCPaymentRequest req);
}
