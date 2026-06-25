package com.maven.rms.controllers;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.rowset.serial.SerialException;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.BillingIssuanceBySBillingDocRequest;
import com.maven.rms.models.BillingIssuanceBySSBilCustomerRequest;
import com.maven.rms.models.BillingIssuanceBySSBilStatusRequest;
import com.maven.rms.models.BillingIssuanceBySSBillingDetails;
import com.maven.rms.models.BillingIssuanceBySSBillingDetailsRequest;
import com.maven.rms.models.BillingIssuanceBySSHistory;
import com.maven.rms.models.BillingIssuanceBySSListOfIssuance;
import com.maven.rms.models.BillingIssuanceBySSListing;
import com.maven.rms.models.BillingIssuanceBySSListingRequest;
import com.maven.rms.models.BillingIssuanceBySSListofBilItems;
import com.maven.rms.models.BillingIssuanceBySSPaymentDetails;
import com.maven.rms.models.BillingIssuanceBySSRunnoRequest;
import com.maven.rms.models.BillingTypeCode;
import com.maven.rms.models.BillingTypeCodeRequest;
import com.maven.rms.models.SubmitBillingCust;
import com.maven.rms.models.SubmitBillingRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.BillingIssuanceBySSService;
import com.maven.rms.services.EmailService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/bibss/v1")
@Slf4j
public class BillingIssuanceSSController {
    
    @Autowired
    private AuthService authService;

    @Autowired
    private BillingIssuanceBySSService bilIsseBySSService;

    // @Value("${rms.application.onlinePortalURL}")
    // private String url;

    @Value("${rms.application.backPortalURL}")
    private String backPortalURL;

    private EmailService emailService;

     public BillingIssuanceSSController(EmailService emailService) {
        this.emailService = emailService;} 

    @PostMapping(value = "/getbillingissuancebyssbillingtypecode") 
    public ResponseEntity<ApiResponse<List<BillingTypeCode>>> sp_getbibssbiltypecode(HttpServletRequest request,
            @RequestBody BillingTypeCodeRequest billingTypeCodeRequest) {

         
                    List<BillingTypeCode> result = Collections.emptyList();

                    if (!authService.isAuthenticated(request)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            
                    }
            
                    result = bilIsseBySSService.sp_getbibssbiltypecode(billingTypeCodeRequest);
            
                    if (result == null) {
                        return APIResponse.NoDataFound(ControllersEnum.BILLING_ISSUANCE_BY_SS_CONTROLLER);
                    }
            
                    return APIResponse.SuccessResponse(result);
    }


    @PostMapping(value = "/insertbillingissuancebyssbillingcustomer")
    public ResponseEntity<ApiResponse<Integer>> sp_insbilissbyssbilcust(HttpServletRequest request, @RequestBody BillingIssuanceBySSBilCustomerRequest bilCustRequest) {
        
        Integer result = 0;
        String username = authService.getLoginUserName();
        // String subject = "";
        // String body = "";
        // String url = "http://localhost:4200";
        // String redirect = url + "/bibss-payment-page?" + "billing_no=" + bilCustRequest.getI_billing_no();

      
              if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            bilCustRequest.setI_created_by(authService.getLoginUserName());
            bilCustRequest.setI_modified_by(authService.getLoginUserName());
            String custIP = authService.getClientIP(request);
         
           
            result=bilIsseBySSService.sp_insbilissbyssbilcust(bilCustRequest,username, custIP);

    
            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

             return APIResponse.SuccessResponse(result);


    }


    @PostMapping(value = "/getbillingissuancebyssrunno") 
    public ResponseEntity<ApiResponse<String>> sp_getbibssrunno(HttpServletRequest request, @RequestBody String runno) {

                    String result = "";

                    if (!authService.isAuthenticated(request)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            
                    }
            
                    result = bilIsseBySSService.sp_getbibssrunno();
            
                    if (result == null) {
                        return APIResponse.NoDataFound(ControllersEnum.BILLING_ISSUANCE_BY_SS_CONTROLLER);
                    }
            
                    return APIResponse.SuccessResponse(result);
    }


    @PostMapping(value = "/getandreservebillrunno") 
    public ResponseEntity<ApiResponse<String>> sp_getandreservebillrunno(HttpServletRequest request, @RequestBody BillingIssuanceBySSRunnoRequest runnoRequest) {

          
                    String result = "";

                    if (!authService.isAuthenticated(request)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            
                    }
            
                    result = bilIsseBySSService.sp_getandreservebillrunno(runnoRequest);
            
                    if (result == null) {
                        return APIResponse.NoDataFound(ControllersEnum.BILLING_ISSUANCE_BY_SS_CONTROLLER);
                    }
            
                    return APIResponse.SuccessResponse(result);

    }


    @PostMapping(value = "/getBillingStatus") 
    public ResponseEntity<ApiResponse<String>> sp_getbilstatus(HttpServletRequest request, @RequestBody BillingIssuanceBySSBilStatusRequest bilStatusRequest) {

             
                    String result = "";

                    if (!authService.isAuthenticated(request)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            
                    }
            
                    result = bilIsseBySSService.sp_getbilstatus(bilStatusRequest);
            
                    if (result == null) {
                        return APIResponse.NoDataFound(ControllersEnum.BILLING_ISSUANCE_BY_SS_CONTROLLER);
                    }
            
                    return APIResponse.SuccessResponse(result);
    }


    @PostMapping(value = "/getbillingissuancebysspaymentdetails") 
    public ResponseEntity<ApiResponse<List<BillingIssuanceBySSPaymentDetails>>> sp_getbibsspaymentdetails(HttpServletRequest request, @RequestBody BillingIssuanceBySSBilStatusRequest bilStatusRequest) {

        
                    List<BillingIssuanceBySSPaymentDetails> result = Collections.emptyList();

                    if (!authService.isAuthenticated(request)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            
                    }
            
                    result = bilIsseBySSService.sp_getbibsspaymentdetails(bilStatusRequest);
            
                    if (result == null) {
                        return APIResponse.NoDataFound(ControllersEnum.BILLING_ISSUANCE_BY_SS_CONTROLLER);
                    }
            
                    return APIResponse.SuccessResponse(result);

    }


      @PostMapping(value = "/addbillingissuancebyssdocument")
    public ResponseEntity<ApiResponse<Integer>> sp_insbibssbildoc(HttpServletRequest request,
            @RequestBody BillingIssuanceBySBillingDocRequest bilDocRequest) throws SerialException, SQLException {

        
                if (!authService.isAuthenticated(request)) {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }
    

                Integer result = 0;
                bilDocRequest.setI_created_by(authService.getLoginUserName());
                bilDocRequest.setI_modified_by(authService.getLoginUserName());
    
                // result = mftwfService.sp_uploadDoc(mftwfDocRequest, authService.getLoginUserName());
                result = bilIsseBySSService.sp_uploadDoc(bilDocRequest);
    

                if (result <= 0) {
                    
                    return APIResponse.InternalServerError();
                }
    
                return APIResponse.SuccessResponse(result);

        }

        @PostMapping(value = "/getbillingissuancebysslisting") 
    public ResponseEntity<ApiResponse<List<BillingIssuanceBySSListing>>> sp_getbibsslisting(HttpServletRequest request,
            @RequestBody BillingIssuanceBySSListingRequest billingListingRequest) {

   
                    List<BillingIssuanceBySSListing> result = Collections.emptyList();

                    if (!authService.isAuthenticated(request)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            
                    }
            
                    result = bilIsseBySSService.sp_getbibsslisting(billingListingRequest);
            
                    if (result == null) {
                        return APIResponse.NoDataFound(ControllersEnum.BILLING_ISSUANCE_BY_SS_CONTROLLER);
                    }
            
                    return APIResponse.SuccessResponse(result);
    }


    @PostMapping(value = "/getbillingissuancebyssbillingdetails") 
    public ResponseEntity<ApiResponse<List<BillingIssuanceBySSBillingDetails>>> sp_getbibssbillingdetails(HttpServletRequest request, @RequestBody BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest) {

          
                    List<BillingIssuanceBySSBillingDetails> result = Collections.emptyList();

                    if (!authService.isAuthenticated(request)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            
                    }
            
                    result = bilIsseBySSService.sp_getbibssbillingdetails(bilDetailsRequest);
            
                    if (result == null) {
                        return APIResponse.NoDataFound(ControllersEnum.BILLING_ISSUANCE_BY_SS_CONTROLLER);
                    }
            
                    return APIResponse.SuccessResponse(result);
    }


    @PostMapping(value = "/getbillingissuancebysslistofbillingitems") 
    public ResponseEntity<ApiResponse<List<BillingIssuanceBySSListofBilItems>>> sp_getbibsslistofbillingitems(HttpServletRequest request, @RequestBody BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest) {

       
                    List<BillingIssuanceBySSListofBilItems> result = Collections.emptyList();

                    if (!authService.isAuthenticated(request)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            
                    }
            
                    result = bilIsseBySSService.sp_getbibsslistofbillingitems(bilDetailsRequest);
            
                    if (result == null) {
                        return APIResponse.NoDataFound(ControllersEnum.BILLING_ISSUANCE_BY_SS_CONTROLLER);
                    }
            
                    return APIResponse.SuccessResponse(result);
    }


    @PostMapping(value = "/getbillingissuancebysslistofbillingissuance") 
    public ResponseEntity<ApiResponse<List<BillingIssuanceBySSListOfIssuance>>> sp_getbibsslistofbillingissuance(HttpServletRequest request, @RequestBody BillingIssuanceBySSBillingDetailsRequest bilRequest) {

      
                    List<BillingIssuanceBySSListOfIssuance> result = Collections.emptyList();

                    if (!authService.isAuthenticated(request)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            
                    }
            
                    result = bilIsseBySSService.sp_getbibsslistofbillingissuance(bilRequest);
            
                    if (result == null) {
                        return APIResponse.NoDataFound(ControllersEnum.BILLING_ISSUANCE_BY_SS_CONTROLLER);
                    }
            
                    return APIResponse.SuccessResponse(result);
    }


    @PostMapping(value = "/getbillingissuancebysshistory") 
    public ResponseEntity<ApiResponse<List<BillingIssuanceBySSHistory>>> sp_getbibsshistory(HttpServletRequest request, @RequestBody BillingIssuanceBySSBillingDetailsRequest bilRequest) {


                    List<BillingIssuanceBySSHistory> result = Collections.emptyList();

                    if (!authService.isAuthenticated(request)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            
                    }
            
                    result = bilIsseBySSService.sp_getbibsshistory(bilRequest);
            
                    if (result == null) {
                        return APIResponse.NoDataFound(ControllersEnum.BILLING_ISSUANCE_BY_SS_CONTROLLER);
                    }
            
                    return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/callbacksubmitbilling") 
    public ResponseEntity<ApiResponse<List<SubmitBillingCust>>> sp_callbacksubmitbilling(HttpServletRequest request, @RequestBody SubmitBillingRequest submitBillingRequest) {

    
                    List<SubmitBillingCust> result = Collections.emptyList();

                    if (!authService.isAuthenticated(request)) {
                        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            
                    }
            
                    result = bilIsseBySSService.sp_callbacksubmitbilling(submitBillingRequest);
            
                    if (result == null) {
                        return APIResponse.NoDataFound(ControllersEnum.BILLING_ISSUANCE_BY_SS_CONTROLLER);
                    }
            
                    return APIResponse.SuccessResponse(result);

    }

}



    // @PostMapping(value = "/getbillingissuancebyssbillingitemdetails") 
    // public ResponseEntity<ApiResponse<List<BillingIssuanceBySSBilItemDets>>> sp_getbibssbillingitemdetails(HttpServletRequest request,
    //         @RequestBody BillingIssuanceBySSBilItemDetsRequest billingItemDetsRequest) {

    //             try {
    //                 List<BillingIssuanceBySSBilItemDets> result = Collections.emptyList();

    //                 if (!authService.isAuthenticated(request)) {
    //                     return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            
    //                 }
            
    //                 result = bilIsseBySSService.sp_getbibssbillingitemdetails(billingItemDetsRequest);
            
    //                 if (result == null) {
    //                     return APIResponse.NoDataFound(ControllersEnum.BILLING_ISSUANCE_BY_SS_CONTROLLER);
    //                 }
            
    //                 return APIResponse.SuccessResponse(result);
    //             } catch (Exception e) {
    //                 System.out.println(e);
    //                        return APIResponse.NoDataFound(ControllersEnum.BILLING_ISSUANCE_BY_SS_CONTROLLER);
    //             }
    // }



