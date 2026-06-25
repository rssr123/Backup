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

import com.maven.rms.models.JPAScheduler;
import com.maven.rms.models.JPASchedulerRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.JPASchedulerService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;


@RestController
@RequestMapping("/api/fpascheduler/v1")
public class FPASchedulerController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JPASchedulerService jpaSchedulerService;

    @PostMapping(value = "/getfpascheduler")
    public ResponseEntity<ApiResponse<List<JPAScheduler>>> getJPAScheduler(
            HttpServletRequest request,
            @RequestBody JPASchedulerRequest jpaSchedulerRequest) {
        List<JPAScheduler> result = Collections.emptyList();

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // result = jpaSchedulerService.sp_getfpascheduler(
            //     jpaSchedulerRequest.getI_job_name());
            result = jpaSchedulerService.sp_getfpascheduler(jpaSchedulerRequest);

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.FPASCHEDULER_CONTROLLER);
            }

            return APIResponse.SuccessResponse(result);

    }
    

}
