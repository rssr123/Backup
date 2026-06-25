package com.maven.rms.repositories;

import com.maven.rms.interfaces.IBillingInterface;
import com.maven.rms.models.BillingMyTaskListingRequest;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

@Repository
public class BillingMyTaskRepository implements IBillingInterface {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getbillinglisting(BillingMyTaskListingRequest billingRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getbillinglisting(:i_page, :i_size, :i_username, :i_my_task_mode, :i_task_id, :i_task_desc, :i_approval_status, :i_requested_by, :i_dt_requested, :i_assigned_to)")
        .setParameter("i_page", billingRequest.getI_page())
        .setParameter("i_size", billingRequest.getI_size())
        .setParameter("i_username", billingRequest.getI_username())
        .setParameter("i_my_task_mode", billingRequest.getI_my_task_mode())
        .setParameter("i_task_id", billingRequest.getI_task_id())
        .setParameter("i_task_desc", billingRequest.getI_task_desc())
        .setParameter("i_approval_status", billingRequest.getI_approval_status())
        .setParameter("i_requested_by", billingRequest.getI_requested_by())
        .setParameter("i_dt_requested", billingRequest.getI_dt_requested())
        .setParameter("i_assigned_to", billingRequest.getI_assigned_to());

        return query.getResultList();
    }

    public Integer sp_getbillingassignedtaskactivetaskcount(BillingMyTaskListingRequest billingMyTaskListingRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getbillingassignedtaskactivetaskcount(:i_assigned_to)")
        .setParameter("i_assigned_to", billingMyTaskListingRequest.getI_assigned_to());

        return (Integer) query.getSingleResult();
    }

    public Integer sp_getbillingcreatedtaskactivetaskcount(BillingMyTaskListingRequest billingMyTaskListingRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getbillingcreatedtaskactivetaskcount(:i_created_by)")
        .setParameter("i_created_by", billingMyTaskListingRequest.getI_created_by());

        return (Integer) query.getSingleResult();
    }
}
