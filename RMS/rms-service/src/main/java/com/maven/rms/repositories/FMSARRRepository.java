package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IFMSARRInterface;
import com.maven.rms.models.FMSARR;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class FMSARRRepository implements IFMSARRInterface{

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getfmsrcbank() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsrcbank()");

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getfmsarrdebit(BigInteger rc_pg_id) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsarrdebit(:i_rc_pg_id)")
                .setParameter("i_rc_pg_id", rc_pg_id);

        return query.getResultList();
    }

    // @Override
    // public Integer sp_insfmsarr(BigInteger rc_bank_id, BigInteger rc_pg_id, BigDecimal credit, BigDecimal mdr_amt,
    //                             BigInteger mtt_pg_id, String acct_cd, String cust) {
    //     Query query = entityManager.createNativeQuery(
    //         "CALL sp_insfmsarr(:i_rc_bank_id, :i_rc_pg_id, :i_credit, :i_mdr_amt, :i_mtt_pg_id, :i_acct_cd, :i_cust)")
    //         .setParameter("i_rc_bank_id", rc_bank_id)
    //         .setParameter("i_rc_pg_id", rc_pg_id)
    //         .setParameter("i_credit", credit)
    //         .setParameter("i_mdr_amt", mdr_amt)
    //         .setParameter("i_mtt_pg_id", mtt_pg_id)
    //         .setParameter("i_acct_cd", acct_cd)
    //         .setParameter("i_cust", cust);

    //     return (Integer) query.getSingleResult();
    // }

    @Override
    public Integer sp_insfmsarr(FMSARR fmsarr, Integer hid, Integer flag) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_insfmsarr(:i_rc_bank_id, :i_rc_pg_id, :i_credit, :i_mdr_amt, :i_mtt_pg_id, :i_acct_cd, :i_cust, :i_fms_ref_no, :i_hid, :i_flag)")
            .setParameter("i_rc_bank_id", fmsarr.getRc_bank_id())
            .setParameter("i_rc_pg_id", fmsarr.getRc_pg_id())
            .setParameter("i_credit", fmsarr.getCredit())
            .setParameter("i_mdr_amt", fmsarr.getMdr_amt())
            .setParameter("i_mtt_pg_id", fmsarr.getMtt_pg_id())
            .setParameter("i_acct_cd", fmsarr.getAcct_cd())
            .setParameter("i_cust", fmsarr.getCust())
            .setParameter("i_fms_ref_no", fmsarr.getFms_ref_no())
            .setParameter("i_hid", hid)
            .setParameter("i_flag", flag);

        return (Integer) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_getfmsarr() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsarr()");

        return query.getResultList();
    }

    // @Override
    // public Integer sp_updfmsarr(BigInteger arr_hid, String resp_attr_ext_sys, String fms_ref_no, String resp_co,
    //                             String resp_status, String resp_msg, Date resp_dt) {
    //     Query query = entityManager.createNativeQuery(
    //         "CALL sp_updfmsarr(:i_arr_hid, :i_resp_attr_ext_sys, :i_fms_ref_no, :i_resp_co, :i_resp_status, :i_resp_msg, :i_resp_dt)")
    //         .setParameter("i_arr_hid", arr_hid)
    //         .setParameter("i_resp_attr_ext_sys", resp_attr_ext_sys)
    //         .setParameter("i_fms_ref_no", fms_ref_no)
    //         .setParameter("i_resp_co", resp_co)
    //         .setParameter("i_resp_status", resp_status)
    //         .setParameter("i_resp_msg", resp_msg)
    //         .setParameter("i_resp_dt", resp_dt);

    //     return (Integer) query.getSingleResult();
    // }

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

    @Override
    public Integer sp_insfmsarrnonrmsrecon(FMSARR fmsarr) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_insfmsarrnonrmsrecon(:i_cust_id, :i_cash_acct, :i_h_amt, :i_fms_ref_no, :i_b_amt, :i_c_amt)")
                .setParameter("i_cust_id", fmsarr.getI_cust_id())
                .setParameter("i_cash_acct", fmsarr.getI_cash_acct())
                .setParameter("i_h_amt", fmsarr.getI_h_amt())
                .setParameter("i_fms_ref_no", fmsarr.getI_fms_ref_no())
                .setParameter("i_b_amt", fmsarr.getI_b_amt())
                .setParameter("i_c_amt", fmsarr.getI_c_amt());

        return (Integer) query.getSingleResult();
    }

}
