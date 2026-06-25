package com.maven.rms.controllers;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.BillingClass;
import com.maven.rms.models.BillingClassRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.BillingClassService;
import com.maven.rms.utils.APIResponse;

@RestController
@RequestMapping("api/blc/v1")
public class BillingClassController {

    @Autowired
    private AuthService authService;

    @Autowired
    private BillingClassService billingClassService;

    @PostMapping(value = "/getBillingClass")
    public ResponseEntity<?> getBillingClasses(
            HttpServletRequest request,
            @RequestBody BillingClassRequest billingClassRequest) {

        List<BillingClass> result = billingClassService.getBillingClass(billingClassRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/addBillingClass")
    public ResponseEntity<ApiResponse<Integer>> addBillingClass(
            HttpServletRequest request,
            @RequestBody BillingClassRequest billingClassRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = billingClassService.sp_insblcm(billingClassRequest);

        if (result <= 0) {
            return APIResponse.DuplicateData();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updBillingClass")
    public ResponseEntity<?> updateBillingClass(
            HttpServletRequest request,
            @RequestBody BillingClassRequest billingClassRequest) {
        try {
            Integer result = billingClassService.sp_updateblcm(billingClassRequest);

            if (result <= 0) {
                return APIResponse.DuplicateData();
            }

            return APIResponse.SuccessResponse(Collections.singletonMap("status", billingClassRequest.getI_status()));
        } catch (Exception e) {
            return APIResponse.InternalServerError();
        }
    }

    @PostMapping(value = "/delBillingClass")
    public ResponseEntity<ApiResponse<Integer>> deleteBillingClass(
            HttpServletRequest request,
            @RequestBody BillingClassRequest billingClassRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = billingClassService.sp_delblcm(billingClassRequest);

        if (result <= 0) {
            return APIResponse.DuplicateData();
        }

        return APIResponse.SuccessResponse(result);
    }
}
