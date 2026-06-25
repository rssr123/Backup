package com.maven.rms.interfaces;

import java.math.BigInteger;
import java.util.List;

import com.maven.rms.models.MTTOrderStatusRequest;
import com.maven.rms.models.OTCReceiptCancellationAssignToRequest;
import com.maven.rms.models.OTCReceiptCancellationBalStatusDetails;
import com.maven.rms.models.OTCReceiptCancellationBalStatusDetailsRequest;
import com.maven.rms.models.OTCReceiptCancellationCreatedByRequest;
import com.maven.rms.models.OTCReceiptCancellationDetailsRequest;
import com.maven.rms.models.OTCReceiptCancellationHistoryDetails;
import com.maven.rms.models.OTCReceiptCancellationHistoryDetailsAudit;
import com.maven.rms.models.OTCReceiptCancellationListing;
import com.maven.rms.models.OTCReceiptCancellationListingRequest;
import com.maven.rms.models.OTCReceiptCancellationMyTaskListing;
import com.maven.rms.models.OTCReceiptCancellationMyTaskListingRequest;
import com.maven.rms.models.OTCReceiptCancellationOrderInfoDetails;
import com.maven.rms.models.OTCReceiptCancellationPaymentInfoDetails;
import com.maven.rms.models.OTCReceiptCancellationPymtItem;
import com.maven.rms.models.OTCReceiptCancellationRCStatusDetails;
import com.maven.rms.models.OTCReceiptCancellationReceiptInfoDetails;
import com.maven.rms.models.OTCReceiptCancellationRequest;
import com.maven.rms.models.OTCReceiptCancellationSupervisor;
import com.maven.rms.models.OTCReceiptCancellationSupervisorRequest;
import com.maven.rms.models.OTCReceiptCancellationTaskAndReqInfoApproval;
import com.maven.rms.models.OTCReceiptCancellationTaskAndReqInfoApprovalRequest;
import com.maven.rms.models.OTCReceiptCancellationUpdateRequest;
import com.maven.rms.models.OTCReceiptCclMTTOrderStatusRequest;
import com.maven.rms.models.OTCReceiptCheck;
import com.maven.rms.models.OTC.OTCCollectionReceiptingPymtItem;
import com.maven.rms.models.OTC.OTCPaymentDone;
import com.maven.rms.models.OTC.OTCRcpt;


public interface IOTCRcptCclService {
    
    List<OTCReceiptCancellationListing> sp_getotcrcptccllisting(OTCReceiptCancellationListingRequest otcrcptcclRequest);

    List<OTCReceiptCancellationOrderInfoDetails> sp_getotcrcptccloderinfodetails(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest);

    List<OTCReceiptCancellationPaymentInfoDetails> sp_getotcrcptcclpymtinfodetails(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest);

    List<OTCReceiptCancellationReceiptInfoDetails> sp_getotcrcptcclrcptinfodetails(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest);

    List<OTCReceiptCancellationHistoryDetails> sp_getotcrcptcclhistorydetails(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest);

    List<OTCReceiptCancellationTaskAndReqInfoApproval> sp_getotcrcptccltaskandreqinfoapproval(OTCReceiptCancellationTaskAndReqInfoApprovalRequest otcrcptcclTaskAndReqInfoApprovalRequest);

    List<OTCReceiptCancellationMyTaskListing> sp_getotcrcptcclmytasklisting(OTCReceiptCancellationMyTaskListingRequest otcrcptcclMyTaskListingRequest);

    List<OTCReceiptCancellationBalStatusDetails> sp_getotcrcptcclbalstatusdetails(OTCReceiptCancellationBalStatusDetailsRequest otcrcptcclBalStatusDetsRequest);

    BigInteger sp_insotcrc(OTCReceiptCancellationRequest otcrcRequest);

    Integer sp_updotcrc(OTCReceiptCancellationUpdateRequest otcrcRequestUpdate);

    Integer sp_updmtt_orderstatus(OTCReceiptCclMTTOrderStatusRequest mttOrderStatusRequest);

    List<OTCReceiptCancellationRCStatusDetails> sp_getotcrcpltoCancel(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest);

    OTCRcpt sp_getotcreceipt(OTCReceiptCclMTTOrderStatusRequest mttOrderStatusRequest);

    OTCPaymentDone sp_getotcrcptcclorder(BigInteger i_mtt_id);

    List<OTCCollectionReceiptingPymtItem> sp_getotcrcptcllpymtitembymtt(BigInteger i_mtt_id);

    List<OTCReceiptCancellationPymtItem> sp_otcrcptcclpymtitem(OTCReceiptCancellationDetailsRequest getRequest);

    Integer sp_updotcrcpt(Integer i_otc_rcpt_id, String i_ver_id, String i_ssdocref_id, String file_nm);

    Integer sp_getotcrcassignedtaskactivetaskcount(OTCReceiptCancellationAssignToRequest otcrcptcclRequest);

    Integer sp_getotcrccreatedtaskactivetaskcount(OTCReceiptCancellationCreatedByRequest otcrcptcclRequest);

    List<OTCReceiptCancellationHistoryDetailsAudit> sp_getotcrcptcclhistorydetailsaudit(
            OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest);

    List<OTCReceiptCancellationSupervisor> sp_getotcrcptcclsupervisor(
            OTCReceiptCancellationSupervisorRequest otcrcptcclsupervisorRequest);

    List<OTCReceiptCheck> sp_checkotcrcpt();
        
}
