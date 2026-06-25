package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.ISPRepository;
import com.maven.rms.interfaces.SchRepositoryInterface;
import com.maven.rms.models.DailySettlementRequest;
import com.maven.rms.models.SchedulerCustReq;

@Repository
public class SPRepository implements ISPRepository{

    @PersistenceContext
    private EntityManager entityManager;



    @Override
    public String sp_insdailysettlement(DailySettlementRequest dailyRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_insdailysettlement(:i_profile_nm, :i_dt_collection, :i_total_amt, :i_daily_stmt_id)")
                .setParameter("i_profile_nm", dailyRequest.getProfile_nm())
                .setParameter("i_dt_collection", dailyRequest.getDt_collection())
                .setParameter("i_total_amt", dailyRequest.getTotal_amt())
                .setParameter("i_daily_stmt_id", dailyRequest.getDaily_stmnt_id());
                
        return (String) query.getSingleResult();
    }

    @Override
    public String sp_checksp(DailySettlementRequest dailyRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_checksp(:i_dt_collection, :i_daily_stmt_id)")
                .setParameter("i_dt_collection", dailyRequest.getDt_collection())
                .setParameter("i_daily_stmt_id", dailyRequest.getDaily_stmnt_id());
                
        return (String) query.getSingleResult();
    }

}
