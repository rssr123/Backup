package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IRTTReturnedChequeRepositoryInterface;
import com.maven.rms.models.RTTChargeback;
import com.maven.rms.models.RTTChargebackRequest;
import com.maven.rms.models.RTTOnlinePayment;
import com.maven.rms.models.RTTOnlinePaymentResubmit;
import com.maven.rms.models.RTTReturnedChequeRequest;
import com.maven.rms.models.RttAppEmailDto;
import com.maven.rms.services.AuthService;

@Repository
public class RTTReturnedChequeRepository implements IRTTReturnedChequeRepositoryInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuthService authService;

    // RTT_WF
    @Override
    public List<Object[]> sp_getrttreturnche(RTTReturnedChequeRequest getRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getrttslipPDF(:rtt_wf_id_input)")
                .setParameter("rtt_wf_id_input", getRequest.getI_rtt_id());

        return query.getResultList();
    }

    @Override
    public List<RTTOnlinePayment> findByRttStatus(String rttstatus) {
        Query query = entityManager
                .createNativeQuery("SELECT * FROM rms_rtt_wf WHERE rtt_status = :rttstatus", RTTOnlinePayment.class)
                .setParameter("rttstatus", rttstatus);
        return query.getResultList();
    }

    @Transactional
    @Override
    public RTTOnlinePayment save(RTTOnlinePayment rttonlinePayment) {
        if (rttonlinePayment.getRttWfId() == null) {
            entityManager.persist(rttonlinePayment);
            return rttonlinePayment;
        } else {
            return entityManager.merge(rttonlinePayment);
        }
    }

    // RTT
    @Override
    public List<Object[]> sp_getrttreturncheResubmit(RTTReturnedChequeRequest getRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getrttslipPDFResubmit(:rtt_id_input)")
                .setParameter("rtt_id_input", getRequest.getI_rtt_id());

        return query.getResultList();
    }

    @Override
    public List<RTTOnlinePaymentResubmit> findByRttStatusResubmit(String rttstatus) {
        Query query = entityManager
                .createNativeQuery("SELECT * FROM rms_rtt WHERE rtt_status = :rttstatus",
                        RTTOnlinePaymentResubmit.class)
                .setParameter("rttstatus", rttstatus);
        return query.getResultList();
    }

    @Transactional
    @Override
    public RTTOnlinePaymentResubmit save(RTTOnlinePaymentResubmit rttonlinePaymentResubmit) {
        if (rttonlinePaymentResubmit.getRttWfId() == null) {
            entityManager.persist(rttonlinePaymentResubmit);
            return rttonlinePaymentResubmit;
        } else {
            return entityManager.merge(rttonlinePaymentResubmit);
        }
    }

    @Override
    public List<Object[]> sp_getrttwfchargeback(RTTChargebackRequest getRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getrttwfchargeback(:rtt_wf_id_input)")
                .setParameter("rtt_wf_id_input", getRequest.getI_rtt_wf_id());

        return query.getResultList();
    }

    @Override
    public List<RTTChargeback> findByChargeBackRttStatus(String rttstatus) {
        Query query = entityManager
                .createNativeQuery("SELECT * FROM rms_rtt_wf WHERE rtt_status = :rttstatus", RTTChargeback.class)
                .setParameter("rttstatus", rttstatus);
        return query.getResultList();
    }

    @Transactional
    @Override
    public RTTChargeback save(RTTChargeback rttChargeback) {
        if (rttChargeback.getRttWfId() == null) {
            entityManager.persist(rttChargeback);
            return rttChargeback;
        } else {
            return entityManager.merge(rttChargeback);
        }
    }

    @Override
    public RttAppEmailDto findAppEmailByWfId(int wfId) {
        // nativeQuery returns Object[]: [ rtt_app_no, cust_email ]
        Query q = entityManager.createNativeQuery(
                "SELECT rtt_app_no, cust_email, orn_no " +
                        "  FROM rms_rtt_wf " +
                        " WHERE rtt_wf_id = :wfId");
        q.setParameter("wfId", wfId);

        try {
            Object[] row = (Object[]) q.getSingleResult();
            String rttappNo = (String) row[0];
            String custEmail = (String) row[1];
            String ornNo = (String) row[2];
            return new RttAppEmailDto(rttappNo, custEmail, ornNo);
        } catch (NoResultException ex) {
            return null;
        }
    }

    @Override
    public int sp_updrttslippdf(int rttWfId) {
        // Informix note: some drivers return BigDecimal for INTEGER, so coerce via
        // Number
        Object result;

        try {
            // Either EXECUTE PROCEDURE or CALL works on Informix. EXECUTE PROCEDURE is
            // common.
            Query q = entityManager.createNativeQuery(
                    "CALL sp_updrttslippdf(:rtt_wf_id_input)").setParameter("rtt_wf_id_input", rttWfId);
            result = q.getSingleResult(); // returns a single scalar
        } catch (NoResultException ex) {
            // Procedure returned nothing; treat as no-op
            return 0;
        }

        if (result == null)
            return 0;
        if (result instanceof Number)
            return ((Number) result).intValue();

        // Fallback: try parse string
        return Integer.parseInt(result.toString());
    }

}