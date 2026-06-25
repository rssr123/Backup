package com.maven.rms.repositories;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IFMSRIPLInterface;
import com.maven.rms.models.FMSRILTJN;
import com.maven.rms.models.FMSRIPL;

@Repository
public class FMSRIPLRepository implements IFMSRIPLInterface{
     @PersistenceContext
     private EntityManager entityManager;

    @Override//Object[]
     public List<Object[]> sp_getfmsripla() {
 
          Query query = entityManager.createNativeQuery(
            "CALL sp_getfmsripla()"
            );
  
          return query.getResultList();
     }

     // @Override
     // public BigInteger sp_insfmsripla(BigInteger i_audit_id, BigInteger i_ripl_id, Date i_dt_txn, BigDecimal i_accr_amt_af, String i_action_type) 
     // {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_insfmsripla(:i_audit_id, :i_ripl_id, :i_dt_txn, :i_accr_amt_af, :i_action_type)"
     //                )
     //                .setParameter("i_audit_id", i_audit_id)
     //                .setParameter("i_ripl_id", i_ripl_id)
     //                .setParameter("i_dt_txn", i_dt_txn)
     //                .setParameter("i_accr_amt_af", i_accr_amt_af)
     //                .setParameter("i_action_type", i_action_type);

     //      BigInteger result = (BigInteger) query.getSingleResult();
     //      return result;
     // }

     @Override
     public BigInteger sp_insfmsripla(FMSRIPL fmsripl) 
     {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_insfmsripla(:i_audit_id, :i_ripl_id, :i_dt_txn, :i_accr_amt_af, :i_action_type)"
                    )
                    .setParameter("i_audit_id", fmsripl.getAudit_id())
                    .setParameter("i_ripl_id", fmsripl.getRipl_id())
                    .setParameter("i_dt_txn", fmsripl.getDt_txn())
                    .setParameter("i_accr_amt_af", fmsripl.getAccr_amt_af())
                    .setParameter("i_action_type", fmsripl.getAction_type());

          BigInteger result = (BigInteger) query.getSingleResult();
          return result;
     }

     @Override
     public List<Object[]> sp_getfmsjnrilt() {
 
          Query query = entityManager.createNativeQuery(
            "CALL sp_getfmsjnrilt()"
            );
  
          return query.getResultList();
     }

     @Override
     public Integer sp_insfmsjnrilt(FMSRILTJN request) {
          Integer result;

          Query query = entityManager.createNativeQuery(
                    "CALL sp_insfmsjnrilt(:i_rilt_a_id, :i_rilt_id, :i_lit_amt_bf, :i_lit_amt_af, :i_dt_txn, :i_status)")
                    .setParameter("i_rilt_a_id", request.getI_rilt_a_id())
                    .setParameter("i_rilt_id", request.getI_rilt_id())
                    .setParameter("i_lit_amt_bf", request.getI_lit_amt_bf())
                    .setParameter("i_lit_amt_af", request.getI_lit_amt_af())
                    .setParameter("i_dt_txn", request.getI_dt_txn())
                    .setParameter("i_status", request.getI_status());
          result = (Integer) query.getSingleResult();
          return result;
     }
}

