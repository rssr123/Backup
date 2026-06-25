package com.maven.rms.interfaces;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.maven.rms.models.FeeGrp;
import com.maven.rms.models.FeeGrpRequest;
import com.maven.rms.models.GHLPayment;
import com.maven.rms.models.GHLPaymentResponse;
import com.maven.rms.models.MFT;
import com.maven.rms.models.MFTRequest;
import com.maven.rms.models.MFTWF;
import com.maven.rms.models.MFTWFDoc;
import com.maven.rms.models.MFTWFDocRequest;
import com.maven.rms.models.FMS;
import com.maven.rms.models.FMSLedger;
import com.maven.rms.models.FMSLedgerDoc;
import com.maven.rms.models.FMSLedgerDocRequest;
import com.maven.rms.models.FeeDetailItems;
import com.maven.rms.models.MFTWFHistory;
import com.maven.rms.models.MFTWFHistoryRequest;
import com.maven.rms.models.MFTWFRequest;
import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.models.Param;
import com.maven.rms.models.ParamRequest;
import com.maven.rms.models.Permission;
import com.maven.rms.models.PermissionByID;
import com.maven.rms.models.Role;
import com.maven.rms.models.RolePermissionGet;
import com.maven.rms.models.SourceSystemCode;
import com.maven.rms.models.TaxCd;
import com.maven.rms.models.TaxCdRequest;
import com.maven.rms.models.UserRole;
import com.maven.rms.models.RMSUser;
import com.maven.rms.models.RMSUserRequest;
import com.maven.rms.models.SourceSystemCode;
import com.maven.rms.models.TaxCd;


public interface IStoreProceduresService {

    // #region tax code
//     List<TaxCd> sp_gettaxcode_v2(TaxCdRequest taxCdRequest);

//     Integer sp_instaxcode(TaxCdRequest insertRequest);

//     Integer sp_updtaxcode(TaxCdRequest updateRequest);

//     Integer sp_deltaxcode(TaxCdRequest deleteRequest);

//     Integer sp_checktaxcdbyid(TaxCdRequest taxCodeRequest);
    // #endregion

    // get all tax code
    // List<TaxCode> getTaxCodes(Integer page, Integer size);

    // #region param
    List<Param> sp_getparam(ParamRequest paramRequest);

    List<SourceSystemCode> sp_getsourcesystem(Integer i_page, Integer i_size, BigInteger i_ss_id, String i_ss_cd,
            String i_ss_nm,
            String i_modified_by, Date i_dt_modified_fr, Date i_dt_modified_to, String i_status);

    // #endregion

    // #region user role
//       List<RMSUser> sp_getuserbyrole(RMSUserRequest rmsUserRequest);
//       RMSUser sp_getuserdetail(RMSUserRequest rmsUserRequest);
    // #endregion

    // #region wtf
//     List<MFT> sp_getmft(MFTRequest mftRequest);

//     Integer sp_updmftwf_status(MFTWFRequest mftwfRequest);

//     List<MFTWF> sp_getmftwf(MFTWFRequest mftwfRequest);

//      BigInteger sp_insmftwf(MFTWFRequest mftwfRequest);

//      List<MFTWFHistory> sp_getmftwfhis(MFTWFHistoryRequest mftwfHistoryRequest);

//      List<MFTWFHistory> sp_getwfh_ast(MFTWFHistoryRequest mftwfHistoryRequest);

//      Integer sp_updmftwf(MFTWFRequest mftwfRequest);

//      //replace by mftwfService.sp_uploadDoc(mftwfDocRequest)
// //       Integer sp_insmftwfdoc(BigInteger i_wf_id, String i_file_nm, Blob i_file_content, String i_file_type, Integer i_file_size,
// //             String i_created_by, String i_modified_by, String i_status);

//        List<FeeDetailItems> sp_getfeedetailitems(MFTRequest mftRequest);

//        List<MFTWFHistory> sp_getwfh_status(MFTWFHistoryRequest mftwfHistoryRequest);

//      List<MFTWFDoc> sp_getmftwfdoc(MFTWFDocRequest mftwfDocRequest);

//        String sp_getmftwfdocfilecontent(MFTWFDocRequest mftwfDocRequest);

    // #endregion

    // #region mtt
    // List<OnlinePaymentItem> sp_getMTTItem(Integer mttId);

    // Integer sp_updateMTT(String ornNo, String custNm, String custAddr1, String custAddr2, String custAddr3,
    //         String custPostCode, String custCity, String custState);

    // String sp_checkLatestOrderStatus(String ornNo);

    // GHLPayment sp_insertPayment(Integer mttID, String pymtMethod, String serviceID, BigDecimal pymtAmt, String langCd,
    //         String usernameC, String usernameM);

    // Integer sp_updatePayment(GHLPaymentResponse ghlResponse, String usernameM);

    // Integer sp_checkPaymentRcpt(String ornNo);

    // MTTRCPT sp_insertReceipt(String paymentId, String username);

    // Integer sp_updateMTTRcpt(Integer mttRcptID, String verID, String ssDocRefID);

    // #endregion

//     // #region Fee Group Start
//     List<FeeGrp> sp_getfeegroup_v2(FeeGrpRequest feeGroupRequest);

//     Integer sp_insfeegroup(FeeGrpRequest feeGroupRequest, String i_created_by, String i_modified_by,
//     String i_status);

//     Integer sp_updfeegroup(FeeGrpRequest feeGroupRequest, String i_fee_grp_nm_en, String i_fee_grp_nm_bm,
//     String i_modified_by, String i_status);

//     Integer sp_checkfeegrpbyid(FeeGrpRequest feeGroupRequest);
//     // #endregion

   
}