package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IJPASchedulerInterface;
import com.maven.rms.models.JPASchedulerRequest;

@Repository
public class JPASchedulerRepository implements IJPASchedulerInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    // public List<Object[]> sp_getfpascheduler(String i_job_name) {
    public List<Object[]> sp_getfpascheduler(JPASchedulerRequest jpaSchedulerRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfpascheduler(:i_job_name)")
                .setParameter("i_job_name", jpaSchedulerRequest.getI_job_name());
                
        return query.getResultList();
    }

}
