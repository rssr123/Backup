package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IHelperInterface;

import com.maven.rms.models.BranchCodeListRequest;
import com.maven.rms.models.FeeDetailListRequest;
import com.maven.rms.models.ParamListRequest;

@Repository
public class HelperRepository implements IHelperInterface{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getbcccodes(BranchCodeListRequest getRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getbcccodes(:i_status)")
                .setParameter("i_status", getRequest.getI_status());
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getfeedetailids(FeeDetailListRequest getRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfeedetailids(:i_status)")
                .setParameter("i_status", getRequest.getI_status());
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getparamsbygroup(ParamListRequest getRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getparamsbygroup(:i_status, :i_param_grp_nm)")
                .setParameter("i_status", getRequest.getI_status())
                .setParameter("i_param_grp_nm", getRequest.getI_param_grp_nm());
        return query.getResultList();
    }
    
}
