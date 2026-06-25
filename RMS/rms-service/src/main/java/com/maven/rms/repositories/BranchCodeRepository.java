package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import com.maven.rms.interfaces.IBranchCodeRepository;
import com.maven.rms.models.BranchCode;
import com.maven.rms.models.BranchCodeAddRequest;
import com.maven.rms.models.BranchCodeRequest;
import com.maven.rms.models.BranchCodeUpdateRequest;
import com.maven.rms.models.BranchCodeDeleteRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.utils.SystemStatus;

@Repository
public class BranchCodeRepository implements IBranchCodeRepository {
    @Autowired
     private AuthService authService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> getBranchCodes(BranchCodeRequest branchCodeRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_getbranchcode(:i_page, :i_size, :i_code, :i_bcm_ty, :i_bcm_desc, :i_dt_modified_fr, :i_dt_modified_to, :i_modified_by, :i_status)")
            .setParameter("i_page", branchCodeRequest.getI_page())
            .setParameter("i_size", branchCodeRequest.getI_size())
            .setParameter("i_code", branchCodeRequest.getI_code() != null ? branchCodeRequest.getI_code() : null)
            .setParameter("i_bcm_ty", branchCodeRequest.getI_bcm_ty() != null ? branchCodeRequest.getI_bcm_ty() : null)
            .setParameter("i_bcm_desc", branchCodeRequest.getI_bcm_desc() != null ? branchCodeRequest.getI_bcm_desc() : null)
            .setParameter("i_dt_modified_fr", branchCodeRequest.getI_dt_modified_fr() != null ? branchCodeRequest.getI_dt_modified_fr() : null)
            .setParameter("i_dt_modified_to", branchCodeRequest.getI_dt_modified_to() != null ? branchCodeRequest.getI_dt_modified_to() : null)
            .setParameter("i_modified_by", branchCodeRequest.getI_modified_by() != null ? branchCodeRequest.getI_modified_by() : null)
            .setParameter("i_status", branchCodeRequest.getI_status() != null ? branchCodeRequest.getI_status() : null);
        
        return query.getResultList();
    }

    @Override
    public Integer sp_insbcm(BranchCodeAddRequest branchCodeAddRequest) {
        Query query = entityManager.createNativeQuery(
                    "CALL sp_insbcm(:i_code, :i_bcm_ty, :i_bcm_desc, :i_created_by, :i_modified_by, :i_status)")
                    .setParameter("i_code", branchCodeAddRequest.getI_code())
                    .setParameter("i_bcm_ty", branchCodeAddRequest.getI_bcm_ty())
                    .setParameter("i_bcm_desc", branchCodeAddRequest.getI_bcm_desc())
                    .setParameter("i_created_by", authService.getLoginUserName())
                    .setParameter("i_modified_by", authService.getLoginUserName())
                    .setParameter("i_status", SystemStatus.Active.getMessage());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_updatebcm(BranchCodeUpdateRequest updateRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_updatebcm(:i_bcm_id, :i_code, :i_bcm_ty, :i_bcm_desc, :i_modified_by, :i_status)")
                .setParameter("i_bcm_id", updateRequest.getI_bcm_id())
                .setParameter("i_code", updateRequest.getI_code())
                .setParameter("i_bcm_ty", updateRequest.getI_bcm_ty())
                .setParameter("i_bcm_desc", updateRequest.getI_bcm_desc())
                .setParameter("i_modified_by", authService.getLoginUserName())
                .setParameter("i_status", updateRequest.getI_status());
        
        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_delbranchcode(BranchCodeDeleteRequest deleteRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_delbranchcode(:i_bcm_id,:i_code,:i_modified_by,:i_status)")
                    .setParameter("i_bcm_id", deleteRequest.getI_bcm_id())
                    .setParameter("i_code", deleteRequest.getI_code())
                    .setParameter("i_modified_by", authService.getLoginUserName())
                    .setParameter("i_status", deleteRequest.getI_status());
          Integer result = (Integer) query.getSingleResult();
          return result;
     }

}
