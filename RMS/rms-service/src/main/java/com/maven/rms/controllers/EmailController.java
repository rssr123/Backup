//TESTING EMAIL SENDING FUNCTION BY WEI ERN, NOT USED IN PRODUCTION
package com.maven.rms.controllers;

import java.io.File;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.Email;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.MTT.PPEmailService;

import lombok.Data;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    private final EmailService emailService;
    private final PPEmailService ppEmailService;

    @Autowired
    public EmailController(EmailService emailService, PPEmailService ppEmailService) {
        this.emailService = emailService;
        this.ppEmailService = ppEmailService;
    }

    @PostMapping("/sendPPEmail")
    public ResponseEntity<String> triggerPPEmailService() {
        try {
            ppEmailService.process();
            return ResponseEntity.ok("PP email process triggered successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to trigger PP email: " + e.getMessage());
        }
    }

    @PostMapping("/sendEmail")
    public ResponseEntity<String> sendSampleEmailWithAttachment(@RequestBody(required = false) SampleEmailRequest req) {
        try {
            String to = (req != null && req.getTo() != null && !req.getTo().trim().isEmpty())
                    ? req.getTo()
                    : "wewong@persys-tech.com";

            String body = (req != null && req.getBody() != null && !req.getBody().trim().isEmpty())
                    ? req.getBody()
                    : "This is a sample email with an attachment (default message).";

            String subject = "PAYMENT SUCCESSFUL - RECEIPT ATTACHED";
            //File att = new File("C:\\RMS-Receipts\\SSM-Receipt-OR20250224000018.pdf");
            Email email = new Email(subject, to, "", "", "Notification", body, null);
            // email.setDtCreated(LocalDateTime.now());
            // email.setDtModified(LocalDateTime.now());
            // email.setCreatedBy("system");
            // email.setModifiedBy("system");
            

            email = emailService.saveEmailDets(email);
            if(email.getAttachmentPath() == null || email.getAttachmentPath().isEmpty())
            	emailService.sendMailHTML(email);
            else
            	emailService.sendMailWithAttachment(email, true);

            return ResponseEntity.ok("Email sent successfully to " + to);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        }
    }

    @PostMapping("/sendEmailModel")
    public ResponseEntity<String> sendEmailModel(@RequestBody(required = false) Email email) {
        try {

            emailService.sendMailHTML(email);

            return ResponseEntity.ok("Email sent successfully to " + email.getTo());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Failed to send email: " + e.getMessage());
        }
    }

    @Data
    public static class SampleEmailRequest {
        private String to;
        private String body;
    }
}
