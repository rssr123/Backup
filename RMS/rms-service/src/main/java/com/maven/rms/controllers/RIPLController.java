package com.maven.rms.controllers;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.config.RMSProperties;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.RIPLRealizedRequest;
import com.maven.rms.models.RIPLRecognitionRequest;
import com.maven.rms.models.RIPLRequest;
import com.maven.rms.models.RIPLResponse;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.CommonService;
import com.maven.rms.services.RIPLService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.CacheManager;
import com.maven.rms.utils.RMSLogger;
import com.maven.rms.utils.ControllersEnum;
import java.text.ParseException;

@Valid
@RestController
@RequestMapping("/api/ripl/v1")
@Slf4j
public class RIPLController {
    // private static final Logger logger =
    // LoggerFactory.getLogger(OnlinePaymentController.class);
    private RIPLService riplService;

    private final RMSProperties rmsProperties;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private RIPLService rService;

    @Autowired
    private CommonService commonSvc;

    @Autowired
    private ObjectMapper objectMapper;

    public RIPLController(RMSProperties rmsProperties, RIPLService riplService) {
        this.rmsProperties = rmsProperties;
        this.riplService = riplService;

        RMSLogger.info("RIPLController services is started");
    }

    // @Secured("ROLE_USER")
    @PostMapping(value = "/submitriplrecognition")
    public ResponseEntity<ApiResponse<String>> sp_insRIPL(HttpServletRequest request,
            @Valid @RequestBody RIPLRecognitionRequest riplRecognitionRequest)
            throws ParseException, JsonProcessingException {

        ExtAudit extAudit = new ExtAudit();
        extAudit.setI_module_nm("submitriplrecognition");
        extAudit.setI_direction("Incoming");
        extAudit.setI_rms_batch_no(null);
        String jsonBody = objectMapper.writeValueAsString(riplRecognitionRequest);
        extAudit.setI_request_body(jsonBody);

        BigInteger result;

        if (!authService.isAuthenticated(request)) {
            externalAudit(extAudit, "No Permission");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String ssCd = riplRecognitionRequest.getSs_cd();
        if (ssCd == null || ssCd.trim().isEmpty()) {
            externalAudit(extAudit, "ss_cd not found");
            return APIResponse.SystemRuleViolationExternal();
        }

        String dateStr = riplRecognitionRequest.getDt_due();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = formatter.parse(dateStr);

        SimpleDateFormat outputFormatter = new SimpleDateFormat("d/M/yyyy");
        String outputDateStr = outputFormatter.format(date);

        riplRecognitionRequest.setDt_due(outputDateStr);
        riplRecognitionRequest.setCreated_by(authService.getLoginUserName());
        riplRecognitionRequest.setModified_by(authService.getLoginUserName());

        result = riplService.sp_insRIPL(riplRecognitionRequest);

        if (result == null) {
            externalAudit(extAudit, "InvalidFormatExternal: Logic Handling at sp_insRIPL, need check sp");
            return APIResponse.InvalidFormatExternal();
        }

        if (result.equals(BigInteger.valueOf(-1)) || result.equals(BigInteger.valueOf(-4))) {
            externalAudit(extAudit, "SystemRuleViolationExternal: Logic Handling at sp_insRIPL, need check sp");
            return APIResponse.SystemRuleViolationExternal();
        }

        if (result.equals(BigInteger.valueOf(-2))) {
            externalAudit(extAudit, "DuplicateDataExternal: Logic Handling at sp_insRIPL, need check sp");
            return APIResponse.DuplicateDataExternal();
        }

        if (result.equals(BigInteger.valueOf(-3))) {
            externalAudit(extAudit, "InternalServerErrorExternal: Logic Handling at sp_insRIPL, need check sp");
            return APIResponse.InternalServerErrorExternal();
        }

        // If it's not -1 or -2, treat it as success
        externalAudit(extAudit, "Success");
        return APIResponse.SuccessResponseExternal("");
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

    // @Secured("ROLE_USER")
    @PostMapping(value = "/submitriplrealizedpayment")
    public ResponseEntity<ApiResponse<String>> sp_updRIPL(HttpServletRequest request,
            @Valid @RequestBody RIPLRealizedRequest riplRealizedRequest) throws JsonProcessingException {

        ExtAudit extAudit = new ExtAudit();
        extAudit.setI_module_nm("submitriplrealizedpayment");
        extAudit.setI_direction("Incoming");
        extAudit.setI_rms_batch_no(null);
        String jsonBody = objectMapper.writeValueAsString(riplRealizedRequest);
        extAudit.setI_request_body(jsonBody);

        BigInteger result = null;

        if (!authService.isAuthenticated(request)) {
            externalAudit(extAudit, "No Permission");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        riplRealizedRequest.setModified_by(authService.getLoginUserName());
        // riplRealizedRequest.setModified_by("system");
        // result=
        // riplService.sp_updRIPL(riplRealizedRequest.getTxn_type(),riplRealizedRequest.getEntity_type(),
        // riplRealizedRequest.getEntity_no(),
        // riplRealizedRequest.getCalendar_yr(),riplRealizedRequest.getRcpt_no(),
        // authService.getLoginUserName());
        result = riplService.sp_updRIPL(riplRealizedRequest);

        // if(result.intValue()==-1){
        // return APIResponse.InvalidFormatExternal();
        // }

        // if (result!=null) {
        // return APIResponse.SuccessResponseExternal("");
        // }

        // return APIResponse.InvalidFormatExternal();

        if (result.intValue() == -1) {
            externalAudit(extAudit, "NoDataFoundExternal: Logic Handling at sp_updRIPL, need check sp");
            return APIResponse.NoDataFoundExternal();
            // return APIResponse.InvalidFormatExternal();
        } else if (result.intValue() == -2) {
            externalAudit(extAudit, "DuplicateDataExternal: Logic Handling at sp_updRIPL, need check sp");
            return APIResponse.DuplicateDataExternal();
        } else if (result == null) {
            externalAudit(extAudit, "InvalidFormatExternal: Logic Handling at sp_updRIPL, need check sp");
            return APIResponse.InvalidFormatExternal();
        } else {
            externalAudit(extAudit, "Success");
            return APIResponse.SuccessResponseExternal("");
        }
        // if (result == null) {
        // return APIResponse.InvalidFormatExternal();
        // } else if (result.intValue() == -1) {
        // return APIResponse.DuplicateDataExternal();
        // // return APIResponse.InvalidFormatExternal();
        // } else {
        // return APIResponse.SuccessResponseExternal("");
        // }

    }

    // @Secured("ROLE_USER")
    @PostMapping(value = "/sp_getRIPL")
    public ResponseEntity<ApiResponse<List<RIPLResponse>>> sp_getRIPL(HttpServletRequest request,
            @RequestBody RIPLRequest RIPLRequest) {

        List<RIPLResponse> result = Collections.emptyList();
        String outputDateStr;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (RIPLRequest.getI_dt_due() == null || RIPLRequest.getI_dt_due().equals("")) {
            outputDateStr = null;
        } else {
            // String dateStr = RIPLRequest.getI_dt_due();
            // SimpleDateFormat formatter = new SimpleDateFormat("d/M/yyyy");
            // Date date = formatter.parse(dateStr);

            // SimpleDateFormat outputFormatter = new SimpleDateFormat("d/M/yyyy");
            outputDateStr = RIPLRequest.getI_dt_due();
        }

        // result= rService.sp_getRIPL(RIPLRequest.getI_page(),RIPLRequest.getI_size(),
        // RIPLRequest.getI_ripl_id(),RIPLRequest.getI_txn_type(),RIPLRequest.getI_entity_type(),
        // RIPLRequest.getI_entity_no(),
        // RIPLRequest.getI_calendar_yr(),outputDateStr, RIPLRequest.getI_ripl_ctype(),
        // RIPLRequest.getI_status());
        result = rService.sp_getRIPL(RIPLRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.RIPL_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }
}
