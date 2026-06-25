package com.maven.rms.repositories;

import java.math.BigInteger;
import java.sql.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IOTCBankInSlipInterface;

@Repository
public class OTCBankInSlipRepository implements IOTCBankInSlipInterface{
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getotcbisinfo(String i_branch_cd, Date i_bal_date){
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcbisinfo(:i_branch_code, :i_bal_date)"
                ).setParameter("i_branch_code", i_branch_cd)
                .setParameter("i_bal_date", i_bal_date);

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcbiscash(String i_branch_cd, Date i_bal_date){
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcbiscash(:i_branch_code, :i_bal_date)"
                ).setParameter("i_branch_code", i_branch_cd)
                .setParameter("i_bal_date", i_bal_date);

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcbisphy(String i_branch_cd, Date i_bal_date){
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcbisphy(:i_branch_code, :i_bal_date)"
                ).setParameter("i_branch_code", i_branch_cd)
                .setParameter("i_bal_date", i_bal_date);

        return query.getResultList();
    }

    @Override
    public BigInteger sp_insbankinslip(String i_branch_cd, Date i_bal_date, String i_ssm4uuserrefno){

        Query query = entityManager.createNativeQuery(
            "CALL sp_insbankinslip(:i_branch_code, :i_dt_balancing, :i_ssm4uuserrefno)"
            ).setParameter("i_branch_code", i_branch_cd)
            .setParameter("i_dt_balancing", i_bal_date)
            .setParameter("i_ssm4uuserrefno", i_ssm4uuserrefno);

        return (BigInteger)query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_getotcfmsari(String i_otc_type, Date i_dt_balancing){

        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcfmsari(:i_otc_type, :i_dt_balancing)"
                ).setParameter("i_otc_type", i_otc_type)
                .setParameter("i_dt_balancing", i_dt_balancing);
                
        return query.getResultList();
    }
}
