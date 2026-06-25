package com.maven.rms.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.RefundMyTaskListing;
import com.maven.rms.models.RefundMyTaskListingRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.NotificationService;
import com.maven.rms.services.RefundService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;
import com.maven.rms.utils.RMSLogger;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/rtt/v1")
@Slf4j
public class RefundController {
    
    @Autowired
    private AuthService authService;
    @Autowired
    private RefundService refundService;
    @Autowired
    private NotificationService notificationSvc;

    public RefundController(AuthService authService, RefundService refundService) {
        this.authService = authService;
        this.refundService = refundService;

        RMSLogger.info("RefundController services is started");
    }

    // Lisitings to show in My Tasks page
    @PostMapping(value = "/getrefundlisting")
    public ResponseEntity<ApiResponse<List<RefundMyTaskListing>>> sp_getrefundlisting(HttpServletRequest request, @RequestBody RefundMyTaskListingRequest refundRequest) {

        List<RefundMyTaskListing> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = refundService.sp_getrefundlisting(refundRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.REFUND_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getrefundassignedtaskcount")
    public ResponseEntity<ApiResponse<Integer>> sp_getrefundassignedtaskactivetaskcount(HttpServletRequest request, @RequestBody RefundMyTaskListingRequest refundMyTaskListingRequest) {

        Integer result = -1;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = refundService.sp_getrefundassignedtaskactivetaskcount(refundMyTaskListingRequest);

        if (result < 0) {
            return APIResponse.NoDataFound(ControllersEnum.REFUND_CONTROLLER);
        }

        notificationSvc.sendNotificationUpdate();
        return APIResponse.SuccessResponse(result);
    }
    @PostMapping(value = "/getrefundcreatedtaskcount")
    public ResponseEntity<ApiResponse<Integer>> sp_getrefundcreatedtaskactivetaskcount(HttpServletRequest request, @RequestBody RefundMyTaskListingRequest refundMyTaskListingRequest) {

        Integer result = -1;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = refundService.sp_getrefundcreatedtaskactivetaskcount(refundMyTaskListingRequest);

        if (result < 0) {
            return APIResponse.NoDataFound(ControllersEnum.REFUND_CONTROLLER);
        }

        notificationSvc.sendNotificationUpdate();
        return APIResponse.SuccessResponse(result);
    }
}
