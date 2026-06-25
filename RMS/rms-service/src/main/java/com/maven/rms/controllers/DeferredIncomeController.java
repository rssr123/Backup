package com.maven.rms.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.models.AgentDeferredIncome;
import com.maven.rms.models.DeferredIncome;
import com.maven.rms.models.DeferredIncomeTermination;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AgentDeferredIncomeService;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.CommonService;
import com.maven.rms.services.DeferredIncomeService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

@Valid
@RestController
@RequestMapping("/api/di/v1")
@Slf4j
public class DeferredIncomeController {

    @Autowired
    private AuthService authService;

    @Autowired
    private DeferredIncomeService diService;

    @Autowired
    private AgentDeferredIncomeService adiService;

    @Autowired
    private CommonService commonSvc;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "/submitdirecognition")
    public ResponseEntity<ApiResponse<String>> deferredIncomeRecognition(
            HttpServletRequest request,
            @Valid @RequestBody DeferredIncome recognitionRequest) throws JsonProcessingException {

        ExtAudit extAudit = new ExtAudit();
        extAudit.setI_module_nm("submitdirecognition");
        extAudit.setI_direction("Incoming");
        extAudit.setI_rms_batch_no(null);
        String jsonBody = objectMapper.writeValueAsString(recognitionRequest);
        extAudit.setI_request_body(jsonBody);

        if (!authService.isAuthenticated(request)) {
            externalAudit(extAudit, "No Permission");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (recognitionRequest.getDt_expiry().compareTo(recognitionRequest.getDt_effective()) >= 0) {

            BigInteger result = diService.sp_insdi(
                    recognitionRequest);

            if (result == null) {
                externalAudit(extAudit, "InvalidFormatExternal: Logic Handling at sp_insdi, need check sp");
                return APIResponse.InvalidFormatExternal();
            } else if (result.compareTo(BigInteger.valueOf(2)) == 0) {
                externalAudit(extAudit, "Success");
                return APIResponse.SuccessResponseExternal("");
            } else if (result.compareTo(BigInteger.valueOf(1)) == 0) {
                externalAudit(extAudit, "DuplicateDataExternal: Logic Handling at sp_insdi, need check sp");
                return APIResponse.DuplicateDataExternal();
            } else {
                externalAudit(extAudit, "NoDataFoundExternal: Logic Handling at sp_insdi, need check sp");
                return APIResponse.NoDataFoundExternal();
            }
        } else {

            externalAudit(extAudit,
                    "NoDataFoundExternal > Expiry date should be the same as or later than the effective date");

            return APIResponse.NoDataFoundExternal();
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

    @PostMapping(value = "/submitditermination")
    public ResponseEntity<ApiResponse<String>> deferredIncomeTermination(
            HttpServletRequest request,
            @Valid @RequestBody DeferredIncomeTermination terminationRequest) throws JsonProcessingException {

        ExtAudit extAudit = new ExtAudit();
        extAudit.setI_module_nm("submitditermination");
        extAudit.setI_direction("Incoming");
        extAudit.setI_rms_batch_no(null);
        String jsonBody = objectMapper.writeValueAsString(terminationRequest);
        extAudit.setI_request_body(jsonBody);

        if (!authService.isAuthenticated(request)) {
            externalAudit(extAudit, "No Permission");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BigInteger result = diService.sp_insdi_tmn_log(
                terminationRequest);

        if (result == null) {
            externalAudit(extAudit, "InvalidFormatExternal: Logic Handling at sp_insdi_tmn_log, need check sp");
            return APIResponse.InvalidFormatExternal();
        } else if (result.compareTo(BigInteger.ZERO) > 0) {
            externalAudit(extAudit, "Success");

            return APIResponse.SuccessResponseExternal("");
        } else if (result.compareTo(BigInteger.ZERO) < 0) {
            externalAudit(extAudit, "DuplicateDataExternal: Logic Handling at sp_insdi_tmn_log, need check sp");
            return APIResponse.DuplicateDataExternal();
        } else {
            externalAudit(extAudit, "NoDataFoundExternal: Logic Handling at sp_insdi_tmn_log, need check sp");
            return APIResponse.NoDataFoundExternal();
        }

    }

    @PostMapping(value = "/getdeferredincome")
    public ResponseEntity<ApiResponse<List<DeferredIncome>>> getDeferredIncome(
            HttpServletRequest request,
            @RequestBody DeferredIncome deferredIncmRequest) {
        List<DeferredIncome> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = diService.sp_getdi(
                deferredIncmRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.DEFERRED_INCOME_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/submitAgentDIPayment")
    public ResponseEntity<ApiResponse<String>> submitAgentDIPayment(
            HttpServletRequest request,
            @Valid @RequestBody AgentDeferredIncome bodyRequest) throws JsonProcessingException {

        ExtAudit extAudit = new ExtAudit();
        extAudit.setI_module_nm("submitAgentDIPayment");
        extAudit.setI_direction("Incoming");
        extAudit.setI_rms_batch_no(null);
        String jsonBody = objectMapper.writeValueAsString(bodyRequest);
        extAudit.setI_request_body(jsonBody);

        if (!authService.isAuthenticated(request)) {
            externalAudit(extAudit, "No Permission");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = adiService.sp_insagentdi(
                bodyRequest);

        if (result == 1) {
            externalAudit(extAudit, "NoDataFoundExternal: Logic Handling at sp_insagentdi, need check sp");
            return APIResponse.NoDataFoundExternal();
        } else if (result == 2) {
            externalAudit(extAudit, "InvalidFormatExternal: Logic Handling at sp_insagentdi, need check sp");
            return APIResponse.InvalidFormatExternal();

        } else if (result == 0) {
            externalAudit(extAudit, "Success");
            return APIResponse.SuccessResponseExternal("");
        } else {
            externalAudit(extAudit,
                    "NoDataFoundExternal: result is null, Logic Handling at sp_insagentdi, need check sp");
            return APIResponse.NoDataFoundExternal();
        }
    }

}
