package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IFMSDeferredIncomeInterface;
import com.maven.rms.models.FMSDeferredIncome;

@Repository
public class FMSDeferredIncomeRepository implements IFMSDeferredIncomeInterface{
     @PersistenceContext
     private EntityManager entityManager;

     @Override//Object[]
     public List<Object[]> sp_getfmsdia() {
 
          Query query = entityManager.createNativeQuery(
            "CALL sp_getfmsdia()"
            );
  
          return query.getResultList();
     }

     // @Override
     // public BigInteger sp_insfmsdia(BigInteger i_audit_id, BigInteger i_di_id, Date i_dt_txn, BigDecimal i_bal_di_amt_af, BigDecimal i_unit_fee, String i_action_type) 
     // {
     //      Query query = entityManager.createNativeQuery(
     //                "CALL sp_insfmsdia(:i_audit_id, :i_di_id, :i_dt_txn, :i_bal_di_amt_af, :i_unit_fee, :i_action_type)"
     //                )
     //                .setParameter("i_audit_id", i_audit_id)
     //                .setParameter("i_di_id", i_di_id)
     //                .setParameter("i_dt_txn", i_dt_txn)
     //                .setParameter("i_bal_di_amt_af", i_bal_di_amt_af)
     //                .setParameter("i_unit_fee", i_unit_fee)
     //                .setParameter("i_action_type", i_action_type);

     //      BigInteger result = (BigInteger) query.getSingleResult();
     //      return result;
     // }

     @Override
     public BigInteger sp_insfmsdia(FMSDeferredIncome fmsDeferredIncome) 
     {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_insfmsdia(:i_audit_id, :i_di_id, :i_dt_txn, :i_bal_di_amt_af, :i_unit_fee, :i_action_type)"
                    )
                    .setParameter("i_audit_id", fmsDeferredIncome.getAudit_id())
                    .setParameter("i_di_id", fmsDeferredIncome.getDi_id())
                    .setParameter("i_dt_txn", fmsDeferredIncome.getDt_txn())
                    .setParameter("i_bal_di_amt_af", fmsDeferredIncome.getBal_di_amt_af())
                    .setParameter("i_unit_fee", fmsDeferredIncome.getUnit_fee())
                    .setParameter("i_action_type", fmsDeferredIncome.getAction_type());

          BigInteger result = (BigInteger) query.getSingleResult();
          return result;
     }

}
