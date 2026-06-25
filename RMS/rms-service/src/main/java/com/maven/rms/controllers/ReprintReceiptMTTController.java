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

import com.maven.rms.models.IdamanAPIUpload;
import com.maven.rms.models.IdamanAPIUploadReq;
import com.maven.rms.models.MTTPG;
import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OTCReceiptRpMTTOrderStatusRequest;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.models.ReceiptRequest;
import com.maven.rms.models.OTC.OTCCollectionReceiptingPymtItem;
import com.maven.rms.models.OTC.OTCPaymentDone;
import com.maven.rms.models.OTC.OTCRcpt;
import com.maven.rms.models.OTC.OTCollectionReceiptRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.IdamanAPIUploadService;
import com.maven.rms.services.MTTService;
import com.maven.rms.services.OnlinePaymentService;
import com.maven.rms.services.ReprintReceiptService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;
import com.maven.rms.utils.receipts.MTTPGWatermarkedReceiptGenerator;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/mttrcpt/v1")
@Slf4j
public class ReprintReceiptMTTController {

    @Autowired
    private AuthService authService;

    @Autowired
    private IdamanAPIUploadService idamanAPIUploadService;

    @Autowired
    private MTTService mttService;

    @Autowired
    private ReprintReceiptService rrService;

    @Autowired
    private MTTPGWatermarkedReceiptGenerator receiptGenerator;

    @Autowired
    private ReprintReceiptService reprintReceiptService;

    @Autowired
    private OnlinePaymentService onlinePaymentService;

    @PostMapping(value = "/generatereceipt")
    public ResponseEntity<ApiResponse<Integer>> reprintmttreceipt(HttpServletRequest request,
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

            // generate receipt
            MTTRCPT rcpt = new MTTRCPT();
            OnlinePayment payment = new OnlinePayment();
            List<OnlinePaymentItem> paymentItems = Collections.emptyList();

            Integer mttid;
            Long pgId;
            BigInteger mttId = new BigInteger(mttOrderStatusRequest.getI_mtt_id().toString());

            rcpt = rrService.sp_getmttrcptinfo(mttId);

            

            mttid = rcpt.getRmsMTT().getMttId();
            
            pgId = rcpt.getMttPG().getMttPgId();

            log.info("RcptExist<1: " + "Mttid: " + mttid + "PGID: " + pgId);

            // get mtt details
            payment = onlinePaymentService.sp_getMTT(mttid);
            
            log.info("RcptExist<1: " + "payment: " + payment.toString());

            paymentItems = mttService.sp_getMTTItem(mttid);

            // get mtt pg details
            MTTPG pG = new MTTPG();
            pG = mttService.getMttPgById(pgId).orElse(null);

            // Generate PDF receipt
            File pdfRcpt = receiptGenerator
                    .generateReceipt(new ReceiptRequest(
                    pG, 
                    payment, 
                    rcpt, 
                    paymentItems, 
                    "pdf"),2);

            // Check if PDF file exists
            if (!pdfRcpt.exists()) {
                throw new Exception("Generated PDF file not found: " + pdfRcpt.toString());
            }

            // Create GUID
            UUID uuid = UUID.randomUUID();
            String guid = "RMS-" + uuid.toString();

            byte[] fileContent = Files.readAllBytes(Paths.get(pdfRcpt.toString()));
            String encodedString = Base64.getEncoder().encodeToString(fileContent);

            String formatedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

            // System.out.println("receipt no" + otcRcpt.getRcptNo());
            // Upload to Idaman
            Integer result1 = uploadIdamanAPI(
                    new IdamanAPIUploadReq("RMS", rcpt.getRcptNo(), "RMSReceipt", formatedDate,
                            "", "", "", "", "", "", guid, payment.getOrnNo(), "", "", "", "", "", "",
                            encodedString, pdfRcpt.getName()),
                    rcpt.getMttRcptID());

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

    private Integer uploadIdamanAPI(IdamanAPIUploadReq req, Integer mttRcptID) throws IOException {
        List<IdamanAPIUpload> result = Collections.emptyList();
        Integer result1 = -1;

        // try {
        result = idamanAPIUploadService.idaman_api_uploadDoc(req);
        // if (result.size() > 0) {
        if (CollectionUtils.size(result) > 0) {
            // update rcpt table
            result1 = mttService.sp_updateMTTRcpt(mttRcptID, result.get(0).getVerid(), req.getSourceSysDocRefID());

            return result1;// result.get(0).getDocRefID();
        }
        // } catch (Exception e) {
        // log.error(e.getMessage(), e);
        // }
        return result1;
    }

}