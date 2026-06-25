package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IMFTWFInterface;
import com.maven.rms.models.MFTRequest;
import com.maven.rms.models.MFTWFDocRequest;
import com.maven.rms.models.MFTWFHistoryRequest;
import com.maven.rms.models.MFTWFRequest;
import com.maven.rms.models.PGRecon;
import com.maven.rms.models.PGReconUploadRequest;

@Repository
public class MFTWFRepository implements IMFTWFInterface {

    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public Integer sp_uploadDoc(MFTWFDocRequest mftwfDocRequest, Blob blob) {
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insmftwfdoc");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_wf_id", BigInteger.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_nm", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_content", Blob.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_type", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_size", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_created_by", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_modified_by", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_status", String.class, javax.persistence.ParameterMode.IN);

        // Set parameters
        storedProcedureQuery.setParameter("i_wf_id", mftwfDocRequest.getI_wf_id());
        storedProcedureQuery.setParameter("i_file_nm", mftwfDocRequest.getI_file_nm());
        storedProcedureQuery.setParameter("i_file_content", blob);
        storedProcedureQuery.setParameter("i_file_type", mftwfDocRequest.getI_file_type());
        storedProcedureQuery.setParameter("i_file_size", mftwfDocRequest.getI_file_size_kb());
        storedProcedureQuery.setParameter("i_created_by", mftwfDocRequest.getI_created_by());
        storedProcedureQuery.setParameter("i_modified_by", mftwfDocRequest.getI_modified_by());
        storedProcedureQuery.setParameter("i_status", mftwfDocRequest.getI_status());
      
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

    public List<Object[]> sp_getMFTWFByStatusAndEffDate(String status) {
        Query query = entityManager.createNativeQuery("CALL sp_getMFTWFByStatusAndEffDate(:i_status)")
                .setParameter("i_status", status);
        return query.getResultList();
    }

    // public Integer sp_updateMFTWFStatus(BigInteger i_wf_id, String i_status) {
    public Integer sp_updateMFTWFStatus(MFTWFRequest mftwfRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_updmftwfstatus(:i_wf_id, :i_status)")
                .setParameter("i_wf_id", mftwfRequest.getI_wf_id())
                .setParameter("i_status", mftwfRequest.getI_status());
        // .setParameter("i_wf_id", i_wf_id)
        // .setParameter("i_status", i_status);
        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_updmftwf_status(MFTWFRequest mftwfRequest) {
        Query query = entityManager
                .createNativeQuery("CALL sp_updmftwf_status(:i_wf_id, :i_assign_to, :i_status, :i_remark, :i_modified_by)")
                .setParameter("i_wf_id", mftwfRequest.getI_wf_id())
                .setParameter("i_assign_to", mftwfRequest.getI_assign_to())
                .setParameter("i_status", mftwfRequest.getI_status())
                .setParameter("i_remark", mftwfRequest.getI_remark() != null ? mftwfRequest.getI_remark() : null)
                .setParameter("i_modified_by", mftwfRequest.getI_modified_by());

        if (mftwfRequest.getI_remark() != null) {
            query.setParameter("i_remark", mftwfRequest.getI_remark());
        } else {
            query.setParameter("i_remark", null);
        }

        return (Integer) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_getmftwf(MFTWFRequest mftwfRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getmftwf(:i_page, :i_size, :i_wf_id, :i_fee_detail_pk, :i_fee_detail_id, :i_assign_to, :i_status , :i_created_by, :i_created_by_nm, :i_modified_by, :i_modified_by_nm, :i_dt_modified_fr, :i_dt_modified_to, :i_dt_created_fr, :i_dt_created_to, :i_dt_effective_fr, :i_dt_effective_to, :i_ss_cd, :i_wf_is_in_prg)")
                .setParameter("i_page", mftwfRequest.getI_page())
                .setParameter("i_size", mftwfRequest.getI_size())
                .setParameter("i_wf_id", mftwfRequest.getI_wf_id())
                .setParameter("i_fee_detail_pk", mftwfRequest.getI_fee_detail_pk())
                .setParameter("i_fee_detail_id", mftwfRequest.getI_fee_detail_id())
                .setParameter("i_assign_to", mftwfRequest.getI_assign_to())
                .setParameter("i_status", mftwfRequest.getI_status())
                .setParameter("i_created_by", mftwfRequest.getI_created_by())
                .setParameter("i_created_by_nm", mftwfRequest.getI_created_by_nm())
                .setParameter("i_modified_by", mftwfRequest.getI_modified_by())
                .setParameter("i_modified_by_nm", mftwfRequest.getI_modified_by_nm())
                .setParameter("i_dt_modified_fr", mftwfRequest.getI_dt_modified_fr())
                .setParameter("i_dt_modified_to", mftwfRequest.getI_dt_modified_to())
                .setParameter("i_dt_created_fr", mftwfRequest.getI_dt_created_fr())
                .setParameter("i_dt_created_to", mftwfRequest.getI_dt_created_to())
                .setParameter("i_dt_effective_fr", mftwfRequest.getI_dt_effective_fr())
                .setParameter("i_dt_effective_to", mftwfRequest.getI_dt_effective_to())
                .setParameter("i_ss_cd", mftwfRequest.getI_ss_cd())
                .setParameter("i_wf_is_in_prg", mftwfRequest.getI_wf_is_in_prg());
        return query.getResultList();
    }

    @Override
    public BigInteger sp_insmftwf(MFTWFRequest mftwfRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_insmftwf(:i_fee_detail_pk, :i_fee_detail_id, :i_fee_grp_id, :i_fee_detail_nm_e, :i_fee_detail_nm_b, :i_fee_amt, :i_promo_startdt, :i_promo_enddt, :i_promo_fee, :i_tax_cd_id, :i_allow_otc, :i_ll_parent_id, :i_ll_start_day, :i_ll_start_mth, :i_ll_end_day, :i_ll_end_mth, :i_ledger_cd, :i_ss_cd, :i_created_by, :i_modified_by, :i_status, :i_effective_date, :i_remark, :i_assign_to, :i_action, :i_r_fee_det_nm, :i_r_fee_amt, :i_r_ss_cd, :i_r_promo_startdt, :i_r_promo_enddt, :i_r_ll_required, :i_r_add_notes, :i_mft_status, :i_r_promo_fee, :i_ispub)")
                .setParameter("i_fee_detail_pk", mftwfRequest.getI_fee_detail_pk() != null ? mftwfRequest.getI_fee_detail_pk() : null)
                .setParameter("i_fee_detail_id", mftwfRequest.getI_fee_detail_id())
                .setParameter("i_fee_grp_id", mftwfRequest.getI_fee_grp_id())
                .setParameter("i_fee_detail_nm_e", mftwfRequest.getI_fee_detail_nm_e())
                .setParameter("i_fee_detail_nm_b", mftwfRequest.getI_fee_detail_nm_b())
                .setParameter("i_fee_amt", mftwfRequest.getI_fee_amt())
                .setParameter("i_promo_startdt", mftwfRequest.getI_promo_startdt() != null ? mftwfRequest.getI_promo_startdt() : null)
                .setParameter("i_promo_enddt", mftwfRequest.getI_promo_enddt() != null ? mftwfRequest.getI_promo_enddt() : null)
                .setParameter("i_promo_fee", mftwfRequest.getI_promo_fee() != null ? mftwfRequest.getI_promo_fee() : null)
                .setParameter("i_tax_cd_id", mftwfRequest.getI_tax_cd_id())
                .setParameter("i_allow_otc", mftwfRequest.getI_allow_otc())
                .setParameter("i_ll_parent_id", mftwfRequest.getI_ll_parent_id() != null ? mftwfRequest.getI_ll_parent_id() : null)
                .setParameter("i_ll_start_day", mftwfRequest.getI_ll_start_day() != null ? mftwfRequest.getI_ll_start_day() : null)
                .setParameter("i_ll_start_mth", mftwfRequest.getI_ll_start_mth() != null ? mftwfRequest.getI_ll_start_mth() : null)
                .setParameter("i_ll_end_day", mftwfRequest.getI_ll_end_day() != null ? mftwfRequest.getI_ll_end_day() : null)
                .setParameter("i_ll_end_mth", mftwfRequest.getI_ll_end_mth() != null ? mftwfRequest.getI_ll_end_mth() : null)
                .setParameter("i_ledger_cd", mftwfRequest.getI_ledger_cd())
                .setParameter("i_ss_cd", mftwfRequest.getI_ss_cd())
                .setParameter("i_created_by", mftwfRequest.getI_created_by())
                .setParameter("i_modified_by", mftwfRequest.getI_modified_by())
                .setParameter("i_status", mftwfRequest.getI_status())
                .setParameter("i_effective_date", mftwfRequest.getI_effective_date())
                .setParameter("i_remark", mftwfRequest.getI_remark() != null ? mftwfRequest.getI_remark() : null)
                .setParameter("i_assign_to", mftwfRequest.getI_assign_to())
                .setParameter("i_action", mftwfRequest.getI_action())
                .setParameter("i_r_fee_det_nm", mftwfRequest.getI_r_fee_det_nm() != null ? mftwfRequest.getI_r_fee_det_nm() : null)
                .setParameter("i_r_fee_amt", mftwfRequest.getI_r_fee_amt() != null ? mftwfRequest.getI_r_fee_amt() : null)
                .setParameter("i_r_ss_cd", mftwfRequest.getI_r_ss_cd() != null ? mftwfRequest.getI_r_ss_cd() : null)
                .setParameter("i_r_promo_startdt", mftwfRequest.getI_r_promo_startdt() != null ? mftwfRequest.getI_r_promo_startdt() : null)
                .setParameter("i_r_promo_enddt", mftwfRequest.getI_r_promo_enddt() != null ? mftwfRequest.getI_r_promo_enddt() : null)
                .setParameter("i_r_ll_required", mftwfRequest.getI_r_ll_required() != null ? mftwfRequest.getI_r_ll_required() : null)
                .setParameter("i_r_add_notes", mftwfRequest.getI_r_add_notes() != null ? mftwfRequest.getI_r_add_notes() : null)
                .setParameter("i_mft_status", mftwfRequest.getI_mft_status() != null ? mftwfRequest.getI_mft_status() : null)
                .setParameter("i_r_promo_fee", mftwfRequest.getI_r_promo_fee() != null ? mftwfRequest.getI_r_promo_fee() : null)
                .setParameter("i_ispub", mftwfRequest.getI_ispub());

        // Check if the dates are null and set accordingly
        if (mftwfRequest.getI_fee_detail_pk() != null) {
            query.setParameter("i_fee_detail_pk", mftwfRequest.getI_fee_detail_pk());
        } else {
            query.setParameter("i_fee_detail_pk", null);
        }

        if (mftwfRequest.getI_promo_startdt() != null) {
            query.setParameter("i_promo_startdt", mftwfRequest.getI_promo_startdt());
        } else {
            query.setParameter("i_promo_startdt", null);
        }

        if (mftwfRequest.getI_promo_enddt() != null) {
            query.setParameter("i_promo_enddt", mftwfRequest.getI_promo_enddt());
        } else {
            query.setParameter("i_promo_enddt", null);
        }

        if (mftwfRequest.getI_promo_fee() != null) {
            query.setParameter("i_promo_fee", mftwfRequest.getI_promo_fee());
        } else {
            query.setParameter("i_promo_fee", null);
        }

        if (mftwfRequest.getI_ll_parent_id() != null) {
            query.setParameter("i_ll_parent_id", mftwfRequest.getI_ll_parent_id());
        } else {
            query.setParameter("i_ll_parent_id", null);
        }

        if (mftwfRequest.getI_ll_start_day() != null) {
            query.setParameter("i_ll_start_day", mftwfRequest.getI_ll_start_day());
        } else {
            query.setParameter("i_ll_start_day", null);
        }

        if (mftwfRequest.getI_ll_start_mth() != null) {
            query.setParameter("i_ll_start_mth", mftwfRequest.getI_ll_start_mth());
        } else {
            query.setParameter("i_ll_start_mth", null);
        }

        if (mftwfRequest.getI_ll_end_day() != null) {
            query.setParameter("i_ll_end_day", mftwfRequest.getI_ll_end_day());
        } else {
            query.setParameter("i_ll_end_day", null);
        }

        if (mftwfRequest.getI_ll_end_mth() != null) {
            query.setParameter("i_ll_end_mth", mftwfRequest.getI_ll_end_mth());
        } else {
            query.setParameter("i_ll_end_mth", null);
        }

        if (mftwfRequest.getI_remark() != null) {
            query.setParameter("i_remark", mftwfRequest.getI_remark());
        } else {
            query.setParameter("i_remark", null);
        }

        if (mftwfRequest.getI_r_fee_det_nm() != null) {
            query.setParameter("i_r_fee_det_nm", mftwfRequest.getI_r_fee_det_nm());
        } else {
            query.setParameter("i_r_fee_det_nm", null);
        }

        if (mftwfRequest.getI_r_fee_amt() != null) {
            query.setParameter("i_r_fee_amt", mftwfRequest.getI_r_fee_amt());
        } else {
            query.setParameter("i_r_fee_amt", null);
        }

        if (mftwfRequest.getI_r_ss_cd() != null) {
            query.setParameter("i_r_ss_cd", mftwfRequest.getI_r_ss_cd());
        } else {
            query.setParameter("i_r_ss_cd", null);
        }

        if (mftwfRequest.getI_r_promo_startdt() != null) {
            query.setParameter("i_r_promo_startdt", mftwfRequest.getI_r_promo_startdt());
        } else {
            query.setParameter("i_r_promo_startdt", null);
        }

        if (mftwfRequest.getI_r_promo_enddt() != null) {
            query.setParameter("i_r_promo_enddt", mftwfRequest.getI_r_promo_enddt());
        } else {
            query.setParameter("i_r_promo_enddt", null);
        }

        if (mftwfRequest.getI_r_ll_required() != null) {
            query.setParameter("i_r_ll_required", mftwfRequest.getI_r_ll_required());
        } else {
            query.setParameter("i_r_ll_required", null);
        }

        if (mftwfRequest.getI_r_add_notes() != null) {
            query.setParameter("i_r_add_notes", mftwfRequest.getI_r_add_notes());
        } else {
            query.setParameter("i_r_add_notes", null);
        }

        if (mftwfRequest.getI_mft_status() != null) {
            query.setParameter("i_mft_status", mftwfRequest.getI_mft_status());
        } else {
            query.setParameter("i_mft_status", null);
        }

        if (mftwfRequest.getI_r_promo_fee() != null) {
            query.setParameter("i_r_promo_fee", mftwfRequest.getI_r_promo_fee());
        } else {
            query.setParameter("i_r_promo_fee", null);
        }

        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
    }

    @Override
    public List<Object[]> sp_getmftwfhis(MFTWFHistoryRequest mftwfHistoryRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getmftwfhis(:i_page, :i_size, :i_wf_id,  :i_status )")
                .setParameter("i_page", mftwfHistoryRequest.getI_page())
                .setParameter("i_size", mftwfHistoryRequest.getI_size())
                .setParameter("i_wf_id", mftwfHistoryRequest.getI_wf_id())
                .setParameter("i_status", mftwfHistoryRequest.getI_status());
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getwfh_ast(MFTWFHistoryRequest mftwfHistoryRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getwfh_ast(:i_task_id, :i_status)")
                .setParameter("i_task_id", mftwfHistoryRequest.getI_task_id())
                .setParameter("i_status", mftwfHistoryRequest.getI_status());
        return query.getResultList();

    }

    @Override
    public Integer sp_updmftwf(MFTWFRequest mftwfRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_updmftwf(:i_wf_id, :i_fee_detail_pk, :i_fee_detail_id, :i_fee_grp_id, :i_fee_detail_nm_e, :i_fee_detail_nm_b, :i_fee_amt, :i_promo_startdt, :i_promo_enddt, :i_promo_fee, :i_tax_cd_id, :i_allow_otc, :i_ll_parent_id, :i_ll_start_day, :i_ll_end_day, :i_ll_start_mth,  :i_ll_end_mth, :i_ledger_cd, :i_ss_cd, :i_effective_date, :i_modified_by, :i_status, :i_assign_to, :i_remark, :i_action, :i_r_fee_det_nm, :i_r_fee_amt, :i_r_ss_cd, :i_r_promo_startdt, :i_r_promo_enddt, :i_r_ll_required, :i_r_add_notes, :i_mft_status, :i_r_promo_fee, :i_ispub)")
                .setParameter("i_wf_id", mftwfRequest.getI_wf_id())
                .setParameter("i_fee_detail_pk", mftwfRequest.getI_fee_detail_pk() != null ? mftwfRequest.getI_fee_detail_pk() : null)
                .setParameter("i_fee_detail_id", mftwfRequest.getI_fee_detail_id())
                .setParameter("i_fee_grp_id", mftwfRequest.getI_fee_grp_id())
                .setParameter("i_fee_detail_nm_e", mftwfRequest.getI_fee_detail_nm_e())
                .setParameter("i_fee_detail_nm_b", mftwfRequest.getI_fee_detail_nm_b())
                .setParameter("i_fee_amt", mftwfRequest.getI_fee_amt())
                .setParameter("i_promo_startdt", mftwfRequest.getI_promo_startdt() != null ? mftwfRequest.getI_promo_startdt() : null)
                .setParameter("i_promo_enddt", mftwfRequest.getI_promo_enddt() != null ? mftwfRequest.getI_promo_enddt() : null)
                .setParameter("i_promo_fee", mftwfRequest.getI_promo_fee() != null ? mftwfRequest.getI_promo_fee() : null)
                .setParameter("i_tax_cd_id", mftwfRequest.getI_tax_cd_id())
                .setParameter("i_allow_otc", mftwfRequest.getI_allow_otc())
                .setParameter("i_ll_parent_id", mftwfRequest.getI_ll_parent_id() != null ? mftwfRequest.getI_ll_parent_id() : null)
                .setParameter("i_ll_start_day", mftwfRequest.getI_ll_start_day() != null ? mftwfRequest.getI_ll_start_day() : null)
                .setParameter("i_ll_end_day", mftwfRequest.getI_ll_end_day() != null ? mftwfRequest.getI_ll_end_day() : null)
                .setParameter("i_ll_start_mth", mftwfRequest.getI_ll_start_mth() != null ? mftwfRequest.getI_ll_start_mth() : null)
                .setParameter("i_ll_end_mth", mftwfRequest.getI_ll_end_mth() != null ? mftwfRequest.getI_ll_end_mth() : null)
                .setParameter("i_ledger_cd", mftwfRequest.getI_ledger_cd())
                .setParameter("i_ss_cd", mftwfRequest.getI_ss_cd())
                .setParameter("i_effective_date", mftwfRequest.getI_effective_date())
                .setParameter("i_modified_by", mftwfRequest.getI_modified_by())
                .setParameter("i_status", mftwfRequest.getI_status())
                .setParameter("i_assign_to", mftwfRequest.getI_assign_to() != null ? mftwfRequest.getI_assign_to() : null)
                .setParameter("i_remark", mftwfRequest.getI_remark())
                .setParameter("i_action", mftwfRequest.getI_action())
                .setParameter("i_r_fee_det_nm", mftwfRequest.getI_r_fee_det_nm() != null ? mftwfRequest.getI_r_fee_det_nm() : null)
                .setParameter("i_r_fee_amt", mftwfRequest.getI_r_fee_amt() != null ? mftwfRequest.getI_r_fee_amt() : null)
                .setParameter("i_r_ss_cd", mftwfRequest.getI_r_ss_cd() != null ? mftwfRequest.getI_r_ss_cd() : null)
                .setParameter("i_r_promo_startdt", mftwfRequest.getI_r_promo_startdt() != null ? mftwfRequest.getI_r_promo_startdt() : null)
                .setParameter("i_r_promo_enddt", mftwfRequest.getI_r_promo_enddt() != null ? mftwfRequest.getI_r_promo_enddt() : null)
                .setParameter("i_r_ll_required", mftwfRequest.getI_r_ll_required() != null ? mftwfRequest.getI_r_ll_required() : null)
                .setParameter("i_r_add_notes", mftwfRequest.getI_r_add_notes() != null ? mftwfRequest.getI_r_add_notes() : null)
                .setParameter("i_mft_status", mftwfRequest.getI_mft_status() != null ? mftwfRequest.getI_mft_status() : null)
                .setParameter("i_r_promo_fee", mftwfRequest.getI_r_promo_fee() != null ? mftwfRequest.getI_r_promo_fee() : null)
                .setParameter("i_ispub", mftwfRequest.getI_ispub());

        // Check if the dates are null and set accordingly
        if (mftwfRequest.getI_fee_detail_pk() != null) {
            query.setParameter("i_fee_detail_pk", mftwfRequest.getI_fee_detail_pk());
        } else {
            query.setParameter("i_fee_detail_pk", null);
        }

        if (mftwfRequest.getI_promo_startdt() != null) {
            query.setParameter("i_promo_startdt", mftwfRequest.getI_promo_startdt());
        } else {
            query.setParameter("i_promo_startdt", null);
        }

        if (mftwfRequest.getI_promo_enddt() != null) {
            query.setParameter("i_promo_enddt", mftwfRequest.getI_promo_enddt());
        } else {
            query.setParameter("i_promo_enddt", null);
        }

        if (mftwfRequest.getI_promo_fee() != null) {
            query.setParameter("i_promo_fee", mftwfRequest.getI_promo_fee());
        } else {
            query.setParameter("i_promo_fee", null);
        }

        if (mftwfRequest.getI_ll_parent_id() != null) {
            query.setParameter("i_ll_parent_id", mftwfRequest.getI_ll_parent_id());
        } else {
            query.setParameter("i_ll_parent_id", null);
        }

        if (mftwfRequest.getI_ll_start_day() != null) {
            query.setParameter("i_ll_start_day", mftwfRequest.getI_ll_start_day());
        } else {
            query.setParameter("i_ll_start_day", null);
        }

        if (mftwfRequest.getI_ll_start_mth() != null) {
            query.setParameter("i_ll_start_mth", mftwfRequest.getI_ll_start_mth());
        } else {
            query.setParameter("i_ll_start_mth", null);
        }

        if (mftwfRequest.getI_ll_end_day() != null) {
            query.setParameter("i_ll_end_day", mftwfRequest.getI_ll_end_day());
        } else {
            query.setParameter("i_ll_end_day", null);
        }

        if (mftwfRequest.getI_ll_end_mth() != null) {
            query.setParameter("i_ll_end_mth", mftwfRequest.getI_ll_end_mth());
        } else {
            query.setParameter("i_ll_end_mth", null);
        }

        if (mftwfRequest.getI_assign_to() != null) {
            query.setParameter("i_assign_to", mftwfRequest.getI_assign_to());
        } else {
            query.setParameter("i_assign_to", null);
        }

        if (mftwfRequest.getI_r_fee_det_nm() != null) {
            query.setParameter("i_r_fee_det_nm", mftwfRequest.getI_r_fee_det_nm());
        } else {
            query.setParameter("i_r_fee_det_nm", null);
        }

        if (mftwfRequest.getI_r_fee_amt() != null) {
            query.setParameter("i_r_fee_amt", mftwfRequest.getI_r_fee_amt());
        } else {
            query.setParameter("i_r_fee_amt", null);
        }

        if (mftwfRequest.getI_r_ss_cd() != null) {
            query.setParameter("i_r_ss_cd", mftwfRequest.getI_r_ss_cd());
        } else {
            query.setParameter("i_r_ss_cd", null);
        }

        if (mftwfRequest.getI_r_promo_startdt() != null) {
            query.setParameter("i_r_promo_startdt", mftwfRequest.getI_r_promo_startdt());
        } else {
            query.setParameter("i_r_promo_startdt", null);
        }

        if (mftwfRequest.getI_r_promo_enddt() != null) {
            query.setParameter("i_r_promo_enddt", mftwfRequest.getI_r_promo_enddt());
        } else {
            query.setParameter("i_r_promo_enddt", null);
        }

        if (mftwfRequest.getI_r_ll_required() != null) {
            query.setParameter("i_r_ll_required", mftwfRequest.getI_r_ll_required());
        } else {
            query.setParameter("i_r_ll_required", null);
        }

        if (mftwfRequest.getI_r_add_notes() != null) {
            query.setParameter("i_r_add_notes", mftwfRequest.getI_r_add_notes());
        } else {
            query.setParameter("i_r_add_notes", null);
        }

        if (mftwfRequest.getI_mft_status() != null) {
            query.setParameter("i_mft_status", mftwfRequest.getI_mft_status());
        } else {
            query.setParameter("i_mft_status", null);
        }

        if (mftwfRequest.getI_r_promo_fee() != null) {
            query.setParameter("i_r_promo_fee", mftwfRequest.getI_r_promo_fee());
        } else {
            query.setParameter("i_r_promo_fee", null);
        }

        return (Integer) query.getSingleResult();

    }

    // @Override
    // public Integer sp_insmftwfdoc(BigInteger i_wf_id, String i_file_nm, Blob
    // i_file_content, String i_file_type,
    // Integer i_file_size,
    // String i_created_by, String i_modified_by, String i_status) {
    // Query query = entityManager.createNativeQuery(
    // "CALL sp_insmftwfdoc(:i_wf_id, :i_file_nm, :i_file_content, :i_file_type,
    // :i_file_size, :i_created_by, :i_modified_by, :i_status)")
    // .setParameter("i_wf_id", i_wf_id)
    // .setParameter("i_file_nm", i_file_nm)
    // .setParameter("i_file_content", i_file_content)
    // .setParameter("i_file_type", i_file_type)
    // .setParameter("i_file_size", i_file_size)
    // .setParameter("i_created_by", i_created_by)
    // .setParameter("i_modified_by", i_modified_by)
    // .setParameter("i_status", i_status);
    // return (Integer) query.getSingleResult();
    // }

    @Override
    public List<Object[]> sp_getmftwfdoc(MFTWFDocRequest mftwfDocRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getmftwfdoc(:i_page, :i_size, :i_wf_id, :i_status)")
                .setParameter("i_page", mftwfDocRequest.getI_page())
                .setParameter("i_size", mftwfDocRequest.getI_size())
                .setParameter("i_wf_id", mftwfDocRequest.getI_wf_id())
                .setParameter("i_status", mftwfDocRequest.getI_status());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getwfh_status(MFTWFHistoryRequest mftwfHistoryRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getwfh_status(:i_task_id, :i_status)")
                .setParameter("i_task_id", mftwfHistoryRequest.getI_task_id())
                .setParameter("i_status", mftwfHistoryRequest.getI_status());
        return query.getResultList();

    }

    @Override
    public Blob sp_getmftwfdocfilecontent(MFTWFDocRequest mftwfDocRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getmftwfdocfilecontent(:i_wfdoc_id)")
                .setParameter("i_wfdoc_id", mftwfDocRequest.getI_wfdoc_id());

        return (Blob) query.getSingleResult();
    }

    //@Override
    public Integer sp_getmytaskactivetaskcount(MFTWFRequest mftwfRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getmytaskactivetaskcount(:i_assign_to)")
             .setParameter("i_assign_to", mftwfRequest.getI_assign_to());

        return (Integer) query.getSingleResult();
    }


    public Integer sp_getcreatedtaskactivetaskcount(MFTWFRequest mftwfRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getcreatedtaskactivetaskcount(:i_created_by)")
             .setParameter("i_created_by", mftwfRequest.getI_created_by());

        return (Integer) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_checkmftwfexist(MFTWFRequest mftwfRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_checkmftwfexist(:i_page, :i_size, :i_fee_detail_pk, :i_fee_detail_id)")
                .setParameter("i_page", mftwfRequest.getI_page())
                .setParameter("i_size", mftwfRequest.getI_size())
                .setParameter("i_fee_detail_pk", mftwfRequest.getI_fee_detail_pk())
                .setParameter("i_fee_detail_id", mftwfRequest.getI_fee_detail_id());
        return query.getResultList();
    }

    @Override
    public Integer sp_removemftwf(BigInteger wf_id) {
        Query query = entityManager.createNativeQuery("CALL sp_removemftwf(:i_wf_id)")
             .setParameter("i_wf_id", wf_id);

        return (Integer) query.getSingleResult();
    }
}
