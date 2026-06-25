package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.maven.rms.models.AgBankTxn;
import com.maven.rms.models.AgBankTxnReq;
import com.maven.rms.models.NonReceiptingAgTxnRequest;
import com.maven.rms.models.NonReceiptingDocRequest;
import com.maven.rms.models.NonReceiptingRequest;
import com.maven.rms.services.AuthService;

@Repository
public class NonReceiptingRepository implements INonReceiptingRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private AuthService authService;

    @Override
    public List<Object[]> sp_getrmsnonreceipting(NonReceiptingRequest request) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getrmsnonreceipting(:i_page, :i_size, :i_task_id, :i_settlement_date, :i_merchant_id, :i_task_status, :i_date_uploaded, :i_settle_status)")
                .setParameter("i_page", request.getI_page())
                .setParameter("i_size", request.getI_size())
                .setParameter("i_task_id", request.getI_task_id())
                .setParameter("i_settlement_date", request.getI_settlement_date())
                .setParameter("i_merchant_id", request.getI_merchant_id())
                .setParameter("i_task_status", request.getI_task_status())
                .setParameter("i_date_uploaded", request.getI_date_uploaded())
                .setParameter("i_settle_status", request.getI_settle_status());

        return query.getResultList();
    }

    private byte[] decodeBase64(String base64String) {
        if (base64String.startsWith("data:")) {
            base64String = base64String.substring(base64String.indexOf(',') + 1);
        }
        base64String = base64String.replaceAll("\\s", "").replace(":", "");
        return Base64.getDecoder().decode(base64String);
    }

    // @Override
    // public List<Integer> sp_insagsaledoc(List<NonReceiptingDocRequest> insertRequests)
    //         throws SerialException, SQLException {
    //     List<Integer> insertedIds = new ArrayList<>();

    //     for (NonReceiptingDocRequest insRequest : insertRequests) {
    //         byte[] decodedBytes = decodeBase64(insRequest.getI_file_content());
    //         Blob blob = new SerialBlob(decodedBytes);

    //         StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insagsaledoc");

    //         // Register parameters (ensure parameter names and types match those defined in
    //         // the stored procedure)
    //         storedProcedureQuery.registerStoredProcedureParameter("i_ag_sale_id", Integer.class,
    //                 javax.persistence.ParameterMode.IN);
    //         storedProcedureQuery.registerStoredProcedureParameter("i_ag_type", String.class,
    //                 javax.persistence.ParameterMode.IN);
    //         storedProcedureQuery.registerStoredProcedureParameter("i_file_name", String.class,
    //                 javax.persistence.ParameterMode.IN);
    //         storedProcedureQuery.registerStoredProcedureParameter("i_file_content", Blob.class,
    //                 javax.persistence.ParameterMode.IN);
    //         storedProcedureQuery.registerStoredProcedureParameter("i_file_type", String.class,
    //                 javax.persistence.ParameterMode.IN);
    //         storedProcedureQuery.registerStoredProcedureParameter("i_file_size_kb", Integer.class,
    //                 javax.persistence.ParameterMode.IN);
    //         storedProcedureQuery.registerStoredProcedureParameter("i_created_by", String.class,
    //                 javax.persistence.ParameterMode.IN);
    //         storedProcedureQuery.registerStoredProcedureParameter("i_modified_by", String.class,
    //                 javax.persistence.ParameterMode.IN);
    //         storedProcedureQuery.registerStoredProcedureParameter("i_settle_status", String.class,
    //                 javax.persistence.ParameterMode.IN);
    //         storedProcedureQuery.registerStoredProcedureParameter("i_task_status", String.class,
    //                 javax.persistence.ParameterMode.IN);
    //         storedProcedureQuery.registerStoredProcedureParameter("i_remark", String.class,
    //                 javax.persistence.ParameterMode.IN);

    //         // Set input parameters
    //         storedProcedureQuery.setParameter("i_ag_sale_id", insRequest.getI_ag_sale_id());
    //         storedProcedureQuery.setParameter("i_ag_type", insRequest.getI_ag_type());
    //         storedProcedureQuery.setParameter("i_file_name", insRequest.getI_file_name());
    //         storedProcedureQuery.setParameter("i_file_content", blob);
    //         storedProcedureQuery.setParameter("i_file_type", insRequest.getI_file_type());
    //         storedProcedureQuery.setParameter("i_file_size_kb", insRequest.getI_file_size_kb());
    //         storedProcedureQuery.setParameter("i_created_by", authService.getLoginUserName());
    //         storedProcedureQuery.setParameter("i_modified_by", authService.getLoginUserName());
    //         storedProcedureQuery.setParameter("i_settle_status", insRequest.getI_settle_status());
    //         storedProcedureQuery.setParameter("i_task_status", insRequest.getI_task_status());
    //         storedProcedureQuery.setParameter("i_remark", insRequest.getI_remark());

    //         // Execute stored procedure
    //         storedProcedureQuery.execute();

    //         // Get inserted ag_doc_id from output parameter
    //         Integer result = (Integer) storedProcedureQuery.getSingleResult();
    //         insertedIds.add(result);

    //     }

    //     return insertedIds;
    // }


    @Override
    public Integer sp_insagsaledoc(NonReceiptingDocRequest insertRequest) throws SerialException, SQLException {

        Integer result = 0;

        byte[] decodedBytes = decodeBase64(insertRequest.getI_file_content());
        Blob blob = new SerialBlob(decodedBytes);

        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_insagsaledoc");

        // Register parameters (ensure parameter names and types match those defined in
        // the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_stmt_no", String.class,
                javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_ag_type", String.class,
                javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_name", String.class,
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
        storedProcedureQuery.registerStoredProcedureParameter("i_settle_status", String.class,
                javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_task_status", String.class,
                javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_remark", String.class,
                javax.persistence.ParameterMode.IN);

        // Set input parameters
        storedProcedureQuery.setParameter("i_stmt_no", insertRequest.getI_stmt_no());
        storedProcedureQuery.setParameter("i_ag_type", insertRequest.getI_ag_type());
        storedProcedureQuery.setParameter("i_file_name", insertRequest.getI_file_name());
        storedProcedureQuery.setParameter("i_file_content", blob);
        storedProcedureQuery.setParameter("i_file_type", insertRequest.getI_file_type());
        storedProcedureQuery.setParameter("i_file_size_kb", insertRequest.getI_file_size_kb());
        storedProcedureQuery.setParameter("i_created_by", authService.getLoginUserName());
        storedProcedureQuery.setParameter("i_modified_by", authService.getLoginUserName());
        storedProcedureQuery.setParameter("i_settle_status", insertRequest.getI_settle_status());
        storedProcedureQuery.setParameter("i_task_status", insertRequest.getI_task_status());
        storedProcedureQuery.setParameter("i_remark", insertRequest.getI_remark());

        // Execute stored procedure
        storedProcedureQuery.execute();

        // Get inserted ag_doc_id from output parameter
        result = (Integer) storedProcedureQuery.getSingleResult();

        return result;
    }

    @Override
    public Integer sp_insagbanktxn(AgBankTxnReq request) {
        Integer result;
        AgBankTxn account = getFileContent(request);

        Query query = entityManager.createNativeQuery(
                "CALL sp_insagbanktxn(:i_stmt_no, :i_ag_doc_id, :i_acct_no, :i_acct_type, :i_acct_nm, :i_dt_fr, :i_dt_to, :i_total_debit, :i_total_credit, :i_begin_bal, :i_end_bal, :i_dt_txn, :i_dt_posting, :i_txn_desc, :i_txn_ref, :i_debit, :i_credit, :i_source_cd, :i_teller_id, :i_brn_chn, :i_txn_cd, :i_end_bal2, :i_virtual_acct, :i_txn_desc2, :i_txn_desc3, :i_txn_desc4, :i_dt_expiry)")
                .setParameter("i_stmt_no", request.getI_stmt_no())
                .setParameter("i_ag_doc_id", request.getI_ag_doc_id())
                .setParameter("i_acct_no", account.getI_acct_no())
                .setParameter("i_acct_type", account.getI_acct_type())
                .setParameter("i_acct_nm", account.getI_acct_nm())
                .setParameter("i_dt_fr", account.getI_dt_fr())
                .setParameter("i_dt_to", account.getI_dt_to())
                .setParameter("i_total_debit", account.getI_total_debit())
                .setParameter("i_total_credit", account.getI_total_credit())
                .setParameter("i_begin_bal", account.getI_begin_bal())
                .setParameter("i_end_bal", account.getI_end_bal())
                .setParameter("i_dt_txn", account.getI_dt_txn())
                .setParameter("i_dt_posting", account.getI_dt_posting())
                .setParameter("i_txn_desc", account.getI_txn_desc())
                .setParameter("i_txn_ref", account.getI_txn_ref())
                .setParameter("i_debit", account.getI_debit())
                .setParameter("i_credit", account.getI_credit())
                .setParameter("i_source_cd", account.getI_source_cd())
                .setParameter("i_teller_id", account.getI_teller_id())
                .setParameter("i_brn_chn", account.getI_brn_chn())
                .setParameter("i_txn_cd", account.getI_txn_cd())
                .setParameter("i_end_bal2", account.getI_end_bal2())
                .setParameter("i_virtual_acct", account.getI_virtual_acct())
                .setParameter("i_txn_desc2", account.getI_txn_desc2())
                .setParameter("i_txn_desc3", account.getI_txn_desc3())
                .setParameter("i_txn_desc4", account.getI_txn_desc4())
                .setParameter("i_dt_expiry", account.getI_dt_expiry());
        result = (Integer) query.getSingleResult();
        return result;
    }

    private AgBankTxn getFileContent(AgBankTxnReq line) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MMM/yyyy");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        AgBankTxn account = new AgBankTxn();

        account.setI_acct_no(line.getI_acct_no());
        account.setI_acct_type(line.getI_acct_type());
        account.setI_acct_nm(line.getI_acct_nm());
        account.setI_dt_fr(java.sql.Date.valueOf(line.getI_dt_fr()));
        account.setI_dt_to(java.sql.Date.valueOf(line.getI_dt_to()));

        // account.setI_total_debit(Integer.parseInt(line.getI_total_debit()));
        // account.setI_total_credit(Integer.parseInt(line.getI_total_credit()));

        account.setI_total_debit(BigDecimal.valueOf(Double.parseDouble(line.getI_total_debit())));
        account.setI_total_credit(BigDecimal.valueOf(Double.parseDouble(line.getI_total_credit())));
        // account.setI_total_credit(Integer.parseInt(line.getI_total_credit()));

        account.setI_begin_bal(line.getI_begin_bal());
        account.setI_end_bal(line.getI_end_bal());

        if ("-".equals(line.getI_dt_txn())) {
            account.setI_dt_txn(null);
        } else {
            String o_dt_txn = line.getI_dt_txn();
            LocalDate o_date_txn = LocalDate.parse(o_dt_txn, inputFormatter);
            o_dt_txn = o_date_txn.format(outputFormatter);
            if ("-".equals(line.getI_time_txn() + ":00")) {
                account.setI_dt_txn(o_dt_txn);
            } else {
                String f_dt_txn = o_dt_txn + " " + line.getI_time_txn() + ":00";
                account.setI_dt_txn(f_dt_txn);
            }
        }

        String o_dt_posting = line.getI_dt_posting();
        LocalDate o_date_posting = LocalDate.parse(o_dt_posting, inputFormatter);
        o_dt_posting = o_date_posting.format(outputFormatter);
        String f_dt_posting = o_dt_posting + " " + line.getI_time_posting() + ":00";
        account.setI_dt_posting(f_dt_posting);
        account.setI_txn_desc(line.getI_txn_desc());
        account.setI_txn_ref(line.getI_txn_ref());

        if ("-".equals(line.getI_debit().trim())) {
            account.setI_debit("0.00");
        } else {
            account.setI_debit(line.getI_debit());
        }

        if ("-".equals(line.getI_credit())) {
            account.setI_credit("0.00");
        } else {
            account.setI_credit(line.getI_credit());
        }

        account.setI_source_cd(line.getI_source_cd());
        account.setI_teller_id(line.getI_teller_id());
        account.setI_brn_chn(line.getI_brn_chn());
        account.setI_txn_cd(line.getI_txn_cd());
        account.setI_end_bal2(line.getI_end_bal2());

        if ("-".equals(line.getI_virtual_acct())) {
            account.setI_virtual_acct(null);
        } else {
            account.setI_virtual_acct(line.getI_virtual_acct());
        }

        if ("-".equals(line.getI_txn_desc2())) {
            account.setI_txn_desc2(null);
        } else {
            account.setI_txn_desc2(line.getI_txn_desc2());
        }

        if ("-".equals(line.getI_txn_desc3())) {
            account.setI_txn_desc3(null);
        } else {
            account.setI_txn_desc3(line.getI_txn_desc3());
        }

        if ("-".equals(line.getI_txn_desc4())) {
            account.setI_txn_desc4(null);
        } else {
            account.setI_txn_desc4(line.getI_txn_desc4());
        }

        if ("-".equals(line.getI_dt_expiry())) {
            account.setI_dt_expiry(null);
        } else {
            account.setI_dt_expiry(java.sql.Date.valueOf(line.getI_dt_expiry()));
        }

        return account;
    }

    @Override
    public List<Object[]> sp_getagdoc(AgBankTxnReq req) {
        Query query = entityManager.createNativeQuery("CALL sp_getagdoc(:i_page, :i_size, :i_stmt_no, :i_file_nm, :i_created_by, :i_dt_created)")
                .setParameter("i_page", req.getI_page())
                .setParameter("i_size", req.getI_size())
                .setParameter("i_stmt_no", req.getI_stmt_no())
                .setParameter("i_file_nm", req.getI_file_nm())
                .setParameter("i_created_by", req.getI_created_by())
                .setParameter("i_dt_created", req.getI_dt_created());

        return query.getResultList();
    }

    @Override
    public Blob sp_getagfilecontent(AgBankTxnReq req) {
        Query query = entityManager.createNativeQuery("CALL sp_getagfilecontent(:i_ag_doc_id)")
                .setParameter("i_ag_doc_id", req.getI_ag_doc_id());

        return (Blob) query.getSingleResult();
    }

    @Override
    public List<Object[]> sp_getagbanktxn(NonReceiptingAgTxnRequest request) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getagbanktxn(:i_page, :i_size, :i_ag_sale_id, :i_stmt_no, :i_txn_ref, :i_acct_no, :i_brn_chn, :i_txn_desc, :i_credit, :i_posting_date)")
                .setParameter("i_page", request.getI_page())
                .setParameter("i_size", request.getI_size())
                .setParameter("i_ag_sale_id", request.getI_ag_sale_id())
                .setParameter("i_stmt_no", request.getI_stmt_no())
                .setParameter("i_txn_ref", request.getI_txn_ref())
                .setParameter("i_acct_no", request.getI_acct_no())
                .setParameter("i_brn_chn", request.getI_brn_chn())
                .setParameter("i_txn_desc", request.getI_txn_desc())
                .setParameter("i_credit", request.getI_credit())
                .setParameter("i_posting_date", request.getI_posting_date());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getagbanktxnpg(NonReceiptingAgTxnRequest request) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getagbanktxnpg(:i_page, :i_size, :i_ag_sale_id, :i_stmt_no, :i_txn_ref, :i_acct_no, :i_brn_chn, :i_txn_desc, :i_credit, :i_posting_date)")
                .setParameter("i_page", request.getI_page())
                .setParameter("i_size", request.getI_size())
                .setParameter("i_ag_sale_id", request.getI_ag_sale_id())
                .setParameter("i_stmt_no", request.getI_stmt_no())
                .setParameter("i_txn_ref", request.getI_txn_ref())
                .setParameter("i_acct_no", request.getI_acct_no())
                .setParameter("i_brn_chn", request.getI_brn_chn())
                .setParameter("i_txn_desc", request.getI_txn_desc())
                .setParameter("i_credit", request.getI_credit())
                .setParameter("i_posting_date", request.getI_posting_date());

        return query.getResultList();
    }

    @Override
    public Integer sp_delagdoc(AgBankTxnReq req) {
          Query query = entityManager.createNativeQuery(
                    "CALL sp_delagdoc(:i_ag_doc_id)")
                    .setParameter("i_ag_doc_id",  req.getI_ag_doc_id());
          Integer result = (Integer) query.getSingleResult();
          return result;
     }

     @Override
     public List<Object[]> sp_getagdocstatistics(NonReceiptingAgTxnRequest request) {
         Query query = entityManager.createNativeQuery(
                 "CALL sp_getagdocstatistics(:i_ag_sale_id, :i_stmt_no)")
                 .setParameter("i_ag_sale_id", request.getI_ag_sale_id())
                 .setParameter("i_stmt_no", request.getI_stmt_no());
 
         return query.getResultList();
     }

     @Override
     public Integer sp_updagsale(NonReceiptingDocRequest req) {
           Query query = entityManager.createNativeQuery(
                     "CALL sp_updagsale(:i_stmt_no, :i_modified_by, :i_settle_status, :i_task_status, :i_remark, :i_discrepancy_amt)")
                     .setParameter("i_stmt_no", req.getI_stmt_no())
                     .setParameter("i_modified_by", authService.getLoginUserName())
                     .setParameter("i_settle_status", req.getI_settle_status())
                     .setParameter("i_task_status", req.getI_task_status())
                     .setParameter("i_remark", req.getI_remark())
                     .setParameter("i_discrepancy_amt", req.getI_discrepancy_amt());

           Integer result = (Integer) query.getSingleResult();
           return result;
      }

    @Override
    public List<Object[]> sp_getfmsnonrmsrecon() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getfmsnonrmsrecon()");

        return query.getResultList();
    }


    @Override
    public Integer sp_insfmsnonrmsrecon(AgBankTxnReq request) {
        Integer result;

        Query query = entityManager.createNativeQuery(
                "CALL sp_insfmsnonrmsrecon(:i_cash_acct, :i_cn_cust_id, :i_dn_cust_id, :i_fms_ari_ref_no, :i_ari_total_amt, :i_mdr_total_amt, :i_discrepancy_amt, :i_ag_sale_id, :i_is_first, :i_arr_hid, :i_header_amt, :i_total_net_amt)")
                .setParameter("i_cash_acct", request.getI_cash_acct())
                .setParameter("i_cn_cust_id", request.getI_cn_cust_id())
                .setParameter("i_dn_cust_id", request.getI_dn_cust_id())
                .setParameter("i_fms_ari_ref_no", request.getI_fms_ari_ref_no())
                .setParameter("i_ari_total_amt", request.getI_ari_total_amt())
                .setParameter("i_mdr_total_amt", request.getI_mdr_total_amt())
                .setParameter("i_discrepancy_amt", request.getI_discrepancy_amt())
                .setParameter("i_ag_sale_id", request.getI_ag_sale_id())
                .setParameter("i_is_first", request.getI_is_first())
                .setParameter("i_arr_hid", request.getI_arr_hid())
                .setParameter("i_header_amt", request.getI_header_amt())
                .setParameter("i_total_net_amt", request.getI_total_net_amt());
        result = (Integer) query.getSingleResult();
        return result;
    }

    @Override
    public Integer sp_insfmsnonrmsreconcreditdebit(AgBankTxnReq request) {
        Integer result;

        Query query = entityManager.createNativeQuery(
                "CALL sp_insfmsnonrmsreconcreditdebit(:i_cash_acct, :i_cn_cust_id, :i_dn_cust_id, :i_fms_ari_ref_no, :i_ari_total_amt, :i_mdr_total_amt, :i_discrepancy_amt, :i_ag_sale_id, :i_total_net_amt)")
                .setParameter("i_cash_acct", request.getI_cash_acct())
                .setParameter("i_cn_cust_id", request.getI_cn_cust_id())
                .setParameter("i_dn_cust_id", request.getI_dn_cust_id())
                .setParameter("i_fms_ari_ref_no", request.getI_fms_ari_ref_no())
                .setParameter("i_ari_total_amt", request.getI_ari_total_amt())
                .setParameter("i_mdr_total_amt", request.getI_mdr_total_amt())
                .setParameter("i_discrepancy_amt", request.getI_discrepancy_amt())
                .setParameter("i_ag_sale_id", request.getI_ag_sale_id())
                .setParameter("i_total_net_amt", request.getI_total_net_amt());
                // .setParameter("i_is_first", request.getI_is_first())
                // .setParameter("i_cndn_hid", request.getI_cndn_hid())
                // .setParameter("i_header_amt", request.getI_header_amt());
        result = (Integer) query.getSingleResult();
        return result;
    }
}