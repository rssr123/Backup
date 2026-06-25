package com.maven.rms.repositories;

import java.math.BigInteger;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IRoleAndPermissionsConfigurationInterface;
import com.maven.rms.models.CheckRoleRequest;
import com.maven.rms.models.RolePermissionGetRequest;
import com.maven.rms.models.RolePermissionRequest;
import com.maven.rms.models.RoleRequest;
import com.maven.rms.models.UserRoleRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.utils.SystemStatus;

@Repository
public class IRoleAndPermissionsConfigurationRepository implements IRoleAndPermissionsConfigurationInterface {
     @PersistenceContext
     private EntityManager entityManager;

     @Autowired
     private AuthService authService;

     @Override
     // public List<Object[]> sp_getuseranduserroles(Integer i_page, Integer i_size,
     // String i_user, String i_user_role) {
     public List<Object[]> sp_getuseranduserroles(UserRoleRequest userRoleRequest) {

          Query query = entityManager.createNativeQuery(
                    "CALL sp_getuseranduserroles(:i_page, :i_size, :i_user, :i_user_role)")
                    .setParameter("i_page", userRoleRequest.getI_page())
                    .setParameter("i_size", userRoleRequest.getI_size())
                    .setParameter("i_user", userRoleRequest.getI_user())
                    .setParameter("i_user_role", userRoleRequest.getI_user_role());

          // Check if the dates are null and set accordingly
          if (userRoleRequest.getI_user() != null) {
               query.setParameter("i_user", userRoleRequest.getI_user());
          } else {
               query.setParameter("i_user", null);
          }

          if (userRoleRequest.getI_user_role() != null) {
               query.setParameter("i_user_role", userRoleRequest.getI_user_role());
          } else {
               query.setParameter("i_user_role", null);
          }
          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getroles(RoleRequest roleRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getroles(:i_page, :i_size, :i_r_id, :i_r_role_nm_en, :i_r_role_nm_bm, :i_modified_by, :i_dt_modified_fr, :i_dt_modified_to, :i_status)")
                    .setParameter("i_page", roleRequest.getI_page())
                    .setParameter("i_size", roleRequest.getI_size())
                    .setParameter("i_r_id", roleRequest.getI_r_id())
                    .setParameter("i_r_role_nm_en", roleRequest.getI_r_role_nm_en())
                    .setParameter("i_r_role_nm_bm", roleRequest.getI_r_role_nm_bm())
                    .setParameter("i_modified_by", roleRequest.getI_modified_by())
                    .setParameter("i_dt_modified_fr", roleRequest.getI_dt_modified_fr())
                    .setParameter("i_dt_modified_to", roleRequest.getI_dt_modified_to())
                    .setParameter("i_status", roleRequest.getI_status());
          return query.getResultList();
     }

     @Override
     public Integer sp_insroles(RoleRequest roleRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_insroles(:i_r_role_nm_en, :i_created_by, :i_modified_by,:i_status)")
                    .setParameter("i_r_role_nm_en", roleRequest.getI_r_role_nm_en())
                    .setParameter("i_created_by", authService.getLoginUserName())
                    .setParameter("i_modified_by", authService.getLoginUserName())
                    .setParameter("i_status", SystemStatus.Active.getMessage());

          Integer result = (Integer) query.getSingleResult();
          return result;
     }

     @Override
     public List<Object[]> sp_getpermissions() {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getpermissions()");
          return query.getResultList();
     }

     @Override
     public Integer sp_updrolestatus(RoleRequest roleRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_updrolestatus(:i_r_role_nm_en, :i_modified_by)")
                    .setParameter("i_r_role_nm_en", roleRequest.getI_r_role_nm_en())
                    .setParameter("i_modified_by", authService.getLoginUserName());
          Integer result = (Integer) query.getSingleResult();
          return result;
     }

     // @Override
     // public Integer sp_insroleperm(RolePermissionRequest rolePermissionRequest) {

     // Query query = entityManager.createNativeQuery(
     // "CALL sp_insroleperm(:i_role_id, :i_perm_id, :i_is_allow,:i_created_by,
     // :i_modified_by)")
     // .setParameter("i_role_id", rolePermissionRequest.getI_role_id())
     // .setParameter("i_perm_id", rolePermissionRequest.getI_perm_id())
     // .setParameter("i_is_allow", rolePermissionRequest.getI_is_allow())
     // .setParameter("i_created_by", authService.getLoginUserName())
     // .setParameter("i_modified_by", authService.getLoginUserName());

     // Integer result = (Integer) query.getSingleResult();
     // return result;
     // }

     @Override
     public Integer sp_insroleperm(List<RolePermissionRequest> rolePermissionRequests) {
          Integer totalResult = 0;

          for (RolePermissionRequest rolePermissionRequest : rolePermissionRequests) {
               Query query = entityManager.createNativeQuery(
                         "CALL sp_insroleperm(:i_role_id, :i_perm_id, :i_is_allow, :i_created_by, :i_modified_by)")
                         .setParameter("i_role_id", rolePermissionRequest.getI_role_id())
                         .setParameter("i_perm_id", rolePermissionRequest.getI_perm_id())
                         .setParameter("i_is_allow", rolePermissionRequest.getI_is_allow())
                         .setParameter("i_created_by", authService.getLoginUserName())
                         .setParameter("i_modified_by", authService.getLoginUserName());

               Integer result = (Integer) query.getSingleResult();
               totalResult += result; // Sum the results of each call
          }

          return totalResult;
     }

     // 250930- v2 created by Wei Ern instructed by Roy. Using v1 in production. V2's code already tested and Roy decided to wait until user complaint only use v2
     @Override
     @Transactional // if not already on the service layer
     public Integer sp_insroleperm_v2(List<RolePermissionRequest> rolePermissionRequests) {
          Session session = entityManager.unwrap(Session.class);
          String username = authService.getLoginUserName();

          return session.doReturningWork(conn -> {
               String sql = "INSERT INTO rms_role_perm(role_id, perm_id, is_allow, dt_created, dt_modified, created_by, modified_by, status)VALUES(?, ?, ?, CURRENT, CURRENT, ?, ?, 'A')";

               int totalInserted = 0;

               try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    for (RolePermissionRequest req : rolePermissionRequests) {
                         ps.setInt(1, req.getI_role_id());
                         ps.setInt(2, req.getI_perm_id());
                         ps.setInt(3, req.getI_is_allow()); // or setBoolean if Informix maps it
                         ps.setString(4, username);
                         ps.setString(5, username);
                         ps.addBatch();
                    }

                    int[] result = ps.executeBatch();
                    for (int r : result) {
                         // Informix/JDBC batch can return SUCCESS_NO_INFO (-2). Treat that as 1 row.
                         totalInserted += (r == java.sql.Statement.SUCCESS_NO_INFO) ? 1 : Math.max(r, 0);
                    }
               }

               // Do NOT close conn here; Hibernate manages it.
               return totalInserted;
          });
     }


     @Override
     public List<Object[]> sp_getpermissionsbyid() {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getpermissionsbyid()");
          return query.getResultList();
     }

     @Override
     public Integer sp_delroleperm(RolePermissionRequest rolePermissionRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_delroleperm(:i_role_id)")
                    .setParameter("i_role_id", rolePermissionRequest.getI_role_id());
          Integer result = (Integer) query.getSingleResult();
          return result;
     }

     @Override
     public List<Object[]> sp_getroleperm(RolePermissionGetRequest rolePermissionGetRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getroleperm(:i_role_id)")
                    .setParameter("i_role_id", rolePermissionGetRequest.getI_role_id());
          return query.getResultList();
     }

     @Override
     public String sp_checkuserrole(CheckRoleRequest checkRoleRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_checkuserperm(:i_username, :i_perm_cd)")
                    .setParameter("i_username", authService.getLoginUserName())
                    .setParameter("i_perm_cd", checkRoleRequest.getI_perm_cd());
          // return query.getSingleResult().toString();
          return (String) query.getSingleResult();
          // return (String) query.getResultList().stream().findFirst().orElse(null);
          // Integer result = (Integer) query.getSingleResult();
          // return result;
     }
}
