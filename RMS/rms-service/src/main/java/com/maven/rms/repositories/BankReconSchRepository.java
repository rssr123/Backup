package com.maven.rms.repositories;

import java.math.BigInteger;
import java.sql.Blob;
import java.util.List;
 
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
 
import org.springframework.stereotype.Repository;
import com.maven.rms.interfaces.IBankReconSchInterface;
import com.maven.rms.models.BankReconSch;

@Repository
public class BankReconSchRepository implements IBankReconSchInterface{
    @PersistenceContext
    private EntityManager entityManager;

    // Get list of rc doc that need to insert into rc bank txn
    public List<Object[]> sp_getBankDoc()
    {
        Query query = entityManager.createNativeQuery(
            "CALL sp_getrcbank()"
        );
       
        return query.getResultList();
    }

    //Get file content in Blob
    public Blob sp_getrcbankdoc(BigInteger i_rc_bankdoc_id)
    {
        Query query = entityManager.createNativeQuery(
            "CALL sp_getrcbankdoc(:i_rc_bankdoc_id)"
        ).setParameter("i_rc_bankdoc_id", i_rc_bankdoc_id);

        return (Blob)query.getSingleResult();
    }

    public BigInteger sp_insrcbankdoc(BankReconSch account, BigInteger i_rc_bank_id, BigInteger i_rc_bankdoc_id){

        Query query = entityManager.createNativeQuery(
            "CALL sp_insrcbanktxn(:i_rc_bank_id, :i_rc_bankdoc_id, :i_acct_no, :i_acct_type, :i_acct_nm," +
                    ":i_dt_fr, :i_dt_to, :i_total_debit, :i_total_credit, :i_begin_bal, :i_end_bal," +
                    ":i_dt_txn, :i_dt_posting, :i_txn_desc, :i_txn_ref," +
                    ":i_debit, :i_credit, :i_source_cd, :i_teller_id, :i_brn_chn," +
                    ":i_txn_cd, :i_end_bal2, :i_virtual_acct, :i_txn_desc2," +
                    ":i_txn_desc3, :i_txn_desc4, :i_dt_expiry)"
            )
            .setParameter("i_rc_bank_id", i_rc_bank_id)
            .setParameter("i_rc_bankdoc_id", i_rc_bankdoc_id)
            .setParameter("i_acct_no", account.getAcct_no())
            .setParameter("i_acct_type", account.getAcct_type())
            .setParameter("i_acct_nm", account.getAcct_nm())

            .setParameter("i_dt_fr", account.getDt_fr())
            .setParameter("i_dt_to", account.getDt_to())
            .setParameter("i_total_debit", account.getTotal_debit())
            .setParameter("i_total_credit", account.getTotal_credit())
            .setParameter("i_begin_bal", account.getBegin_bal())

            .setParameter("i_end_bal", account.getEnd_bal())
            .setParameter("i_dt_txn", account.getDt_txn())
            .setParameter("i_dt_posting", account.getDt_posting())
            .setParameter("i_txn_desc", account.getTxn_desc())
            .setParameter("i_txn_ref", account.getTxn_ref())
 
            .setParameter("i_debit", account.getDebit())
            .setParameter("i_credit", account.getCredit())
            .setParameter("i_source_cd", account.getSource_cd())
            .setParameter("i_teller_id", account.getTeller_id())
            .setParameter("i_brn_chn", account.getBrn_chn())

            .setParameter("i_txn_cd", account.getTxn_cd())
            .setParameter("i_end_bal2", account.getEnd_bal2())
            .setParameter("i_virtual_acct", account.getVirtual_acct())
            .setParameter("i_txn_desc2", account.getTxn_desc2())
            .setParameter("i_txn_desc3", account.getTxn_desc3())

            .setParameter("i_txn_desc4", account.getTxn_desc4())
            .setParameter("i_dt_expiry", account.getDt_expiry());     

        return (BigInteger)query.getSingleResult();
    }

     // Upd rc_bank
     public BigInteger sp_updrcbank(BankReconSch account)
     {
         Query query = entityManager.createNativeQuery(
             "CALL sp_updrcbank(:i_rc_bank_id, :i_status)"
         )
         .setParameter("i_rc_bank_id", account.getRc_bank_id())
         .setParameter("i_status", account.getStatus());
  
         return (BigInteger)query.getSingleResult();
     }
  
     // scheduler compairison credit  
     public Integer sp_updrcbanktxn()
     {
         Query query = entityManager.createNativeQuery(
             "CALL sp_updrcbanktxn()"
         );
  
         return (Integer) query.getSingleResult();
     }
}