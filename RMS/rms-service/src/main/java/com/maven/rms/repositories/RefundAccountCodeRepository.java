package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IRefundAccountCodeInterface;

import com.maven.rms.models.Refund;
import com.maven.rms.models.RefundWf;
import com.maven.rms.models.RefundWfHist;
import com.maven.rms.models.RefundAccountCodeRequest;

@Repository
public class RefundAccountCodeRepository implements IRefundAccountCodeInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getrttacc(RefundAccountCodeRequest getRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getrttacc(:i_page, :i_size, :i_rtt_acc_id, :i_acc_cd, :i_acc_desc, :i_dt_modified_fr, :i_dt_modified_to, :i_modified_by, :i_status)")
                .setParameter("i_page", getRequest.getI_page())
                .setParameter("i_size", getRequest.getI_size())
                .setParameter("i_rtt_acc_id", getRequest.getI_rtt_acc_id())
                .setParameter("i_acc_cd", getRequest.getI_acc_cd())
                .setParameter("i_acc_desc", getRequest.getI_acc_desc())
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
    public Integer sp_insrttacc(RefundAccountCodeRequest insertRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_insrttacc(:i_acc_cd, :i_acc_desc, :i_created_by, :i_modified_by, :i_status)")
                .setParameter("i_acc_cd", insertRequest.getI_acc_cd())
                .setParameter("i_acc_desc", insertRequest.getI_acc_desc())
                .setParameter("i_created_by", insertRequest.getI_created_by())
                .setParameter("i_modified_by", insertRequest.getI_modified_by())
                .setParameter("i_status", insertRequest.getI_status());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_updrttacc(RefundAccountCodeRequest updateRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_updrttacc(:i_rtt_acc_id, :i_acc_cd, :i_acc_desc, :i_modified_by, :i_status)")
            .setParameter("i_rtt_acc_id", updateRequest.getI_rtt_acc_id())
            .setParameter("i_acc_cd", updateRequest.getI_acc_cd())
            .setParameter("i_acc_desc", updateRequest.getI_acc_desc())
            .setParameter("i_modified_by", updateRequest.getI_modified_by())
            .setParameter("i_status", updateRequest.getI_status());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_delrttacc(RefundAccountCodeRequest deleteRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_delrttacc(:i_rtt_acc_id, :i_modified_by, :i_status)")
            .setParameter("i_rtt_acc_id", deleteRequest.getI_rtt_acc_id())
            .setParameter("i_modified_by", deleteRequest.getI_modified_by())
            .setParameter("i_status", deleteRequest.getI_status());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    //scheduler
    @Override
    public List<Refund> findByRttStatus() {
        Query query = entityManager.createNativeQuery("SELECT * FROM rms_rtt where dt_requested is not null", Refund.class);
        return query.getResultList();
    }

    @Override
    public List<RefundWf> findByPickUpByWf() {
        Query query = entityManager.createNativeQuery("SELECT * FROM rms_rtt_wf where pickup_by is not null ", RefundWf.class);
        return query.getResultList();
    }

    @Override
    public List<RefundWfHist> findByPickUpByWfHist() {
        Query query = entityManager.createNativeQuery("SELECT * FROM rms_rtt_wf_hist where pickup_by is not null", RefundWfHist.class);
        return query.getResultList();
    }


    @Transactional
    @Override
    public Refund saveRefund(Refund refund) {
        if (refund.getRtt_id() == null) {
            entityManager.persist(refund);
            return refund;
        } else {
            return entityManager.merge(refund);
        }
    }

    @Transactional
    @Override
    public RefundWf saveRefundWf(RefundWf refundWf) {
        if (refundWf.getRtt_wf_id() == null) {
            entityManager.persist(refundWf);
            return refundWf;
        } else {
            return entityManager.merge(refundWf);
        }
    }

    @Transactional
    @Override
    public RefundWfHist saveRefundWfHist(RefundWfHist refundWfHist) {
        if (refundWfHist.getRtt_wf_hist_id() == null) {
            entityManager.persist(refundWfHist);
            return refundWfHist;
        } else {
            return entityManager.merge(refundWfHist);
        }
    }

}
