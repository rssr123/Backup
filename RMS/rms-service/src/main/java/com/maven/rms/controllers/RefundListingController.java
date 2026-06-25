package com.maven.rms.controllers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.Email;
import com.maven.rms.models.PaymentItemDetails;
import com.maven.rms.models.PaymentRequest;
import com.maven.rms.models.RefundDetailPymtItem;
import com.maven.rms.models.RefundDetails;
import com.maven.rms.models.RefundDoc;
import com.maven.rms.models.RefundPGPaymentDetails;
import com.maven.rms.models.RefundPTTListing;
import com.maven.rms.models.RefundPTTListingDetReq;
import com.maven.rms.models.RefundRcpt;
import com.maven.rms.models.RefundTHTListing;
import com.maven.rms.models.RefundWFList;
import com.maven.rms.models.RefundWFListingDetReq;
import com.maven.rms.models.RttAppEmailDto;
import com.maven.rms.models.TaxCdRequest;
import com.maven.rms.models.UserRole;
import com.maven.rms.models.RefundInfo;
import com.maven.rms.models.RefundList;
import com.maven.rms.models.RefundHist;
import com.maven.rms.models.OTC.OTCCollectionReceiptingPymtItem;
import com.maven.rms.models.OTC.OTCPayment;
import com.maven.rms.models.OTC.OTCPaymentDetails;
import com.maven.rms.models.OTC.OTCPaymentRequest;
import com.maven.rms.models.OTC.OTCollectionReceiptingRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.IdamanAPIDownloadService;
import com.maven.rms.services.NotificationService;
import com.maven.rms.services.RefundPTTListingService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.Common;
import com.maven.rms.utils.ControllersEnum;
import com.maven.rms.utils.SystemStatus;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/refundl/v1")
@Slf4j
public class RefundListingController {

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthService authService;

    @Autowired
    private RefundPTTListingService spService;

    @Autowired
    private IdamanAPIDownloadService idamanAPIDownloadService;

    @Autowired
    private NotificationService notificationSvc;

    @PostMapping("/getrefundpttlisting")
    public ResponseEntity<ApiResponse<List<RefundPTTListing>>> sp_getRefundPTTListing(HttpServletRequest request,
            @RequestBody RefundPTTListingDetReq refundPTTListingRequest) {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<RefundPTTListing> result = spService.sp_getRefundPTTListing(refundPTTListingRequest);

        System.out.println("Received Request dt created from: " + refundPTTListingRequest.getI_orn_dt_fr());
        System.out.println("Received Request dt created to: " + refundPTTListingRequest.getI_orn_dt_to());

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);

    }

    @PostMapping("/getrttwfid")
    public ResponseEntity<ApiResponse<List<RefundList>>> sp_getrttwfid(
            HttpServletRequest request,
            @RequestBody RefundPTTListingDetReq refundPTTListingRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<RefundList> result = spService.sp_getrttwfid(refundPTTListingRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping("/getrefundoionline")
    public ResponseEntity<ApiResponse<List<RefundDetails>>> sp_getRefundOI_online(HttpServletRequest request,
            @RequestBody RefundPTTListingDetReq refundPTTListingRequest) {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<RefundDetails> result = spService.sp_getRefundOI_online(refundPTTListingRequest);
        System.out.println("Request received: " + result);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);

    }

    @PostMapping("/getrefundoiotc")
    public ResponseEntity<ApiResponse<List<RefundDetails>>> sp_getRefundOI_otc(HttpServletRequest request,
            @RequestBody RefundPTTListingDetReq refundPTTListingRequest) {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<RefundDetails> result = spService.sp_getRefundOI_otc(refundPTTListingRequest);
        System.out.println("Request received: " + result);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/getpymtitems")
    public ResponseEntity<ApiResponse<List<RefundDetailPymtItem>>> sp_getRefundPaymentItem(HttpServletRequest request,
            @RequestBody RefundPTTListingDetReq getRequest) {

        List<RefundDetailPymtItem> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getRefundPaymentItem(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getOTCPaymentHeader")
    public ResponseEntity<ApiResponse<List<OTCPayment>>> sp_getotccrpaymentheader(HttpServletRequest request,
            @RequestBody OTCPaymentRequest getRequest) {

        List<OTCPayment> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getotccrpaymentheader(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getOTCPaymentDetails")
    public ResponseEntity<ApiResponse<List<OTCPaymentDetails>>> sp_getotccrpaymentdetails(HttpServletRequest request,
            @RequestBody OTCPaymentRequest getRequest) {

        List<OTCPaymentDetails> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getotccrpaymentdetails(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getPGPaymentInfo")
    public ResponseEntity<ApiResponse<List<RefundPGPaymentDetails>>> sp_getrefundpaymentinfo_online(
            HttpServletRequest request,
            @RequestBody RefundPTTListingDetReq getRequest) {

        List<RefundPGPaymentDetails> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getrefundpaymentinfo_online(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getRefundPGRcpt")
    public ResponseEntity<ApiResponse<List<RefundRcpt>>> sp_getrefundpgrcpt(
            HttpServletRequest request,
            @RequestBody RefundPTTListingDetReq getRequest) {

        List<RefundRcpt> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getrefundpgrcpt(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getRefundOTCRcpt")
    public ResponseEntity<ApiResponse<List<RefundRcpt>>> sp_getrefundotcrcpt(
            HttpServletRequest request,
            @RequestBody RefundPTTListingDetReq getRequest) {

        List<RefundRcpt> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getrefundotcrcpt(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getRefundInfo")
    public ResponseEntity<ApiResponse<List<RefundInfo>>> sp_getrefundinfo(
            HttpServletRequest request,
            @RequestBody RefundPTTListingDetReq getRequest) {

        List<RefundInfo> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getrefundinfo(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/getRefundHist")
    public ResponseEntity<ApiResponse<List<RefundHist>>> sp_getrefundhist(
            HttpServletRequest request,
            @RequestBody RefundPTTListingDetReq getRequest) {

        List<RefundHist> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getrefundhist(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/addrttwf")
    public ResponseEntity<ApiResponse<Integer>> sp_processRefundRequest(
            HttpServletRequest request,
            @RequestBody RefundWFList insertRequest) {

        // Check for authentication
        if (!authService.isAuthenticated(request)) {
            return APIResponse.CustomErrorResponse("Unauthorized", "401", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Set user details
            insertRequest.setRequested_by(authService.getLoginUserName());
            insertRequest.setCreated_by(authService.getLoginUserName());
            insertRequest.setModified_by(authService.getLoginUserName());

            System.out.println("Updated Request with user details: " + insertRequest);

            // Validate payment item details
            List<PaymentItemDetails> paymentItems = insertRequest.getPayment_item_details();
            if (paymentItems == null || paymentItems.isEmpty()) {
                return APIResponse.CustomErrorResponse(
                        "Payment items are required for refund processing.",
                        "400",
                        HttpStatus.BAD_REQUEST);
            }

            // Prepare PaymentRequest object
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setPayment_item_details(paymentItems);

            // Call the service to process the refund request
            Integer result = spService.sp_processRefundRequest(insertRequest, paymentRequest);

            // Check result and return appropriate response
            if (result <= 0) {
                if (result == -3) {
                    return APIResponse.SuccessResponse(result);
                } else if (result == -2) {
                    return APIResponse.SuccessResponse(result);
                } else if (result == -1) {
                    return APIResponse.SuccessResponse(result);
                } else {
                    log.error("[RF:{}] Internal server error from refund service. result={}", result);
                    return APIResponse.InternalServerError();
                }
            }

            notificationSvc.sendNotificationUpdate();

            return APIResponse.SuccessResponse(result);
        } catch (Exception e) {
            // Log unexpected exceptions
            log.error("Unexpected error: {}", e.getMessage(), e);
            return APIResponse.InternalServerError();
        }
    }

    @PostMapping("/getrefundtht")
    public ResponseEntity<ApiResponse<List<RefundTHTListing>>> sp_getrefundtht(HttpServletRequest request,
            @RequestBody RefundPTTListingDetReq getRequest) {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String platformCall = getRequest.getI_platform_call();
        System.out.println("Platform call: " + platformCall);
        if ("pp".equals(platformCall)) {
            System.out.println("Login user name: " + authService.getLoginUserName());
            String loginUserName = authService.getLoginUserName();
            if (loginUserName == null || loginUserName.isEmpty() || "Anonymous".equals(loginUserName)) {
                return APIResponse.CustomErrorResponse(
                        "Unauthorized access. Please log in to continue.",
                        "401",
                        HttpStatus.UNAUTHORIZED);
            } else {
                // getRequest.setI_created_by(null);
                getRequest.setI_created_by(authService.getLoginUserName());
            }

        } else if ("bo".equals(platformCall)) {

            System.out.println("Login user name: " + authService.getLoginUserName());
            // Optionally, set other fields for "bo" if needed
        } else {
            // If platform call is not valid, return unauthorized or bad request
            return APIResponse.CustomErrorResponse(
                    "Invalid platform call. Access denied.",
                    "403",
                    HttpStatus.FORBIDDEN);
        }
        System.out.println("Request received: " + getRequest);

        List<RefundTHTListing> result = spService.sp_getrefundtht(getRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/addrttwf_da")
    public ResponseEntity<ApiResponse<Integer>> sp_processRefundRequest_da(
            HttpServletRequest request,
            @RequestBody RefundWFList insertRequest) {

        // Check for authentication
        if (!authService.isAuthenticated(request)) {
            return APIResponse.CustomErrorResponse("Unauthorized", "401", HttpStatus.UNAUTHORIZED);
        }

        //  Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // simulate unauthorized for testing
        // boolean simulateUnauthorized = true;
        // if (simulateUnauthorized) {
        //     auth = null;
        // }

        // if (auth == null || (auth instanceof AnonymousAuthenticationToken)) {
        //     return APIResponse.CustomErrorResponse("Unauthorized", "401", HttpStatus.UNAUTHORIZED);
        // }

        try {
            // Set user details
            insertRequest.setRequested_by(authService.getLoginUserName());
            insertRequest.setCreated_by(authService.getLoginUserName());
            insertRequest.setModified_by(authService.getLoginUserName());

            // Validate payment item details
            List<PaymentItemDetails> paymentItems = insertRequest.getPayment_item_details();
            if (paymentItems == null || paymentItems.isEmpty()) {
                return APIResponse.CustomErrorResponse(
                        "Payment items are required for refund processing.",
                        "400",
                        HttpStatus.BAD_REQUEST);
            }

            // Prepare PaymentRequest object
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setPayment_item_details(paymentItems);

            // Call the service to process the refund request
            Integer result = spService.sp_processRefundRequest_da(insertRequest, paymentRequest);

            // Check result and return appropriate response
            if (result <= 0) {
                if (result == -3) {
                    return APIResponse.SuccessResponse(result);
                } else if (result == -1) {
                    return APIResponse.SuccessResponse(result);
                } else {
                    log.error("[RF:{}] Internal server error from refund service. result={}", result);
                    return APIResponse.InternalServerError();
                }
            }

            // Fetch both the app‑no and the customer email in one shot
            RttAppEmailDto info = spService.sp_getRttAppEmail(result);
            if (info == null) {
                return APIResponse.CustomErrorResponse(
                        "Unable to find workflow record for ID " + result, "404", HttpStatus.NOT_FOUND);
            }

            String rtt_app_no = info.getRttAppNo();
            // String custEmail = info.getCustEmail();
            System.out.println("Refund Application Number: " + rtt_app_no);

            String custEmail = (insertRequest.getCust_email() != null && !insertRequest.getCust_email().isEmpty())
                    ? insertRequest.getCust_email()
                    : "global@example.com";
            String custName = (insertRequest.getCust_nm() != null && !insertRequest.getCust_nm().isEmpty())
                    ? insertRequest.getCust_nm()
                    : "Customer";

            String ornno = (insertRequest.getOrn_no() != null && !insertRequest.getOrn_no().isEmpty())
                    ? insertRequest.getOrn_no()
                    : "xxx";

            String body = "Assalamualaikum dan Salam Sejahtera.<br><br>"
                    + "Dear Sir/Madam,"
                    + "<br>Please be informed that the Suruhanjaya Syarikat Malaysia has received your Refund Application. Your case reference number is <strong>"
                    + rtt_app_no + " or " + ornno + "</strong>.<br>"
                    + "The document will take 30 working days to process after the application is received.<br>"
                    + "For any inquiries regarding this application, please contact CRM by stating your case reference number / ORN <br>"
                    + "Thank you.<br><br>"
                    + "Tuan/Puan,<br>"
                    + "Dimaklumkan bahawa Suruhanjaya Syarikat Malaysia telah menerima Permohonan Bayaran Balik tuan/puan. Nombor rujukan kes tuan/puan adalah <strong>"
                    + rtt_app_no + " atau " + ornno + "</strong>.<br>"
                    + "Dokumen mengambil masa untuk diproses dalam 30 hari bekerja selepas permohonan diterima.<br>"
                    + "Sebarang pertanyaan mengenai permohonan ini bolehlah menghubungi CRM dengan menyatakan nombor rujukan kes / ORN <br>"
                    + "Sekian, terima kasih.<br><br>"
                    + "[THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL]";

            // Save the email details using the global email service
            // Ensure the Email constructor matches the arguments below or update as needed
            log.debug("Calling emailService.saveEmailDets for customer email: {}", custEmail);
            emailService.saveEmailDets(
                    new Email("Notification", custEmail, "", "", "Request Refund Submission", body, null));
            // If you get a constructor error, update the above line to match your Email
            // class constructor, e.g.:
            // emailService.saveEmailDets(new Email("Notification", custEmail, "Request
            // Refund Submission", body));

            notificationSvc.sendNotificationUpdate();
            return APIResponse.SuccessResponse(result);

        } catch (Exception e) {
            // Log unexpected exceptions
            log.error("Unexpected error: {}", e.getMessage(), e);
            return APIResponse.InternalServerError();
        }
    }

    @PostMapping(value = "/addrttform_rs02")
    public ResponseEntity<ApiResponse<Long>> sp_insrttform_rs02(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody RefundWFList insertRequest) {

        // Check for authentication

        if (!authService.isAuthenticated(request)) {
            return APIResponse.CustomErrorResponse("Unauthorized", "401", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Set user details
            insertRequest.setCreated_by(authService.getLoginUserName());
            insertRequest.setModified_by(authService.getLoginUserName());

            // Call the service to process the refund request
            Long result = spService.sp_insrttform_rs02(insertRequest);

            // Check result and return appropriate response
            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);
        } catch (Exception e) {
            // Log unexpected exceptions
            log.error("Unexpected error: {}", e.getMessage(), e);
            return APIResponse.InternalServerError();
        }
    }

    @PostMapping(value = "/addrttwf_rf")
    public ResponseEntity<ApiResponse<Integer>> sp_processRefundRequest_rf(
            HttpServletRequest request, HttpServletResponse response,
            @RequestBody RefundWFList insertRequest) {

        final String corrId = java.util.UUID.randomUUID().toString().substring(0, 8);
        log.info("[RF:{}] Received Request in Controller: {}", corrId, insertRequest);

        // Check for authentication
        if (!authService.isAuthenticated(request)) {
            log.warn("[RF:{}] Authentication failed for request.", corrId);
            return APIResponse.CustomErrorResponse("Unauthorized", "401", HttpStatus.UNAUTHORIZED);
        }

        // Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // simulate unauthorized for testing
        // boolean simulateUnauthorized = true;
        // if (simulateUnauthorized) {
        //     auth = null;
        // }

        // if (auth == null || (auth instanceof AnonymousAuthenticationToken)) {
        //     return APIResponse.CustomErrorResponse("Unauthorized", "401", HttpStatus.UNAUTHORIZED);
        // }

        try {
            // Set user details
            String loginUserName = authService.getLoginUserName();
            insertRequest.setRequested_by(loginUserName);
            insertRequest.setCreated_by(loginUserName);
            insertRequest.setModified_by(loginUserName);

            log.info("[RF:{}] Updated Request with user details: {}", corrId, insertRequest);

            // Validate payment item details
            List<PaymentItemDetails> paymentItems = insertRequest.getPayment_item_details();
            if (paymentItems == null || paymentItems.isEmpty()) {
                log.warn("[RF:{}] Missing payment item details.", corrId);
                return APIResponse.CustomErrorResponse(
                        "Payment items are required for refund processing.",
                        "400",
                        HttpStatus.BAD_REQUEST);
            }
            log.info("[RF:{}] Payment items count: {}", corrId, paymentItems.size());

            // Validate uploaded files and their total size immediately
            List<RefundDoc> filedoc = insertRequest.getUploadedFiles();
            if (filedoc == null || filedoc.isEmpty()) {
                log.warn("[RF:{}] Missing uploaded files.", corrId);
                return APIResponse.CustomErrorResponse(
                        "Uploaded files are required for refund processing.",
                        "400",
                        HttpStatus.BAD_REQUEST);
            }

            long totalFileSize = filedoc.stream().mapToLong(RefundDoc::getFileSize).sum();
            final long MAX_SIZE = 10L * 1024 * 1024; // 10 MB
            if (totalFileSize > MAX_SIZE) {
                log.warn("[RF:{}] Total file size exceeds 10 MB. Total: {} bytes", corrId, totalFileSize);
                return APIResponse.CustomErrorResponse(
                        "Total uploaded file size must not exceed 10 MB.",
                        "400",
                        HttpStatus.BAD_REQUEST);
            }
            log.info("[RF:{}] Uploaded files count: {}, total size: {} bytes", corrId, filedoc.size(), totalFileSize);

            // Prepare PaymentRequest object
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setPayment_item_details(paymentItems);
            log.info("[RF:{}] PaymentRequest prepared.", corrId);

            RefundWFList refundDoc = new RefundWFList();
            refundDoc.setUploadedFiles(insertRequest.getUploadedFiles());
            log.info("[RF:{}] Uploaded Files Passed to Service: {}", corrId, refundDoc.getUploadedFiles());

            // Call the service to process the refund request
            Integer result = spService.sp_processRefundRequest_rf(insertRequest, paymentRequest, refundDoc);
            log.info("[RF:{}] Service call result (rtt_wf_id): {}", corrId, result);

            // Check result and return appropriate response
            if (result <= 0) {
                if (result == -3) {
                    return APIResponse.SuccessResponse(result);
                } else if (result == -1) {
                    return APIResponse.SuccessResponse(result);
                } else {
                    log.error("[RF:{}] Internal server error from refund service. result={}", corrId, result);
                    return APIResponse.InternalServerError();
                }
            }

            // result is positive, proceed to send email
            RttAppEmailDto info = spService.sp_getRttAppEmail(result);
            if (info == null) {
                log.error("[RF:{}] Unable to find workflow record for ID {}", corrId, result);
                return APIResponse.CustomErrorResponse(
                        "Unable to find workflow record for ID " + result, "404", HttpStatus.NOT_FOUND);
            }

            String rtt_app_no = info.getRttAppNo();
            log.info("[RF:{}] Refund Application Number: {}", corrId, rtt_app_no);

            String custEmail = (insertRequest.getCust_email() != null && !insertRequest.getCust_email().isEmpty())
                    ? insertRequest.getCust_email()
                    : "global@example.com";
            String custName = (insertRequest.getCust_nm() != null && !insertRequest.getCust_nm().isEmpty())
                    ? insertRequest.getCust_nm()
                    : "Customer";

            String body = "Assalamualaikum and Greetings,<br><br>"
                    + "Dear Sir/Madam, <br>"
                    + "Please be informed that your refund application has been received by the Companies Commission of Malaysia (SSM). The case reference number for this application is <strong>"
                    + rtt_app_no + " </strong> .<br>"
                    + "This application will be processed within 30 working days from the date of receipt, subject to the receipt of all complete supporting documents.<br>"
                    + "If you have any further inquiries, you may contact us via email at CRM/refund@ssm.com.my by stating the case reference number for reference. <br>"
                    + "Your cooperation and attention are highly appreciated.<br>"
                    + "Thank you. <br><br>"
                    + "Assalamualaikum dan Salam Sejahtera,<br><br>"
                    + "Tuan/Puan,<br>"
                    + "Dimaklumkan bahawa permohonan bayaran balik tuan/puan telah diterima oleh Suruhanjaya Syarikat Malaysia (SSM). Nombor rujukan kes bagi permohonan ini ialah <strong>"
                    + rtt_app_no + "</strong> .<br>"
                    + "Permohonan ini akan diproses dalam tempoh 30 hari bekerja dari tarikh penerimaan, tertakluk kepada penerimaan semua dokumen sokongan yang lengkap.<br>"
                    + "Sekiranya terdapat sebarang pertanyaan lanjut, tuan/puan boleh menghubungi kami melalui emel di CRM/refund@ssm.com.my dengan menyatakan nombor rujukan kes sebagai rujukan.<br>"
                    + "Kerjasama dan perhatian tuan/puan amat dihargai.<br>"
                    + "Sekian, terima kasih.<br><br>"
                    + "[THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL]";

            emailService.saveEmailDets(
                    new Email("Notification", custEmail, "", "",
                            "Acknowledgement of Refund Application - " + rtt_app_no, body, null));

            notificationSvc.sendNotificationUpdate();
            log.info("[RF:{}] Email queued and notification sent. Success.", corrId);
            return APIResponse.SuccessResponse(result);

        } catch (Exception e) {
            // Log unexpected exceptions
            log.error("[RF:{}] Unexpected error in controller: {}",
                    corrId, e.getMessage(), e);
            return APIResponse.InternalServerError();
        }
    }

    @PostMapping(value = "/updaterefunddateexpiry")
    public ResponseEntity<ApiResponse<Integer>> sp_uptrtt_dateexpiry(
            HttpServletRequest request,
            @RequestBody RefundPTTListingDetReq updateRequest) {

        if (!authService.isAuthenticated(request)) {
            System.out.println("Authentication failed for request: " + request.getRemoteAddr());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        updateRequest.setI_modified_by(authService.getLoginUserName());

        System.out.println("Updated Request with user details: " + updateRequest);

        // Call the service and log the result
        Integer result = spService.sp_uptrtt_dateexpiry(updateRequest);
        System.out.println("SP Service Result: " + result);

        if (result <= 0) {
            System.out.println("Error: Service returned result <= 0");
            return APIResponse.InternalServerError();
        }

        // Log success response
        System.out.println("Success: Returning response with result " + result);
        return APIResponse.SuccessResponse(result);
    }

    @PutMapping(value = "/updrttwf_rf")
    public ResponseEntity<ApiResponse<Integer>> sp_updateRefundRequest_rf(
            HttpServletRequest request,
            @RequestBody RefundWFList updateRequest) {

        System.out.println("Received Update Request in Controller: " + updateRequest);

        // Check for authentication
        if (!authService.isAuthenticated(request)) {
            System.out.println("Authentication failed for update request.");
            return APIResponse.CustomErrorResponse("Unauthorized", "401", HttpStatus.UNAUTHORIZED);
        }

        try {
            // Set user details for update (typically only modified_by is updated)
            String loginUserName = authService.getLoginUserName();
            updateRequest.setModified_by(loginUserName);
            updateRequest.setCreated_by(loginUserName);

            // Ensure the refund workflow ID is provided for update
            if (updateRequest.getRtt_wf_id() == 0) {
                System.out.println("Missing refund workflow ID (rtt_wf_id) for update.");
                return APIResponse.CustomErrorResponse("Refund workflow ID is required for update.", "400",
                        HttpStatus.BAD_REQUEST);
            }

            // Validate payment item details
            List<PaymentItemDetails> paymentItems = updateRequest.getPayment_item_details();
            if (paymentItems == null || paymentItems.isEmpty()) {
                System.out.println("Missing payment item details.");
                return APIResponse.CustomErrorResponse("Payment items are required for refund update.", "400",
                        HttpStatus.BAD_REQUEST);
            }

            System.out.println("Payment items: " + paymentItems);

            // Validate uploaded files (new documents will be inserted)
            List<RefundDoc> fileDocs = updateRequest.getUploadedFiles();
            if (fileDocs != null && !fileDocs.isEmpty()) {
                long totalFileSize = fileDocs.stream().mapToLong(RefundDoc::getFileSize).sum();
                final long MAX_SIZE = 10 * 1024 * 1024; // 10 MB in bytes
                if (totalFileSize > MAX_SIZE) {
                    System.out
                            .println("Total uploaded file size exceeds 10 MB. Total size: " + totalFileSize + " bytes");
                    return APIResponse.CustomErrorResponse("Total uploaded file size must not exceed 10 MB.", "400",
                            HttpStatus.BAD_REQUEST);
                }
                System.out.println("Uploaded files: " + fileDocs);
            } else {
                System.out.println("No new refund documents provided, so no document insertions will be performed.");
            }

            // Prepare PaymentRequest object
            PaymentRequest paymentRequest = new PaymentRequest();
            paymentRequest.setPayment_item_details(paymentItems);
            System.out.println("PaymentRequest object: " + paymentRequest);

            // Call the service to process the refund update request.
            // The update service should handle:
            // - Updating the main refund request record via a stored procedure (e.g.
            // sp_uprttwf_rf)
            // - Updating existing refund item rows based on rtt_item_id
            // - Inserting new refund items (if no rtt_item_id is provided)
            // - Inserting any new refund documents (rttDocument)
            Integer result = spService.sp_updateRefundRequest_rf(updateRequest, paymentRequest, updateRequest);

            System.out.println("Service update call result: " + result);

            // Check result and return appropriate response
            if (result <= 0) {
                return APIResponse.InternalServerError();
            }

            notificationSvc.sendNotificationUpdate();

            return APIResponse.SuccessResponse(result);
        } catch (Exception e) {
            System.out.println("Unexpected error during update: " + e.getMessage());
            e.printStackTrace();
            return APIResponse.InternalServerError();
        }
    }

    @PostMapping("/getrefundlisting")
    public ResponseEntity<ApiResponse<List<RefundList>>> sp_getRefundListing(HttpServletRequest request,
            @RequestBody RefundPTTListingDetReq refundPTTListingRequest) {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        System.out.println("Received Request dt created from: " + refundPTTListingRequest.getI_dt_created_fr());
        System.out.println("Received Request dt created to: " + refundPTTListingRequest.getI_dt_created_to());

        List<RefundList> result = spService.sp_getRefundListing(refundPTTListingRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.REFUNDPTT_LISTING_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);

    }

}
