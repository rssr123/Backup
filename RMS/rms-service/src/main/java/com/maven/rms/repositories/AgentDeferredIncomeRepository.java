package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.joda.time.DateTime;
import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IAgentDeferredIncomeInterface;
import com.maven.rms.models.AgentDeferredIncome;
import com.maven.rms.models.AgentDetailDeferredIncome;
@Repository
public class AgentDeferredIncomeRepository implements IAgentDeferredIncomeInterface{
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Integer sp_inspaymentmtt(AgentDeferredIncome item){

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insagentmtt");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_ss_cd", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_orn_no", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_orn_dt", Date.class, javax.persistence.ParameterMode.IN);
        
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_nm", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_addr_1", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_addr_2", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_addr_3", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_postcode", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_city", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_state", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_email", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_phone", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_total_amt", BigDecimal.class, javax.persistence.ParameterMode.IN);
        
        storedProcedureQuery.registerStoredProcedureParameter("i_username_c", String.class, javax.persistence.ParameterMode.IN);

        //Set parameter
        storedProcedureQuery.setParameter("i_ss_cd", item.getSs_cd());
        storedProcedureQuery.setParameter("i_orn_no", item.getOrn_no());
        storedProcedureQuery.setParameter("i_orn_dt", item.getOrn_dt());
        
        storedProcedureQuery.setParameter("i_cust_nm", item.getCust_nm());
        storedProcedureQuery.setParameter("i_cust_addr_1", item.getCust_addr_1());
        storedProcedureQuery.setParameter("i_cust_addr_2", item.getCust_addr_2());
        storedProcedureQuery.setParameter("i_cust_addr_3", item.getCust_addr_3());
        storedProcedureQuery.setParameter("i_cust_postcode", item.getCust_postcode());
        storedProcedureQuery.setParameter("i_cust_city", item.getCust_city());
        storedProcedureQuery.setParameter("i_cust_state", item.getCust_state());
        storedProcedureQuery.setParameter("i_cust_email", item.getCust_email());
        storedProcedureQuery.setParameter("i_cust_phone", item.getCust_phone());
        storedProcedureQuery.setParameter("i_total_amt", item.getTotal_amt());
        
        storedProcedureQuery.setParameter("i_username_c", "system");

        // Execute stored procedure
        storedProcedureQuery.execute();

        Integer result = null;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }

        return result;
    }

    @Override
    public Integer sp_inspaymentmttitem(AgentDetailDeferredIncome detailItem, Integer mtt_id){

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insagentmttitem");
       
        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_mtt_id", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_fee_detail_id", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_item_ref_no", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_ext_rcpt_no", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_item_desc", String.class, javax.persistence.ParameterMode.IN);
        
        storedProcedureQuery.registerStoredProcedureParameter("i_line_no", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_qty", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_unit_fee", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_gross_amt", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_grant_cd", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_disc_amt", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_tax_pct", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_tax_amt", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_net_amt", BigDecimal.class, javax.persistence.ParameterMode.IN);
        
        storedProcedureQuery.registerStoredProcedureParameter("i_entity_type", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_entity_no", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_entity_nm", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dps_id", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dps_task", String.class, javax.persistence.ParameterMode.IN);

        storedProcedureQuery.setParameter("i_mtt_id", mtt_id);
        storedProcedureQuery.setParameter("i_fee_detail_id", detailItem.getFee_detail_id());
        storedProcedureQuery.setParameter("i_item_ref_no", detailItem.getItem_ref_no());
        storedProcedureQuery.setParameter("i_ext_rcpt_no", detailItem.getExt_rcpt_no());
        storedProcedureQuery.setParameter("i_item_desc", detailItem.getItem_desc());
        
        storedProcedureQuery.setParameter("i_line_no", detailItem.getLine_no());
        storedProcedureQuery.setParameter("i_qty", detailItem.getQty());
        storedProcedureQuery.setParameter("i_unit_fee", detailItem.getUnit_fee());
        storedProcedureQuery.setParameter("i_gross_amt", detailItem.getGross_amt());
        storedProcedureQuery.setParameter("i_grant_cd", detailItem.getGrant_cd());
        storedProcedureQuery.setParameter("i_disc_amt", detailItem.getDisc_amt());
        storedProcedureQuery.setParameter("i_tax_pct", detailItem.getTax_pct());
        storedProcedureQuery.setParameter("i_tax_amt", detailItem.getTax_amt());
        storedProcedureQuery.setParameter("i_net_amt", detailItem.getNet_amt());
        
        storedProcedureQuery.setParameter("i_entity_type", detailItem.getEntity_type());
        storedProcedureQuery.setParameter("i_entity_no", detailItem.getEntity_no());
        storedProcedureQuery.setParameter("i_entity_nm", detailItem.getEntity_nm());
        storedProcedureQuery.setParameter("i_dps_id", detailItem.getDps_id());
        storedProcedureQuery.setParameter("i_dps_task", detailItem.getDps_task());
        
        // Execute stored procedure
        storedProcedureQuery.execute();

        Integer result = null;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }

        return result;
    }

    @Override
    public List<Object[]> sp_getmft(String fee_detail_id) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getmft(:i_page, :i_size, :i_fee_detail_pk, :i_fee_detail_id,  :i_unit_fee_fr, :i_unit_fee_to, :i_ss_cd, :i_tax_cd,  :i_dt_modified_fr,  :i_dt_modified_to ,  :i_modified_by, :i_status)")
                .setParameter("i_page", 1)
                .setParameter("i_size", 10)
                .setParameter("i_fee_detail_pk", null)
                .setParameter("i_fee_detail_id", fee_detail_id)
                .setParameter("i_unit_fee_fr", null)
                .setParameter("i_unit_fee_to", null)
                .setParameter("i_ss_cd", null)
                .setParameter("i_tax_cd", null)
                .setParameter("i_dt_modified_fr", null)
                .setParameter("i_dt_modified_to", null)
                .setParameter("i_modified_by", null)
                .setParameter("i_status", "A")
                .setParameter("i_dt_modified_fr", null)
                .setParameter("i_dt_modified_to", null);

        return query.getResultList();
    }

    @Override
    public BigInteger sp_insagentdi(AgentDetailDeferredIncome recognitionRequest) 
    {
        Date today = new Date(System.currentTimeMillis());
        // Add 1 year to the date using Calendar
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.YEAR, 1);
        Date nextYear = new Date(calendar.getTimeInMillis());

        Query query = entityManager.createNativeQuery(
                "CALL sp_insagentdi(:i_fee_detail_id, :i_txn_type, :i_entity_no, :i_entity_type, :i_dt_effective,:i_dt_expiry, :i_item_ref_no,:i_approval_status, :i_dt_approval)"
                )
                .setParameter("i_fee_detail_id", recognitionRequest.getFee_detail_id())
                .setParameter("i_txn_type", "ROC-RPC")
                .setParameter("i_entity_no", recognitionRequest.getEntity_no())
                .setParameter("i_entity_type", recognitionRequest.getEntity_type())
                .setParameter("i_dt_effective", today)
                .setParameter("i_dt_expiry", nextYear)
                .setParameter("i_item_ref_no", recognitionRequest.getItem_ref_no())
                .setParameter("i_approval_status", "A")
                .setParameter("i_dt_approval", today);

        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
    }

}
