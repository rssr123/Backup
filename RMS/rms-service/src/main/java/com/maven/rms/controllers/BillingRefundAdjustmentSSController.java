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

import com.maven.rms.models.Billing.BillAdjUpdReq;
import com.maven.rms.models.Billing.BillDocReq;
import com.maven.rms.models.Billing.BillDocWithoutFile;
import com.maven.rms.models.Billing.BillGetItem;
import com.maven.rms.models.Billing.BillGetItemReq;
import com.maven.rms.models.Billing.BillLOAAGM;
import com.maven.rms.models.Billing.BillListing;
import com.maven.rms.models.Billing.BillListingRequest;
import com.maven.rms.models.Billing.BillSearch;
import com.maven.rms.models.Billing.BillSearchRequest;
import com.maven.rms.models.Billing.BillingAdjustment;
import com.maven.rms.models.Billing.BillingAdjustmentRequest;
import com.maven.rms.models.Billing.BillingHistory;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.BillingRefundAdjustmentSSService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/BilRASS/v1")
@Slf4j
public class BillingRefundAdjustmentSSController {

    @Autowired
    private AuthService authService;

    @Autowired
    private BillingRefundAdjustmentSSService spService;

    @PostMapping(value = "/getbillsearch")
    public ResponseEntity<ApiResponse<List<BillSearch>>> sp_getbillsearch(HttpServletRequest request,
            @RequestBody BillSearchRequest getRequest) {

        List<BillSearch> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getbillsearch(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.ROLE_AND_PERMISSIONS_CONFIGURATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getbilllisting")
    public ResponseEntity<ApiResponse<List<BillListing>>> sp_getbillcancellisting(HttpServletRequest request,
            @RequestBody BillListingRequest getRequest) {

        List<BillListing> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getbillcancellisting(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.ROLE_AND_PERMISSIONS_CONFIGURATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getbillitems")
    public ResponseEntity<ApiResponse<List<BillGetItem>>> sp_getbillitem(HttpServletRequest request,
            @RequestBody BillGetItemReq getRequest) {

        List<BillGetItem> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getbillitem(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.ROLE_AND_PERMISSIONS_CONFIGURATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getbilldocs")
    public ResponseEntity<ApiResponse<List<BillDocWithoutFile>>> sp_getbilsuppdoc(HttpServletRequest request,
            @RequestBody BillDocReq getRequest) {

        List<BillDocWithoutFile> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getbilsuppdoc(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.ROLE_AND_PERMISSIONS_CONFIGURATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getbillfilecontent")
    public ResponseEntity<ApiResponse<String>> sp_getbillsuppfilecontent(HttpServletRequest request,
            @RequestBody BillDocReq docReq) {

        String result = "";
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getbillsuppfilecontent(docReq);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.FMS_LEDGER_DOC_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updbillcancel")
    public ResponseEntity<ApiResponse<Integer>> sp_updbillcancel(HttpServletRequest request,
            @RequestBody BillDocReq updateRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_updbillcancel(updateRequest);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getbilladjust")
    public ResponseEntity<ApiResponse<List<BillingAdjustment>>> sp_getbilladjustment(HttpServletRequest request,
            @RequestBody BillingAdjustmentRequest getRequest) {

        List<BillingAdjustment> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getbilladjustment(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.ROLE_AND_PERMISSIONS_CONFIGURATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updbilladjust")
    public ResponseEntity<ApiResponse<Integer>> sp_updbilladjust(HttpServletRequest request,
            @RequestBody List<BillAdjUpdReq> insertRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_updbilladjust(insertRequest);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getbillhist")
    public ResponseEntity<ApiResponse<List<BillingHistory>>> sp_getbillhist(HttpServletRequest request,
            @RequestBody BillAdjUpdReq getRequest) {

        List<BillingHistory> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getbillhist(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.ROLE_AND_PERMISSIONS_CONFIGURATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getbillloaagm")
    public ResponseEntity<ApiResponse<List<BillLOAAGM>>> sp_getbillingloaagm(HttpServletRequest request,
            @RequestBody BillDocReq docReq) {

        List<BillLOAAGM> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getbillingloaagm(docReq);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.FMS_LEDGER_DOC_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

}