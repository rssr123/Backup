package com.maven.rms.repositories;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IOTCRcptCclInterface;
import com.maven.rms.models.MFTWFRequest;
import com.maven.rms.models.MTTOrderStatusRequest;
import com.maven.rms.models.OTCReceiptCancellationAssignToRequest;
import com.maven.rms.models.OTCReceiptCancellationBalStatusDetailsRequest;
import com.maven.rms.models.OTCReceiptCancellationCreatedByRequest;
import com.maven.rms.models.OTCReceiptCancellationDetailsRequest;
import com.maven.rms.models.OTCReceiptCancellationListingRequest;
import com.maven.rms.models.OTCReceiptCancellationMyTaskListingRequest;
import com.maven.rms.models.OTCReceiptCancellationRequest;
import com.maven.rms.models.OTCReceiptCancellationSupervisorRequest;
import com.maven.rms.models.OTCReceiptCancellationTaskAndReqInfoApprovalRequest;
import com.maven.rms.models.OTCReceiptCancellationUpdateRequest;
import com.maven.rms.models.OTCReceiptCclMTTOrderStatusRequest;
import com.maven.rms.models.OTC.OTCollectionReceiptingRequest;


@Repository
public class OTCRcptCclRepository implements IOTCRcptCclInterface{
    
    
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<Object[]> sp_getotcrcptccllisting(OTCReceiptCancellationListingRequest otcrcptcclRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getotcrcptccllisting(:i_page, :i_size, :i_rcpt_no, :i_orn_no, :i_cust_nm, :i_otc_counter_id)")
                .setParameter("i_page", otcrcptcclRequest.getI_page())
                .setParameter("i_size", otcrcptcclRequest.getI_size())
                .setParameter("i_rcpt_no", otcrcptcclRequest.getI_rcpt_no())
                .setParameter("i_orn_no", otcrcptcclRequest.getI_orn_no())
                .setParameter("i_cust_nm", otcrcptcclRequest.getI_cust_nm())
                .setParameter("i_otc_counter_id", otcrcptcclRequest.getI_otc_counter_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcrcptccloderinfodetails(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getotcrcptccloderinfodetails(:i_mtt_id)")
                .setParameter("i_mtt_id", otcrcptcclDetsRequest.getI_mtt_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcrcptcclpymtinfodetails(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getotcrcptcclpymtinfodetails(:i_mtt_id)")
                .setParameter("i_mtt_id", otcrcptcclDetsRequest.getI_mtt_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcrcptcclrcptinfodetails(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getotcrcptcclrcptinfodetails(:i_mtt_id)")
                .setParameter("i_mtt_id", otcrcptcclDetsRequest.getI_mtt_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcrcptcclhistorydetails(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getotcrcptcclhistorydetails(:i_mtt_id)")
                .setParameter("i_mtt_id", otcrcptcclDetsRequest.getI_mtt_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcrcptccltaskandreqinfoapproval(OTCReceiptCancellationTaskAndReqInfoApprovalRequest otcrcptcclTaskAndReqInfoApprovalRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getotcrcptccltaskandreqinfoapproval(:i_otc_rc_id)")
                .setParameter("i_otc_rc_id", otcrcptcclTaskAndReqInfoApprovalRequest.getI_otc_rc_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcrcptcclmytasklisting(OTCReceiptCancellationMyTaskListingRequest otcrcptcclMyTaskListingRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getotcrcptcclmytasklisting(:i_page, :i_size, :i_otc_rc_id, :i_otc_id, :i_rc_type, :i_rc_status, :i_task_id, :i_counter_id, :i_requested_by, :i_requested_by_nm, :i_approved_by, :i_approved_by_nm, :i_date_requested_fr, :i_date_requested_to, :i_assigned_to, :i_assigned_to_nm)")
                .setParameter("i_page", otcrcptcclMyTaskListingRequest.getI_page())
                .setParameter("i_size", otcrcptcclMyTaskListingRequest.getI_size())
                .setParameter("i_otc_rc_id", otcrcptcclMyTaskListingRequest.getI_otc_rc_id())
                .setParameter("i_otc_id", otcrcptcclMyTaskListingRequest.getI_otc_id())
                .setParameter("i_rc_type", otcrcptcclMyTaskListingRequest.getI_rc_type())
                .setParameter("i_rc_status", otcrcptcclMyTaskListingRequest.getI_rc_status())
                .setParameter("i_task_id", otcrcptcclMyTaskListingRequest.getI_task_id())
                .setParameter("i_counter_id", otcrcptcclMyTaskListingRequest.getI_counter_id())
                .setParameter("i_requested_by", otcrcptcclMyTaskListingRequest.getI_requested_by())
                .setParameter("i_requested_by_nm", otcrcptcclMyTaskListingRequest.getI_requested_by_nm())
                .setParameter("i_approved_by", otcrcptcclMyTaskListingRequest.getI_approved_by())
                .setParameter("i_approved_by_nm", otcrcptcclMyTaskListingRequest.getI_approved_by_nm())
                .setParameter("i_date_requested_fr", otcrcptcclMyTaskListingRequest.getI_date_requested_fr())
                .setParameter("i_date_requested_to", otcrcptcclMyTaskListingRequest.getI_date_requested_to())
                .setParameter("i_assigned_to", otcrcptcclMyTaskListingRequest.getI_assigned_to())
                .setParameter("i_assigned_to_nm", otcrcptcclMyTaskListingRequest.getI_assigned_to_nm());


        return query.getResultList();
    }


    @Override
    public List<Object[]> sp_getotcrcptcclbalstatusdetails(OTCReceiptCancellationBalStatusDetailsRequest otcrcptcclBalStatusDetsRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getotcrcptcclbalstatusdetails(:i_otc_counter_id, :i_rc_type)")
                .setParameter("i_otc_counter_id", otcrcptcclBalStatusDetsRequest.getI_otc_counter_id())
                .setParameter("i_rc_type", otcrcptcclBalStatusDetsRequest.getI_rc_type());

        return query.getResultList();
    }


    @Override
    public BigInteger sp_insotcrc(OTCReceiptCancellationRequest otcrcRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_insotcrc(:i_otc_id, :i_justication, :i_rc_type, :i_rc_status, :i_counter_id, :i_requested_by, :i_requester_id, :i_others, :i_approved_by, :i_approver_id, :i_remark, :i_created_by, :i_modified_by, :i_status, :i_assigned_to, :i_action, :i_otc_counter_id)")
                .setParameter("i_otc_id", otcrcRequest.getI_otc_id() != null ? otcrcRequest.getI_otc_id() : null)
                .setParameter("i_justication", otcrcRequest.getI_justication() != null ? otcrcRequest.getI_justication() : null)
                .setParameter("i_rc_type", otcrcRequest.getI_rc_type() != null ? otcrcRequest.getI_rc_type() : null)
                .setParameter("i_rc_status", otcrcRequest.getI_rc_status() != null ? otcrcRequest.getI_rc_status() : null)
                .setParameter("i_counter_id", otcrcRequest.getI_counter_id() != null ? otcrcRequest.getI_counter_id() : null)
                .setParameter("i_requested_by", otcrcRequest.getI_requested_by() != null ? otcrcRequest.getI_requested_by() : null)
                .setParameter("i_requester_id", otcrcRequest.getI_requester_id() != null ? otcrcRequest.getI_requester_id() : null)
                .setParameter("i_others", otcrcRequest.getI_others() != null ? otcrcRequest.getI_others() : null)
                .setParameter("i_approved_by", otcrcRequest.getI_approved_by() != null ? otcrcRequest.getI_approved_by() : null)
                .setParameter("i_approver_id", otcrcRequest.getI_approver_id() != null ? otcrcRequest.getI_approver_id() : null)
                .setParameter("i_remark", otcrcRequest.getI_remark() != null ? otcrcRequest.getI_remark() : null)
                .setParameter("i_created_by", otcrcRequest.getI_created_by() != null ? otcrcRequest.getI_created_by() : null)
                .setParameter("i_modified_by", otcrcRequest.getI_modified_by() != null ? otcrcRequest.getI_modified_by() : null)
                .setParameter("i_status", otcrcRequest.getI_status() != null ? otcrcRequest.getI_status() : null)
                .setParameter("i_assigned_to", otcrcRequest.getI_assigned_to() != null ? otcrcRequest.getI_assigned_to() : null)
                .setParameter("i_action", otcrcRequest.getI_action() != null ? otcrcRequest.getI_action() : null)
                .setParameter("i_otc_counter_id", otcrcRequest.getI_otc_counter_id() != null ? otcrcRequest.getI_otc_counter_id() : null);

        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
    }


    @Override
    public Integer sp_updotcrc(OTCReceiptCancellationUpdateRequest otcrcRequestUpdate) {
        Query query = entityManager
                .createNativeQuery("CALL sp_updotcrc(:i_otc_rc_id, :i_otc_id, :i_justication, :i_rc_type, :i_rc_status, :i_task_id, :i_counter_id, :i_requested_by, :i_requester_id, :i_others, :i_approved_by, :i_approver_id, :i_remark, :i_modified_by, :i_status, :i_assigned_to, :i_action, :i_otc_counter_id, :i_from_otcsupervisor)")
                .setParameter("i_otc_rc_id", otcrcRequestUpdate.getI_otc_rc_id())
                .setParameter("i_otc_id", otcrcRequestUpdate.getI_otc_id() != null ? otcrcRequestUpdate.getI_otc_id() : null)
                .setParameter("i_justication", otcrcRequestUpdate.getI_justication() != null ? otcrcRequestUpdate.getI_justication() : null)
                .setParameter("i_rc_type", otcrcRequestUpdate.getI_rc_type() != null ? otcrcRequestUpdate.getI_rc_type() : null)
                .setParameter("i_rc_status", otcrcRequestUpdate.getI_rc_status() != null ? otcrcRequestUpdate.getI_rc_status() : null)
                .setParameter("i_task_id", otcrcRequestUpdate.getI_task_id() != null ? otcrcRequestUpdate.getI_task_id() : null)
                .setParameter("i_counter_id", otcrcRequestUpdate.getI_counter_id() != null ? otcrcRequestUpdate.getI_counter_id() : null)
                .setParameter("i_requested_by", otcrcRequestUpdate.getI_requested_by() != null ? otcrcRequestUpdate.getI_requested_by() : null)
                .setParameter("i_requester_id", otcrcRequestUpdate.getI_requester_id() != null ? otcrcRequestUpdate.getI_requester_id() : null)
                .setParameter("i_others", otcrcRequestUpdate.getI_others() != null ? otcrcRequestUpdate.getI_others() : null)
                .setParameter("i_approved_by", otcrcRequestUpdate.getI_approved_by() != null ? otcrcRequestUpdate.getI_approved_by() : null)
                .setParameter("i_approver_id", otcrcRequestUpdate.getI_approver_id() != null ? otcrcRequestUpdate.getI_approver_id() : null)
                .setParameter("i_remark", otcrcRequestUpdate.getI_remark() != null ? otcrcRequestUpdate.getI_remark() : null)
                .setParameter("i_modified_by", otcrcRequestUpdate.getI_modified_by() != null ? otcrcRequestUpdate.getI_modified_by() : null)
                .setParameter("i_status", otcrcRequestUpdate.getI_status() != null ? otcrcRequestUpdate.getI_status() : null)
                .setParameter("i_assigned_to", otcrcRequestUpdate.getI_assigned_to() != null ? otcrcRequestUpdate.getI_assigned_to() : null)
                .setParameter("i_action", otcrcRequestUpdate.getI_action() != null ? otcrcRequestUpdate.getI_action() : null)
                .setParameter("i_otc_counter_id", otcrcRequestUpdate.getI_otc_counter_id() != null ? otcrcRequestUpdate.getI_otc_counter_id() : null)
                .setParameter("i_from_otcsupervisor", otcrcRequestUpdate.getI_from_otcsupervisor() != null ? otcrcRequestUpdate.getI_from_otcsupervisor() : null);


        return (Integer) query.getSingleResult();
    }


    @Override
    public Integer sp_updmtt_orderstatus(OTCReceiptCclMTTOrderStatusRequest mttOrderStatusRequest) {
        Query query = entityManager
                .createNativeQuery("CALL sp_updmtt_orderstatus(:i_mtt_id, :i_order_status, :i_modified_by)")
                .setParameter("i_mtt_id", mttOrderStatusRequest.getI_mtt_id())
                .setParameter("i_order_status", mttOrderStatusRequest.getI_order_status() != null ? mttOrderStatusRequest.getI_order_status() : null)
                .setParameter("i_modified_by", mttOrderStatusRequest.getI_modified_by() != null ? mttOrderStatusRequest.getI_modified_by() : null);
              

        return (Integer) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_getotcrcpltoCancel(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getotcrcpltoCancel(:i_mtt_id)")
                .setParameter("i_mtt_id", otcrcptcclDetsRequest.getI_mtt_id());

                return query.getResultList();
    }


    @Override
    public Object[] sp_getmttrcptinfowithstatus(BigInteger mtt_id) {
        Query query = entityManager.createNativeQuery("CALL sp_getmttrcptinfowithstatus(:i_mtt_id)")
                    .setParameter("i_mtt_id", mtt_id);
        Object[] result = (Object[]) query.getSingleResult();
        return result;
    }

    public Object[] sp_getotcreceipt(OTCReceiptCclMTTOrderStatusRequest mttOrderStatusRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getotcreceipt(:i_otc_rc_id)")
                    .setParameter("i_otc_rc_id", mttOrderStatusRequest.getI_otc_rc_id());
        Object[] result = (Object[]) query.getSingleResult();
        return result;
    }

    @Override
    public Object[] sp_getotcrcptcclorder(BigInteger i_mtt_id) {
        // Create the query to call the stored procedure
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcrcptcclorder(:i_mtt_id)")
                .setParameter("i_mtt_id", i_mtt_id);
        Object[] resultSet = (Object[]) query.getSingleResult();
        
        return resultSet;  // Return the array of results
    }

    @Override
    public List<Object[]> sp_getotcrcptcllpymtitembymtt(BigInteger i_mtt_id) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcrcptcllpymtitembymtt(:i_mtt_id)")
                .setParameter("i_mtt_id", i_mtt_id);
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_otcrcptcclpymtitem(OTCReceiptCancellationDetailsRequest getRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_otcrcptcclpymtitem(:i_mtt_id)")
                .setParameter("i_mtt_id", getRequest.getI_mtt_id());
        return query.getResultList();
    }


    public Integer sp_getotcrcassignedtaskactivetaskcount(OTCReceiptCancellationAssignToRequest otcrcptcclRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getotcrcassignedtaskactivetaskcount(:i_assign_to)")
             .setParameter("i_assign_to", otcrcptcclRequest.getI_assign_to());

        return (Integer) query.getSingleResult();
    }


    public Integer sp_getotcrccreatedtaskactivetaskcount(OTCReceiptCancellationCreatedByRequest otcrcptcclRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getotcrccreatedtaskactivetaskcount(:i_created_by)")
             .setParameter("i_created_by", otcrcptcclRequest.getI_created_by());

        return (Integer) query.getSingleResult();
    }


    @Override
    public List<Object[]> sp_getotcrcptcclhistorydetailsaudit(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getotcrcptcclhistorydetailsaudit(:i_mtt_id)")
                .setParameter("i_mtt_id", otcrcptcclDetsRequest.getI_mtt_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcrcptcclsupervisor(OTCReceiptCancellationSupervisorRequest otcrcptcclsupervisorRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getotcrcptcclsupervisor(:i_otc_counter_id)")
                .setParameter("i_otc_counter_id", otcrcptcclsupervisorRequest.getI_otc_counter_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_checkotcrcpt() {
        Query query = entityManager.createNativeQuery("call sp_checkotcrcpt()");
        return query.getResultList();
    }

    

}
