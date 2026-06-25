package com.maven.rms.controllers;

import java.sql.SQLException;
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

import com.maven.rms.models.BillingIssuanceBySBillingDoc;
import com.maven.rms.models.BillingIssuanceBySSBillingDetailsRequest;
import com.maven.rms.models.MFTWFDocRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.BillingIssuanceBySSService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/bibssdoc/v1")
@Slf4j
public class BillingIssuanceSSDocController {

    @Autowired
    private AuthService authService;

    @Autowired
    private BillingIssuanceBySSService bilIsseBySSService;




    @PostMapping(value = "/getbillingissuancebyssdocument")
    public ResponseEntity<ApiResponse<List<BillingIssuanceBySBillingDoc>>> sp_getbibsslistofdoc(
            HttpServletRequest request, @RequestBody BillingIssuanceBySSBillingDetailsRequest bilRequest) {

        List<BillingIssuanceBySBillingDoc> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = bilIsseBySSService.sp_getbibsslistofdoc(bilRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.BILLING_ISSUANCE_BY_SS_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/getbillingissuancebyssdocfilecontent")
    public ResponseEntity<ApiResponse<String>> sp_getbibssdocfilecontent(HttpServletRequest request,
            @RequestBody BillingIssuanceBySSBillingDetailsRequest bilRequest) throws SQLException {

        String result = "";

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }


        result = bilIsseBySSService.sp_getbibssdocfilecontent(bilRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.MFT_WF_DOC_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);

    }

}
