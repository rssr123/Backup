package com.maven.rms.repositories;

import com.maven.rms.models.RefundSlipReuploadData;
import com.maven.rms.models.SlipRequest;
import org.springframework.stereotype.Repository;
//import com.maven.rms.interfaces.ISlipRepositoryInterface;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.sql.Blob;
import java.sql.Ref;
import java.sql.Timestamp;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class SlipRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public SlipRequest getSlipData(SlipRequest slipRequest) {
        Query query = entityManager.createNativeQuery(
                "CALL sp_getrttslipinfo(:rtt_wf_id_input)")
                .setParameter("rtt_wf_id_input", slipRequest.getRttWfId());

        Object[] result = (Object[]) query.getSingleResult();
        SlipRequest slipData = new SlipRequest();
        slipData.setRttWfId((Integer) result[0]);
        slipData.setRttAppNo((String) result[1]);
        slipData.setSlipNo((String) result[2]);
        slipData.setCustNm((String) result[3]);
        slipData.setEntNo((String) result[4]);
        slipData.setCustPhone((String) result[5]);
        slipData.setCustEmail((String) result[6]);
        slipData.setRmsType((String) result[7]);
        slipData.setRefundTy((String) result[8]);
        slipData.setCustState((String) result[9]);
        slipData.setRefundReason((String) result[10]);
        slipData.setRcptNo((String) result[11]);
        slipData.setOrnNo((String) result[12]);
        slipData.setTxnId((String) result[13]);
        slipData.setRefundAmt((BigDecimal) result[14]);
        slipData.setRttStatus((String) result[15]);
        slipData.setRcptdate((Timestamp) result[16]);
        slipData.setApprovedBy(slipRequest.getApprovedBy());

        return slipData;
    }

    public String getRttAppNo(int rttWfId) {
        Query query = entityManager.createNativeQuery(
                "SELECT rtt_app_no FROM rms_rtt_wf WHERE rtt_wf_id = :rtt_wf_id_input")
                .setParameter("rtt_wf_id_input", rttWfId);

        return (String) query.getSingleResult();
    }

    @Transactional
    public void insertSlipDocument(SlipRequest slipRequest, String encodedString, int fileSizeKb) {
        Query query = entityManager.createNativeQuery(
                "INSERT INTO rms_rtt_doc (rtt_wf_id, file_nm, file_content, file_type, file_size_kb, created_by, modified_by) "
                        +
                        "VALUES (:rtt_wf_id, :file_nm, :file_content, :file_type, :file_size_kb, :created_by, :modified_by)")
                .setParameter("rtt_wf_id", slipRequest.getRttWfId())
                .setParameter("file_nm", "SSM-Receipt-" + slipRequest.getRttAppNo() + ".pdf")
                .setParameter("file_content", encodedString.getBytes())
                .setParameter("file_type", "PDF")
                .setParameter("file_size_kb", fileSizeKb)
                .setParameter("created_by", slipRequest.getApprovedBy())
                .setParameter("modified_by", slipRequest.getApprovedBy());

        query.executeUpdate();
    }

    public Integer sp_updrtt_ver_ssid(String i_rtt_app_no, String i_ver_id, String i_ssdocref_id, String i_file_nm) {
        Query query = entityManager
                .createNativeQuery("CALL sp_updrtt_ver_ssid(:i_rtt_app_no, :i_ver_id, :i_ssdocref_id, :i_file_nm)")
                .setParameter("i_rtt_app_no", i_rtt_app_no)
                .setParameter("i_ver_id", i_ver_id)
                .setParameter("i_ssdocref_id", i_ssdocref_id)
                .setParameter("i_file_nm", i_file_nm);
        return (Integer) query.getSingleResult();
    }

    public Integer sp_updslipgenerated(String rttAppNo, String slipNo) {
        Query query = entityManager.createNativeQuery("CALL sp_updslipgenerated(:i_rtt_app_no, :i_slip_no)")
                .setParameter("i_rtt_app_no", rttAppNo)
                .setParameter("i_slip_no", slipNo);
        return (Integer) query.getSingleResult();
    }

    public List<Object[]> sp_getrttwf_id_list() {
        Query query = entityManager.createNativeQuery("CALL sp_getrttwf_id_list()");

        return query.getResultList();
    }

    /**
     * New: fetch existing refund slip PDF and metadata for reupload.
     * Assumes stored procedure returns:
     * rtt_doc_id, file_nm, file_content (blob), refund_slip_no, orn_no, rtt_app_no
     */
    public RefundSlipReuploadData getRefundSlipReuploadData(int rttWfId) {
        try {
            Query query = entityManager.createNativeQuery(
                    "CALL sp_getrefundslip_reupload(:rtt_wf_id_input)")
                    .setParameter("rtt_wf_id_input", rttWfId);

            Object[] result = (Object[]) query.getSingleResult();
            RefundSlipReuploadData reuploadData = new RefundSlipReuploadData();
            reuploadData.setFileNm((String) result[0]);
            reuploadData.setRefundSlipNo((String) result[1]);
            reuploadData.setOrnNo((String) result[2]);
            reuploadData.setRttAppNo((String) result[3]);
            return reuploadData;
        } catch (NoResultException e) {
            log.warn("No refund slip data found for rttWfId: {}", rttWfId);
            return null;
        } catch (Exception e) {
            log.error("Error retrieving refund slip data for rttWfId: {}", rttWfId, e);
            throw new RuntimeException("Failed to retrieve refund slip data", e);
        }
    }

    public RefundSlipReuploadData getSsdocRefID(int rttWfId) {
        try {
            Query query = entityManager.createNativeQuery(
                    "CALL sp_getRTTssdocref_id(?)")
                    .setParameter(1, rttWfId);

            Object result = query.getSingleResult();

            if (result == null) {
                log.warn("No ssdocrefId found for rttWfId: {}", rttWfId);
                return null;
            }

            RefundSlipReuploadData reuploadData = new RefundSlipReuploadData();
            reuploadData.setSsdocrefId(result.toString());
            return reuploadData;

        } catch (NoResultException e) {
            log.warn("No ssdocrefId found for rttWfId: {}", rttWfId);
            return null;
        } catch (Exception e) {
            log.error("Error retrieving ssdocrefId for rttWfId: {}", rttWfId, e);
            throw new RuntimeException("Failed to retrieve ssdocrefId data", e);
        }
    }
}