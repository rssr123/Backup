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
import com.maven.rms.models.OTCBalEMV;
import com.maven.rms.models.OTCBalInfo;
import com.maven.rms.models.OTCBalRC;
import com.maven.rms.models.OTCBalancingRequest;
import com.maven.rms.models.OTCCtrBalCol;
import com.maven.rms.models.OTCCtrBalPhy;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.OTCMBalService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/otcmbal/v1")
@Slf4j
public class OTCMBalController {
    @Autowired
    private AuthService authService;

    @Autowired
    private OTCMBalService otcMBalService;

    @PostMapping(value = "/getotcmdetails")
    public ResponseEntity<ApiResponse<List<OTCBalInfo>>> getotcdetails(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        List<OTCBalInfo> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcMBalService.sp_getotcmdetails(BodyRequest.getBranch_code(), BodyRequest.getBal_date());

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcmrc")
    public ResponseEntity<ApiResponse<List<OTCBalRC>>> getotcrc(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        List<OTCBalRC> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcMBalService.sp_getotcmrc(BodyRequest.getBranch_code(), BodyRequest.getBal_date());

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcmemvcol")
    public ResponseEntity<ApiResponse<List<OTCCtrBalCol>>> getotcemvcol(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        List<OTCCtrBalCol> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcMBalService.sp_getotcmemvcol(BodyRequest.getBranch_code(), BodyRequest.getBal_date(), 
                                BodyRequest.getI_page(), BodyRequest.getI_size());

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcmcashcol")
    public ResponseEntity<ApiResponse<List<OTCBalCash>>> getotccashcol(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        List<OTCBalCash> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcMBalService.sp_getotcmcashcol(BodyRequest.getBranch_code(), BodyRequest.getBal_date());

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcmphyinfo")
    public ResponseEntity<ApiResponse<List<OTCCtrBalPhy>>> getotcphyinfo(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        List<OTCCtrBalPhy> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcMBalService.sp_getotcmphyinfo(BodyRequest.getBranch_code(), BodyRequest.getBal_date());

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcmbaldoclist")
    public ResponseEntity<ApiResponse<List<OTCBalEMV>>> getotcbaldoclist(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        List<OTCBalEMV> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcMBalService.sp_getotcmbaldoclist(BodyRequest.getBranch_code(), BodyRequest.getBal_date());

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }
}
