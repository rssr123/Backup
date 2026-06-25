package com.maven.rms.controllers;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.PaymentItemDetails;
import com.maven.rms.models.PaymentRequest;
import com.maven.rms.models.RefundDetailPymtItem;
import com.maven.rms.models.RefundRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.models.payload.responses.SubmitRefundResponse;
import com.maven.rms.models.payload.responses.SubmitRefundResponseFail;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.CommonService;
import com.maven.rms.services.NotificationService;
import com.maven.rms.services.SubmitRefundService;
import com.maven.rms.utils.APIResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/refund/v1")
@Slf4j
public class SubmitRefundController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SubmitRefundService submitRefundService;

    @Autowired
    private NotificationService notificationSvc;

    @Autowired
    private CommonService commonSvc;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "/submitRefund")
    public ResponseEntity<?> submitRefund(
            HttpServletRequest request,
            @Valid @RequestBody RefundRequest refundRequest) throws JsonProcessingException {

        ExtAudit extAudit = new ExtAudit();
        extAudit.setI_module_nm("submitRefund");
        extAudit.setI_direction("Incoming");
        extAudit.setI_rms_batch_no(null);
        String jsonBody = objectMapper.writeValueAsString(refundRequest);
        extAudit.setI_request_body(jsonBody);

        if (!authService.isAuthenticated(request)) {
            externalAudit(extAudit, "No Permission");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        refundRequest.setCreated_by(authService.getLoginUserName());
        refundRequest.setModified_by(authService.getLoginUserName());

        String returnResult = submitRefundService.sp_insrefund_ss(refundRequest);
        // default everything
        Integer mttId = null;
        Integer rttWfId = null;
        String appNo = returnResult; // default to the whole string

        if (returnResult != null && returnResult.contains("|")) {
            String[] parts = returnResult.split("\\|", 3);
            if (parts.length == 3) {
                mttId = Integer.valueOf(parts[0]);
                rttWfId = Integer.valueOf(parts[1]);
                appNo = parts[2];
            }
        }

        if (mttId != null && rttWfId != null) {
            List<PaymentItemDetails> paymentItems = submitRefundService.getRefundPaymentItems(mttId);
            refundRequest.setPayment_item_details(paymentItems);
            refundRequest.setRtt_wf_id(rttWfId);
            refundRequest.setMtt_id(mttId);

            // 2) debug‐print
            System.out.println("DEBUG: Fetched "
                    + paymentItems.size()
                    + " payment items for mttId=" + mttId
                    + ", rttWfId=" + rttWfId);

            for (PaymentItemDetails item : paymentItems) {
                System.out.println("  → " + item);
            }

            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setPayment_item_details(paymentItems);

            Integer result = submitRefundService.sp_insertRttItem(refundRequest, paymentRequest);
        }

        if ("-1".equals(appNo)) {
            // Create no data found response
            SubmitRefundResponse noDataResponse = new SubmitRefundResponse();
            SubmitRefundResponse.Header noDataHeader = new SubmitRefundResponse.Header();
            noDataHeader.setRequest_ts(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            noDataHeader.setResponse_ts(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            noDataHeader.setStatus_cd("401");
            noDataHeader.setMessage("No Data Found");

            noDataResponse.setHeader(noDataHeader);
            noDataResponse.setData(Collections.emptyList());

            externalAudit(extAudit, "noDataResponse: Logic Handling at sp_insertRttItem, need check sp");

            return ResponseEntity.status(HttpStatus.OK).body(noDataResponse);
        }

        if ("-2".equals(appNo)) {
            // Create system rule violation response
            SubmitRefundResponseFail systemRuleViolationResponse = new SubmitRefundResponseFail();
            SubmitRefundResponseFail.Header systemRuleViolationHeader = new SubmitRefundResponseFail.Header();
            systemRuleViolationHeader.setRequest_ts(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            systemRuleViolationHeader.setResponse_ts(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            systemRuleViolationHeader.setStatus_cd("403");
            systemRuleViolationHeader.setMessage("System Rule Violation");

            SubmitRefundResponseFail.Data systemRuleViolationData = new SubmitRefundResponseFail.Data();
            systemRuleViolationData.setApp_msg("Appeal Reason Required");
            systemRuleViolationData
                    .setApp_rejected_reason("Status is not contain appeal reason");

            systemRuleViolationResponse.setHeader(systemRuleViolationHeader);
            systemRuleViolationResponse.setData(Collections.singletonList(systemRuleViolationData));

            externalAudit(extAudit, "system rule violation: Status is not contain appeal reason");

            return ResponseEntity.status(HttpStatus.OK).body(systemRuleViolationResponse);
        }

        if ("-3".equals(appNo)) {
            // Create system rule violation response
            SubmitRefundResponseFail systemRuleViolationResponse = new SubmitRefundResponseFail();
            SubmitRefundResponseFail.Header systemRuleViolationHeader = new SubmitRefundResponseFail.Header();
            systemRuleViolationHeader.setRequest_ts(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            systemRuleViolationHeader.setResponse_ts(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            systemRuleViolationHeader.setStatus_cd("403");
            systemRuleViolationHeader.setMessage("System Rule Violation");

            SubmitRefundResponseFail.Data systemRuleViolationData = new SubmitRefundResponseFail.Data();
            systemRuleViolationData.setApp_msg("Refund Request Error");
            systemRuleViolationData
                    .setApp_rejected_reason("Status is not Refund Rejected or Appeal Count exceeded");

            systemRuleViolationResponse.setHeader(systemRuleViolationHeader);
            systemRuleViolationResponse.setData(Collections.singletonList(systemRuleViolationData));

            externalAudit(extAudit, "system rule violation: Status is not Refund Rejected or Appeal Count exceeded");

            return ResponseEntity.status(HttpStatus.OK).body(systemRuleViolationResponse);
        }

        if ("-4".equals(appNo)) {
            SubmitRefundResponseFail fail = new SubmitRefundResponseFail();
            SubmitRefundResponseFail.Header h = new SubmitRefundResponseFail.Header();
            h.setRequest_ts(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            h.setResponse_ts(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            h.setStatus_cd("403");
            h.setMessage("System Rule Violation");

            SubmitRefundResponseFail.Data d = new SubmitRefundResponseFail.Data();
            d.setApp_msg("New Refund Request not required appeal reason");
            d.setApp_rejected_reason("Remove 'appeal_reason' for first-time requests.");
            fail.setHeader(h);
            fail.setData(Collections.singletonList(d));

            externalAudit(extAudit, "Rule violation: appeal_reason provided on new request");
            return ResponseEntity.status(HttpStatus.OK).body(fail);
        }

        if ("-5".equals(appNo)) {
            SubmitRefundResponseFail fail = new SubmitRefundResponseFail();
            SubmitRefundResponseFail.Header h = new SubmitRefundResponseFail.Header();
            h.setRequest_ts(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            h.setResponse_ts(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            h.setStatus_cd("403");
            h.setMessage("System Rule Violation");

            SubmitRefundResponseFail.Data d = new SubmitRefundResponseFail.Data();
            d.setApp_msg("Invalid Input");
            d.setApp_rejected_reason("Empty string not accepted for 'appeal_reason' (general rule).");
            fail.setHeader(h);
            fail.setData(Collections.singletonList(d));

            externalAudit(extAudit, "Rule violation: empty appeal_reason");
            return ResponseEntity.status(HttpStatus.OK).body(fail);
        }

        if (appNo == null) {
            // Create error response
            SubmitRefundResponse errorResponse = new SubmitRefundResponse();
            SubmitRefundResponse.Header errorHeader = new SubmitRefundResponse.Header();
            errorHeader.setRequest_ts(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            errorHeader.setResponse_ts(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            errorHeader.setStatus_cd("500");
            errorHeader.setMessage("Server Error");

            errorResponse.setHeader(errorHeader);
            errorResponse.setData(Collections.emptyList());

            externalAudit(extAudit, "INTERNAL_SERVER_ERROR: Logic Handling at sp_insertRttItem, need check sp");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }

        // Create success response
        SubmitRefundResponse response = new SubmitRefundResponse();
        SubmitRefundResponse.Header header = new SubmitRefundResponse.Header();
        header.setRequest_ts(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        header.setResponse_ts(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        header.setStatus_cd("200");
        header.setMessage("Success");

        SubmitRefundResponse.Data data = new SubmitRefundResponse.Data();
        data.setApp_no(appNo);
        data.setApp_status("Pending Finance Admin");
        data.setApp_msg("Request Submitted");

        response.setHeader(header);
        response.setData(Collections.singletonList(data));

        notificationSvc.sendNotificationUpdate();

        externalAudit(extAudit, "Success");

        return ResponseEntity.ok(response);
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