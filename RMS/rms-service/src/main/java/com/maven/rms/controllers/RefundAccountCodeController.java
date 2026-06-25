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

import com.maven.rms.models.RefundAccountCode;
import com.maven.rms.models.RefundAccountCodeRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.RefundAccountCodeService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.SystemStatus;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/rac/v1")
@Slf4j
public class RefundAccountCodeController {
    
    @Autowired
    private AuthService authService;

    @Autowired
    private RefundAccountCodeService refundAccountCodeService;

    @PostMapping(value = "/getrefundaccountcode")
    public ResponseEntity<ApiResponse<List<RefundAccountCode>>> getRefundAccountCode(
            HttpServletRequest request,
            @RequestBody RefundAccountCodeRequest refundAccountCodeRequest) {
            List<RefundAccountCode> result = Collections.emptyList();

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            result = refundAccountCodeService.sp_getrttacc(refundAccountCodeRequest);

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.REFUND_ACCOUNT_CODE_CONTROLLER);
            }
            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/addrefundaccountcode")
    public ResponseEntity<ApiResponse<Integer>> addRefundAccountCode(
            HttpServletRequest request,
            @RequestBody RefundAccountCodeRequest insertRequest) {

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            insertRequest.setI_created_by(authService.getLoginUserName());
            insertRequest.setI_modified_by(authService.getLoginUserName());
            insertRequest.setI_status(SystemStatus.Active.getMessage());

            Integer result = refundAccountCodeService.sp_insrttacc(insertRequest);

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updrefundaccountcode")
    public ResponseEntity<ApiResponse<Integer>> updateRefundAccountCode(
            HttpServletRequest request,
            @RequestBody RefundAccountCodeRequest updateRequest) {

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            updateRequest.setI_modified_by(authService.getLoginUserName());
            updateRequest.setI_status(SystemStatus.Active.getMessage());

            Integer result = refundAccountCodeService.sp_updrttacc(updateRequest);

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/delrefundaccountcode")
    public ResponseEntity<ApiResponse<Integer>> deleteRefundAccountCode(
            HttpServletRequest request,
            @RequestBody RefundAccountCodeRequest deleteRequest) {


            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            deleteRequest.setI_modified_by(authService.getLoginUserName());
            Integer result = refundAccountCodeService.sp_delrttacc(deleteRequest);

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
    }

}
