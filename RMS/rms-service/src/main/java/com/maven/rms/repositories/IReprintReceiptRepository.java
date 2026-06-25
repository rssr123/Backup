package com.maven.rms.repositories;

import java.util.List;
import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.models.OTCReceiptCancellationDetailsRequest;
import com.maven.rms.models.OTCReceiptCclMTTOrderStatusRequest;
import com.maven.rms.models.OTCReceiptRpMTTOrderStatusRequest;
import com.maven.rms.models.ReprintRcptRequest;
import com.maven.rms.interfaces.IReprintReceiptInterface;

@Repository
public class IReprintReceiptRepository implements IReprintReceiptInterface {

    @PersistenceContext
    private EntityManager entityManager;

  

    @Override
    public List<Object[]> sp_getreprintreceipt(ReprintRcptRequest reprintRcptRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getreprintreceipt(:i_page, :i_size, :i_rcpt_no)")
                .setParameter("i_page", reprintRcptRequest.getI_page())
                .setParameter("i_size", reprintRcptRequest.getI_size())
                .setParameter("i_rcpt_no", reprintRcptRequest.getI_rcpt_no());
                
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getorderinfo_rr(ReprintRcptRequest reprintRcptRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getorderinfo_rr(:i_mtt_id)")
                .setParameter("i_mtt_id", reprintRcptRequest.getI_mtt_id());

        return query.getResultList();
    }


    @Override
    public List<Object[]> sp_getpaymentitems_rr(ReprintRcptRequest reprintRcptRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getpymtitmes_rr(:i_mtt_id)")
                .setParameter("i_mtt_id", reprintRcptRequest.getI_mtt_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getpaymentinfo_rr(ReprintRcptRequest reprintRcptRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getpymtinfo_rr(:i_mtt_id, :i_otc_id)")
                .setParameter("i_mtt_id", reprintRcptRequest.getI_mtt_id())
                .setParameter("i_otc_id", reprintRcptRequest.getI_otc_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getpaymentinfo_rr_v2(ReprintRcptRequest reprintRcptRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getpymtinfo_rr_v2(:i_mtt_id)")
                .setParameter("i_mtt_id", reprintRcptRequest.getI_mtt_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getreceiptinfo_rr(ReprintRcptRequest reprintRcptRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getrcptinfo_rr_v2(:i_mtt_id)")
                .setParameter("i_mtt_id", reprintRcptRequest.getI_mtt_id())
                //.setParameter("i_otc_id", reprintRcptRequest.getI_otc_id())
                ;
                

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_gethistorytable_rr(ReprintRcptRequest reprintRcptRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_gethisttable_rr(:i_mtt_id)")
                .setParameter("i_mtt_id", reprintRcptRequest.getI_mtt_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_gethistorytable_rr_v2(ReprintRcptRequest reprintRcptRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getotcrcptrpnthistorydetails(:i_mtt_id)")
                .setParameter("i_mtt_id", reprintRcptRequest.getI_mtt_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getmttrcptrp(ReprintRcptRequest reprintRcptRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getmttrcptrp(:i_mtt_id)")
                .setParameter("i_mtt_id", reprintRcptRequest.getI_mtt_id());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getjustification_rr(ReprintRcptRequest reprintRcptRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getjusti_rr(:i_otc_id)")
                .setParameter("i_otc_id", reprintRcptRequest.getI_otc_id());

        return query.getResultList();
    }

    @Override
    public Integer sp_updrcptcount_rr(ReprintRcptRequest reprintRcptRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_updrcptcount_rr(:i_otc_rcpt_id)")
                .setParameter("i_otc_rcpt_id", reprintRcptRequest.getI_otc_rcpt_id())
                //.setParameter("i_modified_by", i_modified_by)
                ;

        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_updrcptcount_mtt(ReprintRcptRequest reprintRcptRequest) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_updrcptcount_mtt(:i_mtt_id)")
                .setParameter("i_mtt_id", reprintRcptRequest.getI_mtt_id())
                //.setParameter("i_modified_by", i_modified_by)
                ;

        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_updrcptjust_rr(ReprintRcptRequest reprintRcptRequest, int i_otc_rc_rp_id, int i_otc_rcpt_id, String i_justication, String i_modified_by) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_updrcptjust_rr(:i_otc_rc_rp_id, :i_otc_rcpt_id, :i_justication, :i_modified_by)")
                .setParameter("i_otc_rc_rp_id", i_otc_rc_rp_id)
                .setParameter("i_otc_rcpt_id", i_otc_rcpt_id)
                .setParameter("i_justication", i_justication)
                .setParameter("i_modified_by", i_modified_by);

        return (Integer) query.getSingleResult();
    }

    @Override
    public Object[] sp_getmttrcptinfo(BigInteger mtt_id) {
        Query query = entityManager.createNativeQuery("CALL sp_getmttrcptinfo(:i_mtt_id)")
                    .setParameter("i_mtt_id", mtt_id);
        Object[] result = (Object[]) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_updotcrcpt(Integer i_otc_rcpt_id, String i_ver_id, String i_ssdocref_id, String i_file_nm) {
        // TODO Auto-generated method stub
        Query query = entityManager.createNativeQuery("CALL sp_updotcrcpt(:i_otc_rcpt_id, :i_ver_id, :i_ssdocref_id, :i_file_nm)")
                .setParameter("i_otc_rcpt_id", i_otc_rcpt_id)
                .setParameter("i_ver_id", i_ver_id)
                .setParameter("i_ssdocref_id", i_ssdocref_id)
                .setParameter("i_file_nm", i_file_nm);
        return (Integer) query.getSingleResult();
    }


    @Override
    public Integer sp_updmtt_orderstatus(OTCReceiptRpMTTOrderStatusRequest mttOrderStatusRequest) {
        Query query = entityManager
                .createNativeQuery("CALL sp_updmtt_orderstatus(:i_mtt_id, :i_order_status, :i_modified_by)")
                .setParameter("i_mtt_id", mttOrderStatusRequest.getI_mtt_id())
                .setParameter("i_order_status", mttOrderStatusRequest.getI_order_status() != null ? mttOrderStatusRequest.getI_order_status() : null)
                .setParameter("i_modified_by", mttOrderStatusRequest.getI_modified_by() != null ? mttOrderStatusRequest.getI_modified_by() : null);
              

        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_checkrcptcl(OTCReceiptRpMTTOrderStatusRequest mttOrderStatusRequest) {
        Query query = entityManager
                .createNativeQuery("CALL sp_checkrcptcl(:i_mtt_id)")
                .setParameter("i_mtt_id", mttOrderStatusRequest.getI_mtt_id());
              

        return (Integer) query.getSingleResult();
    }

    public Object[] sp_getotcreceiptrp(OTCReceiptRpMTTOrderStatusRequest mttOrderStatusRequest) {
        Query query = entityManager.createNativeQuery("CALL sp_getotcreceiptrp(:i_otc_rc_rp_id)")
                    .setParameter("i_otc_rc_rp_id", mttOrderStatusRequest.getI_otc_rc_rp_id());
        Object[] result = (Object[]) query.getSingleResult();
        return result;
    }




  
}
