package com.maven.rms.repositories.OTC;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IOTCCollectionReceiptingRepositoryInterface;
import com.maven.rms.models.OTC.OTCEMVPaymentReq;
import com.maven.rms.models.OTC.OTCEMVRequest;
import com.maven.rms.models.OTC.OTCHistReq;
import com.maven.rms.models.OTC.OTCPaymentRequest;
import com.maven.rms.models.OTC.OTCRcptRequest;
import com.maven.rms.models.OTC.OTCollectionReceiptingRequest;
import com.maven.rms.services.AuthService;

@Repository
public class OTCCollectionReceiptingRepository implements IOTCCollectionReceiptingRepositoryInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuthService authService;

    @Override
    public List<Object[]> sp_getcollectioninfo(OTCollectionReceiptingRequest otCollectionReceiptingRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getcollectioninfo(:i_page, :i_size, :i_coll_slip_no, :i_orn_no, :i_cust_nm,:i_cust_phone,:i_mtt_id)")
                .setParameter("i_page", otCollectionReceiptingRequest.getI_page())
                .setParameter("i_size", otCollectionReceiptingRequest.getI_size())
                .setParameter("i_coll_slip_no", otCollectionReceiptingRequest.getI_coll_slip_no())
                .setParameter("i_orn_no", otCollectionReceiptingRequest.getI_orn_no())
                .setParameter("i_cust_nm", otCollectionReceiptingRequest.getI_cust_nm())
                .setParameter("i_cust_phone", otCollectionReceiptingRequest.getI_cust_phone())
                .setParameter("i_mtt_id", otCollectionReceiptingRequest.getI_mtt_id());
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_otccrpymtitem(OTCollectionReceiptingRequest otCollectionReceiptingRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_otccrpymtitem(:i_coll_slip_no, :i_orn_no)")
                .setParameter("i_coll_slip_no", otCollectionReceiptingRequest.getI_coll_slip_no())
                .setParameter("i_orn_no", otCollectionReceiptingRequest.getI_orn_no());
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_otccrpymtitembymtt(OTCollectionReceiptingRequest otCollectionReceiptingRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_otccrpymtitembymtt(:i_mtt_id)")
                .setParameter("i_mtt_id", otCollectionReceiptingRequest.getI_mtt_id());
        return query.getResultList();
    }

    @Override
    public Integer sp_insotcpymt(OTCPaymentRequest insRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_insotcpymt(:i_mtt_id, :i_emv_sale, :i_otc_counter_id, :i_payer_email, :i_otc_pymt_mode, :i_created_by, :i_modified_by)");
        query.setParameter("i_mtt_id", insRequest.getI_mtt_id());
        query.setParameter("i_emv_sale", insRequest.getI_emv_sale());
        query.setParameter("i_otc_counter_id", insRequest.getI_otc_counter_id());
        query.setParameter("i_payer_email", insRequest.getI_payer_email());
        query.setParameter("i_otc_pymt_mode", insRequest.getI_otc_pymt_mode());
        query.setParameter("i_created_by", authService.getLoginUserName());
        query.setParameter("i_modified_by", authService.getLoginUserName());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }


    @Override
    public Integer sp_insotcpymtemv(OTCEMVPaymentReq insRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_insotcpymt(:i_mtt_id, :i_emv_sale, :i_otc_counter_id, :i_payer_email, :i_otc_pymt_mode, :i_created_by, :i_modified_by)");
        query.setParameter("i_mtt_id", insRequest.getI_mtt_id());
        query.setParameter("i_emv_sale", insRequest.getI_emv_sale());
        query.setParameter("i_otc_counter_id", insRequest.getI_otc_counter_id());
        query.setParameter("i_payer_email", insRequest.getI_payer_email());
        query.setParameter("i_otc_pymt_mode", insRequest.getI_otc_pymt_mode());
        query.setParameter("i_created_by", authService.getLoginUserName());
        query.setParameter("i_modified_by", authService.getLoginUserName());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_insotcpymtbody(List<OTCPaymentRequest> insRequests) {

        Integer result = 0;
        // Iterate over each OTC Payment Request in the list
        for (OTCPaymentRequest otcPaymentRequest : insRequests) {
            Query query = entityManager.createNativeQuery(
                    "CALL sp_insotcpymtbody(:i_mtt_id, :i_cash_amt, :i_che_bank_nm, :i_che_no, :i_che_date, " +
                            ":i_che_ba_acct_no, :i_che_amt, :i_che_payer_nm, :i_che_status, " +
                            ":i_bd_bank_nm, :i_bd_no, :i_bd_date, :i_bd_amt, " +
                            ":i_mo_rm_no, :i_mo_payer_nm, :i_mo_id_no, :i_mo_contact_no, :i_mo_amt, :i_mo_date, " +
                            ":i_created_by, :i_modified_by, :i_che_id)");

            // Map parameters from otcPaymentRequest to the query
            query.setParameter("i_mtt_id", otcPaymentRequest.getI_mtt_id());
            query.setParameter("i_cash_amt", otcPaymentRequest.getI_cash_amt());

            // Cheque details
            query.setParameter("i_che_bank_nm", otcPaymentRequest.getI_che_bank_nm());
            query.setParameter("i_che_no", otcPaymentRequest.getI_che_no());
            query.setParameter("i_che_date", otcPaymentRequest.getI_che_date());
            query.setParameter("i_che_ba_acct_no", otcPaymentRequest.getI_che_ba_acct_no());
            query.setParameter("i_che_amt", otcPaymentRequest.getI_che_amt());
            query.setParameter("i_che_payer_nm", otcPaymentRequest.getI_che_payer_nm());
            query.setParameter("i_che_status", otcPaymentRequest.getI_che_status());

            // Bank draft details
            query.setParameter("i_bd_bank_nm", otcPaymentRequest.getI_bd_bank_nm());
            query.setParameter("i_bd_no", otcPaymentRequest.getI_bd_no());
            query.setParameter("i_bd_date", otcPaymentRequest.getI_bd_date());
            query.setParameter("i_bd_amt", otcPaymentRequest.getI_bd_amt());

            // Money order details
            query.setParameter("i_mo_rm_no", otcPaymentRequest.getI_mo_rm_no());
            query.setParameter("i_mo_payer_nm", otcPaymentRequest.getI_mo_payer_nm());
            query.setParameter("i_mo_id_no", otcPaymentRequest.getI_mo_id_no());
            query.setParameter("i_mo_contact_no", otcPaymentRequest.getI_mo_contact_no());
            query.setParameter("i_mo_amt", otcPaymentRequest.getI_mo_amt());
            query.setParameter("i_mo_date", otcPaymentRequest.getI_mo_date());

            // Audit and additional fields
            query.setParameter("i_created_by", authService.getLoginUserName());
            query.setParameter("i_modified_by", authService.getLoginUserName());
            query.setParameter("i_che_id", otcPaymentRequest.getI_che_id());

            // Execute the stored procedure
            result = (Integer) query.getSingleResult();

        }

        return result; // Return the accumulated total result
    }

    @Override
    public Integer sp_insotchistupdmtt(OTCHistReq insRequests) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_insotchistupdmtt(:i_mtt_id, :i_otc_id, :i_action, :i_dt_action, :i_otc_status, " +
                        ":i_counter_id, :i_act_by, :i_created_by, :i_modified_by)");

        // Map parameters from otcPaymentRequest to the query
        query.setParameter("i_mtt_id", insRequests.getI_mtt_id());
        query.setParameter("i_otc_id", insRequests.getI_otc_id());

        query.setParameter("i_action", insRequests.getI_action());
        query.setParameter("i_dt_action", insRequests.getI_dt_action());
        query.setParameter("i_otc_status", insRequests.getI_otc_status());
        query.setParameter("i_counter_id", insRequests.getI_counter_id());
        query.setParameter("i_act_by", insRequests.getI_act_by());

        // Audit and additional fields
        query.setParameter("i_created_by", authService.getLoginUserName());
        query.setParameter("i_modified_by", authService.getLoginUserName());

        // Execute the stored procedure
        Integer result = (Integer) query.getSingleResult();
        return result; // Return the accumulated total result
    }

    @Override
    public Integer sp_insotchist(OTCHistReq insRequests) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_insotchist(:i_otc_id, :i_action, :i_dt_action, :i_otc_status, " +
                        ":i_counter_id, :i_act_by, :i_created_by, :i_modified_by)");

        query.setParameter("i_otc_id", insRequests.getI_otc_id());

        query.setParameter("i_action", insRequests.getI_action());
        query.setParameter("i_dt_action", insRequests.getI_dt_action());
        query.setParameter("i_otc_status", insRequests.getI_otc_status());
        query.setParameter("i_counter_id", insRequests.getI_counter_id());
        query.setParameter("i_act_by", insRequests.getI_act_by());

        // Audit and additional fields
        query.setParameter("i_created_by", authService.getLoginUserName());
        query.setParameter("i_modified_by", authService.getLoginUserName());

        // Execute the stored procedure
        Integer result = (Integer) query.getSingleResult();
        return result; // Return the accumulated total result
    }

    @Override
    public List<Object[]> sp_otccrhist(OTCPaymentRequest otcHistReq) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_otccrhist(:i_mtt_id)")
                .setParameter("i_mtt_id", otcHistReq.getI_mtt_id());
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotccrpaymentdetails(OTCPaymentRequest otCollectionReceiptingRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotccrpaymentdetails(:i_mtt_id)")
                .setParameter("i_mtt_id", otCollectionReceiptingRequest.getI_mtt_id());
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotccrpaymentheader(OTCPaymentRequest otCollectionReceiptingRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotccrpaymentheader(:i_mtt_id)")
                .setParameter("i_mtt_id", otCollectionReceiptingRequest.getI_mtt_id());
        return query.getResultList();
    }

    @Override
    public Object[] sp_insotcrcpt(OTCRcptRequest insRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_insotcrcpt(:i_otc_id, :i_rcpt_no, :i_rcpt_dt, :i_rcpt_status, :i_rcpt_reprint, :i_ver_id," +
                        ":i_is_uploaded, :i_ssdocref_id, :i_created_by, :i_modified_by, :i_file_nm, :i_remark)");

        // Set parameters from OTCRcptRequest to the query
        query.setParameter("i_otc_id", insRequest.getI_otc_id());
        query.setParameter("i_rcpt_no", insRequest.getI_rcpt_no());
        query.setParameter("i_rcpt_dt", insRequest.getI_rcpt_dt());
        query.setParameter("i_rcpt_status", insRequest.getI_rcpt_status());
        query.setParameter("i_rcpt_reprint", insRequest.getI_rcpt_reprint());
        query.setParameter("i_is_uploaded", insRequest.getI_is_uploaded());
        query.setParameter("i_ver_id", insRequest.getI_ver_id());
        query.setParameter("i_ssdocref_id", insRequest.getI_ssdocref_id());
        query.setParameter("i_created_by", authService.getLoginUserName()); // Assuming authService provides logged-in
        query.setParameter("i_modified_by", authService.getLoginUserName());
        query.setParameter("i_file_nm", insRequest.getI_file_nm());
        query.setParameter("i_remark", insRequest.getI_remark());

        // Execute the stored procedure and retrieve the result
        Object[] resultSet = (Object[]) query.getSingleResult();

        return resultSet; // Return the array of results
    }

    @Override
    public Object[] sp_getotcorder(Integer i_mtt_id) {
        // Create the query to call the stored procedure
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcorder(:i_mtt_id)")
                .setParameter("i_mtt_id", i_mtt_id);
    
        // Get the single result as an Object array
        Object[] resultSet = (Object[]) query.getSingleResult();
        
        return resultSet;  // Return the array of results
    }

    @Override
    public Object[] sp_getotcorderemv(Integer i_mtt_id) {
        // Create the query to call the stored procedure
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcorderemv(:i_mtt_id)")
                .setParameter("i_mtt_id", i_mtt_id);
    
        // Get the single result as an Object array
        Object[] resultSet = (Object[]) query.getSingleResult();
        
        return resultSet;  // Return the array of results
    }

    @Override
    public Integer sp_updotcrcpt(Integer i_otc_rcpt_id, String i_ver_id, String i_ssdocref_id, String i_file_nm) {
        // TODO Auto-generated method stub
        Query query = entityManager.createNativeQuery("CALL sp_updotcrcpt(:i_otc_rcpt_id, :i_ver_id, :i_ssdocref_id, :i_file_nm)")
                .setParameter("i_otc_rcpt_id", i_otc_rcpt_id)
                .setParameter("i_ver_id", i_ver_id)
                .setParameter("i_ssdocref_id", i_ssdocref_id)
                .setParameter("i_file_nm", i_file_nm);
        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_insemvsale(OTCEMVRequest insRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_insemvsale(:i_resp_cd, :i_card_no, :i_dt_expiry, :i_status_cd, :i_approval_cd, " +
                ":i_rrn, :i_trans_trace, :i_batch_no, :i_host_no, :i_t_id, :i_mer_id, :i_aid, :i_tc, " +
                ":i_cardholder_nm, :i_card_ty, :i_prtnr_txn_id, :i_apay_txn_id, :i_cust_id, :i_amt, " +
                ":i_add_data, :i_created_by, :i_modified_by)");
    
        query.setParameter("i_resp_cd", insRequest.getI_resp_cd());
        query.setParameter("i_card_no", insRequest.getI_card_no());
        query.setParameter("i_dt_expiry", insRequest.getI_dt_expiry());
        query.setParameter("i_status_cd", insRequest.getI_status_cd());
        query.setParameter("i_approval_cd", insRequest.getI_approval_cd());
        query.setParameter("i_rrn", insRequest.getI_rrn());
        query.setParameter("i_trans_trace", insRequest.getI_trans_trace());
        query.setParameter("i_batch_no", insRequest.getI_batch_no());
        query.setParameter("i_host_no", insRequest.getI_host_no());
        query.setParameter("i_t_id", insRequest.getI_t_id());
        query.setParameter("i_mer_id", insRequest.getI_mer_id());
        query.setParameter("i_aid", insRequest.getI_aid());
        query.setParameter("i_tc", insRequest.getI_tc());
        query.setParameter("i_cardholder_nm", insRequest.getI_cardholder_nm());
        query.setParameter("i_card_ty", insRequest.getI_card_ty());
        query.setParameter("i_prtnr_txn_id", insRequest.getI_prtnr_txn_id());
        query.setParameter("i_apay_txn_id", insRequest.getI_apay_txn_id());
        query.setParameter("i_cust_id", insRequest.getI_cust_id());
        query.setParameter("i_amt", insRequest.getI_amt());
        query.setParameter("i_add_data", insRequest.getI_add_data());
        query.setParameter("i_created_by", authService.getLoginUserName());
        query.setParameter("i_modified_by", authService.getLoginUserName());
        
        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public List<Object[]> sp_getotcrcpt(OTCPaymentRequest otcRcptReq) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcrcpt(:i_mtt_id)")
                .setParameter("i_mtt_id", otcRcptReq.getI_mtt_id());
        return query.getResultList();
    }

    @Override
    public Object[] sp_getotcemvsales(OTCPaymentRequest paymentRequest) {
        // Create the query to call the stored procedure
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcemvsales(:i_mtt_id)")
                .setParameter("i_mtt_id", paymentRequest.getI_mtt_id());
    
        // Get the single result as an Object array
        if (query.getResultList().size() == 0) {
            return null;
        }
        
        Object[] resultSet = (Object[]) query.getSingleResult();
        
        return resultSet;  // Return the array of results
    }
    

}
