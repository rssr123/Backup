package com.maven.rms.controllers;

import java.sql.SQLException;
import java.text.ParseException;
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
import com.maven.rms.models.OTCBalancingDocRequest;
import com.maven.rms.models.OTCBalancingRequest;
import com.maven.rms.models.OTCCtrBalCol;
import com.maven.rms.models.OTCCtrBalPhy;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.OTCBalService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/otcbal/v1")
@Slf4j
public class OTCBalController {
    @Autowired
    private AuthService authService;

    @Autowired
    private OTCBalService otcBalService;

    @PostMapping(value = "/getotcdetails")
    public ResponseEntity<ApiResponse<List<OTCBalInfo>>> getotcdetails(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        List<OTCBalInfo> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcBalService.sp_getotcdetails(BodyRequest.getBranch_code(), BodyRequest.getBal_date());

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcrc")
    public ResponseEntity<ApiResponse<List<OTCBalRC>>> getotcrc(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        List<OTCBalRC> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcBalService.sp_getotcrc(BodyRequest.getBranch_code(), BodyRequest.getBal_date());

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcemvcol")
    public ResponseEntity<ApiResponse<List<OTCCtrBalCol>>> getotcemvcol(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        List<OTCCtrBalCol> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcBalService.sp_getotcemvcol(BodyRequest.getBranch_code(), BodyRequest.getBal_date(), 
                                BodyRequest.getI_page(), BodyRequest.getI_size());

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotccashcol")
    public ResponseEntity<ApiResponse<List<OTCBalCash>>> getotccashcol(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        List<OTCBalCash> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcBalService.sp_getotccashcol(BodyRequest.getBranch_code(), BodyRequest.getBal_date());

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcphyinfo")
    public ResponseEntity<ApiResponse<List<OTCCtrBalPhy>>> getotcphyinfo(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        List<OTCCtrBalPhy> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcBalService.sp_getotcphyinfo(BodyRequest.getBranch_code(), BodyRequest.getBal_date());

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcbaldoclist")
    public ResponseEntity<ApiResponse<List<OTCBalEMV>>> getotcbaldoclist(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        List<OTCBalEMV> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcBalService.sp_getotcbaldoclist(BodyRequest.getBranch_code(), BodyRequest.getBal_date());

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcbaldoc")
    public ResponseEntity<ApiResponse<String>> getotcbaldoc(
    HttpServletRequest request,
    @RequestBody OTCBalancingDocRequest BodyRequest) throws SQLException, ParseException
    {
        String result = "";

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcBalService.sp_getotcbaldoc(BodyRequest);

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/insotcbaldoc")
    public ResponseEntity<ApiResponse<Integer>> insotcbaldoc(
    HttpServletRequest request,
    @RequestBody OTCBalancingDocRequest BodyRequest) throws SQLException, ParseException
    {
        Integer result = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BodyRequest.setSsm4uuserrefno(authService.getLoginUserName());

        result = otcBalService.sp_insotcbaldoc(BodyRequest);

        if (result == 0) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/insotcdbalcashbytotal")
    public ResponseEntity<ApiResponse<Integer>> insotcdbalcashbytotal(
    HttpServletRequest request,
    @RequestBody List<OTCBalancingRequest> listBodyRequest) 
    {
        Integer result = 0;
        OTCBalancingRequest bodyRequest = new OTCBalancingRequest();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        bodyRequest.setSsm4uuserrefno(authService.getLoginUserName());

        result = otcBalService.sp_insotcdbalcashbytotal(listBodyRequest,bodyRequest);

        if (result == 0) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/insotcbalcashbytotal")
    public ResponseEntity<ApiResponse<Integer>> insotcbalcashbytotal(
    HttpServletRequest request,
    @RequestBody List<OTCBalancingRequest> listBodyRequest) 
    {
        Integer result = 0;
        OTCBalancingRequest bodyRequest = new OTCBalancingRequest();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        bodyRequest.setSsm4uuserrefno(authService.getLoginUserName());

        result = otcBalService.sp_insotcbalcashbytotal(listBodyRequest,bodyRequest);

        if (result == 0) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updotcbalcashbytotal")
    public ResponseEntity<ApiResponse<Integer>> updotcbalcashbytotal(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest bodyRequest)
    {
        Integer result = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        bodyRequest.setSsm4uuserrefno(authService.getLoginUserName());

        result = otcBalService.sp_updotcbalcashbytotal(bodyRequest);

        if (result == 0) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/insotccashgrandtotal")
    public ResponseEntity<ApiResponse<Integer>> insotccashgrandtotal(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest)
    {
        Integer result = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BodyRequest.setSsm4uuserrefno(authService.getLoginUserName());

        result = otcBalService.sp_insotccashgrandtotal(BodyRequest);

        if (result == 0) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updotcdailybalstatus")
    public ResponseEntity<ApiResponse<Integer>> updotcdailybalstatus(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        Integer result = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BodyRequest.setSsm4uuserrefno(authService.getLoginUserName());

        result = otcBalService.sp_updotcdailybalstatus(BodyRequest);

        if (result == 0) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }
}
