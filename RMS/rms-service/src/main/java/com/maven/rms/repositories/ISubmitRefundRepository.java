package com.maven.rms.repositories;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.ZonedDateTime;
import java.util.List;
import java.time.ZoneId;
import java.time.LocalDateTime;

import org.springframework.stereotype.Repository;

import com.maven.rms.models.RefundRequest;
import com.maven.rms.interfaces.ISubmitRefundInterface;
import com.maven.rms.models.ItemRefund;
import com.maven.rms.models.PaymentItemDetails;
import com.maven.rms.models.RefundPTTListingDetReq;

@Repository
public class ISubmitRefundRepository implements ISubmitRefundInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public String sp_insrefund_ss(RefundRequest refundRequest) {
        LocalDateTime gmtPlus8LocalDateTime = null;
        if (refundRequest.getRcpt_date() != null) {
            LocalDateTime utcDateTime = refundRequest.getRcpt_date().toLocalDateTime();
            ZonedDateTime gmtPlus8DateTime = utcDateTime.atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(ZoneId.of("GMT+8"));
            gmtPlus8LocalDateTime = gmtPlus8DateTime.toLocalDateTime();
            gmtPlus8LocalDateTime = gmtPlus8LocalDateTime.withHour(0).withMinute(0).withSecond(0).withNano(0);
        }

        String returnResult = null;

        Query query = entityManager.createNativeQuery(
                "CALL sp_insrefund_ss(:rcpt_no, :rcpt_date, :orn_no, :refund_amt, :cust_email, :sme_email, :remark, :appeal_reason)")
                .setParameter("rcpt_no", refundRequest.getRcpt_no())
                .setParameter("rcpt_date", gmtPlus8LocalDateTime)
                .setParameter("orn_no", refundRequest.getOrn_no())
                .setParameter("refund_amt", refundRequest.getRefund_amt())
                .setParameter("cust_email", refundRequest.getCust_email())
                .setParameter("sme_email", refundRequest.getSme_email())
                .setParameter("remark", refundRequest.getRemark())
                .setParameter("appeal_reason", refundRequest.getAppeal_reason());


        returnResult = (String) query.getSingleResult();

        return returnResult;
    }

    public Integer sp_insertRefundItem(PaymentItemDetails item, Integer rtt_wf_id, String createdBy,
            String modifiedBy) {
        try {
            System.out.println("Executing sp_insRttItem with params:");
            System.out.println("rtt_wf_id=" + rtt_wf_id + ", unit_fee=" +
            item.getUnit_fee() + ", qty=" + item.getQty()
            + ", item_ref_no=" + item.getItem_ref_no() + ", tax_pct=" + item.getTax_pct()
            + ", tax_amt=" + item.getTax_amt()
            + ", grant_cd=" + item.getGrant_cd() + ", disc_amt=" + item.getDisc_amt() +
            ", refund_amt=" + item.getGross_amt()
            + ", created_by=" + createdBy + ", modified_by=" + modifiedBy);

            Query query = entityManager.createNativeQuery(
                    "CALL sp_insRttItem("
                            + ":i_rtt_wf_id, :i_unit_fee, :i_qty, :i_item_ref_no, :i_item_desc, "
                            + ":i_tax_pct, :i_tax_amt, :i_grant_cd, :i_disc_amt, "
                            + ":i_gross_amt, :i_created_by, :i_modified_by, :i_net_amt, :i_entity_no, :i_entity_nm, :i_entity_type)")
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
            throw e; // Re-throw the exception to the service layer
        }
    }

    public List<Object[]> sp_getRefundPaymentItem(Integer mttId) {

        Query query = entityManager.createNativeQuery(
                "CALL sp_getrefundpaymentitem(:i_mtt_id)")
                .setParameter("i_mtt_id", mttId);
        return query.getResultList();
    }

}