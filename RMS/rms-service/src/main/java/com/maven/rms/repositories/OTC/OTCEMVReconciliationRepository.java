package com.maven.rms.repositories.OTC;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.maven.rms.services.AuthService;
import com.maven.rms.utils.SystemStatus;

import com.maven.rms.interfaces.IOTCEMVReconciliationRepositoryInterface;
import com.maven.rms.models.OTC.OTCEMVReconciliationRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliationDocRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliationDocUpRequest;
import com.maven.rms.models.OTC.OTCEMVReconciliationStatusRequest;

@Repository
public class OTCEMVReconciliationRepository implements IOTCEMVReconciliationRepositoryInterface{
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuthService authService;

    @Override
    public List<Object[]> sp_getotcemvreconciliation(OTCEMVReconciliationRequest otcEmvReconciliationRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcemvreconciliation(:i_page, :i_size, :i_date_from, :i_date_to)")
                .setParameter("i_page", otcEmvReconciliationRequest.getI_page())
                .setParameter("i_size", otcEmvReconciliationRequest.getI_size())
                .setParameter("i_date_from", otcEmvReconciliationRequest.getI_date_from())
                .setParameter("i_date_to", otcEmvReconciliationRequest.getI_date_to());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcemvreconciliationcheck(OTCEMVReconciliationRequest otcEmvReconciliationRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcemvreconciliationcheck(:i_date_from, :i_date_to)")
                .setParameter("i_date_from", otcEmvReconciliationRequest.getI_date_from())
                .setParameter("i_date_to", otcEmvReconciliationRequest.getI_date_to());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcemvreconciliationsummary(OTCEMVReconciliationRequest otcEmvReconciliationRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcemvreconciliationsummary(:i_date_from, :i_date_to)")
                .setParameter("i_date_from", otcEmvReconciliationRequest.getI_date_from())
                .setParameter("i_date_to", otcEmvReconciliationRequest.getI_date_to());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcemvreconciliationrc(OTCEMVReconciliationRequest otcEmvReconciliationRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcemvreconciliationrc(:i_date_from, :i_date_to)")
                .setParameter("i_date_from", otcEmvReconciliationRequest.getI_date_from())
                .setParameter("i_date_to", otcEmvReconciliationRequest.getI_date_to());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcemvreconciliationsettlement(OTCEMVReconciliationRequest otcEmvReconciliationRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcemvreconciliationsettlement(:i_date_from, :i_date_to)")
                .setParameter("i_date_from", otcEmvReconciliationRequest.getI_date_from())
                .setParameter("i_date_to", otcEmvReconciliationRequest.getI_date_to());

        return query.getResultList();
    }

    @Override
    public Blob sp_getotcbaldoccontent(OTCEMVReconciliationDocRequest otcEmvReconciliationDocRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcbaldoccontent(:i_otc_bal_doc_id)")
                .setParameter("i_otc_bal_doc_id", otcEmvReconciliationDocRequest.getI_otc_bal_doc_id());

        return (Blob) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_getrcemv(OTCEMVReconciliationStatusRequest request) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getrcemv(:i_date_from, :i_date_to)")
                .setParameter("i_date_from", request.getI_date_from())
                .setParameter("i_date_to", request.getI_date_to());

        return query.getResultList();
    }

    @Override
    public Integer sp_insrcemv(OTCEMVReconciliationStatusRequest request) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_insrcemv(:i_dt_balancing, :i_created_by, :i_modified_by, :i_status, :i_rc_emv_status)")
                .setParameter("i_dt_balancing", request.getI_dt_balancing())
                .setParameter("i_created_by", request.getI_created_by())
                .setParameter("i_modified_by", request.getI_modified_by())
                .setParameter("i_status", request.getI_status())
                .setParameter("i_rc_emv_status", request.getI_rc_emv_status());

        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_updrcemv(OTCEMVReconciliationStatusRequest request) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_updrcemv(:i_dt_balancing, :i_modified_by, :i_rc_emv_status)")
                .setParameter("i_dt_balancing", request.getI_dt_balancing())
                .setParameter("i_modified_by", request.getI_modified_by())
                .setParameter("i_rc_emv_status", request.getI_rc_emv_status());

        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_insrcemvdoc(OTCEMVReconciliationDocUpRequest request, Blob blob) {
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insrcemvdoc");

        storedProcedureQuery.registerStoredProcedureParameter("i_dt_balancing", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_nm", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_content", Blob.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_type", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_size", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dr_count", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dr_amt", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cr_count", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cr_amt", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_total", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_created_by", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_modified_by", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_status", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_rc_emv_id", BigInteger.class, javax.persistence.ParameterMode.IN);

        storedProcedureQuery.setParameter("i_dt_balancing", request.getI_dt_balancing());
        storedProcedureQuery.setParameter("i_file_nm", request.getI_file_nm());
        storedProcedureQuery.setParameter("i_file_content", blob);
        storedProcedureQuery.setParameter("i_file_type", request.getI_file_type());
        storedProcedureQuery.setParameter("i_file_size", request.getI_file_size());
        storedProcedureQuery.setParameter("i_dr_count", request.getI_dr_count());
        storedProcedureQuery.setParameter("i_dr_amt", request.getI_dr_amt());
        storedProcedureQuery.setParameter("i_cr_count", request.getI_cr_count());
        storedProcedureQuery.setParameter("i_cr_amt", request.getI_cr_amt());
        storedProcedureQuery.setParameter("i_total", request.getI_total());
        storedProcedureQuery.setParameter("i_created_by", authService.getLoginUserName());
        storedProcedureQuery.setParameter("i_modified_by", authService.getLoginUserName());
        storedProcedureQuery.setParameter("i_status", SystemStatus.Active.getMessage());
        storedProcedureQuery.setParameter("i_rc_emv_id", request.getI_rc_emv_id());
      
        // Execute stored procedure
        storedProcedureQuery.execute();

        // Handle the result (if the stored procedure returns a result set or an output parameter)
        // For example, if the stored procedure returns a single integer result:
        Integer result = null;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }
        return result;
    }

    @Override
    public List<Object[]> sp_getrcemvdoclist(OTCEMVReconciliationDocRequest otcEmvReconciliationRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getrcemvdoclist(:i_rc_emv_id)")
                .setParameter("i_rc_emv_id", otcEmvReconciliationRequest.getI_rc_emv_id());

        return query.getResultList();
    }

    @Override
    public Blob sp_getrcemvdoccontent(OTCEMVReconciliationDocRequest otcEmvReconciliationDocRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getrcemvdoccontent(:i_rc_emv_doc_id)")
                .setParameter("i_rc_emv_doc_id", otcEmvReconciliationDocRequest.getI_rc_emv_doc_id());

        return (Blob) query.getSingleResult();
    }

}
