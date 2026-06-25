package com.maven.rms.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.maven.rms.models.MyTaskPublicTask;
import com.maven.rms.models.MyTaskPublicTaskRequest;
import com.maven.rms.models.PickUpTasksRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.MyTasksPublicTasksService;
import com.maven.rms.services.NotificationService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.RMSLogger;

import com.maven.rms.models.payload.responses.ApiResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/mytasks/v1")
@Slf4j
public class MyTasksPublicTasksController {

    @Autowired
    private AuthService authService;

    @Autowired
    private MyTasksPublicTasksService myTasksPublicTasksService;

    @Autowired
    private NotificationService notificationSvc;

    public MyTasksPublicTasksController(AuthService authService, MyTasksPublicTasksService myTasksPublicTasksService) {
        this.authService = authService;
        this.myTasksPublicTasksService = myTasksPublicTasksService;

        RMSLogger.info("MyTasksPublicTasksController services is started");
    }

    @PostMapping(value = "/getpublictasks")
    public ResponseEntity<ApiResponse<List<MyTaskPublicTask>>> sp_getpublictasks(HttpServletRequest request,
            @RequestBody MyTaskPublicTaskRequest myTaskPublicTaskRequest) {

        List<MyTaskPublicTask> result = Collections.emptyList();

        if (!authService.isAuthenticated(request))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (myTaskPublicTaskRequest.getI_userrole().length() < 1)
            return APIResponse.NoDataFoundExternal();

        result = myTasksPublicTasksService.sp_getpublictasks(myTaskPublicTaskRequest);

        if (result == null)
            return APIResponse.NoDataFoundExternal();

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/pickuptasks")
    public ResponseEntity<ApiResponse<Integer>> sp_pickuptasks(HttpServletRequest request,
            @RequestBody List<PickUpTasksRequest> pickUpTasksRequests) {

        Integer result = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = myTasksPublicTasksService.sp_pickuptasks(pickUpTasksRequests);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        notificationSvc.sendNotificationUpdate();
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getnotificationcount")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> sp_getnotificationcounts(HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
        if (!authService.isAuthenticated(request))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!payload.containsKey("i_assigned_to") || CollectionUtils.size(payload) != 1)
            return APIResponse.InvalidFormatExternal();

        Map<String, Integer> result = myTasksPublicTasksService
                .sp_getallnotificationcounts(authService.getLoginUserName());

        notificationSvc.sendNotificationUpdate();
        return APIResponse.SuccessResponse(result);
    }
}
