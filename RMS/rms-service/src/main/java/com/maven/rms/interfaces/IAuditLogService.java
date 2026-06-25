package com.maven.rms.interfaces;

import com.maven.rms.models.AuditLogRequest;

public interface IAuditLogService {

    // insert audit log
    public Integer sp_insertAuditLog(AuditLogRequest auditLogRequest);

}