package com.maven.rms.controllers;

import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import com.maven.rms.models.MFTWFHistory;
import com.maven.rms.models.MFTWFHistoryRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.MFTWFService;
import com.maven.rms.utils.APIResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.maven.rms.utils.ControllersEnum;


@RestController
@RequestMapping("/api/mftwfh/v1")
@Slf4j
public class MFTWFHistoryController {

     //private static final Logger logger = LoggerFactory.getLogger(MFTWFHistoryController.class);
    

    @Autowired
    private AuthService authService;

    @Autowired
    private MFTWFService mftwfService;


// @Secured("ROLE_USER")
    @PostMapping(value = "/getworkflowhistory_ast")
    public ResponseEntity<ApiResponse<List<MFTWFHistory>>> sp_getwfh_ast(HttpServletRequest request, @RequestBody MFTWFHistoryRequest mftwfHistoryRequest) {
        
       List< MFTWFHistory> result = Collections.emptyList();

     
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            //  result = spService.sp_getwfh_ast(
            //     mftwfHistoryRequest.getI_task_id(),
            //     mftwfHistoryRequest.getI_status()    
            //  );

             result = mftwfService.sp_getwfh_ast(mftwfHistoryRequest);

            if (result.isEmpty()) {
                    return APIResponse.NoDataFound(ControllersEnum.MFT_WF_HISTORY_CONTROLLER);
                }

            return APIResponse.SuccessResponse(result);

    }

 // @Secured("ROLE_USER")
 @PostMapping(value = "/getmasterfeetableworkflowhistory")
 public ResponseEntity<ApiResponse<List<MFTWFHistory>>> sp_getmftwfhis(HttpServletRequest request, @RequestBody MFTWFHistoryRequest mftwfHistoryRequest) {
     
    List< MFTWFHistory> result = Collections.emptyList();

  
         if (!authService.isAuthenticated(request)) {
             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
         }

        //   result = spService.sp_getmftwfhis(
        //      mftwfHistoryRequest.getI_page(),
        //      mftwfHistoryRequest.getI_size(),
        //      mftwfHistoryRequest.getI_wf_id(),
        //      mftwfHistoryRequest.getI_status()
        //   );

          result = mftwfService.sp_getmftwfhis(mftwfHistoryRequest);

         if (result.isEmpty()) {
                 return APIResponse.NoDataFound(ControllersEnum.MFT_WF_HISTORY_CONTROLLER);
             }

         return APIResponse.SuccessResponse(result);

 }

 // @Secured("ROLE_USER")
    @PostMapping(value = "/getworkflowhistory_status")
    public ResponseEntity<ApiResponse<List<MFTWFHistory>>> sp_getwfh_status(HttpServletRequest request, @RequestBody MFTWFHistoryRequest mftwfHistoryRequest) {
        
       List< MFTWFHistory> result = Collections.emptyList();


            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            //  result = spService.sp_getwfh_status(
            //     mftwfHistoryRequest.getI_task_id(),
            //     mftwfHistoryRequest.getI_status()    
            //  );

             result = mftwfService.sp_getwfh_status(mftwfHistoryRequest);

            if (result.isEmpty()) {
                    return APIResponse.NoDataFound(ControllersEnum.MFT_WF_HISTORY_CONTROLLER);
                }

            return APIResponse.SuccessResponse(result);

    }




}
