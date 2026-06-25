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
import com.maven.rms.models.OTCMasterBal;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.OTCMasterBalService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/otcmasterbal/v1")
@Slf4j
public class OTCMasterBalController {
    @Autowired
    private AuthService authService;

    @Autowired
    private OTCMasterBalService masterBalService;

    @PostMapping(value = "/getotcmasterballist")
    public ResponseEntity<ApiResponse<List<OTCMasterBal>>> getotcmasterballist(
    HttpServletRequest request,
    @RequestBody OTCMasterBal BodyRequest) 
    {
        List<OTCMasterBal> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = masterBalService.sp_getotcmasterballist(BodyRequest);

        if (result.isEmpty()) 
        {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/checkotcmasterbalval")
    public ResponseEntity<ApiResponse<Integer>> checkotcmasterbalval(
    HttpServletRequest request,
    @RequestBody OTCMasterBal BodyRequest) 
    {
        Integer result;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = masterBalService.sp_checkotcmasterbalval(BodyRequest);

        if (result == 0) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updotcmasterbalstatus")
    public ResponseEntity<ApiResponse<Integer>> updotcmasterbalstatus(
    HttpServletRequest request,
    @RequestBody OTCBalancingRequest BodyRequest) 
    {
        Integer result;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BodyRequest.setSsm4uuserrefno(authService.getLoginUserName());

        result = masterBalService.sp_updotcmasterbalstatus(BodyRequest);

        if (result == 0) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }
}
