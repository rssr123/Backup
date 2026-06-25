package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IMTTInterface;
import com.maven.rms.models.GHLPaymentResponse;
import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OnlinePayment;

@Repository
public class MTTRepository implements IMTTInterface {

    @PersistenceContext
    private EntityManager entityManager;
	
	@Value("${rms.application.emailExpiryInDay}")
	private Integer emailExpiryDay;

    @Override
    public List<Object[]> sp_getMTTItem(Integer mttId) {
        Query query = entityManager.createNativeQuery("CALL sp_getMTTItem(:i_mtt_id)")
                .setParameter("i_mtt_id", mttId);
        return query.getResultList();
    }

    @Override
    public Integer sp_updateMTT(String ornNo, String custNm, String custAddr1, String custAddr2, String custAddr3,
            String custPostCode, String custCity, String custState) {
        Query query = entityManager.createNativeQuery("CALL sp_updateMTT(:i_orn_no,:i_cust_nm,:i_cust_addr_1,:i_cust_addr_2,:i_cust_addr_3,:i_cust_postcode,:i_cust_city,:i_cust_state)")
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
    
    public Integer sp_updatemttordstatus(String ornNo, String orderStatus, String username) {
        Query query = entityManager.createNativeQuery("CALL sp_updatemttordstatus(:i_orn_no,:i_order_status,:i_user)")
                .setParameter("i_orn_no", ornNo)
                .setParameter("i_order_status", orderStatus)
                .setParameter("i_user", username);
        // Use getSingleResult to retrieve the single integer value
        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public BigInteger sp_updateMTTStatus(String ornNo, String created_by, String modified_by) {
        Query query = entityManager.createNativeQuery("CALL sp_updMTTStatus(:i_orn_no, :i_created_by, :i_modified_by)")
                .setParameter("i_orn_no", ornNo)
                .setParameter("i_created_by", created_by)
                .setParameter("i_modified_by", modified_by);
        // Use getSingleResult to retrieve the single integer value
        BigInteger result = (BigInteger) query.getSingleResult();
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
    public Integer sp_checkLatestOrderStatus2(String ornNo, BigDecimal totalAmt) {
        // TODO Auto-generated method stub
        Query query = entityManager.createNativeQuery("CALL sp_checkLatestOrderStatus_v3(:i_orn_no,:i_total_amt)")
                .setParameter("i_orn_no", ornNo)
                .setParameter("i_total_amt", totalAmt);
                // Query query = entityManager.createNativeQuery("CALL sp_checkLatestOrderStatus_v2(:i_orn_no)")
                // .setParameter("i_orn_no", ornNo);
        // Use getSingleResult to retrieve the single integer value
        Integer result = (Integer)query.getSingleResult();
        return result;
    }

    @Override
    public Object[] sp_insertPayment(Integer mttID, String pymtMethod, String serviceID, BigDecimal pymtAmt,
            String langCd, String usernameC, String usernameM) {
        // TODO Auto-generated method stub
        Query query = entityManager.createNativeQuery("CALL sp_inspayment(:i_mtt_id,:i_pg_pymt_method,:i_pg_service_id,:i_pg_pymt_amt,:i_pg_lang_cd,:i_username_c,:i_username_m)")
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
        Query query = entityManager.createNativeQuery("CALL sp_updmttpg(:i_pymt_id ,:i_txn_type ,:i_pymt_method ,:i_service_id ,:i_pymt_amt ,:i_curr_cd,:i_txn_id,:i_issuing_bank,:i_auth_cd,:i_txn_status,:i_txn_msg,:i_hash_value,:i_hash_value2,:i_bank_refno,:i_token_type,:i_token,:i_resp_time,:i_cardno_mask,:i_cardholder,:i_cardtype,:i_cardexp,:i_modified_by)")
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
        Query query = entityManager.createNativeQuery("CALL sp_checkpymtrcpt(:i_orn_no)")
                .setParameter("i_orn_no", ornNo);
        return (Integer) query.getSingleResult();
    }

    @Override
    public Object[] sp_insertReceipt(String paymentId, String username) {
        // TODO Auto-generated method stub
        UUID uuid = UUID.randomUUID();
        String guid = "RMS-" + uuid.toString();
        
        Query query = entityManager.createNativeQuery("CALL sp_insmttrcpt(:i_pg_pymt_id, :i_guid, :i_created_by, :i_modified_by)")
                .setParameter("i_pg_pymt_id", paymentId)
                .setParameter("i_guid", guid)
                .setParameter("i_created_by", username)
                .setParameter("i_modified_by", username);
        Object[] result = (Object[]) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_updateMTTRcpt(Integer mttRcptID, String verID, String ssDocRefID) {
        // TODO Auto-generated method stub
        Query query = entityManager.createNativeQuery("CALL sp_updmttrcpt(:i_mtt_rcpt_id, :i_ver_id, :i_ssdocref_id)")
                .setParameter("i_mtt_rcpt_id", mttRcptID)
                .setParameter("i_ver_id", verID)
                .setParameter("i_ssdocref_id", ssDocRefID);
        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_checkornno(String ornno) {
        // TODO Auto-generated method stub
        Query query = entityManager.createNativeQuery("CALL sp_checkornno(:i_orn_no)")
                .setParameter("i_orn_no", ornno);
        return (Integer) query.getSingleResult();
    }
 
    @Override
    public Integer sp_checktxn(String ornno, String pymt_id) {
        // TODO Auto-generated method stub
        Query query = entityManager.createNativeQuery("CALL sp_checktxn(:i_orn_no, :i_pymt_id)")
                .setParameter("i_orn_no", ornno)
                .setParameter("i_pymt_id", pymt_id);
        return (Integer) query.getSingleResult();
    }
 
    @Override
    public String sp_checktxnid(String ornno, String pymt_id) {
        // TODO Auto-generated method stub
        Query query = entityManager.createNativeQuery("CALL sp_checktxnid(:i_orn_no, :i_pymt_id)")
                .setParameter("i_orn_no", ornno)
                .setParameter("i_pymt_id", pymt_id);
        return (String) query.getSingleResult();
    }
    
    //scheduler
    @Override
    public List<OnlinePayment> findByOrderStatusRC(String orderStatus) {
        // Assuming there is a table or stored procedure that retrieves data by order status
        Query query = entityManager.createNativeQuery("SELECT * FROM rms_mtt WHERE order_status = :orderStatus AND orn_no IN (SELECT non_bil_no FROM rms_non_bil) AND (orn_no LIKE '%BIL%' OR orn_no LIKE '%NB%')order by dt_created desc" ,
         OnlinePayment.class)
            .setParameter("orderStatus", orderStatus);
        return query.getResultList();
    }

    @Override
    public List<OnlinePayment> findByOrderStatusPP(String orderStatus) {
        // Assuming there is a table or stored procedure that retrieves data by order status
        Query query = entityManager.createNativeQuery(
            "SELECT * FROM rms_mtt WHERE order_status = :orderStatus AND (orn_no not LIKE '%BIL%' OR orn_no LIKE '%NB%') and orn_no is not null AND orn_no != '' and orn_no not like '%AGB%' and orn_no not like '%NB%' and email_flag = 1;"  , 
            OnlinePayment.class)
            .setParameter("orderStatus", orderStatus);
        return query.getResultList();
    }

    @Override
    public List<OnlinePayment> findByOrderStatusSP(String orderStatus) {
        // Assuming there is a table or stored procedure that retrieves data by order status
        Query query = entityManager.createNativeQuery(
            "SELECT * FROM rms_mtt WHERE order_status = :orderStatus AND orn_no  like '%AGB%'"  , 
            OnlinePayment.class)
            .setParameter("orderStatus", orderStatus);
        return query.getResultList();
    }

    @Override
    public List<OnlinePayment> findByOrderStatus(String orderStatus) {
        // Assuming there is a table or stored procedure that retrieves data by order status
        Query query = entityManager.createNativeQuery("SELECT * FROM rms_mtt WHERE order_status = :orderStatus" ,
         OnlinePayment.class)
            .setParameter("orderStatus", orderStatus);
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getemailpp(Integer mttID) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getemailpp(:i_mtt_id)")
                .setParameter("i_mtt_id", mttID);
                
        return query.getResultList();
    }

    @Transactional
    @Override
    public OnlinePayment save(OnlinePayment onlinePayment) {
        if (onlinePayment.getMttId() == null) {
            entityManager.persist(onlinePayment);
            return onlinePayment;
        } else {
            return entityManager.merge(onlinePayment);
        }
    }

    //scheduler
    @Override
    public List<MTTRCPT> findUnuploadedReceipts() {
        Query query = entityManager.createNativeQuery(
            "SELECT t1.* FROM rms_mtt_rcpt t1 WHERE (t1.is_uploaded = 0 OR t1.ver_id IS NULL) AND t1.dt_created < (CURRENT - INTERVAL(11) MINUTE TO MINUTE) " +
            "AND NOT EXISTS ( " +
            "SELECT 1 FROM rms_ext_audit t2 WHERE t2.request_body LIKE '%' || t1.rcpt_no || '%' AND t2.response_body LIKE '%Successful%'  AND t2.dt_created BETWEEN (CURRENT - INTERVAL(3) MINUTE TO MINUTE) AND CURRENT "
            + ");", MTTRCPT.class
        );
        return query.getResultList();
    }

    // @Override
    // public List<MTTRCPT> findMttReceipts(BigInteger i_mtt_id) {
    //     Query query = entityManager.createNativeQuery(
    //         "SELECT * FROM rms_mtt_rcpt WHERE mtt_id=:i_mtt_id", MTTRCPT.class
    //     );
    //     query.setParameter("i_mtt_id", i_mtt_id);
    //     return query.getResultList();
    // }

    //scheduler
    @Transactional
    public void markAsUploaded(Integer id) {
        entityManager.createNativeQuery(
            "UPDATE rms_mtt_rcpt SET is_uploaded = 1 WHERE mtt_rcpt_id = :id"
        ).setParameter("id", id)
         .executeUpdate();
    }

    @Transactional
    public void markAsUploadedOTC(Integer id) {
        entityManager.createNativeQuery(
            "UPDATE rms_otc_rcpt SET is_uploaded = 1 WHERE otc_id = :id"
        ).setParameter("id", id)
         .executeUpdate();
    }
    
    public Integer sp_refreshMTTEmailExpDt(String ornNo, String username) {
        Query query = entityManager.createNativeQuery("CALL sp_refreshMTTEmailExpDt(:i_orn_no, :i_expiry, :i_user)")
                .setParameter("i_orn_no", ornNo)
                .setParameter("i_expiry", LocalDateTime.now().plusDays(emailExpiryDay.longValue()))
                .setParameter("i_user", username);
        return (Integer) query.getSingleResult();
    }

    @Override
    public Object[] sp_getmttrcptinfo_v2(String orn_no) {
        Query query = entityManager.createNativeQuery("CALL sp_getmttrcptinfo_v2(:i_orn_no)")
                    .setParameter("i_orn_no", orn_no);
        Object[] result = (Object[]) query.getSingleResult();
        return result;
    }
    
}
