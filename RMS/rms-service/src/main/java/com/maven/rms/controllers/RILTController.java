package com.maven.rms.controllers;

import java.math.BigInteger;
// import java.util.Date;
// import java.util.Collections;
// import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.CommonService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.CacheManager;
import com.maven.rms.utils.ControllersEnum;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.config.RMSProperties;
import com.maven.rms.utils.RMSLogger;

import com.maven.rms.services.RILTService;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.RILT;
import com.maven.rms.models.RILTRequest;
import com.maven.rms.models.RILTRequest2;
// import com.maven.rms.models.RILTResponse;

@Valid
@RestController
@RequestMapping("/api/rilt/v1")
@Slf4j
public class RILTController {

    private final RMSProperties rmsProperties;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private RILTService riltService;

    @Autowired
    private CommonService commonSvc;

    @Autowired
    private ObjectMapper objectMapper;

    public RILTController(RMSProperties rmsProperties, RILTService riltService) {
        this.rmsProperties = rmsProperties;
        this.riltService = riltService;

        RMSLogger.info("RILTController services is started");
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

    @PostMapping(value = "/submitRILTIssuance")
    public ResponseEntity<ApiResponse<String>> sp_insRILT(
            HttpServletRequest request,
            @Valid @RequestBody RILTRequest riltRequest) throws ParseException, JsonProcessingException {

        ExtAudit extAudit = new ExtAudit();
        extAudit.setI_module_nm("submitRILTIssuance");
        extAudit.setI_direction("Incoming");
        extAudit.setI_rms_batch_no(null);
        String jsonBody = objectMapper.writeValueAsString(riltRequest);
        extAudit.setI_request_body(jsonBody);

        BigInteger result;

        if (!authService.isAuthenticated(request)) {
            externalAudit(extAudit, "No Permission");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = riltService.sp_insRILT(riltRequest);

        if (result.equals(BigInteger.valueOf(1))) {
            externalAudit(extAudit, "Success");
            return APIResponse.SuccessResponseExternal("");
        }

        if (result.equals(BigInteger.valueOf(-1))) {
            externalAudit(extAudit, "InvalidFormatExternal: Logic Handling at sp_insRILT, need check sp");
            return APIResponse.InvalidFormatExternal();
        }

        if (result.equals(BigInteger.valueOf(-2))) {
            externalAudit(extAudit, "DuplicateDataExternal: Logic Handling at sp_insRILT, need check sp");
            return APIResponse.DuplicateDataExternal();
        }

        externalAudit(extAudit, "InternalServerErrorExternal: Logic Handling at sp_insRILT, need check sp");

        return APIResponse.InternalServerErrorExternal();
    }

    @PostMapping(value = "/submitRILTCancelItem")
    public ResponseEntity<ApiResponse<String>> sp_delRILTItem(
            HttpServletRequest request,
            @Valid @RequestBody RILTRequest riltRequest) throws ParseException, JsonProcessingException {

        ExtAudit extAudit = new ExtAudit();
        extAudit.setI_module_nm("submitRILTCancelItem");
        extAudit.setI_direction("Incoming");
        extAudit.setI_rms_batch_no(null);
        String jsonBody = objectMapper.writeValueAsString(riltRequest);
        extAudit.setI_request_body(jsonBody);

        BigInteger result;

        if (!authService.isAuthenticated(request)) {
            externalAudit(extAudit, "No Permission");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        riltRequest.setLit_no(null);

        result = riltService.sp_delRILT(riltRequest);

        if (result.equals(BigInteger.valueOf(1))) {
            externalAudit(extAudit, "Success");
            return APIResponse.SuccessResponseExternal("");
        }

        if (result.equals(BigInteger.valueOf(-3))) {
            externalAudit(extAudit, "System Rule Violation: Recorded was Collected or Cancelled");
            return APIResponse.SystemRuleViolationExternal();
        }

        if (result.equals(BigInteger.valueOf(-1))) {
            externalAudit(extAudit, "InvalidFormatExternal: Logic Handling at sp_delRILT, need check sp");
            return APIResponse.InvalidFormatExternal();
        }

        if (result.equals(BigInteger.valueOf(-2))) {
            externalAudit(extAudit, "NoDataFoundExternal: Logic Handling at sp_delRILT, need check sp");
            return APIResponse.NoDataFoundExternal();
        }

        externalAudit(extAudit, "InternalServerErrorExternal: Logic Handling at sp_delRILT, need check sp");
        return APIResponse.InternalServerErrorExternal();
    }

    @PostMapping(value = "/submitRILTCancel")
    public ResponseEntity<ApiResponse<String>> sp_delRILT(
            HttpServletRequest request,
            @Valid @RequestBody RILTRequest riltRequest) throws ParseException, JsonProcessingException {

        ExtAudit extAudit = new ExtAudit();
        extAudit.setI_module_nm("submitRILTCancel");
        extAudit.setI_direction("Incoming");
        extAudit.setI_rms_batch_no(null);
        String jsonBody = objectMapper.writeValueAsString(riltRequest);
        extAudit.setI_request_body(jsonBody);

        BigInteger result;

        if (!authService.isAuthenticated(request)) {
            externalAudit(extAudit, "No Permission");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        riltRequest.setLit_item_ref(null);

        result = riltService.sp_delRILT(riltRequest);

        if (result.equals(BigInteger.valueOf(1))) {
            externalAudit(extAudit, "Success");
            return APIResponse.SuccessResponseExternal("");
        }

        if (result.equals(BigInteger.valueOf(-3))) {
            externalAudit(extAudit, "System Rule Violation: Recorded was Collected or Cancelled");
            return APIResponse.SystemRuleViolationExternal();
        }

        if (result.equals(BigInteger.valueOf(-1))) {
            externalAudit(extAudit, "InvalidFormatExternal: Logic Handling at sp_delRILT, need check sp");
            return APIResponse.InvalidFormatExternal();
        }

        if (result.equals(BigInteger.valueOf(-2))) {
            externalAudit(extAudit, "NoDataFoundExternal: Logic Handling at sp_delRILT, need check sp");
            return APIResponse.NoDataFoundExternal();
        }
        externalAudit(extAudit, "InternalServerErrorExternal: Logic Handling at sp_delRILT, need check sp");
        return APIResponse.InternalServerErrorExternal();
    }

    @PostMapping(value = "/getRILT")
    public ResponseEntity<ApiResponse<List<RILT>>> getRILT(
            HttpServletRequest request,
            @RequestBody RILTRequest2 getRequest) {
        List<RILT> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = riltService.sp_getRILT(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.RILT_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/submitRILTUpdateItem")
    public ResponseEntity<ApiResponse<String>> sp_updRILTItem(
            HttpServletRequest request,
            @Valid @RequestBody RILTRequest riltRequest) throws ParseException {

        BigInteger result;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = riltService.sp_updRILT(riltRequest);

        if (result.equals(BigInteger.valueOf(1))) {
            return APIResponse.SuccessResponseExternal("");
        }

        if (result.equals(BigInteger.valueOf(-1))) {
            return APIResponse.InvalidFormatExternal();
        }

        if (result.equals(BigInteger.valueOf(-2))) {
            return APIResponse.NoDataFoundExternal();
        }

        return APIResponse.InternalServerErrorExternal();
    }

}
