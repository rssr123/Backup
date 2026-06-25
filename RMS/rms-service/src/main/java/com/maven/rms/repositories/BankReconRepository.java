package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IBankReconInterface;
import com.maven.rms.models.BankDocRequest;
import com.maven.rms.models.BankReconDetail;
import com.maven.rms.models.BankReconRequest;
import com.maven.rms.models.BankReconSch;
import com.maven.rms.models.PGDetailListingRequest;

@Repository
public class BankReconRepository implements IBankReconInterface{
    
    @PersistenceContext
    private EntityManager entityManager;

  @Override
    public Integer sp_uploadDoc(BankDocRequest bankDocRequest, Blob blob, String username) {
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insbank");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_file_nm", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_content", Blob.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_type", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_size_kb", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dt_settlement", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_created_by", String.class, javax.persistence.ParameterMode.IN);

        // Set parameters
        storedProcedureQuery.setParameter("i_file_nm", bankDocRequest.getI_file_nm());
        storedProcedureQuery.setParameter("i_file_content", blob);
        storedProcedureQuery.setParameter("i_file_type", bankDocRequest.getI_file_type());
        storedProcedureQuery.setParameter("i_file_size_kb", bankDocRequest.getI_file_size());
        storedProcedureQuery.setParameter("i_dt_settlement", bankDocRequest.getI_dt_settlement());
        storedProcedureQuery.setParameter("i_created_by", username);

        // Execute stored procedure
        storedProcedureQuery.execute();

        // Handle the result (if the stored procedure returns a result set or an output parameter)
        // For example, if the stored procedure returns a single integer result:
        Integer result = null;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }
        return result;
    }
 
    @Override
    public List<String> sp_getPGSettlementDateTaskList() {
        Query query = entityManager.createNativeQuery("CALL sp_getPGSettlementDate()");
    List<Object> resultList = query.getResultList();

    List<String> settlementDates = new ArrayList<>();
    for (Object result : resultList) {
        if (result instanceof Object[]) {
            // Handle multiple results
            for (Object obj : (Object[]) result) {
                settlementDates.add(obj.toString());
            }
        } else {
            // Handle single result
            settlementDates.add(result.toString());
        }
    }

    return settlementDates;
    }

    @Override
    public List<Object[]> sp_getBankReconTaskList(BankReconRequest BankRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getBankRecon(:i_page,:i_size,:i_rc_bank_id,:i_task_no, :i_dt_settlement, :i_merchant_id, :i_task_status, :i_dt_upload, :i_recon_status)")
                .setParameter("i_page", BankRequest.getI_page())
                .setParameter("i_size", BankRequest.getI_size())
                .setParameter("i_rc_bank_id", BankRequest.getI_rc_bank_id())
                .setParameter("i_task_no", BankRequest.getI_task_no())
                .setParameter("i_dt_settlement", BankRequest.getI_dt_settlement())
                .setParameter("i_merchant_id", BankRequest.getI_merchant_id())
                .setParameter("i_task_status", BankRequest.getI_task_status())
                .setParameter("i_dt_upload", BankRequest.getI_dt_uploaded())
                .setParameter("i_recon_status", BankRequest.getI_recon_status());
        return query.getResultList();
    }

    @Override
    public Integer sp_checkbktask(BankReconRequest bodyRequest){
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_checkbktask");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_task_no", String.class, javax.persistence.ParameterMode.IN);

        storedProcedureQuery.setParameter("i_task_no", bodyRequest.getI_task_no());

        // Execute stored procedure
        storedProcedureQuery.execute();
        Integer result = 0;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }

        return result;
    }

    @Override
    public List<Object[]> sp_getbanktxnlisting(BankReconDetail bankTxnListingRequest) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sp_getbanktxnlisting'");
    }

    @Override
    public List<Object[]> sp_getbankpgtxnlisting( PGDetailListingRequest pgDetailListingRequest ) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sp_getbankpgtxnlisting'");
    }

    @Override
    public List<Object[]> sp_getrcbankdetails(BankReconDetail bankReconDetail) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'sp_getrcbankdetails'");
    }
    
}
