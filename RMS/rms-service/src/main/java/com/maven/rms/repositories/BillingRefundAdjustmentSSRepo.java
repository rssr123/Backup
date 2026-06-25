package com.maven.rms.repositories;

import java.sql.Blob;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.maven.rms.models.Billing.BillAdjUpdReq;
import com.maven.rms.models.Billing.BillDocReq;
import com.maven.rms.models.Billing.BillGetItemReq;
import com.maven.rms.models.Billing.BillListingRequest;
import com.maven.rms.models.Billing.BillSearchRequest;
import com.maven.rms.models.Billing.BillingAdjustmentRequest;
import com.maven.rms.services.AuthService;

@Repository
public class BillingRefundAdjustmentSSRepo implements IBillingRefundAdjustmentSSRepoInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuthService authService;

    @Override
    public List<Object[]> sp_getbillsearch(BillSearchRequest billSearchRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getbillsearch(:i_page, :i_size, :i_cust_id, :i_bil_no, :i_orn_no,:i_ent_ty,:i_ent_no)")
                .setParameter("i_page", billSearchRequest.getI_page())
                .setParameter("i_size", billSearchRequest.getI_size())
                .setParameter("i_cust_id", billSearchRequest.getI_cust_id())
                .setParameter("i_bil_no", billSearchRequest.getI_bil_no())
                .setParameter("i_orn_no", billSearchRequest.getI_orn_no())
                .setParameter("i_ent_ty", billSearchRequest.getI_ent_ty())
                .setParameter("i_ent_no", billSearchRequest.getI_ent_no());
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getbillcancellisting(BillListingRequest billSearchRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getbillcancellisting(:i_page, :i_size, :i_dt_created_fr, :i_dt_created_to, :i_billing_status, :i_bil_no,:i_cust_id)")
                .setParameter("i_page", billSearchRequest.getI_page())
                .setParameter("i_size", billSearchRequest.getI_size())
                .setParameter("i_dt_created_fr", billSearchRequest.getI_dt_created_fr())
                .setParameter("i_dt_created_to", billSearchRequest.getI_dt_created_to())
                .setParameter("i_billing_status", billSearchRequest.getI_billing_status())
                .setParameter("i_bil_no", billSearchRequest.getI_bil_no())
                .setParameter("i_cust_id", billSearchRequest.getI_cust_id());
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getbillitem(BillGetItemReq billSearchRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getbillitem(:i_bil_id)")
                .setParameter("i_bil_id", billSearchRequest.getI_bil_id());
        return query.getResultList();
    }


    @Override
    public List<Object[]> sp_getbilsuppdoc(BillDocReq billDocReq) {
         Query query = entityManager.createNativeQuery("CALL sp_getbilsuppdoc(:i_bil_id)")
                   .setParameter("i_bil_id", billDocReq.getI_bil_id());
                   
         return query.getResultList();
    }

    @Override
     public Blob sp_getbillsuppfilecontent(BillDocReq billDocReq) {
          Query query = entityManager.createNativeQuery("CALL sp_getbillsuppfilecontent(:i_bil_doc_id)")
                    .setParameter("i_bil_doc_id", billDocReq.getI_bil_doc_id());
          
          return (Blob) query.getSingleResult();
     }

    @Override
     public Integer sp_updbillcancel(BillDocReq billDocReq) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_updbillcancel(:i_bil_id)")
                    .setParameter("i_bil_id", billDocReq.getI_bil_id());
          Integer result = (Integer) query.getSingleResult();
          return result;
     }

     @Override
     public List<Object[]> sp_getbilladjustment(BillingAdjustmentRequest billSearchRequest) {
         Query query = entityManager.createNativeQuery(
                 "CALL sp_getbilladjustment(:i_page, :i_size, :i_bil_no, :i_orn_no,:i_ent_ty,:i_ent_no)")
                 .setParameter("i_page", billSearchRequest.getI_page())
                 .setParameter("i_size", billSearchRequest.getI_size())
                 .setParameter("i_bil_no", billSearchRequest.getI_bil_no())
                 .setParameter("i_orn_no", billSearchRequest.getI_orn_no())
                 .setParameter("i_ent_ty", billSearchRequest.getI_ent_ty())
                 .setParameter("i_ent_no", billSearchRequest.getI_ent_no());
         return query.getResultList();
     }

     @Override
     public Integer sp_updbilladjust(List<BillAdjUpdReq> billAdjUpdReqs) {
        Integer result = 0;
        for (BillAdjUpdReq billAdjUpdReq : billAdjUpdReqs) {
            Query query = entityManager.createNativeQuery(
                "CALL sp_updbilladjust(:i_bil_id, :i_modified_by, :i_bil_item_id, :i_unit_fee, :i_tax_amt, :i_final_amt, :i_qty)")
                .setParameter("i_bil_id", billAdjUpdReq.getI_bil_id())
                .setParameter("i_modified_by", authService.getLoginUserName())
                .setParameter("i_bil_item_id", billAdjUpdReq.getI_bil_item_id())
                .setParameter("i_unit_fee", billAdjUpdReq.getI_unit_fee())
                .setParameter("i_tax_amt", billAdjUpdReq.getI_tax_amt())
                .setParameter("i_final_amt", billAdjUpdReq.getI_final_amt())
                .setParameter("i_qty", billAdjUpdReq.getI_qty());

            result = (Integer) query.getSingleResult();
        }
          return result;
     }

     @Override
     public List<Object[]> sp_getbillhist(BillAdjUpdReq billSearchRequest) {
         Query query = entityManager.createNativeQuery(
                 "CALL sp_getbillhist(:i_bil_id)")
                 .setParameter("i_bil_id", billSearchRequest.getI_bil_id());
         return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getbillingloaagm(BillDocReq billSearchRequest) {
         Query query = entityManager.createNativeQuery(
                 "CALL sp_getbillingloaagm(:i_bil_id)")
                 .setParameter("i_bil_id", billSearchRequest.getI_bil_id());
         return query.getResultList();
     }
}