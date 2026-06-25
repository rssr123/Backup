package com.maven.rms.controllers;

import com.maven.rms.models.RefundPaymentRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.RefundPaymentInfo;
import com.maven.rms.services.CommonService;
import com.maven.rms.services.RequestPaymentInfoService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

@RestController
@RequestMapping("api/refund/v1/")
@Slf4j
public class RequestPaymentInfoController {

    @Autowired
    private CommonService commonSvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RequestPaymentInfoService requestPaymentInfoService;

    @PostMapping("/requestPaymentInfo")
    public ResponseEntity<Map<String, Object>> getPaymentInfo(
            @Valid @RequestBody RefundPaymentRequest refundPaymentRequest)
            throws JsonProcessingException {

        ExtAudit extAudit = new ExtAudit();
        extAudit.setI_module_nm("requestPaymentInfo");
        extAudit.setI_direction("Incoming");
        extAudit.setI_rms_batch_no(null);
        String jsonBody = objectMapper.writeValueAsString(refundPaymentRequest);
        extAudit.setI_request_body(jsonBody);

        List<RefundPaymentInfo> refundPaymentInfoList = requestPaymentInfoService
                .getRefundPaymentInfo(refundPaymentRequest.getOrn_no());

        Map<String, Object> response = new LinkedHashMap<>();
        Map<String, Object> header = new LinkedHashMap<>();
        header.put("request_ts", LocalDateTime.now().toString());
        header.put("response_ts", LocalDateTime.now().plusSeconds(1).toString());

        if (refundPaymentInfoList != null && !refundPaymentInfoList.isEmpty()) {
            header.put("status_cd", "200");
            header.put("message", "Success");
            response.put("header", header);
            response.put("data", refundPaymentInfoList);
            externalAudit(extAudit, "Success");
            return ResponseEntity.ok(response);
        } else {
            header.put("status_cd", "401");
            header.put("message", "No Data Found");
            response.put("header", header);
            response.put("data", new LinkedHashMap<>());
            externalAudit(extAudit, "No Data Found: Logic Handling at sp_getrequestPaymentInfo, need check sp");
            return ResponseEntity.ok(response);
        }
    }

    private void externalAudit(ExtAudit paramAudit, String Msg) {

        try {
            ExtAudit extAudit = paramAudit;
            extAudit.setI_response_body(Msg);
            commonSvc.sp_insextaudit(extAudit);
        } catch (Exception e) {
            log.error("Error: " + e.getMessage() + ", "
                    + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
        }

    }
}