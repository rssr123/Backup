package com.maven.rms.interfaces;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.GHLPaymentResponse;
import com.maven.rms.models.SourceSystemCodeRequest;
import com.maven.rms.models.WhiteIPReq;

public interface ICommonInterface {

        //#region param
        List<Object[]> sp_getparam(Integer page, Integer size, String paramCd, String paramGrpNm);
        List<Object[]> sp_getsourcesystem(SourceSystemCodeRequest sourceSystemCodeRequest);
        List<Object[]> sp_getallbanks();
        List<Object[]> sp_getallrctype();
        List<Object[]> sp_getallbillingstatus();
        List<Object[]> sp_getallbillingmethod();
        // List<Object[]> sp_getsourcesystem(Integer i_page, Integer i_size, BigInteger i_ss_id, String i_ss_cd, String i_ss_nm,
        // String i_modified_by, Date i_dt_modified_fr, Date i_dt_modified_to, String i_status);
        //#endregion

        //#region user role
        //List<Object[]> sp_getuserbyrole(Integer i_page, Integer i_size, String i_role_nm_en, String i_role_nm_bm, String i_status);
        //#endregion

        //#region mft
        List<Object[]> sp_getmft(Integer i_page, Integer i_size, Integer i_fee_detail_pk, String i_fee_detail_id, BigDecimal i_unit_fee_fr, BigDecimal i_unit_fee_to, 
        String i_ss_cd, String i_tax_cd, Date i_dt_modified_fr, Date i_dt_modified_to, String i_modified_by, String i_status);

        Integer sp_updmftwf_status(BigInteger i_wf_id, String i_assign_to, String i_status, String i_remark, String i_modified_by);

        List<Object[]> sp_getmftwf(Integer i_page, Integer i_size, BigInteger i_wf_id, Integer i_fee_detail_pk, String i_fee_detail_id, String i_assign_to, String i_status, String i_created_by, 
          String i_modified_by, String i_modified_by_nm, Date i_dt_modified_fr, Date i_dt_modified_to, Date i_dt_created_fr, Date i_dt_created_to, 
          Date i_dt_effective_fr, Date i_dt_effective_to, String i_ss_cd, String i_wf_is_in_prg);

        BigInteger sp_insmftwf(Integer i_fee_detail_pk, String i_fee_detail_id, Integer i_fee_grp_id, String i_fee_detail_nm_e, String i_fee_detail_nm_b, BigDecimal i_fee_amt, Date i_promo_startdt,
        Date i_promo_enddt, BigDecimal i_promo_fee, Integer i_tax_cd_id, Integer i_allow_otc, String i_ll_parent_id, Integer i_ll_start_day, Integer i_ll_start_mth, Integer i_ll_end_day, Integer i_ll_end_mth,
        String i_ledger_cd, String i_ss_cd, String i_created_by, String i_modified_by, String i_status, Date i_effective_date, String i_remark, String i_assign_to, String i_action,
        String i_r_fee_det_nm, BigDecimal i_r_fee_amt, String i_r_ss_cd, Date i_r_promo_startdt, Date i_r_promo_enddt, Integer i_r_ll_required, String i_r_add_notes, String i_mft_status, BigDecimal i_r_promo_fee);

        List<Object[]> sp_getmftwfhis(Integer i_page, Integer i_size, BigInteger i_wf_id, String i_status);

        List<Object[]> sp_getwfh_ast(String i_task_id, String i_status);

        Integer sp_updmftwf(BigInteger i_wf_id, Integer i_fee_detail_pk, String i_fee_detail_id, Integer i_fee_grp_id, String i_fee_detail_nm_e, String i_fee_detail_nm_b, BigDecimal i_fee_amt, Date i_promo_startdt,
        Date i_promo_enddt, BigDecimal i_promo_fee, Integer i_tax_cd_id, Integer i_allow_otc, String i_ll_parent_id, Integer i_ll_start_day, Integer i_ll_end_day, Integer i_ll_start_mth,  Integer i_ll_end_mth,
        String i_ledger_cd, String i_ss_cd, Date i_effective_date, String i_modified_by, String i_status, String i_assign_to, String i_remark, String i_action,
        String i_r_fee_det_nm, BigDecimal i_r_fee_amt, String i_r_ss_cd, Date i_r_promo_startdt, Date i_r_promo_enddt, Integer i_r_ll_required, String i_r_add_notes, String i_mft_status, BigDecimal i_r_promo_fee);

        Integer sp_insmftwfdoc(BigInteger i_wf_id, String i_file_nm, Blob i_file_content, String i_file_type, Integer i_file_size,
        String i_created_by, String i_modified_by, String i_status);

        List<Object[]> sp_getfeedetailitems(String i_fee_detail_id, Integer i_fee_grp_id, String i_ss_cd, LocalDateTime i_last_sync_dt, Integer i_exclude_deleted);

        List<Object[]> sp_getmftwfdoc(Integer i_page, Integer i_size, BigInteger i_wf_id, String i_status);

        List<Object[]> sp_getwfh_status(String i_task_id, String i_status);

        Blob sp_getmftwfdocfilecontent(BigInteger i_wfdoc_id);

   //  List<Object[]> sp_getuserdetail(String i_ssm4uuserrefno);

        //#endregion

        // #region MTT
        List<Object[]> sp_getMTTItem(Integer mttId);

        Integer sp_updateMTT(String ornNo, String custNm, String custAddr1, String custAddr2, String custAddr3,
                        String custPostCode, String custCity, String custState);

        String sp_checkLatestOrderStatus(String ornNo);

        Object[] sp_insertPayment(Integer mttID, String pymtMethod, String serviceID, BigDecimal pymtAmt, String langCd,
                        String usernameC, String usernameM);

        Integer sp_updatePayment(GHLPaymentResponse ghlResponse,String usernameM);

        Integer sp_checkPaymentRcpt(String ornNo);

        Object[] sp_insertReceipt(String paymentId,String username);

        Integer sp_updateMTTRcpt(Integer mttRcptID, String verID, String ssDocRefID);
        // #endregion

        //#region Fee Group Start
        // List<Object[]> sp_getfeegroup_v2(FeeGrpRequest feeGroupRequest);

        // Integer sp_insfeegroup(FeeGrpRequest feeGroupRequest,
        // String i_created_by, String i_modified_by, String i_status);

        // Integer sp_updfeegroup(FeeGrpRequest feeGroupRequest, String i_fee_grp_nm_en, String i_fee_grp_nm_bm,
        // String i_modified_by, String i_status);
        
        // Integer sp_checkfeegrpbyid(FeeGrpRequest feeGroupRequest);
        //#endregion

        List<Object[]> sp_getpostcode();
        List<Object[]> sp_getwhitelistip();
        Integer sp_inswhiteip(WhiteIPReq insRequest);
        List<String> sp_getuploadedidaman();
        Integer sp_insextaudit(ExtAudit insRequest);
        Integer sp_cleanextaudit();
        Integer sp_updwhiteip(WhiteIPReq insRequest);
}