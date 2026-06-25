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

import com.maven.rms.models.CCTaskList;
import com.maven.rms.models.CCTaskListReq;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.CreditControlSMEService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/CCSME/v1")
@Slf4j
public class CreditControlSMEController {
    @Autowired
    private AuthService authService;

    @Autowired
    private CreditControlSMEService spService;

    @PostMapping(value = "/getccsmetasklist")
    public ResponseEntity<ApiResponse<List<CCTaskList>>> sp_getcreditcontroltasklist(HttpServletRequest request,
            @RequestBody CCTaskListReq getRequest) {

        List<CCTaskList> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getcreditcontroltasklist(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.CREDIT_CONTROL_SME_TASK_LIST_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/assignccsmetask")
    public ResponseEntity<ApiResponse<Integer>> sp_assigncctask(HttpServletRequest request, 
    @RequestBody List<CCTaskListReq> assignRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_assigncctask(assignRequest);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);

    }
}
