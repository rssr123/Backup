package com.maven.rms.controllers;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.config.RMSProperties;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.PGReconService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.CacheManager;
import com.maven.rms.utils.RMSLogger;

@RestController
@RequestMapping("/api/pgreconsc/v1")
@Slf4j
public class PGReconSchController {

    private PGReconService PGReconService;

    private final RMSProperties rmsProperties;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private PGReconService pgReconService;

    public PGReconSchController(RMSProperties rmsProperties, PGReconService PGReconService) {
        this.rmsProperties = rmsProperties;
        this.PGReconService = PGReconService;

        RMSLogger.info("PGReconController services is started");
    }

    @PostMapping(value = "/pgReconSch")
    public ResponseEntity<ApiResponse<String>> PGReconSch(HttpServletRequest request) throws SQLException {

        List<BigInteger> rcPGIds = null;

        // extract excel file and pg vs rms
        rcPGIds = pgReconService.sp_insPGTxn();

        // rms vs pg and not found in this pg, check previous pg
        pgReconService.sp_insMTTTxn(rcPGIds);

        return APIResponse.SuccessResponse("");

    }
}
