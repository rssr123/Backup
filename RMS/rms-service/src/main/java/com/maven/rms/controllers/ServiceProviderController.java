package com.maven.rms.controllers;

import java.math.BigDecimal;
import java.security.Provider.Service;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.Email;
import com.maven.rms.models.NonBillRCEmail;
import com.maven.rms.models.ReprintRcpt;
import com.maven.rms.models.ReprintRcptRequest;
import com.maven.rms.models.ServiceProvider;
import com.maven.rms.models.ServiceProviderEmail;
import com.maven.rms.models.ServiceProviderEmailMtt;
import com.maven.rms.models.ServiceProviderProfile;
import com.maven.rms.models.ServiceProviderProfileRequest;
import com.maven.rms.models.ServiceProviderRequest;
import com.maven.rms.models.TaxCd;
import com.maven.rms.models.TaxCdRequest;
import com.maven.rms.models.OTC.OTCReturnedChequeRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.repositories.IServiceProviderRepository;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.ReprintReceiptService;
import com.maven.rms.services.ServiceProviderService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;
import com.maven.rms.utils.SystemStatus;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/sp/v1")
@Slf4j
public class ServiceProviderController {

    @Autowired
    private AuthService authService;

    @Autowired
    private ServiceProviderService serviceProviderService;

    @Autowired
    private IServiceProviderRepository serviceProviderRepository;

    @Autowired
    private EmailService emailService;

    @Value("${rms.application.onlinePortalURL}")
    private String onlinePortalURL;

    @PostMapping(value = "/serviceprovider")
    public ResponseEntity<ApiResponse<List<ServiceProvider>>> getserviceproviderpayment(
            HttpServletRequest request,
            @RequestBody ServiceProviderRequest serviceProviderRequest) {
        List<ServiceProvider> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = serviceProviderService.sp_getserviceproviderpayment(serviceProviderRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/serviceprovideremail")
    public ResponseEntity<ApiResponse<List<ServiceProviderEmail>>> getserviceprovideremail(
            HttpServletRequest request,
            @RequestBody ServiceProviderRequest serviceProviderRequest) {
        List<ServiceProviderEmail> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = serviceProviderService.sp_getserviceprovideremail(serviceProviderRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);// need to change
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping("/send-notification")
    public ResponseEntity<ApiResponse<String>> sendNotificationEmail(HttpServletRequest request,
            @RequestBody ServiceProviderRequest serviceProviderRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer iAgBil = serviceProviderRequest.getI_ag_bil(); // Extract user input

        if (iAgBil == null) {
            return APIResponse.ErrorResponse("Missing required parameter: i_ag_bil");
        }

        // Retrieve service provider email details using i_ag_bil
        List<ServiceProviderEmail> serviceProviderEmails = serviceProviderService
                .sp_getserviceprovideremail(serviceProviderRequest);

        if (serviceProviderEmails.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);
        }

        // Extract the first record (assuming i_ag_bil is unique)
        ServiceProviderEmail serviceProviderEmail = serviceProviderEmails.get(0);

        // Send the email
        sendEmail(serviceProviderEmail.getAg_bil(), serviceProviderEmail.getEntity_nm(),
                serviceProviderEmail.getAg_bil_no(), serviceProviderEmail.getTotal_amt_payable(),
                serviceProviderEmail.getCust_email());

        // Update date_email_sent after successful email
        Integer result = serviceProviderService.sp_updserviceproviderdatepayment(serviceProviderRequest);
        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse("Email sent successfully.");
    }

    private void sendEmail(Integer agBil, String entityNm, String agBilNo, BigDecimal totalAmtPayable,
            String custEmail) {
        try {
            String redirect1 = onlinePortalURL + "/payment-page?pr=" + agBilNo;
            String redirect2 = onlinePortalURL + "/payment-page";

            String subject = "PENDING PAYMENT";

            String body = "Entity Name: " + entityNm
                    + "<br>Order Reference No.: " + agBilNo
                    + "<br>Total Amount Payable: " + "RM" + totalAmtPayable

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

            // emailService.sendMailHTML(new Email(subject, custEmail, "", "", subject,
            // body));
            emailService.saveEmailDets(new Email("Notification", custEmail, "", "", subject, body, null));
            log.debug("Email sent successfully to {}", custEmail);
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", custEmail, e.getMessage());
        }
    }

    @PostMapping(value = "/getserviceprovidermaintenance")
    public ResponseEntity<ApiResponse<List<ServiceProviderProfile>>> getserviceprovidermaintenance(
            HttpServletRequest request,
            @RequestBody ServiceProviderProfileRequest serviceProviderProfileRequest) {
        List<ServiceProviderProfile> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = serviceProviderService.sp_getserviceprovidermaintenance(serviceProviderProfileRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.TAX_CODE_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/insserviceprovidermaintenance")
    public ResponseEntity<ApiResponse<Integer>> insserviceprovidermaintenance(
            HttpServletRequest request,
            @RequestBody ServiceProviderProfileRequest serviceProviderProfileRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        serviceProviderProfileRequest.setI_created_by(authService.getLoginUserName());
        serviceProviderProfileRequest.setI_modified_by(authService.getLoginUserName());
        serviceProviderProfileRequest.setI_status(SystemStatus.Active.getMessage());

        Integer result = serviceProviderService.sp_insserviceprovidermaintenance(serviceProviderProfileRequest);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updserviceprovidermaintenance")
    public ResponseEntity<ApiResponse<Integer>> updserviceprovidermaintenance(
            HttpServletRequest request,
            @RequestBody ServiceProviderProfileRequest serviceProviderProfileRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        serviceProviderProfileRequest.setI_modified_by(authService.getLoginUserName());
        serviceProviderProfileRequest.setI_status(SystemStatus.Active.getMessage());

        Integer result = serviceProviderService.sp_updserviceprovidermaintenance(serviceProviderProfileRequest);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/delserviceprovidermaintenance")
    public ResponseEntity<ApiResponse<Integer>> delserviceprovidermaintenance(
            HttpServletRequest request,
            @RequestBody ServiceProviderProfileRequest serviceProviderProfileRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        serviceProviderProfileRequest.setI_modified_by(authService.getLoginUserName());
        Integer result = serviceProviderService.sp_delserviceprovidermaintenance(serviceProviderProfileRequest);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/updserviceproviderdatepayment")
    public ResponseEntity<ApiResponse<Integer>> updserviceproviderdatepayment(
            HttpServletRequest request,
            @RequestBody ServiceProviderRequest serviceProviderRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = serviceProviderService.sp_updserviceproviderdatepayment(serviceProviderRequest);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    @PostMapping(value = "/serviceprovideremailmtt")
    public ResponseEntity<ApiResponse<List<ServiceProviderEmailMtt>>> getServiceProviderEmailMtt(
            HttpServletRequest request,
            @RequestBody ServiceProviderRequest serviceProviderRequest) {
        List<ServiceProviderEmailMtt> result = Collections.emptyList();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = serviceProviderService.sp_getserviceprovideremailmtt(serviceProviderRequest);

        if (result.isEmpty()) {
            return APIResponse.InternalServerError();
        }
        return APIResponse.SuccessResponse(result);
    }

}
