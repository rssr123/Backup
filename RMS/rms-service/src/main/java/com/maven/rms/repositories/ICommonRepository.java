package com.maven.rms.repositories;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.ICommonInterface;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.GHLPaymentResponse;
import com.maven.rms.models.MFT;
import com.maven.rms.models.SourceSystemCodeRequest;
import com.maven.rms.models.WhiteIPReq;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public class ICommonRepository implements ICommonInterface {

     @PersistenceContext
     private EntityManager entityManager;

     // #region param
     @Override
     public List<Object[]> sp_getparam(Integer page, Integer size, String paramCd, String paramGrpNm) {
          Query query = entityManager
                    .createNativeQuery("CALL sp_getparam(:i_page, :i_size, :i_param_cd, :i_param_grp_nm)")
                    .setParameter("i_page", page)
                    .setParameter("i_size", size)
                    .setParameter("i_param_cd", paramCd)
                    .setParameter("i_param_grp_nm", paramGrpNm);
          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getsourcesystem(SourceSystemCodeRequest sourceSystemCodeRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getsourcesystem(:i_page, :i_size, :i_ss_id, :i_ss_cd, :i_ss_nm, :i_modified_by, :i_dt_modified_fr, :i_dt_modified_to, :i_status)")
                    .setParameter("i_page", sourceSystemCodeRequest.getI_page())
                    .setParameter("i_size", sourceSystemCodeRequest.getI_size())
                    .setParameter("i_ss_id", sourceSystemCodeRequest.getI_ss_id())
                    .setParameter("i_ss_cd", sourceSystemCodeRequest.getI_ss_cd())
                    .setParameter("i_ss_nm", sourceSystemCodeRequest.getI_ss_nm())
                    .setParameter("i_modified_by", sourceSystemCodeRequest.getI_modified_by())
                    .setParameter("i_dt_modified_fr", sourceSystemCodeRequest.getI_dt_modified_fr())
                    .setParameter("i_dt_modified_to", sourceSystemCodeRequest.getI_dt_modified_to())
                    .setParameter("i_status", sourceSystemCodeRequest.getI_status());
          // Query query = entityManager.createNativeQuery(
          // "CALL sp_getsourcesystem(:i_page, :i_size, :i_ss_id, :i_ss_cd, :i_ss_nm,
          // :i_modified_by, :i_dt_modified_fr, :i_dt_modified_to, :i_status)")
          // .setParameter("i_page", i_page)
          // .setParameter("i_size", i_size)
          // .setParameter("i_ss_id", i_ss_id)
          // .setParameter("i_ss_cd", i_ss_cd)
          // .setParameter("i_ss_nm", i_ss_nm)
          // .setParameter("i_modified_by", i_modified_by)
          // .setParameter("i_dt_modified_fr", i_dt_modified_fr)
          // .setParameter("i_dt_modified_to", i_dt_modified_to)
          // .setParameter("i_status", i_status);
          return query.getResultList();
     }
     // #endregion

     // #region user role
     // @Override
     // public List<Object[]> sp_getuserbyrole(Integer i_page, Integer i_size, String
     // i_role_nm_en, String i_role_nm_bm,
     // String i_status) {
     // Query query = entityManager
     // .createNativeQuery(
     // "CALL sp_getuserbyrole(:i_page, :i_size, :i_role_nm_en, :i_role_nm_bm,
     // :i_status)")
     // .setParameter("i_page", i_page)
     // .setParameter("i_size", i_size)
     // .setParameter("i_role_nm_en", i_role_nm_en)
     // .setParameter("i_role_nm_bm", i_role_nm_bm)
     // .setParameter("i_status", i_status);
     // return query.getResultList();
     // }

     // // @Override
     // public Object sp_getuserdetail(String i_ssm4uuserrefno) {
     // Query query = entityManager.createNativeQuery("CALL
     // sp_getuserdetail(:i_ssm4uuserrefno)")
     // .setParameter("i_ssm4uuserrefno", i_ssm4uuserrefno);
     // return query.getSingleResult();
     // }
     // #endregion

     // #region mft
     public List<Object[]> sp_getMFTWFByStatusAndEffDate(String status) {
          Query query = entityManager.createNativeQuery("CALL sp_getMFTWFByStatusAndEffDate(:i_status)")
                    .setParameter("i_status", status);
          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getmft(Integer i_page, Integer i_size, Integer i_fee_detail_pk, String i_fee_detail_id,
               BigDecimal i_unit_fee_fr, BigDecimal i_unit_fee_to, String i_ss_cd, String i_tax_cd,
               Date i_dt_modified_fr, Date i_dt_modified_to, String i_modified_by, String i_status) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getmft(:i_page, :i_size, :i_fee_detail_pk, :i_fee_detail_id,  :i_unit_fee_fr, :i_unit_fee_to, :i_ss_cd, :i_tax_cd,  :i_dt_modified_fr,  :i_dt_modified_to ,  :i_modified_by, :i_status)")
                    .setParameter("i_page", i_page)
                    .setParameter("i_size", i_size)
                    .setParameter("i_fee_detail_pk", i_fee_detail_pk)
                    .setParameter("i_fee_detail_id", i_fee_detail_id)
                    .setParameter("i_unit_fee_fr", i_unit_fee_fr)
                    .setParameter("i_unit_fee_to", i_unit_fee_to)
                    .setParameter("i_ss_cd", i_ss_cd)
                    .setParameter("i_tax_cd", i_tax_cd)
                    .setParameter("i_dt_modified_fr", i_dt_modified_fr != null ? i_dt_modified_fr : null)
                    .setParameter("i_dt_modified_to", i_dt_modified_to != null ? i_dt_modified_to : null)
                    .setParameter("i_modified_by", i_modified_by)
                    .setParameter("i_status", i_status);

          // Check if the dates are null and set accordingly
          if (i_dt_modified_fr != null) {
               query.setParameter("i_dt_modified_fr", i_dt_modified_fr);
          } else {
               query.setParameter("i_dt_modified_fr", null);
          }

          if (i_dt_modified_to != null) {
               query.setParameter("i_dt_modified_to", i_dt_modified_to);
          } else {
               query.setParameter("i_dt_modified_to", null);
          }

          return query.getResultList();
     }

     public List<Object[]> sp_getMftWFilter(Integer i_fee_detail_pk) {
          Query query = entityManager.createNativeQuery("CALL sp_getmasterfeetablewithfilter(:i_fee_detail_pk)")
                    .setParameter("i_fee_detail_pk", i_fee_detail_pk);

          return query.getResultList();
     }

     public Integer sp_insMFT(MFT mft) {
          Query query = entityManager
                    .createNativeQuery("CALL sp_insmasterfeetable(:i_fee_detail_id, :i_fee_grp_id, :i_fee_detail_nm_e,"
                              + ":i_fee_detail_nm_b, :i_unit_fee, :i_promo_startdt, :i_promo_enddt, :i_promo_fee, :i_tax_cd_id,"
                              + ":i_allow_otc, :i_ll_parent_id, :i_ll_start_day, :i_ll_start_mth, :i_ll_end_day, :i_ll_end_mth,"
                              + ":i_ledger_cd, :i_ss_cd, :i_created_by, :i_modified_by, :i_status)")
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
                    .setParameter("i_created_by", mft.getCreated_by())
                    .setParameter("i_modified_by", mft.getModified_by())
                    .setParameter("i_status", mft.getStatus());

          return (Integer) query.getSingleResult();
     }

     public Integer sp_updMFT(MFT mft) {
          Query query = entityManager
                    .createNativeQuery("CALL sp_updmasterfeetable(:i_fee_detail_id, :i_fee_grp_id, :i_fee_detail_nm_e,"
                              + ":i_fee_detail_nm_b, :i_unit_fee, :i_promo_startdt, :i_promo_enddt, :i_promo_fee, :i_tax_cd_id,"
                              + ":i_allow_otc, :i_ll_parent_id, :i_ll_start_day, :i_ll_start_mth, :i_ll_end_day, :i_ll_end_mth,"
                              + ":i_ledger_cd, :i_ss_cd, :i_modified_by, :i_status)")
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
                    .setParameter("i_status", mft.getStatus());

          return (Integer) query.getSingleResult();
     }

     public Integer sp_updateMFTWFStatus(BigInteger i_wf_id, String i_status) {
          Query query = entityManager.createNativeQuery("CALL sp_updmftwfstatus(:i_wf_id, :i_status)")
                    .setParameter("i_wf_id", i_wf_id)
                    .setParameter("i_status", i_status);
          return (Integer) query.getSingleResult();
     }

     @Override
     public Integer sp_updmftwf_status(BigInteger i_wf_id, String i_assign_to, String i_status, String i_remark,
               String i_modified_by) {
          Query query = entityManager
                    .createNativeQuery(
                              "CALL sp_updmftwf_status(:i_wf_id, :i_assign_to, :i_status, :i_remark, :i_modified_by)")
                    .setParameter("i_wf_id", i_wf_id)
                    .setParameter("i_assign_to", i_assign_to)
                    .setParameter("i_status", i_status)
                    .setParameter("i_remark", i_remark != null ? i_remark : null)
                    .setParameter("i_modified_by", i_modified_by);

          if (i_remark != null) {
               query.setParameter("i_remark", i_remark);
          } else {
               query.setParameter("i_remark", null);
          }

          return (Integer) query.getSingleResult();
     }

     @Override
     public List<Object[]> sp_getmftwf(Integer i_page, Integer i_size, BigInteger i_wf_id, Integer i_fee_detail_pk,
               String i_fee_detail_id, String i_assign_to, String i_status, String i_created_by, String i_modified_by,
               String i_modified_by_nm, Date i_dt_modified_fr, Date i_dt_modified_to,
               Date i_dt_created_fr, Date i_dt_created_to, Date i_dt_effective_fr, Date i_dt_effective_to,
               String i_ss_cd, String i_wf_is_in_prg) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getmftwf(:i_page, :i_size, :i_wf_id, :i_fee_detail_pk, :i_fee_detail_id, :i_assign_to, :i_status , :i_created_by,  :i_modified_by, :i_modified_by_nm, :i_dt_modified_fr, :i_dt_modified_to, :i_dt_created_fr, :i_dt_created_to, :i_dt_effective_fr, :i_dt_effective_to, :i_ss_cd, :i_wf_is_in_prg)")
                    .setParameter("i_page", i_page)
                    .setParameter("i_size", i_size)
                    .setParameter("i_wf_id", i_wf_id)
                    .setParameter("i_fee_detail_pk", i_fee_detail_pk)
                    .setParameter("i_fee_detail_id", i_fee_detail_id)
                    .setParameter("i_assign_to", i_assign_to)
                    .setParameter("i_status", i_status)
                    .setParameter("i_created_by", i_created_by)
                    .setParameter("i_modified_by", i_modified_by)
                    .setParameter("i_modified_by_nm", i_modified_by_nm)
                    .setParameter("i_dt_modified_fr", i_dt_modified_fr)
                    .setParameter("i_dt_modified_to", i_dt_modified_to)
                    .setParameter("i_dt_created_fr", i_dt_created_fr)
                    .setParameter("i_dt_created_to", i_dt_created_to)
                    .setParameter("i_dt_effective_fr", i_dt_effective_fr)
                    .setParameter("i_dt_effective_to", i_dt_effective_to)
                    .setParameter("i_ss_cd", i_ss_cd)
                    .setParameter("i_wf_is_in_prg", i_wf_is_in_prg);
          return query.getResultList();
     }

     @Override
     public BigInteger sp_insmftwf(Integer i_fee_detail_pk, String i_fee_detail_id, Integer i_fee_grp_id,
               String i_fee_detail_nm_e, String i_fee_detail_nm_b, BigDecimal i_fee_amt, Date i_promo_startdt,
               Date i_promo_enddt, BigDecimal i_promo_fee, Integer i_tax_cd_id, Integer i_allow_otc,
               String i_ll_parent_id, Integer i_ll_start_day, Integer i_ll_start_mth, Integer i_ll_end_day,
               Integer i_ll_end_mth,
               String i_ledger_cd, String i_ss_cd, String i_created_by, String i_modified_by, String i_status,
               Date i_effective_date, String i_remark, String i_assign_to, String i_action,
               String i_r_fee_det_nm, BigDecimal i_r_fee_amt, String i_r_ss_cd, Date i_r_promo_startdt,
               Date i_r_promo_enddt, Integer i_r_ll_required, String i_r_add_notes, String i_mft_status,
               BigDecimal i_r_promo_fee) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_insmftwf(:i_fee_detail_pk, :i_fee_detail_id, :i_fee_grp_id, :i_fee_detail_nm_e, :i_fee_detail_nm_b, :i_fee_amt, :i_promo_startdt, :i_promo_enddt, :i_promo_fee, :i_tax_cd_id, :i_allow_otc, :i_ll_parent_id, :i_ll_start_day, :i_ll_start_mth, :i_ll_end_day, :i_ll_end_mth, :i_ledger_cd, :i_ss_cd, :i_created_by, :i_modified_by, :i_status, :i_effective_date, :i_remark, :i_assign_to, :i_action, :i_r_fee_det_nm, :i_r_fee_amt, :i_r_ss_cd, :i_r_promo_startdt, :i_r_promo_enddt, :i_r_ll_required, :i_r_add_notes, :i_mft_status, :i_r_promo_fee)")
                    .setParameter("i_fee_detail_pk", i_fee_detail_pk != null ? i_fee_detail_pk : null)
                    .setParameter("i_fee_detail_id", i_fee_detail_id)
                    .setParameter("i_fee_grp_id", i_fee_grp_id)
                    .setParameter("i_fee_detail_nm_e", i_fee_detail_nm_e)
                    .setParameter("i_fee_detail_nm_b", i_fee_detail_nm_b)
                    .setParameter("i_fee_amt", i_fee_amt)
                    .setParameter("i_promo_startdt", i_promo_startdt != null ? i_promo_startdt : null)
                    .setParameter("i_promo_enddt", i_promo_enddt != null ? i_promo_enddt : null)
                    .setParameter("i_promo_fee", i_promo_fee != null ? i_promo_fee : null)
                    .setParameter("i_tax_cd_id", i_tax_cd_id)
                    .setParameter("i_allow_otc", i_allow_otc)
                    .setParameter("i_ll_parent_id", i_ll_parent_id != null ? i_ll_parent_id : null)
                    .setParameter("i_ll_start_day", i_ll_start_day != null ? i_ll_start_day : null)
                    .setParameter("i_ll_start_mth", i_ll_start_mth != null ? i_ll_start_mth : null)
                    .setParameter("i_ll_end_day", i_ll_end_day != null ? i_ll_end_day : null)
                    .setParameter("i_ll_end_mth", i_ll_end_mth != null ? i_ll_end_mth : null)
                    .setParameter("i_ledger_cd", i_ledger_cd)
                    .setParameter("i_ss_cd", i_ss_cd)
                    .setParameter("i_created_by", i_created_by)
                    .setParameter("i_modified_by", i_modified_by)
                    .setParameter("i_status", i_status)
                    .setParameter("i_effective_date", i_effective_date)
                    .setParameter("i_remark", i_remark != null ? i_remark : null)
                    .setParameter("i_assign_to", i_assign_to)
                    .setParameter("i_action", i_action)
                    .setParameter("i_r_fee_det_nm", i_r_fee_det_nm != null ? i_r_fee_det_nm : null)
                    .setParameter("i_r_fee_amt", i_r_fee_amt != null ? i_r_fee_amt : null)
                    .setParameter("i_r_ss_cd", i_r_ss_cd != null ? i_r_ss_cd : null)
                    .setParameter("i_r_promo_startdt", i_r_promo_startdt != null ? i_r_promo_startdt : null)
                    .setParameter("i_r_promo_enddt", i_r_promo_enddt != null ? i_r_promo_enddt : null)
                    .setParameter("i_r_ll_required", i_r_ll_required != null ? i_r_ll_required : null)
                    .setParameter("i_r_add_notes", i_r_add_notes != null ? i_r_add_notes : null)
                    .setParameter("i_mft_status", i_mft_status != null ? i_mft_status : null)
                    .setParameter("i_r_promo_fee", i_r_promo_fee != null ? i_r_promo_fee : null);

          // Check if the dates are null and set accordingly
          if (i_fee_detail_pk != null) {
               query.setParameter("i_fee_detail_pk", i_fee_detail_pk);
          } else {
               query.setParameter("i_fee_detail_pk", null);
          }

          if (i_promo_startdt != null) {
               query.setParameter("i_promo_startdt", i_promo_startdt);
          } else {
               query.setParameter("i_promo_startdt", null);
          }

          if (i_promo_enddt != null) {
               query.setParameter("i_promo_enddt", i_promo_enddt);
          } else {
               query.setParameter("i_promo_enddt", null);
          }

          if (i_promo_fee != null) {
               query.setParameter("i_promo_fee", i_promo_fee);
          } else {
               query.setParameter("i_promo_fee", null);
          }

          if (i_ll_parent_id != null) {
               query.setParameter("i_ll_parent_id", i_ll_parent_id);
          } else {
               query.setParameter("i_ll_parent_id", null);
          }

          if (i_ll_start_day != null) {
               query.setParameter("i_ll_start_day", i_ll_start_day);
          } else {
               query.setParameter("i_ll_start_day", null);
          }

          if (i_ll_start_mth != null) {
               query.setParameter("i_ll_start_mth", i_ll_start_mth);
          } else {
               query.setParameter("i_ll_start_mth", null);
          }

          if (i_ll_end_day != null) {
               query.setParameter("i_ll_end_day", i_ll_end_day);
          } else {
               query.setParameter("i_ll_end_day", null);
          }

          if (i_ll_end_mth != null) {
               query.setParameter("i_ll_end_mth", i_ll_end_mth);
          } else {
               query.setParameter("i_ll_end_mth", null);
          }

          if (i_remark != null) {
               query.setParameter("i_remark", i_remark);
          } else {
               query.setParameter("i_remark", null);
          }

          if (i_r_fee_det_nm != null) {
               query.setParameter("i_r_fee_det_nm", i_r_fee_det_nm);
          } else {
               query.setParameter("i_r_fee_det_nm", null);
          }

          if (i_r_fee_amt != null) {
               query.setParameter("i_r_fee_amt", i_r_fee_amt);
          } else {
               query.setParameter("i_r_fee_amt", null);
          }

          if (i_r_ss_cd != null) {
               query.setParameter("i_r_ss_cd", i_r_ss_cd);
          } else {
               query.setParameter("i_r_ss_cd", null);
          }

          if (i_r_promo_startdt != null) {
               query.setParameter("i_r_promo_startdt", i_r_promo_startdt);
          } else {
               query.setParameter("i_r_promo_startdt", null);
          }

          if (i_r_promo_enddt != null) {
               query.setParameter("i_r_promo_enddt", i_r_promo_enddt);
          } else {
               query.setParameter("i_r_promo_enddt", null);
          }

          if (i_r_ll_required != null) {
               query.setParameter("i_r_ll_required", i_r_ll_required);
          } else {
               query.setParameter("i_r_ll_required", null);
          }

          if (i_r_add_notes != null) {
               query.setParameter("i_r_add_notes", i_r_add_notes);
          } else {
               query.setParameter("i_r_add_notes", null);
          }

          if (i_mft_status != null) {
               query.setParameter("i_mft_status", i_mft_status);
          } else {
               query.setParameter("i_mft_status", null);
          }

          if (i_r_promo_fee != null) {
               query.setParameter("i_r_promo_fee", i_r_promo_fee);
          } else {
               query.setParameter("i_r_promo_fee", null);
          }

          BigInteger result = (BigInteger) query.getSingleResult();
          return result;
     }

     @Override
     public List<Object[]> sp_getmftwfhis(Integer i_page, Integer i_size, BigInteger i_wf_id, String i_status) {
          Query query = entityManager.createNativeQuery("CALL sp_getmftwfhis(:i_page, :i_size, :i_wf_id,  :i_status )")
                    .setParameter("i_page", i_page)
                    .setParameter("i_size", i_size)
                    .setParameter("i_wf_id", i_wf_id)
                    .setParameter("i_status", i_status);
          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getwfh_ast(String i_task_id, String i_status) {
          Query query = entityManager.createNativeQuery("CALL sp_getwfh_ast(:i_task_id, :i_status)")
                    .setParameter("i_task_id", i_task_id)
                    .setParameter("i_status", i_status);
          return query.getResultList();

     }

     @Override
     public Integer sp_updmftwf(BigInteger i_wf_id, Integer i_fee_detail_pk, String i_fee_detail_id,
               Integer i_fee_grp_id, String i_fee_detail_nm_e, String i_fee_detail_nm_b, BigDecimal i_fee_amt,
               Date i_promo_startdt,
               Date i_promo_enddt, BigDecimal i_promo_fee, Integer i_tax_cd_id, Integer i_allow_otc,
               String i_ll_parent_id, Integer i_ll_start_day, Integer i_ll_end_day, Integer i_ll_start_mth,
               Integer i_ll_end_mth,
               String i_ledger_cd, String i_ss_cd, Date i_effective_date, String i_modified_by, String i_status,
               String i_assign_to, String i_remark, String i_action, String i_r_fee_det_nm, BigDecimal i_r_fee_amt,
               String i_r_ss_cd,
               Date i_r_promo_startdt, Date i_r_promo_enddt, Integer i_r_ll_required, String i_r_add_notes,
               String i_mft_status, BigDecimal i_r_promo_fee) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_updmftwf(:i_wf_id, :i_fee_detail_pk, :i_fee_detail_id, :i_fee_grp_id, :i_fee_detail_nm_e, :i_fee_detail_nm_b, :i_fee_amt, :i_promo_startdt, :i_promo_enddt, :i_promo_fee, :i_tax_cd_id, :i_allow_otc, :i_ll_parent_id, :i_ll_start_day, :i_ll_end_day, :i_ll_start_mth,  :i_ll_end_mth, :i_ledger_cd, :i_ss_cd, :i_effective_date, :i_modified_by, :i_status, :i_assign_to, :i_remark, :i_action, :i_r_fee_det_nm, :i_r_fee_amt, :i_r_ss_cd, :i_r_promo_startdt, :i_r_promo_enddt, :i_r_ll_required, :i_r_add_notes, :i_mft_status, :i_r_promo_fee)")
                    .setParameter("i_wf_id", i_wf_id)
                    .setParameter("i_fee_detail_pk", i_fee_detail_pk != null ? i_fee_detail_pk : null)
                    .setParameter("i_fee_detail_id", i_fee_detail_id)
                    .setParameter("i_fee_grp_id", i_fee_grp_id)
                    .setParameter("i_fee_detail_nm_e", i_fee_detail_nm_e)
                    .setParameter("i_fee_detail_nm_b", i_fee_detail_nm_b)
                    .setParameter("i_fee_amt", i_fee_amt)
                    .setParameter("i_promo_startdt", i_promo_startdt != null ? i_promo_startdt : null)
                    .setParameter("i_promo_enddt", i_promo_enddt != null ? i_promo_enddt : null)
                    .setParameter("i_promo_fee", i_promo_fee != null ? i_promo_fee : null)
                    .setParameter("i_tax_cd_id", i_tax_cd_id)
                    .setParameter("i_allow_otc", i_allow_otc)
                    .setParameter("i_ll_parent_id", i_ll_parent_id != null ? i_ll_parent_id : null)
                    .setParameter("i_ll_start_day", i_ll_start_day != null ? i_ll_start_day : null)
                    .setParameter("i_ll_end_day", i_ll_end_day != null ? i_ll_end_day : null)
                    .setParameter("i_ll_start_mth", i_ll_start_mth != null ? i_ll_start_mth : null)
                    .setParameter("i_ll_end_mth", i_ll_end_mth != null ? i_ll_end_mth : null)
                    .setParameter("i_ledger_cd", i_ledger_cd)
                    .setParameter("i_ss_cd", i_ss_cd)
                    .setParameter("i_effective_date", i_effective_date)
                    .setParameter("i_modified_by", i_modified_by)
                    .setParameter("i_status", i_status)
                    .setParameter("i_assign_to", i_assign_to != null ? i_assign_to : null)
                    .setParameter("i_remark", i_remark)
                    .setParameter("i_action", i_action)
                    .setParameter("i_r_fee_det_nm", i_r_fee_det_nm != null ? i_r_fee_det_nm : null)
                    .setParameter("i_r_fee_amt", i_r_fee_amt != null ? i_r_fee_amt : null)
                    .setParameter("i_r_ss_cd", i_r_ss_cd != null ? i_r_ss_cd : null)
                    .setParameter("i_r_promo_startdt", i_r_promo_startdt != null ? i_r_promo_startdt : null)
                    .setParameter("i_r_promo_enddt", i_r_promo_enddt != null ? i_r_promo_enddt : null)
                    .setParameter("i_r_ll_required", i_r_ll_required != null ? i_r_ll_required : null)
                    .setParameter("i_r_add_notes", i_r_add_notes != null ? i_r_add_notes : null)
                    .setParameter("i_mft_status", i_mft_status != null ? i_mft_status : null)
                    .setParameter("i_r_promo_fee", i_r_promo_fee != null ? i_r_promo_fee : null);

          // Check if the dates are null and set accordingly
          if (i_fee_detail_pk != null) {
               query.setParameter("i_fee_detail_pk", i_fee_detail_pk);
          } else {
               query.setParameter("i_fee_detail_pk", null);
          }

          if (i_promo_startdt != null) {
               query.setParameter("i_promo_startdt", i_promo_startdt);
          } else {
               query.setParameter("i_promo_startdt", null);
          }

          if (i_promo_enddt != null) {
               query.setParameter("i_promo_enddt", i_promo_enddt);
          } else {
               query.setParameter("i_promo_enddt", null);
          }

          if (i_promo_fee != null) {
               query.setParameter("i_promo_fee", i_promo_fee);
          } else {
               query.setParameter("i_promo_fee", null);
          }

          if (i_ll_parent_id != null) {
               query.setParameter("i_ll_parent_id", i_ll_parent_id);
          } else {
               query.setParameter("i_ll_parent_id", null);
          }

          if (i_ll_start_day != null) {
               query.setParameter("i_ll_start_day", i_ll_start_day);
          } else {
               query.setParameter("i_ll_start_day", null);
          }

          if (i_ll_start_mth != null) {
               query.setParameter("i_ll_start_mth", i_ll_start_mth);
          } else {
               query.setParameter("i_ll_start_mth", null);
          }

          if (i_ll_end_day != null) {
               query.setParameter("i_ll_end_day", i_ll_end_day);
          } else {
               query.setParameter("i_ll_end_day", null);
          }

          if (i_ll_end_mth != null) {
               query.setParameter("i_ll_end_mth", i_ll_end_mth);
          } else {
               query.setParameter("i_ll_end_mth", null);
          }

          if (i_assign_to != null) {
               query.setParameter("i_assign_to", i_assign_to);
          } else {
               query.setParameter("i_assign_to", null);
          }

          if (i_r_fee_det_nm != null) {
               query.setParameter("i_r_fee_det_nm", i_r_fee_det_nm);
          } else {
               query.setParameter("i_r_fee_det_nm", null);
          }

          if (i_r_fee_amt != null) {
               query.setParameter("i_r_fee_amt", i_r_fee_amt);
          } else {
               query.setParameter("i_r_fee_amt", null);
          }

          if (i_r_ss_cd != null) {
               query.setParameter("i_r_ss_cd", i_r_ss_cd);
          } else {
               query.setParameter("i_r_ss_cd", null);
          }

          if (i_r_promo_startdt != null) {
               query.setParameter("i_r_promo_startdt", i_r_promo_startdt);
          } else {
               query.setParameter("i_r_promo_startdt", null);
          }

          if (i_r_promo_enddt != null) {
               query.setParameter("i_r_promo_enddt", i_r_promo_enddt);
          } else {
               query.setParameter("i_r_promo_enddt", null);
          }

          if (i_r_ll_required != null) {
               query.setParameter("i_r_ll_required", i_r_ll_required);
          } else {
               query.setParameter("i_r_ll_required", null);
          }

          if (i_r_add_notes != null) {
               query.setParameter("i_r_add_notes", i_r_add_notes);
          } else {
               query.setParameter("i_r_add_notes", null);
          }

          if (i_mft_status != null) {
               query.setParameter("i_mft_status", i_mft_status);
          } else {
               query.setParameter("i_mft_status", null);
          }

          if (i_r_promo_fee != null) {
               query.setParameter("i_r_promo_fee", i_r_promo_fee);
          } else {
               query.setParameter("i_r_promo_fee", null);
          }

          return (Integer) query.getSingleResult();

     }

     @Override
     public Integer sp_insmftwfdoc(BigInteger i_wf_id, String i_file_nm, Blob i_file_content, String i_file_type,
               Integer i_file_size,
               String i_created_by, String i_modified_by, String i_status) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_insmftwfdoc(:i_wf_id, :i_file_nm, :i_file_content, :i_file_type, :i_file_size, :i_created_by, :i_modified_by, :i_status)")
                    .setParameter("i_wf_id", i_wf_id)
                    .setParameter("i_file_nm", i_file_nm)
                    .setParameter("i_file_content", i_file_content)
                    .setParameter("i_file_type", i_file_type)
                    .setParameter("i_file_size", i_file_size)
                    .setParameter("i_created_by", i_created_by)
                    .setParameter("i_modified_by", i_modified_by)
                    .setParameter("i_status", i_status);
          return (Integer) query.getSingleResult();
     }

     @Override
     public List<Object[]> sp_getmftwfdoc(Integer i_page, Integer i_size, BigInteger i_wf_id, String i_status) {
          Query query = entityManager.createNativeQuery("CALL sp_getmftwfdoc(:i_page, :i_size, :i_wf_id, :i_status)")
                    .setParameter("i_page", i_page)
                    .setParameter("i_size", i_size)
                    .setParameter("i_wf_id", i_wf_id)
                    .setParameter("i_status", i_status);

          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getwfh_status(String i_task_id, String i_status) {
          Query query = entityManager.createNativeQuery("CALL sp_getwfh_status(:i_task_id, :i_status)")
                    .setParameter("i_task_id", i_task_id)
                    .setParameter("i_status", i_status);
          return query.getResultList();

     }

     @Override
     public List<Object[]> sp_getfeedetailitems(String fee_detail_id, Integer fee_grp_id, String ss_cd,
               LocalDateTime last_sync_dt, Integer exclude_deleted) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getfeedetailitems(:fee_detail_id, :fee_grp_id, :ss_cd, :last_sync_dt, :exclude_deleted)")

                    .setParameter("fee_detail_id", fee_detail_id != null ? fee_detail_id : null)
                    .setParameter("fee_grp_id", fee_grp_id != null ? fee_grp_id : null)
                    .setParameter("ss_cd", ss_cd != null ? ss_cd : null)
                    .setParameter("last_sync_dt", last_sync_dt != null ? last_sync_dt : null)
                    .setParameter("exclude_deleted", exclude_deleted != null ? exclude_deleted : null);

          if (fee_detail_id != null) {
               query.setParameter("fee_detail_id", fee_detail_id);
          } else {
               query.setParameter("fee_detail_id", null);
          }

          if (fee_grp_id != null) {
               query.setParameter("fee_grp_id", fee_grp_id);
          } else {
               query.setParameter("fee_grp_id", null);
          }

          if (ss_cd != null) {
               query.setParameter("ss_cd", ss_cd);
          } else {
               query.setParameter("ss_cd", null);
          }

          if (last_sync_dt != null) {
               query.setParameter("last_sync_dt", last_sync_dt);
          } else {
               query.setParameter("last_sync_dt", null);
          }

          if (exclude_deleted != null) {
               query.setParameter("exclude_deleted", exclude_deleted);
          } else {
               query.setParameter("exclude_deleted", null);
          }

          return query.getResultList();
     }

     @Override
     public Blob sp_getmftwfdocfilecontent(BigInteger i_wfdoc_id) {
          Query query = entityManager.createNativeQuery("CALL sp_getmftwfdocfilecontent(:i_wfdoc_id)")
                    .setParameter("i_wfdoc_id", i_wfdoc_id);

          return (Blob) query.getSingleResult();
     }
     // #endregion

     // #region mtt
     @Override
     public List<Object[]> sp_getMTTItem(Integer mttId) {
          Query query = entityManager.createNativeQuery("CALL sp_getMTTItem(:i_mtt_id)")
                    .setParameter("i_mtt_id", mttId);
          return query.getResultList();
     }

     @Override
     public Integer sp_updateMTT(String ornNo, String custNm, String custAddr1, String custAddr2, String custAddr3,
               String custPostCode, String custCity, String custState) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_updateMTT(:i_orn_no,:i_cust_nm,:i_cust_addr_1,:i_cust_addr_2,:i_cust_addr_3,:i_cust_postcode,:i_cust_city,:i_cust_state)")
                    .setParameter("i_orn_no", ornNo)
                    .setParameter("i_billing_nm", custNm)
                    .setParameter("i_cust_addr_1", custAddr1)
                    .setParameter("i_cust_addr_2", custAddr2)
                    .setParameter("i_cust_addr_3", custAddr3)
                    .setParameter("i_cust_postcode", custPostCode)
                    .setParameter("i_cust_city", custCity)
                    .setParameter("i_cust_state", custState);
          // Use getSingleResult to retrieve the single integer value
          Integer result = (Integer) query.getSingleResult();
          return result;
     }

     @Override
     public String sp_checkLatestOrderStatus(String ornNo) {
          // TODO Auto-generated method stub
          Query query = entityManager.createNativeQuery("CALL sp_checkLatestOrderStatus_v2(:i_orn_no)")
                    .setParameter("i_orn_no", ornNo);
          // Use getSingleResult to retrieve the single integer value
          String result = query.getSingleResult().toString();
          return result;
     }

     @Override
     public Object[] sp_insertPayment(Integer mttID, String pymtMethod, String serviceID, BigDecimal pymtAmt,
               String langCd, String usernameC, String usernameM) {
          // TODO Auto-generated method stub
          Query query = entityManager.createNativeQuery(
                    "CALL sp_inspayment(:i_mtt_id,:i_pg_pymt_method,:i_pg_service_id,:i_pg_pymt_amt,:i_pg_lang_cd,:i_username_c,:i_username_m)")
                    .setParameter("i_mtt_id", mttID)
                    .setParameter("i_pg_pymt_method", pymtMethod)
                    .setParameter("i_pg_service_id", serviceID)
                    .setParameter("i_pg_pymt_amt", pymtAmt)
                    .setParameter("i_pg_lang_cd", langCd)
                    .setParameter("i_username_c", usernameC)
                    .setParameter("i_username_m", usernameM);
          Object[] result = (Object[]) query.getSingleResult();
          return result;
     }

     @Override
     public Integer sp_updatePayment(GHLPaymentResponse ghlResponse, String usernameM) {
          // TODO Auto-generated method stub
          Query query = entityManager.createNativeQuery(
                    "CALL sp_updmttpg(:i_pymt_id ,:i_txn_type ,:i_pymt_method ,:i_service_id ,:i_pymt_amt ,:i_curr_cd,:i_txn_id,:i_issuing_bank,:i_auth_cd,:i_txn_status,:i_txn_msg,:i_hash_value,:i_hash_value2,:i_bank_refno,:i_token_type,:i_token,:i_resp_time,:i_cardno_mask,:i_cardholder,:i_cardtype,:i_cardexp,:i_modified_by)")
                    .setParameter("i_pymt_id", ghlResponse.getPaymentID())
                    .setParameter("i_txn_type", ghlResponse.getTransactionType())
                    .setParameter("i_pymt_method", ghlResponse.getPaymentMethod())
                    .setParameter("i_service_id", ghlResponse.getServiceID())
                    .setParameter("i_pymt_amt", ghlResponse.getAmount())
                    .setParameter("i_curr_cd", ghlResponse.getCurrencyCode())
                    .setParameter("i_txn_id", ghlResponse.getTxnID())
                    .setParameter("i_issuing_bank", ghlResponse.getIssuingBank())
                    .setParameter("i_auth_cd", ghlResponse.getAuthCode())
                    .setParameter("i_txn_status", ghlResponse.getTxnStatus())
                    .setParameter("i_txn_msg", ghlResponse.getTxnMsg())
                    .setParameter("i_hash_value", ghlResponse.getHashValue())
                    .setParameter("i_hash_value2", ghlResponse.getHashValue2())
                    .setParameter("i_bank_refno", ghlResponse.getBankRefNo())
                    .setParameter("i_token_type", ghlResponse.getTokenType())
                    .setParameter("i_token", ghlResponse.getToken())
                    .setParameter("i_resp_time", ghlResponse.getRespTime())
                    .setParameter("i_cardno_mask", ghlResponse.getCardNoMask())
                    .setParameter("i_cardholder", ghlResponse.getCardHolder())
                    .setParameter("i_cardtype", ghlResponse.getCardType())
                    .setParameter("i_cardexp", ghlResponse.getCardExp())
                    .setParameter("i_modified_by", usernameM);
          return (Integer) query.getSingleResult();
     }

     @Override
     public Integer sp_checkPaymentRcpt(String ornNo) {
          // TODO Auto-generated method stub
          Query query = entityManager.createNativeQuery(
                    "CALL sp_checkpymtrcpt(:i_orn_no)")
                    .setParameter("i_orn_no", ornNo);
          return (Integer) query.getSingleResult();
     }

     @Override
     public Object[] sp_insertReceipt(String paymentId, String username) {
          // TODO Auto-generated method stub
          Query query = entityManager.createNativeQuery(
                    "CALL sp_insmttrcpt(:i_pg_pymt_id, :i_created_by, :i_modified_by)")
                    .setParameter("i_pg_pymt_id", paymentId)
                    .setParameter("i_created_by", username)
                    .setParameter("i_modified_by", username);
          Object[] result = (Object[]) query.getSingleResult();
          return result;
     }

     @Override
     public Integer sp_updateMTTRcpt(Integer mttRcptID, String verID, String ssDocRefID) {
          // TODO Auto-generated method stub
          Query query = entityManager.createNativeQuery(
                    "CALL sp_updmttrcpt(:i_mtt_rcpt_id, :i_ver_id, :i_ssdocref_id)")
                    .setParameter("i_mtt_rcpt_id", mttRcptID)
                    .setParameter("i_ver_id", verID)
                    .setParameter("i_ssdocref_id", ssDocRefID);
          return (Integer) query.getSingleResult();
     }
     // #endregion

     // // #region Fee Group Start
     // @Override
     // public Integer sp_insfeegroup(FeeGrpRequest feeGroupRequest,
     // String i_created_by, String i_modified_by, String i_status) {
     // Query query = entityManager.createNativeQuery(
     // "CALL sp_insfeegroup(:i_fee_grp_nm_en, :i_fee_grp_nm_bm, :i_created_by,
     // :i_modified_by, :i_status)")
     // .setParameter("i_fee_grp_nm_en", feeGroupRequest.getI_fee_grp_nm_en())
     // .setParameter("i_fee_grp_nm_bm", feeGroupRequest.getI_fee_grp_nm_bm())
     // .setParameter("i_created_by", i_created_by)
     // .setParameter("i_modified_by", i_modified_by)
     // .setParameter("i_status", i_status);
     // return (Integer) query.getSingleResult();
     // }

     // @Override
     // public Integer sp_updfeegroup(FeeGrpRequest feeGroupRequest, String
     // i_fee_grp_nm_en, String i_fee_grp_nm_bm,
     // String i_modified_by, String i_status) {
     // Query query = entityManager.createNativeQuery(
     // "CALL sp_updfeegroup(:i_fee_grp_id, :i_fee_grp_nm_en, :i_fee_grp_nm_bm,
     // :i_modified_by, :i_status)")
     // .setParameter("i_fee_grp_id", feeGroupRequest.getI_fee_grp_id())
     // .setParameter("i_fee_grp_nm_en", i_fee_grp_nm_en)
     // .setParameter("i_fee_grp_nm_bm", i_fee_grp_nm_bm)
     // .setParameter("i_modified_by", i_modified_by)
     // .setParameter("i_status", i_status);
     // return (Integer) query.getSingleResult();
     // }

     // @Override
     // public List<Object[]> sp_getfeegroup_v2(FeeGrpRequest feeGroupRequest) {
     // Query query = entityManager.createNativeQuery(
     // "CALL sp_getfeegroup_v2(:i_page, :i_size, :i_fee_grp_nm_en, :i_fee_grp_nm_bm,
     // :i_modified_by, :i_dt_modified_fr, :i_dt_modified_to, :i_status)")
     // .setParameter("i_page", feeGroupRequest.getI_page())
     // .setParameter("i_size", feeGroupRequest.getI_size())
     // // .setParameter("i_fee_grp_id", i_fee_grp_id)
     // .setParameter("i_fee_grp_nm_en", feeGroupRequest.getI_fee_grp_nm_en())
     // .setParameter("i_fee_grp_nm_bm", feeGroupRequest.getI_fee_grp_nm_bm())
     // .setParameter("i_modified_by", feeGroupRequest.getI_modified_by())
     // .setParameter("i_dt_modified_fr", feeGroupRequest.getI_dt_modified_fr() !=
     // null ? feeGroupRequest.getI_dt_modified_fr() : null)
     // .setParameter("i_dt_modified_to", feeGroupRequest.getI_dt_modified_to() !=
     // null ? feeGroupRequest.getI_dt_modified_to() : null)
     // .setParameter("i_status", feeGroupRequest.getI_status());

     // // Check if the dates are null and set accordingly
     // if (feeGroupRequest.getI_dt_modified_fr() != null) {
     // query.setParameter("i_dt_modified_fr",
     // feeGroupRequest.getI_dt_modified_fr());
     // } else {
     // query.setParameter("i_dt_modified_fr", null);
     // }

     // if (feeGroupRequest.getI_dt_modified_to() != null) {
     // query.setParameter("i_dt_modified_to",
     // feeGroupRequest.getI_dt_modified_to());
     // } else {
     // query.setParameter("i_dt_modified_to", null);
     // }
     // return query.getResultList();
     // }

     // @Override
     // public Integer sp_checkfeegrpbyid(FeeGrpRequest feeGroupRequest) {
     // Query query = entityManager.createNativeQuery(
     // "CALL sp_checkfeetaxbyid(:i_fee_grp_id,:i_tax_cd_id)")
     // .setParameter("i_fee_grp_id", feeGroupRequest.getI_fee_grp_id())
     // .setParameter("i_tax_cd_id", "");
     // return (Integer) query.getSingleResult();
     // }
     // // #endregion

     // #region DI Aging start
     // @Override
     public BigInteger sp_insdiagingrpt(Date i_p_dt_req, Integer i_p_tmn_status, String i_p_ent_ty, String i_p_ent_nm,
               String i_p_txn_ty, String i_p_status, Date i_p_dt_exp_fr, Date i_p_dt_exp_to,
               Date i_p_dt_eff_fr, Date i_p_dt_eff_to, Date i_p_dt_app_fr, Date i_p_dt_app_to, Date i_p_dt_tmn_fr,
               Date i_p_dt_tmn_to, String i_created_by, String i_modified_by,
               String i_status, String i_p_email, String i_p_file_type, Integer i_p_file_size, String i_p_file_nm,
               String i_p_batch_no, String i_p_fms_ref_no) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_insdiagingrpt(:i_p_dt_req, :i_p_tmn_status, :i_p_ent_ty, :i_p_ent_nm, :i_p_txn_ty, :i_p_status, :i_p_dt_exp_fr, :i_p_dt_exp_to, :i_p_dt_eff_fr, :i_p_dt_eff_to, :i_p_dt_app_fr, :i_p_dt_app_to, :i_p_dt_tmn_fr, :i_p_dt_tmn_to, :i_created_by, :i_modified_by, :i_status, :i_p_email, :i_p_file_type, :i_p_file_size, :i_p_file_nm, :i_p_batch_no, :i_p_fms_ref_no)")
                    .setParameter("i_p_dt_req", i_p_dt_req)
                    .setParameter("i_p_tmn_status", i_p_tmn_status != null ? i_p_tmn_status : null)
                    .setParameter("i_p_ent_ty", i_p_ent_ty != null ? i_p_ent_ty : null)
                    .setParameter("i_p_ent_nm", i_p_ent_nm != null ? i_p_ent_nm : null)
                    .setParameter("i_p_txn_ty", i_p_txn_ty != null ? i_p_txn_ty : null)
                    .setParameter("i_p_status", i_p_status != null ? i_p_status : null)
                    .setParameter("i_p_dt_exp_fr", i_p_dt_exp_fr != null ? i_p_dt_exp_fr : null)
                    .setParameter("i_p_dt_exp_to", i_p_dt_exp_to != null ? i_p_dt_exp_to : null)
                    .setParameter("i_p_dt_eff_fr", i_p_dt_eff_fr)
                    .setParameter("i_p_dt_eff_to", i_p_dt_eff_to)
                    .setParameter("i_p_dt_app_fr", i_p_dt_app_fr != null ? i_p_dt_app_fr : null)
                    .setParameter("i_p_dt_app_to", i_p_dt_app_to != null ? i_p_dt_app_to : null)
                    .setParameter("i_p_dt_tmn_fr", i_p_dt_tmn_fr != null ? i_p_dt_tmn_fr : null)
                    .setParameter("i_p_dt_tmn_to", i_p_dt_tmn_to != null ? i_p_dt_tmn_to : null)
                    .setParameter("i_created_by", i_created_by)
                    .setParameter("i_modified_by", i_modified_by)
                    .setParameter("i_status", i_status)
                    .setParameter("i_p_email", i_p_email != null ? i_p_email : null)
                    .setParameter("i_p_file_type", i_p_file_type != null ? i_p_file_type : null)
                    .setParameter("i_p_file_size", i_p_file_size != null ? i_p_file_size : null)
                    .setParameter("i_p_file_nm", i_p_file_nm != null ? i_p_file_nm : null)
                    .setParameter("i_p_batch_no", i_p_batch_no != null ? i_p_batch_no : null)
                    .setParameter("i_p_fms_ref_no", i_p_fms_ref_no != null ? i_p_fms_ref_no : null);

          // Check if the dates are null and set accordingly
          if (i_p_tmn_status != null) {
               query.setParameter("i_p_tmn_status", i_p_tmn_status);
          } else {
               query.setParameter("i_p_tmn_status", null);
          }

          if (i_p_ent_ty != null) {
               query.setParameter("i_p_ent_ty", i_p_ent_ty);
          } else {
               query.setParameter("i_p_ent_ty", null);
          }

          if (i_p_ent_nm != null) {
               query.setParameter("i_p_ent_nm", i_p_ent_nm);
          } else {
               query.setParameter("i_p_ent_nm", null);
          }

          if (i_p_txn_ty != null) {
               query.setParameter("i_p_txn_ty", i_p_txn_ty);
          } else {
               query.setParameter("i_p_txn_ty", null);
          }

          if (i_p_status != null) {
               query.setParameter("i_p_status", i_p_status);
          } else {
               query.setParameter("i_p_status", null);
          }

          if (i_p_dt_eff_fr != null) {
               query.setParameter("i_p_dt_eff_fr", i_p_dt_eff_fr);
          } else {
               query.setParameter("i_p_dt_eff_fr", null);
          }

          if (i_p_dt_eff_to != null) {
               query.setParameter("i_p_dt_eff_to", i_p_dt_eff_to);
          } else {
               query.setParameter("i_p_dt_eff_to", null);
          }

          if (i_p_dt_app_fr != null) {
               query.setParameter("i_p_dt_app_fr", i_p_dt_app_fr);
          } else {
               query.setParameter("i_p_dt_app_fr", null);
          }

          if (i_p_dt_app_to != null) {
               query.setParameter("i_p_dt_app_to", i_p_dt_app_to);
          } else {
               query.setParameter("i_p_dt_app_to", null);
          }

          if (i_p_dt_tmn_fr != null) {
               query.setParameter("i_p_dt_tmn_fr", i_p_dt_tmn_fr);
          } else {
               query.setParameter("i_p_dt_tmn_fr", null);
          }

          if (i_p_dt_tmn_to != null) {
               query.setParameter("i_p_dt_tmn_to", i_p_dt_tmn_to);
          } else {
               query.setParameter("i_p_dt_tmn_to", null);
          }

          if (i_p_email != null) {
               query.setParameter("i_p_email", i_p_email);
          } else {
               query.setParameter("i_p_email", null);
          }

          if (i_p_file_type != null) {
               query.setParameter("i_p_file_type", i_p_file_type);
          } else {
               query.setParameter("i_p_file_type", null);
          }

          if (i_p_file_size != null) {
               query.setParameter("i_p_file_size", i_p_file_size);
          } else {
               query.setParameter("i_p_file_size", null);
          }

          if (i_p_file_nm != null) {
               query.setParameter("i_p_file_nm", i_p_file_nm);
          } else {
               query.setParameter("i_p_file_nm", null);
          }

          if (i_p_batch_no != null) {
               query.setParameter("i_p_batch_no", i_p_batch_no);
          } else {
               query.setParameter("i_p_batch_no", null);
          }

          if (i_p_fms_ref_no != null) {
               query.setParameter("i_p_fms_ref_no", i_p_fms_ref_no);
          } else {
               query.setParameter("i_p_fms_ref_no", null);
          }

          BigInteger result = (BigInteger) query.getSingleResult();
          return result;
     }

     // @Override
     public List<Object[]> sp_getdiaginglistingrpt(Integer i_page, Integer i_size, BigInteger i_rpt_di_age_id,
               Date i_p_dt_req, Integer i_p_tmn_status, String i_p_ent_ty, String i_p_ent_nm, String i_p_txn_ty,
               String i_p_status, Date i_p_dt_exp_fr, Date i_p_dt_exp_to,
               Date i_p_dt_eff_fr, Date i_p_dt_eff_to, Date i_p_dt_app_fr, Date i_p_dt_app_to, Date i_p_dt_tmn_fr,
               Date i_p_dt_tmn_to, String i_created_by, String i_modified_by, String i_status, String i_p_email,
               String i_p_file_type, Integer i_p_file_size, String i_p_file_nm,
               String i_p_batch_no, String i_p_fms_ref_no) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getdiaginglistingrpt(:i_page, :i_size, :i_rpt_di_age_id, :i_p_dt_req, :i_p_tmn_status, :i_p_ent_ty, :i_p_ent_nm, :i_p_txn_ty, :i_p_status, :i_p_dt_exp_fr, :i_p_dt_exp_to, :i_p_dt_eff_fr, :i_p_dt_eff_to, :i_p_dt_app_fr, :i_p_dt_app_to, :i_p_dt_tmn_fr, :i_p_dt_tmn_to, :i_created_by, :i_modified_by, :i_status, :i_p_email, :i_p_file_type, :i_p_file_size, :i_p_file_nm, :i_p_batch_no, :i_p_fms_ref_no)")
                    .setParameter("i_page", i_page)
                    .setParameter("i_size", i_size)
                    .setParameter("i_rpt_di_age_id", i_rpt_di_age_id)
                    .setParameter("i_p_dt_req", i_p_dt_req)
                    .setParameter("i_p_tmn_status", i_p_tmn_status)
                    .setParameter("i_p_ent_ty", i_p_ent_ty)
                    .setParameter("i_p_ent_nm", i_p_ent_nm)
                    .setParameter("i_p_txn_ty", i_p_txn_ty)
                    .setParameter("i_p_status", i_p_status)
                    .setParameter("i_p_dt_exp_fr", i_p_dt_exp_fr)
                    .setParameter("i_p_dt_exp_to", i_p_dt_exp_to)
                    .setParameter("i_p_dt_eff_fr", i_p_dt_eff_fr)
                    .setParameter("i_p_dt_eff_to", i_p_dt_eff_to)
                    .setParameter("i_p_dt_app_fr", i_p_dt_app_fr)
                    .setParameter("i_p_dt_app_to", i_p_dt_app_to)
                    .setParameter("i_p_dt_tmn_fr", i_p_dt_tmn_fr)
                    .setParameter("i_p_dt_tmn_to", i_p_dt_tmn_to)
                    .setParameter("i_created_by", i_created_by)
                    .setParameter("i_modified_by", i_modified_by)
                    .setParameter("i_status", i_status)
                    .setParameter("i_p_email", i_p_email)
                    .setParameter("i_p_file_type", i_p_file_type)
                    .setParameter("i_p_file_size", i_p_file_size)
                    .setParameter("i_p_file_nm", i_p_file_nm)
                    .setParameter("i_p_batch_no", i_p_batch_no)
                    .setParameter("i_p_fms_ref_no", i_p_fms_ref_no);

          return query.getResultList();
     }

     public Integer sp_upddiagingrpt(BigInteger i_rpt_di_age_id, String i_status, Integer i_p_file_size,
               String i_p_file_nm, String i_modified_by) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_upddiagingrpt(:i_rpt_di_age_id, :i_status, :i_p_file_size, :i_p_file_nm, :i_modified_by)")
                    .setParameter("i_rpt_di_age_id", i_rpt_di_age_id)
                    .setParameter("i_status", i_status)
                    .setParameter("i_p_file_size", i_p_file_size != null ? i_p_file_size : null)
                    .setParameter("i_p_file_nm", i_p_file_nm != null ? i_p_file_nm : null)
                    .setParameter("i_modified_by", i_modified_by);

          if (i_p_file_size != null) {
               query.setParameter("i_p_file_size", i_p_file_size);
          } else {
               query.setParameter("i_p_file_size", null);
          }

          if (i_p_file_nm != null) {
               query.setParameter("i_p_file_nm", i_p_file_nm);
          } else {
               query.setParameter("i_p_file_nm", null);
          }

          return (Integer) query.getSingleResult();
     }

     // @Override
     public List<Object[]> sp_getdiagingrpt(BigInteger i_rpt_di_age_id) {
          Query query = entityManager.createNativeQuery("CALL sp_getdiagingrpt(:i_rpt_di_age_id)")
                    .setParameter("i_rpt_di_age_id", i_rpt_di_age_id);

          return query.getResultList();
     }

     public Integer sp_getdiagequeuerpt() {
          Query query = entityManager.createNativeQuery("CALL sp_getdiagequeuerpt()");

          return (Integer) query.getSingleResult();
     }

     public List<Object[]> sp_getpendingdiagingrpt() {
          Query query = entityManager.createNativeQuery("CALL sp_getpendingdiagingrpt()");

          return query.getResultList();
     }

     public Integer sp_getpendingdiagingrptbyid(BigInteger i_rpt_di_age_id) {
          Query query = entityManager.createNativeQuery("CALL sp_getpendingdiagingrptbyid(:i_rpt_di_age_id)")
                    .setParameter("i_rpt_di_age_id", i_rpt_di_age_id);

          return (Integer) query.getSingleResult();
     }

     // #endregion
     // #region RIPL Aging start
     // @Override
     // public BigInteger sp_insriplagingrpt(Date i_p_dt_req, Integer i_p_imp_status,
     // Integer i_p_exp_status,
     // String i_p_ent_ty, String i_p_ent_nm, Date i_p_dt_due_fr, Date i_p_dt_due_to,
     // Date i_p_dt_rcpt_fr, Date i_p_dt_rcpt_to, Date i_p_dt_imp_fr, Date
     // i_p_dt_imp_to, Date i_p_dt_wo_fr,
     // Date i_p_dt_wo_to, String i_created_by, String i_modified_by, String
     // i_status, String i_p_email,
     // String i_p_file_type, Integer i_p_file_size, String i_p_file_nm, String
     // i_p_batch_no,
     // String i_p_fms_ref_no) {
     // Query query = entityManager.createNativeQuery(
     // "CALL sp_insriplagingrpt(:i_p_dt_req, :i_p_imp_status, :i_p_exp_status,
     // :i_p_ent_ty, :i_p_ent_nm, :i_p_dt_due_fr, :i_p_dt_due_to, :i_p_dt_rcpt_fr,
     // :i_p_dt_rcpt_to, :i_p_dt_imp_fr, :i_p_dt_imp_to, :i_p_dt_wo_fr,
     // :i_p_dt_wo_to, :i_created_by, :i_modified_by, :i_status, :i_p_email,
     // :i_p_file_type, :i_p_file_size, :i_p_file_nm, :i_p_batch_no,
     // :i_p_fms_ref_no)")
     // .setParameter("i_p_dt_req", i_p_dt_req)
     // .setParameter("i_p_imp_status", i_p_imp_status != null ? i_p_imp_status :
     // null)
     // .setParameter("i_p_exp_status", i_p_exp_status != null ? i_p_exp_status :
     // null)
     // .setParameter("i_p_ent_ty", i_p_ent_ty != null ? i_p_ent_ty : null)
     // .setParameter("i_p_ent_nm", i_p_ent_nm != null ? i_p_ent_nm : null)
     // .setParameter("i_p_dt_due_fr", i_p_dt_due_fr)
     // .setParameter("i_p_dt_due_to", i_p_dt_due_to)
     // .setParameter("i_p_dt_rcpt_fr", i_p_dt_rcpt_fr != null ? i_p_dt_rcpt_fr :
     // null)
     // .setParameter("i_p_dt_rcpt_to", i_p_dt_rcpt_to != null ? i_p_dt_rcpt_to :
     // null)
     // .setParameter("i_p_dt_imp_fr", i_p_dt_imp_fr != null ? i_p_dt_imp_fr : null)
     // .setParameter("i_p_dt_imp_to", i_p_dt_imp_to != null ? i_p_dt_imp_to : null)
     // .setParameter("i_p_dt_wo_fr", i_p_dt_wo_fr != null ? i_p_dt_wo_fr : null)
     // .setParameter("i_p_dt_wo_to", i_p_dt_wo_to != null ? i_p_dt_wo_to : null)
     // .setParameter("i_created_by", i_created_by)
     // .setParameter("i_modified_by", i_modified_by)
     // .setParameter("i_status", i_status)
     // .setParameter("i_p_email", i_p_email != null ? i_p_email : null)
     // .setParameter("i_p_file_type", i_p_file_type != null ? i_p_file_type : null)
     // .setParameter("i_p_file_size", i_p_file_size != null ? i_p_file_size : null)
     // .setParameter("i_p_file_nm", i_p_file_nm != null ? i_p_file_nm : null)
     // .setParameter("i_p_batch_no", i_p_batch_no != null ? i_p_batch_no : null)
     // .setParameter("i_p_fms_ref_no", i_p_fms_ref_no != null ? i_p_fms_ref_no :
     // null);

     // // Check if the dates are null and set accordingly
     // if (i_p_imp_status != null) {
     // query.setParameter("i_p_imp_status", i_p_imp_status);
     // } else {
     // query.setParameter("i_p_imp_status", null);
     // }

     // if (i_p_exp_status != null) {
     // query.setParameter("i_p_exp_status", i_p_exp_status);
     // } else {
     // query.setParameter("i_p_exp_status", null);
     // }

     // if (i_p_ent_ty != null) {
     // query.setParameter("i_p_ent_ty", i_p_ent_ty);
     // } else {
     // query.setParameter("i_p_ent_ty", null);
     // }

     // if (i_p_ent_nm != null) {
     // query.setParameter("i_p_ent_nm", i_p_ent_nm);
     // } else {
     // query.setParameter("i_p_ent_nm", null);
     // }

     // if (i_p_dt_rcpt_fr != null) {
     // query.setParameter("i_p_dt_rcpt_fr", i_p_dt_rcpt_fr);
     // } else {
     // query.setParameter("i_p_dt_rcpt_fr", null);
     // }

     // if (i_p_dt_rcpt_to != null) {
     // query.setParameter("i_p_dt_rcpt_to", i_p_dt_rcpt_to);
     // } else {
     // query.setParameter("i_p_dt_rcpt_to", null);
     // }

     // if (i_p_dt_imp_fr != null) {
     // query.setParameter("i_p_dt_imp_fr", i_p_dt_imp_fr);
     // } else {
     // query.setParameter("i_p_dt_imp_fr", null);
     // }

     // if (i_p_dt_imp_to != null) {
     // query.setParameter("i_p_dt_imp_to", i_p_dt_imp_to);
     // } else {
     // query.setParameter("i_p_dt_imp_to", null);
     // }

     // if (i_p_dt_wo_fr != null) {
     // query.setParameter("i_p_dt_wo_fr", i_p_dt_wo_fr);
     // } else {
     // query.setParameter("i_p_dt_wo_fr", null);
     // }

     // if (i_p_dt_wo_to != null) {
     // query.setParameter("i_p_dt_wo_to", i_p_dt_wo_to);
     // } else {
     // query.setParameter("i_p_dt_wo_to", null);
     // }

     // if (i_p_email != null) {
     // query.setParameter("i_p_email", i_p_email);
     // } else {
     // query.setParameter("i_p_email", null);
     // }

     // if (i_p_file_type != null) {
     // query.setParameter("i_p_file_type", i_p_file_type);
     // } else {
     // query.setParameter("i_p_file_type", null);
     // }

     // if (i_p_file_size != null) {
     // query.setParameter("i_p_file_size", i_p_file_size);
     // } else {
     // query.setParameter("i_p_file_size", null);
     // }

     // if (i_p_file_nm != null) {
     // query.setParameter("i_p_file_nm", i_p_file_nm);
     // } else {
     // query.setParameter("i_p_file_nm", null);
     // }

     // if (i_p_batch_no != null) {
     // query.setParameter("i_p_batch_no", i_p_batch_no);
     // } else {
     // query.setParameter("i_p_batch_no", null);
     // }

     // if (i_p_fms_ref_no != null) {
     // query.setParameter("i_p_fms_ref_no", i_p_fms_ref_no);
     // } else {
     // query.setParameter("i_p_fms_ref_no", null);
     // }

     // BigInteger result = (BigInteger) query.getSingleResult();
     // return result;
     // }

     // // @Override
     // public List<Object[]> sp_getriplaginglistingrpt(Integer i_page, Integer
     // i_size, BigInteger i_rpt_ripl_age_id,
     // Date i_p_dt_req, Integer i_p_imp_status,
     // Integer i_p_exp_status, String i_p_ent_ty, String i_p_ent_nm, Date
     // i_p_dt_due_fr, Date i_p_dt_due_to,
     // Date i_p_dt_rcpt_fr, Date i_p_dt_rcpt_to, Date i_p_dt_imp_fr, Date
     // i_p_dt_imp_to, Date i_p_dt_wo_fr,
     // Date i_p_dt_wo_to, String i_created_by, String i_modified_by, String
     // i_status, String i_p_email,
     // String i_p_file_type, Integer i_p_file_size, String i_p_file_nm) {
     // Query query = entityManager.createNativeQuery(
     // "CALL sp_getriplaginglistingrpt(:i_page,:i_size,:i_rpt_ripl_age_id,
     // :i_p_dt_req, :i_p_imp_status, :i_p_exp_status, :i_p_ent_ty, :i_p_ent_nm,
     // :i_p_dt_due_fr, :i_p_dt_due_to, :i_p_dt_rcpt_fr, :i_p_dt_rcpt_to,
     // :i_p_dt_imp_fr, :i_p_dt_imp_to, :i_p_dt_wo_fr, :i_p_dt_wo_to, :i_created_by,
     // :i_modified_by, :i_status, :i_p_email, :i_p_file_type, :i_p_file_size,
     // :i_p_file_nm)")
     // .setParameter("i_page", i_page)
     // .setParameter("i_size", i_size)
     // .setParameter("i_rpt_ripl_age_id", i_rpt_ripl_age_id)
     // .setParameter("i_p_dt_req", i_p_dt_req)
     // .setParameter("i_p_imp_status", i_p_imp_status)
     // .setParameter("i_p_exp_status", i_p_exp_status)
     // .setParameter("i_p_ent_ty", i_p_ent_ty)
     // .setParameter("i_p_ent_nm", i_p_ent_nm)
     // .setParameter("i_p_dt_due_fr", i_p_dt_due_fr)
     // .setParameter("i_p_dt_due_to", i_p_dt_due_to)
     // .setParameter("i_p_dt_rcpt_fr", i_p_dt_rcpt_fr)
     // .setParameter("i_p_dt_rcpt_to", i_p_dt_rcpt_to)
     // .setParameter("i_p_dt_imp_fr", i_p_dt_imp_fr)
     // .setParameter("i_p_dt_imp_to", i_p_dt_imp_to)
     // .setParameter("i_p_dt_wo_fr", i_p_dt_wo_fr)
     // .setParameter("i_p_dt_wo_to", i_p_dt_wo_to)
     // .setParameter("i_created_by", i_created_by)
     // .setParameter("i_modified_by", i_modified_by)
     // .setParameter("i_status", i_status)
     // .setParameter("i_p_email", i_p_email)
     // .setParameter("i_p_file_type", i_p_file_type)
     // .setParameter("i_p_file_size", i_p_file_size)
     // .setParameter("i_p_file_nm", i_p_file_nm);

     // return query.getResultList();
     // }

     // public Integer sp_updriplagingrpt(BigInteger i_rpt_ripl_age_id, String
     // i_status, Integer i_p_file_size,
     // String i_p_file_nm, String i_modified_by) {
     // Query query = entityManager
     // .createNativeQuery(
     // "CALL sp_updriplagingrpt(:i_rpt_ripl_age_id, :i_status, :i_p_file_size,
     // :i_p_file_nm,:i_modified_by )")
     // .setParameter("i_rpt_ripl_age_id", i_rpt_ripl_age_id)
     // .setParameter("i_status", i_status)
     // .setParameter("i_p_file_size", i_p_file_size != null ? i_p_file_size : null)
     // .setParameter("i_p_file_nm", i_p_file_nm != null ? i_p_file_nm : null)
     // .setParameter("i_modified_by", i_modified_by);

     // if (i_p_file_size != null) {
     // query.setParameter("i_p_file_size", i_p_file_size);
     // } else {
     // query.setParameter("i_p_file_size", null);
     // }

     // if (i_p_file_nm != null) {
     // query.setParameter("i_p_file_nm", i_p_file_nm);
     // } else {
     // query.setParameter("i_p_file_nm", null);
     // }

     // return (Integer) query.getSingleResult();
     // }

     // // @Override
     // public List<Object[]> sp_getriplagingrpt(BigInteger i_rpt_ripl_age_id) {
     // Query query = entityManager.createNativeQuery("CALL
     // sp_getriplagingrpt(:i_rpt_ripl_age_id)")
     // .setParameter("i_rpt_ripl_age_id", i_rpt_ripl_age_id);

     // return query.getResultList();
     // }

     // public Integer sp_getriplagequeuerpt() {
     // Query query = entityManager.createNativeQuery("CALL
     // sp_getriplagequeuerpt()");

     // return (Integer) query.getSingleResult();
     // }

     // public List<Object[]> sp_getpendingriplagingrpt() {
     // Query query = entityManager.createNativeQuery("CALL
     // sp_getpendingriplagingrpt()");

     // return query.getResultList();
     // }

     // public Integer sp_getpendingriplagingrptbyid(BigInteger i_rpt_ripl_age_id) {
     // Query query = entityManager.createNativeQuery("CALL
     // sp_getpendingriplagingrptbyid(:i_rpt_ripl_age_id)")
     // .setParameter("i_rpt_ripl_age_id", i_rpt_ripl_age_id);

     // return (Integer) query.getSingleResult();
     // }

     // // #endregion

     @Override
     public List<Object[]> sp_getallbanks() {
          Query query = entityManager.createNativeQuery("CALL sp_getallbanks()");
          return query.getResultList();

     }

     @Override
     public List<Object[]> sp_getallrctype() {
          Query query = entityManager.createNativeQuery("CALL sp_getallrctype()");
          return query.getResultList();

     }

     @Override
     public List<Object[]> sp_getallbillingstatus() {
          Query query = entityManager.createNativeQuery("CALL sp_getallbillingstatus()");
          return query.getResultList();

     }

     @Override
     public List<Object[]> sp_getallbillingmethod() {
          Query query = entityManager.createNativeQuery("CALL sp_getallbillingmethod()");
          return query.getResultList();

     }

     @Override
     public List<Object[]> sp_getpostcode() {
          Query query = entityManager
                    .createNativeQuery("CALL sp_getpostcode()");
          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getwhitelistip() {
          Query query = entityManager
                    .createNativeQuery("CALL sp_getwhitelistip()");
          return query.getResultList();
     }

     @Override
     public Integer sp_inswhiteip(WhiteIPReq insRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_inswhiteip(:i_ss_cd, :i_ip, :i_remark)");
          query.setParameter("i_ss_cd", insRequest.getI_ss_cd());
          query.setParameter("i_ip", insRequest.getI_ip());
          query.setParameter("i_remark", insRequest.getI_remark());

          Integer result = (Integer) query.getSingleResult();
          return result;
     }

     @Override
     public Integer sp_updwhiteip(WhiteIPReq insRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_updwhiteip(:i_ip, :i_remark)");
          query.setParameter("i_ip", insRequest.getI_ip());
          query.setParameter("i_remark", insRequest.getI_remark());

          Integer result = (Integer) query.getSingleResult();
          return result;
     }

     @Override
     public List<String> sp_getuploadedidaman() {
          Query query = entityManager
                    .createNativeQuery("CALL sp_getuploadedidaman()");
          return query.getResultList();
     }

     @Override
     public Integer sp_insextaudit(ExtAudit insRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_insextaudit(:i_module_nm, :i_rms_batch_no, :i_request_body, :i_response_body, :i_direction, :i_remark)");
          query.setParameter("i_module_nm", insRequest.getI_module_nm());
          query.setParameter("i_rms_batch_no", insRequest.getI_rms_batch_no());
          query.setParameter("i_request_body", insRequest.getI_request_body());
          query.setParameter("i_response_body", insRequest.getI_response_body());
          query.setParameter("i_direction", insRequest.getI_direction());
          query.setParameter("i_remark", insRequest.getI_remark());

          Object result = query.getSingleResult();

          if (result == null) {
               return 0;
          }

          return (Integer) result;
     }

     @Override
     public Integer sp_cleanextaudit() {
          Query query = entityManager.createNativeQuery("CALL sp_cleanextaudit()");

          Object result = query.getSingleResult();

          if (result == null) {
               return 0;
          }

          return (Integer) result;
     }
}