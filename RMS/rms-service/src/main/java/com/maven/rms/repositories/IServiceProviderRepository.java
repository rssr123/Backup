package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IServiceProviderInterface;
import com.maven.rms.models.ServiceProviderProfileRequest;
import com.maven.rms.models.ServiceProviderRequest;
import com.maven.rms.models.TaxCdRequest;
import com.maven.rms.models.OTC.OTCReturnedChequeRequest;

@Repository
public class IServiceProviderRepository implements IServiceProviderInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getserviceproviderpayment(ServiceProviderRequest serviceProviderRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getserviceproviderpayment(:i_page, :i_size, :i_profile_nm, :i_ag_bil_no, :i_cust_email, :i_total_amt_payable, :i_date_collection_fr, :i_date_collection_to, :i_pymt_status, :i_dt_pymt_fr, :i_dt_pymt_to, :i_date_email_sent_fr, :i_date_email_sent_to)")
                .setParameter("i_page", serviceProviderRequest.getI_page())
                .setParameter("i_size", serviceProviderRequest.getI_size())
                .setParameter("i_profile_nm", serviceProviderRequest.getI_profile_nm())
                .setParameter("i_ag_bil_no", serviceProviderRequest.getI_ag_bil_no())
                .setParameter("i_cust_email", serviceProviderRequest.getI_cust_email())
                .setParameter("i_total_amt_payable", serviceProviderRequest.getI_total_amt_payable())
                .setParameter("i_date_collection_fr", serviceProviderRequest.getI_date_collection_fr())
                .setParameter("i_date_collection_to", serviceProviderRequest.getI_date_collection_to())
                .setParameter("i_pymt_status", serviceProviderRequest.getI_pymt_status())
                .setParameter("i_dt_pymt_fr", serviceProviderRequest.getI_dt_pymt_fr())
                .setParameter("i_dt_pymt_to", serviceProviderRequest.getI_dt_pymt_to())
                .setParameter("i_date_email_sent_fr", serviceProviderRequest.getI_date_email_sent_fr())
                .setParameter("i_date_email_sent_to", serviceProviderRequest.getI_date_email_sent_to())

        ;

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getserviceprovideremail(ServiceProviderRequest serviceProviderRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getserviceprovideremail(:i_ag_bil)")
                .setParameter("i_ag_bil", serviceProviderRequest.getI_ag_bil())

        ;

        return query.getResultList();
    }

    @Override

    public List<Object[]> sp_getserviceprovidermaintenance(
            ServiceProviderProfileRequest serviceProviderProfileRequest) {

        Query query = entityManager.createNativeQuery(

                "call sp_getserviceprovidermaintenance(:i_page, :i_size, :i_profile_nm, :i_cust_nm, :i_cust_postcode, :i_cust_city, :i_cust_state, :i_cust_email, :i_cust_phone, :i_fee_detail_id, :i_entity_type, :i_entity_no, :i_entity_nm, :i_status)")
                .setParameter("i_page", serviceProviderProfileRequest.getI_page())
                .setParameter("i_size", serviceProviderProfileRequest.getI_size())
                .setParameter("i_profile_nm", serviceProviderProfileRequest.getI_profile_nm())
                .setParameter("i_cust_nm", serviceProviderProfileRequest.getI_cust_nm())
                .setParameter("i_cust_postcode", serviceProviderProfileRequest.getI_cust_postcode())
                .setParameter("i_cust_city", serviceProviderProfileRequest.getI_cust_city())
                .setParameter("i_cust_state", serviceProviderProfileRequest.getI_cust_state())
                .setParameter("i_cust_email", serviceProviderProfileRequest.getI_cust_email())
                .setParameter("i_cust_phone", serviceProviderProfileRequest.getI_cust_phone())
                .setParameter("i_fee_detail_id", serviceProviderProfileRequest.getI_fee_detail_id())
                .setParameter("i_entity_type", serviceProviderProfileRequest.getI_entity_type())
                .setParameter("i_entity_no", serviceProviderProfileRequest.getI_entity_no())
                .setParameter("i_entity_nm", serviceProviderProfileRequest.getI_entity_nm())
                .setParameter("i_status", serviceProviderProfileRequest.getI_status());

        return query.getResultList();
    }

    @Override
    public Integer sp_insserviceprovidermaintenance(ServiceProviderProfileRequest serviceProviderProfileRequest) {
        Query query = entityManager.createNativeQuery(
            
        
        "CALL sp_insserviceprovidermaintenance(:i_profile_nm, :i_cust_nm, :i_cust_addr_1, :i_cust_addr_2, :i_cust_addr_3, :i_cust_postcode, :i_cust_city, :i_cust_state, :i_cust_email, :i_cust_phone, :i_fee_detail_id, :i_entity_type, :i_entity_no, :i_entity_nm, :i_status, :i_created_by, :i_modified_by)")
                .setParameter("i_profile_nm", serviceProviderProfileRequest.getI_profile_nm())
                .setParameter("i_cust_nm", serviceProviderProfileRequest.getI_cust_nm())
                .setParameter("i_cust_addr_1", serviceProviderProfileRequest.getI_cust_addr_1())
                .setParameter("i_cust_addr_2", serviceProviderProfileRequest.getI_cust_addr_2())
                .setParameter("i_cust_addr_3", serviceProviderProfileRequest.getI_cust_addr_3())
                .setParameter("i_cust_postcode", serviceProviderProfileRequest.getI_cust_postcode())
                .setParameter("i_cust_city", serviceProviderProfileRequest.getI_cust_city())
                .setParameter("i_cust_state", serviceProviderProfileRequest.getI_cust_state())
                .setParameter("i_cust_email", serviceProviderProfileRequest.getI_cust_email())
                .setParameter("i_cust_phone", serviceProviderProfileRequest.getI_cust_phone())
                .setParameter("i_fee_detail_id", serviceProviderProfileRequest.getI_fee_detail_id())
                .setParameter("i_entity_type", serviceProviderProfileRequest.getI_entity_type())
                .setParameter("i_entity_no", serviceProviderProfileRequest.getI_entity_no())
                .setParameter("i_entity_nm", serviceProviderProfileRequest.getI_entity_nm())
                .setParameter("i_status", serviceProviderProfileRequest.getI_status())
                .setParameter("i_created_by", serviceProviderProfileRequest.getI_created_by())
                .setParameter("i_modified_by", serviceProviderProfileRequest.getI_modified_by());
                
        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_updserviceprovidermaintenance(ServiceProviderProfileRequest serviceProviderProfileRequest) {
        Query query = entityManager.createNativeQuery(
     

                "CALL sp_updserviceprovidermaintenance(:i_ag_pf_id, :i_profile_nm, :i_cust_nm, :i_cust_addr_1, :i_cust_addr_2, :i_cust_addr_3, :i_cust_postcode, :i_cust_city, :i_cust_state, :i_cust_email, :i_cust_phone, :i_fee_detail_id, :i_entity_type, :i_entity_no, :i_entity_nm, :i_status, :i_modified_by)")

                .setParameter("i_ag_pf_id", serviceProviderProfileRequest.getI_ag_pf_id())
                .setParameter("i_profile_nm", serviceProviderProfileRequest.getI_profile_nm())
                .setParameter("i_cust_nm", serviceProviderProfileRequest.getI_cust_nm())
                .setParameter("i_cust_addr_1", serviceProviderProfileRequest.getI_cust_addr_1())
                .setParameter("i_cust_addr_2", serviceProviderProfileRequest.getI_cust_addr_2())
                .setParameter("i_cust_addr_3", serviceProviderProfileRequest.getI_cust_addr_3())
                .setParameter("i_cust_postcode", serviceProviderProfileRequest.getI_cust_postcode())
                .setParameter("i_cust_city", serviceProviderProfileRequest.getI_cust_city())
                .setParameter("i_cust_state", serviceProviderProfileRequest.getI_cust_state())
                .setParameter("i_cust_email", serviceProviderProfileRequest.getI_cust_email())
                .setParameter("i_cust_phone", serviceProviderProfileRequest.getI_cust_phone())
                .setParameter("i_fee_detail_id", serviceProviderProfileRequest.getI_fee_detail_id())
                .setParameter("i_entity_type", serviceProviderProfileRequest.getI_entity_type())
                .setParameter("i_entity_no", serviceProviderProfileRequest.getI_entity_no())
                .setParameter("i_entity_nm", serviceProviderProfileRequest.getI_entity_nm())
                .setParameter("i_status", serviceProviderProfileRequest.getI_status())
                .setParameter("i_modified_by", serviceProviderProfileRequest.getI_modified_by());
                
                
        Integer result = (Integer) query.getSingleResult();
        return result;
    }


    @Override
    public Integer sp_delserviceprovidermaintenance(ServiceProviderProfileRequest serviceProviderProfileRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_delserviceprovidermaintenance(:i_ag_pf_id, :i_modified_by, :i_status)")

                .setParameter("i_ag_pf_id", serviceProviderProfileRequest.getI_ag_pf_id())
                .setParameter("i_modified_by", serviceProviderProfileRequest.getI_modified_by())
                .setParameter("i_status", serviceProviderProfileRequest.getI_status());
        Integer result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public List<Object[]> sp_getserviceprovideremailmtt(ServiceProviderRequest serviceProviderRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getserviceprovideremailmtt(:i_mtt_id)")
                .setParameter("i_mtt_id", serviceProviderRequest.getI_mtt_id());
                
        return query.getResultList();
    }

    @Override
    public Integer sp_updserviceproviderdatepayment(ServiceProviderRequest serviceProviderRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_updspdtpymt(:i_ag_bil)")

                .setParameter("i_ag_bil", serviceProviderRequest.getI_ag_bil());
        Integer result = (Integer) query.getSingleResult();
        return result;
    }

}
