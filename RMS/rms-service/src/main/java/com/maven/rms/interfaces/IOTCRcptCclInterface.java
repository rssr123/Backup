package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.util.List;

import com.maven.rms.models.MTTOrderStatusRequest;
import com.maven.rms.models.OTCReceiptCancellationBalStatusDetailsRequest;
import com.maven.rms.models.OTCReceiptCancellationDetailsRequest;
import com.maven.rms.models.OTCReceiptCancellationListingRequest;
import com.maven.rms.models.OTCReceiptCancellationMyTaskListingRequest;
import com.maven.rms.models.OTCReceiptCancellationRequest;
import com.maven.rms.models.OTCReceiptCancellationSupervisorRequest;
import com.maven.rms.models.OTCReceiptCancellationTaskAndReqInfoApprovalRequest;
import com.maven.rms.models.OTCReceiptCancellationUpdateRequest;
import com.maven.rms.models.OTCReceiptCclMTTOrderStatusRequest;


public interface IOTCRcptCclInterface {
    
    List<Object[]> sp_getotcrcptccllisting(OTCReceiptCancellationListingRequest otcrcptcclRequest);

    List<Object[]> sp_getotcrcptccloderinfodetails(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest);

    List<Object[]> sp_getotcrcptcclpymtinfodetails(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest);

    List<Object[]> sp_getotcrcptcclrcptinfodetails(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest);

    List<Object[]> sp_getotcrcptcclhistorydetails(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest);

    List<Object[]> sp_getotcrcptccltaskandreqinfoapproval(OTCReceiptCancellationTaskAndReqInfoApprovalRequest otcrcptcclTaskAndReqInfoApprovalRequest);

    List<Object[]> sp_getotcrcptcclmytasklisting(OTCReceiptCancellationMyTaskListingRequest otcrcptcclMyTaskListingRequest);

    List<Object[]> sp_getotcrcptcclbalstatusdetails(OTCReceiptCancellationBalStatusDetailsRequest otcrcptcclBalStatusDetsRequest);

    BigInteger sp_insotcrc(OTCReceiptCancellationRequest otcrcRequest);

    Integer sp_updotcrc(OTCReceiptCancellationUpdateRequest otcrcRequestUpdate);

    Integer sp_updmtt_orderstatus(OTCReceiptCclMTTOrderStatusRequest mttOrderStatusRequest);

    List<Object[]> sp_getotcrcpltoCancel(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest);

    Object[] sp_getmttrcptinfowithstatus(BigInteger mtt_id);

    Object[] sp_getotcreceipt(OTCReceiptCclMTTOrderStatusRequest mttOrderStatusRequest);

    Object[] sp_getotcrcptcclorder(BigInteger i_mtt_id);

    List<Object[]> sp_getotcrcptcllpymtitembymtt(BigInteger i_mtt_id);

    List<Object[]> sp_otcrcptcclpymtitem(OTCReceiptCancellationDetailsRequest getRequest);

    List<Object[]> sp_getotcrcptcclhistorydetailsaudit(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest);

    List<Object[]> sp_getotcrcptcclsupervisor(OTCReceiptCancellationSupervisorRequest otcrcptcclsupervisorRequest);

    //
    List<Object[]> sp_checkotcrcpt();
}
