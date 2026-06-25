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

import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.BranchCodeCounterService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.SystemStatus;
import com.maven.rms.utils.ControllersEnum;

import com.maven.rms.services.HelperService;

import com.maven.rms.models.BranchCodeList;
import com.maven.rms.models.BranchCodeListRequest;

import com.maven.rms.models.MFT;
import com.maven.rms.models.FeeDetailListRequest;

import com.maven.rms.models.Param;
import com.maven.rms.models.ParamListRequest;

@RestController
@RequestMapping("/api/helper/v1")
@Slf4j
public class HelperController {

    @Autowired
    private AuthService authService;

    @Autowired
    private HelperService helperService;

    @PostMapping(value = "/getbranchcodecounterlist")
    public ResponseEntity<ApiResponse<List<BranchCodeList>>> getBranchCodeCounterList(
        HttpServletRequest request,
        @RequestBody BranchCodeListRequest getRequest) {
        List<BranchCodeList> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = helperService.sp_getbcccodes(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.HELPER_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getfeedetaillist")
    public ResponseEntity<ApiResponse<List<MFT>>> getFeeDetailList(
        HttpServletRequest request,
        @RequestBody FeeDetailListRequest getRequest) {
        List<MFT> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = helperService.sp_getfeedetailids(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.HELPER_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getparamlist")
    public ResponseEntity<ApiResponse<List<Param>>> getParamList(
        HttpServletRequest request,
        @RequestBody ParamListRequest getRequest) {
        List<Param> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = helperService.sp_getparamsbygroup(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.HELPER_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

}
