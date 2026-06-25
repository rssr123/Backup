package com.maven.rms.controllers;

import com.maven.rms.models.IdamanAPIUpload;
import com.maven.rms.models.IdamanAPIUploadReq;
import com.maven.rms.models.OTCReceiptCheck;
import com.maven.rms.models.RefundSlipCheck;
import com.maven.rms.models.SlipRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.ISlipServiceInterface;
import com.maven.rms.services.IdamanAPIUploadService;
import com.maven.rms.services.RefundService;
import com.maven.rms.services.SlipService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;
import com.maven.rms.repositories.SlipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/refund/v1/")
@RequiredArgsConstructor
@Slf4j
public class SlipController {
    @Value("${refund.slip.folder.path}")
    private String folderPath;

    private final SlipService slipService;

    @Autowired
    private AuthService authService;

    @Autowired
    private IdamanAPIUploadService idamanAPIUploadService;

    @Autowired
    private SlipRepository slipRepository;

    @Autowired
    private RefundService refundService;
    
    /*
    @PostMapping("/testSlip")
    public ResponseEntity<?> test(HttpServletRequest request) throws Exception {
    	
        slipService.testLoadPng();
        
        return ResponseEntity.ok("200");
    }
    */
    
    @PostMapping("/GetpdfSlip")
    public ResponseEntity<byte[]> GetpdfSlip(@RequestBody SlipRequest slipRequest, HttpServletRequest request)
            throws Exception {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).build();
        }

        slipRequest.setApprovedBy(authService.getLoginUserName());
        String rttAppNo = slipRepository.getRttAppNo(slipRequest.getRttWfId()); // Fetch only RttAppNo
        slipRequest.setRttAppNo(rttAppNo);

        byte[] pdf = slipService.generateSlipPDF(slipRequest);

        if (pdf == null || pdf.length == 0) {
            log.error("Refund Slip Error: PDF generation failed for RTT_APP_NO={} slipNo={}", rttAppNo,
                    slipRequest.getSlipNo());
        }

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=slip.pdf")
                .header("Content-Type", "application/pdf")
                .body(pdf);
    }

    @PostMapping("/PDFSlipGenerator")
    public ResponseEntity<String> PDFSlipGenerator(
            @RequestBody SlipRequest slipRequest,
            HttpServletRequest request) throws Exception {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).build();
        }

        slipRequest.setApprovedBy(authService.getLoginUserName());
        String rttAppNo = slipRepository.getRttAppNo(slipRequest.getRttWfId());
        SlipRequest slipData = slipRepository.getSlipData(slipRequest);
        slipRequest.setSlipNo(slipData.getSlipNo());
        slipRequest.setOrnNo(slipData.getOrnNo());
        slipRequest.setRttAppNo(rttAppNo);

        // 1) Generate PDF and base64-encode
        byte[] pdf = slipService.generateSlipPDF(slipRequest);
        String encodedString = Base64.getEncoder().encodeToString(pdf);

        // 2) Persist the PDF record (always swallow errors here too)
        int fileSizeKb = pdf.length / 1024;
        try {
            slipRepository.insertSlipDocument(slipRequest, encodedString, fileSizeKb);
        } catch (Exception e) {
            log.warn("Could not insert slip document record for {}:{}", rttAppNo, slipRequest.getSlipNo(), e);
        }

        // 3) Update ‘slip generated’ status (if this fails, we’ll still send the PDF
        // back)
        try {
            int upd = sp_updslipgenerated(rttAppNo, slipRequest.getSlipNo());
            if (upd == -1) {
                log.error("sp_updslipgenerated failed for {}:{}", rttAppNo, slipRequest.getSlipNo());
            }
        } catch (Exception e) {
            log.error("Exception calling sp_updslipgenerated for {}:{}", rttAppNo, slipRequest.getSlipNo(), e);
        }

        // 4) Fire off your Idaman upload, but don’t let it change your response
        try {
            UUID uuid = UUID.randomUUID();
            String guid = "RMS-" + uuid;
            String dateStr = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
            String filename = "SSM-Receipt-" + rttAppNo + ".pdf";

            Integer uploadResult = uploadIdamanAPI(
                    new IdamanAPIUploadReq(
                            "RMS",
                            slipRequest.getSlipNo(),
                            "RMSRefundSlip",
                            dateStr,
                            "", "", "", "", "", "",
                            guid,
                            slipRequest.getOrnNo(),
                            "", "", "", "", "", "",
                            encodedString,
                            filename),
                    rttAppNo,
                    filename);

            if (uploadResult == null || uploadResult <= 0) {
                log.warn("Idaman upload failed (result={}) for {}:{}", uploadResult, rttAppNo, filename);
            } else {
                log.info("Idaman upload succeeded (verId={}) for {}:{}", uploadResult, rttAppNo, filename);
            }
        } catch (Exception e) {
            log.error("Unexpected error uploading to Idaman for {}:{}", rttAppNo, slipRequest.getSlipNo(), e);
        }

        // 5) Always return success with the PDF
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        String body = String.format(
                "{\"status\":\"success\",\"timestamp\":\"%s\",\"data\":\"%s\"}",
                timestamp,
                encodedString);
        return ResponseEntity.ok(body);
    }

    // @PostMapping("/PDFSlipGenerator")
    // public ResponseEntity<String> PDFSlipGenerator(@RequestBody SlipRequest
    // slipRequest, HttpServletRequest request) throws Exception {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).build();
    // }

    // slipRequest.setApprovedBy(authService.getLoginUserName());
    // String rttAppNo = slipRepository.getRttAppNo(slipRequest.getRttWfId()); //
    // Fetch only RttAppNo
    // slipRequest.setRttAppNo(rttAppNo);
    // byte[] pdf = slipService.generateSlipPDF(slipRequest);
    // String encodedString = Base64.getEncoder().encodeToString(pdf);

    // if (encodedString.length() > 100) {
    // encodedString = encodedString.substring(0, 100);
    // }

    // String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new
    // Date());
    // String responseBody =
    // String.format("{\"status\":\"success\",\"timestamp\":\"%s\",\"data\":\"%s\"}",
    // timestamp, encodedString);

    // int fileSizeKb = pdf.length / 1024;
    // slipRepository.insertSlipDocument(slipRequest, encodedString, fileSizeKb);

    // return ResponseEntity.ok(responseBody);
    // }

    private Integer uploadIdamanAPI(IdamanAPIUploadReq req,
            String rtt_app_no,
            String file_nm) {
        Integer result1 = -1;
        List<IdamanAPIUpload> result = Collections.emptyList();

        try {
            result = idamanAPIUploadService.idaman_api_uploadDoc(req);
        } catch (IOException e) {
            // log and swallow – continue on even if the upload failed
            log.error("Idaman upload failed for appNo=" + rtt_app_no +
                    ", docRef=" + req.getSourceSysDocRefID(), e);
        }

        // Only update your table if upload succeeded AND verId is non-null
        if (CollectionUtils.isNotEmpty(result)) {
            try {
                result1 = sp_updrtt_ver_ssid(
                        rtt_app_no,
                        result.get(0).getVerid(),
                        req.getSourceSysDocRefID(),
                        file_nm);
            } catch (Exception e) {
                // optional: catch any DB/SP errors too
                log.error("Failed to update version for appNo=" + rtt_app_no, e);
            }
        }

        // result1 will be -1 if either upload or update didn’t happen
        // — but your method will NOT throw, so the caller can keep going.
        return result1;
    }

    public Integer sp_updrtt_ver_ssid(String i_rtt_app_no, String i_ver_id, String i_ssdocref_id, String file_nm) {
        Integer result = 0;
        try {
            result = slipRepository.sp_updrtt_ver_ssid(i_rtt_app_no, i_ver_id, i_ssdocref_id, file_nm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Integer sp_updslipgenerated(String rttAppNo, String slipNo) {
        Integer result = 0;
        try {
            result = slipRepository.sp_updslipgenerated(rttAppNo, slipNo);
        } catch (Exception e) {
            e.printStackTrace();
            // Optionally, you can assign a specific error code, such as -1, to indicate
            // failure.
            result = -1;
        }
        return result;
    }
    // @PostMapping("/UploadpdfSlip")
    // public ResponseEntity<String> UploadpdfSlip(@RequestBody SlipRequest
    // slipRequest, HttpServletRequest request) throws Exception {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).build();
    // }

    // slipRequest.setApprovedBy(authService.getLoginUserName());
    // byte[] pdf = slipService.generateSlipPDF(slipRequest);
    // String encodedString = Base64.getEncoder().encodeToString(pdf);

    // String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new
    // Date());
    // String responseBody =
    // String.format("{\"status\":\"success\",\"timestamp\":\"%s\",\"data\":\"%s\"}",
    // timestamp, encodedString);

    // return ResponseEntity.ok(responseBody);
    // }

    @PostMapping("/refundSlipCheck")
    public ResponseEntity<ApiResponse<List<RefundSlipCheck>>> sp_getrttwf_id_list(HttpServletRequest request) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).build();
        }

        List<RefundSlipCheck> result = refundService.sp_getrttwf_id_list();

        if (result == null || result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RECEIPT_CANCELLATION_CONTROLLER);// need to change
        }

        return APIResponse.SuccessResponse(result);
    }
}