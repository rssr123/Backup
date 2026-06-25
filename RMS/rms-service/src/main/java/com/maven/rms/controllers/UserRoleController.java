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

import com.maven.rms.models.UserRole;
import com.maven.rms.models.UserRoleRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.RoleAndPermissionsConfigurationService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/UR/v1")
public class UserRoleController {

    @Autowired
    private AuthService authService;

    @Autowired
    private RoleAndPermissionsConfigurationService spService;

    // @Secured("ROLE_USER")
    // @PostMapping(value = "/getuserrole")
    // public ResponseEntity<ApiResponse<List<UserRole>>> getUserRole(
    // HttpServletRequest request,
    // @RequestBody UserRoleRequest userRoleRequest) {
    // List<UserRole> result = Collections.emptyList();

    // try {

    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // // result = spService.sp_getuseranduserroles(
    // // userRoleRequest.getI_page(),
    // // userRoleRequest.getI_size(),
    // // userRoleRequest.getI_user(),
    // // userRoleRequest.getI_user_role()
    // // );

    // result = spService.sp_getuseranduserroles(userRoleRequest);

    // if (result.isEmpty()) {
    // return APIResponse.NoDataFound(ControllersEnum.USER_ROLE_CONTROLLER);
    // }
    // return APIResponse.SuccessResponse(result);
    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();
    // } catch (Exception e) {
    // return APIResponse.InternalServerError();
    // }

    // }

    @PostMapping(value = "/getuserrole")
    public ResponseEntity<ApiResponse<List<UserRole>>> getUserRole(
            HttpServletRequest request,
            @RequestBody UserRoleRequest userRoleRequest) {
        List<UserRole> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        result = spService.sp_getuseranduserroles(userRoleRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.USER_ROLE_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);

    }

}
