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
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import com.maven.rms.exceptionhandler.ApplicationException;
import com.maven.rms.models.CheckRoleRequest;
import com.maven.rms.models.Permission;
import com.maven.rms.models.PermissionByID;
import com.maven.rms.models.RolePermissionGet;
import com.maven.rms.models.RolePermissionGetRequest;
import com.maven.rms.models.RolePermissionRequest;
import com.maven.rms.models.RoleRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.RoleAndPermissionsConfigurationService;
import com.maven.rms.utils.APIResponse;
// import com.maven.rms.utils.SystemStatus;
import com.maven.rms.utils.ErrorCode;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/RPC/v1")
@Slf4j
public class RoleAndPermissionsConfigurationController {

    // private static final Logger logger =
    // LoggerFactory.getLogger(RoleAndPermissionsConfigurationController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private RoleAndPermissionsConfigurationService spService;

    @PostMapping(value = "/addroles")
    public ResponseEntity<ApiResponse<Integer>> addRoles(
            HttpServletRequest request,
            @RequestBody RoleRequest insertRequest) throws ApplicationException {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_insroles(insertRequest);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getpermissions")
    public ResponseEntity<ApiResponse<List<Permission>>> getPermission(
            HttpServletRequest request) {
        List<Permission> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getpermissions();

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.ROLE_AND_PERMISSIONS_CONFIGURATION_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updrolestatus")
    public ResponseEntity<ApiResponse<Integer>> updateRoleStatus(
            HttpServletRequest request,
            @RequestBody RoleRequest updateRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_updrolestatus(updateRequest);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    // @PostMapping(value = "/addroleperm")
    // public ResponseEntity<ApiResponse<Integer>> addRolePerm(
    // HttpServletRequest request,
    // @RequestBody RolePermissionRequest insertRequest) throws ApplicationException
    // {

    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // Integer result = spService.sp_insroleperm(insertRequest);

    // if (result <= 0) {
    // return APIResponse.InternalServerError();
    // }

    // return APIResponse.SuccessResponse(result);
    // }

    @PostMapping(value = "/addroleperm")
    public ResponseEntity<ApiResponse<Integer>> addRolePerm(
            HttpServletRequest request,
            @RequestBody List<RolePermissionRequest> insertRequests) throws ApplicationException {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Integer result = spService.sp_insroleperm(insertRequests);

        Integer result = spService.sp_insroleperm_v2(insertRequests);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getpermissionsid")
    public ResponseEntity<ApiResponse<List<PermissionByID>>> getPermissionId(
            HttpServletRequest request) {
        List<PermissionByID> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getpermissionsbyid();

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.ROLE_AND_PERMISSIONS_CONFIGURATION_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/delroleperm")
    public ResponseEntity<ApiResponse<Integer>> delRolePerm(
            HttpServletRequest request,
            @RequestBody RolePermissionRequest deleteRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_delroleperm(deleteRequest);

        if (result == 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getroleperm")
    public ResponseEntity<ApiResponse<List<RolePermissionGet>>> sp_getroleperm(HttpServletRequest request,
            @RequestBody RolePermissionGetRequest getRequest) {

        List<RolePermissionGet> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getroleperm(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.ROLE_AND_PERMISSIONS_CONFIGURATION_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/checkuserrole")
    public ResponseEntity<ApiResponse<String>> checkUserRole(
            HttpServletRequest request,
            @RequestBody CheckRoleRequest checkRoleRequest) {

        String result = "";

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_checkuserrole(checkRoleRequest);

        if (result == null || result.isEmpty() || result.equals(null)) {
            return APIResponse.NoDataFound(ControllersEnum.ROLE_AND_PERMISSIONS_CONFIGURATION_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

}
