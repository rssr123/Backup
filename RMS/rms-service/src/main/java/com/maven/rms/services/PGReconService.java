package com.maven.rms.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.maven.rms.interfaces.IPGReconService;
import com.maven.rms.models.PGReconUploadRequest;
import com.maven.rms.models.RMSDetailListingRequest;
import com.maven.rms.models.RMSDetailListingResponse;
import com.maven.rms.models.PGDetailListingRequest;
import com.maven.rms.models.PGDetailListingResponse;
import com.maven.rms.models.PGRecon;
import com.maven.rms.models.PGReconDetailRequest;
import com.maven.rms.models.PGReconDetailResponse;
import com.maven.rms.models.PGReconDocList;
import com.maven.rms.models.PGReconExcelFile;
import com.maven.rms.models.PGReconListRequest;
import com.maven.rms.models.PGReconListResponse;
import com.maven.rms.models.PGReconTaskRequest;
import com.maven.rms.repositories.PGReconRepository;

@Service
@Slf4j
public class PGReconService implements IPGReconService {
    @Autowired
    private DataSource ds;
    
    private final PGReconRepository pgReconRepo;
	
	public PGReconService(PGReconRepository pgReconRepo) {
		this.pgReconRepo = pgReconRepo;
	}

    @Override
    public Integer sp_uploadDoc(PGReconUploadRequest pgDocRequest) throws SerialException, SQLException {
        // Decode Base64 content
        byte[] decodedBytes = decodeBase64(pgDocRequest.getI_file_content());
        PGRecon pgRecon = new PGRecon();
        Blob blob = new SerialBlob(decodedBytes);
        pgRecon = readExcelHeader(decodedBytes);

        // Call the repository method
        Integer result = pgReconRepo.sp_uploadDoc(pgDocRequest, blob, pgRecon);
        return result;
    }

    private byte[] decodeBase64(String base64String) {
        if (base64String.startsWith("data:")) {
            base64String = base64String.substring(base64String.indexOf(',') + 1);
        }
        base64String = base64String.replaceAll("\\s", "").replace(":", "");
        return Base64.getDecoder().decode(base64String);
    }

    //20250319 Modified the function by Geo
    @Override
    public List<BigInteger> sp_insPGTxn() throws SQLException {

        BigInteger result;
        Integer result2 = 0;
        List<BigInteger> rcPGTxnIds = new ArrayList<>();
        List<BigInteger> rcPGIds = new ArrayList<>();
        List<BigInteger> uniqueRcPGIds = new ArrayList<>();
        List<PGReconExcelFile> pgReconExcelFiles = new ArrayList<>();

        //for success insert into txn table
        //get file from db
        List<Object[]> objects = pgReconRepo.sp_getPGReconDoc();

        if(objects != null && objects.size() > 0)
        {
            List<PGReconDocList> pgReconDocList = convertToPGReconDocList(objects);

            // Vicky : 20250304 - change to insert per file. Old way is extract all file and insert one by one.
            try {
                //read excel content
                for (PGReconDocList pgReconDocList2 : pgReconDocList) {
                    pgReconExcelFiles.clear();
                    // pgReconExcelFiles.addAll(readExcelContent(pgReconDocList2.getI_rc_pg_id(),pgReconDocList2.getI_file_content()));
                    pgReconExcelFiles.addAll(readExcelContent(pgReconDocList2));

                    result2 = 0;
                    rcPGTxnIds.clear();

                    //insert txn into db
                    if(pgReconExcelFiles != null && pgReconExcelFiles.size() > 0){
                        for (PGReconExcelFile pgReconExcelFile : pgReconExcelFiles) 
                        {
                            try{
                                //update status to EIP
                                if(result2 == 0){
                                    result2 = pgReconRepo.sp_updPGReconStatus(pgReconExcelFile.getRc_pg_id(),"EIP");
                                }

                                if(result2 > 0){
                                    result = pgReconRepo.sp_insPGTxn(pgReconExcelFile);
                                    if(result.compareTo(BigInteger.ZERO) > 0){
                                        rcPGTxnIds.add(result);
                                        rcPGIds.add(pgReconExcelFile.getRc_pg_id());
                                    }      
                                    else{
                                        break;
                                    }
                                }
                                else{
                                    break;
                                }
                            }catch (Exception e) {
                                //update status to FE
                                result2 = pgReconRepo.sp_updPGReconStatus(pgReconExcelFile.getRc_pg_id(),"EF");
                                rcPGIds.clear();
                                e.printStackTrace();
                            }
                        }

                        //check any failed insert
                        // if(rcPGTxnIds.size() != pgReconExcelFiles.size()){
                        if(CollectionUtils.size(rcPGTxnIds) != CollectionUtils.size(pgReconExcelFiles)){

                            //update status to FE
                            result2 = pgReconRepo.sp_updPGReconStatus(pgReconExcelFiles.get(0).getRc_pg_id(),"EF");

                            //delete the inserted txn
                            for (BigInteger rcPGTxnId : rcPGTxnIds) {
                                pgReconRepo.sp_delPGTxn(rcPGTxnId);
                            }

                            rcPGIds.clear();
                        }
                    }
                    else{
                        result2 = pgReconRepo.sp_updPGReconStatus(pgReconExcelFiles.get(0).getRc_pg_id(),"EF");
                    }
                }
            } catch (Exception e) {
                //update status to FE
                result2 = pgReconRepo.sp_updPGReconStatus(pgReconExcelFiles.get(0).getRc_pg_id(),"EF");
                rcPGIds.clear();
                e.printStackTrace();
            }

            uniqueRcPGIds = rcPGIds.stream().distinct().collect(Collectors.toList());

            if(uniqueRcPGIds == null || uniqueRcPGIds.size() == 0){
                uniqueRcPGIds = new ArrayList<>();
            }
        }
        
        return uniqueRcPGIds;
    }

    @Override
    public Integer sp_insMTTTxn(List<BigInteger> rcPGTxnIds) {

        BigInteger result;

        //compare and install into rms_rc_pgmtt
        for (BigInteger bigInteger : rcPGTxnIds) {
            result = pgReconRepo.sp_insMTTTxn(bigInteger);
            //compare the not found record and update status, put here because per settelement
            result = pgReconRepo.sp_updMTTTxn();
        }

        return 0;
    }

    @Override
    public List<PGReconListResponse> sp_getPGReconList(PGReconListRequest pgListRequest) {

		BigInteger result;
        List<PGReconListResponse> pgReconListResponse = new ArrayList<>();

        pgReconListResponse = convertTopgReconListResponse(pgReconRepo.sp_getPGReconList(pgListRequest));

        return pgReconListResponse;
	}

    @Override
    public PGReconDetailResponse sp_getPGReconDetail(PGReconDetailRequest pgReconDetailRequest) {

		BigInteger result;
        PGReconDetailResponse pgReconListResponse = new PGReconDetailResponse();

        pgReconListResponse = convertToPGReconDetail(pgReconRepo.sp_getPGReconDetail(pgReconDetailRequest));

        return pgReconListResponse;
	}

    @Override
    public BigInteger sp_updPGReconDetail(PGReconTaskRequest pgReconTaskRequest) {

		BigInteger result=null;

        result = pgReconRepo.sp_updPGReconDetail(pgReconTaskRequest);

        return result;
	}

    @Override
    public List<PGDetailListingResponse> sp_getPGDetailListing(PGDetailListingRequest pgDetailListing) {

		List<PGDetailListingResponse> pgDetailListingResponses = new ArrayList<>();

        pgDetailListingResponses = convertToPGDetailListingResponse(pgReconRepo.sp_getPGDetailListing(pgDetailListing));

        return pgDetailListingResponses;
	}

    
    @Override
    public List<RMSDetailListingResponse> sp_getRMSDetailListing(RMSDetailListingRequest rmsDetailListingRequest) {

		List<RMSDetailListingResponse> rmsDetailListingResponses = new ArrayList<>();

        rmsDetailListingResponses = convertToRMSDetailListingResponse(pgReconRepo.sp_getRMSDetailListing(rmsDetailListingRequest));

        return rmsDetailListingResponses;
	}

	@Override
    public String sp_getrcpgdoc(PGReconListRequest pgReconRequest) throws SQLException{
        Blob blob = (Blob) pgReconRepo.sp_getrcpgdoc(pgReconRequest);

        // Convert Blob to byte array
        byte[] bytes = blob.getBytes(1, (int) blob.length());

        // Convert byte array to Base64-encoded string
        String base64Content = Base64.getEncoder().encodeToString(bytes);

        return base64Content;
    }


    private List<PGReconDocList> convertToPGReconDocList(List<Object[]> objects) throws SQLException {
    List<PGReconDocList> pgReconDocList = new ArrayList<>();

    for (Object[] obj : objects) {
        PGReconDocList pgReconDoc = new PGReconDocList();
        pgReconDoc.setI_rc_pg_id((BigInteger) obj[0]);
        pgReconDoc.setI_file_nm((String) obj[1]);

        Blob blob = (Blob) obj[2];

        // Convert Blob to byte array
        byte[] bytes = blob.getBytes(1, (int) blob.length());

        // Convert byte array to Base64-encoded string
        String base64Content = Base64.getEncoder().encodeToString(bytes);

        pgReconDoc.setI_file_content(base64Content);

        pgReconDocList.add(pgReconDoc);
    }

    return pgReconDocList;
}
    
    private List<PGReconListResponse> convertTopgReconListResponse(List<Object[]> objects) {
        List<PGReconListResponse> pgReconListResponses = new ArrayList<>();

        for (Object[] obj : objects) {
            PGReconListResponse pgReconListResponse = new PGReconListResponse();
            pgReconListResponse.setI_task_id((String) obj[0]);
            pgReconListResponse.setI_file_nm((String) obj[1]);
            pgReconListResponse.setI_dt_settlement((String) obj[2]);
            pgReconListResponse.setI_dt_uploaded((String) obj[3]);
            pgReconListResponse.setI_merchant_id((String) obj[4]);
            pgReconListResponse.setI_task_status((String) obj[5]);
            pgReconListResponse.setI_recon_status((String) obj[6]);
            pgReconListResponse.setTotal((Integer) obj[7]);
            pgReconListResponse.setI_uploadedby((String) obj[8]);
            pgReconListResponses.add(pgReconListResponse);
        }

        return pgReconListResponses;
    }

    public PGReconDetailResponse convertToPGReconDetail(Object[] obj) {
    PGReconDetailResponse pgReconDetail = new PGReconDetailResponse();
    pgReconDetail.setPg_txn_settlement_no((Integer) obj[0]);
    pgReconDetail.setPg_total_txn_settlement_amt((BigDecimal) obj[1]);
    pgReconDetail.setPg_txn_adj_no((Integer) obj[2]);
    pgReconDetail.setPg_total_txn_adj_amt((BigDecimal) obj[3]);
    pgReconDetail.setPg_total_txn_other((BigDecimal) obj[4]);
    pgReconDetail.setPg_found_no((Integer) obj[5]);
    pgReconDetail.setPg_found_total((BigDecimal) obj[6]);
    pgReconDetail.setPg_not_found_no((Integer) obj[7]);
    pgReconDetail.setPg_not_found_total((BigDecimal) obj[8]);
    pgReconDetail.setPg_sam_no((Integer) obj[9]);
    pgReconDetail.setPg_sam_total((BigDecimal) obj[10]);
    pgReconDetail.setPg_snm_no((Integer) obj[11]);
    pgReconDetail.setPg_snm_total((BigDecimal) obj[12]);
    pgReconDetail.setPg_txf_no((Integer) obj[13]);
    pgReconDetail.setPg_txf_total((BigDecimal) obj[14]);
    pgReconDetail.setPg_matched_no((Integer) obj[15]);
    pgReconDetail.setPg_matched_total((BigDecimal) obj[16]);
    pgReconDetail.setRms_txn_no((Integer) obj[17]);
    pgReconDetail.setRms_paid_no((Integer) obj[18]);
    pgReconDetail.setRms_paid_total((BigDecimal) obj[19]);
    pgReconDetail.setRms_failed_no((Integer) obj[20]);
    pgReconDetail.setRms_failed_total((BigDecimal) obj[21]);
    pgReconDetail.setRms_rcpt_no((Integer) obj[22]);
    pgReconDetail.setRms_rcpt_total((BigDecimal) obj[23]);
    pgReconDetail.setRms_sam_no((Integer) obj[24]);
    pgReconDetail.setRms_sam_total((BigDecimal) obj[25]);
    pgReconDetail.setRms_snm_no((Integer) obj[26]);
    pgReconDetail.setRms_snm_total((BigDecimal) obj[27]);
    pgReconDetail.setRms_txf_no((Integer) obj[28]);
    pgReconDetail.setRms_txf_total((BigDecimal) obj[29]);
    pgReconDetail.setRms_cip_no((Integer) obj[30]);
    pgReconDetail.setRms_cip_total((BigDecimal) obj[31]);
    pgReconDetail.setRms_ncp_no((Integer) obj[32]);
    pgReconDetail.setRms_ncp_total((BigDecimal) obj[33]);
    pgReconDetail.setRms_nfp_no((Integer) obj[34]);
    pgReconDetail.setRms_nfp_total((BigDecimal) obj[35]);
    pgReconDetail.setTask_id((String) obj[36]);
    pgReconDetail.setRecon_status((String) obj[37]);
    pgReconDetail.setDt_statement((String) obj[38]);
    pgReconDetail.setDt_settlement_char((String) obj[39]);
    pgReconDetail.setRemarks((String) obj[40]);
    pgReconDetail.setTask_status((String) obj[41]);
    return pgReconDetail;
}

    private List<PGDetailListingResponse> convertToPGDetailListingResponse(List<Object[]> objects) {
        List<PGDetailListingResponse> pgDetailListingResponses = new ArrayList<>();

        for (Object[] obj : objects) {
            PGDetailListingResponse pgDetailListingResponse = new PGDetailListingResponse();
            pgDetailListingResponse.setDt_txn((String) obj[0]);
            pgDetailListingResponse.setTxn_id((String) obj[1]);
            pgDetailListingResponse.setTxn_type((String) obj[2]);
            pgDetailListingResponse.setTxn_cd((String) obj[3]);
            pgDetailListingResponse.setFound_in_rms((String) obj[4]);
            pgDetailListingResponse.setSub_criteria((String) obj[5]);
            pgDetailListingResponse.setTxn_amt((BigDecimal) obj[6]);
            pgDetailListingResponse.setMdr_amt((BigDecimal) obj[7]);
            pgDetailListingResponse.setSst_amt((BigDecimal) obj[8]);
            pgDetailListingResponse.setNet_amt((BigDecimal) obj[9]);
            pgDetailListingResponse.setTotal((Integer) obj[10]);
            pgDetailListingResponses.add(pgDetailListingResponse);
        }

        return pgDetailListingResponses;
    }

    private List<RMSDetailListingResponse> convertToRMSDetailListingResponse(List<Object[]> objects) {
        List<RMSDetailListingResponse> rmsDetailListingResponses = new ArrayList<>();
    
        for (Object[] obj : objects) {
            RMSDetailListingResponse rmsDetailListingResponse = new RMSDetailListingResponse();
            rmsDetailListingResponse.setDt_txn((String) obj[0]);
            rmsDetailListingResponse.setTxn_id((String) obj[1]);
            rmsDetailListingResponse.setCust_nm((String) obj[2]);
            rmsDetailListingResponse.setOrn_no((String) obj[3]);
            rmsDetailListingResponse.setFound_in_pg((String) obj[4]);
            rmsDetailListingResponse.setSub_criteria((String) obj[5]);
            rmsDetailListingResponse.setTxn_amt((BigDecimal) obj[6]);
            rmsDetailListingResponse.setOrder_status((String) obj[7]);
            rmsDetailListingResponse.setTotal((Integer) obj[8]);
            rmsDetailListingResponses.add(rmsDetailListingResponse);
        }
    
        return rmsDetailListingResponses;
    }

    private static PGRecon readExcelHeader(byte[] excelData) {

        PGRecon pgRecon = new PGRecon();

        List<String> cellsToCheck = Arrays.asList("K4", "K5", "K6","L9","L10","L11","L12","L13","L14","L15"); // replace with the cells you want to check

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excelData))) {
            // Iterate through sheets
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);

                for (String cellToCheck : cellsToCheck) {
                    int rowIndex = Integer.parseInt(cellToCheck.substring(1)) - 1;
                    //CellReference.convertColStringToIndex(cellToCheck.substring(1)) - 1; // 0-indexed
                    int cellIndex = cellToCheck.charAt(0) - 'A'; // 0-indexed

                    int a = sheet.getPhysicalNumberOfRows();
                    int b = sheet.getRow(rowIndex).getLastCellNum();

                    // Check if the sheet has enough rows and the row has enough cells
                    if (sheet.getPhysicalNumberOfRows() > rowIndex && sheet.getRow(rowIndex).getLastCellNum() > cellIndex) {
                        Cell cell = sheet.getRow(rowIndex).getCell(cellIndex);

                        if (cellToCheck =="K4" && cell != null) {
                            pgRecon.setI_merchantId(cell.toString());
                        } else if (cellToCheck =="K5" &&cell != null) {
                            pgRecon.setI_stmtNo(cell.toString());
                        } else if (cellToCheck =="K6" && cell != null) {
                            pgRecon.setI_dtStatement(cell.toString());
                        } else if (cellToCheck =="L9" && cell != null) {
                            pgRecon.setI_balBfwd(new BigDecimal(cell.toString()));
                        } else if (cellToCheck =="L10" && cell != null) {
                            pgRecon.setI_totalTxn(new BigDecimal(cell.toString()));
                        } else if (cellToCheck =="L11" && cell != null) {
                            pgRecon.setI_totalRefund(new BigDecimal(cell.toString()));
                        } else if (cellToCheck =="L12" && cell != null) {
                            pgRecon.setI_totalAdj(new BigDecimal(cell.toString()));
                        } else if (cellToCheck =="L13" && cell != null) {
                            pgRecon.setI_totalOthers(new BigDecimal(cell.toString()));
                        } else if (cellToCheck =="L14" && cell != null) {
                            pgRecon.setI_totalPaid(new BigDecimal(cell.toString()));
                        } else if (cellToCheck =="L15" && cell != null) {
                            pgRecon.setI_balCfwd(new BigDecimal(cell.toString()));
                        } 

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return pgRecon;
    }
    
    // private static List<PGReconExcelFile> readExcelContent(BigInteger rcPGId,String excelData){
    private static List<PGReconExcelFile> readExcelContent(PGReconDocList pgReconDocList2){
        List<PGReconExcelFile> pgReconExcelFiles = new ArrayList<>();

        try {
            // String base64Excel = excelData;// get the Base64 string from the database
            String base64Excel = pgReconDocList2.getI_file_content();// get the Base64 string from the database
            byte[] decodedBytes = Base64.getDecoder().decode(base64Excel);
            InputStream is = new ByteArrayInputStream(decodedBytes);

            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0); // or getSheet(name)
            int firstRow = 20; // 0-based, so 20 is the 21st row
            int lastRow = sheet.getLastRowNum();

            for (int rowIndex = firstRow; rowIndex <= lastRow; rowIndex++) {
                Row row = sheet.getRow(rowIndex);

                // If the row is null or the cell in column B is empty, break the loop
                if (row == null || row.getCell(1) == null || row.getCell(1).toString().trim().isEmpty()) {
                    break;
                }

                PGReconExcelFile pgReconExcelFile = new PGReconExcelFile();

                for (int colIndex = 1; colIndex < 12; colIndex++) { // for columns B-L
                    Cell cell = row.getCell(colIndex);
                    if(cell != null && (colIndex == 3 || colIndex == 5 || colIndex == 7)){
                        DataFormat dataFormat = workbook.createDataFormat();
                        CellStyle textStyle = workbook.createCellStyle();
                        textStyle.setDataFormat(dataFormat.getFormat("@"));
                        cell.setCellStyle(textStyle);

                    }
                    String cellValue = cell.toString();
                    CellType a = cell.getCellType();

                    // pgReconExcelFile.setRc_pg_id(rcPGId);
                    pgReconExcelFile.setRc_pg_id(pgReconDocList2.getI_rc_pg_id());
                    switch (colIndex) {
                        case 1:
                            pgReconExcelFile.setDt_txn(cellValue);
                            break;
                        case 2:
                            pgReconExcelFile.setTxn_id(cellValue);
                            break;
                        case 4: //3>4
                            pgReconExcelFile.setTxn_type(cellValue);
                            break;
                        case 6: //5>6
                            pgReconExcelFile.setTxn_cd(cellValue);
                            break;
                        case 8: //7>8
                            pgReconExcelFile.setTxn_amt(new BigDecimal(cellValue));
                            break;
                        case 9:
                            pgReconExcelFile.setMdr_amt(new BigDecimal(cellValue));
                            break;
                        case 10:
                            pgReconExcelFile.setSst_amt(new BigDecimal(cellValue));
                            break;
                        case 11:
                            pgReconExcelFile.setNet_amt(new BigDecimal(cellValue));
                            break;
                        default:
                            break;
                    }
                }

                pgReconExcelFiles.add(pgReconExcelFile);
            }

        } catch (Exception e) {
             e.printStackTrace();
        }

            return pgReconExcelFiles;

    }

    //20250317 - By Geo
    @Override
    public Integer sp_checkpgtask(PGReconTaskRequest pgReconTaskRequest){
        Integer result = 0;

        result = pgReconRepo.sp_checkpgtask(pgReconTaskRequest);

        return result;
    }

}
