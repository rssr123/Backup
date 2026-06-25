package com.maven.rms.services;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceException;
import javax.persistence.StoredProcedureQuery;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.maven.rms.interfaces.IReprintReceiptService;
import com.maven.rms.models.ReprintRcpt;
import com.maven.rms.models.ReprintRcptRequest;
import com.maven.rms.models.OTC.OTCRcpt;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.models.RROrderInfo;
import com.maven.rms.models.RRPaymentItems;
import com.maven.rms.models.RRPaymentInfo;
import com.maven.rms.models.RRPaymentInfoV2;
import com.maven.rms.models.RRReceiptInfo;
import com.maven.rms.models.BankReconRequest;
import com.maven.rms.models.FeeGrpRequest;
import com.maven.rms.models.IdamanAPIDownload;
import com.maven.rms.models.IdamanAPIDownloadRequest;
import com.maven.rms.models.MTTPG;
import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OTCReceiptCancellationDetailsRequest;
import com.maven.rms.models.OTCReceiptCancellationHistoryDetails;
import com.maven.rms.models.OTCReceiptCclMTTOrderStatusRequest;
import com.maven.rms.models.OTCReceiptRpMTTOrderStatusRequest;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.RRHistoryTable;
import com.maven.rms.models.RRHistoryTableV2;
import com.maven.rms.models.RRJustification;
import com.maven.rms.repositories.IOnlinePaymentRepository;
import com.maven.rms.repositories.IReprintReceiptRepository;
import com.maven.rms.repositories.MTTRCPTRepository;
import com.maven.rms.utils.APIResponse;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReprintReceiptService implements IReprintReceiptService {

    private final IReprintReceiptRepository reprintReceiptRepository;

    public ReprintReceiptService(IReprintReceiptRepository reprintReceiptRepository, MTTRCPTRepository rcptRepo) {
        this.reprintReceiptRepository = reprintReceiptRepository;
        this.rcptRepo = rcptRepo;
    }

    @Autowired
    private IdamanAPIDownloadService idamanDS;

    private final MTTRCPTRepository rcptRepo;

    @Autowired
    private IOnlinePaymentRepository onlinePaymentRepository;

    @Autowired
    private EntityManager entityManager;

    @Value("${idaman.requestor.id}")
	private String requestorId;

    @Value("${idaman.profile.name}")
	private String profile_nm;

    @Override
    public List<ReprintRcpt> sp_getreprintreceipt(ReprintRcptRequest reprintRcptRequest) {
        List<ReprintRcpt> result = Collections.emptyList();

        List<Object[]> objects = reprintReceiptRepository.sp_getreprintreceipt(reprintRcptRequest);
        result = convertReprintRcptList(objects);

        return result;
    }

    private List<ReprintRcpt> convertReprintRcptList(List<Object[]> objects) {
        List<ReprintRcpt> reprintRcptList = new ArrayList<>();

        for (Object[] obj : objects) {
            ReprintRcpt reprintRcpt = new ReprintRcpt();
            reprintRcpt.setMtt_id((Integer) obj[0]);
            reprintRcpt.setRcpt_no((String) obj[1]);
            reprintRcpt.setOrn_no((String) obj[2]);
            reprintRcpt.setCust_nm((String) obj[3]);
            reprintRcpt.setOtc_id((BigInteger) obj[4]);
            reprintRcpt.setOtc_pymt_mode((String) obj[5]);
            reprintRcpt.setTotal_amt((BigDecimal) obj[6]);
            reprintRcpt.setOtc_counter_id((Integer) obj[7]);
            reprintRcpt.setCounter_id((String) obj[8]);
            reprintRcpt.setBranch_cd((String) obj[9]);
            reprintRcpt.setOtc_rcpt_id((Integer) obj[10]);
            reprintRcpt.setOtc_rc_rp_id((Integer) obj[11]);
            reprintRcpt.setTotal((Integer) obj[12]);

            reprintRcptList.add(reprintRcpt);
        }

        return reprintRcptList;
    }

    @Override
    public List<RROrderInfo> sp_getorderinfo_rr(ReprintRcptRequest reprintRcptRequest) {
        List<RROrderInfo> result = Collections.emptyList();

        List<Object[]> objects = reprintReceiptRepository.sp_getorderinfo_rr(reprintRcptRequest);
        result = convertRROrderInfoList(objects);//

        return result;
    }

    private List<RROrderInfo> convertRROrderInfoList(List<Object[]> objects) {
        List<RROrderInfo> rrOrderInfoList = new ArrayList<>();

        for (Object[] obj : objects) {
            RROrderInfo rrOrderInfo = new RROrderInfo();

            rrOrderInfo.setMtt_id((String) obj[0]);
            rrOrderInfo.setSs_cd((String) obj[1]);
            rrOrderInfo.setColl_slip_no((String) obj[2]);
            rrOrderInfo.setOrn_no((String) obj[3]);
            rrOrderInfo.setCust_nm((String) obj[4]);
            rrOrderInfo.setCust_phone((String) obj[5]);
            rrOrderInfo.setCust_email((String) obj[6]);
            rrOrderInfo.setCust_addr_1((String) obj[7]);
            rrOrderInfo.setCust_addr_2((String) obj[8]);
            rrOrderInfo.setCust_addr_3((String) obj[9]);
            rrOrderInfo.setCust_postcode((String) obj[10]);
            rrOrderInfo.setCust_city((String) obj[11]);
            rrOrderInfo.setCust_state((String) obj[12]);
            rrOrderInfo.setOrder_status((String) obj[13]);

            rrOrderInfoList.add(rrOrderInfo);
        }

        return rrOrderInfoList;
    }

    @Override
    public List<RRPaymentItems> sp_getpaymentitems_rr(ReprintRcptRequest reprintRcptRequest) {
        List<RRPaymentItems> result = Collections.emptyList();

        List<Object[]> objects = reprintReceiptRepository.sp_getpaymentitems_rr(reprintRcptRequest);
        result = convertRRPaymentItemsList(objects);//

        return result;
    }

    private List<RRPaymentItems> convertRRPaymentItemsList(List<Object[]> objects) {
        List<RRPaymentItems> rrPaymentItemsList = new ArrayList<>();

        for (Object[] obj : objects) {
            RRPaymentItems rrPaymentItems = new RRPaymentItems();

            rrPaymentItems.setMtt_id((String) obj[0]);
            rrPaymentItems.setItem_desc((String) obj[1]);
            rrPaymentItems.setQty((Integer) obj[2]);
            rrPaymentItems.setNet_amt((BigDecimal) obj[3]);
            rrPaymentItems.setTax_amt((BigDecimal) obj[4]);
            rrPaymentItems.setGrant_cd((String) obj[5]);
            rrPaymentItems.setDisc_amt((BigDecimal) obj[6]);
            rrPaymentItems.setGross_amt((BigDecimal) obj[7]);
            rrPaymentItems.setGross_amt_total((BigDecimal) obj[8]);

            rrPaymentItemsList.add(rrPaymentItems);
        }

        return rrPaymentItemsList;
    }

    

    @Override
    public List<RRPaymentInfo> sp_getpaymentinfo_rr(ReprintRcptRequest reprintRcptRequest) {
        List<RRPaymentInfo> result = Collections.emptyList();

        List<Object[]> objects = reprintReceiptRepository.sp_getpaymentinfo_rr(reprintRcptRequest);
        result = convertRRPaymentInfoList(objects);//

        return result;
    }

    private List<RRPaymentInfo> convertRRPaymentInfoList(List<Object[]> objects) {
        List<RRPaymentInfo> rrPaymentInfoList = new ArrayList<>();

        for (Object[] obj : objects) {
            RRPaymentInfo rrPaymentInfo = new RRPaymentInfo();

            rrPaymentInfo.setMtt_id((String) obj[0]);
            rrPaymentInfo.setOtc_id((String) obj[1]);
            rrPaymentInfo.setPayer_email((String) obj[2]);
            rrPaymentInfo.setOtc_pymt_mode((String) obj[3]);
            rrPaymentInfo.setCash_amt((BigDecimal) obj[4]);
            rrPaymentInfo.setCash_amt_total((BigDecimal) obj[5]);
            rrPaymentInfo.setOtc_che_id((String) obj[6]);
            rrPaymentInfo.setChe_status((String) obj[7]);
            rrPaymentInfo.setChe_bank_nm((String) obj[8]);
            rrPaymentInfo.setChe_no((String) obj[9]);
            rrPaymentInfo.setChe_date((Date) obj[10]);
            rrPaymentInfo.setChe_ba_acct_no((String) obj[11]);
            rrPaymentInfo.setChe_amt((BigDecimal) obj[12]);
            rrPaymentInfo.setChe_amt_total((BigDecimal) obj[13]);

            rrPaymentInfoList.add(rrPaymentInfo);
        }

        return rrPaymentInfoList;
    }

    @Override
    public List<RRPaymentInfoV2> sp_getpaymentinfo_rr_v2(ReprintRcptRequest reprintRcptRequest) {
        List<RRPaymentInfoV2> result = Collections.emptyList();

        List<Object[]> objects = reprintReceiptRepository.sp_getpaymentinfo_rr_v2(reprintRcptRequest);
        result = convertRRPaymentInfoV2List(objects);//

        return result;
    }

    private List<RRPaymentInfoV2> convertRRPaymentInfoV2List(List<Object[]> objects) {
        List<RRPaymentInfoV2> rrPaymentInfoV2List = new ArrayList<>();

        for (Object[] obj : objects) {
            RRPaymentInfoV2 rrPaymentInfoV2 = new RRPaymentInfoV2();

            rrPaymentInfoV2.setMtt_id((BigInteger) obj[0]);
            rrPaymentInfoV2.setOrn_no((String) obj[1]);
            rrPaymentInfoV2.setColl_slip_no((String) obj[2]);
            rrPaymentInfoV2.setPayer_email((String) obj[3]);
            rrPaymentInfoV2.setOtc_pymt_mode((String) obj[4]);
            rrPaymentInfoV2.setOtc_body_id((Integer) obj[5]);
            rrPaymentInfoV2.setCash_amt((BigDecimal) obj[6]);
            rrPaymentInfoV2.setChe_amt((BigDecimal) obj[7]);
            rrPaymentInfoV2.setChe_date((Date) obj[8]);
            rrPaymentInfoV2.setChe_bank_nm((String) obj[9]);
            rrPaymentInfoV2.setChe_payer_nm((String) obj[10]);
            rrPaymentInfoV2.setChe_no((String) obj[11]);
            rrPaymentInfoV2.setChe_status((String) obj[12]);
            rrPaymentInfoV2.setMo_amt((BigDecimal) obj[13]);
            rrPaymentInfoV2.setMo_rm_no((String) obj[14]);
            rrPaymentInfoV2.setMo_date((Date) obj[15]);
            rrPaymentInfoV2.setMo_payer_nm((String) obj[16]);
            rrPaymentInfoV2.setMo_id_no((String) obj[17]);
            rrPaymentInfoV2.setMo_contact_no((String) obj[18]);
            rrPaymentInfoV2.setBd_amt((BigDecimal) obj[19]);
            rrPaymentInfoV2.setBd_no((String) obj[20]);
            rrPaymentInfoV2.setBd_date((Date) obj[21]);
            rrPaymentInfoV2.setBd_bank_nm((String) obj[22]);
            rrPaymentInfoV2.setChe_ba_acct_no((String) obj[23]);
            rrPaymentInfoV2.setChe_id((String) obj[24]);
            rrPaymentInfoV2.setTrans_trace((String) obj[25]);
            rrPaymentInfoV2.setBatch_no((String) obj[26]);
            rrPaymentInfoV2.setHost_no((String) obj[27]);
            rrPaymentInfoV2.setT_id((String) obj[28]);
            rrPaymentInfoV2.setAmt((BigDecimal) obj[29]);
            rrPaymentInfoV2.setTotal((Integer) obj[30]);
            

            rrPaymentInfoV2List.add(rrPaymentInfoV2);
        }

        return rrPaymentInfoV2List;
    }

    @Override
    public List<RRReceiptInfo> sp_getreceiptinfo_rr(ReprintRcptRequest reprintRcptRequest) {
        List<RRReceiptInfo> result = Collections.emptyList();

        List<Object[]> objects = reprintReceiptRepository.sp_getreceiptinfo_rr(reprintRcptRequest);
        result = convertRRReceiptInfoList(objects);

        return result;
    }

    private List<RRReceiptInfo> convertRRReceiptInfoList(List<Object[]> objects) {
        List<RRReceiptInfo> rrReceiptInfoList = new ArrayList<>();

        for (Object[] obj : objects) {
            RRReceiptInfo rrReceiptInfo = new RRReceiptInfo();

            rrReceiptInfo.setMtt_id((BigInteger) obj[0]);
            rrReceiptInfo.setOtc_id((Integer) obj[1]);
            rrReceiptInfo.setRcpt_no((String) obj[2]);
            rrReceiptInfo.setRcpt_dt((Date) obj[3]);
            rrReceiptInfo.setRcpt_status((String) obj[4]);
            rrReceiptInfo.setRcpt_reprint((Integer) obj[5]);
            rrReceiptInfo.setVer_id((String) obj[6]);
            rrReceiptInfo.setSsdocref_id((String) obj[7]);
            rrReceiptInfo.setFile_nm((String) obj[8]);
            rrReceiptInfo.setRemark((String) obj[9]);
            rrReceiptInfo.setOrn_no((String) obj[10]);
            rrReceiptInfo.setTotal((Integer) obj[11]);

            log.info("Requesting Idaman API with parameters: requestorId=" + requestorId + ", profile_nm=" + profile_nm + ", orn_no=" + rrReceiptInfo.getOrn_no() + ", ver_id=" + rrReceiptInfo.getVer_id());


            // Use orn_no, ver_id, requestorId, and profileNm to retrieve filename and file
            // content from idaman_api_downloadDoc
           try {
                List<IdamanAPIDownload> data = idamanDS.idaman_api_downloadDoc(
                        new IdamanAPIDownloadRequest(
                                requestorId,
                                profile_nm,
                                rrReceiptInfo.getOrn_no(),
                                rrReceiptInfo.getVer_id(),
                                rrReceiptInfo.getSsdocref_id()));

                if (data == null || data.isEmpty()) {
                    log.error("No data found in Idaman API for orn_no: " + rrReceiptInfo.getOrn_no()
                            + ", ver_id: "
                            + rrReceiptInfo.getVer_id());
                            rrReceiptInfo.setFile_content("Cannot find Idaman PDF");
                } else {
                    String fileString = data.get(0).getFile_content();
                    if (fileString != null && !fileString.isEmpty()) {
                        rrReceiptInfo.setFile_type(
                                data.get(0).getFile_nm().substring(data.get(0).getFile_nm().lastIndexOf(".") + 1));
                                rrReceiptInfo.setFile_content(fileString);
                                rrReceiptInfo.setIdaman_file_name(data.get(0).getFile_nm()); // Set the Idaman
                                                                                                    // file name

                    } else {
                        log.error("Cannot get base64 encoded string. Desc: " + data.get(0).getDesc() + ", orn_no: "
                                + rrReceiptInfo.getOrn_no() + ", ver_id: "
                                + rrReceiptInfo.getVer_id() + ", ssdocref_id: "
                                + rrReceiptInfo.getSsdocref_id() + ", requestorId: " + requestorId
                                + ", profileNm: " + profile_nm);
                                rrReceiptInfo.setFile_content("Cannot grab PDF from IDAMAN.");
                    }
                }
            } catch (IOException e) {
                log.error("IOException occurred while fetching data from Idaman API for orn_no: "
                        + rrReceiptInfo.getOrn_no() + ", ver_id: " + rrReceiptInfo.getVer_id(), e);
                rrReceiptInfo.setFile_content("Error fetching PDF from IDAMAN.");
            }

            rrReceiptInfoList.add(rrReceiptInfo);
        }

        return rrReceiptInfoList;
    }

    @Override
    public List<RRHistoryTable> sp_gethistorytable_rr(ReprintRcptRequest reprintRcptRequest) {
        List<RRHistoryTable> result = Collections.emptyList();

        List<Object[]> objects = reprintReceiptRepository.sp_gethistorytable_rr(reprintRcptRequest);
        result = convertRRHistoryTableList(objects);//

        return result;
    }

    private List<RRHistoryTable> convertRRHistoryTableList(List<Object[]> objects) {
        List<RRHistoryTable> rrHistoryTableList = new ArrayList<>();

        for (Object[] obj : objects) {
            RRHistoryTable rrHistoryTable = new RRHistoryTable();

            rrHistoryTable.setMtt_id((String) obj[0]);
            rrHistoryTable.setAction((String) obj[1]);
            rrHistoryTable.setDt_action((Date) obj[2]);
            rrHistoryTable.setOtc_status((String) obj[3]);
            rrHistoryTable.setCounter_id((String) obj[4]);
            rrHistoryTable.setAct_by((String) obj[5]);
            rrHistoryTable.setStatus((String) obj[6]);

            rrHistoryTableList.add(rrHistoryTable);
        }

        return rrHistoryTableList;
    }

    @Override
    public List<RRHistoryTableV2> sp_getotcrcptrpnthistorydetails(
            ReprintRcptRequest reprintRcptRequest) {

        List<RRHistoryTableV2> result = Collections.emptyList();

        List<Object[]> objects = reprintReceiptRepository.sp_gethistorytable_rr_v2(reprintRcptRequest);

        result = convertToGetOTCRpntHistoryDetails(objects);

        return result;
    }

    private List<RRHistoryTableV2> convertToGetOTCRpntHistoryDetails(List<Object[]> objects) {
        List<RRHistoryTableV2> otcrcptrpntHistoryDetsList = new ArrayList<>();

        for (Object[] obj : objects) {
            RRHistoryTableV2 rrHistoryTableV2 = new RRHistoryTableV2();

            rrHistoryTableV2.setMtt_id((BigInteger) obj[0]);
            rrHistoryTableV2.setOtc_id((Integer) obj[1]);
            rrHistoryTableV2.setAction((String) obj[2]);
            rrHistoryTableV2.setDt_action((Date) obj[3]);
            rrHistoryTableV2.setOtc_status((String) obj[4]);
            rrHistoryTableV2.setCounter_id((String) obj[5]);
            rrHistoryTableV2.setAct_by((String) obj[6]);
            rrHistoryTableV2.setNm_en((String) obj[7]);
            rrHistoryTableV2.setBcm_desc((String) obj[8]);
            rrHistoryTableV2.setTotal((Integer) obj[9]);

            otcrcptrpntHistoryDetsList.add(rrHistoryTableV2);

        }
        return otcrcptrpntHistoryDetsList;
    }


    @Override
    public List<RRReceiptInfo> sp_getmttrcptrp(ReprintRcptRequest reprintRcptRequest) {

        List<RRReceiptInfo> result = Collections.emptyList();

        List<Object[]> objects = reprintReceiptRepository.sp_getmttrcptrp(reprintRcptRequest);

        result = convertToGetMttRcptRp(objects);

        return result;
    }

    private List<RRReceiptInfo> convertToGetMttRcptRp(List<Object[]> objects) {
        List<RRReceiptInfo> mttrcptrpList = new ArrayList<>();

        for (Object[] obj : objects) {
            RRReceiptInfo mttRcptRp = new RRReceiptInfo();

            mttRcptRp.setRcpt_no((String) obj[0]);
            mttRcptRp.setVer_id((String) obj[1]);
            mttRcptRp.setSsdocref_id((String) obj[2]);
            
            mttrcptrpList.add(mttRcptRp);

        }
        return mttrcptrpList;
    }


    @Override
    public List<RRJustification> sp_getjustification_rr(ReprintRcptRequest reprintRcptRequest) {
        List<RRJustification> result = Collections.emptyList();
    
        List<Object[]> objects = reprintReceiptRepository.sp_getjustification_rr(reprintRcptRequest);
        result = convertRRJustificationList(objects);
    
        return result;
    }
    
    private List<RRJustification> convertRRJustificationList(List<Object[]> objects) {
        List<RRJustification> rrJustificationList = new ArrayList<>();
    
        for (Object[] obj : objects) {
            RRJustification rrJustification = new RRJustification();
    
            rrJustification.setOtc_id((String) obj[0]);
            rrJustification.setMtt_id((String) obj[1]);
            rrJustification.setOtc_rc_rp_id((String) obj[2]);
            rrJustification.setOtc_rcpt_id((String) obj[3]);
            rrJustification.setJustication((String) obj[4]);
            rrJustification.setDt_created((Date) obj[5]);
            rrJustification.setDt_modified((Date) obj[6]);
            rrJustification.setCreated_by((String) obj[7]);
            rrJustification.setModified_by((String) obj[8]);
            rrJustification.setStatus((String) obj[9]);
            rrJustification.setSsdocref_id((String) obj[10]);
            rrJustification.setVer_id_otc((String) obj[11]);
            rrJustification.setVer_id_mtt((String) obj[12]);
            rrJustification.setRcpt_no((String) obj[13]);
    
            log.info("Requesting Idaman API with parameters: requestorId=" + requestorId + ", profile_nm=" + profile_nm + 
                     ", rcpt_no=" + rrJustification.getRcpt_no() + ", ver_id=" + rrJustification.getVer_id_mtt());
    
            // Use rcpt_no, ver_id, requestorId, and profileNm to retrieve filename and file content from Idaman API
            try {
                List<IdamanAPIDownload> data = idamanDS.idaman_api_downloadDoc(
                        new IdamanAPIDownloadRequest(
                            requestorId,
                            profile_nm,
                            rrJustification.getRcpt_no(),
                            rrJustification.getVer_id_mtt(),
                            rrJustification.getSsdocref_id()));
    
                if (data == null || data.isEmpty()) {
                    log.error("No data found in Idaman API for rcpt_no: " + rrJustification.getRcpt_no() + ", ver_id: "
                            + rrJustification.getVer_id_mtt());
                    rrJustification.setFile_content("Cannot find Idaman PDF");
                } else {
                    String fileString = data.get(0).getFile_content();
                    if (fileString != null && !fileString.isEmpty()) {
                        rrJustification.setFile_type(
                                data.get(0).getFile_nm().substring(data.get(0).getFile_nm().lastIndexOf(".") + 1));
                        rrJustification.setFile_content(fileString);
                        rrJustification.setIdaman_file_name(data.get(0).getFile_nm()); // Set the Idaman file name
                    } else {
                        log.error("Cannot get base64 encoded string. Desc: " + data.get(0).getDesc() + ", rcpt_no: "
                                + rrJustification.getRcpt_no() + ", ver_id: " + rrJustification.getVer_id_mtt() 
                                + ", ssdocref_id: " + rrJustification.getSsdocref_id() 
                                + ", requestorId: " + requestorId + ", profileNm: " + profile_nm);
                        rrJustification.setFile_content("Cannot grab PDF from IDAMAN.");
                    }
                }
            } catch (IOException e) {
                log.error("IOException occurred while fetching data from Idaman API for rcpt_no: "
                        + rrJustification.getRcpt_no() + ", ver_id: " + rrJustification.getVer_id_mtt(), e);
                rrJustification.setFile_content("Error fetching PDF from IDAMAN.");
            }
    
            rrJustificationList.add(rrJustification);
        }
    
        return rrJustificationList;
    }

    @Override
    public Integer sp_updrcptcount_rr (ReprintRcptRequest reprintRcptRequest) {
        Integer result = 0;
        try {
            result = reprintReceiptRepository.sp_updrcptcount_rr(reprintRcptRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Integer sp_updrcptcount_mtt (ReprintRcptRequest reprintRcptRequest) {
        Integer result = 0;
        try {
            result = reprintReceiptRepository.sp_updrcptcount_mtt(reprintRcptRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Integer sp_updrcptjust_rr(ReprintRcptRequest reprintRcptRequest, int i_otc_rc_rp_id, int i_otc_rcpt_id, String i_justication, String i_modified_by) {
        Integer result = 0;
        try {
            result = reprintReceiptRepository.sp_updrcptjust_rr(reprintRcptRequest, i_otc_rc_rp_id, i_otc_rcpt_id, i_justication, i_modified_by);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    
   

    ////////////////////////////////

    // public List<Map<String, Object>> getReceipt(String orderNo) {
    //     List<Map<String, Object>> resultList = new ArrayList<>();
    //     Map<String, Object> item = new HashMap<>();

    //     if (orderNo == null || orderNo.trim().isEmpty()) {
    //         log.error("mttId is null or empty!");
    //         item.put("Error", "mttId is null!");
    //         return Collections.singletonList(item);
    //     }

    //     try {
    //         StoredProcedureQuery query = entityManager.createStoredProcedureQuery("sp_getrcptinfo_rr")
    //                 .registerStoredProcedureParameter("i_mtt_id", String.class, ParameterMode.IN)
    //                 .setParameter("i_mtt_id", orderNo);

    //         // Execute the stored procedure and get the result list
    //         List<Object[]> results = query.getResultList();

    //         if (results == null || results.isEmpty()) {
    //             log.error("No data returned from stored procedure for mttId: " + orderNo);
    //             item.put("Error", "No receipt found for the given mttId.");
    //             return Collections.singletonList(item);
    //         }

    //         // Process each result row
    //         for (Object[] row : results) {
    //             Map<String, Object> resultMap = new HashMap<>();
    //             resultMap.put("mtt_id", row[0]);
    //             resultMap.put("rcpt_no", row[1]);
    //             resultMap.put("file_nm", row[2]);
    //             resultMap.put("rcpt_dt", row[3]);
    //             resultMap.put("rcpt_status", row[4]);
    //             resultMap.put("rcpt_reprint", row[5]);
    //             resultMap.put("ssdocref_id", row[6]);
    //             resultMap.put("ver_id", row[7]);
    //             resultMap.put("orn_no", row[8]);

    //             // Add logic for Idaman API Download
    //             List<IdamanAPIDownload> data;
    //             try {
    //                 data = idamanDS.idaman_api_downloadDoc(
    //                         new IdamanAPIDownloadRequest(
    //                                 resultMap.get("rcpt_no").toString(),
    //                                 resultMap.get("ver_id").toString(),
    //                                 requestorId,
    //                                 profile_nm));
    //             } catch (IOException e) {
    //                 log.error("IOException occurred while calling Idaman API: ", e);
    //                 resultMap.put("Error", "Cannot find Idaman PDF due to IOException.");
    //                 resultList.add(resultMap);
    //                 continue;
    //             }

    //             if (data == null || data.isEmpty()) {
    //                 log.error("No data found in Idaman API for rcpt_no: " + resultMap.get("rcpt_no"));

    //                 resultMap.put("Error", "Cannot find Idaman PDF");
    //             } else {
    //                 String fileString = data.get(0).getFile_content();
    //                 if (fileString != null && !fileString.isEmpty()) {
    //                     resultMap.put("file_type",
    //                             data.get(0).getFile_nm().substring(data.get(0).getFile_nm().lastIndexOf(".") + 1));
    //                     resultMap.put("file_data", fileString);
    //                 } else {
    //                     log.error("Cannot get base64 encoded string. Desc: " + data.get(0).getDesc());
    //                     resultMap.put("Error", "Cannot grab PDF from IDAMAN.");
    //                 }
    //             }

    //             resultList.add(resultMap);
    //         }
    //     } catch (DataAccessException | PersistenceException e) {
    //         log.error("Error occurred while processing the request: ", e);
    //         item.put("Error", "An error occurred while processing the request.");
    //         return Collections.singletonList(item);
    //     }

    //     return resultList;
    // }

    public MTTRCPT sp_getmttrcptinfo(BigInteger mtt_id) {

        MTTRCPT result = new MTTRCPT();

        try {

            result = convertToMTTRCPT(reprintReceiptRepository.sp_getmttrcptinfo(mtt_id));
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

//     private MTTRCPT convertToMTTRCPT(Object[] obj) {

//         MTTRCPT mttrcpt = new MTTRCPT();

//         if (obj != null && obj.length > 0) {
//             try {
//                 //((Integer) obj[0]);
//               //  otcrcptcclBalStatusDets.setCount((Integer) obj[0]);
//                 // Assuming obj array contains values in a specific order
//                 mttrcpt.setMttRcptID((Integer) obj[0]);
//                 mttrcpt.setRcptNo((String) obj[1]);
//                 mttrcpt.setRcptDt(convertToLocalDateTime((Date) obj[2]));
//                 mttrcpt.setRcptStatus((String) obj[3]);
//                 mttrcpt.setRcptReprint((Integer) obj[4]);
//                 mttrcpt.setIsUploaded((Integer) obj[5]);
//                 mttrcpt.setDtCreated(convertToLocalDateTime((Date) obj[6]));
//                 mttrcpt.setDtModified(convertToLocalDateTime((Date) obj[7]));
//                 mttrcpt.setCreatedBy((String) obj[8]);
//                 mttrcpt.setModifiedBy((String) obj[9]);
//                 mttrcpt.setRcptUUID((String) obj[10]);
//                 mttrcpt.setVersionId((String) obj[11]);
//                 //mttrcpt.setMtt_pg_id((long) obj[12]);
//                 // Add more fields as required and map them accordingly
//             } catch (Exception e) {
//                 e.printStackTrace();
//             }
//         } else {
//             System.out.println("The input object array is null or empty.");
//         }

//         return mttrcpt;
//     }


//     private LocalDateTime convertToLocalDateTime(Date date) {
//     if (date == null) {
//         return null; // Handle null input gracefully
//     }
//     return date.toInstant()
//                .atZone(ZoneId.systemDefault())
//                .toLocalDateTime();
// }

    

    @Transactional(readOnly = true, rollbackFor = Exception.class)
    public MTTRCPT convertToMTTRCPT(Object[] obj) {
        try {
            String rcptNo = (String) obj[3];

            MTTRCPT mttrcpt = rcptRepo.sp_getRcptByRcptNo(rcptNo).orElse(null);
            System.out.println("MTTRCPT found: " + (mttrcpt.getRmsMTT().getCust_nm()));
            System.out.println("MTTRCPT found: " + (mttrcpt.getRmsMTT().getCust_phone()));
            System.out.println("MTTRCPT found: " + (mttrcpt.getRmsMTT().getCust_email()));
            System.out.println("MTTRCPT found: " + (mttrcpt));

            return mttrcpt;

        } catch (Exception e) {
            log.error("Error in convertToMTTRCPT: ", e);
            return null;
        }
    }



    // private MTTRCPT convertToMTTRCPT(Object[] obj) {
    //     MTTRCPT rcpt = new MTTRCPT();

    //     rcpt.setMttRcptID((Integer) obj[0]);

    //     OnlinePayment mtt = new OnlinePayment();
    //     mtt.setMttId((Integer) obj[1]);
    //     rcpt.setRmsMTT(mtt);

    //     // Set MTTPG with mtt_pg_id
    //     MTTPG pg = new MTTPG();
    //     pg.setMttPgId(obj[2] != null ? ((Number) obj[2]).longValue() : null);
    //     rcpt.setMttPG(pg);

    //     rcpt.setRcptNo((String) obj[3]);
    //     rcpt.setRcptDt(obj[4] != null ? ((java.sql.Timestamp) obj[4]).toLocalDateTime() : null);
    //     rcpt.setRcptStatus((String) obj[5]);
    //     rcpt.setRcptReprint((Integer) obj[6]);
    //     rcpt.setIsUploaded((Integer) obj[7]);
    //     rcpt.setDtCreated(obj[8] != null ? ((java.sql.Timestamp) obj[8]).toLocalDateTime() : null);
    //     rcpt.setDtModified(obj[9] != null ? ((java.sql.Timestamp) obj[9]).toLocalDateTime() : null);
    //     rcpt.setCreatedBy((String) obj[10]);
    //     rcpt.setModifiedBy((String) obj[11]);
    //     rcpt.setRcptUUID((String) obj[12]);
    //     rcpt.setVersionId((String) obj[13]);

    //     // Set MTTPG with just the ID, assuming lazy loading will handle the rest
    //     // MTTPG pg = new MTTPG();
    //     // pg.setMttPgId(obj[12] != null ? ((Number) obj[12]).longValue() : null);
    //     // rcpt.setMttPG(pg);

    //     return rcpt;
    // }



    @Override
    public Integer sp_updotcrcpt(Integer i_otc_rcpt_id, String i_ver_id, String i_ssdocref_id, String file_nm) {
        Integer result = 0;
        try {
            result = reprintReceiptRepository.sp_updotcrcpt(i_otc_rcpt_id, i_ver_id, i_ssdocref_id, file_nm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    @Override
    public Integer sp_updmtt_orderstatus(OTCReceiptRpMTTOrderStatusRequest mttOrderStatusRequest) {

        Integer result = 0;

        result = reprintReceiptRepository.sp_updmtt_orderstatus(mttOrderStatusRequest);

        return result;
    }

    @Override
    public Integer sp_checkrcptcl(OTCReceiptRpMTTOrderStatusRequest mttOrderStatusRequest) {

        Integer result = 0;

        result = reprintReceiptRepository.sp_checkrcptcl(mttOrderStatusRequest);

        return result;
    }

    @Override
    public OTCRcpt sp_getotcreceiptrp(OTCReceiptRpMTTOrderStatusRequest mttOrderStatusRequest) {
        OTCRcpt result = new OTCRcpt();
        Object[] resultSet = reprintReceiptRepository.sp_getotcreceiptrp(mttOrderStatusRequest);
        result.setOtc_id((Integer) resultSet[0]);
        result.setRcptNo((String) resultSet[1]);
        result.setRcpt_dt((Date) resultSet[2]);
        result.setRcpt_status((String) resultSet[3]);
        result.setRcpt_reprint((Integer) resultSet[4]);
        result.setIs_uploaded((Integer) resultSet[5]);
        result.setVer_id((String) resultSet[6]);
        result.setSsdocref_id((String) resultSet[7]);
        result.setCreated_by((String) resultSet[8]);
        result.setModified_by((String) resultSet[9]);
        result.setFile_nm((String) resultSet[10]);
        result.setRemark((String) resultSet[11]);
        result.setStatus((String) resultSet[12]);
        result.setOtc_rcpt_id((Integer) resultSet[13]);
        return result;
    }



}
