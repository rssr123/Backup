package com.maven.rms.repositories;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import java.util.Date;
import java.util.List;


import com.maven.rms.interfaces.IUnmatchedTransInterface;
import com.maven.rms.models.UnmatchTransRequest;

@Repository
public class IUnmatchTransRepository implements IUnmatchedTransInterface{

    @PersistenceContext
     private EntityManager entityManager;

     @Override
     public List<Object[]> sp_getutlmonths(UnmatchTransRequest unmatchedTransRequest) {

          Query query = entityManager.createNativeQuery(
                    "CALL sp_getutlmonths(:i_period_key)")
                    .setParameter("i_period_key", unmatchedTransRequest.getI_period_key());
                    
                    return query.getResultList();       
     }

     @Override
     public List<Object[]> sp_getutldays(UnmatchTransRequest unmatchedTransRequest) {

          Query query = entityManager.createNativeQuery(
                    "CALL sp_getutldays(:i_period_key)")
                    .setParameter("i_period_key", unmatchedTransRequest.getI_period_key());
                    
                    return query.getResultList();       
     }
    
}
