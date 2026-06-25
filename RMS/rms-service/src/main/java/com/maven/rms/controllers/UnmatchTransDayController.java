package com.maven.rms.controllers;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.TaxCd;
import com.maven.rms.models.TaxCdRequest;
import com.maven.rms.models.UnmatchTrans;
import com.maven.rms.models.UnmatchTransRequest;

import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.StoreProcedureService;
import com.maven.rms.services.UnmatchTransService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/utl/v1")
@Slf4j
public class UnmatchTransDayController {

    //private static final Logger logger = LoggerFactory.getLogger(UnmatchTransDayController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private UnmatchTransService unmatchTransService;

    // @Secured("ROLE_USER")
    @PostMapping(value = "/getunmatchtransday")
    public ResponseEntity<ApiResponse<List<UnmatchTrans>>> getUnmatchTrans(
            HttpServletRequest request,
            @RequestBody UnmatchTransRequest unmatchedTransRequest) {
        List<UnmatchTrans> result = Collections.emptyList();

        // try {

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            result = unmatchTransService.sp_getutldays(unmatchedTransRequest
            );

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.UNMATCH_TRANS_DAY_CONTROLLER);
            }
            return APIResponse.SuccessResponse(result);
        // } catch (NumberFormatException e) {
        //     log.error("Exception in " + this.getClass().toString(), e);
        //     return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        //     log.error("Exception in " + this.getClass().toString(), e);
        //     return APIResponse.InternalServerError();
        // }

    }
    
}
