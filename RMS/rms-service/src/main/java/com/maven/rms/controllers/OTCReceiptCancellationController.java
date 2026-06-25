package com.maven.rms.controllers;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.CacheManager;

import lombok.extern.slf4j.Slf4j;

import com.maven.rms.config.RMSProperties;
import com.maven.rms.models.IdamanAPIUpload;
import com.maven.rms.models.IdamanAPIUploadReq;
import com.maven.rms.models.MFTWFRequest;
import com.maven.rms.models.MTTPG;
import com.maven.rms.models.MTTRCPT;
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
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.models.RMSUser;
import com.maven.rms.models.RMSUserRequest;
import com.maven.rms.models.ReceiptRequest;
import com.maven.rms.models.OTC.OTCCollectionReceiptingPymtItem;
import com.maven.rms.models.OTC.OTCPaymentDone;
import com.maven.rms.models.OTC.OTCRcpt;
import com.maven.rms.models.OTC.OTCollectionReceiptRequest;
import com.maven.rms.models.OTC.OTCollectionReceiptingRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.IdamanAPIUploadService;
import com.maven.rms.services.MTTService;
import com.maven.rms.services.NotificationService;
import com.maven.rms.services.OTCRcptCclService;
import com.maven.rms.services.OnlinePaymentService;
import com.maven.rms.services.RICPService;
import com.maven.rms.services.SSM4UAPI;
import com.maven.rms.services.UAMService;
import com.maven.rms.utils.ControllersEnum;
import com.maven.rms.utils.RMSLogger;
import com.maven.rms.utils.receipts.MTTPGCancelledReceiptGenerator;
import com.maven.rms.utils.receipts.MTTPGReceiptGenerator;
import com.maven.rms.utils.receipts.MTTPGWatermarkedReceiptGenerator;
import com.maven.rms.utils.receipts.OTCReceiptCancelledGenerator;
import com.maven.rms.utils.receipts.OTCReceiptGenerator;

@RestController
@RequestMapping("/api/otcrcptccl/v1")
@Slf4j
public class OTCReceiptCancellationController {

    @Autowired
    private AuthService authService;
    @Autowired
    private OTCRcptCclService otcrcptcclService;
    //@Autowired
    //private NotificationService notificationSvc;

    // start
    private OnlinePaymentService onlinePaymentService;

    @Autowired
    private EmailService emailService;
    @Autowired
    private UAMService uamSvc;
    @Autowired
    private SSM4UAPI ssm4uSvc;
    // private MTTPGReceiptGenerator receiptGenerator;

    private final String serviceID;
    private final String servicePW;
    private final String callBackURL;
    private final String returnURL;
    private final String onlinePortalURL;

    @Autowired
    private RICPService ricpSvc;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private MTTService mttService;

    @Autowired
    private MTTPGReceiptGenerator receiptGenerator;

    @Autowired
    private MTTPGCancelledReceiptGenerator receiptCancelledGenerator;

    @Autowired
    private MTTPGWatermarkedReceiptGenerator mttpgWatermarkedReceiptGenerator;

    @Autowired
    private IdamanAPIUploadService idamanAPIUploadService;

    @Autowired
    private NotificationService notificationSvc;

    @Autowired
    private OTCReceiptCancelledGenerator receiptOTCGenerator;

    public OTCReceiptCancellationController(OnlinePaymentService onlinePaymentService, RMSProperties rmsProperties) {
        // StoreProcedureService storeProcedureService, MTTService mttService,
        // EmailService emailService) {
        this.onlinePaymentService = onlinePaymentService;
        // this.rmsProperties = rmsProperties;
        // this.mttService = mttService;
        // this.emailService = emailService;
        // this.receiptGenerator=receiptGenerator;

        this.serviceID = rmsProperties.getGHLServiceID();
        this.servicePW = rmsProperties.getGHLPw();
        this.callBackURL = rmsProperties.getcallBackURL();
        this.onlinePortalURL = rmsProperties.getOnlinePortalURL();
        this.returnURL = rmsProperties.getReturnURL();

        RMSLogger.info("OnlinePaymentController services is started");

    }

    //

    // listing page start
    @PostMapping(value = "/getotcreceiptcancellationlisting")
    public ResponseEntity<ApiResponse<List<OTCReceiptCancellationListing>>> sp_getotcrcptccllisting(
            HttpServletRequest request,
            @RequestBody OTCReceiptCancellationListingRequest otcrcptcclRequest) {

        List<OTCReceiptCancellationListing> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }

        result = otcrcptcclService.sp_getotcrcptccllisting(otcrcptcclRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RECEIPT_CANCELLATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }
    // listing page end

    // details page start
    @PostMapping(value = "/getotcreceiptcancellationorderinfodetails")
    public ResponseEntity<ApiResponse<List<OTCReceiptCancellationOrderInfoDetails>>> sp_getotcrcptccloderinfodetails(
            HttpServletRequest request,
            @RequestBody OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {

        List<OTCReceiptCancellationOrderInfoDetails> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }

        result = otcrcptcclService.sp_getotcrcptccloderinfodetails(otcrcptcclDetsRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RECEIPT_CANCELLATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcreceiptcancellationpaymentinfodetails")
    public ResponseEntity<ApiResponse<List<OTCReceiptCancellationPaymentInfoDetails>>> sp_getotcrcptcclpymtinfodetails(
            HttpServletRequest request,
            @RequestBody OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {

        List<OTCReceiptCancellationPaymentInfoDetails> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }

        result = otcrcptcclService.sp_getotcrcptcclpymtinfodetails(otcrcptcclDetsRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RECEIPT_CANCELLATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/getotcreceiptcancellationreceiptinfodetails")
    public ResponseEntity<ApiResponse<List<OTCReceiptCancellationReceiptInfoDetails>>> sp_getotcrcptcclrcptinfodetails(
            HttpServletRequest request,
            @RequestBody OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {

        List<OTCReceiptCancellationReceiptInfoDetails> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }

        result = otcrcptcclService.sp_getotcrcptcclrcptinfodetails(otcrcptcclDetsRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RECEIPT_CANCELLATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/getotcreceiptcancellationhistorydetails")
    public ResponseEntity<ApiResponse<List<OTCReceiptCancellationHistoryDetails>>> sp_getotcrcptcclhistorydetails(
            HttpServletRequest request,
            @RequestBody OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {

        List<OTCReceiptCancellationHistoryDetails> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }

        result = otcrcptcclService.sp_getotcrcptcclhistorydetails(otcrcptcclDetsRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RECEIPT_CANCELLATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/getotcreceiptcancellationbalancestatusdetails")
    public ResponseEntity<ApiResponse<List<OTCReceiptCancellationBalStatusDetails>>> sp_getotcrcptcclbalstatusdetails(
            HttpServletRequest request,
            @RequestBody OTCReceiptCancellationBalStatusDetailsRequest otcrcptcclBalStatusDetailsRequest) {

        List<OTCReceiptCancellationBalStatusDetails> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }

        result = otcrcptcclService.sp_getotcrcptcclbalstatusdetails(otcrcptcclBalStatusDetailsRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RECEIPT_CANCELLATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/insertotcreceiptcancellation")
    public ResponseEntity<ApiResponse<BigInteger>> sp_insotcrc(HttpServletRequest request,
            @RequestBody OTCReceiptCancellationRequest otcrcRequest) {

        BigInteger result = BigInteger.ZERO;

        try {
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            otcrcRequest.setI_requested_by(authService.getLoginUserName());
            otcrcRequest.setI_requester_id(authService.getUserEmail());
            otcrcRequest.setI_created_by(authService.getLoginUserName());
            otcrcRequest.setI_modified_by(authService.getLoginUserName());

            result = otcrcptcclService.sp_insotcrc(otcrcRequest);

            if (result.compareTo(BigInteger.ZERO) <= 0) {
                return APIResponse.InternalServerError();
            }

            notificationSvc.sendNotificationUpdate();

            return APIResponse.SuccessResponse(result);
        } catch (Exception e) {
            System.out.println(e);
            return APIResponse.InternalServerError();
        }

    }

    @PostMapping(value = "/getotcrcpltoCancel")
    public ResponseEntity<ApiResponse<List<OTCReceiptCancellationRCStatusDetails>>> sp_getotcrcpltoCancel(
            HttpServletRequest request,
            @RequestBody OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {

        List<OTCReceiptCancellationRCStatusDetails> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }

        result = otcrcptcclService.sp_getotcrcpltoCancel(otcrcptcclDetsRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RECEIPT_CANCELLATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }
    // details page end

    // approval and justification page start

    @PostMapping(value = "/getotcreceiptcancellationtaskandreqinfoapproval")
    public ResponseEntity<ApiResponse<List<OTCReceiptCancellationTaskAndReqInfoApproval>>> sp_getotcrcptccltaskandreqinfoapproval(
            HttpServletRequest request,
            @RequestBody OTCReceiptCancellationTaskAndReqInfoApprovalRequest otcrcptcclTaskAndReqInfoApprovalRequest) {

        List<OTCReceiptCancellationTaskAndReqInfoApproval> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }

        result = otcrcptcclService.sp_getotcrcptccltaskandreqinfoapproval(otcrcptcclTaskAndReqInfoApprovalRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RECEIPT_CANCELLATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/updotcreceiptcancellation")
    public ResponseEntity<ApiResponse<Integer>> sp_updotcrc(HttpServletRequest request,
            @RequestBody OTCReceiptCancellationUpdateRequest otcrcRequestUpdate) {

        Integer result = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (otcrcRequestUpdate.getI_approved_by() == null || otcrcRequestUpdate.getI_approved_by().isEmpty()) {// front // endm// dont // have// email// and
            otcrcRequestUpdate.setI_approved_by(authService.getLoginUserName());
            otcrcRequestUpdate.setI_approver_id(authService.getUserEmail());
        }
        otcrcRequestUpdate.setI_modified_by(authService.getLoginUserName());

        result = otcrcptcclService.sp_updotcrc(otcrcRequestUpdate);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

         notificationSvc.sendNotificationUpdate();
         
        return APIResponse.SuccessResponse(result);

    }

    // approval and justification page end

    // otc cancellation my task start

    @PostMapping(value = "/getotcreceiptcancellationmytasklisting")
    public ResponseEntity<ApiResponse<List<OTCReceiptCancellationMyTaskListing>>> sp_getotcrcptcclmytasklisting(
            HttpServletRequest request,
            @RequestBody OTCReceiptCancellationMyTaskListingRequest otcrcptcclTaskAndMyTaskListingRequest) {

        List<OTCReceiptCancellationMyTaskListing> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }

        result = otcrcptcclService.sp_getotcrcptcclmytasklisting(otcrcptcclTaskAndMyTaskListingRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RECEIPT_CANCELLATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    // otc cancellation my task end

    @PostMapping(value = "/getpymtitems")
    public ResponseEntity<ApiResponse<List<OTCReceiptCancellationPymtItem>>> sp_otcrcptcclpymtitem(
            HttpServletRequest request,
            @RequestBody OTCReceiptCancellationDetailsRequest getRequest) {

        List<OTCReceiptCancellationPymtItem> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcrcptcclService.sp_otcrcptcclpymtitem(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.ROLE_AND_PERMISSIONS_CONFIGURATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    // update mtt order status start
    @PostMapping(value = "/updatemttorderstatusandreceipt")
    public ResponseEntity<ApiResponse<Integer>> sp_updmtt_orderstatus(HttpServletRequest request,
            @RequestBody OTCReceiptCclMTTOrderStatusRequest mttOrderStatusRequest) {

        try {
            Integer result = 0;

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            mttOrderStatusRequest.setI_modified_by("system");

            // if(mttOrderStatusRequest.getI_update_MTT_status() == true){

            result = otcrcptcclService.sp_updmtt_orderstatus(mttOrderStatusRequest);

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }
            // }

            OTCRcpt otcRcpt = otcrcptcclService.sp_getotcreceipt(mttOrderStatusRequest);

            OTCPaymentDone payment = otcrcptcclService.sp_getotcrcptcclorder(mttOrderStatusRequest.getI_mtt_id());

            // Use SimpleDateFormat to format the date
           // SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
            String formattedDate = sdf.format(payment.getPayment_dt());
            payment.setFormattedDate(formattedDate);

            List<OTCCollectionReceiptingPymtItem> paymentItems = otcrcptcclService
                    .sp_getotcrcptcllpymtitembymtt(mttOrderStatusRequest.getI_mtt_id());

            File pdfRcpt = receiptOTCGenerator
                    .generateReceipt(new OTCollectionReceiptRequest(payment, otcRcpt, paymentItems, "pdf"));
            // upload to idaman start
            // create guid
            UUID uuid = UUID.randomUUID();
            String guid = "RMS-" + uuid.toString();

            byte[] fileContent = Files.readAllBytes(Paths.get(pdfRcpt.toString()));
            String encodedString = Base64.getEncoder().encodeToString(fileContent);

            String formatedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            // upload to idaman
            Integer result1 = uploadIdamanAPI(
                    new IdamanAPIUploadReq("RMS", otcRcpt.getRcptNo(), "RMSReceipt", formatedDate,
                            "", "", "", "", "", "", guid, payment.getOrn_no(), "", "", "", "", "", "",
                            encodedString, pdfRcpt.getName()),
                    otcRcpt.getOtc_rcpt_id(), pdfRcpt.getName());

            if (result1 < 1) {
                log.error("Exception in Error in uploading receipt to Idaman");
                //throw new Exception("Error in uploading receipt to Idaman");
            }

            // // generate receipt is compulsory
            // BigInteger mtt_id = mttOrderStatusRequest.getI_mtt_id();

            // // generateReceipt(BigInteger mtt_id) {

            // //mtt_id = BigInteger.valueOf(204);

            // MTTRCPT rcpt = new MTTRCPT();

            // rcpt = otcrcptcclService.sp_getmttrcptinfowithstatus(mtt_id);

            // Integer mttid;
            // Long pgId;
            // //mttid = mtt_id.intValue();
            // //pgId = (long)263;
            // mttid = rcpt.getRmsMTT().getMttId();
            // pgId = rcpt.getMttPG().getMttPgId();

            // OnlinePayment payment = new OnlinePayment();
            // List<OnlinePaymentItem> paymentItems = Collections.emptyList();
            // String orderStatus;
            // // get mtt details
            // payment = onlinePaymentService.sp_getMTT(mttid);

            // // get mtt items details
            // // paymentItems = storeProcedureService.sp_getMTTItem(mttid); // vicky old
            // code
            // paymentItems = mttService.sp_getMTTItem(mttid); // vicky old code
            // // paymentItems = mttService.getListOfItems(mttid); // use brian code

            // // get mtt pg details
            // MTTPG pG = new MTTPG();
            // pG = mttService.getMttPgById(pgId).orElse(null);

            // // generate a new receipt

            // File pdfRcpt = receiptCancelledGenerator
            // .generateReceipt(new ReceiptRequest(pG, payment, rcpt, paymentItems, "pdf"));

            // UUID uuid = UUID.randomUUID();
            // String guid = "RMS-" + uuid.toString();

            // byte[] fileContent = Files.readAllBytes(Paths.get(pdfRcpt.toString()));
            // String encodedString = Base64.getEncoder().encodeToString(fileContent);

            // String formatedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

            // Integer result1 = uploadIdamanAPI(
            // new IdamanAPIUploadReq("RMS", rcpt.getRcptNo(), "RMSReceipt", formatedDate,
            // "", "", "", "", "", "", guid, payment.getOrnNo(), "", "", "", "", "", "",
            // encodedString, pdfRcpt.getName()),
            // rcpt.getMttRcptID());

            // // Integer result1 = uploadIdamanAPI(rcpt.getRcptNo(), "RMSReceipt",
            // // rcpt.getRcptDt().toString(),
            // // guid, payment.getOrnNo(), encodedString,
            // // pdfRcpt.getName(),rcpt.getMttRcptID());

            // if (result1 < 1) {
            // throw new Exception("Error in uploading receipt to Idaman");
            // }

            // }

            notificationSvc.sendNotificationUpdate();
            return APIResponse.SuccessResponse(result);

        } catch (Exception e) {
            System.out.println(e);
            return APIResponse.NoDataFound(ControllersEnum.OTC_RECEIPT_CANCELLATION_CONTROLLER);
        }
    }

    // update mtt order status end

    
    // @PostMapping(value = "/myo")
    // public void generateReceipt2(HttpServletRequest request,
    //         @RequestBody OTCReceiptCclMTTOrderStatusRequest mttOrderStatusRequest) {

    //     OTCRcpt otcRcpt = otcrcptcclService.sp_getotcreceipt(mttOrderStatusRequest);

    //     OTCPaymentDone payment = otcrcptcclService.sp_getotcrcptcclorder(mttOrderStatusRequest.getI_mtt_id());

    //     // Use SimpleDateFormat to format the date
    //     //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    //     SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
    //     String formattedDate = sdf.format(payment.getPayment_dt());
    //     payment.setFormattedDate(formattedDate);

    //     List<OTCCollectionReceiptingPymtItem> paymentItems = otcrcptcclService
    //             .sp_getotcrcptcllpymtitembymtt(mttOrderStatusRequest.getI_mtt_id());

    //     // mtt_id = BigInteger.valueOf(204);

    //     // MTTRCPT rcpt = new MTTRCPT();

    //     try {
    //         // rcpt = otcrcptcclService.sp_getmttrcptinfowithstatus(mtt_id);

    //         File pdfRcpt = receiptOTCGenerator
    //                 .generateReceipt(new OTCollectionReceiptRequest(payment, otcRcpt, paymentItems, "pdf"));

    //         // Integer mttid;
    //         // Long pgId;
    //         // mttid = mtt_id.intValue();
    //         // pgId = (long)263;
    //         // mttid = rcpt.getRmsMTT().getMttId();
    //         // pgId = rcpt.getMttPG().getMttPgId();

    //         // OnlinePayment payment = new OnlinePayment();
    //         // List<OnlinePaymentItem> paymentItems = Collections.emptyList();
    //         // String orderStatus;
    //         // // get mtt details
    //         // payment = onlinePaymentService.sp_getMTT(mttid);

    //         // // get mtt items details
    //         // //paymentItems = mttService.sp_getMTTItem(mttid); // vicky old code
    //         // // paymentItems = mttService.getListOfItems(mttid); // use brian code
    //         // paymentItems=mttService.sp_getMTTItem(mttid);
    //         // // get mtt pg details
    //         // MTTPG pG = new MTTPG();
    //         // pG = mttService.getMttPgById(pgId).orElse(null);

    //         // // generate a new receipt

    //         // File pdfRcpt = receiptCancelledGenerator
    //         // .generateReceipt(new ReceiptRequest(pG, payment, rcpt, paymentItems, "pdf"));

    //         // File pdfRcpt = mttpgWatermarkedReceiptGenerator
    //         // .generateReceipt(new ReceiptRequest(pG, payment, rcpt, paymentItems, "pdf"),
    //         // 1);

    //         UUID uuid = UUID.randomUUID();
    //         String guid = "RMS-" + uuid.toString();

    //         byte[] fileContent = Files.readAllBytes(Paths.get(pdfRcpt.toString()));
    //         String encodedString = Base64.getEncoder().encodeToString(fileContent);

    //         String formatedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

    //         // Integer result1 = uploadIdamanAPI(
    //         // new IdamanAPIUploadReq("RMS", rcpt.getRcptNo(), "RMSReceipt", formatedDate,
    //         // "", "", "", "", "", "", guid, payment.getOrnNo(), "", "", "", "", "", "",
    //         // encodedString, pdfRcpt.getName()),
    //         // rcpt.getMttRcptID());

    //         // // Integer result1 = uploadIdamanAPI(rcpt.getRcptNo(), "RMSReceipt",
    //         // // rcpt.getRcptDt().toString(),
    //         // // guid, payment.getOrnNo(), encodedString,
    //         // // pdfRcpt.getName(),rcpt.getMttRcptID());

    //         // if (result1 < 1) {
    //         // throw new Exception("Error in uploading receipt to Idaman");
    //         // }
    //     } catch (IOException e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     } catch (Exception e) {
    //         // TODO Auto-generated catch block
    //         e.printStackTrace();
    //     }
    // }

    // // private Integer uploadIdamanAPI(IdamanAPIUploadReq req, Integer mttRcptID)
    // // throws IOException {
    // // List<IdamanAPIUpload> result = Collections.emptyList();
    // // Integer result1 = -1;

    // // // try {
    // // result = idamanAPIUploadService.idaman_api_uploadDoc(req);
    // // // if (result.size() > 0) {
    // // if (CollectionUtils.size(result) > 0) {
    // // // update rcpt table
    // // result1 = mttService.sp_updateMTTRcpt(mttRcptID, result.get(0).getVerid(),
    // // req.getSourceSysDocRefID());

    // // return result1;// result.get(0).getDocRefID();
    // // }
    // // // } catch (Exception e) {
    // // // log.error(e.getMessage(), e);
    // // // }
    // // return result1;
    // // }

    private Integer uploadIdamanAPI(IdamanAPIUploadReq req, Integer otcRcptID, String file_nm) throws IOException {
        List<IdamanAPIUpload> result = Collections.emptyList();
        Integer result1 = -1;

        // try {
        result = idamanAPIUploadService.idaman_api_uploadDoc(req);
        // if (result.size() > 0) {
        if (CollectionUtils.size(result) > 0) {
            // update rcpt table
            result1 = otcrcptcclService.sp_updotcrcpt(otcRcptID, result.get(0).getVerid(), req.getSourceSysDocRefID(),
                    file_nm);
            return result1;// result.get(0).getDocRefID();
        }
        return result1;
    }

     @PostMapping(value = "/getotcrcassignedtaskcount")
       public ResponseEntity<ApiResponse<Integer>> sp_getotcrcassignedtaskactivetaskcount(HttpServletRequest request, @RequestBody OTCReceiptCancellationAssignToRequest otcrcptcclRequest) {
           Integer result = -1;
               if (!authService.isAuthenticated(request)) {
                   return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
               }
              result = otcrcptcclService.sp_getotcrcassignedtaskactivetaskcount(
                otcrcptcclRequest	
              );
              if (result < 0) {
               return APIResponse.InternalServerError();
           }
           notificationSvc.sendNotificationUpdate();
           return APIResponse.SuccessResponse(result);
      }
      @PostMapping(value = "/getotcrccreatedtaskcount")
      public ResponseEntity<ApiResponse<Integer>> sp_getotcrccreatedtaskactivetaskcount(HttpServletRequest request, @RequestBody OTCReceiptCancellationCreatedByRequest otcrcptcclRequest) {
    	  Integer result = -1;
          if (!authService.isAuthenticated(request)) {
              return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
          }
          result = otcrcptcclService.sp_getotcrccreatedtaskactivetaskcount(otcrcptcclRequest);
          if (result < 0) {
          return APIResponse.InternalServerError();
          }
          notificationSvc.sendNotificationUpdate();
          return APIResponse.SuccessResponse(result);
      }

    @PostMapping(value = "/getotcreceiptcancellationhistorydetailsaudit")
    public ResponseEntity<ApiResponse<List<OTCReceiptCancellationHistoryDetailsAudit>>> sp_getotcrcptcclhistorydetailsaudit(
            HttpServletRequest request,
            @RequestBody OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {

        List<OTCReceiptCancellationHistoryDetailsAudit> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }

        result = otcrcptcclService.sp_getotcrcptcclhistorydetailsaudit(otcrcptcclDetsRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RECEIPT_CANCELLATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/getotcreceiptcancellationsupervisor")
    public ResponseEntity<ApiResponse<List<OTCReceiptCancellationSupervisor>>> sp_getotcrcptcclsupervisor(
            HttpServletRequest request,
            @RequestBody OTCReceiptCancellationSupervisorRequest otcrcptcclsupervisorRequest) {

        List<OTCReceiptCancellationSupervisor> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }

        result = otcrcptcclService.sp_getotcrcptcclsupervisor(otcrcptcclsupervisorRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RECEIPT_CANCELLATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }
    
    //
    @PostMapping("/checkotcrcpt")
    public ResponseEntity<ApiResponse<List<OTCReceiptCheck>>> sp_checkotcrcpt(HttpServletRequest request) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<OTCReceiptCheck> result = otcrcptcclService.sp_checkotcrcpt();

        if (result == null || result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RECEIPT_CANCELLATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }


}
