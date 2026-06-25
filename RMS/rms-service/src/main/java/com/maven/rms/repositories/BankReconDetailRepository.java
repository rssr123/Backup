package com.maven.rms.repositories;

import java.math.BigInteger;
import java.sql.Blob;
import java.util.List;

import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IBankReconDetailInterface;
import com.maven.rms.models.BankReconDetail;
import com.maven.rms.models.BankReconRequest;
import com.maven.rms.models.PGDetailListingRequest;

@Repository
public class BankReconDetailRepository implements IBankReconDetailInterface{
    @PersistenceContext
    private EntityManager entityManager;

    @Override//Object[]
    public List<Object[]> sp_getrcbankdetails(BankReconDetail bankReconDetail)
    {
 
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getrcbankdetails(:i_task_no)")
                    .setParameter("i_task_no", bankReconDetail.getTask_no());
 
          return query.getResultList();
     }

    @Override//Object[]
    public List<Object[]> sp_getbankpgtxnlisting( PGDetailListingRequest pgDetailListingRequest)
    {
        String i_found_in_rms = "";
        if(pgDetailListingRequest.getI_found_in_rms() == null){
                i_found_in_rms = null;
        }
        else if(pgDetailListingRequest.getI_found_in_rms() == 0){
                i_found_in_rms = "No";
        }
        else if(pgDetailListingRequest.getI_found_in_rms() == 1){
                i_found_in_rms = "Yes";
        }
 
           Query query = entityManager.createNativeQuery(
                     "CALL sp_getbankpgtxnlisting(:i_page, :i_size, :i_task_no, :i_txn_date, :i_txn_type, :i_fnd_pg, :i_txn_id, :i_txn_cd)")
                     .setParameter("i_page", pgDetailListingRequest.getI_page())
                     .setParameter("i_size", pgDetailListingRequest.getI_size())
                     .setParameter("i_task_no", pgDetailListingRequest.getI_task_no())
                     .setParameter("i_txn_date", pgDetailListingRequest.getI_txn_date())
                     .setParameter("i_txn_type", pgDetailListingRequest.getI_txn_type())
                     .setParameter("i_fnd_pg", i_found_in_rms)
                     .setParameter("i_txn_id", pgDetailListingRequest.getI_txn_id())
                     .setParameter("i_txn_cd", pgDetailListingRequest.getI_txn_code());
 
           return query.getResultList();
    }

    @Override//Object[]
    public List<Object[]> sp_getbanktxnlisting(BankReconDetail bankTxnListingRequest  )
    {
   
            Query query = entityManager.createNativeQuery(
                      "CALL sp_getbanktxnlisting(:i_page, :i_size, :i_task_no, :i_txn_ref, :i_acct_no, :i_brn_chn)")
                      .setParameter("i_page", bankTxnListingRequest.getI_page())
                      .setParameter("i_size", bankTxnListingRequest.getI_size())
                      .setParameter("i_task_no", bankTxnListingRequest.getTask_no())
                      .setParameter("i_txn_ref",bankTxnListingRequest.getTxn_ref() )
                      .setParameter("i_acct_no", bankTxnListingRequest.getAcct_no())
                      .setParameter("i_brn_chn", bankTxnListingRequest.getBrn_chn());
   
            return query.getResultList();
    }

    @Override//Object[]
    public List<Object[]> sp_getbankpgfiletxn(BankReconDetail pgfilerelatedtxnRequest)
    {
   
            Query query = entityManager.createNativeQuery(
                      "CALL sp_getbankpgfiletxn(:i_page, :i_size, :i_task_no, :i_txn_ref, :i_dt_posting)")
                      .setParameter("i_page", pgfilerelatedtxnRequest.getI_page())
                      .setParameter("i_size", pgfilerelatedtxnRequest.getI_size())
                      .setParameter("i_task_no", pgfilerelatedtxnRequest.getTask_no())
                      .setParameter("i_txn_ref", pgfilerelatedtxnRequest.getTxn_ref())
                      .setParameter("i_dt_posting", pgfilerelatedtxnRequest.getDt_posting());
   
            return query.getResultList();
    }

    @Override//Object[]
    public List<Object[]> sp_getbanknostmt(BankReconDetail nobankstmtRequest)
    {
   
            Query query = entityManager.createNativeQuery(
                      "CALL sp_getbanknostmt(:i_page, :i_size, :i_task_no, :i_file_nm, :i_uploaded_by, :i_dt_uploaded)")
                      .setParameter("i_page", nobankstmtRequest.getI_page())
                      .setParameter("i_size", nobankstmtRequest.getI_size())
                      .setParameter("i_task_no", nobankstmtRequest.getTask_no())
                      .setParameter("i_file_nm", nobankstmtRequest.getFile_nm())
                      .setParameter("i_uploaded_by", nobankstmtRequest.getUploaded_by())
                      .setParameter("i_dt_uploaded", nobankstmtRequest.getDt_uploaded());
   
            return query.getResultList();
    }

    @Override
    public BigInteger sp_updrcbankdetailstatus(BankReconDetail bankDetailStatusRequest)
    {
   
            Query query = entityManager.createNativeQuery(
                      "CALL sp_updrcbankstatus(:i_task_no, :i_task_status, :i_remarks)")
                      .setParameter("i_task_no", bankDetailStatusRequest.getTask_no())
                      .setParameter("i_task_status", bankDetailStatusRequest.getTask_status())
                      .setParameter("i_remarks", bankDetailStatusRequest.getRemarks());
   
            return (BigInteger) query.getSingleResult();
    }

    @Override
    public Blob sp_getrcpgdoc(BankReconRequest bankReconDetailRequest)
    {
   
            Query query = entityManager.createNativeQuery(
                      "CALL sp_getrcpgdoc(:i_task_no)")
                      .setParameter("i_task_no", bankReconDetailRequest.getI_task_no());
   
            return (Blob)query.getSingleResult();
    }

    @Override
    public Blob sp_getrcbkdoc(BankReconDetail bankReconDetailRequest)
    {
   
            Query query = entityManager.createNativeQuery(
                      "CALL sp_getrcbkdoc(:i_task_no, :i_file_nm)")
                      .setParameter("i_task_no", bankReconDetailRequest.getTask_no())
                      .setParameter("i_file_nm", bankReconDetailRequest.getFile_nm());
   
            return (Blob)query.getSingleResult();
    }
}
