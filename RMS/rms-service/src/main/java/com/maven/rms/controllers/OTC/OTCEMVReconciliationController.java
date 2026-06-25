package com.maven.rms.controllers.OTC;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.exceptionhandler.ApplicationException;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;
import com.maven.rms.utils.SystemStatus;
import com.maven.rms.services.AuthService;

import com.maven.rms.services.OTC.OTCEMVReconciliationService;

import com.maven.rms.models.OTC.OTCEMVReconciliationRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliationDocRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliationDocUpRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliationStatusRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliation;
import com.maven.rms.models.OTC.OTCEMVReconciliationBoolean;
import com.maven.rms.models.OTC.OTCEMVReconciliationSummary;
import com.maven.rms.models.OTC.OTCEMVReconciliationRC;
import com.maven.rms.models.OTC.OTCEMVReconciliationSettlement;
import com.maven.rms.models.OTC.OTCEMVReconciliationSettlement2;
import com.maven.rms.models.OTC.OTCEMVReconciliationStatus;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/OTCEMVR/v1")
@Slf4j
public class OTCEMVReconciliationController {

    @Autowired
    private AuthService authService;

    @Autowired
    private OTCEMVReconciliationService spService;

    @PostMapping(value = "/getotcemvreconciliation")
    public ResponseEntity<ApiResponse<List<OTCEMVReconciliation>>> sp_getotcemvreconciliation(HttpServletRequest request,
            @RequestBody OTCEMVReconciliationRequest getRequest) {

        List<OTCEMVReconciliation> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getotcemvreconciliation(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_EMV_RECONCILIATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcemvreconciliationcheck")
    public ResponseEntity<ApiResponse<List<OTCEMVReconciliationBoolean>>> sp_getotcemvreconciliationcheck(HttpServletRequest request,
            @RequestBody OTCEMVReconciliationRequest getRequest) {

        List<OTCEMVReconciliationBoolean> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getotcemvreconciliationcheck(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_EMV_RECONCILIATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcemvreconciliationsummary")
    public ResponseEntity<ApiResponse<List<OTCEMVReconciliationSummary>>> sp_getotcemvreconciliationsummary(HttpServletRequest request,
            @RequestBody OTCEMVReconciliationRequest getRequest) {

        List<OTCEMVReconciliationSummary> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getotcemvreconciliationsummary(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_EMV_RECONCILIATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcemvreconciliationrc")
    public ResponseEntity<ApiResponse<List<OTCEMVReconciliationRC>>> sp_getotcemvreconciliationrc(HttpServletRequest request,
            @RequestBody OTCEMVReconciliationRequest getRequest) {

        List<OTCEMVReconciliationRC> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getotcemvreconciliationrc(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_EMV_RECONCILIATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcemvreconciliationsettlement")
    public ResponseEntity<ApiResponse<List<OTCEMVReconciliationSettlement>>> sp_getotcemvreconciliationsettlement(HttpServletRequest request,
            @RequestBody OTCEMVReconciliationRequest getRequest) {

        List<OTCEMVReconciliationSettlement> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getotcemvreconciliationsettlement(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_EMV_RECONCILIATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcbaldoccontent")
    public ResponseEntity<ApiResponse<String>> sp_getotcbaldoccontent(HttpServletRequest request,
            @RequestBody OTCEMVReconciliationDocRequest getRequest) {

            String result = "";
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
    
            result = spService.sp_getotcbaldoccontent(getRequest);
    
            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.OTC_EMV_RECONCILIATION_CONTROLLER);
            }
            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getrcemv")
    public ResponseEntity<ApiResponse<List<OTCEMVReconciliationStatus>>> sp_getrcemv(HttpServletRequest request,
            @RequestBody OTCEMVReconciliationStatusRequest getRequest) {

        List<OTCEMVReconciliationStatus> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getrcemv(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_EMV_RECONCILIATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/insrcemv")
    public ResponseEntity<ApiResponse<Integer>> sp_insrcemv(
            HttpServletRequest request,
            @RequestBody OTCEMVReconciliationStatusRequest insertRequest) {

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            insertRequest.setI_created_by(authService.getLoginUserName());
            insertRequest.setI_modified_by(authService.getLoginUserName());
            insertRequest.setI_status(SystemStatus.Active.getMessage());

            Integer result = spService.sp_insrcemv(insertRequest);

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updrcemv")
    public ResponseEntity<ApiResponse<Integer>> sp_updrcemv(
            HttpServletRequest request,
            @RequestBody OTCEMVReconciliationStatusRequest updateRequest) {

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            updateRequest.setI_modified_by(authService.getLoginUserName());

            Integer result = spService.sp_updrcemv(updateRequest);

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/insrcemvdoc")
    public ResponseEntity<ApiResponse<Integer>> sp_insrcemvdoc(
            HttpServletRequest request,
            @RequestBody OTCEMVReconciliationDocUpRequest insertRequest) throws ApplicationException, IOException, SerialException, SQLException{

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_insrcemvdoc(insertRequest);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcemvreconciliationsettlement2")
    public ResponseEntity<ApiResponse<List<OTCEMVReconciliationSettlement2>>> sp_getrcemvdoclist(HttpServletRequest request,
            @RequestBody OTCEMVReconciliationDocRequest getRequest) {

        List<OTCEMVReconciliationSettlement2> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getrcemvdoclist(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_EMV_RECONCILIATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getrcemvdoccontent")
    public ResponseEntity<ApiResponse<String>> sp_getrcemvdoccontent(HttpServletRequest request,
            @RequestBody OTCEMVReconciliationDocRequest getRequest) {

            String result = "";
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
    
            result = spService.sp_getrcemvdoccontent(getRequest);
    
            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.OTC_EMV_RECONCILIATION_CONTROLLER);
            }
            return APIResponse.SuccessResponse(result);
    }

}
