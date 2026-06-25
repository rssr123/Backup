package com.maven.rms.controllers;
import java.math.BigInteger;
import java.sql.SQLException;
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
 
import com.maven.rms.models.BankReconDetail;
import com.maven.rms.models.BankReconRequest;
import com.maven.rms.models.BankReconSch;
import com.maven.rms.models.PGDetailListingRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.BankReconDetailService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/brdc/v1")
@Slf4j
public class BankReconDetailController {
	//private static final Logger logger = LoggerFactory.getLogger(BankReconDetailController.class);

    @Autowired
    private AuthService authService;
    @Autowired
    private BankReconDetailService bankReconDetailService;

    public BankReconDetailController(BankReconDetailService bankReconDetailService) {
        this.bankReconDetailService = bankReconDetailService;
    }

    @PostMapping(value = "/getrcbankdetails")
    public ResponseEntity<ApiResponse<List<BankReconDetail>>> bankReconDetails(
            HttpServletRequest request,
            @RequestBody BankReconDetail brDetailsRequest) {
           
            List<BankReconDetail> result = Collections.emptyList();
 
        // try {
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
 
                    result = bankReconDetailService.sp_getrcbankdetails(
                    // brDetailsRequest.getTask_no()
                    brDetailsRequest
                    );                
                    //authService.getLoginUserName(),
                    //authService.getLoginUserName(),
                    //SystemStatus.Active.getMessage());
                    
            if (result == null)
            {
                return APIResponse.NoDataFound();
            }
            else{
                return APIResponse.SuccessResponse(result);
            }  
        // } catch (NumberFormatException e) {
        //     log.error("Exception in " + this.getClass().toString(), e);
        //     return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        //     log.error("Exception in " + this.getClass().toString(), e);
        //     return APIResponse.InternalServerError();
        // }
    }

    @PostMapping(value = "/getbankpgtxnlisting")
    public ResponseEntity<ApiResponse<List<BankReconDetail>>> bankPgTxnListing(
            HttpServletRequest request,
            @RequestBody  PGDetailListingRequest pgDetailListingRequest) {
           
            List<BankReconDetail> result = Collections.emptyList();
 
        // try {
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
 
                result = bankReconDetailService.sp_getbankpgtxnlisting(//
                        pgDetailListingRequest
                );                
                //authService.getLoginUserName(),
                //authService.getLoginUserName(),
                //SystemStatus.Active.getMessage());
 
            if (result == null)
            {
                return APIResponse.NoDataFound();
            }
            else{
                return APIResponse.SuccessResponse(result);
            }  
        // } catch (NumberFormatException e) {
        //     log.error("Exception in " + this.getClass().toString(), e);
        //     return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        //     log.error("Exception in " + this.getClass().toString(), e);
        //     return APIResponse.InternalServerError();
        // }
    }

    @PostMapping(value = "/getbanktxnlisting")
    public ResponseEntity<ApiResponse<List<BankReconDetail>>> bankTxnListing(
            HttpServletRequest request,
            @RequestBody BankReconDetail bankTxnListingRequest) {
           
            List<BankReconDetail> result = Collections.emptyList();
 
        // try {
            if (!authService.isAuthenticated(request)) 
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
 
                    result = bankReconDetailService.sp_getbanktxnlisting(
                        bankTxnListingRequest
                    );                
                    //authService.getLoginUserName(),
                    //authService.getLoginUserName(),
                    //SystemStatus.Active.getMessage());
 
            if (result == null)
            {
                return APIResponse.NoDataFound();
            }
            else{
                return APIResponse.SuccessResponse(result);
            }  
        // } catch (NumberFormatException e) {
        //     log.error("Exception in " + this.getClass().toString(), e);
        //     return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        //     log.error("Exception in " + this.getClass().toString(), e);
        //     return APIResponse.InternalServerError();
        // }
    }

    @PostMapping(value = "/getpgfilerelatedtxn")
    public ResponseEntity<ApiResponse<List<BankReconDetail>>> pgfilerelatedtxn(
            HttpServletRequest request,
            @RequestBody BankReconDetail pgfilerelatedtxnRequest, BankReconRequest bankReconRequest , BankReconSch bankReconSch) {
           
            List<BankReconDetail> result = Collections.emptyList();
 
        // try {
            if (!authService.isAuthenticated(request)) 
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
 
                    result = bankReconDetailService.sp_getbankpgfiletxn(
                        pgfilerelatedtxnRequest
                    );                
                    //authService.getLoginUserName(),
                    //authService.getLoginUserName(),
                    //SystemStatus.Active.getMessage());
 
            if (result == null)
            {
                return APIResponse.NoDataFound();
            }
            else{
                return APIResponse.SuccessResponse(result);
            }  
        // } catch (NumberFormatException e) {
        //     log.error("Exception in " + this.getClass().toString(), e);
        //     return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        //     log.error("Exception in " + this.getClass().toString(), e);
        //     return APIResponse.InternalServerError();
        // }
    }

    @PostMapping(value = "/getnobankstmt")
    public ResponseEntity<ApiResponse<List<BankReconDetail>>> nobankstmt(
            HttpServletRequest request,
            @RequestBody BankReconDetail nobankstmtRequest, BankReconRequest bankReconRequest , BankReconDetail bankReconDetail) {
           
            List<BankReconDetail> result = Collections.emptyList();
 
        // try {
            if (!authService.isAuthenticated(request)) 
            {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
 
                    result = bankReconDetailService.sp_getbanknostmt(
                        nobankstmtRequest
                    );                
                    //authService.getLoginUserName(),
                    //authService.getLoginUserName(),
                    //SystemStatus.Active.getMessage());
 
            if (result == null)
            {
                return APIResponse.NoDataFound();
            }
            else{
                return APIResponse.SuccessResponse(result);
            }  
        // } catch (NumberFormatException e) {
        //     log.error("Exception in " + this.getClass().toString(), e);
        //     return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        //     log.error("Exception in " + this.getClass().toString(), e);
        //     return APIResponse.InternalServerError();
        // }
    }

    @PostMapping("/updrcbankdetailstatus")
    public ResponseEntity<ApiResponse<BigInteger>> bankDetailStatus(
            HttpServletRequest request,
            @RequestBody BankReconDetail bankDetailStatusRequest) {
           
            BigInteger result = BigInteger.valueOf(0);
 
        // try {
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
 
                    result = bankReconDetailService.sp_updrcbankdetailstatus(
                        bankDetailStatusRequest
                    );                
                    //authService.getLoginUserName(),
                    //authService.getLoginUserName(),
                    //SystemStatus.Active.getMessage());
 
            if (result == null)
            {
                return APIResponse.NoDataFound();
            }
            else{
                return APIResponse.SuccessResponse(result);
            }  
        // } catch (NumberFormatException e) {
        //     log.error("Exception in " + this.getClass().toString(), e);
        //     return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        //     log.error("Exception in " + this.getClass().toString(), e);
        //     return APIResponse.InternalServerError();
        // }
    }

    @PostMapping(value = "/getrcpgdoc")
    public ResponseEntity<ApiResponse<String>> sp_getrcpgdoc(HttpServletRequest request,
            @RequestBody BankReconRequest bankReconDetailRequest) throws SQLException {
 
        String result = "";
 
        // try {
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
 
            result = bankReconDetailService.sp_getrcpgdoc(
                bankReconDetailRequest
                );
 
            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.BANK_RECON_DETAIL_CONTROLLER);
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

    @PostMapping(value = "/getrcbkdoc")
    public ResponseEntity<ApiResponse<String>> sp_getrcbkdoc(HttpServletRequest request,
            @RequestBody BankReconDetail bankReconDetailRequest) throws SQLException {
 
        String result = "";
 
        // try {
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
 
            result = bankReconDetailService.sp_getrcbkdoc(
                bankReconDetailRequest);
 
            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.BANK_RECON_DETAIL_CONTROLLER);
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
