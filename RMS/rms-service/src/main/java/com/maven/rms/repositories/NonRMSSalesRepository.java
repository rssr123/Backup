package com.maven.rms.repositories;

import java.sql.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.INonRMSSalesInterface;
import com.maven.rms.models.NonRMSSales;

@Repository
public class NonRMSSalesRepository implements INonRMSSalesInterface{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Integer sp_insnonrmssales(NonRMSSales bodyRequest){

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insnonrmssales");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_ss_cd", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cn_cust_id", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dn_cust_id", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cash_acct", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_merchant_id", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_stmt_no", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_fms_ari_ref_no", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_ari_total_amt", java.math.BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_mdr_total_amt", java.math.BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_total_net_amt", java.math.BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dt_settlement", Date.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_total_trx_no", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_batch_size", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_batch_cnt", Integer.class, javax.persistence.ParameterMode.IN);
        

        // Set parameters
        storedProcedureQuery.setParameter("i_ss_cd", bodyRequest.getSs_cd());
        storedProcedureQuery.setParameter("i_cn_cust_id", bodyRequest.getCn_cust_id());
        storedProcedureQuery.setParameter("i_dn_cust_id", bodyRequest.getDn_cust_id());
        storedProcedureQuery.setParameter("i_cash_acct", bodyRequest.getCash_acct());
        storedProcedureQuery.setParameter("i_merchant_id", bodyRequest.getMerchant_id());
        storedProcedureQuery.setParameter("i_stmt_no", bodyRequest.getStmt_no());
        storedProcedureQuery.setParameter("i_fms_ari_ref_no", bodyRequest.getFms_ari_ref_no());
        storedProcedureQuery.setParameter("i_ari_total_amt",    bodyRequest.getAri_total_amt());
        storedProcedureQuery.setParameter("i_mdr_total_amt", bodyRequest.getMdr_total_amt());
        storedProcedureQuery.setParameter("i_total_net_amt", bodyRequest.getTotal_net_amt());
        storedProcedureQuery.setParameter("i_dt_settlement", bodyRequest.getDt_settlement());
        storedProcedureQuery.setParameter("i_total_trx_no", bodyRequest.getTotal_trx_no());
        storedProcedureQuery.setParameter("i_batch_size", bodyRequest.getBatch_size());
        storedProcedureQuery.setParameter("i_batch_cnt", bodyRequest.getBatch_cnt());
        

        // Execute stored procedure
        storedProcedureQuery.execute();

        Integer result = null;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }

        return result;
    }
}
