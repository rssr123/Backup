package com.maven.rms.controllers.OTC;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.maven.rms.exceptionhandler.ApplicationException;
import com.maven.rms.models.FMSARIImmediateRequest;
import com.maven.rms.models.MTTEmailExpiry;
import com.maven.rms.models.NonBillRCEmail;
import com.maven.rms.models.OTC.NBLDocInsRequest;
import com.maven.rms.models.OTC.NBLInsRequest;
import com.maven.rms.models.OTC.NBLItem;
import com.maven.rms.models.OTC.NBLItemInsRequest;
import com.maven.rms.models.OTC.NBLItemRequest;
import com.maven.rms.models.OTC.NBLTC;
import com.maven.rms.models.OTC.NonBilHist;
import com.maven.rms.models.OTC.NonBilResult;
import com.maven.rms.models.OTC.NonBillDoc;
import com.maven.rms.models.OTC.NonBillingItems;
import com.maven.rms.models.OTC.NonBillingListing;
import com.maven.rms.models.OTC.NonBillingListingRequest;
import com.maven.rms.models.OTC.OTCReturnedCheque;
import com.maven.rms.models.OTC.OTCReturnedChequeRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.FMSARIService;
import com.maven.rms.services.FMSARVService;
import com.maven.rms.services.OTC.OTCReturnedChequeService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/OTCRC/v1")
@Slf4j
public class OTCReturnedChequeController {
    @Autowired
    private AuthService authService;

    @Autowired
    private OTCReturnedChequeService spService;

    @Autowired
    private FMSARIService fmsARIService;

    @Autowired
    private FMSARVService fmsARVService;


    @PostMapping(value = "/getchequeinfo")
    public ResponseEntity<ApiResponse<List<OTCReturnedCheque>>> sp_getchequeinfo(HttpServletRequest request,
            @RequestBody OTCReturnedChequeRequest getRequest) {

        List<OTCReturnedCheque> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getchequeinfo(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RETURNED_CHEQUE_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getnbltc")
    public ResponseEntity<ApiResponse<List<NBLTC>>> sp_getnbltc
    (HttpServletRequest request) {

        List<NBLTC> result = Collections.emptyList();

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            result = spService.sp_getnbltc();
            
            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.OTC_RETURNED_CHEQUE_CONTROLLER);
            }

            return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/getnblitem")
    public ResponseEntity<ApiResponse<List<NBLItem>>> sp_getnblitem
    (HttpServletRequest request, @RequestBody NBLItemRequest nblItrmRequest) {

        List<NBLItem> result = Collections.emptyList();

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            result = spService.sp_getnblitem(nblItrmRequest);
            
            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.OTC_RETURNED_CHEQUE_CONTROLLER);
            }

            return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/getnbno")
    public ResponseEntity<ApiResponse<String>> sp_getnbrunno
    (HttpServletRequest request) {

        String result = "";

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            result = spService.sp_getnbrunno();
            
            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.OTC_RETURNED_CHEQUE_CONTROLLER);
            }

            return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/insnonbil")
    public ResponseEntity<ApiResponse<List<NonBilResult>>> sp_insnonbill(
            HttpServletRequest request,
            @RequestBody NBLInsRequest insertRequest) throws ApplicationException, IOException {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // List<Integer> result = spService.sp_insnonbill(insertRequest);
        List<NonBilResult> result = spService.sp_insnonbill(insertRequest);

        if (result.size() < 1) {
            return APIResponse.InternalServerError();
        }

        try {
            // Send immediate FMSARI Posting to FMS
            FMSARIImmediateRequest fmsARIImmediateRequest = new FMSARIImmediateRequest();
            fmsARIImmediateRequest.setI_non_bil_id(result.get(0).getNon_bil_id());

            fmsARIService.postFMSARIImmediate(fmsARIImmediateRequest);
            // Send FMSARI completed

            // Send Immediate FMSARV Posting to FMS
            fmsARVService.postFMSAVImmediate(fmsARIImmediateRequest);
            // Send FMSARV completed
            
        } catch (Exception e) {
            log.error("Error in sending FMS Posting: " + e.getMessage());
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/insnonbilitem")
    public ResponseEntity<ApiResponse<Integer>> sp_insnonbillitem(
            HttpServletRequest request,
            @RequestBody List<NBLItemInsRequest> insertRequest) throws ApplicationException, IOException{

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_insnonbillitem(insertRequest);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    // @PostMapping(value = "/insnonbildoc")
    // public ResponseEntity<ApiResponse<Integer>> sp_insnonbilldoc(
    //         HttpServletRequest request,
    //         @RequestBody List<NBLDocInsRequest> insertRequest) throws ApplicationException, IOException{

    //     if (!authService.isAuthenticated(request)) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    //     }

    //     Integer result = spService.sp_insnonbilldoc(insertRequest);

    //     if (result <= 0) {
    //         return APIResponse.InternalServerError();
    //     }

    //     return APIResponse.SuccessResponse(result);
    // }

    @PostMapping(value = "/insnonbildoc")
    public ResponseEntity<ApiResponse<Integer>> sp_insnonbilldoc(
            HttpServletRequest request,
            @RequestBody NBLDocInsRequest insertRequest) throws ApplicationException, IOException, SerialException, SQLException{

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_insnonbilldoc(insertRequest);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getnonbillinglisting")
    public ResponseEntity<ApiResponse<List<NonBillingListing>>> sp_getnblitem
    (HttpServletRequest request, @RequestBody NonBillingListingRequest req) {

        List<NonBillingListing> result = Collections.emptyList();

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            result = spService.sp_getnonbilllisting(req);
            
            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.OTC_RETURNED_CHEQUE_CONTROLLER);
            }

            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getnonbillingitems")
    public ResponseEntity<ApiResponse<List<NonBillingItems>>> sp_getnonbillitem
    (HttpServletRequest request, @RequestBody NonBillingListingRequest req) {

        List<NonBillingItems> result = Collections.emptyList();

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            result = spService.sp_getnonbillitem(req);
            
            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.OTC_RETURNED_CHEQUE_CONTROLLER);
            }

            return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getnonbilldocs")
    public ResponseEntity<ApiResponse<List<NonBillDoc>>> sp_getnonbildoc(HttpServletRequest request,
            @RequestBody NonBillingListingRequest getRequest) {

        List<NonBillDoc> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getnonbildoc(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RETURNED_CHEQUE_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getnonbilldocscontent")
    public ResponseEntity<ApiResponse<String>> sp_getnonbildoccontent(HttpServletRequest request,
            @RequestBody NonBillingListingRequest docReq) {

        String result = "";
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getnonbildoccontent(docReq);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RETURNED_CHEQUE_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getnonbilhist")
    public ResponseEntity<ApiResponse<List<NonBilHist>>> sp_getnonbilhist(HttpServletRequest request,
            @RequestBody NonBillingListingRequest getRequest) {

        List<NonBilHist> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getnonbilhist(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.OTC_RETURNED_CHEQUE_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    // scheduler
    @PostMapping(value = "/nonbillreturnche")
    public ResponseEntity<ApiResponse<List<NonBillRCEmail>>> getNonBillRCEmail(
            HttpServletRequest request,
            @RequestBody OTCReturnedChequeRequest getRequest) {
        List<NonBillRCEmail> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getnonbillreturnche(getRequest);

        if (result.isEmpty()) {
            return APIResponse.InternalServerError();
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/mttemaildtexpiry")
    public ResponseEntity<ApiResponse<List<MTTEmailExpiry>>> getMTTEmailExpiry(
            HttpServletRequest request,
            @RequestBody OTCReturnedChequeRequest getRequest) {
        List<MTTEmailExpiry> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getmttemaildtexpiry(getRequest);

        if (result.isEmpty()) {
            return APIResponse.InternalServerError();
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updnonbil")
    public ResponseEntity<ApiResponse<Integer>> sp_updnonbillinsa(
            HttpServletRequest request,
            @RequestBody NBLInsRequest insertRequest) throws ApplicationException, IOException{

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_updnonbillinsa(insertRequest);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }
    
}
