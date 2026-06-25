package com.maven.rms.repositories;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.maven.rms.interfaces.IAuditLogInterface;
import com.maven.rms.models.AuditLogRequest;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class AuditLogRepository implements IAuditLogInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Integer sp_insertAuditLog(AuditLogRequest auditLogRequest) {
    	/*
    	System.out.print("CALL sp_insauditlog('");
    	System.out.print(auditLogRequest.getI_actor() + "','");
    	System.out.print(auditLogRequest.getI_request_url() + "','");
    	System.out.print(auditLogRequest.getI_module() + "','");
    	System.out.print(auditLogRequest.getI_request_msg() + "','");
    	System.out.print(auditLogRequest.getI_action() + "','");
    	System.out.print(auditLogRequest.getI_page_url() + "','");
    	System.out.print(auditLogRequest.getI_remark() + "','");
    	System.out.print(auditLogRequest.getI_source() + "','");
    	System.out.print(auditLogRequest.getI_response_msg() + "','");
    	System.out.print(auditLogRequest.getI_created_by() + "','");
    	System.out.print(auditLogRequest.getI_modified_by() + "','");
    	System.out.print(auditLogRequest.getI_status() + "');");
    	*/
        Query query = entityManager.createNativeQuery(
                "CALL sp_insauditlog(:i_actor, :i_request_url, :i_module, :i_request_msg, :i_action, :i_page_url, "
                + ":i_remark, :i_source, :i_response_msg, :i_created_by, :i_modified_by, :i_status)")
                .setParameter("i_actor", auditLogRequest.getI_actor())
                .setParameter("i_request_url", auditLogRequest.getI_request_url())
                .setParameter("i_module", auditLogRequest.getI_module())
                .setParameter("i_request_msg", auditLogRequest.getI_request_msg())
                .setParameter("i_action", auditLogRequest.getI_action())
                .setParameter("i_page_url", auditLogRequest.getI_page_url())
                .setParameter("i_remark", auditLogRequest.getI_remark())
                .setParameter("i_source", auditLogRequest.getI_source())
                .setParameter("i_response_msg", auditLogRequest.getI_response_msg())
                .setParameter("i_created_by", auditLogRequest.getI_created_by())
                .setParameter("i_modified_by", auditLogRequest.getI_modified_by())
                .setParameter("i_status", auditLogRequest.getI_status());

        Object result = query.getSingleResult();

        // Handle null or unexpected types gracefully
        if (result != null) {
            try {
                return Integer.parseInt(result.toString());
            } catch (NumberFormatException e) {
                // Log the unexpected result if needed
                log.error("AuditLogRepository Error", e);
            }
        }

        return 0;
    }
}
