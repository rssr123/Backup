package com.maven.rms.scheduler.jobs;

import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OTCReceiptCclMTTOrderStatusRequest;
import com.maven.rms.models.OTCReceiptCheck;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.models.ReceiptRequest;
import com.maven.rms.models.RefundSlipCheck;
import com.maven.rms.models.RefundSlipReuploadData;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.models.SlipRequest;
import com.maven.rms.models.OTC.OTCCollectionReceiptingPymtItem;
import com.maven.rms.models.OTC.OTCPaymentDone;
import com.maven.rms.models.OTC.OTCRcpt;
import com.maven.rms.models.OTC.OTCollectionReceiptRequest;
import com.maven.rms.models.IdamanAPIUploadReq;
import com.maven.rms.models.MTTPG;
import com.maven.rms.models.IdamanAPIUpload;
import com.maven.rms.repositories.MTTRCPTRepository;
import com.maven.rms.repositories.MTTRepository;
import com.maven.rms.repositories.SlipRepository;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.MTTService;
import com.maven.rms.services.OTCRcptCclService;
import com.maven.rms.services.OnlinePaymentService;
import com.maven.rms.services.RefundService;
import com.maven.rms.services.SlipService;
import com.maven.rms.services.IdamanAPIUploadService;
import com.maven.rms.utils.RMSLogger;
import com.maven.rms.utils.receipts.MTTPGReceiptGenerator;
import com.maven.rms.utils.receipts.OTCReceiptCancelledGenerator;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@DisallowConcurrentExecution
@Component
@Slf4j
public class ReUploadBankSlip implements Job {

    @Value("${refund.slip.folder.path}")
    private String folderPath;

    @Autowired
    private IdamanAPIUploadService idamanAPIUploadService;

    @Autowired
    private SchedulerLogService schLogSvc;

    @Autowired
    private SlipService slipService;

    @Autowired
    private SlipRepository slipRepository;

    private String SchedulerName = "ReUploadBankSlip";

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${jasper.rcpt.directory}")
    private String rcpt_directory;

    @Autowired
    private RefundService refundService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        RMSLogger.schedulerInfo(schLogSvc, SchedulerName, "Scheduler initialization started.", 0);

        SchedulerLog schedulerLog = new SchedulerLog(
                "ReUpload Refund Slip",
                "This job ReUpload Refund Slip to Idaman",
                1);
        schedulerLog = schLogSvc.saveNewScheduleLog(schedulerLog);

        try {
            List<RefundSlipCheck> slipsToUpload = refundService.sp_getrttwf_id_list();

            if (slipsToUpload.isEmpty()) {
                log.info("No Refund Slip to re-upload.");
                return;
            }

            log.info("Fetched {} Refund Slip records to reupload", slipsToUpload.size());

            for (RefundSlipCheck rcptCheck : slipsToUpload) {
                // Add null check before processing
                if (rcptCheck == null || rcptCheck.getRtt_wf_id() == null) {
                    log.warn("Skipping null receipt check or missing rtt_wf_id");
                    continue;
                }

                boolean uploaded = false;
                int retryCount = 0;

                while (!uploaded && retryCount < 3) {
                    retryCount++;
                    try {
                        Integer rttWfId = rcptCheck.getRtt_wf_id();
                        RefundSlipReuploadData ssdocref_id = slipRepository.getSsdocRefID(rttWfId);
                        RefundSlipReuploadData reuploadData = slipRepository.getRefundSlipReuploadData(rttWfId);

                        // Check if reupload data exists and is valid
                        if (reuploadData == null) {
                            throw new IllegalStateException("No reupload data found for rtt_wf_id: " + rttWfId);
                        }

                        if (reuploadData.getFileNm() == null || reuploadData.getFileNm().trim().isEmpty()) {
                            throw new IllegalArgumentException("Invalid filename for rtt_wf_id: " + rttWfId);
                        }

                        if (reuploadData.getRttAppNo() == null || reuploadData.getRttAppNo().trim().isEmpty()) {
                            throw new IllegalArgumentException("Invalid RTT App No for rtt_wf_id: " + rttWfId);
                        }
                        String rttslipno = reuploadData.getRefundSlipNo();
                        String ornNo = reuploadData.getOrnNo();
                        String rttAppNo = reuploadData.getRttAppNo();
                        System.out.println("Reuploading Refund Slip for rtt_wf_id: " + rttWfId +
                                ", Slip No: " + rttslipno + ", ORN No: " + ornNo + ", RTT App No: " + rttAppNo  + ", ssdocrefId: " + ssdocref_id.getSsdocrefId());

                        // Find the file in the refund slip folder and encode its content to Base64
                        String filePath = folderPath + File.separator + reuploadData.getFileNm();
                        File pdfFile = new File(filePath);

                        if (!pdfFile.exists()) {
                            throw new IOException("Refund slip file not found: " + filePath);
                        }

                        if (!pdfFile.canRead()) {
                            throw new IOException("Cannot read refund slip file: " + filePath);
                        }

                        if (pdfFile.length() == 0) {
                            throw new IOException("Refund slip file is empty: " + filePath);
                        }

                        byte[] pdf = Files.readAllBytes(Paths.get(filePath));
                        String encodedString = Base64.getEncoder().encodeToString(pdf);

                        // UUID uuid = UUID.randomUUID();
                        // String guid = "RMS-" + uuid.toString();
                        String guid;
                        if (rcptCheck.getSsdocref_id() != null && !rcptCheck.getSsdocref_id().trim().isEmpty()) {
                            guid = rcptCheck.getSsdocref_id();
                        } else {
                            UUID uuid = UUID.randomUUID();
                            guid = "RMS-" + uuid;
                        }

                        String formatedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                        String filename = reuploadData.getFileNm();

                        Integer result1 = uploadIdamanAPI(
                                new IdamanAPIUploadReq("RMS", rttslipno, "RMSRefundSlip",
                                        formatedDate,
                                        "", "", "", "", "", "", guid, ornNo, "", "", "", "", "", "",
                                        encodedString, filename),
                                rttAppNo,
                                filename);

                        if (result1 == null || result1 <= 0) {
                            log.warn("Idaman upload failed (result={}) for {}:{}", result1, rttAppNo,
                                    filename);
                        } else {
                            uploaded = true;
                            log.info("Idaman upload succeeded (verId={}) for {}:{}", result1,
                                    rttAppNo, filename);
                        }

                    } catch (IllegalArgumentException | IllegalStateException e) {
                        // Don't retry for validation errors - log and break
                        String errorMsg = "Validation error for rtt_wf_id " + rcptCheck.getRtt_wf_id() +
                                " on attempt " + retryCount + ": " + e.getMessage();
                        RMSLogger.schedulerError(schLogSvc, SchedulerName, errorMsg, 0);
                        log.error(errorMsg, e);
                        break; // Exit retry loop for validation errors
                    } catch (IOException e) {
                        String errorMsg = "I/O error for rtt_wf_id " + rcptCheck.getRtt_wf_id() +
                                " on attempt " + retryCount + ": " + e.getMessage();
                        RMSLogger.schedulerError(schLogSvc, SchedulerName, errorMsg, 0);
                        log.error(errorMsg, e);

                        if (retryCount >= 3) {
                            log.error("Failed to process rtt_wf_id {} after {} attempts due to I/O error",
                                    rcptCheck.getRtt_wf_id(), retryCount);
                        }
                    } catch (Exception e) {
                        String errorMsg = "Unexpected error for rtt_wf_id " + rcptCheck.getRtt_wf_id() +
                                " on attempt " + retryCount + ": " + e.getMessage();
                        RMSLogger.schedulerError(schLogSvc, SchedulerName, errorMsg, 0);
                        log.error(errorMsg, e);

                        if (retryCount >= 3) {
                            log.error("Failed to process rtt_wf_id {} after {} attempts due to unexpected error",
                                    rcptCheck.getRtt_wf_id(), retryCount);
                        }
                    }
                }

                // Log final status for this item
                if (!uploaded) {
                    log.warn("Failed to upload refund slip for rtt_wf_id: {} after all retry attempts",
                            rcptCheck.getRtt_wf_id());
                }
            }

            RMSLogger.schedulerInfo(schLogSvc, SchedulerName, "ReUpload Refund Slip job completed successfully.", 0);

        } catch (Exception e) {
            log.error("Error occurred while re-uploading refund Slips: ", e);
            throw new JobExecutionException(e);
        } finally {
            schLogSvc.saveNewScheduleLog(schedulerLog);
        }
    }

    private Integer uploadIdamanAPI(IdamanAPIUploadReq req,
            String rtt_app_no,
            String file_nm) {
        Integer result1 = -1;
        List<IdamanAPIUpload> result = Collections.emptyList();

        try {
            result = idamanAPIUploadService.idaman_api_uploadDoc(req);
        } catch (IOException e) {
            // log and swallow – continue on even if the upload failed
            log.error("Idaman upload failed for appNo=" + rtt_app_no +
                    ", docRef=" + req.getSourceSysDocRefID(), e);
        }

        // Only update your table if upload succeeded AND verId is non-null
        if (CollectionUtils.isNotEmpty(result)
                && result.get(0).getVerid() != null) {
            try {
                result1 = sp_updrtt_ver_ssid(
                        rtt_app_no,
                        result.get(0).getVerid(),
                        req.getSourceSysDocRefID(),
                        file_nm);
            } catch (Exception e) {
                // optional: catch any DB/SP errors too
                log.error("Failed to update version for appNo=" + rtt_app_no, e);
            }
        }

        // result1 will be -1 if either upload or update didn’t happen
        // — but your method will NOT throw, so the caller can keep going.
        return result1;
    }

    public Integer sp_updrtt_ver_ssid(String i_rtt_app_no, String i_ver_id, String i_ssdocref_id, String file_nm) {
        Integer result = 0;
        try {
            result = slipRepository.sp_updrtt_ver_ssid(i_rtt_app_no, i_ver_id, i_ssdocref_id, file_nm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
