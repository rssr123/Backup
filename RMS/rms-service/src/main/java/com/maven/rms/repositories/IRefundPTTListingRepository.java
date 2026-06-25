package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IRefundPTTListingInterface;
import com.maven.rms.models.PaymentItemDetails;
import com.maven.rms.models.RefundDoc;
import com.maven.rms.models.RefundPTTListingDetReq;
import com.maven.rms.models.RefundWFList;
import com.maven.rms.models.RefundWFListingDetReq;
import com.maven.rms.models.TaxCdRequest;

@Repository
public class IRefundPTTListingRepository implements IRefundPTTListingInterface {
     @PersistenceContext
     private EntityManager entityManager;

     @Autowired
     private JdbcTemplate jdbcTemplate;

     // #region FMS Start
     @Override
     public List<Object[]> sp_getRefundPTTListing(RefundPTTListingDetReq req) {

          Query query = entityManager.createNativeQuery(

                    "CALL sp_getrefundpttlisting(:i_page, :i_size, :i_orn_no, :i_orn_dt_fr, :i_orn_dt_to, :i_ent_nm, :i_txn_id, :i_order_status, :i_rcpt_no)")
                    .setParameter("i_page", req.getI_page())
                    .setParameter("i_size", req.getI_size())
                    .setParameter("i_orn_no", req.getI_orn_no())
                    .setParameter("i_orn_dt_fr", req.getI_orn_dt_fr())
                    .setParameter("i_orn_dt_to", req.getI_orn_dt_to())
                    .setParameter("i_ent_nm", req.getI_ent_nm())
                    .setParameter("i_txn_id", req.getI_txn_id())
                    .setParameter("i_order_status", req.getI_order_status())
                    .setParameter("i_rcpt_no", req.getI_rcpt_no());
          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getRefundOI_online(RefundPTTListingDetReq req) {

          Query query = entityManager.createNativeQuery(

                    "CALL sp_getrefundoi_online(:i_mtt_id)")
                    .setParameter("i_mtt_id", req.getI_mtt_id());
          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getRefundOI_otc(RefundPTTListingDetReq req) {

          Query query = entityManager.createNativeQuery(

                    "CALL sp_getrefundoi_otc(:i_mtt_id)")
                    .setParameter("i_mtt_id", req.getI_mtt_id());
          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getRefundPaymentItem(RefundPTTListingDetReq req) {

          Query query = entityManager.createNativeQuery(

                    "CALL sp_getrefundpaymentitem(:i_mtt_id)")
                    .setParameter("i_mtt_id", req.getI_mtt_id());
          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getrefundpaymentinfo_online(RefundPTTListingDetReq req) {

          Query query = entityManager.createNativeQuery(

                    "CALL sp_getrefundpaymentinfo_online(:i_mtt_id, :i_txn_id)")
                    .setParameter("i_mtt_id", req.getI_mtt_id())
                    .setParameter("i_txn_id", req.getI_txn_id());
          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getrefundpgrcpt(RefundPTTListingDetReq req) {

          Query query = entityManager.createNativeQuery(

                    "CALL sp_getrefundpgrcpt(:i_mtt_id)")
                    .setParameter("i_mtt_id", req.getI_mtt_id());
          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getrefundotcrcpt(RefundPTTListingDetReq req) {

          Query query = entityManager.createNativeQuery(

                    "CALL sp_getrefundotcrcpt(:i_mtt_id)")
                    .setParameter("i_mtt_id", req.getI_mtt_id());
          return query.getResultList();
     }

     @Override
     public List<Object[]> sp_getrefundinfo(RefundPTTListingDetReq req) {
          // Create the query
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getrttinfo(:i_txn_id, :i_orn_no)")
                    .setParameter("i_txn_id", req.getI_txn_id())
                    .setParameter("i_orn_no", req.getI_orn_no());

          // Get the result list
          List<Object[]> resultList = query.getResultList();

          // Debug: Print the result to the console
          if (resultList != null && !resultList.isEmpty()) {
               System.out.println("Query Result:");
               for (Object[] row : resultList) {
                    System.out.println(Arrays.toString(row));
               }
          } else {
               System.out.println("Query returned no results or null.");
          }

          // Return the result list
          return resultList;
     }

     @Override
     public List<Object[]> sp_getrefundhist(RefundPTTListingDetReq req) {

          Query query = entityManager.createNativeQuery(

                    "CALL sp_getrefundhist(:i_txn_id, :i_orn_no)")
                    .setParameter("i_txn_id", req.getI_txn_id())
                    .setParameter("i_orn_no", req.getI_orn_no());
          return query.getResultList();
     }

     @Override
     public Integer sp_insrttwf(RefundWFList insertRequest) {

          Query query = entityManager.createNativeQuery(
                    "CALL sp_insrttwf(:rcpt_no, :rcpt_date, :orn_no, :txn_id, :refund_amt, :ent_no, :ent_nm, :cust_email, :sme_email , :requested_by, :created_by, :modified_by, :msg, :refund_cd, :assign_to,:rtt_status,:refund_ty,:refund_reason)")
                    .setParameter("rcpt_no", insertRequest.getRcpt_no())
                    .setParameter("rcpt_date", insertRequest.getRcpt_date())
                    .setParameter("orn_no", insertRequest.getOrn_no())
                    .setParameter("txn_id", insertRequest.getTxn_id())
                    .setParameter("refund_amt", insertRequest.getRefund_amt())
                    .setParameter("ent_no", insertRequest.getEnt_no())
                    .setParameter("ent_nm", insertRequest.getEnt_nm())
                    .setParameter("cust_email", insertRequest.getCust_email())
                    .setParameter("sme_email", insertRequest.getSme_email())
                    .setParameter("requested_by", insertRequest.getRequested_by())
                    .setParameter("created_by", insertRequest.getCreated_by())
                    .setParameter("modified_by", insertRequest.getModified_by())
                    .setParameter("msg", insertRequest.getMsg())
                    .setParameter("refund_cd", insertRequest.getRefund_cd())
                    .setParameter("rtt_status", insertRequest.getRtt_status())
                    .setParameter("refund_ty", insertRequest.getRefund_ty())
                    .setParameter("assign_to", insertRequest.getAssign_to())
                    .setParameter("refund_reason", insertRequest.getRefund_reason());

          Integer result = (Integer) query.getSingleResult();
          return result;
     }

     public Integer sp_insertRefundItem(
               PaymentItemDetails item,
               Integer rtt_wf_id,
               String createdBy,
               String modifiedBy) {
          try {
               // build the literal CALL
               String call = buildCallString(item, rtt_wf_id, createdBy, modifiedBy);
               System.out.println("DEBUG — Executing: " + call);

               Query query = entityManager.createNativeQuery(
                         "CALL sp_insRttItem("
                                   + ":i_rtt_wf_id, :i_unit_fee, :i_qty, :i_item_ref_no, :i_item_desc, "
                                   + ":i_tax_pct, :i_tax_amt, :i_grant_cd, :i_disc_amt, "
                                   + ":i_gross_amt, :i_created_by, :i_modified_by, "
                                   + ":i_net_amt, :i_entity_no, :i_entity_nm, :i_entity_type)")
                         .setParameter("i_rtt_wf_id", rtt_wf_id)
                         .setParameter("i_unit_fee", item.getUnit_fee())
                         .setParameter("i_qty", item.getQty())
                         .setParameter("i_item_ref_no", item.getItem_ref_no())
                         .setParameter("i_item_desc", item.getItem_desc())
                         .setParameter("i_tax_pct", item.getTax_pct())
                         .setParameter("i_tax_amt", item.getTax_amt())
                         .setParameter("i_grant_cd", item.getGrant_cd())
                         .setParameter("i_disc_amt", item.getDisc_amt())
                         .setParameter("i_gross_amt", item.getGross_amt())
                         .setParameter("i_created_by", createdBy)
                         .setParameter("i_modified_by", modifiedBy)
                         .setParameter("i_net_amt", item.getNet_amt())
                         .setParameter("i_entity_no", item.getEntity_no())
                         .setParameter("i_entity_nm", item.getEntity_nm())
                         .setParameter("i_entity_type", item.getEntity_type());

               return (Integer) query.getSingleResult();

          } catch (Exception e) {
               System.out.println("Error executing sp_insertRefundItem: " + e.getMessage());
               e.printStackTrace();
               throw e;
          }
     }

     // helper to turn each Java value into a SQL literal (with proper quoting &
     // NULL-handling)
     private String buildCallString(PaymentItemDetails item,
               Integer rtt_wf_id,
               String createdBy,
               String modifiedBy) {
          Object[] params = new Object[] {
                    rtt_wf_id,
                    item.getUnit_fee(),
                    item.getQty(),
                    item.getItem_ref_no(),
                    item.getItem_desc(),
                    item.getTax_pct(),
                    item.getTax_amt(),
                    item.getGrant_cd(),
                    item.getDisc_amt(),
                    item.getGross_amt(),
                    createdBy,
                    modifiedBy,
                    item.getNet_amt(),
                    item.getEntity_no(),
                    item.getEntity_nm(),
                    item.getEntity_type()
          };

          String joined = Arrays.stream(params)
                    .map(p -> {
                         if (p == null)
                              return "NULL";
                         if (p instanceof String) {
                              // escape single quotes by doubling
                              String s = ((String) p).replace("'", "''");
                              return "'" + s + "'";
                         }
                         // numeric or other toString()
                         return p.toString();
                    })
                    .collect(Collectors.joining(", "));

          return "CALL sp_insRttItem(" + joined + ")";
     }

     // @Override
     // public List<Object[]> sp_getrefundtht(RefundPTTListingDetReq req) {

     // Query query = entityManager.createNativeQuery(

     // "CALL sp_getrefundtht(:i_page, :i_size, :i_orn_no, :i_rcpt_no, :i_txn_id,
     // :i_refund_slip_no, :i_created_by,:i_order_status, :i_rtt_app_no)")
     // .setParameter("i_page", req.getI_page())
     // .setParameter("i_size", req.getI_size())
     // .setParameter("i_orn_no", req.getI_orn_no())
     // .setParameter("i_rcpt_no", req.getI_rcpt_no())
     // .setParameter("i_txn_id", req.getI_txn_id())
     // .setParameter("i_refund_slip_no", req.getI_refund_slip_no())
     // .setParameter("i_created_by", req.getI_created_by())
     // .setParameter("i_order_status", req.getI_order_status())
     // .setParameter("i_rtt_app_no", req.getI_rtt_app_no());
     // return query.getResultList();
     // }
     @Override
     public List<Object[]> sp_getrefundtht(RefundPTTListingDetReq req) {
          // Print input parameters for debugging
          System.out.println("Received Request:");
          System.out.println("i_page: " + req.getI_page());
          System.out.println("i_size: " + req.getI_size());
          System.out.println("i_orn_no: " + req.getI_orn_no());
          System.out.println("i_rcpt_no: " + req.getI_rcpt_no());
          System.out.println("i_txn_id: " + req.getI_txn_id());
          System.out.println("i_refund_slip_no: " + req.getI_refund_slip_no());
          System.out.println("i_created_by: " + req.getI_created_by());
          System.out.println("i_order_status: " + req.getI_order_status());
          System.out.println("i_rtt_app_no: " + req.getI_rtt_app_no());
          System.out.println(("i_rms_type: " + req.getI_rms_type()));
          System.out.println(("i_orn_dt_fr: " + req.getI_orn_dt_fr()));
          System.out.println(("i_orn_dt_to: " + req.getI_orn_dt_to()));
   

          Query query = entityManager.createNativeQuery(
                    "CALL sp_getrefundtht(:i_page, :i_size, :i_orn_no, :i_rcpt_no, :i_txn_id, :i_refund_slip_no, :i_created_by,:i_order_status, :i_rtt_app_no, :i_rms_type, :i_orn_dt_fr, :i_orn_dt_to)")
                    .setParameter("i_page", req.getI_page())
                    .setParameter("i_size", req.getI_size())
                    .setParameter("i_orn_no", req.getI_orn_no())
                    .setParameter("i_rcpt_no", req.getI_rcpt_no())
                    .setParameter("i_txn_id", req.getI_txn_id())
                    .setParameter("i_refund_slip_no", req.getI_refund_slip_no())
                    .setParameter("i_created_by", req.getI_created_by())
                    .setParameter("i_order_status", req.getI_order_status())
                    .setParameter("i_rtt_app_no", req.getI_rtt_app_no())
                    .setParameter("i_rms_type", req.getI_rms_type())
                    .setParameter("i_orn_dt_fr", req.getI_orn_dt_fr())
                    .setParameter("i_orn_dt_to", req.getI_orn_dt_to());
          return query.getResultList();
     }

     @Override
     public Integer sp_insrttwf_da(RefundWFList insertRequest) {

          Query query = entityManager.createNativeQuery(
                    "CALL sp_insrttwf_da(:rcpt_no, :rcpt_date, :orn_no, :txn_id, :refund_amt, :ent_no, :ent_nm, :cust_email, :sme_email , :requested_by, :created_by, :modified_by, :msg, :refund_cd, :assign_to,:rtt_status,:refund_ty,:refund_reason, "
                              +
                              ":identity_type, :identity_number, :bank_account_no, :bank_account_name, :bank_account_type, :bank_holder_name, :billing_address_1, :billing_address_2, :billing_address_3, :city, :postcode, :state ,:rec_email)")
                    .setParameter("rcpt_no", insertRequest.getRcpt_no())
                    .setParameter("rcpt_date", insertRequest.getRcpt_date())
                    .setParameter("orn_no", insertRequest.getOrn_no())
                    .setParameter("txn_id", insertRequest.getTxn_id())
                    .setParameter("refund_amt", insertRequest.getRefund_amt())
                    .setParameter("ent_no", insertRequest.getEnt_no())
                    .setParameter("ent_nm", insertRequest.getEnt_nm())
                    .setParameter("cust_email", insertRequest.getCust_email())
                    .setParameter("sme_email", insertRequest.getSme_email())
                    .setParameter("requested_by", insertRequest.getRequested_by())
                    .setParameter("created_by", insertRequest.getCreated_by())
                    .setParameter("modified_by", insertRequest.getModified_by())
                    .setParameter("msg", insertRequest.getMsg())
                    .setParameter("refund_cd", insertRequest.getRefund_cd())
                    .setParameter("rtt_status", insertRequest.getRtt_status())
                    .setParameter("refund_ty", insertRequest.getRefund_ty())
                    .setParameter("assign_to", insertRequest.getAssign_to())
                    .setParameter("refund_reason", insertRequest.getRefund_reason())
                    .setParameter("identity_type", insertRequest.getIdentity_type())
                    .setParameter("identity_number", insertRequest.getIdentity_number())
                    .setParameter("bank_account_no", insertRequest.getBank_account_no())
                    .setParameter("bank_account_name", insertRequest.getBank_account_name())
                    .setParameter("bank_account_type", insertRequest.getBank_account_type())
                    .setParameter("bank_holder_name", insertRequest.getBank_holder_name())
                    .setParameter("billing_address_1", insertRequest.getBilling_address_1())
                    .setParameter("billing_address_2", insertRequest.getBilling_address_2())
                    .setParameter("billing_address_3", insertRequest.getBilling_address_3())
                    .setParameter("city", insertRequest.getCity())
                    .setParameter("postcode", insertRequest.getPostcode())
                    .setParameter("state", insertRequest.getState())
                    .setParameter("rec_email", insertRequest.getRec_email());

          Integer result = (Integer) query.getSingleResult();
          return result;
     }

     public Long sp_insrttform_rs02(RefundWFList insertRequest) {
          try {
               // Debugging: Print the values passed to the query
               System.out.println("orn_no: " + insertRequest.getOrn_no());
               System.out.println("identity_type: " + insertRequest.getIdentity_type());
               System.out.println("identity_number: " + insertRequest.getIdentity_number());
               System.out.println("bank_account_no: " + insertRequest.getBank_account_no());
               System.out.println("bank_account_name: " + insertRequest.getBank_account_name());
               System.out.println("bank_account_type: " + insertRequest.getBank_account_type());
               System.out.println("bank_holder_name: " + insertRequest.getBank_holder_name());
               System.out.println("billing_address_1: " + insertRequest.getBilling_address_1());
               System.out.println("billing_address_2: " + insertRequest.getBilling_address_2());
               System.out.println("billing_address_3: " + insertRequest.getBilling_address_3());
               System.out.println("city: " + insertRequest.getCity());
               System.out.println("postcode: " + insertRequest.getPostcode());
               System.out.println("state: " + insertRequest.getState());
               System.out.println("rec_email: " + insertRequest.getRec_email());
               System.out.println("created_by: " + insertRequest.getCreated_by());
               System.out.println("modified_by: " + insertRequest.getModified_by());

               Query query = entityManager.createNativeQuery(
                         "CALL sp_insrttform_rs02(:orn_no, :identity_type, :identity_number, :bank_account_no, :bank_account_name,"
                                   +
                                   ":bank_account_type, :bank_holder_name, :billing_address_1, :billing_address_2, :billing_address_3, :city, :postcode, :state ,:rec_email, :created_by, :modified_by)")
                         .setParameter("orn_no", insertRequest.getOrn_no())
                         .setParameter("identity_type", insertRequest.getIdentity_type())
                         .setParameter("identity_number", insertRequest.getIdentity_number())
                         .setParameter("bank_account_no", insertRequest.getBank_account_no())
                         .setParameter("bank_account_name", insertRequest.getBank_account_name())
                         .setParameter("bank_account_type", insertRequest.getBank_account_type())
                         .setParameter("bank_holder_name", insertRequest.getBank_holder_name())
                         .setParameter("billing_address_1", insertRequest.getBilling_address_1())
                         .setParameter("billing_address_2", insertRequest.getBilling_address_2())
                         .setParameter("billing_address_3", insertRequest.getBilling_address_3())
                         .setParameter("city", insertRequest.getCity())
                         .setParameter("postcode", insertRequest.getPostcode())
                         .setParameter("state", insertRequest.getState())
                         .setParameter("rec_email", insertRequest.getRec_email())
                         .setParameter("created_by", insertRequest.getCreated_by())
                         .setParameter("modified_by", insertRequest.getModified_by());

               // Execute the query and retrieve the result
               BigInteger result = (BigInteger) query.getSingleResult();
               System.out.println("Stored procedure result: " + result);

               return result.longValue();
          } catch (Exception e) {
               System.out.println("Error executing stored procedure: " + e.getMessage());
               e.printStackTrace();
               throw new RuntimeException("Stored procedure execution failed: " + e.getMessage(), e);
          }
     }

     @Override
     public Integer sp_insrttwf_rf(RefundWFList insertRequest) {

          // Print input parameters received from Postman
          System.out.println("Received Request:");
          System.out.println("rcpt_no: " + insertRequest.getRcpt_no());
          System.out.println("rcpt_date: " + insertRequest.getRcpt_date());
          System.out.println("orn_no: " + insertRequest.getOrn_no());
          System.out.println("txn_id: " + insertRequest.getTxn_id());
          System.out.println("refund_amt: " + insertRequest.getRefund_amt());
          System.out.println("ent_no: " + insertRequest.getEnt_no());
          System.out.println("ent_nm: " + insertRequest.getEnt_nm());
          System.out.println("ent_ty: " + insertRequest.getEnt_ty());
          System.out.println("cust_nm: " + insertRequest.getCust_nm());
          System.out.println("cust_phone: " + insertRequest.getCust_phone());
          System.out.println("rcpt_amt: " + insertRequest.getRcpt_amt());
          System.out.println("cust_email: " + insertRequest.getCust_email());
          System.out.println("sme_email: " + insertRequest.getSme_email());
          System.out.println("requested_by: " + insertRequest.getRequested_by());
          System.out.println("created_by: " + insertRequest.getCreated_by());
          System.out.println("modified_by: " + insertRequest.getModified_by());
          System.out.println("msg: " + insertRequest.getMsg());
          System.out.println("refund_cd: " + insertRequest.getRefund_cd());
          System.out.println("assign_to: " + insertRequest.getAssign_to());
          System.out.println("rtt_status: " + insertRequest.getRtt_status());
          System.out.println("refund_ty: " + insertRequest.getRefund_ty());
          System.out.println("refund_reason: " + insertRequest.getRefund_reason());
          System.out.println("identity_type: " + insertRequest.getIdentity_type());
          System.out.println("identity_number: " + insertRequest.getIdentity_number());
          System.out.println("bank_account_no: " + insertRequest.getBank_account_no());
          System.out.println("bank_account_name: " + insertRequest.getBank_account_name());
          System.out.println("bank_account_type: " + insertRequest.getBank_account_type());
          System.out.println("bank_holder_name: " + insertRequest.getBank_holder_name());
          System.out.println("billing_address_1: " + insertRequest.getBilling_address_1());
          System.out.println("billing_address_2: " + insertRequest.getBilling_address_2());
          System.out.println("billing_address_3: " + insertRequest.getBilling_address_3());
          System.out.println("city: " + insertRequest.getCity());
          System.out.println("postcode: " + insertRequest.getPostcode());
          System.out.println("state: " + insertRequest.getState());
          System.out.println("rec_email: " + insertRequest.getRec_email());

          Query query = entityManager.createNativeQuery(
                    "CALL sp_insrttwf_rf(:rcpt_no, :rcpt_date, :orn_no, :txn_id, :refund_amt, :ent_no, :ent_nm, :ent_ty, :cust_nm, :cust_phone, :rcpt_amt, :cust_email, :sme_email , :requested_by, :created_by, :modified_by, :msg,"
                              +
                              " :refund_cd, :assign_to,:rtt_status,:refund_ty,:refund_reason, :identity_type, :identity_number, :bank_account_no, :bank_account_name, :bank_account_type, :bank_holder_name, :billing_address_1, :billing_address_2, "
                              +
                              " :billing_address_3, :city, :postcode, :state ,:rec_email)")
                    .setParameter("rcpt_no", insertRequest.getRcpt_no())
                    .setParameter("rcpt_date", insertRequest.getRcpt_date())
                    .setParameter("orn_no", insertRequest.getOrn_no())
                    .setParameter("txn_id", insertRequest.getTxn_id())
                    .setParameter("refund_amt", insertRequest.getRefund_amt())
                    .setParameter("ent_no", insertRequest.getEnt_no())
                    .setParameter("ent_nm", insertRequest.getEnt_nm())
                    .setParameter("ent_ty", insertRequest.getEnt_ty())
                    .setParameter("cust_nm", insertRequest.getCust_nm())
                    .setParameter("cust_phone", insertRequest.getCust_phone())
                    .setParameter("rcpt_amt", insertRequest.getRcpt_amt())
                    .setParameter("cust_email", insertRequest.getCust_email())
                    .setParameter("sme_email", insertRequest.getSme_email())
                    .setParameter("requested_by", insertRequest.getRequested_by())
                    .setParameter("created_by", insertRequest.getCreated_by())
                    .setParameter("modified_by", insertRequest.getModified_by())
                    .setParameter("msg", insertRequest.getMsg())
                    .setParameter("refund_cd", insertRequest.getRefund_cd())
                    .setParameter("rtt_status", insertRequest.getRtt_status())
                    .setParameter("refund_ty", insertRequest.getRefund_ty())
                    .setParameter("assign_to", insertRequest.getAssign_to())
                    .setParameter("refund_reason", insertRequest.getRefund_reason())
                    .setParameter("identity_type", insertRequest.getIdentity_type())
                    .setParameter("identity_number", insertRequest.getIdentity_number())
                    .setParameter("bank_account_no", insertRequest.getBank_account_no())
                    .setParameter("bank_account_name", insertRequest.getBank_account_name())
                    .setParameter("bank_account_type", insertRequest.getBank_account_type())
                    .setParameter("bank_holder_name", insertRequest.getBank_holder_name())
                    .setParameter("billing_address_1", insertRequest.getBilling_address_1())
                    .setParameter("billing_address_2", insertRequest.getBilling_address_2())
                    .setParameter("billing_address_3", insertRequest.getBilling_address_3())
                    .setParameter("city", insertRequest.getCity())
                    .setParameter("postcode", insertRequest.getPostcode())
                    .setParameter("state", insertRequest.getState())
                    .setParameter("rec_email", insertRequest.getRec_email());

          Integer result = (Integer) query.getSingleResult();
          return result;
     }

     @Override
     public Integer sp_insertRefundDoc(RefundDoc refundDoc, Blob blob, Integer rtt_wf_id, String createdBy,
               String modifiedBy) {
          try {
               // Create the stored procedure query
               StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insRttdoc");

               // Register parameters
               storedProcedureQuery.registerStoredProcedureParameter("i_rtt_wf_id", Integer.class,
                         javax.persistence.ParameterMode.IN);
               storedProcedureQuery.registerStoredProcedureParameter("i_file_nm", String.class,
                         javax.persistence.ParameterMode.IN);
               storedProcedureQuery.registerStoredProcedureParameter("i_file_content", Blob.class,
                         javax.persistence.ParameterMode.IN);
               storedProcedureQuery.registerStoredProcedureParameter("i_file_type", String.class,
                         javax.persistence.ParameterMode.IN);
               storedProcedureQuery.registerStoredProcedureParameter("i_file_size_kb", Integer.class,
                         javax.persistence.ParameterMode.IN);
               storedProcedureQuery.registerStoredProcedureParameter("i_created_by", String.class,
                         javax.persistence.ParameterMode.IN);
               storedProcedureQuery.registerStoredProcedureParameter("i_modified_by", String.class,
                         javax.persistence.ParameterMode.IN);

               // Set input parameters
               storedProcedureQuery.setParameter("i_rtt_wf_id", rtt_wf_id);
               storedProcedureQuery.setParameter("i_file_nm", refundDoc.getFile_nm());
               storedProcedureQuery.setParameter("i_file_content", blob); // Pass the Blob object
               storedProcedureQuery.setParameter("i_file_type", refundDoc.getFile_type());
               storedProcedureQuery.setParameter("i_file_size_kb", refundDoc.getFile_size_kb());
               storedProcedureQuery.setParameter("i_created_by", createdBy);
               storedProcedureQuery.setParameter("i_modified_by", modifiedBy);

               // Execute the stored procedure
               storedProcedureQuery.execute();

               // Handle the result (if the stored procedure returns an output parameter or
               // result)
               // For example, if the procedure returns an integer:
               Integer result = null;
               if (storedProcedureQuery.getResultList().size() > 0) {
                    result = (Integer) storedProcedureQuery.getSingleResult();
               }
               return result;

          } catch (Exception e) {
               System.out.println("Error executing sp_insertRefundDoc: " + e.getMessage());
               e.printStackTrace();
               throw new RuntimeException("Error executing sp_insertRefundDoc: " + e.getMessage(), e);
          }
     }

     @Override
     public Integer sp_uptrtt_dateexpiry(RefundPTTListingDetReq updateRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_uptrtt_dateexpiry(:i_rtt_app_no, :i_modified_by)")

                    .setParameter("i_rtt_app_no", updateRequest.getI_rtt_app_no())
                    .setParameter("i_modified_by", updateRequest.getI_modified_by());
          Integer result = (Integer) query.getSingleResult();
          return result;
     }

     @Override
     public List<Object[]> sp_getrttwfid(RefundPTTListingDetReq refundPTTListingRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_getrttwfid(:i_rtt_app_no)")

                    .setParameter("i_rtt_app_no", refundPTTListingRequest.getI_rtt_app_no());
          // Get the result list
          List<Object[]> resultList = query.getResultList();

          // Debug: Print the result to the console
          if (resultList != null && !resultList.isEmpty()) {
               System.out.println("Query Result:");
               for (Object[] row : resultList) {
                    System.out.println(Arrays.toString(row));
               }
          } else {
               System.out.println("Query returned no results or null.");
          }

          // Return the result list
          return resultList;
     }

     @Override
     public Integer sp_updrttwf_rf(RefundWFList updateRequest) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_updrttwf_rf(:rtt_wf_id, :rtt_wf_hist_id, :rcpt_no, :rcpt_date, :orn_no, :txn_id, :refund_amt, :ent_no, :ent_nm, "
                              +
                              ":ent_ty, :cust_nm, :cust_phone, :rcpt_amt, :cust_email, :sme_email, :requested_by, :created_by, :modified_by, :msg, "
                              +
                              ":refund_cd, :assign_to, :rtt_status, :refund_ty, :refund_reason, :identity_type, :identity_number, "
                              +
                              ":bank_account_no, :bank_account_name, :bank_account_type, :bank_holder_name, :billing_address_1, "
                              +
                              ":billing_address_2, :billing_address_3, :city, :postcode, :state, :rec_email)");
          query.setParameter("rtt_wf_id", updateRequest.getRtt_wf_id());
          query.setParameter("rtt_wf_hist_id", updateRequest.getRtt_wf_hist_id());
          query.setParameter("rcpt_no", updateRequest.getRcpt_no());
          query.setParameter("rcpt_date", updateRequest.getRcpt_date());
          query.setParameter("orn_no", updateRequest.getOrn_no());
          query.setParameter("txn_id", updateRequest.getTxn_id());
          query.setParameter("refund_amt", updateRequest.getRefund_amt());
          query.setParameter("ent_no", updateRequest.getEnt_no());
          query.setParameter("ent_nm", updateRequest.getEnt_nm());
          query.setParameter("ent_ty", updateRequest.getEnt_ty());
          query.setParameter("cust_nm", updateRequest.getCust_nm());
          query.setParameter("cust_phone", updateRequest.getCust_phone());
          query.setParameter("rcpt_amt", updateRequest.getRcpt_amt());
          query.setParameter("cust_email", updateRequest.getCust_email());
          query.setParameter("sme_email", updateRequest.getSme_email());
          query.setParameter("requested_by", updateRequest.getRequested_by());
          query.setParameter("created_by", updateRequest.getCreated_by());
          query.setParameter("modified_by", updateRequest.getModified_by());
          query.setParameter("msg", updateRequest.getMsg());
          query.setParameter("refund_cd", updateRequest.getRefund_cd());
          query.setParameter("assign_to", updateRequest.getAssign_to());
          query.setParameter("rtt_status", updateRequest.getRtt_status());
          query.setParameter("refund_ty", updateRequest.getRefund_ty());
          query.setParameter("refund_reason", updateRequest.getRefund_reason());
          query.setParameter("identity_type", updateRequest.getIdentity_type());
          query.setParameter("identity_number", updateRequest.getIdentity_number());
          query.setParameter("bank_account_no", updateRequest.getBank_account_no());
          query.setParameter("bank_account_name", updateRequest.getBank_account_name());
          query.setParameter("bank_account_type", updateRequest.getBank_account_type());
          query.setParameter("bank_holder_name", updateRequest.getBank_holder_name());
          query.setParameter("billing_address_1", updateRequest.getBilling_address_1());
          query.setParameter("billing_address_2", updateRequest.getBilling_address_2());
          query.setParameter("billing_address_3", updateRequest.getBilling_address_3());
          query.setParameter("city", updateRequest.getCity());
          query.setParameter("postcode", updateRequest.getPostcode());
          query.setParameter("state", updateRequest.getState());
          query.setParameter("rec_email", updateRequest.getRec_email());

          Integer result = (Integer) query.getSingleResult();
          return result;
     }

     @Override
     public Integer sp_updateRefundItem(PaymentItemDetails item, Integer rttWfId, String modifiedBy) {
          StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_updRttItem");

          // Register input parameters
          storedProcedureQuery.registerStoredProcedureParameter("i_rtt_item_id", Integer.class, ParameterMode.IN);
          storedProcedureQuery.registerStoredProcedureParameter("i_unit_fee", BigDecimal.class, ParameterMode.IN);
          storedProcedureQuery.registerStoredProcedureParameter("i_qty", Integer.class, ParameterMode.IN);
          storedProcedureQuery.registerStoredProcedureParameter("i_item_ref_no", String.class, ParameterMode.IN);
          storedProcedureQuery.registerStoredProcedureParameter("i_item_desc", String.class, ParameterMode.IN);
          storedProcedureQuery.registerStoredProcedureParameter("i_tax_pct", BigDecimal.class, ParameterMode.IN);
          storedProcedureQuery.registerStoredProcedureParameter("i_tax_amt", BigDecimal.class, ParameterMode.IN);
          storedProcedureQuery.registerStoredProcedureParameter("i_grant_cd", String.class, ParameterMode.IN);
          storedProcedureQuery.registerStoredProcedureParameter("i_disc_amt", BigDecimal.class, ParameterMode.IN);
          storedProcedureQuery.registerStoredProcedureParameter("i_refund_amt", BigDecimal.class, ParameterMode.IN);
          storedProcedureQuery.registerStoredProcedureParameter("i_modified_by", String.class, ParameterMode.IN);
          storedProcedureQuery.registerStoredProcedureParameter("i_gross_amt", BigDecimal.class, ParameterMode.IN);

          System.out.println("Executing sp_updRttItem with parameters:");
          System.out.println("i_rtt_item_id: " + item.getRtt_item_id());
          System.out.println("i_unit_fee: " + item.getUnit_fee());
          System.out.println("i_qty: " + item.getQty());
          System.out.println("i_item_ref_no: " + item.getItem_ref_no());
          System.out.println("i_item_desc: " + item.getItem_desc());
          System.out.println("i_tax_pct: " + item.getTax_pct());
          System.out.println("i_tax_amt: " + item.getTax_amt());
          System.out.println("i_grant_cd: " + item.getGrant_cd());
          System.out.println("i_disc_amt: " + item.getDisc_amt());
          System.out.println("i_refund_amt: " + item.getNet_amt());
          System.out.println("i_modified_by: " + modifiedBy);
          System.out.println("i_gross_amt: " + item.getGross_amt());

          // Set parameters from PaymentItemDetails and context
          storedProcedureQuery.setParameter("i_rtt_item_id", item.getRtt_item_id());
          storedProcedureQuery.setParameter("i_unit_fee", item.getUnit_fee());
          storedProcedureQuery.setParameter("i_qty", item.getQty());
          storedProcedureQuery.setParameter("i_item_ref_no", item.getItem_ref_no());
          storedProcedureQuery.setParameter("i_item_desc", item.getItem_desc());
          storedProcedureQuery.setParameter("i_tax_pct", item.getTax_pct());
          storedProcedureQuery.setParameter("i_tax_amt", item.getTax_amt());
          storedProcedureQuery.setParameter("i_grant_cd", item.getGrant_cd());
          storedProcedureQuery.setParameter("i_disc_amt", item.getDisc_amt());
          storedProcedureQuery.setParameter("i_refund_amt", item.getNet_amt());
          storedProcedureQuery.setParameter("i_modified_by", modifiedBy);
          storedProcedureQuery.setParameter("i_gross_amt", item.getGross_amt());

          storedProcedureQuery.execute();

          // Retrieve and return the result (assuming the SP returns an integer result)
          Integer result = (Integer) storedProcedureQuery.getSingleResult();
          return result;
     }

     @Override
     public List<Object[]> sp_getRefundListing(RefundPTTListingDetReq req) {
          System.out.println(req.getI_dt_created_fr() + " " + req.getI_dt_created_to());
          System.out.println(req.getI_orn_dt_to() + " " + req.getI_orn_dt_fr());

          Query query = entityManager.createNativeQuery(

                    "CALL sp_getrttwflisting(:i_page, :i_size, :i_orn_no, :i_rtt_app_no, :i_refund_ty, :i_dt_created_fr,:i_dt_created_to)")
                    .setParameter("i_page", req.getI_page())
                    .setParameter("i_size", req.getI_size())
                    .setParameter("i_orn_no", req.getI_orn_no())
                    .setParameter("i_rtt_app_no", req.getI_rtt_app_no())
                    .setParameter("i_refund_ty", req.getI_refund_ty())
                    .setParameter("i_dt_created_fr", req.getI_dt_created_fr())
                    .setParameter("i_dt_created_to", req.getI_dt_created_to());

          return query.getResultList();
     }

}
