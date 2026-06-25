package com.maven.rms.services;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.interfaces.IMFTService;
import com.maven.rms.interfaces.IOTCRcptCclService;
import com.maven.rms.models.IdamanAPIDownload;
import com.maven.rms.models.IdamanAPIDownloadRequest;
import com.maven.rms.models.MFTWF;
import com.maven.rms.models.MFTWFRequest;
import com.maven.rms.models.MTTOrderStatusRequest;
import com.maven.rms.models.MTTPG;
import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OTCReceiptCancellationAssignToRequest;
import com.maven.rms.models.OTCReceiptCancellationBalStatusDetails;
import com.maven.rms.models.OTCReceiptCancellationBalStatusDetailsRequest;
import com.maven.rms.models.OTCReceiptCancellationCreatedByRequest;
import com.maven.rms.models.OTCReceiptCancellationDetailsRequest;
import com.maven.rms.models.OTCReceiptCancellationHistoryDetails;
import com.maven.rms.models.OTCReceiptCancellationHistoryDetailsAudit;
import com.maven.rms.models.OTCReceiptCancellationListing;
import com.maven.rms.models.OTCReceiptCancellationListingRequest;
import com.maven.rms.models.OTCReceiptCancellationMyTaskListing;
import com.maven.rms.models.OTCReceiptCancellationMyTaskListingRequest;
import com.maven.rms.models.OTCReceiptCancellationOrderInfoDetails;
import com.maven.rms.models.OTCReceiptCancellationPaymentInfoDetails;
import com.maven.rms.models.OTCReceiptCancellationPymtItem;
import com.maven.rms.models.OTCReceiptCancellationRCStatusDetails;
import com.maven.rms.models.OTCReceiptCancellationReceiptInfoDetails;
import com.maven.rms.models.OTCReceiptCancellationRequest;
import com.maven.rms.models.OTCReceiptCancellationSupervisor;
import com.maven.rms.models.OTCReceiptCancellationSupervisorRequest;
import com.maven.rms.models.OTCReceiptCancellationTaskAndReqInfoApproval;
import com.maven.rms.models.OTCReceiptCancellationTaskAndReqInfoApprovalRequest;
import com.maven.rms.models.OTCReceiptCancellationUpdateRequest;
import com.maven.rms.models.OTCReceiptCclMTTOrderStatusRequest;
import com.maven.rms.models.OTCReceiptCheck;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.OTC.OTCCollectionReceiptingPymtItem;
import com.maven.rms.models.OTC.OTCPaymentDone;
import com.maven.rms.models.OTC.OTCRcpt;
import com.maven.rms.models.OTC.OTCRcptRequest;
import com.maven.rms.models.OTC.OTCollectionReceiptingRequest;
import com.maven.rms.repositories.MFTRepository;
import com.maven.rms.repositories.MTTRCPTRepository;
import com.maven.rms.repositories.OTCRcptCclRepository;
import com.maven.rms.repositories.OTC.OTCCollectionReceiptingRepository;

import org.springframework.beans.factory.annotation.Value;

@Service
@Slf4j
public class OTCRcptCclService implements IOTCRcptCclService {

    @Autowired
    private IdamanAPIDownloadService idamanDS;

    @Autowired
    private EntityManager entityManager;

    @Value("${idaman.requestor.id}")
    private String requestorId;

    @Value("${idaman.profile.name}")
    private String profile_nm;
    // private static final Logger logger =
    // LoggerFactory.getLogger(StoreProcedureService.class);
    private final OTCRcptCclRepository otcrcptcclRepository;
    private final OTCCollectionReceiptingRepository otcCollectionReceiptingRepository;

    private final MTTRCPTRepository rcptRepo;

    public OTCRcptCclService(OTCRcptCclRepository otcrcptcclRepository, MTTRCPTRepository rcptRepo,
    OTCCollectionReceiptingRepository otcCollectionReceiptingRepository) {
        this.otcrcptcclRepository = otcrcptcclRepository;
        this.rcptRepo = rcptRepo;
        this.otcCollectionReceiptingRepository = otcCollectionReceiptingRepository;
    }

    @Override
    public List<OTCReceiptCancellationListing> sp_getotcrcptccllisting(
            OTCReceiptCancellationListingRequest otcrcptcclRequest) {

        List<OTCReceiptCancellationListing> result = Collections.emptyList();

        List<Object[]> objects = otcrcptcclRepository.sp_getotcrcptccllisting(otcrcptcclRequest);

        result = convertToGetOTCRcptCclListing(objects);

        return result;
    }

    private List<OTCReceiptCancellationListing> convertToGetOTCRcptCclListing(List<Object[]> objects) {
        List<OTCReceiptCancellationListing> otcrcptcclList = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCReceiptCancellationListing otcrcptccl = new OTCReceiptCancellationListing();

            otcrcptccl.setMtt_id((Integer) obj[0]);
            otcrcptccl.setRcpt_no((String) obj[1]);
            otcrcptccl.setOrn_no((String) obj[2]);
            otcrcptccl.setCust_nm((String) obj[3]);
            otcrcptccl.setOtc_id((BigInteger) obj[4]);
            otcrcptccl.setOtc_pymt_mode((String) obj[5]);
            otcrcptccl.setTotal_amt((BigDecimal) obj[6]);
            otcrcptccl.setOtc_counter_id((Integer) obj[7]);
            otcrcptccl.setCounter_id((String) obj[8]);
            otcrcptccl.setBranch_cd((String) obj[9]);
            otcrcptccl.setNm_en((String) obj[10]);
            otcrcptccl.setTotal((Integer) obj[11]);

            otcrcptcclList.add(otcrcptccl);
        }
        return otcrcptcclList;
    }

    @Override
    public List<OTCReceiptCancellationOrderInfoDetails> sp_getotcrcptccloderinfodetails(
            OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {

        List<OTCReceiptCancellationOrderInfoDetails> result = Collections.emptyList();

        List<Object[]> objects = otcrcptcclRepository.sp_getotcrcptccloderinfodetails(otcrcptcclDetsRequest);

        result = convertToGetOTCRcptCclOrderInfoDetails(objects);

        return result;
    }

    private List<OTCReceiptCancellationOrderInfoDetails> convertToGetOTCRcptCclOrderInfoDetails(
            List<Object[]> objects) {
        List<OTCReceiptCancellationOrderInfoDetails> otcrcptcclOrderInfoDetsList = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCReceiptCancellationOrderInfoDetails otcrcptcclorderInfoDetails = new OTCReceiptCancellationOrderInfoDetails();

            otcrcptcclorderInfoDetails.setMtt_id((BigInteger) obj[0]);
            otcrcptcclorderInfoDetails.setSs_cd((String) obj[1]);
            otcrcptcclorderInfoDetails.setOrn_no((String) obj[2]);
            otcrcptcclorderInfoDetails.setCust_nm((String) obj[3]);
            otcrcptcclorderInfoDetails.setCust_addr_1((String) obj[4]);
            otcrcptcclorderInfoDetails.setCust_addr_2((String) obj[5]);
            otcrcptcclorderInfoDetails.setCust_addr_3((String) obj[6]);
            otcrcptcclorderInfoDetails.setCust_postcode((String) obj[7]);
            otcrcptcclorderInfoDetails.setCust_city((String) obj[8]);
            otcrcptcclorderInfoDetails.setCust_state((String) obj[9]);
            otcrcptcclorderInfoDetails.setCust_email((String) obj[10]);
            otcrcptcclorderInfoDetails.setCust_phone((String) obj[11]);
            otcrcptcclorderInfoDetails.setTotal_amt((BigDecimal) obj[12]);
            otcrcptcclorderInfoDetails.setOrder_status((String) obj[13]);
            otcrcptcclorderInfoDetails.setColl_slip_no((String) obj[14]);

            otcrcptcclOrderInfoDetsList.add(otcrcptcclorderInfoDetails);
        }
        return otcrcptcclOrderInfoDetsList;
    }

    @Override
    public List<OTCReceiptCancellationPaymentInfoDetails> sp_getotcrcptcclpymtinfodetails(
            OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {

        List<OTCReceiptCancellationPaymentInfoDetails> result = Collections.emptyList();

        List<Object[]> objects = otcrcptcclRepository.sp_getotcrcptcclpymtinfodetails(otcrcptcclDetsRequest);

        result = convertToGetOTCRcptCclPaymentInfoDetails(objects);

        return result;
    }

    private List<OTCReceiptCancellationPaymentInfoDetails> convertToGetOTCRcptCclPaymentInfoDetails(
            List<Object[]> objects) {
        List<OTCReceiptCancellationPaymentInfoDetails> otcrcptcclPaymentInfoDetsList = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCReceiptCancellationPaymentInfoDetails otcrcptcclPaymentInfoDetails = new OTCReceiptCancellationPaymentInfoDetails();

            otcrcptcclPaymentInfoDetails.setMtt_id((BigInteger) obj[0]);
            otcrcptcclPaymentInfoDetails.setOrn_no((String) obj[1]);
            otcrcptcclPaymentInfoDetails.setColl_slip_no((String) obj[2]);
            otcrcptcclPaymentInfoDetails.setPayer_email((String) obj[3]);
            otcrcptcclPaymentInfoDetails.setOtc_pymt_mode((String) obj[4]);
            otcrcptcclPaymentInfoDetails.setOtc_body_id((Integer) obj[5]);
            otcrcptcclPaymentInfoDetails.setCash_amt((BigDecimal) obj[6]);
            otcrcptcclPaymentInfoDetails.setChe_amt((BigDecimal) obj[7]);
            otcrcptcclPaymentInfoDetails.setChe_date((Date) obj[8]);
            otcrcptcclPaymentInfoDetails.setChe_bank_nm((String) obj[9]);
            otcrcptcclPaymentInfoDetails.setChe_payer_nm((String) obj[10]);
            otcrcptcclPaymentInfoDetails.setChe_no((String) obj[11]);
            otcrcptcclPaymentInfoDetails.setChe_status((String) obj[12]);
            otcrcptcclPaymentInfoDetails.setMo_amt((BigDecimal) obj[13]);
            otcrcptcclPaymentInfoDetails.setMo_rm_no((String) obj[14]);
            otcrcptcclPaymentInfoDetails.setMo_date((Date) obj[15]);
            otcrcptcclPaymentInfoDetails.setMo_payer_nm((String) obj[16]);
            otcrcptcclPaymentInfoDetails.setMo_id_no((String) obj[17]);
            otcrcptcclPaymentInfoDetails.setMo_contact_no((String) obj[18]);
            otcrcptcclPaymentInfoDetails.setBd_amt((BigDecimal) obj[19]);
            otcrcptcclPaymentInfoDetails.setBd_no((String) obj[20]);
            otcrcptcclPaymentInfoDetails.setBd_date((Date) obj[21]);
            otcrcptcclPaymentInfoDetails.setBd_bank_nm((String) obj[22]);
            otcrcptcclPaymentInfoDetails.setChe_ba_acct_no((String) obj[23]);
            otcrcptcclPaymentInfoDetails.setChe_id((String) obj[24]);
            otcrcptcclPaymentInfoDetails.setTrans_trace((String) obj[25]);
            otcrcptcclPaymentInfoDetails.setBatch_no((String) obj[26]);
            otcrcptcclPaymentInfoDetails.setHost_no((String) obj[27]);
            otcrcptcclPaymentInfoDetails.setT_id((String) obj[28]);
            otcrcptcclPaymentInfoDetails.setAmt((BigDecimal) obj[29]);
            otcrcptcclPaymentInfoDetails.setTotal((Integer) obj[30]);

            otcrcptcclPaymentInfoDetsList.add(otcrcptcclPaymentInfoDetails);
        }
        return otcrcptcclPaymentInfoDetsList;
    }

    @Override
    public List<OTCReceiptCancellationReceiptInfoDetails> sp_getotcrcptcclrcptinfodetails(
            OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {

        List<OTCReceiptCancellationReceiptInfoDetails> result = Collections.emptyList();

        List<Object[]> objects = otcrcptcclRepository.sp_getotcrcptcclrcptinfodetails(otcrcptcclDetsRequest);

        result = convertToGetOTCRcptCclReceiptInfoDetails(objects);

        return result;
    }

    private List<OTCReceiptCancellationReceiptInfoDetails> convertToGetOTCRcptCclReceiptInfoDetails(
            List<Object[]> objects) {
        List<OTCReceiptCancellationReceiptInfoDetails> otcrcptcclReceiptInfoDetsList = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCReceiptCancellationReceiptInfoDetails otcrcptcclReceiptInfoDetails = new OTCReceiptCancellationReceiptInfoDetails();

            otcrcptcclReceiptInfoDetails.setMtt_id((BigInteger) obj[0]);
            otcrcptcclReceiptInfoDetails.setOtc_id((Integer) obj[1]);
            otcrcptcclReceiptInfoDetails.setRcpt_no((String) obj[2]);
            otcrcptcclReceiptInfoDetails.setRcpt_dt((Date) obj[3]);
            otcrcptcclReceiptInfoDetails.setRcpt_status((String) obj[4]);
            otcrcptcclReceiptInfoDetails.setRcpt_reprint((Integer) obj[5]);
            otcrcptcclReceiptInfoDetails.setVer_id((String) obj[6]);
            otcrcptcclReceiptInfoDetails.setSsdocref_id((String) obj[7]);
            otcrcptcclReceiptInfoDetails.setFile_nm((String) obj[8]);
            otcrcptcclReceiptInfoDetails.setRemark((String) obj[9]);
            otcrcptcclReceiptInfoDetails.setOrn_no((String) obj[10]);
            otcrcptcclReceiptInfoDetails.setTotal((Integer) obj[11]);
            //temporary off start
            // log.info("Requesting Idaman API with parameters: requestorId=" + requestorId + ", profile_nm=" + profile_nm
            //         + ", orn_no=" + otcrcptcclReceiptInfoDetails.getOrn_no() + ", ver_id="
            //         + otcrcptcclReceiptInfoDetails.getVer_id());

            // // Use orn_no, ver_id, requestorId, and profileNm to retrieve filename and file
            // // content from idaman_api_downloadDoc
            // try {
            //     List<IdamanAPIDownload> data = idamanDS.idaman_api_downloadDoc(
            //             new IdamanAPIDownloadRequest(
            //                     requestorId,
            //                     profile_nm,
            //                     otcrcptcclReceiptInfoDetails.getOrn_no(),
            //                     otcrcptcclReceiptInfoDetails.getVer_id(),
            //                     otcrcptcclReceiptInfoDetails.getSsdocref_id()));

            //     if (data == null || data.isEmpty()) {
            //         log.error("No data found in Idaman API for orn_no: " + otcrcptcclReceiptInfoDetails.getOrn_no()
            //                 + ", ver_id: "
            //                 + otcrcptcclReceiptInfoDetails.getVer_id());
            //         otcrcptcclReceiptInfoDetails.setFile_content("Cannot find Idaman PDF");
            //     } else {
            //         String fileString = data.get(0).getFile_content();
            //         if (fileString != null && !fileString.isEmpty()) {
            //             otcrcptcclReceiptInfoDetails.setFile_type(
            //                     data.get(0).getFile_nm().substring(data.get(0).getFile_nm().lastIndexOf(".") + 1));
            //             otcrcptcclReceiptInfoDetails.setFile_content(fileString);
            //             otcrcptcclReceiptInfoDetails.setIdaman_file_name(data.get(0).getFile_nm()); // Set the Idaman
            //                                                                                         // file name

            //         } else {
            //             log.error("Cannot get base64 encoded string. Desc: " + data.get(0).getDesc() + ", orn_no: "
            //                     + otcrcptcclReceiptInfoDetails.getOrn_no() + ", ver_id: "
            //                     + otcrcptcclReceiptInfoDetails.getVer_id() + ", ssdocref_id: "
            //                     + otcrcptcclReceiptInfoDetails.getSsdocref_id() + ", requestorId: " + requestorId
            //                     + ", profileNm: " + profile_nm);
            //             otcrcptcclReceiptInfoDetails.setFile_content("Cannot grab PDF from IDAMAN.");
            //         }
            //     }
            // } catch (IOException e) {
            //     log.error("IOException occurred while fetching data from Idaman API for orn_no: "
            //             + otcrcptcclReceiptInfoDetails.getOrn_no() + ", ver_id: "
            //             + otcrcptcclReceiptInfoDetails.getVer_id(), e);
            //     otcrcptcclReceiptInfoDetails.setFile_content("Error fetching PDF from IDAMAN.");
            // }
            //temporary off end
            otcrcptcclReceiptInfoDetsList.add(otcrcptcclReceiptInfoDetails);

        }
        return otcrcptcclReceiptInfoDetsList;
    }

    @Override
    public List<OTCReceiptCancellationHistoryDetails> sp_getotcrcptcclhistorydetails(
            OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {

        List<OTCReceiptCancellationHistoryDetails> result = Collections.emptyList();

        List<Object[]> objects = otcrcptcclRepository.sp_getotcrcptcclhistorydetails(otcrcptcclDetsRequest);

        result = convertToGetOTCRcptCclHistoryDetails(objects);

        return result;
    }

    private List<OTCReceiptCancellationHistoryDetails> convertToGetOTCRcptCclHistoryDetails(List<Object[]> objects) {
        List<OTCReceiptCancellationHistoryDetails> otcrcptcclHistoryDetsList = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCReceiptCancellationHistoryDetails otcrcptcclHistoryDetails = new OTCReceiptCancellationHistoryDetails();

            otcrcptcclHistoryDetails.setMtt_id((BigInteger) obj[0]);
            otcrcptcclHistoryDetails.setOtc_id((Integer) obj[1]);
            otcrcptcclHistoryDetails.setAction((String) obj[2]);
            otcrcptcclHistoryDetails.setDt_action((Date) obj[3]);
            otcrcptcclHistoryDetails.setOtc_status((String) obj[4]);
            otcrcptcclHistoryDetails.setCounter_id((String) obj[5]);
            otcrcptcclHistoryDetails.setAct_by((String) obj[6]);
            otcrcptcclHistoryDetails.setNm_en((String) obj[7]);
            otcrcptcclHistoryDetails.setTotal((Integer) obj[8]);

            otcrcptcclHistoryDetsList.add(otcrcptcclHistoryDetails);

        }
        return otcrcptcclHistoryDetsList;
    }

    @Override
    public List<OTCReceiptCancellationTaskAndReqInfoApproval> sp_getotcrcptccltaskandreqinfoapproval(
            OTCReceiptCancellationTaskAndReqInfoApprovalRequest otcrcptcclTaskAndReqInfoApprovalRequest) {

        List<OTCReceiptCancellationTaskAndReqInfoApproval> result = Collections.emptyList();

        List<Object[]> objects = otcrcptcclRepository
                .sp_getotcrcptccltaskandreqinfoapproval(otcrcptcclTaskAndReqInfoApprovalRequest);

        result = convertToGetOTCRcptCclTaskAndreqInfoApproval(objects);

        return result;
    }

    private List<OTCReceiptCancellationTaskAndReqInfoApproval> convertToGetOTCRcptCclTaskAndreqInfoApproval(
            List<Object[]> objects) {
        List<OTCReceiptCancellationTaskAndReqInfoApproval> otcrcptcclTaskAndReqInfoApprovalList = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCReceiptCancellationTaskAndReqInfoApproval otcrcptcclTaskAndReqInfoApproval = new OTCReceiptCancellationTaskAndReqInfoApproval();

            otcrcptcclTaskAndReqInfoApproval.setOtc_rc_id((Integer) obj[0]);
            otcrcptcclTaskAndReqInfoApproval.setOtc_id((Integer) obj[1]);
            otcrcptcclTaskAndReqInfoApproval.setJustication((String) obj[2]);
            otcrcptcclTaskAndReqInfoApproval.setOthers((String) obj[3]);
            otcrcptcclTaskAndReqInfoApproval.setRc_type((Integer) obj[4]);
            otcrcptcclTaskAndReqInfoApproval.setRc_status((String) obj[5]);
            otcrcptcclTaskAndReqInfoApproval.setTask_id((String) obj[6]);
            otcrcptcclTaskAndReqInfoApproval.setDate_assigned((Date) obj[7]);
            otcrcptcclTaskAndReqInfoApproval.setCounter_id((String) obj[8]);
            otcrcptcclTaskAndReqInfoApproval.setRequested_by((String) obj[9]);
            otcrcptcclTaskAndReqInfoApproval.setRequested_by_nm((String) obj[10]);
            otcrcptcclTaskAndReqInfoApproval.setRequester_id((String) obj[11]);
            otcrcptcclTaskAndReqInfoApproval.setDate_requested((Date) obj[12]);
            otcrcptcclTaskAndReqInfoApproval.setApproved_by((String) obj[13]);
            otcrcptcclTaskAndReqInfoApproval.setApproved_by_nm((String) obj[14]);
            otcrcptcclTaskAndReqInfoApproval.setApprover_id((String) obj[15]);
            otcrcptcclTaskAndReqInfoApproval.setDt_approved((Date) obj[16]);
            otcrcptcclTaskAndReqInfoApproval.setDt_created((Date) obj[17]);
            otcrcptcclTaskAndReqInfoApproval.setDt_modified((Date) obj[18]);
            otcrcptcclTaskAndReqInfoApproval.setCreated_by((String) obj[19]);
            otcrcptcclTaskAndReqInfoApproval.setCreated_by_nm((String) obj[20]);
            otcrcptcclTaskAndReqInfoApproval.setModified_by((String) obj[21]);
            otcrcptcclTaskAndReqInfoApproval.setModified_by_nm((String) obj[22]);
            otcrcptcclTaskAndReqInfoApproval.setMtt_id((Integer) obj[23]);
            otcrcptcclTaskAndReqInfoApproval.setOtc_counter_id((Integer) obj[24]);
            otcrcptcclTaskAndReqInfoApproval.setNm_en((String) obj[25]);

            otcrcptcclTaskAndReqInfoApprovalList.add(otcrcptcclTaskAndReqInfoApproval);

        }
        return otcrcptcclTaskAndReqInfoApprovalList;
    }

    @Override
    public List<OTCReceiptCancellationMyTaskListing> sp_getotcrcptcclmytasklisting(
            OTCReceiptCancellationMyTaskListingRequest otcrcptcclMyTaskListingRequest) {

        List<OTCReceiptCancellationMyTaskListing> result = Collections.emptyList();

        List<Object[]> objects = otcrcptcclRepository.sp_getotcrcptcclmytasklisting(otcrcptcclMyTaskListingRequest);

        result = convertToGetOTCRcptCclMyTaskListing(objects);

        return result;
    }

    private List<OTCReceiptCancellationMyTaskListing> convertToGetOTCRcptCclMyTaskListing(List<Object[]> objects) {
        List<OTCReceiptCancellationMyTaskListing> otcrcptcclMyTaskListingList = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCReceiptCancellationMyTaskListing otcrcptcclMyTaskListing = new OTCReceiptCancellationMyTaskListing();

            otcrcptcclMyTaskListing.setOtc_rc_id((Integer) obj[0]);
            otcrcptcclMyTaskListing.setOtc_id((Integer) obj[1]);
            otcrcptcclMyTaskListing.setJustication((String) obj[2]);
            otcrcptcclMyTaskListing.setRc_type((Integer) obj[3]);
            otcrcptcclMyTaskListing.setRc_status((String) obj[4]);
            otcrcptcclMyTaskListing.setTask_id((String) obj[5]);
            otcrcptcclMyTaskListing.setCounter_id((String) obj[6]);
            otcrcptcclMyTaskListing.setRequested_by((String) obj[7]);
            otcrcptcclMyTaskListing.setRequested_by_nm((String) obj[8]);
            otcrcptcclMyTaskListing.setDate_requested((Date) obj[9]);
            otcrcptcclMyTaskListing.setApproved_by((String) obj[10]);
            otcrcptcclMyTaskListing.setApproved_by_nm((String) obj[11]);
            otcrcptcclMyTaskListing.setDt_approved((Date) obj[12]);
            otcrcptcclMyTaskListing.setDt_created((Date) obj[13]);
            otcrcptcclMyTaskListing.setDt_modified((Date) obj[14]);
            otcrcptcclMyTaskListing.setCreated_by((String) obj[15]);
            otcrcptcclMyTaskListing.setCreated_by_nm((String) obj[16]);
            otcrcptcclMyTaskListing.setModified_by((String) obj[17]);
            otcrcptcclMyTaskListing.setModified_by_nm((String) obj[18]);
            otcrcptcclMyTaskListing.setAssigned_to((String) obj[19]);
            otcrcptcclMyTaskListing.setAssigned_to_nm((String) obj[20]);
            otcrcptcclMyTaskListing.setMtt_id((Integer) obj[21]);
            otcrcptcclMyTaskListing.setOtc_pymt_mode((String) obj[22]);
            otcrcptcclMyTaskListing.setTask_description((String) obj[23]);
            otcrcptcclMyTaskListing.setStatus((String) obj[24]);
            otcrcptcclMyTaskListing.setTotal((Integer) obj[25]);

            otcrcptcclMyTaskListingList.add(otcrcptcclMyTaskListing);

        }
        return otcrcptcclMyTaskListingList;
    }

    @Override
    public List<OTCReceiptCancellationBalStatusDetails> sp_getotcrcptcclbalstatusdetails(
            OTCReceiptCancellationBalStatusDetailsRequest otcrcptcclBalStatusDetsRequest) {

        List<OTCReceiptCancellationBalStatusDetails> result = Collections.emptyList();

        List<Object[]> objects = otcrcptcclRepository.sp_getotcrcptcclbalstatusdetails(otcrcptcclBalStatusDetsRequest);

        result = convertToGetOTCRcptCclBalStatusDetails(objects);

        return result;
    }

    private List<OTCReceiptCancellationBalStatusDetails> convertToGetOTCRcptCclBalStatusDetails(
            List<Object[]> objects) {
        List<OTCReceiptCancellationBalStatusDetails> otcrcptcclBalStatusDetsList = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCReceiptCancellationBalStatusDetails otcrcptcclBalStatusDets = new OTCReceiptCancellationBalStatusDetails();

            otcrcptcclBalStatusDets.setAllow_cancel((Integer) obj[0]);
            otcrcptcclBalStatusDets.setError_msg((String) obj[1]);

            otcrcptcclBalStatusDetsList.add(otcrcptcclBalStatusDets);

        }
        return otcrcptcclBalStatusDetsList;
    }

    @Override
    public BigInteger sp_insotcrc(OTCReceiptCancellationRequest otcrcRequest) {

        BigInteger result = BigInteger.ZERO;

        result = otcrcptcclRepository.sp_insotcrc(otcrcRequest);

        return result;
    }

    @Override
    public Integer sp_updotcrc(OTCReceiptCancellationUpdateRequest otcrcRequestUpdate) {

        Integer result = 0;

        result = otcrcptcclRepository.sp_updotcrc(otcrcRequestUpdate);

        return result;
    }

    @Override
    public Integer sp_updmtt_orderstatus(OTCReceiptCclMTTOrderStatusRequest mttOrderStatusRequest) {

        Integer result = 0;

        result = otcrcptcclRepository.sp_updmtt_orderstatus(mttOrderStatusRequest);

        return result;
    }
    
    @Override
    public List<OTCReceiptCancellationRCStatusDetails> sp_getotcrcpltoCancel(OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {

        List<OTCReceiptCancellationRCStatusDetails> result = Collections.emptyList();

        List<Object[]> objects  = otcrcptcclRepository.sp_getotcrcpltoCancel(otcrcptcclDetsRequest);

        result = convertToGetOTCRcptCclRCStatusDetails(objects);

        return result;
    }

    private List<OTCReceiptCancellationRCStatusDetails> convertToGetOTCRcptCclRCStatusDetails(
        List<Object[]> objects) {
    List<OTCReceiptCancellationRCStatusDetails> otcrcptcclRCStatusDetsList = new ArrayList<>();

    for (Object[] obj : objects) {
        OTCReceiptCancellationRCStatusDetails otcrcptcclRCStatusDets = new OTCReceiptCancellationRCStatusDetails();

        otcrcptcclRCStatusDets.setCount((Integer) obj[0]);
        otcrcptcclRCStatusDets.setRc_status((String) obj[1]);

        otcrcptcclRCStatusDetsList.add(otcrcptcclRCStatusDets);

    }
    return otcrcptcclRCStatusDetsList;
}

    public MTTRCPT sp_getmttrcptinfowithstatus(BigInteger mtt_id) {

        MTTRCPT result = new MTTRCPT();

        try {

            result = convertToMTTRCPT(otcrcptcclRepository.sp_getmttrcptinfowithstatus(mtt_id));
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

    private MTTRCPT convertToMTTRCPT(Object[] obj) {

        MTTRCPT mttrcpt = new MTTRCPT();

        // Extract values from the obj and cast them to their respective types
        // Assuming the order and types match your MTTRCPT class
        // BigInteger mttRcptID = (BigInteger) obj[0];
        // OnlinePayment rmsMTT = (OnlinePayment) obj[1]; // Assuming OnlinePayment is
        // the correct type
        // MTTPG mttPG = (MTTPG) obj[2]; // Assuming MTTPG is the correct type
        String rcptNo = (String) obj[1];
        // LocalDateTime rcptDt = (LocalDateTime) obj[4];
        // String rcptStatus = (String) obj[5];
        // Integer rcptReprint = (Integer) obj[6];
        // Integer isUploaded = (Integer) obj[7];
        // LocalDateTime dtCreated = (LocalDateTime) obj[8];
        // LocalDateTime dtModified = (LocalDateTime) obj[9];
        // String createdBy = (String) obj[10];
        // String modifiedBy = (String) obj[11];

        // Set the values in the MTTRCPT instance
        mttrcpt = rcptRepo.sp_getRcptByRcptNo(rcptNo).orElse(null);

        return mttrcpt;
    }

    @Override
    public OTCRcpt sp_getotcreceipt(OTCReceiptCclMTTOrderStatusRequest mttOrderStatusRequest) {
        OTCRcpt result = new OTCRcpt();
        Object[] resultSet = otcrcptcclRepository.sp_getotcreceipt(mttOrderStatusRequest);
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

    @Override
    public OTCPaymentDone sp_getotcrcptcclorder(BigInteger i_mtt_id) {
        
        OTCPaymentDone result = new OTCPaymentDone();
        Object[] resultSet = otcrcptcclRepository.sp_getotcrcptcclorder(i_mtt_id);
        
        // Assuming the stored procedure returns an array of values
        result.setMtt_id((Integer) resultSet[0]);
        result.setSs_cd((String) resultSet[1]);
        result.setColl_slip_no((String) resultSet[2]);
        result.setOrn_no((String) resultSet[3]);
        result.setCust_nm((String) resultSet[4]);
        result.setCust_phone((String) resultSet[5]);
        result.setCust_email((String) resultSet[6]);
        result.setCust_addr1((String) resultSet[7]);
        result.setCust_addr2((String) resultSet[8]);
        result.setCust_addr3((String) resultSet[9]);
        result.setCust_postcode((String) resultSet[10]);
        result.setCust_city((String) resultSet[11]);
        result.setCust_state((String) resultSet[12]);
        result.setTotal_amt((BigDecimal) resultSet[13]);
        result.setOrder_status((String) resultSet[14]);
        result.setTotal_amount_paid((BigDecimal) resultSet[15]);
        result.setCounter_id((String) resultSet[16]);
        result.setDt_created((Date) resultSet[17]);
        result.setOtc_pymt_mode((String) resultSet[18]);
        result.setBranch_cd((String) resultSet[19]);
        result.setPayment_dt((Date) resultSet[20]);
        result.setEmv_terminal_id((String) resultSet[21]);
        result.setTrace_no((String) resultSet[22]);
        result.setTotal((Integer) resultSet[23]);

        return result;
    }

    @Override
    public List<OTCCollectionReceiptingPymtItem> sp_getotcrcptcllpymtitembymtt(BigInteger i_mtt_id) {
        List<OTCCollectionReceiptingPymtItem> result = Collections.emptyList();
        List<Object[]> objects = otcrcptcclRepository.sp_getotcrcptcllpymtitembymtt(i_mtt_id);
        result = convertOTCRcptCclPymtList(objects);
        return result;
    }

    private List<OTCCollectionReceiptingPymtItem> convertOTCRcptCclPymtList(List<Object[]> objects) {
        List<OTCCollectionReceiptingPymtItem> otcCollectionReceiptings = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCCollectionReceiptingPymtItem otcCollectionReceipting = new OTCCollectionReceiptingPymtItem();
            otcCollectionReceipting.setItem_desc((String) obj[0]);
            otcCollectionReceipting.setQty((Integer) obj[1]);
            otcCollectionReceipting.setUnit_fee((BigDecimal) obj[2]);
            otcCollectionReceipting.setTax_pct((BigDecimal) obj[3]);
            otcCollectionReceipting.setTax_amt((BigDecimal) obj[4]);
            otcCollectionReceipting.setGrant_cd((String) obj[5]);
            otcCollectionReceipting.setDisc_amt((BigDecimal) obj[6]);
            otcCollectionReceipting.setGross_amt((BigDecimal) obj[7]);
            otcCollectionReceipting.setTotal((Integer) obj[8]);

            otcCollectionReceiptings.add(otcCollectionReceipting);
        }
        return otcCollectionReceiptings;
    }

    @Override
    public List<OTCReceiptCancellationPymtItem> sp_otcrcptcclpymtitem(OTCReceiptCancellationDetailsRequest getRequest) {
        List<OTCReceiptCancellationPymtItem> result = Collections.emptyList();
        List<Object[]> objects = otcrcptcclRepository.sp_otcrcptcclpymtitem(getRequest);
        result = convertOTCRcptCclPymtitem(objects);
        return result;
    }

    private List<OTCReceiptCancellationPymtItem> convertOTCRcptCclPymtitem(List<Object[]> objects) {
        List<OTCReceiptCancellationPymtItem> otcRcptCclPymtitemList = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCReceiptCancellationPymtItem otcRcptCclPymtitem = new OTCReceiptCancellationPymtItem();
            otcRcptCclPymtitem.setItem_desc((String) obj[0]);
            otcRcptCclPymtitem.setQty((Integer) obj[1]);
            otcRcptCclPymtitem.setUnit_fee((BigDecimal) obj[2]);
            otcRcptCclPymtitem.setTax_pct((BigDecimal) obj[3]);
            otcRcptCclPymtitem.setTax_amt((BigDecimal) obj[4]);
            otcRcptCclPymtitem.setGrant_cd((String) obj[5]);
            otcRcptCclPymtitem.setDisc_amt((BigDecimal) obj[6]);
            otcRcptCclPymtitem.setGross_amt((BigDecimal) obj[7]);
            otcRcptCclPymtitem.setNet_amt((BigDecimal) obj[8]);
            otcRcptCclPymtitem.setTotal((Integer) obj[9]);

            otcRcptCclPymtitemList.add(otcRcptCclPymtitem);
        }
        return otcRcptCclPymtitemList;
    }

    
    @Override
    public Integer sp_updotcrcpt(Integer i_otc_rcpt_id, String i_ver_id, String i_ssdocref_id, String file_nm) {
        Integer result = 0;
        try {
            result = otcCollectionReceiptingRepository.sp_updotcrcpt(i_otc_rcpt_id, i_ver_id, i_ssdocref_id, file_nm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Integer sp_getotcrcassignedtaskactivetaskcount(OTCReceiptCancellationAssignToRequest otcrcptcclRequest) {

        Integer result = 0;

            result = otcrcptcclRepository.sp_getotcrcassignedtaskactivetaskcount(otcrcptcclRequest);

      //  notificationSvc.sendNotificationUpdate(5, 10);

        return result;
    }


    @Override
    public Integer sp_getotcrccreatedtaskactivetaskcount(OTCReceiptCancellationCreatedByRequest otcrcptcclRequest) {

        Integer result = 0;

            result = otcrcptcclRepository.sp_getotcrccreatedtaskactivetaskcount(otcrcptcclRequest);

      //  notificationSvc.sendNotificationUpdate(5, 10);

        return result;
    }


    @Override
    public List<OTCReceiptCancellationHistoryDetailsAudit> sp_getotcrcptcclhistorydetailsaudit(
            OTCReceiptCancellationDetailsRequest otcrcptcclDetsRequest) {

        List<OTCReceiptCancellationHistoryDetailsAudit> result = Collections.emptyList();

        List<Object[]> objects = otcrcptcclRepository.sp_getotcrcptcclhistorydetailsaudit(otcrcptcclDetsRequest);

        result = convertToGetOTCRcptCclHistoryDetailsAudit(objects);

        return result;
    }

    private List<OTCReceiptCancellationHistoryDetailsAudit> convertToGetOTCRcptCclHistoryDetailsAudit(List<Object[]> objects) {
        List<OTCReceiptCancellationHistoryDetailsAudit> otcrcptcclHistoryDetsAuditList = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCReceiptCancellationHistoryDetailsAudit otcrcptcclHistoryDetailsAudit = new OTCReceiptCancellationHistoryDetailsAudit();


            otcrcptcclHistoryDetailsAudit.setMtt_id((BigInteger) obj[0]);
            otcrcptcclHistoryDetailsAudit.setOtc_id((Integer) obj[1]);
            otcrcptcclHistoryDetailsAudit.setAction((String) obj[2]);
            otcrcptcclHistoryDetailsAudit.setDt_action((Date) obj[3]);
            otcrcptcclHistoryDetailsAudit.setBcm_desc((String) obj[4]);
            otcrcptcclHistoryDetailsAudit.setCounter_id((String) obj[5]);
            otcrcptcclHistoryDetailsAudit.setHist_status((String) obj[6]);
            otcrcptcclHistoryDetailsAudit.setStatus((String) obj[7]);
            otcrcptcclHistoryDetailsAudit.setJustification((String) obj[8]);
            otcrcptcclHistoryDetailsAudit.setRc_type((String) obj[9]);
            otcrcptcclHistoryDetailsAudit.setOthers((String) obj[10]);
            otcrcptcclHistoryDetailsAudit.setRemark((String) obj[11]);
            otcrcptcclHistoryDetailsAudit.setPerformed_by((String) obj[12]);
            otcrcptcclHistoryDetailsAudit.setPerformed_by_nm((String) obj[13]);
            otcrcptcclHistoryDetailsAudit.setAssigned_to((String) obj[14]);
            otcrcptcclHistoryDetailsAudit.setAssigned_to_nm((String) obj[15]);
            otcrcptcclHistoryDetailsAudit.setTotal((Integer) obj[16]);
            
            otcrcptcclHistoryDetsAuditList.add(otcrcptcclHistoryDetailsAudit);

        }
        return otcrcptcclHistoryDetsAuditList;
    }

    @Override
    public List<OTCReceiptCancellationSupervisor> sp_getotcrcptcclsupervisor(
            OTCReceiptCancellationSupervisorRequest otcrcptcclsupervisorRequest) {

        List<OTCReceiptCancellationSupervisor> result = Collections.emptyList();

        List<Object[]> objects = otcrcptcclRepository.sp_getotcrcptcclsupervisor(otcrcptcclsupervisorRequest);

        result = convertToGetOTCRcptCclSupervisor(objects);

        return result;
    }

    private List<OTCReceiptCancellationSupervisor> convertToGetOTCRcptCclSupervisor(
            List<Object[]> objects) {
        List<OTCReceiptCancellationSupervisor> otcrcptcclsupervisorList = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCReceiptCancellationSupervisor otcrcptcclsupervisor = new OTCReceiptCancellationSupervisor();

            otcrcptcclsupervisor.setSsm4uuserrefno((String) obj[0]);
            otcrcptcclsupervisor.setNm((String) obj[1]);
            otcrcptcclsupervisor.setEmail((String) obj[2]);
            otcrcptcclsupervisor.setRole_branch_id((Integer) obj[3]);
            otcrcptcclsupervisor.setBcm_desc((String) obj[4]);
            otcrcptcclsupervisor.setRole_id((Integer) obj[5]);
            otcrcptcclsupervisor.setRole_nm_en((String) obj[6]);

            otcrcptcclsupervisorList.add(otcrcptcclsupervisor);

        }
        return otcrcptcclsupervisorList;
    }

    ///
    @Override
    public List<OTCReceiptCheck> sp_checkotcrcpt() {
        List<Object[]> objects = otcrcptcclRepository.sp_checkotcrcpt();
        return convertToOtcRcptCheckList(objects);
    }

    private List<OTCReceiptCheck> convertToOtcRcptCheckList(List<Object[]> objects) {
        List<OTCReceiptCheck> list = new ArrayList<>();
        for (Object[] obj : objects) {
            OTCReceiptCheck item = new OTCReceiptCheck();
            item.setMtt_id((BigInteger) obj[0]);
            item.setOtc_rc_id((BigInteger) obj[1]);
            item.setSsdocref_id((String) obj[2]);

            list.add(item);
        }
        return list;
    }
}
