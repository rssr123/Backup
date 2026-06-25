package com.maven.rms.controllers;

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
import com.maven.rms.models.AgBankTxnModel;
import com.maven.rms.models.AgBankTxnReq;
import com.maven.rms.models.AgBankTxnStatistic;
import com.maven.rms.models.AgDoc;
import com.maven.rms.models.NonReceipting;
import com.maven.rms.models.NonReceiptingAgTxnRequest;
import com.maven.rms.models.NonReceiptingDocRequest;
import com.maven.rms.models.NonReceiptingRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.NonReceiptingService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/RMSNR/v1")
@Slf4j
public class RMSNonReceiptingController {
    @Autowired
    private AuthService authService;

    @Autowired
    private NonReceiptingService spService;

    @PostMapping(value = "/getrmsnonreceipting")
    public ResponseEntity<ApiResponse<List<NonReceipting>>> sp_getrmsnonreceipting(HttpServletRequest request,
            @RequestBody NonReceiptingRequest getRequest) {

        List<NonReceipting> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getrmsnonreceipting(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.NON_RMS_RECEIPTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    // @PostMapping(value = "/insagsaledoc")
    // public ResponseEntity<ApiResponse<List<Integer>>> sp_insagsaledoc(
    //         HttpServletRequest request,
    //         @RequestBody List<NonReceiptingDocRequest> insertRequests)
    //         throws ApplicationException, IOException, SerialException, SQLException {

    //     if (!authService.isAuthenticated(request)) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    //     }

    //     // Call service to insert all files and return their IDs
    //     List<Integer> insertedIds = spService.sp_insagsaledoc(insertRequests);

    //     if (insertedIds.isEmpty()) {
    //         return APIResponse.InternalServerError();
    //     }

    //     return APIResponse.SuccessResponse(insertedIds);
    // }

    @PostMapping(value = "/insagsaledoc")
    public ResponseEntity<ApiResponse<Integer>> sp_insagsaledoc(
            HttpServletRequest request,
            @RequestBody NonReceiptingDocRequest insertRequests)
            throws ApplicationException, IOException, SerialException, SQLException {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Call service to insert all files and return their IDs
        Integer result = spService.sp_insagsaledoc(insertRequests);

        if (result == 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/insagbanktxn")
    public ResponseEntity<ApiResponse<Integer>> sp_insagbanktxn(HttpServletRequest request,
            @RequestBody List<AgBankTxnReq> insertRequests)
            throws ApplicationException, IOException, SerialException, SQLException {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        int totalInserted = 0;
        for (AgBankTxnReq insertRequest : insertRequests) {
            Integer result = spService.sp_insagbanktxn(insertRequest);
            if (result > 0) {
                totalInserted++;
            }
        }

        if (totalInserted == 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(totalInserted);
    }

    @PostMapping(value = "/getagdoc")
    public ResponseEntity<ApiResponse<List<AgDoc>>> sp_getagdoc(HttpServletRequest request,
            @RequestBody AgBankTxnReq getRequest) {

        List<AgDoc> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getagdoc(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.NON_RMS_RECEIPTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getagdoccontent")
    public ResponseEntity<ApiResponse<String>> sp_getagfilecontent(HttpServletRequest request,
            @RequestBody AgBankTxnReq docReq) {

        String result = "";
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getagfilecontent(docReq);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.NON_RMS_RECEIPTING_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getagbanktxn")
    public ResponseEntity<ApiResponse<List<AgBankTxnModel>>> sp_getagbanktxn(HttpServletRequest request,
            @RequestBody NonReceiptingAgTxnRequest getRequest) {

        List<AgBankTxnModel> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getagbanktxn(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.NON_RMS_RECEIPTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getagbanktxnpg")
    public ResponseEntity<ApiResponse<List<AgBankTxnModel>>> sp_getagbanktxnpg(HttpServletRequest request,
            @RequestBody NonReceiptingAgTxnRequest getRequest) {

        List<AgBankTxnModel> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getagbanktxnpg(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.NON_RMS_RECEIPTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }


    @PostMapping(value = "/delagdoc")
    public ResponseEntity<ApiResponse<Integer>> sp_delagdoc(
            HttpServletRequest request,
            @RequestBody AgBankTxnReq updateRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_delagdoc(updateRequest);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getagdocstatistics")
    public ResponseEntity<ApiResponse<List<AgBankTxnStatistic>>> sp_getagdocstatistics(HttpServletRequest request, @RequestBody NonReceiptingAgTxnRequest getRequest) {

        List<AgBankTxnStatistic> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getagdocstatistics(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.NON_RMS_RECEIPTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updagsale")
    public ResponseEntity<ApiResponse<Integer>> sp_updagsale(
            HttpServletRequest request, @RequestBody NonReceiptingDocRequest insertRequests)
            throws ApplicationException, IOException, SerialException, SQLException {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Call service to insert all files and return their IDs
        Integer insertedIds = spService.sp_updagsale(insertRequests);

        if (insertedIds < 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(insertedIds);
    }

    @PostMapping(value = "/nonrmsreconsch")
    public ResponseEntity<ApiResponse<Integer>> nonrmsreconsch(
            HttpServletRequest request)
            throws ApplicationException, IOException, SerialException, SQLException {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Call service to insert all files and return their IDs
        Integer insertedIds = spService.fms_non_rms_recon();

        return APIResponse.SuccessResponse(insertedIds);
    }

}