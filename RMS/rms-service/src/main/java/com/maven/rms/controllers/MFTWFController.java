package com.maven.rms.controllers;
import java.math.BigInteger;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.weaver.ast.Not;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.utils.APIResponse;

import lombok.extern.slf4j.Slf4j;

import com.maven.rms.models.MFTWF;
import com.maven.rms.models.MFTWFRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.MFTWFService;
import com.maven.rms.services.NotificationService;
import com.maven.rms.utils.ControllersEnum;



@RestController
@RequestMapping("/api/mftwf/v1")
@Slf4j
public class MFTWFController {

    // private static final Logger logger = LoggerFactory.getLogger(MFTWFController.class);
    
    @Autowired
    private AuthService authService;

    @Autowired
    private MFTWFService mftwfService;

    @Autowired
    private NotificationService notificationSvc;

//@Secured("ROLE_USER")
    @PostMapping(value = "/updatemasterfeetableworkflow_status")
    public ResponseEntity<ApiResponse<Integer>> sp_updmftwf_status(HttpServletRequest request, @RequestBody MFTWFRequest mftwfRequest) {
        
        Integer result = 0;

   
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

        //    result = spService.sp_updmftwf_status(
        //     mftwfRequest.getI_wf_id(),
        //     mftwfRequest.getI_assign_to(),
        //     mftwfRequest.getI_status(),
        //     mftwfRequest.getI_remark(),
        //     authService.getLoginUserName()
        //    );

        mftwfRequest.setI_modified_by(authService.getLoginUserName());

        result = mftwfService.sp_updmftwf_status(mftwfRequest);

           if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        notificationSvc.sendNotificationUpdate();
        // System.out.println("Notification sent 1");
        // System.out.println("Notification sent 2");
        // System.out.println("Notification sent 3");
        return APIResponse.SuccessResponse(result);

    }

// @Secured("ROLE_USER")
    @PostMapping(value = "/getmasterfeetableworkflow")
    public ResponseEntity<ApiResponse<List<MFTWF>>> sp_getmftwf(HttpServletRequest request, @RequestBody MFTWFRequest mftwfRequest) {

       List< MFTWF> result = Collections.emptyList();

           if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

        //    result = spService.sp_getmftwf(
        //    mftwfRequest.getI_page(),
        //    mftwfRequest.getI_size(),
        //    mftwfRequest.getI_wf_id(),
        //    mftwfRequest.getI_fee_detail_pk(),
        //    mftwfRequest.getI_fee_detail_id(),
        //    mftwfRequest.getI_assign_to(),
        //    mftwfRequest.getI_status(),
        //    mftwfRequest.getI_created_by(),
        //    mftwfRequest.getI_modified_by(),
        //    mftwfRequest.getI_modified_by_nm(),
        //    mftwfRequest.getI_dt_modified_fr(),
        //    mftwfRequest.getI_dt_modified_to(),
        //    mftwfRequest.getI_dt_created_fr(),
        //    mftwfRequest.getI_dt_created_to(),
        //    mftwfRequest.getI_dt_effective_fr(),
        //    mftwfRequest.getI_dt_effective_to(),
        //    mftwfRequest.getI_ss_cd(),
        //    mftwfRequest.getI_wf_is_in_prg()
        //    );

        result = mftwfService.sp_getmftwf(mftwfRequest);

          if (result.isEmpty()) {
                    return APIResponse.NoDataFound(ControllersEnum.MFT_WF_CONTROLLER);
                }

                // NotificationMessage notificationMessage = new NotificationMessage(myTaskCount, createdTaskCount);
                // messagingTemplate.convertAndSend("/topic/notifications", notificationMessage);
                // notificationHandler.sendNotificationUpdate(myTaskCount, createdTaskCount);
                return APIResponse.SuccessResponse(result);

    }


//   @Secured("ROLE_USER")
    @PostMapping(value = "/insertmasterfeetableworkflow")
    public ResponseEntity<ApiResponse<BigInteger>> sp_insmftwf(HttpServletRequest request, @RequestBody MFTWFRequest mftwfRequest) {
        
        BigInteger result = BigInteger.ZERO;

       
              if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            mftwfRequest.setI_created_by(authService.getLoginUserName());
            mftwfRequest.setI_modified_by(authService.getLoginUserName());

            // result=spService.sp_insmftwf(
            //     mftwfRequest.getI_fee_detail_pk(),
            //     mftwfRequest.getI_fee_detail_id(),
            //     mftwfRequest.getI_fee_grp_id(),
            //     mftwfRequest.getI_fee_detail_nm_e(),
            //     mftwfRequest.getI_fee_detail_nm_b(),
            //     mftwfRequest.getI_fee_amt(),
            //     mftwfRequest.getI_promo_startdt(),
            //     mftwfRequest.getI_promo_enddt(),
            //     mftwfRequest.getI_promo_fee(),
            //     mftwfRequest.getI_tax_cd_id(),
            //     mftwfRequest.getI_allow_otc(),
            //     mftwfRequest.getI_ll_parent_id(),
            //     mftwfRequest.getI_ll_start_day(),
            //     mftwfRequest.getI_ll_start_mth(),
            //     mftwfRequest.getI_ll_end_day(),
            //     mftwfRequest.getI_ll_end_mth(),
            //     mftwfRequest.getI_ledger_cd(),
            //     mftwfRequest.getI_ss_cd(),
            //     authService.getLoginUserName(), //created_by
            //     authService.getLoginUserName(), //modified_by
            //     mftwfRequest.getI_status(),    //status
            //     mftwfRequest.getI_effective_date(),
            //     mftwfRequest.getI_remark(),
            //     mftwfRequest.getI_assign_to(),
            //     mftwfRequest.getI_action(),
            //     mftwfRequest.getI_r_fee_det_nm(),
            //     mftwfRequest.getI_r_fee_amt(),
            //     mftwfRequest.getI_r_ss_cd(),
            //     mftwfRequest.getI_r_promo_startdt(),
            //     mftwfRequest.getI_r_promo_enddt(),
            //     mftwfRequest.getI_r_ll_required(),
            //     mftwfRequest.getI_r_add_notes(),
            //     mftwfRequest.getI_mft_status(),
            //     mftwfRequest.getI_r_promo_fee()
            // );

            
            result=mftwfService.sp_insmftwf(mftwfRequest);

            if (result.compareTo(BigInteger.ZERO) <= 0) {
                return APIResponse.InternalServerError();
            }

               // Execute the notification update asynchronously after returning the response
                // new Thread(() -> {
                //     try {
                //         notificationSvc.sendNotificationUpdate();
                //     } catch (Exception e) {
                //         logger.error("Failed to send notification update", e);
                //     }
                // }).start();
             notificationSvc.sendNotificationUpdate();

             return APIResponse.SuccessResponse(result);

    }


    //   @Secured("ROLE_USER")
    @PostMapping(value = "/updatemasterfeetableworkflow")
    public ResponseEntity<ApiResponse<Integer>> sp_updmftwf(HttpServletRequest request, @RequestBody MFTWFRequest mftwfRequest) {
        
              if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            mftwfRequest.setI_modified_by(authService.getLoginUserName());

        //  Integer  result=spService.sp_updmftwf(
        //         mftwfRequest.getI_wf_id(),
        //         mftwfRequest.getI_fee_detail_pk(),
        //         mftwfRequest.getI_fee_detail_id(),
        //         mftwfRequest.getI_fee_grp_id(),
        //         mftwfRequest.getI_fee_detail_nm_e(),
        //         mftwfRequest.getI_fee_detail_nm_b(),
        //         mftwfRequest.getI_fee_amt(),
        //         mftwfRequest.getI_promo_startdt(),
        //         mftwfRequest.getI_promo_enddt(),
        //         mftwfRequest.getI_promo_fee(),
        //         mftwfRequest.getI_tax_cd_id(),
        //         mftwfRequest.getI_allow_otc(),
        //         mftwfRequest.getI_ll_parent_id(),
        //         mftwfRequest.getI_ll_start_day(),
        //         mftwfRequest.getI_ll_end_day(),
        //         mftwfRequest.getI_ll_start_mth(), 
        //         mftwfRequest.getI_ll_end_mth(),
        //         mftwfRequest.getI_ledger_cd(),
        //         mftwfRequest.getI_ss_cd(),
        //         mftwfRequest.getI_effective_date(),
        //         authService.getLoginUserName(), //modified_by
        //         mftwfRequest.getI_status(),    
        //         mftwfRequest.getI_assign_to(),
        //         mftwfRequest.getI_remark(),
        //         mftwfRequest.getI_action(),
        //         mftwfRequest.getI_r_fee_det_nm(),
        //         mftwfRequest.getI_r_fee_amt(),
        //         mftwfRequest.getI_r_ss_cd(),
        //         mftwfRequest.getI_r_promo_startdt(),
        //         mftwfRequest.getI_r_promo_enddt(),
        //         mftwfRequest.getI_r_ll_required(),
        //         mftwfRequest.getI_r_add_notes(),
        //         mftwfRequest.getI_mft_status(),
        //         mftwfRequest.getI_r_promo_fee()
        //     );

        Integer  result=mftwfService.sp_updmftwf(mftwfRequest);


            if (result == null) {
                return APIResponse.InternalServerError();
            }

            notificationSvc.sendNotificationUpdate();

            return APIResponse.SuccessResponse(result);

    }

    //   @Secured("ROLE_USER")
    @PostMapping(value = "/getmytaskactivetaskcount")
    public ResponseEntity<ApiResponse<Integer>> sp_getmytaskactivetaskcount(HttpServletRequest request, @RequestBody MFTWFRequest mftwfRequest) {
        Integer result = -1;
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
           result = mftwfService.sp_getmytaskactivetaskcount(
			mftwfRequest	
		   );
           if (result < 0) {
            return APIResponse.InternalServerError();
        }
        return APIResponse.SuccessResponse(result);
    }


   @PostMapping(value = "/getcreatedtaskactivetaskcount")
   public ResponseEntity<ApiResponse<Integer>> sp_getcreatedtaskactivetaskcount(HttpServletRequest request, @RequestBody MFTWFRequest mftwfRequest) {
       Integer result = -1;
           if (!authService.isAuthenticated(request)) {
               return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
           }
          result = mftwfService.sp_getcreatedtaskactivetaskcount(mftwfRequest);
          if (result < 0) {
           return APIResponse.InternalServerError();
       }
       return APIResponse.SuccessResponse(result);
   }


      // @Secured("ROLE_USER")
      @PostMapping(value = "/checkmasterfeetableworkflowexist")
      public ResponseEntity<ApiResponse<List<MFTWF>>> sp_checkmftwfexist(HttpServletRequest request, @RequestBody MFTWFRequest mftwfRequest) {
  
      List< MFTWF> result = Collections.emptyList();
  
          if (!authService.isAuthenticated(request)) {
                  return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
              }
  
      
          result = mftwfService.sp_checkmftwfexist(mftwfRequest);
  
          if (result.isEmpty()) {
                      return APIResponse.NoDataFound(ControllersEnum.MFT_WF_CONTROLLER);
                  }
  
          return APIResponse.SuccessResponse(result);
  
  }
}





    // // Inner class to represent the notification message
    // public static class NotificationMessage {
    //     private int myTaskCount;
    //     private int createdTaskCount;

    //     public NotificationMessage(int myTaskCount, int createdTaskCount) {
    //         this.myTaskCount = myTaskCount;
    //         this.createdTaskCount = createdTaskCount;
    //     }

    //     public int getMyTaskCount() {
    //         return myTaskCount;
    //     }

    //     public void setMyTaskCount(int myTaskCount) {
    //         this.myTaskCount = myTaskCount;
    //     }

    //     public int getCreatedTaskCount() {
    //         return createdTaskCount;
    //     }

    //     public void setCreatedTaskCount(int createdTaskCount) {
    //         this.createdTaskCount = createdTaskCount;
    //     }
    // }





