package com.maven.rms.services;

import java.math.BigDecimal;

import java.sql.Blob;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import javax.sql.rowset.serial.SerialBlob;
import org.springframework.stereotype.Service;

import com.maven.rms.interfaces.IRefundPTTListingService;
import com.maven.rms.models.PaymentItemDetails;
import com.maven.rms.models.PaymentRequest;
import com.maven.rms.models.RefundDetailPymtItem;
import com.maven.rms.models.RefundDetails;
import com.maven.rms.models.RefundDoc;
import com.maven.rms.models.RefundInfo;
import com.maven.rms.models.RefundList;
import com.maven.rms.models.RefundHist;
import com.maven.rms.models.RefundPGPaymentDetails;
import com.maven.rms.models.RefundPTTListing;
import com.maven.rms.models.RefundPTTListingDetReq;
import com.maven.rms.models.RefundRcpt;
import com.maven.rms.models.RefundTHTListing;
import com.maven.rms.models.RefundWFList;
import com.maven.rms.models.RefundWFListingDetReq;
import com.maven.rms.models.RttAppEmailDto;
import com.maven.rms.models.TaxCdRequest;
import com.maven.rms.models.UserRole;
import com.maven.rms.models.OTC.OTCCollectionReceiptingPymtItem;
import com.maven.rms.models.OTC.OTCPayment;
import com.maven.rms.models.OTC.OTCPaymentDetails;
import com.maven.rms.models.OTC.OTCPaymentRequest;
import com.maven.rms.models.OTC.OTCRcpt;
import com.maven.rms.models.OTC.OTCollectionReceiptingRequest;
import com.maven.rms.repositories.IRefundPTTListingRepository;
import com.maven.rms.repositories.RTTReturnedChequeRepository;
import com.maven.rms.repositories.OTC.OTCCollectionReceiptingRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RefundPTTListingService implements IRefundPTTListingService {
    // private static final Logger logger =
    // LoggerFactory.getLogger(StoreProcedureService.class);
    private final IRefundPTTListingRepository storeProcedureRepository;
    private final RTTReturnedChequeRepository rttReturnedChequeRepository;
    private final OTCCollectionReceiptingRepository otcCollectionReceiptingRepository;
    // private final MTTRCPTRepository mttrcptRepository;
    // private final RICPRepository ricpRepository;

    public RefundPTTListingService(IRefundPTTListingRepository storeProcedureRepository,
            OTCCollectionReceiptingRepository otcCollectionReceiptingRepository,
            RTTReturnedChequeRepository rttReturnedChequeRepository) {
        this.storeProcedureRepository = storeProcedureRepository;
        this.otcCollectionReceiptingRepository = otcCollectionReceiptingRepository;
        this.rttReturnedChequeRepository = rttReturnedChequeRepository;
        // this.mttrcptRepository=mttrcptRepository;
        // this.ricpRepository=ricpRepository;
    }

    @Override
    public List<RefundPTTListing> sp_getRefundPTTListing(RefundPTTListingDetReq req) {

        List<RefundPTTListing> result = new ArrayList<>();

        try {
            List<Object[]> objects = storeProcedureRepository.sp_getRefundPTTListing(req);
            result = convertToRefundPTTListingList(objects);
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    public List<RefundPTTListing> convertToRefundPTTListingList(List<Object[]> objects) {
        List<RefundPTTListing> refundPTTListingList = new ArrayList<>();

        for (Object[] obj : objects) {
            RefundPTTListing refundPTTListing = new RefundPTTListing();

            // 0: ORN_NO
            refundPTTListing.setOrn_no((String) obj[0]);

            // 1: ORN_DT (Timestamp → LocalDateTime)
            Object raw = obj[1];
            if (raw instanceof Timestamp) {
                refundPTTListing.setOrn_dt(((Timestamp) raw).toLocalDateTime());
            } else if (raw instanceof Date) {
                // fallback if you somehow got a java.util.Date
                Date d = (Date) raw;
                LocalDateTime ldt = d.toInstant()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDateTime();
                refundPTTListing.setOrn_dt(ldt);
            } else {
                refundPTTListing.setOrn_dt(null);
            }

            refundPTTListing.setTxn_id((String) obj[2]);
            refundPTTListing.setTotal_amt((BigDecimal) obj[3]);
            refundPTTListing.setOrder_status((String) obj[4]);
            refundPTTListing.setRcpt_no((String) obj[5]);
            refundPTTListing.setTotal((Integer) obj[6]);
            refundPTTListing.setMtt_id((Integer) obj[7]);
            refundPTTListing.setRms_type((String) obj[8]);
            refundPTTListing.setRtt_status((String) obj[9]);

            refundPTTListingList.add(refundPTTListing);
        }

        return refundPTTListingList;
    }

    @Override
    public List<RefundDetails> sp_getRefundOI_otc(RefundPTTListingDetReq req) {
        List<RefundDetails> result = new ArrayList<>();

        try {
            // Fetch data from the stored procedure
            List<Object[]> objects = storeProcedureRepository.sp_getRefundOI_otc(req);

            // Convert the result to a list of RefundDetails
            result = convertToRefundOIDetailsList(objects);

            // Filter out records where orn_no is null or empty
            result = result.stream()
                    .filter(refundDetails -> refundDetails.getOrn_no() != null && !refundDetails.getOrn_no().isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public List<RefundDetails> sp_getRefundOI_online(RefundPTTListingDetReq req) {
        List<RefundDetails> result = new ArrayList<>();

        try {
            List<Object[]> objects = storeProcedureRepository.sp_getRefundOI_online(req);
            result = convertToRefundOIDetailsList(objects);

            result = result.stream()
                    .filter(refundDetails -> refundDetails.getOrn_no() != null && !refundDetails.getOrn_no().isEmpty())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<RefundDetails> convertToRefundOIDetailsList(List<Object[]> objects) {
        List<RefundDetails> refundDetailsList = new ArrayList<>();
        for (Object[] obj : objects) {
            RefundDetails refundDetails = new RefundDetails();

            refundDetails.setMtt_id((Integer) obj[0]);
            refundDetails.setRms_type((String) obj[1]);
            refundDetails.setSs_cd((String) obj[2]);
            refundDetails.setOrn_no((String) obj[3]);
            refundDetails.setTxn_id((String) obj[4]);
            // refundDetails.setEnt_nm((String) obj[5]);
            // refundDetails.setEnt_type((String) obj[6]);
            // refundDetails.setEnt_no((String) obj[7]);
            refundDetails.setCust_nm((String) obj[5]);
            refundDetails.setCust_phone((String) obj[6]);
            refundDetails.setCust_email((String) obj[7]);
            refundDetails.setCust_addr_1((String) obj[8]);
            refundDetails.setCust_addr_2((String) obj[9]);
            refundDetails.setCust_addr_3((String) obj[10]);
            refundDetails.setCust_postcode((String) obj[11]);
            refundDetails.setCust_city((String) obj[12]);
            refundDetails.setCust_state((String) obj[13]);
            refundDetails.setOrder_status((String) obj[14]);

            refundDetailsList.add(refundDetails);
        }
        return refundDetailsList;
    }

    @Override
    public List<RefundDetailPymtItem> sp_getRefundPaymentItem(
            RefundPTTListingDetReq req) {
        List<RefundDetailPymtItem> result = Collections.emptyList();
        List<Object[]> objects = storeProcedureRepository.sp_getRefundPaymentItem(req);
        result = convertRefundPymtList(objects);
        return result;
    }

    private List<RefundDetailPymtItem> convertRefundPymtList(List<Object[]> objects) {
        List<RefundDetailPymtItem> refundDetailPymtItems = new ArrayList<>();

        for (Object[] obj : objects) {
            RefundDetailPymtItem refundDetailPymtItem = new RefundDetailPymtItem();
            refundDetailPymtItem.setMtt_item_id((int) obj[0]);
            refundDetailPymtItem.setItem_desc((String) obj[1]);
            refundDetailPymtItem.setQty((Integer) obj[2]);
            refundDetailPymtItem.setUnit_fee((BigDecimal) obj[3]);
            refundDetailPymtItem.setTax_amt((BigDecimal) obj[4]);
            refundDetailPymtItem.setTax_pct((BigDecimal) obj[5]);
            refundDetailPymtItem.setGrant_cd((String) obj[6]);
            refundDetailPymtItem.setDisc_amt((BigDecimal) obj[7]);
            refundDetailPymtItem.setGross_amt((BigDecimal) obj[8]);
            refundDetailPymtItem.setItem_ref_no((String) obj[9]);
            refundDetailPymtItem.setNet_amt((BigDecimal) obj[10]);
            refundDetailPymtItem.setTotal((BigDecimal) obj[11]);
            refundDetailPymtItem.setEntity_type((String) obj[12]);
            refundDetailPymtItem.setEntity_no((String) obj[13]);
            refundDetailPymtItem.setEntity_nm((String) obj[14]);

            refundDetailPymtItems.add(refundDetailPymtItem);
        }
        return refundDetailPymtItems;
    }

    @Override
    public List<OTCPayment> sp_getotccrpaymentheader(OTCPaymentRequest otCollectionReceiptingRequest) {
        List<OTCPayment> result = Collections.emptyList();
        List<Object[]> objects = otcCollectionReceiptingRepository
                .sp_getotccrpaymentheader(otCollectionReceiptingRequest);
        result = convertOTCPymtList(objects);
        return result;
    }

    private List<OTCPayment> convertOTCPymtList(List<Object[]> objects) {
        List<OTCPayment> otcCollectionPaymentDetails = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCPayment otcPayment = new OTCPayment();
            otcPayment.setMtt_id((Integer) obj[0]);
            otcPayment.setEmv_sale_id((Integer) obj[1]);
            otcPayment.setOtc_counter_id((Integer) obj[2]);
            otcPayment.setPayer_email((String) obj[3]);
            otcPayment.setOtc_pymt_mode((String) obj[4]);
            otcPayment.setDt_created((Date) obj[5]);
            otcPayment.setDt_modified((Date) obj[6]);
            otcPayment.setCreated_by((String) obj[7]);
            otcPayment.setModified_by((String) obj[8]);
            otcPayment.setStatus((String) obj[9]);
            // otcPayment.setV_reason_cd((String) obj[10]);

            otcCollectionPaymentDetails.add(otcPayment);
        }
        return otcCollectionPaymentDetails;
    }

    @Override
    public List<OTCPaymentDetails> sp_getotccrpaymentdetails(OTCPaymentRequest otCollectionReceiptingRequest) {
        List<OTCPaymentDetails> result = Collections.emptyList();
        List<Object[]> objects = otcCollectionReceiptingRepository
                .sp_getotccrpaymentdetails(otCollectionReceiptingRequest);
        result = convertOTCPymtDetailsList(objects);
        return result;
    }

    private List<OTCPaymentDetails> convertOTCPymtDetailsList(List<Object[]> objects) {
        List<OTCPaymentDetails> otcCollectionPaymentDetails = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCPaymentDetails otcPaymentDetails = new OTCPaymentDetails();
            otcPaymentDetails.setOtc_body_id((Integer) obj[0]);
            otcPaymentDetails.setOtc_id((Integer) obj[1]);
            otcPaymentDetails.setCash_amt((BigDecimal) obj[2]);
            otcPaymentDetails.setChe_amt((BigDecimal) obj[3]);
            otcPaymentDetails.setCheDate((Date) obj[4]);
            otcPaymentDetails.setChe_bank_nm((String) obj[5]);
            otcPaymentDetails.setChe_payer_nm((String) obj[6]);
            otcPaymentDetails.setChe_no((String) obj[7]);
            otcPaymentDetails.setChe_status((String) obj[8]);
            otcPaymentDetails.setMo_amt((BigDecimal) obj[9]);
            otcPaymentDetails.setMo_rm_no((String) obj[10]);
            otcPaymentDetails.setMo_date((Date) obj[11]);
            otcPaymentDetails.setMo_payer_nm((String) obj[12]);
            otcPaymentDetails.setMo_id_no((String) obj[13]);
            otcPaymentDetails.setMo_contact_no((String) obj[14]);
            otcPaymentDetails.setBd_amt((BigDecimal) obj[15]);
            otcPaymentDetails.setBd_no((String) obj[16]);
            otcPaymentDetails.setBd_date((Date) obj[17]);
            otcPaymentDetails.setBd_bank_nm((String) obj[18]);
            otcPaymentDetails.setDt_created((Date) obj[19]);
            otcPaymentDetails.setDt_modified((Date) obj[20]);
            otcPaymentDetails.setCreated_by((String) obj[21]);
            otcPaymentDetails.setModified_by((String) obj[22]);
            otcPaymentDetails.setStatus((String) obj[23]);
            otcPaymentDetails.setChe_ba_acct_no((String) obj[24]);
            otcPaymentDetails.setChe_id((String) obj[25]);
            otcPaymentDetails.setTotal((Integer) obj[26]);

            otcCollectionPaymentDetails.add(otcPaymentDetails);
        }
        return otcCollectionPaymentDetails;
    }

    @Override
    public List<RefundPGPaymentDetails> sp_getrefundpaymentinfo_online(
            RefundPTTListingDetReq req) {
        List<RefundPGPaymentDetails> result = Collections.emptyList();
        List<Object[]> objects = storeProcedureRepository.sp_getrefundpaymentinfo_online(req);
        result = convertRefundPymtInfoOnline(objects);
        return result;
    }

    private List<RefundPGPaymentDetails> convertRefundPymtInfoOnline(List<Object[]> objects) {
        List<RefundPGPaymentDetails> refundRGPaymentDetails = new ArrayList<>();

        for (Object[] obj : objects) {
            RefundPGPaymentDetails refundPGPaymentDetail = new RefundPGPaymentDetails();
            refundPGPaymentDetail.setCust_email((String) obj[0]);
            refundPGPaymentDetail.setPg_payment_date((Date) obj[1]);
            refundPGPaymentDetail.setPg_payment_id((String) obj[2]);
            refundPGPaymentDetail.setPg_payment_amt((BigDecimal) obj[3]);
            refundPGPaymentDetail.setPg_payment_status((String) obj[4]);
            refundPGPaymentDetail.setRms_type((String) obj[5]);
            refundRGPaymentDetails.add(refundPGPaymentDetail);
        }
        return refundRGPaymentDetails;
    }

    @Override
    public List<RefundRcpt> sp_getrefundotcrcpt(RefundPTTListingDetReq req) {
        List<RefundRcpt> result = Collections.emptyList();
        List<Object[]> objects = storeProcedureRepository.sp_getrefundotcrcpt(req);
        result = convertRefundOTCRcptList(objects);
        return result;
    }

    private List<RefundRcpt> convertRefundOTCRcptList(List<Object[]> objects) {
        List<RefundRcpt> refundotcRcpts = new ArrayList<>();

        for (Object[] obj : objects) {
            RefundRcpt refundotcRcpt = new RefundRcpt();
            refundotcRcpt.setOtc_id((Integer) obj[0]);
            refundotcRcpt.setRcptNo((String) obj[1]);
            refundotcRcpt.setRcpt_dt((Date) obj[2]);
            refundotcRcpt.setRcpt_status((String) obj[3]);
            refundotcRcpt.setRcpt_reprint((Integer) obj[4]);
            refundotcRcpt.setIs_uploaded((Integer) obj[5]);
            refundotcRcpt.setVer_id((String) obj[6]);

            refundotcRcpt.setSsdocref_id((String) obj[7]);
            refundotcRcpt.setDt_created((Date) obj[8]);
            refundotcRcpt.setDt_modified((Date) obj[9]);
            refundotcRcpt.setCreated_by((String) obj[10]);
            refundotcRcpt.setModified_by((String) obj[11]);
            refundotcRcpt.setStatus((String) obj[12]);
            refundotcRcpt.setFile_nm((String) obj[13]);
            refundotcRcpt.setRemark((String) obj[14]);

            refundotcRcpts.add(refundotcRcpt);
        }

        return refundotcRcpts;
    }

    @Override
    public List<RefundRcpt> sp_getrefundpgrcpt(RefundPTTListingDetReq req) {
        List<RefundRcpt> result = Collections.emptyList();
        List<Object[]> objects = storeProcedureRepository.sp_getrefundpgrcpt(req);
        result = convertRefundPgRcptList(objects);
        return result;
    }

    private List<RefundRcpt> convertRefundPgRcptList(List<Object[]> objects) {
        List<RefundRcpt> refundPgRcpts = new ArrayList<>();

        for (Object[] obj : objects) {
            RefundRcpt refundPgRcpt = new RefundRcpt();

            refundPgRcpt.setRcptNo((String) obj[0]);
            refundPgRcpt.setFile_nm((String) obj[1]);
            refundPgRcpt.setRcpt_dt((Date) obj[2]);
            refundPgRcpt.setRcpt_status((String) obj[3]);
            refundPgRcpt.setRcpt_reprint((Integer) obj[4]);

            refundPgRcpts.add(refundPgRcpt);
        }

        return refundPgRcpts;
    }

    @Override
    public List<RefundInfo> sp_getrefundinfo(RefundPTTListingDetReq req) {
        List<RefundInfo> result = Collections.emptyList();
        List<Object[]> objects = storeProcedureRepository.sp_getrefundinfo(req);
        result = convertRefundInfoList(objects);
        return result;
    }

    private List<RefundInfo> convertRefundInfoList(List<Object[]> objects) {
        List<RefundInfo> refundInfos = new ArrayList<>();

        for (Object[] obj : objects) {
            RefundInfo refundinfo = new RefundInfo();

            // Use null checks and safe casting
            refundinfo.setRtt_id(obj[0] != null ? (Integer) obj[0] : 0);
            refundinfo.setRefund_slip_no(obj[1] != null ? (String) obj[1] : null);
            refundinfo.setRequested_by(obj[2] != null ? (String) obj[2] : null);
            refundinfo.setDt_process(obj[3] != null ? (Date) obj[3] : null);
            refundinfo.setAppeal_cnt(obj[4] != null ? (Integer) obj[4] : -1);
            refundinfo.setRtt_status(obj[5] != null ? (String) obj[5] : null);
            refundinfo.setRtt_app_no(obj[6] != null ? (String) obj[6] : null);

            refundInfos.add(refundinfo);
        }

        return refundInfos;
    }

    @Override
    public List<RefundHist> sp_getrefundhist(RefundPTTListingDetReq req) {
        List<RefundHist> result = Collections.emptyList();
        List<Object[]> objects = storeProcedureRepository.sp_getrefundhist(req);
        result = convertRefundHistList(objects);
        return result;
    }

    private List<RefundHist> convertRefundHistList(List<Object[]> objects) {
        List<RefundHist> refundHists = new ArrayList<>();

        for (Object[] obj : objects) {
            RefundHist refundHist = new RefundHist();

            refundHist.setRtt_wf_hist_id((Integer) obj[0]);
            refundHist.setAction((String) obj[1]);
            refundHist.setRtt_status((String) obj[2]);
            refundHist.setDt_action((Date) obj[3]);
            refundHist.setRequested_by((String) obj[4]);
            refundHist.setAssign_to((String) obj[5]);
            refundHist.setMsg((String) obj[6]);
            refundHist.setTotal((Integer) obj[7]);
            refundHist.setPickup_by((String) obj[8]);
            refundHist.setModified_by((String) obj[9]);
            refundHist.setModified_by_nm((String) obj[10]);
            refundHists.add(refundHist);
        }

        // System.out.println("Converted RefundHist List: " + refundHists);
        return refundHists;
    }

    @Override
    public Integer sp_processRefundRequest(RefundWFList req, PaymentRequest paymentRequest) {
        Integer rttWfId = 0;
        Integer rtt_item_id = 0;
        String finalResult = "";

        try {
            String createdBy = req.getCreated_by();
            String modifiedBy = req.getModified_by();

            // Step 1: Insert into rms_rtt_wf and get rtt_wf_id
            rttWfId = storeProcedureRepository.sp_insrttwf(req);
            System.out.println("Generated rtt_wf_id: " + rttWfId);

            if (rttWfId > 0) {
                List<PaymentItemDetails> itemDetailsList = paymentRequest.getPayment_item_details();

                for (PaymentItemDetails item : itemDetailsList) {
                    System.out.println("Inserting refund item: " + item);

                    rtt_item_id = storeProcedureRepository.sp_insertRefundItem(item, rttWfId, createdBy, modifiedBy);

                    if (rtt_item_id < 1) {
                        finalResult = "RTT Item table insert failed for item_ref_no: " + item.getItem_ref_no();
                        System.out.println(finalResult);
                        throw new Exception(finalResult);
                    }
                    System.out.println("Successfully inserted item_ref_no: " + item.getItem_ref_no());
                }

                finalResult = "Insert successful";
                System.out.println(finalResult);
            } else if (rttWfId == -3) {
                
                finalResult = "RTT Form table insert failed, appeal count over 3 times or Refund status is not rejected";
                
                return rttWfId; // Directly returning -3 allows the controller to handle it appropriately.
           
            } else if   (rttWfId == -2) {
                
                finalResult = "Appeal Refund require remark message";
                
                return rttWfId;
            } else if (rttWfId == -1) {
                finalResult = "Reference No. Cannot be null or empty";
                
                return rttWfId;
            }

            else {
                finalResult =  "Unknown error during insert RTT WF table insert failed";
                throw new Exception(finalResult);
            }

        } catch (Exception e) {
            log.error("Error during refund request processing: {}. Cause chain: {}", e.getMessage(), flattenCauses(e),
                    e);
            rttWfId = -5; // Indicate failure
        }

        return rttWfId;
    }

    @Override
    public List<RefundTHTListing> sp_getrefundtht(RefundPTTListingDetReq req) {

        List<RefundTHTListing> result = new ArrayList<>();

        try {
            List<Object[]> objects = storeProcedureRepository.sp_getrefundtht(req);
            result = convertToRefundTHTListingList(objects);
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<RefundTHTListing> convertToRefundTHTListingList(List<Object[]> objects) {
        List<RefundTHTListing> refundTHTListingList = new ArrayList<>();
        for (Object[] obj : objects) {
            RefundTHTListing refundTHTListing = new RefundTHTListing();

            refundTHTListing.setOrn_no((String) obj[0]);
            refundTHTListing.setRefund_slip_no((String) obj[1]);
            refundTHTListing.setOrn_dt((Date) obj[2]);
            refundTHTListing.setTxn_id((String) obj[3]);
            refundTHTListing.setTotal_amt((BigDecimal) obj[4]);
            refundTHTListing.setRms_type((String) obj[5]);
            refundTHTListing.setOrder_status((String) obj[6]);
            refundTHTListing.setRcpt_no((String) obj[7]);
            refundTHTListing.setRtt_app_no((String) obj[8]);
            refundTHTListing.setDate_expiry(obj[9] != null ? (Date) obj[9] : null);
            refundTHTListing.setRtt_status((String) obj[10]);
            refundTHTListing.setTotal((Integer) obj[11]);
            refundTHTListing.setMtt_id((Integer) obj[12]);

            refundTHTListingList.add(refundTHTListing);
        }
        return refundTHTListingList;
    }

    @Override
    public Integer sp_processRefundRequest_da(RefundWFList req, PaymentRequest paymentRequest) {
        Integer rttWfId = 0;
        Integer rtt_item_id = 0;
        String finalResult = "";

        try {
            String createdBy = req.getCreated_by();
            String modifiedBy = req.getModified_by();

            // Step 1: Insert into rms_rtt_wf and get rtt_wf_id
            rttWfId = storeProcedureRepository.sp_insrttwf_da(req);
            System.out.println("Generated rtt_wf_id: " + rttWfId);

            if (rttWfId > 0) {
                List<PaymentItemDetails> itemDetailsList = paymentRequest.getPayment_item_details();

                for (PaymentItemDetails item : itemDetailsList) {
                    System.out.println("Inserting refund item: " + item);

                    rtt_item_id = storeProcedureRepository.sp_insertRefundItem(item, rttWfId, createdBy, modifiedBy);

                    if (rtt_item_id < 1) {
                        finalResult = "RTT Item table insert failed for item_ref_no: " + item.getItem_ref_no();
                        System.out.println(finalResult);
                        throw new Exception(finalResult);
                    }
                    System.out.println("Successfully inserted item_ref_no: " + item.getItem_ref_no());
                }

                finalResult = "Insert successful";
                System.out.println(finalResult);
            } else if (rttWfId == -3) {

                finalResult = "RTT Form table insert failed, appeal count over 3 times or Refund status is not rejected";

                return rttWfId; // Directly returning -3 allows the controller to handle it appropriately.

            } else if (rttWfId == -1) {

                finalResult = "Reference No. Cannot be null or empty";

                return rttWfId;
            }

            else {
                finalResult = "Unknown error during insert RTT WF table insert failed";
                throw new Exception(finalResult);
            }

        } catch (Exception e) {
            log.error("Error during refund request processing: {}. Cause chain: {}", e.getMessage(), flattenCauses(e),
                    e);
            rttWfId = -5; // Indicate failure
        }
        return rttWfId;
    }

    @Override
    public Long sp_insrttform_rs02(RefundWFList req) {
        Long rttformid = 0L;

        try {
            String createdBy = req.getCreated_by();
            String modifiedBy = req.getModified_by();

            System.out.println(createdBy);

            // Call the stored procedure repository method
            rttformid = storeProcedureRepository.sp_insrttform_rs02(req);
            System.out.println("Generated rtt_form_id: " + rttformid);

            if (rttformid < 0) {
                System.out.println("RTT Form table insert failed");
                throw new Exception("RTT Form table insert failed");
            }

        } catch (Exception e) {
            System.out.println("Error during refund request processing: " + e.getMessage());
            e.printStackTrace();
            rttformid = -1L; // Indicate failure
        }

        return rttformid;
    }

    @Override
    public Integer sp_processRefundRequest_rf(RefundWFList req, PaymentRequest paymentRequest, RefundWFList refundDoc) {
        Integer rttWfId = 0;
        Integer rtt_item_id = 0;
        Integer rtt_doc_id = 0;
        String finalResult = "";

        try {
            log.info("Received RefundWFList in Service: {}", req);
            log.info("Received PaymentRequest in Service: {}", paymentRequest);
            log.info("Received RefundDoc in Service: {}", refundDoc);

            String createdBy = req.getCreated_by();
            String modifiedBy = req.getModified_by();

            // Step 1: Insert into rms_rtt_wf and get rtt_wf_id
            rttWfId = storeProcedureRepository.sp_insrttwf_rf(req);
            log.info("Generated rtt_wf_id: {}", rttWfId);

            if (rttWfId > 0) {
                List<PaymentItemDetails> itemDetailsList = paymentRequest.getPayment_item_details();

                for (PaymentItemDetails item : itemDetailsList) {
                    log.info("Inserting refund item: {}", item);

                    rtt_item_id = storeProcedureRepository.sp_insertRefundItem(item, rttWfId, createdBy, modifiedBy);

                    if (rtt_item_id < 1) {
                        finalResult = "RTT Item table insert failed for item_ref_no: " + item.getItem_ref_no();
                        log.error(finalResult);
                        throw new Exception(finalResult);
                    }
                    log.info("Successfully inserted item_ref_no: {}", item.getItem_ref_no());
                }

                // Step 3: Insert refund documents into the document table
                List<RefundDoc> uploadedList = refundDoc.getUploadedFiles();
                if (uploadedList != null && !uploadedList.isEmpty()) {
                    for (RefundDoc doc : uploadedList) {
                        boolean success = false;
                        int maxRetries = 3;

                        for (int attempt = 1; attempt <= maxRetries && !success; attempt++) {
                            try {
                                log.info("Processing refund document (attempt {}/{}) [wfId={}, fileName={}]",
                                        attempt, maxRetries, rttWfId, doc.getFile_nm());

                                // Decode Base64 content
                                byte[] decodedBytes = decodeBase64(doc.getFile_content());
                                if (decodedBytes == null || decodedBytes.length == 0) {
                                    throw new IllegalArgumentException(
                                            "Invalid or empty file content for document: " + doc.getFile_nm());
                                }

                                Blob blob = new SerialBlob(decodedBytes);

                                // Insert into database
                                rtt_doc_id = storeProcedureRepository.sp_insertRefundDoc(
                                        doc, blob, rttWfId, createdBy, modifiedBy);

                                if (rtt_doc_id < 1) {
                                    throw new Exception("RTT Document table insert failed for document_ref_no: "
                                            + doc.getRtt_doc_id());
                                }

                                log.info("Successfully inserted document [wfId={}, fileName={}, rtt_doc_id={}]",
                                        rttWfId, doc.getFile_nm(), rtt_doc_id);
                                success = true;

                            } catch (Exception e) {
                                log.error(
                                        "Failed to insert refund document (attempt {}/{}) [wfId={}, fileName={}, size={}]: {}. Cause chain: {}",
                                        attempt, maxRetries, rttWfId, doc.getFile_nm(), doc.getFileSize(),
                                        e.getMessage(), flattenCauses(e), e);

                                if (attempt == maxRetries) {
                                    // after last attempt, give up → rethrow to trigger rollback
                                    throw e;
                                }

                                try {
                                    // short delay before retrying
                                    Thread.sleep(500L);
                                } catch (InterruptedException ie) {
                                    Thread.currentThread().interrupt();
                                }
                            }
                        }
                    }
                }

                finalResult = "Insert successful";
                log.info(finalResult);

            } else if (rttWfId == -3) {

                finalResult = "RTT Form table insert failed, appeal count over 3 times or Refund status is not rejected";

                return rttWfId; // Directly returning -3 allows the controller to handle it appropriately.

            } else if (rttWfId == -1) {

                finalResult = "Reference No. Cannot be null or empty";

                return rttWfId;
            }

            else {
                finalResult = "Unknown error during insert RTT WF table insert failed";
                throw new Exception(finalResult);
            }

        } catch (Exception e) {
            log.error("Error during refund request processing: {}. Cause chain: {}", e.getMessage(), flattenCauses(e),
                    e);
            rttWfId = -5; // Indicate failure
        }

        return rttWfId;
    }

    private String flattenCauses(Throwable t) {
        StringBuilder sb = new StringBuilder();
        while (t != null) {
            sb.append(t.getClass().getSimpleName()).append(": ").append(t.getMessage());
            t = t.getCause();
            if (t != null)
                sb.append(" -> ");
        }
        return sb.toString();
    }

    private byte[] decodeBase64(String base64String) {
        if (base64String.startsWith("data:")) {
            base64String = base64String.substring(base64String.indexOf(",") + 1);
        }
        base64String = base64String.replaceAll("[\\s.]", ""); // Remove spaces and dots
        return Base64.getDecoder().decode(base64String);
    }

    private boolean isBase64(String base64String) {
        try {
            if (base64String == null || base64String.trim().isEmpty()) {
                return false;
            }
            // Remove data URI prefix if present
            if (base64String.startsWith("data:")) {
                base64String = base64String.substring(base64String.indexOf(",") + 1);
            }
            // Attempt to decode
            Base64.getDecoder().decode(base64String);
            return true;
        } catch (IllegalArgumentException e) {
            return false; // Invalid base64 string
        }
    }

    @Override
    public Integer sp_uptrtt_dateexpiry(RefundPTTListingDetReq updateRequest) {
        Integer result = 0;

        result = storeProcedureRepository.sp_uptrtt_dateexpiry(updateRequest);

        return result;
    }

    @Override
    public List<RefundList> sp_getrttwfid(RefundPTTListingDetReq req) {

        List<RefundList> result = new ArrayList<>();

        try {
            List<Object[]> objects = storeProcedureRepository.sp_getrttwfid(req);
            result = convertToRefundListingList(objects);
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<RefundList> convertToRefundListingList(List<Object[]> objects) {
        List<RefundList> refundLists = new ArrayList<>();

        for (Object[] obj : objects) {
            RefundList refundlist = new RefundList();
            refundlist.setRtt_wf_id((Integer) obj[0]);
            refundlist.setRefund_ty((String) obj[1]);
            refundlist.setOrn_no((String) obj[2]);
            refundlist.setRefund_cd((String) obj[3]);
            refundlist.setRtt_status((String) obj[4]);
            refundLists.add(refundlist);
        }

        return refundLists;
    }

    @Override
    public Integer sp_updateRefundRequest_rf(RefundWFList req, PaymentRequest paymentRequest, RefundWFList refundDoc) {
        Integer rttWfId = req.getRtt_wf_id(); // This must be provided in the update request
        Integer rtt_item_id = 0;
        Integer rtt_doc_id = 0;
        String finalResult = "";

        try {
            // Step 1: Update the main refund workflow record via stored procedure
            Integer updateResult = storeProcedureRepository.sp_updrttwf_rf(req);
            if (updateResult < 1) {
                throw new Exception("Failed to update main refund workflow record.");
            }
            System.out.println("Main refund record updated successfully, rtt_wf_id: " + rttWfId);

            // Step 2: Process each payment item
            List<PaymentItemDetails> itemDetailsList = paymentRequest.getPayment_item_details();
            if (itemDetailsList != null && !itemDetailsList.isEmpty()) {
                for (PaymentItemDetails item : itemDetailsList) {
                    System.out.println("Processing refund item: " + item);
                    if (item.getRtt_item_id() != null) {
                        // Update existing refund item
                        rtt_item_id = storeProcedureRepository.sp_updateRefundItem(item, rttWfId, req.getModified_by());
                        if (rtt_item_id < 1) {
                            finalResult = "Refund item update failed for item_ref_no: " + item.getItem_ref_no();
                            System.out.println(finalResult);
                            throw new Exception(finalResult);
                        }
                        System.out.println("Successfully updated refund item: " + item.getItem_ref_no());
                    } else {
                        // Insert new refund item if identifier is not provided
                        rtt_item_id = storeProcedureRepository.sp_insertRefundItem(item, rttWfId, req.getCreated_by(),
                                req.getModified_by());
                        if (rtt_item_id < 1) {
                            finalResult = "Refund item insert failed for item_ref_no: " + item.getItem_ref_no();
                            System.out.println(finalResult);
                            throw new Exception(finalResult);
                        }
                        System.out.println("Successfully inserted new refund item: " + item.getItem_ref_no());
                    }
                }
            }

            // Step 3: Process new refund documents (always inserted)
            List<RefundDoc> uploadedList = refundDoc.getUploadedFiles();
            if (uploadedList != null && !uploadedList.isEmpty()) {
                for (RefundDoc doc : uploadedList) {
                    try {
                        System.out.println("Processing refund document: " + doc);
                        byte[] decodedBytes = decodeBase64(doc.getFile_content());
                        if (decodedBytes == null || decodedBytes.length == 0) {
                            throw new IllegalArgumentException(
                                    "Invalid or empty file content for document: " + doc.getFile_nm());
                        }
                        Blob blob = new SerialBlob(decodedBytes);
                        rtt_doc_id = storeProcedureRepository.sp_insertRefundDoc(doc, blob, rttWfId,
                                req.getCreated_by(), req.getModified_by());
                        if (rtt_doc_id < 1) {
                            finalResult = "Refund document insert failed for document: " + doc.getFile_nm();
                            System.out.println(finalResult);
                            throw new Exception(finalResult);
                        }
                        System.out.println("Successfully inserted refund document: " + doc.getFile_nm());
                    } catch (Exception e) {
                        System.out.println(
                                "Error processing document: " + doc.getFile_nm() + " Error: " + e.getMessage());
                    }
                }
            }

            finalResult = "Update successful";
            System.out.println(finalResult);
            return rttWfId;
        } catch (Exception e) {
            System.out.println("Error during refund update processing: " + e.getMessage());
            e.printStackTrace();
            return -1; // Indicate failure
        }
    }

    @Override
    public List<RefundList> sp_getRefundListing(RefundPTTListingDetReq req) {

        List<RefundList> result = new ArrayList<>();

        try {
            List<Object[]> objects = storeProcedureRepository.sp_getRefundListing(req);
            result = convertToRefundWFListingList(objects);
        } catch (Exception e) {
            e.printStackTrace();

        }

        return result;
    }

    private List<RefundList> convertToRefundWFListingList(List<Object[]> objects) {
        List<RefundList> refundListingList = new ArrayList<>();
        for (Object[] obj : objects) {
            RefundList refundListing = new RefundList();
            refundListing.setRtt_wf_id((Integer) obj[0]);
            refundListing.setOrn_no((String) obj[1]);
            refundListing.setRtt_app_no((String) obj[2]);
            refundListing.setDt_created((Date) obj[3]);
            refundListing.setTxn_id((String) obj[4]);
            refundListing.setRefund_amt((BigDecimal) obj[5]);
            refundListing.setRtt_status((String) obj[6]);
            refundListing.setRcpt_no((String) obj[7]);
            refundListing.setRefund_ty((String) obj[8]);
            refundListing.setMtt_id((Integer) obj[9]);
            refundListing.setRms_type((String) obj[10]);
            refundListing.setTotal((Integer) obj[11]);

            refundListingList.add(refundListing);
        }
        return refundListingList;
    }

    @Override
    public RttAppEmailDto sp_getRttAppEmail(int rttWfId) {
        return rttReturnedChequeRepository.findAppEmailByWfId(rttWfId);
    }

}
