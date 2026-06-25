package com.maven.rms.repositories;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IRILTInterface;
import com.maven.rms.models.RILTRequest;
import com.maven.rms.models.RILTRequest2;

@Repository
public class RILTRepository implements IRILTInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public BigInteger sp_insRILT(RILTRequest request) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_insrilt(:i_lit_no, :i_lit_item_ref, :i_lit_amount, :i_entity_type, :i_entity_no, :i_dt_due)")
            .setParameter("i_lit_no", request.getLit_no())
            .setParameter("i_lit_item_ref", request.getLit_item_ref())
            .setParameter("i_lit_amount", request.getLit_amount())
            .setParameter("i_entity_type", request.getEntity_type())
            .setParameter("i_entity_no", request.getEntity_no())
            .setParameter("i_dt_due", request.getDt_due());
        
        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
    }

    @Override
    public BigInteger sp_delRILT(RILTRequest request) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_delrilt(:i_lit_no, :i_lit_item_ref)")
            .setParameter("i_lit_no", request.getLit_no())
            .setParameter("i_lit_item_ref", request.getLit_item_ref());

        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
    }

    @Override
    public List<Object[]> sp_getRILT(RILTRequest2 request) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getrilt(:i_page, :i_size, :i_rilt_id, :i_lit_no, :i_lit_item_ref, :i_lit_amount, :i_entity_type, :i_entity_no, :i_dt_due_fr, :i_dt_due_to, :i_dt_created_fr, :i_dt_created_to, :i_dt_modified_fr, :i_dt_modified_to, :i_status)")
                .setParameter("i_page", request.getI_page())
                .setParameter("i_size", request.getI_size())
                .setParameter("i_rilt_id", request.getI_rilt_id())
                .setParameter("i_lit_no", request.getI_lit_no())
                .setParameter("i_lit_item_ref", request.getI_lit_item_ref())
                .setParameter("i_lit_amount", request.getI_lit_amount())
                .setParameter("i_entity_type", request.getI_entity_type())
                .setParameter("i_entity_no", request.getI_entity_no())
                .setParameter("i_dt_due_fr", request.getI_dt_due_fr() != null ? request.getI_dt_due_fr() : null)
                .setParameter("i_dt_due_to", request.getI_dt_due_to() != null ? request.getI_dt_due_to() : null)
                .setParameter("i_dt_created_fr", request.getI_dt_created_fr() != null ? request.getI_dt_created_fr() : null)
                .setParameter("i_dt_created_to", request.getI_dt_created_to() != null ? request.getI_dt_created_to() : null)
                .setParameter("i_dt_modified_fr", request.getI_dt_modified_fr() != null ? request.getI_dt_modified_fr() : null)
                .setParameter("i_dt_modified_to", request.getI_dt_modified_to() != null ? request.getI_dt_modified_to() : null)
                .setParameter("i_status", request.getI_status());

            if (request.getI_dt_due_fr() != null) {
                query.setParameter("i_dt_due_fr", request.getI_dt_due_fr());
            } else {
                query.setParameter("i_dt_due_fr", null);
            }
    
            if (request.getI_dt_due_to() != null) {
                query.setParameter("i_dt_due_to", request.getI_dt_due_to());
            } else {
                query.setParameter("i_dt_due_to", null);
            }
    
            if (request.getI_dt_created_fr() != null) {
                query.setParameter("i_dt_created_fr", request.getI_dt_created_fr());
            } else {
                query.setParameter("i_dt_created_fr", null);
            }
    
            if (request.getI_dt_created_to() != null) {
                query.setParameter("i_dt_created_to", request.getI_dt_created_to());
            } else {
                query.setParameter("i_dt_created_to", null);
            }
            
            if (request.getI_dt_modified_fr() != null) {
                query.setParameter("i_dt_modified_fr", request.getI_dt_modified_fr());
            } else {
                query.setParameter("i_dt_modified_fr", null);
            }
    
            if (request.getI_dt_modified_to() != null) {
                query.setParameter("i_dt_modified_to", request.getI_dt_modified_to());
            } else {
                query.setParameter("i_dt_modified_to", null);
            }

        return query.getResultList();
    }

    @Override
    public BigInteger sp_updRILT(RILTRequest request) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_updrilt(:i_lit_item_ref)")
            .setParameter("i_lit_item_ref", request.getLit_item_ref());

        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
    }
    
}
