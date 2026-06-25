package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.INonBillingItemRepository;
import com.maven.rms.models.NonBillingItemRequest;
import com.maven.rms.services.AuthService;

@Repository
public class NonBillingItemRepository implements INonBillingItemRepository {
    
    // @Autowired
    // private AuthService authService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> getNonBillingItem(NonBillingItemRequest nonBillingItemRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_getnonbilitem(:i_page, :i_size, :i_non_bil_no, :i_cust_email)")
            .setParameter("i_page", nonBillingItemRequest.getI_page())
            .setParameter("i_size", nonBillingItemRequest.getI_size())
            .setParameter("i_non_bil_no", nonBillingItemRequest.getI_non_bil_no())
            .setParameter("i_cust_email", nonBillingItemRequest.getI_cust_email());

        return query.getResultList();
    }
}
