package com.maven.rms.controllers;

import java.math.BigInteger;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.models.FMS;
import com.maven.rms.models.FMSARIImmediateRequest;
import com.maven.rms.models.FMSARIModel;
import com.maven.rms.models.FMSRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.FMSARIService;
import com.maven.rms.services.FMSARRService;
import com.maven.rms.services.FMSARVService;
import com.maven.rms.services.FMSCRMemoService;
import com.maven.rms.services.FMSDRMemoService;
import com.maven.rms.services.FMSDeferredIncomeService;
import com.maven.rms.services.FMSJournalService;
import com.maven.rms.services.FMSRICPService;
import com.maven.rms.services.FMSRIPLService;
import com.maven.rms.services.FMSService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.SystemStatus;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/fms/v1")
@Slf4j
public class FMSController {

    // private static final Logger logger =
    // LoggerFactory.getLogger(UpdateRICPWriteOff.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private FMSService spService;

    @Autowired
    private FMSDeferredIncomeService fmsDiService;

    @Autowired
    private FMSRICPService fmsRicpService;

    @Autowired
    private FMSRIPLService fmsRiplService;

    @Autowired
    private FMSJournalService fmsJournalService;

    @Autowired
    private FMSARIService fmsARIService;

    @Autowired
    private FMSARRService fmsARRService;

    @Autowired
    private FMSCRMemoService fmsCRMemoService;

    @Autowired
    private FMSDRMemoService fmsDRMemoService;

    @Autowired
    private FMSARVService fmsARVService;

    @GetMapping(value = "/testJsonString")
    public ResponseEntity<ApiResponse<String>> fmsCRMemoJson(HttpServletRequest request) {
        String stringBody = fmsARIService.generateStringBody(fmsARIService.sp_getfmsaribybilchildid(316));
        /*
         * try {
         * FMSCRMemoJson json = fmsCRMemoService.generateStringBody(fmsCRMemoService.
         * sp_getfmscrmemobyarifmsrefno("b4b67255-fe83-4914-8981-g")
         * , true, true, new FMSCRMemoJson());
         * ObjectMapper mapper = new ObjectMapper();
         * stringBody = mapper.writeValueAsString(json);
         * } catch (JsonProcessingException e) {
         * e.printStackTrace();
         * }
         */

        return APIResponse.SuccessResponse(stringBody);
    }

    // @PostMapping("/getfms")
    // public ResponseEntity<ApiResponse<List<FMS>>> getFMS(HttpServletRequest
    // request,
    // @RequestBody FMSRequest fmsRequest) {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }
    // try {
    // List<FMS> result = spService.sp_getfms(
    // fmsRequest
    // );

    // if (result.isEmpty()) {
    // return APIResponse.NoDataFound(ControllersEnum.FMS_CONTROLLER);
    // }
    // return APIResponse.SuccessResponse(result);
    // } catch (DataAccessException e) {

    // return APIResponse.InternalServerError();
    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();
    // } catch (Exception e) {
    // return APIResponse.InternalServerError();
    // }
    // }

    @PostMapping("/getfms")
    public ResponseEntity<ApiResponse<List<FMS>>> getFMS(HttpServletRequest request,
            @RequestBody FMSRequest fmsRequest) {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<FMS> result = spService.sp_getfms(fmsRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.FMS_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
    }

    // @PostMapping(value = "/addfms")
    // public ResponseEntity<ApiResponse<Integer>> addFms(
    // HttpServletRequest request,
    // @RequestBody FMSRequest insertRequest) {

    // try {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // Integer result = spService.sp_insfms(
    // insertRequest,
    // authService.getLoginUserName(),
    // SystemStatus.Inactive.getMessage(),
    // 0);

    // if (result <= 0) {
    // return APIResponse.InternalServerError();
    // }

    // return APIResponse.SuccessResponse(result);
    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();
    // } catch (Exception e) {
    // return APIResponse.InternalServerError();
    // }
    // }

    @PostMapping(value = "/addfms")
    public ResponseEntity<ApiResponse<Integer>> addFms(
            HttpServletRequest request,
            @RequestBody FMSRequest insertRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_insfms(
                insertRequest,
                authService.getLoginUserName(),
                SystemStatus.Inactive.getMessage(),
                0);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    // @PostMapping(value = "/updatefms")
    // public ResponseEntity<ApiResponse<Integer>> updateFms(
    // HttpServletRequest request,
    // @RequestBody FMSRequest updateRequest) {

    // try {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // Integer result = spService.sp_updfms(
    // updateRequest,
    // authService.getLoginUserName()
    // );

    // if (result <= 0) {
    // return APIResponse.InternalServerError();
    // }

    // return APIResponse.SuccessResponse(result);
    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();
    // } catch (Exception e) {
    // return APIResponse.InternalServerError();
    // }
    // }

    @PostMapping(value = "/updatefms")
    public ResponseEntity<ApiResponse<Integer>> updateFms(
            HttpServletRequest request,
            @RequestBody FMSRequest updateRequest) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_updfms(
                updateRequest,
                authService.getLoginUserName());

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    // @PostMapping(value = "/checkfmsexist")
    // public ResponseEntity<ApiResponse<Integer>> checkFmsExist(
    // HttpServletRequest request,
    // @RequestBody FMSRequest fmsRequest) {

    // Integer fmsID = fmsRequest.getI_fms_id();

    // try {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // Integer result = spService.sp_checkfmsbyid(fmsRequest);

    // if (result > 0) { // got module using it
    // return APIResponse.RecordInUsed(result);
    // }

    // return APIResponse.SuccessResponse(result);
    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();
    // } catch (Exception e) {
    // return APIResponse.InternalServerError();
    // }
    // }

    @PostMapping(value = "/checkfmsexist")
    public ResponseEntity<ApiResponse<Integer>> checkFmsExist(
            HttpServletRequest request,
            @RequestBody FMSRequest fmsRequest) {

        Integer fmsID = fmsRequest.getI_fms_id();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_checkfmsbyid(fmsRequest);

        if (result > 0) { // got module using it
            return APIResponse.RecordInUsed(result);
        }

        return APIResponse.SuccessResponse(result);
    }

    // @PostMapping(value = "/fmsactivated")
    // public ResponseEntity<ApiResponse<Integer>> fmsActivated(
    // HttpServletRequest request,
    // @RequestBody FMSRequest fmsRequest) {

    // //Integer fmsID = fmsRequest.getI_fms_id();

    // try {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // Integer result = spService.sp_updfms_activation(fmsRequest,
    // authService.getLoginUserName());

    // if (result <= 0) {
    // return APIResponse.InternalServerError();
    // }

    // return APIResponse.SuccessResponse(result);
    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();
    // } catch (Exception e) {
    // return APIResponse.InternalServerError();
    // }
    // }

    @PostMapping(value = "/fmsactivated")
    public ResponseEntity<ApiResponse<Integer>> fmsActivated(
            HttpServletRequest request,
            @RequestBody FMSRequest fmsRequest) {

        // Integer fmsID = fmsRequest.getI_fms_id();

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_updfms_activation(fmsRequest, authService.getLoginUserName());

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);
    }

    // @PostMapping(value = "/fmsdi")
    // public ResponseEntity<ApiResponse<Integer>> fmsDi(
    // HttpServletRequest request) {

    // try {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // Integer result = fmsDiService.sp_fmsDiSch();

    // if (result < 0) {
    // return APIResponse.InternalServerError();
    // } else if (result == 0) {
    // return APIResponse.NoDataFound();
    // } else {
    // return APIResponse.SuccessResponse(result);
    // }
    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();
    // } catch (Exception e) {
    // return APIResponse.InternalServerError();
    // }
    // }

    @PostMapping(value = "/fmsdi")
    public ResponseEntity<ApiResponse<Integer>> fmsDi(
            HttpServletRequest request) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = fmsDiService.sp_fmsDiSch();

        if (result < 0) {
            return APIResponse.InternalServerError();
        } else if (result == 0) {
            return APIResponse.NoDataFound();
        } else {
            return APIResponse.SuccessResponse(result);
        }
    }

    // @PostMapping(value = "/fmsricp")
    // public ResponseEntity<ApiResponse<Integer>> fmsRicp(
    // HttpServletRequest request,
    // @RequestBody FMSRequest fmsRicp) {

    // try {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // Integer result = fmsRicpService.sp_fmsricpSch();

    // if (result <= 0) {
    // return APIResponse.InternalServerError();
    // }
    // else if(result == 0){
    // return APIResponse.NoDataFound();
    // }
    // else{
    // return APIResponse.SuccessResponse(result);
    // }
    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();
    // } catch (Exception e) {
    // return APIResponse.InternalServerError();
    // }
    // }

    // @PostMapping(value = "/fmsricp")
    // public ResponseEntity<ApiResponse<Integer>> fmsRicp(
    // HttpServletRequest request) {

    // try {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // Integer result = fmsRicpService.sp_fmsricpSch();

    // if (result <= 0) {
    // return APIResponse.InternalServerError();
    // } else if (result == 0) {
    // return APIResponse.NoDataFound();
    // } else {
    // return APIResponse.SuccessResponse(result);
    // }
    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();
    // } catch (Exception e) {
    // return APIResponse.InternalServerError();
    // }
    // }

    @PostMapping(value = "/fmsricp")
    public ResponseEntity<ApiResponse<Integer>> fmsRicp(
            HttpServletRequest request) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = fmsRicpService.sp_fmsricpSch();

        if (result <= 0) {
            return APIResponse.InternalServerError();
        } else if (result == 0) {
            return APIResponse.NoDataFound();
        } else {
            return APIResponse.SuccessResponse(result);
        }
    }

    // @PostMapping(value = "/fmsripl")
    // public ResponseEntity<ApiResponse<Integer>> fmsRipl(
    // HttpServletRequest request,
    // @RequestBody FMSRequest fmsRipl) {

    // try {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // Integer result = fmsRiplService.sp_fmsriplSch();

    // if (result <= 0) {
    // return APIResponse.InternalServerError();
    // }
    // else if(result == 0){
    // return APIResponse.NoDataFound();
    // }
    // else{
    // return APIResponse.SuccessResponse(result);
    // }
    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();
    // } catch (Exception e) {
    // return APIResponse.InternalServerError();
    // }
    // }

    // @PostMapping(value = "/fmsripl")
    // public ResponseEntity<ApiResponse<Integer>> fmsRipl(HttpServletRequest
    // request) {

    // try {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // Integer result = fmsRiplService.sp_fmsriplSch();

    // if (result <= 0) {
    // return APIResponse.InternalServerError();
    // } else if (result == 0) {
    // return APIResponse.NoDataFound();
    // } else {
    // return APIResponse.SuccessResponse(result);
    // }
    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();
    // } catch (Exception e) {
    // return APIResponse.InternalServerError();
    // }
    // }

    @PostMapping(value = "/fmsripl")
    public ResponseEntity<ApiResponse<Integer>> fmsRipl(HttpServletRequest request) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = fmsRiplService.sp_fmsriplSch();

        if (result <= 0) {
            return APIResponse.InternalServerError();
        } else if (result == 0) {
            return APIResponse.NoDataFound();
        } else {
            return APIResponse.SuccessResponse(result);
        }
    }

    // @PostMapping(value = "/fmsJournal")
    // public ResponseEntity<ApiResponse<Integer>> fmsJournal(
    // HttpServletRequest request) {

    // try {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // Integer result = fmsJournalService.fmsJnSch();

    // if (result <= 0) {
    // return APIResponse.InternalServerError();
    // } else if (result == 0) {
    // return APIResponse.NoDataFound();
    // } else {
    // return APIResponse.SuccessResponse(result);
    // }
    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();
    // } catch (Exception e) {
    // return APIResponse.InternalServerError();
    // }
    // }

    @PostMapping(value = "/fmsJournal")
    public ResponseEntity<ApiResponse<Integer>> fmsJournal(
            HttpServletRequest request) {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = fmsJournalService.fmsJnSch();

        if (result <= 0) {
            return APIResponse.InternalServerError();
        } else if (result == 0) {
            return APIResponse.NoDataFound();
        } else {
            return APIResponse.SuccessResponse(result);
        }
    }

    // @PostMapping("/fms_activation")
    // public ResponseEntity<ApiResponse<Integer>> fmsActivation(HttpServletRequest
    // request,
    // @RequestBody FMSRequest fmsRequest) {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }
    // try {
    // Integer result = spService.sp_updfms_activation(
    // fmsRequest.getI_fms_cd(),
    // authService.getLoginUserName());

    // if (result == null) {
    // return APIResponse.NoDataFound();
    // }
    // return APIResponse.SuccessResponse(result);
    // } catch (DataAccessException e) {

    // return APIResponse.InternalServerError();
    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();
    // } catch (Exception e) {
    // return APIResponse.InternalServerError();
    // }
    // }

    // @PostMapping("/fms_delete")
    // public ResponseEntity<ApiResponse<Integer>> fmsDelete(HttpServletRequest
    // request,
    // @RequestBody FMSRequest fmsRequest) {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }
    // try {
    // Integer result = spService.sp_updfms_ina(
    // fmsRequest.getI_fms_cd(),
    // authService.getLoginUserName());

    // if (result == null) {
    // return APIResponse.NoDataFound();
    // }
    // return APIResponse.SuccessResponse(result);
    // } catch (DataAccessException e) {

    // return APIResponse.InternalServerError();
    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();
    // } catch (Exception e) {
    // return APIResponse.InternalServerError();
    // }
    // }

    // for debit scheduler
    // @PostMapping(value = "/TestDebit")
    // public ResponseEntity<ApiResponse<String>> TestDebit(HttpServletRequest
    // request) {

    // Integer result;

    // try {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // // put schduler logic
    // result = fmsDRMemoService.fms_drmemo_sch().size();

    // return APIResponse.SuccessResponse("");

    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();

    // } catch (Exception e) {
    // return APIResponse.InternalServerError();

    // }
    // }
    @PostMapping(value = "/TestDebit")
    public ResponseEntity<ApiResponse<String>> TestDebit(HttpServletRequest request) throws JsonProcessingException {

        Integer result;
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // put scheduler logic
        result = fmsDRMemoService.fms_drmemo_sch().size();

        return APIResponse.SuccessResponse("");

    }

    // // for credit scheduler
    // @PostMapping(value = "/TestCredit")
    // public ResponseEntity<ApiResponse<String>> TestCredit(HttpServletRequest
    // request) {

    // Integer result;

    // try {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // // put scheduler logic
    // result = fmsCRMemoService.fms_crmemo_sch().size();

    // return APIResponse.SuccessResponse("");

    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();

    // } catch (Exception e) {
    // return APIResponse.InternalServerError();

    // }
    // }

    // for credit scheduler
    @PostMapping(value = "/TestCredit")
    public ResponseEntity<ApiResponse<String>> TestCredit(HttpServletRequest request) throws JsonProcessingException {

        Integer result;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // put scheduler logic
        result = fmsCRMemoService.fms_crmemo_sch().size();

        return APIResponse.SuccessResponse("");

    }

    // // for arr scheduler
    // @PostMapping(value = "/TestARR")
    // public ResponseEntity<ApiResponse<String>> TestARR(HttpServletRequest
    // request) {

    // Integer result;

    // try {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // // put schduler logic
    // result = fmsARRService.fms_arr_sch().size();

    // return APIResponse.SuccessResponse("");

    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();

    // } catch (Exception e) {
    // return APIResponse.InternalServerError();

    // }
    // }

    // for arr scheduler
    @PostMapping(value = "/TestARR")
    public ResponseEntity<ApiResponse<String>> TestARR(HttpServletRequest request) throws JsonProcessingException {

        Integer result;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // put schduler logic
        result = fmsARRService.fms_arr_sch().size();

        return APIResponse.SuccessResponse("");

    }

    // // for ari scheduler
    // @PostMapping(value = "/TestARI")
    // public ResponseEntity<ApiResponse<String>> TestARI(HttpServletRequest
    // request) {
    // BigInteger result;

    // try {
    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // // put schduler logic
    // // get info and insert into fmsari table
    // List<FMSARIModel> fmsMTT = fmsARIService.sp_getfmsmtt();

    // while (fmsMTT.size() > 0) {
    // log.info("FMSARI: " + fmsMTT.size() + " records found.");
    // BigInteger ari_hid = null;

    // for (int i = 0; i < fmsMTT.size(); i++) {

    // // insert to header table
    // if (i == 0) {
    // ari_hid = fmsARIService.sp_insfmsmtth(fmsMTT.get(i).getCustomer());
    // }

    // // insert to detail table
    // // result = fmsARIService.sp_insfmsmttb(fmsMTT.get(i).getMtt_pg_id(),
    // // fmsMTT.get(i).getPg_pymt_amt(), fmsMTT.get(i).getQty(),
    // // fmsMTT.get(i).getItem_desc(),
    // // fmsMTT.get(i).getUnit_fee(), fmsMTT.get(i).getRcpt_no(),
    // // fmsMTT.get(i).getCust_nm(), fmsMTT.get(i).getEntity_nm(),
    // // fmsMTT.get(i).getEntity_no(),
    // // fmsMTT.get(i).getEntity_type(), fmsMTT.get(i).getGross_amt(),
    // // fmsMTT.get(i).getFee_detail_id(), fmsMTT.get(i).getPg_pymt_method(),
    // // fmsMTT.get(i).getTax_amt(),
    // // fmsMTT.get(i).getCustomer(), ari_hid, fmsMTT.get(i).getItem_ref_no(),
    // // fmsMTT.get(i).getCp_no());

    // result = fmsARIService.sp_insfmsmttb(fmsMTT.get(i), ari_hid);

    // }

    // fmsMTT = fmsARIService.sp_getfmsmtt();

    // }

    // // call fms posting api
    // List<FMSARIModel> fmsARI = fmsARIService.sp_getfmsari();

    // if (fmsARI.size() > 0) {
    // log.info("FMSARI: " + fmsARI.size() + " records found.");

    // String body = fmsARIService.generateStringBody(fmsARI);

    // }

    // return APIResponse.SuccessResponse("");

    // } catch (NumberFormatException e) {
    // return APIResponse.InvalidFormat();

    // } catch (Exception e) {
    // return APIResponse.InternalServerError();

    // }
    // }

    // for ari scheduler
    @PostMapping(value = "/TestARI")
    public ResponseEntity<ApiResponse<String>> TestARI(HttpServletRequest request) {
        BigInteger result;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // put schduler logic
        // get info and insert into fmsari table
        List<FMSARIModel> fmsMTT = fmsARIService.sp_getfmsmtt();

        while (fmsMTT.size() > 0) {
            log.debug("FMSARI: " + fmsMTT.size() + " records found.");
            BigInteger ari_hid = null;

            for (int i = 0; i < fmsMTT.size(); i++) {

                // insert to header table
                if (i == 0) {
                    ari_hid = fmsARIService.sp_insfmsmtth(fmsMTT.get(i).getCustomer(),fmsMTT.get(i).getInv_dt());
                }
                BigInteger current_mtt_pg_id = fmsMTT.get(i).getMtt_pg_id();
                Integer flag = 0;

                // If this is not the last record, compare with the next record's mtt_pg_id
                if (i < CollectionUtils.size(fmsMTT) - 1) {
                    BigInteger next_mtt_pg_id = fmsMTT.get(i + 1).getMtt_pg_id();
                    if (!current_mtt_pg_id.equals(next_mtt_pg_id)) {
                        flag = 1; // Set the flag if current mtt_pg_id is different from the next one
                    }
                } else {
                    // If it's the last record, set flag to 1 (assuming there's no next record)
                    flag = 1;
                }
                // insert to detail table
                // result = fmsARIService.sp_insfmsmttb(fmsMTT.get(i).getMtt_pg_id(),
                // fmsMTT.get(i).getPg_pymt_amt(), fmsMTT.get(i).getQty(),
                // fmsMTT.get(i).getItem_desc(),
                // fmsMTT.get(i).getUnit_fee(), fmsMTT.get(i).getRcpt_no(),
                // fmsMTT.get(i).getCust_nm(), fmsMTT.get(i).getEntity_nm(),
                // fmsMTT.get(i).getEntity_no(),
                // fmsMTT.get(i).getEntity_type(), fmsMTT.get(i).getGross_amt(),
                // fmsMTT.get(i).getFee_detail_id(), fmsMTT.get(i).getPg_pymt_method(),
                // fmsMTT.get(i).getTax_amt(),
                // fmsMTT.get(i).getCustomer(), ari_hid, fmsMTT.get(i).getItem_ref_no(),
                // fmsMTT.get(i).getCp_no());

                result = fmsARIService.sp_insfmsmttb(fmsMTT.get(i), ari_hid, flag);

            }

            fmsMTT = fmsARIService.sp_getfmsmtt();

        }

        // call fms posting api
        List<FMSARIModel> fmsARI = fmsARIService.sp_getfmsari();

        if (fmsARI.size() > 0) {
            log.debug("FMSARI: " + fmsARI.size() + " records found.");

            String body = fmsARIService.generateStringBody(fmsARI);

        }

        return APIResponse.SuccessResponse("");
    }

    // Sample API Call to send FMS ARI in real-time
    @PostMapping(value = "/triggerFMSARI")
    public ResponseEntity<ApiResponse<String>> sp_getfmsariimmediate(HttpServletRequest request,
            @RequestBody FMSARIImmediateRequest fmsARIImmediateRequest) {
        BigInteger result;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Call FMS Post Accounting API
        List<FMSARIModel> fmsARI = fmsARIService.sp_getfmsariimmediate(fmsARIImmediateRequest);

        if (fmsARI.size() > 0) {
            log.debug("FMSARI: " + fmsARI.size() + " records found.");

            String body = fmsARIService.generateStringBody(fmsARI);
        }
        return APIResponse.SuccessResponse("");
    }

    // Sample API Call to send FMS ARI in real-time
    @PostMapping(value = "/triggerFMSARV")
    public ResponseEntity<ApiResponse<String>> sp_getfmsarvimmediate(HttpServletRequest request,
            @RequestBody FMSARIImmediateRequest fmsARIImmediateRequest) throws JsonProcessingException {

        BigInteger result;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Call FMS Post Accounting API
        fmsARVService.postFMSAVImmediate(fmsARIImmediateRequest);

        return APIResponse.SuccessResponse("");
    }

    @PostMapping(value = "/getfmsarijson")
    public ResponseEntity<ApiResponse<Object>> sp_getfmsarijson(HttpServletRequest request,
            @RequestParam String rms_batch_no) {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        List<FMSARIModel> fmsARI = fmsARIService.sp_getfmsarijson(rms_batch_no);

        if (!fmsARI.isEmpty()) {
            log.debug("FMSARI: " + fmsARI.size() + " records found.");

            String bodyJson = fmsARIService.generateStringBodyJSON(fmsARI);

            try {
                // Convert JSON string to object so response will not be escaped
                Object jsonObject = new ObjectMapper().readValue(bodyJson, Object.class);
                return APIResponse.SuccessResponse(jsonObject);
            } catch (Exception e) {
                log.error("Failed to parse JSON", e);
                return APIResponse.SuccessResponse(bodyJson);
            }
        }
        return APIResponse.NoDataFound();
    }

}
