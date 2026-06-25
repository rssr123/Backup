package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jfree.util.Log;
import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IFMSCRMemoInterface;
import com.maven.rms.models.FMSCRMemo;
import com.maven.rms.models.FMSDRMemo;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class FMSCRMemoRepository implements IFMSCRMemoInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Object[]> sp_getfmsrcpgmtt() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsrcpgmtt()");

        return query.getResultList();
    }
    
    @Override
    public List<Object[]> sp_getfmscreditdebit() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmscreditdebit()");

        return query.getResultList();
    }

    public BigInteger sp_insfmscrmemo_h(FMSCRMemo fmscrMemo) {
    	/*String debug = "CALL sp_insfmscrmemo_h(" + (fmscrMemo.getType() == null ? null : "'" + fmscrMemo.getType() + "'")
				+ ", " + (fmscrMemo.getLink_branch() == null ? null : "'" + fmscrMemo.getLink_branch() + "'")
				+ ", " + (fmscrMemo.getPg_pymt_amt() == null ? null : fmscrMemo.getPg_pymt_amt())
				+ ", " + (fmscrMemo.getDesc() == null ? null : "'" + fmscrMemo.getDesc() + "'")
				+ ", " + (fmscrMemo.getAttr_ext_sys() == null ? null : "'" + fmscrMemo.getAttr_ext_sys() + "'")
				+ ", " + "null";*/
    	    	
    	//if(fmscrMemo.getGenPdf() != null && fmscrMemo.getGenPdf() > 0) {
    	if(fmscrMemo.getCust() != null && !fmscrMemo.getCust().isEmpty()) {
    		/*debug = debug + ", " + (fmscrMemo.getGenPdf() == null ? "0" : fmscrMemo.getGenPdf())
    				+ ", " + (fmscrMemo.getCust() == null ? null : "'" + fmscrMemo.getCust() + "'") + ");";
    		log.error(debug);
    		System.out.println(debug);*/
    		
    		Query query = entityManager.createNativeQuery(
	                "CALL sp_insfmscrmemo_h(:i_type, :i_link_branch, :i_pg_pymt_amt,"
	                + ":i_desc, :i_attr_ext_sys, :i_fms_ref_no, :i_genpdf, :i_cust_id)")
	                .setParameter("i_type", fmscrMemo.getType())
	                .setParameter("i_link_branch", fmscrMemo.getLink_branch())
	                .setParameter("i_pg_pymt_amt", fmscrMemo.getPg_pymt_amt())
	                .setParameter("i_desc", fmscrMemo.getDesc())
	                .setParameter("i_attr_ext_sys", fmscrMemo.getAttr_ext_sys())
	                .setParameter("i_fms_ref_no", null)// fmscrMemo.getFms_ref_no()) //fms_ref_no in header varies from api call, is null on fresh insert, will fill on successful api call
	                .setParameter("i_genpdf", fmscrMemo.getGenPdf() == null ? 0 : fmscrMemo.getGenPdf())
	                .setParameter("i_cust_id", fmscrMemo.getCust());
	        return (BigInteger) query.getSingleResult();
    	}
		/*debug = debug + ", " + ", " + (fmscrMemo.getAcct_nm() == null ? null : "'" + fmscrMemo.getAcct_nm() + "'")
				+ (fmscrMemo.getGenPdf() == null ? "0" : fmscrMemo.getGenPdf()) + ");";
		log.error(debug);
		System.out.println(debug);*/
    	
    	Query query = entityManager.createNativeQuery(
            "CALL sp_insfmscrmemo_h1(:i_type, :i_link_branch, :i_pg_pymt_amt,"
            + ":i_desc, :i_attr_ext_sys, :i_fms_ref_no, :i_acct_nm, :i_genpdf)")
            .setParameter("i_type", fmscrMemo.getType())
            .setParameter("i_link_branch", fmscrMemo.getLink_branch())
            .setParameter("i_pg_pymt_amt", fmscrMemo.getPg_pymt_amt())
            .setParameter("i_desc", fmscrMemo.getDesc())
            .setParameter("i_attr_ext_sys", fmscrMemo.getAttr_ext_sys())
            .setParameter("i_fms_ref_no", null)// fmscrMemo.getFms_ref_no()) //fms_ref_no in header varies from api call, is null on fresh insert, will fill on successful api call
            .setParameter("i_acct_nm", fmscrMemo.getAcct_nm())
            .setParameter("i_genpdf", fmscrMemo.getGenPdf() == null ? 0 : fmscrMemo.getGenPdf());

		return (BigInteger) query.getSingleResult();
    }

    public BigInteger sp_insfmscrmemo_h2(FMSCRMemo fmscrMemo) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_insfmscrmemo_h(:i_type, :i_link_branch, :i_pg_pymt_amt,"
                + ":i_desc, :i_attr_ext_sys, :i_fms_ref_no, :i_acct_nm, :i_genpdf)")
                .setParameter("i_type", fmscrMemo.getType())
                .setParameter("i_link_branch", fmscrMemo.getLink_branch())
                .setParameter("i_pg_pymt_amt", fmscrMemo.getPg_pymt_amt())
                .setParameter("i_desc", fmscrMemo.getDesc())
                .setParameter("i_attr_ext_sys", fmscrMemo.getAttr_ext_sys())
                .setParameter("i_fms_ref_no", fmscrMemo.getFms_ref_no())//fmscrMemo.getFms_ref_no()) //fms_ref_no in header varies from api call, is null on fresh insert, will fill on successful api call
                .setParameter("i_acct_nm", fmscrMemo.getAcct_nm())
                .setParameter("i_genpdf", fmscrMemo.getGenPdf() == null ? 0 : fmscrMemo.getGenPdf());

            return (BigInteger) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_getfmscnbr() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmscnbr()");

        return query.getResultList();
    }

    @Override
    public BigInteger sp_insfmsrcpgmtt_h(BigDecimal pg_pymt_amt, Integer flag) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_insfmsrcpgmtt_h(:i_pg_pymt_amt, :i_flag)")
            .setParameter("i_pg_pymt_amt", pg_pymt_amt)
            .setParameter("i_flag", flag);

        return (BigInteger) query.getSingleResult();
    }
    
    // @Override
    // public Integer sp_insfmsrcpgmtt_b(BigInteger crmemo_hid, String pg_pymt_method, BigInteger mtt_pg_id, int qty,
    //                             String item_desc, BigDecimal unit_fee, String entity_nm, String entity_no, String entity_type, BigDecimal gross_amt,
    //                             String fee_detail_id, BigDecimal tax_amt, String rcpt_no, String cust_nm) {
    //     Query query = entityManager.createNativeQuery(
    //         "CALL sp_insfmsrcpgmtt_b(:i_crmemo_hid, :i_pg_pymt_method, :i_mtt_pg_id, :i_qty, :i_item_desc, :i_unit_fee, :i_entity_nm, :i_entity_no, :i_entity_type, :i_gross_amt, :i_fee_detail_id, :i_tax_amt, :i_rcpt_no, :i_cust_nm)")
    //         .setParameter("i_crmemo_hid", crmemo_hid)
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
    public Integer sp_insfmsrcpgmtt_b(BigInteger crmemo_hid, FMSCRMemo fmscrMemo, Integer flag) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_insfmsrcpgmtt_b(:i_crmemo_hid, :i_pg_pymt_method, :i_mtt_pg_id, :i_qty, :i_item_desc, :i_unit_fee, :i_entity_nm, :i_entity_no, :i_entity_type, :i_gross_amt, :i_fee_detail_id, :i_tax_amt, :i_rcpt_no, :i_cust_nm, :i_dps_id, :i_dps_task, :i_flag, :i_rc_pgmtt_id)")
            .setParameter("i_crmemo_hid", crmemo_hid)
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
            .setParameter("i_flag", flag)
            .setParameter("i_rc_pgmtt_id", fmscrMemo.getRc_pgmtt_id()!=null?fmscrMemo.getRc_pgmtt_id():BigInteger.ZERO);

        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_insfmsrcpgmtt_b(BigInteger crmemo_hid, FMSDRMemo fmsdrMemo, Integer flag) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_insfmsrcpgmtt_b(:i_crmemo_hid, :i_pg_pymt_method, :i_mtt_pg_id, :i_qty, :i_item_desc, :i_unit_fee, :i_entity_nm, :i_entity_no, :i_entity_type, :i_gross_amt, :i_fee_detail_id, :i_tax_amt, :i_rcpt_no, :i_cust_nm, :i_dps_id, :i_dps_task, :i_flag, :i_rc_pgmtt_id)")
            .setParameter("i_crmemo_hid", crmemo_hid)
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
            .setParameter("i_flag", flag)
            .setParameter("i_rc_pgmtt_id", null);

        return (Integer) query.getSingleResult();
    }
    
    public Integer sp_insfmscrmemo_b(BigInteger crmemo_hid, FMSCRMemo fmscrMemo, Integer flag) {
    	/*String debug = "CALL sp_insfmscrmemo_b(" + (crmemo_hid == null ? null : crmemo_hid)
    	+ ", " + (fmscrMemo.getPg_pymt_method() == null ? null : "'" +fmscrMemo.getPg_pymt_method() + "'")
    	+ ", " + (fmscrMemo.getMtt_pg_id() == null ? null : fmscrMemo.getMtt_pg_id())
    	+ ", " + (fmscrMemo.getQty())
    	+ ", " + (fmscrMemo.getItem_desc() == null ? null : "'" + fmscrMemo.getItem_desc() + "'")
    	+ ", " + (fmscrMemo.getUnit_fee() == null ? null : fmscrMemo.getUnit_fee())
    	+ ", " + (fmscrMemo.getEnt_nm() == null ? null : "'" + fmscrMemo.getEnt_nm() + "'")
    	+ ", " + (fmscrMemo.getEnt_no() == null ? null : "'" + fmscrMemo.getEnt_no() + "'")
    	+ ", " + (fmscrMemo.getEnt_ty() == null ? null : "'" + fmscrMemo.getEnt_ty() + "'")
    	+ ", " + (fmscrMemo.getGross_amt() == null ? null : fmscrMemo.getGross_amt())
    	+ ", " + (fmscrMemo.getFee_detail_id() == null ? "''" : "'" + fmscrMemo.getFee_detail_id() + "'")
    	+ ", " + (fmscrMemo.getTax_amt() == null ? null : fmscrMemo.getTax_amt())
    	+ ", " + (fmscrMemo.getRcpt_no() == null ? null : "'" + fmscrMemo.getRcpt_no() + "'")
    	+ ", " + (fmscrMemo.getCust_nm() == null ? null : "'" + fmscrMemo.getCust_nm() + "'")
    	+ ", " + (fmscrMemo.getDepositID() == null ? null : "'" + fmscrMemo.getDepositID() + "'")
    	+ ", " + (fmscrMemo.getDepositTask() == null ? null : "'" + fmscrMemo.getDepositTask() + "'")
    	+ ", " + (flag == null ? null : flag)
    	+ ", " + (fmscrMemo.getRc_pgmtt_id() == null ? "0" :fmscrMemo.getRc_pgmtt_id())
    	+ ", " + (fmscrMemo.getAcct_nm() == null ? null : "'" + fmscrMemo.getAcct_nm() + "'")
    	+ ", " + (fmscrMemo.getAcct() == null ? null : "'" + fmscrMemo.getAcct() + "'")
    	+ ", " + (fmscrMemo.getBranch() == null ? null : "'" + fmscrMemo.getBranch() + "'")
    	+ ", " + (fmscrMemo.getSub_acct() == null ? null : "'" + fmscrMemo.getSub_acct() + "'")
    	+ ", " + (fmscrMemo.getBil_child_id() == null ? null : fmscrMemo.getBil_child_id())
		+ ", " + (fmscrMemo.getCoa1() == null ? null : "'" + fmscrMemo.getCoa1() + "'")
		+ ")";
	
		log.error(debug);
		System.out.println(debug);*/
    	Query query = entityManager.createNativeQuery(
            "CALL sp_insfmscrmemo_b(:i_crmemo_hid, :i_pg_pymt_method, :i_mtt_pg_id, :i_qty, :i_item_desc, "
            + ":i_unit_fee, :i_entity_nm, :i_entity_no, :i_entity_type, :i_gross_amt, :i_fee_detail_id, "
            + ":i_tax_amt, :i_rcpt_no, :i_cust_nm, :i_dps_id, :i_dps_task, :i_flag, :i_rc_pgmtt_id, :i_acct_nm,"
            + ":i_acct, :i_branch, :i_sub_acct, :i_bil_child_id, :i_coa1)")
            .setParameter("i_crmemo_hid", crmemo_hid)
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
            .setParameter("i_flag", flag)
            .setParameter("i_rc_pgmtt_id", fmscrMemo.getRc_pgmtt_id()!=null?fmscrMemo.getRc_pgmtt_id():BigInteger.ZERO) //has == check in SP. Cannot be null
            .setParameter("i_acct_nm", fmscrMemo.getAcct_nm())
            .setParameter("i_acct", fmscrMemo.getAcct())
            .setParameter("i_branch", fmscrMemo.getBranch())
            .setParameter("i_sub_acct", fmscrMemo.getSub_acct())
            .setParameter("i_bil_child_id", fmscrMemo.getBil_child_id())
            .setParameter("i_coa1", fmscrMemo.getCoa1());

        return (Integer) query.getSingleResult();
    }

    @Override
    public Integer sp_insfmsrcpgmtt_f(BigInteger crmemo_hid, BigInteger mtt_pg_id, Integer i_is_drmemo) {
        Query query = entityManager.createNativeQuery(
            "CALL sp_insfmsrcpgmtt_f(:i_crmemo_hid, :i_mtt_pg_id, :i_is_drmemo)")
            .setParameter("i_crmemo_hid", crmemo_hid)
            .setParameter("i_mtt_pg_id", mtt_pg_id)
            .setParameter("i_is_drmemo", i_is_drmemo);

        return (Integer) query.getSingleResult();
    }
    
    public Integer sp_insfmscrmemo_f(BigInteger crmemo_hid, String fms_ref_no, String doc_type) {
    	/*String debug = "CALL sp_insfmscrmemo_f(" + (crmemo_hid == null ? null : crmemo_hid)
			+ ", " + (fms_ref_no == null ? "''" : "'" + fms_ref_no + "'")
			+ ", " + (doc_type == null ? null : "'" + doc_type + "'")+ ")";
		log.error(debug);
		System.out.println(debug);*/
		
        Query query = entityManager.createNativeQuery(
                "CALL sp_insfmscrmemo_f(:i_crmemo_hid, :i_fms_ref_no, :i_doc_type)")
                .setParameter("i_crmemo_hid", crmemo_hid)
                .setParameter("i_fms_ref_no", fms_ref_no!=null?fms_ref_no:"") //has == check in SP. Cannot be null
                .setParameter("i_doc_type", doc_type);

            return (Integer) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_getfmscrmemo() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmscrmemo()");

        return query.getResultList();
    }
    
    public List<Object[]> sp_getfmscrmemobyhid(BigInteger hid) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmscrmemobyhid(:i_hid)")
                .setParameter("i_hid", hid);

        return query.getResultList();
    }
    
    public Object[] sp_getfmscrmemobyarifmsrefno(String ari_fms_ref_no) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmscrmemobyarifmsrefno(:i_fms_ref_no)")
                .setParameter("i_fms_ref_no", ari_fms_ref_no);

        return (Object[]) query.getSingleResult();
    }

    // @Override
    // public Integer sp_updfmscrmemo(BigInteger crmemo_hid, String resp_attr_ext_sys, String fms_ref_no, String resp_co,
    //         String resp_status, String resp_msg, Date resp_dt) {
    //     Query query = entityManager.createNativeQuery(
    //         "CALL sp_updfmscrmemo(:i_crmemo_hid, :i_resp_attr_ext_sys, :i_fms_ref_no, :i_resp_co, :i_resp_status, :i_resp_msg, :i_resp_dt)")
    //         .setParameter("i_crmemo_hid", crmemo_hid)
    //         .setParameter("i_resp_attr_ext_sys", resp_attr_ext_sys)
    //         .setParameter("i_fms_ref_no", fms_ref_no)
    //         .setParameter("i_resp_co", resp_co)
    //         .setParameter("i_resp_status", resp_status)
    //         .setParameter("i_resp_msg", resp_msg)
    //         .setParameter("i_resp_dt", resp_dt);
        
    //     return (Integer) query.getSingleResult();
    // }

    @Override
    public Integer sp_updfmscrmemo(BigInteger crmemo_hid, FMSCRMemo fmscrMemo) {
    	/*logger.error("CALL sp_updfmscrmemo(" + 
    								crmemo_hid.toString() + ", '" + 
    								fmscrMemo.getResp_attr_ext_sys() + "', '" +
    								fmscrMemo.getFms_ref_no() + "', '" + 
    								fmscrMemo.getResp_co() + "', '" + 
    								fmscrMemo.getResp_status() + "', '" +
    								fmscrMemo.getResp_msg() + "', '" +
    								fmscrMemo.getResp_dt().toLocaleString() + "');");
        log.error("CALL sp_updfmscrmemo(" + 
    								crmemo_hid.toString() + ", '" + 
    								fmscrMemo.getResp_attr_ext_sys() + "', '" +
    								fmscrMemo.getFms_ref_no() + "', '" + 
    								fmscrMemo.getResp_co() + "', '" + 
    								fmscrMemo.getResp_status() + "', '" +
    								fmscrMemo.getResp_msg() + "', '" +
    								fmscrMemo.getResp_dt() + "');");*/

        Query query = entityManager.createNativeQuery(
            "CALL sp_updfmscrmemo(:i_crmemo_hid, :i_resp_attr_ext_sys, :i_fms_ref_no, :i_resp_co, :i_resp_status, :i_resp_msg, :i_resp_dt)")
            .setParameter("i_crmemo_hid", crmemo_hid)
            .setParameter("i_resp_attr_ext_sys", fmscrMemo.getResp_attr_ext_sys())
            .setParameter("i_fms_ref_no", fmscrMemo.getFms_ref_no())
            .setParameter("i_resp_co", fmscrMemo.getResp_co())
            .setParameter("i_resp_status",  fmscrMemo.getResp_status())
            .setParameter("i_resp_msg", fmscrMemo.getResp_msg())
            .setParameter("i_resp_dt", fmscrMemo.getResp_dt());
        
        return (Integer) query.getSingleResult();
    }
    
    public Integer sp_rollbackfmscrmemohbh(BigInteger crmemo_hid) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_rollbackfmscrmemohbh(:i_crmemo_hid)")
                .setParameter("i_crmemo_hid", crmemo_hid);
            
            return (Integer) query.getSingleResult();
    }

    @Override
    public Integer creditNonBilCrMemo(String orn_no) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_insfmscrnb(:i_orn_no)")
                .setParameter("i_orn_no", orn_no);
            
            return (Integer) query.getSingleResult();
    }
}
