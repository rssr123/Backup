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

import com.maven.rms.models.OTCBalancingRequest;
import com.maven.rms.models.OTCDailyBal;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.OTCDailyBalService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/otcdailybal/v1")
@Slf4j
public class OTCDailyBalController {

    @Autowired
    private AuthService authService;

    @Autowired
    private OTCDailyBalService dailyBalService;

    @PostMapping(value = "/getotcbranchcode")
    public ResponseEntity<ApiResponse<List<OTCDailyBal>>> getotcbranchcode(
    HttpServletRequest request) 
    {
        List<OTCDailyBal> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String ssm4uuserrefno = authService.getLoginUserName();
        
        if(ssm4uuserrefno.isEmpty() || ssm4uuserrefno.length() == 0){
            return APIResponse.SuccessResponse(result);
        }

        result = dailyBalService.sp_getotcbranchcode(ssm4uuserrefno);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getotcdailyballisting")
    public ResponseEntity<ApiResponse<List<OTCDailyBal>>> getotcdailyballisting(
    HttpServletRequest request,
    @RequestBody OTCDailyBal BodyRequest) 
    {
        List<OTCDailyBal> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = dailyBalService.sp_getotcdailyballist(BodyRequest);

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/checkotcdailybalval")
    public ResponseEntity<ApiResponse<Integer>> checkotcdailybalval(
    HttpServletRequest request,
    @RequestBody OTCDailyBal BodyRequest) 
    {
        Integer result;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = dailyBalService.sp_checkotcdailybalval(BodyRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updotcdailybalstatus")
    public ResponseEntity<ApiResponse<Integer>> updotcdailybalstatus(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        Integer result;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BodyRequest.setSsm4uuserrefno(authService.getLoginUserName());

        result = dailyBalService.sp_updotcdailybalstatus(BodyRequest);

        if (result == 0) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/checkotcbalstatus")
    public ResponseEntity<ApiResponse<List<OTCDailyBal>>> checkotcbalstatus(
    HttpServletRequest request,
    @RequestBody OTCDailyBal BodyRequest) 
    {
        List<OTCDailyBal> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = dailyBalService.sp_checkotcbalstatus(BodyRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

}
