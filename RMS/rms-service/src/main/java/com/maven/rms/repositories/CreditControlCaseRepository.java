package com.maven.rms.repositories;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.ICreditControlCaseInterface;
import com.maven.rms.models.CreditControlCase;

@Repository
public class CreditControlCaseRepository implements ICreditControlCaseInterface{
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Integer sp_inscccase(CreditControlCase bodyRequest){
        Integer result = 0;

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_inscccase");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        // Invoice Information
        storedProcedureQuery.registerStoredProcedureParameter("i_type", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_fms_ari_ref_no", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_inv_cust_id", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cur_doc_bal", java.math.BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dt_dunning", java.sql.Date.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_lv_dunning", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_attr_case_no", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_Invoice_desc", String.class, javax.persistence.ParameterMode.IN);

        // Customer Information
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_nm", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_id_ty", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_id_no", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_addr_1", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_addr_2", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_addr_3", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_postcode", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_city", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_state", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_country", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_email", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cust_phone", String.class, javax.persistence.ParameterMode.IN);

        // Payment Information
        storedProcedureQuery.registerStoredProcedureParameter("i_pymt_ty", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_pymt_ref_no", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_pymt_dt_application", java.sql.Date.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_pymt_ref", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_pymt_attr_doc_no", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_pymt_attr_doc_ty", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_pymt_amt", java.math.BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_pymt_status", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_txn_ty", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_ref_no_txn", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_rcpt_no", String.class, javax.persistence.ParameterMode.IN);

        // Credit Memo Information
        storedProcedureQuery.registerStoredProcedureParameter("i_cn_type", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cn_ref_no", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cn_cust_orn", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cn_amt", java.math.BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cn_desc", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cn_branch", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cn_coa1", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cn_coa2", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cn_acct", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cn_sub_acct", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cn_qty", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cn_unit_price", java.math.BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_cn_disc_amt", java.math.BigDecimal.class, javax.persistence.ParameterMode.IN);

        // Debit Memo Information
        storedProcedureQuery.registerStoredProcedureParameter("i_dn_type", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dn_ref_no", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dn_cust_orn", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dn_amt", java.math.BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dn_desc", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dn_branch", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dn_coa1", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dn_coa2", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dn_acct", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dn_sub_acct", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dn_qty", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dn_unit_price", java.math.BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dn_disc_amt", java.math.BigDecimal.class, javax.persistence.ParameterMode.IN);

        // Set parameters
        // Invoice Information
        storedProcedureQuery.setParameter("i_type", bodyRequest.getInvoiceInformation().getType());
        storedProcedureQuery.setParameter("i_fms_ari_ref_no", bodyRequest.getInvoiceInformation().getFms_ari_ref_no());
        storedProcedureQuery.setParameter("i_inv_cust_id", bodyRequest.getInvoiceInformation().getInv_cust_id());
        storedProcedureQuery.setParameter("i_cur_doc_bal", bodyRequest.getInvoiceInformation().getCur_doc_bal());
        storedProcedureQuery.setParameter("i_dt_dunning", bodyRequest.getInvoiceInformation().getDt_dunning());
        storedProcedureQuery.setParameter("i_lv_dunning", bodyRequest.getInvoiceInformation().getLv_dunning());
        storedProcedureQuery.setParameter("i_attr_case_no", bodyRequest.getInvoiceInformation().getAttr_case_no());
        storedProcedureQuery.setParameter("i_Invoice_desc", bodyRequest.getInvoiceInformation().getInvoice_desc());

        // Customer Information
        storedProcedureQuery.setParameter("i_cust_nm", bodyRequest.getCustomerInformation().getCust_nm());
        storedProcedureQuery.setParameter("i_cust_id_ty", bodyRequest.getCustomerInformation().getCust_id_ty());
        storedProcedureQuery.setParameter("i_cust_id_no", bodyRequest.getCustomerInformation().getCust_id_no());
        storedProcedureQuery.setParameter("i_cust_addr_1", bodyRequest.getCustomerInformation().getCust_addr_1());
        storedProcedureQuery.setParameter("i_cust_addr_2", bodyRequest.getCustomerInformation().getCust_addr_2());
        storedProcedureQuery.setParameter("i_cust_addr_3", bodyRequest.getCustomerInformation().getCust_addr_3());
        storedProcedureQuery.setParameter("i_cust_postcode", bodyRequest.getCustomerInformation().getCust_postcode());
        storedProcedureQuery.setParameter("i_cust_city", bodyRequest.getCustomerInformation().getCust_city());
        storedProcedureQuery.setParameter("i_cust_state", bodyRequest.getCustomerInformation().getCust_state());
        storedProcedureQuery.setParameter("i_cust_country", bodyRequest.getCustomerInformation().getCust_country());
        storedProcedureQuery.setParameter("i_cust_email", bodyRequest.getCustomerInformation().getCust_email());
        storedProcedureQuery.setParameter("i_cust_phone", bodyRequest.getCustomerInformation().getCust_phone());

        // Payment Information
        storedProcedureQuery.setParameter("i_pymt_ty", bodyRequest.getPaymentInformation().getPymt_ty());
        storedProcedureQuery.setParameter("i_pymt_ref_no", bodyRequest.getPaymentInformation().getPymt_ref_no());
        storedProcedureQuery.setParameter("i_pymt_dt_application", bodyRequest.getPaymentInformation().getPymt_dt_application());
        storedProcedureQuery.setParameter("i_pymt_ref", bodyRequest.getPaymentInformation().getPymt_ref());
        storedProcedureQuery.setParameter("i_pymt_attr_doc_no", bodyRequest.getPaymentInformation().getPymt_attr_doc_no());
        storedProcedureQuery.setParameter("i_pymt_attr_doc_ty", bodyRequest.getPaymentInformation().getPymt_attr_doc_ty());
        storedProcedureQuery.setParameter("i_pymt_amt", bodyRequest.getPaymentInformation().getPymt_amt());
        storedProcedureQuery.setParameter("i_pymt_status", bodyRequest.getPaymentInformation().getPymt_status());
        storedProcedureQuery.setParameter("i_txn_ty", bodyRequest.getPaymentInformation().getTxn_ty());
        storedProcedureQuery.setParameter("i_ref_no_txn", bodyRequest.getPaymentInformation().getRef_no_txn());
        storedProcedureQuery.setParameter("i_rcpt_no", bodyRequest.getPaymentInformation().getRcpt_no());

        // Credit Memo Information
        storedProcedureQuery.setParameter("i_cn_type", bodyRequest.getCreditMemoInformation().getCn_type());
        storedProcedureQuery.setParameter("i_cn_ref_no", bodyRequest.getCreditMemoInformation().getCn_ref_no());
        storedProcedureQuery.setParameter("i_cn_cust_orn", bodyRequest.getCreditMemoInformation().getCn_cust_orn());
        storedProcedureQuery.setParameter("i_cn_amt", bodyRequest.getCreditMemoInformation().getCn_amt());
        storedProcedureQuery.setParameter("i_cn_desc", bodyRequest.getCreditMemoInformation().getCn_desc());
        storedProcedureQuery.setParameter("i_cn_branch", bodyRequest.getCreditMemoInformation().getCn_branch());
        storedProcedureQuery.setParameter("i_cn_coa1", bodyRequest.getCreditMemoInformation().getCn_coa1());
        storedProcedureQuery.setParameter("i_cn_coa2", bodyRequest.getCreditMemoInformation().getCn_coa2());
        storedProcedureQuery.setParameter("i_cn_acct", bodyRequest.getCreditMemoInformation().getCn_acct());
        storedProcedureQuery.setParameter("i_cn_sub_acct", bodyRequest.getCreditMemoInformation().getCn_sub_acct());
        storedProcedureQuery.setParameter("i_cn_qty", bodyRequest.getCreditMemoInformation().getCn_qty());
        storedProcedureQuery.setParameter("i_cn_unit_price", bodyRequest.getCreditMemoInformation().getCn_unit_price());
        storedProcedureQuery.setParameter("i_cn_disc_amt", bodyRequest.getCreditMemoInformation().getCn_disc_amt());

        // Debit Memo Information
        storedProcedureQuery.setParameter("i_dn_type", bodyRequest.getDebitMemoInformation().getDn_type());
        storedProcedureQuery.setParameter("i_dn_ref_no", bodyRequest.getDebitMemoInformation().getDn_ref_no());
        storedProcedureQuery.setParameter("i_dn_cust_orn", bodyRequest.getDebitMemoInformation().getDn_cust_orn());
        storedProcedureQuery.setParameter("i_dn_amt", bodyRequest.getDebitMemoInformation().getDn_amt());
        storedProcedureQuery.setParameter("i_dn_desc", bodyRequest.getDebitMemoInformation().getDn_desc());
        storedProcedureQuery.setParameter("i_dn_branch", bodyRequest.getDebitMemoInformation().getDn_branch());
        storedProcedureQuery.setParameter("i_dn_coa1", bodyRequest.getDebitMemoInformation().getDn_coa1());
        storedProcedureQuery.setParameter("i_dn_coa2", bodyRequest.getDebitMemoInformation().getDn_coa2());
        storedProcedureQuery.setParameter("i_dn_acct", bodyRequest.getDebitMemoInformation().getDn_acct());
        storedProcedureQuery.setParameter("i_dn_sub_acct", bodyRequest.getDebitMemoInformation().getDn_sub_acct());
        storedProcedureQuery.setParameter("i_dn_qty", bodyRequest.getDebitMemoInformation().getDn_qty());
        storedProcedureQuery.setParameter("i_dn_unit_price", bodyRequest.getDebitMemoInformation().getDn_unit_price());
        storedProcedureQuery.setParameter("i_dn_disc_amt", bodyRequest.getDebitMemoInformation().getDn_disc_amt());

        // Execute stored procedure
        storedProcedureQuery.execute();

        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }

        return result;
    }

    public Integer sp_inscccaseItem(CreditControlCase.ItemInformation item, Integer case_id){
        Integer result = 0;

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_inscccaseItem");

        //Register parameter
        storedProcedureQuery.registerStoredProcedureParameter("i_cc_case_id", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_coa1", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_coa2", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_sub_acct", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_qty", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_unit_price", java.math.BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_disc_amt", java.math.BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_txn_item_ref", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_txn_item_desc", String.class, javax.persistence.ParameterMode.IN);

        //Set parameter
        storedProcedureQuery.setParameter("i_cc_case_id", case_id);
        storedProcedureQuery.setParameter("i_coa1", item.getCoa1());
        storedProcedureQuery.setParameter("i_coa2", item.getCoa2());
        storedProcedureQuery.setParameter("i_sub_acct", item.getSub_acct());
        storedProcedureQuery.setParameter("i_qty", item.getQty());
        storedProcedureQuery.setParameter("i_unit_price", item.getUnit_price());
        storedProcedureQuery.setParameter("i_disc_amt", item.getDisc_amt());
        storedProcedureQuery.setParameter("i_txn_item_ref", item.getTxn_item_ref());
        storedProcedureQuery.setParameter("i_txn_item_desc", item.getTxn_item_desc());

        // Execute stored procedure
        storedProcedureQuery.execute();

        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }

        return result;
    }
}
