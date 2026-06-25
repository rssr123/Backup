package com.maven.rms.repositories;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IStoreProcedureInterface;
import com.maven.rms.interfaces.IUserRoleInterface;
import com.maven.rms.models.ParamRequest;
import com.maven.rms.models.RMSUserRequest;

@Repository
public class IUserRoleRepository implements IUserRoleInterface{

    @PersistenceContext
     private EntityManager entityManager;

    // #region user role
     @Override
     public List<Object[]> sp_getuserbyrole(RMSUserRequest rmsUserRequest) {
          Query query = entityManager
                    .createNativeQuery(
                    "CALL sp_getuserbyrole(:i_page, :i_size, :i_role_nm_en, :i_role_nm_bm,  :i_status)")
                    .setParameter("i_page", rmsUserRequest.getI_page())
                    .setParameter("i_size", rmsUserRequest.getI_size())
                    .setParameter("i_role_nm_en", rmsUserRequest.getI_role_nm_en())
                    .setParameter("i_role_nm_bm", rmsUserRequest.getI_role_nm_bm())
                    .setParameter("i_status", rmsUserRequest.getI_status());
          return query.getResultList();
     }

     @Override
     public Object sp_getuserdetail(RMSUserRequest rmsUserRequest) {
          Query query = entityManager.createNativeQuery("CALL sp_getuserdetail(:i_ssm4uuserrefno)")
                    .setParameter("i_ssm4uuserrefno", rmsUserRequest.getI_ssm4uuserrefno());
          return query.getSingleResult();
     }
     // #endregion

     @Override
     public List<Object[]> sp_getparam(ParamRequest paramRequest) {
          Query query = entityManager
                    .createNativeQuery("CALL sp_getparam(:i_page, :i_size, :i_param_cd, :i_param_grp_nm)")
                    .setParameter("i_page", paramRequest.getI_page())
                    .setParameter("i_size", paramRequest.getI_size())
                    .setParameter("i_param_cd", paramRequest.getI_param_cd())
                    .setParameter("i_param_grp_nm", paramRequest.getI_param_grp_nm());
          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getsourcesystem(Integer i_page, Integer i_size, BigInteger i_ss_id, String i_ss_cd,
               String i_ss_nm,
               String i_modified_by, Date i_dt_modified_fr, Date i_dt_modified_to, String i_status) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getsourcesystem(:i_page, :i_size, :i_ss_id, :i_ss_cd, :i_ss_nm, :i_modified_by, :i_dt_modified_fr, :i_dt_modified_to, :i_status)")
                    .setParameter("i_page", i_page)
                    .setParameter("i_size", i_size)
                    .setParameter("i_ss_id", i_ss_id)
                    .setParameter("i_ss_cd", i_ss_cd)
                    .setParameter("i_ss_nm", i_ss_nm)
                    .setParameter("i_modified_by", i_modified_by)
                    .setParameter("i_dt_modified_fr", i_dt_modified_fr)
                    .setParameter("i_dt_modified_to", i_dt_modified_to)
                    .setParameter("i_status", i_status);
          return query.getResultList();
     }
    
}
