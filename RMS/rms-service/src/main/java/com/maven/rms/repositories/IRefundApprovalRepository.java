package com.maven.rms.repositories;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import java.util.Arrays;
import java.util.stream.Collectors;
import com.maven.rms.interfaces.IRefundApprovalInterface;
import com.maven.rms.models.RefundApprovalDetReq;
import com.maven.rms.models.RefundPTTListingDetReq;

@Repository
public class IRefundApprovalRepository implements IRefundApprovalInterface {
    @PersistenceContext
    private EntityManager entityManager;

    // #region FMS Start
    @Override
    public List<Object[]> sp_getrefundapproval(RefundApprovalDetReq req) {

        // Log the stored procedure call
        String spCall = "CALL sp_getrefundapproval(" + req.getI_rtt_wf_id() + ")";
        System.out.println("Executing stored procedure: " + spCall);

        Query query = entityManager.createNativeQuery(
                "CALL sp_getrefundapproval(:i_rtt_wf_id)")
                .setParameter("i_rtt_wf_id", req.getI_rtt_wf_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getrttitem(RefundApprovalDetReq req) {

        Query query = entityManager.createNativeQuery(

                "CALL sp_getrttitem(:i_rtt_wf_id)")
                .setParameter("i_rtt_wf_id", req.getI_rtt_wf_id());
        return query.getResultList();
    }

    @Override
    public Integer sp_updrttwf_status(RefundApprovalDetReq req) {
        // 1) build & print the literal CALL(...)
        String call = buildCallString(req);
        System.out.println("DEBUG — Executing: " + call);

        // 2) run it
        Query q = entityManager.createNativeQuery(
                "CALL sp_updrttwf_status("
                        + ":i_rtt_wf_id, :i_rtt_status, :i_msg, :i_modified_by, "
                        + ":i_refund_cd, :i_pickup_by, :i_assign_to, :i_refund_reason)")
                .setParameter("i_rtt_wf_id", req.getI_rtt_wf_id())
                .setParameter("i_rtt_status", req.getI_rtt_status())
                .setParameter("i_msg", req.getI_msg())
                .setParameter("i_modified_by", req.getI_modified_by())
                .setParameter("i_refund_cd", req.getI_refund_cd())
                .setParameter("i_pickup_by", req.getI_pickup_by())
                .setParameter("i_assign_to", req.getI_assign_to())
                .setParameter("i_refund_reason", req.getI_refund_reason());

        Integer result = (Integer) q.getSingleResult();
        System.out.println("DEBUG — sp_updrttwf_status returned: " + result);
        return result;
    }

    private String buildCallString(RefundApprovalDetReq r) {
        Object[] vals = {
                r.getI_rtt_wf_id(),
                r.getI_rtt_status(),
                r.getI_msg(),
                r.getI_modified_by(),
                r.getI_refund_cd(),
                r.getI_pickup_by(),
                r.getI_assign_to(),
                r.getI_refund_reason()
        };
        String joined = Arrays.stream(vals)
                .map(v -> {
                    if (v == null)
                        return "NULL";
                    if (v instanceof String) {
                        String s = ((String) v).replace("'", "''");
                        return "'" + s + "'";
                    }
                    return v.toString();
                })
                .collect(Collectors.joining(", "));
        return "CALL sp_updrttwf_status(" + joined + ")";
    }

    @Override
    public List<Object[]> sp_getrttform(RefundApprovalDetReq req) {

        Query query = entityManager.createNativeQuery(

                "CALL sp_getrttform(:i_orn_no)")
                .setParameter("i_orn_no", req.getI_orn_no());
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getrttdoc(RefundApprovalDetReq req) {
        System.out.println("Entering sp_getrttdoc with parameter: i_rtt_wf_id = " + req.getI_rtt_wf_id());

        try {
            Query query = entityManager.createNativeQuery(
                    "CALL sp_getrttdoc(:i_rtt_wf_id)")
                    .setParameter("i_rtt_wf_id", req.getI_rtt_wf_id());

            System.out.println("Executing query: CALL sp_getrttdoc(" + req.getI_rtt_wf_id() + ")");

            // Get results as a list of Object[]
            @SuppressWarnings("unchecked")
            List<Object[]> results = query.getResultList();

            System.out.println("Query executed successfully. Results size: " + (results != null ? results.size() : 0));
            return results;
        } catch (Exception e) {
            System.out.println("Error in sp_getrttdoc: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw the exception to propagate it up
        }
    }

    @Override
    public Integer sp_updrttwf_returntask(RefundApprovalDetReq rttwfrequest) {
        Query query = entityManager
                .createNativeQuery(
                        "CALL sp_updrttwf_returntask(:i_rtt_wf_id, :i_modified_by)")
                .setParameter("i_rtt_wf_id", rttwfrequest.getI_rtt_wf_id())
                .setParameter("i_modified_by", rttwfrequest.getI_modified_by());

        return (Integer) query.getSingleResult();
    }

    @Override
    public String sp_getRttAppEmail(String app_no) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getcustinfo_appno(:app_no)")
                .setParameter("app_no", app_no);

        return (String) query.getSingleResult();
    }

}
