package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IBranchCodeCounterInterface;
import com.maven.rms.models.BranchCodeCounterRequest;

@Repository
public class BranchCodeCounterRepository implements IBranchCodeCounterInterface {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getbccmap(BranchCodeCounterRequest getRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getbccmap(:i_page, :i_size, :i_bcc_id, :i_counter_id, :i_terminal_id, :i_counter_ip, :i_bcm_id, :i_bcm_code, :i_dt_modified_fr, :i_dt_modified_to, :i_modified_by, :i_status)")
                .setParameter("i_page", getRequest.getI_page())
                .setParameter("i_size", getRequest.getI_size())
                .setParameter("i_bcc_id", getRequest.getI_bcc_id())
                .setParameter("i_counter_id", getRequest.getI_counter_id())
                .setParameter("i_terminal_id", getRequest.getI_terminal_id())
                .setParameter("i_counter_ip", getRequest.getI_counter_ip())
                .setParameter("i_bcm_id", getRequest.getI_bcm_id())
                .setParameter("i_bcm_code", getRequest.getI_bcm_code())
                .setParameter("i_dt_modified_fr", getRequest.getI_dt_modified_fr() != null ? getRequest.getI_dt_modified_fr() : null)
                .setParameter("i_dt_modified_to", getRequest.getI_dt_modified_to() != null ? getRequest.getI_dt_modified_to() : null)
                .setParameter("i_modified_by", getRequest.getI_modified_by())
                .setParameter("i_status", getRequest.getI_status());
            if (getRequest.getI_dt_modified_fr() != null) {
                query.setParameter("i_dt_modified_fr", getRequest.getI_dt_modified_fr());
            } else {
                query.setParameter("i_dt_modified_fr", null);
            }
    
            if (getRequest.getI_dt_modified_to() != null) {
                query.setParameter("i_dt_modified_to", getRequest.getI_dt_modified_to());
            } else {
                query.setParameter("i_dt_modified_to", null);
            }
        return query.getResultList();
    }

    @Override
    public Integer sp_insbccmap(BranchCodeCounterRequest insertRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_insbccmap(:i_counter_id, :i_terminal_id, :i_counter_ip, :i_bcm_id, :i_created_by, :i_modified_by, :i_status)")
                .setParameter("i_counter_id", insertRequest.getI_counter_id())
                .setParameter("i_terminal_id", insertRequest.getI_terminal_id())
                .setParameter("i_counter_ip", insertRequest.getI_counter_ip())
                .setParameter("i_bcm_id", insertRequest.getI_bcm_id())
                .setParameter("i_created_by", insertRequest.getI_created_by())
                .setParameter("i_modified_by", insertRequest.getI_modified_by())
                .setParameter("i_status", insertRequest.getI_status());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_updbccmap(BranchCodeCounterRequest updateRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_updbccmap(:i_bcc_id, :i_counter_id, :i_terminal_id, :i_counter_ip, :i_bcm_id, :i_modified_by, :i_status)")
            .setParameter("i_bcc_id", updateRequest.getI_bcc_id())
            .setParameter("i_counter_id", updateRequest.getI_counter_id())
            .setParameter("i_terminal_id", updateRequest.getI_terminal_id())
            .setParameter("i_counter_ip", updateRequest.getI_counter_ip())
            .setParameter("i_bcm_id", updateRequest.getI_bcm_id())
            .setParameter("i_modified_by", updateRequest.getI_modified_by())
            .setParameter("i_status", updateRequest.getI_status());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_delbccmap(BranchCodeCounterRequest deleteRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_delbccmap(:i_bcc_id, :i_modified_by, :i_status)")
            .setParameter("i_bcc_id", deleteRequest.getI_bcc_id())
            .setParameter("i_modified_by", deleteRequest.getI_modified_by())
            .setParameter("i_status", deleteRequest.getI_status());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }
    
}
