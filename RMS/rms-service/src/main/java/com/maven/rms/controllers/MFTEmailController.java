package com.maven.rms.controllers;

import java.math.BigInteger;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.Email;
import com.maven.rms.models.MFTEmailRequest;
import com.maven.rms.models.MFTWF;
import com.maven.rms.models.MFTWFHistory;
import com.maven.rms.models.Param;
import com.maven.rms.models.ParamRequest;
import com.maven.rms.models.RMSUser;
import com.maven.rms.models.RMSUserRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.CommonService;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.MTTService;
import com.maven.rms.services.OnlinePaymentService;
import com.maven.rms.services.StoreProcedureService;
import com.maven.rms.services.UserRoleService;
import com.maven.rms.utils.RMSLogger;
import com.maven.rms.utils.receipts.MTTPGReceiptGenerator;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.CacheManager;
import com.maven.rms.utils.Common;
import com.maven.rms.utils.EGHLPostUtility;

@RestController
@RequestMapping("/api/mftemail/v1")
@Slf4j
public class MFTEmailController {

    //private static final Logger logger = LoggerFactory.getLogger(MFTEmailController.class);
    private EmailService emailService;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private CommonService commonService;

    @Value("${rms.application.backPortalURL}")
    private String url;

    public MFTEmailController(EmailService emailService, UserRoleService userRoleService
                             ,CommonService commonService) {
        this.emailService = emailService;
        this.userRoleService = userRoleService;
        this.commonService = commonService;
            }


    @PostMapping(value = "/backend")
    public void postBackend(HttpServletRequest request, @RequestBody MFTEmailRequest mftEmailRequest){
      
           
            String subject = "";
            String body="";
            String redirect="";
            String statusDesription ="";
            String tempFeeDetailId = "";
            String editMode = "";
            String showRequesterTable ="";

            //to get user for send to start
            RMSUser rmsUser = new RMSUser();
            RMSUserRequest rmsUserRequest = new RMSUserRequest();
            rmsUserRequest.setI_ssm4uuserrefno(mftEmailRequest.getI_send_to());
            // rmsUser= spService.sp_getuserdetail(mftEmailRequest.getI_send_to());
            rmsUser= userRoleService.sp_getuserdetail(rmsUserRequest);
            //ParamRequest paramRequest = new ParamRequest(1,1,mftEmailRequest.getI_status(),"Status-MFT");

            List<Param> paramList = commonService.sp_getparam(1,1,mftEmailRequest.getI_status(),"Status-MFT");
            if (!paramList.isEmpty()) {
                // If the list is not empty, get the first element
                statusDesription = paramList.get(0).getNm_en();
            }

            if(mftEmailRequest.getI_fee_detail_id() == null){
                tempFeeDetailId = mftEmailRequest.getI_r_fee_det_nm();
            }
            else{
                tempFeeDetailId = mftEmailRequest.getI_fee_detail_id();
            }


            if(mftEmailRequest.getI_action() != null){
                if(mftEmailRequest.getI_action().equals("Request Add")  || mftEmailRequest.getI_action().equals("Request Add-FIN")){
                    editMode="false";
                }
                else{
                    editMode="true";
                }

                if(mftEmailRequest.getI_action().equals("Request Add-FIN")  || mftEmailRequest.getI_action().equals("Request Edit-FIN")){
                    showRequesterTable="false";
                }
                else{
                    showRequesterTable="true";
                }
            }

           // System.out.println("StatusDesription is "+statusDesription);
            // System.out.println("rmsUser.getEmail() is "+rmsUser.getEmail());
            log.debug("rmsUser.getEmail() is "+rmsUser.getEmail());
              //to get user for send to end

            // System.out.println("mftEmailRequest.getI_action() is "+mftEmailRequest.getI_action());
            log.debug("mftEmailRequest.getI_action() is "+mftEmailRequest.getI_action());
            //System.out.println("mftEmailRequest.getI_status() is "+mftEmailRequest.getI_status());

            //start requester
            if(mftEmailRequest.getI_action().equals("Request Add") && mftEmailRequest.getI_status().equals("P-RHOD")){
                redirect = url + "/mft-reqhod-appr-add?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&status__From_Assigned=" + mftEmailRequest.getI_status();
            }
             else if(mftEmailRequest.getI_action().equals("Request Add") && mftEmailRequest.getI_status().equals("P-FA")){
                redirect = url + "/mft-fa-appr-add?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&status__From_Assigned=" + mftEmailRequest.getI_status();
            }
            else if(mftEmailRequest.getI_action().equals("Request Add") && mftEmailRequest.getI_status().equals("P-FHOD")){
                redirect = url + "/mft-fhod-appr-add?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&edit_Mode=" + editMode
                + "&status__From_Assigned=" + mftEmailRequest.getI_status();
            }
            else if(mftEmailRequest.getI_action().equals("Request Add") && mftEmailRequest.getI_status().equals("Q-R")){
                redirect = url + "/mft-req-form-add?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&status__From_Assigned=" + mftEmailRequest.getI_status();
            }
            else if(mftEmailRequest.getI_action().equals("Request Add") && mftEmailRequest.getI_status().equals("Q-RHOD")){
                redirect = url + "/mft-reqhod-appr-add?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&status__From_Assigned=" + mftEmailRequest.getI_status();
            }
            else if(mftEmailRequest.getI_action().equals("Request Add") && mftEmailRequest.getI_status().equals("Q-FA")){
                redirect = url + "/mft-fa-appr-add?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&status__From_Assigned=" + mftEmailRequest.getI_status();
            }
            else if(mftEmailRequest.getI_action().equals("Request Edit") && mftEmailRequest.getI_status().equals("P-RHOD")){
                redirect = url + "/mft-reqhod-appr-edit?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&status__From_Assigned=" + mftEmailRequest.getI_status();
            }
            else if(mftEmailRequest.getI_action().equals("Request Edit") && mftEmailRequest.getI_status().equals("P-FA")){
                redirect = url + "/mft-fa-appr-edit?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&status__From_Assigned=" + mftEmailRequest.getI_status()
                + "&fee_detail_pk=" + mftEmailRequest.getI_fee_detail_pk();

            }
            else if(mftEmailRequest.getI_action().equals("Request Edit") && mftEmailRequest.getI_status().equals("P-FHOD")){
                redirect = url + "/mft-fhod-appr-add?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&edit_Mode=" + editMode
                + "&fee_detail_pk=" + mftEmailRequest.getI_fee_detail_pk()
                + "&status__From_Assigned=" + mftEmailRequest.getI_status();
            }
            else if(mftEmailRequest.getI_action().equals("Request Edit") && mftEmailRequest.getI_status().equals("Q-R")){
                redirect = url + "/mft-req-form-edit?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&status__From_Assigned=" + mftEmailRequest.getI_status();
            }
            else if(mftEmailRequest.getI_action().equals("Request Edit") && mftEmailRequest.getI_status().equals("Q-RHOD")){
                redirect = url + "/mft-reqhod-appr-edit?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&status__From_Assigned=" + mftEmailRequest.getI_status();
            }
            else if(mftEmailRequest.getI_action().equals("Request Edit") && mftEmailRequest.getI_status().equals("Q-FA")){
                redirect = url + "/mft-fa-appr-edit?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&status__From_Assigned=" + mftEmailRequest.getI_status()
                + "&fee_detail_pk=" + mftEmailRequest.getI_fee_detail_pk();
            }

            //start finance admin
            else if(mftEmailRequest.getI_action().equals("Request Add-FIN") && mftEmailRequest.getI_status().equals("P-FHOD")){
                redirect = url + "/mft-fa-fhod-appr-add?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&edit_Mode=" + editMode
                + "&status__From_Assigned=" + mftEmailRequest.getI_status();
            }
            else if(mftEmailRequest.getI_action().equals("Request Add-FIN") && mftEmailRequest.getI_status().equals("Q-FA")){
                redirect = url + "/mft-fa-fa-rqt-add?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&status__From_Assigned=" + mftEmailRequest.getI_status();
            }
            else if(mftEmailRequest.getI_action().equals("Request Edit-FIN") && mftEmailRequest.getI_status().equals("P-FHOD")){
                redirect = url + "/mft-fa-fhod-appr-add?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&edit_Mode=" + editMode
                + "&fee_detail_pk=" + mftEmailRequest.getI_fee_detail_pk()
                + "&status__From_Assigned=" + mftEmailRequest.getI_status();
            }
             else if(mftEmailRequest.getI_action().equals("Request Edit-FIN") && mftEmailRequest.getI_status().equals("Q-FA")){
                redirect = url + "/mft-fa-fa-rqt-edit?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&fee_detail_pk=" + mftEmailRequest.getI_fee_detail_pk()
                + "&status__From_Assigned=" + mftEmailRequest.getI_status();
            }
            //end finance admin

            //created task
            else if(mftEmailRequest.getI_status().equals("RJ-RHOD") || mftEmailRequest.getI_status().equals("RJ-FA")
            || mftEmailRequest.getI_status().equals("RJ-FHOD") || mftEmailRequest.getI_status().equals("APV")||
            mftEmailRequest.getI_status().equals("C")||mftEmailRequest.getI_status().equals("EFT")){
                redirect = url + "/created-task-details?"
                + "wf_id=" + mftEmailRequest.getI_wf_id()
                + "&task_id=" + mftEmailRequest.getI_task_id()
                + "&fee_detail_pk=" + mftEmailRequest.getI_fee_detail_pk()
                + "&status__From_Assigned=" + mftEmailRequest.getI_status()
                + "&edit_Mode=" + editMode
                + "&show_requester_table=" + showRequesterTable;
            }

            // System.out.println("redirect is "+redirect);
            log.debug("redirect is "+redirect);

            //email setting
            if (mftEmailRequest.getI_status().equals("P-RHOD") || mftEmailRequest.getI_status().equals("P-FA")
                 || mftEmailRequest.getI_status().equals("P-FHOD")) {

                subject = "MFT PENDING APPROVAL - TASK ID " + mftEmailRequest.getI_task_id();

                body = "Fee Detail ID: " + (tempFeeDetailId != null ? tempFeeDetailId : "-")
                + "<br>Task ID: " + mftEmailRequest.getI_task_id()
                + "<br>Task Status: " + statusDesription
                + "<br><br>Dear Sir/Madam,<br><br>"
                + "We wish to inform you that task " + mftEmailRequest.getI_task_id() + " is currently awaiting approval in our system. "
                + "We kindly request your attention to review and take necessary action on this task."
                + "<br><br><a href='" + redirect + "'>Click here</a> to begin processing task."
                + "<br><br>***PLEASE IGNORE THIS EMAIL IF YOUR TASK ALREADY APPROVED *** "
                + "<br><br>Thank you for using our services.<br><br><br>Tuan/Puan,<br>"
                + "<br>Kami ingin memaklumkan bahawa task " + mftEmailRequest.getI_task_id()
                + " sedang menunggu kelulusan dalam sistem kami. Kami memohon Tuan/Puan untuk menyemak dan mengambil tindakan selanjutnya terhadap tugasan ini."
                + "<br><br><a href='" + redirect + "'>Klik di sini</a> untuk tujuan pemprosesan tugasan."
                + "<br><br>***MOHON ABAIKAN EMEL INI JIKA TASK ANDA TELAH DILULUSKAN ***"
                + "<br><br>Terima kasih kerana menggunakan perkhidmatan kami."
                + "<br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
                + "DO NOT REPLY DIRECTLY TO THIS EMAIL] <br>";
        

            }
            else if(mftEmailRequest.getI_status().equals("Q-R") || mftEmailRequest.getI_status().equals("Q-RHOD")
                 || mftEmailRequest.getI_status().equals("Q-FA")){

                subject = "MFT PENDING QUERY - TASK ID " + mftEmailRequest.getI_task_id();

                body = "Fee Detail ID: " + (tempFeeDetailId != null ? tempFeeDetailId : "-")
                + "<br>Task ID: " + mftEmailRequest.getI_task_id()
                + "<br>Task Status: " + statusDesription
                + "<br><br>Dear Sir/Madam,<br><br>"
                + "We have identified a task " + mftEmailRequest.getI_task_id() + " that requires further clarification. "
                + "Please take a moment to review and address the query related to this task."
                + "<br><br><a href='" + redirect + "'>Click here</a> to begin processing task."
                + "<br><br>***PLEASE IGNORE THIS EMAIL IF YOUR QUERY ALREADY APPROVED *** "
                + "<br><br>Thank you for using our services.<br><br><br>Tuan/Puan,<br>"
                + "<br>Kami telah mengenal pasti satu task " + mftEmailRequest.getI_task_id()
                + " yang memerlukan klarifikasi lanjut. Sila semak dan kemukakan query yang berkait dengan tugasan ini."
                + "<br><br><a href='" + redirect + "'>Klik di sini</a> untuk tujuan pemprosesan tugasan."
                + "<br><br>***MOHON ABAIKAN EMEL INI JIKA QUERY ANDA TELAH DILULUSKAN ***"
                + "<br><br>Terima kasih kerana menggunakan perkhidmatan kami."
                + "<br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
                + "DO NOT REPLY DIRECTLY TO THIS EMAIL] <br>";
        
            }
            else if(mftEmailRequest.getI_status().equals("RJ-RHOD") || mftEmailRequest.getI_status().equals("RJ-FA")
            || mftEmailRequest.getI_status().equals("RJ-FHOD")){

                subject = "MFT TASK REJECTED - TASK ID " + mftEmailRequest.getI_task_id();

                body = "Fee Detail ID: " + (tempFeeDetailId != null ? tempFeeDetailId : "-")
                + "<br>Task ID: " + mftEmailRequest.getI_task_id()
                + "<br>Task Status: " + statusDesription
                + "<br><br>Dear Sir/Madam,<br><br>"
                + "This is to inform you that your MFT Task has been REJECTED."
                + "<br><br>Please refer to the feedback provided in the system for detailed information regarding the rejection."
                + "<br><br><a href='" + redirect + "'>Click here</a> to begin processing task."
                + "<br><br>Thank you for using our services.<br><br><br>Tuan/Puan,<br>"
                + "<br>Ini adalah untuk memaklumkan bahawa MFT Task anda telah DITOLAK."
                + "<br><br>Sila rujuk maklum balas yang diberikan dalam sistem untuk butiran terperinci berkaitan penolakan ini."
                + "<br><br><a href='" + redirect + "'>Klik di sini</a> untuk tujuan pemprosesan tugasan."
                + "<br><br>Terima kasih kerana menggunakan perkhidmatan kami."
                + "<br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
                + "DO NOT REPLY DIRECTLY TO THIS EMAIL] <br>";
        

            }
            else if(mftEmailRequest.getI_status().equals("APV")){

                subject = "MFT TASK APPROVED - TASK ID " + mftEmailRequest.getI_task_id();

                body = "Fee Detail ID: " + (tempFeeDetailId != null ? tempFeeDetailId : "-")
                + "<br>Task ID: " + mftEmailRequest.getI_task_id()
                + "<br>Task Status: " + statusDesription
                + "<br><br>Dear Sir/Madam,<br><br>This is to inform you that your MFT Task has been APPROVED."
                + "<br><br>You may proceed with the next steps according to the established plan."
                + "<br><br><a href='" + redirect + "'>Click here</a> to begin processing task."
                + "<br><br>Thank you for using our services.<br><br><br>Tuan/Puan,"
                + "<br><br>Ini adalah untuk memaklumkan bahawa MFT Task anda telah DILULUSKAN."
                + "<br><br>Anda boleh meneruskan langkah seterusnya seperti yang ditetapkan."
                + "<br><br><a href='" + redirect + "'>Klik di sini</a> untuk tujuan pemprosesan tugasan."
                + "<br><br>Terima kasih kerana menggunakan perkhidmatan kami."
                + "<br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
                + "DO NOT REPLY DIRECTLY TO THIS EMAIL] \n";
        
            }
            else {//cancel
                subject = "MFT TASK CANCEL - TASK ID " + mftEmailRequest.getI_task_id();

                body = "Fee Detail ID: " + (tempFeeDetailId != null ? tempFeeDetailId : "-")
                + "<br>Task ID: " + mftEmailRequest.getI_task_id()
                + "<br>Task Status: " + statusDesription
                + "<br><br>Dear Sir/Madam,<br><br>This is to inform you that your MFT Task has been CANCELED."
                + "<br><br>For more details, please refer to the information available in the system regarding the cancellation."
                + "<br><br><a href='" + redirect + "'>Click here</a> to begin processing task."
                + "<br><br>Thank you for using our services.<br><br><br>Tuan/Puan,"
                + "<br><br>Ini adalah untuk memaklumkan bahawa MFT Task anda telah DIBATALKAN."
                + "<br><br>Untuk maklumat lanjut, sila rujuk informasi yang tersedia dalam sistem berkenaan "
                + "pembatalan ini."
                + "<br><br><a href='" + redirect + "'>Klik di sini</a> untuk tujuan pemprosesan tugasan."
                + "<br><br>Terima kasih kerana menggunakan perkhidmatan kami."
                + "<br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
                + "DO NOT REPLY DIRECTLY TO THIS EMAIL] \n";

            }
            
          //  emailService.sendMailHTML(rmsUser.getEmail(), mftEmailRequest.getI_cc(), mftEmailRequest.getI_bcc(), subject, body);
              Email email = new Email("Notification",rmsUser.getEmail(), mftEmailRequest.getI_cc(), mftEmailRequest.getI_bcc(), subject, body, null);
              emailService.saveEmailDets(email);
            // emailService.saveEmailDets((new Email("Notification"
            //         ,rmsUser.getEmail(), mftEmailRequest.getI_cc(), mftEmailRequest.getI_bcc(), subject, body)));
 
        }

}