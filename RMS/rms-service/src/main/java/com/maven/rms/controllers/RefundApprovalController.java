package com.maven.rms.controllers;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.Email;
import com.maven.rms.models.RTTChargebackRequest;
import com.maven.rms.models.RefundApprovalDetReq;
import com.maven.rms.models.RefundApprovalInfo;
import com.maven.rms.models.RefundDoc;
import com.maven.rms.models.RttAppEmailDto;
import com.maven.rms.models.RttForm;
import com.maven.rms.models.RttItem;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.repositories.RTTReturnedChequeRepository;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.NotificationService;
import com.maven.rms.services.RefundApprovalService;
import com.maven.rms.services.RefundPTTListingService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/refundapproval/v1")
@Slf4j

public class RefundApprovalController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthService authService;

    @Autowired
    private RefundApprovalService spService;

    @Autowired
    private RefundPTTListingService pttService;

    @Autowired
    private NotificationService notificationSvc;

    @Autowired
    private RTTReturnedChequeRepository rttReturnedChequeRepository;

    @PostMapping(value = "/getrefundapprovalinfo")
    public ResponseEntity<ApiResponse<List<RefundApprovalInfo>>> sp_getrefundapproval(HttpServletRequest request,
            @RequestBody RefundApprovalDetReq getRequest) {

        List<RefundApprovalInfo> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getrefundapproval(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getrttitems")
    public ResponseEntity<ApiResponse<List<RttItem>>> sp_getrttitem(HttpServletRequest request,
            @RequestBody RefundApprovalDetReq getRequest) {

        List<RttItem> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getrttitem(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updaterttwfstatus")
    public ResponseEntity<ApiResponse<Integer>> sp_updrttwf(HttpServletRequest request,
            @RequestBody RefundApprovalDetReq rttwfrequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        rttwfrequest.setI_modified_by(authService.getLoginUserName());
        // rttwfrequest.setI_pickup_by(authService.getLoginUserName());

        // check if the rtt status is 'RS' then send email to the user
        String rttstatus = rttwfrequest.getI_rtt_status();
        Integer rtt_wf_id = rttwfrequest.getI_rtt_wf_id();
        String remarks = rttwfrequest.getI_msg();
        BigDecimal refundAmt = rttwfrequest.getI_refund_amt();
        String refundType = rttwfrequest.getI_refund_type();

        System.out.println("refund amount: " + refundAmt);
        System.out.println("refund type: " + refundType);
        // Insert API to check the 3rd party API for the status of the refund

        // failed --> return APIResponse.InternalServerError();

        // correct as below code
        Integer result = spService.sp_updrttwf_status(rttwfrequest);

        if (result == null) {
            return APIResponse.InternalServerError();
        }

        // rttStatus = "RS" means refund approved
        // refundType = "CB" means Chargeback
        // refundType = "DA" means Direct Appeal
        if (rttstatus.equals("RS") && refundType.equals("DA")) {
            // Fetch both the app‑no and the customer email in one shot
            RttAppEmailDto info = pttService.sp_getRttAppEmail(rtt_wf_id);
            if (info == null) {
                log.error("[RTTWF:{}] Data inconsistency: Workflow exists but customer info is missing. rtt_wf_id={}",
                        rtt_wf_id, rtt_wf_id);

                return APIResponse.CustomErrorResponse(
                        "Data inconsistency detected for workflow ID " + rtt_wf_id,
                        "200",
                        HttpStatus.NOT_FOUND);
            }

            String rtt_app_no = info.getRttAppNo();
            String custEmail = info.getCustEmail();
            String orn_no = info.getOrnNo();

            String body = "Assalamualaikum and Greetings,<br><br>" +
                    "Dear Sir/Madam,<br>"
                    + "Please be informed that your refund application with reference number <strong>" + rtt_app_no
                    + "</strong> has been approved by the Approving Body (BYM). <br>"
                    + "The approved refund amount is <strong> RM " + refundAmt
                    + " </strong>. The payment will be processed within the stipulated timeframe and credited to the account specified in your application. "
                    + "Any delays will be communicated from time to time. <br>"
                    + "If you have any further inquiries, please contact us via email at CRM/refund@ssm.com.my by quoting your case reference number for reference. <br>"
                    + "Thank you. <br><br>"

                    + "Assalamualaikum dan Salam Sejahtera, <br><br>"
                    + "Tuan/Puan,<br>"
                    + "Dimaklumkan bahawa permohonan bayaran balik tuan/puan dengan nombor rujukan " + rtt_app_no
                    + " telah diluluskan oleh Badan Yang Meluluskan (BYM). <br>"
                    + "Jumlah bayaran balik yang telah diluluskan adalah sebanyak RM " + refundAmt
                    + ". Bayaran akan diproses dalam tempoh yang ditetapkan dan dikreditkan ke akaun yang telah dinyatakan dalam permohonan."
                    + "Sebarang kelewatan akan dimaklumkan dari semasa ke semasa. <br>"
                    + "Sekiranya tuan/puan mempunyai sebarang pertanyaan lanjut, sila hubungi kami melalui emel di CRM/refund@ssm.com.my dengan menyatakan nombor rujukan kes sebagai rujukan. <br>"
                    + "Sekian, terima kasih. <br><br>"
                    + "[THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL]";

            // Save the email details using the global email service
            emailService.saveEmailDets(new Email("Notification", custEmail, "", "",
                    "Approval of Refund Application - " + rtt_app_no, body, null));

        }

        if (rttstatus.equals("PRS") && refundType.equals("CB")) {
            // Fetch both the app‑no and the customer email in one shot
            RttAppEmailDto info = pttService.sp_getRttAppEmail(rtt_wf_id);
            if (info == null) {
                log.error("[RTTWF:{}] Data inconsistency: Workflow exists but customer info is missing. rtt_wf_id={}",
                        rtt_wf_id, rtt_wf_id);

                return APIResponse.CustomErrorResponse(
                        "Data inconsistency detected for workflow ID " + rtt_wf_id,
                        "200",
                        HttpStatus.NOT_FOUND);
            }
            String rtt_app_no = info.getRttAppNo();
            String custEmail = info.getCustEmail();

            String body = "Assalamualaikum and Greetings,<br><br>" +
                    "Dear Sir/Madam,<br>"
                    + "We are pleased to inform you that your refund request has been approved by our finance team.<br>"
                    + "The refund will now be processed via a <strong>chargeback</strong> through our payment gateway provider. This means the amount will be credited back to your original payment method (debit/credit card) by your bank or card issuer. <br>"
                    + "Additional information: <br>"
                    + "<ul>"
                    + "<li>This process will be fully managed by our payment gateway provider.</li>"
                    + "<li>The refund may take <strong>7 to 14 working days</strong> to be completed, depending on your bank's policy.</li>"
                    + "<li>You may receive a notification or confirmation directly from your bank once the refund is finalized.</li>"
                    + "</ul><br>"
                    + "If you have any further inquiries, you may contact us via email at CRM/refund@ssm.com.my by stating the case reference number for reference.<br>"
                    + "Your cooperation and attention are highly appreciated. <br>"
                    + "Thank you. <br><br>"

                    + "Assalamualaikum dan Salam Sejahtera, <br><br>"
                    + "Tuan/Puan,<br>"
                    + "Dimaklumkan bahawa permohonan bayaran balik bagi transaksi Tuan/Puan telah diluluskan oleh pihak kewangan kami. <br>"
                    + "Seterusnya, bayaran balik akan diproses melalui kaedah <strong>chargeback</strong> oleh pihak gerbang pembayaran. Ini bermakna jumlah bayaran akan dikreditkan semula ke akaun asal Tuan/Puan (kad debit/kredit) oleh pihak bank atau pengeluar kad. <br>"
                    + "Maklumat tambahan: <br>"
                    + "<ul>"
                    + "<li>Proses ini akan dikendalikan sepenuhnya oleh penyedia gerbang pembayaran kami.</li>"
                    + "<li>Tempoh pemprosesan dijangka mengambil masa antara <strong>7 hingga 14 hari bekerja</strong>, bergantung kepada polisi pihak bank.</li>"
                    + "<li>Tuan/Puan mungkin akan menerima notifikasi atau pengesahan daripada pihak bank apabila bayaran balik telah selesai.</li>"
                    + "</ul><br>"
                    + "Sekiranya terdapat sebarang pertanyaan lanjut, tuan/puan boleh menghubungi kami melalui emel di CRM/refund@ssm.com.my dengan menyatakan nombor rujukan kes sebagai rujukan. <br>"
                    + "Kerjasama dan perhatian tuan/puan amat dihargai. <br>"
                    + "Sekian, terima kasih. <br><br>"
                    + "[THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL]";

            // Save the email details using the global email service
            emailService.saveEmailDets(new Email("Notification", custEmail, "", "",
                    "Approval of Chargeback - " + rtt_app_no, body, null));

            // sent email to PG personal -- approve email

            RTTChargebackRequest chargebackRequest = new RTTChargebackRequest();

            chargebackRequest.setI_rtt_wf_id(rtt_wf_id.toString());
            List<Object[]> resultForChargeback = rttReturnedChequeRepository.sp_getrttwfchargeback(chargebackRequest);
            if (resultForChargeback != null && !resultForChargeback.isEmpty()) {
                for (Object[] row : resultForChargeback) {
                    Integer rttwfid = (Integer) row[0];
                    String orn_no = (String) row[1];
                    String txn_id = (row[2] == null || ((String) row[2]).trim().isEmpty())
                            ? "-"
                            : ((String) row[2]).trim();
                    // do:
                    Object rawDate = row[3];
                    Date dt_requested;
                    if (rawDate instanceof java.sql.Timestamp) {
                        // convert Timestamp → java.sql.Date
                        java.sql.Timestamp ts = (java.sql.Timestamp) rawDate;
                        dt_requested = new java.sql.Date(ts.getTime());
                    } else if (rawDate instanceof java.sql.Date) {
                        // already the correct type
                        dt_requested = (java.sql.Date) rawDate;
                    } else {
                        // fallback if it ever comes back as a java.util.Date
                        dt_requested = new java.sql.Date(((java.util.Date) rawDate).getTime());
                    }
                    String requested_by = (String) row[4];
                    String rtt_status = (String) row[5];
                    String pg_email = (String) row[6];

                    // Send email to PG personal
                    sendApproveEmail(pg_email, "", orn_no, txn_id, dt_requested, requested_by);
                }

            } else {
                System.out.println("No data found for chargeback request.");
            }

        }

        // sent email to PG personal -- Reject email

        if (rttstatus.equals("RR") && refundType.equals("CB")) {
            RTTChargebackRequest chargebackRequest = new RTTChargebackRequest();

            chargebackRequest.setI_rtt_wf_id(rtt_wf_id.toString());
            List<Object[]> resultForChargeback = rttReturnedChequeRepository.sp_getrttwfchargeback(chargebackRequest);
            if (resultForChargeback != null && !resultForChargeback.isEmpty()) {
                for (Object[] row : resultForChargeback) {
                    Integer rttwfid = (Integer) row[0];
                    String orn_no = (String) row[1];
                    String txn_id = (row[2] == null || ((String) row[2]).trim().isEmpty())
                            ? "-"
                            : ((String) row[2]).trim();
                    // do:
                    Object rawDate = row[3];
                    Date dt_requested;
                    if (rawDate instanceof java.sql.Timestamp) {
                        // convert Timestamp → java.sql.Date
                        java.sql.Timestamp ts = (java.sql.Timestamp) rawDate;
                        dt_requested = new java.sql.Date(ts.getTime());
                    } else if (rawDate instanceof java.sql.Date) {
                        // already the correct type
                        dt_requested = (java.sql.Date) rawDate;
                    } else {
                        // fallback if it ever comes back as a java.util.Date
                        dt_requested = new java.sql.Date(((java.util.Date) rawDate).getTime());
                    }
                    String requested_by = (String) row[4];
                    String rtt_status = (String) row[5];
                    String pg_email = (String) row[6];

                    // Send email to PG personal
                    sendRejectEmail(pg_email, "", orn_no, txn_id, dt_requested, requested_by,
                            remarks);
                }

            } else {
                System.out.println("No data found for chargeback request.");
            }

        }

        if (rttstatus.equals("RR")) {
            // Rejected
            RttAppEmailDto info = pttService.sp_getRttAppEmail(rtt_wf_id);
            if (info == null) {
                log.error("[RTTWF:{}] Data inconsistency: Workflow exists but customer info is missing. rtt_wf_id={}",
                        rtt_wf_id, rtt_wf_id);

                return APIResponse.CustomErrorResponse(
                        "Data inconsistency detected for workflow ID " + rtt_wf_id,
                        "200",
                        HttpStatus.NOT_FOUND);
            }

            String rtt_app_no = info.getRttAppNo();
            String custEmail = info.getCustEmail();
            String orn_no = info.getOrnNo();

            String body = "Assalamualaikum and Greetings,<br><br>"
                    + "Dear Sir/Madam,<br>"
                    + "Please be informed that your refund application with reference number <strong>" + rtt_app_no
                    + "</strong> could not be approved following a thorough review and consideration. <br>"
                    + "If you require further information regarding this decision, please contact us via email at CRM/refund@ssm.com.my by quoting your case reference number for reference.<br>"
                    + "We greatly appreciate your attention and cooperation. <br>"
                    + "Thanks you. <br><br>"

                    + "Assalamualaikum dan Salam Sejahtera,<br><br>"
                    + "Tuan/Puan,<br>"
                    + "Dimaklumkan bahawa permohonan bayaran balik tuan/puan dengan nombor rujukan <strong>"
                    + rtt_app_no
                    + "</strong> tidak dapat diluluskan selepas semakan dan pertimbangan yang telah dibuat. <br>"
                    + "Sekiranya tuan/puan memerlukan maklumat lanjut berkenaan keputusan ini, sila hubungi kami melalui emel di CRM/refund@ssm.com.my dengan menyatakan nombor rujukan kes sebagai rujukan. <br>"
                    + "Segala perhatian dan kerjasama tuan/puan amat dihargai. <br>"
                    + "Sekian, terima kasih. <br><br>"
                    + "[THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL]";

            // Save the email details using the global email service
            emailService
                    .saveEmailDets(new Email("Notification", custEmail, "", "",
                            "Notification of Unsuccessful Refund Application", body, null));

        }

        if (rttstatus.equals("RS") && refundType.equals("RF")) {
            // Fetch both the app‑no and the customer email in one shot
            RttAppEmailDto info = pttService.sp_getRttAppEmail(rtt_wf_id);
            if (info == null) {
                log.error("[RTTWF:{}] Data inconsistency: Workflow exists but customer info is missing. rtt_wf_id={}",
                        rtt_wf_id, rtt_wf_id);

                return APIResponse.CustomErrorResponse(
                        "Data inconsistency detected for workflow ID " + rtt_wf_id,
                        "200",
                        HttpStatus.NOT_FOUND);
            }

            String rtt_app_no = info.getRttAppNo();
            String custEmail = info.getCustEmail();
            String orn_no = info.getOrnNo();

            String body = "Assalamualaikum and Greetings,<br><br>" +
                    "Dear Sir/Madam,<br>"
                    + "Please be informed that your refund application with reference number <strong>" + rtt_app_no
                    + "</strong> has been approved by the Approving Body (BYM). <br>"
                    + "The approved refund amount is <strong> RM " + refundAmt
                    + " </strong>. The payment will be processed within the stipulated timeframe and credited to the account specified in your application. "
                    + "Any delays will be communicated from time to time. <br>"
                    + "If you have any further inquiries, please contact us via email at CRM/refund@ssm.com.my by quoting your case reference number for reference. <br>"
                    + "Thank you. <br><br>"

                    + "Assalamualaikum dan Salam Sejahtera, <br><br>"
                    + "Tuan/Puan,<br>"
                    + "Dimaklumkan bahawa permohonan bayaran balik tuan/puan dengan nombor rujukan " + rtt_app_no
                    + " telah diluluskan oleh Badan Yang Meluluskan (BYM). <br>"
                    + "Jumlah bayaran balik yang telah diluluskan adalah sebanyak RM " + refundAmt
                    + ". Bayaran akan diproses dalam tempoh yang ditetapkan dan dikreditkan ke akaun yang telah dinyatakan dalam permohonan."
                    + "Sebarang kelewatan akan dimaklumkan dari semasa ke semasa. <br>"
                    + "Sekiranya tuan/puan mempunyai sebarang pertanyaan lanjut, sila hubungi kami melalui emel di CRM/refund@ssm.com.my dengan menyatakan nombor rujukan kes sebagai rujukan. <br>"
                    + "Sekian, terima kasih. <br><br>"
                    + "[THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL]";

            // Save the email details using the global email service
            emailService.saveEmailDets(new Email("Notification", custEmail, "", "",
                    "Approval of Refund Application - " + rtt_app_no, body, null));

        }

        notificationSvc.sendNotificationUpdate();
        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/getrttform")
    public ResponseEntity<ApiResponse<List<RttForm>>> sp_getrttform(HttpServletRequest request,
            @RequestBody RefundApprovalDetReq getRequest) {

        List<RttForm> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getrttform(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getrttdoc")
    public ResponseEntity<?> getrttDocuments(@RequestBody RefundApprovalDetReq req) {
        System.out.println("Received request for listDocuments: " + req);
        try {
            List<RefundDoc> documents = spService.sp_getrttdoc(req); // Implement this in your service layer
            System.out.println("Fetched document metadata: " + documents);
            return ResponseEntity.ok(documents);
        } catch (Exception e) {
            System.out.println("Error in listDocuments: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving document metadata: " + e.getMessage());
        }
    }

    @PostMapping(value = "/updaterttwfreturntask")
    public ResponseEntity<ApiResponse<Integer>> sp_updrttwfreturntask(HttpServletRequest request,
            @RequestBody RefundApprovalDetReq rttwfrequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        rttwfrequest.setI_modified_by(authService.getLoginUserName());
        // rttwfrequest.setI_pickup_by(authService.getLoginUserName());

        // Insert API to check the 3rd party API for the status of the refund

        // failed --> return APIResponse.InternalServerError();

        // correct as below code
        Integer result = spService.sp_updrttwf_returntask(rttwfrequest);

        if (result == null) {
            return APIResponse.InternalServerError();
        }

        notificationSvc.sendNotificationUpdate();

        return APIResponse.SuccessResponse(result);

    }

    private void sendApproveEmail(String email, String BCC, String Ornno, String TxnID, Date dtRequested,
            String requestedBy) {
        try {
            String subject = "Chargeback Request Approved";
            String body = "<strong>Order Reference Number: " + Ornno
                    + "<br>Transaction ID: " + TxnID
                    + "<br>Date Requested: " + dtRequested
                    + "<br>Requested By: " + requestedBy + "</strong>"
                    + "<br><br>Dear Sir/Madam,"
                    + "<br>Your chargeback request in RMS has been approved. Please proceed with the chargeback process."
                    + "<br>Thank you for using our services."

                    + "<br><br>Tuan/Puan,"
                    + "<br>Permintaan caj balik anda dalam RMS telah diluluskan. Sila teruskan dengan proses caj balik."
                    + "<br>Terima kasih kerana menggunakan perkhidmatan kami."
                    + "<br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL]";

            emailService.saveEmailDets(new Email("Notification", email, "", "",
                    subject, body, null));

            System.out.println("INFO: Chargeback approval email sent successfully to " + email);
        } catch (Exception e) {
            String ErrorMsg = "ERROR: Failed to send email to " + email + ": " + e.getMessage();
            log.error(ErrorMsg, e);
        }
    }

    private void sendRejectEmail(String email, String BCC, String Ornno, String TxnID, Date dtRequested,
            String requestedBy, String remarks) {
        try {
            String subject = "Chargeback Request Rejected";
            String body = "<strong>Order Reference Number: " + Ornno
                    + "<br>Transaction ID: " + TxnID
                    + "<br>Date Requested: " + dtRequested
                    + "<br>Requested By: " + requestedBy + "</strong>"
                    + "<br><br>Dear Sir/Madam,"
                    + "<br>Your chargeback request in RMS has been rejected."
                    + "<br>Rejected reason: " + remarks
                    + "<br>Thank you for using our services."

                    + "<br><br>Tuan/Puan,"
                    + "</br>Permintaan caj balik anda dalam RMS telah ditolak."
                    + "<br>Sebab penolakan: " + remarks
                    + "<br>Terima kasih kerana menggunakan perkhidmatan kami."
                    + "<br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL]";

            emailService.saveEmailDets(new Email("Notification", email, "", "",
                    subject, body, null));

            System.out.println("INFO: Chargeback Rejected email sent successfully to " + email);
        } catch (Exception e) {
            String ErrorMsg = "ERROR: Failed to send email to " + email + ": " + e.getMessage();
            log.error(ErrorMsg, e);
        }
    }

}
