package com.maven.rms.controllers;

import java.math.BigInteger;
import java.sql.Date;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import com.maven.rms.models.BranchCodeCounter;
import com.maven.rms.models.BranchCodeCounterRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.BranchCodeCounterService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.SystemStatus;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/bcc/v1")
@Slf4j
public class BranchCodeCounterController {

    @Autowired
    private AuthService authService;

    @Autowired
    private BranchCodeCounterService branchCodeCounterService;

    @PostMapping(value = "/getbranchcodecounter")
    public ResponseEntity<ApiResponse<List<BranchCodeCounter>>> getBranchCodeCounter(
            HttpServletRequest request,
            @RequestBody BranchCodeCounterRequest getRequest) {
            List<BranchCodeCounter> result = Collections.emptyList();

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            result = branchCodeCounterService.sp_getbccmap(getRequest);

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.BRANCH_CODE_COUNTER_CONTROLLER);
            }
            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/addbranchcodecounter")
    public ResponseEntity<ApiResponse<Integer>> addBranchCodeCounter(
            HttpServletRequest request,
            @RequestBody BranchCodeCounterRequest insertRequest) {

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            insertRequest.setI_created_by(authService.getLoginUserName());
            insertRequest.setI_modified_by(authService.getLoginUserName());
            insertRequest.setI_status(SystemStatus.Active.getMessage());

            Integer result = branchCodeCounterService.sp_insbccmap(insertRequest);

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updbranchcodecounter")
    public ResponseEntity<ApiResponse<Integer>> updateBranchCodeCounter(
            HttpServletRequest request,
            @RequestBody BranchCodeCounterRequest updateRequest) {

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            updateRequest.setI_modified_by(authService.getLoginUserName());
            updateRequest.setI_status(SystemStatus.Active.getMessage());

            Integer result = branchCodeCounterService.sp_updbccmap(updateRequest);

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/delbranchcodecounter")
    public ResponseEntity<ApiResponse<Integer>> deleteBranchCodeCounter(
            HttpServletRequest request,
            @RequestBody BranchCodeCounterRequest deleteRequest) {


            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            deleteRequest.setI_modified_by(authService.getLoginUserName());
            Integer result = branchCodeCounterService.sp_delbccmap(deleteRequest);

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
    }

}
