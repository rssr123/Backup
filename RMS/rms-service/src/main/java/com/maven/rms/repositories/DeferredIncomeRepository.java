package com.maven.rms.repositories;

import org.springframework.stereotype.Repository;
import com.maven.rms.interfaces.IDeferredIncomeInterface;
import com.maven.rms.models.DeferredIncome;
import com.maven.rms.models.DeferredIncomeTermination;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import java.util.List;
import java.math.BigInteger;


@Repository
public class DeferredIncomeRepository implements IDeferredIncomeInterface {
     @PersistenceContext
     private EntityManager entityManager;

     @Override
     public BigInteger sp_insdi(DeferredIncome recognitionRequest) 
     {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_insdi(:i_fee_detail_id, :i_txn_type, :i_entity_no, :i_entity_type, :i_dt_effective,:i_dt_expiry, :i_item_ref_no,:i_approval_status, :i_dt_approval)"
                    )
                    .setParameter("i_fee_detail_id", recognitionRequest.getFee_detail_id())
                    .setParameter("i_txn_type", recognitionRequest.getTxn_type())
                    .setParameter("i_entity_no", recognitionRequest.getEntity_no())
                    .setParameter("i_entity_type", recognitionRequest.getEntity_type())
                    .setParameter("i_dt_effective", recognitionRequest.getDt_effective())
                    .setParameter("i_dt_expiry", recognitionRequest.getDt_expiry())
                    .setParameter("i_item_ref_no", recognitionRequest.getItem_ref_no())
                    .setParameter("i_approval_status", recognitionRequest.getApproval_status())
                    .setParameter("i_dt_approval", recognitionRequest.getDt_approval());

          BigInteger result = (BigInteger) query.getSingleResult();
          return result;
     }

     @Override
     public BigInteger sp_insdi_tmn_log(DeferredIncomeTermination terminationRequest)
     {
        Query query = entityManager.createNativeQuery(
            "CALL sp_insdi_tmn_log(:i_txn_type, :i_entity_no, :i_entity_type, :i_dt_termination, :i_dt_approval)"
        )
        .setParameter("i_txn_type", terminationRequest.getTxn_type())
        .setParameter("i_entity_no", terminationRequest.getEntity_no())
        .setParameter("i_entity_type", terminationRequest.getEntity_type())
        .setParameter("i_dt_termination", terminationRequest.getDt_termination())
        .setParameter("i_dt_approval", terminationRequest.getDt_approval());

        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
     }

     public Integer sp_upddi()
     {
        Query query = entityManager.createNativeQuery(
            "CALL sp_upddi()"
        );

        Integer result = (Integer) query.getSingleResult();
        return result;
     }

     @Override//Object[]
     public List<Object[]> sp_getdi(DeferredIncome deferredIncome) {
 
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getdi(:i_page, :i_size, :i_di_id, :i_fee_detail_id, :i_txn_type, :i_entity_type, :i_entity_no, :i_dt_effective, :i_dt_expiry, :i_item_ref_no, :i_approval_status, :i_dt_approval, :i_status)")
                    .setParameter("i_page", deferredIncome.getI_page())
                    .setParameter("i_size", deferredIncome.getI_size())
                    .setParameter("i_di_id", deferredIncome.getI_di_id())
                    .setParameter("i_fee_detail_id", deferredIncome.getI_fee_detail_id())
                    .setParameter("i_txn_type", deferredIncome.getI_txn_type())
                    .setParameter("i_entity_type", deferredIncome.getI_entity_type())
                    .setParameter("i_entity_no", deferredIncome.getI_entity_no())
                    .setParameter("i_dt_effective", deferredIncome.getI_dt_effective())
                    .setParameter("i_dt_expiry", deferredIncome.getI_dt_expiry())
                    .setParameter("i_item_ref_no", deferredIncome.getI_item_ref_no())
                    .setParameter("i_approval_status", deferredIncome.getI_approval_status())
                    .setParameter("i_dt_approval", deferredIncome.getI_dt_approval())
                    .setParameter("i_status", deferredIncome.getI_status());
 
          // Check if the dates are null and set accordingly
          if (deferredIncome.getI_dt_effective() != null) {
               query.setParameter("i_dt_effective", deferredIncome.getI_dt_effective());
          } else {
               query.setParameter("i_dt_effective", null);
          }
 
          if (deferredIncome.getI_dt_expiry() != null) {
               query.setParameter("i_dt_expiry", deferredIncome.getI_dt_expiry());
          } else {
               query.setParameter("i_dt_expiry", null);
          }
 
          if (deferredIncome.getI_dt_approval() != null) {
               query.setParameter("i_dt_approval", deferredIncome.getI_dt_approval());
          } else {
               query.setParameter("i_dt_approval", null);
          }
 
          return query.getResultList();
     }
}
