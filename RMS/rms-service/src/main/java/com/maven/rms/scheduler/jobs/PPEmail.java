package com.maven.rms.scheduler.jobs;

import com.maven.rms.config.RMSProperties;
import com.maven.rms.models.Email;
import com.maven.rms.models.EmailPP;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.repositories.MTTRepository;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.MTT.PPEmailService;
import com.maven.rms.utils.RMSLogger;
import lombok.extern.slf4j.Slf4j;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.transaction.Transactional;

@DisallowConcurrentExecution
@Component
@Slf4j
public class PPEmail implements Job {

    @Autowired
    private MTTRepository mttRepository;

    @Autowired
    private SchedulerLogService schLogSvc;

    // @Autowired
    // private MTTRepository mttRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PPEmailService ppemailService;

    @Autowired
    private RMSProperties rmsProperties;

    @Value("${rms.application.backPortalURL}")
    private String backPortalURL;

    @Value("${rms.application.onlinePortalURL}")
    private String onlinePortalURL;

    private String SchedulerName = "PPEMail";
    private String SchedulerStart = "Scheduler initialization started.";
    private String SchedulerCompleted = "Scheduler is Completed";

    private Email dbEmail = null;

    // @Override
    // @Transactional
    // public void execute(JobExecutionContext context) throws JobExecutionException {
    //     try {
    //         RMSLogger.schedulerInfo(schLogSvc, SchedulerName, SchedulerStart, 0);

    //         restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(
    //                 HttpClientBuilder.create()
    //                         .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
    //                         .build()));

    //         String url = rmsProperties.getJavaUrl() + "api/email/sendPPEmail";

    //         ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

    //         if (response.getStatusCode().is2xxSuccessful()) {
    //             String Msg = "Email trigger successful: " + response.getBody();
    //             log.info(Msg);
    //         } else {
    //             String msg = "PPEmail triggered failed with status: " + response.getStatusCode();
    //             log.error(msg);
    //             RMSLogger.schedulerError(schLogSvc, SchedulerName, msg, 0);
    //         }
    //     } catch (Throwable ex) { // catch everything, even Errors
    //         String errorMsg = ("Unexpected error while triggering email endpoint: " + ex.getMessage());
    //         if (errorMsg.length() > 500) {
    //             errorMsg = errorMsg.substring(0, 497) + "...";
    //         }
    //         RMSLogger.schedulerError(schLogSvc, SchedulerName, errorMsg, 0);
    //         throw new JobExecutionException(ex); // ensure Quartz marks it failed
    //     } finally {
    //         RMSLogger.schedulerInfo(schLogSvc, SchedulerName, SchedulerCompleted, 0);
    //     }
    // }


//#region archived code
@Override
@Transactional
public void execute(JobExecutionContext context) throws JobExecutionException {

    try {
        RMSLogger.schedulerInfo(schLogSvc, SchedulerName, SchedulerStart, 0);

        // restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(
        //         HttpClientBuilder.create()
        //                 .setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE)
        //                 .build()));

        // String url = rmsProperties.getJavaUrl() + "api/email/sendPPEmail"; // or your actual endpoint

        // try {
        // ResponseEntity<String> response = restTemplate.postForEntity(url, null, String.class);

        // if (response.getStatusCode().is2xxSuccessful()) {
        //     String Msg = "Email trigger successful: " + response.getBody();
        //     log.info(Msg);
        //     // RMSLogger.schedulerError(schLogSvc, SchedulerName, Msg, 0);
        // } else {
        //     RMSLogger.schedulerError(schLogSvc, SchedulerName, "PPEmail triggered failed", 0);
        //     log.error("Email trigger failed with status: " + response.getStatusCode());
        // }

        ppemailService.process();
        
    } catch (HttpClientErrorException | HttpServerErrorException ex) {

        String ErrorMsg = "HTTP error:Response body = " + ex.getResponseBodyAsString();

        log.error(ErrorMsg);

        RMSLogger.schedulerError(schLogSvc, SchedulerName, ErrorMsg, 0);
    } catch (ResourceAccessException ex) {

        String ErrorMsg = "Connection error (e.g., SSL, timeout, refused): " + ex.getMessage();
        log.error(ErrorMsg);

        RMSLogger.schedulerError(schLogSvc, SchedulerName, ErrorMsg, 0);
    } catch (Exception ex) {

        String ErrorMsg = "Unexpected error while triggering email endpoint: " + ex.getMessage();
        log.error(ErrorMsg);

        RMSLogger.schedulerError(schLogSvc, SchedulerName, ErrorMsg, 0);
    } finally {
        RMSLogger.schedulerInfo(schLogSvc, SchedulerName, SchedulerCompleted, 0);
    }

}
//#endregion

        // // Log scheduler execution
        // SchedulerLog schedulerLog = new SchedulerLog(
        // "Update Payment Pending Status",
        // "This job sends reminder emails and updates order status",
        // 1);
        // schedulerLog = schLogSvc.saveNewScheduleLog(schedulerLog);

        // try {
        // // Retrieve all entries from rms_mtt where order_status = 'PP'
        // List<OnlinePayment> paymentPendingRecords =
        // mttRepository.findByOrderStatusPP("EP");

        // if (paymentPendingRecords.isEmpty()) {
        // RMSLogger.schedulerInfo(schLogSvc, SchedulerName, "No Email is required to
        // send.", 0);
        // log.info("No records with order_status 'EP' found.");
        // return;
        // }

        // log.info("Fetched {} records with order_status 'EP'",
        // paymentPendingRecords.size());
        // RMSLogger.schedulerInfo(schLogSvc, SchedulerName,
        // paymentPendingRecords.size() + " Email is required to send.", 0);
        // String tempEmail = "";

        // // Fetch data using stored procedure sp_getemailpp
        // for (OnlinePayment record : paymentPendingRecords) {
        // try {
        // if (record == null || record.getMttId() == null) {
        // log.warn("Skipping record due to null record or MTT ID.");
        // RMSLogger.schedulerInfo(schLogSvc, SchedulerName,
        // "Null MttID, Need to Check Java", 0);
        // continue;
        // }

        // // Directly call stored procedure with mtt_id
        // List<Object[]> result = mttRepository.sp_getemailpp(record.getMttId());
        // List<EmailPP> paymentPendingEmails = result.stream().map(obj -> {
        // EmailPP email = new EmailPP();
        // email.setMtt_id((String) obj[0]);
        // email.setCust_email((String) obj[1]);
        // email.setOrn_no((String) obj[2]);
        // email.setTotal_amt((BigDecimal) obj[3]);
        // email.setOrder_status((String) obj[4]);
        // email.setEntity_nm((String) obj[5]);
        // email.setEmail_flag((Integer) obj[6]);
        // return email;
        // }).collect(Collectors.toList());

        // log.info("Retrieved data for MTT ID {}: {}", record.getMttId(),
        // paymentPendingEmails);

        // for (EmailPP emailData : paymentPendingEmails) {
        // // Send a reminder email to the payer
        // if (emailData.getCust_email() != null) {
        // tempEmail = emailData.getCust_email();
        // // RMSLogger.schedulerInfo(schLogSvc, SchedulerName,
        // // "System is trying send to " + tempEmail, 0);

        // // sendemail trigger here;
        // sendEmail(emailData.getEntity_nm(), emailData.getCust_email(),
        // emailData.getOrn_no(),
        // emailData.getTotal_amt());

        // // RMSLogger.schedulerInfo(schLogSvc, SchedulerName,
        // // emailData.getCust_email() + " is sent successfully.", 0);
        // updateOrderStatus(record);
        // }
        // }

        // // Process the mapped data if additional actions are needed
        // processPaymentPendingData(paymentPendingEmails);

        // } catch (Exception e) {
        // RMSLogger.schedulerError(schLogSvc, SchedulerName,
        // tempEmail + " is failed to send. hit error > " + e.getMessage(), 0);
        // log.error("Error processing MTT ID {}: {}", record != null ?
        // record.getMttId() : "null", e);
        // continue;
        // }
        // }

        // // RMSLogger.schedulerInfo("ProcessPaymentPending job completed
        // successfully.");
        // } catch (Exception e) {
        // RMSLogger.schedulerError(schLogSvc, SchedulerName,
        // "Scheduler Error, hit error > " + e.getMessage(), 0);
        // log.error("Error occurred while processing Payment Pending statuses: ", e);
        // throw new JobExecutionException(e);
        // } finally {
        // // schLogSvc.saveNewScheduleLog(schedulerLog);
        // RMSLogger.schedulerInfo(schLogSvc, SchedulerName, SchedulerCompleted, 0);
        // }
    // }

    private void processPaymentPendingData(List<EmailPP> data) {
        // Implement the logic to process the retrieved data here
        log.info("Processing payment pending data: {}", data);
    }

    private void sendEmail(String entityNm, String custEmail, String ornNo, BigDecimal totalAmt) {
        try {

            String redirect1 = onlinePortalURL + "/payment-page?pr=" + ornNo;
            String redirect2 = onlinePortalURL + "/payment-page";

            String subject = "Pending Payment ";
            String body = "Entity Name: " + entityNm
                    + "<br>Order Reference No.: " + ornNo
                    + "<br>Total Amount Payable: " + "RM" + totalAmt

                    + "<br><br>Dear Sir/Madam,"
                    + "<br><i>Tuan/Puan</i>,"
                    + "<br><br>We wish to inform you that a payment is pending in our system. "
                    + "Please review and complete the transactions using the payment link provided below."
                    + "<br><i>Kami ingin memaklumkan bahawa bahawa terdapat pembayaran yang masih terunggak dalam sistem kami. Kami memohon Tuan/Puan untuk membuat semakan dan menyelesaikan transaksi menggunakan pautan pembayaran yang disediakan di bawah.</i>"
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

            Email emailObject = new Email(subject, custEmail, "", "",
                    "Pending Payment", body, null);

            log.info("Sending email to {}", custEmail);

            dbEmail = new Email();
            dbEmail.setTo(custEmail);
            dbEmail.setEmailType("PPEmail");
            dbEmail.setSubject(subject);
            dbEmail.setBody(body);
            dbEmail.setDtCreated(LocalDateTime.now());
            dbEmail.setDtModified(LocalDateTime.now());
            dbEmail.setCreatedBy("system");
            dbEmail.setModifiedBy("system");

            RMSLogger.schedulerInfo(schLogSvc, SchedulerName,
                    "System is trying send to " + custEmail, 0);

            emailService.sendMailHTML(emailObject);
            RMSLogger.schedulerInfo(schLogSvc, SchedulerName,
                    custEmail + " is sent successfully.", 0);
            dbEmail.setStatus("S");
            log.info("Email sent to {}", custEmail);

            // Commented out to ensure email is not sent twice
            // emailService.saveEmailDets(new Email("Notification", custEmail, "", "",
            // subject, body));

            log.info("Payment reminder email sent successfully to {}", custEmail);
        } catch (Exception e) {
            dbEmail.setStatus("F");
            log.error("Failed to send email to {}: {}", custEmail, e.getMessage());
            RMSLogger.schedulerError(schLogSvc, SchedulerName,
                    custEmail + " is sent Failed. Exception >> " + e.getMessage(), 0);
        } finally {
            emailService.saveEmailDets(dbEmail);
        }
    }

    private void updateOrderStatus(OnlinePayment record) {
        try {
            record.setOrder_status("ES");
            mttRepository.save(record);
            log.info("Updated order status to EP for MTT ID: {}", record.getMttId());
        } catch (Exception e) {
            log.error("Failed to update order status for MTT ID: {}: {}", record.getMttId(), e.getMessage());
        }
    }

}
