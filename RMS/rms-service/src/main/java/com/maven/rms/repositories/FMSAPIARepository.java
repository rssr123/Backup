package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IFMSAPIAInterface;
import com.maven.rms.models.FMSAPIA;
import com.maven.rms.models.PaymentItemDetails;

@Repository
public class FMSAPIARepository implements IFMSAPIAInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getrefunddetails() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getrefunddetails()");

        return query.getResultList();
    }

    @Override
    public Integer sp_insfmsapia(FMSAPIA fmsapia) {
        // Debug: print input parameters
        // for the vendor details
        System.out.println("Executing sp_insfmsapia with the following parameters:");
        System.out.println("i_ext_sys: " + fmsapia.getExt_sys());
        System.out.println("i_vendor_id: " + fmsapia.getVendor_id());
        System.out.println("i_vendor_nm: " + fmsapia.getVendor_nm());
        System.out.println("i_id_ty: " + fmsapia.getId_ty());
        System.out.println("i_id_no: " + fmsapia.getId_no());
        System.out.println(("i_pm: " + fmsapia.getPm()));
        System.out.println("i_p_desc: " + fmsapia.getP_desc());
        System.out.println("i_p_id: " + fmsapia.getP_id());
        System.out.println("i_p_bankname: " + fmsapia.getP_bankname());
        System.out.println("i_p_value: " + fmsapia.getP_value());
        System.out.println("i_addr1: " + fmsapia.getAddr1());
        System.out.println("i_addr2: " + fmsapia.getAddr2());
        System.out.println("i_addr3: " + fmsapia.getAddr3());
        System.out.println("i_city: " + fmsapia.getCity());
        System.out.println("i_country: " + fmsapia.getCountry());
        System.out.println("i_postcode: " + fmsapia.getPostcode());
        System.out.println("i_state: " + fmsapia.getState());
        System.out.println("i_email: " + fmsapia.getEmail());
        System.out.println("i_phone: " + fmsapia.getPhone());

        // for Invoice Header

        System.out.println("i_rtt_app_no: " + fmsapia.getRtt_app_no());
        System.out.println("i_refund_slip_no: " + fmsapia.getRefund_slip_no());
        System.out.println("i_refund_total_amt: " + fmsapia.getRefund_total_amt());

        // // for Invoice Details
        // System.out.println(fmsapia.getPayment_item_details());
        // for (int i = 0; i < fmsapia.getPayment_item_details().size(); i++) {
        //     System.out.println("i_fee_detail_id: " + fmsapia.getPayment_item_details().get(i).getFee_detail_id());
        //     System.out.println("i_item_ref_no: " + fmsapia.getPayment_item_details().get(i).getItem_ref_no());
        //     System.out.println("i_item_desc: " + fmsapia.getPayment_item_details().get(i).getItem_desc());
        //     System.out.println("i_line_no: " + fmsapia.getPayment_item_details().get(i).getLine_no());
        //     System.out.println("i_qty: " + fmsapia.getPayment_item_details().get(i).getQty());
        //     System.out.println("i_unit_fee: " + fmsapia.getPayment_item_details().get(i).getUnit_fee());
        //     System.out.println("i_gross_amt: " + fmsapia.getPayment_item_details().get(i).getGross_amt());
        //     System.out.println("i_grant_cd: " + fmsapia.getPayment_item_details().get(i).getGrant_cd());
        //     System.out.println("i_disc_amt: " + fmsapia.getPayment_item_details().get(i).getDisc_amt());
        //     System.out.println("i_tax_pct: " + fmsapia.getPayment_item_details().get(i).getTax_pct());
        //     System.out.println("i_tax_amt: " + fmsapia.getPayment_item_details().get(i).getTax_amt());
        //     System.out.println("i_net_amt: " + fmsapia.getPayment_item_details().get(i).getNet_amt());
        //     System.out.println("i_entity_type: " + fmsapia.getPayment_item_details().get(i).getEntity_type());
        //     System.out.println("i_entity_no: " + fmsapia.getPayment_item_details().get(i).getEntity_no());
        //     System.out.println("i_entity_nm: " + fmsapia.getPayment_item_details().get(i).getEntity_nm());
        // }
    
        // Create and set up the native query with named parameters
        Query query = entityManager.createNativeQuery(
                        "CALL sp_insfmsapia(" +
                    ":i_ext_sys, :i_vendor_id, :i_vendor_nm, :i_id_ty, :i_id_no, " +
                    ":i_pm, :i_p_desc, :i_p_id, :i_p_bankname, :i_p_value, " +
                    ":i_addr1, :i_addr2, :i_addr3, :i_city, " +
                    ":i_country, :i_postcode, :i_state, :i_email, " +
                    ":i_phone, :i_rtt_app_no, :i_refund_slip_no, :i_refund_total_amt" +
                ")")
                .setParameter("i_ext_sys",         fmsapia.getExt_sys())
                .setParameter("i_vendor_id",       fmsapia.getVendor_id())
                .setParameter("i_vendor_nm",       fmsapia.getVendor_nm())
                .setParameter("i_id_ty",           fmsapia.getId_ty())
                .setParameter("i_id_no",           fmsapia.getId_no())
                .setParameter("i_pm",              fmsapia.getPm())
                .setParameter("i_p_desc",          fmsapia.getP_desc())
                .setParameter("i_p_id",            fmsapia.getP_id())
                .setParameter("i_p_bankname",     fmsapia.getP_bankname())
                .setParameter("i_p_value",         fmsapia.getP_value())
                .setParameter("i_addr1",           fmsapia.getAddr1())
                .setParameter("i_addr2",           fmsapia.getAddr2())
                .setParameter("i_addr3",           fmsapia.getAddr3())
                .setParameter("i_city",            fmsapia.getCity())
                .setParameter("i_country",         fmsapia.getCountry())
                .setParameter("i_postcode",        fmsapia.getPostcode())
                .setParameter("i_state",           fmsapia.getState())
                .setParameter("i_email",           fmsapia.getEmail())
                .setParameter("i_phone",           fmsapia.getPhone())
                .setParameter("i_rtt_app_no",      fmsapia.getRtt_app_no())
                .setParameter("i_refund_slip_no",  fmsapia.getRefund_slip_no())
                .setParameter("i_refund_total_amt",fmsapia.getRefund_total_amt());
    
        // Execute the stored procedure and retrieve the result
        Integer result = (Integer) query.getSingleResult();
    
        // Debug: print the result of the stored procedure
        System.out.println("Result from sp_insfmsapia: " + result);
    
        return result;
    }

     public Integer sp_insertInvoiceItem(PaymentItemDetails item, Integer fmsApiaIH_ID, String rttAppNo) {
          try {
               Query query = entityManager.createNativeQuery(
                         "CALL sp_insfmsapia_id(:i_fmsApiaIH_ID, :i_rtt_app_no, :i_unit_fee, :i_qty, :i_item_desc, :i_item_ref_no, :i_net_amt)")
                         .setParameter("i_fmsApiaIH_ID", fmsApiaIH_ID)
                         .setParameter("i_rtt_app_no", rttAppNo)
                         .setParameter("i_unit_fee", item.getUnit_fee())
                         .setParameter("i_qty", item.getQty())
                         .setParameter("i_item_desc", item.getItem_desc())
                         .setParameter("i_item_ref_no", item.getItem_ref_no())
                         .setParameter("i_net_amt", item.getNet_amt());

               return (Integer) query.getSingleResult();

          } catch (Exception e) {
               System.out.println("Error executing sp_insfmsapia_id: " + e.getMessage());
               e.printStackTrace();
               throw e; // Re-throw the exception to the service layer
          }
     }

    @Override
    public List<Object[]> sp_getfmsapia() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsapia()");

        return query.getResultList();
    }

    @Override
    public Integer sp_updfmsapia(FMSAPIA fmsapia) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_updfmsapia(:i_fms_apia_v_id, :i_fms_ext_sys, :i_fms_ref_no, :i_fms_ven_ref, :i_fms_status, :i_fms_msg)")

                .setParameter("i_fms_apia_v_id", fmsapia.getFms_apia_v_id()) /// this need to be changed
                .setParameter("i_fms_ext_sys", fmsapia.getResp_attr_ext_sys())
                .setParameter("i_fms_ref_no", fmsapia.getFms_ref_no())
                .setParameter("i_fms_ven_ref", fmsapia.getVendor_ref())
                .setParameter("i_fms_status", fmsapia.getResp_status())
                .setParameter("i_fms_msg", fmsapia.getResp_msg())

        ;

        return (Integer) query.getSingleResult();
    }

}
