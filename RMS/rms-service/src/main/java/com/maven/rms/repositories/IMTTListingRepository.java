package com.maven.rms.repositories;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import com.maven.rms.interfaces.IMTTListingInterface;
import com.maven.rms.models.MTTListingDetReq;

@Repository
public class IMTTListingRepository implements IMTTListingInterface{

    @PersistenceContext
     private EntityManager entityManager;

     // #region FMS Start
     @Override
     public List<Object[]> sp_getMTTListing(MTTListingDetReq req) {

          Query query = entityManager.createNativeQuery(
                    "CALL sp_getmttlisting(:i_page, :i_size, :i_ss_cd, :i_orn_no, :i_orn_dt_fr, :i_orn_dt_to,:i_total_amt, :i_order_status, \r\n" + //
                            "               :i_rcpt_no, :i_rcpt_dt_fr, :i_rcpt_dt_to, :i_rms_type)")
                    .setParameter("i_page", req.getI_page())
                    .setParameter("i_size", req.getI_size())
                    .setParameter("i_ss_cd", req.getI_ss_cd())
                    .setParameter("i_orn_no", req.getI_orn_no())
                    .setParameter("i_orn_dt_fr", req.getI_orn_dt_fr())
                    .setParameter("i_orn_dt_to", req.getI_orn_dt_to())
                    .setParameter("i_total_amt", req.getI_total_amt())
                    .setParameter("i_order_status", req.getI_order_status())
                    .setParameter("i_rcpt_no", req.getI_rcpt_no())
                    .setParameter("i_rcpt_dt_fr", req.getI_rcpt_dt_fr())
                    .setParameter("i_rcpt_dt_to", req.getI_rcpt_dt_to())
                    .setParameter("i_rms_type", req.getI_rms_type());
                    
          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getMTTDetails(MTTListingDetReq req) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getmttdetails(:i_ss_cd, :i_orn_no, :i_total_amt, :i_order_status, :i_rcpt_no)")
                    .setParameter("i_ss_cd", req.getI_ss_cd())
                    .setParameter("i_orn_no", req.getI_orn_no())
                    .setParameter("i_total_amt", req.getI_total_amt())
                    .setParameter("i_order_status", req.getI_order_status())
                    .setParameter("i_rcpt_no", req.getI_rcpt_no());

          return query.getResultList();
     }
     
     @Override
     public List<Object[]> sp_getmttlistingitem(Integer i_mtt_id) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getmttitem(:i_mtt_id)")
                    .setParameter("i_mtt_id", i_mtt_id);

          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getmttpg(Integer i_mtt_id) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getmttpg(:i_mtt_id)")
                    .setParameter("i_mtt_id", i_mtt_id);

          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getmttrcpt(Integer i_mtt_id) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getmttrcpt(:i_mtt_id)")
                    .setParameter("i_mtt_id", i_mtt_id);

          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getmttpg_details(Integer i_mtt_pg_id) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getmttpg_details(:i_mtt_pg_id)")
                    .setParameter("i_mtt_pg_id", i_mtt_pg_id);

          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getmttitem_details(Integer i_mtt_item_id) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getmttitem_details(:i_mtt_item_id)")
                    .setParameter("i_mtt_item_id", i_mtt_item_id);

          return query.getResultList();
     }
     
}
