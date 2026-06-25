package com.maven.rms.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.math.BigInteger;
import java.util.Collections;
import java.nio.file.Files;
import java.util.List;
import java.util.Locale;
import java.util.Base64;
import java.nio.file.Paths;
import java.util.UUID;
import java.io.File;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.commons.collections4.CollectionUtils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import com.maven.rms.models.ReprintRcpt;
import com.maven.rms.models.ReprintRcptRequest;
import com.maven.rms.models.OTC.OTCCollectionReceiptingPymtItem;
import com.maven.rms.models.OTC.OTCPaymentDone;
import com.maven.rms.models.OTC.OTCRcpt;
import com.maven.rms.models.OTC.OTCollectionReceiptRequest;
import com.maven.rms.models.RROrderInfo;
import com.maven.rms.models.RRPaymentItems;
import com.maven.rms.models.RRPaymentInfo;
import com.maven.rms.models.RRPaymentInfoV2;
import com.maven.rms.models.RRReceiptInfo;
import com.maven.rms.models.ReceiptRequest;
import com.maven.rms.models.RRJustification;
import com.maven.rms.config.RMSProperties;
import com.maven.rms.models.IdamanAPIUpload;
import com.maven.rms.models.IdamanAPIUploadReq;
import com.maven.rms.models.MTTPG;
import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OTCReceiptCancellationDetailsRequest;
import com.maven.rms.models.OTCReceiptCancellationHistoryDetails;
import com.maven.rms.models.OTCReceiptCclMTTOrderStatusRequest;
import com.maven.rms.models.OTCReceiptRpMTTOrderStatusRequest;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.models.RRHistoryTable;
import com.maven.rms.models.RRHistoryTableV2;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.IdamanAPIUploadService;
import com.maven.rms.services.MTTService;
import com.maven.rms.services.OTCRcptCclService;
import com.maven.rms.services.OnlinePaymentService;
import com.maven.rms.services.RICPService;
import com.maven.rms.services.ReprintReceiptService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.CacheManager;
import com.maven.rms.utils.ControllersEnum;
import com.maven.rms.utils.RMSLogger;
import com.maven.rms.utils.receipts.MTTPGReceiptGenerator;
import com.maven.rms.utils.receipts.MTTPGWatermarkedReceiptGenerator;
import com.maven.rms.utils.receipts.OTCReceiptCancelledGenerator;
import com.maven.rms.utils.receipts.OTCReceiptReprintGenerator;

@RestController
@RequestMapping("/api/rr/v1")
@Slf4j
public class ReprintReceiptController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ReprintReceiptService reprintReceiptService;

    private OnlinePaymentService onlinePaymentService;

    @Autowired
    private IdamanAPIUploadService idamanAPIUploadService;

    @Autowired
    private MTTPGWatermarkedReceiptGenerator mttpgWatermarkedReceiptGenerator;

    @Autowired
    private MTTPGReceiptGenerator mttpgReceiptGenerator;

    @Autowired
    private RICPService ricpSvc;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private MTTService mttService;

    @Autowired
    private OTCRcptCclService otcrcptcclService;

    @Autowired
    private OTCReceiptReprintGenerator receiptOTCGenerator;

    private final String serviceID;
    private final String servicePW;
    private final String callBackURL;
    private final String returnURL;
    private final String onlinePortalURL;

    public ReprintReceiptController(OnlinePaymentService onlinePaymentService, RMSProperties rmsProperties) {
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

    @PostMapping(value = "/reprintreceipt")
    public ResponseEntity<ApiResponse<List<ReprintRcpt>>> getReprintReceipt(
            HttpServletRequest request,
            @RequestBody ReprintRcptRequest reprintRcptRequest) {
        List<ReprintRcpt> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = reprintReceiptService.sp_getreprintreceipt(reprintRcptRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/orderinfo_rr")
    public ResponseEntity<ApiResponse<List<RROrderInfo>>> getRROrderInfo(
            HttpServletRequest request,
            @RequestBody ReprintRcptRequest reprintRcptRequest) {
        List<RROrderInfo> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = reprintReceiptService.sp_getorderinfo_rr(reprintRcptRequest);//

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/paymentitems_rr")
    public ResponseEntity<ApiResponse<List<RRPaymentItems>>> getRRPaymentItems(
            HttpServletRequest request,
            @RequestBody ReprintRcptRequest reprintRcptRequest) {
        List<RRPaymentItems> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = reprintReceiptService.sp_getpaymentitems_rr(reprintRcptRequest);//

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/paymentinfo_rr")
    public ResponseEntity<ApiResponse<List<RRPaymentInfo>>> getRRPaymentInfo(
            HttpServletRequest request,
            @RequestBody ReprintRcptRequest reprintRcptRequest) {
        List<RRPaymentInfo> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = reprintReceiptService.sp_getpaymentinfo_rr(reprintRcptRequest);//

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/mttrcptrp")
    public ResponseEntity<ApiResponse<List<RRReceiptInfo>>> getMTTRcptRp(
            HttpServletRequest request,
            @RequestBody ReprintRcptRequest reprintRcptRequest) {
        List<RRReceiptInfo> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = reprintReceiptService.sp_getmttrcptrp(reprintRcptRequest);//

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/paymentinfo_rr_v2")
    public ResponseEntity<ApiResponse<List<RRPaymentInfoV2>>> getRRPaymentInfoV2(
            HttpServletRequest request,
            @RequestBody ReprintRcptRequest reprintRcptRequest) {
        List<RRPaymentInfoV2> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = reprintReceiptService.sp_getpaymentinfo_rr_v2(reprintRcptRequest);//

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/receiptinfo_rr")
    public ResponseEntity<ApiResponse<List<RRReceiptInfo>>> getRRReceiptInfo(
            HttpServletRequest request,
            @RequestBody ReprintRcptRequest reprintRcptRequest) {
        List<RRReceiptInfo> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = reprintReceiptService.sp_getreceiptinfo_rr(reprintRcptRequest);//

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/historytable_rr")
    public ResponseEntity<ApiResponse<List<RRHistoryTable>>> getRRHistoryTable(
            HttpServletRequest request,
            @RequestBody ReprintRcptRequest reprintRcptRequest) {
        List<RRHistoryTable> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = reprintReceiptService.sp_gethistorytable_rr(reprintRcptRequest);//

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/historytable_rr_v2")
    public ResponseEntity<ApiResponse<List<RRHistoryTableV2>>> getotcrcptrpnthistorydetails(
            HttpServletRequest request,
            @RequestBody ReprintRcptRequest reprintRcptRequest) {

        List<RRHistoryTableV2> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }

        result = reprintReceiptService.sp_getotcrcptrpnthistorydetails(reprintRcptRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/justification_rr")
    public ResponseEntity<ApiResponse<List<RRJustification>>> getRRJustification(
            HttpServletRequest request,
            @RequestBody ReprintRcptRequest reprintRcptRequest) {
        List<RRJustification> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = reprintReceiptService.sp_getjustification_rr(reprintRcptRequest);//

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updrcptcount_rr")
    public ResponseEntity<ApiResponse<Integer>> updrcptcount(
            HttpServletRequest request,
            @RequestBody ReprintRcptRequest reprintRcptRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = reprintReceiptService.sp_updrcptcount_rr(
                reprintRcptRequest
        // reprintRcptRequest.getI_mtt_id()
        // authService.getLoginUserName()

        );

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/updrcptcount_mtt")
    public ResponseEntity<ApiResponse<Integer>> updrcptcountmtt(
            HttpServletRequest request,
            @RequestBody ReprintRcptRequest reprintRcptRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = reprintReceiptService.sp_updrcptcount_mtt(
                reprintRcptRequest
        // reprintRcptRequest.getI_mtt_id()
        // authService.getLoginUserName()

        );

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/updrcptjust_rr")
    public ResponseEntity<ApiResponse<Integer>> updrcptjust(
            HttpServletRequest request,
            @RequestBody ReprintRcptRequest reprintRcptRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = reprintReceiptService.sp_updrcptjust_rr(
                reprintRcptRequest,
                reprintRcptRequest.getI_otc_rc_rp_id(),
                reprintRcptRequest.getI_otc_rcpt_id(),
                reprintRcptRequest.getI_justication(),
                authService.getLoginUserName()

        );

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);

    }


    /// new method
    @PostMapping(value = "/updatemttorderstatusandreceipt")
    public ResponseEntity<ApiResponse<Integer>> sp_updmtt_orderstatus(HttpServletRequest request,
            @RequestBody OTCReceiptRpMTTOrderStatusRequest mttOrderStatusRequest) {

        try {
            Integer result = 0;

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            mttOrderStatusRequest.setI_modified_by("system");

            result = reprintReceiptService.sp_checkrcptcl(mttOrderStatusRequest);

            if (result > 0) {
                return APIResponse.InternalServerError();
            }

            OTCRcpt otcRcpt = reprintReceiptService.sp_getotcreceiptrp(mttOrderStatusRequest);
            System.out.println("otcRcpt: " + otcRcpt);
            System.out.println("mttOrderStatusRequest" + mttOrderStatusRequest);
            OTCPaymentDone payment = otcrcptcclService.sp_getotcrcptcclorder(mttOrderStatusRequest.getI_mtt_id());

            // Format date for payment
            //SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH);
            String formattedDate = sdf.format(payment.getPayment_dt());
            payment.setFormattedDate(formattedDate);

            List<OTCCollectionReceiptingPymtItem> paymentItems = otcrcptcclService
                    .sp_getotcrcptcllpymtitembymtt(mttOrderStatusRequest.getI_mtt_id());

            // Generate PDF receipt
            File pdfRcpt = receiptOTCGenerator.generateReceipt(
                    new OTCollectionReceiptRequest(payment, otcRcpt, paymentItems, "pdf"));

            // Check if PDF file exists
            if (!pdfRcpt.exists()) {
                throw new Exception("Generated PDF file not found: " + pdfRcpt.toString());
            }

            // Create GUID
            UUID uuid = UUID.randomUUID();
            String guid = "RMS-" + uuid.toString();

            byte[] fileContent = Files.readAllBytes(Paths.get(pdfRcpt.toString()));
            String encodedString = Base64.getEncoder().encodeToString(fileContent);

            System.out.println("Encoded PDF Length: " + encodedString.length()); // Debugging

            String formattedReceiptDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            
            System.out.println("receipt no"+otcRcpt.getRcptNo());
            // Upload to Idaman
            Integer result1 = uploadIdamanAPI(
                    new IdamanAPIUploadReq("RMS", otcRcpt.getRcptNo(), "RMSReceipt", formattedReceiptDate,
                            "", "", "", "", "", "", guid, payment.getOrn_no(), "", "", "", "", "", "",
                            encodedString, pdfRcpt.getName()),
                    otcRcpt.getOtc_rcpt_id(), pdfRcpt.getName());

            if (result1 < 1) {
                throw new Exception("Error in uploading receipt to Idaman");
            }

            return APIResponse.SuccessResponse(result);

        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
            e.printStackTrace();
            return APIResponse.NoDataFound(ControllersEnum.OTC_RECEIPT_CANCELLATION_CONTROLLER);
        }
    }

    private Integer uploadIdamanAPI(IdamanAPIUploadReq req, Integer otcRcptID, String file_nm) throws IOException {
        List<IdamanAPIUpload> result = Collections.emptyList();
        Integer result1 = -1;

        result = idamanAPIUploadService.idaman_api_uploadDoc(req);

        // Debug API response
        System.out.println("Idaman API Upload Response: " + result);

        if (CollectionUtils.size(result) > 0) {
            System.out.println("VerID: " + result.get(0).getVerid());
            System.out.println("SourceSysDocRefID: " + req.getSourceSysDocRefID());

            result1 = otcrcptcclService.sp_updotcrcpt(
                    otcRcptID, result.get(0).getVerid(), req.getSourceSysDocRefID(), file_nm);
            return result1;
        }

        return result1;
    }

}