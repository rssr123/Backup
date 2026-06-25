package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
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

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.informix.jdbc.IfxLobDescriptor;
import com.informix.jdbc.IfxLocator;
import com.informix.jdbc.IfxSmartBlob;
import com.maven.rms.interfaces.IMFTWFService;
import com.maven.rms.models.MFTWF;
import com.maven.rms.models.MFTWFDoc;
import com.maven.rms.models.MFTWFDocRequest;
import com.maven.rms.models.MFTWFHistory;
import com.maven.rms.models.MFTWFHistoryRequest;
import com.maven.rms.models.MFTWFRequest;
import com.maven.rms.models.PGRecon;
import com.maven.rms.repositories.MFTWFRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MFTWFService implements IMFTWFService {
    
    // private static final Logger logger = LoggerFactory.getLogger(MTTService.class);
    private final MFTWFRepository mftwfRepository;
   // private final NotificationHandler notificationHandler;

    @Autowired
    private DataSource ds;


    public MFTWFService(MFTWFRepository mftwfRepository) {
        this.mftwfRepository = mftwfRepository;
       
    }

    @Override
    public Integer sp_uploadDoc(MFTWFDocRequest mftwfDocRequest) throws SerialException, SQLException {
        // Decode Base64 content
        byte[] decodedBytes = decodeBase64(mftwfDocRequest.getI_file_content());
        Blob blob = new SerialBlob(decodedBytes);

        // Call the repository method
        Integer result = mftwfRepository.sp_uploadDoc(mftwfDocRequest, blob);
        return result; 
    }

        private byte[] decodeBase64(String base64String) {
            if (base64String.startsWith("data:")) {
                base64String = base64String.substring(base64String.indexOf(',') + 1);
            }
            base64String = base64String.replaceAll("\\s", "").replace(":", "");
            return Base64.getDecoder().decode(base64String);
        }


	// @Override
    // public Integer sp_uploadDoc(MFTWFDocRequest mftwfDocRequest) {
    // // public Integer sp_uploadDoc(MFTWFDocRequest mftwfDocRequest,String username) {

	// 	Integer result = 0;

    //     try (Connection connection = ds.getConnection()) {

	// 		String base64String = mftwfDocRequest.getI_file_content();

    //         if (base64String.startsWith("data:")) {
    //             base64String = base64String.substring(base64String.indexOf(',') + 1);
    //         }
    //         base64String = base64String.replaceAll("\\s", "").replace(":", "");

	// 		byte[] decodedBytes = Base64.getDecoder().decode(base64String);

    //         System.out.println("Decoded bytes length: " + decodedBytes.length);
    //         Blob blob = new SerialBlob(decodedBytes);
            
	// 		// Informix-specific BLOB handling
    //         /*
    //         IfxLobDescriptor loDesc = new IfxLobDescriptor(connection); // Replace 'connection' with your actual
    //                                                                     // database connection
    //         IfxLocator loPtr = new IfxLocator();
    //         IfxSmartBlob smb = new IfxSmartBlob(connection);

    //         // Create the smart large object on the server and write your data
    //         int loFd = smb.IfxLoCreate(loDesc, smb.LO_RDWR, loPtr);
    //         smb.IfxLoWrite(loFd, decodedBytes);
    //         smb.IfxLoClose(loFd);
    //          */
	// 		// Convert IfxLocator to IfxBblob for compatibility with JDBC Blob
    //         //IfxBblob informixBlob = new IfxBblob(loPtr);
           

    //         String sql = "CALL sp_insmftwfdoc(?, ?, ?, ?, ?, ?, ?, ?)";
    //         try (PreparedStatement statement = connection.prepareStatement(sql)) {
    //             // Set parameters
    //             statement.setObject(1, mftwfDocRequest.getI_wf_id().intValue());
    //             statement.setString(2, mftwfDocRequest.getI_file_nm());
    //             // For IfxBblob, handle it using the specific methods provided by Informix JDBC
    //             // driver
    //             // ...
    //             statement.setBlob(3, blob); // Set the IfxBblob here
    //             statement.setString(4, mftwfDocRequest.getI_file_type());
    //             statement.setInt(5, mftwfDocRequest.getI_file_size_kb());
    //             // statement.setString(6, username);
    //             // statement.setString(7, username);
    //             statement.setString(6, mftwfDocRequest.getI_created_by());
    //             statement.setString(7, mftwfDocRequest.getI_modified_by());
    //             statement.setString(8, mftwfDocRequest.getI_status());

    //             // Execute the query
    //             boolean isResultSet = statement.execute();
    //             if (isResultSet) {
    //                 try (ResultSet rs = statement.getResultSet()) {
    //                     // Process the results
    //                     if (rs.next()) {
    //                         result = rs.getInt(1); // Assuming the result is an integer
    //                         // Handle the result
    //                     }
    //                 }
    //             }
    //         }
			
    // 	}catch (NumberFormatException e) {
	// 		log.error("Exception in " + this.getClass().toString(), e); // Log the error or handle it as needed
	// 	} catch (Exception e) {
	// 		log.error("Exception in " + this.getClass().toString(), e); // Log the error or handle it as needed
	// 	}
	// 	return result;
	// }

     @Override
    public Integer sp_updmftwf_status(MFTWFRequest mftwfRequest) {

        Integer result = 0;

            result = mftwfRepository.sp_updmftwf_status(mftwfRequest);

        return result;
    }

    // public Integer sp_updmftwfStatus(BigInteger i_wf_id, String i_status) {
    public Integer sp_updmftwfStatus(MFTWFRequest mftwfRequest) {
        Integer result = 0;

            // result = storeProcedureRepository.sp_updateMFTWFStatus(i_wf_id, i_status);
            result = mftwfRepository.sp_updateMFTWFStatus(mftwfRequest);

        return result;
    }

    @Override
    public List<MFTWF> sp_getmftwf(MFTWFRequest mftwfRequest) {

        List<MFTWF> result = Collections.emptyList();

            List<Object[]> objects = mftwfRepository.sp_getmftwf(mftwfRequest);

            result = convertToGetMFTWF(objects);

        return result;
    }

    public List<MFTWF> sp_getmftwfByStatusAndEffDate(String status) {
        // try {
            return convertToGetMFTWFByStatusAndEffDate(mftwfRepository.sp_getMFTWFByStatusAndEffDate(status));
        // } catch (Exception e) {
        //     log.error("Exception in " + this.getClass().toString(), e);
        // }
        // return Collections.emptyList();
    }

    private List<MFTWF> convertToGetMFTWFByStatusAndEffDate(List<Object[]> objects) {
        List<MFTWF> mftwfList = new ArrayList<>();

        for (Object[] obj : objects) {
            MFTWF mftwf = new MFTWF();

            mftwf.setWf_id((BigInteger) obj[0]);
            mftwf.setFee_detail_pk((Integer) obj[1]);
            mftwf.setFee_detail_id((String) obj[2]);
            mftwf.setFee_grp_id((Integer) obj[3]);
            mftwf.setFee_detail_nm_e((String) obj[4]);
            mftwf.setFee_detail_nm_b((String) obj[5]);
            mftwf.setFee_amt((BigDecimal) obj[6]);
            mftwf.setPromo_startdt((Date) obj[7]);
            mftwf.setPromo_enddt((Date) obj[8]);
            mftwf.setPromo_fee((BigDecimal) obj[9]);
            mftwf.setTax_cd_id((Integer) obj[10]);
            mftwf.setAllow_otc((Integer) obj[11]);
            mftwf.setLl_parent_id((String) obj[12]);
            mftwf.setLl_start_day((Integer) obj[13]);
            mftwf.setLl_start_mth((Integer) obj[14]);
            mftwf.setLl_end_day((Integer) obj[15]);
            mftwf.setLl_end_mth((Integer) obj[16]);
            mftwf.setLedger_cd((String) obj[17]);
            mftwf.setSs_cd((String) obj[18]);
            mftwf.setEffective_date((Date) obj[19]);
            mftwf.setDt_created((Date) obj[20]);
            mftwf.setDt_modified((Date) obj[21]);
            mftwf.setCreated_by((String) obj[22]);
            mftwf.setModified_by((String) obj[23]);
            mftwf.setStatus((String) obj[24]);
            mftwf.setAssign_to((String) obj[25]);
            mftwf.setAction((String) obj[26]);
            mftwf.setMft_status((String) obj[27]);
            mftwf.setIs_pub((Integer) obj[28]);

            mftwfList.add(mftwf);
        }
        return mftwfList;
    }

    private List<MFTWF> convertToGetMFTWF(List<Object[]> objects) {
        List<MFTWF> mftwfList = new ArrayList<>();

        for (Object[] obj : objects) {
            MFTWF mftwf = new MFTWF();

            mftwf.setWf_id((BigInteger) obj[0]);
            mftwf.setFee_detail_pk((Integer) obj[1]);
            mftwf.setFee_detail_id((String) obj[2]);
            mftwf.setFee_grp_id((Integer) obj[3]);
            mftwf.setFee_grp_nm_en((String) obj[4]);
            mftwf.setFee_grp_nm_bm((String) obj[5]);
            mftwf.setFee_detail_nm_e((String) obj[6]);
            mftwf.setFee_detail_nm_b((String) obj[7]);
            mftwf.setFee_amt((BigDecimal) obj[8]);
            mftwf.setPromo_startdt((Date) obj[9]);
            mftwf.setPromo_enddt((Date) obj[10]);
            mftwf.setPromo_fee((BigDecimal) obj[11]);
            mftwf.setTax_cd_id((Integer) obj[12]);
            mftwf.setTax_cd((String) obj[13]);
            mftwf.setAllow_otc((Integer) obj[14]);
            mftwf.setLl_parent_id((String) obj[15]);
            mftwf.setLl_start_day((Integer) obj[16]);
            mftwf.setLl_end_day((Integer) obj[17]);
            mftwf.setLl_start_mth((Integer) obj[18]);
            mftwf.setLl_end_mth((Integer) obj[19]);
            mftwf.setLedger_cd((String) obj[20]);
            mftwf.setSs_cd((String) obj[21]);
            mftwf.setSs_nm((String) obj[22]);
            mftwf.setEffective_date((Date) obj[23]);
            mftwf.setDt_created((Date) obj[24]);
            mftwf.setDt_modified((Date) obj[25]);
            mftwf.setCreated_by((String) obj[26]);
            mftwf.setCreated_by_nm((String) obj[27]);
            mftwf.setModified_by((String) obj[28]);
            mftwf.setModified_by_nm((String) obj[29]);
            mftwf.setStatus((String) obj[30]);
            mftwf.setStatus_en((String) obj[31]);
            mftwf.setStatus_bm((String) obj[32]);
            mftwf.setAssign_to((String) obj[33]);
            mftwf.setAssign_to_nm((String) obj[34]);
            mftwf.setAction((String) obj[35]);
            mftwf.setR_fee_det_nm((String) obj[36]);
            mftwf.setR_fee_amt((BigDecimal) obj[37]);
            mftwf.setR_ss_cd((String) obj[38]);
            mftwf.setR_promo_startdt((Date) obj[39]);
            mftwf.setR_promo_enddt((Date) obj[40]);
            mftwf.setR_ll_required((Integer) obj[41]);
            mftwf.setR_add_notes((String) obj[42]);
            mftwf.setMft_status((String) obj[43]);
            mftwf.setR_promo_fee((BigDecimal) obj[44]);
            mftwf.setTask_id((String) obj[45]);
            mftwf.setIs_pub((Integer) obj[46]);
            mftwf.setTotal((Integer) obj[47]);

            mftwfList.add(mftwf);
        }
        return mftwfList;
    }

    @Override
    public BigInteger sp_insmftwf(MFTWFRequest mftwfRequest) {

        BigInteger result = BigInteger.ZERO;

            result = mftwfRepository.sp_insmftwf(mftwfRequest);

        return result;
    }

    @Override
    public List<MFTWFHistory> sp_getmftwfhis(MFTWFHistoryRequest mftwfHistoryRequest) {
        List<MFTWFHistory> result = Collections.emptyList();

            List<Object[]> objects = mftwfRepository.sp_getmftwfhis(mftwfHistoryRequest);
            result = convertGetmftwfhis(objects);

        return result;
    }

    private List<MFTWFHistory> convertGetmftwfhis(List<Object[]> objects) {
        List<MFTWFHistory> MFTWFHistoryList = new ArrayList<>();

        for (Object[] obj : objects) {

            MFTWFHistory mftwfHist = new MFTWFHistory();

            mftwfHist.setAction((String) obj[0]);
            mftwfHist.setDt_activity((Date) obj[1]);
            mftwfHist.setAct_by((String) obj[2]);
            mftwfHist.setAssign_to((String) obj[3]);
            mftwfHist.setRemark((String) obj[4]);
            mftwfHist.setDt_created((Date) obj[5]);
            mftwfHist.setDt_modified((Date) obj[6]);
            mftwfHist.setCreated_by((String) obj[7]);
            mftwfHist.setModified_by((String) obj[8]);
            mftwfHist.setStatus_en((String) obj[9]);
            mftwfHist.setStatus_bm((String) obj[10]);
            mftwfHist.setAssign_to_nm((String) obj[11]);
            mftwfHist.setAct_by_nm((String) obj[12]);
            mftwfHist.setTotal((Integer) obj[13]);

            MFTWFHistoryList.add(mftwfHist);
        }
        return MFTWFHistoryList;
    }

    @Override
    public List<MFTWFHistory> sp_getwfh_ast(MFTWFHistoryRequest mftwfHistoryRequest) {
        List<MFTWFHistory> result = Collections.emptyList();


            List<Object[]> objects = mftwfRepository.sp_getwfh_ast(mftwfHistoryRequest);
            result = convertGetmftwfhisAst(objects);

        return result;
    }

    private List<MFTWFHistory> convertGetmftwfhisAst(List<Object[]> objects) {
        List<MFTWFHistory> MFTWFHistoryAstList = new ArrayList<>();

        for (Object[] obj : objects) {

            MFTWFHistory mftwfHistAst = new MFTWFHistory();

            mftwfHistAst.setAssign_to((String) obj[0]);
            mftwfHistAst.setAssign_to_nm((String) obj[1]);

            MFTWFHistoryAstList.add(mftwfHistAst);
        }
        return MFTWFHistoryAstList;
    }

    @Override
    public Integer sp_updmftwf(MFTWFRequest mftwfRequest) {

        Integer result = 0;

            result = mftwfRepository.sp_updmftwf(mftwfRequest);

        return result;
    }

    // @Override
    // public Integer sp_insmftwfdoc(BigInteger i_wf_id, String i_file_nm, Blob i_file_content, String i_file_type,
    //         Integer i_file_size,
    //         String i_created_by, String i_modified_by, String i_status) {

    //     Integer result = 0;

    //     try {

    //         result = storeProcedureRepository.sp_insmftwfdoc(i_wf_id, i_file_nm, i_file_content, i_file_type,
    //                 i_file_size, i_created_by,
    //                 i_modified_by, i_status);

    //     } catch (Exception e) {

    //         e.printStackTrace();

    //     } finally {

    //     }

    //     return result;
    // }

    @Override
    public List<MFTWFDoc> sp_getmftwfdoc(MFTWFDocRequest mftwfDocRequest) {

        List<MFTWFDoc> result = Collections.emptyList();

            List<Object[]> objects = mftwfRepository.sp_getmftwfdoc(mftwfDocRequest);

            result = convertToGetMFTWDoc(objects);

        return result;
    }

    private List<MFTWFDoc> convertToGetMFTWDoc(List<Object[]> objects) {
        List<MFTWFDoc> mftwfDocList = new ArrayList<>();

        for (Object[] obj : objects) {
            MFTWFDoc mftwfDoc = new MFTWFDoc();

            mftwfDoc.setWfdoc_id((BigInteger) obj[0]);
            mftwfDoc.setFile_nm((String) obj[1]);

            /*
             * Blob blob = (Blob) obj[1];
             * 
             * try {
             * // Convert Blob to byte array
             * byte[] bytes = blob.getBytes(1, (int) blob.length());
             * 
             * // Convert byte array to Base64-encoded string
             * String base64Content = Base64.getEncoder().encodeToString(bytes);
             * 
             * mftwfDoc.setFile_content(base64Content);
             * } catch (SQLException e) {
             * e.printStackTrace();
             * mftwfDoc.setFile_content(null);
             * }
             */
            mftwfDoc.setFile_type((String) obj[2]);
            mftwfDoc.setFile_size_kb((Integer) obj[3]);
            mftwfDoc.setDt_created((Date) obj[4]);
            mftwfDoc.setDt_modified((Date) obj[5]);
            mftwfDoc.setCreated_by((String) obj[6]);
            mftwfDoc.setModified_by((String) obj[7]);
            mftwfDoc.setTotal((Integer) obj[8]);

            mftwfDocList.add(mftwfDoc);
        }
        return mftwfDocList;
    }

    @Override
    public List<MFTWFHistory> sp_getwfh_status(MFTWFHistoryRequest mftwfHistoryRequest) {
        List<MFTWFHistory> result = Collections.emptyList();

            List<Object[]> objects = mftwfRepository.sp_getwfh_status(mftwfHistoryRequest);
            result = convertGetmftwfhisStatus(objects);

        return result;
    }

    private List<MFTWFHistory> convertGetmftwfhisStatus(List<Object[]> objects) {
        List<MFTWFHistory> MFTWFHistoryStatusList = new ArrayList<>();

        for (Object[] obj : objects) {

            MFTWFHistory mftwfHistStatus = new MFTWFHistory();

            mftwfHistStatus.setAssign_to((String) obj[0]);
            mftwfHistStatus.setAssign_to_nm((String) obj[1]);
            mftwfHistStatus.setStatus((String) obj[2]);

            MFTWFHistoryStatusList.add(mftwfHistStatus);
        }
        return MFTWFHistoryStatusList;
    }

     @Override
    public String sp_getmftwfdocfilecontent(MFTWFDocRequest mftwfDocRequest) throws SQLException {

        String result = "";

   

            Blob blob = (Blob) mftwfRepository.sp_getmftwfdocfilecontent(mftwfDocRequest);

   
                // Convert Blob to byte array
                byte[] bytes = blob.getBytes(1, (int) blob.length());

                // Convert byte array to Base64-encoded string
                String base64Content = Base64.getEncoder().encodeToString(bytes);
                result = base64Content;


        return result;
    }

   // @Override
    public Integer sp_getmytaskactivetaskcount(MFTWFRequest mftwfRequest) {

        Integer result = 0;

            result = mftwfRepository.sp_getmytaskactivetaskcount(mftwfRequest);

      //  notificationSvc.sendNotificationUpdate(5, 10);

        return result;
    }


    public Integer sp_getcreatedtaskactivetaskcount(MFTWFRequest mftwfRequest) {

        Integer result = 0;

            result = mftwfRepository.sp_getcreatedtaskactivetaskcount(mftwfRequest);

      //  notificationSvc.sendNotificationUpdate(5, 10);

        return result;
    }

    @Override
    public List<MFTWF> sp_checkmftwfexist(MFTWFRequest mftwfRequest) {

        List<MFTWF> result = Collections.emptyList();

            List<Object[]> objects = mftwfRepository.sp_checkmftwfexist(mftwfRequest);

            result = convertToGetMFTWF(objects);

        return result;
    }

    
    @Override
    public Integer sp_removemftwf(BigInteger wf_id) {

        Integer result = 0;

            result = mftwfRepository.sp_removemftwf(wf_id);

      //  notificationSvc.sendNotificationUpdate(5, 10);

        return result;
    }


}


