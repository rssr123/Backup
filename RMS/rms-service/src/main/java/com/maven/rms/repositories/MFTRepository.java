package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IMFTInterface;
import com.maven.rms.models.FeeDetailItemsRequest;
import com.maven.rms.models.MFT;
import com.maven.rms.models.MFTRequest;
import com.maven.rms.models.MFTTypeSearchRequest;

@Repository
public class MFTRepository implements IMFTInterface {


    @PersistenceContext
    private EntityManager entityManager;
    @Override
    public List<Object[]> sp_getmft(MFTRequest mftRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_getmft(:i_page, :i_size, :i_fee_detail_pk, :i_fee_detail_id,  :i_unit_fee_fr, :i_unit_fee_to, :i_ss_cd, :i_tax_cd,  :i_dt_modified_fr,  :i_dt_modified_to ,  :i_modified_by, :i_status)")
            .setParameter("i_page", mftRequest.getI_page())
            .setParameter("i_size", mftRequest.getI_size())
            .setParameter("i_fee_detail_pk", mftRequest.getI_fee_detail_pk())
            .setParameter("i_fee_detail_id", mftRequest.getI_fee_detail_id())
            .setParameter("i_unit_fee_fr", mftRequest.getI_unit_fee_fr())
            .setParameter("i_unit_fee_to", mftRequest.getI_unit_fee_to())
            .setParameter("i_ss_cd", mftRequest.getI_ss_cd())
            .setParameter("i_tax_cd", mftRequest.getI_tax_cd())
            .setParameter("i_dt_modified_fr", mftRequest.getI_dt_modified_fr() != null ? mftRequest.getI_dt_modified_fr() : null)
            .setParameter("i_dt_modified_to", mftRequest.getI_dt_modified_to() != null ? mftRequest.getI_dt_modified_to() : null)
            .setParameter("i_modified_by", mftRequest.getI_modified_by())
            .setParameter("i_status", mftRequest.getI_status());

        // Check if the dates are null and set accordingly
        if (mftRequest.getI_dt_modified_fr() != null) {
            query.setParameter("i_dt_modified_fr", mftRequest.getI_dt_modified_fr());
        } else {
            query.setParameter("i_dt_modified_fr", null);
        }

        if (mftRequest.getI_dt_modified_to() != null) {
            query.setParameter("i_dt_modified_to", mftRequest.getI_dt_modified_to());
        } else {
            query.setParameter("i_dt_modified_to", null);
        }

        return query.getResultList();
    }

    // public List<Object[]> sp_getMftWFilter(Integer i_fee_detail_pk) {
    public List<Object[]> sp_getMftWFilter(MFTRequest mftRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getmasterfeetablewithfilter(:i_fee_detail_pk)")
                .setParameter("i_fee_detail_pk", mftRequest.getI_fee_detail_pk());
        // .setParameter("i_fee_detail_pk", i_fee_detail_pk);

        return query.getResultList();
    }
    
    public List<Object[]> sp_getMftWFilterID(MFTRequest mftRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getmasterfeetablewithfilterfeedetailid(:i_fee_detail_id)")
                .setParameter("i_fee_detail_id", mftRequest.getI_fee_detail_id());
        // .setParameter("i_fee_detail_pk", i_fee_detail_pk);

        return query.getResultList();
    }
    
    public Integer sp_insMFT(MFT mft) {
        Query query = entityManager
                .createNativeQuery("CALL sp_insmasterfeetable(:i_fee_detail_id, :i_fee_grp_id, :i_fee_detail_nm_e,"
                        + ":i_fee_detail_nm_b, :i_unit_fee, :i_promo_startdt, :i_promo_enddt, :i_promo_fee, :i_tax_cd_id,"
                        + ":i_allow_otc, :i_ll_parent_id, :i_ll_start_day, :i_ll_start_mth, :i_ll_end_day, :i_ll_end_mth,"
                        + ":i_ledger_cd, :i_ss_cd, :i_created_by, :i_modified_by, :i_status, :i_is_pub)")
                .setParameter("i_fee_detail_id", mft.getFee_detail_id())
                .setParameter("i_fee_grp_id", mft.getFee_grp_id())		//no null
                .setParameter("i_fee_detail_nm_e", mft.getFee_detail_nm_e())
                .setParameter("i_fee_detail_nm_b", mft.getFee_detail_nm_b())
                .setParameter("i_unit_fee", mft.getUnit_fee())
                .setParameter("i_promo_startdt", mft.getPromo_startdt())
                .setParameter("i_promo_enddt", mft.getPromo_enddt())
                .setParameter("i_promo_fee", mft.getPromo_fee())
                .setParameter("i_tax_cd_id", mft.getTax_cd_id())	//no null
                .setParameter("i_allow_otc", mft.getAllow_otc())
                .setParameter("i_ll_parent_id", mft.getLl_parent_id())
                .setParameter("i_ll_start_day", mft.getLl_start_day())
                .setParameter("i_ll_start_mth", mft.getLl_start_mth())
                .setParameter("i_ll_end_day", mft.getLl_end_day())
                .setParameter("i_ll_end_mth", mft.getLl_end_mth())
                .setParameter("i_ledger_cd", mft.getLedger_cd())	//no null
                .setParameter("i_ss_cd", mft.getSs_cd())			//no null
                .setParameter("i_created_by", mft.getCreated_by())
                .setParameter("i_modified_by", mft.getModified_by())
                .setParameter("i_status", mft.getStatus())
                .setParameter("i_is_pub", mft.getIsPub());

        return (Integer) query.getSingleResult();
    }

    public Integer sp_updMFT(MFT mft) {
        Query query = entityManager
                .createNativeQuery("CALL sp_updmasterfeetable(:i_fee_detail_id, :i_fee_grp_id, :i_fee_detail_nm_e,"
                        + ":i_fee_detail_nm_b, :i_unit_fee, :i_promo_startdt, :i_promo_enddt, :i_promo_fee, :i_tax_cd_id,"
                        + ":i_allow_otc, :i_ll_parent_id, :i_ll_start_day, :i_ll_start_mth, :i_ll_end_day, :i_ll_end_mth,"
                        + ":i_ledger_cd, :i_ss_cd, :i_modified_by, :i_status, :i_is_pub)")
                .setParameter("i_fee_detail_id", mft.getFee_detail_id())
                .setParameter("i_fee_grp_id", mft.getFee_grp_id())
                .setParameter("i_fee_detail_nm_e", mft.getFee_detail_nm_e())
                .setParameter("i_fee_detail_nm_b", mft.getFee_detail_nm_b())
                .setParameter("i_unit_fee", mft.getUnit_fee())
                .setParameter("i_promo_startdt", mft.getPromo_startdt())
                .setParameter("i_promo_enddt", mft.getPromo_enddt())
                .setParameter("i_promo_fee", mft.getPromo_fee())
                .setParameter("i_tax_cd_id", mft.getTax_cd_id())
                .setParameter("i_allow_otc", mft.getAllow_otc())
                .setParameter("i_ll_parent_id", mft.getLl_parent_id())
                .setParameter("i_ll_start_day", mft.getLl_start_day())
                .setParameter("i_ll_start_mth", mft.getLl_start_mth())
                .setParameter("i_ll_end_day", mft.getLl_end_day())
                .setParameter("i_ll_end_mth", mft.getLl_end_mth())
                .setParameter("i_ledger_cd", mft.getLedger_cd())
                .setParameter("i_ss_cd", mft.getSs_cd())
                .setParameter("i_modified_by", mft.getModified_by())
                .setParameter("i_status", mft.getStatus())
                .setParameter("i_is_pub", mft.getIsPub());

        return (Integer) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_getfeedetailitems(FeeDetailItemsRequest feeDetailItemsReq) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfeedetailitems(:fee_detail_id, :fee_grp_id, :ss_cd, :last_sync_dt, :exclude_deleted)")
                .setParameter("fee_detail_id", feeDetailItemsReq.getFee_detail_id() != null ? feeDetailItemsReq.getFee_detail_id() : null)
                .setParameter("fee_grp_id", feeDetailItemsReq.getFee_grp_id() != null ? feeDetailItemsReq.getFee_grp_id() : null)
                .setParameter("ss_cd", feeDetailItemsReq.getSs_cd() != null ? feeDetailItemsReq.getSs_cd() : null)
                .setParameter("last_sync_dt", feeDetailItemsReq.getLast_sync_dt() != null ? feeDetailItemsReq.getLast_sync_dt() : null)
                .setParameter("exclude_deleted", feeDetailItemsReq.getExclude_deleted() != null ? feeDetailItemsReq.getExclude_deleted() : null);

        if (feeDetailItemsReq.getFee_detail_id() != null) {
            query.setParameter("fee_detail_id", feeDetailItemsReq.getFee_detail_id());
        } else {
            query.setParameter("fee_detail_id", null);
        }

        if (feeDetailItemsReq.getFee_grp_id() != null) {
            query.setParameter("fee_grp_id", feeDetailItemsReq.getFee_grp_id());
        } else {
            query.setParameter("fee_grp_id", null);
        }

        if (feeDetailItemsReq.getSs_cd() != null) {
            query.setParameter("ss_cd", feeDetailItemsReq.getSs_cd());
        } else {
            query.setParameter("ss_cd", null);
        }

        if (feeDetailItemsReq.getLast_sync_dt() != null) {
            query.setParameter("last_sync_dt", feeDetailItemsReq.getLast_sync_dt());
        } else {
            query.setParameter("last_sync_dt", null);
        }

        if (feeDetailItemsReq.getExclude_deleted() != null) {
            query.setParameter("exclude_deleted", feeDetailItemsReq.getExclude_deleted());
        } else {
            query.setParameter("exclude_deleted", null);
        }

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_checkmftexist(MFTRequest mftRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_checkmftexist(:i_page, :i_size, :i_fee_detail_id, :i_status)")
                .setParameter("i_page", mftRequest.getI_page())
                .setParameter("i_size", mftRequest.getI_size())
                .setParameter("i_fee_detail_id", mftRequest.getI_fee_detail_id())
                .setParameter("i_status", mftRequest.getI_status());

        return query.getResultList();
    }

    //@Override
    public List<Object[]> sp_getmft_typesearch(MFTTypeSearchRequest mfttypesearchRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_getmft_typesearch(:i_page, :i_size, :i_ss_cd, :i_status, :i_searchTerm)")
            .setParameter("i_page", mfttypesearchRequest.getI_page())
            .setParameter("i_size", mfttypesearchRequest.getI_size())
            .setParameter("i_ss_cd", mfttypesearchRequest.getI_ss_cd())
            .setParameter("i_status", mfttypesearchRequest.getI_status())
            .setParameter("i_searchTerm", mfttypesearchRequest.getI_searchTerm())
            ;

        return query.getResultList();
    }

}
