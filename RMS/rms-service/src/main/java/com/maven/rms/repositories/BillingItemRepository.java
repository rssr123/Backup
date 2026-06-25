package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IBillingItemRepository;
import com.maven.rms.models.BillingItemRequest;
import com.maven.rms.services.AuthService;
@Repository
public class BillingItemRepository implements IBillingItemRepository{
    @Autowired
    private AuthService authService;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> getBillingItem(BillingItemRequest billingItemRequest) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_getbilitem(:i_page, :i_size, :i_billing_no, :i_cust_email)")
            .setParameter("i_page", billingItemRequest.getI_page())
            .setParameter("i_size", billingItemRequest.getI_size())
            .setParameter("i_billing_no", billingItemRequest.getI_billing_no())
            .setParameter("i_cust_email", billingItemRequest.getI_cust_email());
            
        return query.getResultList();
    }
}
