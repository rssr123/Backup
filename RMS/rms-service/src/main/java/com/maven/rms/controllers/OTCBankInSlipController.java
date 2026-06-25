package com.maven.rms.controllers;

import java.math.BigInteger;
import java.sql.Date;
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

import com.maven.rms.models.FMSARIModel;
import com.maven.rms.models.OTCBalancingRequest;
import com.maven.rms.models.OTCBankInSlip;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.FMSARIService;
import com.maven.rms.services.OTCBankInSlipService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/otcbis/v1")
@Slf4j
public class OTCBankInSlipController {
    @Autowired
    private AuthService authService;

    @Autowired
    private OTCBankInSlipService otcBankInSlip;

    @Autowired
    private FMSARIService fmsARIService;

    @PostMapping(value = "/getotcbisinfo")
    public ResponseEntity<ApiResponse<List<OTCBankInSlip>>> getotcbisinfo(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        List<OTCBankInSlip> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcBankInSlip.sp_getotcbisinfo(BodyRequest.getBranch_code(), BodyRequest.getBal_date());

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcbiscash")
    public ResponseEntity<ApiResponse<List<OTCBankInSlip>>> getotcbiscash(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        List<OTCBankInSlip> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcBankInSlip.sp_getotcbiscash(BodyRequest.getBranch_code(), BodyRequest.getBal_date());

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcbisphy")
    public ResponseEntity<ApiResponse<List<OTCBankInSlip>>> getotcbisphy(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        List<OTCBankInSlip> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcBankInSlip.sp_getotcbisphy(BodyRequest.getBranch_code(), BodyRequest.getBal_date());

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/insbankinslip")
    public ResponseEntity<ApiResponse<BigInteger>> insbankinslip(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        BigInteger result = null;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcBankInSlip.sp_insbankinslip(BodyRequest.getBranch_code(), BodyRequest.getBal_date(), authService.getLoginUserName());

        if (result == null) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updotcfms")
    public ResponseEntity<ApiResponse<Integer>> updotcfms(
    HttpServletRequest request,
    @RequestBody String otc_type, Date dt_balancing) 
    {
        Integer result = 0;
        List<FMSARIModel> arilist = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        //FMS ARI
        arilist = otcBankInSlip.sp_getotcfmsari(otc_type, dt_balancing);

        //Call generateStringBody once AFTER the loop
        if(arilist != null && arilist.size() > 0){
            fmsARIService.generateStringBody(arilist);
        }

        if(arilist != null && arilist.size() > 0)
        {           
            return APIResponse.SuccessResponse(result);
        }
        else{
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }
    }
}
