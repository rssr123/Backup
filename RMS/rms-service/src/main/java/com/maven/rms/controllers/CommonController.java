package com.maven.rms.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.config.RMSProperties;
import com.maven.rms.exceptionhandler.ApplicationException;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.models.Param;
import com.maven.rms.models.ParamRequest;
import com.maven.rms.models.PostCode;
import com.maven.rms.models.Role;
import com.maven.rms.models.RoleRequest;
import com.maven.rms.models.SourceSystemCode;
import com.maven.rms.models.SourceSystemCodeRequest;
import com.maven.rms.models.WhiteIPReq;
import com.maven.rms.models.WhiteList;
import com.maven.rms.models.OTC.OTCBank;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.CommonService;
import com.maven.rms.services.RoleAndPermissionsConfigurationService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/rms/v1")
@Slf4j
public class CommonController {

    // private static final Logger logger =
    // LoggerFactory.getLogger(CommonController.class);
    private final RMSProperties rmsProperties;

    @Autowired
    private RoleAndPermissionsConfigurationService roleAndPermissionsConfigurationService;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private AuthService authService;
    @Autowired
    private CommonService commonService;

    public CommonController(RMSProperties rmsProperties, CommonService commonService) {
        this.rmsProperties = rmsProperties;
        this.commonService = commonService;
        log.info("CommonController services is started");
    }

    @GetMapping("/check-origin")
    public ResponseEntity<Map<String, Object>> checkOrigin(@RequestParam String origin) {
        log.info("Received origin check request for: {}", origin);
        log.warn("Received origin check request for: {}", origin);
        log.error("Received origin check request for: {}", origin);
        return commonService.checkOrigin(origin);
    }

    // @Secured("ROLE_USER")
    @PostMapping(value = "/getparam")
    public ResponseEntity<ApiResponse<List<Param>>> sp_getparam(HttpServletRequest request,
            @RequestBody ParamRequest paramRequest) {

        List<Param> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        result = commonService.sp_getparam(
                paramRequest.getI_page(),
                paramRequest.getI_size(),
                paramRequest.getI_param_cd(),
                paramRequest.getI_param_grp_nm());

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.COMMON_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    // @Secured("ROLE_USER")
    @PostMapping(value = "/getsourcesystem")
    public ResponseEntity<ApiResponse<List<SourceSystemCode>>> sp_getsourcesystem(HttpServletRequest request,
            @RequestBody SourceSystemCodeRequest sourceSystemCodeRequest) {

        List<SourceSystemCode> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = commonService.sp_getsourcesystem(
                sourceSystemCodeRequest);

        // result =commonService.sp_getsourcesystem(
        // sourceSystemCodeRequest.getI_page(),
        // sourceSystemCodeRequest.getI_size(),
        // sourceSystemCodeRequest.getI_ss_id(),
        // sourceSystemCodeRequest.getI_ss_cd(),
        // sourceSystemCodeRequest.getI_ss_nm(),
        // sourceSystemCodeRequest.getI_modified_by(),
        // sourceSystemCodeRequest.getI_dt_modified_fr(),
        // sourceSystemCodeRequest.getI_dt_modified_to(),
        // sourceSystemCodeRequest.getI_status()
        // );

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.COMMON_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getroles")
    public ResponseEntity<ApiResponse<List<Role>>> sp_getroles(HttpServletRequest request,
            @RequestBody RoleRequest roleRequest) {

        List<Role> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // result = roleAndPermissionsConfigurationService.sp_getrole(
        // roleRequest.getI_page(),
        // roleRequest.getI_size(),
        // roleRequest.getI_r_id(),
        // roleRequest.getI_r_role_nm_en(),
        // roleRequest.getI_r_role_nm_bm(),
        // roleRequest.getI_modified_by(),
        // roleRequest.getI_dt_modified_fr(),
        // roleRequest.getI_dt_modified_to(),
        // roleRequest.getI_status()
        // );

        result = roleAndPermissionsConfigurationService.sp_getroles(roleRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.COMMON_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/getbanks")
    public ResponseEntity<ApiResponse<List<OTCBank>>> sp_getallbanks(HttpServletRequest request) {

        List<OTCBank> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = commonService.sp_getallbanks();

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.COMMON_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/getrctype")
    public ResponseEntity<ApiResponse<List<OTCBank>>> sp_getallrctype(HttpServletRequest request) {

        List<OTCBank> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = commonService.sp_getallrctype();

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.COMMON_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/getbillingstatus")
    public ResponseEntity<ApiResponse<List<OTCBank>>> sp_getallbillingstatus(HttpServletRequest request) {

        List<OTCBank> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = commonService.sp_getallbillingstatus();

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.COMMON_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getbillingmethod")
    public ResponseEntity<ApiResponse<List<OTCBank>>> sp_getallbillingmethod(HttpServletRequest request) {

        List<OTCBank> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = commonService.sp_getallbillingmethod();

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.COMMON_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    // @Secured("ROLE_USER")
    @PostMapping(value = "/getpostcode")
    public ResponseEntity<ApiResponse<List<PostCode>>> sp_getpostcode(HttpServletRequest request) {

        List<PostCode> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        result = commonService.sp_getpostcode();

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.COMMON_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/getwhitelistip")
    public ResponseEntity<ApiResponse<List<WhiteList>>> sp_getwhitelistip(HttpServletRequest request) {

        List<WhiteList> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        result = commonService.sp_getwhitelistip();

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.COMMON_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

    }

    // @PostMapping(value = "/inswhiteip")
    // public ResponseEntity<ApiResponse<String>> sp_inswhiteip(
    //         HttpServletRequest request,
    //         @RequestBody WhiteIPReq insertRequest) throws ApplicationException {

    //     if (!authService.isAuthenticated(request)) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    //     }

    //     Integer result = commonService.sp_inswhiteip(insertRequest);

    //     if (result < 0) {
    //         return APIResponse.InternalServerError();
    //     }

    //     if (result == 0){
    //         return APIResponse.DuplicateDataExternal("IP addresses " + insertRequest.getI_ip() + " already exists in the whitelist.");
    //     }

    //     return APIResponse.SuccessResponse("IP addresses " + insertRequest.getI_ip() + " added successfully.");
    // }

    @PostMapping(value = "/inswhiteip")
    public ResponseEntity<ApiResponse<String>> sp_inswhiteip(
            HttpServletRequest request,
            @RequestBody WhiteIPReq insertRequest) throws ApplicationException {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Integer> resultMap = commonService.sp_inswhiteip_v2(insertRequest);

        List<String> inserted = new ArrayList<>();
        List<String> duplicates = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (Map.Entry<String, Integer> e : resultMap.entrySet()) {
            String ip = e.getKey();
            Integer res = e.getValue();

            if (res == null) {
                failed.add(ip);
            } else if (res > 0) {
                inserted.add(ip); // at least one row inserted
            } else if (res == 0) {
                duplicates.add(ip); // SP says duplicate
            } else {
                failed.add(ip); // 0 or other codes -> treat as failure
            }
        }

        // No IP processed at all (shouldn't happen usually)
        if (inserted.isEmpty() && duplicates.isEmpty() && failed.isEmpty()) {
            return APIResponse.InternalServerError();
        }

        // All duplicates, no new insert
        if (inserted.isEmpty() && !duplicates.isEmpty() && failed.isEmpty()) {
            return APIResponse.DuplicateDataExternal(
                    "IP address(es) already exist in the whitelist: " + String.join(", ", duplicates));
        }

        // Build combined success message
        StringBuilder msg = new StringBuilder();

        if (!inserted.isEmpty()) {
            msg.append("IP address(es) added successfully: ")
                    .append(String.join(", ", inserted));
        }

        if (!duplicates.isEmpty()) {
            if (msg.length() > 0)
                msg.append(". ");
            msg.append("Already existed: ")
                    .append(String.join(", ", duplicates));
        }

        if (!failed.isEmpty()) {
            if (msg.length() > 0)
                msg.append(". ");
            msg.append("Failed to add: ")
                    .append(String.join(", ", failed));
        }

        return APIResponse.SuccessResponse(msg.toString());
    }


    // @PostMapping(value = "/delwhiteip")
    // public ResponseEntity<ApiResponse<String>> sp_updwhiteip(
    //         HttpServletRequest request,
    //         @RequestBody WhiteIPReq insertRequest) throws ApplicationException {

    //     if (!authService.isAuthenticated(request)) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    //     }

    //     Integer result = commonService.sp_updwhiteip(insertRequest);

    //     if (result == 0) {
    //         return APIResponse.NoDataFound();
    //     }

    //     return APIResponse.SuccessResponse("IP addresses " + insertRequest.getI_ip() + " removed successfully.");
    // }



    @PostMapping(value = "/delwhiteip")
    public ResponseEntity<ApiResponse<String>> sp_updwhiteip_v2(
            HttpServletRequest request,
            @RequestBody WhiteIPReq insertRequest) throws ApplicationException {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Map<String, Boolean> resultMap = commonService.sp_updwhiteip_v2(insertRequest);

        List<String> success = new ArrayList<>();
        List<String> failed = new ArrayList<>();

        for (Map.Entry<String, Boolean> e : resultMap.entrySet()) {
            if (e.getValue()) {
                success.add(e.getKey());
            } else {
                failed.add(e.getKey());
            }
        }

        if (success.isEmpty()) {
            return APIResponse.NoDataFound(); // or custom "No IP found to delete"
        }

        StringBuilder msg = new StringBuilder();
        msg.append("Removed IP(s): ").append(String.join(", ", success));
        if (!failed.isEmpty()) {
            msg.append(". Not found: ").append(String.join(", ", failed));
        }

        return APIResponse.SuccessResponse(msg.toString());
    }


}
