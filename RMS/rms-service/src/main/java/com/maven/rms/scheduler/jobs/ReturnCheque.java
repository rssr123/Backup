package com.maven.rms.scheduler.jobs;

import com.maven.rms.models.Email;
import com.maven.rms.models.NonBillRCEmail;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.repositories.MTTRepository;
import com.maven.rms.repositories.OTC.OTCReturnedChequeRepository;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.MTTService;
import com.maven.rms.utils.RMSLogger;
import com.maven.rms.models.OTC.OTCReturnedChequeRequest;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

@DisallowConcurrentExecution
@Component
@Slf4j
public class ReturnCheque implements Job {

    @Autowired
    private MTTRepository mttRepository;

    @Autowired
    private SchedulerLogService schedulerLogService;

    @Autowired
    private MTTService mttService;

    @Autowired
    private OTCReturnedChequeRepository otcReturnedChequeRepository;

    @Autowired
    private EmailService emailService;

    @Value("${rms.application.backPortalURL}")
    private String backPortalURL;

    @Value("${rms.application.onlinePortalURL}")
    private String onlinePortalURL;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        RMSLogger.schedulerInfo("ReturnCheque is Initializing...");

        // Log scheduler execution
        SchedulerLog schedulerLog = new SchedulerLog(
                "Update OTC Status",
                "This job sends emails and updates order status to ES",
                1);
        schedulerLog = schedulerLogService.saveNewScheduleLog(schedulerLog);

        try {
            // Retrieve all entries from rms_mtt where order_status = 'EP'
            List<OnlinePayment> otcRecords = mttRepository.findByOrderStatusRC("EP");

            if (otcRecords.isEmpty()) {
                log.info("No records with order_status 'EP' found.");
                return;
            }

            log.info("Fetched {} records with order_status 'EP'", otcRecords.size());

            // Fetch data using stored procedure sp_getnonbillreturnche
            for (OnlinePayment otcRecord : otcRecords) {
                try {
                    if (otcRecord == null || otcRecord.getMttId() == null) {
                        log.warn("Skipping record due to null otcRecord or MTT ID.");
                        continue;
                    }

                    OTCReturnedChequeRequest request = new OTCReturnedChequeRequest();
                    request.setI_mtt_id(otcRecord.getMttId().toString());

                    // Fetch processed data directly
                    List<Object[]> result = otcReturnedChequeRepository.sp_getnonbillreturnche(request);
                    List<NonBillRCEmail> nonBillRCEmails = result.stream().map(obj -> {
                        NonBillRCEmail email = new NonBillRCEmail();

                        email.setNon_bil_id((String) obj[0]);
                        email.setMtt_id((String) obj[1]);
                        email.setOrn_no((String) obj[2]);
                        email.setPayer_email((String) obj[3]);
                        email.setFms_admin_email((String) obj[4]);
                        email.setReq_name((String) obj[5]);
                        email.setNon_bil_no((String) obj[6]);
                        email.setTotal_bil_amt((BigDecimal) obj[7]);
                        email.setRet_che_no((String) obj[8]);
                        email.setReason((String) obj[9]);
                        email.setOrder_status((String) obj[10]);
                        email.setChe_bank_nm((String) obj[11]);

                        return email;
                    }).collect(Collectors.toList());

                    log.info("Retrieved data for MTT ID {}: {}", otcRecord.getMttId(), nonBillRCEmails);

                    for (NonBillRCEmail emailData : nonBillRCEmails) {
                        // Send an email to the requester
                        if (emailData.getPayer_email() != null) {
                            sendEmail(emailData.getPayer_email(), emailData.getFms_admin_email(),
                                    emailData.getReq_name(),
                                    emailData.getNon_bil_no(), emailData.getTotal_bil_amt(), emailData.getRet_che_no(),
                                    emailData.getReason(), emailData.getOrn_no(), emailData.getChe_bank_nm());
                            updateOrderStatus(otcRecord);
                        }
                    }

                    // Process the mapped data if additional actions are needed
                    processNonBillReturnData(nonBillRCEmails);

                } catch (Exception e) {
                    log.error("Error processing MTT ID {}: {}", otcRecord != null ? otcRecord.getMttId() : "null", e);
                    continue;
                }
            }

            RMSLogger.schedulerInfo("ReturnCheque job completed successfully.");
        } catch (Exception e) {
            log.error("Error occurred while updating OTC statuses: ", e);
            throw new JobExecutionException(e);
        } finally {
            schedulerLogService.saveNewScheduleLog(schedulerLog);
        }
    }

    private void processNonBillReturnData(List<NonBillRCEmail> data) {
        // Implement the logic to process the retrieved data here
        log.info("Processing non-bill return data: {}", data);
    }

    private void sendEmail(String email, String fmsAdminEmail, String name, String nonBillNo,
            BigDecimal totalBillAmount, String returnedChequeNo, String reason, String ornNo, String cheBankNm) {
        try {
            String redirect1 = onlinePortalURL + "/payment-page?pr=" + ornNo;
            String redirect2 = onlinePortalURL + "/payment-page";

            // String subject = "Payment Notification for Non-Billing - Action Required ";
            // 250402- Change the subject to "Notification of Dishonoured Cheque - Action Required"
            String subject = "Notification of Dishonoured Cheque - Action Required ";

            String body = "Customer Name: " + name
                    + "<br>Non-Billing No.: " + nonBillNo
                    + "<br>Total Payment Amount: " + "RM" + totalBillAmount
                    + "<br>Returned Cheque No.: " + returnedChequeNo
                    + "<br>Bank Name: " + cheBankNm
                    + "<br>Reason:" + reason
                    + "<br><br>Dear Sir/Madam,"
                    + "<br><i>Tuan/Puan</i>,"
                    + "<br><br>We wish to inform you that the bank has returned the cheque you submitted for payment for the above reasons. "
                    + "Please review and complete the transactions using the payment link provided below within 30 days from this e-mail issuance."
                    + "<br><i>Kami ingin memaklumkan bahawa cek yang anda kemukakan untuk pembayaran telah dikembalikan oleh pihak bank atas sebab yang dinyatakan di atas. Kami memohon Tuan/Puan untuk membuat semakan dan menyelesaikan transaksi menggunakan pautan pembayaran yang disediakan di bawah.</i>"
                    + "<br><br><a href='" + redirect1 + "'>CLICK HERE</a> to proceed with the payment."
                    + "<br><i><a href='" + redirect1 + "'>KLIK DI SINI</a> untuk tujuan pemprosesan pembayaran.</i>"
                    + "<br><br>For further information or to access other services, you may also visit our RMS Public Portal: <a href='"
                    + redirect2 + "'>RMS Public Portal Link</a>."
                    + "<br><i>Untuk maklumat lanjut atau untuk mengakses perkhidmatan lain, anda juga boleh melayari Portal Awam RMS kami: <a href='"
                    + redirect2 + "'>Pautan Portal Awam RMS</a>.</i>"
                    + "<br><br>**PLEASE IGNORE THIS EMAIL IF YOUR PAYMENT HAS ALREADY BEEN PROCESSED***"
                    + "<br>**<i>MOHON ABAIKAN EMEIL INI SEKIRANYA PEMBAYARAN TELAH DILAKUKAN</i>***"
                    + "<br><br>Thank you for using our services."
                    + "<br><i>Terima kasih kerana menggunakan perkhidmatan kami.</i>"
                    + "<br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL]";

            Email emailObject = new Email(subject, email, "", "",
                    "Payment Notification for Non-Billing - Action Required ", body, null);

            // Send email
            //emailService.sendMailHTML(emailObject);

            emailService.saveEmailDets((new Email("Notification", email, "", fmsAdminEmail, subject, body, null)));
            log.info("Email sent successfully to {}", email);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", email, e.getMessage());
        }
    }

    private void updateOrderStatus(OnlinePayment record) {
        try {
            record.setOrder_status("ES"); // Update the status to 'ES'
            mttRepository.save(record); // Save the updated record
            log.info("Updated order_status to 'ES' for MTT ID: {}", record.getMttId());
        } catch (Exception e) {
            log.error("Failed to update order_status for MTT ID: {}: {}", record.getMttId(), e.getMessage());
        }
    }

}
