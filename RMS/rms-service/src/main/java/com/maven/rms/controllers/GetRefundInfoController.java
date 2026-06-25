package com.maven.rms.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.GetRefundInfo;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.CommonService;
import com.maven.rms.services.GetRefundInfoService;
import com.maven.rms.utils.APIResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/refund/v1/")
@Slf4j
public class GetRefundInfoController {

    @Autowired
    private GetRefundInfoService getRefundInfoService;

    @Autowired
    private CommonService commonSvc;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/getRefundInfo")
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> getRefundInfo(
            @Valid @RequestBody Map<String, String> request) throws JsonProcessingException {

        // Setup audit logging
        ExtAudit extAudit = new ExtAudit();
        extAudit.setI_module_nm("getRefundInfo");
        extAudit.setI_direction("Incoming");
        extAudit.setI_rms_batch_no(null);
        String jsonBody = objectMapper.writeValueAsString(request);
        extAudit.setI_request_body(jsonBody);

        try {
            String ornNo = request.get("orn_no");
            String appNo = request.get("app_no");

            // Validate input parameters
            if ((ornNo == null || ornNo.trim().isEmpty()) && (appNo == null || appNo.trim().isEmpty())) {
                externalAudit(extAudit, "Invalid input parameters: both orn_no and app_no are missing");
                return APIResponse.InvalidFormatExternal();
            }

            // Get refund information from service
            List<GetRefundInfo> refundInfoList = getRefundInfoService.getRefundInfo(ornNo, appNo);

            // Check if data exists and is valid
            if (refundInfoList == null || refundInfoList.isEmpty() || 
                (refundInfoList.size() > 0 && (refundInfoList.get(0).getAppNo() == null || refundInfoList.get(0).getAppNo().trim().isEmpty()))) {
                
                externalAudit(extAudit, "No data found for the given parameters");
                // Use a custom response with proper status code instead of the problematic NoDataFoundExternal
                return APIResponse.CustomErrorResponse(
                    "No Data Found", 
                    "401", 
                    org.springframework.http.HttpStatus.NOT_FOUND
                );
            }

            // Transform data for response
            List<Map<String, String>> responseData = new ArrayList<>();
            for (GetRefundInfo refundInfo : refundInfoList) {
                Map<String, String> dataItem = new LinkedHashMap<>();
                dataItem.put("app_no", refundInfo.getAppNo());
                dataItem.put("app_status", refundInfo.getAppStatus());
                dataItem.put("app_msg", refundInfo.getAppMsg());
                dataItem.put("app_rejected_reason", refundInfo.getAppRejectedReason());
                dataItem.put("slip_no", refundInfo.getSlipNo());
                dataItem.put("file", refundInfo.getFileBase64());
                responseData.add(dataItem);
            }

            externalAudit(extAudit, "Success");
            return APIResponse.SuccessResponseExternal(responseData);

        } catch (Exception e) {
            log.error("Error in getRefundInfo: " + e.getMessage() + ", " + 
                     (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            
            externalAudit(extAudit, "Internal server error: " + e.getMessage());
            return APIResponse.InternalServerErrorExternal();
        }
    }

    private void externalAudit(ExtAudit paramAudit, String msg) {
        try {
            ExtAudit extAudit = paramAudit;
            extAudit.setI_response_body(msg);
            commonSvc.sp_insextaudit(extAudit);
        } catch (Exception e) {
            log.error("Error in audit logging: " + e.getMessage() + ", " + 
                     (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
        }
    }
}