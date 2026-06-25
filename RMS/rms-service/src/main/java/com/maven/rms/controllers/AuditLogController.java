package com.maven.rms.controllers;

import javax.servlet.http.HttpServletRequest;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.AuditLogRequest;
// import com.maven.rms.models.RmsAuditLog;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuditLogService;
// import com.maven.rms.services.RmsAuditLogService;
import com.maven.rms.utils.APIResponse;

@RestController
@RequestMapping("/api/auditlog")
@Slf4j
public class AuditLogController {

    // private static final Logger logger = LoggerFactory.getLogger(AuditLogController.class);

    @Autowired
    private AuditLogService auditLogService;

    @PostMapping(value = "/insauditlog")
    public ResponseEntity<ApiResponse<Integer>> logAudit(HttpServletRequest request, @RequestBody AuditLogRequest auditLogRequest) {
        // auditLogService.saveAuditLog(auditLog);
        // return ResponseEntity.ok().build();
   
            Integer result = auditLogService.sp_insertAuditLog(
                auditLogRequest
            );

            // Integer result = auditLogService.sp_insertAuditLog(
            //     // auditLogRequest.getI_audit_log_id(),
            //     auditLogRequest.getI_actor(),
            //     auditLogRequest.getI_request_url(),
            //     auditLogRequest.getI_module(),
            //     auditLogRequest.getI_request_msg(),
            //     auditLogRequest.getI_action(),
            //     auditLogRequest.getI_page_url(),
            //     auditLogRequest.getI_remark(),
            //     auditLogRequest.getI_source(),
            //     auditLogRequest.getI_response_msg(),
            //     auditLogRequest.getI_modified_by(),
            //     auditLogRequest.getI_created_by(),
            //     auditLogRequest.getI_status()
            // );

            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
    }
}