package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IFeeGroupInterface;
import com.maven.rms.interfaces.IStoreProcedureInterface;
import com.maven.rms.models.FeeGrpRequest;

@Repository
public class IFeeGroupRepository implements IFeeGroupInterface{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Integer sp_insfeegroup(FeeGrpRequest feeGroupRequest,
            String i_created_by, String i_modified_by, String i_status) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_insfeegroup(:i_fee_grp_nm_en, :i_fee_grp_nm_bm, :i_created_by, :i_modified_by, :i_status,:i_ss_cd,:i_ss_fee_grp_id)")
                .setParameter("i_fee_grp_nm_en", feeGroupRequest.getI_fee_grp_nm_en())
                .setParameter("i_fee_grp_nm_bm", feeGroupRequest.getI_fee_grp_nm_bm())
                .setParameter("i_created_by", i_created_by)
                .setParameter("i_modified_by", i_modified_by)
                .setParameter("i_status", i_status)
                .setParameter("i_ss_cd", feeGroupRequest.getI_ss_cd())
                .setParameter("i_ss_fee_grp_id", feeGroupRequest.getI_ss_fee_grp_id());
        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_updfeegroup(FeeGrpRequest feeGroupRequest, String i_fee_grp_nm_en, String i_fee_grp_nm_bm,
            String i_modified_by, String i_status) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_updfeegroup(:i_fee_grp_id, :i_fee_grp_nm_en, :i_fee_grp_nm_bm, :i_modified_by, :i_status,:i_ss_cd,:i_ss_fee_grp_id)")
                .setParameter("i_fee_grp_id", feeGroupRequest.getI_fee_grp_id())
                .setParameter("i_fee_grp_nm_en", i_fee_grp_nm_en)
                .setParameter("i_fee_grp_nm_bm", i_fee_grp_nm_bm)
                .setParameter("i_modified_by", i_modified_by)
                .setParameter("i_status", i_status)
                .setParameter("i_ss_cd", feeGroupRequest.getI_ss_cd())
                .setParameter("i_ss_fee_grp_id", feeGroupRequest.getI_ss_fee_grp_id());
        return (Integer) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_getfeegroup_v2(FeeGrpRequest feeGroupRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfeegroup_v2(:i_page, :i_size,:i_ss_cd,:i_ss_fee_grp_id,:i_fee_grp_nm_en, :i_fee_grp_nm_bm, :i_modified_by, :i_dt_modified_fr, :i_dt_modified_to, :i_status)")
                .setParameter("i_page", feeGroupRequest.getI_page())
                .setParameter("i_size", feeGroupRequest.getI_size())
                // .setParameter("i_fee_grp_id", i_fee_grp_id)
                .setParameter("i_ss_cd", feeGroupRequest.getI_ss_cd())
                .setParameter("i_ss_fee_grp_id", feeGroupRequest.getI_ss_fee_grp_id())
                .setParameter("i_fee_grp_nm_en", feeGroupRequest.getI_fee_grp_nm_en())
                .setParameter("i_fee_grp_nm_bm", feeGroupRequest.getI_fee_grp_nm_bm())
                .setParameter("i_modified_by", feeGroupRequest.getI_modified_by())
                .setParameter("i_dt_modified_fr",
                        feeGroupRequest.getI_dt_modified_fr() != null ? feeGroupRequest.getI_dt_modified_fr() : null)
                .setParameter("i_dt_modified_to",
                        feeGroupRequest.getI_dt_modified_to() != null ? feeGroupRequest.getI_dt_modified_to() : null)
                .setParameter("i_status", feeGroupRequest.getI_status());

        // Check if the dates are null and set accordingly
        if (feeGroupRequest.getI_dt_modified_fr() != null) {
            query.setParameter("i_dt_modified_fr", feeGroupRequest.getI_dt_modified_fr());
        } else {
            query.setParameter("i_dt_modified_fr", null);
        }

        if (feeGroupRequest.getI_dt_modified_to() != null) {
            query.setParameter("i_dt_modified_to", feeGroupRequest.getI_dt_modified_to());
        } else {
            query.setParameter("i_dt_modified_to", null);
        }
        return query.getResultList();
    }

    @Override
    public Integer sp_checkfeegrpbyid(FeeGrpRequest feeGroupRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_checkfeetaxbyid(:i_fee_grp_id,:i_tax_cd_id)")
                .setParameter("i_fee_grp_id", feeGroupRequest.getI_fee_grp_id())
                .setParameter("i_tax_cd_id", "");
        return (Integer) query.getSingleResult();
    }
    // #endregion
}
