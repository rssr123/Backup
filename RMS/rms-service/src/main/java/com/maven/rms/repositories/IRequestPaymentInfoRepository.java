package com.maven.rms.repositories;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

@Repository
public class IRequestPaymentInfoRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Object[]> sp_getrequestPaymentInfo(String ornNo) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getrequestPaymentInfo(:orn_no)")
                .setParameter("orn_no", ornNo);

        return query.getResultList();
    }
}