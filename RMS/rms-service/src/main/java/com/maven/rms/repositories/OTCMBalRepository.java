package com.maven.rms.repositories;

import java.sql.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IOTCMBalInterface;

@Repository
public class OTCMBalRepository implements IOTCMBalInterface{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getotcmdetails(String i_branch_cd, Date i_bal_date){
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcmdetails(:i_branch_code, :i_bal_date)"
                ).setParameter("i_branch_code", i_branch_cd)
                .setParameter("i_bal_date", i_bal_date);

        return query.getResultList();
    }
    
    @Override
    public List<Object[]> sp_getotcmrc(String i_branch_cd, Date i_bal_date){
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcmrc(:i_branch_code, :i_bal_date)"
                ).setParameter("i_branch_code", i_branch_cd)
                .setParameter("i_bal_date", i_bal_date);

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcmemvcol(String i_branch_cd, Date i_bal_date, Integer i_page, Integer i_size){
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcmemvcol(:i_page, :i_size, :i_branch_code, :i_bal_date)"
                ).setParameter("i_page", i_page)
                .setParameter("i_size", i_size)
                .setParameter("i_branch_code", i_branch_cd)
                .setParameter("i_bal_date", i_bal_date);

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcmcashcol(String i_branch_cd, Date i_bal_date){
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcmcashcol(:i_branch_code, :i_bal_date)"
                ).setParameter("i_branch_code", i_branch_cd)
                .setParameter("i_bal_date", i_bal_date);

        return query.getResultList();
    }
    
    @Override
    public List<Object[]> sp_getotcmphyinfo(String i_branch_cd, Date i_bal_date){
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcphyinfo(:i_branch_code, :i_bal_date)"
                ).setParameter("i_branch_code", i_branch_cd)
                .setParameter("i_bal_date", i_bal_date);

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcmbaldoclist(String i_branch_cd, Date i_bal_date){
        Query query = entityManager.createNativeQuery(
            "CALL sp_getotcmbaldoclist(:i_branch_code, :i_bal_date)"
            ).setParameter("i_branch_code", i_branch_cd)
            .setParameter("i_bal_date", i_bal_date);

        return query.getResultList();
    }

}
