package com.maven.rms.services;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maven.rms.controllers.OnlinePaymentController;
import com.maven.rms.models.Email;
import com.maven.rms.models.MFTWF;
import com.maven.rms.models.MFTWFHistory;
import com.maven.rms.models.MFTWFHistoryRequest;
import com.maven.rms.models.MFTWFRequest;
import com.maven.rms.models.RMSUser;
import com.maven.rms.models.RMSUserRequest;
import com.maven.rms.utils.CacheManager;

@Service
@Slf4j
public class MFTEffectiveEmailService {
    
 //private static final Logger logger = LoggerFactory.getLogger(MFTEffectiveEmailService.class);

   
    @Autowired
    private MFTWFService mftwfService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRoleService userRoleService;

    
    @Value("${rms.application.backPortalURL}")
    private String url;

    // public void insMFTEffectiveEmail(BigInteger wfId){
    public void insMFTEffectiveEmail(MFTWFRequest mftwfRequest){

     

            String subject = "";
            String body="";
            String redirect="";
            String editMode = "";
            String showRequesterTable ="";
            String statusForSP = "";
            String assignTo = "";
            String assignToName="";
            Integer feeDetailPK = 0;
            String feeDetailId = "";
            String taskId = "";
            String status = "";
            String action = "";
            String createdBy = "";
            String createdByNm = "";

            // List<MFTWF> mftwfList = spService.sp_getmftwf(1,1,wfId,null,null,null,null,null,null,null,null,null,null,
            //                         null,null,null,null,null);

            mftwfRequest.setI_page(1);
            mftwfRequest.setI_size(1);
            mftwfRequest.setI_wf_id(mftwfRequest.getI_wf_id());
            mftwfRequest.setI_fee_detail_pk(null);
            mftwfRequest.setI_fee_detail_id(null);
            mftwfRequest.setI_assign_to(null);
            mftwfRequest.setI_status(null);
            mftwfRequest.setI_created_by(null);
            mftwfRequest.setI_modified_by(null);
            mftwfRequest.setI_modified_by_nm(null);
            mftwfRequest.setI_dt_modified_fr(null);
            mftwfRequest.setI_dt_modified_to(null);
            mftwfRequest.setI_dt_created_fr(null);
            mftwfRequest.setI_dt_created_to(null);
            mftwfRequest.setI_dt_effective_fr(null);
            mftwfRequest.setI_dt_effective_to(null);
            mftwfRequest.setI_ss_cd(null);
            mftwfRequest.setI_wf_is_in_prg(null);

            List<MFTWF> mftwfList = mftwfService.sp_getmftwf(mftwfRequest);

            if (!mftwfList.isEmpty()) {
                                        
                feeDetailPK = mftwfList.get(0).getFee_detail_pk();
                feeDetailId = mftwfList.get(0).getFee_detail_id();
                taskId = mftwfList.get(0).getTask_id();
                status = mftwfList.get(0).getStatus();
                action = mftwfList.get(0).getAction();
                createdBy = mftwfList.get(0).getCreated_by();
                createdByNm = mftwfList.get(0).getCreated_by_nm();
            }

    
            if(action != null){
                if(action.equals("Request Add")  || action.equals("Request Add-FIN")){
                    editMode="false";
                }
                else{
                    editMode="true";
                }
    
                if(action.equals("Request Add-FIN")  || action.equals("Request Edit-FIN")){
                    showRequesterTable="false";
                    statusForSP = "Q-FA";
                }
                else{
                    showRequesterTable="true";
                    statusForSP = "Q-R";
                }
            }
        
            redirect = url + "/created-task-details?"
            + "wf_id=" + mftwfRequest.getI_wf_id()  //original is wfId
            + "&task_id=" + taskId
            + "&fee_detail_pk=" + feeDetailPK
            + "&status__From_Assigned=" + status
            + "&edit_Mode=" + editMode
            + "&show_requester_table=" + showRequesterTable;
    
            // System.out.println("redirect is "+redirect);
            log.debug("redirect is "+redirect);
     
            MFTWFHistoryRequest mftwfHistoryRequest = new MFTWFHistoryRequest();
            mftwfHistoryRequest.setI_task_id(taskId);
            mftwfHistoryRequest.setI_status(statusForSP);
            // List<MFTWFHistory> mftwfHistory = spService.sp_getwfh_ast(taskId, statusForSP);
            List<MFTWFHistory> mftwfHistory = mftwfService.sp_getwfh_ast(mftwfHistoryRequest);
            
            //to get user for send to start
            RMSUser rmsUser = new RMSUser();
            RMSUserRequest rmsUserRequest = new RMSUserRequest();


                if (!mftwfHistory.isEmpty()) {

                    // if(mftwfHistory.get(0).getAssign_to() != null){ //mft work flow has been reassigned
                    //     // If the list is not empty, get the first element
                    //     assignTo = mftwfHistory.get(0).getAssign_to();
                    //     assignToName = mftwfHistory.get(0).getAssign_to_nm();

                    //     rmsUser= spService.sp_getuserdetail(assignTo);
                    // }
                    // else{ //mft work flow has not been reassigned
                    //     assignToName = createdByNm;
                    //     rmsUser= spService.sp_getuserdetail(createdBy);
                    // }

                    if(mftwfHistory.get(0).getAssign_to() != null){ //mft work flow has been reassigned
                        // If the list is not empty, get the first element
                            assignTo = mftwfHistory.get(0).getAssign_to();
                            assignToName = mftwfHistory.get(0).getAssign_to_nm();
                            rmsUserRequest.setI_ssm4uuserrefno(assignTo);

                            rmsUser= userRoleService.sp_getuserdetail(rmsUserRequest);
                    }
                    else{ //mft work flow has not been reassigned
                            assignToName = createdByNm;
                            rmsUserRequest.setI_ssm4uuserrefno(createdBy);

                            rmsUser= userRoleService.sp_getuserdetail(rmsUserRequest);
                    }
                }
               // else{
               //     throw new IllegalArgumentException("Failed to read data from MFT history!");
               // }
             
     
            subject = "MFT EFFECTIVE - TASK ID " + taskId;
    
            body = "Fee Detail ID: " + feeDetailId
            + "<br>Task ID: " + taskId
            + "<br>Task Status: Effective" 
            + "<br><br>Dear Sir/Madam "+ assignToName +",<br><br>"
            + "We are delighted to inform you that task  " + taskId + " is now effective. "
            + "You may proceed with the next steps as planned."
            + "<br><br><a href='" + redirect + "'>Click here</a> to begin processing task."
            + "<br><br>Thank you for using our services.<br><br><br>Tuan/Puan "+ assignToName +",<br>"
            + "<br>Kami berbesar hati ingin memaklumkan bahawa task  " + taskId
            + " kini telah berkuat kuasa. . Tuan/Puan boleh meneruskan dengan langkah seterusnya seperti yang ditetapkan."
            + "<br><br><a href='" + redirect + "'>Klik di sini</a> untuk tujuan pemprosesan tugasan."
            + "<br><br>Terima kasih kerana menggunakan perkhidmatan kami."
            + "<br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
            + "DO NOT REPLY DIRECTLY TO THIS EMAIL] <br>";

           //    emailService.sendMailHTML(rmsUser.getEmail(), "", "", subject, body);

           emailService.saveEmailDets((new Email("Notification"
           ,rmsUser.getEmail(), "", "", subject, body, null)));

        }

}
