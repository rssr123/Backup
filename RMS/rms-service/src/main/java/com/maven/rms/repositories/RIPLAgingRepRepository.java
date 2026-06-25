package com.maven.rms.repositories;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IRIPLAgingRepInterface;
import com.maven.rms.models.RIPLAgingRequest;

@Repository
public class RIPLAgingRepRepository implements IRIPLAgingRepInterface {

     @PersistenceContext
     private EntityManager entityManager;

     @Override
     public BigInteger sp_insriplagingrpt(RIPLAgingRequest RIPLRequest, String i_p_email, String i_created_by,
               String i_modified_by) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_insriplagingrpt(:i_p_dt_req, :i_p_imp_status, :i_p_exp_status, :i_p_ent_ty, :i_p_ent_nm, :i_p_dt_due_fr, :i_p_dt_due_to, :i_p_dt_rcpt_fr, :i_p_dt_rcpt_to, :i_p_dt_imp_fr, :i_p_dt_imp_to, :i_p_dt_wo_fr, :i_p_dt_wo_to, :i_created_by, :i_modified_by, :i_status, :i_p_email, :i_p_file_type, :i_p_file_size, :i_p_file_nm, :i_p_batch_no, :i_p_fms_ref_no)")
                    .setParameter("i_p_dt_req", RIPLRequest.getI_p_dt_req())
                    .setParameter("i_p_imp_status", RIPLRequest.getI_p_imp_status() != null ? RIPLRequest.getI_p_imp_status() : null)
                    .setParameter("i_p_exp_status", RIPLRequest.getI_p_exp_status() != null ? RIPLRequest.getI_p_exp_status() : null)
                    .setParameter("i_p_ent_ty", RIPLRequest.getI_p_ent_ty() != null ? RIPLRequest.getI_p_ent_ty() : null)
                    .setParameter("i_p_ent_nm", RIPLRequest.getI_p_ent_nm() != null ? RIPLRequest.getI_p_ent_nm() : null)
                    .setParameter("i_p_dt_due_fr", RIPLRequest.getI_p_dt_due_fr())
                    .setParameter("i_p_dt_due_to", RIPLRequest.getI_p_dt_due_to())
                    .setParameter("i_p_dt_rcpt_fr", RIPLRequest.getI_p_dt_rcpt_fr() != null ? RIPLRequest.getI_p_dt_rcpt_fr() : null)
                    .setParameter("i_p_dt_rcpt_to", RIPLRequest.getI_p_dt_rcpt_to() != null ? RIPLRequest.getI_p_dt_rcpt_to() : null)
                    .setParameter("i_p_dt_imp_fr", RIPLRequest.getI_p_dt_imp_fr() != null ? RIPLRequest.getI_p_dt_imp_fr() : null)
                    .setParameter("i_p_dt_imp_to", RIPLRequest.getI_p_dt_imp_to() != null ? RIPLRequest.getI_p_dt_imp_to() : null)
                    .setParameter("i_p_dt_wo_fr", RIPLRequest.getI_p_dt_wo_fr() != null ? RIPLRequest.getI_p_dt_wo_fr() : null)
                    .setParameter("i_p_dt_wo_to", RIPLRequest.getI_p_dt_wo_to() != null ? RIPLRequest.getI_p_dt_wo_to() : null)
                    .setParameter("i_created_by", i_created_by)
                    .setParameter("i_modified_by", i_modified_by)
                    .setParameter("i_status", RIPLRequest.getI_status())
                    .setParameter("i_p_email", i_p_email)
                    .setParameter("i_p_file_type", RIPLRequest.getI_p_file_type() != null ? RIPLRequest.getI_p_file_type() : null)
                    .setParameter("i_p_file_size", RIPLRequest.getI_p_file_size() != null ? RIPLRequest.getI_p_file_size() : null)
                    .setParameter("i_p_file_nm", RIPLRequest.getI_p_file_nm() != null ? RIPLRequest.getI_p_file_nm() : null)
                    .setParameter("i_p_batch_no", RIPLRequest.getI_p_batch_no() != null ? RIPLRequest.getI_p_batch_no() : null)
                    .setParameter("i_p_fms_ref_no", RIPLRequest.getI_p_fms_ref_no() != null ? RIPLRequest.getI_p_fms_ref_no() : null);

          // Check if the dates are null and set accordingly
          if (RIPLRequest.getI_p_imp_status() != null) {
               query.setParameter("i_p_imp_status", RIPLRequest.getI_p_imp_status());
          } else {
               query.setParameter("i_p_imp_status", null);
          }

          if (RIPLRequest.getI_p_exp_status() != null) {
               query.setParameter("i_p_exp_status", RIPLRequest.getI_p_exp_status());
          } else {
               query.setParameter("i_p_exp_status", null);
          }

          if (RIPLRequest.getI_p_ent_ty() != null) {
               query.setParameter("i_p_ent_ty", RIPLRequest.getI_p_ent_ty());
          } else {
               query.setParameter("i_p_ent_ty", null);
          }

          if (RIPLRequest.getI_p_ent_nm() != null) {
               query.setParameter("i_p_ent_nm", RIPLRequest.getI_p_ent_nm());
          } else {
               query.setParameter("i_p_ent_nm", null);
          }

          if (RIPLRequest.getI_p_dt_rcpt_fr() != null) {
               query.setParameter("i_p_dt_rcpt_fr", RIPLRequest.getI_p_dt_rcpt_fr());
          } else {
               query.setParameter("i_p_dt_rcpt_fr", null);
          }

          if (RIPLRequest.getI_p_dt_rcpt_to() != null) {
               query.setParameter("i_p_dt_rcpt_to", RIPLRequest.getI_p_dt_rcpt_to());
          } else {
               query.setParameter("i_p_dt_rcpt_to", null);
          }

          if (RIPLRequest.getI_p_dt_imp_fr() != null) {
               query.setParameter("i_p_dt_imp_fr", RIPLRequest.getI_p_dt_imp_fr());
          } else {
               query.setParameter("i_p_dt_imp_fr", null);
          }

          if (RIPLRequest.getI_p_dt_imp_to() != null) {
               query.setParameter("i_p_dt_imp_to", RIPLRequest.getI_p_dt_imp_to());
          } else {
               query.setParameter("i_p_dt_imp_to", null);
          }

          if (RIPLRequest.getI_p_dt_wo_fr() != null) {
               query.setParameter("i_p_dt_wo_fr", RIPLRequest.getI_p_dt_wo_fr());
          } else {
               query.setParameter("i_p_dt_wo_fr", null);
          }

          if (RIPLRequest.getI_p_dt_wo_to() != null) {
               query.setParameter("i_p_dt_wo_to", RIPLRequest.getI_p_dt_wo_to());
          } else {
               query.setParameter("i_p_dt_wo_to", null);
          }


          if (RIPLRequest.getI_p_file_type() != null) {
               query.setParameter("i_p_file_type", RIPLRequest.getI_p_file_type());
          } else {
               query.setParameter("i_p_file_type", null);
          }

          if (RIPLRequest.getI_p_file_size() != null) {
               query.setParameter("i_p_file_size", RIPLRequest.getI_p_file_size());
          } else {
               query.setParameter("i_p_file_size", null);
          }

          if (RIPLRequest.getI_p_file_nm() != null) {
               query.setParameter("i_p_file_nm", RIPLRequest.getI_p_file_nm());
          } else {
               query.setParameter("i_p_file_nm", null);
          }

          if (RIPLRequest.getI_p_batch_no() != null) {
               query.setParameter("i_p_batch_no", RIPLRequest.getI_p_batch_no());
          } else {
               query.setParameter("i_p_batch_no", null);
          }

          if (RIPLRequest.getI_p_fms_ref_no() != null) {
               query.setParameter("i_p_fms_ref_no", RIPLRequest.getI_p_fms_ref_no());
          } else {
               query.setParameter("i_p_fms_ref_no", null);
          }

          BigInteger result = (BigInteger) query.getSingleResult();
          return result;
     }

     @Override
     public List<Object[]> sp_getriplaginglistingrpt(RIPLAgingRequest RIPLRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getriplaginglistingrpt(:i_page,:i_size,:i_rpt_ripl_age_id, :i_p_dt_req, :i_p_imp_status, :i_p_exp_status, :i_p_ent_ty, :i_p_ent_nm, :i_p_dt_due_fr, :i_p_dt_due_to, :i_p_dt_rcpt_fr, :i_p_dt_rcpt_to, :i_p_dt_imp_fr, :i_p_dt_imp_to, :i_p_dt_wo_fr, :i_p_dt_wo_to, :i_created_by, :i_modified_by, :i_status, :i_p_email, :i_p_file_type, :i_p_file_size, :i_p_file_nm)")
                    .setParameter("i_page", RIPLRequest.getI_page())
                    .setParameter("i_size", RIPLRequest.getI_size())
                    .setParameter("i_rpt_ripl_age_id", RIPLRequest.getI_rpt_ripl_age_id())
                    .setParameter("i_p_dt_req", RIPLRequest.getI_p_dt_req())
                    .setParameter("i_p_imp_status", RIPLRequest.getI_p_imp_status())
                    .setParameter("i_p_exp_status", RIPLRequest.getI_p_exp_status())
                    .setParameter("i_p_ent_ty", RIPLRequest.getI_p_ent_ty())
                    .setParameter("i_p_ent_nm", RIPLRequest.getI_p_ent_nm())
                    .setParameter("i_p_dt_due_fr", RIPLRequest.getI_p_dt_due_fr())
                    .setParameter("i_p_dt_due_to", RIPLRequest.getI_p_dt_due_to())
                    .setParameter("i_p_dt_rcpt_fr", RIPLRequest.getI_p_dt_rcpt_fr())
                    .setParameter("i_p_dt_rcpt_to", RIPLRequest.getI_p_dt_rcpt_to())
                    .setParameter("i_p_dt_imp_fr", RIPLRequest.getI_p_dt_imp_fr())
                    .setParameter("i_p_dt_imp_to", RIPLRequest.getI_p_dt_imp_to())
                    .setParameter("i_p_dt_wo_fr", RIPLRequest.getI_p_dt_wo_fr())
                    .setParameter("i_p_dt_wo_to", RIPLRequest.getI_p_dt_wo_to())
                    .setParameter("i_created_by", RIPLRequest.getI_created_by())
                    .setParameter("i_modified_by", RIPLRequest.getI_modified_by())
                    .setParameter("i_status", RIPLRequest.getI_status())
                    .setParameter("i_p_email", RIPLRequest.getI_p_email())
                    .setParameter("i_p_file_type", RIPLRequest.getI_p_file_type())
                    .setParameter("i_p_file_size", RIPLRequest.getI_p_file_size())
                    .setParameter("i_p_file_nm", RIPLRequest.getI_p_file_nm());

          return query.getResultList();
     }

     // public Integer sp_updriplagingrpt(BigInteger i_rpt_ripl_age_id, String
     // i_status, Integer i_p_file_size,
     // String i_p_file_nm, String i_modified_by) {
     @Override
     public Integer sp_updriplagingrpt(RIPLAgingRequest riplAgingRequest) {
          Query query = entityManager
                    .createNativeQuery(
                              "CALL sp_updriplagingrpt(:i_rpt_ripl_age_id, :i_status, :i_p_file_size, :i_p_file_nm,:i_modified_by )")
                    .setParameter("i_rpt_ripl_age_id", riplAgingRequest.getI_rpt_ripl_age_id())
                    .setParameter("i_status", riplAgingRequest.getI_status())
                    .setParameter("i_p_file_size",
                              riplAgingRequest.getI_p_file_size() != null ? riplAgingRequest.getI_p_file_size() : null)
                    .setParameter("i_p_file_nm",
                              riplAgingRequest.getI_p_file_nm() != null ? riplAgingRequest.getI_p_file_nm() : null)
                    .setParameter("i_modified_by", riplAgingRequest.getI_modified_by());

          if (riplAgingRequest.getI_p_file_size() != null) {
               query.setParameter("i_p_file_size", riplAgingRequest.getI_p_file_size());
          } else {
               query.setParameter("i_p_file_size", null);
          }

          if (riplAgingRequest.getI_p_file_nm() != null) {
               query.setParameter("i_p_file_nm", riplAgingRequest.getI_p_file_nm());
          } else {
               query.setParameter("i_p_file_nm", null);
          }

          return (Integer) query.getSingleResult();
     }

     @Override
     public List<Object[]> sp_getriplagingrpt(BigInteger i_rpt_ripl_age_id) {
          Query query = entityManager.createNativeQuery("CALL sp_getriplagingrpt(:i_rpt_ripl_age_id)")
                    .setParameter("i_rpt_ripl_age_id", i_rpt_ripl_age_id);

          return query.getResultList();
     }

     @Override
     public Integer sp_getriplagequeuerpt() {
          Query query = entityManager.createNativeQuery("CALL sp_getriplagequeuerpt()");

          return (Integer) query.getSingleResult();
     }

     @Override
     public List<Object[]> sp_getpendingriplagingrpt() {
          Query query = entityManager.createNativeQuery("CALL sp_getpendingriplagingrpt()");

          return query.getResultList();
     }

     @Override
     public Integer sp_getpendingriplagingrptbyid(BigInteger i_rpt_ripl_age_id) {
          Query query = entityManager.createNativeQuery("CALL sp_getpendingriplagingrptbyid(:i_rpt_ripl_age_id)")
                    .setParameter("i_rpt_ripl_age_id", i_rpt_ripl_age_id);

          return (Integer) query.getSingleResult();
     }

     // #endregion

}
