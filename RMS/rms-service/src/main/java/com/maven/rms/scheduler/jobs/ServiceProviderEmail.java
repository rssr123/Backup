package com.maven.rms.scheduler.jobs;

import com.maven.rms.models.Email;
import com.maven.rms.models.NonBillRCEmail;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.ServiceProviderRequest;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.models.ServiceProviderEmailMtt;
import com.maven.rms.repositories.IServiceProviderRepository;
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
public class ServiceProviderEmail implements Job {

    @Autowired
    private MTTRepository mttRepository;

    @Autowired
    private SchedulerLogService schedulerLogService;

    @Autowired
    private MTTService mttService;

    @Autowired
    private OTCReturnedChequeRepository otcReturnedChequeRepository;

    @Autowired
    private IServiceProviderRepository serviceProviderRepository;

    @Autowired
    private EmailService emailService;

    @Value("${rms.application.backPortalURL}")
    private String backPortalURL;

    @Value("${rms.application.onlinePortalURL}")
    private String onlinePortalURL;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        RMSLogger.schedulerInfo("ServiceProviderEmail is Initializing...");

        // Log scheduler execution
        SchedulerLog schedulerLog = new SchedulerLog(
                "Update Service Provider Status",
                "This job sends emails and updates order status to ES",
                1);
        schedulerLog = schedulerLogService.saveNewScheduleLog(schedulerLog);

        try {
            // Retrieve all entries from rms_mtt where order_status = 'EP'
            List<OnlinePayment> emailRecords = mttRepository.findByOrderStatusSP("EP");

            if (emailRecords.isEmpty()) {
                log.info("No records with order_status 'EP' found.");
                return;
            }

            log.info("Fetched {} records with order_status 'EP'", emailRecords.size());

            // Fetch data using stored procedure sp_getserviceprovideremailmtt
            for (OnlinePayment emailRecord : emailRecords) {
                try {
                    if (emailRecord == null || emailRecord.getMttId() == null) {
                        log.warn("Skipping record due to null otcRecord or MTT ID.");
                        continue;
                    }

                    ServiceProviderRequest request = new ServiceProviderRequest();
                    request.setI_mtt_id(emailRecord.getMttId().toString());

                    // Fetch processed data directly
                    List<Object[]> result = serviceProviderRepository.sp_getserviceprovideremailmtt(request);
                    List<ServiceProviderEmailMtt> ServiceProviderEmailMtts = result.stream().map(obj -> {
                        ServiceProviderEmailMtt email = new ServiceProviderEmailMtt();

                        email.setMtt_id((String) obj[0]);
                        email.setEntity_name((String) obj[1]);
                        email.setOrn_no((String) obj[2]);
                        email.setTotal_amount((BigDecimal) obj[3]);
                        email.setProfile_name((String) obj[4]);
                        email.setCust_email((String) obj[5]);
                        email.setAg_bil((String) obj[6]);
                        return email;
                    }).collect(Collectors.toList());

                    log.info("Retrieved data for MTT ID {}: {}", emailRecord.getMttId(), ServiceProviderEmailMtts);

                    for (ServiceProviderEmailMtt emailData : ServiceProviderEmailMtts) {
                        // Send an email to the requester
                        if (emailData.getCust_email() != null) {
                            sendEmail(emailData.getMtt_id(), emailData.getEntity_name(),
                                    emailData.getOrn_no(),
                                    emailData.getTotal_amount(), emailData.getProfile_name(), emailData.getCust_email(),
                                    emailData.getAg_bil());
                            updateOrderStatus(emailRecord);
                            ServiceProviderRequest updateRequest = new ServiceProviderRequest();
                            updateRequest.setI_mtt_id(emailRecord.getMttId().toString());
                            updateRequest.setI_ag_bil(Integer.parseInt(emailData.getAg_bil()));
                            updateDatePayment(updateRequest);
                        }
                    }

                    // Process the mapped data if additional actions are needed
                    processServiceProviderEmailMttReturnData(ServiceProviderEmailMtts);

                } catch (Exception e) {
                    log.error("Error processing MTT ID {}: {}", emailRecord != null ? emailRecord.getMttId() : "null",
                            e);
                    continue;
                }
            }

            RMSLogger.schedulerInfo("ServiceProviderEmail job completed successfully.");
        } catch (Exception e) {
            log.error("Error occurred while updating OTC statuses: ", e);
            throw new JobExecutionException(e);
        } finally {
            schedulerLogService.saveNewScheduleLog(schedulerLog);
        }
    }

    private void processServiceProviderEmailMttReturnData(List<ServiceProviderEmailMtt> data) {
        // Implement the logic to process the retrieved data here
        log.info("Processing Service Provider MTT return data: {}", data);
    }

    private void sendEmail(String mttId, String entityName, String ornNo, BigDecimal totalAmount, String profileName,
            String custEmail, String agBil) {
        try {
            String redirect1 = onlinePortalURL + "/payment-page?pr=" + ornNo;
            String redirect2 = onlinePortalURL + "/payment-page";

            String subject = "PENDING PAYMENT";

            String body = "Entity Name: " + entityName
                    + "<br>Order Reference No.: " + ornNo
                    + "<br>Total Amount Payable: " + "RM" + totalAmount

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

            // Email emailObject = new Email(subject, custEmail, "", "",
            // "Payment Notification for Non-Billing - Action Required ", body);

            // Send email
            //emailService.sendMailHTML(new Email(subject, custEmail, "", "", subject, body));
            emailService.saveEmailDets(new Email("Notification", custEmail, "", "", subject, body, null,null));
            log.info("Email sent successfully to {}", custEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", custEmail, e.getMessage());
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

    private void updateDatePayment(ServiceProviderRequest serviceProviderRequest) {
        try {
            serviceProviderRepository.sp_updserviceproviderdatepayment(serviceProviderRequest);
            log.info("Updated date payment for MTT ID: {}", serviceProviderRequest.getI_ag_bil());
        } catch (Exception e) {
            log.error("Failed to update date payment for ag bil: {}: {}", serviceProviderRequest.getI_ag_bil(), e.getMessage());
        }
    }

}
