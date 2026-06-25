package com.maven.rms.scheduler.jobs;

import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.models.ReceiptRequest;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.models.IdamanAPIUploadReq;
import com.maven.rms.models.MTTPG;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.IdamanAPIUpload;
import com.maven.rms.repositories.MTTRCPTRepository;
import com.maven.rms.repositories.MTTRepository;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.MTTService;
import com.maven.rms.services.OnlinePaymentService;
import com.maven.rms.services.CommonService;
import com.maven.rms.services.IdamanAPIUploadService;
import com.maven.rms.utils.RMSLogger;
import com.maven.rms.utils.receipts.MTTPGReceiptGenerator;
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
//
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@DisallowConcurrentExecution
@Component
@Slf4j
public class ReUploadReceipt implements Job {

    @Autowired
    private MTTRCPTRepository mttrcptRepository;

    @Autowired
    private MTTPGReceiptGenerator receiptGenerator;

    @Autowired
    private MTTService mttService;

    @Autowired
    private OnlinePaymentService onlinePaymentService;

    @Autowired
    private IdamanAPIUploadService idamanAPIUploadService;

    @Autowired
    private SchedulerLogService schLogSvc;

    @Autowired
    private MTTRepository mttRepository;

    @Autowired
    private CommonService commonSvc;

    private String SchedulerName = "ReUploadReceipt";

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${jasper.rcpt.directory}")
    private String rcpt_directory;

    @Override
    //
    public void execute(JobExecutionContext context) throws JobExecutionException {
        RMSLogger.schedulerInfo(schLogSvc, SchedulerName, "Scheduler initialization started.", 0);

        SchedulerLog schedulerLog = new SchedulerLog(
                "ReUpload Receipt",
                "This job ReUpload Receipt to Idaman",
                1);
        schedulerLog = schLogSvc.saveNewScheduleLog(schedulerLog);

        try {
            List<MTTRCPT> receiptsToUpload = mttRepository.findUnuploadedReceipts();

            if (receiptsToUpload.isEmpty()) {
                log.info("No records to re-upload.");
                return;
            }

            log.info("Fetched {} records to reupload", receiptsToUpload.size());

            for (MTTRCPT rcpt : receiptsToUpload) {
                try {
                    if (rcpt == null || rcpt.getMttRcptID() == null) {
                        log.info("Skipping re-upload for reprinted receipt ID: {}", rcpt.getMttRcptID());
                        continue;
                    }

                    Long pgId = rcpt.getMttPG().getMttPgId();
                    Integer mttId = rcpt.getRmsMTT().getMttId();

                    OnlinePayment payment = onlinePaymentService.sp_getMTT(mttId);
                    MTTPG pg = mttService.getMttPgById(pgId)
                            .orElseThrow(() -> new IllegalArgumentException("MTTPG not found for id: " + pgId));
                    List<OnlinePaymentItem> paymentItems = mttService.getListOfItems(mttId);

                    // Loop to ensure new receipt is generated if update fails
                    boolean uploaded = false;
                    int retry = 0;
                    final int maxRetry = 2; // in case you want to retry at most 2 times

                    // 2025-10-07 temp closed retry for idaman duplicated uploade by Geo
                    //while (!uploaded && retry < maxRetry) {
                       //retry++;

                        File pdfRcpt = receiptGenerator.generateReceipt(
                                new ReceiptRequest(pg, payment, rcpt, paymentItems, "pdf"));

                        if (pdfRcpt == null || !pdfRcpt.exists()) {
                            throw new IOException("Generated PDF file does not exist for receipt ID " + rcpt.getMttRcptID());
                        }

                        byte[] fileContent = Files.readAllBytes(Paths.get(pdfRcpt.getAbsolutePath()));
                        String encodedString = Base64.getEncoder().encodeToString(fileContent);
                        String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                        // UUID uuid = UUID.randomUUID();
                        // String guid = "RMS-" + uuid;
                        String guid;
                        if (rcpt.getRcptUUID() != null && !rcpt.getRcptUUID().trim().isEmpty()) {
                            guid = rcpt.getRcptUUID();
                        } else {
                            UUID uuid = UUID.randomUUID();
                            guid = "RMS-" + uuid;
                        }
                        

                        IdamanAPIUploadReq req = new IdamanAPIUploadReq(
                                "RMS", rcpt.getRcptNo(), "RMSReceipt", formattedDate,
                                "", "", "", "", "", "", guid, payment.getOrnNo(),
                                "", "", "", "", "", "", encodedString, pdfRcpt.getName());

                        Integer uploadResult = uploadIdamanAPI(req, rcpt.getMttRcptID());

                        if (uploadResult == null || uploadResult < 1) {
                            log.warn("Upload failed for receipt ID {}, retrying with new receipt.", rcpt.getMttRcptID());
                            if (pdfRcpt.exists()) {
                                pdfRcpt.delete(); // delete failed PDF
                            }
                            //continue; // Try regenerating a new one
                        } else {
                            uploaded = true;
                            mttRepository.markAsUploaded(rcpt.getMttRcptID());

                            if (pdfRcpt.exists() && !pdfRcpt.delete()) {
                                log.warn("PDF file could not be deleted: {}", pdfRcpt.getAbsolutePath());
                            }
                        }
                    //}

                    if (!uploaded) {
                        throw new RuntimeException("Upload failed after retries for receipt ID " + rcpt.getMttRcptID());
                    }

                } catch (Exception e) {
                    String errorMsg = "Receipt ID " + rcpt.getMttRcptID() + " failed to process: " + e.getMessage();
                    RMSLogger.schedulerError(schLogSvc, SchedulerName, errorMsg, 0);
                    log.error(errorMsg, e);
                }
            }

            RMSLogger.schedulerInfo(schLogSvc, SchedulerName, "ReUpload Receipt job completed successfully.", 0);

        } catch (Exception e) {
            log.error("Error occurred while re-uploading receipts: ", e);
            throw new JobExecutionException(e);
        } finally {
            schLogSvc.saveNewScheduleLog(schedulerLog);
        }
    }

    private Integer uploadIdamanAPI(IdamanAPIUploadReq req, Integer mttRcptID) throws IOException {
        //Added by Geo to handle duplicate upload issue
        logAudit(req.getRefNo1());
        List<IdamanAPIUpload> result = idamanAPIUploadService.idaman_api_uploadDoc(req);
        if (CollectionUtils.isNotEmpty(result)) {
            return mttService.sp_updateMTTRcpt(mttRcptID, result.get(0).getVerid(), req.getSourceSysDocRefID());
        }
        return -1;
    }

    private void logAudit(String requestBody) {
        try {
            ExtAudit extAudit = new ExtAudit();
            extAudit.setI_module_nm("ReuploadIdamanUpload");
            extAudit.setI_request_body(requestBody);
            extAudit.setI_response_body(null);
            extAudit.setI_rms_batch_no(null);
            extAudit.setI_direction("Outgoing");
            extAudit.setI_remark(null);
            commonSvc.sp_insextaudit(extAudit);
        } catch (Exception e) {
            // Don't fail the main operation if audit fails
            // log.warn("Failed to log audit for IdamanUpload: " + e.getMessage());
            log.error("Error in sp_insextaudit for ReuploadIdamanUpload: " + e.getMessage() + ", "
                    + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
        }
    }
}
