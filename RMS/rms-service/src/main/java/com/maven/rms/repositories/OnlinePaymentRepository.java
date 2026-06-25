package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.CheckAccrual;
import com.maven.rms.models.GHLRequest;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.models.PaymentItemDetails;
import com.maven.rms.models.PaymentRequest;
import com.maven.rms.services.AuthService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class OnlinePaymentRepository {

    @PersistenceContext
    private EntityManager entityManager;
     
    @Autowired
    private AuthService authService;

    public Integer sp_insertPaymentMTT(PaymentRequest req, String rmsType, String custIp, String usernameC, String usernameM) {
        Query query = entityManager.createNativeQuery("CALL sp_inspaymentmtt("
        		+ ":i_rms_type,:i_ss_cd,:i_orn_no,:i_orn_dt,"
        		+ ":i_cust_ip,:i_cust_nm,:i_cust_addr_1,:i_cust_addr_2,"
        		+ ":i_cust_addr_3 ,:i_cust_postcode,:i_cust_city,:i_cust_state,"
        		+ ":i_cust_email,:i_cust_phone,:i_total_amt ,:i_ss_return_url"
        		+ ",:i_username_c, :i_username_m,:i_email_flag,:i_ss_callback_url)")
                  .setParameter("i_rms_type", rmsType)
                  .setParameter("i_ss_cd", req.getSs_cd())
                  .setParameter("i_orn_no", req.getOrn_no())
                  .setParameter("i_orn_dt", req.getOrn_dt())
                  .setParameter("i_cust_ip", custIp)
                  .setParameter("i_cust_nm", req.getCust_nm())
                  .setParameter("i_cust_addr_1", req.getCust_addr_1())
                  .setParameter("i_cust_addr_2", req.getCust_addr_2())
                  .setParameter("i_cust_addr_3", req.getCust_addr_3())
                  .setParameter("i_cust_postcode", req.getCust_postcode())
                  .setParameter("i_cust_city", req.getCust_city())
                  .setParameter("i_cust_state", req.getCust_state())
                  .setParameter("i_cust_email", req.getCust_email())
                  .setParameter("i_cust_phone", req.getCust_phone())
                  .setParameter("i_total_amt", req.getTotal_amt())
                  .setParameter("i_ss_return_url", req.getSs_return_url())
                  .setParameter("i_username_c", usernameC)
                  .setParameter("i_username_m", usernameM)
                  .setParameter("i_email_flag", req.getEmail_flag())
                  .setParameter("i_ss_callback_url", req.getSs_callback_url());

        Integer result = (Integer) query.getSingleResult();
        return result;
   }
    
    public Integer sp_insertPaymentMTTItem(PaymentItemDetails item, Integer MTTId, String usernameC, String usernameM) {
         Query query = entityManager.createNativeQuery(
                   "CALL sp_inspaymentmttitem("
                   + ":i_mtt_id,:i_fee_detail_id,:i_item_ref_no,:i_item_desc,"
                   + ":i_line_no,:i_qty,:i_unit_fee,:i_gross_amt,"
                   + ":i_grant_cd,:i_disc_amt,:i_tax_pct,:i_tax_amt,"
                   + ":i_net_amt,:i_entity_type,:i_entity_no,:i_entity_nm,"
                   + ":i_cp_no,:i_cp_tier,:i_cp_tier_amt,:i_cp_tier_disc_pct,"
                   + ":i_username_c,:i_username_m,"
                   + ":i_dps_id,:i_dps_task,:i_pymt_case,:i_location,:i_lit_item_ref,:i_txn_type,:i_calendar_yr)")
                   .setParameter("i_mtt_id", MTTId)
                   .setParameter("i_fee_detail_id", item.getFee_detail_id())
                   .setParameter("i_item_ref_no", item.getItem_ref_no())
                   .setParameter("i_item_desc", item.getItem_desc())
                   .setParameter("i_line_no", item.getLine_no())
                   .setParameter("i_qty", item.getQty())
                   .setParameter("i_unit_fee", item.getUnit_fee())
                   .setParameter("i_gross_amt", item.getGross_amt())
                   .setParameter("i_grant_cd", item.getGrant_cd())
                   .setParameter("i_disc_amt", item.getDisc_amt())
                   .setParameter("i_tax_pct", item.getTax_pct())
                   .setParameter("i_tax_amt", item.getTax_amt())
                   .setParameter("i_net_amt", item.getNet_amt())
                   .setParameter("i_entity_type", item.getEntity_type())
                   .setParameter("i_entity_no", item.getEntity_no())
                   .setParameter("i_entity_nm", item.getEntity_nm())
                   .setParameter("i_cp_no", item.getCp_no())
                   .setParameter("i_cp_tier", item.getCp_tier())
                   .setParameter("i_cp_tier_amt", item.getCp_tier_amt())
                   .setParameter("i_cp_tier_disc_pct", item.getCp_tier_disc_pct())
                   .setParameter("i_username_c", usernameC)
                   .setParameter("i_username_m", usernameM)
                   .setParameter("i_dps_id", item.getDps_id())
                    .setParameter("i_dps_task", item.getDps_task())
                    .setParameter("i_pymt_case", item.getPymt_case())
                    .setParameter("i_location", item.getLocation())
                    .setParameter("i_lit_item_ref", item.getLit_item_ref())
                    .setParameter("i_txn_type", item.getTxn_type())
                    .setParameter("i_calendar_yr", item.getCalendar_yr());

         Integer result = (Integer) query.getSingleResult();
         return result;
    }


    public Integer sp_insghlresp(GHLRequest insRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_insghlresp(:i_txn_ty, :i_pymt_method, :i_service_id, :i_pymt_id, :i_orn_no, :i_amt, " +
            ":i_cur_cd, :i_hash_val, :i_hash_val2, :i_txn_id, :i_iss_bank, :i_txn_status, :i_txn_msg, :i_auth_cd, " +
            ":i_bank_ref_no, :i_token_ty, :i_token, :i_resp_time, :i_card_no_mask, :i_card_holder, :i_card_ty, " +
            ":i_card_exp, :i_param7, :i_created_by, :i_modified_by, :i_processed)"
        );

        //log.error(insRequest.toString());
    
        query.setParameter("i_txn_ty", insRequest.getI_txn_ty());
        query.setParameter("i_pymt_method", insRequest.getI_pymt_method());
        query.setParameter("i_service_id", insRequest.getI_service_id());
        query.setParameter("i_pymt_id", insRequest.getI_pymt_id());
        query.setParameter("i_orn_no", insRequest.getI_orn_no());
        query.setParameter("i_amt", insRequest.getI_amt());
        query.setParameter("i_cur_cd", insRequest.getI_cur_cd());
        query.setParameter("i_hash_val", insRequest.getI_hash_val());
        query.setParameter("i_hash_val2", insRequest.getI_hash_val2());
        query.setParameter("i_txn_id", insRequest.getI_txn_id());
        query.setParameter("i_iss_bank", insRequest.getI_iss_bank());
        query.setParameter("i_txn_status", insRequest.getI_txn_status());
        query.setParameter("i_txn_msg", insRequest.getI_txn_msg());
        query.setParameter("i_auth_cd", insRequest.getI_auth_cd());
        query.setParameter("i_bank_ref_no", insRequest.getI_bank_ref_no());
        query.setParameter("i_token_ty", insRequest.getI_token_ty());
        query.setParameter("i_token", insRequest.getI_token());
        query.setParameter("i_resp_time", insRequest.getI_resp_time());
        query.setParameter("i_card_no_mask", insRequest.getI_card_no_mask());
        query.setParameter("i_card_holder", insRequest.getI_card_holder());
        query.setParameter("i_card_ty", insRequest.getI_card_ty());
        query.setParameter("i_card_exp", insRequest.getI_card_exp());
        query.setParameter("i_param7", insRequest.getI_param7());
        query.setParameter("i_created_by", authService.getLoginUserName());
        query.setParameter("i_modified_by", authService.getLoginUserName());
        query.setParameter("i_processed", insRequest.getI_processed());
    
        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    public Integer sp_updghlresp(GHLRequest insRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_updghlresp(:i_pymt_id)"
        );

        query.setParameter("i_pymt_id", insRequest.getI_pymt_id());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    public List<Object[]> sp_getghlresp() {
        Query query = entityManager.createNativeQuery(
            "CALL sp_getghlresp()");
        
        return query.getResultList();
    }

    public Integer sp_checkAccrual(CheckAccrual checkAccrual) {
        Query query = entityManager.createNativeQuery("CALL sp_checkAccrual(:i_entity_type,:i_entity_no,:i_cp_no,:i_lit_item_ref,:i_txn_type,:i_calendar_yr,:i_fee_details_id)")
                  .setParameter("i_entity_type", checkAccrual.getEntityType())
                  .setParameter("i_entity_no", checkAccrual.getEntityNo())
                  .setParameter("i_cp_no", checkAccrual.getCpNo())
                  .setParameter("i_lit_item_ref", checkAccrual.getLitItemRef())
                  .setParameter("i_txn_type", checkAccrual.getTxnType())
                  .setParameter("i_calendar_yr", checkAccrual.getCalanderYr())
                  .setParameter("i_fee_details_id", checkAccrual.getFeeDetailsId());

        Integer result = (Integer) query.getSingleResult();
        return result;
   }

       
    public Object[] sp_checksubmitbilpaymentstatus(String billing_no) {
        Query query = entityManager.createNativeQuery("CALL sp_checksubmitbilpaymentstatus(:i_billing_no)")
                .setParameter("i_billing_no", billing_no);
        return (Object[]) query.getSingleResult();
    }

    public BigDecimal sp_getrmsfee(OnlinePaymentItem request) {
        Query query = entityManager.createNativeQuery("CALL sp_getrmsfee(:i_fee_detatil_pk)")
                .setParameter("i_fee_detatil_pk", request.getFee_detail_pk());

        BigDecimal result = (BigDecimal) query.getSingleResult();
        return result;
    }

    public String sp_getmttornno(Integer mtt_id) {
        Query query = entityManager.createNativeQuery("CALL sp_getmttornno(:i_mtt_id)")
                .setParameter("i_mtt_id", mtt_id);

        String result = (String) query.getSingleResult();
        return result;
    }

    // 251023- Handle duplicated email sent to customer
    public Integer sp_checkemailsent(String orn_no) {
        Query query = entityManager.createNativeQuery("CALL sp_checkemailsent(:i_orn_no)")
                .setParameter("i_orn_no", orn_no);

        Integer result = (Integer) query.getSingleResult();
        return result;
    }
}
