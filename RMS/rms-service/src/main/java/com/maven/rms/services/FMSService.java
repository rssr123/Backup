package com.maven.rms.services;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.informix.jdbc.IfxLobDescriptor;
import com.informix.jdbc.IfxLocator;
import com.informix.jdbc.IfxSmartBlob;
import com.maven.rms.interfaces.IFMSService;
import com.maven.rms.models.FMS;
import com.maven.rms.models.FMSLedger;
import com.maven.rms.models.FMSLedgerDoc;
import com.maven.rms.models.FMSLedgerDocRequest;
import com.maven.rms.models.FMSLedgerDocWithoutFile;
import com.maven.rms.models.FMSLedgerRequest;
import com.maven.rms.models.FMSRequest;
import com.maven.rms.models.PGRecon;
import com.maven.rms.repositories.IFmsRepository;
import com.maven.rms.repositories.MTTRCPTRepository;
import com.maven.rms.repositories.RICPRepository;

@Service
@Slf4j
public class FMSService implements IFMSService {

    //private static final Logger logger = LoggerFactory.getLogger(StoreProcedureService.class);
    
    @Autowired
    private DataSource ds;
    
    private final IFmsRepository storeProcedureRepository;
    // private final MTTRCPTRepository mttrcptRepository;
    // private final RICPRepository ricpRepository;

    public FMSService(IFmsRepository storeProcedureRepository, MTTRCPTRepository mttrcptRepository,
            RICPRepository ricpRepository) {
        this.storeProcedureRepository = storeProcedureRepository;
        // this.mttrcptRepository=mttrcptRepository;
        // this.ricpRepository=ricpRepository;
    }

    // #region FMS start
    @Override
    public List<FMS> sp_getfms(FMSRequest fmsRequests) {
        List<FMS> result = new ArrayList<>();

        try {
            List<Object[]> objects = storeProcedureRepository.sp_getfms(fmsRequests);
            result = convertToFMSList(objects);
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<FMS> convertToFMSList(List<Object[]> objects) {
        List<FMS> fmsList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMS fms = new FMS();

            fms.setFmsId((Integer) obj[0]);
            fms.setFmsCd((String) obj[1]);

            // For Integer conversions
            fms.setCongLedCnt(obj[2] != null ? ((Number) obj[2]).intValue() : null);
            fms.setIsActive(obj[5] != null ? ((Number) obj[5]).intValue() : null);

            fms.setModifiedBy((String) obj[3]);
            fms.setMft_total((Integer) obj[6]);

            // Assuming that the date is stored as java.sql.Timestamp or java.sql.Date in
            // the database
            fms.setDtModified((Date) obj[4]);
            fms.setTotal((Integer) obj[7]);

            fmsList.add(fms);
        }
        return fmsList;
    }

    @Override
    public Integer sp_insfms(FMSRequest insertRequest, String i_created_by, String i_status, Integer i_is_active) {
        Integer result = 0;
        try {
            result = storeProcedureRepository.sp_insfms(insertRequest, i_created_by, i_status, i_is_active);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Integer sp_updfms(FMSRequest updateRequest, String i_modified_by) {
        Integer result = 0;
        try {
            result = storeProcedureRepository.sp_updfms(updateRequest, i_modified_by);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Integer sp_checkfmsbyid(FMSRequest fmsRequest) {
        Integer result = 0;
        try {
            result = storeProcedureRepository.sp_checkfmsbyid(fmsRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Integer sp_updfms_activation(FMSRequest fmsRequest, String i_modified_by) {
        Integer result = 0;
        try {
            result = storeProcedureRepository.sp_updfms_activation(fmsRequest, i_modified_by);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // #endregion

    // #region FMS Ledger start

    @Override
    public List<FMSLedger> sp_getfmsledger_v2(FMSLedgerRequest fmsLedgerRequest) {
        List<FMSLedger> result = Collections.emptyList();

        try {
            List<Object[]> objects = storeProcedureRepository.sp_getfmsledger_v2(fmsLedgerRequest);
            result = convertToFMSLedgerList(objects);
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<FMSLedger> convertToFMSLedgerList(List<Object[]> objects) {
        List<FMSLedger> fmsLedgerList = new ArrayList<>();
        for (Object[] obj : objects) {
            FMSLedger fmsLedger = new FMSLedger();
            // fmsLedger.setFms_ledger_id((BigInteger) obj[0]);
            fmsLedger.setFms_detail_id((String) obj[1]);
            fmsLedger.setFms_detail_nm_en((String) obj[2]);
            fmsLedger.setFms_ledger_cd((String) obj[0]);
            fmsLedger.setFms_cd((String) obj[4]);
            fmsLedger.setTotal((Integer) obj[6]);
            fmsLedger.setFound((String) obj[3]);
            fmsLedger.setMft_total((Integer) obj[5]);
            fmsLedgerList.add(fmsLedger);
        }
        return fmsLedgerList;
    }

    // @Override
    // public ArrayList<Integer> sp_insfmsledgerdoc(FMSLedgerDocRequest fmsLedgerDocRequest, String username)
    //         throws SerialException, SQLException {
    //     // Decode Base64 content
    //     ArrayList<Integer> resList = new ArrayList<>();

    //     byte[] decodedBytes = decodeBase64(fmsLedgerDocRequest.getI_file_content());
    //     Blob blob = new SerialBlob(decodedBytes);

    //     List<FMSLedgerDoc> fmsDocList = readExcelContentFms(decodedBytes);

    //     // Call the repository method
    //     for (FMSLedgerDoc fmsDoc : fmsDocList) {
    //         List<Integer> result = storeProcedureRepository.sp_uploadDoc(fmsLedgerDocRequest, blob, username, fmsDoc);
    //         if (result.size() >= 2) {
    //             resList.add(result.get(0));
    //             resList.add(result.get(1));
    //         }
    //     }
    //     return resList;
    // }

    @Override
    public List<Integer> sp_insfmsledgerdoc(FMSLedgerDocRequest fmsLedgerDocRequest, String username)
            throws SerialException, SQLException {
        List<Integer> resList = new ArrayList<>();

        // Decode Base64 content
        byte[] decodedBytes = decodeBase64(fmsLedgerDocRequest.getI_file_content());
        Blob blob = new SerialBlob(decodedBytes);

        // Extract content from the Excel file
        List<FMSLedgerDoc> fmsDocList = readExcelContentFms(decodedBytes);

        // Call repository method to first insert document and then insert ledgers
        resList = storeProcedureRepository.sp_uploadDoc(fmsLedgerDocRequest, blob, username, fmsDocList);

        return resList;
    }

    private byte[] decodeBase64(String base64String) {
        if (base64String.startsWith("data:")) {
            base64String = base64String.substring(base64String.indexOf(',') + 1);
        }
        base64String = base64String.replaceAll("\\s", "").replace(":", "");
        return Base64.getDecoder().decode(base64String);
    }

    // @Override
    // public ArrayList<Integer> sp_insfmsledgerdoc(FMSLedgerDocRequest fmsLedgerDocRequest, String username) {

    //     Integer result = 0;
    //     Integer result2 = 0;
    //     ArrayList<Integer> resList = new ArrayList<>();

    //     try (Connection connection = ds.getConnection()) {

    //         String base64String = fmsLedgerDocRequest.getI_file_content();

    //         if (base64String.startsWith("data:")) {
    //             base64String = base64String.substring(base64String.indexOf(',') + 1);
    //         }
    //         base64String = base64String.replaceAll("\\s", "").replace(":", "");

    //         byte[] decodedBytes = Base64.getDecoder().decode(base64String);

    //         System.out.println("Decoded bytes length: " + decodedBytes.length);
    //         Blob blob = new SerialBlob(decodedBytes);

         
    //         // Informix-specific BLOB handling   
    //         /*
    //         IfxLobDescriptor loDesc = new IfxLobDescriptor(connection); // Replace 'connection' with your actual database connection
    //         IfxLocator loPtr = new IfxLocator();
    //         IfxSmartBlob smb = new IfxSmartBlob(connection);

    //         // Create the smart large object on the server and write your data
    //         int loFd = smb.IfxLoCreate(loDesc, smb.LO_RDWR, loPtr);
    //         smb.IfxLoWrite(loFd, decodedBytes);
    //         smb.IfxLoClose(loFd);
	// 		*/
            
    //         // Convert IfxLocator to IfxBblob for compatibility with JDBC Blob
    //         // IfxBblob informixBlob = new IfxBblob(loPtr);

    //         List<FMSLedgerDoc> fmsDocList = readExcelContentFms(decodedBytes);

    //         String sql = "CALL sp_insfmsledgerdoc(?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";
    //         try (PreparedStatement statement = connection.prepareStatement(sql)) {
    //             for (FMSLedgerDoc fmsDoc : fmsDocList) {
    //                 // Set parameters
    //                 statement.setInt(1, fmsLedgerDocRequest.getI_fms_id());
    //                 statement.setString(2, fmsLedgerDocRequest.getI_file_nm());
    //                 statement.setBlob(3, blob);
    //                 statement.setString(4, fmsLedgerDocRequest.getI_file_type());
    //                 statement.setInt(5, fmsLedgerDocRequest.getI_file_size());
    //                 statement.setString(6, username);
    //                 statement.setString(7, username);
    //                 statement.setString(8, fmsLedgerDocRequest.getI_status());
    //                 statement.setString(9, fmsDoc.getI_fee_detail_id());
    //                 statement.setString(10, fmsDoc.getI_fee_detail_nm_en());
    //                 statement.setString(11, fmsDoc.getI_fms_ledger_cd());
    //                 boolean isResultSet = statement.execute();
    //                 if (isResultSet) {
    //                     try (ResultSet rs = statement.getResultSet()) {
    //                         // Process the results
    //                         if (rs.next()) {
    //                             result = rs.getInt(1);
    //                             result2 = rs.getInt(2);// Assuming the result is an integer
    //                             // Handle the result for each PGReconDoc in the list
    //                         }
    //                     }
    //                 }
    //             }
    //         }

    //         resList.add(result);
    //         resList.add(result2);

    //     } catch (NumberFormatException e) {
    //         log.error("Exception in " + this.getClass().toString(), e); // Log the error or handle it as needed
    //     } catch (Exception e) {
    //         log.error("Exception in " + this.getClass().toString(), e); // Log the error or handle it as needed
    //     }
    //     return resList;
    // }

    private static List<FMSLedgerDoc> readExcelContentFms(byte[] excelData) {

        List<FMSLedgerDoc> dataList = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(new ByteArrayInputStream(excelData))) {
            // Iterate through sheets
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);

                int headerRowIndex = 0;

                for (int rowIndex = headerRowIndex + 1; rowIndex < sheet.getPhysicalNumberOfRows(); rowIndex++) {
                    Row row = sheet.getRow(rowIndex);

                    if (row != null) {
                        // Assuming the sheet has at least 3 columns, adjust accordingly if needed
                        if (row.getLastCellNum() >= 3) {
                            Cell cell1 = row.getCell(0);
                            Cell cell2 = row.getCell(1);
                            Cell cell3 = row.getCell(2);

                            FMSLedgerDoc fmsLedgerDoc = new FMSLedgerDoc();
                            fmsLedgerDoc.setI_fee_detail_id(cell1.toString());
                            fmsLedgerDoc.setI_fee_detail_nm_en(cell2.toString());
                            fmsLedgerDoc.setI_fms_ledger_cd(cell3.toString());
                            dataList.add(fmsLedgerDoc);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return dataList;
    }

    public List<FMSLedgerDocWithoutFile> sp_getfmsdoc(FMSLedgerDocRequest fmsLedgerDocRequest) {

        List<FMSLedgerDocWithoutFile> result = Collections.emptyList();

        try {

            List<Object[]> objects = storeProcedureRepository.sp_getfmsdoc(fmsLedgerDocRequest);

            result = convertToGetFmsDoc(objects);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<FMSLedgerDocWithoutFile> convertToGetFmsDoc(List<Object[]> objects) {
        List<FMSLedgerDocWithoutFile> fmsDocList = new ArrayList<>();

        for (Object[] obj : objects) {
            FMSLedgerDocWithoutFile fmsDoc = new FMSLedgerDocWithoutFile();

            fmsDoc.setFile_nm((String) obj[0]);

            // Blob blob = (Blob) obj[1];

            // try {
            //     // Convert Blob to byte array
            //     byte[] bytes = blob.getBytes(1, (int) blob.length());

            //     // Convert byte array to Base64-encoded string
            //     String base64Content = Base64.getEncoder().encodeToString(bytes);

            //     fmsDoc.setFile_content(base64Content);
            // } catch (SQLException e) {
            //     e.printStackTrace();
            //     fmsDoc.setFile_content(null);
            // }
            fmsDoc.setFile_type((String) obj[1]);
            fmsDoc.setFile_size_kb((Integer) obj[2]);
            fmsDoc.setDt_created((Date) obj[3]);
            fmsDoc.setDt_modified((Date) obj[4]);
            fmsDoc.setCreated_by((String) obj[5]);
            fmsDoc.setModified_by((String) obj[6]);
            fmsDoc.setTotal((Integer) obj[7]);

            fmsDocList.add(fmsDoc);
        }
        return fmsDocList;
    }

    @Override
    public Integer sp_checkdocexist(FMSLedgerDocRequest fmsLedgerDocRequest) {
        Integer result = 0;
        try {
            result = storeProcedureRepository.sp_checkdocexist(fmsLedgerDocRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String sp_getfmsfilecontent(FMSLedgerDocRequest fmsLedgerDocRequest) {

        String result = "";

        try {

            Blob blob = (Blob) storeProcedureRepository.sp_getfmsfilecontent(fmsLedgerDocRequest);

            try {
                // Convert Blob to byte array
                byte[] bytes = blob.getBytes(1, (int) blob.length());

                // Convert byte array to Base64-encoded string
                String base64Content = Base64.getEncoder().encodeToString(bytes);
                result = base64Content;

            } catch (SQLException e) {
                e.printStackTrace();
                result = "";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }



    // private List<FMSDoc> convertToGetFmsDoccontent(List<Object[]> objects) {
    //     List<FMSDoc> fmsDocList = new ArrayList<>();

    //     for (Object[] obj : objects) {
    //         FMSDoc fmsDoc = new FMSDoc();

    //         Blob blob = (Blob) obj[0];

            // try {
            //     // Convert Blob to byte array
            //     byte[] bytes = blob.getBytes(1, (int) blob.length());

            //     // Convert byte array to Base64-encoded string
            //     String base64Content = Base64.getEncoder().encodeToString(bytes);

            //     fmsDoc.setFile_content(base64Content);
            // } catch (SQLException e) {
            //     e.printStackTrace();
            //     fmsDoc.setFile_content(null);
            // }

    //         fmsDocList.add(fmsDoc);
    //     }
    //     return fmsDocList;
    // }

    @Override
    public ArrayList<Integer> sp_getfmsledgersummarycount(FMSLedgerDocRequest fmsLedgerDocRequest){
        // Decode Base64 content
        ArrayList<Integer> resList = new ArrayList<>();

        List<Integer> result = storeProcedureRepository.sp_getfmsledgersummarycount(fmsLedgerDocRequest);
        if (result.size() == 2) {
            resList.add(result.get(0));
            resList.add(result.get(1));
        }
        return resList;
    }
}
