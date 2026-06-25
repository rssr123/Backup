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

import com.maven.rms.models.BillingClass;
import com.maven.rms.models.BillingType;
import com.maven.rms.models.BillingTypeRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.BillingTypeService;
import com.maven.rms.utils.APIResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("api/bltc/v1")
public class BillingTypeController {

    @Autowired
    private AuthService authService;

    @Autowired
    private BillingTypeService billingTypeService;

    @PostMapping(value = "/getBillingType")
    public ResponseEntity<?> getBillingType(
            HttpServletRequest request,
            @RequestBody BillingTypeRequest billingTypeRequest) {

        List<BillingType> result = billingTypeService.getBillingType(billingTypeRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/addBillingType")
    public ResponseEntity<ApiResponse<Integer>> addBillingType(
            HttpServletRequest request,
            @RequestBody BillingTypeRequest billingTypeRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Integer result = billingTypeService.sp_insbltc(billingTypeRequest);

        if (result == -1){
            return APIResponse.DuplicateData();
        }
        else if (result < -1) {
            log.error("Exception in " + this.getClass().toString() 
                    + "addBillingType func - sp_insbltc failed with code:" + Integer.toString(result));
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/insBLTCItem")
    public ResponseEntity<ApiResponse<Integer>> sp_insbltcitem(
            HttpServletRequest request,
            @RequestBody List<BillingTypeRequest> billingTypeRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = billingTypeService.sp_insbltcitem(billingTypeRequest);

        if (result <= 0) {
        	log.error("Exception in " + this.getClass().toString() 
        			+ "addBillingType func - sp_insbltc failed with code:" + Integer.toString(result));
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updBLTCItem")
    public ResponseEntity<ApiResponse<Integer>> sp_updbltcitem(
            HttpServletRequest request,
            @RequestBody List<BillingTypeRequest> billingTypeRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = billingTypeService.sp_updbltcitem(billingTypeRequest);

        if (result < 0) {
        	log.error("Exception in " + this.getClass().toString() 
        			+ "updBillingType func - sp_updbltc failed with code:" + Integer.toString(result));
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updBillingType")
    public ResponseEntity<?> updateBillingType(
            HttpServletRequest request,
            @RequestBody BillingTypeRequest billingTypeRequest) {
        try {
            Integer result = billingTypeService.sp_updatebltc(billingTypeRequest);

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(Collections.singletonMap("status", billingTypeRequest.getI_status()));
        } catch (Exception e) {
            return APIResponse.InternalServerError();
        }
    }

    @PostMapping(value = "/delBillingType")
    public ResponseEntity<ApiResponse<Integer>> deleteBillingType(
            HttpServletRequest request,
            @RequestBody BillingTypeRequest billingTypeRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = billingTypeService.sp_delbltc(billingTypeRequest);

        if (result <= 0) {
            return APIResponse.DuplicateData();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getnblcm")
    public ResponseEntity<ApiResponse<List<BillingClass>>> sp_getnblcm
    (HttpServletRequest request) {

        List<BillingClass> result = Collections.emptyList();

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            result = billingTypeService.sp_getnblcm();

            if (result.isEmpty()) {
                return APIResponse.NoDataFound();
            }

            return APIResponse.SuccessResponse(result);

    }
}
