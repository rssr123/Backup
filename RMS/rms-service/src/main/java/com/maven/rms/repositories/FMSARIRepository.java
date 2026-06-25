package com.maven.rms.repositories;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.stereotype.Repository;
import com.maven.rms.interfaces.IFMSARIInterface;
import com.maven.rms.models.FMSARIImmediateRequest;
import com.maven.rms.models.FMSARIModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class FMSARIRepository implements IFMSARIInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getfmsmtt() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsmtt()");
        return query.getResultList();
    }

    @Override
    public BigInteger sp_insfmsmtth(String customer, LocalDateTime inv_dt) {
        // public BigInteger sp_insfmsmtth(BigInteger mtt_pg_id, BigDecimal pg_pymt_amt,
        // int qty, String item_desc,BigDecimal unit_fee, String rcpt_no, String
        // cust_nm, String entity_nm, String entity_no,String entity_type, BigDecimal
        // gross_amt, String fee_detail_id, String pg_pymt_method, BigDecimal tax_amt,
        // String customer) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_insfmsmtth(:i_customer, :i_inv_dt)")
                // "CALL
                // sp_insfmsmtth(:i_mtt_pg_id,:i_pg_pymt_amt,:i_qty,:i_item_desc,:i_unit_fee,:i_rcpt_no,:i_cust_nm,
                // :i_entity_nm, :i_entity_no,:i_entity_type, :i_gross_amt,:i_fee_detail_id,
                // :i_pg_pymt_method,:i_tax_amt,:i_customer)")
                // .setParameter("i_mtt_pg_id", mtt_pg_id)
                // .setParameter("i_pg_pymt_amt", pg_pymt_amt)
                // .setParameter("i_qty", qty)
                // .setParameter("i_item_desc", item_desc)
                // .setParameter("i_unit_fee", unit_fee)
                // .setParameter("i_rcpt_no", rcpt_no)
                // .setParameter("i_cust_nm", cust_nm)
                // .setParameter("i_entity_nm", entity_nm)
                // .setParameter("i_entity_no", entity_no)
                // .setParameter("i_entity_type", entity_type)
                // .setParameter("i_gross_amt", gross_amt)
                // .setParameter("i_fee_detail_id", fee_detail_id)
                // .setParameter("i_pg_pymt_method", pg_pymt_method)
                // .setParameter("i_tax_amt", tax_amt)
                .setParameter("i_customer", customer)
                .setParameter("i_inv_dt", inv_dt);

        return (BigInteger) query.getSingleResult();
    }

    public BigInteger sp_insfmsari_h(FMSARIModel ari) {
        
         System.out.println("CALL sp_insfmsari_h('" + ari.getType() + "', '" +
         ari.getLink_branch() + "', '" +
         ari.getCust() + "', '" + ari.getDesc() + "', '" + ari.getAttr_ext_sys() +
         "', '" + ari.getCreated_by() + "', " + ari.getGeneratePDF() + ", " + ari.getAmt()+ ", "
         + (ari.getInv_dt() != null ? java.sql.Timestamp.valueOf(ari.getInv_dt()) 
        		 : java.sql.Timestamp.valueOf(LocalDateTime.now())) + ");");
         
        Query query = entityManager.createNativeQuery(
                "CALL sp_insfmsari_h(:i_type, :i_link_branch, :i_customer, :i_desc, :i_attr_ext_sys, :i_user, :i_genpdf, :i_amt, :i_inv_dt)")
                .setParameter("i_type", ari.getType())
                .setParameter("i_link_branch", ari.getLink_branch())
                .setParameter("i_customer", ari.getCust())
                .setParameter("i_desc", ari.getDesc())
                .setParameter("i_attr_ext_sys", ari.getAttr_ext_sys())
                .setParameter("i_user", ari.getCreated_by())
                .setParameter("i_genpdf", ari.getGeneratePDF())
                .setParameter("i_amt", ari.getAmt())
                .setParameter("i_inv_dt", ari.getInv_dt() != null ? ari.getInv_dt() : LocalDateTime.now());
        return (BigInteger) query.getSingleResult();
    }

    // @Override
    // public BigInteger sp_insfmsmttb(BigInteger mtt_pg_id, BigDecimal pg_pymt_amt,
    // int qty, String item_desc,
    // BigDecimal unit_fee, String rcpt_no, String cust_nm, String entity_nm, String
    // entity_no,
    // String entity_type, BigDecimal gross_amt, String fee_detail_id, String
    // pg_pymt_method, BigDecimal tax_amt,
    // String customer, BigInteger hid, String item_ref_no, String cp_no) {
    // Query query = entityManager.createNativeQuery(
    // "CALL
    // sp_insfmsmttb(:i_mtt_pg_id,:i_pg_pymt_amt,:i_qty,:i_item_desc,:i_unit_fee,:i_rcpt_no,:i_cust_nm,
    // :i_entity_nm, :i_entity_no,:i_entity_type, :i_gross_amt,:i_fee_detail_id,
    // :i_pg_pymt_method,:i_tax_amt,:i_customer,:i_hid,:i_item_ref_no,:i_cp_no)")
    // .setParameter("i_mtt_pg_id", mtt_pg_id)
    // .setParameter("i_pg_pymt_amt", pg_pymt_amt)
    // .setParameter("i_qty", qty)
    // .setParameter("i_item_desc", item_desc)
    // .setParameter("i_unit_fee", unit_fee)
    // .setParameter("i_rcpt_no", rcpt_no)
    // .setParameter("i_cust_nm", cust_nm)
    // .setParameter("i_entity_nm", entity_nm)
    // .setParameter("i_entity_no", entity_no)
    // .setParameter("i_entity_type", entity_type)
    // .setParameter("i_gross_amt", gross_amt)
    // .setParameter("i_fee_detail_id", fee_detail_id)
    // .setParameter("i_pg_pymt_method", pg_pymt_method)
    // .setParameter("i_tax_amt", tax_amt)
    // .setParameter("i_customer", customer)
    // .setParameter("i_hid", hid)
    // .setParameter("i_item_ref_no", item_ref_no)
    // .setParameter("i_cp_no", cp_no);

    // return (BigInteger) query.getSingleResult();
    // }

    @Override
    public BigInteger sp_insfmsmttb(FMSARIModel fmsariModel, BigInteger hid, Integer flag) {
    	/*
    	log.error("CALL sp_insfmsmttb(null, " + fmsariModel.getPg_pymt_amt().toPlainString() + ", "
    			+ Integer.toString(fmsariModel.getQty()) + ", '"
    			+ fmsariModel.getItem_desc() + "', "
    			+ fmsariModel.getUnit_fee().toPlainString() + ", null, '"
    			+ fmsariModel.getCust_nm() + "', '"
    			+ fmsariModel.getEnt_nm() + "', '"
    			+ fmsariModel.getEnt_no() + "', '"
    			+ fmsariModel.getEnt_ty() + "', "
    			+ fmsariModel.getGross_amt().toPlainString() + ", '"
    			+ fmsariModel.getFee_detail_pk().toString() + "', '"
    			+ fmsariModel.getPg_pymt_method() + "', "
    			+ fmsariModel.getTax_amt().toPlainString() + ", '"
    			+ fmsariModel.getCustomer() + "', " + hid.toString() + ", '"
    			+ fmsariModel.getItem_ref_no() + "', null, " + fmsariModel.getDiscAmt().toPlainString() + ", "
    			+ "null, null, " + Integer.toString(flag) + ", " + Integer.toString(fmsariModel.getBil_child_id()) + ", "
    			+ fmsariModel.getNet_amt().toPlainString() + ");"
    			);*/
        Query query = entityManager.createNativeQuery(
                // "CALL
                // sp_insfmsmttb(:i_mtt_pg_id,:i_pg_pymt_amt,:i_qty,:i_item_desc,:i_unit_fee,:i_rcpt_no,:i_cust_nm,
                // :i_entity_nm, :i_entity_no,:i_entity_type, :i_gross_amt,:i_fee_detail_id,
                // :i_pg_pymt_method,:i_tax_amt,:i_customer,:i_hid,:i_item_ref_no,:i_cp_no,:i_disc_amt,:i_deposit_id,:i_deposit_task,:i_flag)")
                // //change to use fee_detail_pk
                "CALL sp_insfmsmttb(:i_mtt_pg_id,:i_pg_pymt_amt,:i_qty,:i_item_desc,:i_unit_fee,:i_rcpt_no,:i_cust_nm, :i_entity_nm, "
                        + ":i_entity_no,:i_entity_type, :i_gross_amt,:i_fee_detail_pk, :i_pg_pymt_method,:i_tax_amt,:i_customer,:i_hid,"
                        + ":i_item_ref_no,:i_cp_no,:i_disc_amt,:i_deposit_id,:i_deposit_task,:i_flag,:i_bil_child_id,:i_net_amt,:i_lit_item_ref)")
                .setParameter("i_mtt_pg_id", fmsariModel.getMtt_pg_id())
                .setParameter("i_pg_pymt_amt", fmsariModel.getPg_pymt_amt())
                .setParameter("i_qty", fmsariModel.getQty())
                .setParameter("i_item_desc", fmsariModel.getItem_desc())
                .setParameter("i_unit_fee", fmsariModel.getUnit_fee())
                .setParameter("i_rcpt_no", fmsariModel.getRcpt_no())
                .setParameter("i_cust_nm", fmsariModel.getCust_nm())
                .setParameter("i_entity_nm", fmsariModel.getEnt_nm() == null ? "" : fmsariModel.getEnt_nm())
                .setParameter("i_entity_no", fmsariModel.getEnt_no() == null ? "" : fmsariModel.getEnt_no())
                .setParameter("i_entity_type", fmsariModel.getEnt_ty() == null ? "" : fmsariModel.getEnt_ty())
                .setParameter("i_gross_amt", fmsariModel.getGross_amt())
                .setParameter("i_fee_detail_pk", fmsariModel.getFee_detail_pk())
                // .setParameter("i_fee_detail_id", fmsariModel.getFee_detail_id()) //change to
                // use fee_detail_pk
                .setParameter("i_pg_pymt_method", fmsariModel.getPg_pymt_method())
                .setParameter("i_tax_amt", fmsariModel.getTax_amt())
                .setParameter("i_customer", fmsariModel.getCustomer())
                .setParameter("i_hid", hid)
                .setParameter("i_item_ref_no", fmsariModel.getItem_ref_no())
                .setParameter("i_cp_no", fmsariModel.getCp_no())
                .setParameter("i_disc_amt", fmsariModel.getDiscAmt())
                .setParameter("i_deposit_id", fmsariModel.getDepositID())
                .setParameter("i_deposit_task", fmsariModel.getDepositTask())
                .setParameter("i_flag", flag)
                .setParameter("i_bil_child_id", fmsariModel.getBil_child_id())
                .setParameter("i_net_amt", fmsariModel.getNet_amt())
                .setParameter("i_lit_item_ref", fmsariModel.getLit_item_ref());
        return (BigInteger) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_getfmsari() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsari()");
        return query.getResultList();
    }

    public List<Object[]> sp_getfmsaribybilchildid(Integer bil_child_id) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsaribybilchildid(:i_bil_child_id)")
                .setParameter("i_bil_child_id", bil_child_id);
        return query.getResultList();
    }

    public Integer sp_getfmsarihidbyaribodybilchildid(Integer bil_child_id) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsarihidbyaribodybilchildid(:i_bil_child_id)")
                .setParameter("i_bil_child_id", bil_child_id);
        Integer result = (Integer) query.getSingleResult();
        return result != null ? result : 0;
    }

    public String sp_getfmsarirespcodebyarihid(Integer i_ari_hid) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsarirespcodebyarihid(:i_ari_hid)")
                .setParameter("i_ari_hid", i_ari_hid);
        return (String) query.getSingleResult();
    }

    public String sp_getfmsarirespcodebybilchildid(Integer i_bil_child_id) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsarirespcodebybilchildid(:i_bil_child_id)")
                .setParameter("i_bil_child_id", i_bil_child_id);
        return (String) query.getSingleResult();
    }

    public Integer sp_deactivatefmsaribyarihid(Integer i_ari_hid) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_deactivatefmsaribyarihid(:i_ari_hid)")
                .setParameter("i_ari_hid", i_ari_hid);
        Integer result = (Integer) query.getSingleResult();
        return result != null ? result : 0;
    }

    @Override
    public List<Object[]> sp_getfmsariimmediate(FMSARIImmediateRequest fmsARIImmediateRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsariimmediate(:i_rms_batch_no, :i_otc_cash_s_id, :i_otc_mo_s_id, :i_otc_emv_s_id, :i_otc_che_id, :i_otc_bd_id, :i_non_bil_id)")
                .setParameter("i_rms_batch_no", fmsARIImmediateRequest.getI_rms_batch_no())
                .setParameter("i_otc_cash_s_id", fmsARIImmediateRequest.getI_otc_cash_s_id())
                .setParameter("i_otc_mo_s_id", fmsARIImmediateRequest.getI_otc_mo_s_id())
                .setParameter("i_otc_emv_s_id", fmsARIImmediateRequest.getI_otc_emv_s_id())
                .setParameter("i_otc_che_id", fmsARIImmediateRequest.getI_otc_che_id())
                .setParameter("i_otc_bd_id", fmsARIImmediateRequest.getI_otc_bd_id())
                .setParameter("i_non_bil_id", fmsARIImmediateRequest.getI_non_bil_id());
        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getfmsarijson(String i_rms_batch_no) {
            Query query = entityManager.createNativeQuery(
                            "CALL sp_getfmsarijson(:i_rms_batch_no)")
                            .setParameter("i_rms_batch_no", i_rms_batch_no);
            return query.getResultList();
    }


    // @Override
    // public Integer sp_updfmsari(String resp_attr_ext_sys, String fms_ref_no,
    // String resp_co,
    // String resp_status, String resp_msg, String resp_dt) {
    // Query query = entityManager.createNativeQuery(
    // "CALL sp_updfmsari(:i_resp_attr_ext_sys, :i_fms_ref_no, :i_resp_ext_ref_no,
    // :i_resp_status, :i_resp_msg, :i_resp_dt)")
    // .setParameter("i_resp_attr_ext_sys", resp_attr_ext_sys)
    // .setParameter("i_fms_ref_no", fms_ref_no)
    // .setParameter("i_resp_ext_ref_no", resp_co)
    // .setParameter("i_resp_status", resp_status)
    // .setParameter("i_resp_msg", resp_msg)
    // .setParameter("i_resp_dt", resp_dt);

    // return (Integer) query.getSingleResult();
    // }

    @Override
    public Integer sp_updfmsari(FMSARIModel fmsariModel) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_updfmsari(:i_resp_attr_ext_sys, :i_fms_ref_no, :i_resp_ext_ref_no, :i_resp_status, :i_resp_msg, :i_resp_dt)")
                .setParameter("i_resp_attr_ext_sys", fmsariModel.getResp_attr_ext_sys())
                .setParameter("i_fms_ref_no", fmsariModel.getFms_ref_no())
                .setParameter("i_resp_ext_ref_no", fmsariModel.getResp_co())
                .setParameter("i_resp_status", fmsariModel.getResp_status())
                .setParameter("i_resp_msg", fmsariModel.getResp_msg())
                .setParameter("i_resp_dt", fmsariModel.getResp_dt());

        return (Integer) query.getSingleResult();
    }

}
