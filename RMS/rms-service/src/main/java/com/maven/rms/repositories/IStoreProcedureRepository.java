package com.maven.rms.repositories;

import org.springframework.stereotype.Repository;

//import com.lowagie.text.pdf.codec.Base64.InputStream;
import com.maven.rms.interfaces.IStoreProcedureInterface;
import com.maven.rms.models.DeferredIncomeAgingRequest;
import com.maven.rms.models.FMSLedgerDoc;
import com.maven.rms.models.FeeGrpRequest;
import com.maven.rms.models.GHLPaymentResponse;
import com.maven.rms.models.MFT;
import com.maven.rms.models.MFTRequest;
import com.maven.rms.models.MFTWF;
import com.maven.rms.models.MFTWFDocRequest;
import com.maven.rms.models.MFTWFHistoryRequest;
import com.maven.rms.models.MFTWFRequest;
import com.maven.rms.models.ParamRequest;
import com.maven.rms.models.RIPLAgingRequest;
import com.maven.rms.models.RMSUserRequest;
import com.maven.rms.models.RolePermissionRequest;
import com.maven.rms.models.TaxCdRequest;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;

@Repository
public class IStoreProcedureRepository implements IStoreProcedureInterface {

     @PersistenceContext
     private EntityManager entityManager;

     // #region tax code
     // @Override
     // public Integer sp_instaxcode(TaxCdRequest insertRequest) {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_insTaxCode(:i_tax_cd, :i_tax_cd_nm_en, :i_tax_cd_nm_bm, :i_tax_pct,:i_created_by, :i_modified_by,:i_status)")
     //                .setParameter("i_tax_cd", insertRequest.getI_tax_cd())
     //                .setParameter("i_tax_cd_nm_en", insertRequest.getI_tax_cd_nm_en())
     //                .setParameter("i_tax_cd_nm_bm", insertRequest.getI_tax_cd_nm_bm())
     //                .setParameter("i_tax_pct", insertRequest.getI_tax_pct())
     //                .setParameter("i_created_by", insertRequest.getI_created_by())
     //                .setParameter("i_modified_by", insertRequest.getI_modified_by())
     //                .setParameter("i_status", insertRequest.getI_status());

     //      Integer result = (Integer) query.getSingleResult();
     //      return result;
     // }

     // @Override
     // public Integer sp_updtaxcode(TaxCdRequest updateRequest) {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_updTaxCode(:i_tax_cd, :i_tax_cd_nm_en, :i_tax_cd_nm_bm, :i_tax_pct, :i_modified_by, :i_status)")

     //                .setParameter("i_tax_cd", updateRequest.getI_tax_cd())
     //                .setParameter("i_tax_cd_nm_en", updateRequest.getI_tax_cd_nm_en())
     //                .setParameter("i_tax_cd_nm_bm", updateRequest.getI_tax_cd_nm_bm())
     //                .setParameter("i_tax_pct", updateRequest.getI_tax_pct())
     //                .setParameter("i_modified_by", updateRequest.getI_modified_by())
     //                .setParameter("i_status", updateRequest.getI_status());
     //      Integer result = (Integer) query.getSingleResult();
     //      return result;
     // }

     // @Override
     // public Integer sp_deltaxcode(TaxCdRequest deleteRequest) {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_delTaxCode(:i_tax_cd, :i_modified_by, :i_status)")

     //                .setParameter("i_tax_cd", deleteRequest.getI_tax_cd())
     //                .setParameter("i_modified_by", deleteRequest.getI_modified_by())
     //                .setParameter("i_status", deleteRequest.getI_status());
     //      Integer result = (Integer) query.getSingleResult();
     //      return result;
     // }

     // @Override
     // // public List<Object[]> sp_gettaxcode_v2(Integer i_page, Integer i_size, Long i_tax_cd_id, String i_tax_cd,
     // //           String i_tax_cd_nm_en, String i_tax_cd_nm_bm, String i_modified_by, Date i_dt_modified_fr,
     // //           Date i_dt_modified_to, String i_status) {
     // public List<Object[]> sp_gettaxcode_v2(TaxCdRequest taxCdRequest) {

     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_gettaxcode_v2(:i_page, :i_size, :i_tax_cd_id, :i_tax_cd, :i_tax_cd_nm_en, :i_tax_cd_nm_bm, :i_modified_by, :i_dt_modified_fr, :i_dt_modified_to, :i_status)")
     //                .setParameter("i_page", taxCdRequest.getI_page())
     //                .setParameter("i_size", taxCdRequest.getI_size())
     //                .setParameter("i_tax_cd_id", taxCdRequest.getI_tax_cd_id())
     //                .setParameter("i_tax_cd", taxCdRequest.getI_tax_cd())
     //                .setParameter("i_tax_cd_nm_en", taxCdRequest.getI_tax_cd_nm_en())
     //                .setParameter("i_tax_cd_nm_bm", taxCdRequest.getI_tax_cd_nm_bm())
     //                .setParameter("i_modified_by", taxCdRequest.getI_modified_by())
     //                .setParameter("i_dt_modified_fr", taxCdRequest.getI_dt_modified_fr() != null ? taxCdRequest.getI_dt_modified_fr() : null)
     //                .setParameter("i_dt_modified_to", taxCdRequest.getI_dt_modified_to() != null ? taxCdRequest.getI_dt_modified_to() : null)
     //                .setParameter("i_status", taxCdRequest.getI_status());

     //      // Check if the dates are null and set accordingly
     //      if (taxCdRequest.getI_dt_modified_fr() != null) {
     //           query.setParameter("i_dt_modified_fr", taxCdRequest.getI_dt_modified_fr());
     //      } else {
     //           query.setParameter("i_dt_modified_fr", null);
     //      }

     //      if (taxCdRequest.getI_dt_modified_to() != null) {
     //           query.setParameter("i_dt_modified_to", taxCdRequest.getI_dt_modified_to());
     //      } else {
     //           query.setParameter("i_dt_modified_to", null);
     //      }
     //      return query.getResultList();
     // }

     // @Override
     // public Integer sp_checktaxcdbyid(TaxCdRequest taxCodeRequest) {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_checkfeetaxbyid(:i_fee_grp_id,:i_tax_cd_id)")
     //                .setParameter("i_fee_grp_id", "")
     //                .setParameter("i_tax_cd_id", taxCodeRequest.getI_tax_cd_id());
     //      return (Integer) query.getSingleResult();
     // }
     // #endregion

     // #region param
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
     // #endregion

     // #region user role
     // @Override
     // public List<Object[]> sp_getuserbyrole(RMSUserRequest rmsUserRequest) {
     //      Query query = entityManager
     //                .createNativeQuery(
     //                "CALL sp_getuserbyrole(:i_page, :i_size, :i_role_nm_en, :i_role_nm_bm,  :i_status)")
     //                .setParameter("i_page", rmsUserRequest.getI_page())
     //                .setParameter("i_size", rmsUserRequest.getI_size())
     //                .setParameter("i_role_nm_en", rmsUserRequest.getI_role_nm_en())
     //                .setParameter("i_role_nm_bm", rmsUserRequest.getI_role_nm_bm())
     //                .setParameter("i_status", rmsUserRequest.getI_status());
     //      return query.getResultList();
     // }

     // @Override
     // public Object sp_getuserdetail(RMSUserRequest rmsUserRequest) {
     //      Query query = entityManager.createNativeQuery("CALL sp_getuserdetail(:i_ssm4uuserrefno)")
     //                .setParameter("i_ssm4uuserrefno", rmsUserRequest.getI_ssm4uuserrefno());
     //      return query.getSingleResult();
     // }
     // // #endregion

     // #region mft
     // public List<Object[]> sp_getMFTWFByStatusAndEffDate(String status) {
     //      Query query = entityManager.createNativeQuery("CALL sp_getMFTWFByStatusAndEffDate(:i_status)")
     //                .setParameter("i_status", status);
     //      return query.getResultList();
     // }

     // @Override
     // public List<Object[]> sp_getmft(MFTRequest mftRequest) {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_getmft(:i_page, :i_size, :i_fee_detail_pk, :i_fee_detail_id,  :i_unit_fee_fr, :i_unit_fee_to, :i_ss_cd, :i_tax_cd,  :i_dt_modified_fr,  :i_dt_modified_to ,  :i_modified_by, :i_status)")
     //                .setParameter("i_page", mftRequest.getI_page())
     //                .setParameter("i_size", mftRequest.getI_size())
     //                .setParameter("i_fee_detail_pk", mftRequest.getI_fee_detail_pk())
     //                .setParameter("i_fee_detail_id", mftRequest.getI_fee_detail_id())
     //                .setParameter("i_unit_fee_fr", mftRequest.getI_unit_fee_fr())
     //                .setParameter("i_unit_fee_to", mftRequest.getI_unit_fee_to())
     //                .setParameter("i_ss_cd", mftRequest.getI_ss_cd())
     //                .setParameter("i_tax_cd", mftRequest.getI_tax_cd())
     //                .setParameter("i_dt_modified_fr", mftRequest.getI_dt_modified_fr() != null ? mftRequest.getI_dt_modified_fr() : null)
     //                .setParameter("i_dt_modified_to", mftRequest.getI_dt_modified_to() != null ? mftRequest.getI_dt_modified_to() : null)
     //                .setParameter("i_modified_by", mftRequest.getI_modified_by())
     //                .setParameter("i_status", mftRequest.getI_status());

     //      // Check if the dates are null and set accordingly
     //      if (mftRequest.getI_dt_modified_fr() != null) {
     //           query.setParameter("i_dt_modified_fr", mftRequest.getI_dt_modified_fr());
     //      } else {
     //           query.setParameter("i_dt_modified_fr", null);
     //      }

     //      if (mftRequest.getI_dt_modified_to() != null) {
     //           query.setParameter("i_dt_modified_to", mftRequest.getI_dt_modified_to());
     //      } else {
     //           query.setParameter("i_dt_modified_to", null);
     //      }

     //      return query.getResultList();
     // }

     // // public List<Object[]> sp_getMftWFilter(Integer i_fee_detail_pk) {
     // public List<Object[]> sp_getMftWFilter(MFTRequest mftRequest) {
     //      Query query = entityManager.createNativeQuery("CALL sp_getmasterfeetablewithfilter(:i_fee_detail_pk)")
     //                .setParameter("i_fee_detail_pk", mftRequest.getI_fee_detail_pk());
     //                // .setParameter("i_fee_detail_pk", i_fee_detail_pk);

     //      return query.getResultList();
     // }

     // public Integer sp_insMFT(MFT mft) {
     //      Query query = entityManager
     //                .createNativeQuery("CALL sp_insmasterfeetable(:i_fee_detail_id, :i_fee_grp_id, :i_fee_detail_nm_e,"
     //                          + ":i_fee_detail_nm_b, :i_unit_fee, :i_promo_startdt, :i_promo_enddt, :i_promo_fee, :i_tax_cd_id,"
     //                          + ":i_allow_otc, :i_ll_parent_id, :i_ll_start_day, :i_ll_start_mth, :i_ll_end_day, :i_ll_end_mth,"
     //                          + ":i_ledger_cd, :i_ss_cd, :i_created_by, :i_modified_by, :i_status)")
     //                .setParameter("i_fee_detail_id", mft.getFee_detail_id())
     //                .setParameter("i_fee_grp_id", mft.getFee_grp_id())
     //                .setParameter("i_fee_detail_nm_e", mft.getFee_detail_nm_e())
     //                .setParameter("i_fee_detail_nm_b", mft.getFee_detail_nm_b())
     //                .setParameter("i_unit_fee", mft.getUnit_fee())
     //                .setParameter("i_promo_startdt", mft.getPromo_startdt())
     //                .setParameter("i_promo_enddt", mft.getPromo_enddt())
     //                .setParameter("i_promo_fee", mft.getPromo_fee())
     //                .setParameter("i_tax_cd_id", mft.getTax_cd_id())
     //                .setParameter("i_allow_otc", mft.getAllow_otc())
     //                .setParameter("i_ll_parent_id", mft.getLl_parent_id())
     //                .setParameter("i_ll_start_day", mft.getLl_start_day())
     //                .setParameter("i_ll_start_mth", mft.getLl_start_mth())
     //                .setParameter("i_ll_end_day", mft.getLl_end_day())
     //                .setParameter("i_ll_end_mth", mft.getLl_end_mth())
     //                .setParameter("i_ledger_cd", mft.getLedger_cd())
     //                .setParameter("i_ss_cd", mft.getSs_cd())
     //                .setParameter("i_created_by", mft.getCreated_by())
     //                .setParameter("i_modified_by", mft.getModified_by())
     //                .setParameter("i_status", mft.getStatus());

     //      return (Integer) query.getSingleResult();
     // }

     // public Integer sp_updMFT(MFT mft) {
     //      Query query = entityManager
     //                .createNativeQuery("CALL sp_updmasterfeetable(:i_fee_detail_id, :i_fee_grp_id, :i_fee_detail_nm_e,"
     //                          + ":i_fee_detail_nm_b, :i_unit_fee, :i_promo_startdt, :i_promo_enddt, :i_promo_fee, :i_tax_cd_id,"
     //                          + ":i_allow_otc, :i_ll_parent_id, :i_ll_start_day, :i_ll_start_mth, :i_ll_end_day, :i_ll_end_mth,"
     //                          + ":i_ledger_cd, :i_ss_cd, :i_modified_by, :i_status)")
     //                .setParameter("i_fee_detail_id", mft.getFee_detail_id())
     //                .setParameter("i_fee_grp_id", mft.getFee_grp_id())
     //                .setParameter("i_fee_detail_nm_e", mft.getFee_detail_nm_e())
     //                .setParameter("i_fee_detail_nm_b", mft.getFee_detail_nm_b())
     //                .setParameter("i_unit_fee", mft.getUnit_fee())
     //                .setParameter("i_promo_startdt", mft.getPromo_startdt())
     //                .setParameter("i_promo_enddt", mft.getPromo_enddt())
     //                .setParameter("i_promo_fee", mft.getPromo_fee())
     //                .setParameter("i_tax_cd_id", mft.getTax_cd_id())
     //                .setParameter("i_allow_otc", mft.getAllow_otc())
     //                .setParameter("i_ll_parent_id", mft.getLl_parent_id())
     //                .setParameter("i_ll_start_day", mft.getLl_start_day())
     //                .setParameter("i_ll_start_mth", mft.getLl_start_mth())
     //                .setParameter("i_ll_end_day", mft.getLl_end_day())
     //                .setParameter("i_ll_end_mth", mft.getLl_end_mth())
     //                .setParameter("i_ledger_cd", mft.getLedger_cd())
     //                .setParameter("i_ss_cd", mft.getSs_cd())
     //                .setParameter("i_modified_by", mft.getModified_by())
     //                .setParameter("i_status", mft.getStatus());

     //      return (Integer) query.getSingleResult();
     // }

     // // public Integer sp_updateMFTWFStatus(BigInteger i_wf_id, String i_status) {
     // public Integer sp_updateMFTWFStatus(MFTWFRequest mftwfRequest) {
     //      Query query = entityManager.createNativeQuery("CALL sp_updmftwfstatus(:i_wf_id, :i_status)")
     //                  .setParameter("i_wf_id", mftwfRequest.getI_wf_id())
     //                  .setParameter("i_status", mftwfRequest.getI_status());
     //                // .setParameter("i_wf_id", i_wf_id)
     //                // .setParameter("i_status", i_status);
     //      return (Integer) query.getSingleResult();
     // }

     // @Override
     // public Integer sp_updmftwf_status(MFTWFRequest mftwfRequest) {
     //      Query query = entityManager
     //                .createNativeQuery(
     //                          "CALL sp_updmftwf_status(:i_wf_id, :i_assign_to, :i_status, :i_remark, :i_modified_by)")
     //                .setParameter("i_wf_id", mftwfRequest.getI_wf_id())
     //                .setParameter("i_assign_to", mftwfRequest.getI_assign_to())
     //                .setParameter("i_status", mftwfRequest.getI_status())
     //                .setParameter("i_remark", mftwfRequest.getI_remark() != null ? mftwfRequest.getI_remark() : null)
     //                .setParameter("i_modified_by", mftwfRequest.getI_modified_by());

     //      if (mftwfRequest.getI_remark() != null) {
     //           query.setParameter("i_remark", mftwfRequest.getI_remark());
     //      } else {
     //           query.setParameter("i_remark", null);
     //      }

     //      return (Integer) query.getSingleResult();
     // }

     // @Override
     // public List<Object[]> sp_getmftwf(MFTWFRequest mftwfRequest) {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_getmftwf(:i_page, :i_size, :i_wf_id, :i_fee_detail_pk, :i_fee_detail_id, :i_assign_to, :i_status , :i_created_by,  :i_modified_by, :i_modified_by_nm, :i_dt_modified_fr, :i_dt_modified_to, :i_dt_created_fr, :i_dt_created_to, :i_dt_effective_fr, :i_dt_effective_to, :i_ss_cd, :i_wf_is_in_prg)")
     //                .setParameter("i_page", mftwfRequest.getI_page())
     //                .setParameter("i_size", mftwfRequest.getI_size())
     //                .setParameter("i_wf_id", mftwfRequest.getI_wf_id())
     //                .setParameter("i_fee_detail_pk", mftwfRequest.getI_fee_detail_pk())
     //                .setParameter("i_fee_detail_id", mftwfRequest.getI_fee_detail_id())
     //                .setParameter("i_assign_to", mftwfRequest.getI_assign_to())
     //                .setParameter("i_status", mftwfRequest.getI_status())
     //                .setParameter("i_created_by", mftwfRequest.getI_created_by())
     //                .setParameter("i_modified_by", mftwfRequest.getI_modified_by())
     //                .setParameter("i_modified_by_nm", mftwfRequest.getI_modified_by_nm())
     //                .setParameter("i_dt_modified_fr", mftwfRequest.getI_dt_modified_fr())
     //                .setParameter("i_dt_modified_to", mftwfRequest.getI_dt_modified_to())
     //                .setParameter("i_dt_created_fr", mftwfRequest.getI_dt_created_fr())
     //                .setParameter("i_dt_created_to", mftwfRequest.getI_dt_created_to())
     //                .setParameter("i_dt_effective_fr", mftwfRequest.getI_dt_effective_fr())
     //                .setParameter("i_dt_effective_to", mftwfRequest.getI_dt_effective_to())
     //                .setParameter("i_ss_cd", mftwfRequest.getI_ss_cd())
     //                .setParameter("i_wf_is_in_prg", mftwfRequest.getI_wf_is_in_prg());
     //      return query.getResultList();
     // }

     // @Override
     // public BigInteger sp_insmftwf(MFTWFRequest mftwfRequest) {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_insmftwf(:i_fee_detail_pk, :i_fee_detail_id, :i_fee_grp_id, :i_fee_detail_nm_e, :i_fee_detail_nm_b, :i_fee_amt, :i_promo_startdt, :i_promo_enddt, :i_promo_fee, :i_tax_cd_id, :i_allow_otc, :i_ll_parent_id, :i_ll_start_day, :i_ll_start_mth, :i_ll_end_day, :i_ll_end_mth, :i_ledger_cd, :i_ss_cd, :i_created_by, :i_modified_by, :i_status, :i_effective_date, :i_remark, :i_assign_to, :i_action, :i_r_fee_det_nm, :i_r_fee_amt, :i_r_ss_cd, :i_r_promo_startdt, :i_r_promo_enddt, :i_r_ll_required, :i_r_add_notes, :i_mft_status, :i_r_promo_fee)")
     //                .setParameter("i_fee_detail_pk", mftwfRequest.getI_fee_detail_pk() != null ? mftwfRequest.getI_fee_detail_pk() : null)
     //                .setParameter("i_fee_detail_id", mftwfRequest.getI_fee_detail_id())
     //                .setParameter("i_fee_grp_id", mftwfRequest.getI_fee_grp_id())
     //                .setParameter("i_fee_detail_nm_e", mftwfRequest.getI_fee_detail_nm_e())
     //                .setParameter("i_fee_detail_nm_b", mftwfRequest.getI_fee_detail_nm_b())
     //                .setParameter("i_fee_amt", mftwfRequest.getI_fee_amt())
     //                .setParameter("i_promo_startdt", mftwfRequest.getI_promo_startdt() != null ? mftwfRequest.getI_promo_startdt() : null)
     //                .setParameter("i_promo_enddt", mftwfRequest.getI_promo_enddt() != null ? mftwfRequest.getI_promo_enddt() : null)
     //                .setParameter("i_promo_fee", mftwfRequest.getI_promo_fee() != null ? mftwfRequest.getI_promo_fee() : null)
     //                .setParameter("i_tax_cd_id", mftwfRequest.getI_tax_cd_id())
     //                .setParameter("i_allow_otc", mftwfRequest.getI_allow_otc())
     //                .setParameter("i_ll_parent_id", mftwfRequest.getI_ll_parent_id() != null ? mftwfRequest.getI_ll_parent_id() : null)
     //                .setParameter("i_ll_start_day", mftwfRequest.getI_ll_start_day() != null ? mftwfRequest.getI_ll_start_day() : null)
     //                .setParameter("i_ll_start_mth", mftwfRequest.getI_ll_start_mth() != null ? mftwfRequest.getI_ll_start_mth() : null)
     //                .setParameter("i_ll_end_day", mftwfRequest.getI_ll_end_day() != null ? mftwfRequest.getI_ll_end_day() : null)
     //                .setParameter("i_ll_end_mth", mftwfRequest.getI_ll_end_mth() != null ? mftwfRequest.getI_ll_end_mth() : null)
     //                .setParameter("i_ledger_cd", mftwfRequest.getI_ledger_cd())
     //                .setParameter("i_ss_cd", mftwfRequest.getI_ss_cd())
     //                .setParameter("i_created_by", mftwfRequest.getI_created_by())
     //                .setParameter("i_modified_by", mftwfRequest.getI_modified_by())
     //                .setParameter("i_status", mftwfRequest.getI_status())
     //                .setParameter("i_effective_date", mftwfRequest.getI_effective_date())
     //                .setParameter("i_remark", mftwfRequest.getI_remark() != null ? mftwfRequest.getI_remark() : null)
     //                .setParameter("i_assign_to", mftwfRequest.getI_assign_to())
     //                .setParameter("i_action", mftwfRequest.getI_action())
     //                .setParameter("i_r_fee_det_nm", mftwfRequest.getI_r_fee_det_nm() != null ? mftwfRequest.getI_r_fee_det_nm() : null)
     //                .setParameter("i_r_fee_amt", mftwfRequest.getI_r_fee_amt() != null ? mftwfRequest.getI_r_fee_amt() : null)
     //                .setParameter("i_r_ss_cd", mftwfRequest.getI_r_ss_cd() != null ? mftwfRequest.getI_r_ss_cd() : null)
     //                .setParameter("i_r_promo_startdt", mftwfRequest.getI_r_promo_startdt() != null ? mftwfRequest.getI_r_promo_startdt() : null)
     //                .setParameter("i_r_promo_enddt", mftwfRequest.getI_r_promo_enddt() != null ? mftwfRequest.getI_r_promo_enddt() : null)
     //                .setParameter("i_r_ll_required", mftwfRequest.getI_r_ll_required() != null ? mftwfRequest.getI_r_ll_required() : null)
     //                .setParameter("i_r_add_notes", mftwfRequest.getI_r_add_notes() != null ? mftwfRequest.getI_r_add_notes() : null)
     //                .setParameter("i_mft_status", mftwfRequest.getI_mft_status() != null ? mftwfRequest.getI_mft_status() : null)
     //                .setParameter("i_r_promo_fee", mftwfRequest.getI_r_promo_fee() != null ? mftwfRequest.getI_r_promo_fee() : null);

     //      // Check if the dates are null and set accordingly
     //      if (mftwfRequest.getI_fee_detail_pk() != null) {
     //           query.setParameter("i_fee_detail_pk", mftwfRequest.getI_fee_detail_pk());
     //      } else {
     //           query.setParameter("i_fee_detail_pk", null);
     //      }

     //      if (mftwfRequest.getI_promo_startdt() != null) {
     //           query.setParameter("i_promo_startdt", mftwfRequest.getI_promo_startdt());
     //      } else {
     //           query.setParameter("i_promo_startdt", null);
     //      }

     //      if (mftwfRequest.getI_promo_enddt() != null) {
     //           query.setParameter("i_promo_enddt", mftwfRequest.getI_promo_enddt());
     //      } else {
     //           query.setParameter("i_promo_enddt", null);
     //      }

     //      if (mftwfRequest.getI_promo_fee() != null) {
     //           query.setParameter("i_promo_fee", mftwfRequest.getI_promo_fee());
     //      } else {
     //           query.setParameter("i_promo_fee", null);
     //      }

     //      if (mftwfRequest.getI_ll_parent_id() != null) {
     //           query.setParameter("i_ll_parent_id", mftwfRequest.getI_ll_parent_id());
     //      } else {
     //           query.setParameter("i_ll_parent_id", null);
     //      }

     //      if (mftwfRequest.getI_ll_start_day() != null) {
     //           query.setParameter("i_ll_start_day", mftwfRequest.getI_ll_start_day());
     //      } else {
     //           query.setParameter("i_ll_start_day", null);
     //      }

     //      if (mftwfRequest.getI_ll_start_mth() != null) {
     //           query.setParameter("i_ll_start_mth", mftwfRequest.getI_ll_start_mth());
     //      } else {
     //           query.setParameter("i_ll_start_mth", null);
     //      }

     //      if (mftwfRequest.getI_ll_end_day() != null) {
     //           query.setParameter("i_ll_end_day", mftwfRequest.getI_ll_end_day());
     //      } else {
     //           query.setParameter("i_ll_end_day", null);
     //      }

     //      if (mftwfRequest.getI_ll_end_mth() != null) {
     //           query.setParameter("i_ll_end_mth", mftwfRequest.getI_ll_end_mth());
     //      } else {
     //           query.setParameter("i_ll_end_mth", null);
     //      }

     //      if (mftwfRequest.getI_remark() != null) {
     //           query.setParameter("i_remark", mftwfRequest.getI_remark());
     //      } else {
     //           query.setParameter("i_remark", null);
     //      }

     //      if (mftwfRequest.getI_r_fee_det_nm() != null) {
     //           query.setParameter("i_r_fee_det_nm", mftwfRequest.getI_r_fee_det_nm());
     //      } else {
     //           query.setParameter("i_r_fee_det_nm", null);
     //      }

     //      if (mftwfRequest.getI_r_fee_amt() != null) {
     //           query.setParameter("i_r_fee_amt", mftwfRequest.getI_r_fee_amt());
     //      } else {
     //           query.setParameter("i_r_fee_amt", null);
     //      }

     //      if (mftwfRequest.getI_r_ss_cd() != null) {
     //           query.setParameter("i_r_ss_cd", mftwfRequest.getI_r_ss_cd());
     //      } else {
     //           query.setParameter("i_r_ss_cd", null);
     //      }

     //      if (mftwfRequest.getI_r_promo_startdt() != null) {
     //           query.setParameter("i_r_promo_startdt", mftwfRequest.getI_r_promo_startdt());
     //      } else {
     //           query.setParameter("i_r_promo_startdt", null);
     //      }

     //      if (mftwfRequest.getI_r_promo_enddt() != null) {
     //           query.setParameter("i_r_promo_enddt", mftwfRequest.getI_r_promo_enddt());
     //      } else {
     //           query.setParameter("i_r_promo_enddt", null);
     //      }

     //      if (mftwfRequest.getI_r_ll_required() != null) {
     //           query.setParameter("i_r_ll_required", mftwfRequest.getI_r_ll_required());
     //      } else {
     //           query.setParameter("i_r_ll_required", null);
     //      }

     //      if (mftwfRequest.getI_r_add_notes() != null) {
     //           query.setParameter("i_r_add_notes", mftwfRequest.getI_r_add_notes());
     //      } else {
     //           query.setParameter("i_r_add_notes", null);
     //      }

     //      if (mftwfRequest.getI_mft_status() != null) {
     //           query.setParameter("i_mft_status", mftwfRequest.getI_mft_status());
     //      } else {
     //           query.setParameter("i_mft_status", null);
     //      }

     //      if (mftwfRequest.getI_r_promo_fee() != null) {
     //           query.setParameter("i_r_promo_fee", mftwfRequest.getI_r_promo_fee());
     //      } else {
     //           query.setParameter("i_r_promo_fee", null);
     //      }

     //      BigInteger result = (BigInteger) query.getSingleResult();
     //      return result;
     // }

     // @Override
     // public List<Object[]> sp_getmftwfhis(MFTWFHistoryRequest mftwfHistoryRequest) {
     //      Query query = entityManager.createNativeQuery("CALL sp_getmftwfhis(:i_page, :i_size, :i_wf_id,  :i_status )")
     //                .setParameter("i_page", mftwfHistoryRequest.getI_page())
     //                .setParameter("i_size", mftwfHistoryRequest.getI_size())
     //                .setParameter("i_wf_id", mftwfHistoryRequest.getI_wf_id())
     //                .setParameter("i_status", mftwfHistoryRequest.getI_status());
     //      return query.getResultList();
     // }

     // @Override
     // public List<Object[]> sp_getwfh_ast(MFTWFHistoryRequest mftwfHistoryRequest) {
     //      Query query = entityManager.createNativeQuery("CALL sp_getwfh_ast(:i_task_id, :i_status)")
     //                .setParameter("i_task_id", mftwfHistoryRequest.getI_task_id())
     //                .setParameter("i_status", mftwfHistoryRequest.getI_status());
     //      return query.getResultList();

     // }

     // @Override
     // public Integer sp_updmftwf(MFTWFRequest mftwfRequest) {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_updmftwf(:i_wf_id, :i_fee_detail_pk, :i_fee_detail_id, :i_fee_grp_id, :i_fee_detail_nm_e, :i_fee_detail_nm_b, :i_fee_amt, :i_promo_startdt, :i_promo_enddt, :i_promo_fee, :i_tax_cd_id, :i_allow_otc, :i_ll_parent_id, :i_ll_start_day, :i_ll_end_day, :i_ll_start_mth,  :i_ll_end_mth, :i_ledger_cd, :i_ss_cd, :i_effective_date, :i_modified_by, :i_status, :i_assign_to, :i_remark, :i_action, :i_r_fee_det_nm, :i_r_fee_amt, :i_r_ss_cd, :i_r_promo_startdt, :i_r_promo_enddt, :i_r_ll_required, :i_r_add_notes, :i_mft_status, :i_r_promo_fee)")
     //                .setParameter("i_wf_id", mftwfRequest.getI_wf_id())
     //                .setParameter("i_fee_detail_pk", mftwfRequest.getI_fee_detail_pk() != null ? mftwfRequest.getI_fee_detail_pk() : null)
     //                .setParameter("i_fee_detail_id", mftwfRequest.getI_fee_detail_id())
     //                .setParameter("i_fee_grp_id", mftwfRequest.getI_fee_grp_id())
     //                .setParameter("i_fee_detail_nm_e", mftwfRequest.getI_fee_detail_nm_e())
     //                .setParameter("i_fee_detail_nm_b", mftwfRequest.getI_fee_detail_nm_b())
     //                .setParameter("i_fee_amt", mftwfRequest.getI_fee_amt())
     //                .setParameter("i_promo_startdt", mftwfRequest.getI_promo_startdt() != null ? mftwfRequest.getI_promo_startdt() : null)
     //                .setParameter("i_promo_enddt", mftwfRequest.getI_promo_enddt() != null ? mftwfRequest.getI_promo_enddt() : null)
     //                .setParameter("i_promo_fee", mftwfRequest.getI_promo_fee() != null ? mftwfRequest.getI_promo_fee() : null)
     //                .setParameter("i_tax_cd_id", mftwfRequest.getI_tax_cd_id())
     //                .setParameter("i_allow_otc", mftwfRequest.getI_allow_otc())
     //                .setParameter("i_ll_parent_id", mftwfRequest.getI_ll_parent_id() != null ? mftwfRequest.getI_ll_parent_id() : null)
     //                .setParameter("i_ll_start_day", mftwfRequest.getI_ll_start_day() != null ? mftwfRequest.getI_ll_start_day() : null)
     //                .setParameter("i_ll_end_day", mftwfRequest.getI_ll_end_day() != null ? mftwfRequest.getI_ll_end_day() : null)
     //                .setParameter("i_ll_start_mth", mftwfRequest.getI_ll_start_mth() != null ? mftwfRequest.getI_ll_start_mth() : null)
     //                .setParameter("i_ll_end_mth", mftwfRequest.getI_ll_end_mth() != null ? mftwfRequest.getI_ll_end_mth() : null)
     //                .setParameter("i_ledger_cd", mftwfRequest.getI_ledger_cd())
     //                .setParameter("i_ss_cd", mftwfRequest.getI_ss_cd())
     //                .setParameter("i_effective_date", mftwfRequest.getI_effective_date())
     //                .setParameter("i_modified_by", mftwfRequest.getI_modified_by())
     //                .setParameter("i_status", mftwfRequest.getI_status())
     //                .setParameter("i_assign_to", mftwfRequest.getI_assign_to() != null ? mftwfRequest.getI_assign_to() : null)
     //                .setParameter("i_remark", mftwfRequest.getI_remark())
     //                .setParameter("i_action", mftwfRequest.getI_action())
     //                .setParameter("i_r_fee_det_nm", mftwfRequest.getI_r_fee_det_nm() != null ? mftwfRequest.getI_r_fee_det_nm() : null)
     //                .setParameter("i_r_fee_amt", mftwfRequest.getI_r_fee_amt() != null ? mftwfRequest.getI_r_fee_amt() : null)
     //                .setParameter("i_r_ss_cd", mftwfRequest.getI_r_ss_cd() != null ? mftwfRequest.getI_r_ss_cd() : null)
     //                .setParameter("i_r_promo_startdt", mftwfRequest.getI_r_promo_startdt() != null ? mftwfRequest.getI_r_promo_startdt() : null)
     //                .setParameter("i_r_promo_enddt", mftwfRequest.getI_r_promo_enddt() != null ? mftwfRequest.getI_r_promo_enddt() : null)
     //                .setParameter("i_r_ll_required", mftwfRequest.getI_r_ll_required() != null ? mftwfRequest.getI_r_ll_required() : null)
     //                .setParameter("i_r_add_notes", mftwfRequest.getI_r_add_notes() != null ? mftwfRequest.getI_r_add_notes() : null)
     //                .setParameter("i_mft_status", mftwfRequest.getI_mft_status() != null ? mftwfRequest.getI_mft_status() : null)
     //                .setParameter("i_r_promo_fee", mftwfRequest.getI_r_promo_fee() != null ? mftwfRequest.getI_r_promo_fee() : null);

     //      // Check if the dates are null and set accordingly
     //      if (mftwfRequest.getI_fee_detail_pk() != null) {
     //           query.setParameter("i_fee_detail_pk", mftwfRequest.getI_fee_detail_pk());
     //      } else {
     //           query.setParameter("i_fee_detail_pk", null);
     //      }

     //      if (mftwfRequest.getI_promo_startdt() != null) {
     //           query.setParameter("i_promo_startdt", mftwfRequest.getI_promo_startdt());
     //      } else {
     //           query.setParameter("i_promo_startdt", null);
     //      }

     //      if (mftwfRequest.getI_promo_enddt() != null) {
     //           query.setParameter("i_promo_enddt", mftwfRequest.getI_promo_enddt());
     //      } else {
     //           query.setParameter("i_promo_enddt", null);
     //      }

     //      if (mftwfRequest.getI_promo_fee() != null) {
     //           query.setParameter("i_promo_fee", mftwfRequest.getI_promo_fee());
     //      } else {
     //           query.setParameter("i_promo_fee", null);
     //      }

     //      if (mftwfRequest.getI_ll_parent_id() != null) {
     //           query.setParameter("i_ll_parent_id", mftwfRequest.getI_ll_parent_id());
     //      } else {
     //           query.setParameter("i_ll_parent_id", null);
     //      }

     //      if (mftwfRequest.getI_ll_start_day() != null) {
     //           query.setParameter("i_ll_start_day", mftwfRequest.getI_ll_start_day());
     //      } else {
     //           query.setParameter("i_ll_start_day", null);
     //      }

     //      if (mftwfRequest.getI_ll_start_mth() != null) {
     //           query.setParameter("i_ll_start_mth", mftwfRequest.getI_ll_start_mth());
     //      } else {
     //           query.setParameter("i_ll_start_mth", null);
     //      }

     //      if (mftwfRequest.getI_ll_end_day() != null) {
     //           query.setParameter("i_ll_end_day", mftwfRequest.getI_ll_end_day());
     //      } else {
     //           query.setParameter("i_ll_end_day", null);
     //      }

     //      if (mftwfRequest.getI_ll_end_mth() != null) {
     //           query.setParameter("i_ll_end_mth", mftwfRequest.getI_ll_end_mth());
     //      } else {
     //           query.setParameter("i_ll_end_mth", null);
     //      }

     //      if (mftwfRequest.getI_assign_to() != null) {
     //           query.setParameter("i_assign_to", mftwfRequest.getI_assign_to());
     //      } else {
     //           query.setParameter("i_assign_to", null);
     //      }

     //      if (mftwfRequest.getI_r_fee_det_nm() != null) {
     //           query.setParameter("i_r_fee_det_nm", mftwfRequest.getI_r_fee_det_nm());
     //      } else {
     //           query.setParameter("i_r_fee_det_nm", null);
     //      }

     //      if (mftwfRequest.getI_r_fee_amt() != null) {
     //           query.setParameter("i_r_fee_amt", mftwfRequest.getI_r_fee_amt());
     //      } else {
     //           query.setParameter("i_r_fee_amt", null);
     //      }

     //      if (mftwfRequest.getI_r_ss_cd() != null) {
     //           query.setParameter("i_r_ss_cd", mftwfRequest.getI_r_ss_cd());
     //      } else {
     //           query.setParameter("i_r_ss_cd", null);
     //      }

     //      if (mftwfRequest.getI_r_promo_startdt() != null) {
     //           query.setParameter("i_r_promo_startdt", mftwfRequest.getI_r_promo_startdt());
     //      } else {
     //           query.setParameter("i_r_promo_startdt", null);
     //      }

     //      if (mftwfRequest.getI_r_promo_enddt() != null) {
     //           query.setParameter("i_r_promo_enddt", mftwfRequest.getI_r_promo_enddt());
     //      } else {
     //           query.setParameter("i_r_promo_enddt", null);
     //      }

     //      if (mftwfRequest.getI_r_ll_required() != null) {
     //           query.setParameter("i_r_ll_required", mftwfRequest.getI_r_ll_required());
     //      } else {
     //           query.setParameter("i_r_ll_required", null);
     //      }

     //      if (mftwfRequest.getI_r_add_notes() != null) {
     //           query.setParameter("i_r_add_notes", mftwfRequest.getI_r_add_notes());
     //      } else {
     //           query.setParameter("i_r_add_notes", null);
     //      }

     //      if (mftwfRequest.getI_mft_status() != null) {
     //           query.setParameter("i_mft_status", mftwfRequest.getI_mft_status());
     //      } else {
     //           query.setParameter("i_mft_status", null);
     //      }

     //      if (mftwfRequest.getI_r_promo_fee() != null) {
     //           query.setParameter("i_r_promo_fee", mftwfRequest.getI_r_promo_fee());
     //      } else {
     //           query.setParameter("i_r_promo_fee", null);
     //      }

     //      return (Integer) query.getSingleResult();

     // }

     // // @Override
     // // public Integer sp_insmftwfdoc(BigInteger i_wf_id, String i_file_nm, Blob i_file_content, String i_file_type,
     // //           Integer i_file_size,
     // //           String i_created_by, String i_modified_by, String i_status) {
     // //      Query query = entityManager.createNativeQuery(
     // //                "CALL sp_insmftwfdoc(:i_wf_id, :i_file_nm, :i_file_content, :i_file_type, :i_file_size, :i_created_by, :i_modified_by, :i_status)")
     // //                .setParameter("i_wf_id", i_wf_id)
     // //                .setParameter("i_file_nm", i_file_nm)
     // //                .setParameter("i_file_content", i_file_content)
     // //                .setParameter("i_file_type", i_file_type)
     // //                .setParameter("i_file_size", i_file_size)
     // //                .setParameter("i_created_by", i_created_by)
     // //                .setParameter("i_modified_by", i_modified_by)
     // //                .setParameter("i_status", i_status);
     // //      return (Integer) query.getSingleResult();
     // // }

     // @Override
     // public List<Object[]> sp_getmftwfdoc(MFTWFDocRequest mftwfDocRequest) {
     //      Query query = entityManager.createNativeQuery("CALL sp_getmftwfdoc(:i_page, :i_size, :i_wf_id, :i_status)")
     //                .setParameter("i_page", mftwfDocRequest.getI_page())
     //                .setParameter("i_size", mftwfDocRequest.getI_size())
     //                .setParameter("i_wf_id", mftwfDocRequest.getI_wf_id())
     //                .setParameter("i_status", mftwfDocRequest.getI_status());

     //      return query.getResultList();
     // }

     // @Override
     // public List<Object[]> sp_getwfh_status(MFTWFHistoryRequest mftwfHistoryRequest) {
     //      Query query = entityManager.createNativeQuery("CALL sp_getwfh_status(:i_task_id, :i_status)")
     //                .setParameter("i_task_id", mftwfHistoryRequest.getI_task_id())
     //                .setParameter("i_status", mftwfHistoryRequest.getI_status());
     //      return query.getResultList();

     // }

     // @Override
     // public List<Object[]> sp_getfeedetailitems(MFTRequest mftRequest) {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_getfeedetailitems(:fee_detail_id, :fee_grp_id, :ss_cd, :last_sync_dt, :exclude_deleted)")

     //                .setParameter("fee_detail_id", mftRequest.getFee_detail_id() != null ? mftRequest.getFee_detail_id() : null)
     //                .setParameter("fee_grp_id", mftRequest.getFee_grp_id() != null ? mftRequest.getFee_grp_id() : null)
     //                .setParameter("ss_cd", mftRequest.getSs_cd() != null ? mftRequest.getSs_cd() : null)
     //                .setParameter("last_sync_dt", mftRequest.getLast_sync_dt() != null ? mftRequest.getLast_sync_dt() : null)
     //                .setParameter("exclude_deleted", mftRequest.getExclude_deleted() != null ? mftRequest.getExclude_deleted() : null);

     //      if (mftRequest.getFee_detail_id() != null) {
     //           query.setParameter("fee_detail_id", mftRequest.getFee_detail_id());
     //      } else {
     //           query.setParameter("fee_detail_id", null);
     //      }

     //      if (mftRequest.getFee_grp_id() != null) {
     //           query.setParameter("fee_grp_id", mftRequest.getFee_grp_id());
     //      } else {
     //           query.setParameter("fee_grp_id", null);
     //      }

     //      if (mftRequest.getSs_cd() != null) {
     //           query.setParameter("ss_cd", mftRequest.getSs_cd());
     //      } else {
     //           query.setParameter("ss_cd", null);
     //      }

     //      if (mftRequest.getLast_sync_dt() != null) {
     //           query.setParameter("last_sync_dt", mftRequest.getLast_sync_dt());
     //      } else {
     //           query.setParameter("last_sync_dt", null);
     //      }

     //      if (mftRequest.getExclude_deleted() != null) {
     //           query.setParameter("exclude_deleted", mftRequest.getExclude_deleted());
     //      } else {
     //           query.setParameter("exclude_deleted", null);
     //      }

     //      return query.getResultList();
     // }

     // @Override
     // public Blob sp_getmftwfdocfilecontent(MFTWFDocRequest mftwfDocRequest) {
     //      Query query = entityManager.createNativeQuery("CALL sp_getmftwfdocfilecontent(:i_wfdoc_id)")
     //                .setParameter("i_wfdoc_id", mftwfDocRequest.getI_wfdoc_id());

     //      return (Blob) query.getSingleResult();
     // }
     // #endregion

     // #region mtt
     // @Override
     // public List<Object[]> sp_getMTTItem(Integer mttId) {
     //      Query query = entityManager.createNativeQuery("CALL sp_getMTTItem(:i_mtt_id)")
     //                .setParameter("i_mtt_id", mttId);
     //      return query.getResultList();
     // }

     // @Override
     // public Integer sp_updateMTT(String ornNo, String custNm, String custAddr1, String custAddr2, String custAddr3,
     //           String custPostCode, String custCity, String custState) {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_updateMTT(:i_orn_no,:i_cust_nm,:i_cust_addr_1,:i_cust_addr_2,:i_cust_addr_3,:i_cust_postcode,:i_cust_city,:i_cust_state)")
     //                .setParameter("i_orn_no", ornNo)
     //                .setParameter("i_billing_nm", custNm)
     //                .setParameter("i_cust_addr_1", custAddr1)
     //                .setParameter("i_cust_addr_2", custAddr2)
     //                .setParameter("i_cust_addr_3", custAddr3)
     //                .setParameter("i_cust_postcode", custPostCode)
     //                .setParameter("i_cust_city", custCity)
     //                .setParameter("i_cust_state", custState);
     //      // Use getSingleResult to retrieve the single integer value
     //      Integer result = (Integer) query.getSingleResult();
     //      return result;
     // }

     // @Override
     // public String sp_checkLatestOrderStatus(String ornNo) {
     //      // TODO Auto-generated method stub
     //      Query query = entityManager.createNativeQuery("CALL sp_checkLatestOrderStatus_v2(:i_orn_no)")
     //                .setParameter("i_orn_no", ornNo);
     //      // Use getSingleResult to retrieve the single integer value
     //      String result = query.getSingleResult().toString();
     //      return result;
     // }

     // @Override
     // public Object[] sp_insertPayment(Integer mttID, String pymtMethod, String serviceID, BigDecimal pymtAmt,
     //           String langCd, String usernameC, String usernameM) {
     //      // TODO Auto-generated method stub
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_inspayment(:i_mtt_id,:i_pg_pymt_method,:i_pg_service_id,:i_pg_pymt_amt,:i_pg_lang_cd,:i_username_c,:i_username_m)")
     //                .setParameter("i_mtt_id", mttID)
     //                .setParameter("i_pg_pymt_method", pymtMethod)
     //                .setParameter("i_pg_service_id", serviceID)
     //                .setParameter("i_pg_pymt_amt", pymtAmt)
     //                .setParameter("i_pg_lang_cd", langCd)
     //                .setParameter("i_username_c", usernameC)
     //                .setParameter("i_username_m", usernameM);
     //      Object[] result = (Object[]) query.getSingleResult();
     //      return result;
     // }

     // @Override
     // public Integer sp_updatePayment(GHLPaymentResponse ghlResponse, String usernameM) {
     //      // TODO Auto-generated method stub
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_updmttpg(:i_pymt_id ,:i_txn_type ,:i_pymt_method ,:i_service_id ,:i_pymt_amt ,:i_curr_cd,:i_txn_id,:i_issuing_bank,:i_auth_cd,:i_txn_status,:i_txn_msg,:i_hash_value,:i_hash_value2,:i_bank_refno,:i_token_type,:i_token,:i_resp_time,:i_cardno_mask,:i_cardholder,:i_cardtype,:i_cardexp,:i_modified_by)")
     //                .setParameter("i_pymt_id", ghlResponse.getPaymentID())
     //                .setParameter("i_txn_type", ghlResponse.getTransactionType())
     //                .setParameter("i_pymt_method", ghlResponse.getPaymentMethod())
     //                .setParameter("i_service_id", ghlResponse.getServiceID())
     //                .setParameter("i_pymt_amt", ghlResponse.getAmount())
     //                .setParameter("i_curr_cd", ghlResponse.getCurrencyCode())
     //                .setParameter("i_txn_id", ghlResponse.getTxnID())
     //                .setParameter("i_issuing_bank", ghlResponse.getIssuingBank())
     //                .setParameter("i_auth_cd", ghlResponse.getAuthCode())
     //                .setParameter("i_txn_status", ghlResponse.getTxnStatus())
     //                .setParameter("i_txn_msg", ghlResponse.getTxnMsg())
     //                .setParameter("i_hash_value", ghlResponse.getHashValue())
     //                .setParameter("i_hash_value2", ghlResponse.getHashValue2())
     //                .setParameter("i_bank_refno", ghlResponse.getBankRefNo())
     //                .setParameter("i_token_type", ghlResponse.getTokenType())
     //                .setParameter("i_token", ghlResponse.getToken())
     //                .setParameter("i_resp_time", ghlResponse.getRespTime())
     //                .setParameter("i_cardno_mask", ghlResponse.getCardNoMask())
     //                .setParameter("i_cardholder", ghlResponse.getCardHolder())
     //                .setParameter("i_cardtype", ghlResponse.getCardType())
     //                .setParameter("i_cardexp", ghlResponse.getCardExp())
     //                .setParameter("i_modified_by", usernameM);
     //      return (Integer) query.getSingleResult();
     // }

     // @Override
     // public Integer sp_checkPaymentRcpt(String ornNo) {
     //      // TODO Auto-generated method stub
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_checkpymtrcpt(:i_orn_no)")
     //                .setParameter("i_orn_no", ornNo);
     //      return (Integer) query.getSingleResult();
     // }

     // @Override
     // public Object[] sp_insertReceipt(String paymentId, String username) {
     //      // TODO Auto-generated method stub
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_insmttrcpt(:i_pg_pymt_id, :i_created_by, :i_modified_by)")
     //                .setParameter("i_pg_pymt_id", paymentId)
     //                .setParameter("i_created_by", username)
     //                .setParameter("i_modified_by", username);
     //      Object[] result = (Object[]) query.getSingleResult();
     //      return result;
     // }

     // @Override
     // public Integer sp_updateMTTRcpt(Integer mttRcptID, String verID, String ssDocRefID) {
     //      // TODO Auto-generated method stub
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_updmttrcpt(:i_mtt_rcpt_id, :i_ver_id, :i_ssdocref_id)")
     //                .setParameter("i_mtt_rcpt_id", mttRcptID)
     //                .setParameter("i_ver_id", verID)
     //                .setParameter("i_ssdocref_id", ssDocRefID);
     //      return (Integer) query.getSingleResult();
     // }
     // // #endregion

     // // // #region Fee Group Start
     // // @Override
     // // public Integer sp_insfeegroup(FeeGrpRequest feeGroupRequest,
     // //           String i_created_by, String i_modified_by, String i_status) {
     // //      Query query = entityManager.createNativeQuery(
     // //                "CALL sp_insfeegroup(:i_fee_grp_nm_en, :i_fee_grp_nm_bm, :i_created_by, :i_modified_by, :i_status)")
     // //                .setParameter("i_fee_grp_nm_en", feeGroupRequest.getI_fee_grp_nm_en())
     // //                .setParameter("i_fee_grp_nm_bm", feeGroupRequest.getI_fee_grp_nm_bm())
     // //                .setParameter("i_created_by", i_created_by)
     // //                .setParameter("i_modified_by", i_modified_by)
     // //                .setParameter("i_status", i_status);
     // //      return (Integer) query.getSingleResult();
     // // }

     // // @Override
     // // public Integer sp_updfeegroup(FeeGrpRequest feeGroupRequest, String i_fee_grp_nm_en, String i_fee_grp_nm_bm,
     // // String i_modified_by, String i_status) {
     // //      Query query = entityManager.createNativeQuery(
     // //                "CALL sp_updfeegroup(:i_fee_grp_id, :i_fee_grp_nm_en, :i_fee_grp_nm_bm, :i_modified_by, :i_status)")
     // //                .setParameter("i_fee_grp_id", feeGroupRequest.getI_fee_grp_id())
     // //                .setParameter("i_fee_grp_nm_en", i_fee_grp_nm_en)
     // //                .setParameter("i_fee_grp_nm_bm", i_fee_grp_nm_bm)
     // //                .setParameter("i_modified_by", i_modified_by)
     // //                .setParameter("i_status", i_status);
     // //      return (Integer) query.getSingleResult();
     // // }

     // // @Override
     // // public List<Object[]> sp_getfeegroup_v2(FeeGrpRequest feeGroupRequest) {
     // //      Query query = entityManager.createNativeQuery(
     // //                "CALL sp_getfeegroup_v2(:i_page, :i_size, :i_fee_grp_nm_en, :i_fee_grp_nm_bm, :i_modified_by, :i_dt_modified_fr, :i_dt_modified_to, :i_status)")
     // //                .setParameter("i_page", feeGroupRequest.getI_page())
     // //                .setParameter("i_size", feeGroupRequest.getI_size())
     // //                // .setParameter("i_fee_grp_id", i_fee_grp_id)
     // //                .setParameter("i_fee_grp_nm_en", feeGroupRequest.getI_fee_grp_nm_en())
     // //                .setParameter("i_fee_grp_nm_bm", feeGroupRequest.getI_fee_grp_nm_bm())
     // //                .setParameter("i_modified_by", feeGroupRequest.getI_modified_by())
     // //                .setParameter("i_dt_modified_fr", feeGroupRequest.getI_dt_modified_fr() != null ? feeGroupRequest.getI_dt_modified_fr() : null)
     // //                .setParameter("i_dt_modified_to", feeGroupRequest.getI_dt_modified_to() != null ? feeGroupRequest.getI_dt_modified_to() : null)
     // //                .setParameter("i_status", feeGroupRequest.getI_status());

     // //      // Check if the dates are null and set accordingly
     // //      if (feeGroupRequest.getI_dt_modified_fr() != null) {
     // //           query.setParameter("i_dt_modified_fr", feeGroupRequest.getI_dt_modified_fr());
     // //      } else {
     // //           query.setParameter("i_dt_modified_fr", null);
     // //      }

     // //      if (feeGroupRequest.getI_dt_modified_to() != null) {
     // //           query.setParameter("i_dt_modified_to", feeGroupRequest.getI_dt_modified_to());
     // //      } else {
     // //           query.setParameter("i_dt_modified_to", null);
     // //      }
     // //      return query.getResultList();
     // // }

     // // @Override
     // // public Integer sp_checkfeegrpbyid(FeeGrpRequest feeGroupRequest) {
     // //      Query query = entityManager.createNativeQuery(
     // //                "CALL sp_checkfeetaxbyid(:i_fee_grp_id,:i_tax_cd_id)")
     // //                .setParameter("i_fee_grp_id", feeGroupRequest.getI_fee_grp_id())
     // //                .setParameter("i_tax_cd_id", "");
     // //      return (Integer) query.getSingleResult();
     // // }
     // // #endregion

     // #region DI Aging start
     // // @Override
     // public BigInteger sp_insdiagingrpt(DeferredIncomeAgingRequest DIRequest,String i_p_email, String i_created_by, String i_modified_by ) {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_insdiagingrpt(:i_p_dt_req, :i_p_tmn_status, :i_p_ent_ty, :i_p_ent_nm, :i_p_txn_ty, :i_p_status, :i_p_dt_exp_fr, :i_p_dt_exp_to, :i_p_dt_eff_fr, :i_p_dt_eff_to, :i_p_dt_app_fr, :i_p_dt_app_to, :i_p_dt_tmn_fr, :i_p_dt_tmn_to, :i_created_by, :i_modified_by, :i_status, :i_p_email, :i_p_file_type, :i_p_file_size, :i_p_file_nm, :i_p_batch_no, :i_p_fms_ref_no)")
     //                .setParameter("i_p_dt_req", DIRequest.getI_p_dt_req())
     //                .setParameter("i_p_tmn_status", DIRequest.getI_p_tmn_status() != null ? DIRequest.getI_p_tmn_status() : null)
     //                .setParameter("i_p_ent_ty", DIRequest.getI_p_ent_ty() != null ? DIRequest.getI_p_ent_ty() : null)
     //                .setParameter("i_p_ent_nm", DIRequest.getI_p_ent_nm() != null ? DIRequest.getI_p_ent_nm() : null)
     //                .setParameter("i_p_txn_ty", DIRequest.getI_p_txn_ty() != null ? DIRequest.getI_p_txn_ty() : null)
     //                .setParameter("i_p_status", DIRequest.getI_p_status() != null ? DIRequest.getI_p_status() : null)
     //                .setParameter("i_p_dt_exp_fr", DIRequest.getI_p_dt_exp_fr() != null ? DIRequest.getI_p_dt_exp_fr() : null)
     //                .setParameter("i_p_dt_exp_to", DIRequest.getI_p_dt_exp_to() != null ? DIRequest.getI_p_dt_exp_to() : null)
     //                .setParameter("i_p_dt_eff_fr", DIRequest.getI_p_dt_eff_fr())
     //                .setParameter("i_p_dt_eff_to", DIRequest.getI_p_dt_eff_to())
     //                .setParameter("i_p_dt_app_fr", DIRequest.getI_p_dt_app_fr() != null ? DIRequest.getI_p_dt_app_fr() : null)
     //                .setParameter("i_p_dt_app_to", DIRequest.getI_p_dt_app_to() != null ? DIRequest.getI_p_dt_app_to() : null)
     //                .setParameter("i_p_dt_tmn_fr", DIRequest.getI_p_dt_tmn_fr() != null ? DIRequest.getI_p_dt_tmn_fr() : null)
     //                .setParameter("i_p_dt_tmn_to", DIRequest.getI_p_dt_tmn_to() != null ? DIRequest.getI_p_dt_tmn_to() : null)
     //                .setParameter("i_created_by", DIRequest.getI_created_by())
     //                .setParameter("i_modified_by", DIRequest.getI_modified_by())
     //                .setParameter("i_status", DIRequest.getI_status())
     //                .setParameter("i_p_email", DIRequest.getI_p_email() != null ? DIRequest.getI_p_email() : null)
     //                .setParameter("i_p_file_type", DIRequest.getI_p_file_type() != null ? DIRequest.getI_p_file_type() : null)
     //                .setParameter("i_p_file_size", DIRequest.getI_p_file_size() != null ? DIRequest.getI_p_file_size() : null)
     //                .setParameter("i_p_file_nm", DIRequest.getI_p_file_nm() != null ? DIRequest.getI_p_file_nm() : null)
     //                .setParameter("i_p_batch_no", DIRequest.getI_p_batch_no() != null ? DIRequest.getI_p_batch_no() : null)
     //                .setParameter("i_p_fms_ref_no", DIRequest.getI_p_fms_ref_no() != null ? DIRequest.getI_p_fms_ref_no() : null);

     //      // Check if the dates are null and set accordingly
     //      if (DIRequest.getI_p_tmn_status() != null) {
     //           query.setParameter("i_p_tmn_status", DIRequest.getI_p_tmn_status());
     //      } else {
     //           query.setParameter("i_p_tmn_status", null);
     //      }

     //      if (DIRequest.getI_p_ent_ty() != null) {
     //           query.setParameter("i_p_ent_ty", DIRequest.getI_p_ent_ty());
     //      } else {
     //           query.setParameter("i_p_ent_ty", null);
     //      }

     //      if (DIRequest.getI_p_ent_nm() != null) {
     //           query.setParameter("i_p_ent_nm", DIRequest.getI_p_ent_nm());
     //      } else {
     //           query.setParameter("i_p_ent_nm", null);
     //      }

     //      if (DIRequest.getI_p_txn_ty() != null) {
     //           query.setParameter("i_p_txn_ty", DIRequest.getI_p_txn_ty());
     //      } else {
     //           query.setParameter("i_p_txn_ty", null);
     //      }

     //      if (DIRequest.getI_p_status() != null) {
     //           query.setParameter("i_p_status", DIRequest.getI_p_status());
     //      } else {
     //           query.setParameter("i_p_status", null);
     //      }

     //      if (DIRequest.getI_p_dt_eff_fr() != null) {
     //           query.setParameter("i_p_dt_eff_fr", DIRequest.getI_p_dt_eff_fr());
     //      } else {
     //           query.setParameter("i_p_dt_eff_fr", null);
     //      }

     //      if (DIRequest.getI_p_dt_eff_to() != null) {
     //           query.setParameter("i_p_dt_eff_to", DIRequest.getI_p_dt_eff_to());
     //      } else {
     //           query.setParameter("i_p_dt_eff_to", null);
     //      }

     //      if (DIRequest.getI_p_dt_app_fr() != null) {
     //           query.setParameter("i_p_dt_app_fr", DIRequest.getI_p_dt_app_fr());
     //      } else {
     //           query.setParameter("i_p_dt_app_fr", null);
     //      }

     //      if (DIRequest.getI_p_dt_app_to() != null) {
     //           query.setParameter("i_p_dt_app_to", DIRequest.getI_p_dt_app_to());
     //      } else {
     //           query.setParameter("i_p_dt_app_to", null);
     //      }

     //      if (DIRequest.getI_p_dt_tmn_fr() != null) {
     //           query.setParameter("i_p_dt_tmn_fr", DIRequest.getI_p_dt_tmn_fr());
     //      } else {
     //           query.setParameter("i_p_dt_tmn_fr", null);
     //      }

     //      if (DIRequest.getI_p_dt_tmn_to() != null) {
     //           query.setParameter("i_p_dt_tmn_to", DIRequest.getI_p_dt_tmn_to());
     //      } else {
     //           query.setParameter("i_p_dt_tmn_to", null);
     //      }

     //      if (DIRequest.getI_p_email() != null) {
     //           query.setParameter("i_p_email", DIRequest.getI_p_email());
     //      } else {
     //           query.setParameter("i_p_email", null);
     //      }

     //      if (DIRequest.getI_p_file_type() != null) {
     //           query.setParameter("i_p_file_type", DIRequest.getI_p_file_type());
     //      } else {
     //           query.setParameter("i_p_file_type", null);
     //      }

     //      if (DIRequest.getI_p_file_size() != null) {
     //           query.setParameter("i_p_file_size", DIRequest.getI_p_file_size());
     //      } else {
     //           query.setParameter("i_p_file_size", null);
     //      }

     //      if (DIRequest.getI_p_file_nm() != null) {
     //           query.setParameter("i_p_file_nm", DIRequest.getI_p_file_nm());
     //      } else {
     //           query.setParameter("i_p_file_nm", null);
     //      }

     //      if (DIRequest.getI_p_batch_no() != null) {
     //           query.setParameter("i_p_batch_no", DIRequest.getI_p_batch_no());
     //      } else {
     //           query.setParameter("i_p_batch_no", null);
     //      }

     //      if (DIRequest.getI_p_fms_ref_no() != null) {
     //           query.setParameter("i_p_fms_ref_no", DIRequest.getI_p_fms_ref_no());
     //      } else {
     //           query.setParameter("i_p_fms_ref_no", null);
     //      }

     //      BigInteger result = (BigInteger) query.getSingleResult();
     //      return result;
     // }

     // // @Override
     // public List<Object[]> sp_getdiaginglistingrpt(DeferredIncomeAgingRequest DIRequest) {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_getdiaginglistingrpt(:i_page, :i_size, :i_rpt_di_age_id, :i_p_dt_req, :i_p_tmn_status, :i_p_ent_ty, :i_p_ent_nm, :i_p_txn_ty, :i_p_status, :i_p_dt_exp_fr, :i_p_dt_exp_to, :i_p_dt_eff_fr, :i_p_dt_eff_to, :i_p_dt_app_fr, :i_p_dt_app_to, :i_p_dt_tmn_fr, :i_p_dt_tmn_to, :i_created_by, :i_modified_by, :i_status, :i_p_email, :i_p_file_type, :i_p_file_size, :i_p_file_nm, :i_p_batch_no, :i_p_fms_ref_no)")
     //                .setParameter("i_page", DIRequest.getI_page())
     //                .setParameter("i_size", DIRequest.getI_size())
     //                .setParameter("i_rpt_di_age_id", DIRequest.getI_rpt_di_age_id())
     //                .setParameter("i_p_dt_req", DIRequest.getI_p_dt_req())
     //                .setParameter("i_p_tmn_status", DIRequest.getI_p_tmn_status())
     //                .setParameter("i_p_ent_ty", DIRequest.getI_p_ent_ty())
     //                .setParameter("i_p_ent_nm", DIRequest.getI_p_ent_nm())
     //                .setParameter("i_p_txn_ty", DIRequest.getI_p_txn_ty())
     //                .setParameter("i_p_status",  DIRequest.getI_p_status())
     //                .setParameter("i_p_dt_exp_fr", DIRequest.getI_p_dt_exp_fr())
     //                .setParameter("i_p_dt_exp_to", DIRequest.getI_p_dt_exp_to())
     //                .setParameter("i_p_dt_eff_fr", DIRequest.getI_p_dt_eff_fr())
     //                .setParameter("i_p_dt_eff_to", DIRequest.getI_p_dt_eff_to())
     //                .setParameter("i_p_dt_app_fr", DIRequest.getI_p_dt_app_fr())
     //                .setParameter("i_p_dt_app_to", DIRequest.getI_p_dt_app_to())
     //                .setParameter("i_p_dt_tmn_fr", DIRequest.getI_p_dt_tmn_fr())
     //                .setParameter("i_p_dt_tmn_to", DIRequest.getI_p_dt_tmn_to())
     //                .setParameter("i_created_by", DIRequest.getI_created_by())
     //                .setParameter("i_modified_by", DIRequest.getI_modified_by())
     //                .setParameter("i_status", DIRequest.getI_status())
     //                .setParameter("i_p_email", DIRequest.getI_p_email())
     //                .setParameter("i_p_file_type", DIRequest.getI_p_file_type())
     //                .setParameter("i_p_file_size", DIRequest.getI_p_file_size())
     //                .setParameter("i_p_file_nm", DIRequest.getI_p_file_nm())
     //                .setParameter("i_p_batch_no", DIRequest.getI_p_batch_no())
     //                .setParameter("i_p_fms_ref_no", DIRequest.getI_p_fms_ref_no());

     //      return query.getResultList();
     // }

     // public Integer sp_upddiagingrpt(DeferredIncomeAgingRequest DIRequest) {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_upddiagingrpt(:i_rpt_di_age_id, :i_status, :i_p_file_size, :i_p_file_nm, :i_modified_by)")
     //                .setParameter("i_rpt_di_age_id", DIRequest.getI_rpt_di_age_id())
     //                .setParameter("i_status", DIRequest.getI_status())
     //                .setParameter("i_p_file_size", DIRequest.getI_p_file_size() != null ? DIRequest.getI_p_file_size() : null)
     //                .setParameter("i_p_file_nm", DIRequest.getI_p_file_nm() != null ? DIRequest.getI_p_file_nm() : null)
     //                .setParameter("i_modified_by", DIRequest.getI_modified_by());

     //      if (DIRequest.getI_p_file_size() != null) {
     //           query.setParameter("i_p_file_size", DIRequest.getI_p_file_size());
     //      } else {
     //           query.setParameter("i_p_file_size", null);
     //      }

     //      if (DIRequest.getI_p_file_nm() != null) {
     //           query.setParameter("i_p_file_nm", DIRequest.getI_p_file_nm());
     //      } else {
     //           query.setParameter("i_p_file_nm", null);
     //      }

     //      return (Integer) query.getSingleResult();
     // }

     // // @Override
     // public List<Object[]> sp_getdiagingrpt(BigInteger i_rpt_di_age_id) {
     //      Query query = entityManager.createNativeQuery("CALL sp_getdiagingrpt(:i_rpt_di_age_id)")
     //                .setParameter("i_rpt_di_age_id", i_rpt_di_age_id);

     //      return query.getResultList();
     // }

     // public Integer sp_getdiagequeuerpt() {
     //      Query query = entityManager.createNativeQuery("CALL sp_getdiagequeuerpt()");

     //      return (Integer) query.getSingleResult();
     // }

     // public List<Object[]> sp_getpendingdiagingrpt() {
     //      Query query = entityManager.createNativeQuery("CALL sp_getpendingdiagingrpt()");

     //      return query.getResultList();
     // }

     // public Integer sp_getpendingdiagingrptbyid(BigInteger i_rpt_di_age_id) {
     //      Query query = entityManager.createNativeQuery("CALL sp_getpendingdiagingrptbyid(:i_rpt_di_age_id)")
     //                .setParameter("i_rpt_di_age_id", i_rpt_di_age_id);

     //      return (Integer) query.getSingleResult();
     // }

     // #endregion
     // #region RIPL Aging start
     // @Override
     // public BigInteger sp_insriplagingrpt(RIPLAgingRequest RIPLRequest, String i_p_email, String i_created_by, String i_modified_by ) {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_insriplagingrpt(:i_p_dt_req, :i_p_imp_status, :i_p_exp_status, :i_p_ent_ty, :i_p_ent_nm, :i_p_dt_due_fr, :i_p_dt_due_to, :i_p_dt_rcpt_fr, :i_p_dt_rcpt_to, :i_p_dt_imp_fr, :i_p_dt_imp_to, :i_p_dt_wo_fr, :i_p_dt_wo_to, :i_created_by, :i_modified_by, :i_status, :i_p_email, :i_p_file_type, :i_p_file_size, :i_p_file_nm, :i_p_batch_no, :i_p_fms_ref_no)")
     //                .setParameter("i_p_dt_req", RIPLRequest.getI_p_dt_req())
     //                .setParameter("i_p_imp_status", RIPLRequest.getI_p_imp_status() != null ? RIPLRequest.getI_p_imp_status() : null)
     //                .setParameter("i_p_exp_status", RIPLRequest.getI_p_exp_status() != null ? RIPLRequest.getI_p_exp_status() : null)
     //                .setParameter("i_p_ent_ty", RIPLRequest.getI_p_ent_ty() != null ? RIPLRequest.getI_p_ent_ty() : null)
     //                .setParameter("i_p_ent_nm", RIPLRequest.getI_p_ent_nm() != null ? RIPLRequest.getI_p_ent_nm() : null)
     //                .setParameter("i_p_dt_due_fr", RIPLRequest.getI_p_dt_due_fr())
     //                .setParameter("i_p_dt_due_to", RIPLRequest.getI_p_dt_due_to())
     //                .setParameter("i_p_dt_rcpt_fr", RIPLRequest.getI_p_dt_rcpt_fr() != null ? RIPLRequest.getI_p_dt_rcpt_fr() : null)
     //                .setParameter("i_p_dt_rcpt_to", RIPLRequest.getI_p_dt_rcpt_to() != null ? RIPLRequest.getI_p_dt_rcpt_to() : null)
     //                .setParameter("i_p_dt_imp_fr", RIPLRequest.getI_p_dt_imp_fr() != null ? RIPLRequest.getI_p_dt_imp_fr() : null)
     //                .setParameter("i_p_dt_imp_to", RIPLRequest.getI_p_dt_imp_to() != null ? RIPLRequest.getI_p_dt_imp_to() : null)
     //                .setParameter("i_p_dt_wo_fr", RIPLRequest.getI_p_dt_wo_fr() != null ? RIPLRequest.getI_p_dt_wo_fr() : null)
     //                .setParameter("i_p_dt_wo_to", RIPLRequest.getI_p_dt_wo_to() != null ? RIPLRequest.getI_p_dt_wo_to() : null)
     //                .setParameter("i_created_by", RIPLRequest.getI_created_by())
     //                .setParameter("i_modified_by", RIPLRequest.getI_modified_by())
     //                .setParameter("i_status", RIPLRequest.getI_status())
     //                .setParameter("i_p_email", RIPLRequest.getI_p_email() != null ? RIPLRequest.getI_p_email() : null)
     //                .setParameter("i_p_file_type", RIPLRequest.getI_p_file_type() != null ? RIPLRequest.getI_p_file_type() : null)
     //                .setParameter("i_p_file_size", RIPLRequest.getI_p_file_size() != null ? RIPLRequest.getI_p_file_size() : null)
     //                .setParameter("i_p_file_nm", RIPLRequest.getI_p_file_nm() != null ? RIPLRequest.getI_p_file_nm() : null)
     //                .setParameter("i_p_batch_no", RIPLRequest.getI_p_batch_no() != null ? RIPLRequest.getI_p_batch_no() : null)
     //                .setParameter("i_p_fms_ref_no", RIPLRequest.getI_p_fms_ref_no() != null ? RIPLRequest.getI_p_fms_ref_no() : null);

     //      // Check if the dates are null and set accordingly
     //      if (RIPLRequest.getI_p_imp_status() != null) {
     //           query.setParameter("i_p_imp_status", RIPLRequest.getI_p_imp_status());
     //      } else {
     //           query.setParameter("i_p_imp_status", null);
     //      }

     //      if (RIPLRequest.getI_p_exp_status() != null) {
     //           query.setParameter("i_p_exp_status", RIPLRequest.getI_p_exp_status());
     //      } else {
     //           query.setParameter("i_p_exp_status", null);
     //      }

     //      if (RIPLRequest.getI_p_ent_ty() != null) {
     //           query.setParameter("i_p_ent_ty", RIPLRequest.getI_p_ent_ty());
     //      } else {
     //           query.setParameter("i_p_ent_ty", null);
     //      }

     //      if (RIPLRequest.getI_p_ent_nm() != null) {
     //           query.setParameter("i_p_ent_nm", RIPLRequest.getI_p_ent_nm());
     //      } else {
     //           query.setParameter("i_p_ent_nm", null);
     //      }

     //      if (RIPLRequest.getI_p_dt_rcpt_fr() != null) {
     //           query.setParameter("i_p_dt_rcpt_fr", RIPLRequest.getI_p_dt_rcpt_fr());
     //      } else {
     //           query.setParameter("i_p_dt_rcpt_fr", null);
     //      }

     //      if (RIPLRequest.getI_p_dt_rcpt_to() != null) {
     //           query.setParameter("i_p_dt_rcpt_to", RIPLRequest.getI_p_dt_rcpt_to());
     //      } else {
     //           query.setParameter("i_p_dt_rcpt_to", null);
     //      }

     //      if (RIPLRequest.getI_p_dt_imp_fr() != null) {
     //           query.setParameter("i_p_dt_imp_fr", RIPLRequest.getI_p_dt_imp_fr());
     //      } else {
     //           query.setParameter("i_p_dt_imp_fr", null);
     //      }

     //      if (RIPLRequest.getI_p_dt_imp_to() != null) {
     //           query.setParameter("i_p_dt_imp_to", RIPLRequest.getI_p_dt_imp_to());
     //      } else {
     //           query.setParameter("i_p_dt_imp_to", null);
     //      }

     //      if (RIPLRequest.getI_p_dt_wo_fr() != null) {
     //           query.setParameter("i_p_dt_wo_fr", RIPLRequest.getI_p_dt_wo_fr());
     //      } else {
     //           query.setParameter("i_p_dt_wo_fr", null);
     //      }

     //      if (RIPLRequest.getI_p_dt_wo_to() != null) {
     //           query.setParameter("i_p_dt_wo_to", RIPLRequest.getI_p_dt_wo_to());
     //      } else {
     //           query.setParameter("i_p_dt_wo_to", null);
     //      }

     //      if (RIPLRequest.getI_p_email() != null) {
     //           query.setParameter("i_p_email", RIPLRequest.getI_p_email());
     //      } else {
     //           query.setParameter("i_p_email", null);
     //      }

     //      if (RIPLRequest.getI_p_file_type() != null) {
     //           query.setParameter("i_p_file_type", RIPLRequest.getI_p_file_type());
     //      } else {
     //           query.setParameter("i_p_file_type", null);
     //      }

     //      if (RIPLRequest.getI_p_file_size() != null) {
     //           query.setParameter("i_p_file_size", RIPLRequest.getI_p_file_size());
     //      } else {
     //           query.setParameter("i_p_file_size", null);
     //      }

     //      if (RIPLRequest.getI_p_file_nm() != null) {
     //           query.setParameter("i_p_file_nm", RIPLRequest.getI_p_file_nm());
     //      } else {
     //           query.setParameter("i_p_file_nm", null);
     //      }

     //      if (RIPLRequest.getI_p_batch_no() != null) {
     //           query.setParameter("i_p_batch_no", RIPLRequest.getI_p_batch_no());
     //      } else {
     //           query.setParameter("i_p_batch_no", null);
     //      }

     //      if (RIPLRequest.getI_p_fms_ref_no() != null) {
     //           query.setParameter("i_p_fms_ref_no", RIPLRequest.getI_p_fms_ref_no());
     //      } else {
     //           query.setParameter("i_p_fms_ref_no", null);
     //      }

     //      BigInteger result = (BigInteger) query.getSingleResult();
     //      return result;
     // }

     // // @Override
     // public List<Object[]> sp_getriplaginglistingrpt(RIPLAgingRequest RIPLRequest) {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_getriplaginglistingrpt(:i_page,:i_size,:i_rpt_ripl_age_id, :i_p_dt_req, :i_p_imp_status, :i_p_exp_status, :i_p_ent_ty, :i_p_ent_nm, :i_p_dt_due_fr, :i_p_dt_due_to, :i_p_dt_rcpt_fr, :i_p_dt_rcpt_to, :i_p_dt_imp_fr, :i_p_dt_imp_to, :i_p_dt_wo_fr, :i_p_dt_wo_to, :i_created_by, :i_modified_by, :i_status, :i_p_email, :i_p_file_type, :i_p_file_size, :i_p_file_nm)")
     //                .setParameter("i_page", RIPLRequest.getI_page())
     //                .setParameter("i_size", RIPLRequest.getI_size())
     //                .setParameter("i_rpt_ripl_age_id", RIPLRequest.getI_rpt_ripl_age_id())
     //                .setParameter("i_p_dt_req", RIPLRequest.getI_p_dt_req())
     //                .setParameter("i_p_imp_status", RIPLRequest.getI_p_imp_status())
     //                .setParameter("i_p_exp_status", RIPLRequest.getI_p_exp_status())
     //                .setParameter("i_p_ent_ty", RIPLRequest.getI_p_ent_ty())
     //                .setParameter("i_p_ent_nm", RIPLRequest.getI_p_ent_nm())
     //                .setParameter("i_p_dt_due_fr", RIPLRequest.getI_p_dt_due_fr())
     //                .setParameter("i_p_dt_due_to", RIPLRequest.getI_p_dt_due_to())
     //                .setParameter("i_p_dt_rcpt_fr", RIPLRequest.getI_p_dt_rcpt_fr())
     //                .setParameter("i_p_dt_rcpt_to", RIPLRequest.getI_p_dt_rcpt_to())
     //                .setParameter("i_p_dt_imp_fr", RIPLRequest.getI_p_dt_imp_fr())
     //                .setParameter("i_p_dt_imp_to", RIPLRequest.getI_p_dt_imp_to())
     //                .setParameter("i_p_dt_wo_fr", RIPLRequest.getI_p_dt_wo_fr())
     //                .setParameter("i_p_dt_wo_to", RIPLRequest.getI_p_dt_wo_to())
     //                .setParameter("i_created_by", RIPLRequest.getI_created_by())
     //                .setParameter("i_modified_by", RIPLRequest.getI_modified_by())
     //                .setParameter("i_status", RIPLRequest.getI_status())
     //                .setParameter("i_p_email", RIPLRequest.getI_p_email())
     //                .setParameter("i_p_file_type", RIPLRequest.getI_p_file_type())
     //                .setParameter("i_p_file_size", RIPLRequest.getI_p_file_size())
     //                .setParameter("i_p_file_nm", RIPLRequest.getI_p_file_nm());

     //      return query.getResultList();
     // }

     // // public Integer sp_updriplagingrpt(BigInteger i_rpt_ripl_age_id, String i_status, Integer i_p_file_size,
     // //           String i_p_file_nm, String i_modified_by) {
     // public Integer sp_updriplagingrpt(RIPLAgingRequest riplAgingRequest) {
     //                Query query = entityManager
     //                .createNativeQuery(
     //                          "CALL sp_updriplagingrpt(:i_rpt_ripl_age_id, :i_status, :i_p_file_size, :i_p_file_nm,:i_modified_by )")
     //                .setParameter("i_rpt_ripl_age_id", riplAgingRequest.getI_rpt_ripl_age_id())
     //                .setParameter("i_status", riplAgingRequest.getI_status())
     //                .setParameter("i_p_file_size", riplAgingRequest.getI_p_file_size() != null ? riplAgingRequest.getI_p_file_size() : null)
     //                .setParameter("i_p_file_nm", riplAgingRequest.getI_p_file_nm() != null ? riplAgingRequest.getI_p_file_nm() : null)
     //                .setParameter("i_modified_by", riplAgingRequest.getI_modified_by());

     //                if (riplAgingRequest.getI_p_file_size() != null) {
     //                     query.setParameter("i_p_file_size", riplAgingRequest.getI_p_file_size());
     //                } else {
     //                     query.setParameter("i_p_file_size", null);
     //                }
          
     //                if (riplAgingRequest.getI_p_file_nm() != null) {
     //                     query.setParameter("i_p_file_nm", riplAgingRequest.getI_p_file_nm());
     //                } else {
     //                     query.setParameter("i_p_file_nm", null);
     //                }
          
     //                return (Integer) query.getSingleResult();
     // }

     // // @Override
     // public List<Object[]> sp_getriplagingrpt(BigInteger i_rpt_ripl_age_id) {
     //      Query query = entityManager.createNativeQuery("CALL sp_getriplagingrpt(:i_rpt_ripl_age_id)")
     //                .setParameter("i_rpt_ripl_age_id", i_rpt_ripl_age_id);

     //      return query.getResultList();
     // }

     // public Integer sp_getriplagequeuerpt() {
     //      Query query = entityManager.createNativeQuery("CALL sp_getriplagequeuerpt()");

     //      return (Integer) query.getSingleResult();
     // }

     // public List<Object[]> sp_getpendingriplagingrpt() {
     //      Query query = entityManager.createNativeQuery("CALL sp_getpendingriplagingrpt()");

     //      return query.getResultList();
     // }

     // public Integer sp_getpendingriplagingrptbyid(BigInteger i_rpt_ripl_age_id) {
     //      Query query = entityManager.createNativeQuery("CALL sp_getpendingriplagingrptbyid(:i_rpt_ripl_age_id)")
     //                .setParameter("i_rpt_ripl_age_id", i_rpt_ripl_age_id);

     //      return (Integer) query.getSingleResult();
     // }

     // #endregion
}