package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import com.maven.rms.interfaces.IFMSDRMemoInterface;
import com.maven.rms.models.FMSCRMemo;
import com.maven.rms.models.FMSDRMemo;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class FMSDRMemoRepository implements IFMSDRMemoInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getfmsrcpgtxn() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsrcpgtxn()");

        return query.getResultList();
    }

    @Override
    public BigInteger sp_insfmsrcpgtxn_h(BigDecimal pg_pymt_amt, Integer flag) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_insfmsrcpgtxn_h(:i_pg_pymt_amt, :i_flag)")
            .setParameter("i_pg_pymt_amt", pg_pymt_amt)
            .setParameter("i_flag", flag); // Assuming 0 is the value for flag

        return (BigInteger) query.getSingleResult();
    }
    
    // @Override
    // public Integer sp_insfmsrcpgtxn_b(BigInteger drmemo_hid, String pg_pymt_method, BigInteger mtt_pg_id, int qty,
    //                             String item_desc, BigDecimal unit_fee, String entity_nm, String entity_no, String entity_type, BigDecimal gross_amt,
    //                             String fee_detail_id, BigDecimal tax_amt, String rcpt_no, String cust_nm) {
    //     Query query = entityManager.createNativeQuery(
    //         "CALL sp_insfmsrcpgtxn_b(:i_drmemo_hid, :i_pg_pymt_method, :i_mtt_pg_id, :i_qty, :i_item_desc, :i_unit_fee, :i_entity_nm, :i_entity_no, :i_entity_type, :i_gross_amt, :i_fee_detail_id, :i_tax_amt, :i_rcpt_no, :i_cust_nm)")
    //         .setParameter("i_drmemo_hid", drmemo_hid)
    //         .setParameter("i_pg_pymt_method", pg_pymt_method)
    //         .setParameter("i_mtt_pg_id", mtt_pg_id)
    //         .setParameter("i_qty", qty)
    //         .setParameter("i_item_desc", item_desc)
    //         .setParameter("i_unit_fee", unit_fee)
    //         .setParameter("i_entity_nm", entity_nm)
    //         .setParameter("i_entity_no", entity_no)
    //         .setParameter("i_entity_type", entity_type)
    //         .setParameter("i_gross_amt", gross_amt)
    //         .setParameter("i_fee_detail_id", fee_detail_id)
    //         .setParameter("i_tax_amt", tax_amt)
    //         .setParameter("i_rcpt_no", rcpt_no)
    //         .setParameter("i_cust_nm", cust_nm);

    //     return (Integer) query.getSingleResult();
    // }

    @Override
    public Integer sp_insfmsrcpgtxn_b(BigInteger drmemo_hid, FMSDRMemo fmsdrMemo, Integer flag) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_insfmsrcpgtxn_b(:i_drmemo_hid, :i_pg_pymt_method, :i_mtt_pg_id, :i_qty, :i_item_desc, :i_unit_fee, :i_entity_nm, :i_entity_no, :i_entity_type, :i_gross_amt, :i_fee_detail_id, :i_tax_amt, :i_rcpt_no, :i_cust_nm, :i_dps_id, :i_dps_task, :i_rcpg_txn_id, :i_flag, :i_in_fms_posting)")
            .setParameter("i_drmemo_hid", drmemo_hid)
            .setParameter("i_pg_pymt_method", fmsdrMemo.getPg_pymt_method())
            .setParameter("i_mtt_pg_id", fmsdrMemo.getMtt_pg_id())
            .setParameter("i_qty", fmsdrMemo.getQty())
            .setParameter("i_item_desc", fmsdrMemo.getItem_desc())
            .setParameter("i_unit_fee", fmsdrMemo.getUnit_fee() == null ? fmsdrMemo.getPg_pymt_amt() : fmsdrMemo.getUnit_fee() ) // Use pg_pymt_amt if unit_fee is true, else use 0
            .setParameter("i_entity_nm", fmsdrMemo.getEnt_nm())
            .setParameter("i_entity_no", fmsdrMemo.getEnt_no())
            .setParameter("i_entity_type", fmsdrMemo.getEnt_ty())
            .setParameter("i_gross_amt", fmsdrMemo.getGross_amt())
            .setParameter("i_fee_detail_id", fmsdrMemo.getFee_detail_id() == null ? "" : fmsdrMemo.getFee_detail_id())
            .setParameter("i_tax_amt", fmsdrMemo.getTax_amt())
            .setParameter("i_rcpt_no", fmsdrMemo.getRcpt_no())
            .setParameter("i_cust_nm", fmsdrMemo.getCust_nm())
            .setParameter("i_dps_id", fmsdrMemo.getDepositID())
            .setParameter("i_dps_task", fmsdrMemo.getDepositTask())
            .setParameter("i_rcpg_txn_id", fmsdrMemo.getRc_pgtxn_id())
            .setParameter("i_flag", flag)
            .setParameter("i_in_fms_posting", 0); // Assuming 2 is the value for in_fms_posting

        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_insfmsrcpgtxn_b(BigInteger drmemo_hid, FMSCRMemo fmscrMemo, Integer flag) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_insfmsrcpgtxn_b(:i_drmemo_hid, :i_pg_pymt_method, :i_mtt_pg_id, :i_qty, :i_item_desc, :i_unit_fee, :i_entity_nm, :i_entity_no, :i_entity_type, :i_gross_amt, :i_fee_detail_id, :i_tax_amt, :i_rcpt_no, :i_cust_nm, :i_dps_id, :i_dps_task, :i_rcpg_txn_id, :i_flag, :i_in_fms_posting)")
            .setParameter("i_drmemo_hid", drmemo_hid)
            .setParameter("i_pg_pymt_method", fmscrMemo.getPg_pymt_method())
            .setParameter("i_mtt_pg_id", fmscrMemo.getMtt_pg_id())
            .setParameter("i_qty", fmscrMemo.getQty())
            .setParameter("i_item_desc", fmscrMemo.getItem_desc())
            .setParameter("i_unit_fee", fmscrMemo.getUnit_fee())
            .setParameter("i_entity_nm", fmscrMemo.getEnt_nm())
            .setParameter("i_entity_no", fmscrMemo.getEnt_no())
            .setParameter("i_entity_type", fmscrMemo.getEnt_ty())
            .setParameter("i_gross_amt", fmscrMemo.getGross_amt())
            .setParameter("i_fee_detail_id", fmscrMemo.getFee_detail_id() == null ? "" : fmscrMemo.getFee_detail_id())
            .setParameter("i_tax_amt", fmscrMemo.getTax_amt())
            .setParameter("i_rcpt_no", fmscrMemo.getRcpt_no())
            .setParameter("i_cust_nm", fmscrMemo.getCust_nm())
            .setParameter("i_dps_id", fmscrMemo.getDepositID())
            .setParameter("i_dps_task", fmscrMemo.getDepositTask())
            .setParameter("i_rcpg_txn_id", null)
            .setParameter("i_flag", flag)
            .setParameter("i_in_fms_posting", 1);

        return (Integer) query.getSingleResult();
    }
    
    public Integer sp_insfmsdrmemo_b(BigInteger drmemo_hid, FMSDRMemo fmsdrMemo, Integer flag) {
    	/*String debug = "CALL sp_insfmsdrmemo_b(" + (drmemo_hid == null ? null : drmemo_hid)
    	+", " + (fmsdrMemo.getPg_pymt_method() == null ? null : fmsdrMemo.getPg_pymt_method())
		+", " + (fmsdrMemo.getMtt_pg_id() == null ? null : fmsdrMemo.getMtt_pg_id())
		+", " + (fmsdrMemo.getQty() == null ? null : fmsdrMemo.getQty())
		+", " + (fmsdrMemo.getItem_desc() == null ? null : "'" + fmsdrMemo.getItem_desc() + "'")
		+", " + (fmsdrMemo.getUnit_fee() == null ? null : fmsdrMemo.getUnit_fee())
		+", " + (fmsdrMemo.getEnt_nm() == null ? null : "'" + fmsdrMemo.getEnt_nm() + "'")
		+", " + (fmsdrMemo.getEnt_no() == null ? null : "'" + fmsdrMemo.getEnt_no() + "'")
		+", " + (fmsdrMemo.getEnt_ty() == null ? null : "'" + fmsdrMemo.getEnt_ty() + "'")
		+", " + (fmsdrMemo.getGross_amt() == null ? null : fmsdrMemo.getGross_amt())
		+", " + (fmsdrMemo.getFee_detail_id() == null ? "''" : "'" + fmsdrMemo.getFee_detail_id() + "'")
		+", " + (fmsdrMemo.getTax_amt() == null ? null : fmsdrMemo.getTax_amt())
		+", " + (fmsdrMemo.getRcpt_no() == null ? null : "'" + fmsdrMemo.getRcpt_no() + "'")
		+", " + (fmsdrMemo.getCust_nm() == null ? null : "'" + fmsdrMemo.getCust_nm() + "'")
		+", " + (fmsdrMemo.getDepositID() == null ? null : "'" + fmsdrMemo.getDepositID() + "'")
		+", " + (fmsdrMemo.getDepositTask() == null ? null : "'" + fmsdrMemo.getDepositTask() + "'")
		+", " + (fmsdrMemo.getRc_pgtxn_id() == null ? "0" :fmsdrMemo.getRc_pgtxn_id())
		+", " + (flag == null ? null : flag)
		+", " + (fmsdrMemo.getAcct_nm() == null ? null : "'" + fmsdrMemo.getAcct_nm() + "'")
		+", " + (fmsdrMemo.getAcct() == null ? null : "'" + fmsdrMemo.getAcct() + "'")
		+", " + (fmsdrMemo.getBranch() == null ? null : "'" + fmsdrMemo.getBranch() + "'")
		+", " + (fmsdrMemo.getSub_acct() == null ? null : "'" + fmsdrMemo.getSub_acct() + "'")
		+", " + (fmsdrMemo.getBil_child_id() == null ? null : fmsdrMemo.getBil_child_id())
		+", " + (fmsdrMemo.getCoa1() == null ? null : "'" + fmsdrMemo.getCoa1() + "'")
		+")";
    	
    	log.error(debug);
		System.out.println(debug);*/
        Query query = entityManager.createNativeQuery(
            "CALL sp_insfmsdrmemo_b(:i_drmemo_hid, :i_pg_pymt_method, :i_mtt_pg_id, :i_qty, :i_item_desc, "
            + ":i_unit_fee, :i_entity_nm, :i_entity_no, :i_entity_type, :i_gross_amt, :i_fee_detail_id, "
            + ":i_tax_amt, :i_rcpt_no, :i_cust_nm, :i_dps_id, :i_dps_task, :i_rcpg_txn_id, :i_flag, :i_acct_nm,"
            + ":i_acct, :i_branch, :i_sub_acct, :i_bil_child_id, :i_coa1)")
            .setParameter("i_drmemo_hid", drmemo_hid)
            .setParameter("i_pg_pymt_method", fmsdrMemo.getPg_pymt_method())
            .setParameter("i_mtt_pg_id", fmsdrMemo.getMtt_pg_id())
            .setParameter("i_qty", fmsdrMemo.getQty())
            .setParameter("i_item_desc", fmsdrMemo.getItem_desc())
            .setParameter("i_unit_fee", fmsdrMemo.getUnit_fee())
            .setParameter("i_entity_nm", fmsdrMemo.getEnt_nm())
            .setParameter("i_entity_no", fmsdrMemo.getEnt_no())
            .setParameter("i_entity_type", fmsdrMemo.getEnt_ty())
            .setParameter("i_gross_amt", fmsdrMemo.getGross_amt())
            .setParameter("i_fee_detail_id", fmsdrMemo.getFee_detail_id() == null ? "" : fmsdrMemo.getFee_detail_id())
            .setParameter("i_tax_amt", fmsdrMemo.getTax_amt())
            .setParameter("i_rcpt_no", fmsdrMemo.getRcpt_no())
            .setParameter("i_cust_nm", fmsdrMemo.getCust_nm())
            .setParameter("i_dps_id", fmsdrMemo.getDepositID())
            .setParameter("i_dps_task", fmsdrMemo.getDepositTask())
            .setParameter("i_rcpg_txn_id", fmsdrMemo.getRc_pgtxn_id()!=null?fmsdrMemo.getRc_pgtxn_id():BigInteger.ZERO) //has == check in SP. Cannot be null
            .setParameter("i_flag", flag)
            .setParameter("i_acct_nm", fmsdrMemo.getAcct_nm())
            .setParameter("i_acct", fmsdrMemo.getAcct())
            .setParameter("i_branch", fmsdrMemo.getBranch())
            .setParameter("i_sub_acct", fmsdrMemo.getSub_acct())
            .setParameter("i_bil_child_id", fmsdrMemo.getBil_child_id())
            .setParameter("i_coa1", fmsdrMemo.getCoa1());

        return (Integer) query.getSingleResult();
    }


    // @Override
    // public Integer sp_insfmsrcpgtxn_f(BigInteger drmemo_hid, BigInteger mtt_pg_id) {
    //     Query query = entityManager.createNativeQuery(
    //         "CALL sp_insfmsrcpgtxn_f(:i_drmemo_hid, :i_mtt_pg_id)")
    //         .setParameter("i_drmemo_hid", drmemo_hid)
    //         .setParameter("i_mtt_pg_id", mtt_pg_id);

    //     return (Integer) query.getSingleResult();
    // }

    @Override
    public Integer sp_insfmsrcpgtxn_f(BigInteger drmemo_hid, FMSDRMemo fmsdrMemo) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_insfmsrcpgtxn_f(:i_drmemo_hid, :i_mtt_pg_id)")
            .setParameter("i_drmemo_hid", drmemo_hid)
            .setParameter("i_mtt_pg_id", fmsdrMemo.getMtt_pg_id());

        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_insfmsrcpgtxn_f(BigInteger drmemo_hid, FMSCRMemo fmscrMemo) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_insfmsrcpgtxn_f(:i_drmemo_hid, :i_mtt_pg_id)")
            .setParameter("i_drmemo_hid", drmemo_hid)
            .setParameter("i_mtt_pg_id", fmscrMemo.getMtt_pg_id());

        return (Integer) query.getSingleResult();
    }
    
    public Integer sp_insfmsdrmemo_f(BigInteger drmemo_hid, String fms_ref_no, String doc_type) {
    	/*String debug = "CALL sp_insfmsdrmemo_f(" + (drmemo_hid == null ? null : drmemo_hid)
			+ ", " + (fms_ref_no == null ? "''" : "'" + fms_ref_no + "'")
			+ ", " + (doc_type == null ? null : "'" + doc_type + "'")+ ")";
	
		log.error(debug);
		System.out.println(debug);*/
        Query query = entityManager.createNativeQuery(
                "CALL sp_insfmsdrmemo_f(:i_drmemo_hid, :i_fms_ref_no, :i_doc_type)")
                .setParameter("i_drmemo_hid", drmemo_hid)
                .setParameter("i_fms_ref_no", fms_ref_no!=null?fms_ref_no:"") //has == check in SP. Cannot be null
                .setParameter("i_doc_type", doc_type);

            return (Integer) query.getSingleResult();
    }

    public BigInteger sp_insfmsdrmemo_h(FMSDRMemo fmsdrMemo) {    	
    	/*String debug = "CALL sp_insfmsdrmemo_h(" + (fmsdrMemo.getType() == null ? null : "'" + fmsdrMemo.getType() + "'")
				+ ", " + (fmsdrMemo.getLink_branch() == null ? null : "'" + fmsdrMemo.getLink_branch() + "'")
				+ ", " + (fmsdrMemo.getPg_pymt_amt() == null ? null : fmsdrMemo.getPg_pymt_amt())
				+ ", " + (fmsdrMemo.getDesc() == null ? null : "'" + fmsdrMemo.getDesc() + "'")
				+ ", " + (fmsdrMemo.getAttr_ext_sys() == null ? null : "'" + fmsdrMemo.getAttr_ext_sys() + "'")
				+ ", " + "null";
    	*/
    	//if(fmsdrMemo.getGenPdf() != null && fmsdrMemo.getGenPdf() > 0) {
    	if(fmsdrMemo.getCust() != null && !fmsdrMemo.getCust().isEmpty()) {
		/*	debug = debug + ", " + (fmsdrMemo.getGenPdf() == null ? "0" : fmsdrMemo.getGenPdf())
					+ ", " + (fmsdrMemo.getCust() == null ? null : "'" + fmsdrMemo.getCust() + "'") + ");";
			log.error(debug);
			System.out.println(debug);*/
	    		
	        Query query = entityManager.createNativeQuery(
	                "CALL sp_insfmsdrmemo_h(:i_type, :i_link_branch, :i_pg_pymt_amt,"
	                + ":i_desc, :i_attr_ext_sys, :i_fms_ref_no, :i_genpdf, :i_cust_id)")
	                .setParameter("i_type", fmsdrMemo.getType())
	                .setParameter("i_link_branch", fmsdrMemo.getLink_branch())
	                .setParameter("i_pg_pymt_amt", fmsdrMemo.getPg_pymt_amt())
	                .setParameter("i_desc", fmsdrMemo.getDesc())
	                .setParameter("i_attr_ext_sys", fmsdrMemo.getAttr_ext_sys())
	                .setParameter("i_fms_ref_no", null)//fmsdrMemo.getFms_ref_no()) //fms_ref_no in header varies from api call, is null on fresh insert, will fill on successful api call
	                .setParameter("i_genpdf", fmsdrMemo.getGenPdf() == null ? 0 : fmsdrMemo.getGenPdf())
	                .setParameter("i_cust_id", fmsdrMemo.getCust());
	        return (BigInteger) query.getSingleResult();
    	}
		/*
		debug = debug + ", " + ", " + (fmsdrMemo.getAcct_nm() == null ? null : "'" + fmsdrMemo.getAcct_nm() + "'")
				+ (fmsdrMemo.getGenPdf() == null ? "0" : fmsdrMemo.getGenPdf()) + ");";
		log.error(debug);
		System.out.println(debug);*/
        Query query = entityManager.createNativeQuery(
                "CALL sp_insfmsdrmemo_h1(:i_type, :i_link_branch, :i_pg_pymt_amt,"
                + ":i_desc, :i_attr_ext_sys, :i_fms_ref_no, :i_acct_nm, :i_genpdf)")
                .setParameter("i_type", fmsdrMemo.getType())
                .setParameter("i_link_branch", fmsdrMemo.getLink_branch())
                .setParameter("i_pg_pymt_amt", fmsdrMemo.getPg_pymt_amt())
                .setParameter("i_desc", fmsdrMemo.getDesc())
                .setParameter("i_attr_ext_sys", fmsdrMemo.getAttr_ext_sys())
                .setParameter("i_fms_ref_no", null)// fmsdrMemo.getFms_ref_no()) //fms_ref_no in header varies from api call, is null on fresh insert, will fill on successful api call
                .setParameter("i_acct_nm", fmsdrMemo.getAcct_nm())
                .setParameter("i_genpdf", fmsdrMemo.getGenPdf() == null ? 0 : fmsdrMemo.getGenPdf());
        return (BigInteger) query.getSingleResult();
    }
    
    @Override
    public List<Object[]> sp_getfmsdrmemo() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsdrmemo()");

        return query.getResultList();
    }
    
    public List<Object[]> sp_getfmsdrmemobyhid(BigInteger hid) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsdrmemobyhid(:i_hid)")
                .setParameter("i_hid", hid);

        return query.getResultList();
    }
    
    public Object[] sp_getfmsdrmemobyarifmsrefno(String ari_fms_ref_no) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsdrmemobyarifmsrefno(:i_fms_ref_no)")
                .setParameter("i_fms_ref_no", ari_fms_ref_no);

        return (Object[]) query.getSingleResult();
    }


    // @Override
    // public Integer sp_updfmsdrmemo(BigInteger drmemo_hid, String resp_attr_ext_sys, String fms_ref_no, String resp_co,
    //         String resp_status, String resp_msg, Date resp_dt) {
    //     Query query = entityManager.createNativeQuery(
    //         "CALL sp_updfmsdrmemo(:i_drmemo_hid, :i_resp_attr_ext_sys, :i_fms_ref_no, :i_resp_co, :i_resp_status, :i_resp_msg, :i_resp_dt)")
    //         .setParameter("i_drmemo_hid", drmemo_hid)
    //         .setParameter("i_resp_attr_ext_sys", resp_attr_ext_sys)
    //         .setParameter("i_fms_ref_no", fms_ref_no)
    //         .setParameter("i_resp_co", resp_co)
    //         .setParameter("i_resp_status", resp_status)
    //         .setParameter("i_resp_msg", resp_msg)
    //         .setParameter("i_resp_dt", resp_dt);
        
    //     return (Integer) query.getSingleResult();
    // }

    @Override
    public Integer sp_updfmsdrmemo(BigInteger drmemo_hid, FMSDRMemo fmsdrMemo) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_updfmsdrmemo(:i_drmemo_hid, :i_resp_attr_ext_sys, :i_fms_ref_no, :i_resp_co, :i_resp_status, :i_resp_msg, :i_resp_dt)")
            .setParameter("i_drmemo_hid", drmemo_hid)
            .setParameter("i_resp_attr_ext_sys", fmsdrMemo.getResp_attr_ext_sys())
            .setParameter("i_fms_ref_no", fmsdrMemo.getFms_ref_no())
            .setParameter("i_resp_co", fmsdrMemo.getResp_co())
            .setParameter("i_resp_status", fmsdrMemo.getResp_status())
            .setParameter("i_resp_msg", fmsdrMemo.getResp_msg())
            .setParameter("i_resp_dt", fmsdrMemo.getResp_dt());
        
        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_updfmsdrmemohid(BigInteger drmemo_hid, BigInteger crmemo_hid) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_updfmsdrmemohid(:i_drmemo_hid, :i_crmemo_hid)")
            .setParameter("i_drmemo_hid", drmemo_hid)
            .setParameter("i_crmemo_hid", crmemo_hid);
        
        return (Integer) query.getSingleResult();
    }

    public Integer sp_rollbackfmsdrmemohbh(BigInteger drmemo_hid) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_rollbackfmsdrmemohbh(:i_drmemo_hid)")
                .setParameter("i_drmemo_hid", drmemo_hid);
            
            return (Integer) query.getSingleResult();
    }
}