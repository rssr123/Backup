package com.maven.rms.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.maven.rms.config.RMSProperties;
import com.maven.rms.models.BankDocRequest;
import com.maven.rms.models.BankReconRequest;
import com.maven.rms.models.BankReconResponse;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.BankReconService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.CacheManager;
import com.maven.rms.utils.RMSLogger;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/bankRecon/v1")
@Slf4j
public class BankReconController {

    private BankReconService bankService;

    private final RMSProperties rmsProperties;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private BankReconService bService;

    @PersistenceContext
    private EntityManager entityManager;

    private final String ghlServiceId;

    public BankReconController(RMSProperties rmsProperties, BankReconService bankService) {
        this.rmsProperties = rmsProperties;
        this.bService = bankService;
        this.ghlServiceId = rmsProperties.getGHLServiceID();

        RMSLogger.info("BankReconController services is started");
        RMSLogger.info("GHL Service ID: " + ghlServiceId);
    }

    @PostMapping(value = "/sp_uploadBankDoc")
    public ResponseEntity<ApiResponse<String>> sp_uploadBankFile(HttpServletRequest request,@RequestBody BankDocRequest bankDocRequest) throws IllegalArgumentException, SQLException, IOException {
        Integer result;

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            result = bService.sp_uploadDoc(bankDocRequest, authService.getLoginUserName());

            if(result == 1){
                return APIResponse.InvalidFormat();
            }
            return APIResponse.SuccessResponse(result.toString());
    }


    @PostMapping(value = "/sp_getPGSettlementDate")
    public ResponseEntity<ApiResponse<List<String>>> sp_getPGSettlementDate(HttpServletRequest request) {

        List<String> result = Collections.emptyList();
        String settlementDate;

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            result = bService.sp_getPGSettlementDateTaskList();

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.BANK_RECON_CONTROLLER);
            }

            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/sp_getBankReconTask")
    public ResponseEntity<ApiResponse<List<BankReconResponse>>> sp_getBankReconTask(HttpServletRequest request,
            @RequestBody BankReconRequest BankRequest) {

        List<BankReconResponse> result = Collections.emptyList();

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            result = bService.sp_getBankReconTaskList(BankRequest);

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.BANK_RECON_CONTROLLER);
            }

            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/sp_checkbktask")
    public ResponseEntity<ApiResponse<Integer>> sp_checkbktask(HttpServletRequest request,
    @RequestBody BankReconRequest BankRequest) {

    Integer result = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = bService.sp_checkbktask(BankRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.BANK_RECON_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }
}
