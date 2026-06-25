package com.maven.rms.repositories;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IBillingTypeRepository;
import com.maven.rms.models.BillingTypeRequest;
import com.maven.rms.services.AuthService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class BillingTypeRepository implements IBillingTypeRepository {

    @Autowired
    private AuthService authService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> getBillingType(BillingTypeRequest billingTypeRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_getbltc(:i_page, :i_size, :i_bt_cd, :i_bt_ty, :i_bt_desc, :i_class_id, :i_ss_cd, :i_mft_pk, :i_mft_id, :i_dps_mft_pk, :i_dps_mft_id, :i_dt_modified_fr, :i_dt_modified_to, :i_modified_by, :i_status)")
            .setParameter("i_page", billingTypeRequest.getI_page())
            .setParameter("i_size", billingTypeRequest.getI_size())
            .setParameter("i_bt_cd", billingTypeRequest.getI_bt_cd())
            .setParameter("i_bt_ty", billingTypeRequest.getI_bt_ty())
            .setParameter("i_bt_desc", billingTypeRequest.getI_bt_desc())
            .setParameter("i_class_id", billingTypeRequest.getI_class_id())
            .setParameter("i_ss_cd", billingTypeRequest.getI_ss_cd())
            .setParameter("i_mft_pk", billingTypeRequest.getI_mft_pk())
            .setParameter("i_mft_id", billingTypeRequest.getI_mft_id())
            .setParameter("i_dps_mft_pk", billingTypeRequest.getI_dps_mft_pk())
            .setParameter("i_dps_mft_id", billingTypeRequest.getI_dps_mft_id())
            .setParameter("i_dt_modified_fr", billingTypeRequest.getI_dt_modified_fr() != null ? billingTypeRequest.getI_dt_modified_fr() : null)
            .setParameter("i_dt_modified_to", billingTypeRequest.getI_dt_modified_to() != null ? billingTypeRequest.getI_dt_modified_to() : null)
            .setParameter("i_modified_by", billingTypeRequest.getI_modified_by() != null ? billingTypeRequest.getI_modified_by() : null)
            .setParameter("i_status", billingTypeRequest.getI_status() != null ? billingTypeRequest.getI_status() : null);

        return query.getResultList();
    }

    @Override
    public Integer sp_insbltc(BillingTypeRequest billingTypeRequest) {
    	/*log.error("CALL sp_insbltc('" + billingTypeRequest.getI_bt_cd() + "','" + billingTypeRequest.getI_bt_ty() + "','"
    			+ billingTypeRequest.getI_bt_desc() + "','" + billingTypeRequest.getI_class_id() + "','" + billingTypeRequest.getI_ss_cd()
    			+ "'," + (billingTypeRequest.getI_mft_pk() == null? "null" : Integer.toString(billingTypeRequest.getI_mft_pk())) + ",'"
    			+ billingTypeRequest.getI_mft_id() + "'," + (billingTypeRequest.getI_dps_mft_pk() == null? "null":Integer.toString(billingTypeRequest.getI_dps_mft_pk()))
    			+ ",'" + billingTypeRequest.getI_dps_mft_id() + "','" + authService.getLoginUserName() + "','" + authService.getLoginUserName()
    			+ "','" + billingTypeRequest.getI_status() + "');");*/
        Query query = entityManager.createNativeQuery(
            "CALL sp_insbltc(:i_bt_cd, :i_bt_ty, :i_bt_desc, :i_class_id, :i_ss_cd, :i_created_by, :i_modified_by)")
            .setParameter("i_bt_cd", billingTypeRequest.getI_bt_cd())
            .setParameter("i_bt_ty", billingTypeRequest.getI_bt_ty())
            .setParameter("i_bt_desc", billingTypeRequest.getI_bt_desc())
            .setParameter("i_class_id", billingTypeRequest.getI_class_id())
            .setParameter("i_ss_cd", billingTypeRequest.getI_ss_cd())
            .setParameter("i_created_by", authService.getLoginUserName())
            .setParameter("i_modified_by", authService.getLoginUserName());

        return (Integer) query.getSingleResult();
    }

    // @Override
    // public Integer sp_insbltcitem(List<BillingTypeRequest> billingTypeRequest) {

    //     Query query = entityManager.createNativeQuery(
    //         "CALL sp_insbltcitem(:i_bltc_id, :i_mft_pk, :i_mft_id, :i_dps_mft_pk, :i_dps_mft_id, :i_created_by, :i_modified_by)")
    //         .setParameter("i_bltc_id", billingTypeRequest.getI_bltc_id())
    //         .setParameter("i_mft_pk", billingTypeRequest.getI_mft_pk())
    //         .setParameter("i_mft_id", billingTypeRequest.getI_mft_id())
    //         .setParameter("i_dps_mft_pk", billingTypeRequest.getI_dps_mft_pk())
    //         .setParameter("i_dps_mft_id", billingTypeRequest.getI_dps_mft_id())
    //         .setParameter("i_created_by", authService.getLoginUserName())
    //         .setParameter("i_modified_by", authService.getLoginUserName());

    //     return (Integer) query.getSingleResult();
    // }

    @Override
    public Integer sp_insbltcitem(List<BillingTypeRequest> billingTypeRequest) {

        Integer result = 0;
        // Iterate over each OTC Payment Request in the list
        for (BillingTypeRequest request : billingTypeRequest) {
            Query query = entityManager.createNativeQuery(
                    "CALL sp_insbltcitem(:i_bltc_id, :i_mft_pk, :i_mft_id, :i_dps_mft_pk, :i_dps_mft_id, :i_created_by, :i_modified_by)");

            // Set the parameters for the stored procedure call
            query.setParameter("i_bltc_id", request.getI_bltc_id())
                    .setParameter("i_mft_pk", request.getI_mft_pk())
                    .setParameter("i_mft_id", request.getI_mft_id())
                    .setParameter("i_dps_mft_pk", request.getI_dps_mft_pk())
                    .setParameter("i_dps_mft_id", request.getI_dps_mft_id())
                    .setParameter("i_created_by", authService.getLoginUserName())
                    .setParameter("i_modified_by", authService.getLoginUserName());
            // Execute the stored procedure
            result = (Integer) query.getSingleResult();
        }

        return result; // Return the accumulated total result
    }

    @Override
    @Transactional
    public Integer sp_updbltcitem(List<BillingTypeRequest> billingTypeRequest) {
        Integer totalResult = 0;
    
        // Extract all incoming mft_ids (uppercased for safety)
        List<String> validMftIds = billingTypeRequest.stream()
                .map(req -> req.getI_mft_id().toUpperCase())
                .collect(Collectors.toList());
    
        Integer bltcId = billingTypeRequest.get(0).getI_bltc_id(); // assumed all same
    
        for (BillingTypeRequest request : billingTypeRequest) {
            Query query = entityManager.createNativeQuery(
                    "CALL sp_updbltcitem(:i_bltc_id, :i_mft_pk, :i_mft_id, :i_dps_mft_pk, :i_dps_mft_id)");
            query.setParameter("i_bltc_id", request.getI_bltc_id());
            query.setParameter("i_mft_pk", request.getI_mft_pk());
            query.setParameter("i_mft_id", request.getI_mft_id());
            query.setParameter("i_dps_mft_pk", request.getI_dps_mft_pk());
            query.setParameter("i_dps_mft_id", request.getI_dps_mft_id());
    
            totalResult += (Integer) query.getSingleResult();
        }
    
        // Now delete any records that are not in the valid list
        Query deleteQuery = entityManager.createNativeQuery(
                "DELETE FROM rms_bltc_item WHERE bltc_id = :bltc_id AND UPPER(mft_id) NOT IN :validMftIds"
        );
        deleteQuery.setParameter("bltc_id", bltcId);
        deleteQuery.setParameter("validMftIds", validMftIds);
        deleteQuery.executeUpdate();
    
        return totalResult;
    }

    @Override
    public Integer sp_updatebltc(BillingTypeRequest billingTypeRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_updbltc(:i_bt_cd, :i_bt_ty, :i_bt_desc, :i_class_id, :i_ss_cd, :i_modified_by, :i_status)")
            .setParameter("i_bt_cd", billingTypeRequest.getI_bt_cd())
            .setParameter("i_bt_ty", billingTypeRequest.getI_bt_ty())
            .setParameter("i_bt_desc", billingTypeRequest.getI_bt_desc())
            .setParameter("i_class_id", billingTypeRequest.getI_class_id())
            .setParameter("i_ss_cd", billingTypeRequest.getI_ss_cd())
            .setParameter("i_modified_by", authService.getLoginUserName())
            .setParameter("i_status", billingTypeRequest.getI_status());

        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_delbltc(BillingTypeRequest billingTypeRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_delbltc(:i_bltc_id, :i_bt_cd, :i_modified_by, :i_status)")
            .setParameter("i_bltc_id", billingTypeRequest.getI_bltc_id())
            .setParameter("i_bt_cd", billingTypeRequest.getI_bt_cd())
            .setParameter("i_modified_by", authService.getLoginUserName())
            .setParameter("i_status", billingTypeRequest.getI_status());

        return (Integer) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_getnblcm() {
         Query query = entityManager.createNativeQuery("CALL sp_getnblcm()");
         return query.getResultList();

    }
}
