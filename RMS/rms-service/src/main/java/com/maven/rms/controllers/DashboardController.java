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

import com.maven.rms.models.Dashboard;
import com.maven.rms.models.DashboardInit;
import com.maven.rms.models.DashboardRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.DashboardService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/dashboard/v1")
public class DashboardController {

    @Autowired
    private AuthService authService;

    @Autowired
    private DashboardService dashboardService;

    @PostMapping(value = "/getdashboardinit")
    public ResponseEntity<ApiResponse<List<DashboardInit>>> getDashboardInit(
            HttpServletRequest request) {
        
        List<DashboardInit> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = dashboardService.sp_getdashboardinit();

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.DASHBOARD_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getrcptyear")
    public ResponseEntity<ApiResponse<List<Dashboard>>> getRcptYear(
            HttpServletRequest request) {
        
        List<Dashboard> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = dashboardService.sp_getrcptbyyear();

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.DASHBOARD_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getrcptmonth")
    public ResponseEntity<ApiResponse<List<Dashboard>>> getRcptMonth(HttpServletRequest request, @RequestBody DashboardRequest dashboardReq) {
        
        List<Dashboard> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = dashboardService.sp_getrcptbymonth(dashboardReq);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.DASHBOARD_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }
    
    @PostMapping(value = "/getrcptday")
    public ResponseEntity<ApiResponse<List<Dashboard>>> getRcptDay(HttpServletRequest request, @RequestBody DashboardRequest dashboardReq) {
        
        List<Dashboard> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = dashboardService.sp_getrcptbyday(dashboardReq);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.DASHBOARD_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getrevenuebyss")
    public ResponseEntity<ApiResponse<List<Dashboard>>> getrevenuebyss(
            HttpServletRequest request) {
        
        List<Dashboard> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = dashboardService.sp_getrevenuebyss();

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.DASHBOARD_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getrevenuebyspaymentmethod")
    public ResponseEntity<ApiResponse<List<Dashboard>>> getrevenuebypaymentmethod(
            HttpServletRequest request) {
        
        List<Dashboard> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = dashboardService.sp_getrevenuebypaymentmethod();

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.DASHBOARD_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getrevenueyear")
    public ResponseEntity<ApiResponse<List<Dashboard>>> getRevenueYear(
            HttpServletRequest request) {
        
        List<Dashboard> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = dashboardService.sp_getrevenuebyyear();

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.DASHBOARD_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getrevenuemonth")
    public ResponseEntity<ApiResponse<List<Dashboard>>> getRevenueMonth(HttpServletRequest request, @RequestBody DashboardRequest dashboardReq) {
        
        List<Dashboard> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = dashboardService.sp_getrevenuebymonth(dashboardReq);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.DASHBOARD_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }
    
    @PostMapping(value = "/getrevenueday")
    public ResponseEntity<ApiResponse<List<Dashboard>>> getRevenueDay(HttpServletRequest request, @RequestBody DashboardRequest dashboardReq) {
        
        List<Dashboard> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = dashboardService.sp_getrevenuebyday(dashboardReq);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.DASHBOARD_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getrefundcount")
    public ResponseEntity<ApiResponse<List<Dashboard>>> getrefundcount(
            HttpServletRequest request) {
        
        List<Dashboard> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = dashboardService.sp_getrrefundstatuscnt();

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.DASHBOARD_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    
}