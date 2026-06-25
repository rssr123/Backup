package com.maven.rms.services;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

import com.maven.rms.interfaces.IStoreProceduresService;
import com.maven.rms.models.FeeGrp;
import com.maven.rms.models.FeeGrpRequest;
import com.maven.rms.models.GHLPayment;
import com.maven.rms.models.GHLPaymentResponse;
import com.maven.rms.models.MFT;
import com.maven.rms.models.MFTRequest;
import com.maven.rms.models.MFTWF;
import com.maven.rms.models.MFTWFDoc;
import com.maven.rms.models.MFTWFDocRequest;
import com.maven.rms.models.DeferredIncomeAging;
import com.maven.rms.models.DeferredIncomeAgingRequest;
import com.maven.rms.models.RiplAging;
import com.maven.rms.models.FeeDetailItems;
import com.maven.rms.models.MFTWFHistory;
import com.maven.rms.models.MFTWFHistoryRequest;
import com.maven.rms.models.MFTWFRequest;
import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.models.Param;
import com.maven.rms.models.ParamRequest;
import com.maven.rms.models.RIPLAgingRequest;
import com.maven.rms.models.SourceSystemCode;
import com.maven.rms.models.TaxCd;
import com.maven.rms.models.TaxCdRequest;
import com.maven.rms.models.RMSUser;
import com.maven.rms.models.RMSUserRequest;
import com.maven.rms.repositories.IStoreProcedureRepository;
import com.maven.rms.repositories.MTTRCPTRepository;
import com.maven.rms.repositories.RICPRepository;

@Service
@Slf4j
public class StoreProcedureService implements IStoreProceduresService {
    //private static final Logger logger = LoggerFactory.getLogger(StoreProcedureService.class);
    private final IStoreProcedureRepository storeProcedureRepository;
    private final MTTRCPTRepository mttrcptRepository;
    private final RICPRepository ricpRepository;

    public StoreProcedureService(IStoreProcedureRepository storeProcedureRepository,
            MTTRCPTRepository mttrcptRepository, RICPRepository ricpRepository) {
        this.storeProcedureRepository = storeProcedureRepository;
        this.mttrcptRepository = mttrcptRepository;
        this.ricpRepository = ricpRepository;

    }

    // #region tax code
    // @Override
    // // public List<TaxCd> sp_gettaxcode_v2(Integer i_page, Integer i_size, Long i_tax_cd_id, String i_tax_cd,
    // //         String i_tax_cd_nm_en, String i_tax_cd_nm_bm, String i_modified_by, Date i_dt_modified_fr,
    // //         Date i_dt_modified_to, String i_status) {
    // public List<TaxCd> sp_gettaxcode_v2(TaxCdRequest taxCdRequest) {
    //     List<TaxCd> result = Collections.emptyList();

    //     try {
    //         List<Object[]> objects = storeProcedureRepository.sp_gettaxcode_v2(taxCdRequest);
    //         result = convertTTaxCdList(objects);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return result;
    // }

    // private List<TaxCd> convertTTaxCdList(List<Object[]> objects) {
    //     List<TaxCd> taxCdList = new ArrayList<>();

    //     for (Object[] obj : objects) {
    //         TaxCd taxCd = new TaxCd();
    //         taxCd.setTax_cd((String) obj[0]);
    //         taxCd.setTax_cd_id((BigInteger) obj[1]);
    //         taxCd.setTax_cd_nm_en((String) obj[2]);
    //         taxCd.setTax_cd_nm_bm((String) obj[3]);
    //         taxCd.setTax_pct((BigDecimal) obj[4]);
    //         taxCd.setDtModified((Date) obj[5]);
    //         taxCd.setModifiedBy((String) obj[6]);
    //         taxCd.setStatus((String) obj[7]);
    //         taxCd.setStatus_en((String) obj[8]);
    //         taxCd.setStatus_bm((String) obj[9]);
    //         taxCd.setTotal((Integer) obj[10]);
    //         taxCdList.add(taxCd);
    //     }

    //     return taxCdList;
    // }

    // @Override
    // public Integer sp_instaxcode(TaxCdRequest insertRequest) {
    //     Integer result = 0;
    //     try {
    //         result = storeProcedureRepository.sp_instaxcode(insertRequest);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // @Override
    // public Integer sp_updtaxcode(TaxCdRequest updateRequest) {
    //     Integer result = 0;
    //     try {
    //         result = storeProcedureRepository.sp_updtaxcode(updateRequest);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // @Override
    // public Integer sp_deltaxcode(TaxCdRequest deleteRequest) {
    //     Integer result = 0;
    //     try {
    //         result = storeProcedureRepository.sp_deltaxcode(deleteRequest);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // @Override
    // public Integer sp_checktaxcdbyid(TaxCdRequest taxCodeRequest) {
    //     Integer result = 0;
    //     try {
    //         result = storeProcedureRepository.sp_checktaxcdbyid(taxCodeRequest);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // #endregion

    // #region param
    @Override
    public List<Param> sp_getparam(ParamRequest paramRequest) {
        List<Param> result = Collections.emptyList();

        try {

            result = convertToGetParam(storeProcedureRepository.sp_getparam(paramRequest));
        } catch (NumberFormatException e) {

            e.printStackTrace();

        } catch (Exception e) {

            e.printStackTrace();

        } finally {

        }

        return result;
    }

    private List<Param> convertToGetParam(List<Object[]> objects) {
        List<Param> paramList = new ArrayList<>();

        for (Object[] obj : objects) {
            // Extract values from obj array and cast them to their respective types
            String param_cd = (String) obj[0];
            String nm_en = (String) obj[1];
            String nm_bm = (String) obj[2];
            Integer total = (Integer) obj[3];

            // Create a new param instance using the extracted values
            Param param = new Param();
            param.setParam_cd(param_cd);
            param.setNm_en(nm_en);
            param.setNm_bm(nm_bm);
            param.setTotal(total);

            // Add the param instance to the paramList list
            paramList.add(param);
        }
        return paramList;
    }

    @Override
    public List<SourceSystemCode> sp_getsourcesystem(Integer i_page, Integer i_size, BigInteger i_ss_id, String i_ss_cd,
            String i_ss_nm,
            String i_modified_by, Date i_dt_modified_fr, Date i_dt_modified_to, String i_status) {
        List<SourceSystemCode> result = Collections.emptyList();

        try {

            List<Object[]> objects = storeProcedureRepository.sp_getsourcesystem(i_page, i_size, i_ss_id, i_ss_cd,
                    i_ss_nm, i_modified_by,
                    i_dt_modified_fr, i_dt_modified_to, i_status);
            result = convertToGetSourceSystem(objects);

        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<SourceSystemCode> convertToGetSourceSystem(List<Object[]> objects) {
        List<SourceSystemCode> sourceSystemCodeList = new ArrayList<>();

        for (Object[] obj : objects) {

            // Create a new sourceSystemCode instance using the extracted values
            SourceSystemCode sourceSystemCode = new SourceSystemCode();
            sourceSystemCode.setSs_id((BigInteger) obj[0]);
            sourceSystemCode.setSs_cd((String) obj[1]);
            sourceSystemCode.setSs_nm((String) obj[2]);
            sourceSystemCode.setDt_modified((Date) obj[3]);
            sourceSystemCode.setModified_by((String) obj[4]);
            sourceSystemCode.setStatus((String) obj[5]);
            sourceSystemCode.setStatus_en((String) obj[6]);
            sourceSystemCode.setStatus_bm((String) obj[7]);
            sourceSystemCode.setTotal((Integer) obj[8]);

            sourceSystemCodeList.add(sourceSystemCode);
        }
        return sourceSystemCodeList;
    }

    // #endregion

    // // #region user role
    // @Override
    // public List<RMSUser> sp_getuserbyrole(RMSUserRequest rmsUserRequest) {

    //     List<RMSUser> result = Collections.emptyList();

    //     try {

    //         List<Object[]> objects = storeProcedureRepository.sp_getuserbyrole(rmsUserRequest);

    //         result = convertToGetUserByRole(objects);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return result;
    // }

    // private List<RMSUser> convertToGetUserByRole(List<Object[]> objects) {
    //     List<RMSUser> userList = new ArrayList<>();

    //     for (Object[] obj : objects) {

    //         RMSUser user = new RMSUser();
    //         user.setSsm4uuserrefno((String) obj[0]);
    //         user.setNm((String) obj[1]);
    //         user.setEmail((String) obj[2]);

    //         userList.add(user);
    //     }

    //     return userList;
    // }

    // @Override
    // public RMSUser sp_getuserdetail(RMSUserRequest rmsUserRequest) {
    //     RMSUser result = new RMSUser();

    //     try {

    //         Object objects = storeProcedureRepository.sp_getuserdetail(rmsUserRequest);

    //         result = convertToGetUserDetail(objects);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return result;
    // }

    // private RMSUser convertToGetUserDetail(Object objects) {
    //     // RMSUser userList = new ArrayList<>();
    //     RMSUser user = new RMSUser();
    //     // for (Object obj : objects) {

    //     if (objects instanceof Object[]) {
    //         Object[] objArray = (Object[]) objects;

    //         // if (objArray.length >= 4) {
    //         user.setSsm4uuserrefno((String) objArray[0]);
    //         user.setNm((String) objArray[1]);
    //         user.setEmail((String) objArray[2]);
    //         user.setStatus((String) objArray[3]);
    //         // }
    //     }

    //     // userList.add(user);
    //     // }

    //     return user;
    // }
    // // #endregion

    // #region mft


    // @Override
    // public List<MFT> sp_getmft(MFTRequest mftRequest) {
    // // public List<MFT> sp_getmft(Integer i_page, Integer i_size, Integer i_fee_detail_pk, String i_fee_detail_id,
    // //         BigDecimal i_unit_fee_fr, BigDecimal i_unit_fee_to, String i_ss_cd,
    // //         String i_tax_cd, Date i_dt_modified_fr, Date i_dt_modified_to, String i_modified_by, String i_status) {

    //     List<MFT> result = Collections.emptyList();

    //     try {

    //         List<Object[]> objects = storeProcedureRepository.sp_getmft(mftRequest);

    //         result = convertToGetMFT(objects);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // // public MFT sp_getmftByPK(Integer i_fee_detail_pk) {
    // public MFT sp_getmftByPK(MFTRequest mftRequest) {
    //     try {
    //         // List<MFT> data = convertToGetMFT(storeProcedureRepository.sp_getMftWFilter(i_fee_detail_pk));
    //         List<MFT> data = convertToGetMFT(storeProcedureRepository.sp_getMftWFilter(mftRequest));
    //         return data.size() > 0 ? data.get(0) : null;
    //     } catch (Exception e) {
    //         logger.error("Exception in " + this.getClass().toString(), e);
    //     }
    //     return null;
    // }

    // public Integer sp_insMFT(MFT mft) {
    //     Integer result = 0;
    //     try {
    //         result = storeProcedureRepository.sp_insMFT(mft);

    //     } catch (Exception e) {
    //         logger.error("Exception in " + this.getClass().toString(), e);
    //     }
    //     return result;
    // }

    // public Integer sp_updMFT(MFT mft) {
    //     Integer result = 0;
    //     try {
    //         result = storeProcedureRepository.sp_updMFT(mft);

    //     } catch (Exception e) {
    //         logger.error("Exception in " + this.getClass().toString(), e);
    //     }
    //     return result;
    // }

    // private List<MFT> convertToGetMFT(List<Object[]> objects) {
    //     List<MFT> mftList = new ArrayList<>();

    //     for (Object[] obj : objects) {
    //         MFT mft = new MFT();

    //         mft.setFee_detail_pk((Integer) obj[0]);
    //         mft.setFee_detail_id((String) obj[1]);
    //         mft.setFee_grp_id((Integer) obj[2]);
    //         mft.setFee_grp_nm_en((String) obj[3]);
    //         mft.setFee_grp_nm_bm((String) obj[4]);
    //         mft.setFee_detail_nm_e((String) obj[5]);
    //         mft.setFee_detail_nm_b((String) obj[6]);
    //         mft.setUnit_fee((BigDecimal) obj[7]);
    //         mft.setPromo_startdt((Date) obj[8]);
    //         mft.setPromo_enddt((Date) obj[9]);
    //         mft.setPromo_fee((BigDecimal) obj[10]);
    //         mft.setTax_cd_id((Integer) obj[11]);
    //         mft.setTax_cd((String) obj[12]);
    //         mft.setAllow_otc((Integer) obj[13]);
    //         mft.setLl_parent_id((String) obj[14]);
    //         mft.setLl_start_day((Integer) obj[15]);
    //         mft.setLl_end_day((Integer) obj[16]);
    //         mft.setLl_start_mth((Integer) obj[17]);
    //         mft.setLl_end_mth((Integer) obj[18]);
    //         mft.setLedger_cd((String) obj[19]);
    //         mft.setSs_cd((String) obj[20]);
    //         mft.setDt_modified((Date) obj[21]);
    //         mft.setDt_created((Date) obj[22]);
    //         mft.setModified_by((String) obj[23]);
    //         mft.setCreated_by((String) obj[24]);
    //         mft.setModified_by_nm((String) obj[25]);
    //         mft.setCreated_by_nm((String) obj[26]);
    //         mft.setStatus((String) obj[27]);
    //         mft.setTotal((Integer) obj[28]);

    //         mftList.add(mft);
    //     }
    //     return mftList;
    // }

    // @Override
    // public Integer sp_updmftwf_status(MFTWFRequest mftwfRequest) {

    //     Integer result = 0;

    //     try {

    //         result = storeProcedureRepository.sp_updmftwf_status(mftwfRequest);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // // public Integer sp_updmftwfStatus(BigInteger i_wf_id, String i_status) {
    // public Integer sp_updmftwfStatus(MFTWFRequest mftwfRequest) {
    //     Integer result = 0;
    //     try {
    //         // result = storeProcedureRepository.sp_updateMFTWFStatus(i_wf_id, i_status);
    //         result = storeProcedureRepository.sp_updateMFTWFStatus(mftwfRequest);
    //     } catch (Exception e) {
    //         logger.error("Exception in " + this.getClass().toString(), e);
    //     }
    //     return result;
    // }

    // @Override
    // public List<MFTWF> sp_getmftwf(MFTWFRequest mftwfRequest) {

    //     List<MFTWF> result = Collections.emptyList();

    //     try {

    //         List<Object[]> objects = storeProcedureRepository.sp_getmftwf(mftwfRequest);

    //         result = convertToGetMFTWF(objects);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // public List<MFTWF> sp_getmftwfByStatusAndEffDate(String status) {
    //     try {
    //         return convertToGetMFTWFByStatusAndEffDate(storeProcedureRepository.sp_getMFTWFByStatusAndEffDate(status));
    //     } catch (Exception e) {
    //         logger.error("Exception in " + this.getClass().toString(), e);
    //     }
    //     return Collections.emptyList();
    // }

    // private List<MFTWF> convertToGetMFTWFByStatusAndEffDate(List<Object[]> objects) {
    //     List<MFTWF> mftwfList = new ArrayList<>();

    //     for (Object[] obj : objects) {
    //         MFTWF mftwf = new MFTWF();

    //         mftwf.setWf_id((BigInteger) obj[0]);
    //         mftwf.setFee_detail_pk((Integer) obj[1]);
    //         mftwf.setFee_detail_id((String) obj[2]);
    //         mftwf.setFee_grp_id((Integer) obj[3]);
    //         mftwf.setFee_detail_nm_e((String) obj[4]);
    //         mftwf.setFee_detail_nm_b((String) obj[5]);
    //         mftwf.setFee_amt((BigDecimal) obj[6]);
    //         mftwf.setPromo_startdt((Date) obj[7]);
    //         mftwf.setPromo_enddt((Date) obj[8]);
    //         mftwf.setPromo_fee((BigDecimal) obj[9]);
    //         mftwf.setTax_cd_id((Integer) obj[10]);
    //         mftwf.setAllow_otc((Integer) obj[11]);
    //         mftwf.setLl_parent_id((String) obj[12]);
    //         mftwf.setLl_start_day((Integer) obj[13]);
    //         mftwf.setLl_start_mth((Integer) obj[14]);
    //         mftwf.setLl_end_day((Integer) obj[15]);
    //         mftwf.setLl_end_mth((Integer) obj[16]);
    //         mftwf.setLedger_cd((String) obj[17]);
    //         mftwf.setSs_cd((String) obj[18]);
    //         mftwf.setEffective_date((Date) obj[19]);
    //         mftwf.setDt_created((Date) obj[20]);
    //         mftwf.setDt_modified((Date) obj[21]);
    //         mftwf.setCreated_by((String) obj[22]);
    //         mftwf.setModified_by((String) obj[23]);
    //         mftwf.setStatus((String) obj[24]);
    //         mftwf.setAssign_to((String) obj[25]);
    //         mftwf.setAction((String) obj[26]);

    //         mftwfList.add(mftwf);
    //     }
    //     return mftwfList;
    // }

    // private List<MFTWF> convertToGetMFTWF(List<Object[]> objects) {
    //     List<MFTWF> mftwfList = new ArrayList<>();

    //     for (Object[] obj : objects) {
    //         MFTWF mftwf = new MFTWF();

    //         mftwf.setWf_id((BigInteger) obj[0]);
    //         mftwf.setFee_detail_pk((Integer) obj[1]);
    //         mftwf.setFee_detail_id((String) obj[2]);
    //         mftwf.setFee_grp_id((Integer) obj[3]);
    //         mftwf.setFee_grp_nm_en((String) obj[4]);
    //         mftwf.setFee_grp_nm_bm((String) obj[5]);
    //         mftwf.setFee_detail_nm_e((String) obj[6]);
    //         mftwf.setFee_detail_nm_b((String) obj[7]);
    //         mftwf.setFee_amt((BigDecimal) obj[8]);
    //         mftwf.setPromo_startdt((Date) obj[9]);
    //         mftwf.setPromo_enddt((Date) obj[10]);
    //         mftwf.setPromo_fee((BigDecimal) obj[11]);
    //         mftwf.setTax_cd_id((Integer) obj[12]);
    //         mftwf.setTax_cd((String) obj[13]);
    //         mftwf.setAllow_otc((Integer) obj[14]);
    //         mftwf.setLl_parent_id((String) obj[15]);
    //         mftwf.setLl_start_day((Integer) obj[16]);
    //         mftwf.setLl_end_day((Integer) obj[17]);
    //         mftwf.setLl_start_mth((Integer) obj[18]);
    //         mftwf.setLl_end_mth((Integer) obj[19]);
    //         mftwf.setLedger_cd((String) obj[20]);
    //         mftwf.setSs_cd((String) obj[21]);
    //         mftwf.setSs_nm((String) obj[22]);
    //         mftwf.setEffective_date((Date) obj[23]);
    //         mftwf.setDt_created((Date) obj[24]);
    //         mftwf.setDt_modified((Date) obj[25]);
    //         mftwf.setCreated_by((String) obj[26]);
    //         mftwf.setCreated_by_nm((String) obj[27]);
    //         mftwf.setModified_by((String) obj[28]);
    //         mftwf.setModified_by_nm((String) obj[29]);
    //         mftwf.setStatus((String) obj[30]);
    //         mftwf.setStatus_en((String) obj[31]);
    //         mftwf.setStatus_bm((String) obj[32]);
    //         mftwf.setAssign_to((String) obj[33]);
    //         mftwf.setAssign_to_nm((String) obj[34]);
    //         mftwf.setAction((String) obj[35]);
    //         mftwf.setR_fee_det_nm((String) obj[36]);
    //         mftwf.setR_fee_amt((BigDecimal) obj[37]);
    //         mftwf.setR_ss_cd((String) obj[38]);
    //         mftwf.setR_promo_startdt((Date) obj[39]);
    //         mftwf.setR_promo_enddt((Date) obj[40]);
    //         mftwf.setR_ll_required((Integer) obj[41]);
    //         mftwf.setR_add_notes((String) obj[42]);
    //         mftwf.setMft_status((String) obj[43]);
    //         mftwf.setR_promo_fee((BigDecimal) obj[44]);
    //         mftwf.setTask_id((String) obj[45]);
    //         mftwf.setTotal((Integer) obj[46]);

    //         mftwfList.add(mftwf);
    //     }
    //     return mftwfList;
    // }

    // @Override
    // public BigInteger sp_insmftwf(MFTWFRequest mftwfRequest) {

    //     BigInteger result = BigInteger.ZERO;

    //     try {

    //         result = storeProcedureRepository.sp_insmftwf(mftwfRequest);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // @Override
    // public List<MFTWFHistory> sp_getmftwfhis(MFTWFHistoryRequest mftwfHistoryRequest) {
    //     List<MFTWFHistory> result = Collections.emptyList();

    //     try {

    //         List<Object[]> objects = storeProcedureRepository.sp_getmftwfhis(mftwfHistoryRequest);
    //         result = convertGetmftwfhis(objects);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return result;
    // }

    // private List<MFTWFHistory> convertGetmftwfhis(List<Object[]> objects) {
    //     List<MFTWFHistory> MFTWFHistoryList = new ArrayList<>();

    //     for (Object[] obj : objects) {

    //         MFTWFHistory mftwfHist = new MFTWFHistory();

    //         mftwfHist.setAction((String) obj[0]);
    //         mftwfHist.setDt_activity((Date) obj[1]);
    //         mftwfHist.setAct_by((String) obj[2]);
    //         mftwfHist.setAssign_to((String) obj[3]);
    //         mftwfHist.setRemark((String) obj[4]);
    //         mftwfHist.setDt_created((Date) obj[5]);
    //         mftwfHist.setDt_modified((Date) obj[6]);
    //         mftwfHist.setCreated_by((String) obj[7]);
    //         mftwfHist.setModified_by((String) obj[8]);
    //         mftwfHist.setStatus_en((String) obj[9]);
    //         mftwfHist.setStatus_bm((String) obj[10]);
    //         mftwfHist.setTotal((Integer) obj[11]);

    //         MFTWFHistoryList.add(mftwfHist);
    //     }
    //     return MFTWFHistoryList;
    // }

    // @Override
    // public List<MFTWFHistory> sp_getwfh_ast(MFTWFHistoryRequest mftwfHistoryRequest) {
    //     List<MFTWFHistory> result = Collections.emptyList();

    //     try {

    //         List<Object[]> objects = storeProcedureRepository.sp_getwfh_ast(mftwfHistoryRequest);
    //         result = convertGetmftwfhisAst(objects);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return result;
    // }

    // private List<MFTWFHistory> convertGetmftwfhisAst(List<Object[]> objects) {
    //     List<MFTWFHistory> MFTWFHistoryAstList = new ArrayList<>();

    //     for (Object[] obj : objects) {

    //         MFTWFHistory mftwfHistAst = new MFTWFHistory();

    //         mftwfHistAst.setAssign_to((String) obj[0]);
    //         mftwfHistAst.setAssign_to_nm((String) obj[1]);

    //         MFTWFHistoryAstList.add(mftwfHistAst);
    //     }
    //     return MFTWFHistoryAstList;
    // }

    // @Override
    // public Integer sp_updmftwf(MFTWFRequest mftwfRequest) {

    //     Integer result = 0;

    //     try {

    //         result = storeProcedureRepository.sp_updmftwf(mftwfRequest);

    //     } catch (Exception e) {

    //         e.printStackTrace();

    //     } finally {

    //     }

    //     return result;
    // }

    // // @Override
    // // public Integer sp_insmftwfdoc(BigInteger i_wf_id, String i_file_nm, Blob i_file_content, String i_file_type,
    // //         Integer i_file_size,
    // //         String i_created_by, String i_modified_by, String i_status) {

    // //     Integer result = 0;

    // //     try {

    // //         result = storeProcedureRepository.sp_insmftwfdoc(i_wf_id, i_file_nm, i_file_content, i_file_type,
    // //                 i_file_size, i_created_by,
    // //                 i_modified_by, i_status);

    // //     } catch (Exception e) {

    // //         e.printStackTrace();

    // //     } finally {

    // //     }

    // //     return result;
    // // }

    // @Override
    // public List<MFTWFDoc> sp_getmftwfdoc(MFTWFDocRequest mftwfDocRequest) {

    //     List<MFTWFDoc> result = Collections.emptyList();

    //     try {

    //         List<Object[]> objects = storeProcedureRepository.sp_getmftwfdoc(mftwfDocRequest);

    //         result = convertToGetMFTWDoc(objects);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // private List<MFTWFDoc> convertToGetMFTWDoc(List<Object[]> objects) {
    //     List<MFTWFDoc> mftwfDocList = new ArrayList<>();

    //     for (Object[] obj : objects) {
    //         MFTWFDoc mftwfDoc = new MFTWFDoc();

    //         mftwfDoc.setWfdoc_id((BigInteger) obj[0]);
    //         mftwfDoc.setFile_nm((String) obj[1]);

    //         /*
    //          * Blob blob = (Blob) obj[1];
    //          * 
    //          * try {
    //          * // Convert Blob to byte array
    //          * byte[] bytes = blob.getBytes(1, (int) blob.length());
    //          * 
    //          * // Convert byte array to Base64-encoded string
    //          * String base64Content = Base64.getEncoder().encodeToString(bytes);
    //          * 
    //          * mftwfDoc.setFile_content(base64Content);
    //          * } catch (SQLException e) {
    //          * e.printStackTrace();
    //          * mftwfDoc.setFile_content(null);
    //          * }
    //          */
    //         mftwfDoc.setFile_type((String) obj[2]);
    //         mftwfDoc.setFile_size_kb((Integer) obj[3]);
    //         mftwfDoc.setDt_created((Date) obj[4]);
    //         mftwfDoc.setDt_modified((Date) obj[5]);
    //         mftwfDoc.setCreated_by((String) obj[6]);
    //         mftwfDoc.setModified_by((String) obj[7]);
    //         mftwfDoc.setTotal((Integer) obj[8]);

    //         mftwfDocList.add(mftwfDoc);
    //     }
    //     return mftwfDocList;
    // }

    // @Override
    // public List<MFTWFHistory> sp_getwfh_status(MFTWFHistoryRequest mftwfHistoryRequest) {
    //     List<MFTWFHistory> result = Collections.emptyList();

    //     try {

    //         List<Object[]> objects = storeProcedureRepository.sp_getwfh_status(mftwfHistoryRequest);
    //         result = convertGetmftwfhisStatus(objects);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return result;
    // }

    // private List<MFTWFHistory> convertGetmftwfhisStatus(List<Object[]> objects) {
    //     List<MFTWFHistory> MFTWFHistoryStatusList = new ArrayList<>();

    //     for (Object[] obj : objects) {

    //         MFTWFHistory mftwfHistStatus = new MFTWFHistory();

    //         mftwfHistStatus.setAssign_to((String) obj[0]);
    //         mftwfHistStatus.setAssign_to_nm((String) obj[1]);
    //         mftwfHistStatus.setStatus((String) obj[2]);

    //         MFTWFHistoryStatusList.add(mftwfHistStatus);
    //     }
    //     return MFTWFHistoryStatusList;
    // }

    // @Override
    // public List<FeeDetailItems> sp_getfeedetailitems(MFTRequest mftRequest) {

    //     List<FeeDetailItems> result = Collections.emptyList();

    //     try {

    //         List<Object[]> objects = storeProcedureRepository.sp_getfeedetailitems(mftRequest);

    //         result = convertToGetFeeDetailItems(objects);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // private List<FeeDetailItems> convertToGetFeeDetailItems(List<Object[]> objects) {
    //     List<FeeDetailItems> feeDetailItemsList = new ArrayList<>();

    //     for (Object[] obj : objects) {
    //         FeeDetailItems feeDetailItems = new FeeDetailItems();
    //         feeDetailItems.setFee_detail_id((String) obj[0]);
    //         feeDetailItems.setFee_grp_id((Integer) obj[1]);
    //         feeDetailItems.setFee_detail_nm_en((String) obj[2]);
    //         feeDetailItems.setFee_detail_nm_bm((String) obj[3]);
    //         feeDetailItems.setUnit_fee((BigDecimal) obj[4]);
    //         feeDetailItems.setPromo_startdt((Date) obj[5]);
    //         feeDetailItems.setPromo_enddt((Date) obj[6]);
    //         feeDetailItems.setPromo_fee((BigDecimal) obj[7]);
    //         feeDetailItems.setTax_cd((String) obj[8]);
    //         feeDetailItems.setAllow_otc((Integer) obj[9]);
    //         feeDetailItems.setLl_parent_id((String) obj[10]);
    //         feeDetailItems.setLl_start_day((Integer) obj[11]);
    //         feeDetailItems.setLl_start_mth((Integer) obj[12]);
    //         feeDetailItems.setLl_end_day((Integer) obj[13]);
    //         feeDetailItems.setLl_end_mth((Integer) obj[14]);
    //         feeDetailItems.setLedger_cd((String) obj[15]);
    //         feeDetailItems.setSs_cd((String) obj[16]);
    //         feeDetailItems.setFee_grp_nm_en((String) obj[17]);
    //         feeDetailItems.setFee_grp_nm_bm((String) obj[18]);
    //         feeDetailItems.setTax_cd_nm_en((String) obj[19]);
    //         feeDetailItems.setTax_cd_nm_bm((String) obj[20]);
    //         feeDetailItems.setTax_pct((BigDecimal) obj[21]);
    //         feeDetailItems.setStatus((String) obj[22]);

    //         feeDetailItemsList.add(feeDetailItems);
    //     }
    //     return feeDetailItemsList;
    // }

    // @Override
    // public String sp_getmftwfdocfilecontent(MFTWFDocRequest mftwfDocRequest) {

    //     String result = "";

    //     try {

    //         Blob blob = (Blob) storeProcedureRepository.sp_getmftwfdocfilecontent(mftwfDocRequest);

    //         try {
    //             // Convert Blob to byte array
    //             byte[] bytes = blob.getBytes(1, (int) blob.length());

    //             // Convert byte array to Base64-encoded string
    //             String base64Content = Base64.getEncoder().encodeToString(bytes);
    //             result = base64Content;

    //         } catch (SQLException e) {
    //             e.printStackTrace();
    //             result = "";
    //         }

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // // @Override
    // // public List<TaxCd> sp_gettaxcode_v2(Integer i_page, Integer i_size,
    // // BigInteger i_tax_cd_id, String i_tax_cd, String i_tax_cd_nm_en, String
    // // i_tax_cd_nm_bm, String i_modified_by, Date i_dt_modified_fr, Date
    // // i_dt_modified_to, String i_status) {
    // // List<TaxCd> result = Collections.emptyList();

    // // try {
    // // List<Object[]> objects = storeProcedureRepository.sp_gettaxcode_v2( i_page,
    // // i_size, i_tax_cd_id, i_tax_cd, i_tax_cd_nm_en, i_tax_cd_nm_bm, i_modified_by,
    // // i_dt_modified_fr, i_dt_modified_to, i_status);
    // // result = convertTTaxCdList(objects);
    // // } catch (Exception e) {
    // // e.printStackTrace();
    // // }

    // // return result;
    // // }

    // // private List<TaxCd> convertTTaxCdList(List<Object[]> objects) {
    // // List<TaxCd> taxCdList = new ArrayList<>();

    // // for (Object[] obj : objects) {
    // // TaxCd taxCd = new TaxCd();
    // // taxCd.setTax_cd((String) obj[0]);
    // // taxCd.setTax_cd_id((BigInteger) obj[1]);
    // // taxCd.setTax_cd_nm_en((String) obj[2]);
    // // taxCd.setTax_cd_nm_bm((String) obj[3]);
    // // taxCd.setTax_pct((BigDecimal) obj[4]);
    // // taxCd.setDtModified((Date) obj[5]);
    // // taxCd.setModifiedBy((String) obj[6]);
    // // taxCd.setStatus((String) obj[7]);
    // // taxCd.setStatus_en((String) obj[8]);
    // // taxCd.setStatus_bm((String) obj[9]);
    // // taxCd.setTotal((Integer) obj[10]);
    // // taxCdList.add(taxCd);
    // // }

    // // return taxCdList;
    // // }

    // #endregion

    // #region mtt
    // @Override
    // public List<OnlinePaymentItem> sp_getMTTItem(Integer mttId) {
    //     List<OnlinePaymentItem> result = Collections.emptyList();

    //     try {

    //         result = convertToOnlinePaymentItemList(storeProcedureRepository.sp_getMTTItem(mttId));
    //     } catch (NumberFormatException e) {

    //         logger.error("Exception in " + this.getClass().toString(), e);

    //     } catch (Exception e) {

    //         logger.error("Exception in " + this.getClass().toString(), e);

    //     } finally {

    //     }

    //     return result;
    // }

    // @Override
    // public Integer sp_updateMTT(String ornNo, String billingNm, String custAddr1, String custAddr2, String custAddr3,
    //         String custPostCode, String custCity, String custState) {
    //     Integer result = 0;

    //     try {

    //         result = storeProcedureRepository.sp_updateMTT(ornNo, billingNm, custAddr1, custAddr2, custAddr3,
    //                 custPostCode, custCity, custState);
    //     } catch (NumberFormatException e) {

    //         logger.error("Exception in " + this.getClass().toString(), e);

    //     } catch (Exception e) {

    //         logger.error("Exception in " + this.getClass().toString(), e);

    //     } finally {

    //     }

    //     return result;
    // }

    // @Override
    // public String sp_checkLatestOrderStatus(String ornNo) {
    //     // TODO Auto-generated method stub
    //     String result = "";

    //     try {

    //         result = storeProcedureRepository.sp_checkLatestOrderStatus(ornNo);
    //         if (result == "") {
    //             result = "empty";
    //         }

    //     } catch (NumberFormatException e) {

    //         logger.error("Exception in " + this.getClass().toString(), e);

    //     } catch (Exception e) {

    //         logger.error("Exception in " + this.getClass().toString(), e);

    //     } finally {

    //     }

    //     return result;
    // }

    // private List<OnlinePaymentItem> convertToOnlinePaymentItemList(List<Object[]> objects) {
    //     List<OnlinePaymentItem> onlinePaymentItems = new ArrayList<>();

    //     for (Object[] obj : objects) {
    //         // Extract values from obj array and cast them to their respective types
    //         Integer mtt_item_id = (Integer) obj[0];
    //         Integer line_no = (Integer) obj[1];
    //         String item_desc = (String) obj[2];
    //         Integer qty = (Integer) obj[3];
    //         BigDecimal unit_fee = (BigDecimal) obj[4];
    //         BigDecimal tax_amt = (BigDecimal) obj[5];
    //         BigDecimal disc_amt = (BigDecimal) obj[6];
    //         BigDecimal gross_amt = (BigDecimal) obj[7];

    //         // Create a new OnlinePaymentItem instance using the extracted values
    //         OnlinePaymentItem onlinePaymentItem = new OnlinePaymentItem();
    //         onlinePaymentItem.setMtt_item_id(mtt_item_id);
    //         onlinePaymentItem.setLine_no(line_no);
    //         onlinePaymentItem.setItem_desc(item_desc);
    //         onlinePaymentItem.setQty(qty);
    //         onlinePaymentItem.setUnit_fee(unit_fee);
    //         onlinePaymentItem.setTax_amt(tax_amt);
    //         onlinePaymentItem.setDisc_amt(disc_amt);
    //         onlinePaymentItem.setGross_amt(gross_amt);

    //         // Add the OnlinePaymentItem instance to the onlinePaymentItems list
    //         onlinePaymentItems.add(onlinePaymentItem);
    //     }
    //     return onlinePaymentItems;
    // }

    // public GHLPayment sp_insertPayment(Integer mttID, String pymtMethod, String serviceID,
    //         BigDecimal pymtAmt, String langCd, String usernameC,
    //         String usernameM) {

    //     // TODO Auto-generated method stub
    //     GHLPayment result = new GHLPayment();
    //     // Integer result=0;

    //     try {

    //         result = convertToGHLPayment(storeProcedureRepository.sp_insertPayment(mttID, pymtMethod, serviceID,
    //                 pymtAmt, langCd, usernameC, usernameM));

    //     } catch (NumberFormatException e) {

    //         e.printStackTrace();

    //     } catch (Exception e) {

    //         e.printStackTrace();

    //     } finally {

    //     }

    //     return result;
    // }

    // @Override
    // public Integer sp_updatePayment(GHLPaymentResponse ghlResponse, String usernameM) {

    //     // TODO Auto-generated method stub
    //     // GHLPayment result = new GHLPayment();
    //     Integer result = 0;

    //     try {

    //         String decodedInput = URLDecoder.decode(ghlResponse.getRespTime(), "UTF-8");

    //         // Define a formatter for the output date format
    //         // DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd
    //         // HH:mm:ss.SSS");

    //         // Parse the input date string
    //         // LocalDateTime inputDateTime = LocalDateTime.parse(decodedInput,
    //         // DateTimeFormatter.ofPattern("yyyy-MM-dd+HH:mm:ss"));

    //         // Format the parsed date and time as per the desired format
    //         ghlResponse.setRespTime(decodedInput);

    //         result = storeProcedureRepository.sp_updatePayment(ghlResponse, usernameM);

    //     } catch (NumberFormatException e) {

    //         e.printStackTrace();

    //     } catch (Exception e) {

    //         e.printStackTrace();

    //     } finally {

    //     }

    //     return result;
    // }

    // @Override
    // public Integer sp_checkPaymentRcpt(String ornNo) {

    //     // TODO Auto-generated method stub
    //     // GHLPayment result = new GHLPayment();
    //     Integer result = 0;

    //     try {

    //         result = storeProcedureRepository.sp_checkPaymentRcpt(ornNo);

    //     } catch (NumberFormatException e) {

    //         e.printStackTrace();

    //     } catch (Exception e) {

    //         e.printStackTrace();

    //     } finally {

    //     }

    //     return result;
    // }

    // @Override
    // public MTTRCPT sp_insertReceipt(String paymentId, String username) {

    //     MTTRCPT result = new MTTRCPT();
    //     // Integer result=0;

    //     try {

    //         result = convertToMTTRCPT(storeProcedureRepository.sp_insertReceipt(paymentId, username));

    //     } catch (NumberFormatException e) {

    //         e.printStackTrace();

    //     } catch (Exception e) {

    //         e.printStackTrace();

    //     } finally {

    //     }

    //     return result;
    // }

    // @Transactional(readOnly = true)
    // public MTTRCPT convertToMTTRCPT(Object[] obj) {
    //     MTTRCPT mttrcpt = new MTTRCPT();

    //     // Extract values from the obj and cast them to their respective types
    //     // Assuming the order and types match your MTTRCPT class
    //     // BigInteger mttRcptID = (BigInteger) obj[0];
    //     // OnlinePayment rmsMTT = (OnlinePayment) obj[1]; // Assuming OnlinePayment is
    //     // the correct type
    //     // MTTPG mttPG = (MTTPG) obj[2]; // Assuming MTTPG is the correct type
    //     String rcptNo = (String) obj[3];
    //     // LocalDateTime rcptDt = (LocalDateTime) obj[4];
    //     // String rcptStatus = (String) obj[5];
    //     // Integer rcptReprint = (Integer) obj[6];
    //     // Integer isUploaded = (Integer) obj[7];
    //     // LocalDateTime dtCreated = (LocalDateTime) obj[8];
    //     // LocalDateTime dtModified = (LocalDateTime) obj[9];
    //     // String createdBy = (String) obj[10];
    //     // String modifiedBy = (String) obj[11];

    //     // Set the values in the MTTRCPT instance
    //     mttrcpt = mttrcptRepository.findMTTRCPTByRcptNo(rcptNo).orElse(null);

    //     return mttrcpt;
    // }

    // private GHLPayment convertToGHLPayment(Object[] obj) {
    //     GHLPayment ghLPayment = new GHLPayment();

    //     // Extract values from the obj and cast them to their respective types
    //     // Assuming the order and types match your GHLPayment class
    //     String pymtId = (String) obj[0];
    //     String transactionType = (String) obj[1];
    //     String pymtMethod = (String) obj[2];
    //     String serviceId = (String) obj[3];
    //     String ordNo = (String) obj[4];
    //     String pymtDesc = (String) obj[5];
    //     String returnUrl = (String) obj[6];
    //     BigDecimal amt = (BigDecimal) obj[7];
    //     String currCd = (String) obj[8];
    //     String custIp = (String) obj[9];
    //     String custNm = (String) obj[10];
    //     String custPh = (String) obj[11];
    //     String hashValue = (String) obj[12];
    //     Integer pageTimeout = Integer.parseInt((String) obj[13]);

    //     // Set the values in the GHLPayment instance
    //     ghLPayment.setTransaction_type(transactionType);
    //     ghLPayment.setPymt_method(pymtMethod);
    //     ghLPayment.setService_id(serviceId);
    //     ghLPayment.setPymt_id(pymtId);
    //     ghLPayment.setOrd_no(ordNo);
    //     ghLPayment.setPymt_desc(pymtDesc);
    //     ghLPayment.setReturn_url(returnUrl);
    //     ghLPayment.setAmt(amt);
    //     ghLPayment.setCurr_cd(currCd);
    //     ghLPayment.setCust_ip(custIp);
    //     ghLPayment.setCust_nm(custNm);
    //     ghLPayment.setCust_ph(custPh);
    //     ghLPayment.setHash_value(hashValue);
    //     ghLPayment.setPage_timeout(pageTimeout);

    //     return ghLPayment;
    // }

    // @Override
    // public Integer sp_updateMTTRcpt(Integer mttRcptID, String verID, String ssDocRefID) {
    //     Integer result = 0;
    //     try {
    //         result = storeProcedureRepository.sp_updateMTTRcpt(mttRcptID, verID, ssDocRefID);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // #endregion

    // // #region Fee Group Start
    // @Override
    // // public List<FeeGrp> sp_getfeegroup_v2(Integer i_page, Integer i_size, String
    // // i_fee_grp_nm_en,
    // // String i_fee_grp_nm_bm, String i_modified_by, Date i_dt_modified_fr,
    // // Date i_dt_modified_to, String i_status) {
    // public List<FeeGrp> sp_getfeegroup_v2(FeeGrpRequest feeGroupRequest) {
    //     List<FeeGrp> result = Collections.emptyList();

    //     try {
    //         // List<Object[]> objects = storeProcedureRepository.sp_getfeegroup_v2(i_page,
    //         // i_size,
    //         // i_fee_grp_nm_en, i_fee_grp_nm_bm, i_modified_by, i_dt_modified_fr,
    //         // i_dt_modified_to, i_status);
    //         List<Object[]> objects = storeProcedureRepository.sp_getfeegroup_v2(feeGroupRequest);
    //         result = convertTFeeGrpList(objects);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }

    //     return result;
    // }

    // private List<FeeGrp> convertTFeeGrpList(List<Object[]> objects) {
    //     List<FeeGrp> feeGrpList = new ArrayList<>();

    //     for (Object[] obj : objects) {
    //         FeeGrp feeGrp = new FeeGrp();
    //         feeGrp.setFee_grp_id((Integer) obj[0]);
    //         feeGrp.setFee_grp_nm_en((String) obj[1]);
    //         feeGrp.setFee_grp_nm_bm((String) obj[2]);
    //         feeGrp.setDtModified((Date) obj[3]);
    //         feeGrp.setModifiedBy((String) obj[4]);
    //         feeGrp.setStatus((String) obj[5]);
    //         feeGrp.setStatus_en((String) obj[6]);
    //         feeGrp.setStatus_bm((String) obj[7]);
    //         feeGrp.setTotal((Integer) obj[8]);
    //         feeGrpList.add(feeGrp);
    //     }

    //     return feeGrpList;
    // }

    // @Override
    // // public Integer sp_insfeegroup(String i_fee_grp_nm_en, String i_fee_grp_nm_bm,
    // // String i_created_by, String i_modified_by, String i_status) {
    // public Integer sp_insfeegroup(FeeGrpRequest feeGroupRequest, String i_created_by, String i_modified_by,
    //         String i_status) {
    //     Integer result = 0;
    //     try {
    //         // result = storeProcedureRepository.sp_insfeegroup(i_fee_grp_nm_en,
    //         // i_fee_grp_nm_bm, i_created_by,
    //         // i_modified_by, i_status);
    //         result = storeProcedureRepository.sp_insfeegroup(feeGroupRequest, i_created_by, i_modified_by, i_status);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // @Override
    // // public Integer sp_updfeegroup(Long i_fee_grp_id, String i_fee_grp_nm_en,
    // // String i_fee_grp_nm_bm,
    // // String i_modified_by, String i_status) {
    // public Integer sp_updfeegroup(FeeGrpRequest feeGroupRequest, String i_fee_grp_nm_en, String i_fee_grp_nm_bm,
    //         String i_modified_by, String i_status) {
    //     Integer result = 0;
    //     try {
    //         // result = storeProcedureRepository.sp_updfeegroup(i_fee_grp_id,
    //         // i_fee_grp_nm_en, i_fee_grp_nm_bm,
    //         // i_modified_by, i_status);
    //         result = storeProcedureRepository.sp_updfeegroup(feeGroupRequest, i_fee_grp_nm_en, i_fee_grp_nm_bm,
    //                 i_modified_by, i_status);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // @Override
    // // public Integer sp_checkfeegrpbyid(Long i_fee_grp_id) {
    // public Integer sp_checkfeegrpbyid(FeeGrpRequest feeGroupRequest) {
    //     Integer result = 0;
    //     try {
    //         // result = storeProcedureRepository.sp_checkfeegrpbyid(i_fee_grp_id);
    //         result = storeProcedureRepository.sp_checkfeegrpbyid(feeGroupRequest);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // // #endregion

    // #region DI Aging Start

    // // @Override
    // // public BigInteger sp_insdiagingrpt(Date i_p_dt_req, Integer i_p_tmn_status,
    // // String i_p_ent_ty, String i_p_ent_nm,
    // // String i_p_txn_ty, String i_p_status, Date i_p_dt_exp_fr, Date i_p_dt_exp_to,
    // // Date i_p_dt_eff_fr, Date i_p_dt_eff_to, Date i_p_dt_app_fr, Date
    // // i_p_dt_app_to, Date i_p_dt_tmn_fr,
    // // Date i_p_dt_tmn_to, String i_created_by, String i_modified_by,
    // // String i_status, String i_p_email, String i_p_file_type, Integer
    // // i_p_file_size, String i_p_file_nm,
    // // String i_p_batch_no, String i_p_fms_ref_no) {
    // public BigInteger sp_insdiagingrpt(DeferredIncomeAgingRequest DIRequest, String i_p_email, String i_created_by,
    //         String i_modified_by) {
    //     {
    //         BigInteger result = BigInteger.ZERO;

    //         try {

    //             // result = storeProcedureRepository.sp_insdiagingrpt(i_p_dt_req,
    //             // i_p_tmn_status, i_p_ent_ty, i_p_ent_nm,
    //             // i_p_txn_ty, i_p_status, i_p_dt_exp_fr, i_p_dt_exp_to, i_p_dt_eff_fr,
    //             // i_p_dt_eff_to,
    //             // i_p_dt_app_fr, i_p_dt_app_to, i_p_dt_tmn_fr,
    //             // i_p_dt_tmn_to, i_created_by, i_modified_by, i_status, i_p_email,
    //             // i_p_file_type, i_p_file_size,
    //             // i_p_file_nm, i_p_batch_no, i_p_fms_ref_no);
    //             result = storeProcedureRepository.sp_insdiagingrpt(DIRequest, i_p_email, i_created_by, i_modified_by);

    //         } catch (Exception e) {
    //             e.printStackTrace();
    //         }
    //         return result;
    //     }
    // }

    // // public List<DeferredIncomeAging> sp_getdiaginglistingrpt(Integer i_page,
    // // Integer i_size, BigInteger i_rpt_di_age_id,
    // // Date i_p_dt_req, Integer i_p_tmn_status, String i_p_ent_ty, String
    // // i_p_ent_nm, String i_p_txn_ty,
    // // String i_p_status, Date i_p_dt_exp_fr, Date i_p_dt_exp_to,
    // // Date i_p_dt_eff_fr, Date i_p_dt_eff_to, Date i_p_dt_app_fr, Date
    // // i_p_dt_app_to, Date i_p_dt_tmn_fr,
    // // Date i_p_dt_tmn_to, String i_created_by, String i_modified_by, String
    // // i_status, String i_p_email,
    // // String i_p_file_type,
    // // Integer i_p_file_size, String i_p_file_nm, String i_p_batch_no, String
    // // i_p_fms_ref_no) {
    // public List<DeferredIncomeAging> sp_getdiaginglistingrpt(DeferredIncomeAgingRequest DIRequest) {

    //     List<DeferredIncomeAging> result = Collections.emptyList();

    //     try {

    //         // List<Object[]> objects =
    //         // storeProcedureRepository.sp_getdiaginglistingrpt(i_page, i_size,
    //         // i_rpt_di_age_id,
    //         // i_p_dt_req, i_p_tmn_status, i_p_ent_ty, i_p_ent_nm, i_p_txn_ty, i_p_status,
    //         // i_p_dt_exp_fr,
    //         // i_p_dt_exp_to, i_p_dt_eff_fr, i_p_dt_eff_to, i_p_dt_app_fr, i_p_dt_app_to,
    //         // i_p_dt_tmn_fr,
    //         // i_p_dt_tmn_to, i_created_by, i_modified_by, i_status, i_p_email,
    //         // i_p_file_type, i_p_file_size,
    //         // i_p_file_nm, i_p_batch_no, i_p_fms_ref_no);
    //         List<Object[]> objects = storeProcedureRepository.sp_getdiaginglistingrpt(DIRequest);

    //         result = convertToGetDIAgingListing(objects);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // private List<DeferredIncomeAging> convertToGetDIAgingListing(List<Object[]> objects) {
    //     List<DeferredIncomeAging> DIagingList = new ArrayList<>();

    //     for (Object[] obj : objects) {
    //         DeferredIncomeAging diaging = new DeferredIncomeAging();

    //         diaging.setRpt_di_age_id((BigInteger) obj[0]);
    //         diaging.setP_dt_req((Date) obj[1]);
    //         diaging.setP_tmn_status((Integer) obj[2]);
    //         diaging.setP_ent_ty((String) obj[3]);
    //         diaging.setP_ent_nm((String) obj[4]);
    //         diaging.setP_txn_ty((String) obj[5]);
    //         diaging.setP_status((String) obj[6]);
    //         diaging.setP_dt_exp_fr((Date) obj[7]);
    //         diaging.setP_dt_exp_to((Date) obj[8]);
    //         diaging.setP_dt_eff_fr((Date) obj[9]);
    //         diaging.setP_dt_eff_to((Date) obj[10]);
    //         diaging.setP_dt_app_fr((Date) obj[11]);
    //         diaging.setP_dt_app_to((Date) obj[12]);
    //         diaging.setP_dt_tmn_fr((Date) obj[13]);
    //         diaging.setP_dt_tmn_to((Date) obj[14]);
    //         diaging.setDt_created((Date) obj[15]);
    //         diaging.setDt_modified((Date) obj[16]);
    //         diaging.setCreated_by((String) obj[17]);
    //         diaging.setModified_by((String) obj[18]);
    //         diaging.setStatus((String) obj[19]);
    //         diaging.setP_email((String) obj[20]);
    //         diaging.setP_file_type((String) obj[21]);
    //         diaging.setP_file_size((Integer) obj[22]);
    //         diaging.setP_file_nm((String) obj[23]);
    //         diaging.setP_batch_no((String) obj[24]);
    //         diaging.setP_fms_ref_no((String) obj[25]);
    //         diaging.setTask_id((String) obj[26]);
    //         diaging.setTotal((Integer) obj[27]);

    //         DIagingList.add(diaging);
    //     }
    //     return DIagingList;
    // }

    // public Integer sp_upddiagingrpt(DeferredIncomeAgingRequest DIRequest) {
    //     Integer result = 0;
    //     try {
    //         result = storeProcedureRepository.sp_upddiagingrpt(DIRequest);
    //     } catch (Exception e) {
    //         logger.error("Exception in " + this.getClass().toString(), e);
    //     }
    //     return result;
    // }

    // public List<DeferredIncomeAging> sp_getdiagingrpt(BigInteger i_rpt_di_age_id) {

    //     List<DeferredIncomeAging> result = Collections.emptyList();
    //     try {
    //         List<Object[]> objects = storeProcedureRepository.sp_getdiagingrpt(i_rpt_di_age_id);
    //         result = convertToGetDIAging(objects);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // public Integer sp_getdiagequeuerpt() {

    //     Integer result = 0;
    //     try {
    //         result = storeProcedureRepository.sp_getdiagequeuerpt();
    //     } catch (Exception e) {
    //         logger.error("Exception in " + this.getClass().toString(), e);
    //     }
    //     return result;
    // }

    // public List<DeferredIncomeAging> sp_getpendingdiagingrpt() {

    //     List<DeferredIncomeAging> result = Collections.emptyList();
    //     try {
    //         List<Object[]> objects = storeProcedureRepository.sp_getpendingdiagingrpt();
    //         result = convertToGetDIAging(objects);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // private List<DeferredIncomeAging> convertToGetDIAging(List<Object[]> objects) {
    //     List<DeferredIncomeAging> DIagingList = new ArrayList<>();

    //     for (Object[] obj : objects) {
    //         DeferredIncomeAging diaging = new DeferredIncomeAging();

    //         diaging.setRpt_di_age_id((BigInteger) obj[0]);
    //         diaging.setP_dt_req((Date) obj[1]);
    //         diaging.setP_tmn_status((Integer) obj[2]);
    //         diaging.setP_ent_ty((String) obj[3]);
    //         diaging.setP_ent_nm((String) obj[4]);
    //         diaging.setP_txn_ty((String) obj[5]);
    //         diaging.setP_status((String) obj[6]);
    //         diaging.setP_dt_exp_fr((Date) obj[7]);
    //         diaging.setP_dt_exp_to((Date) obj[8]);
    //         diaging.setP_dt_eff_fr((Date) obj[9]);
    //         diaging.setP_dt_eff_to((Date) obj[10]);
    //         diaging.setP_dt_app_fr((Date) obj[11]);
    //         diaging.setP_dt_app_to((Date) obj[12]);
    //         diaging.setP_dt_tmn_fr((Date) obj[13]);
    //         diaging.setP_dt_tmn_to((Date) obj[14]);
    //         diaging.setDt_created((Date) obj[15]);
    //         diaging.setDt_modified((Date) obj[16]);
    //         diaging.setCreated_by((String) obj[17]);
    //         diaging.setModified_by((String) obj[18]);
    //         diaging.setStatus((String) obj[19]);
    //         diaging.setP_email((String) obj[20]);
    //         diaging.setP_file_type((String) obj[21]);
    //         diaging.setP_file_size((Integer) obj[22]);
    //         diaging.setP_file_nm((String) obj[23]);
    //         diaging.setP_batch_no((String) obj[24]);
    //         diaging.setP_fms_ref_no((String) obj[25]);
    //         diaging.setTask_id((String) obj[26]);

    //         DIagingList.add(diaging);
    //     }
    //     return DIagingList;
    // }

    // public Integer sp_getpendingdiagingrptbyid(BigInteger i_rpt_di_age_id) {

    //     Integer result = 0;
    //     try {
    //         result = storeProcedureRepository.sp_getpendingdiagingrptbyid(i_rpt_di_age_id);
    //     } catch (Exception e) {
    //         logger.error("Exception in " + this.getClass().toString(), e);
    //     }
    //     return result;
    // }

    // #endregion
    // #region RIPL Aging Start

    // public BigInteger sp_insriplagingrpt(Date i_p_dt_req, Integer i_p_imp_status,
    // Integer i_p_exp_status,
    // String i_p_ent_ty, String i_p_ent_nm, Date i_p_dt_due_fr, Date i_p_dt_due_to,
    // Date i_p_dt_rcpt_fr, Date i_p_dt_rcpt_to, Date i_p_dt_imp_fr, Date
    // i_p_dt_imp_to, Date i_p_dt_wo_fr,
    // Date i_p_dt_wo_to, String i_created_by, String i_modified_by, String
    // i_status, String i_p_email,
    // String i_p_file_type, Integer i_p_file_size, String i_p_file_nm, String
    // i_p_batch_no,
    // String i_p_fms_ref_no) {
    // public BigInteger sp_insriplagingrpt(RIPLAgingRequest RIPLRequest, String i_p_email, String i_created_by,
    //         String i_modified_by) {
    //     {
    //         BigInteger result = BigInteger.ZERO;

    //         try {

    //             // result = storeProcedureRepository.sp_insriplagingrpt(i_p_dt_req,
    //             // i_p_imp_status, i_p_exp_status,
    //             // i_p_ent_ty, i_p_ent_nm, i_p_dt_due_fr, i_p_dt_due_to, i_p_dt_rcpt_fr,
    //             // i_p_dt_rcpt_to,
    //             // i_p_dt_imp_fr, i_p_dt_imp_to, i_p_dt_wo_fr, i_p_dt_wo_to, i_created_by,
    //             // i_modified_by, i_status,
    //             // i_p_email, i_p_file_type, i_p_file_size, i_p_file_nm, i_p_batch_no,
    //             // i_p_fms_ref_no);
    //             result = storeProcedureRepository.sp_insriplagingrpt(RIPLRequest, i_p_email, i_created_by,
    //                     i_modified_by);

    //         } catch (Exception e) {
    //             e.printStackTrace();
    //         }
    //         return result;
    //     }
    // }

    // public List<RiplAging> sp_getriplaginglistingrpt(Integer i_page, Integer
    // i_size, BigInteger i_rpt_ripl_age_id,
    // Date i_p_dt_req, Integer i_p_imp_status, Integer i_p_exp_status, String
    // i_p_ent_ty, String i_p_ent_nm, Date i_p_dt_due_fr, Date i_p_dt_due_to,
    // Date i_p_dt_rcpt_fr, Date i_p_dt_rcpt_to, Date i_p_dt_imp_fr, Date
    // i_p_dt_imp_to, Date i_p_dt_wo_fr,
    // Date i_p_dt_wo_to, String i_created_by, String i_modified_by, String
    // i_status, String i_p_email,
    // String i_p_file_type,
    // Integer i_p_file_size, String i_p_file_nm) {
    // public List<RiplAging> sp_getriplaginglistingrpt(RIPLAgingRequest RIPLRequest) {

    //     List<RiplAging> result = Collections.emptyList();

    //     try {

    //         // List<Object[]> objects =
    //         // storeProcedureRepository.sp_getriplaginglistingrpt(i_page, i_size,
    //         // i_rpt_ripl_age_id, i_p_dt_req, i_p_imp_status, i_p_exp_status, i_p_ent_ty,
    //         // i_p_ent_nm,
    //         // i_p_dt_due_fr, i_p_dt_due_to, i_p_dt_rcpt_fr, i_p_dt_rcpt_to, i_p_dt_imp_fr,
    //         // i_p_dt_imp_to,
    //         // i_p_dt_wo_fr, i_p_dt_wo_to, i_created_by, i_modified_by, i_status, i_p_email,
    //         // i_p_file_type, i_p_file_size, i_p_file_nm);
    //         List<Object[]> objects = storeProcedureRepository.sp_getriplaginglistingrpt(RIPLRequest);

    //         result = convertToGetRIPLAgingListing(objects);

    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // private List<RiplAging> convertToGetRIPLAgingListing(List<Object[]> objects) {
    //     List<RiplAging> RIPLagingList = new ArrayList<>();

    //     for (Object[] obj : objects) {
    //         RiplAging riplaging = new RiplAging();

    //         riplaging.setRpt_ripl_age_id((BigInteger) obj[0]);
    //         riplaging.setP_dt_req((Date) obj[1]);
    //         riplaging.setP_imp_status((Integer) obj[2]);
    //         riplaging.setP_exp_status((Integer) obj[3]);
    //         riplaging.setP_ent_ty((String) obj[4]);
    //         riplaging.setP_ent_nm((String) obj[5]);
    //         riplaging.setP_dt_due_fr((Date) obj[6]);
    //         riplaging.setP_dt_due_to((Date) obj[7]);
    //         riplaging.setP_dt_rcpt_fr((Date) obj[8]);
    //         riplaging.setP_dt_rcpt_to((Date) obj[9]);
    //         riplaging.setP_dt_imp_fr((Date) obj[10]);
    //         riplaging.setP_dt_imp_to((Date) obj[11]);
    //         riplaging.setP_dt_wo_fr((Date) obj[12]);
    //         riplaging.setP_dt_wo_to((Date) obj[13]);
    //         riplaging.setDt_created((Date) obj[14]);
    //         riplaging.setDt_modified((Date) obj[15]);
    //         riplaging.setCreated_by((String) obj[16]);
    //         riplaging.setModified_by((String) obj[17]);
    //         riplaging.setStatus((String) obj[18]);
    //         riplaging.setP_email((String) obj[19]);
    //         riplaging.setP_file_type((String) obj[20]);
    //         riplaging.setP_file_size((Integer) obj[21]);
    //         riplaging.setP_file_nm((String) obj[22]);
    //         riplaging.setTask_id((String) obj[23]);
    //         riplaging.setTotal((Integer) obj[24]);

    //         RIPLagingList.add(riplaging);
    //     }
    //     return RIPLagingList;
    // }

    // public Integer sp_updriplagingrpt(BigInteger i_rpt_ripl_age_id, String i_status, Integer i_p_file_size,
    //         String i_p_file_nm, String i_modified_by) {
    //     public Integer sp_updriplagingrpt(RIPLAgingRequest riplAgingRequest) {
    //     Integer result = 0;
    //     try {
    //         result = storeProcedureRepository.sp_updriplagingrpt(riplAgingRequest);
    //     } catch (Exception e) {
    //         logger.error("Exception in " + this.getClass().toString(), e);
    //     }
    //     return result;
    // }

    // public List<RiplAging> sp_getriplagingrpt(BigInteger i_rpt_ripl_age_id) {

    //     List<RiplAging> result = Collections.emptyList();
    //     try {
    //         List<Object[]> objects = storeProcedureRepository.sp_getriplagingrpt(i_rpt_ripl_age_id);
    //         result = convertToGetRIPLAging(objects);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // public Integer sp_getriplagequeuerpt() {

    //     Integer result = 0;
    //     try {
    //         result = storeProcedureRepository.sp_getriplagequeuerpt();
    //     } catch (Exception e) {
    //         logger.error("Exception in " + this.getClass().toString(), e);
    //     }
    //     return result;
    // }

    // public List<RiplAging> sp_getpendingriplagingrpt() {

    //     List<RiplAging> result = Collections.emptyList();
    //     try {
    //         List<Object[]> objects = storeProcedureRepository.sp_getpendingriplagingrpt();
    //         result = convertToGetRIPLAging(objects);
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return result;
    // }

    // private List<RiplAging> convertToGetRIPLAging(List<Object[]> objects) {
    //     List<RiplAging> RIPLagingList = new ArrayList<>();

    //     for (Object[] obj : objects) {
    //         RiplAging riplaging = new RiplAging();

    //         riplaging.setRpt_ripl_age_id((BigInteger) obj[0]);
    //         riplaging.setP_dt_req((Date) obj[1]);
    //         riplaging.setP_imp_status((Integer) obj[2]);
    //         riplaging.setP_exp_status((Integer) obj[3]);
    //         riplaging.setP_ent_ty((String) obj[4]);
    //         riplaging.setP_ent_nm((String) obj[5]);
    //         riplaging.setP_dt_due_fr((Date) obj[6]);
    //         riplaging.setP_dt_due_to((Date) obj[7]);
    //         riplaging.setP_dt_rcpt_fr((Date) obj[8]);
    //         riplaging.setP_dt_rcpt_to((Date) obj[9]);
    //         riplaging.setP_dt_imp_fr((Date) obj[10]);
    //         riplaging.setP_dt_imp_to((Date) obj[11]);
    //         riplaging.setP_dt_wo_fr((Date) obj[12]);
    //         riplaging.setP_dt_wo_to((Date) obj[13]);
    //         riplaging.setDt_created((Date) obj[14]);
    //         riplaging.setDt_modified((Date) obj[15]);
    //         riplaging.setCreated_by((String) obj[16]);
    //         riplaging.setModified_by((String) obj[17]);
    //         riplaging.setStatus((String) obj[18]);
    //         riplaging.setP_email((String) obj[19]);
    //         riplaging.setP_file_type((String) obj[20]);
    //         riplaging.setP_file_size((Integer) obj[21]);
    //         riplaging.setP_file_nm((String) obj[22]);
    //         riplaging.setTask_id((String) obj[23]);

    //         RIPLagingList.add(riplaging);
    //     }
    //     return RIPLagingList;
    // }

    // public Integer sp_getpendingriplagingrptbyid(BigInteger i_rpt_ripl_age_id) {

    //     Integer result = 0;
    //     try {
    //         result = storeProcedureRepository.sp_getpendingriplagingrptbyid(i_rpt_ripl_age_id);
    //     } catch (Exception e) {
    //         logger.error("Exception in " + this.getClass().toString(), e);
    //     }
    //     return result;
    // }

    // #endregion

}
