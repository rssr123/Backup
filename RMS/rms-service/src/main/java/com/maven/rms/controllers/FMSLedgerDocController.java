package com.maven.rms.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.rowset.serial.SerialException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.FMSLedgerDocRequest;
import com.maven.rms.models.FMSLedgerDocWithoutFile;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.FMSService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/fmsl/v1")
@Slf4j
public class FMSLedgerDocController {
    // private static final Logger logger =
    // LoggerFactory.getLogger(FMSLedgerDocController.class);

    @Autowired
    private AuthService authService;
    @Autowired
    private FMSService spService;

    @PersistenceContext
    private EntityManager entityManager;

    @PostMapping(value = "/addfmsdoc")
    public ResponseEntity<ApiResponse<List<Integer>>> sp_insfmsledgerdoc(HttpServletRequest request,
            @RequestBody FMSLedgerDocRequest fmsLedgerDocRequest) throws SerialException, SQLException {

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        log.debug(fmsLedgerDocRequest.toString());
        log.debug("User calling: " + SecurityContextHolder.getContext().getAuthentication().getName());

        List<Integer> res;

        // Extract Base64 and validate Excel content
        String base64String = fmsLedgerDocRequest.getI_file_content();
        if (base64String.startsWith("data:")) {
            base64String = base64String.substring(base64String.indexOf(',') + 1);
        }
        base64String = base64String.replaceAll("\\s", "").replace(":", "");

        byte[] decodedBytes = Base64.getDecoder().decode(base64String);

        String errorMessage = validateExcelContent(decodedBytes);

        if (!errorMessage.isEmpty()) {
            log.error("Excel validation failed: " + errorMessage);
            return APIResponse.InvalidFormat(errorMessage);
        }

        // if (!validateExcelContent(decodedBytes)) {
        // log.error("Excel validation failed for request: " +
        // fmsLedgerDocRequest.toString());
        // return APIResponse.InvalidFormat();
        // }

        res = spService.sp_insfmsledgerdoc(fmsLedgerDocRequest, authService.getLoginUserName());
        Integer result = res.get(0); // First value should be file_index

        if (result <= 0) {
            log.error("Document insert failed: " + fmsLedgerDocRequest.toString());
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(res);
    }

    // @Secured("ROLE_USER")
    // @PostMapping(value = "/addfmsdoc")
    // public ResponseEntity<ApiResponse<ArrayList<Integer>>>
    // sp_insfmsledgerdoc(HttpServletRequest request,
    // @RequestBody FMSLedgerDocRequest fmsLedgerDocRequest) throws SerialException,
    // SQLException {

    // if (!authService.isAuthenticated(request)) {
    // return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    // }

    // // System.out.println(fmsLedgerDocRequest.toString());
    // log.info(fmsLedgerDocRequest.toString());
    // // System.out.println("User calling: " +
    // SecurityContextHolder.getContext().getAuthentication().getName());
    // log.info("User calling: " +
    // SecurityContextHolder.getContext().getAuthentication().getName());

    // Integer result;
    // ArrayList<Integer> res;

    // // try {

    // String base64String = fmsLedgerDocRequest.getI_file_content();

    // if (base64String.startsWith("data:")) {
    // base64String = base64String.substring(base64String.indexOf(',') + 1);
    // }
    // base64String = base64String.replaceAll("\\s", "").replace(":", "");

    // // check header and minimum required fields
    // byte[] decodedBytes = Base64.getDecoder().decode(base64String);

    // // Validate the content of the Excel file using Apache POI
    // if (validateExcelContent(decodedBytes)) {
    // res = spService.sp_insfmsledgerdoc(fmsLedgerDocRequest,
    // authService.getLoginUserName());
    // result = res.get(0);
    // } else {
    // log.error("Excel: not able to be parsed correctly!: " +
    // fmsLedgerDocRequest.toString());
    // return APIResponse.InvalidFormat();
    // }

    // if (result <= 0) {
    // log.error("Excel: Hit result <=0 here: " + fmsLedgerDocRequest.toString());
    // return APIResponse.InternalServerError();
    // }

    // return APIResponse.SuccessResponse(res);

    // // } catch (NumberFormatException e) {
    // // log.error("Hit NumberFormatException Error here: " +
    // fmsLedgerDocRequest.toString());
    // // log.error(e.getMessage());
    // // return APIResponse.InvalidFormat();

    // // } catch (Exception e) {
    // // //logger.error("Hit NumberFormatException Error here: " +
    // fmsLedgerDocRequest.toString());
    // // e.printStackTrace();
    // // log.error(e.getMessage());
    // // return APIResponse.InternalServerError();

    // // }
    // }

    public static Blob decodeBase64ToBlob(String base64String) throws SQLException {
        byte[] decodedBytes = Base64Utils.decodeFromString(base64String);
        return new javax.sql.rowset.serial.SerialBlob(decodedBytes);
    }

    private static String validateExcelContent(byte[] excelData) {
        boolean isValid = true; // Default to true, set to false if any check fails
        List<String> cellsToCheck = Arrays.asList("A1", "B1", "C1"); // Cells to validate

        String errorMessage = "";

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excelData))) {
            // Iterate through sheets
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);

                // Check if the first row has exactly 3 cells
                Row firstRow = sheet.getRow(0);
                if (firstRow == null || firstRow.getPhysicalNumberOfCells() != 3) {
                    errorMessage = "The first row must contain exactly 3 columns: 'Fee Detail ID', 'Fee Detail Name (EN)', and 'Ledger Code'.";
                    isValid = false; // More or fewer than 3 columns
                    break;
                }

                for (String cellToCheck : cellsToCheck) {
                    int rowIndex = Integer.parseInt(cellToCheck.substring(1)) - 1;
                    int cellIndex = cellToCheck.charAt(0) - 'A';

                    // Check if the row exists and has the required number of cells
                    if (sheet.getPhysicalNumberOfRows() > rowIndex
                            && sheet.getRow(rowIndex).getPhysicalNumberOfCells() >= 3) {

                        Cell cell = sheet.getRow(rowIndex).getCell(cellIndex);

                        // Validate cell content
                        if (cellIndex == 0 && !"Fee Detail ID".equals(cell.toString())) {
                            errorMessage = "Column A should be 'Fee Detail ID', but found " + cell.toString() + ".";
                            isValid = false;
                            break;
                        } else if (cellIndex == 1 && !"Fee Detail Name (EN)".equals(cell.toString())) {
                            errorMessage = "Column B should be 'Fee Detail Name (EN)', but found " + cell.toString()
                                    + ".";
                            isValid = false;
                            break;
                        } else if (cellIndex == 2 && !"Ledger Code".equals(cell.toString())) {
                            errorMessage = "Column C should be 'Ledger Code', but found " + cell.toString() + ".";
                            isValid = false;
                            break;
                        }
                    } else {
                        isValid = false; // Row doesn't exist or has fewer cells than expected
                        break;
                    }
                }

                // Break the loop if invalid
                if (!isValid) {
                    break;
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            isValid = false; // Error while reading the file
        }

        return errorMessage;
        // return isValid;
    }

    @PostMapping(value = "/getfmsdoc")
    public ResponseEntity<ApiResponse<List<FMSLedgerDocWithoutFile>>> sp_getfmsdoc(HttpServletRequest request,
            @RequestBody FMSLedgerDocRequest fmsLedgerDocRequest) {

        List<FMSLedgerDocWithoutFile> result = Collections.emptyList();

        // try {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getfmsdoc(
                fmsLedgerDocRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.FMS_LEDGER_DOC_CONTROLLER);
        }

        return APIResponse.SuccessResponse(result);

        // } catch (NumberFormatException e) {
        // log.error("Exception in " + this.getClass().toString(), e);
        // return APIResponse.InvalidFormat();

        // } catch (Exception e) {
        // log.error("Exception in " + this.getClass().toString(), e);
        // return APIResponse.InternalServerError();

        // }

    }

    @PostMapping(value = "/checkdocexist")
    public ResponseEntity<ApiResponse<Integer>> checkDocExist(
            HttpServletRequest request,
            @RequestBody FMSLedgerDocRequest fmsLedgerDocRequest) {

        // String fmsDoc = fmsLedgerDocRequest.getI_file_nm();

        // try {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Integer result = spService.sp_checkdocexist(fmsLedgerDocRequest);

        if (result > 0) { // got module using it
            return APIResponse.RecordInUsed(result);
        }

        return APIResponse.SuccessResponse(result);
        // } catch (NumberFormatException e) {
        // log.error("Exception in " + this.getClass().toString(), e);
        // return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        // log.error("Exception in " + this.getClass().toString(), e);
        // return APIResponse.InternalServerError();
        // }
    }

    @PostMapping(value = "/getfmsfilecontent")
    public ResponseEntity<ApiResponse<String>> sp_getfmsfilecontent(HttpServletRequest request,
            @RequestBody FMSLedgerDocRequest fmsLedgerDocRequest) {

        String result = "";

        // try {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = spService.sp_getfmsfilecontent(
                fmsLedgerDocRequest);

        if (result.isEmpty()) {
            return APIResponse.NoDataFound(ControllersEnum.FMS_LEDGER_DOC_CONTROLLER);
        }
        return APIResponse.SuccessResponse(result);
        // } catch (NumberFormatException e) {
        // log.error("Exception in " + this.getClass().toString(), e);
        // return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        // log.error("Exception in " + this.getClass().toString(), e);
        // return APIResponse.InternalServerError();
        // }

    }

    @PostMapping(value = "/getsummarycount")
    public ResponseEntity<ApiResponse<ArrayList<Integer>>> sp_getfmsledgersummarycount(HttpServletRequest request,
            @RequestBody FMSLedgerDocRequest fmsLedgerDocRequest) {
        ArrayList<Integer> res;
        Integer result = 0;
        Integer result2 = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        res = spService.sp_getfmsledgersummarycount(fmsLedgerDocRequest);
        result = res.get(0);
        result2 = res.get(1);

        if (result <= 0) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(res);

    }

}