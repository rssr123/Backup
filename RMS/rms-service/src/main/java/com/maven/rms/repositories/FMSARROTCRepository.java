package com.maven.rms.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IFMSARROTCInterface;
import com.maven.rms.models.FMSARR;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class FMSARROTCRepository implements IFMSARROTCInterface{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<String> sp_getotcfmsarirefno(){

        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcfmsarirefno()"
                );
                
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getotcfmsarr(String i_otc_type){

        Query query = entityManager.createNativeQuery(
                "CALL sp_getotcfmsarr(:i_otc_type)"
                ).setParameter("i_otc_type", i_otc_type);
                
        return query.getResultList();
    }

    @Override
    public Integer sp_insotcfmsarr(String i_fms_ref_no){

        Query query = entityManager.createNativeQuery(
            "CALL sp_insotcfmsarr(:i_fms_ref_no)"
            ).setParameter("i_fms_ref_no", i_fms_ref_no);

        return (Integer)query.getSingleResult();
    }

    @Override
    public Integer sp_updfmsarr(FMSARR fmsarr) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_updfmsarr(:i_payment_ref, :i_resp_attr_ext_sys, :i_fms_ref_no, :i_resp_co, :i_resp_status, :i_resp_msg, :i_resp_dt)")
            .setParameter("i_payment_ref", fmsarr.getPayment_ref())
            .setParameter("i_resp_attr_ext_sys", fmsarr.getResp_attr_ext_sys())
            .setParameter("i_fms_ref_no", fmsarr.getFms_ref_no())
            .setParameter("i_resp_co", fmsarr.getResp_co())
            .setParameter("i_resp_status", fmsarr.getResp_status())
            .setParameter("i_resp_msg", fmsarr.getResp_msg())
            .setParameter("i_resp_dt", fmsarr.getResp_dt());

        return (Integer) query.getSingleResult();
    }
}
