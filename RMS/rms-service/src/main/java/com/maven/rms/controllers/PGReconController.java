package com.maven.rms.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.maven.rms.config.RMSProperties;
import com.maven.rms.models.PGDetailListingRequest;
import com.maven.rms.models.PGDetailListingResponse;
import com.maven.rms.models.PGReconDetailRequest;
import com.maven.rms.models.PGReconDetailResponse;
import com.maven.rms.models.PGReconListRequest;
import com.maven.rms.models.PGReconListResponse;
import com.maven.rms.models.PGReconTaskRequest;
import com.maven.rms.models.PGReconUploadRequest;
import com.maven.rms.models.RMSDetailListingRequest;
import com.maven.rms.models.RMSDetailListingResponse;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.FMSARRService;
import com.maven.rms.services.FMSCRMemoService;
import com.maven.rms.services.FMSDRMemoService;
import com.maven.rms.services.PGReconService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.CacheManager;
import com.maven.rms.utils.ControllersEnum;
import com.maven.rms.utils.RMSLogger;

@RestController
@RequestMapping("/api/pgrecon/v1")
@Slf4j
public class PGReconController {

    //private static final Logger logger = LoggerFactory.getLogger(OnlinePaymentController.class);

    private PGReconService PGReconService;

    private final RMSProperties rmsProperties;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private PGReconService pgReconService;

    @Autowired
    private FMSDRMemoService fmsDRMemoService;

    @Autowired
    private FMSCRMemoService fmsCRMemoService;
    
    @Autowired
    private FMSARRService fmsARRService;

    public PGReconController(RMSProperties rmsProperties,PGReconService PGReconService){
        this.rmsProperties = rmsProperties;
        this.PGReconService=PGReconService;

        RMSLogger.info("PGReconController services is started");
    }

    //@Secured("ROLE_USER")
    @PostMapping(value = "/sp_uploadPGDoc")
    public ResponseEntity<ApiResponse<String>> sp_uploadPGFile(HttpServletRequest request, @RequestBody PGReconUploadRequest pgDocRequest) throws SerialException, SQLException {
        
        Integer result;

     
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String base64String = pgDocRequest.getI_file_content();

        if (base64String.startsWith("data:")) {
            base64String = base64String.substring(base64String.indexOf(',') + 1);
        }
        base64String = base64String.replaceAll("\\s", "").replace(":", "");

        // check header and minimum required fields
        byte[] decodedBytes = Base64.getDecoder().decode(base64String);
        

        // Validate the content of the Excel file using Apache POI
        if(validateExcelContent(decodedBytes)){
            pgDocRequest.setI_created_by(authService.getLoginUserName());
            pgDocRequest.setI_modified_by(authService.getLoginUserName());
            // result = PGReconService.sp_uploadDoc(pgDocRequest, authService.getLoginUserName());
            result = PGReconService.sp_uploadDoc(pgDocRequest);

            if(result==-1 || result == 0){
                // failed upload
                return APIResponse.InvalidFormat();
            }
            else if(result==-2){
                // failed upload due to duplicate
                return APIResponse.DuplicateData();
            }
            else{
                return APIResponse.SuccessResponse("");
            }
        }
        else{
            //invalid format
            return APIResponse.InvalidFormat();
        }
    }

    private static boolean validateExcelContent(byte[] excelData) {

        boolean isValid = false;

        List<String> cellsToCheck = Arrays.asList("I4", "I5", "I6","I9","I10","I11","I12","I13","I14","I15"); // replace with the cells you want to check

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excelData))) {
            // Iterate through sheets
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);

                for (String cellToCheck : cellsToCheck) {
                    isValid = false;

                    int rowIndex = Integer.parseInt(cellToCheck.substring(1)) - 1;
                    //CellReference.convertColStringToIndex(cellToCheck.substring(1)) - 1; // 0-indexed
                    int cellIndex = cellToCheck.charAt(0) - 'A'; // 0-indexed

                    // Check if the sheet has enough rows and the row has enough cells
                    if (sheet.getPhysicalNumberOfRows() > rowIndex && sheet.getRow(rowIndex).getPhysicalNumberOfCells() > cellIndex) {
                        Cell cell = sheet.getRow(rowIndex).getCell(cellIndex);

                        // Check if the cell's value is 'Merchant ID:'
                        if (cell != null && "Merchant ID:".equals(cell.toString())) {
                            isValid=true;
                        } else if (cell != null && "Statement No.:".equals(cell.toString())) {
                            isValid=true;
                        } else if (cell != null && "Statement Date:".equals(cell.toString())) {
                            isValid=true;
                        } else if (cell != null && "Balance Brought Forward".equals(cell.toString())) {
                            isValid=true;
                        } else if (cell != null && "Total Transactions".equals(cell.toString())) {
                            isValid=true;
                        } else if (cell != null && "Total Chargeback / Refund".equals(cell.toString())) {
                            isValid=true;
                        } else if (cell != null && "Total Transaction Adjustments".equals(cell.toString())) {
                            isValid=true;
                        } else if (cell != null && "Others".equals(cell.toString())) {
                            isValid=true;
                        } else if (cell != null && "Less: Paid by GHL".equals(cell.toString())) {
                            isValid=true;
                        } else if (cell != null && "Balance Carried Forward".equals(cell.toString())) {
                            isValid=true;
                        } 

                        if (isValid==false) {
                            break;
                        }

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            isValid=false;
        }

        return isValid;
    }
    
    @PostMapping(value = "/sp_getPGReconList")
    public ResponseEntity<ApiResponse<List<PGReconListResponse>>> sp_getPGReconList(HttpServletRequest request, @RequestBody PGReconListRequest pgListRequest) {
        
        Integer result;
        List<PGReconListResponse> pgReconListResponse = new ArrayList<>();

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            pgReconListResponse = PGReconService.sp_getPGReconList(pgListRequest);

            if (pgReconListResponse.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.PG_RECON_CONTROLLER);
            }

           return APIResponse.SuccessResponse(pgReconListResponse);

    }

    @PostMapping(value = "/sp_getPGReconDetail")
    public ResponseEntity<ApiResponse<PGReconDetailResponse>> sp_getPGReconDetail(HttpServletRequest request, @RequestBody PGReconDetailRequest pgReconDetailRequest) {
        
        Integer result;
        PGReconDetailResponse pgReconDetail = new PGReconDetailResponse();

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // pgReconDetail = PGReconService.sp_getPGReconDetail(pgReconDetailRequest.getI_task_id());
            pgReconDetail = PGReconService.sp_getPGReconDetail(pgReconDetailRequest);

            if (pgReconDetail==null) {
                return APIResponse.NoDataFound(ControllersEnum.PG_RECON_CONTROLLER);
            }

           return APIResponse.SuccessResponse(pgReconDetail);

    }

    @PostMapping(value = "/sp_updPGReconDetail")
    public ResponseEntity<ApiResponse<String>> sp_updPGReconDetail(HttpServletRequest request, @RequestBody PGReconTaskRequest pgReconTaskRequest) {
        
        BigInteger result;
        PGReconDetailResponse pgReconDetail = new PGReconDetailResponse();

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            pgReconTaskRequest.setI_modified_by(authService.getLoginUserName());

            // result = PGReconService.sp_updPGReconDetail(pgReconTaskRequest, authService.getLoginUserName());
            result = PGReconService.sp_updPGReconDetail(pgReconTaskRequest);
            //pgReconDetail = PGReconService.sp_getPGReconDetail(pgReconDetailRequest.getI_task_id());

            if (result==null) {
                return APIResponse.NoDataFound(ControllersEnum.PG_RECON_CONTROLLER);
            }

           return APIResponse.SuccessResponse("");

    }

    @PostMapping(value = "/sp_getPGDetailListing")
    public ResponseEntity<ApiResponse<List<PGDetailListingResponse>>> sp_getPGDetailListing(HttpServletRequest request, @RequestBody PGDetailListingRequest pgDetailListing) {
        
        Integer result;
        List<PGDetailListingResponse> pgDetailListingResponses = new ArrayList<>();


            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            pgDetailListingResponses = PGReconService.sp_getPGDetailListing(pgDetailListing);

            if (pgDetailListingResponses==null) {
                return APIResponse.NoDataFound(ControllersEnum.PG_RECON_CONTROLLER);
            }

           return APIResponse.SuccessResponse(pgDetailListingResponses);

    }

    @PostMapping(value = "/sp_getRMSDetailListing")
    public ResponseEntity<ApiResponse<List<RMSDetailListingResponse>>> sp_getRMSDetailListing(HttpServletRequest request, @RequestBody RMSDetailListingRequest rmsDetailListingRequest) {
        
        Integer result;
        List<RMSDetailListingResponse> rmsDetailListingResponses = new ArrayList<>();

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            rmsDetailListingResponses = PGReconService.sp_getRMSDetailListing(rmsDetailListingRequest);

            if (rmsDetailListingResponses==null) {
                return APIResponse.NoDataFound(ControllersEnum.PG_RECON_CONTROLLER);
            }

           return APIResponse.SuccessResponse(rmsDetailListingResponses);

    }

    @PostMapping(value = "/sp_getrcpgdoc")
    public ResponseEntity<ApiResponse<String>> sp_getrcpgdoc(HttpServletRequest request,
            @RequestBody PGReconListRequest pgReconRequest) throws SQLException {
 
        String result = "";

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = pgReconService.sp_getrcpgdoc(pgReconRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.BANK_RECON_DETAIL_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }


    //for testing scheduler
    @PostMapping(value = "/TestReadExcel")
    public ResponseEntity<ApiResponse<String>> TestReadExcel(HttpServletRequest request) throws SQLException {
        
        Integer result;
        List<BigInteger> rcPGIds = null;

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            //extract excel file and pg vs rms
            rcPGIds = pgReconService.sp_insPGTxn();

            //rms vs pg and not found in this pg, check previous pg
            result = pgReconService.sp_insMTTTxn(rcPGIds);

           return APIResponse.SuccessResponse("");

    }

    //for debit scheduler
    @PostMapping(value = "/TestDebit")
    public ResponseEntity<ApiResponse<String>> TestDebit(HttpServletRequest request) throws JsonProcessingException{
        
        Integer result;

      
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            //put schduler logic
           // result = fmsDRMemoService.fms_drmemo_sch().size();
            result = CollectionUtils.size(fmsDRMemoService.fms_drmemo_sch());
            return APIResponse.SuccessResponse("");

    }


    //for credit scheduler
    @PostMapping(value = "/TestCredit")
    public ResponseEntity<ApiResponse<String>> TestCredit(HttpServletRequest request) throws JsonProcessingException{
        
        Integer result;

       
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            //put schduler logic
            // result = fmsCRMemoService.fms_crmemo_sch().size();
            result = CollectionUtils.size(fmsCRMemoService.fms_crmemo_sch());
            return APIResponse.SuccessResponse("");

    }


     //for arr scheduler
    @PostMapping(value = "/TestARR2")
    public ResponseEntity<ApiResponse<String>> TestARR(HttpServletRequest request) throws JsonProcessingException{
        
        Integer result;

       
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            //put schduler logic
          //  result = fmsARRService.fms_arr_sch().size();
            result = CollectionUtils.size(fmsARRService.fms_arr_sch());;
            return APIResponse.SuccessResponse("");

    }

    //20250317 - By Geo
    @PostMapping(value = "/sp_checkpgtask")
    public ResponseEntity<ApiResponse<Integer>> sp_checkpgtask(HttpServletRequest request,
    @RequestBody PGReconTaskRequest pgReconTaskRequest) {

    Integer result = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = pgReconService.sp_checkpgtask(pgReconTaskRequest);

        if (result == null) {
            return APIResponse.NoDataFound(ControllersEnum.BANK_RECON_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);
    }

}
