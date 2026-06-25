package com.maven.rms.interfaces;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

//import com.lowagie.text.pdf.codec.Base64.InputStream;
import com.maven.rms.models.FMSLedgerDoc;
import com.maven.rms.models.FMSLedgerDocRequest;
import com.maven.rms.models.FeeGrp;
import com.maven.rms.models.FeeGrpRequest;
import com.maven.rms.models.GHLPaymentResponse;
import com.maven.rms.models.MFTRequest;
import com.maven.rms.models.MFTWFDocRequest;
import com.maven.rms.models.MFTWFHistoryRequest;
import com.maven.rms.models.MFTWFRequest;
import com.maven.rms.models.ParamRequest;
import com.maven.rms.models.RMSUserRequest;
import com.maven.rms.models.RolePermissionRequest;
import com.maven.rms.models.TaxCdRequest;

public interface IStoreProcedureInterface {

        //#region tax code
        // List<Object[]> sp_gettaxcode_v2(TaxCdRequest taxCdRequest);

        // Integer sp_instaxcode(TaxCdRequest insertRequest);

        // Integer sp_updtaxcode(TaxCdRequest updateRequest);

        // Integer sp_deltaxcode(TaxCdRequest deleteRequest);

        // Integer sp_checktaxcdbyid(TaxCdRequest taxCodeRequest);
        //#endregion

        //List<Object[]> getTaxCodes(Integer page, Integer size);

        //#region param
        List<Object[]> sp_getparam(ParamRequest paramRequest);
        List<Object[]> sp_getsourcesystem(Integer i_page, Integer i_size, BigInteger i_ss_id, String i_ss_cd, String i_ss_nm,
                                          String i_modified_by, Date i_dt_modified_fr, Date i_dt_modified_to, String i_status);
        //#endregion

        // //#region user role
        // List<Object[]> sp_getuserbyrole(RMSUserRequest rmsUserRequest);
        // Object sp_getuserdetail(RMSUserRequest rmsUserRequest);
        // //#endregion

        //#region mft
        // List<Object[]> sp_getmft(MFTRequest mftRequest);

        // Integer sp_updmftwf_status(MFTWFRequest mftwfRequest);

        // List<Object[]> sp_getmftwf(MFTWFRequest mftwfRequest);

        // BigInteger sp_insmftwf(MFTWFRequest mftwfRequest);

        // List<Object[]> sp_getmftwfhis(MFTWFHistoryRequest mftwfHistoryRequest);

        // List<Object[]> sp_getwfh_ast(MFTWFHistoryRequest mftwfHistoryRequest);

        // Integer sp_updmftwf(MFTWFRequest mftwfRequest);

        // //replace by mftwfService.sp_uploadDoc(mftwfDocRequest)
        // // Integer sp_insmftwfdoc(BigInteger i_wf_id, String i_file_nm, Blob i_file_content, String i_file_type, Integer i_file_size,
        // // String i_created_by, String i_modified_by, String i_status);

        // List<Object[]> sp_getfeedetailitems(MFTRequest mftRequest);

        // List<Object[]> sp_getmftwfdoc(MFTWFDocRequest mftwfDocRequest);

        // List<Object[]> sp_getwfh_status(MFTWFHistoryRequest mftwfHistoryRequest);

        // Blob sp_getmftwfdocfilecontent(MFTWFDocRequest mftwfDocRequest);

   //  List<Object[]> sp_getuserdetail(String i_ssm4uuserrefno);

        //#endregion

        // #region MTT
        // List<Object[]> sp_getMTTItem(Integer mttId);

        // Integer sp_updateMTT(String ornNo, String custNm, String custAddr1, String custAddr2, String custAddr3,
        //                 String custPostCode, String custCity, String custState);

        // String sp_checkLatestOrderStatus(String ornNo);

        // Object[] sp_insertPayment(Integer mttID, String pymtMethod, String serviceID, BigDecimal pymtAmt, String langCd,
        //                 String usernameC, String usernameM);

        // Integer sp_updatePayment(GHLPaymentResponse ghlResponse,String usernameM);

        // Integer sp_checkPaymentRcpt(String ornNo);

        // Object[] sp_insertReceipt(String paymentId,String username);

        // Integer sp_updateMTTRcpt(Integer mttRcptID, String verID, String ssDocRefID);
        // #endregion


//   // #region Fee Group Start
//   // List<Object[]> sp_getfeegroup_v2(Integer i_page, Integer i_size,
//   // String i_fee_grp_nm_en, String i_fee_grp_nm_bm,
//   // String i_modified_by, Date i_dt_modified_fr,
//   // Date i_dt_modified_to, String i_status);
//   List<Object[]> sp_getfeegroup_v2(FeeGrpRequest feeGroupRequest);

//   // Integer sp_insfeegroup(String i_fee_grp_nm_en, String i_fee_grp_nm_bm,
//   // String i_created_by, String i_modified_by, String i_status);
//   Integer sp_insfeegroup(FeeGrpRequest feeGroupRequest,
//       String i_created_by, String i_modified_by, String i_status);

//   // Integer sp_updfeegroup(Long i_fee_grp_id, String i_fee_grp_nm_en, String
//   // i_fee_grp_nm_bm,
//   // String i_modified_by, String i_status);
//   Integer sp_updfeegroup(FeeGrpRequest feeGroupRequest, String i_fee_grp_nm_en, String i_fee_grp_nm_bm,
//       String i_modified_by, String i_status);

//   // Integer sp_checkfeegrpbyid(Long i_fee_grp_id);
//   Integer sp_checkfeegrpbyid(FeeGrpRequest feeGroupRequest);
//   // #endregion

}