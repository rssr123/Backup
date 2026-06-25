package com.maven.rms.repositories;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IFMSRICPInterface;
import com.maven.rms.models.FMSRICP;

@Repository
public class FMSRICPRepository implements IFMSRICPInterface{
    @PersistenceContext
     private EntityManager entityManager;

      @Override//Object[]
     public List<Object[]> sp_getfmsricpa() {
 
          Query query = entityManager.createNativeQuery(
            "CALL sp_getfmsricpa()"
            );
  
          return query.getResultList();
     }

     @Override
     public BigInteger sp_insfmsricpa(FMSRICP fmsricp) 
     {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_insfmsricpa(:i_audit_id, :i_ricp_id, :i_dt_txn, :i_accr_amt_af, :i_action_type)"
                    )
                    .setParameter("i_audit_id", fmsricp.getAudit_id())
                    .setParameter("i_ricp_id", fmsricp.getRicp_id())
                    .setParameter("i_dt_txn", fmsricp.getDt_txn())
                    .setParameter("i_accr_amt_af", fmsricp.getAccr_amt_af())
                    .setParameter("i_action_type", fmsricp.getAction_type());

          BigInteger result = (BigInteger) query.getSingleResult();
          return result;
     }
}
