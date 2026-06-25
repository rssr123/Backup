package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.models.TaxCdRequest;
import com.maven.rms.interfaces.ITaxCodeInterface;

@Repository
public class ITaxCodeRepository implements ITaxCodeInterface {

    @PersistenceContext
    private EntityManager entityManager;

    // #region tax code
    @Override
    public Integer sp_instaxcode(TaxCdRequest insertRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_insTaxCode(:i_tax_cd, :i_tax_cd_nm_en, :i_tax_cd_nm_bm, :i_tax_pct,:i_created_by, :i_modified_by,:i_status)")
                .setParameter("i_tax_cd", insertRequest.getI_tax_cd())
                .setParameter("i_tax_cd_nm_en", insertRequest.getI_tax_cd_nm_en())
                .setParameter("i_tax_cd_nm_bm", insertRequest.getI_tax_cd_nm_bm())
                .setParameter("i_tax_pct", insertRequest.getI_tax_pct())
                .setParameter("i_created_by", insertRequest.getI_created_by())
                .setParameter("i_modified_by", insertRequest.getI_modified_by())
                .setParameter("i_status", insertRequest.getI_status());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_updtaxcode(TaxCdRequest updateRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_updTaxCode(:i_tax_cd_id, :i_tax_cd, :i_tax_cd_nm_en, :i_tax_cd_nm_bm, :i_tax_pct, :i_modified_by, :i_status)")
                .setParameter("i_tax_cd_id", updateRequest.getI_tax_cd_id())
                .setParameter("i_tax_cd", updateRequest.getI_tax_cd())
                .setParameter("i_tax_cd_nm_en", updateRequest.getI_tax_cd_nm_en())
                .setParameter("i_tax_cd_nm_bm", updateRequest.getI_tax_cd_nm_bm())
                .setParameter("i_tax_pct", updateRequest.getI_tax_pct())
                .setParameter("i_modified_by", updateRequest.getI_modified_by())
                .setParameter("i_status", updateRequest.getI_status());
        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_deltaxcode(TaxCdRequest deleteRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_delTaxCode(:i_tax_cd, :i_modified_by, :i_status)")

                .setParameter("i_tax_cd", deleteRequest.getI_tax_cd())
                .setParameter("i_modified_by", deleteRequest.getI_modified_by())
                .setParameter("i_status", deleteRequest.getI_status());
        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    // public List<Object[]> sp_gettaxcode_v2(Integer i_page, Integer i_size, Long
    // i_tax_cd_id, String i_tax_cd,
    // String i_tax_cd_nm_en, String i_tax_cd_nm_bm, String i_modified_by, Date
    // i_dt_modified_fr,
    // Date i_dt_modified_to, String i_status) {
    public List<Object[]> sp_gettaxcode_v2(TaxCdRequest taxCdRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_gettaxcode_v2(:i_page, :i_size, :i_tax_cd_id, :i_tax_cd, :i_tax_cd_nm_en, :i_tax_cd_nm_bm, :i_modified_by, :i_dt_modified_fr, :i_dt_modified_to, :i_status)")
                .setParameter("i_page", taxCdRequest.getI_page())
                .setParameter("i_size", taxCdRequest.getI_size())
                .setParameter("i_tax_cd_id", taxCdRequest.getI_tax_cd_id())
                .setParameter("i_tax_cd", taxCdRequest.getI_tax_cd())
                .setParameter("i_tax_cd_nm_en", taxCdRequest.getI_tax_cd_nm_en())
                .setParameter("i_tax_cd_nm_bm", taxCdRequest.getI_tax_cd_nm_bm())
                .setParameter("i_modified_by", taxCdRequest.getI_modified_by())
                .setParameter("i_dt_modified_fr",
                        taxCdRequest.getI_dt_modified_fr() != null ? taxCdRequest.getI_dt_modified_fr() : null)
                .setParameter("i_dt_modified_to",
                        taxCdRequest.getI_dt_modified_to() != null ? taxCdRequest.getI_dt_modified_to() : null)
                .setParameter("i_status", taxCdRequest.getI_status());

        // Check if the dates are null and set accordingly
        if (taxCdRequest.getI_dt_modified_fr() != null) {
            query.setParameter("i_dt_modified_fr", taxCdRequest.getI_dt_modified_fr());
        } else {
            query.setParameter("i_dt_modified_fr", null);
        }

        if (taxCdRequest.getI_dt_modified_to() != null) {
            query.setParameter("i_dt_modified_to", taxCdRequest.getI_dt_modified_to());
        } else {
            query.setParameter("i_dt_modified_to", null);
        }
        return query.getResultList();
    }

    @Override
    public Integer sp_checktaxcdbyid(TaxCdRequest taxCodeRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_checkfeetaxbyid(:i_fee_grp_id,:i_tax_cd_id)")
                .setParameter("i_fee_grp_id", "")
                .setParameter("i_tax_cd_id", taxCodeRequest.getI_tax_cd_id());
        return (Integer) query.getSingleResult();
    }
    // #endregion
}
