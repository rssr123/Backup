package com.maven.rms.scheduler.jobs;

import com.maven.rms.models.Email;
import com.maven.rms.models.RTTOnlinePayment;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.models.SlipRequest;
import com.maven.rms.repositories.RTTReturnedChequeRepository;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.EmailService;
import com.maven.rms.utils.RMSLogger;
import com.maven.rms.models.RTTReturnedChequeRequest;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.transaction.Transactional;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@DisallowConcurrentExecution
@Component
@Slf4j
public class RTTRefunSlipNotice implements Job {

    @Autowired
    private SchedulerLogService schedulerLogService;

    @Autowired
    private RTTReturnedChequeRepository rttReturnedChequeRepository;

    @Autowired
    private EmailService emailService;

    @Value("${refund.slip.folder.path}")
    private String folderPath;

    @Value("${rms.application.backPortalURL}")
    private String backPortalURL;

    @Value("${rms.application.onlinePortalURL}")
    private String onlinePortalURL;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // Heartbeat
        log.info("RTTRefunSlipNotice.execute invoked; fireInstanceId={}", context.getFireInstanceId());
        System.out.println("DEBUG: RTTRefunSlipNotice.execute invoked; fireInstanceId=" + context.getFireInstanceId());
        Instant start = Instant.now();

        int totalProcessed = 0;
        int successfulEmails = 0;
        int failedEmails = 0;
        int skippedRecords = 0;
        String finalStatus = "UNKNOWN";
        String description;

        try {
            List<RTTOnlinePayment> rttRecords = rttReturnedChequeRepository.findByRttStatus("RG");

            if (rttRecords == null || rttRecords.isEmpty()) {
                String desc = "No RTTOnlinePayment records with status 'RG' found. Nothing to process.";
                log.info(desc);
                System.out.println("DEBUG: " + desc);
                finalStatus = "NO_RECORDS";
                description = String.format(
                        "Update RTT Status - This job sends emails and updates rtt status to REG. %s Processed=0, Sent=0, Failed=0, Skipped=0. Status=%s",
                        desc, finalStatus);
                schedulerLogService.saveNewScheduleLog(new SchedulerLog("Update RTT Status", description, 1));
                return;
            }

            log.info("Fetched {} records with rtt_status 'RG'", rttRecords.size());
            System.out.println("DEBUG: Fetched " + rttRecords.size() + " RTTOnlinePayment records with status RG.");

            for (RTTOnlinePayment rttRecord : rttRecords) {
                totalProcessed++;
                try {
                    if (rttRecord == null || rttRecord.getRttWfId() == null) {
                        log.warn("Skipping record due to null rttRecord or RTT ID.");
                        System.out.println("WARN: Skipping null rttRecord or missing RTT ID.");
                        skippedRecords++;
                        continue;
                    }

                    Integer rttId = rttRecord.getRttWfId();
                    System.out.println("DEBUG: Processing RTT ID = " + rttId);
                    log.debug("Processing RTT record: RTT ID = {}", rttId);

                    RTTReturnedChequeRequest request = new RTTReturnedChequeRequest();
                    request.setI_rtt_id(rttId.toString());

                    List<Object[]> result = null;
                    try {
                        System.out.println("DEBUG: Calling stored procedure sp_getrttreturnche for RTT ID " + rttId
                                + " with param i_rtt_id=" + rttId);
                        result = rttReturnedChequeRepository.sp_getrttreturnche(request);
                        System.out.println("DEBUG: Stored procedure returned: "
                                + (result == null ? "null" : result.size() + " rows"));
                    } catch (Exception e) {
                        log.error("Stored procedure call failed for RTT ID {}: {}. Cause chain: {}", rttId,
                                e.getMessage(), flattenCauses(e), e);
                        System.out.println("ERROR: Stored procedure sp_getrttreturnche threw exception for RTT ID "
                                + rttId + ": " + e.getMessage());
                        printExceptionChain(e);
                        e.printStackTrace(System.out);
                        failedEmails++;
                        continue;
                    }

                    if (result == null || result.isEmpty()) {
                        log.warn("No data returned from stored procedure for RTT ID: {}", rttId);
                        System.out.println(
                                "WARN: No data returned (empty/null) from stored procedure for RTT ID " + rttId);
                        skippedRecords++;
                        continue;
                    }

                    List<SlipRequest> slipRequests = result.stream().map(obj -> {
                        SlipRequest email = new SlipRequest();
                        try {
                            System.out.println(
                                    "DEBUG: Raw stored-proc row for RTT ID " + rttId + " -> " + Arrays.toString(obj));
                            email.setRttWfId(obj[0] instanceof Integer ? (Integer) obj[0] : null);
                            email.setRttAppNo(obj[1] != null ? obj[1].toString() : null);
                            email.setSlipNo(obj[2] != null ? obj[2].toString() : null);
                            email.setCustNm(obj[3] != null ? obj[3].toString() : null);
                            email.setEntNo(obj[4] != null ? obj[4].toString() : null);
                            email.setCustPhone(obj[5] != null ? obj[5].toString() : null);
                            email.setCustEmail(obj[6] != null ? obj[6].toString() : null);
                            email.setRmsType(obj[7] != null ? obj[7].toString() : null);
                            email.setRefundTy(obj[8] != null ? obj[8].toString() : null);
                            email.setCustState(obj[9] != null ? obj[9].toString() : null);
                            email.setRefundReason(obj[10] != null ? obj[10].toString() : null);
                            email.setRcptNo(obj[11] != null ? obj[11].toString() : null);
                            email.setOrnNo(obj[12] != null ? obj[12].toString() : null);
                            email.setTxnId(obj[13] != null ? obj[13].toString() : null);
                            email.setRefundAmt(obj[14] instanceof BigDecimal ? (BigDecimal) obj[14] : BigDecimal.ZERO);
                            email.setRttStatus(obj[15] != null ? obj[15].toString() : null);
                        } catch (ClassCastException cce) {
                            log.error(
                                    "Type casting error when mapping slip request for RTT ID {}: {}. Object array: {}",
                                    rttId, cce.getMessage(), Arrays.toString(obj));
                            System.out.println(
                                    "ERROR: Casting error for RTT ID " + rttId + ", content: " + Arrays.toString(obj));
                            cce.printStackTrace(System.out);
                        } catch (Exception ex) {
                            log.error("Unexpected error mapping slip request for RTT ID {}: {}", rttId, ex.getMessage(),
                                    ex);
                            System.out.println(
                                    "ERROR: Unexpected mapping error for RTT ID " + rttId + ": " + ex.getMessage());
                            ex.printStackTrace(System.out);
                        }
                        return email;
                    }).collect(Collectors.toList());

                    log.info("Retrieved data for RTT ID {}: {}", rttId, slipRequests);
                    System.out.println("DEBUG: SlipRequests for RTT ID " + rttId + ": " + slipRequests);

                    boolean anyEmailSentForThisRTT = false;
                    for (SlipRequest emailData : slipRequests) {
                        if (emailData != null && StringUtils.hasText(emailData.getCustEmail())) {
                            boolean sent = sendEmail(emailData.getCustEmail(), "", emailData.getCustNm(),
                                    emailData.getSlipNo(), emailData.getRefundAmt(), emailData.getOrnNo(),
                                    emailData.getRefundReason(), emailData.getTxnId(), emailData.getRttAppNo());
                            if (sent) {
                                successfulEmails++;
                                anyEmailSentForThisRTT = true;
                            } else {
                                failedEmails++;
                                System.out.println("WARN: Email send failed for RTT ID " + rttId + " to "
                                        + emailData.getCustEmail());
                            }
                        } else {
                            log.warn("Skipping email send due to missing customer email for RTT ID {}", rttId);
                            System.out.println("WARN: Missing customer email; skipping send for RTT ID " + rttId);
                            skippedRecords++;
                        }
                    }

                    if (anyEmailSentForThisRTT) {
                        updateRTTStatus(rttId);
                    }

                    processRefundReturnData(slipRequests);

                } catch (Exception e) {
                    log.error("Error processing RTT ID {}: {}. Cause chain: {}",
                            rttRecord != null ? rttRecord.getRttWfId() : "null", e.getMessage(), flattenCauses(e), e);
                    System.out.println("ERROR: Exception processing RTT ID "
                            + (rttRecord != null ? rttRecord.getRttWfId() : "null") + ": " + e.getMessage());
                    printExceptionChain(e);
                    e.printStackTrace(System.out);
                    failedEmails++;
                }
            }

            // Determine final status
            if (successfulEmails == 0 && failedEmails == 0 && skippedRecords > 0) {
                finalStatus = "NO_EMAILS_SENT";
            } else if (failedEmails > 0) {
                finalStatus = "PARTIAL_SUCCESS";
            } else {
                finalStatus = "SUCCESS";
            }

            String summary = String.format(
                    "Processed %d RTT(s): successfulEmails=%d, failedEmails=%d, skippedRecords=%d. Status=%s",
                    totalProcessed, successfulEmails, failedEmails, skippedRecords, finalStatus);
            log.info("RTT Refund job completed. {}", summary);
            System.out.println("INFO: RTT Refund job completed. " + summary);
            RMSLogger.schedulerInfo("RTT Refund job completed successfully.");

            description = "Update RTT Status - This job sends emails and updates rtt status to REG. " + summary;
            schedulerLogService.saveNewScheduleLog(new SchedulerLog("Update RTT Status", description, 1));

        } catch (Exception e) {
            log.error("Error occurred while updating RTT statuses: {}. Cause chain: {}", e.getMessage(),
                    flattenCauses(e), e);
            System.out.println("FATAL: Outer exception in execute(): " + e.getMessage());
            printExceptionChain(e);
            e.printStackTrace(System.out);
            finalStatus = "FAILED";
            String errDesc = String.format("Update RTT Status - Job failed with exception: %s. Status=%s",
                    e.getMessage(), finalStatus);
            schedulerLogService.saveNewScheduleLog(new SchedulerLog("Update RTT Status", errDesc, 1));
            throw new JobExecutionException(e);
        }
    }

    private void processRefundReturnData(List<SlipRequest> data) {
        log.info("Processing refund return data: {} records", data == null ? 0 : data.size());
        System.out.println(
                "DEBUG: processRefundReturnData called with " + (data == null ? 0 : data.size()) + " records.");
    }

    private boolean sendEmail(String email, String BCC, String name, String slipNo,
            BigDecimal refundAmount, String ornNo, String reason, String txnID, String rttAppNo) {
        try {
            String redirect1 = onlinePortalURL + "/home";
            String subject = "Refund Slip Generated. ";

            String body = "<strong>Order Reference Number: </strong>" + (ornNo != null ? ornNo : "N/A")
                    + "<br><strong>Transaction ID: </strong>" + (txnID != null ? txnID : "N/A")
                    + "<br><strong>Refund Slip No: </strong>" + (slipNo != null ? slipNo : "N/A")

                    + "<br><br><strong>Dear Sir/Madam, </strong>"
                    + "<br><br>We are pleased to inform you that a refund slip has been issued for your recent transaction with us. "
                    + "Please note that this refund slip is valid for 6 months from the date of issuance. "
                    + "We recommend completing the redemption process within this period to ensure a smooth process."

                    + "<br><br><strong>Tuan/Puan,</strong>"
                    + "<br><br>Kami ingin memaklumkan bahawa slip bayaran balik telah dikeluarkan bagi transaksi terkini Tuan/Puan bersama kami. "
                    + "Sila ambil perhatian bahawa tempoh sah slip bayaran balik ini adalah selama 6 bulan dari tarikh dikeluarkan. "
                    + "Kami menyarankan penebusan dilakukan dalam tempoh tersebut bagi memastikan kelancaran proses."
                    
                    + "<br><br>For further details or to access other services, you may visit our RMS Public Portal: "
                    + "<br><i>Untuk maklumat lanjut atau untuk mengakses perkhidmatan lain, anda boleh melayari Portal Awam RMS kami: </i>"
                    + "<br><br><a href='" + redirect1 + "'>RMS Public Portal Link</a>."
                    + "<br><br>**PLEASE IGNORE THIS EMAIL IF THE REFUND HAS ALREADY BEEN REDEEMED***"
                    + "<br>**<i>MOHON ABAIKAN EMAIL INI SEKIRANYA BAYARAN BALIK TELAH DITEBUS</i>***"
                    + "<br><br>Thank you for using our services."
                    + "<br><i>Terima kasih kerana menggunakan perkhidmatan kami.</i>"
                    + "<br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL]";

            Email emailObject;
            File pdfFile = new File(folderPath + File.separator + "SSM-Receipt-" + rttAppNo + ".pdf");
            System.out.println("DEBUG: Looking for PDF at " + pdfFile.getAbsolutePath());
            if (pdfFile.exists()) {
                byte[] fileContent = Files.readAllBytes(pdfFile.toPath()); // optional diagnostic
                emailObject = new Email(subject, email, "", BCC, "Refund Slip Generated. ", body, pdfFile);
                emailService.sendMailWithAttachment(emailObject, true);
            } else {
                emailObject = new Email(subject, email, "", BCC, "Refund Slip Generated. ", body, null);
                emailService.sendMailHTML(emailObject);
                System.out.println("DEBUG: PDF not found, sent email without attachment to " + email);
            }

            log.info("Email sent successfully to {}", email);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}. Cause chain: {}", email, e.getMessage(), flattenCauses(e), e);
            System.out.println("ERROR: Failed to send email to " + email + ": " + e.getMessage());
            printExceptionChain(e);
            e.printStackTrace(System.out);
            return false;
        }
    }

    private void updateRTTStatus(Integer rttId) {
        try {
            int code = rttReturnedChequeRepository.sp_updrttslippdf(rttId);

            if (code == 1) {
                log.info("Finalized RTT WF {}: rtt_status updated to REG and history written.", rttId);
                System.out.println("DEBUG: Finalized RTT WF " + rttId + " -> REG.");
            } else if (code == 0) {
                log.info("RTT WF {} already finalized (no-op).", rttId);
                System.out.println("DEBUG: RTT WF " + rttId + " already REG (no-op).");
            } else if (code == -1) {
                log.warn("RTT WF {} not found during finalize.", rttId);
                System.out.println("WARN: RTT WF " + rttId + " not found for finalize.");
            }
        } catch (Exception e) {
            log.error("Failed to finalize RTT WF {}: {}. Cause chain: {}", rttId, e.getMessage(), flattenCauses(e), e);
            System.out.println("ERROR: Failed to finalize RTT WF " + rttId + ": " + e.getMessage());
            printExceptionChain(e);
            e.printStackTrace(System.out);
        }
    }

    /**
     * Helper to print full exception cause chain to stdout.
     */
    private void printExceptionChain(Throwable t) {
        System.out.println("EXCEPTION CHAIN:");
        int depth = 0;
        while (t != null) {
            System.out.println("  [" + depth + "] " + t.getClass().getName() + ": " + t.getMessage());
            t = t.getCause();
            depth++;
        }
    }

    private String flattenCauses(Throwable t) {
        StringBuilder sb = new StringBuilder();
        while (t != null) {
            sb.append(t.getClass().getSimpleName()).append(": ").append(t.getMessage());
            t = t.getCause();
            if (t != null)
                sb.append(" -> ");
        }
        return sb.toString();
    }
}
