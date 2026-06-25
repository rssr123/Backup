package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.CCTaskListReq;
import com.maven.rms.services.AuthService;

@Repository
public class CreditControlSMERepository implements ICreditControlSMERepository {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuthService authService;

    @Override
    public List<Object[]> sp_getcreditcontroltasklist(CCTaskListReq request) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getcreditcontroltasklist(:i_page, :i_size, :i_task_id, :i_task_status, :i_payment_status, :i_txn_ty, :i_case_no, :i_cust_nm)")
                .setParameter("i_page", request.getI_page())
                .setParameter("i_size", request.getI_size())
                .setParameter("i_task_id", request.getI_task_id())
                .setParameter("i_task_status", request.getI_task_status())
                .setParameter("i_payment_status", request.getI_payment_status())
                .setParameter("i_txn_ty", request.getI_txn_type())
                .setParameter("i_case_no", request.getI_case_no())
                .setParameter("i_cust_nm", request.getI_cust_nm());

        return query.getResultList();
    }

    @Override
    public Integer sp_assigncctask(List<CCTaskListReq> request) {
        Integer result = 0;

        for(CCTaskListReq req : request) {
            Query query = entityManager.createNativeQuery(
                    "CALL sp_assigncctask(:i_cc_case_id, :i_assign_to, :i_modified_by)")
                    .setParameter("i_cc_case_id", req.getI_cc_case_id())
                    .setParameter("i_assign_to", authService.getLoginUserName())
                    .setParameter("i_modified_by", authService.getLoginUserName());
        result = (Integer) query.getSingleResult();
        }
        return result;

    }
}
