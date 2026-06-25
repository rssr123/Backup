package com.maven.rms.services;

import org.springframework.stereotype.Service;

import com.maven.rms.models.RmsAuditLog;
import com.maven.rms.repositories.IRmsAuditLogRepository;

@Service
public class RmsAuditLogService {

    private final IRmsAuditLogRepository repository;

    public RmsAuditLogService(IRmsAuditLogRepository repository) {
        this.repository = repository;
    }

    public RmsAuditLog saveAuditLog(RmsAuditLog auditLog) {
        // Add any business logic if needed
        return repository.save(auditLog);
    }

}
