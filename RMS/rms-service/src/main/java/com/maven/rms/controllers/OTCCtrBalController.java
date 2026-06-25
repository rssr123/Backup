package com.maven.rms.controllers;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.OTCBalCash;
import com.maven.rms.models.OTCCtrBalCol;
import com.maven.rms.models.OTCCtrBalInfo;
import com.maven.rms.models.OTCCtrBalPhy;
import com.maven.rms.models.OTCCtrBalRMS;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.OTCCtrBalService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/otcctrbal/v1")
@Slf4j
public class OTCCtrBalController {

    @Autowired
    private AuthService authService;

    @Autowired
    private OTCCtrBalService ctrBalService;

    @PostMapping(value = "/getotcbalctrinfo")
    public ResponseEntity<ApiResponse<List<OTCCtrBalInfo>>> getotcbalctrinfo(
    HttpServletRequest request,
    @RequestBody OTCCtrBalInfo BodyRequest) 
    {
        List<OTCCtrBalInfo> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = ctrBalService.sp_getotcbalctrinfo(BodyRequest.getCounter_id(), BodyRequest.getOtc_counter_id());

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcrmscol")
    public ResponseEntity<ApiResponse<List<OTCCtrBalRMS>>> getotcrmscol(
    HttpServletRequest request,
    @RequestBody OTCCtrBalRMS BodyRequest) 
    {
        List<OTCCtrBalRMS> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = ctrBalService.sp_getotcrmscol(BodyRequest.getI_page(), BodyRequest.getI_size(), 
                                    BodyRequest.getCounter_id(), BodyRequest.getOtc_counter_id());

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcctrcol")
    public ResponseEntity<ApiResponse<List<OTCCtrBalCol>>> getotcctrcol(
    HttpServletRequest request,
    @RequestBody OTCCtrBalCol BodyRequest) 
    {
        List<OTCCtrBalCol> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = ctrBalService.sp_getotcctrcol(BodyRequest.getI_page(), BodyRequest.getI_size(),
                                 BodyRequest.getCounter_id(), BodyRequest.getOtc_counter_id());

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcphyinfo")
    public ResponseEntity<ApiResponse<List<OTCCtrBalPhy>>> getotcphyinfo(
    HttpServletRequest request,
    @RequestBody OTCCtrBalPhy BodyRequest) 
    {
        List<OTCCtrBalPhy> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = ctrBalService.sp_getotcphyinfo(BodyRequest.getCounter_id(), BodyRequest.getOtc_counter_id());

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotccashinfo")
    public ResponseEntity<ApiResponse<List<OTCBalCash>>> getotccashinfo(
    HttpServletRequest request,
    @RequestBody OTCCtrBalInfo BodyRequest) 
    {
        List<OTCBalCash> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = ctrBalService.sp_getotccashinfo(BodyRequest.getOtc_counter_id());

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }  
    
}
