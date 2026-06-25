package com.maven.rms.controllers;

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

import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.models.FeeGrp;
import com.maven.rms.models.FeeGrpRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.FeeGroupService;
import com.maven.rms.services.StoreProcedureService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;
import com.maven.rms.utils.SystemStatus;

@RestController
@RequestMapping("/api/fg/v1")
public class FeeGroupController {

    @Autowired
    private AuthService authService;

    @Autowired
    private FeeGroupService feeGroupService;

    // @Secured("ROLE_USER")
    @PostMapping(value = "/getfeegroup")
    public ResponseEntity<ApiResponse<List<FeeGrp>>> getFeeGroup(
            HttpServletRequest request,
            @RequestBody FeeGrpRequest feeGroupRequest) {
        List<FeeGrp> result = Collections.emptyList();

        

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            result = feeGroupService.sp_getfeegroup_v2(
                    feeGroupRequest);

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.FEE_GROUP_CONTROLLER);
            }

            return APIResponse.SuccessResponse(result);

       

    }

    @PostMapping(value = "/addfeegroup")
    public ResponseEntity<ApiResponse<Integer>> addFeeGroup(
            HttpServletRequest request,
            @RequestBody FeeGrpRequest insertRequest) {

        
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Integer result = feeGroupService.sp_insfeegroup(
                    insertRequest,
                    authService.getLoginUserName(),
                    authService.getLoginUserName(),
                    SystemStatus.Active.getMessage());

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
        
    }

    @PostMapping(value = "/updatefeegroup")
    public ResponseEntity<ApiResponse<Integer>> updateFeeGroup(
            HttpServletRequest request,
            @RequestBody FeeGrpRequest updateRequest) {

        
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Integer result = feeGroupService.sp_updfeegroup(
                    updateRequest,
                    updateRequest.getI_fee_grp_nm_en(),
                    updateRequest.getI_fee_grp_nm_bm(),
                    authService.getLoginUserName(),
                    updateRequest.getI_status());

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
        
    }

    @PostMapping(value = "/deletefeegroup")
    public ResponseEntity<ApiResponse<Integer>> deleteFeeGroup(
            HttpServletRequest request,
            @RequestBody FeeGrpRequest deleteRequest) {

        
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Integer result = feeGroupService.sp_updfeegroup(
                    deleteRequest,
                    null,
                    null,
                    authService.getLoginUserName(),
                    SystemStatus.Deleted.getMessage());

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
       
    }

    @PostMapping(value = "/checkfeegroupexist")
    public ResponseEntity<ApiResponse<Integer>> checkFeeGroupExist(
            HttpServletRequest request,
            @RequestBody FeeGrpRequest feeGroupRequest) {

        // Long feeGroupID = feeGroupRequest.getI_fee_grp_id();

        
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Integer result = feeGroupService.sp_checkfeegrpbyid(
                    feeGroupRequest);

            if (result > 0) { // got module using it
                return APIResponse.RecordInUsed(result);
            }

            return APIResponse.SuccessResponse(result);
        
    }

}
