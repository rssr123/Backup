package com.maven.rms.repositories.OTC;

import java.sql.Blob;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;


// import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IOTCReturnedChequeRepositoryInterface;
import com.maven.rms.models.ServiceProviderRequest;
import com.maven.rms.models.OTC.NBLDocInsRequest;
import com.maven.rms.models.OTC.NBLInsRequest;
import com.maven.rms.models.OTC.NBLItemInsRequest;
import com.maven.rms.models.OTC.NBLItemRequest;
import com.maven.rms.models.OTC.NonBilResult;
import com.maven.rms.models.OTC.NonBillingListingRequest;
import com.maven.rms.models.OTC.OTCReturnedChequeRequest;
import com.maven.rms.services.AuthService;

@Repository
public class OTCReturnedChequeRepository implements IOTCReturnedChequeRepositoryInterface {
    
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${rms.application.emailExpiryInDay}")
    private int emailExpiryInDays;

    public int getEmailExpiryInDays() {
        return emailExpiryInDays;
    }

    @Autowired
    private AuthService authService;

    @Override
    public List<Object[]> sp_getchequeinfo(OTCReturnedChequeRequest otcReturnedChequeRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getchequeinfo(:i_page, :i_size, :i_che_no, :i_che_bank_nm, :i_rcpt_no)")
                .setParameter("i_page", otcReturnedChequeRequest.getI_page())
                .setParameter("i_size", otcReturnedChequeRequest.getI_size())
                .setParameter("i_che_no", otcReturnedChequeRequest.getI_che_no())
                .setParameter("i_che_bank_nm", otcReturnedChequeRequest.getI_che_bank_nm())
                .setParameter("i_rcpt_no", otcReturnedChequeRequest.getI_rcpt_no());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getnbltc() {
         Query query = entityManager.createNativeQuery("CALL sp_getnbltc()");
         return query.getResultList();

    }

    @Override
    public List<Object[]> sp_getnblitem(NBLItemRequest nblItemRequest) {
         Query query = entityManager.createNativeQuery("CALL sp_getnblitem(:i_bt_cd)")
                .setParameter("i_bt_cd", nblItemRequest.getI_bt_cd());
         return query.getResultList();

    }

    @Override
    public String sp_getnbrunno() {
         Query query = entityManager.createNativeQuery("CALL sp_getnbrunno()");
         return (String) query.getSingleResult();

    }

    @Override
    public List<NonBilResult> sp_insnonbill(NBLInsRequest insRequest) {
        // List<Integer> resultList = new ArrayList<>();
        // Iterate over each OTC Payment Request in the list
        Query query = entityManager.createNativeQuery(
                "CALL sp_insnonbill(:i_cust_id, :i_cust_nm, :i_cust_email, :i_cust_phone, :i_cust_addr_1, " +
                        ":i_cust_addr_2, :i_cust_addr_3, :i_ent_nm, :i_ent_no, " +
                        ":i_ent_ty, :i_created_by, :i_modified_by, :i_req_name, " +
                        ":i_req_email, :i_non_bil_no, :i_non_bil_desc, :i_ret_che_no, :i_total_bil_amt, :i_remark, " +
                        ":i_bil_status, :i_fms_admin_email, :i_fms_admin_nm, :i_cust_postcode, :i_cust_city, :i_cust_state, :i_void_reason, :i_otc_body_id, :i_counter_id, :i_che_amt, :i_dt_email_expiry, :i_bt_cd, :i_che_id, :i_payer_nm, :i_payer_email)");

        query.setParameter("i_cust_id", insRequest.getI_cust_id());
        query.setParameter("i_cust_nm", insRequest.getI_cust_nm());
        query.setParameter("i_cust_email", insRequest.getI_cust_email());
        query.setParameter("i_cust_phone", insRequest.getI_cust_phone());
        query.setParameter("i_cust_addr_1", insRequest.getI_cust_addr_1());
        query.setParameter("i_cust_addr_2", insRequest.getI_cust_addr_2());
        query.setParameter("i_cust_addr_3", insRequest.getI_cust_addr_3());
        query.setParameter("i_ent_nm", insRequest.getI_ent_nm());
        query.setParameter("i_ent_no", insRequest.getI_ent_no());
        query.setParameter("i_ent_ty", insRequest.getI_ent_ty());
        query.setParameter("i_created_by", authService.getLoginUserName());
        query.setParameter("i_modified_by", authService.getLoginUserName());
        query.setParameter("i_req_name", authService.getLoginUserName());
        query.setParameter("i_req_email", authService.getUserEmail());
        query.setParameter("i_non_bil_no", insRequest.getI_non_bil_no());
        query.setParameter("i_non_bil_desc", insRequest.getI_non_bil_desc());
        query.setParameter("i_ret_che_no", insRequest.getI_ret_che_no());
        query.setParameter("i_total_bil_amt", insRequest.getI_total_bil_amt());
        query.setParameter("i_remark", insRequest.getI_remark());
        query.setParameter("i_bil_status", insRequest.getI_bil_status());
        query.setParameter("i_fms_admin_email", insRequest.getI_fms_admin_email());
        query.setParameter("i_fms_admin_nm", insRequest.getI_fms_admin_nm());
        query.setParameter("i_cust_postcode", insRequest.getI_cust_postcode());
        query.setParameter("i_cust_city", insRequest.getI_cust_city());
        query.setParameter("i_cust_state", insRequest.getI_cust_state());
        query.setParameter("i_void_reason", insRequest.getI_void_reason());
        // query.setParameter("i_mtt_id", insRequest.getI_mtt_id());
        query.setParameter("i_otc_body_id", insRequest.getI_otc_body_id());
        query.setParameter("i_counter_id", insRequest.getI_counter_id());
        query.setParameter("i_che_amt", insRequest.getI_che_amt());
        query.setParameter("i_dt_email_expiry", LocalDate.now().plusDays(getEmailExpiryInDays()));
        query.setParameter("i_bt_cd", insRequest.getI_bt_cd());
        query.setParameter("i_che_id", insRequest.getI_che_id());
        query.setParameter("i_payer_nm", insRequest.getI_payer_nm());
        query.setParameter("i_payer_email", insRequest.getI_payer_email());

        // Execute and retrieve multiple results
        List<Object[]> results = query.getResultList();

        List<NonBilResult> resultList = new ArrayList<>();

        for (Object[] row : results) {
            Integer mttId = row[0] != null ? ((Number) row[0]).intValue() : null;
            Integer nonBilId = row[1] != null ? ((Number) row[1]).intValue() : null;
            String nonBilNo = row[2] != null ? row[2].toString() : null;
            resultList.add(new NonBilResult(mttId, nonBilId, nonBilNo));
        }
        // // Convert results to a List<Integer>
        // for (Object[] row : results) {
        //     for (Object obj : row) {
        //         resultList.add(obj != null ? ((Number) obj).intValue() : 0);
        //     }
        // }

        return resultList; // Return the list containing both integers
    }

    // @Override
    // public Integer sp_insnonbillitem(List<NBLItemInsRequest> insRequests) {

    //     Integer result = 0;
    //     // Iterate over each OTC Payment Request in the list
    //     for (int i = 0; i < insRequests.size(); i++) {
    //         Query query = entityManager.createNativeQuery(
    //                 "CALL sp_insnonbillitem(:i_mft_pk, :i_unit_fee, :i_quantity, :i_tax_pct, " +
    //                         ":i_tax_amt, :i_item_total_amt, :i_created_by, :i_modified_by, :i_non_bil_no, :i_line_nbr, :i_mtt_id)");

    //         query.setParameter("i_mft_pk", insRequests.get(i).getI_mft_pk());
    //         query.setParameter("i_unit_fee", insRequests.get(i).getI_unit_fee());
    //         query.setParameter("i_quantity", insRequests.get(i).getI_quantity());
    //         query.setParameter("i_tax_pct", insRequests.get(i).getI_tax_pct());
    //         query.setParameter("i_tax_amt", insRequests.get(i).getI_tax_amt());
    //         query.setParameter("i_item_total_amt", insRequests.get(i).getI_item_total_amt());
    //         query.setParameter("i_created_by", authService.getLoginUserName());
    //         query.setParameter("i_modified_by", authService.getLoginUserName());
    //         query.setParameter("i_non_bil_no", insRequests.get(i).getI_non_bil_no());
    //         query.setParameter("i_line_nbr", i + 1);
    //         query.setParameter("i_mtt_id", insRequests.get(i).getI_mtt_id());
    //         // Execute the stored procedure
    //         result = (Integer) query.getSingleResult();
    //     }

    //     return result; // Return the accumulated total result
    // }

    @Override
    public Integer sp_insnonbillitem(List<NBLItemInsRequest> insRequests) {

        Integer result = 0;
        // Iterate over each OTC Payment Request in the list
        for (int i = 0; i < insRequests.size(); i++) {
            Query query = entityManager.createNativeQuery(
                    "CALL sp_insnonbillitem(:i_mft_pk, :i_unit_fee, :i_quantity, :i_tax_pct, " +
                            ":i_tax_amt, :i_item_total_amt, :i_created_by, :i_modified_by, :i_non_bil_id, :i_line_nbr, :i_mtt_id)");

            query.setParameter("i_mft_pk", insRequests.get(i).getI_mft_pk());
            query.setParameter("i_unit_fee", insRequests.get(i).getI_unit_fee());
            query.setParameter("i_quantity", insRequests.get(i).getI_quantity());
            query.setParameter("i_tax_pct", insRequests.get(i).getI_tax_pct());
            query.setParameter("i_tax_amt", insRequests.get(i).getI_tax_amt());
            query.setParameter("i_item_total_amt", insRequests.get(i).getI_item_total_amt());
            query.setParameter("i_created_by", authService.getLoginUserName());
            query.setParameter("i_modified_by", authService.getLoginUserName());
            query.setParameter("i_non_bil_id", insRequests.get(i).getI_non_bil_id());
            query.setParameter("i_line_nbr", i + 1);
            query.setParameter("i_mtt_id", insRequests.get(i).getI_mtt_id());
            // Execute the stored procedure
            result = (Integer) query.getSingleResult();
        }

        return result; // Return the accumulated total result
    }

    // @Override
    // public Integer sp_insnonbilldoc(List<NBLDocInsRequest> insRequests) {

    //     Integer result = 0;
    //     // Iterate over each OTC Payment Request in the list
    //     for (NBLDocInsRequest req : insRequests) {
    //         Query query = entityManager.createNativeQuery(
    //                 "CALL sp_insnonbilldoc(:i_file_nm, :i_file_content, :i_file_type, :i_file_size, " +
    //                         ":i_file_category, :i_created_by, :i_modified_by)");

    //         query.setParameter("i_file_nm", req.getI_file_nm());
    //         query.setParameter("i_file_content", req.getI_file_content());
    //         query.setParameter("i_file_type", req.getI_file_type());
    //         query.setParameter("i_file_size", req.getI_file_size());
    //         query.setParameter("i_file_category", req.getI_file_category());
    //         query.setParameter("i_created_by", authService.getLoginUserName());
    //         query.setParameter("i_modified_by", authService.getLoginUserName());

    //         // Execute the stored procedure
    //         result = (Integer) query.getSingleResult();
    //     }

    //     return result; // Return the accumulated total result
    // }


    @Override
    public Integer sp_insnonbilldoc(NBLDocInsRequest insRequests, Blob blob) {
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insnonbilldoc");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_file_nm", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_content", Blob.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_type", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_size", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_category", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_created_by", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_modified_by", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_non_bil_no", String.class, javax.persistence.ParameterMode.IN);

        // Set parameters
        storedProcedureQuery.setParameter("i_file_nm", insRequests.getI_file_nm());
        storedProcedureQuery.setParameter("i_file_content", blob);
        storedProcedureQuery.setParameter("i_file_type", insRequests.getI_file_type());
        storedProcedureQuery.setParameter("i_file_size", insRequests.getI_file_size());
        storedProcedureQuery.setParameter("i_file_category", insRequests.getI_file_category());
        storedProcedureQuery.setParameter("i_created_by", authService.getLoginUserName());
        storedProcedureQuery.setParameter("i_modified_by", authService.getLoginUserName());
        storedProcedureQuery.setParameter("i_non_bil_no", insRequests.getI_non_bil_no());
      
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
    public List<Object[]> sp_getnonbilllisting(NonBillingListingRequest req) {
         Query query = entityManager.createNativeQuery("CALL sp_getnonbilllisting(:i_page, :i_size, :i_ent_nm, :i_ent_no, :i_cust_id, :i_bil_status, :i_non_bil_no, :i_che_id, :i_che_no)")
                .setParameter("i_page", req.getI_page())
                .setParameter("i_size", req.getI_size())
                .setParameter("i_ent_nm", req.getI_ent_nm())
                .setParameter("i_ent_no", req.getI_ent_no())
                .setParameter("i_cust_id", req.getI_cust_id())
                .setParameter("i_bil_status", req.getI_bil_status())
                .setParameter("i_non_bil_no", req.getI_non_bil_no())
                .setParameter("i_che_id", req.getI_che_id())
                .setParameter("i_che_no", req.getI_che_no());

         return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getnonbillitem(NonBillingListingRequest req) {
         Query query = entityManager.createNativeQuery("CALL sp_getnonbillitem(:i_non_bil_id)")
                .setParameter("i_non_bil_id", req.getI_non_bil_id());

         return query.getResultList();

    }


    @Override
    public List<Object[]> sp_getnonbildoc(NonBillingListingRequest billDocReq) {
         Query query = entityManager.createNativeQuery("CALL sp_getnonbildoc(:i_non_bil_id)")
                   .setParameter("i_non_bil_id", billDocReq.getI_non_bil_id());
                   
         return query.getResultList();
    }

    @Override
     public Blob sp_getnonbildoccontent(NonBillingListingRequest billDocReq) {
          Query query = entityManager.createNativeQuery("CALL sp_getnonbildoccontent(:i_non_bil_doc_id)")
                    .setParameter("i_non_bil_doc_id", billDocReq.getI_non_bil_doc_id());
          
          return (Blob) query.getSingleResult();
     }

     @Override
     public List<Object[]> sp_getnonbilhist(NonBillingListingRequest req) {
          Query query = entityManager.createNativeQuery("CALL sp_getnonbilhist(:i_non_bil_id)")
                 .setParameter("i_non_bil_id", req.getI_non_bil_id());
 
          return query.getResultList();
 
     }

    //scheduler
    @Override
    public List<Object[]> sp_getnonbillreturnche(OTCReturnedChequeRequest getRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getnonbillreturnche(:i_mtt_id)")
                .setParameter("i_mtt_id", getRequest.getI_mtt_id());
                
        return query.getResultList();
    }

    @Override
    public List<Object> sp_getmttemaildtexpiry(OTCReturnedChequeRequest getRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getmttemaildtexpiry(:i_orn_no)")
                .setParameter("i_orn_no", getRequest.getI_orn_no());
                
        return query.getResultList();
    }

    @Override
    public Integer sp_updnonbillinsa(NBLInsRequest insRequest) {

        Integer result = 0;
        // Iterate over each OTC Payment Request in the list
            Query query = entityManager.createNativeQuery(
                    "CALL sp_updnonbillinsa(:i_non_bill_no, :i_created_by, :i_modified_by)");

            query.setParameter("i_non_bill_no", insRequest.getI_non_bil_no());
            query.setParameter("i_created_by", authService.getLoginUserName());
            query.setParameter("i_modified_by", authService.getLoginUserName());

            result = (Integer) query.getSingleResult();

        return result; // Return the accumulated total result
    }

    //////////////////////////////////////////
    @Override
    public Integer sp_updsp(ServiceProviderRequest insRequest) {

        Integer result = 0;
        // Iterate over each OTC Payment Request in the list
            Query query = entityManager.createNativeQuery(
                    "CALL sp_updsp(:i_ag_bil_no, :i_modified_by)");

            query.setParameter("i_ag_bil_no", insRequest.getI_ag_bil_no());
            query.setParameter("i_modified_by", authService.getLoginUserName());

            result = (Integer) query.getSingleResult();

        return result; // Return the accumulated total result
    }
    
    
}
