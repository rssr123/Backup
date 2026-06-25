package com.maven.rms.services.OTC;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maven.rms.models.Email;
import com.maven.rms.models.IdamanAPIUpload;
import com.maven.rms.models.IdamanAPIUploadReq;
import com.maven.rms.models.OTC.OTCCollectionReceipting;
import com.maven.rms.models.OTC.OTCCollectionReceiptingPymtItem;
import com.maven.rms.models.OTC.OTCEMV;
import com.maven.rms.models.OTC.OTCEMVRequest;
import com.maven.rms.models.OTC.OTCHist;
import com.maven.rms.models.OTC.OTCHistReq;
import com.maven.rms.models.OTC.OTCPayment;
import com.maven.rms.models.OTC.OTCPaymentDetails;
import com.maven.rms.models.OTC.OTCPaymentDone;
import com.maven.rms.models.OTC.OTCPaymentRequest;
import com.maven.rms.models.OTC.OTCRcpt;
import com.maven.rms.models.OTC.OTCRcptRequest;
import com.maven.rms.models.OTC.OTCollectionReceiptRequest;
import com.maven.rms.models.OTC.OTCollectionReceiptingRequest;
import com.maven.rms.repositories.OTC.OTCCollectionReceiptingRepository;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.IdamanAPIUploadService;
import com.maven.rms.utils.receipts.OTCReceiptGenerator;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class OTCCollectionReceiptingService implements IOTCCollectionReceiptingServiceInterface {
    private final OTCCollectionReceiptingRepository otcCollectionReceiptingRepository;
    @Autowired
    private AuthService authService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OTCReceiptGenerator receiptGenerator;

    @Autowired
    private IdamanAPIUploadService idamanAPIUploadService;

    public OTCCollectionReceiptingService(OTCCollectionReceiptingRepository otcCollectionReceiptingRepository) {
        this.otcCollectionReceiptingRepository = otcCollectionReceiptingRepository;
    }

    @Override
    public List<OTCCollectionReceipting> sp_getcollectioninfo(
            OTCollectionReceiptingRequest otCollectionReceiptingRequest) {
        List<OTCCollectionReceipting> result = Collections.emptyList();
        List<Object[]> objects = otcCollectionReceiptingRepository.sp_getcollectioninfo(otCollectionReceiptingRequest);
        result = convertOTCReceiptingList(objects);
        return result;
    }

    private List<OTCCollectionReceipting> convertOTCReceiptingList(List<Object[]> objects) {
        List<OTCCollectionReceipting> otcCollectionReceiptings = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCCollectionReceipting otcCollectionReceipting = new OTCCollectionReceipting();
            otcCollectionReceipting.setMtt_id((Integer) obj[0]);
            otcCollectionReceipting.setSs_cd((String) obj[1]);
            otcCollectionReceipting.setColl_slip_no((String) obj[2]);
            otcCollectionReceipting.setOrn_no((String) obj[3]);
            otcCollectionReceipting.setCust_nm((String) obj[4]);
            otcCollectionReceipting.setCust_phone((String) obj[5]);
            otcCollectionReceipting.setCust_email((String) obj[6]);
            otcCollectionReceipting.setCust_addr1((String) obj[7]);
            otcCollectionReceipting.setCust_addr2((String) obj[8]);
            otcCollectionReceipting.setCust_addr3((String) obj[9]);
            otcCollectionReceipting.setCust_postcode((String) obj[10]);
            otcCollectionReceipting.setCust_city((String) obj[11]);
            otcCollectionReceipting.setCust_state((String) obj[12]);
            otcCollectionReceipting.setTotal_amt((BigDecimal) obj[13]);
            otcCollectionReceipting.setOrder_status((String) obj[14]);
            otcCollectionReceipting.setPayment_mode((String) obj[15]);
            otcCollectionReceipting.setTotal((Integer) obj[16]);

            otcCollectionReceiptings.add(otcCollectionReceipting);
        }

        return otcCollectionReceiptings;
    }

    @Override
    public List<OTCCollectionReceiptingPymtItem> sp_otccrpymtitem(
            OTCollectionReceiptingRequest otCollectionReceiptingRequest) {
        List<OTCCollectionReceiptingPymtItem> result = Collections.emptyList();
        List<Object[]> objects = otcCollectionReceiptingRepository.sp_otccrpymtitem(otCollectionReceiptingRequest);
        result = convertOTCReceiptingPymtList(objects);
        return result;
    }

    @Override
    public List<OTCCollectionReceiptingPymtItem> sp_otccrpymtitembymtt(
            OTCollectionReceiptingRequest otCollectionReceiptingRequest) {
        List<OTCCollectionReceiptingPymtItem> result = Collections.emptyList();
        List<Object[]> objects = otcCollectionReceiptingRepository.sp_otccrpymtitembymtt(otCollectionReceiptingRequest);
        result = convertOTCReceiptingPymtList(objects);
        return result;
    }

    private List<OTCCollectionReceiptingPymtItem> convertOTCReceiptingPymtList(List<Object[]> objects) {
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
            otcCollectionReceipting.setNet_amt((BigDecimal) obj[8]);
            otcCollectionReceipting.setTotal((Integer) obj[9]);

            otcCollectionReceiptings.add(otcCollectionReceipting);
        }
        return otcCollectionReceiptings;
    }

    @Override
    public Integer sp_insotcpymt(OTCPaymentRequest insertRequest) {
        Integer result = 0;
        result = otcCollectionReceiptingRepository.sp_insotcpymt(insertRequest);
        return result;
    }

    @Override
    public Integer sp_insotcpymtbody(List<OTCPaymentRequest> insertRequest) throws IOException {
        Integer result = 0;

        Integer otc_id = otcCollectionReceiptingRepository.sp_insotcpymtbody(insertRequest);

        // Insert into rms_otc_hist table
        // OTCHistReq histReq = new OTCHistReq();
        // histReq.setI_mtt_id(insertRequest.get(0).getI_mtt_id());
        // histReq.setI_otc_id(otc_id);
        // histReq.setI_action("Payment Received");
        // histReq.setI_otc_status("Paid");

        // histReq.setI_dt_action(timestamp);
        // histReq.setI_counter_id("KUL001");
        // histReq.setI_act_by(authService.getLoginUserName());
        // histReq.setI_created_by(authService.getLoginUserName());
        // histReq.setI_modified_by(authService.getLoginUserName());

        // result = otcCollectionReceiptingRepository.sp_insotchistupdmtt(histReq);
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.now());
        // Insert into rms_otc_rcpt table
        OTCRcptRequest otcRcptRequest = new OTCRcptRequest();
        otcRcptRequest.setI_otc_id(otc_id);
        otcRcptRequest.setI_rcpt_no("TEST123");
        otcRcptRequest.setI_rcpt_dt(timestamp);
        otcRcptRequest.setI_rcpt_status("Valid");
        otcRcptRequest.setI_rcpt_reprint(0);
        otcRcptRequest.setI_is_uploaded(0);
        otcRcptRequest.setI_ver_id("1");
        otcRcptRequest.setI_ssdocref_id("");
        otcRcptRequest.setI_created_by(authService.getLoginUserName());
        otcRcptRequest.setI_created_by(authService.getLoginUserName());
        otcRcptRequest.setI_file_nm("");
        otcRcptRequest.setI_remark("");

        OTCRcpt otcRcpt = sp_insotcrcpt(otcRcptRequest);
        OTCPaymentDone payment = sp_getotcorder(insertRequest.get(0).getI_mtt_id());

        // Use SimpleDateFormat to format the date
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
        String formattedDate = sdf.format(payment.getPayment_dt());
        payment.setFormattedDate(formattedDate);

        OTCHistReq collectionSlipSubmit = new OTCHistReq();
        collectionSlipSubmit.setI_mtt_id(insertRequest.get(0).getI_mtt_id());
        collectionSlipSubmit.setI_otc_id(otc_id);
        collectionSlipSubmit.setI_action("Collection Slip Submitted");
        collectionSlipSubmit.setI_otc_status("PO");
        collectionSlipSubmit.setI_dt_action(payment.getDt_created());
        collectionSlipSubmit.setI_counter_id(payment.getCounter_id());
        collectionSlipSubmit.setI_act_by(authService.getLoginUserName());
        collectionSlipSubmit.setI_created_by(authService.getLoginUserName());
        collectionSlipSubmit.setI_modified_by(authService.getLoginUserName());

        result = otcCollectionReceiptingRepository.sp_insotchist(collectionSlipSubmit);

        OTCollectionReceiptingRequest otCollectionReceiptingRequest = new OTCollectionReceiptingRequest();
        otCollectionReceiptingRequest.setI_mtt_id(insertRequest.get(0).getI_mtt_id());

        List<OTCCollectionReceiptingPymtItem> paymentItems = sp_otccrpymtitembymtt(otCollectionReceiptingRequest);
        // Generate Receipt
        // File pdfRcpt = receiptGenerator.generateReceipt(new ReceiptRequest(pG,
        // payment, rcpt, paymentItems, "pdf"));
        File pdfRcpt = receiptGenerator
                .generateReceipt(new OTCollectionReceiptRequest(payment, otcRcpt, paymentItems, "pdf"));

        Timestamp timestamp3 = Timestamp.valueOf(LocalDateTime.now());
        OTCHistReq paid = new OTCHistReq();
        paid.setI_mtt_id(insertRequest.get(0).getI_mtt_id());
        paid.setI_otc_id(otc_id);
        paid.setI_action("Payment Received");
        paid.setI_otc_status("P");
        paid.setI_dt_action(timestamp3);
        paid.setI_counter_id(payment.getCounter_id());
        paid.setI_act_by(authService.getLoginUserName());
        paid.setI_created_by(authService.getLoginUserName());
        paid.setI_modified_by(authService.getLoginUserName());

        result = otcCollectionReceiptingRepository.sp_insotchistupdmtt(paid);

        // Send payment email
        String body = "Entity Name: " + payment.getCust_nm()
                + "<br>Receipt No: " + otcRcpt.getRcptNo().toUpperCase()
                + "<br>Order Reference No.: " + payment.getOrn_no().toUpperCase()
                + "<br>Total Amount Paid: RM" + String.format("%.2f", payment.getTotal_amount_paid().doubleValue())
                + "<br><br>Dear Sir/Madam,<br>We are pleased to inform you that your "
                + "payment made in counter has been successfully processed. An official payment receipt "
                + "has been generated for your records. Please find the attached receipt for "
                + "your reference.<br>Thank you for using our services.<br><br><br>Tuan/Puan,<br>"
                + "Dengan hormatnya, kami berbesar hati ingin memaklumkan bahawa pembayaran "
                + "dalam talian anda telah berjaya diproses. Bersama-sama ini disertakan resit "
                + "pembayaran untuk perhatian pihak Tuan/Puan selanjutnya.\r<br>Terima kasih kerana"
                + " menggunakan perkhidmatan kami.<br><br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
                + "DO NOT REPLY DIRECTLY TO THIS EMAIL]<br>";

        // save email object into db
        Email email = new Email("Receipt", payment.getCust_email(), "", "",
                "PAYMENT SUCCESSFUL - RECEIPT ATTACHED", body, payment.getOrn_no().toUpperCase(), pdfRcpt);

        // save and send email
        email = emailService.saveEmailDets(email);
        Boolean emailSent = false;

        try {
            // emailService.sendMail(email);
            emailService.sendMailWithAttachment(email, true);
            emailSent = true;

            Timestamp timestamp2 = Timestamp.valueOf(LocalDateTime.now());
            OTCHistReq emailPending = new OTCHistReq();
            emailPending.setI_mtt_id(insertRequest.get(0).getI_mtt_id());
            emailPending.setI_otc_id(otc_id);
            emailPending.setI_action("Email Receipt Pending");
            emailPending.setI_otc_status("EP");
            emailPending.setI_dt_action(timestamp2);
            emailPending.setI_counter_id(payment.getCounter_id());
            emailPending.setI_act_by(authService.getLoginUserName());
            emailPending.setI_created_by(authService.getLoginUserName());
            emailPending.setI_modified_by(authService.getLoginUserName());

            result = otcCollectionReceiptingRepository.sp_insotchist(emailPending);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            emailSent = false;
            email.setRetryCnt(1);
            emailService.saveEmailDets(email);
        } finally {
            if (emailSent) {
                // 'S' = Sent
                email.setStatus("S");
                // update email status into db
                emailService.saveEmailDets(email);

                Timestamp timestamp4 = Timestamp.valueOf(LocalDateTime.now());
                OTCHistReq emailsent = new OTCHistReq();
                emailsent.setI_mtt_id(insertRequest.get(0).getI_mtt_id());
                emailsent.setI_otc_id(otc_id);
                emailsent.setI_action("Email Receipt Sent");
                emailsent.setI_otc_status("ES");
                emailsent.setI_dt_action(timestamp4);
                emailsent.setI_counter_id(payment.getCounter_id());
                emailsent.setI_act_by(authService.getLoginUserName());
                emailsent.setI_created_by(authService.getLoginUserName());
                emailsent.setI_modified_by(authService.getLoginUserName());

                result = otcCollectionReceiptingRepository.sp_insotchist(emailsent);
            }

            try {
                // Upload receipt to IDAMAN
                // Create random GUID
                UUID uuid = UUID.randomUUID();
                String guid = "RMS-" + uuid.toString();

                byte[] fileContent = Files.readAllBytes(Paths.get(pdfRcpt.toString()));
                String encodedString = Base64.getEncoder().encodeToString(fileContent);

                String formatedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                
                // Call Upload idaman API
                Integer result1 = uploadIdamanAPI(
                        new IdamanAPIUploadReq("RMS", otcRcpt.getRcptNo(), "RMSReceipt", formatedDate,
                                "", "", "", "", "", "", guid, payment.getOrn_no(), "", "", "", "", "", "",
                                encodedString, pdfRcpt.getName()),
                        otcRcpt.getOtc_rcpt_id(), pdfRcpt.getName());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        return otc_id;
    }

    private Integer uploadIdamanAPI(IdamanAPIUploadReq req, Integer otcRcptID, String file_nm) throws IOException {
        List<IdamanAPIUpload> result = Collections.emptyList();
        Integer result1 = -1;

        // try {
        result = idamanAPIUploadService.idaman_api_uploadDoc(req);
        // if (result.size() > 0) {
        if (CollectionUtils.size(result) > 0) {
            // update rcpt table
            result1 = sp_updotcrcpt(otcRcptID, result.get(0).getVerid(), req.getSourceSysDocRefID(), file_nm);
            return result1;// result.get(0).getDocRefID();
        }
        return result1;
    }

    @Override
    public List<OTCHist> sp_otccrhist(OTCPaymentRequest otCollectionReceiptingRequest) {
        List<OTCHist> result = Collections.emptyList();
        List<Object[]> objects = otcCollectionReceiptingRepository.sp_otccrhist(otCollectionReceiptingRequest);
        result = convertOTCHistList(objects);
        return result;
    }

    private List<OTCHist> convertOTCHistList(List<Object[]> objects) {
        List<OTCHist> otcHists = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCHist otcHist = new OTCHist();
            otcHist.setOtc_id((Integer) obj[0]);
            otcHist.setAction((String) obj[1]);
            otcHist.setDt_action((Date) obj[2]);
            otcHist.setOtc_status((String) obj[3]);
            otcHist.setCounter_id((String) obj[4]);
            otcHist.setAct_by((String) obj[5]);
            otcHist.setBranch((String) obj[6]);
            otcHist.setJustification((String) obj[7]);
            otcHist.setRemark((String) obj[8]);
            otcHist.setOthers((String) obj[9]);
            otcHist.setTotal((Integer) obj[10]);
            
            otcHists.add(otcHist);
        }

        return otcHists;
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
    public OTCRcpt sp_insotcrcpt(OTCRcptRequest insertRequest) {
        OTCRcpt result = new OTCRcpt();
        Object[] resultSet = otcCollectionReceiptingRepository.sp_insotcrcpt(insertRequest);
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
    public OTCPaymentDone sp_getotcorder(Integer i_mtt_id) {
        
        OTCPaymentDone result = new OTCPaymentDone();
        Object[] resultSet = otcCollectionReceiptingRepository.sp_getotcorder(i_mtt_id);
        
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
    public Integer sp_insemvsale(OTCEMVRequest insertRequest) {
        Integer result = 0;
        result = otcCollectionReceiptingRepository.sp_insemvsale(insertRequest);
        return result;
    }


    @Override
    public List<OTCRcpt> sp_getotcrcpt(OTCPaymentRequest otCollectionReceiptingRequest) {
        List<OTCRcpt> result = Collections.emptyList();
        List<Object[]> objects = otcCollectionReceiptingRepository.sp_getotcrcpt(otCollectionReceiptingRequest);
        result = convertOTCRcptList(objects);
        return result;
    }

    private List<OTCRcpt> convertOTCRcptList(List<Object[]> objects) {
        List<OTCRcpt> otcRcpts = new ArrayList<>();

        for (Object[] obj : objects) {
            OTCRcpt otcRcpt = new OTCRcpt();
            otcRcpt.setOtc_id((Integer) obj[0]);
            otcRcpt.setRcptNo((String) obj[1]);
            otcRcpt.setRcpt_dt((Date) obj[2]);
            otcRcpt.setRcpt_status((String) obj[3]);
            otcRcpt.setRcpt_reprint((Integer) obj[4]);
            otcRcpt.setIs_uploaded((Integer) obj[5]);
            otcRcpt.setVer_id((String) obj[6]);

            otcRcpt.setSsdocref_id((String) obj[7]);
            otcRcpt.setDt_created((Date) obj[8]);
            otcRcpt.setDt_modified((Date) obj[9]);
            otcRcpt.setCreated_by((String) obj[10]);
            otcRcpt.setModified_by((String) obj[11]);
            otcRcpt.setStatus((String) obj[12]);
            otcRcpt.setFile_nm((String) obj[13]);
            otcRcpt.setRemark((String) obj[14]);

            otcRcpts.add(otcRcpt);
        }

        return otcRcpts;
    }


    @Override
    public OTCPaymentDone sp_getotcorderemv(Integer i_mtt_id) {
        
        OTCPaymentDone result = new OTCPaymentDone();
        Object[] resultSet = otcCollectionReceiptingRepository.sp_getotcorderemv(i_mtt_id);
        
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
        result.setDt_created((Date) resultSet[16]);
        result.setCounter_id((String) resultSet[17]);
        result.setOtc_pymt_mode((String) resultSet[18]);
        result.setBranch_cd((String) resultSet[19]);
        result.setPayment_dt((Date) resultSet[20]);
        result.setEmv_terminal_id((String) resultSet[21]);
        result.setTrace_no((String) resultSet[22]);
        result.setTotal((Integer) resultSet[23]);

        return result;
    }

    @Override
    public OTCEMV sp_getotcemvsales(OTCPaymentRequest req) {
    
        // Create a new OTCEMV object
        OTCEMV otcEMV = new OTCEMV();
    
        // Retrieve the result set from the stored procedure
        Object[] resultSet = otcCollectionReceiptingRepository.sp_getotcemvsales(req);

        if(resultSet == null) {
            return null;
        }
    
        // Map the result set to the OTCEMV object
        otcEMV.setResp_cd((String) resultSet[0]);
        otcEMV.setCard_no((String) resultSet[1]);
        otcEMV.setDt_expiry((String) resultSet[2]);
        otcEMV.setStatus_cd((String) resultSet[3]);
        otcEMV.setApproval_cd((String) resultSet[4]);
        otcEMV.setRrn((String) resultSet[5]);
        otcEMV.setTrans_trace((String) resultSet[6]);
        otcEMV.setBatch_no((String) resultSet[7]);
        otcEMV.setHost_no((String) resultSet[8]);
        otcEMV.setT_id((String) resultSet[9]);
        otcEMV.setMer_id((String) resultSet[10]);
        otcEMV.setAid((String) resultSet[11]);
        otcEMV.setTc((String) resultSet[12]);
        otcEMV.setCardholder_nm((String) resultSet[13]);
        otcEMV.setCard_ty((String) resultSet[14]);
        otcEMV.setPrtnr_txn_id((String) resultSet[15]);
        otcEMV.setApay_txn_id((String) resultSet[16]);
        otcEMV.setCust_id((String) resultSet[17]);
        otcEMV.setAmt((BigDecimal) resultSet[18]);
        otcEMV.setAdd_data((String) resultSet[19]);
        otcEMV.setDt_created((Date) resultSet[20]);
        otcEMV.setDt_modified((Date) resultSet[21]);
        otcEMV.setCreated_by((String) resultSet[22]);
        otcEMV.setModified_by((String) resultSet[23]);
    
        // Return the populated OTCEMV object
        return otcEMV;
    }
    
}