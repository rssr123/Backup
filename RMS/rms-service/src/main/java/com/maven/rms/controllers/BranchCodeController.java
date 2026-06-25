package com.maven.rms.controllers;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.maven.rms.models.BranchCode;
import com.maven.rms.models.BranchCodeRequest;
import com.maven.rms.models.BranchCodeUpdateRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.BranchCodeService;
import com.maven.rms.utils.APIResponse;
import org.springframework.http.HttpStatus;
import com.maven.rms.models.BranchCodeAddRequest;
import com.maven.rms.models.BranchCodeDeleteRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.exceptionhandler.ApplicationException;

@RestController
@RequestMapping("api/bc/v1")
public class BranchCodeController {

    @Autowired
    private AuthService authService;

    @Autowired
    private BranchCodeService branchCodeService;

    @PostMapping(value = "/getbranchcodes")
    public ResponseEntity<?> getBranchCodes(
            HttpServletRequest request,
            @RequestBody BranchCodeRequest branchCodeRequest) {
        // if (!authService.isAuthenticated(request)) {
        // return ResponseEntity.status(401).build();
        // }

        List<BranchCode> result = branchCodeService.getBranchCodes(branchCodeRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/addbranchcodes")
    public ResponseEntity<ApiResponse<Integer>> addBranchCode(
            HttpServletRequest request,
            @RequestBody BranchCodeAddRequest insertRequests) throws ApplicationException {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = branchCodeService.sp_insbcm(insertRequests);

        if (result <= 0) {
            return APIResponse.DuplicateData();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updateBranchCode")
    public ResponseEntity<?> updateBranchCode(
            HttpServletRequest request,
            @RequestBody BranchCodeUpdateRequest updateRequest) {
        try {
            Integer result = branchCodeService.sp_updatebcm(updateRequest);

            if (result <= 0) {
                return APIResponse.DuplicateData();
            }
            return APIResponse.SuccessResponse(result);
        } catch (Exception e) {
            return APIResponse.InternalServerError();
        }
    }

    @PostMapping(value = "/delbranchcode")
    public ResponseEntity<ApiResponse<Integer>> delBranchCode(
            HttpServletRequest request,
            @RequestBody BranchCodeDeleteRequest deleteRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = branchCodeService.sp_delbranchcode(deleteRequest);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);

    }
}
