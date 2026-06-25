package com.maven.rms.controllers;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

//import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.config.RMSProperties;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.FeeDetailItems;
import com.maven.rms.models.FeeDetailItemsRequest;
import com.maven.rms.models.FeeGrp;
import com.maven.rms.models.MFT;
import com.maven.rms.models.MFTRequest;
import com.maven.rms.models.MFTTypeSearchRequest;
import com.maven.rms.models.RMSUser;
import com.maven.rms.models.RMSUserRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.CommonService;
import com.maven.rms.services.MFTService;
import com.maven.rms.services.Table1Service;
import com.maven.rms.services.UserRoleService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.RMSLogger;

import lombok.extern.slf4j.Slf4j;
import com.maven.rms.utils.ControllersEnum;

@Valid
@RestController
@RequestMapping("/api/mft/v1")
@Slf4j
public class MFTController {

    @Autowired
    private CommonService commonSvc;

    @Autowired
    private ObjectMapper objectMapper;
    // private static final Logger logger =
    // LoggerFactory.getLogger(MFTController.class);

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private AuthService authService;

    @Autowired
    private MFTService mftService;

    @Autowired
    private UserRoleService userRoleService;

    // start
    // @Secured("ROLE_USER")
    @PostMapping(value = "/getuserbyrole")
    public ResponseEntity<ApiResponse<List<RMSUser>>> sp_getuserbyrole(HttpServletRequest request,
            @RequestBody RMSUserRequest rmsUserRequest) {

        List<RMSUser> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // result =spService.sp_getuserbyrole(
        // rmsUserRequest.getI_page(),
        // rmsUserRequest.getI_size(),
        // rmsUserRequest.getI_role_nm_en(),
        // rmsUserRequest.getI_role_nm_bm(),
        // rmsUserRequest.getI_status()
        // );

        result = userRoleService.sp_getuserbyrole(rmsUserRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.MFT_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/getuserdetail") // not in use now
    public ResponseEntity<ApiResponse<RMSUser>> sp_getuserdetail(HttpServletRequest request,
            @RequestBody RMSUserRequest rmsUserRequest) {

        RMSUser result = new RMSUser();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }

        // result =spService.sp_getuserdetail(
        // rmsUserRequest.getI_ssm4uuserrefno()
        // );

        result = userRoleService.sp_getuserdetail(rmsUserRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.MFT_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    // @Secured("ROLE_USER")
    @PostMapping(value = "/getmasterfeetable")
    public ResponseEntity<ApiResponse<List<MFT>>> sp_getmft(HttpServletRequest request,
            @RequestBody MFTRequest mftRequest) {

        List<MFT> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // result = spService.sp_getmft(
        // mftRequest.getI_page(),
        // mftRequest.getI_size(),
        // mftRequest.getI_fee_detail_pk(),
        // mftRequest.getI_fee_detail_id(),
        // mftRequest.getI_unit_fee_fr(),
        // mftRequest.getI_unit_fee_to(),
        // mftRequest.getI_ss_cd(),
        // mftRequest.getI_tax_cd(),
        // mftRequest.getI_dt_modified_fr(),
        // mftRequest.getI_dt_modified_to(),
        // mftRequest.getI_modified_by(),
        // mftRequest.getI_status()
        // );

        result = mftService.sp_getmft(mftRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.MFT_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    // @Secured("ROLE_USER")
    @PostMapping(value = "/getfeedetailitems")
    public ResponseEntity<ApiResponse<List<FeeDetailItems>>> sp_getfeedetailitems(HttpServletRequest request,
            @Valid @RequestBody FeeDetailItemsRequest feeDetailItemsReq) throws JsonProcessingException {

        ExtAudit extAudit = new ExtAudit();
        extAudit.setI_module_nm("getfeedetailitems");
        extAudit.setI_direction("Incoming");
        extAudit.setI_rms_batch_no(null);
        String jsonBody = objectMapper.writeValueAsString(feeDetailItemsReq);
        extAudit.setI_request_body(jsonBody);

        List<FeeDetailItems> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            try {

                extAudit.setI_response_body("NoPermission");
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for getfeedetailitems: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // result = spService.sp_getfeedetailitems(
        // mftRequest.getFee_detail_id(),
        // mftRequest.getFee_grp_id(),
        // mftRequest.getSs_cd(),
        // mftRequest.getLast_sync_dt(),
        // mftRequest.getExclude_deleted()
        // );

        result = mftService.sp_getfeedetailitems(feeDetailItemsReq);

        if (result.isEmpty()) {
            try {

                extAudit.setI_response_body("No Data Found");
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for getfeedetailitems: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }
            return APIResponse.NoDataFoundExternal();
        }

        try {

            extAudit.setI_response_body("Success");
            commonSvc.sp_insextaudit(extAudit);
        } catch (Exception e) {
            log.error("Error in sp_insextaudit for getfeedetailitems: " + e.getMessage() + ", "
                    + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
        }

        return APIResponse.SuccessResponseExternal(result);

    }

    // @Secured("ROLE_USER")
    @PostMapping(value = "/checkmasterfeetableexist")
    public ResponseEntity<ApiResponse<List<MFT>>> sp_checkmftexist(HttpServletRequest request,
            @RequestBody MFTRequest mftRequest) {

        List<MFT> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = mftService.sp_checkmftexist(mftRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.MFT_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

     // @Secured("ROLE_USER")
    @PostMapping(value = "/getmasterfeetable_typesearch")
    public ResponseEntity<ApiResponse<List<MFT>>> sp_getmft_typesearch(HttpServletRequest request,
            @RequestBody MFTTypeSearchRequest mfttypesearchRequest) {

        List<MFT> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // result = spService.sp_getmft(
        // mftRequest.getI_page(),
        // mftRequest.getI_size(),
        // mftRequest.getI_fee_detail_pk(),
        // mftRequest.getI_fee_detail_id(),
        // mftRequest.getI_unit_fee_fr(),
        // mftRequest.getI_unit_fee_to(),
        // mftRequest.getI_ss_cd(),
        // mftRequest.getI_tax_cd(),
        // mftRequest.getI_dt_modified_fr(),
        // mftRequest.getI_dt_modified_to(),
        // mftRequest.getI_modified_by(),
        // mftRequest.getI_status()
        // );

        result = mftService.sp_getmft_typesearch(mfttypesearchRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.MFT_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

}