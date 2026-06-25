package com.maven.rms.repositories;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IDIAgingRepInterface;
import com.maven.rms.models.DeferredIncomeAgingRequest;

@Repository
public class DIAgingRepRepository implements IDIAgingRepInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BigInteger sp_insdiagingrpt(DeferredIncomeAgingRequest DIRequest, String i_p_email, String i_created_by,
                                       String i_modified_by) {
        Query query = entityManager.createNativeQuery("CALL sp_insdiagingrpt(:i_p_dt_req, :i_p_tmn_status, :i_p_ent_ty, :i_p_ent_nm, :i_p_txn_ty, :i_p_status, :i_p_dt_exp_fr, :i_p_dt_exp_to, :i_p_dt_eff_fr, :i_p_dt_eff_to, :i_p_dt_app_fr, :i_p_dt_app_to, :i_p_dt_tmn_fr, :i_p_dt_tmn_to, :i_created_by, :i_modified_by, :i_status, :i_p_email, :i_p_file_type, :i_p_file_size, :i_p_file_nm, :i_p_batch_no, :i_p_fms_ref_no)")
                .setParameter("i_p_dt_req", DIRequest.getI_p_dt_req())
                .setParameter("i_p_tmn_status", DIRequest.getI_p_tmn_status() != null ? DIRequest.getI_p_tmn_status() : null)
                .setParameter("i_p_ent_ty", DIRequest.getI_p_ent_ty() != null ? DIRequest.getI_p_ent_ty() : null)
                .setParameter("i_p_ent_nm", DIRequest.getI_p_ent_nm() != null ? DIRequest.getI_p_ent_nm() : null)
                .setParameter("i_p_txn_ty", DIRequest.getI_p_txn_ty() != null ? DIRequest.getI_p_txn_ty() : null)
                .setParameter("i_p_status", DIRequest.getI_p_status() != null ? DIRequest.getI_p_status() : null)
                .setParameter("i_p_dt_exp_fr", DIRequest.getI_p_dt_exp_fr() != null ? DIRequest.getI_p_dt_exp_fr() : null)
                .setParameter("i_p_dt_exp_to", DIRequest.getI_p_dt_exp_to() != null ? DIRequest.getI_p_dt_exp_to() : null)
                .setParameter("i_p_dt_eff_fr", DIRequest.getI_p_dt_eff_fr())
                .setParameter("i_p_dt_eff_to", DIRequest.getI_p_dt_eff_to())
                .setParameter("i_p_dt_app_fr", DIRequest.getI_p_dt_app_fr() != null ? DIRequest.getI_p_dt_app_fr() : null)
                .setParameter("i_p_dt_app_to", DIRequest.getI_p_dt_app_to() != null ? DIRequest.getI_p_dt_app_to() : null)
                .setParameter("i_p_dt_tmn_fr", DIRequest.getI_p_dt_tmn_fr() != null ? DIRequest.getI_p_dt_tmn_fr() : null)
                .setParameter("i_p_dt_tmn_to", DIRequest.getI_p_dt_tmn_to() != null ? DIRequest.getI_p_dt_tmn_to() : null)
                .setParameter("i_created_by", i_created_by)
                .setParameter("i_modified_by", i_modified_by)
                .setParameter("i_status", DIRequest.getI_status())
                .setParameter("i_p_email", i_p_email)
                .setParameter("i_p_file_type", DIRequest.getI_p_file_type() != null ? DIRequest.getI_p_file_type() : null)
                .setParameter("i_p_file_size", DIRequest.getI_p_file_size() != null ? DIRequest.getI_p_file_size() : null)
                .setParameter("i_p_file_nm", DIRequest.getI_p_file_nm() != null ? DIRequest.getI_p_file_nm() : null)
                .setParameter("i_p_batch_no", DIRequest.getI_p_batch_no() != null ? DIRequest.getI_p_batch_no() : null)
                .setParameter("i_p_fms_ref_no", DIRequest.getI_p_fms_ref_no() != null ? DIRequest.getI_p_fms_ref_no() : null);

        // Check if the dates are null and set accordingly
        if (DIRequest.getI_p_tmn_status() != null) {
            query.setParameter("i_p_tmn_status", DIRequest.getI_p_tmn_status());
        } else {
            query.setParameter("i_p_tmn_status", null);
        }

        if (DIRequest.getI_p_ent_ty() != null) {
            query.setParameter("i_p_ent_ty", DIRequest.getI_p_ent_ty());
        } else {
            query.setParameter("i_p_ent_ty", null);
        }

        if (DIRequest.getI_p_ent_nm() != null) {
            query.setParameter("i_p_ent_nm", DIRequest.getI_p_ent_nm());
        } else {
            query.setParameter("i_p_ent_nm", null);
        }

        if (DIRequest.getI_p_txn_ty() != null) {
            query.setParameter("i_p_txn_ty", DIRequest.getI_p_txn_ty());
        } else {
            query.setParameter("i_p_txn_ty", null);
        }

        if (DIRequest.getI_p_status() != null) {
            query.setParameter("i_p_status", DIRequest.getI_p_status());
        } else {
            query.setParameter("i_p_status", null);
        }

        if (DIRequest.getI_p_dt_eff_fr() != null) {
            query.setParameter("i_p_dt_eff_fr", DIRequest.getI_p_dt_eff_fr());
        } else {
            query.setParameter("i_p_dt_eff_fr", null);
        }

        if (DIRequest.getI_p_dt_eff_to() != null) {
            query.setParameter("i_p_dt_eff_to", DIRequest.getI_p_dt_eff_to());
        } else {
            query.setParameter("i_p_dt_eff_to", null);
        }

        if (DIRequest.getI_p_dt_app_fr() != null) {
            query.setParameter("i_p_dt_app_fr", DIRequest.getI_p_dt_app_fr());
        } else {
            query.setParameter("i_p_dt_app_fr", null);
        }

        if (DIRequest.getI_p_dt_app_to() != null) {
            query.setParameter("i_p_dt_app_to", DIRequest.getI_p_dt_app_to());
        } else {
            query.setParameter("i_p_dt_app_to", null);
        }

        if (DIRequest.getI_p_dt_tmn_fr() != null) {
            query.setParameter("i_p_dt_tmn_fr", DIRequest.getI_p_dt_tmn_fr());
        } else {
            query.setParameter("i_p_dt_tmn_fr", null);
        }

        if (DIRequest.getI_p_dt_tmn_to() != null) {
            query.setParameter("i_p_dt_tmn_to", DIRequest.getI_p_dt_tmn_to());
        } else {
            query.setParameter("i_p_dt_tmn_to", null);
        }

        if (DIRequest.getI_p_email() != null) {
            query.setParameter("i_p_email", DIRequest.getI_p_email());
        } else {
            query.setParameter("i_p_email", null);
        }

        if (DIRequest.getI_p_file_type() != null) {
            query.setParameter("i_p_file_type", DIRequest.getI_p_file_type());
        } else {
            query.setParameter("i_p_file_type", null);
        }

        if (DIRequest.getI_p_file_size() != null) {
            query.setParameter("i_p_file_size", DIRequest.getI_p_file_size());
        } else {
            query.setParameter("i_p_file_size", null);
        }

        if (DIRequest.getI_p_file_nm() != null) {
            query.setParameter("i_p_file_nm", DIRequest.getI_p_file_nm());
        } else {
            query.setParameter("i_p_file_nm", null);
        }

        if (DIRequest.getI_p_batch_no() != null) {
            query.setParameter("i_p_batch_no", DIRequest.getI_p_batch_no());
        } else {
            query.setParameter("i_p_batch_no", null);
        }

        if (DIRequest.getI_p_fms_ref_no() != null) {
            query.setParameter("i_p_fms_ref_no", DIRequest.getI_p_fms_ref_no());
        } else {
            query.setParameter("i_p_fms_ref_no", null);
        }

        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
    }

    @Override
    public List<Object[]> sp_getdiaginglistingrpt(DeferredIncomeAgingRequest DIRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getdiaginglistingrpt(:i_page, :i_size, :i_rpt_di_age_id, :i_p_dt_req, :i_p_tmn_status, :i_p_ent_ty, :i_p_ent_nm, :i_p_txn_ty, :i_p_status, :i_p_dt_exp_fr, :i_p_dt_exp_to, :i_p_dt_eff_fr, :i_p_dt_eff_to, :i_p_dt_app_fr, :i_p_dt_app_to, :i_p_dt_tmn_fr, :i_p_dt_tmn_to, :i_created_by, :i_modified_by, :i_status, :i_p_email, :i_p_file_type, :i_p_file_size, :i_p_file_nm, :i_p_batch_no, :i_p_fms_ref_no)")
                .setParameter("i_page", DIRequest.getI_page())
                .setParameter("i_size", DIRequest.getI_size())
                .setParameter("i_rpt_di_age_id", DIRequest.getI_rpt_di_age_id())
                .setParameter("i_p_dt_req", DIRequest.getI_p_dt_req())
                .setParameter("i_p_tmn_status", DIRequest.getI_p_tmn_status())
                .setParameter("i_p_ent_ty", DIRequest.getI_p_ent_ty())
                .setParameter("i_p_ent_nm", DIRequest.getI_p_ent_nm())
                .setParameter("i_p_txn_ty", DIRequest.getI_p_txn_ty())
                .setParameter("i_p_status", DIRequest.getI_p_status())
                .setParameter("i_p_dt_exp_fr", DIRequest.getI_p_dt_exp_fr())
                .setParameter("i_p_dt_exp_to", DIRequest.getI_p_dt_exp_to())
                .setParameter("i_p_dt_eff_fr", DIRequest.getI_p_dt_eff_fr())
                .setParameter("i_p_dt_eff_to", DIRequest.getI_p_dt_eff_to())
                .setParameter("i_p_dt_app_fr", DIRequest.getI_p_dt_app_fr())
                .setParameter("i_p_dt_app_to", DIRequest.getI_p_dt_app_to())
                .setParameter("i_p_dt_tmn_fr", DIRequest.getI_p_dt_tmn_fr())
                .setParameter("i_p_dt_tmn_to", DIRequest.getI_p_dt_tmn_to())
                .setParameter("i_created_by", DIRequest.getI_created_by())
                .setParameter("i_modified_by", DIRequest.getI_modified_by())
                .setParameter("i_status", DIRequest.getI_status())
                .setParameter("i_p_email", DIRequest.getI_p_email())
                .setParameter("i_p_file_type", DIRequest.getI_p_file_type())
                .setParameter("i_p_file_size", DIRequest.getI_p_file_size())
                .setParameter("i_p_file_nm", DIRequest.getI_p_file_nm())
                .setParameter("i_p_batch_no", DIRequest.getI_p_batch_no())
                .setParameter("i_p_fms_ref_no", DIRequest.getI_p_fms_ref_no());

        return query.getResultList();
    }

    @Override
    public Integer sp_upddiagingrpt(DeferredIncomeAgingRequest DIRequest, String i_modified_by) {
        Query query = entityManager.createNativeQuery("CALL sp_upddiagingrpt(:i_rpt_di_age_id, :i_status, :i_p_file_size, :i_p_file_nm, :i_modified_by)")
                .setParameter("i_rpt_di_age_id", DIRequest.getI_rpt_di_age_id())
                .setParameter("i_status", DIRequest.getI_status())
                .setParameter("i_p_file_size", DIRequest.getI_p_file_size() != null ? DIRequest.getI_p_file_size() : null)
                .setParameter("i_p_file_nm", DIRequest.getI_p_file_nm() != null ? DIRequest.getI_p_file_nm() : null)
                .setParameter("i_modified_by", i_modified_by);

        if (DIRequest.getI_p_file_size() != null) {
            query.setParameter("i_p_file_size", DIRequest.getI_p_file_size());
        } else {
            query.setParameter("i_p_file_size", null);
        }

        if (DIRequest.getI_p_file_nm() != null) {
            query.setParameter("i_p_file_nm", DIRequest.getI_p_file_nm());
        } else {
            query.setParameter("i_p_file_nm", null);
        }

        return (Integer) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_getdiagingrpt(BigInteger i_rpt_di_age_id) {
        Query query = entityManager.createNativeQuery("CALL sp_getdiagingrpt(:i_rpt_di_age_id)")
                .setParameter("i_rpt_di_age_id", i_rpt_di_age_id);

        return query.getResultList();
    }

    @Override
    public Integer sp_getdiagequeuerpt() {
        Query query = entityManager.createNativeQuery("CALL sp_getdiagequeuerpt()");

        return (Integer) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_getpendingdiagingrpt() {
        Query query = entityManager.createNativeQuery("CALL sp_getpendingdiagingrpt()");

        return query.getResultList();
    }

    @Override
    public Integer sp_getpendingdiagingrptbyid(BigInteger i_rpt_di_age_id) {
        Query query = entityManager.createNativeQuery("CALL sp_getpendingdiagingrptbyid(:i_rpt_di_age_id)")
                .setParameter("i_rpt_di_age_id", i_rpt_di_age_id);

        return (Integer) query.getSingleResult();
    }
}
