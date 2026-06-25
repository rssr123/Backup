package com.maven.rms.repositories;

import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Repository;
import com.maven.rms.interfaces.IOTCBalInterface;
import com.maven.rms.models.OTCBalancingDocRequest;
import com.maven.rms.models.OTCBalancingRequest;

@Repository
public class OTCBalRespository implements IOTCBalInterface{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getotcdetails(String i_branch_cd, Date i_bal_date){
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcdetails(:i_branch_code, :i_bal_date)"
                ).setParameter("i_branch_code", i_branch_cd)
                .setParameter("i_bal_date", i_bal_date);

        return query.getResultList();
    }
    
    @Override
    public List<Object[]> sp_getotcrc(String i_branch_cd, Date i_bal_date){
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcrc(:i_branch_code, :i_bal_date)"
                ).setParameter("i_branch_code", i_branch_cd)
                .setParameter("i_bal_date", i_bal_date);

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcemvcol(String i_branch_cd, Date i_bal_date, Integer i_page, Integer i_size){
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcemvcol(:i_page, :i_size, :i_branch_code, :i_bal_date)"
                ).setParameter("i_page", i_page)
                .setParameter("i_size", i_size)
                .setParameter("i_branch_code", i_branch_cd)
                .setParameter("i_bal_date", i_bal_date);

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotccashcol(String i_branch_cd, Date i_bal_date){
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotccashcol(:i_branch_code, :i_bal_date)"
                ).setParameter("i_branch_code", i_branch_cd)
                .setParameter("i_bal_date", i_bal_date);

        return query.getResultList();
    }
    
    @Override
    public List<Object[]> sp_getotcphyinfo(String i_branch_cd, Date i_bal_date){
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcphyinfo(:i_branch_code, :i_bal_date)"
                ).setParameter("i_branch_code", i_branch_cd)
                .setParameter("i_bal_date", i_bal_date);

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcbaldoclist(String i_branch_cd, Date i_bal_date){
        Query query = entityManager.createNativeQuery(
            "CALL sp_getotcbaldoclist(:i_branch_code, :i_bal_date)"
            ).setParameter("i_branch_code", i_branch_cd)
            .setParameter("i_bal_date", i_bal_date);

        return query.getResultList();
    }

    @Override
    public Blob sp_getotcbaldoc(OTCBalancingDocRequest bodyRequest){
        Query query = entityManager.createNativeQuery(
            "CALL sp_getotcbaldoc(:i_doc_id)"
            ).setParameter("i_doc_id", bodyRequest.getDocID());

        return (Blob)query.getSingleResult();    
    }

    @Override
    public BigInteger sp_insotcbaldoc(OTCBalancingDocRequest bodyRequest){

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insotcbaldoc");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_file_nm", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_content", Blob.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_type", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_size", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_category", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_ssm4urefno", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_branch_cd", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dt_balancing", Date.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dt_settlement", Date.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_terminal_id", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_batch_no", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_batch_count", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_batch_amt", String.class, javax.persistence.ParameterMode.IN);

        // Set parameters
        storedProcedureQuery.setParameter("i_file_nm", bodyRequest.getFileNm());
        storedProcedureQuery.setParameter("i_file_content", bodyRequest.getI_file_content());
        storedProcedureQuery.setParameter("i_file_type", bodyRequest.getFileType());
        storedProcedureQuery.setParameter("i_file_size", bodyRequest.getFileSize());
        storedProcedureQuery.setParameter("i_file_category", bodyRequest.getFileCategory());
        storedProcedureQuery.setParameter("i_ssm4urefno", bodyRequest.getSsm4uuserrefno());
        storedProcedureQuery.setParameter("i_branch_cd", bodyRequest.getBranch_code());
        storedProcedureQuery.setParameter("i_dt_balancing", bodyRequest.getI_bal_date());
        storedProcedureQuery.setParameter("i_dt_settlement", bodyRequest.getI_dtSettlement());
        storedProcedureQuery.setParameter("i_terminal_id", bodyRequest.getTerminalId());
        storedProcedureQuery.setParameter("i_batch_no", bodyRequest.getBatchNo());
        storedProcedureQuery.setParameter("i_batch_count", bodyRequest.getBatchCount());
        storedProcedureQuery.setParameter("i_batch_amt", bodyRequest.getBatchAmt());

        // Execute stored procedure
        storedProcedureQuery.execute();

        BigInteger result = null;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (BigInteger) storedProcedureQuery.getSingleResult();
        }

        return result;
    }

        @Override
    public BigInteger sp_insotcdbalcashbytotal(OTCBalancingRequest bodyRequest){

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insotcdbalcashbytotal");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_ssm4uuserrefno", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_branch_cd", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dt_balancing", Date.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_param_cd", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_qty", Integer.class, javax.persistence.ParameterMode.IN);

        storedProcedureQuery.setParameter("i_ssm4uuserrefno", bodyRequest.getSsm4uuserrefno());
        storedProcedureQuery.setParameter("i_branch_cd", bodyRequest.getBranch_code());
        storedProcedureQuery.setParameter("i_dt_balancing", bodyRequest.getBal_date());
        storedProcedureQuery.setParameter("i_param_cd", bodyRequest.getParam_cd());
        storedProcedureQuery.setParameter("i_qty", bodyRequest.getQuantity());

        // Execute stored procedure
        storedProcedureQuery.execute();

        BigInteger result = null;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (BigInteger) storedProcedureQuery.getSingleResult();
        }

        return result;
    }

    @Override
    public BigInteger sp_insotcbalcashbytotal(OTCBalancingRequest bodyRequest){

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insotcbalcashbytotal");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_ssm4uuserrefno", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_branch_cd", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dt_balancing", Date.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_param_cd", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_qty", Integer.class, javax.persistence.ParameterMode.IN);

        storedProcedureQuery.setParameter("i_ssm4uuserrefno", bodyRequest.getSsm4uuserrefno());
        storedProcedureQuery.setParameter("i_branch_cd", bodyRequest.getBranch_code());
        storedProcedureQuery.setParameter("i_dt_balancing", bodyRequest.getBal_date());
        storedProcedureQuery.setParameter("i_param_cd", bodyRequest.getParam_cd());
        storedProcedureQuery.setParameter("i_qty", bodyRequest.getQuantity());

        // Execute stored procedure
        storedProcedureQuery.execute();

        BigInteger result = null;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (BigInteger) storedProcedureQuery.getSingleResult();
        }

        return result;
    }

    @Override
    public Integer sp_updotcbalcashbytotal(OTCBalancingRequest bodyRequest){

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_updotcbalcashbytotal");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_ssm4uuserrefno", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_branch_cd", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dt_balancing", Date.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_param_cd", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_qty", Integer.class, javax.persistence.ParameterMode.IN);

        storedProcedureQuery.setParameter("i_ssm4uuserrefno", bodyRequest.getSsm4uuserrefno());
        storedProcedureQuery.setParameter("i_branch_cd", bodyRequest.getBranch_code());
        storedProcedureQuery.setParameter("i_dt_balancing", bodyRequest.getBal_date());
        storedProcedureQuery.setParameter("i_param_cd", bodyRequest.getParam_cd());
        storedProcedureQuery.setParameter("i_qty", bodyRequest.getQuantity());

        // Execute stored procedure
        storedProcedureQuery.execute();

        Integer result = 0;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }

        return result;
    }

    @Override
    public Integer sp_insotccashgrandtotal(OTCBalancingRequest bodyRequest){
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insotccashgrandtotal");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_ssm4uuserrefno", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_branch_cd", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dt_balancing", Date.class, javax.persistence.ParameterMode.IN);

        storedProcedureQuery.setParameter("i_ssm4uuserrefno", bodyRequest.getSsm4uuserrefno());
        storedProcedureQuery.setParameter("i_branch_cd", bodyRequest.getBranch_code());
        storedProcedureQuery.setParameter("i_dt_balancing", bodyRequest.getBal_date());

        // Execute stored procedure
        storedProcedureQuery.execute();

        Integer result = 0;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }

        return result;
    }

    @Override
    public Integer sp_updotcdailybalstatus(OTCBalancingRequest bodyRequest)
    {
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_updotcdailybalstatus");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_branch_code", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_bal_date", Date.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_bal_status", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_bal_type", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_ssm4uuserrefno", String.class, javax.persistence.ParameterMode.IN);

        storedProcedureQuery.setParameter("i_branch_code", bodyRequest.getBranch_code());
        storedProcedureQuery.setParameter("i_bal_date", bodyRequest.getBal_date());
        storedProcedureQuery.setParameter("i_bal_status", bodyRequest.getBal_status());
        storedProcedureQuery.setParameter("i_bal_type", bodyRequest.getBal_type());
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
