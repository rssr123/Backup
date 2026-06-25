package com.maven.rms.controllers;

import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import com.maven.rms.utils.ControllersEnum;
import com.maven.rms.models.BillingMyTaskListing;
import com.maven.rms.models.BillingMyTaskListingRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.BillingMyTaskService;
import com.maven.rms.services.NotificationService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.RMSLogger;
import com.maven.rms.models.payload.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/bil/v1")
@Slf4j
public class BillingMyTaskController {

    @Autowired
    private AuthService authService;
    @Autowired
    private BillingMyTaskService billingService;
    @Autowired
    private NotificationService notificationSvc;

    public BillingMyTaskController(AuthService authService, BillingMyTaskService billingService) {
        this.authService = authService;
        this.billingService = billingService;

        RMSLogger.info("BillingController services is started");
    }

    @PostMapping(value = "/getbillinglisting")
    public ResponseEntity<ApiResponse<List<BillingMyTaskListing>>> sp_getbillinglisting(HttpServletRequest request, @RequestBody BillingMyTaskListingRequest billingRequest) {
        
        List<BillingMyTaskListing> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = billingService.sp_getbillinglisting(billingRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.BILLING_CONTRILLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getbillingassignedtaskcount")
    public ResponseEntity<ApiResponse<Integer>> sp_getbillingassignedtaskactivetaskcount(HttpServletRequest request, @RequestBody BillingMyTaskListingRequest billingRequest) {
        
        Integer result = -1;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = billingService.sp_getbillingassignedtaskactivetaskcount(billingRequest);

        if (result < 0) {
            return APIResponse.NoDataFound(ControllersEnum.BILLING_CONTRILLER);
        }

        notificationSvc.sendNotificationUpdate();
        return APIResponse.SuccessResponse(result);
    }
    
    @PostMapping(value = "/getbillingcreatedtaskcount")
    public ResponseEntity<ApiResponse<Integer>> sp_getbillingcreatedtaskactivetaskcount(HttpServletRequest request, @RequestBody BillingMyTaskListingRequest billingRequest) {
        
        Integer result = -1;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = billingService.sp_getbillingcreatedtaskactivetaskcount(billingRequest);

        if (result < 0) {
            return APIResponse.NoDataFound(ControllersEnum.BILLING_CONTRILLER);
        }

       notificationSvc.sendNotificationUpdate();
        return APIResponse.SuccessResponse(result);
    }
    
}
