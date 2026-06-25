package com.maven.rms.controllers;

import java.util.Collections;
import java.util.List;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.models.FMSAccount;
import com.maven.rms.models.FMSAccountRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.FMSAccountService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/fmsaccount/v1")
public class FMSAccountController {

    @Autowired
    private AuthService authService;

    @Autowired
    private FMSAccountService fmsAccountService;

    // @PostMapping(value = "/getfmsaccount")
    // public ResponseEntity<ApiResponse<List<FMSAccount>>> getFMSAccount(
    //         HttpServletRequest request,
    //         @RequestBody FMSAccountRequest fmsAccountRequest) {
    //     List<FMSAccount> result = Collections.emptyList();

    //     try {

    //         if (!authService.isAuthenticated(request)) {
    //             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    //         }

    //         result = fmsAccountService.sp_getfmsaccount(
    //             fmsAccountRequest.getI_page(),
    //             fmsAccountRequest.getI_size(),
    //             fmsAccountRequest.getI_acct_nm(),
    //             fmsAccountRequest.getI_acct_type(),
    //             fmsAccountRequest.getI_acct_cd(),
    //             fmsAccountRequest.getI_modified_by(),
    //             fmsAccountRequest.getI_dt_modified());

    //         if (result.isEmpty()) {
    //             return APIResponse.NoDataFound();
    //         }

    //         return APIResponse.SuccessResponse(result);

    //     } catch (NumberFormatException e) {
    //         return APIResponse.InvalidFormat();
    //     } catch (Exception e) {
    //         return APIResponse.InternalServerError();
    //     }

    // }


    @PostMapping(value = "/getfmsaccount")
    public ResponseEntity<ApiResponse<List<FMSAccount>>> getFMSAccount(
            HttpServletRequest request,
            @RequestBody FMSAccountRequest fmsAccountRequest) {
        List<FMSAccount> result = Collections.emptyList();

        // try {

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            result = fmsAccountService.sp_getfmsaccount(
                fmsAccountRequest
                );

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.FMS_ACCOUNT_CONTROLLER);
            }

            return APIResponse.SuccessResponse(result);

        // } catch (NumberFormatException e) {
        //     return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        //     return APIResponse.InternalServerError();
        // }

    }


    @PostMapping(value = "/updatefmsaccount")
    public ResponseEntity<ApiResponse<Integer>> updateFMSAccount(
            HttpServletRequest request,
            @RequestBody FMSAccountRequest fmsAccountRequest) {

        // try {

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            Integer result = fmsAccountService.sp_updfmsaccount(
                fmsAccountRequest
                );

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
        // } catch (NumberFormatException e) {
        //     return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        //     return APIResponse.InternalServerError();
        // }
    }

}
