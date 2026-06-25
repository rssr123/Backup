package com.maven.rms.repositories;

import com.maven.rms.interfaces.IRefundInterface;
import com.maven.rms.models.RefundMyTaskListingRequest;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

@Repository
public class RefundRepository implements IRefundInterface {

    @PersistenceContext
    private EntityManager entityManager;
    
    @Override
    public List<Object[]> sp_getrefundlisting(RefundMyTaskListingRequest refundRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getrefundlisting(:i_page, :i_size, :i_username, :i_user_role, :i_my_task_mode, :i_task_id, :i_task_desc, :i_rtt_app_no, :i_requested_by, :i_dt_requested, :i_dt_pick, :i_rtt_status, :i_assigned_to)")
        .setParameter("i_page", refundRequest.getI_page())
        .setParameter("i_size", refundRequest.getI_size())
        .setParameter("i_username", refundRequest.getI_username())
        .setParameter("i_user_role", refundRequest.getI_user_role())
        .setParameter("i_my_task_mode", refundRequest.getI_my_task_mode())
        .setParameter("i_task_id", refundRequest.getI_task_id())
        .setParameter("i_task_desc", refundRequest.getI_task_desc())
        .setParameter("i_rtt_app_no", refundRequest.getI_rtt_app_no())
        .setParameter("i_requested_by", refundRequest.getI_requested_by())
        .setParameter("i_dt_requested", refundRequest.getI_dt_requested())
        .setParameter("i_dt_pick", refundRequest.getI_dt_pick())
        .setParameter("i_rtt_status", refundRequest.getI_rtt_status())
        .setParameter("i_assigned_to", refundRequest.getI_assigned_to());

        return query.getResultList();
    }

    public Integer sp_getrefundassignedtaskactivetaskcount(RefundMyTaskListingRequest refundMyTaskListingRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getrefundassignedtaskactivetaskcount(:i_assign_to)")
        .setParameter("i_assign_to", refundMyTaskListingRequest.getI_assigned_to());

        return (Integer) query.getSingleResult();
    }

    public Integer sp_getrefundcreatedtaskactivetaskcount(RefundMyTaskListingRequest refundMyTaskListingRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getrefundcreatedtaskactivetaskcount(:i_created_by)")
        .setParameter("i_created_by", refundMyTaskListingRequest.getI_created_by());

        return (Integer) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_getrttwf_id_list() {
        Query query = entityManager.createNativeQuery("call sp_getrttwf_id_list()");
        return query.getResultList();
    }
    
}
