package com.maven.rms.controllers;

import java.math.BigInteger;
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
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import com.maven.rms.models.FMSLedger;
import com.maven.rms.models.FMSLedgerRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.FMSService;
import com.maven.rms.services.StoreProcedureService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.RMSLogger;

@RestController
@RequestMapping("/api/fmsl/v1")
@Slf4j
public class FMSLedgerController {

    //private static final Logger logger = LoggerFactory.getLogger(FMSLedgerDocController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private FMSService spService;

    // @Secured("ROLE_USER")
    @PostMapping(value = "/getfmsledger")
    public ResponseEntity<ApiResponse<List<FMSLedger>>> getFmsLedger(HttpServletRequest request,
            @RequestBody FMSLedgerRequest fmsLedgerRequest) {
        List<FMSLedger> result = Collections.emptyList();
        // try {

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            result = spService.sp_getfmsledger_v2(
                    fmsLedgerRequest
                    );

            if (result.isEmpty()) {
                return APIResponse.NoDataFound();
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
