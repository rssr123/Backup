package com.maven.rms.repositories;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IBillingClassRepository;
import com.maven.rms.models.BillingClassRequest;
import com.maven.rms.services.AuthService;

@Repository
public class BillingClassRepository implements IBillingClassRepository {

    @Autowired
    private AuthService authService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> getBillingClass(BillingClassRequest billingClassRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_getclassid(:i_page, :i_size, :i_class_id, :i_class_desc, :i_dt_modified_fr, :i_dt_modified_to, :i_modified_by, :i_status)")
            .setParameter("i_page", billingClassRequest.getI_page())
            .setParameter("i_size", billingClassRequest.getI_size())
            .setParameter("i_class_id", billingClassRequest.getI_class_id() != null ? billingClassRequest.getI_class_id() : null)
            .setParameter("i_class_desc", billingClassRequest.getI_class_desc() != null ? billingClassRequest.getI_class_desc() : null)
            .setParameter("i_dt_modified_fr", billingClassRequest.getI_dt_modified_fr() != null ? billingClassRequest.getI_dt_modified_fr() : null)
            .setParameter("i_dt_modified_to", billingClassRequest.getI_dt_modified_to() != null ? billingClassRequest.getI_dt_modified_to() : null)
            .setParameter("i_modified_by", billingClassRequest.getI_modified_by() != null ? billingClassRequest.getI_modified_by() : null)
            .setParameter("i_status", billingClassRequest.getI_status() != null ? billingClassRequest.getI_status() : null);

        return query.getResultList();
    }

    @Override
    public Integer sp_insblcm(BillingClassRequest billingClassRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_insblcm(:i_class_id, :i_class_desc, :i_created_by, :i_modified_by, :i_status)")
            .setParameter("i_class_id", billingClassRequest.getI_class_id())
            .setParameter("i_class_desc", billingClassRequest.getI_class_desc())
            .setParameter("i_created_by", authService.getLoginUserName())
            .setParameter("i_modified_by", authService.getLoginUserName())
            .setParameter("i_status", billingClassRequest.getI_status());

        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_updateblcm(BillingClassRequest billingClassRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_updateblcm(:i_blcm_id, :i_class_id, :i_class_desc, :i_modified_by, :i_status)")
            .setParameter("i_blcm_id", billingClassRequest.getI_blcm_id())
            .setParameter("i_class_id", billingClassRequest.getI_class_id())
            .setParameter("i_class_desc", billingClassRequest.getI_class_desc())
            .setParameter("i_modified_by", authService.getLoginUserName())
            .setParameter("i_status", billingClassRequest.getI_status());

        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_delblcm(BillingClassRequest billingClassRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_delblcm(:i_blcm_id, :i_class_id, :i_modified_by, :i_status)")
            .setParameter("i_blcm_id", billingClassRequest.getI_blcm_id())
            .setParameter("i_class_id", billingClassRequest.getI_class_id())
            .setParameter("i_modified_by", authService.getLoginUserName())
            .setParameter("i_status", billingClassRequest.getI_status());

        return (Integer) query.getSingleResult();
    }
}
