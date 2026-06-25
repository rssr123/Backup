package com.maven.rms.repositories;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import javax.transaction.Transactional;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IIdamanApiTokenInterface;
import com.maven.rms.models.IdamanAPITokenReq;

@Repository
public class IdamanApiTokenRepository implements IIdamanApiTokenInterface{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Integer updidamantoken(IdamanAPITokenReq bodyReq){
        Integer result = 0;
        
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_updidamantoken");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_token", String.class, javax.persistence.ParameterMode.IN);

        storedProcedureQuery.setParameter("i_token", bodyReq.getToken());

        // Execute stored procedure
        storedProcedureQuery.execute();

        result = (Integer) storedProcedureQuery.getSingleResult();

        return result;
    }

    @Override
    @Transactional
    public String getidamantoken(){
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_getidamantoken");

        // Execute stored procedure
        storedProcedureQuery.execute();
        String result = "";
        
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (String) storedProcedureQuery.getSingleResult();
        }

        return result;
    }
}
