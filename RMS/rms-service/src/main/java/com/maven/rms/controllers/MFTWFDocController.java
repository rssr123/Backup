package com.maven.rms.controllers;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Base64Utils;

import com.maven.rms.models.FMSLedgerDocRequest;
import com.maven.rms.models.MFT;
import com.maven.rms.models.MFTWFDoc;
import com.maven.rms.models.MFTWFDocRequest;
import com.maven.rms.models.MFTWFHistory;
import com.maven.rms.models.MFTWFHistoryRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.MFTWFService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.RMSLogger;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.maven.rms.utils.ControllersEnum;



@RestController
@RequestMapping("/api/mftwfdoc/v1")
public class MFTWFDocController {
    
    @Autowired
    private AuthService authService;

    @Autowired
    private MFTWFService mftwfService;

    private final DataSource dataSource;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public MFTWFDocController(DataSource dataSource,MFTWFService mftwfService) {
        this.dataSource = dataSource;
        this.mftwfService = mftwfService;
    }


 // @Secured("ROLE_USER")
    @PostMapping(value = "/addmasterfeetableworkflowdocument")
    public ResponseEntity<ApiResponse<Integer>> sp_insmftwfdoc(HttpServletRequest request,
            @RequestBody MFTWFDocRequest mftwfDocRequest) throws SerialException, SQLException {

        // try (Connection connection = DatabaseUtil.getManualConnection()) {
        //     if (!authService.isAuthenticated(request)) {
        //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        //     }

        //     // byte[] base64Bytes = mftwfDocRequest.getI_file_content();
        //     // String base64String = new String(base64Bytes, StandardCharsets.UTF_8);
        //     String base64String = mftwfDocRequest.getI_file_content();

        //     if (base64String.startsWith("data:")) {
        //         base64String = base64String.substring(base64String.indexOf(',') + 1);
        //     }
        //     base64String = base64String.replaceAll("\\s", "").replace(":", "");

        //     byte[] decodedBytes = Base64.getDecoder().decode(base64String);

        //     System.out.println("Decoded bytes length: " + decodedBytes.length);
        //     Blob blob = new SerialBlob(decodedBytes);

        //     // Informix-specific BLOB handling
        //     IfxLobDescriptor loDesc = new IfxLobDescriptor(connection); // Replace 'connection' with your actual
        //                                                                 // database connection
        //     IfxLocator loPtr = new IfxLocator();
        //     IfxSmartBlob smb = new IfxSmartBlob(connection);

        //     // Create the smart large object on the server and write your data
        //     int loFd = smb.IfxLoCreate(loDesc, smb.LO_RDWR, loPtr);
        //     smb.IfxLoWrite(loFd, decodedBytes);
        //     smb.IfxLoClose(loFd);

        //     // Convert IfxLocator to IfxBblob for compatibility with JDBC Blob
        //     //IfxBblob informixBlob = new IfxBblob(loPtr);
        //     Integer result = 0;

        //     String sql = "CALL sp_insmftwfdoc(?, ?, ?, ?, ?, ?, ?, ?)";
        //     try (PreparedStatement statement = connection.prepareStatement(sql)) {
        //         // Set parameters
        //         statement.setObject(1, mftwfDocRequest.getI_wf_id().intValue());
        //         statement.setString(2, mftwfDocRequest.getI_file_nm());
        //         // For IfxBblob, handle it using the specific methods provided by Informix JDBC
        //         // driver
        //         // ...
        //         statement.setBlob(3, blob); // Set the IfxBblob here
        //         statement.setString(4, mftwfDocRequest.getI_file_type());
        //         statement.setInt(5, mftwfDocRequest.getI_file_size());
        //         statement.setString(6, authService.getLoginUserName());
        //         statement.setString(7, authService.getLoginUserName());
        //         statement.setString(8, mftwfDocRequest.getI_status());

        //         // Execute the query
        //         boolean isResultSet = statement.execute();
        //         if (isResultSet) {
        //             try (ResultSet rs = statement.getResultSet()) {
        //                 // Process the results
        //                 if (rs.next()) {
        //                     result = rs.getInt(1); // Assuming the result is an integer
        //                     // Handle the result
        //                 }
        //             }
        //         }
        //     }
        //     // Integer result = spService.sp_insmftwfdoc(
        //     // mftwfDocRequest.getI_wf_id(),
        //     // mftwfDocRequest.getI_file_nm(),
        //     // informixBlob,
        //     // mftwfDocRequest.getI_file_type(),
        //     // mftwfDocRequest.getI_file_size(),
        //     // authService.getLoginUserName(), // createdby
        //     // authService.getLoginUserName(), // modifiedby
        //     // mftwfDocRequest.getI_status());

        //     if (result <= 0) {
        //         return APIResponse.InternalServerError();
        //     }

        //     return APIResponse.SuccessResponse(result);

        // } catch (SQLException e) {
        //     RMSLogger.error(e.getMessage());
        //     return APIResponse.InternalServerError();
        // } catch (NumberFormatException e) {
        //     RMSLogger.error(e.getMessage());
        //     return APIResponse.InvalidFormat();

        // } catch (Exception e) {
        //     RMSLogger.error(e.getMessage());
        //     return APIResponse.InternalServerError();

        // }

        if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

        // try{
            // String base64String = mftwfDocRequest.getI_file_content();

            // if (base64String.startsWith("data:")) {
            //     base64String = base64String.substring(base64String.indexOf(',') + 1);
            // }
            // base64String = base64String.replaceAll("\\s", "").replace(":", "");

            // byte[] decodedBytes = Base64.getDecoder().decode(base64String);

            // System.out.println("Decoded bytes length: " + decodedBytes.length);
            // Blob blob = new SerialBlob(decodedBytes);

            // // Informix-specific BLOB handling
            // IfxLobDescriptor loDesc = new IfxLobDescriptor(connection); // Replace 'connection' with your actual
            //                                                             // database connection
            // IfxLocator loPtr = new IfxLocator();
            // IfxSmartBlob smb = new IfxSmartBlob(connection);

            // // Create the smart large object on the server and write your data
            // int loFd = smb.IfxLoCreate(loDesc, smb.LO_RDWR, loPtr);
            // smb.IfxLoWrite(loFd, decodedBytes);
            // smb.IfxLoClose(loFd);

            // Convert IfxLocator to IfxBblob for compatibility with JDBC Blob
            //IfxBblob informixBlob = new IfxBblob(loPtr);
            Integer result = 0;
            mftwfDocRequest.setI_created_by(authService.getLoginUserName());
            mftwfDocRequest.setI_modified_by(authService.getLoginUserName());

            // result = mftwfService.sp_uploadDoc(mftwfDocRequest, authService.getLoginUserName());
            result = mftwfService.sp_uploadDoc(mftwfDocRequest);

            // String sql = "CALL sp_insmftwfdoc(?, ?, ?, ?, ?, ?, ?, ?)";
            // try (PreparedStatement statement = connection.prepareStatement(sql)) {
            //     // Set parameters
            //     statement.setObject(1, mftwfDocRequest.getI_wf_id().intValue());
            //     statement.setString(2, mftwfDocRequest.getI_file_nm());
            //     // For IfxBblob, handle it using the specific methods provided by Informix JDBC
            //     // driver
            //     // ...
            //     statement.setBlob(3, blob); // Set the IfxBblob here
            //     statement.setString(4, mftwfDocRequest.getI_file_type());
            //     statement.setInt(5, mftwfDocRequest.getI_file_size());
            //     statement.setString(6, authService.getLoginUserName());
            //     statement.setString(7, authService.getLoginUserName());
            //     statement.setString(8, mftwfDocRequest.getI_status());

            //     // Execute the query
            //     boolean isResultSet = statement.execute();
            //     if (isResultSet) {
            //         try (ResultSet rs = statement.getResultSet()) {
            //             // Process the results
            //             if (rs.next()) {
            //                 result = rs.getInt(1); // Assuming the result is an integer
            //                 // Handle the result
            //             }
            //         }
            //     }
            // }
            // Integer result = spService.sp_insmftwfdoc(
            // mftwfDocRequest.getI_wf_id(),
            // mftwfDocRequest.getI_file_nm(),
            // informixBlob,
            // mftwfDocRequest.getI_file_type(),
            // mftwfDocRequest.getI_file_size(),
            // authService.getLoginUserName(), // createdby
            // authService.getLoginUserName(), // modifiedby
            // mftwfDocRequest.getI_status());

            if (result <= 0) {
                mftwfService.sp_removemftwf(mftwfDocRequest.getI_wf_id());
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);

        // } catch (SQLException e) {
        //     RMSLogger.error(e.getMessage());
        //     return APIResponse.InternalServerError();
        // } 
    //     }catch (NumberFormatException e) {
    //         RMSLogger.error(e.getMessage());
    //         return APIResponse.InvalidFormat();

    //     } catch (Exception e) {
    //         RMSLogger.error(e.getMessage());
    //         return APIResponse.InternalServerError();

    //     }
     }


// @Secured("ROLE_USER")
    @PostMapping(value = "/getmasterfeetableworkflowdocument")
    public ResponseEntity<ApiResponse<List<MFTWFDoc>>> sp_getmftwfdoc(HttpServletRequest request, @RequestBody  MFTWFDocRequest mftwfDocRequest) {



  List< MFTWFDoc> result = Collections.emptyList();


            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            
            // result = spService.sp_getmftwfdoc(
            //     mftwfDocRequest.getI_page(),
            //     mftwfDocRequest.getI_size(),
            //     mftwfDocRequest.getI_wf_id(),
            //     mftwfDocRequest.getI_status()
            // );

            result = mftwfService.sp_getmftwfdoc(mftwfDocRequest);

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.MFT_WF_DOC_CONTROLLER);
            }

            return APIResponse.SuccessResponse(result);

    }

    @PostMapping(value = "/getmftwfdocfilecontent")
    public ResponseEntity<ApiResponse<String>> sp_getmftwfdocfilecontent(HttpServletRequest request,
            @RequestBody MFTWFDocRequest mftwfDocRequest) throws SQLException {

        String result = "";

     
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // result = spService.sp_getmftwfdocfilecontent(
            //     mftwfDocRequest.getI_wfdoc_id());

            result = mftwfService.sp_getmftwfdocfilecontent(mftwfDocRequest);

            if (result.isEmpty()) {
                return APIResponse.NoDataFound(ControllersEnum.MFT_WF_DOC_CONTROLLER);
            }
            return APIResponse.SuccessResponse(result);


    }



}
