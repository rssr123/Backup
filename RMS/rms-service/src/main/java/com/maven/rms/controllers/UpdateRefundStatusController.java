package com.maven.rms.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.models.Email;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.RefundStatusResult;
import com.maven.rms.models.RefundStatusUpdateRequest;
import com.maven.rms.models.RttAppEmailDto;
import com.maven.rms.models.payload.responses.UpdateRefundResponse;
import com.maven.rms.services.CommonService;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.NotificationService;
import com.maven.rms.services.RefundApprovalService;
import com.maven.rms.services.UpdateRefundStatusService;
import com.maven.rms.utils.APIResponse;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList; // Import ArrayList
import java.util.HashMap;
import java.util.List; // Import List
import java.util.Map;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/refund/v1/")
@Slf4j
public class UpdateRefundStatusController {

    private final UpdateRefundStatusService updateRefundStatusService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RefundApprovalService spService;

    @Autowired
    private CommonService commonSvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${rms.application.onlinePortalURL}")
    private String onlinePortalURL;

    @Autowired
    public UpdateRefundStatusController(UpdateRefundStatusService updateRefundStatusService) {
        this.updateRefundStatusService = updateRefundStatusService;
    }

    @PostMapping("/updateRefundsStatus")
    public ResponseEntity<UpdateRefundResponse> updateRefundStatus(
            @Valid @RequestBody RefundStatusUpdateRequest request)
            throws JsonProcessingException {

        ExtAudit extAudit = new ExtAudit();
        extAudit.setI_module_nm("updateRefundsStatus");
        extAudit.setI_direction("Incoming");
        extAudit.setI_rms_batch_no(null);
        String jsonBody = objectMapper.writeValueAsString(request);
        extAudit.setI_request_body(jsonBody);

        LocalDateTime requestTimestamp = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
        String formattedRequestTimestamp = requestTimestamp.format(formatter);

        UpdateRefundResponse updateRefundResponse = new UpdateRefundResponse();
        UpdateRefundResponse.Header header = new UpdateRefundResponse.Header();
        header.setRequest_ts(formattedRequestTimestamp);

        // Use this:
        RefundStatusResult result = updateRefundStatusService.updateRefundStatus(request);
        String status = result.getStatus();
        String refundTy = result.getRefundTy();

        String refundRefNo = request.getApp_no();

        updateRefundResponse.setData(new ArrayList<>()); // Explicitly set an empty ArrayList

        if ("BE".equals(status)) {
            header.setStatus_cd("200");
            header.setMessage("Success");

            // Email sending logic (unchanged)
            String redirect1 = onlinePortalURL + "/home";
            String custEmail = spService.sp_getRttAppEmail(refundRefNo);
            if (custEmail != null) {
                String body;
                String emailSubject;

                if ("RF".equals(refundTy)) {
                    // RF refund type - different email content
                    emailSubject = "Refund Application Rejected by Bank";
                    body = "<strong>Assalamualaikum and Greetings, </strong><br><br>" +
                            "<strong>Dear Sir/Madam,</strong><br>" +
                            "<strong>Refund Application:</strong> <strong>" + refundRefNo + "</strong><br><br>" +
                            "Please be informed that your refund application submitted to the Companies Commission of Malaysia (SSM) has been approved.<br>"
                            +
                            "However, the bank has rejected the refund transaction due to invalid or incorrect bank account information.<br>"
                            +
                            "In this regard, you are required to <strong>submit a new refund application via the system in order for the refund to be processed again.</strong><br>"
                            +
                            "Kindly <strong>click the link below</strong> to submit your new refund application: <a href='" + redirect1
                            + "'>Submit New Application</a><br>" +
                            "Should you require further assistance, please do not hesitate to contact us via this email.<br>"
                            +
                            "Thank you.<br><br>" +

                            "<strong>Assalamualaikum dan Salam Sejahtera,</strong><br><br>" +
                            "<strong>Tuan/Puan yang dihormati,</strong><br>" +
                            "<strong>Permohonan Bayaran Balik:</strong> <strong>" + refundRefNo + "</strong><br><br>" +
                            "Untuk makluman Tuan/Puan, permohonan bayaran balik yang dikemukakan kepada Suruhanjaya Syarikat Malaysia (SSM) telah diluluskan.<br>"
                            +
                            "Namun begitu, pihak bank telah menolak transaksi bayaran tersebut kerana maklumat akaun bank yang dibekalkan adalah tidak sah atau tidak tepat.<br>"
                            +
                            "Sehubungan itu, Tuan/Puan diminta untuk <strong>mengemukakan permohonan bayaran balik baharu melalui sistem bagi membolehkan proses pembayaran semula dibuat.</strong><br>"
                            +
                            "Sila <strong>klik pada pautan berikut</strong> untuk mengemukakan permohonan baharu: <a href='" + redirect1
                            + "'>Kemukakan Permohonan Baharu</a><br>" +
                            "Sekiranya Tuan/Puan memerlukan maklumat lanjut, sila hubungi kami melalui emel ini.<br>" +
                            "Sekian, terima kasih.<br><br>" +
                            "[THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL]";
                } else {
                    // No
                    emailSubject = "Refund Application - Invalid Bank Details";
                    body = "<strong>Assalamualaikum and Greetings, </strong><br><br>" +
                            "<strong>Dear Sir/Madam,</strong><br>" +
                            "<strong>REFUND APPLICATION:</strong> <strong>" + refundRefNo + "</strong><br><br>" +
                            "Please be informed that your refund application submitted to the Companies Commission of Malaysia (SSM) has been approved.<br>"
                            + "However, the bank has rejected the refund transaction due to invalid or incorrect bank account information.<br>"
                            + "To enable us to process the refund again, kindly provide your complete and accurate bank account details as shown below:<br>"

                            + "<strong>Assalamualaikum dan Salam Sejahtera,</strong><br><br>"
                            + "<strong>Tuan/Puan,</strong><br>"
                            + "<strong>PERMOHONAN BAYARAN BALIK:</strong> <strong>" + refundRefNo + "</strong><br>"
                            + "Untuk makluman Tuan/Puan, permohonan bayaran balik yang dikemukakan kepada Suruhanjaya Syarikat Malaysia (SSM) telah diluluskan.<br>"
                            + "Namun begitu, pihak bank telah menolak bayaran tersebut kerana maklumat akaun bank yang dibekalkan adalah tidak sah atau tidak tepat.<br>"
                            + "Bagi membolehkan proses bayaran balik dibuat semula, mohon Tuan/Puan untuk mengemukakan semula maklumat akaun bank yang lengkap dan tepat seperti berikut:<br><br>"
                            +

                            "<strong>Contoh Akaun Persendirian / Example for Personal Account</strong><br>" +
                            "<ul>" +
                            "<li>Nama Pemilik Akaun / Account Holder's Name: SAIFUL HAFIZ BIN HAMIDON</li>" +
                            "<li>Nama Bank / Bank Name: CIMB BANK</li>" +
                            "<li>Nombor Akaun / Account Number: 14730001484568</li>" +
                            "<li>No. Kad Pengenalan / Identification Number: 640319-12-6403</li>" +
                            "</ul><br>" +

                            "<strong>Contoh Akaun Syarikat / Perniagaan / Example for Business / Company Account</strong><br>"
                            +
                            "<ul>" +
                            "<li>Nama Syarikat / Company Name: SHH ENTERPRISE</li>" +
                            "<li>Nama Bank / Bank Name: CIMB BANK</li>" +
                            "<li>Nombor Akaun / Account Number: 14730001484568</li>" +
                            "<li>No. Pendaftaran Syarikat / Company Registration No.: 00008472 - X</li>" +
                            "</ul><br><br>" +
                            "For further details or to access other services, you may visit our RMS Public Portal: "
                            + "<br>Untuk maklumat lanjut atau untuk mengakses perkhidmatan lain, anda boleh melayari Portal Awam RMS kami:"
                            + "<br><br><a href='" + redirect1 + "'>RMS Public Portal Link</a>."
                            + "<br><br>Jika anda memerlukan maklumat lanjut, sila hubungi kami melalui emel ini.<br>"
                            + "Should you require further assistance, please do not hesitate to contact us via this email.<br><br>"
                            +
                            "[THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL]";
                }

                emailService.saveEmailDets(new Email("Notification", custEmail, "", "",
                        emailSubject, body, null));
            }

        } else {
            // Handle other statuses, like "No Data Found" or potential errors
            header.setStatus_cd("401"); // Or a more appropriate error code
            header.setMessage("No Data Found"); // Or a more specific error message
            updateRefundResponse.setData(new ArrayList<>()); // Ensure data is an empty list
        }

        LocalDateTime responseTimestamp = LocalDateTime.now();
        String formattedResponseTimestamp = responseTimestamp.format(formatter);
        header.setResponse_ts(formattedResponseTimestamp);

        updateRefundResponse.setHeader(header);

        // Perform external audit
        externalAudit(extAudit, "Success"); // Ensure this is called after processing

        return ResponseEntity.ok(updateRefundResponse);
    }

    private void externalAudit(ExtAudit paramAudit, String Msg) {
        try {
            ExtAudit extAudit = paramAudit;
            extAudit.setI_response_body(Msg);
            commonSvc.sp_insextaudit(extAudit);
        } catch (Exception e) {
            log.error("Error during external audit: " + e.getMessage() + ", "
                    + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
        }
    }
}