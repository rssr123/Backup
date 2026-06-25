package com.maven.rms.controllers;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.models.DailySettlementRequest;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.NonRMSSales;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.CommonService;
import com.maven.rms.services.NonRMSSalesService;
import com.maven.rms.services.SPService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.RMSLogger;

import lombok.extern.slf4j.Slf4j;

@Valid
@RestController
@RequestMapping("/api/sp/v1")
@Slf4j
public class SPController {

    @Autowired
    private AuthService authService;

    @Autowired
    private SPService spService;

    @Autowired
    private NonRMSSalesService rmsService;

    @Autowired
    private CommonService commonSvc;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "/submitDailySettlement")
    public ResponseEntity<?> sp_insdailysettlement(HttpServletRequest request,
            @Valid @RequestBody DailySettlementRequest dailyRequest) throws JsonProcessingException {

        ExtAudit extAudit = new ExtAudit();
        extAudit.setI_module_nm("submitDailySettlement");
        extAudit.setI_direction("Incoming");
        extAudit.setI_rms_batch_no(null);
        String jsonBody = objectMapper.writeValueAsString(dailyRequest);
        extAudit.setI_request_body(jsonBody);

        if (!authService.isAuthenticated(request)) {
            externalAudit(extAudit, "No Permission");
            //return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String result = spService.sp_checksp(dailyRequest);

        if (!"1".equals(result)) {
            externalAudit(extAudit, "DuplicateDataExternal: Logic Handling at sp_checksp, need check sp");
            return APIResponse.DuplicateDataExternal(Collections.singletonMap("referenceNo", result)); // Stop
                                                                                                       // processing if
                                                                                                       // check fails
        }

        String result1 = spService.sp_insdailysettlement(dailyRequest);

        if ("-1".equals(result1) || "-7".equals(result1)) {
            externalAudit(extAudit,
                    "SystemRuleViolationExternal: Logic Handling at sp_insdailysettlement, need check sp");
            return APIResponse.SystemRuleViolationExternal();
        } else if ("-6".equals(result1)) {
            externalAudit(extAudit, "DuplicateDataExternal: Logic Handling at sp_insdailysettlement, need check sp");
            return APIResponse.DuplicateDataExternal();
        } else if (result1 == null || result1.startsWith("-")) {
            externalAudit(extAudit, "InvalidFormatExternal: Logic Handling at sp_insdailysettlement, need check sp");
            return APIResponse.InvalidFormatExternal();
        }

        externalAudit(extAudit, "Success");
        // Success case, return the generated ID
        return APIResponse.SuccessResponseExternal(Collections.singletonMap("referenceNo", result1));
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

    @PostMapping(value = "/submitNonRMSSales")
    public ResponseEntity<ApiResponse<String>> submitNonRMSSales(
            HttpServletRequest request,
            @Valid @RequestBody NonRMSSales bodyRequest) throws JsonProcessingException {

        ExtAudit extAudit = new ExtAudit();
        extAudit.setI_module_nm("submitNonRMSSales");
        extAudit.setI_direction("Incoming");
        extAudit.setI_rms_batch_no(null);
        String jsonBody = objectMapper.writeValueAsString(bodyRequest);
        extAudit.setI_request_body(jsonBody);

        if (!authService.isAuthenticated(request)) {
            externalAudit(extAudit, "No Permission");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (bodyRequest.getSs_cd() == null || bodyRequest.getSs_cd().trim().isEmpty()
                || bodyRequest.getCn_cust_id() == null || bodyRequest.getCn_cust_id().trim().isEmpty()
                || bodyRequest.getDn_cust_id() == null || bodyRequest.getDn_cust_id().trim().isEmpty()
                || bodyRequest.getCash_acct() == null || bodyRequest.getCash_acct().trim().isEmpty()
                || bodyRequest.getMerchant_id() == null || bodyRequest.getMerchant_id().trim().isEmpty()
                || bodyRequest.getStmt_no() == null || bodyRequest.getStmt_no().trim().isEmpty()
                || bodyRequest.getFms_ari_ref_no() == null || bodyRequest.getFms_ari_ref_no().trim().isEmpty()
                || bodyRequest.getAri_total_amt() == null
                || bodyRequest.getMdr_total_amt() == null
                || bodyRequest.getTotal_net_amt() == null
                || bodyRequest.getDt_settlement() == null) {
            externalAudit(extAudit, "SystemRuleViolationExternal, Mandatory Field Is Null");
            return APIResponse.SystemRuleViolationExternal();
        }

        if (bodyRequest.getBatch_cnt() > bodyRequest.getBatch_size()) {
            externalAudit(extAudit, "SystemRuleViolationExternal, Batch Count > Batch Size");
            return APIResponse.SystemRuleViolationExternal();
        }

        Integer result = rmsService.sp_insnonrmssales(
                bodyRequest);

        if (result == 0) {
            externalAudit(extAudit, "Success");
            return APIResponse.SuccessResponseExternal("");
        } else if (result == 1) {
            externalAudit(extAudit, "NoDataFoundExternal: Logic Handling at sp_insnonrmssales, need check sp");
            return APIResponse.NoDataFoundExternal();
        } else if (result == 2) {
            externalAudit(extAudit, "InvalidFormatExternal: Logic Handling at sp_insnonrmssales, need check sp");
            return APIResponse.InvalidFormatExternal();

        } else if (result == 3) {
            externalAudit(extAudit, "DuplicateDataExternal: Logic Handling at sp_insnonrmssales, need check sp");
            return APIResponse.DuplicateDataExternal();
        } else if (result == 4) {
            externalAudit(extAudit, "SystemRuleViolationExternal: Logic Handling at sp_insnonrmssales, need check sp");
            return APIResponse.SystemRuleViolationExternal();
        } else {
            externalAudit(extAudit, "InvalidFormatExternal: Logic Handling at sp_insnonrmssales, need check sp");
            return APIResponse.InvalidFormatExternal();
        }
    }
}