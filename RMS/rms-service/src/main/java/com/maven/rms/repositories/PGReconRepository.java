package com.maven.rms.repositories;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.StoredProcedureQuery;

import org.springframework.stereotype.Repository;

import com.maven.rms.interfaces.IPGReconInterface;
import com.maven.rms.models.PGDetailListingRequest;
import com.maven.rms.models.PGRecon;
import com.maven.rms.models.PGReconDetailRequest;
import com.maven.rms.models.PGReconExcelFile;
import com.maven.rms.models.PGReconListRequest;
import com.maven.rms.models.PGReconTaskRequest;
import com.maven.rms.models.PGReconUploadRequest;
import com.maven.rms.models.RMSDetailListingRequest;

@Repository
public class PGReconRepository implements IPGReconInterface {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Integer sp_uploadDoc(PGReconUploadRequest pgDocRequest, Blob blob, PGRecon pgRecon) {
        StoredProcedureQuery storedProcedureQuery = entityManager.createStoredProcedureQuery("sp_inspg");

        // Register parameters (ensure parameter names and types match those defined in the stored procedure)
        storedProcedureQuery.registerStoredProcedureParameter("i_dt_settlement", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_merchant_id", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_stmt_no", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_dt_statement", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_total_txn", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_total_refund", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_total_adj", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_total_others", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_total_paid", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_bal_bfwd", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_bal_cfwd", BigDecimal.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_nm", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_content", Blob.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_type", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_file_size_kb", Integer.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_created_by", String.class, javax.persistence.ParameterMode.IN);
        storedProcedureQuery.registerStoredProcedureParameter("i_modified_by", String.class, javax.persistence.ParameterMode.IN);

        // Set parameters
        storedProcedureQuery.setParameter("i_dt_settlement", pgDocRequest.getI_dt_settlement());
        storedProcedureQuery.setParameter("i_merchant_id", pgRecon.getI_merchantId());
        storedProcedureQuery.setParameter("i_stmt_no", pgRecon.getI_stmtNo());
        storedProcedureQuery.setParameter("i_dt_statement", pgRecon.getI_dtStatement());
        storedProcedureQuery.setParameter("i_total_txn", pgRecon.getI_totalTxn());
        storedProcedureQuery.setParameter("i_total_refund", pgRecon.getI_totalRefund());
        storedProcedureQuery.setParameter("i_total_adj", pgRecon.getI_totalAdj());
        storedProcedureQuery.setParameter("i_total_others", pgRecon.getI_totalOthers());
        storedProcedureQuery.setParameter("i_total_paid", pgRecon.getI_totalPaid());
        storedProcedureQuery.setParameter("i_bal_bfwd", pgRecon.getI_balBfwd());
        storedProcedureQuery.setParameter("i_bal_cfwd", pgRecon.getI_balCfwd());
        storedProcedureQuery.setParameter("i_file_nm", pgDocRequest.getI_file_nm());
        storedProcedureQuery.setParameter("i_file_content", blob);
        storedProcedureQuery.setParameter("i_file_type", pgDocRequest.getI_file_type());
        storedProcedureQuery.setParameter("i_file_size_kb", pgDocRequest.getI_file_size());
        storedProcedureQuery.setParameter("i_created_by", pgDocRequest.getI_created_by());
        storedProcedureQuery.setParameter("i_modified_by", pgDocRequest.getI_modified_by());

        // Execute stored procedure
        storedProcedureQuery.execute();

        // Handle the result (if the stored procedure returns a result set or an output parameter)
        // For example, if the stored procedure returns a single integer result:
        Integer result = 0;
        if (storedProcedureQuery.getResultList().size() > 0) {
            result = (Integer) storedProcedureQuery.getSingleResult();
        }
        return result;
    }

    @Override
    public List<Object[]> sp_getPGReconDoc() {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getpgdoc()");
        return query.getResultList();
    }

    @Override
    public Integer sp_updPGReconStatus(BigInteger rcPGId, String reconStatus) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_updrcpgstatus(:i_rc_pg_id,:i_recon_status)")
                .setParameter("i_rc_pg_id", rcPGId)
                .setParameter("i_recon_status", reconStatus);

                Integer result = (Integer) query.getSingleResult();
        return result;
        // return query.getResultList();
    }

    @Override
    public BigInteger sp_insPGTxn(PGReconExcelFile pgReconExcelFile) {
        BigDecimal mdrAmt = pgReconExcelFile.getMdr_amt().multiply(BigDecimal.valueOf(-1));

        Query query = entityManager.createNativeQuery(
                "CALL sp_inspgtxn(:i_rc_pg_id,:i_dt_txn,:i_txn_id,:i_txn_type,:i_txn_cd,:i_txn_amt,:i_mdr_amt,:i_sst_amt,:i_net_amt,:i_created_by,:i_modified_by)")
                .setParameter("i_rc_pg_id", pgReconExcelFile.getRc_pg_id())
                .setParameter("i_dt_txn", pgReconExcelFile.getDt_txn())
                .setParameter("i_txn_id", pgReconExcelFile.getTxn_id())
                .setParameter("i_txn_type", pgReconExcelFile.getTxn_type())
                .setParameter("i_txn_cd", pgReconExcelFile.getTxn_cd())
                .setParameter("i_txn_amt", pgReconExcelFile.getTxn_amt())
                .setParameter("i_mdr_amt", mdrAmt)
                .setParameter("i_sst_amt", pgReconExcelFile.getSst_amt())
                .setParameter("i_net_amt", pgReconExcelFile.getNet_amt())
                .setParameter("i_created_by", "system") // default to system
                .setParameter("i_modified_by", "system"); // default to system

        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
        // return query.getResultList();
    }

    @Override
    public BigInteger sp_delPGTxn(BigInteger rcPGTxnId) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_delpgtxn(:i_rc_pgtxn_id)")
                .setParameter("i_rc_pgtxn_id", rcPGTxnId);

        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
        // return query.getResultList();
    }

    @Override
    public BigInteger sp_insMTTTxn(BigInteger rcPGTId) {
        Query query = entityManager.createNativeQuery("CALL sp_inspgmtttxn(:i_rc_pg_id)")
                .setParameter("i_rc_pg_id", rcPGTId);

        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
        // return query.getResultList();
    }

    @Override
    public BigInteger sp_updMTTTxn() {
        // Query query = entityManager.createNativeQuery("CALL sp_updMTTTxn()");
        Query query = entityManager.createNativeQuery("CALL sp_updpgmtttxn()");

        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
        // return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getPGReconList(PGReconListRequest pgListRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getPGReconList(:i_page,:i_size,:i_task_id,:i_file_nm, :i_dt_settlement_fr,:i_dt_settlement_to, :i_dt_uploaded_fr,:i_dt_uploaded_to, :i_merchant_id, :i_task_status, :i_recon_status)")
                .setParameter("i_page", pgListRequest.getI_page())
                .setParameter("i_size", pgListRequest.getI_size())
                .setParameter("i_task_id", pgListRequest.getI_task_id())
                .setParameter("i_file_nm", pgListRequest.getI_file_nm())
                .setParameter("i_dt_settlement_fr", pgListRequest.getI_dt_settlement_fr())
                .setParameter("i_dt_settlement_to", pgListRequest.getI_dt_settlement_to())
                .setParameter("i_dt_uploaded_fr", pgListRequest.getI_dt_uploaded_fr())
                .setParameter("i_dt_uploaded_to", pgListRequest.getI_dt_uploaded_to())
                .setParameter("i_merchant_id", pgListRequest.getI_merchant_id())
                .setParameter("i_task_status", pgListRequest.getI_task_status())
                .setParameter("i_recon_status", pgListRequest.getI_recon_status());
        return query.getResultList();
    }

    @Override
    public Object[] sp_getPGReconDetail(PGReconDetailRequest pgReconDetailRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getPGReconDetail(:i_task_id)")
                .setParameter("i_task_id", pgReconDetailRequest.getI_task_id());
        // .setParameter("i_task_id", taskID);
        return (Object[]) query.getSingleResult();
    }

    @Override
    // public BigInteger sp_updPGReconDetail(PGReconTaskRequest pgReconTaskRequest,
    // String username) {
    public BigInteger sp_updPGReconDetail(PGReconTaskRequest pgReconTaskRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_updPGReconDetail(:i_task_id,:i_remarks,:i_task_status,:i_modified_by)")
                .setParameter("i_task_id", pgReconTaskRequest.getI_task_id())
                .setParameter("i_remarks", pgReconTaskRequest.getI_remarks())
                .setParameter("i_task_status", pgReconTaskRequest.getI_task_status())
                .setParameter("i_modified_by", pgReconTaskRequest.getI_modified_by());
        // .setParameter("i_modified_by", username);
        BigInteger result = (BigInteger) query.getSingleResult();
        return result;
        // return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getPGDetailListing(PGDetailListingRequest pgDetailListing) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getpgdetaillisting(:i_page,:i_size,:i_task_no,:i_txn_date,:i_txn_type,:i_fnd_rms,:i_txn_id,:i_txn_cd,:i_sub_cri)")
                .setParameter("i_page", pgDetailListing.getI_page())
                .setParameter("i_size", pgDetailListing.getI_size())
                .setParameter("i_task_no", pgDetailListing.getI_task_no())
                .setParameter("i_txn_date", pgDetailListing.getI_txn_date())
                .setParameter("i_txn_type", pgDetailListing.getI_txn_type())
                .setParameter("i_fnd_rms", pgDetailListing.getI_found_in_rms())
                .setParameter("i_txn_id", pgDetailListing.getI_txn_id())
                .setParameter("i_txn_cd", pgDetailListing.getI_txn_code())
                .setParameter("i_sub_cri", pgDetailListing.getI_sub_cri());

        return query.getResultList();
    }

    @Override
    public List<Object[]> sp_getRMSDetailListing(RMSDetailListingRequest rmsDetailListingRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getrmsdetaillisting(:i_page,:i_size,:i_dt_pymt,:i_cust_nm,:i_fnd_in_pg,:i_txn_id,:i_orn_no,:i_sub_cri,:i_order_status)")
                .setParameter("i_page", rmsDetailListingRequest.getI_page())
                .setParameter("i_size", rmsDetailListingRequest.getI_size())
                .setParameter("i_dt_pymt", rmsDetailListingRequest.getI_dt_pymt())
                .setParameter("i_cust_nm", rmsDetailListingRequest.getI_cust_nm())
                .setParameter("i_fnd_in_pg", rmsDetailListingRequest.getI_fnd_in_pg())
                .setParameter("i_txn_id", rmsDetailListingRequest.getI_txn_id())
                .setParameter("i_orn_no", rmsDetailListingRequest.getI_orn_no())
                .setParameter("i_sub_cri", rmsDetailListingRequest.getI_sub_cri())
                .setParameter("i_order_status", rmsDetailListingRequest.getI_order_status());

        return query.getResultList();
    }

    @Override
    public Blob sp_getrcpgdoc(PGReconListRequest pgReconRequest)
    {
   
            Query query = entityManager.createNativeQuery(
                      "CALL sp_getrcpgdoc_pgpage(:i_task_id)")
                      .setParameter("i_task_id", pgReconRequest.getI_task_id());
   
            return (Blob)query.getSingleResult();
    }

    //20250317 - By Geo
    @Override
    public Integer sp_checkpgtask(PGReconTaskRequest pgReconTaskRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_checkpgtask(:i_task_id)")
                .setParameter("i_task_id", pgReconTaskRequest.getI_task_id());

        Integer result = (Integer) query.getSingleResult();
        return result;
    }
}
