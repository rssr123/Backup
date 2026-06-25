package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IBankReconSchInterface;
import com.maven.rms.interfaces.IBillingIssuanceBySSInterface;
import com.maven.rms.models.BillingIssuanceBySBillingDocRequest;
import com.maven.rms.models.BillingIssuanceBySSBilCustomerRequest;
import com.maven.rms.models.BillingIssuanceBySSBilStatusRequest;
import com.maven.rms.models.BillingIssuanceBySSBillingChildDetails;
import com.maven.rms.models.BillingIssuanceBySSBillingDetailsRequest;
import com.maven.rms.models.BillingIssuanceBySSBillingItemDetails;
import com.maven.rms.models.BillingIssuanceBySSBillingMethod;
import com.maven.rms.models.BillingIssuanceBySSListing;
import com.maven.rms.models.BillingIssuanceBySSListingRequest;
import com.maven.rms.models.BillingIssuanceBySSRunnoRequest;
import com.maven.rms.models.BillingTypeCodeRequest;
import com.maven.rms.models.MFTWFDocRequest;
import com.maven.rms.models.OTCReceiptCancellationListingRequest;
import com.maven.rms.models.OTCReceiptCancellationRequest;
import com.maven.rms.models.PaymentItemDetails;
import com.maven.rms.models.PaymentRequest;
import com.maven.rms.models.SubmitBillingRequest;

@Repository
public class BillingIssuanceBySSRepository implements IBillingIssuanceBySSInterface{
    


    @PersistenceContext
    private EntityManager entityManager;
    @Value("${rms.application.emailExpiryInDay}")
	private Integer emailExpiryDay;

    

    @Override
    public List<Object[]> sp_getbibssbiltypecode( BillingTypeCodeRequest billingTypeCodeRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getbibssbiltypecode(:i_page, :i_size, :i_bt_ty, :i_class_id, :i_ss_cd)")
                .setParameter("i_page", billingTypeCodeRequest.getI_page())
                .setParameter("i_size", billingTypeCodeRequest.getI_size())
                .setParameter("i_bt_ty", billingTypeCodeRequest.getI_bt_ty())
                .setParameter("i_class_id", billingTypeCodeRequest.getI_class_id())
                .setParameter("i_ss_cd", billingTypeCodeRequest.getI_ss_cd());

        return query.getResultList();
    }


    @Override
    public Integer sp_insbilissbyssbilcust(BillingIssuanceBySSBilCustomerRequest bilCustRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_insbilissbyssbilcust(:i_cust_id, :i_cust_nm, :i_cust_email, :i_cust_phone, :i_cust_addr1, :i_cust_addr2, :i_cust_addr3, :i_cust_postcode, :i_cust_city, :i_cust_state, :i_ent_nm, :i_ent_no, :i_ent_ty, :i_created_by, :i_modified_by, :i_status, :i_bltc_id, :i_req_name, :i_req_email, :i_ss_cd, :i_billing_no, :i_billing_desc, :i_action, :i_dps_amt, :i_billing_cnt, :i_billing_freq, :i_loa_id, :i_dt_loa_start, :i_dt_loa_end, :i_agm_id, :i_dt_agm_start, :i_dt_agm_end, :i_bil_wf_status, :i_pickup_by, :i_dt_pick, :i_billing_mthd, :i_msg, :i_msg_type)")
                .setParameter("i_cust_id", bilCustRequest.getI_cust_id())
                .setParameter("i_cust_nm", bilCustRequest.getI_cust_nm())
                .setParameter("i_cust_email", bilCustRequest.getI_cust_email())
                .setParameter("i_cust_phone", bilCustRequest.getI_cust_phone())
                .setParameter("i_cust_addr1", bilCustRequest.getI_cust_addr1())
                .setParameter("i_cust_addr2", bilCustRequest.getI_cust_addr2())
                .setParameter("i_cust_addr3", bilCustRequest.getI_cust_addr3())
                .setParameter("i_cust_postcode", bilCustRequest.getI_cust_postcode())
                .setParameter("i_cust_city", bilCustRequest.getI_cust_city())
                .setParameter("i_cust_state", bilCustRequest.getI_cust_state())
                .setParameter("i_ent_nm", bilCustRequest.getI_ent_nm())
                .setParameter("i_ent_no", bilCustRequest.getI_ent_no())
                .setParameter("i_ent_ty", bilCustRequest.getI_ent_ty())
                .setParameter("i_created_by", bilCustRequest.getI_created_by())
                .setParameter("i_modified_by", bilCustRequest.getI_modified_by())
                .setParameter("i_status", bilCustRequest.getI_status() != null ? bilCustRequest.getI_status() : null)
                .setParameter("i_bltc_id", bilCustRequest.getI_bltc_id() != null ? bilCustRequest.getI_bltc_id() : null)
                .setParameter("i_req_name", bilCustRequest.getI_req_name() != null ? bilCustRequest.getI_req_name() : null)
                .setParameter("i_req_email", bilCustRequest.getI_req_email() != null ? bilCustRequest.getI_req_email() : null)
                .setParameter("i_ss_cd", bilCustRequest.getI_ss_cd() != null ? bilCustRequest.getI_ss_cd() : null)
                .setParameter("i_billing_no", bilCustRequest.getI_billing_no() != null ? bilCustRequest.getI_billing_no() : null)
                .setParameter("i_billing_desc", bilCustRequest.getI_billing_desc() != null ? bilCustRequest.getI_billing_desc() : null)
                .setParameter("i_action", bilCustRequest.getI_action() != null ? bilCustRequest.getI_action() : null)
                .setParameter("i_dps_amt", bilCustRequest.getI_dps_amt() != null ? bilCustRequest.getI_dps_amt() : null)
                .setParameter("i_billing_cnt", bilCustRequest.getI_billing_cnt() != null ? bilCustRequest.getI_billing_cnt() : null)
                .setParameter("i_billing_freq", bilCustRequest.getI_billing_freq() != null ? bilCustRequest.getI_billing_freq() : null)
                .setParameter("i_loa_id", bilCustRequest.getI_loa_id() != null ? bilCustRequest.getI_loa_id() : null)
                .setParameter("i_dt_loa_start", bilCustRequest.getI_dt_loa_start() != null ? bilCustRequest.getI_dt_loa_start() : null)
                .setParameter("i_dt_loa_end", bilCustRequest.getI_dt_loa_end() != null ? bilCustRequest.getI_dt_loa_end() : null)
                .setParameter("i_agm_id", bilCustRequest.getI_agm_id() != null ? bilCustRequest.getI_agm_id() : null)
                .setParameter("i_dt_agm_start", bilCustRequest.getI_dt_agm_start() != null ? bilCustRequest.getI_dt_agm_start() : null)
                .setParameter("i_dt_agm_end", bilCustRequest.getI_dt_agm_end() != null ? bilCustRequest.getI_dt_agm_end() : null)
                .setParameter("i_bil_wf_status", bilCustRequest.getI_bil_wf_status() != null ? bilCustRequest.getI_bil_wf_status() : null)
                .setParameter("i_pickup_by", bilCustRequest.getI_pickup_by() != null ? bilCustRequest.getI_pickup_by() : null)
                .setParameter("i_dt_pick", bilCustRequest.getI_dt_pick() != null ? bilCustRequest.getI_dt_pick() : null)
                .setParameter("i_billing_mthd", bilCustRequest.getI_billing_mthd() != null ? bilCustRequest.getI_billing_mthd() : null)
                .setParameter("i_msg", bilCustRequest.getI_msg())
                .setParameter("i_msg_type", bilCustRequest.getI_msg_type());
        Integer result = (Integer) query.getSingleResult();
        return result;
    }


    @Override
    public Integer sp_insbilissbyssbilitem(BillingIssuanceBySSBillingItemDetails bilItemDets, Integer bilId ,String username) {
        Query query = entityManager.createNativeQuery("CALL sp_insbilissbyssbilitem(:i_bil_id, :i_mft_pk, :i_unit_fee, :i_qty, :i_tax_pct, :i_tax_amt, :i_final_amt, :i_created_by, :i_modified_by, :i_status, :i_bil_wf_id)")
                .setParameter("i_bil_id", bilId)
                .setParameter("i_mft_pk", bilItemDets.getMft_pk())
                .setParameter("i_unit_fee", bilItemDets.getUnit_fee())
                .setParameter("i_qty", bilItemDets.getQty())
                .setParameter("i_tax_pct", bilItemDets.getTax_pct())
                .setParameter("i_tax_amt", bilItemDets.getTax_amt())
                .setParameter("i_final_amt", bilItemDets.getFinal_amt())
                .setParameter("i_created_by", username)
                .setParameter("i_modified_by", username)
                .setParameter("i_status", bilItemDets.getStatus())
                .setParameter("i_bil_wf_id", bilItemDets.getBil_wf_id());
  
        Integer result = (Integer) query.getSingleResult();
        return result;
    }


    // @Override
    // public Integer sp_insbilissbyssbilchild(BillingIssuanceBySSBillingChildDetails bilChildDets, Integer bilId ,String username) {
    //     Query query = entityManager.createNativeQuery("CALL sp_insbilissbyssbilchild(:i_bil_id, :i_bil_child_date, :i_bil_child_status, :i_created_by, :i_modified_by, :i_status, :i_bil_wf_id, :i_bil_no, :i_bil_status)")
    //             .setParameter("i_bil_id", bilId)
    //             .setParameter("i_bil_child_date", bilChildDets.getBil_child_date())
    //             .setParameter("i_bil_child_status", bilChildDets.getBil_child_status())
    //             .setParameter("i_created_by", username)
    //             .setParameter("i_modified_by", username)
    //             .setParameter("i_status", bilChildDets.getStatus() != null ? bilChildDets.getStatus() : null)
    //             .setParameter("i_bil_wf_id", bilChildDets.getBil_wf_id() != null ? bilChildDets.getBil_wf_id() : null)
    //             .setParameter("i_bil_no", bilChildDets.getBil_no() != null ? bilChildDets.getBil_no() : null)
    //             .setParameter("i_bil_status", bilChildDets.getBil_status() != null ? bilChildDets.getBil_status() : null);
         
  
    //     Integer result = (Integer) query.getSingleResult();
    //     return result;
    // }

    @Override
    public Integer sp_insbilissbyssbilchild(BillingIssuanceBySSBillingChildDetails bilChildDets, Integer bilId, String username) {
   
    Date originalDate = bilChildDets.getBil_child_date();
    LocalDate localDate = originalDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate(); 

    // Get the current local time
    LocalTime currentTime = LocalTime.now(ZoneId.systemDefault());
    LocalDateTime combinedDateTime = LocalDateTime.of(localDate, currentTime);
    Date billChildDate = Date.from(combinedDateTime.atZone(ZoneId.systemDefault()).toInstant());

    Query query = entityManager.createNativeQuery(
            "CALL sp_insbilissbyssbilchild(:i_bil_id, :i_bil_child_date, :i_bil_child_status, :i_created_by, :i_modified_by, :i_status, :i_bil_wf_id, :i_bil_no, :i_bil_status)")
            .setParameter("i_bil_id", bilId)
            .setParameter("i_bil_child_date", billChildDate) // Use same date with current time
            .setParameter("i_bil_child_status", bilChildDets.getBil_child_status())
            .setParameter("i_created_by", username)
            .setParameter("i_modified_by", username)
            .setParameter("i_status", bilChildDets.getStatus())
            .setParameter("i_bil_wf_id", bilChildDets.getBil_wf_id())
            .setParameter("i_bil_no", bilChildDets.getBil_no())
            .setParameter("i_bil_status", bilChildDets.getBil_status());

    Integer result = (Integer) query.getSingleResult();
    return result;
}

    @Override
    public String sp_getbibssrunno() {
        Query query = entityManager.createNativeQuery("CALL sp_getbibssrunningno()");
        return (String) query.getSingleResult();

    }


    @Override
    public String sp_getandreservebillrunno(BillingIssuanceBySSRunnoRequest runnoRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getandreservebillrunno(:i_number_to_reserve)")
                .setParameter("i_number_to_reserve", runnoRequest.getI_number_to_reserve());
        return (String) query.getSingleResult();

    }


    @Override
    public String sp_getbilstatus(BillingIssuanceBySSBilStatusRequest bilStatusRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getbilstatus(:i_billing_no)")
                .setParameter("i_billing_no", bilStatusRequest.getI_billing_no());
        return (String) query.getSingleResult();

    }


    @Override
    public List<Object[]> sp_getbibsspaymentdetails(BillingIssuanceBySSBilStatusRequest bilStatusRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getbibsspaymentdetails(:i_billing_no)")
               .setParameter("i_billing_no", bilStatusRequest.getI_billing_no());

        return query.getResultList();
    }


     @Override
    public Integer sp_uploadDoc(BillingIssuanceBySBillingDocRequest bilDocRequest, Blob blob) {
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insbilissbildoc");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_bil_id", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_nm", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_content", Blob.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_type", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_size", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_category", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_created_by", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_modified_by", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_status", String.class, javax.persistence.ParameterMode.IN);

        // Set parameters
        storedProcedureQuery.setParameter("i_bil_id", bilDocRequest.getI_bil_id() != null ? bilDocRequest.getI_bil_id() : null);
        storedProcedureQuery.setParameter("i_file_nm", bilDocRequest.getI_file_nm());
        storedProcedureQuery.setParameter("i_file_content", blob);
        storedProcedureQuery.setParameter("i_file_type", bilDocRequest.getI_file_type());
        storedProcedureQuery.setParameter("i_file_size", bilDocRequest.getI_file_size());
        storedProcedureQuery.setParameter("i_file_category", bilDocRequest.getI_file_category());
        storedProcedureQuery.setParameter("i_created_by", bilDocRequest.getI_created_by());
        storedProcedureQuery.setParameter("i_modified_by", bilDocRequest.getI_modified_by());
        storedProcedureQuery.setParameter("i_status", bilDocRequest.getI_status());  
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

    public List<Object[]> sp_getMFTWFByStatusAndEffDate(String status) {
        Query query = entityManager.createNativeQuery("CALL sp_getMFTWFByStatusAndEffDate(:i_status)")
                .setParameter("i_status", status);
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getbibsslisting(BillingIssuanceBySSListingRequest billingListingRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getbibsslisting(:i_page, :i_size, :i_ent_nm, :i_ent_no, :i_ss_cd, :i_rcpt_no, :i_billing_mthd, :i_bil_wf_status, :i_dt_created_fr, :i_dt_created_to, :i_bt_ty, :i_billing_no)")
                .setParameter("i_page", billingListingRequest.getI_page())
                .setParameter("i_size", billingListingRequest.getI_size())
                .setParameter("i_ent_nm", billingListingRequest.getI_ent_nm() != null ? billingListingRequest.getI_ent_nm() : null)
                .setParameter("i_ent_no", billingListingRequest.getI_ent_no() != null ? billingListingRequest.getI_ent_no() : null)
                .setParameter("i_ss_cd", billingListingRequest.getI_ss_cd() != null ? billingListingRequest.getI_ss_cd() : null)
                .setParameter("i_rcpt_no", billingListingRequest.getI_rcpt_no() != null ? billingListingRequest.getI_rcpt_no() : null)
                .setParameter("i_billing_mthd", billingListingRequest.getI_billing_mthd() != null ? billingListingRequest.getI_billing_mthd() : null)
                .setParameter("i_bil_wf_status", billingListingRequest.getI_bil_wf_status() != null ? billingListingRequest.getI_bil_wf_status() : null)
                .setParameter("i_dt_created_fr", billingListingRequest.getI_dt_created_fr() != null ? billingListingRequest.getI_dt_created_fr() : null)
                .setParameter("i_dt_created_to", billingListingRequest.getI_dt_created_to() != null ? billingListingRequest.getI_dt_created_to() : null)
                .setParameter("i_bt_ty", billingListingRequest.getI_bt_ty() != null ? billingListingRequest.getI_bt_ty() : null)
                .setParameter("i_billing_no", billingListingRequest.getI_billing_no() != null ? billingListingRequest.getI_billing_no() : null);
 
        return query.getResultList();
    }


    @Override
    public List<Object[]> sp_getbibssbillingdetails(BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getbibssbillingdetails(:i_bil_id)")
                .setParameter("i_bil_id", bilDetailsRequest.getI_bil_id());
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getbibsslistofbillingitems(BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getbibsslistofbillingitems(:i_bil_id)")
                .setParameter("i_bil_id", bilDetailsRequest.getI_bil_id());
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getbibsslistofbillingissuance(BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getbibsslistofbillingissuance(:i_bil_id)")
                .setParameter("i_bil_id", bilDetailsRequest.getI_bil_id());
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getbibsslistofdoc(BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getbibsslistofdoc(:i_bil_id)")
                .setParameter("i_bil_id", bilDetailsRequest.getI_bil_id());
        return query.getResultList();
    }

    @Override
    public Blob sp_getbibssdocfilecontent(BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getbibssdocfilecontent(:i_bil_id)")
                .setParameter("i_bil_id", bilDetailsRequest.getI_bil_id());

        return (Blob) query.getSingleResult();
    }


    @Override
    public List<Object[]> sp_getbibsshistory(BillingIssuanceBySSBillingDetailsRequest bilDetailsRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getbibsshistory(:i_bil_id)")
                .setParameter("i_bil_id", bilDetailsRequest.getI_bil_id());
        return query.getResultList();
    }


    public Integer sp_confirmnewbill(String billingNo, String username) {
        Query query = entityManager.createNativeQuery("CALL sp_confirmnewbill(:i_billing_no, :i_user, :i_expiry)")
                .setParameter("i_billing_no", billingNo)
                .setParameter("i_user", username)
                .setParameter("i_expiry", LocalDateTime.now().plusDays(emailExpiryDay.longValue()));

            System.out.println("Expiry Date: " + LocalDateTime.now().plusDays(emailExpiryDay.longValue()));
        return (Integer)query.getSingleResult();
}


public Object[] sp_getbibssbillingmethod(String billingNo) {
    Query query = entityManager.createNativeQuery("CALL sp_getbibssbillingmethod(:i_bil_no)")
            .setParameter("i_bil_no", billingNo);
            return (Object[]) query.getSingleResult(); // Ensure only one row is returned
}

public List<Object[]> sp_callbacksubmitbilling(SubmitBillingRequest submitBillingRequest) {
    Query query = entityManager.createNativeQuery("CALL sp_callbacksubmitbilling(:i_billing_no)")
            .setParameter("i_billing_no", submitBillingRequest.getI_billing_no());
    return query.getResultList();
}

@Override
public Integer sp_removebilissbyss(Integer bilId) {
    Query query = entityManager.createNativeQuery("CALL sp_removebilissbyss(:i_bil_id)")
            .setParameter("i_bil_id", bilId);

    Integer result = (Integer) query.getSingleResult();
    return result;
}












//      // @Override
//      public List<Object[]> sp_getbibssbillingitemdetails(BillingIssuanceBySSBilItemDetsRequest billingItemDetsRequest) {
//         Query query = entityManager.createNativeQuery("CALL sp_getbibssbillingitemdetails(:i_bltc_id)")
//                 .setParameter("i_bltc_id", billingItemDetsRequest.getI_bltc_id());

//         return query.getResultList();
//     }







}
