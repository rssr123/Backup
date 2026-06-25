package com.maven.rms.interfaces;

import com.maven.rms.models.AuditLogRequest;

public interface IAuditLogInterface {

    // insert audit log
    public Integer sp_insertAuditLog(AuditLogRequest auditLogRequest);

}