package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IFMSARVInterface;
import com.maven.rms.models.FMSARIImmediateRequest;
import com.maven.rms.models.FMSARV;

@Repository
public class FMSARVRepository implements IFMSARVInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getfmsrefno() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsrefno()");

        return query.getResultList();
    }

    @Override
    public Integer sp_insfmsarv(FMSARV fmsarv) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_insfmsarv(:i_ref_no, :i_arv_reason)")
                .setParameter("i_ref_no", fmsarv.getFms_ref_no())
                .setParameter("i_arv_reason", fmsarv.getArv_reason())
        // .setParameter("i_rc_pg_id", fmsarr.getRc_pg_id())
        // .setParameter("i_credit", fmsarr.getCredit())
        // .setParameter("i_mdr_amt", fmsarr.getMdr_amt())
        // .setParameter("i_mtt_pg_id", fmsarr.getMtt_pg_id())
        // .setParameter("i_acct_cd", fmsarr.getAcct_cd())
        // .setParameter("i_cust", fmsarr.getCust())
        ;

        return (Integer) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_getfmsarv() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsarv()");

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getfmsarvimmediate(FMSARIImmediateRequest fmsari) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsarvimmediate(:i_non_bil_id)")
                .setParameter("i_non_bil_id", fmsari.getI_non_bil_id());

        return query.getResultList();
    }

    @Override
    public Integer sp_updfmsarv(FMSARV fmsarv) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_updfmsarv(:i_arv_reason, :i_fms_ext_sys, :i_fms_status, :i_fms_message, :i_fms_arv_id)")
        .setParameter("i_arv_reason", fmsarv.getArv_reason())
        .setParameter("i_fms_ext_sys", fmsarv.getResp_attr_ext_sys())
        .setParameter("i_fms_status", fmsarv.getResp_status())
        .setParameter("i_fms_message", fmsarv.getResp_msg())
        .setParameter("i_fms_arv_id", fmsarv.getFms_arv_id())
        
        ;

        return (Integer) query.getSingleResult();
    }

}
