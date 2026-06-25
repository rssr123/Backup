package com.maven.rms.repositories;


import java.sql.Blob;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IFMSInterface;
import com.maven.rms.models.FMSRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.models.BankDocRequest;
import com.maven.rms.models.FMS;
import com.maven.rms.models.FMSLedgerDoc;
import com.maven.rms.models.FMSLedgerDocRequest;
import com.maven.rms.models.FMSLedgerRequest;


@Repository
public class IFmsRepository implements IFMSInterface {

    @PersistenceContext
    private EntityManager entityManager;

     @Autowired
    private AuthService authService;

//      @Override
//     public List<Integer> sp_uploadDoc(FMSLedgerDocRequest fmsLedgerDocRequest, Blob blob, String username, FMSLedgerDoc fmsDoc) {
//         StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insfmsledgerdoc");

//         // Register parameters (ensure parameter names and types match those defined in the stored procedure)
//         storedProcedureQuery.registerStoredProcedureParameter("i_fms_id", Integer.class, javax.persistence.ParameterMode.IN);
//         storedProcedureQuery.registerStoredProcedureParameter("i_file_nm", String.class, javax.persistence.ParameterMode.IN);
//         storedProcedureQuery.registerStoredProcedureParameter("i_file_content", Blob.class, javax.persistence.ParameterMode.IN);
//         storedProcedureQuery.registerStoredProcedureParameter("i_file_type", String.class, javax.persistence.ParameterMode.IN);
//         storedProcedureQuery.registerStoredProcedureParameter("i_file_size", Integer.class, javax.persistence.ParameterMode.IN);
//         storedProcedureQuery.registerStoredProcedureParameter("i_created_by", String.class, javax.persistence.ParameterMode.IN);
//         storedProcedureQuery.registerStoredProcedureParameter("i_modified_by", String.class, javax.persistence.ParameterMode.IN);
//         storedProcedureQuery.registerStoredProcedureParameter("i_status", String.class, javax.persistence.ParameterMode.IN);
//         storedProcedureQuery.registerStoredProcedureParameter("i_fee_detail_id", String.class, javax.persistence.ParameterMode.IN);
//         storedProcedureQuery.registerStoredProcedureParameter("i_fee_detail_nm_en", String.class, javax.persistence.ParameterMode.IN);
//         storedProcedureQuery.registerStoredProcedureParameter("i_fms_ledger_cd", String.class, javax.persistence.ParameterMode.IN);

//         // Set parameters
//         storedProcedureQuery.setParameter("i_fms_id", fmsLedgerDocRequest.getI_fms_id());
//         storedProcedureQuery.setParameter("i_file_nm", fmsLedgerDocRequest.getI_file_nm());
//         storedProcedureQuery.setParameter("i_file_content", blob);
//         storedProcedureQuery.setParameter("i_file_type", fmsLedgerDocRequest.getI_file_type());
//         storedProcedureQuery.setParameter("i_file_size", fmsLedgerDocRequest.getI_file_size());
//         storedProcedureQuery.setParameter("i_created_by", username);
//         storedProcedureQuery.setParameter("i_modified_by", username);
//         storedProcedureQuery.setParameter("i_status", fmsLedgerDocRequest.getI_status());
//         storedProcedureQuery.setParameter("i_fee_detail_id", fmsDoc.getI_fee_detail_id());
//         storedProcedureQuery.setParameter("i_fee_detail_nm_en", fmsDoc.getI_fee_detail_nm_en());
//         storedProcedureQuery.setParameter("i_fms_ledger_cd", fmsDoc.getI_fms_ledger_cd());

//         // Execute stored procedure
//         storedProcedureQuery.execute();

//         // Handle the result (if the stored procedure returns a result set or an output parameter)
//         // For example, if the stored procedure returns a single integer result:
//     // Handle results (assuming two integer results are returned)
//         List<Integer> results = new ArrayList<>();
//         List<Object[]> resultList = storedProcedureQuery.getResultList();
//         if (!resultList.isEmpty()) {
//              Object[] resultRow = resultList.get(0); // Get the first row of results
//              if (resultRow.length >= 2) {
//                   results.add((Integer) resultRow[0]); // result1
//                   results.add((Integer) resultRow[1]); // result2
//              }
//         }
//         return results;
//     }


@Override
public List<Integer> sp_uploadDoc(FMSLedgerDocRequest fmsLedgerDocRequest, Blob blob, String username,
          List<FMSLedgerDoc> fmsDocList) {
     List<Integer> resultList = new ArrayList<>();
     StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insert_fms_doc");

     // Register parameters (ensure parameter names and types match those defined in
     // the stored procedure)
     storedProcedureQuery.registerStoredProcedureParameter("i_fms_id", Integer.class, javax.persistence.ParameterMode.IN);
     storedProcedureQuery.registerStoredProcedureParameter("i_file_nm", String.class, javax.persistence.ParameterMode.IN);
     storedProcedureQuery.registerStoredProcedureParameter("i_file_content", Blob.class, javax.persistence.ParameterMode.IN);
     storedProcedureQuery.registerStoredProcedureParameter("i_file_type", String.class, javax.persistence.ParameterMode.IN);
     storedProcedureQuery.registerStoredProcedureParameter("i_file_size", Integer.class, javax.persistence.ParameterMode.IN);
     storedProcedureQuery.registerStoredProcedureParameter("i_created_by", String.class, javax.persistence.ParameterMode.IN);
     storedProcedureQuery.registerStoredProcedureParameter("i_modified_by", String.class, javax.persistence.ParameterMode.IN);
     storedProcedureQuery.registerStoredProcedureParameter("i_status", String.class, javax.persistence.ParameterMode.IN);

     // Set parameters
     storedProcedureQuery.setParameter("i_fms_id", fmsLedgerDocRequest.getI_fms_id());
     storedProcedureQuery.setParameter("i_file_nm", fmsLedgerDocRequest.getI_file_nm());
     storedProcedureQuery.setParameter("i_file_content", blob);
     storedProcedureQuery.setParameter("i_file_type", fmsLedgerDocRequest.getI_file_type());
     storedProcedureQuery.setParameter("i_file_size", fmsLedgerDocRequest.getI_file_size());
     storedProcedureQuery.setParameter("i_created_by", username);
     storedProcedureQuery.setParameter("i_modified_by", username);
     storedProcedureQuery.setParameter("i_status", fmsLedgerDocRequest.getI_status());

     // Execute stored procedure
     storedProcedureQuery.execute();
     Integer result = (Integer) storedProcedureQuery.getSingleResult();

     for (FMSLedgerDoc fmsDoc : fmsDocList) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_insert_fms_ledger(:i_fms_id, :i_fee_detail_id, :i_fee_detail_nm_en, :i_fms_ledger_cd, :i_created_by, :i_modified_by, :i_file_index)")
                    .setParameter("i_fms_id", fmsLedgerDocRequest.getI_fms_id())
                    .setParameter("i_fee_detail_id", fmsDoc.getI_fee_detail_id())
                    .setParameter("i_fee_detail_nm_en", fmsDoc.getI_fee_detail_nm_en())
                    .setParameter("i_fms_ledger_cd", fmsDoc.getI_fms_ledger_cd())
                    .setParameter("i_created_by", username)
                    .setParameter("i_modified_by", username)
                    .setParameter("i_file_index", result);

          Integer result2 = (Integer) query.getSingleResult();
          resultList.add(result2);
     }
     return resultList;
}


     // #region FMS Start
     @Override
     public List<Object[]> sp_getfms(FMSRequest fmsRequest) {

          Query query = entityManager.createNativeQuery(
                    "CALL sp_getfms(:i_page, :i_size, :i_fms_cd, :i_modified_by, :i_dt_modified_fr, :i_dt_modified_to, :i_status)")
                    .setParameter("i_page", fmsRequest.getI_page())
                    .setParameter("i_size", fmsRequest.getI_size())
                    .setParameter("i_fms_cd", fmsRequest.getI_fms_cd())
                    .setParameter("i_modified_by", fmsRequest.getI_modified_by())
                    .setParameter("i_dt_modified_fr", fmsRequest.getI_dt_modified_fr() != null ? fmsRequest.getI_dt_modified_fr() : null)
                    .setParameter("i_dt_modified_to", fmsRequest.getI_dt_modified_to() != null ? fmsRequest.getI_dt_modified_to() : null)
                    .setParameter("i_status", fmsRequest.getI_status());

          return query.getResultList();
     }

     @Override
     public Integer sp_insfms(FMSRequest insertRequest, String i_created_by, String i_status, Integer i_is_active) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_insfms(:i_fms_cd, :i_created_by, :i_status, :i_is_active)")
                    .setParameter("i_fms_cd", insertRequest.getI_fms_cd())
                    .setParameter("i_created_by", i_created_by)
                    .setParameter("i_status", i_status)
                    .setParameter("i_is_active", i_is_active);
          return (Integer) query.getSingleResult();
     }

     @Override
     public Integer sp_updfms(FMSRequest updateRequest, String i_modified_by) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_updfms(:i_fms_id, :i_fms_cd, :i_modified_by, :i_is_active)")
                    .setParameter("i_fms_id", updateRequest.getI_fms_id())
                    .setParameter("i_fms_cd", updateRequest.getI_fms_cd())
                    .setParameter("i_is_active", updateRequest.getI_is_active())
                    .setParameter("i_modified_by", i_modified_by);
          return (Integer) query.getSingleResult();
     }

     @Override
     public Integer sp_checkfmsbyid(FMSRequest fmsRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_checkfmsbyid(:i_fms_id)")
                    .setParameter("i_fms_id", fmsRequest.getI_fms_id());
          return (Integer) query.getSingleResult();
     }

     @Override
     public Integer sp_updfms_activation(FMSRequest fmsRequest, String i_modified_by) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_updfms_activation(:i_fms_id, :i_modified_by)")
                    .setParameter("i_fms_id", fmsRequest.getI_fms_id())
                    .setParameter("i_modified_by", i_modified_by);
          return (Integer) query.getSingleResult();
     }

     // #endregion

     // #region FMS Ledger Start

     @Override
     public List<Object[]> sp_getfmsledger_v2(FMSLedgerRequest fmsLedgerRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getfmsledger_v2(:i_page, :i_size, :i_fms_id, :i_file_index, :i_fms_detail_id, :i_fms_detail_nm_en, :i_fms_ledger_cd, :i_found)")
                    .setParameter("i_page", fmsLedgerRequest.getI_page())
                    .setParameter("i_size", fmsLedgerRequest.getI_size())
                    .setParameter("i_fms_id", fmsLedgerRequest.getI_fms_id())
                    .setParameter("i_file_index", fmsLedgerRequest.getI_file_index())
                    .setParameter("i_fms_detail_id", fmsLedgerRequest.getI_fms_detail_id())
                    .setParameter("i_fms_detail_nm_en", fmsLedgerRequest.getI_fms_detail_nm_en())
                    .setParameter("i_fms_ledger_cd", fmsLedgerRequest.getI_fms_ledger_cd())
                    .setParameter("i_found", fmsLedgerRequest.getI_found());

          return query.getResultList();
     }

     //#FMS Ledger Doc

      @Override
     public List<Object[]> sp_getfmsdoc(FMSLedgerDocRequest fmsLedgerDocRequest) {
          Query query = entityManager.createNativeQuery("CALL sp_getfmsdoc(:i_fms_id)")
                    .setParameter("i_fms_id", fmsLedgerDocRequest.getI_fms_id());
                    
          return query.getResultList();
     }

     @Override
     public Integer sp_checkdocexist(FMSLedgerDocRequest fmsLedgerDocRequest) {
          Query query = entityManager.createNativeQuery("CALL sp_checkdocexist(:i_file_nm)")
                    .setParameter("i_file_nm", fmsLedgerDocRequest.getI_file_nm());
                    
                return (Integer) query.getSingleResult();
     }

     @Override
     public Blob sp_getfmsfilecontent(FMSLedgerDocRequest fmsLedgerDocRequest) {
          Query query = entityManager.createNativeQuery("CALL sp_getfmsfilecontent(:i_file_nm)")
                    .setParameter("i_file_nm", fmsLedgerDocRequest.getI_file_nm());
          
          return (Blob) query.getSingleResult();
     }


     @Override
    public List<Integer> sp_getfmsledgersummarycount(FMSLedgerDocRequest fmsLedgerDocRequest) {
     Query query = entityManager.createNativeQuery("CALL sp_getfmsledgersummarycount(:i_fms_id)").setParameter("i_fms_id", fmsLedgerDocRequest.getI_fms_id());

        // Handle the result (if the stored procedure returns a result set or an output parameter)
        // For example, if the stored procedure returns a single integer result:
    // Handle results (assuming two integer results are returned)
        List<Integer> results = new ArrayList<>();
        List<Object[]> resultList = query.getResultList();
        if (!resultList.isEmpty()) {
             Object[] resultRow = resultList.get(0); // Get the first row of results
             if (resultRow.length >= 2) {
                  results.add((Integer) resultRow[0]); // result1
                  results.add((Integer) resultRow[1]); // result2
             }
        }
        return results;
    }
}