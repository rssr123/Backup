package com.maven.rms.services.MTT;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.maven.rms.config.RMSProperties;
import com.maven.rms.models.Email;
import com.maven.rms.models.EmailPP;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.repositories.MTTRepository;
import com.maven.rms.services.EmailService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PPEmailService {

    @Autowired
    private MTTRepository mttRepository;

    @Autowired
    private RMSProperties configRMSProperties;

    @Autowired
    private EmailService servicesEmail;

    @Autowired
    private TemplateEngine templateEngine;

    private Email dbEmail = null;

    public PPEmailService(MTTRepository mttRepository, RMSProperties configRMSProperties, EmailService servicesEmail) {
        this.mttRepository = mttRepository;
        this.configRMSProperties = configRMSProperties;
        this.servicesEmail = servicesEmail;
    }

    public void process() {
        try {

            List<OnlinePayment> paymentPendingRecords = mttRepository.findByOrderStatusPP("EP");
            String tempEmail = "";

            if (paymentPendingRecords.isEmpty()) {

                log.info("No records with order_status 'EP' found.");
                return;
            }

            log.info("Fetched {} records with order_status 'EP'", paymentPendingRecords.size());

            // Fetch data using stored procedure sp_getemailpp
            for (OnlinePayment record : paymentPendingRecords) {
                try {
                    if (record == null || record.getMttId() == null) {
                        log.warn("Skipping record due to null record or MTT ID.");

                        continue;
                    }

                    // Directly call stored procedure with mtt_id
                    List<Object[]> result = mttRepository.sp_getemailpp(record.getMttId());
                    List<EmailPP> paymentPendingEmails = result.stream().map(obj -> {
                        EmailPP email = new EmailPP();
                        email.setMtt_id((String) obj[0]);
                        email.setCust_email((String) obj[1]);
                        email.setOrn_no((String) obj[2]);
                        email.setTotal_amt((BigDecimal) obj[3]);
                        email.setOrder_status((String) obj[4]);
                        email.setEntity_nm((String) obj[5]);
                        email.setEmail_flag((Integer) obj[6]);
                        return email;
                    }).collect(Collectors.toList());

                    log.info("Retrieved data for MTT ID {}: {}", record.getMttId(), paymentPendingEmails);

                    for (EmailPP emailData : paymentPendingEmails) {
                        // Send a reminder email to the payer
                        if (emailData.getCust_email() != null) {
                            tempEmail = emailData.getCust_email();

                            // sendemail trigger here;
                            sendEmail(emailData.getEntity_nm(), emailData.getCust_email(), emailData.getOrn_no(),
                                    emailData.getTotal_amt());

                            updateOrderStatus(record, "ES");
                        }
                    }

                } catch (Exception e) {
                    updateOrderStatus(record, "EF");

                    log.error("Error processing MTT ID {}: {}", record != null ? record.getMttId() : "null", e);
                    continue;
                }
            }

        } catch (Exception e) {

            log.error("Error occurred while processing Payment Pending statuses: ", e);

        } finally {

        }

    }

    private void sendEmail(String entityNm, String custEmail, String ornNo, BigDecimal totalAmt) {
       try {

            String redirect1 = this.configRMSProperties.getOnlinePortalURL() + "/payment-page?pr=" + ornNo;
            String redirect2 = this.configRMSProperties.getOnlinePortalURL() + "/payment-page";

           String subject = "Pending Payment ";

            Context context = new Context();
            context.setVariable("entityNm", entityNm);
            context.setVariable("ornNo", ornNo);
            context.setVariable("totalAmt", totalAmt);
            context.setVariable("redirect1", redirect1);
            context.setVariable("redirect2", redirect2);

            String body = templateEngine.process("email/PPEmail", context);

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

            servicesEmail.sendMailHTML(emailObject);

            dbEmail.setStatus("S");

            log.info("Payment reminder email sent successfully to {}", custEmail);
        } catch (Exception e) {
            dbEmail.setStatus("F");
            log.error("Failed to send email to {}: {}", custEmail, e.getMessage());

        } finally {
            servicesEmail.saveEmailDets(dbEmail);
        }
    }

    private void updateOrderStatus(OnlinePayment record, String Status) {
        try {
            record.setOrder_status(Status);
            mttRepository.save(record);
            log.info("Updated order status to EP for MTT ID: {}", record.getMttId());
        } catch (Exception e) {
            log.error("Failed to update order status for MTT ID: {}: {}", record.getMttId(), e.getMessage());
        }
    }

}
