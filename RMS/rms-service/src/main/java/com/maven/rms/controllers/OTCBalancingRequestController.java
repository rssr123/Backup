package com.maven.rms.controllers;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.maven.rms.models.payload.responses.ApiResponse;

import com.maven.rms.models.OTCBalancingRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.OTCBalancingRequestService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/otcbalancingreq/v1")
@Slf4j
public class OTCBalancingRequestController {
    @Autowired
    private AuthService authService;

    @Autowired
    private OTCBalancingRequestService otcReqService;

    @PostMapping(value = "/insotcbalcash")
    public ResponseEntity<ApiResponse<Integer>> sp_insotcbalcash(
            HttpServletRequest request,
            @RequestBody List<OTCBalancingRequest> otcBalRequest) {

        Integer result = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcReqService.sp_insotcbalcash(otcBalRequest, authService.getLoginUserName());


        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/insotcbalcheque")
    public ResponseEntity<ApiResponse<Integer>> sp_insotcbalcheque(
            HttpServletRequest request,
            @RequestBody List<OTCBalancingRequest> otcBalRequest) {

        Integer result = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcReqService.sp_insotcbalcheque(otcBalRequest, authService.getLoginUserName());


        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/insotcbalbd")
    public ResponseEntity<ApiResponse<Integer>> sp_insotcbalbd(
            HttpServletRequest request,
            @RequestBody List<OTCBalancingRequest> otcBalRequest) {

        Integer result = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcReqService.sp_insotcbalbd(otcBalRequest, authService.getLoginUserName());


        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/insotcbalmo")
    public ResponseEntity<ApiResponse<Integer>> sp_insotcbalmo(
            HttpServletRequest request,
            @RequestBody List<OTCBalancingRequest> otcBalRequest) {

        Integer result = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = otcReqService.sp_insotcbalmo(otcBalRequest, authService.getLoginUserName());

        if (result == 0) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }
    
    @PostMapping(value = "/insotcbalphy")
    public ResponseEntity<ApiResponse<Integer>> sp_insotcbalphy(
            HttpServletRequest request,
            @RequestBody List<OTCBalancingRequest> otcBalRequest) {

        Integer result = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authService.getLoginUserName();
        List<OTCBalancingRequest> chequeRequests = new ArrayList<>();
        List<OTCBalancingRequest> bdRequests = new ArrayList<>();
        List<OTCBalancingRequest> moRequests = new ArrayList<>();

        // Categorize requests based on detail type
        for (OTCBalancingRequest requestItem : otcBalRequest) {
            if ("cheque".equalsIgnoreCase(requestItem.getDetail_type().trim())) {
                chequeRequests.add(requestItem);
            } 
            else if("bank draft".equalsIgnoreCase(requestItem.getDetail_type().trim())){
                bdRequests.add(requestItem);
            }
            else if("money order".equalsIgnoreCase(requestItem.getDetail_type().trim())){
                moRequests.add(requestItem);
            }
        }

        // Process cheque requests if any
        if (!chequeRequests.isEmpty()) {
            result += otcReqService.sp_insotcbalcheque(chequeRequests, username);
        }

        // Process bank draft requests if any
        if (!bdRequests.isEmpty()) {
            result += otcReqService.sp_insotcbalbd(bdRequests, username);
        }
        
        // Process money order requests if any
        if (!moRequests.isEmpty()) {
            result += otcReqService.sp_insotcbalmo(moRequests, username);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updotcbalpymtmode")
    public ResponseEntity<ApiResponse<Integer>> sp_updotcbalpymtmode(
            HttpServletRequest request,
            @RequestBody OTCBalancingRequest otcBalRequest) {

        Integer result = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        otcBalRequest.setSsm4uuserrefno(authService.getLoginUserName());
        result = otcReqService.sp_updotcbalpymtmode(otcBalRequest);

        if (result == 0) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/insotcbalemvs")
    public ResponseEntity<ApiResponse<Integer>> sp_insotcbalemvs(
            HttpServletRequest request,
            @RequestBody OTCBalancingRequest otcBalRequest) {

        Integer result = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        otcBalRequest.setSsm4uuserrefno(authService.getLoginUserName());
        result = otcReqService.sp_insotcbalemvs(otcBalRequest);

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updotcbalstatus")
    public ResponseEntity<ApiResponse<Integer>> sp_updotcbalstatus(
            HttpServletRequest request,
            @RequestBody OTCBalancingRequest otcBalRequest) {

        Integer result = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        otcBalRequest.setSsm4uuserrefno(authService.getLoginUserName());
        result = otcReqService.sp_updotcbalstatus(otcBalRequest);

        if (result == 0) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_COUNTER_BALANCING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }
}
