package com.maven.rms.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IAuditLogService;
import com.maven.rms.models.AuditLogRequest;
import com.maven.rms.repositories.AuditLogRepository;

@Service
@Slf4j
public class AuditLogService implements IAuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public Integer sp_insertAuditLog(AuditLogRequest auditLogRequest) {
        Integer result = 0;

        result = auditLogRepository.sp_insertAuditLog(auditLogRequest);

        return result;
    }

}