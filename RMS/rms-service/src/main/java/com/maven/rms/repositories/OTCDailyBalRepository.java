package com.maven.rms.repositories;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Repository;
import com.maven.rms.interfaces.IOTCDailyBalInterface;
import com.maven.rms.models.OTCBalancingRequest;
import com.maven.rms.models.OTCDailyBal;

@Repository
public class OTCDailyBalRepository implements IOTCDailyBalInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<String> sp_getotcbranchcode(String ssm4uuserrefno) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcbranchcode(:i_ssm4uuserrefno)")
                .setParameter("i_ssm4uuserrefno", ssm4uuserrefno);

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcdailyballist(OTCDailyBal bodyRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcdailyballist(:i_branch_code, :i_bal_date)")
                .setParameter("i_branch_code", bodyRequest.getBranch_code())
                .setParameter("i_bal_date", bodyRequest.getBal_date());

        return query.getResultList();
    }

    @Override
    public Integer sp_checkotcdailybalval(OTCDailyBal bodyRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_checkotcdailybalval(:i_branch_code, :i_bal_date)")
                .setParameter("i_branch_code", bodyRequest.getBranch_code())
                .setParameter("i_bal_date", bodyRequest.getBal_date());

        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_updotcdailybalstatus(OTCBalancingRequest bodyRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_updotcdailybalstatus(:i_branch_code, :i_bal_date, :i_bal_status, :i_bal_type, :i_ssm4uuserrefno)")
                .setParameter("i_branch_code", bodyRequest.getBranch_code())
                .setParameter("i_bal_date", bodyRequest.getBal_date())
                .setParameter("i_bal_status", bodyRequest.getBal_status())
                .setParameter("i_bal_type", bodyRequest.getBal_type())
                .setParameter("i_ssm4uuserrefno", bodyRequest.getSsm4uuserrefno());

        return (Integer) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_checkotcbalstatus(OTCDailyBal bodyRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_checkotcbalstatus(:i_branch_code, :i_bal_date)")
                .setParameter("i_branch_code", bodyRequest.getBranch_code())
                .setParameter("i_bal_date", bodyRequest.getBal_date());

        return query.getResultList();
    }

    // Added 02-05-2025 Geo
    @Override
    public List<Object> sp_getotcdailybalctr(OTCDailyBal bodyRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcdailybalctr(:i_branch_code, :i_bal_date)")
                .setParameter("i_branch_code", bodyRequest.getBranch_code())
                .setParameter("i_bal_date", bodyRequest.getBal_date());

        return query.getResultList();
    }

    // @Transactional
    // @Override
    // public Integer sp_updotcdailybalctr(OTCDailyBal bodyRequest) {
    // StoredProcedureQuery storedProcedureQuery =
    // entityManager.createStoredProcedureQuery("sp_updotcdailybalctr");

    // // Register parameters (ensure parameter names and types match those defined
    // in
    // // the stored procedure)
    // storedProcedureQuery.registerStoredProcedureParameter("i_otc_counter_id",
    // BigInteger.class,
    // javax.persistence.ParameterMode.IN);

    // storedProcedureQuery.setParameter("i_otc_counter_id",
    // bodyRequest.getOtc_counter_id());

    // // Execute stored procedure
    // storedProcedureQuery.execute();

    // Integer result = 0;
    // if (storedProcedureQuery.getResultList().size() > 0) {
    // result = (Integer) storedProcedureQuery.getSingleResult();
    // }

    // return result;
    // }

    @Override
    public Integer sp_updotcdailybalctr(OTCDailyBal bodyRequest) {

        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("sp_updotcdailybalctr");

        query.registerStoredProcedureParameter("i_otc_counter_id", BigInteger.class, ParameterMode.IN);
        query.setParameter("i_otc_counter_id", bodyRequest.getOtc_counter_id());

        // Execute and fetch single result (assumes procedure returns 1 value)
        boolean hasResult = query.execute();

        if (hasResult) {
            Object rawResult = query.getSingleResult();
            return rawResult != null ? Integer.parseInt(rawResult.toString()) : 0;
        } else {
            return 0; // No result returned from SP
        }

    }

}
