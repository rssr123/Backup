package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IOTCMasterBalInterface;
import com.maven.rms.models.OTCBalancingRequest;
import com.maven.rms.models.OTCMasterBal;

@Repository
public class OTCMasterBalRepository implements IOTCMasterBalInterface{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getotcmasterballist(OTCMasterBal bodyRequest)
    {
        Query query = entityManager.createNativeQuery(
            "CALL sp_getotcmasterballist(:i_branch_code, :i_bal_date)"
            ).setParameter("i_branch_code", bodyRequest.getBranch_code())
            .setParameter("i_bal_date", bodyRequest.getBal_date());

        return query.getResultList();
    }

    @Override
    public Integer sp_checkotcmasterbalval(OTCMasterBal bodyRequest)
    {
        Query query = entityManager.createNativeQuery(
            "CALL sp_checkotcmasterbalval(:i_branch_code, :i_bal_date)"
            ).setParameter("i_branch_code", bodyRequest.getBranch_code())
            .setParameter("i_bal_date", bodyRequest.getBal_date());

        return (Integer)query.getSingleResult();
    }

    @Override
    public Integer sp_updotcmasterbalstatus(OTCBalancingRequest bodyRequest)
    {
        Query query = entityManager.createNativeQuery(
            "CALL sp_updotcmasterbalstatus(:i_branch_code, :i_bal_date, :i_bal_status, :i_bal_type, :i_ssm4uuserrefno)"
            ).setParameter("i_branch_code", bodyRequest.getBranch_code())
            .setParameter("i_bal_date", bodyRequest.getBal_date())
            .setParameter("i_bal_status", bodyRequest.getBal_status())
            .setParameter("i_bal_type", bodyRequest.getBal_type())
            .setParameter("i_ssm4uuserrefno", bodyRequest.getSsm4uuserrefno());

        return (Integer)query.getSingleResult();
    }

}
