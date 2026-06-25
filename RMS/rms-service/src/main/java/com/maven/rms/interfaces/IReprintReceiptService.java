package com.maven.rms.interfaces;

import java.util.List;

import com.maven.rms.models.RROrderInfo;
import com.maven.rms.models.ReprintRcpt;
import com.maven.rms.models.RRPaymentItems;
import com.maven.rms.models.ReprintRcptRequest;
import com.maven.rms.models.OTC.OTCRcpt;
import com.maven.rms.models.RRPaymentInfo;
import com.maven.rms.models.RRPaymentInfoV2;
import com.maven.rms.models.RRReceiptInfo;
import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OTCReceiptCancellationDetailsRequest;
import com.maven.rms.models.OTCReceiptCancellationHistoryDetails;
import com.maven.rms.models.OTCReceiptCclMTTOrderStatusRequest;
import com.maven.rms.models.OTCReceiptRpMTTOrderStatusRequest;
import com.maven.rms.models.RRHistoryTable;
import com.maven.rms.models.RRHistoryTableV2;
import com.maven.rms.models.RRJustification;    


public interface IReprintReceiptService {
    List<ReprintRcpt> sp_getreprintreceipt(ReprintRcptRequest reprintRcptRequest);
    List<RROrderInfo> sp_getorderinfo_rr(ReprintRcptRequest reprintRcptRequest);
    List<RRPaymentItems> sp_getpaymentitems_rr(ReprintRcptRequest reprintRcptRequest);
    List<RRPaymentInfo> sp_getpaymentinfo_rr(ReprintRcptRequest reprintRcptRequest);
    List<RRPaymentInfoV2> sp_getpaymentinfo_rr_v2(ReprintRcptRequest reprintRcptRequest);
    List<RRReceiptInfo> sp_getreceiptinfo_rr(ReprintRcptRequest reprintRcptRequest);
    List<RRHistoryTable> sp_gethistorytable_rr(ReprintRcptRequest reprintRcptRequest);
    List<RRJustification> sp_getjustification_rr(ReprintRcptRequest reprintRcptRequest);
    List<RRReceiptInfo> sp_getmttrcptrp(ReprintRcptRequest reprintRcptRequest);
    Integer sp_updrcptcount_rr (ReprintRcptRequest reprintRcptRequest);
    Integer sp_updrcptcount_mtt (ReprintRcptRequest reprintRcptRequest);
    Integer sp_updrcptjust_rr (ReprintRcptRequest reprintRcptRequest, int i_otc_rc_rp_id, int i_otc_rcpt_id, String i_modified_by, String i_justication);
    Integer sp_updotcrcpt(Integer i_otc_rcpt_id, String i_ver_id, String i_ssdocref_id, String file_nm);
    Integer sp_updmtt_orderstatus(OTCReceiptRpMTTOrderStatusRequest mttOrderStatusRequest);
    Integer sp_checkrcptcl(OTCReceiptRpMTTOrderStatusRequest mttOrderStatusRequest);
    OTCRcpt sp_getotcreceiptrp(OTCReceiptRpMTTOrderStatusRequest mttOrderStatusRequest);
    List<RRHistoryTableV2> sp_getotcrcptrpnthistorydetails(ReprintRcptRequest reprintRcptRequest);



    
}
