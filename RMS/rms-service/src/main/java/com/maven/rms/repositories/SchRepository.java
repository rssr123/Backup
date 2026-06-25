package com.maven.rms.repositories;

import java.util.List;

import com.maven.rms.interfaces.SchRepositoryInterface;
import com.maven.rms.models.SchedulerCustReq;
import com.maven.rms.models.SchedulerUpdRequest;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

@Repository
public class SchRepository implements SchRepositoryInterface {

     @PersistenceContext
     private EntityManager entityManager;

     @Override
     public List<Object[]> sp_getschseq() {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getschseq()");
          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getschind() {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getschind()");
          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getschcustom(SchedulerCustReq schedulerCustReq) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getschcustom(:i_start_job, :i_end_job)")
                    .setParameter("i_start_job", schedulerCustReq.getI_start_job())
                    .setParameter("i_end_job", schedulerCustReq.getI_end_job());
          return query.getResultList();
     }

     @Override
     public Integer sp_updschstatus(SchedulerUpdRequest schedulerUpdRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_updschstatus(:i_chain_group, :i_sch_status)")
                    .setParameter("i_chain_group", schedulerUpdRequest.getI_chain_group())
                    .setParameter("i_sch_status", schedulerUpdRequest.getI_sch_status());
          Integer result = (Integer) query.getSingleResult();
          return result;
     }

     @Override
     public Integer sp_updjobstatus(SchedulerUpdRequest schedulerUpdRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_updjobstatus(:i_function_nm, :i_sch_status)")
                    .setParameter("i_function_nm", schedulerUpdRequest.getI_function_nm())
                    .setParameter("i_sch_status", schedulerUpdRequest.getI_sch_status());
          Integer result = (Integer) query.getSingleResult();
          return result;
     }

     @Override
     public Integer sp_upderrorjobs(SchedulerUpdRequest schedulerUpdRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_upderrorjobs(:i_function_nm)")
                    .setParameter("i_function_nm", schedulerUpdRequest.getI_function_nm());
          Integer result = (Integer) query.getSingleResult();
          return result;
     }

     @Override
     public String sp_getjobstatus(SchedulerUpdRequest schedulerUpdRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getjobstatus(:i_function_nm)")
                    .setParameter("i_function_nm", schedulerUpdRequest.getI_function_nm());
          String result = (String) query.getSingleResult();
          return result;
     }

     @Override
     public List<Object[]> sp_getchaingroup(SchedulerCustReq schedulerCustRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getchaingroup(:i_chain_name)")
                    .setParameter("i_chain_name", schedulerCustRequest.getI_chain_name());
          return query.getResultList();
     }
}
