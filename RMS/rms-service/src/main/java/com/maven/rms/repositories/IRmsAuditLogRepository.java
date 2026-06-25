package com.maven.rms.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.RmsAuditLog;

@Repository
public interface IRmsAuditLogRepository extends JpaRepository<RmsAuditLog, Long> {

}
