package com.maven.rms.services;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.config.RMSProperties;
import com.maven.rms.interfaces.IOnlinePaymentService;
import com.maven.rms.models.CheckAccrual;
import com.maven.rms.models.Email;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.GHLPaymentResponse;
import com.maven.rms.models.GHLRequest;
import com.maven.rms.models.IdamanAPIUpload;
import com.maven.rms.models.IdamanAPIUploadReq;
import com.maven.rms.models.MTTPG;
import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.models.PaymentItemDetails;
import com.maven.rms.models.PaymentRequest;
import com.maven.rms.models.PaymentResponse;
import com.maven.rms.models.RILTRequest;
import com.maven.rms.models.RIPLRealizedRequest;
import com.maven.rms.models.ReceiptRequest;
import com.maven.rms.models.ServiceProviderRequest;
import com.maven.rms.models.SubmitBilPymtStatus;
import com.maven.rms.models.OTC.NBLInsRequest;
import com.maven.rms.models.payload.requests.SubmitRICPCanRequest;
import com.maven.rms.repositories.IOnlinePaymentRepository;
import com.maven.rms.repositories.MTTRCPTRepository;
import com.maven.rms.repositories.OnlinePaymentRepository;
import com.maven.rms.services.OTC.OTCReturnedChequeService;
import com.maven.rms.utils.receipts.MTTPGReceiptGenerator;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

@Service
@Slf4j
public class OnlinePaymentService implements IOnlinePaymentService {

    // private static final Logger logger =
    // LoggerFactory.getLogger(OnlinePaymentService.class);
    private final IOnlinePaymentRepository ionlinePaymentRepository;
    private final OnlinePaymentRepository opRep;
    private final MTTRCPTRepository rcptRepo;
    private final MTTService mttService;

    @Autowired
    private AuthService authService;

    // @Autowired
    // private StoreProcedureService spService;

    @Autowired
    private MTTPGReceiptGenerator receiptGenerator;

    @Autowired
    private IdamanAPIUploadService idamanAPIUploadService;

    private final String onlinePortalURL;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RICPService ricpSvc;

    private final RMSProperties rmsProperties;

    @Autowired
    private BillingService bSvc;

    @Autowired
    private OTCReturnedChequeService nbSvc;

    @Autowired
    private RIPLService riplSvc;

    @Autowired
    private RILTService riltSvc;

    @Autowired
    private CommonService commonSvc;

    public OnlinePaymentService(IOnlinePaymentRepository ionlinePaymentRepository, OnlinePaymentRepository opRep,
            MTTRCPTRepository rcptRepo, MTTService mttService, RMSProperties rmsProperties) {
        this.ionlinePaymentRepository = ionlinePaymentRepository;
        this.opRep = opRep;
        this.rcptRepo = rcptRepo;
        this.mttService = mttService;
        this.onlinePortalURL = rmsProperties.getOnlinePortalURL();
        this.rmsProperties = rmsProperties;
    }

    @Override
    public Integer sp_insertPaymentMTT(PaymentRequest paymentRequest, String username, String custIP) {
        // List<TaxCode> result = Collections.emptyList(); // Define a default return
        // value

        Integer mtt_id = 0;
        Integer mtt_item_id = 0;

        // temp hardcode rms_type to online for now
        // String rmsType="Online";
        String rmsType = paymentRequest.getPymt_method();

        String finalResult = "";

        try {
            // Perform the operation
            /*
             * mtt_id =
             * opRep.sp_insertPaymentMTT(rmsType,paymentRequest.getSs_cd(),paymentRequest.
             * getOrn_no(),paymentRequest.getOrn_dt(),custIP,paymentRequest.getCust_nm(),
             * paymentRequest.getCust_addr_1(),paymentRequest.getCust_addr_2(),
             * paymentRequest.getCust_addr_3(),paymentRequest.getCust_postcode(),
             * paymentRequest.getCust_city(),paymentRequest.getCust_state(),paymentRequest.
             * getCust_email(),paymentRequest.getCust_phone(),paymentRequest.getTotal_amt(),
             * paymentRequest.getSs_return_url(),username,username);
             */
            mtt_id = opRep.sp_insertPaymentMTT(paymentRequest, rmsType, custIP, username, username);

            // loop insert mtt_item
            if (mtt_id > 0) {
                List<PaymentItemDetails> itemDetailsList = paymentRequest.getPayment_item_details();
                for (PaymentItemDetails item : itemDetailsList) {
                    /*
                     * mtt_item_id =
                     * opRep.sp_insertPaymentMTTItem(mtt_id,item.getFee_detail_id(),item.
                     * getItem_ref_no(),item.getItem_desc(),item.getLine_no(),item.getQty(),item.
                     * getUnit_fee(),
                     * item.getGross_amt(),item.getGrant_cd(),item.getDisc_amt(),item.getTax_pct(),
                     * item.getTax_amt(),item.getNet_amt(),item.getEntity_type(),
                     * item.getEntity_no(),item.getEntity_nm(),item.getCp_no(),item.getCp_tier(),
                     * item.getCp_tier_amt(),item.getCp_tier_disc_pct(),
                     * username,username);
                     */
                    mtt_item_id = opRep.sp_insertPaymentMTTItem(item, mtt_id, username, username);

                    if (mtt_item_id < 1) {
                        finalResult = "MTT Item table insert failed";
                        // revert back MTT table
                        throw new Exception(finalResult);
                    }
                }

                finalResult = "insert successful";

            } else if (mtt_id < 0) {

                finalResult = "update successful";

            } else {
                finalResult = "MTT table insert failed";
                mtt_id = 0;
                throw new Exception(finalResult);
            }

            // if(result!=""){
            // finalResult="insert failed";
            // }
            // else{
            // finalResult="insert successful";
            // }
        } catch (NumberFormatException e) {
            // Handle the exception if feeGrpId is not a valid Long
            mtt_id = -1;
            finalResult = e.getMessage();
            log.error("Exception in " + this.getClass().toString(), e);
        } catch (Exception e) {
            // Handle other exceptions here
            mtt_id = -1;
            finalResult = e.getMessage();
            log.error("Exception in " + this.getClass().toString(), e);
        }

        return mtt_id;
    }

    @Override
    public OnlinePayment sp_getMTT(String ornNo) {
        OnlinePayment result = new OnlinePayment();

        try {
            // System.out.println("Geo " + onlinePaymentRepository.sp_getMTT(ornNo));
            // result = ionlinePaymentRepository.sp_getMTT(ornNo).orElse(null);
            result = mttService.getMttFromOrderNo(ornNo).orElse(null);

        } catch (NumberFormatException e) {
            log.error("Exception in " + this.getClass().toString(), e);
        } catch (Exception e) {
            log.error("Exception in " + this.getClass().toString(), e);
        }
        return result;
    }

    @Override
    public Integer sp_checkAccrual(CheckAccrual checkAccrual) {

        Integer result = 0;

        try {

            result = opRep.sp_checkAccrual(checkAccrual);

        } catch (NumberFormatException e) {
            log.error("Exception in " + this.getClass().toString(), e);
        } catch (Exception e) {
            log.error("Exception in " + this.getClass().toString(), e);
        }
        return result;
    }

    @Transactional(readOnly = true)
    @Override
    public OnlinePayment sp_getMTT(Integer mttid) {
        OnlinePayment result = new OnlinePayment();

        try {
            result = ionlinePaymentRepository.getOnlinePaymentByMttId(mttid).orElse(null);
            // result = onlinePaymentRepository.sp_getMTT(ornNo).orElse(null);
        } catch (NumberFormatException e) {
            log.error("Exception in " + this.getClass().toString(), e);
        } catch (Exception e) {
            log.error("Exception in " + this.getClass().toString(), e);
        }

        return result;
    }

    // Brian - 1-Aug-2024: who even made this blank function?????
    @Override
    public List<OnlinePaymentItem> sp_getMTTItem(Integer mttID) {
        List<OnlinePaymentItem> result = Collections.emptyList(); // Define a default return value

        try {
            // result = onlinePaymentRepository.sp_getMTTItem(mttID);
        } catch (NumberFormatException e) {
            log.error("Exception in " + this.getClass().toString(), e);
        } catch (Exception e) {
            log.error("Exception in " + this.getClass().toString(), e);
        }

        return result;
    }

    @Transactional(readOnly = true)
    public Optional<MTTRCPT> getExistingReceipt(int rmsMTTId) {
        return rcptRepo.sp_getRcptByMttId(rmsMTTId);
    }

    @Transactional(rollbackFor = Exception.class)
    public OnlinePayment saveMTT(OnlinePayment order) {
        try {
            ionlinePaymentRepository.save(order);
            return order;
        } catch (Exception e) {
            log.error("Exception in " + this.getClass().toString(), e);
            return null;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Integer deleteMTT(OnlinePayment order) {
        try {
            ionlinePaymentRepository.delete(order);
            return 1;
        } catch (Exception e) {
            log.error("Exception in " + this.getClass().toString(), e);
            return 0;
        }
    }

    @Override
    public Integer sp_insghlresp(GHLRequest insertRequest) {
        Integer result = 0;
        result = opRep.sp_insghlresp(insertRequest);
        return result;
    }

    @Override
    public Integer sp_updghlresp(GHLRequest insertRequest) {
        Integer result = 0;
        result = opRep.sp_updghlresp(insertRequest);
        return result;
    }

    @Override
    public List<GHLPaymentResponse> sp_getghlresp() {
        List<GHLPaymentResponse> result = Collections.emptyList();
        List<Object[]> objects = opRep.sp_getghlresp();
        result = convertGHLResp(objects);
        return result;
    }

    private List<GHLPaymentResponse> convertGHLResp(List<Object[]> objects) {
        List<GHLPaymentResponse> ghlPaymentResponses = new ArrayList<>();

        for (Object[] obj : objects) {
            GHLPaymentResponse ghlPaymentResponse = new GHLPaymentResponse();
            ghlPaymentResponse.setTransactionType((String) obj[0]);
            ghlPaymentResponse.setPaymentMethod((String) obj[1]);
            ghlPaymentResponse.setServiceID((String) obj[2]);
            ghlPaymentResponse.setPaymentID((String) obj[3]);
            ghlPaymentResponse.setOrderNumber((String) obj[4]);
            ghlPaymentResponse.setAmount((BigDecimal) obj[5]);
            ghlPaymentResponse.setCurrencyCode((String) obj[6]);
            ghlPaymentResponse.setHashValue((String) obj[7]);
            ghlPaymentResponse.setHashValue2((String) obj[8]);
            ghlPaymentResponse.setTxnID((String) obj[9]);
            ghlPaymentResponse.setIssuingBank((String) obj[10]);
            ghlPaymentResponse.setTxnStatus((Integer) obj[11]);
            ghlPaymentResponse.setTxnMsg((String) obj[12]);
            ghlPaymentResponse.setAuthCode((String) obj[13]);
            ghlPaymentResponse.setBankRefNo((String) obj[14]);
            ghlPaymentResponse.setTokenType((String) obj[15]);
            ghlPaymentResponse.setToken((String) obj[16]);
            ghlPaymentResponse.setRespTime((String) obj[17]);
            ghlPaymentResponse.setCardNoMask((String) obj[18]);
            ghlPaymentResponse.setCardHolder((String) obj[19]);
            ghlPaymentResponse.setCardType((String) obj[20]);
            ghlPaymentResponse.setCardExp((String) obj[21]);
            ghlPaymentResponse.setParam7((String) obj[22]);
            ghlPaymentResponses.add(ghlPaymentResponse);
        }
        return ghlPaymentResponses;
    }

    // 250327: Payment Logic move to OnlinePaymentService from Online Payment
    // Controller
    // online payment changes based on roy flow
    public void processPayment(GHLPaymentResponse ghlResponse, HttpServletResponse response, Boolean returned)
            throws Exception {

        GHLRequest ghlRequest = new GHLRequest();
        ghlRequest.setI_pymt_id(ghlResponse.getPaymentID());
        log.info("[OnlinePayment]-Step1-ghlRequest");
        sp_updghlresp(ghlRequest);

        log.debug("TXNID:" + ghlResponse.getTxnID() + "EGHL" + "Order Number: " + ghlResponse.getOrderNumber()
                + "Payment Id:" + ghlResponse.getPaymentID() + "TXN STATUS:" + ghlResponse.getTxnStatus());

        log.info("[OnlinePayment]-Step2-getLoginUserName");
        String username = authService.getLoginUserName();

        PaymentResponse result = new PaymentResponse();

        String ssCallBackURL = null;

        // Integer rowCount2 = mttService.sp_updatePayment(ghlResponse, username);

        // if (rowCount == 0) {
        // // return APIResponse.InternalServerError();
        // }

        // update order status
        // String result2 =
        // mttService.sp_checkLatestOrderStatus(ghlResponse.getOrderNumber());
        log.info("[OnlinePayment]-Step3-sp_checkornno");
        Integer ornExist = mttService.sp_checkornno(ghlResponse.getOrderNumber());

        // if the order number already exist in mtt_pg table, send duplicate email to
        // the customer
        log.debug("OrnExist:" + ornExist);
        if (ornExist > 0) {
            // send duplicate email
            // Split the string based on a space delimiter
            String[] details = ghlResponse.getParam7().split(" ");

            // Extract the individual attributes
            String cust_nm = details.length > 0 ? details[0] : "";
            String cust_email = details.length > 1 ? details[1] : "";

            String body = "Entity Name: " + cust_nm
                    + "<br>Order Reference No.: " + ghlResponse.getOrderNumber().toUpperCase()
                    + "<br>Total Amount Paid: RM" + String.format("%.2f", ghlResponse.getAmount())
                    + "<br><br>Dear Sir/Madam,<br>We would like to bring your attention that it appears a duplicated payment has been processed in error for your account. "
                    + "We kindly request you to review this matter at your earliest convenience.<br>We sincerely apologize for the incovenience caused.<br><br><br>Tuan/Puan,<br>"
                    + "Kami ingin memaklumkan bahawa terdapat satu bayaran ulang telah diproses secara tidak sengaja kepada akaun anda. "
                    + "Kami rendah hati ingin meminta anda untuk meyemak perkara ini secepat mungkin."
                    + "<br>Segala kesulitan amat dikesali."
                    + "<br><br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
                    + "DO NOT REPLY DIRECTLY TO THIS EMAIL] <br>";

            log.info("[OnlinePayment]-Step4-saveEmailDets");

            if (sp_checkemailsent(ghlResponse.getOrderNumber()) <= 0) {
                emailService.saveEmailDets(
                        (new Email("Receipt", cust_email, "", "", "DUPLICATED PAYMENT", body,
                                ghlResponse.getOrderNumber().toUpperCase(), null)));
            }

            
            OnlinePayment payment = new OnlinePayment();

            log.info("[OnlinePayment]-Step5-getMttFromOrderNo");
            payment = mttService.getMttFromOrderNo(ghlResponse.getOrderNumber()).orElse(null);
            // payment = onlinePaymentService.sp_getMTT(ghlResponse.getOrderNumber()); vicky
            // comment this, not sure why not working
            if (returned) {
                log.info("[OnlinePayment]-Step6-sendRedirect");
                response.sendRedirect(onlinePortalURL + "/payment-response?orn_no=" + ghlResponse.getOrderNumber()
                        + "&pymt_status=" + payment.getOrder_status() + "&rcpt_dt="
                        + "&rcpt_no=" + "&ss_return_url=" + payment.getSs_return_url());
            }
        }

        log.info("[OnlinePayment]-Step7-sp_checktxn");
        Integer txnExist = mttService.sp_checktxn(ghlResponse.getOrderNumber(), ghlResponse.getPaymentID());
        log.debug("TxnExist:" + txnExist);
        if (txnExist > 0) {

            log.info("[OnlinePayment]-Step8-sp_checktxnid");
            String txnID = mttService.sp_checktxnid(ghlResponse.getOrderNumber(), ghlResponse.getPaymentID());
            log.debug("order number: " + ghlResponse.getOrderNumber() + " Payment ID: " + ghlResponse.getPaymentID());
            log.debug("TXN ID: " + txnID);

            if (txnID != null && !txnID.equals(ghlResponse.getTxnID())) {
                // update mttpg
                // Split the string based on a space delimiter
                String[] details = ghlResponse.getParam7().split(" ");

                // Extract the individual attributes
                String cust_nm = details.length > 0 ? details[0] : "";
                String cust_email = details.length > 1 ? details[1] : "";

                String body = "Entity Name: " + cust_nm
                        + "<br>Order Reference No.: " + ghlResponse.getOrderNumber().toUpperCase()
                        + "<br>Total Amount Paid: RM" + String.format("%.2f", ghlResponse.getAmount())
                        + "<br><br>Dear Sir/Madam,<br>We would like to bring your attention that it appears a duplicated payment has been processed in error for your account. "
                        + "We kindly request you to review this matter at your earliest convenience.<br>We sincerely apologize for the incovenience caused.<br><br><br>Tuan/Puan,<br>"
                        + "Kami ingin memaklumkan bahawa terdapat satu bayaran ulang telah diproses secara tidak sengaja kepada akaun anda. "
                        + "Kami rendah hati ingin meminta anda untuk meyemak perkara ini secepat mungkin."
                        + "<br>Segala kesulitan amat dikesali."
                        + "<br><br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
                        + "DO NOT REPLY DIRECTLY TO THIS EMAIL] <br>";
                log.info("[OnlinePayment]-Step9-saveEmailDets");

                if (sp_checkemailsent(ghlResponse.getOrderNumber()) <= 0) {
                    emailService.saveEmailDets(
                            (new Email("Receipt", cust_email, "", "", "DUPLICATED PAYMENT", body,
                                    ghlResponse.getOrderNumber().toUpperCase(), null)));
                }

                OnlinePayment payment = new OnlinePayment();
                log.info("[OnlinePayment]-Step10-sp_getMTT");
                payment = sp_getMTT(ghlResponse.getOrderNumber());

                if (returned) {
                    log.info("[OnlinePayment]-Step11-sendRedirect");
                    response.sendRedirect(onlinePortalURL + "/payment-response?orn_no=" + ghlResponse.getOrderNumber()
                            + "&pymt_status=" + payment.getOrder_status() + "&rcpt_dt="
                            + "&rcpt_no=" + "&ss_return_url=" + payment.getSs_return_url());
                }
            }

            else {
                if (ghlResponse.getTxnStatus() == 0) {
                    // 🌟 Declare variables OUTSIDE the try block first
                    String orderStatus = null;
                    MTTRCPT rcpt = null;

                    try {
                        log.debug("txnstatus is 0 success if block");
                        // update into mtt pg table
                        log.info("[OnlinePayment]-Step12-sp_updatePayment");
                        Integer rowCount = mttService.sp_updatePayment(ghlResponse, username);
                        log.info("[OnlinePayment]-Step13-sp_checkLatestOrderStatus");
                        String result2 = mttService.sp_checkLatestOrderStatus(ghlResponse.getOrderNumber());

                        // update mtt, mttpg, mttreceipt

                        // send success email

                        Integer rcptExist = 0;

                        // call sp here
                        log.info("[OnlinePayment]-Step14-sp_checkPaymentRcpt");
                        rcptExist = mttService.sp_checkPaymentRcpt(ghlResponse.getOrderNumber());

                        // MTTRCPT rcpt = new MTTRCPT();
                        OnlinePayment payment = new OnlinePayment();
                        List<OnlinePaymentItem> paymentItems = Collections.emptyList();

                        Integer mttid;
                        Long pgId;

                        log.info("[OnlinePayment]-Step15-sp_insertReceipt");

                        String guid = "";

                        if(rcptExist < 1){
                            rcpt = mttService.sp_insertReceipt(ghlResponse.getPaymentID(), username);
                        }
                        else{
                            rcpt = mttService.sp_getmttrcptinfo_v2(ghlResponse.getOrderNumber());
                        }

                        guid = rcpt.getRcptUUID();

                        log.info("[OnlinePayment]-Step16-getRmsMTT");
                        mttid = rcpt.getRmsMTT().getMttId();
                        log.info("[OnlinePayment]-Step17-getMttPG");
                        pgId = rcpt.getMttPG().getMttPgId();

                        log.debug("RcptExist<1: " + "Mttid: " + mttid + "PGID: " + pgId);

                        // get mtt details
                        log.info("[OnlinePayment]-Step18-sp_getMTT");
                        payment = sp_getMTT(mttid);

                        ssCallBackURL = payment.getSs_return_url();

                        log.debug("RcptExist<1: " + "payment: " + payment.toString());

                        // get mtt items details
                        // paymentItems = storeProcedureService.sp_getMTTItem(mttid); // vicky old code
                        // paymentItems = mttService.getListOfItems(mttid); // use brian code, vicky
                        // comment this
                        log.info("[OnlinePayment]-Step19-sp_getMTTItem");
                        paymentItems = mttService.sp_getMTTItem(mttid);

                        // get mtt pg details
                        MTTPG pG = new MTTPG();
                        log.info("[OnlinePayment]-Step20-getMttPgById");
                        pG = mttService.getMttPgById(pgId).orElse(null);

                        // generate a new receipt
                        log.info("[OnlinePayment]-Step21-receiptGenerator");
                        File pdfRcpt = receiptGenerator
                                .generateReceipt(new ReceiptRequest(pG, payment, rcpt, paymentItems, "pdf"));

                        // send email
                        String body = "Entity Name: " + payment.getCust_nm()
                                + "<br>Receipt No: " + rcpt.getRcptNo().toUpperCase()
                                + "<br>Order Reference No.: " + payment.getOrnNo().toUpperCase()
                                + "<br>Total Amount Paid: RM" + String.format("%.2f", pG.getPgPymtAmt().doubleValue())
                                + "<br><br>Dear Sir/Madam,<br>We are pleased to inform you that your "
                                + "online payment has been successfully processed. An official payment receipt "
                                + "has been generated for your records. Please find the attached receipt for "
                                + "your reference.<br>Thank you for using our services.<br><br><br>Tuan/Puan,<br>"
                                + "Dengan hormatnya, kami berbesar hati ingin memaklumkan bahawa pembayaran "
                                + "dalam talian anda telah berjaya diproses. Bersama-sama ini disertakan resit "
                                + "pembayaran untuk perhatian pihak Tuan/Puan selanjutnya.\r<br>Terima kasih kerana"
                                + " menggunakan perkhidmatan kami.<br><br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
                                + "DO NOT REPLY DIRECTLY TO THIS EMAIL]<br>";

                        // save email object into db
                        Email email = new Email("Receipt", payment.getCust_email(), "", "",
                                "PAYMENT SUCCESSFUL - RECEIPT ATTACHED", body, ghlResponse.getOrderNumber().toUpperCase(), pdfRcpt);

                        // save and send email
                        log.info("[OnlinePayment]-Step22-saveEmailDets");

                        if (sp_checkemailsent(ghlResponse.getOrderNumber()) <= 0) {
                            email = emailService.saveEmailDets(email);
                        }
                        
                        Boolean emailSent = false;

                        try {
                            log.info("[OnlinePayment]-Step23-sendMailWithAttachment");
                            if (sp_checkemailsent(ghlResponse.getOrderNumber()) <= 0) {
                                emailService.sendMailWithAttachment(email, true);
                            }
                            emailSent = true;
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                            emailSent = false;
                            email.setRetryCnt(1);
                            log.info("[OnlinePayment]-Step24-saveEmailDets");
                            if (sp_checkemailsent(ghlResponse.getOrderNumber()) <= 0) {
                                emailService.saveEmailDets(email);
                            }
                        } finally {
                            if (emailSent) {
                                // 'S' = Sent
                                email.setStatus("S");
                                // update email status into db
                                log.info("[OnlinePayment]-Step25-saveEmailDets");

                                if (sp_checkemailsent(ghlResponse.getOrderNumber()) <= 0) {
                                    emailService.saveEmailDets(email);
                                }
                            }

                            // upload to idaman start
                            // create guid

                            // temp try block

                            try {

                                // UUID uuid = UUID.randomUUID();
                                // String guid = "RMS-" + uuid.toString();

                                byte[] fileContent = Files.readAllBytes(Paths.get(pdfRcpt.toString()));
                                String encodedString = Base64.getEncoder().encodeToString(fileContent);

                                String formatedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                                // upload to idaman
                                log.info("[OnlinePayment]-Step26-uploadIdamanAPI");

                                if (rcpt.getVersionId() == null || rcpt.getVersionId().equals("")) {
                                    Integer result1 = uploadIdamanAPI(
                                            new IdamanAPIUploadReq("RMS", rcpt.getRcptNo(), "RMSReceipt", formatedDate,
                                                    "", "", "", "", "", "", guid, payment.getOrnNo(), "", "", "", "",
                                                    "",
                                                    "",
                                                    encodedString, pdfRcpt.getName()),
                                            rcpt.getMttRcptID());

                                    if (result1 < 1) {
                                        log.error("Error in uploading receipt to Idaman");
                                        // throw new Exception("Error in uploading receipt to Idaman");
                                    }
                                }


                            } catch (Exception e) {
                                log.error("Failed to read file content: " + e.getMessage(), e);
                            }

                            // end try temp block

                            // upload to idaman end
                        }

                        // call sp to update the latest order status and get latest mtt order status
                        // String orderStatus =
                        // mttService.sp_checkLatestOrderStatus(ghlResponse.getOrderNumber());
                        log.info("[OnlinePayment]-Step27-sp_checkLatestOrderStatus");
                        orderStatus = mttService.sp_checkLatestOrderStatus(ghlResponse.getOrderNumber());
                        String[] split = orderStatus.split(":");
                        orderStatus = split[0];

                        log.debug(
                                "GHL order number: " + ghlResponse.getOrderNumber() + " Order Status: " + orderStatus);
                        // if billing
                        if (ghlResponse.getOrderNumber().contains("BIL")) {
                            log.info("[OnlinePayment]-Step28-updateBill");
                            updateBill(payment.getOrnNo());

                        } else if (ghlResponse.getOrderNumber().contains("NB")) { // if non billing
                            log.info("[OnlinePayment]-Step29-updateNonBill");
                            updateNonBill(ghlResponse.getOrderNumber());

                        } else if (ghlResponse.getOrderNumber().contains("AGB")) { // if agent
                            log.warn("[OnlinePayment]-Step30-updateSP");
                            updateSP(ghlResponse.getOrderNumber());

                        } else { // if normal payment
                            log.info("[OnlinePayment]-Step31-updateAccrual");
                            log.info("Payment accrual update for order number: " + ghlResponse.getOrderNumber());
                            updateAccrual(paymentItems, rcpt);
                        }

                        // set return value
                        result.setOrn_no(ghlResponse.getOrderNumber());
                        result.setPymt_status(orderStatus); // this need get from SP
                        result.setRcpt_dt(rcpt.getRcptDt().toString()); // this get from receipt sp
                        result.setRcpt_no(rcpt.getRcptNo()); // this get from receipt sp

                        // ssCallBackURL = payment.getSs_return_url();

                        // if ss_callback_url is not null/empty
                        if (payment.getSs_callback_url() != null && payment.getSs_callback_url() != "") {
                            PaymentResponse callBack = new PaymentResponse();
                            String ssCallBackUrl1 = payment.getSs_callback_url();

                            callBack.setOrn_no(ghlResponse.getOrderNumber());
                            callBack.setPymt_status(payment.getOrder_status());
                            callBack.setRcpt_dt(rcpt.getRcptDt().toString());
                            callBack.setRcpt_no(rcpt.getRcptNo());
                            log.info("[OnlinePayment]-Step32-sendResponse");
                            sendResponse(callBack, ssCallBackUrl1);

                        }

                        if (returned) {
                            log.info("[OnlinePayment]-Step33-sendRedirect");
                            response.sendRedirect(onlinePortalURL + "/payment-response?orn_no="
                                    +
                                    ghlResponse.getOrderNumber() + "&pymt_status=" + orderStatus + "&rcpt_dt="
                                    + rcpt.getRcptDt().toString() + "&rcpt_no=" + rcpt.getRcptNo() +
                                    "&ss_return_url=" + ssCallBackURL);
                        }

                    } catch (Exception e) {
                        log.error("Backend error during success payment handling: " + e.getMessage(), e);
                        String safeOrderStatus = (orderStatus != null) ? orderStatus : "P";
                        String safeRcptDt = (rcpt != null && rcpt.getRcptDt() != null) ? rcpt.getRcptDt().toString()
                                : "";
                        String safeRcptNo = (rcpt != null) ? rcpt.getRcptNo() : "";
                        String safeSsCallBackURL = (ssCallBackURL != null) ? ssCallBackURL : "";

                        if (returned) {
                            response.sendRedirect(
                                    onlinePortalURL + "/payment-response?orn_no=" + ghlResponse.getOrderNumber()
                                            + "&pymt_status=" + safeOrderStatus
                                            + "&rcpt_dt=" + safeRcptDt
                                            + "&rcpt_no=" + safeRcptNo
                                            + "&ss_return_url=" + safeSsCallBackURL);
                        }

                    }
                } else {
                    // failed
                    // update mtt
                    // update mttpg
                    log.info("[OnlinePayment]-Step34-sp_updatePayment");
                    Integer rowCount = mttService.sp_updatePayment(ghlResponse, username);
                    // update order status
                    log.info("[OnlinePayment]-Step35-sp_checkLatestOrderStatus");
                    String result1 = mttService.sp_checkLatestOrderStatus(ghlResponse.getOrderNumber());
                    // get mtt details
                    OnlinePayment payment = new OnlinePayment();
                    log.info("[OnlinePayment]-Step36-sp_getMTT");
                    payment = sp_getMTT(ghlResponse.getOrderNumber());

                    // if ss_callback_url is not null/empty
                    if (payment.getSs_callback_url() != null && payment.getSs_callback_url() != "") {
                        PaymentResponse callBack = new PaymentResponse();
                        String ssCallBackUrl1 = payment.getSs_callback_url();

                        callBack.setOrn_no(ghlResponse.getOrderNumber());
                        callBack.setPymt_status(payment.getOrder_status());
                        callBack.setRcpt_dt("");
                        callBack.setRcpt_no("");
                        log.info("[OnlinePayment]-Step37-sendResponse");
                        sendResponse(callBack, ssCallBackUrl1);

                    }

                    if (returned) {
                        log.info("[OnlinePayment]-Step38-sendRedirect");
                        response.sendRedirect(
                                onlinePortalURL + "/payment-response?orn_no=" + ghlResponse.getOrderNumber()
                                        + "&pymt_status=" + payment.getOrder_status() + "&rcpt_dt="
                                        + "&rcpt_no=" + "&ss_return_url=" + payment.getSs_return_url());
                    }

                }

            }
        }
    }

    // send response to ss_callback_url
    private void sendResponse(PaymentResponse callBack, String ssCallBackURL) {

        // Define the external API URL
        String apiUrl = ssCallBackURL;

        // Set up headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Wrap the request body and headers in an HttpEntity
        HttpEntity<PaymentResponse> requestEntity = new HttpEntity<>(callBack, headers);

        // Use RestTemplate to send the request
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class);

            // Log the response
            log.info("Response from API: " + response.getBody());

            try {
                // Store the payment request in cache for later retrieval
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("OnlinePaymentResponse");
                extAudit.setI_request_body(new ObjectMapper().writeValueAsString(callBack));
                extAudit.setI_response_body(response.getBody());
                extAudit.setI_direction("Outgoing");
                extAudit.setI_rms_batch_no(callBack.getOrn_no());

                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for Online Payment: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }

        } catch (Exception e) {
            log.error("Error while calling external API", e);
        }
    }

    private Integer uploadIdamanAPI(IdamanAPIUploadReq req, Integer mttRcptID) throws IOException {
        List<IdamanAPIUpload> result = Collections.emptyList();
        Integer result1 = -1;

        // try {
        //Added by Geo to handle duplicate upload issue
        logAudit(req.getRefNo1());

        result = idamanAPIUploadService.idaman_api_uploadDoc(req);
        // if (result.size() > 0) {
        if (CollectionUtils.size(result) > 0) {
            // update rcpt table
            result1 = mttService.sp_updateMTTRcpt(mttRcptID, result.get(0).getVerid(), req.getSourceSysDocRefID());

            return result1;// result.get(0).getDocRefID();
        }
        // } catch (Exception e) {
        // log.error(e.getMessage(), e);
        // }
        return result1;
    }

    private void updateBill(String ornNo) throws JsonProcessingException {
        Integer result = bSvc.sp_updatebillstatuspaid(ornNo, authService.getLoginUserName());
    }

    private void updateNonBill(String ornNo) {
        NBLInsRequest nbreq = new NBLInsRequest();
        nbreq.setI_non_bil_no(ornNo);
        nbSvc.sp_updnonbillinsa(nbreq);
    }

    private void updateSP(String ornNo) {
        ServiceProviderRequest spreq = new ServiceProviderRequest();

        spreq.setI_ag_bil_no(ornNo);

        nbSvc.sp_updsp(spreq);
    }

    // ss start
    // @Override
    public SubmitBilPymtStatus sp_checksubmitbilpaymentstatus(String billing_no) {
        SubmitBilPymtStatus result = new SubmitBilPymtStatus();
        Object[] objects = opRep.sp_checksubmitbilpaymentstatus(billing_no);
        result = convertToSubmitBilPymntStatus(objects);
        return result;
    }

    private SubmitBilPymtStatus convertToSubmitBilPymntStatus(Object[] objects) {
        SubmitBilPymtStatus submitBilPymtStatus = new SubmitBilPymtStatus();
        submitBilPymtStatus.setCount((Integer) objects[0]);
        submitBilPymtStatus.setOrder_status((String) objects[1]);
        submitBilPymtStatus.setOrder_status_nm((String) objects[2]);
        submitBilPymtStatus.setRcpt_no((String) objects[3]);
        submitBilPymtStatus.setRcpt_dt((Date) objects[4]);

        return submitBilPymtStatus;
    }

    private void updateAccrual(List<OnlinePaymentItem> paymentItems, MTTRCPT rcpt) {
        for (OnlinePaymentItem item : paymentItems) {
            // call ricp function
            // log.error("CPNo:{} | entitytype:{} | entityno:{} | mttitemid:{}",
            // item.getCp_no(), item.getEntity_type(), item.getEntity_no(),
            // item.getMtt_item_id());

            // if (item.getCp_no() != "" && item.getCp_no() != null)
            // log.error("CALL UPDATE RICP COLLECTED");
            ricpSvc.updateRICPCollected(
                    new SubmitRICPCanRequest(item.getEntity_type(), item.getEntity_no(), item.getCp_no(), "CE",
                            item.getMtt_item_id()),
                    "CPP", "CA",
                    authService.getLoginUserName().equals("Anonymous") ? "system"
                            : authService.getLoginUserName());

            // log.error("RICP update result: " + result);

            // call ripl function
            if (item.getCalendar_yr() != null && item.getTxn_type() != null && item.getCalendar_yr() != 0
                    && item.getTxn_type() != "") {

                RIPLRealizedRequest ripl = new RIPLRealizedRequest();

                ripl.setTxn_type(item.getTxn_type());
                ripl.setCalendar_yr(item.getCalendar_yr().toString());
                ripl.setEntity_no(item.getEntity_no());
                ripl.setEntity_type(item.getEntity_type());
                ripl.setRcpt_no(rcpt.getRcptNo());
                ripl.setModified_by(authService.getLoginUserName().toString());

                riplSvc.sp_updRIPL(ripl);

            }
            // call rilt function
            if (item.getLit_item_ref() != null && item.getLit_item_ref() != "") {

                RILTRequest rilt = new RILTRequest();
                rilt.setLit_item_ref(item.getLit_item_ref());

                riltSvc.sp_updRILT(rilt);
            }

        }
    }

    // ss end

    @Override
    public BigDecimal sp_getrmsfee(OnlinePaymentItem request) {
        BigDecimal result = new BigDecimal(0);
        result = opRep.sp_getrmsfee(request);
        return result;
    }

    @Override
    public String sp_getmttornno(Integer mtt_id) {
        String result = opRep.sp_getmttornno(mtt_id);
        return result;
    }

    private void logAudit(String requestBody) {
        try {
            ExtAudit extAudit = new ExtAudit();
            extAudit.setI_module_nm("OnlineIdamanUpload");
            extAudit.setI_request_body(requestBody);
            extAudit.setI_response_body(null);
            extAudit.setI_rms_batch_no(null);
            extAudit.setI_direction("Outgoing");
            extAudit.setI_remark(null);
            commonSvc.sp_insextaudit(extAudit);
        } catch (Exception e) {
            // Don't fail the main operation if audit fails
            // log.warn("Failed to log audit for IdamanUpload: " + e.getMessage());
            log.error("Error in sp_insextaudit for OnlineIdamanUpload: " + e.getMessage() + ", "
                    + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
        }
    }

    // 251023- Handle duplicated email sent to customer
    @Override
    public Integer sp_checkemailsent(String orn_no) {
        Integer result = 0;
        result = opRep.sp_checkemailsent(orn_no);
        return result;
    }
}