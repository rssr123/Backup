package com.maven.rms.controllers;

import javax.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.BankReconSchService;


@RestController
@RequestMapping("/api/brsc/v1")
@Slf4j
public class BankReconSchController {
    //private static final Logger logger = LoggerFactory.getLogger(BankReconSchController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private BankReconSchService bankReconSchService;

    public BankReconSchController(BankReconSchService bankReconSchService) {
        this.bankReconSchService = bankReconSchService;
    }

    @PostMapping(value = "/bankReconSch")
    public ResponseEntity<ApiResponse<String>> getTransactionFromFile (
            HttpServletRequest request){

        int result = bankReconSchService.sp_insrcbanktxn();

        if(result > 0){
            return APIResponse.SuccessResponse("");
        }
        else{
            return APIResponse.NoDataFound();
        }
    }    
}
