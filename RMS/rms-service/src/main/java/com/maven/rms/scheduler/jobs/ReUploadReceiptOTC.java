package com.maven.rms.scheduler.jobs;

import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OTCReceiptCclMTTOrderStatusRequest;
import com.maven.rms.models.OTCReceiptCheck;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.models.ReceiptRequest;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.models.OTC.OTCCollectionReceiptingPymtItem;
import com.maven.rms.models.OTC.OTCPaymentDone;
import com.maven.rms.models.OTC.OTCRcpt;
import com.maven.rms.models.OTC.OTCollectionReceiptRequest;
import com.maven.rms.models.IdamanAPIUploadReq;
import com.maven.rms.models.MTTPG;
import com.maven.rms.models.IdamanAPIUpload;
import com.maven.rms.repositories.MTTRCPTRepository;
import com.maven.rms.repositories.MTTRepository;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.MTTService;
import com.maven.rms.services.OTCRcptCclService;
import com.maven.rms.services.OnlinePaymentService;
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
public class ReUploadReceiptOTC implements Job {

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

    private String SchedulerName = "ReUploadReceiptOTC";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private OTCReceiptCancelledGenerator receiptOTCGenerator;

    @Value("${jasper.rcpt.directory}")
    private String rcpt_directory;

    @Autowired
    private OTCRcptCclService otcrcptcclService;

    @Override
    // @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        RMSLogger.schedulerInfo(schLogSvc, SchedulerName, "Scheduler initialization started.", 0);

        SchedulerLog schedulerLog = new SchedulerLog(
                "ReUpload Receipt OTC",
                "This job ReUpload Receipt OTC to Idaman",
                1);
        schedulerLog = schLogSvc.saveNewScheduleLog(schedulerLog);

        try {
            List<OTCReceiptCheck> receiptsToUpload = otcrcptcclService.sp_checkotcrcpt();

            if (receiptsToUpload.isEmpty()) {
                log.info("No OTC receipts to re-upload.");
                return;
            }

            log.info("Fetched {} OTC receipt records to reupload", receiptsToUpload.size());

            for (OTCReceiptCheck rcptCheck : receiptsToUpload) {
                boolean uploaded = false;
                int retryCount = 0;

                while (!uploaded && retryCount < 3) {
                    retryCount++;
                    File pdfRcpt = null;

                    try {
                        OTCReceiptCclMTTOrderStatusRequest request = new OTCReceiptCclMTTOrderStatusRequest();
                        request.setI_otc_rc_id(rcptCheck.getOtc_rc_id());

                        OTCRcpt otcRcpt = otcrcptcclService.sp_getotcreceipt(request);
                        OTCPaymentDone payment = otcrcptcclService.sp_getotcrcptcclorder(rcptCheck.getMtt_id());

                        if (otcRcpt == null || payment == null) {
                            log.warn("Skipping upload due to missing receipt/payment data for mtt_id={}, otc_rc_id={}",
                                    rcptCheck.getMtt_id(), rcptCheck.getOtc_rc_id());
                            break;
                        }

                        String formattedDate = new SimpleDateFormat("dd MMM yyyy HH:mm:ss", Locale.ENGLISH)
                                .format(payment.getPayment_dt());
                        payment.setFormattedDate(formattedDate);

                        List<OTCCollectionReceiptingPymtItem> paymentItems = otcrcptcclService
                                .sp_getotcrcptcllpymtitembymtt(rcptCheck.getMtt_id());

                        pdfRcpt = receiptOTCGenerator.generateReceipt(
                                new OTCollectionReceiptRequest(payment, otcRcpt, paymentItems, "pdf"));

                        // UUID uuid = UUID.randomUUID();
                        // String guid = "RMS-" + uuid;
                        String guid;
                        if (rcptCheck.getSsdocref_id() != null && !rcptCheck.getSsdocref_id().trim().isEmpty()) {
                            guid = rcptCheck.getSsdocref_id();
                        } else {
                            UUID uuid = UUID.randomUUID();
                            guid = "RMS-" + uuid;
                        }
                        

                        byte[] fileContent = Files.readAllBytes(Paths.get(pdfRcpt.toString()));
                        String encodedString = Base64.getEncoder().encodeToString(fileContent);

                        String formatedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

                        Integer result1 = uploadIdamanAPI(
                                new IdamanAPIUploadReq("RMS", otcRcpt.getRcptNo(), "RMSReceipt", formatedDate,
                                        "", "", "", "", "", "", guid, payment.getOrn_no(), "", "", "", "", "", "",
                                        encodedString, pdfRcpt.getName()),
                                otcRcpt.getOtc_rcpt_id(), pdfRcpt.getName());

                        if (result1 == null || result1 < 1) {
                            log.warn("Upload failed for receipt ID {}, retrying with new receipt.");
                            if (pdfRcpt.exists()) {
                                pdfRcpt.delete(); // delete failed PDF
                            }
                            continue; // Try regenerating a new one
                        } else {
                            uploaded = true;
                            mttRepository.markAsUploadedOTC(otcRcpt.getOtc_id());

                            if (pdfRcpt.exists() && !pdfRcpt.delete()) {
                                log.warn("PDF file could not be deleted: {}", pdfRcpt.getAbsolutePath());
                            }
                        }

                        // uploaded = true;
                        // log.info("Successfully uploaded receipt for mtt_id={}, otc_rc_id={}",
                        //         rcptCheck.getMtt_id(), rcptCheck.getOtc_rc_id());

                    } catch (Exception e) {
                        String errorMsg = "Attempt " + retryCount + " failed for mtt_id=" + rcptCheck.getMtt_id() +
                                ", otc_rc_id=" + rcptCheck.getOtc_rc_id() + ": " + e.getMessage();
                        RMSLogger.schedulerError(schLogSvc, SchedulerName, errorMsg, 0);
                        log.error(errorMsg, e);

                        if (pdfRcpt != null && pdfRcpt.exists()) {
                            pdfRcpt.delete();
                        }
                    }
                }
            }

            RMSLogger.schedulerInfo(schLogSvc, SchedulerName, "ReUpload Receipt OTC job completed successfully.", 0);

        } catch (Exception e) {
            log.error("Error occurred while re-uploading OTC receipts: ", e);
            throw new JobExecutionException(e);
        } finally {
            schLogSvc.saveNewScheduleLog(schedulerLog);
        }
    }

    private Integer uploadIdamanAPI(IdamanAPIUploadReq req, Integer otcRcptID, String file_nm) throws IOException {
        List<IdamanAPIUpload> result = Collections.emptyList();
        Integer result1 = -1;

        // try {
        result = idamanAPIUploadService.idaman_api_uploadDoc(req);
        // if (result.size() > 0) {
        if (CollectionUtils.size(result) > 0) {
            // update rcpt table
            result1 = otcrcptcclService.sp_updotcrcpt(otcRcptID, result.get(0).getVerid(), req.getSourceSysDocRefID(),
                    file_nm);
            return result1;// result.get(0).getDocRefID();
        }
        return result1;
    }
}
