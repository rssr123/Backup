package com.maven.rms.repositories;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.ICourtOrderInterface;
import com.maven.rms.interfaces.IServiceProviderInterface;
import com.maven.rms.models.CourtOrderRequest;
import com.maven.rms.models.ReprintRcptRequest;
import com.maven.rms.models.ServiceProviderRequest;

@Repository
public class ICourtOrderRepository implements ICourtOrderInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getcourtorderlisting(CourtOrderRequest courtOrderRequest) {
        Query query = entityManager.createNativeQuery(
    
                "CALL sp_getcourtorderlisting(:i_page, :i_size, :i_task_no, :i_pymt_status, :i_txn_ty, :i_attr_case_no)")
                .setParameter("i_page", courtOrderRequest.getI_page())
                .setParameter("i_size", courtOrderRequest.getI_size())
                .setParameter("i_task_no", courtOrderRequest.getI_task_no())
                .setParameter("i_pymt_status", courtOrderRequest.getI_pymt_status())
                .setParameter("i_txn_ty", courtOrderRequest.getI_txn_ty())
                .setParameter("i_attr_case_no", courtOrderRequest.getI_attr_case_no())

                ;
                
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getcreditcontrolcaseinfo(CourtOrderRequest courtOrderRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getcreditcontrolcaseinfo(:i_cc_case_id,:i_cc_case_a_id,:i_cc_cs_item_id)")
                .setParameter("i_cc_case_id", courtOrderRequest.getI_cc_case_id())
                .setParameter("i_cc_case_a_id", courtOrderRequest.getI_cc_case_a_id())
                .setParameter("i_cc_cs_item_id", courtOrderRequest.getI_cc_cs_item_id())
                ;

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getcourtorderpymtiteminfo(CourtOrderRequest courtOrderRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getcourtorderpymtiteminfo(:i_cc_case_id)")
                .setParameter("i_cc_case_id", courtOrderRequest.getI_cc_case_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getcourtorderrmdrinfo(CourtOrderRequest courtOrderRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getcourtorderrmdrinfo(:i_cc_case_id)")
                .setParameter("i_cc_case_id", courtOrderRequest.getI_cc_case_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getcourtorderdocs(CourtOrderRequest courtOrderRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getcourtorderdocs(:i_cc_case_id)")
                .setParameter("i_cc_case_id", courtOrderRequest.getI_cc_case_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getcourtorderhist(CourtOrderRequest courtOrderRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getcourtorderhist(:i_cc_case_id)")
                .setParameter("i_cc_case_id", courtOrderRequest.getI_cc_case_id());

        return query.getResultList();
    }



    //blob file
    public Blob sp_getcccasedocblob(Integer ccDocId) throws SQLException, IOException {
    	Query query = entityManager.createNativeQuery("CALL sp_getcccasedocblob(:i_cc_doc_id)")
                .setParameter("i_cc_doc_id", ccDocId);
        return (Blob) query.getSingleResult();
    }


    


    
}
