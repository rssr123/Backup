package com.maven.rms.controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.CourtOrder;
import com.maven.rms.models.CourtOrderCaseInfo;
import com.maven.rms.models.CourtOrderDocs;
import com.maven.rms.models.CourtOrderHistory;
import com.maven.rms.models.CourtOrderPymtInfo;
import com.maven.rms.models.CourtOrderRequest;
import com.maven.rms.models.CourtOrderRmdrInfo;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.CourtOrderService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;
import com.maven.rms.utils.RMSLogger;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/co/v1")
@Slf4j
public class CourtOrderController {
    @Autowired
    private AuthService authService;

    @Autowired
    private CourtOrderService courtOrderService;

    @PostMapping(value = "/courtorder")
    public ResponseEntity<ApiResponse<List<CourtOrder>>> getcourtorderlisting(
            HttpServletRequest request,
            @RequestBody CourtOrderRequest courtOrderRequest) {
        List<CourtOrder> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = courtOrderService.sp_getcourtorderlisting(courtOrderRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/courtordercaseinfo")
    public ResponseEntity<ApiResponse<List<CourtOrderCaseInfo>>> getcreditcontrolcaseinfo(
            HttpServletRequest request,
            @RequestBody CourtOrderRequest courtOrderRequest) {
        List<CourtOrderCaseInfo> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = courtOrderService.sp_getcreditcontrolcaseinfo(courtOrderRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/courtorderpymtinfo")
    public ResponseEntity<ApiResponse<List<CourtOrderPymtInfo>>> getcaseorderpymtiteminfo(
            HttpServletRequest request,
            @RequestBody CourtOrderRequest courtOrderRequest) {
        List<CourtOrderPymtInfo> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = courtOrderService.sp_getcourtorderpymtiteminfo(courtOrderRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/courtorderrmdrinfo")
    public ResponseEntity<ApiResponse<List<CourtOrderRmdrInfo>>> getcourtorderrmdrinfo(
            HttpServletRequest request,
            @RequestBody CourtOrderRequest courtOrderRequest) {
        List<CourtOrderRmdrInfo> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = courtOrderService.sp_getcourtorderrmdrinfo(courtOrderRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/courtorderdocs")
    public ResponseEntity<ApiResponse<List<CourtOrderDocs>>> getcourtorderdocs(
            HttpServletRequest request,
            @RequestBody CourtOrderRequest courtOrderRequest) {
        List<CourtOrderDocs> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = courtOrderService.sp_getcourtorderdocs(courtOrderRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/courtorderhist")
    public ResponseEntity<ApiResponse<List<CourtOrderHistory>>> getcourtorderhist(
            HttpServletRequest request,
            @RequestBody CourtOrderRequest courtOrderRequest) {
        List<CourtOrderHistory> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = courtOrderService.sp_getcourtorderhist(courtOrderRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    /// blob file
    @PostMapping(value = "/getcccasedocblob")
    public ResponseEntity<ApiResponse<String>> getcccasedocblob(HttpServletRequest request,
            @Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
        if (!authService.isAuthenticated(request))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!payload.containsKey("i_cc_case_id") || CollectionUtils.size(payload) != 1)//this got error
            return APIResponse.InvalidFormatExternal();

        String result = courtOrderService.sp_getcccasedocblob((Integer) payload.get("i_cc_case_id"));

        if (result.isEmpty()) {
            RMSLogger.error("Exception in " + this.getClass().toString() + "getcccasedocblob func - blob is empty!");
            return APIResponse.NoDataFound();
        }
        return APIResponse.SuccessResponse(result);
    }
}
