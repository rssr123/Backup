package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.jfree.util.Log;
import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IOTCBalancingRequestInterface;
import com.maven.rms.models.OTCBalancingRequest;

@Repository
public class OTCBalancingRequestRepository implements IOTCBalancingRequestInterface{
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Integer sp_insotcbalcash(OTCBalancingRequest requestBody) {

        Query query = entityManager.createNativeQuery(
            "CALL sp_insotcbalcash(:i_otc_id, :i_ssm4uuserrefno, :i_param_cd, :i_qty)"
        ).setParameter("i_otc_id", requestBody.getId())
        .setParameter("i_ssm4uuserrefno", requestBody.getSsm4uuserrefno())
        .setParameter("i_param_cd", requestBody.getParam_cd())
        .setParameter("i_qty", requestBody.getQuantity());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_insotcbalcheque(OTCBalancingRequest requestBody) {

        Query query = entityManager.createNativeQuery(
            "CALL sp_insotcbalcheque(:i_otc_id, :i_ssm4uuserrefno, :i_che_amt, :i_che_date, "+ 
            ":i_che_bank_nm, :i_che_payer_nm, :i_che_ba_acct_no, :i_che_id, :i_che_no)"
        ).setParameter("i_otc_id", requestBody.getId())
        .setParameter("i_ssm4uuserrefno", requestBody.getSsm4uuserrefno())
        .setParameter("i_che_amt", requestBody.getChe_amt())
        .setParameter("i_che_date", requestBody.getChe_date())
        .setParameter("i_che_bank_nm", requestBody.getChe_bank_nm())
        .setParameter("i_che_payer_nm", requestBody.getChe_payer_nm())
        .setParameter("i_che_ba_acct_no", requestBody.getChe_ba_acct_no())
        .setParameter("i_che_id", requestBody.getChe_no())
        .setParameter("i_che_no", requestBody.getChe_no());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_insotcbalbd(OTCBalancingRequest requestBody) {

        Query query = entityManager.createNativeQuery(
            "CALL sp_insotcbalbd(:i_otc_id, :i_ssm4uuserrefno, :i_bd_amt, :i_bd_date, :i_bd_no, :i_bd_bank_nm)"
        ).setParameter("i_otc_id", requestBody.getId())
        .setParameter("i_ssm4uuserrefno", requestBody.getSsm4uuserrefno())
        .setParameter("i_bd_amt", requestBody.getBd_amt())
        .setParameter("i_bd_date", requestBody.getBd_date())
        .setParameter("i_bd_no", requestBody.getBd_no())
        .setParameter("i_bd_bank_nm", requestBody.getBd_bank_nm());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_insotcbalmo(OTCBalancingRequest requestBody) {

        Query query = entityManager.createNativeQuery(
            "CALL sp_insotcbalmo(:i_otc_id, :i_ssm4uuserrefno, :i_mo_amt, :i_mo_date, :i_mo_rm_no, :i_mo_payer_nm, :i_mo_id_no, :i_mo_contact_no)"
        ).setParameter("i_otc_id", requestBody.getId())
        .setParameter("i_ssm4uuserrefno", requestBody.getSsm4uuserrefno())
        .setParameter("i_mo_amt", requestBody.getMo_amt())
        .setParameter("i_mo_date", requestBody.getMo_date())
        .setParameter("i_mo_rm_no", requestBody.getMo_rm_no())
        .setParameter("i_mo_payer_nm", requestBody.getMo_payer_nm())
        .setParameter("i_mo_id_no", requestBody.getMo_id_no())
        .setParameter("i_mo_contact_no", requestBody.getMo_contact_no());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_updotcbalpymtmode(OTCBalancingRequest requestBody) {

        Query query = entityManager.createNativeQuery(
            "CALL sp_updotcbalpymtmode(" + 
            ":i_id, :i_otc_id, :i_ssm4uuserrefno, :i_current_mode, :i_new_mode," +
            ":i_che_amt, :i_che_date, :i_che_bank_nm, :i_che_payer_nm, :i_che_ba_acct_no, :i_che_no, " +
            ":i_bd_amt, :i_bd_no, :i_bd_date, :i_bd_bank_nm, " +
            ":i_mo_amt, :i_mo_rm_no, :i_mo_date, :i_mo_payer_nm, :i_mo_id_no, :i_mo_contact_no" +
            ")"
        ).setParameter("i_id", requestBody.getId())
        .setParameter("i_otc_id", requestBody.getOtc_id())
        .setParameter("i_ssm4uuserrefno", requestBody.getSsm4uuserrefno())
        .setParameter("i_current_mode", requestBody.getDetail_type().trim().toLowerCase())
        .setParameter("i_new_mode", requestBody.getN_detail_type().trim().toLowerCase())
        .setParameter("i_che_amt", requestBody.getChe_amt())
        .setParameter("i_che_date", requestBody.getChe_date())
        .setParameter("i_che_bank_nm", requestBody.getChe_bank_nm())
        .setParameter("i_che_payer_nm", requestBody.getChe_payer_nm())
        .setParameter("i_che_ba_acct_no", requestBody.getChe_ba_acct_no())
        .setParameter("i_che_no", requestBody.getChe_no())
        .setParameter("i_bd_amt", requestBody.getBd_amt())
        .setParameter("i_bd_no", requestBody.getBd_no())
        .setParameter("i_bd_date", requestBody.getBd_date())
        .setParameter("i_bd_bank_nm", requestBody.getBd_bank_nm())
        .setParameter("i_mo_amt", requestBody.getMo_amt())
        .setParameter("i_mo_rm_no", requestBody.getMo_rm_no())
        .setParameter("i_mo_date", requestBody.getMo_date())
        .setParameter("i_mo_payer_nm", requestBody.getMo_payer_nm())
        .setParameter("i_mo_id_no", requestBody.getMo_id_no())
        .setParameter("i_mo_contact_no", requestBody.getMo_contact_no());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_updotcbalstatus(OTCBalancingRequest requestBody) {

        //Need to remove in advanced
        Log.error("CB :" + requestBody.getTotal_collection() + ";" +requestBody.getTotal_emv_amt() + ";" + requestBody.getTotal_phy_amt());

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_updotcbalstatus");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_counter_id", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_ssm4uuserrefno", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_bal_status", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_bal_type", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_total_emv_amt", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_total_phy_amt", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_total_collection", BigDecimal.class, javax.persistence.ParameterMode.IN);

        storedProcedureQuery.setParameter("i_counter_id",requestBody.getCounter_id());
        storedProcedureQuery.setParameter("i_ssm4uuserrefno",  requestBody.getSsm4uuserrefno());
        storedProcedureQuery.setParameter("i_bal_status",  requestBody.getBal_status());
        storedProcedureQuery.setParameter("i_bal_type", requestBody.getBal_type());
        storedProcedureQuery.setParameter("i_total_emv_amt", requestBody.getTotal_emv_amt());
        storedProcedureQuery.setParameter("i_total_phy_amt", requestBody.getTotal_phy_amt());
        storedProcedureQuery.setParameter("i_total_collection", requestBody.getTotal_collection());
        
        // Execute stored procedure
        storedProcedureQuery.execute();

        Integer result = 0;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }

        return result;
    }

    @Override
    public Integer sp_insotcbalmograndtotal(OTCBalancingRequest bodyRequest){
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insotcbalmograndtotal");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_branch_cd", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dt_balancing", Date.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_ssm4uuserrefno", String.class, javax.persistence.ParameterMode.IN);

        storedProcedureQuery.setParameter("i_branch_cd", bodyRequest.getBranch_code());
        storedProcedureQuery.setParameter("i_dt_balancing", bodyRequest.getBal_date());
        storedProcedureQuery.setParameter("i_ssm4uuserrefno", bodyRequest.getSsm4uuserrefno());

        // Execute stored procedure
        storedProcedureQuery.execute();

        Integer result = 0;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }

        return result;
    }

    @Override
    public Integer sp_insfmsotcbalphysum(OTCBalancingRequest requestBody) {
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insfmsotcbalphysum");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_branch_cd", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dt_balancing", Date.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_type", String.class, javax.persistence.ParameterMode.IN);

        storedProcedureQuery.setParameter("i_branch_cd", requestBody.getBranch_code());
        storedProcedureQuery.setParameter("i_dt_balancing", requestBody.getBal_date());
        storedProcedureQuery.setParameter("i_type", requestBody.getDetail_type());

        // Execute stored procedure
        storedProcedureQuery.execute();
        Integer result = 0;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }

        return result;
    }

    @Override
    public Integer sp_insfmsotcbalphyidv(OTCBalancingRequest requestBody) {
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insfmsotcbalphyidv");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_id", BigInteger.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_type", String.class, javax.persistence.ParameterMode.IN);

        storedProcedureQuery.setParameter("i_id", requestBody.getId());
        storedProcedureQuery.setParameter("i_type", requestBody.getDetail_type());

        // Execute stored procedure
        storedProcedureQuery.execute();
        Integer result = 0;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }

        return result;
    }

    @Override
    public Integer sp_insotcbalemvs(OTCBalancingRequest bodyRequest){
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insotcbalemvs");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_branch_cd", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dt_balancing", Date.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_ssm4uuserrefno", String.class, javax.persistence.ParameterMode.IN);

        storedProcedureQuery.setParameter("i_branch_cd", bodyRequest.getBranch_code());
        storedProcedureQuery.setParameter("i_dt_balancing", bodyRequest.getBal_date());
        storedProcedureQuery.setParameter("i_ssm4uuserrefno", bodyRequest.getSsm4uuserrefno());


        // Execute stored procedure
        storedProcedureQuery.execute();
        Integer result = 0;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }

        return result;
    }

}
