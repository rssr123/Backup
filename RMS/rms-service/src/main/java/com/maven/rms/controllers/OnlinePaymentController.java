package com.maven.rms.controllers;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.config.RMSProperties;
import com.maven.rms.models.payload.requests.OTCPymtItemDet;
import com.maven.rms.models.payload.requests.OtcPaymentRequest;
import com.maven.rms.models.payload.requests.SubmitRICPCanRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.models.CheckAccrual;
import com.maven.rms.models.Email;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.GHLPayment;
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
import com.maven.rms.models.OTC.NBLInsRequest;
import com.maven.rms.models.SubmitBilPymtStatus;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.BillingService;
import com.maven.rms.services.CommonService;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.IdamanAPIUploadService;
import com.maven.rms.services.MTTService;
import com.maven.rms.services.OTCService;
import com.maven.rms.services.OnlinePaymentService;
import com.maven.rms.services.RICPService;
import com.maven.rms.services.RILTService;
import com.maven.rms.services.RIPLService;
import com.maven.rms.services.SSM4UAPI;
import com.maven.rms.services.UAMService;
import com.maven.rms.services.OTC.OTCReturnedChequeService;
import com.maven.rms.utils.RMSLogger;
import com.maven.rms.utils.receipts.MTTPGReceiptGenerator;

import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.CacheManager;
import com.maven.rms.utils.Common;

import org.apache.commons.collections4.CollectionUtils;

@Valid
@RestController
@RequestMapping("/api/onlinepayment/v1")
@Slf4j
public class OnlinePaymentController {
    // private static final Logger logger =
    // LoggerFactory.getLogger(OnlinePaymentController.class);

    private OnlinePaymentService onlinePaymentService;

    private final RMSProperties rmsProperties;

    // private StoreProcedureService storeProcedureService;

    // private MTTService mttService;
    @Autowired
    private EmailService emailService;

    @Autowired
    private OTCService otcSvc;

    @Autowired
    private UAMService uamSvc;

    @Autowired
    private SSM4UAPI ssm4uSvc;

    // private MTTPGReceiptGenerator receiptGenerator;

    private final String serviceID;
    private final String servicePW;
    private final String callBackURL;
    private final String returnURL;
    private final String onlinePortalURL;

    @Autowired
    private RICPService ricpSvc;

    @Autowired
    private RIPLService riplSvc;

    @Autowired
    private RILTService riltSvc;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private AuthService authService;

    @Autowired
    private BillingService bSvc;

    @Autowired
    private OTCReturnedChequeService nbSvc;

    // @Autowired
    // private StoreProcedureService spService;

    @Autowired
    private MTTService mttService;

    @Autowired
    private MTTPGReceiptGenerator receiptGenerator;

    @Autowired
    private IdamanAPIUploadService idamanAPIUploadService;

    @Autowired
    private CommonService commonSvc;

    OnlinePayment paymentInfo = new OnlinePayment();

    @Autowired
    private ObjectMapper objectMapper;

    public OnlinePaymentController(OnlinePaymentService onlinePaymentService, RMSProperties rmsProperties) {
        // StoreProcedureService storeProcedureService, MTTService mttService,
        // EmailService emailService) {
        this.onlinePaymentService = onlinePaymentService;
        this.rmsProperties = rmsProperties;
        // this.mttService = mttService;
        // this.emailService = emailService;
        // this.receiptGenerator=receiptGenerator;

        this.serviceID = rmsProperties.getGHLServiceID();
        this.servicePW = rmsProperties.getGHLPw();
        this.callBackURL = rmsProperties.getcallBackURL();
        this.onlinePortalURL = rmsProperties.getOnlinePortalURL();
        this.returnURL = rmsProperties.getReturnURL();

        RMSLogger.info("OnlinePaymentController services is started");

    }

    @GetMapping("/GetPaymentRequestDetailByID")
    public ResponseEntity<ApiResponse<PaymentRequest>> GetPaymentRequestDetailByID(@RequestParam String param1) {

        // try {
        PaymentRequest paymentRequest = new PaymentRequest();

        String orn_no = param1;
        String alphabetsOnly = orn_no.replaceAll("[^a-zA-Z]", "");
        Object cachedObject = cacheManager.get(param1);

        List<OnlinePaymentItem> itemInfo = Collections.emptyList();
        paymentInfo = mttService.getMttFromOrderNo(orn_no).orElse(null);// onlinePaymentService.sp_getMTT(orn_no);

        if (paymentInfo != null) {

            itemInfo = mttService.sp_getMTTItem(paymentInfo.getMttId());
            // itemInfo = mttService.getListOfItems(paymentInfo.getMttId());

            if (CollectionUtils.isNotEmpty(itemInfo)) {

                // Assuming paymentInfo.getOrnDt() returns a LocalDateTime
                LocalDateTime localDateTime = paymentInfo.getOrnDt();

                // Convert LocalDateTime to Date
                Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

                paymentRequest.setOrn_no(paymentInfo.getOrnNo());
                paymentRequest.setOrn_dt(date);
                paymentRequest.setCust_nm(paymentInfo.getCust_nm());
                paymentRequest.setCust_email(paymentInfo.getCust_email());
                paymentRequest.setCust_phone(paymentInfo.getCust_phone());
                paymentRequest.setSs_cd(paymentInfo.getSs_cd());
                paymentRequest.setCust_addr_1(paymentInfo.getCust_addr_1());
                paymentRequest.setCust_addr_2(paymentInfo.getCust_addr_2());
                paymentRequest.setCust_addr_3(paymentInfo.getCust_addr_3());
                paymentRequest.setCust_city(paymentInfo.getCust_city());
                paymentRequest.setCust_state(paymentInfo.getCust_state());
                paymentRequest.setCust_postcode(paymentInfo.getCust_postcode());
                paymentRequest.setTotal_amt(paymentInfo.getTotal_amt());
                paymentRequest.setSs_return_url(paymentInfo.getSs_return_url());
                paymentRequest.setSs_callback_url(paymentInfo.getSs_callback_url());
                paymentRequest.setPymt_method(paymentInfo.getRms_type());
                if (paymentInfo.getOrder_status().equals("EP")) {
                    paymentRequest.setEmail_flag(1);
                } else {
                    paymentRequest.setEmail_flag(paymentInfo.getEmail_flag());
                }
                // paymentRequest.setEmail_flag(paymentInfo.getEmail_flag());
                paymentRequest.setOrder_status(paymentInfo.getOrder_status());

                List<PaymentItemDetails> paymentItemDetails = new ArrayList<PaymentItemDetails>();

                for (OnlinePaymentItem onlinePaymentItem : itemInfo) {

                    PaymentItemDetails paymentItemDetail = new PaymentItemDetails();

                    paymentItemDetail.setFee_detail_pk(onlinePaymentItem.getFee_detail_pk());
                    paymentItemDetail.setFee_detail_id(onlinePaymentItem.getFee_detail_id());
                    paymentItemDetail.setItem_desc(onlinePaymentItem.getItem_desc());
                    paymentItemDetail.setItem_ref_no(onlinePaymentItem.getItem_ref_no());
                    paymentItemDetail.setLine_no(onlinePaymentItem.getLine_no());
                    paymentItemDetail.setQty(onlinePaymentItem.getQty());
                    paymentItemDetail.setUnit_fee(onlinePaymentItem.getUnit_fee());
                    paymentItemDetail.setGross_amt(onlinePaymentItem.getGross_amt());
                    paymentItemDetail.setGrant_cd(onlinePaymentItem.getGrant_cd());
                    paymentItemDetail.setDisc_amt(onlinePaymentItem.getDisc_amt());
                    paymentItemDetail.setTax_pct(onlinePaymentItem.getTax_pct());
                    paymentItemDetail.setTax_amt(onlinePaymentItem.getTax_amt());
                    paymentItemDetail.setNet_amt(onlinePaymentItem.getNet_amt());
                    paymentItemDetail.setEntity_type(onlinePaymentItem.getEntity_type());
                    paymentItemDetail.setEntity_no(onlinePaymentItem.getEntity_no());
                    paymentItemDetail.setEntity_nm(onlinePaymentItem.getEntity_nm());
                    paymentItemDetail.setCp_no(onlinePaymentItem.getCp_no());
                    paymentItemDetail.setCp_tier(onlinePaymentItem.getCp_tier());
                    paymentItemDetail.setCp_tier_amt(onlinePaymentItem.getCp_tier_amt());
                    paymentItemDetail.setCp_tier_disc_pct(onlinePaymentItem.getCp_tier_discpct());
                    paymentItemDetail.setDps_id(onlinePaymentItem.getDps_id());
                    paymentItemDetail.setDps_task(onlinePaymentItem.getDps_task());
                    paymentItemDetail.setPymt_case(onlinePaymentItem.getPymt_case());
                    paymentItemDetail.setLocation(onlinePaymentItem.getLocation());
                    paymentItemDetail.setLit_item_ref(onlinePaymentItem.getLit_item_ref());
                    paymentItemDetail.setTxn_type(onlinePaymentItem.getTxn_type());
                    paymentItemDetail.setCalendar_yr(onlinePaymentItem.getCalendar_yr());

                    paymentItemDetails.add(paymentItemDetail);

                }

                paymentRequest.setPayment_item_details(paymentItemDetails);

            }
        } else {
            if (cachedObject instanceof PaymentRequest) {
                paymentRequest = (PaymentRequest) cachedObject;
            }
        }

        // #region Archive
        // if (cachedObject != null) {
        // if (cachedObject instanceof PaymentRequest) {
        // paymentRequest = (PaymentRequest) cachedObject;
        // }
        // }else{

        // List<OnlinePaymentItem> itemInfo = Collections.emptyList();
        // paymentInfo =
        // mttService.getMttFromOrderNo(orn_no).orElse(null);//onlinePaymentService.sp_getMTT(orn_no);

        // if (paymentInfo!=null){

        // itemInfo=mttService.sp_getMTTItem(paymentInfo.getMttId());
        // //itemInfo = mttService.getListOfItems(paymentInfo.getMttId());

        // if(CollectionUtils.isNotEmpty(itemInfo)){

        // // Assuming paymentInfo.getOrnDt() returns a LocalDateTime
        // LocalDateTime localDateTime = paymentInfo.getOrnDt();

        // // Convert LocalDateTime to Date
        // Date date =
        // Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());

        // paymentRequest.setOrn_no(paymentInfo.getOrnNo());
        // paymentRequest.setOrn_dt(date);
        // paymentRequest.setCust_nm(paymentInfo.getCust_nm());
        // paymentRequest.setCust_email(paymentInfo.getCust_email());
        // paymentRequest.setCust_phone(paymentInfo.getCust_phone());
        // paymentRequest.setSs_cd(paymentInfo.getSs_cd());
        // paymentRequest.setCust_addr_1(paymentInfo.getCust_addr_1());
        // paymentRequest.setCust_addr_2(paymentInfo.getCust_addr_2());
        // paymentRequest.setCust_addr_3(paymentInfo.getCust_addr_3());
        // paymentRequest.setCust_city(paymentInfo.getCust_city());
        // paymentRequest.setCust_state(paymentInfo.getCust_state());
        // paymentRequest.setCust_postcode(paymentInfo.getCust_postcode());
        // paymentRequest.setTotal_amt(paymentInfo.getTotal_amt());
        // paymentRequest.setSs_return_url(paymentInfo.getSs_return_url());
        // paymentRequest.setPymt_method(paymentInfo.getRms_type());
        // paymentRequest.setEmail_flag(paymentInfo.getEmail_flag());
        // paymentRequest.setOrder_status(paymentInfo.getOrder_status());

        // List<PaymentItemDetails> paymentItemDetails = new
        // ArrayList<PaymentItemDetails>();

        // for (OnlinePaymentItem onlinePaymentItem : itemInfo) {

        // PaymentItemDetails paymentItemDetail = new PaymentItemDetails();

        // paymentItemDetail.setFee_detail_id(onlinePaymentItem.getFee_detail_id());
        // paymentItemDetail.setItem_desc(onlinePaymentItem.getItem_desc());
        // paymentItemDetail.setItem_ref_no(onlinePaymentItem.getItem_ref_no());
        // paymentItemDetail.setLine_no(onlinePaymentItem.getLine_no());
        // paymentItemDetail.setQty(onlinePaymentItem.getQty());
        // paymentItemDetail.setUnit_fee(onlinePaymentItem.getUnit_fee());
        // paymentItemDetail.setGross_amt(onlinePaymentItem.getGross_amt());
        // paymentItemDetail.setGrant_cd(onlinePaymentItem.getGrant_cd());
        // paymentItemDetail.setDisc_amt(onlinePaymentItem.getDisc_amt());
        // paymentItemDetail.setTax_pct(onlinePaymentItem.getTax_pct());
        // paymentItemDetail.setTax_amt(onlinePaymentItem.getTax_amt());
        // paymentItemDetail.setNet_amt(onlinePaymentItem.getNet_amt());
        // paymentItemDetail.setEntity_type(onlinePaymentItem.getEntity_type());
        // paymentItemDetail.setEntity_no(onlinePaymentItem.getEntity_no());
        // paymentItemDetail.setEntity_nm(onlinePaymentItem.getEntity_nm());
        // paymentItemDetail.setCp_no(onlinePaymentItem.getCp_no());
        // paymentItemDetail.setCp_tier(onlinePaymentItem.getCp_tier());
        // paymentItemDetail.setCp_tier_amt(onlinePaymentItem.getCp_tier_amt());
        // paymentItemDetail.setCp_tier_disc_pct(onlinePaymentItem.getCp_tier_discpct());
        // paymentItemDetail.setDps_id(onlinePaymentItem.getDps_id());
        // paymentItemDetail.setDps_task(onlinePaymentItem.getDps_task());
        // paymentItemDetail.setPymt_case(onlinePaymentItem.getPymt_case());
        // paymentItemDetail.setLocation(onlinePaymentItem.getLocation());
        // paymentItemDetail.setLit_item_ref(onlinePaymentItem.getLit_item_ref());
        // paymentItemDetail.setTxn_type(onlinePaymentItem.getTxn_type());
        // paymentItemDetail.setCalendar_yr(onlinePaymentItem.getCalendar_yr());

        // paymentItemDetails.add(paymentItemDetail);

        // }

        // paymentRequest.setPayment_item_details(paymentItemDetails);

        // }

        // }

        // }
        // #endregion

        return APIResponse.SuccessResponse(paymentRequest);

    }

    // @Secured("ROLE_USER")
    // @PostMapping(value = "/rms_paymentPage")
    // public void rms_paymentPage(@RequestParam("jsonData") String jsonData,
    // HttpServletResponse response,
    // HttpSession session) throws IOException {

    // ObjectMapper objectMapper = new ObjectMapper();
    // PaymentRequest paymentRequest = objectMapper.readValue(jsonData,
    // PaymentRequest.class);

    // cacheManager.put(paymentRequest.getOrn_no(), paymentRequest);
    // response.sendRedirect(
    // onlinePortalURL + "/payment-page?pr=" + paymentRequest.getOrn_no());
    // // return;

    // // If something went wrong, you might want to send an error status or message
    // to
    // // the client.
    // response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    // response.getWriter().write("An internal error occurred");
    // return;
    // }

    @PostMapping(value = "/rms_paymentPage")
    public void rms_paymentPage(@RequestParam("jsonData") String jsonData,
            HttpServletResponse response,
            HttpSession session) throws IOException {

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            PaymentRequest paymentRequest = objectMapper.readValue(jsonData, PaymentRequest.class);

            if (paymentRequest.getCust_nm() == null || paymentRequest.getCust_nm().isEmpty()){
                paymentRequest.setCust_nm("N/A");
            }

            if (paymentRequest.getCust_phone() == null || paymentRequest.getCust_phone().isEmpty()){
                paymentRequest.setCust_phone("N/A");
            }

            if (paymentRequest.getCust_email() == null || paymentRequest.getCust_email().isEmpty()){
                paymentRequest.setCust_email("N/A");
            }

            if (paymentRequest.getCust_addr_1() == null || paymentRequest.getCust_addr_1().isEmpty()){
                paymentRequest.setCust_addr_1("N/A");
            }

            if (paymentRequest.getCust_addr_2() == null || paymentRequest.getCust_addr_2().isEmpty()){
                paymentRequest.setCust_addr_2("N/A");
            }

            if (paymentRequest.getCust_city() == null || paymentRequest.getCust_city().isEmpty()){
                paymentRequest.setCust_city("N/A");
            }

            if (paymentRequest.getCust_state() == null || paymentRequest.getCust_state().isEmpty()){
                paymentRequest.setCust_state("N/A");
            }

            if (paymentRequest.getCust_postcode() == null || paymentRequest.getCust_postcode().isEmpty()){
                paymentRequest.setCust_postcode("N/A");
            }

            try {
                // Store the payment request in cache for later retrieval
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("OnlinePayment");
                extAudit.setI_request_body(jsonData);
                extAudit.setI_response_body("");
                extAudit.setI_direction("Incoming");
                extAudit.setI_rms_batch_no(paymentRequest.getOrn_no());

                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for Online Payment: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }

            String username = authService.getLoginUserName();
            String custIP = authService.getClientIP(request);

            // at here i want to call sp_getmttdetails, if return order_status = "Paid", the
            // orn_no from PPayment Request sp_getmttdetails is coming from
            // MTTListingServices

            boolean isPaid = false;
            String OrderNo = paymentRequest.getOrn_no();
            String orderStatus = mttService.sp_checkLatestOrderStatus(OrderNo);
            // the output of orderStatus if not exist = _0 if exist will be something like
            // P_something, if after split the first part is P then consider Paid
            if (orderStatus != null && !orderStatus.equals("_0")) {
                String[] statusParts = orderStatus.split("_");
                if (statusParts.length > 0 && "P".equals(statusParts[0])) {
                    // Payment already completed, redirect to success page with existing receipt
                    // info
                    isPaid = true;

                }
            }

            // Insert directly into DB
            if (!isPaid) {
                Integer mtt_id = onlinePaymentService.sp_insertPaymentMTT(paymentRequest, username, custIP);
                if (OrderNo.startsWith("CTL")){
                    OrderNo = onlinePaymentService.sp_getmttornno(mtt_id);
                }
            }

            // String orn_no = onlinePaymentService.sp_getmttornno(mttID);

            // Redirect to payment page with unique ID (e.g., orn_no or mttID)
            response.sendRedirect(onlinePortalURL + "/payment-page?pr=" + OrderNo);
            //response.sendRedirect("https://localhost:4300" + "/payment-page?pr=" + OrderNo);
        } catch (Exception ex) {
            // Handle errors properly
            log.error("Request Body: " + jsonData + ex.getMessage(), ex);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An internal error occurred: " + ex.getMessage());
        }
    }

    // @Secured("ROLE_USER")
    @PostMapping(value = "/sp_checkLatestOrderStatus")
    public ResponseEntity<ApiResponse<Integer>> sp_checkLatestOrderStatus(@RequestBody JsonNode json) {

        // List<TaxCode> result = Collections.emptyList();
        Integer result = 0;

        try {

            // read json
            JsonNode ornNoNode = json.get("orn_no");
            String orn = ornNoNode.asText();
            JsonNode totalAmtNode = json.get("total_amt");
            BigDecimal totalAmt = new BigDecimal(totalAmtNode.asText());

            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // check latest order status
            result = mttService.sp_checkLatestOrderStatus2(orn, totalAmt);

            if (result == null) {
                return APIResponse.InternalServerError();
            }

            return APIResponse.SuccessResponse(result);

        } catch (NumberFormatException e) {
            log.error("NumberFormatException in sp_checkLatestOrderStatus:" + e.getMessage(), e);
            return APIResponse.InvalidFormat();
        } catch (Exception e) {
            log.error("Exception in sp_checkLatestOrderStatus" + e.getMessage(), e);
            return APIResponse.InternalServerError();
        } finally {
            // Add any cleanup or finalization logic here
        }
    }

    // @Secured("ROLE_USER")
    @PostMapping(value = "/sp_checkAccrual")
    public ResponseEntity<ApiResponse<Integer>> sp_checkAccrual(@RequestBody JsonNode json) {

        Integer isExist = 0;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // read json

        // try {

        JsonNode paymentItemDetailsNode = json.get("payment_item_details");
        JsonNode entityTypeNode;
        JsonNode entityNoNode;
        JsonNode cpNoNode;
        JsonNode litItemRefNode;
        JsonNode txnTypeNode;
        JsonNode calendarYrNode;
        JsonNode feeDetailIdNode;

        if (paymentItemDetailsNode.isArray()) {

            for (JsonNode itemNode : paymentItemDetailsNode) {

                entityTypeNode = itemNode.get("entity_type");
                entityNoNode = itemNode.get("entity_no");
                cpNoNode = itemNode.get("cp_no");
                litItemRefNode = itemNode.get("lit_item_ref");
                txnTypeNode = itemNode.get("txn_type");
                calendarYrNode = itemNode.get("calendar_yr");
                feeDetailIdNode = itemNode.get("fee_detail_id");

                CheckAccrual checkAccrual = new CheckAccrual();
                checkAccrual.setEntityType(entityTypeNode.asText());
                checkAccrual.setEntityNo(entityNoNode.asText());
                checkAccrual.setCpNo(cpNoNode.asText());
                checkAccrual.setLitItemRef(litItemRefNode.asText());
                checkAccrual.setTxnType(txnTypeNode.asText());
                checkAccrual.setCalanderYr(calendarYrNode.asText());
                checkAccrual.setFeeDetailsId(feeDetailIdNode.asText());

                isExist = onlinePaymentService.sp_checkAccrual(checkAccrual);
            }
        }
        return APIResponse.SuccessResponse(isExist);

        // } catch (NumberFormatException e) {
        // return APIResponse.InvalidFormat();
        // } catch (Exception e) {
        // return APIResponse.InternalServerError();
        // } finally {
        // // Add any cleanup or finalization logic here
        // }
    }

    // @Secured("ROLE_USER")
    @PostMapping(value = "/sp_getMTT")
    public ResponseEntity<ApiResponse<OnlinePayment>> sp_getMTT(@RequestBody JsonNode json) {
        // public ResponseEntity<ApiResponse<String>> sp_installPayment(@RequestBody
        // MultiValueMap<String, String> params) {

        // List<TaxCode> result = Collections.emptyList();
        OnlinePayment result = new OnlinePayment();

        // try {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // hardcode for username
        String username = authService.getLoginUserName();
        JsonNode ornNoNode = json.get("orn_no");
        String orn = ornNoNode.asText();

        result = onlinePaymentService.sp_getMTT(orn);

        if (result == null) {
            return APIResponse.InternalServerError();
        }

        return APIResponse.SuccessResponse(result);

    }

    // @Secured("ROLE_USER")
    @PostMapping(value = "/sp_getMTTItem")
    public ResponseEntity<ApiResponse<List<OnlinePaymentItem>>> sp_getMTTItem(@RequestBody JsonNode json) {
        // public ResponseEntity<ApiResponse<String>> sp_installPayment(@RequestBody
        // MultiValueMap<String, String> params) {

        List<OnlinePaymentItem> result = Collections.emptyList();

        // try {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // hardcode for username
        String username = authService.getLoginUserName();
        JsonNode ornNoNode = json.get("mtt_id");
        Integer mttId = Integer.parseInt(ornNoNode.asText());

        // result = mttService.sp_getMTTItem(mttId);
        result = mttService.getListOfItems(mttId);
        // onlinePaymentService.sp_getMTTItem(mttId);

        if (result == null) {
            return APIResponse.InternalServerError();
        }
        return APIResponse.SuccessResponse(result);

    }

    // @Secured("ROLE_USER")
    @PostMapping(value = "/sp_insertPayment")
    public ResponseEntity<ApiResponse<GHLPayment>> sp_insertPayment(HttpServletResponse response,
            @Valid @RequestBody PaymentRequest paymentRequest) throws Exception {

        // List<OnlinePaymentItem> result = Collections.emptyList();
        GHLPayment result = new GHLPayment();
        BigInteger updateResult = BigInteger.ZERO;

        // try {
        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String username = authService.getLoginUserName();
        String custIP = authService.getClientIP(request);

        String orn_no = paymentRequest.getOrn_no();
        String alphabetsOnly = orn_no.replaceAll("[^a-zA-Z]", "");

        // OnlinePayment paymentInfo = new OnlinePayment();

        BigDecimal amt = paymentRequest.getTotal_amt();

        // checking here
        // if is billing/non-billing
        if (alphabetsOnly.equals("BIL") || alphabetsOnly.equals("NB") || alphabetsOnly.equals("AGB")) {
            // update mtt table status to PIP
            updateResult = mttService.sp_updateMTTStatus(orn_no, username, username);

            // insert into mtt_pg table
            result = mttService.sp_insertPayment(updateResult.intValue(), "ANY", serviceID, amt, "EN", username,
                    username);

            if (result != null) {
                // later hash
                String beforeHash = servicePW + serviceID + result.getPymt_id() + returnURL + callBackURL
                // +callBackURL+callBackURL
                        + result.getAmt().toString() + result.getCurr_cd() + result.getCust_ip()
                        + result.getPage_timeout() * 60;
                String hashedvalue = "";
                hashedvalue = Common.hashStringWithSHA256(beforeHash);

                // set value
                result.setHash_value(hashedvalue);
                // result.setReturn_url(callBackURL);
                result.setReturn_url(returnURL);
                result.setCallback_url(callBackURL);

                log.debug("Service PW:" + servicePW + "Service ID: " + serviceID + "Pymt ID: " + result.getPymt_id() +
                        "Return URL: " + returnURL + "CallbackURL: " + callBackURL + "Amount: "
                        + result.getAmt().toString() + "Currency Code: " + result.getCurr_cd() +
                        "Cust IP: " + result.getCust_ip() + "Page Timeout: " + result.getPage_timeout() * 60);
                log.debug("Before Value: " + beforeHash);
                log.debug("Hashed Value: " + hashedvalue);

                // get mtt details
                // OnlinePayment payment = new OnlinePayment();
                // payment = onlinePaymentService.sp_getMTT(result.getOrd_no());
                // result.setSs_return_url(payment.getSs_return_url());
            }

        } else {
            // if is payment or catalogue
            // update/insert mtt and delete mtt item and insert mtt item again
            Integer mttID = onlinePaymentService.sp_insertPaymentMTT(paymentRequest, username, custIP);

            if (mttID != 0) {

                // insert mtt pg // sp to handle 0 amt
                result = mttService.sp_insertPayment(mttID, "ANY", serviceID, amt, "EN", username, username);

                // if total = 0
                if (paymentRequest.getTotal_amt().compareTo(BigDecimal.ZERO) == 0) {
                    // generate receipt
                    MTTRCPT rcpt = new MTTRCPT();
                    OnlinePayment payment = new OnlinePayment();
                    List<OnlinePaymentItem> paymentItems = Collections.emptyList();

                    Integer mttid;
                    Long pgId;

                    Integer rcptExist = mttService.sp_checkPaymentRcpt(paymentRequest.getOrn_no());

                    if (rcptExist < 1) {
                        rcpt = mttService.sp_insertReceipt(result.getPymt_id(), username);
                    } else {
                        rcpt = mttService.sp_getmttrcptinfo_v2(paymentRequest.getOrn_no());
                    }

                    mttid = rcpt.getRmsMTT().getMttId();
                    pgId = rcpt.getMttPG().getMttPgId();

                    log.debug("RcptExist<1: " + "Mttid: " + mttid + "PGID: " + pgId);

                    // get mtt details
                    payment = onlinePaymentService.sp_getMTT(mttid);

                    log.debug("RcptExist<1: " + "payment: " + payment.toString());

                    // get mtt items details
                    paymentItems = mttService.sp_getMTTItem(mttid);

                    // get mtt pg details
                    MTTPG pG = new MTTPG();
                    pG = mttService.getMttPgById(pgId).orElse(null);

                    // generate a new receipt
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
                            + "Dengan hormatnya, Kami berbesar hati ingin memaklumkan bahawa pembayaran "
                            + "dalam talian anda telah berjaya diproses. Bersama-sama ini disertakan resit "
                            + "pembayaran untuk perhatian pihak Tuan/Puan selanjutnya.\r<br>Terima kasih kerana"
                            + " menggunakan perkhidmatan kami.<br><br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
                            + "DO NOT REPLY DIRECTLY TO THIS EMAIL]<br>";

                    // save email object into db
                    Email email = new Email("Receipt", payment.getCust_email(), "", "",
                            "PAYMENT SUCCESSFUL - RECEIPT ATTACHED", body, payment.getOrnNo().toUpperCase(), pdfRcpt);

                    // save and send email
                    email = emailService.saveEmailDets(email);
                    Boolean emailSent = false;

                    try {
                        emailService.sendMailWithAttachment(email, true);
                        emailSent = true;
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
                        }

                        // upload to idaman start
                        // create guid
                        UUID uuid = UUID.randomUUID();
                        String guid = "RMS-" + uuid.toString();

                        byte[] fileContent = Files.readAllBytes(Paths.get(pdfRcpt.toString()));
                        String encodedString = Base64.getEncoder().encodeToString(fileContent);

                        String formatedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                        // upload to idaman
                        Integer result1 = uploadIdamanAPI(
                                new IdamanAPIUploadReq("RMS", rcpt.getRcptNo(), "RMSReceipt", formatedDate,
                                        "", "", "", "", "", "", guid, payment.getOrnNo(), "", "", "", "", "", "",
                                        encodedString, pdfRcpt.getName()),
                                rcpt.getMttRcptID());

                        if (result1 < 1) {
                            log.error("Error in uploading receipt to Idaman");
                            // throw new Exception("Error in uploading receipt to Idaman");
                        }

                        // upload to idaman end
                    }

                    // call sp to update the latest order status and get latest mtt order status
                    String orderStatus = mttService.sp_checkLatestOrderStatus(result.getOrd_no());
                    String[] split = orderStatus.split(":");
                    orderStatus = split[0];

                    // if billing
                    if (payment.getOrnNo().contains("BIL")) {

                        updateBill(payment.getOrnNo());

                    } else if (payment.getOrnNo().contains("NB")) { // if non billing

                        updateNonBill(payment.getOrnNo());

                    } else if (payment.getOrnNo().contains("AGB")) { // if non billing
                        log.warn("updateSP");
                        updateSP(payment.getOrnNo());

                    } else { // if normal payment
                        updateAccrual(paymentItems, rcpt);
                    }

                    result.setOrder_status(orderStatus);
                    result.setRcpt_no(rcpt.getRcptNo());
                    result.setRcpt_dt(rcpt.getRcptDt().toString());
                    result.setSs_return_url(payment.getSs_return_url());

                    PaymentResponse result1 = new PaymentResponse();

                    // // set return value
                    // result1.setOrn_no(result.getOrd_no());
                    // result1.setPymt_status(orderStatus); // this need get from SP
                    // result1.setRcpt_dt(rcpt.getRcptDt().toString()); // this get from receipt sp
                    // result1.setRcpt_no(rcpt.getRcptNo()); // this get from receipt sp

                    // String ssCallBackURL = payment.getSs_return_url();

                    // // redirectPage(result,orderStatus,rcpt,ssCallBackURL,response);
                    // response.sendRedirect(onlinePortalURL + "/payment-response?orn_no="
                    // +
                    // result.getOrd_no() + "&pymt_status=" + orderStatus + "&rcpt_dt="
                    // + rcpt.getRcptDt().toString() + "&rcpt_no=" + rcpt.getRcptNo() +
                    // "&ss_return_url=" + ssCallBackURL);

                }
                // if total > 0
                else {

                    if (result != null) {
                        // later hash
                        String beforeHash = servicePW + serviceID + result.getPymt_id() + returnURL + callBackURL
                        // +callBackURL+callBackURL
                                + result.getAmt().toString() + result.getCurr_cd() + result.getCust_ip()
                                + result.getPage_timeout() * 60;
                        String hashedvalue = "";
                        hashedvalue = Common.hashStringWithSHA256(beforeHash);

                        // set value
                        result.setHash_value(hashedvalue);
                        // result.setReturn_url(callBackURL);
                        result.setReturn_url(returnURL);
                        result.setCallback_url(callBackURL);

                        log.debug("Service PW:" + servicePW + "Service ID: " + serviceID + "Pymt ID: "
                                + result.getPymt_id() +
                                "Return URL: " + returnURL + "CallbackURL: " + callBackURL + "Amount: "
                                + result.getAmt().toString() + "Currency Code: " + result.getCurr_cd() +
                                "Cust IP: " + result.getCust_ip() + "Page Timeout: " + result.getPage_timeout() * 60);
                        log.debug("Before Value: " + beforeHash);
                        log.debug("Hashed Value: " + hashedvalue);

                    }
                }
            }

        }

        return APIResponse.SuccessResponse(result);
    }

    // for payment email_flag = 1 only
    @PostMapping(value = "/sp_insertPaymentEmail")
    public ResponseEntity<ApiResponse<GHLPayment>> sp_insertPaymentEmail(
            @Valid @RequestBody PaymentRequest paymentRequest) throws Exception {

        // insert mtt and mtt item
        // return insert result
        // angular page will redirect to ss page.

        String username = authService.getLoginUserName();
        String custIP = authService.getClientIP(request);
        GHLPayment result = new GHLPayment();

        // update/insert mtt and delete mtt item and insert mtt item again
        Integer mttID = onlinePaymentService.sp_insertPaymentMTT(paymentRequest, username, custIP);

        // if total = 0
        if (paymentRequest.getTotal_amt().compareTo(BigDecimal.ZERO) == 0) {
            // insert mtt pg // sp to handle 0 amt
            result = mttService.sp_insertPayment(mttID, "ANY", serviceID, paymentRequest.getTotal_amt(), "EN", username,
                    username);

            // generate receipt
            MTTRCPT rcpt = new MTTRCPT();
            OnlinePayment payment = new OnlinePayment();
            List<OnlinePaymentItem> paymentItems = Collections.emptyList();

            Integer mttid;
            Long pgId;

            Integer rcptExist = mttService.sp_checkPaymentRcpt(paymentRequest.getOrn_no());
            if (rcptExist < 1) {
                rcpt = mttService.sp_insertReceipt(result.getPymt_id(), username);
            } else {
                rcpt = mttService.sp_getmttrcptinfo_v2(paymentRequest.getOrn_no());
            }

            mttid = rcpt.getRmsMTT().getMttId();
            pgId = rcpt.getMttPG().getMttPgId();

            log.debug("RcptExist<1: " + "Mttid: " + mttid + "PGID: " + pgId);

            // get mtt details
            payment = onlinePaymentService.sp_getMTT(mttid);

            log.debug("RcptExist<1: " + "payment: " + payment.toString());

            // get mtt items details
            paymentItems = mttService.sp_getMTTItem(mttid);

            // get mtt pg details
            MTTPG pG = new MTTPG();
            pG = mttService.getMttPgById(pgId).orElse(null);

            // generate a new receipt
            File pdfRcpt = receiptGenerator.generateReceipt(new ReceiptRequest(pG, payment, rcpt, paymentItems, "pdf"));

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
                    "PAYMENT SUCCESSFUL - RECEIPT ATTACHED", body,  payment.getOrnNo().toUpperCase(), pdfRcpt);

            // save and send email
            email = emailService.saveEmailDets(email);
            Boolean emailSent = false;

            try {
                emailService.sendMailWithAttachment(email, true);
                emailSent = true;
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
                }

                // upload to idaman start
                // create guid
                UUID uuid = UUID.randomUUID();
                String guid = "RMS-" + uuid.toString();

                byte[] fileContent = Files.readAllBytes(Paths.get(pdfRcpt.toString()));
                String encodedString = Base64.getEncoder().encodeToString(fileContent);

                String formatedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
                // upload to idaman
                Integer result1 = uploadIdamanAPI(
                        new IdamanAPIUploadReq("RMS", rcpt.getRcptNo(), "RMSReceipt", formatedDate,
                                "", "", "", "", "", "", guid, payment.getOrnNo(), "", "", "", "", "", "",
                                encodedString, pdfRcpt.getName()),
                        rcpt.getMttRcptID());

                if (result1 < 1) {
                    log.error("Error in uploading receipt to Idaman");
                    // throw new Exception("Error in uploading receipt to Idaman");
                }

                // upload to idaman end
            }

            // call sp to update the latest order status and get latest mtt order status
            String orderStatus = mttService.sp_checkLatestOrderStatus(result.getOrd_no());
            String[] split = orderStatus.split(":");
            orderStatus = split[0];

            // if billing
            if (payment.getOrnNo().contains("BIL")) {

                updateBill(payment.getOrnNo());

            } else if (payment.getOrnNo().contains("NB")) { // if non billing

                updateNonBill(payment.getOrnNo());

            } else if (payment.getOrnNo().contains("AGB")) { // if non billing
                log.warn("updateSP2");
                updateSP(payment.getOrnNo());

            } else { // if normal payment
                updateAccrual(paymentItems, rcpt);
            }

            result.setOrder_status(orderStatus);
            result.setRcpt_no(rcpt.getRcptNo());
            result.setRcpt_dt(rcpt.getRcptDt().toString());
            result.setSs_return_url(payment.getSs_return_url());
        }

        return APIResponse.SuccessResponse(result);
    }

    // #region Process payment logic move to service
    // // online payment changes based on roy flow
    // public void processPayment(@RequestBody MultiValueMap<String, String> data,
    // HttpServletResponse response,
    // Boolean returned) throws Exception {
    // // #region GHL Payment Response
    // GHLPaymentResponse ghlResponse = new GHLPaymentResponse();
    // ghlResponse.setTransactionType(data.getFirst("TransactionType"));
    // ghlResponse.setPaymentMethod(data.getFirst("PymtMethod"));
    // ghlResponse.setServiceID(data.getFirst("ServiceID"));
    // ghlResponse.setPaymentID(data.getFirst("PaymentID"));
    // ghlResponse.setOrderNumber(data.getFirst("OrderNumber"));
    // // ghlResponse.setAmount(Double.parseDouble(data.getFirst("Amount")));
    // ghlResponse.setAmount(new BigDecimal(data.getFirst("Amount")));
    // ghlResponse.setCurrencyCode(data.getFirst("CurrencyCode"));
    // ghlResponse.setHashValue(data.getFirst("HashValue"));
    // ghlResponse.setHashValue2(data.getFirst("HashValue2"));
    // ghlResponse.setTxnID(data.getFirst("TxnID"));
    // ghlResponse.setIssuingBank(data.getFirst("IssuingBank"));
    // ghlResponse.setTxnStatus(Integer.parseInt(data.getFirst("TxnStatus")));
    // ghlResponse.setTxnMsg(data.getFirst("TxnMessage"));
    // ghlResponse.setAuthCode(data.getFirst("AuthCode"));
    // ghlResponse.setBankRefNo(data.getFirst("BankRefNo"));
    // ghlResponse.setTokenType(data.getFirst("TokenType"));
    // ghlResponse.setToken(data.getFirst("Token"));
    // ghlResponse.setRespTime(data.getFirst("RespTime"));
    // ghlResponse.setCardNoMask(data.getFirst("CardNoMask"));
    // ghlResponse.setCardHolder(data.getFirst("CardHolder"));
    // ghlResponse.setCardType(data.getFirst("CardType"));
    // ghlResponse.setCardExp(data.getFirst("CardExp"));
    // ghlResponse.setParam7(data.getFirst("Param7"));
    // // #endregion

    // log.info("TXNID:" + ghlResponse.getTxnID() + "EGHL" + "Order Number: " +
    // ghlResponse.getOrderNumber()
    // + "Payment Id:" + ghlResponse.getPaymentID() + "TXN STATUS:" +
    // ghlResponse.getTxnStatus());

    // String username = authService.getLoginUserName();

    // PaymentResponse result = new PaymentResponse();

    // String ssCallBackURL;

    // GHLRequest ghlRequest = new GHLRequest();
    // ghlRequest.setI_pymt_id(ghlResponse.getPaymentID());
    // Integer updGhlResp = onlinePaymentService.sp_updghlresp(ghlRequest);

    // // Integer rowCount2 = mttService.sp_updatePayment(ghlResponse, username);

    // // if (rowCount == 0) {
    // // // return APIResponse.InternalServerError();
    // // }

    // // update order status
    // // String result2 =
    // // mttService.sp_checkLatestOrderStatus(ghlResponse.getOrderNumber());

    // Integer ornExist = mttService.sp_checkornno(ghlResponse.getOrderNumber());

    // // if the order number already exist in mtt_pg table, send duplicate email to
    // // the customer
    // log.info("OrnExist:" + ornExist);
    // if (ornExist > 0) {
    // // send duplicate email
    // // Split the string based on a space delimiter
    // String[] details = ghlResponse.getParam7().split(" ");

    // // Extract the individual attributes
    // String cust_nm = details.length > 0 ? details[0] : "";
    // String cust_email = details.length > 1 ? details[1] : "";

    // String body = "Entity Name: " + cust_nm
    // + "<br>Order Reference No.: " + ghlResponse.getOrderNumber().toUpperCase()
    // + "<br>Total Amount Paid: RM" + String.format("%.2f",
    // ghlResponse.getAmount())
    // + "<br><br>Dear Sir/Madam,<br>We would like to bring your attention that it
    // appears a duplicated payment has been processed in error for your account. "
    // + "We kindly request you to review this matter at your earliest
    // convenience.<br>We sincerely apologize for the incovenience
    // caused.<br><br><br>Tuan/Puan,<br>"
    // + "Kami ingin memaklumkan bahawa terdapat satu bayaran ulang telah diproses
    // secara tidak sengaja kepada akaun anda. "
    // + "Kami rendah hati ingin meminta anda untuk meyemak perkara ini secepat
    // mungkin."
    // + "<br>Segala kesulitan amat dikesali."
    // + "<br><br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
    // + "DO NOT REPLY DIRECTLY TO THIS EMAIL] <br>";

    // emailService.saveEmailDets(
    // (new Email("Receipt", cust_email, "", "", "DUPLICATED PAYMENT", body)));

    // OnlinePayment payment = new OnlinePayment();
    // payment =
    // mttService.getMttFromOrderNo(ghlResponse.getOrderNumber()).orElse(null);
    // // payment = onlinePaymentService.sp_getMTT(ghlResponse.getOrderNumber());
    // vicky
    // // comment this, not sure why not working
    // if (returned) {
    // response.sendRedirect(onlinePortalURL + "/payment-response?orn_no=" +
    // ghlResponse.getOrderNumber()
    // + "&pymt_status=" + payment.getOrder_status() + "&rcpt_dt="
    // + "&rcpt_no=" + "&ss_return_url=" + payment.getSs_return_url());
    // }
    // }

    // Integer txnExist = mttService.sp_checktxn(ghlResponse.getOrderNumber(),
    // ghlResponse.getPaymentID());
    // log.info("TxnExist:" + txnExist);
    // if (txnExist > 0) {

    // String txnID = mttService.sp_checktxnid(ghlResponse.getOrderNumber(),
    // ghlResponse.getPaymentID());
    // log.info("order number: " + ghlResponse.getOrderNumber() + " Payment ID: " +
    // ghlResponse.getPaymentID());
    // log.info("TXN ID: " + txnID);

    // if (txnID != null && !txnID.equals(ghlResponse.getTxnID())) {
    // // update mttpg
    // // Split the string based on a space delimiter
    // String[] details = ghlResponse.getParam7().split(" ");

    // // Extract the individual attributes
    // String cust_nm = details.length > 0 ? details[0] : "";
    // String cust_email = details.length > 1 ? details[1] : "";

    // String body = "Entity Name: " + cust_nm
    // + "<br>Order Reference No.: " + ghlResponse.getOrderNumber().toUpperCase()
    // + "<br>Total Amount Paid: RM" + String.format("%.2f",
    // ghlResponse.getAmount())
    // + "<br><br>Dear Sir/Madam,<br>We would like to bring your attention that it
    // appears a duplicated payment has been processed in error for your account. "
    // + "We kindly request you to review this matter at your earliest
    // convenience.<br>We sincerely apologize for the incovenience
    // caused.<br><br><br>Tuan/Puan,<br>"
    // + "Kami ingin memaklumkan bahawa terdapat satu bayaran ulang telah diproses
    // secara tidak sengaja kepada akaun anda. "
    // + "Kami rendah hati ingin meminta anda untuk meyemak perkara ini secepat
    // mungkin."
    // + "<br>Segala kesulitan amat dikesali."
    // + "<br><br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
    // + "DO NOT REPLY DIRECTLY TO THIS EMAIL] <br>";

    // emailService.saveEmailDets(
    // (new Email("Receipt", cust_email, "", "", "DUPLICATED PAYMENT", body)));

    // OnlinePayment payment = new OnlinePayment();
    // payment = onlinePaymentService.sp_getMTT(ghlResponse.getOrderNumber());

    // if (returned) {
    // response.sendRedirect(onlinePortalURL + "/payment-response?orn_no=" +
    // ghlResponse.getOrderNumber()
    // + "&pymt_status=" + payment.getOrder_status() + "&rcpt_dt="
    // + "&rcpt_no=" + "&ss_return_url=" + payment.getSs_return_url());
    // }
    // }

    // else {
    // if (ghlResponse.getTxnStatus() == 0) {
    // log.info("txnstatus is 0 success if block");
    // // update into mtt pg table
    // Integer rowCount = mttService.sp_updatePayment(ghlResponse, username);
    // String result2 =
    // mttService.sp_checkLatestOrderStatus(ghlResponse.getOrderNumber());

    // // update mtt, mttpg, mttreceipt

    // // send success email

    // Integer rcptExist = 0;

    // // call sp here
    // rcptExist = mttService.sp_checkPaymentRcpt(ghlResponse.getOrderNumber());

    // MTTRCPT rcpt = new MTTRCPT();
    // OnlinePayment payment = new OnlinePayment();
    // List<OnlinePaymentItem> paymentItems = Collections.emptyList();

    // Integer mttid;
    // Long pgId;

    // rcpt = mttService.sp_insertReceipt(ghlResponse.getPaymentID(), username);

    // mttid = rcpt.getRmsMTT().getMttId();
    // pgId = rcpt.getMttPG().getMttPgId();

    // log.info("RcptExist<1: " + "Mttid: " + mttid + "PGID: " + pgId);

    // // get mtt details
    // payment = onlinePaymentService.sp_getMTT(mttid);

    // log.info("RcptExist<1: " + "payment: " + payment.toString());

    // // get mtt items details
    // // paymentItems = storeProcedureService.sp_getMTTItem(mttid); // vicky old
    // code
    // // paymentItems = mttService.getListOfItems(mttid); // use brian code, vicky
    // // comment this
    // paymentItems = mttService.sp_getMTTItem(mttid);

    // // get mtt pg details
    // MTTPG pG = new MTTPG();
    // pG = mttService.getMttPgById(pgId).orElse(null);

    // // generate a new receipt
    // File pdfRcpt = receiptGenerator
    // .generateReceipt(new ReceiptRequest(pG, payment, rcpt, paymentItems, "pdf"));

    // // send email
    // String body = "Entity Name: " + payment.getCust_nm()
    // + "<br>Receipt No: " + rcpt.getRcptNo().toUpperCase()
    // + "<br>Order Reference No.: " + payment.getOrnNo().toUpperCase()
    // + "<br>Total Amount Paid: RM" + String.format("%.2f",
    // pG.getPgPymtAmt().doubleValue())
    // + "<br><br>Dear Sir/Madam,<br>We are pleased to inform you that your "
    // + "online payment has been successfully processed. An official payment
    // receipt "
    // + "has been generated for your records. Please find the attached receipt for
    // "
    // + "your reference.<br>Thank you for using our
    // services.<br><br><br>Tuan/Puan,<br>"
    // + "Dengan hormatnya, kami berbesar hati ingin memaklumkan bahawa pembayaran "
    // + "dalam talian anda telah berjaya diproses. Bersama-sama ini disertakan
    // resit "
    // + "pembayaran untuk perhatian pihak Tuan/Puan selanjutnya.\r<br>Terima kasih
    // kerana"
    // + " menggunakan perkhidmatan kami.<br><br><br><br>[THIS IS AN AUTOMATED
    // MESSAGE - PLEASE "
    // + "DO NOT REPLY DIRECTLY TO THIS EMAIL]<br>";

    // // save email object into db
    // Email email = new Email("Receipt", payment.getCust_email(), "", "",
    // "PAYMENT SUCCESSFUL - RECEIPT ATTACHED", body);
    // EmailWithAttachment emailWithAttachment = new EmailWithAttachment(email,
    // pdfRcpt);

    // // save and send email
    // emailWithAttachment = emailService.saveEmailWithAttDets(emailWithAttachment);
    // Boolean emailSent = false;

    // try {
    // emailService.sendMailWithAttachment(emailWithAttachment, true);
    // emailSent = true;
    // } catch (Exception e) {
    // log.error(e.getMessage(), e);
    // emailSent = false;
    // email.setRetryCnt(1);
    // emailService.saveEmailDets(email);
    // } finally {
    // if (emailSent) {
    // // 'S' = Sent
    // email.setStatus("S");
    // // update email status into db
    // emailService.saveEmailDets(email);
    // }

    // // upload to idaman start
    // // create guid
    // UUID uuid = UUID.randomUUID();
    // String guid = "RMS-" + uuid.toString();

    // byte[] fileContent = Files.readAllBytes(Paths.get(pdfRcpt.toString()));
    // String encodedString = Base64.getEncoder().encodeToString(fileContent);

    // String formatedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
    // // upload to idaman
    // Integer result1 = uploadIdamanAPI(
    // new IdamanAPIUploadReq("RMS", rcpt.getRcptNo(), "RMSReceipt", formatedDate,
    // "", "", "", "", "", "", guid, payment.getOrnNo(), "", "", "", "", "", "",
    // encodedString, pdfRcpt.getName()),
    // rcpt.getMttRcptID());

    // if (result1 < 1) {
    // log.error("Error in uploading receipt to Idaman");
    // //throw new Exception("Error in uploading receipt to Idaman");
    // }

    // // upload to idaman end
    // }

    // // call sp to update the latest order status and get latest mtt order status
    // String orderStatus =
    // mttService.sp_checkLatestOrderStatus(ghlResponse.getOrderNumber());
    // String[] split = orderStatus.split(":");
    // orderStatus = split[0];

    // // if billing
    // if(ghlResponse.getOrderNumber().contains("BIL")){

    // updateBill(payment.getOrnNo());

    // }else if(ghlResponse.getOrderNumber().contains("NB")){ // if non billing

    // updateNonBill(ghlResponse.getOrderNumber());

    // }else{ // if normal payment
    // updateAccrual(paymentItems, rcpt);
    // }

    // // set return value
    // result.setOrn_no(ghlResponse.getOrderNumber());
    // result.setPymt_status(orderStatus); // this need get from SP
    // result.setRcpt_dt(rcpt.getRcptDt().toString()); // this get from receipt sp
    // result.setRcpt_no(rcpt.getRcptNo()); // this get from receipt sp

    // ssCallBackURL = payment.getSs_return_url();

    // if (returned) {
    // response.sendRedirect(onlinePortalURL + "/payment-response?orn_no="
    // +
    // ghlResponse.getOrderNumber() + "&pymt_status=" + orderStatus + "&rcpt_dt="
    // + rcpt.getRcptDt().toString() + "&rcpt_no=" + rcpt.getRcptNo() +
    // "&ss_return_url=" + ssCallBackURL);
    // }
    // } else {
    // // failed
    // // update mtt
    // // update mttpg
    // Integer rowCount = mttService.sp_updatePayment(ghlResponse, username);
    // // update order status
    // String result1 =
    // mttService.sp_checkLatestOrderStatus(ghlResponse.getOrderNumber());
    // // get mtt details
    // OnlinePayment payment = new OnlinePayment();
    // payment = onlinePaymentService.sp_getMTT(ghlResponse.getOrderNumber());
    // if (returned) {
    // response.sendRedirect(
    // onlinePortalURL + "/payment-response?orn_no=" + ghlResponse.getOrderNumber()
    // + "&pymt_status=" + payment.getOrder_status() + "&rcpt_dt="
    // + "&rcpt_no=" + "&ss_return_url=" + payment.getSs_return_url());
    // }

    // }
    // }
    // }
    // }
    // #endregion

    private void processPayment(@RequestBody MultiValueMap<String, String> data, HttpServletResponse response,
            Boolean returned) throws Exception {
        // #region GHL Payment Response
        GHLPaymentResponse ghlResponse = new GHLPaymentResponse();
        ghlResponse.setTransactionType(data.getFirst("TransactionType"));
        ghlResponse.setPaymentMethod(data.getFirst("PymtMethod"));
        ghlResponse.setServiceID(data.getFirst("ServiceID"));
        ghlResponse.setPaymentID(data.getFirst("PaymentID"));
        ghlResponse.setOrderNumber(data.getFirst("OrderNumber"));
        // ghlResponse.setAmount(Double.parseDouble(data.getFirst("Amount")));
        // 250325: Change Data Type to BigDecimal with Vicky Approval
        ghlResponse.setAmount(new BigDecimal(data.getFirst("Amount")));
        ghlResponse.setCurrencyCode(data.getFirst("CurrencyCode"));
        ghlResponse.setHashValue(data.getFirst("HashValue"));
        ghlResponse.setHashValue2(data.getFirst("HashValue2"));
        ghlResponse.setTxnID(data.getFirst("TxnID"));
        ghlResponse.setIssuingBank(data.getFirst("IssuingBank"));
        ghlResponse.setTxnStatus(Integer.parseInt(data.getFirst("TxnStatus")));
        ghlResponse.setTxnMsg(data.getFirst("TxnMessage"));
        ghlResponse.setAuthCode(data.getFirst("AuthCode"));
        ghlResponse.setBankRefNo(data.getFirst("BankRefNo"));
        ghlResponse.setTokenType(data.getFirst("TokenType"));
        ghlResponse.setToken(data.getFirst("Token"));
        ghlResponse.setRespTime(data.getFirst("RespTime"));

        // if(data.getFirst("RespTime2") != null || !data.getFirst("RespTime2").isEmpty()) {
        //     ghlResponse.setRespTime(data.getFirst("RespTime2"));
        // } else {
        //     ghlResponse.setRespTime(data.getFirst("RespTime"));
        // }
        ghlResponse.setCardNoMask(data.getFirst("CardNoMask"));
        ghlResponse.setCardHolder(data.getFirst("CardHolder"));
        ghlResponse.setCardType(data.getFirst("CardType"));
        ghlResponse.setCardExp(data.getFirst("CardExp"));
        ghlResponse.setParam7(data.getFirst("Param7"));

        onlinePaymentService.processPayment(ghlResponse, response, returned);
    }

    @PostMapping(value = "/return")
    // public ResponseEntity<?>
    public void postReturn(@RequestBody MultiValueMap<String, String> data, HttpServletResponse response)
            throws Exception {
        processPayment(data, response, true);
    }

    @GetMapping(value = "/return")
    public void postGetReturn(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Extract query parameters from the GET request
        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();

        // Get all parameter names from the request
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            for (String value : values) {
                data.add(key, value);
            }
        }

        // Log the incoming GET request for debugging
        log.debug("Received GET request for Online Payment with parameters: " + data.toString());

        // Call the existing processPayment method with the extracted parameters
        processPayment(data, response, true);

        log.debug("GET Return processed successfully for Online Payment return URL GET Request.");
    }

    @PostMapping(value = "/backendonly")
    public String postBackend(@RequestParam(required = false) MultiValueMap<String, String> paramData,
            @RequestBody(required = false) MultiValueMap<String, String> bodyData, HttpServletResponse response)
            throws Exception {

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();

        // Map input data to GHLPaymentResponse
        if (paramData != null && !paramData.isEmpty()) {
            data = paramData;
        } else if (bodyData != null && !bodyData.isEmpty()) {
            data = bodyData;
        } else {
            log.error("No data received");
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return "Error";
        }

        GHLPaymentResponse ghlResponse = new GHLPaymentResponse();
        ghlResponse.setTransactionType(data.getFirst("TransactionType"));
        ghlResponse.setPaymentMethod(data.getFirst("PymtMethod"));
        ghlResponse.setServiceID(data.getFirst("ServiceID"));
        ghlResponse.setPaymentID(data.getFirst("PaymentID"));
        ghlResponse.setOrderNumber(data.getFirst("OrderNumber"));
        // ghlResponse.setAmount(Double.parseDouble(data.getFirst("Amount")));
        // 250325: Change Data Type to BigDecimal with Vicky Approval
        ghlResponse.setAmount(new BigDecimal(data.getFirst("Amount")));
        ghlResponse.setCurrencyCode(data.getFirst("CurrencyCode"));
        ghlResponse.setHashValue(data.getFirst("HashValue"));
        ghlResponse.setHashValue2(data.getFirst("HashValue2"));
        ghlResponse.setTxnID(data.getFirst("TxnID"));
        ghlResponse.setIssuingBank(data.getFirst("IssuingBank"));
        ghlResponse.setTxnStatus(Integer.parseInt(data.getFirst("TxnStatus")));
        ghlResponse.setTxnMsg(data.getFirst("TxnMessage"));
        ghlResponse.setAuthCode(data.getFirst("AuthCode"));
        ghlResponse.setBankRefNo(data.getFirst("BankRefNo"));
        ghlResponse.setTokenType(data.getFirst("TokenType"));
        ghlResponse.setToken(data.getFirst("Token"));
        
        // if(data.getFirst("RespTime2") != null || !data.getFirst("RespTime2").isEmpty()) {
        //     ghlResponse.setRespTime(data.getFirst("RespTime2"));
        // } else {
        //     ghlResponse.setRespTime(data.getFirst("RespTime"));
        // }
        
        ghlResponse.setRespTime(data.getFirst("RespTime"));
        ghlResponse.setCardNoMask(data.getFirst("CardNoMask"));
        ghlResponse.setCardHolder(data.getFirst("CardHolder"));
        ghlResponse.setCardType(data.getFirst("CardType"));
        ghlResponse.setCardExp(data.getFirst("CardExp"));
        ghlResponse.setParam7(data.getFirst("Param7"));

        // Map GHLPaymentResponse to GHLRequest
        GHLRequest ghlRequest = new GHLRequest();
        ghlRequest.setI_txn_ty(ghlResponse.getTransactionType());
        ghlRequest.setI_pymt_method(ghlResponse.getPaymentMethod());
        ghlRequest.setI_service_id(ghlResponse.getServiceID());
        ghlRequest.setI_pymt_id(ghlResponse.getPaymentID());
        ghlRequest.setI_orn_no(ghlResponse.getOrderNumber());
        ghlRequest.setI_amt(ghlResponse.getAmount());
        ghlRequest.setI_cur_cd(ghlResponse.getCurrencyCode());
        ghlRequest.setI_hash_val(ghlResponse.getHashValue());
        ghlRequest.setI_hash_val2(ghlResponse.getHashValue2());
        ghlRequest.setI_txn_id(ghlResponse.getTxnID());
        ghlRequest.setI_iss_bank(ghlResponse.getIssuingBank());
        ghlRequest.setI_txn_status(String.valueOf(ghlResponse.getTxnStatus()));
        ghlRequest.setI_txn_msg(ghlResponse.getTxnMsg());
        ghlRequest.setI_auth_cd(ghlResponse.getAuthCode());
        ghlRequest.setI_bank_ref_no(ghlResponse.getBankRefNo());
        ghlRequest.setI_token_ty(ghlResponse.getTokenType());
        ghlRequest.setI_token(ghlResponse.getToken());
        ghlRequest.setI_resp_time(ghlResponse.getRespTime());
        ghlRequest.setI_card_no_mask(ghlResponse.getCardNoMask());
        ghlRequest.setI_card_holder(ghlResponse.getCardHolder());
        ghlRequest.setI_card_ty(ghlResponse.getCardType());
        ghlRequest.setI_card_exp(ghlResponse.getCardExp());
        ghlRequest.setI_param7(ghlResponse.getParam7());
        ghlRequest.setI_created_by("system");
        ghlRequest.setI_modified_by("system");
        ghlRequest.setI_processed(0);

        // Call stored procedure
        try {
            Integer result = onlinePaymentService.sp_insghlresp(ghlRequest);

            try {
                // Store the payment request in cache for later retrieval
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("backendonly");
                extAudit.setI_request_body(convertToReadableFormat(data));
                extAudit.setI_response_body("Success");
                extAudit.setI_direction("Incoming");
                extAudit.setI_rms_batch_no("");

                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insghlresp for Online Payment: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }

        } catch (Exception e) {
            log.error("Error while executing stored procedure: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            try {
                // Store the payment request in cache for later retrieval
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("backendonly");
                extAudit.setI_request_body(convertToReadableFormat(data));
                extAudit.setI_response_body("Error");
                extAudit.setI_direction("Incoming");
                extAudit.setI_rms_batch_no("");

                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception ex) {
                log.error("Error in sp_insghlresp for Online Payment: " + ex.getMessage() + ", "
                        + (ex.getCause() != null ? ex.getCause().getMessage() : "No cause"), ex);
            }
            return "Error";
        }
        return "OK";
    }

    private String convertToReadableFormat(MultiValueMap<String, String> data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> readableMap = new HashMap<>();

            data.forEach((key, values) -> {
                if (values.size() == 1) {
                    readableMap.put(key, values.get(0));
                } else {
                    readableMap.put(key, values);
                }
            });

            return mapper.writeValueAsString(readableMap);
        } catch (Exception e) {
            return data.toString(); // Fallback to default toString
        }
    }

    private Integer uploadIdamanAPI(IdamanAPIUploadReq req, Integer mttRcptID) throws IOException {
        List<IdamanAPIUpload> result = Collections.emptyList();
        Integer result1 = -1;

        // try {
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

    @PostMapping(value = "/otcPayment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> otcPayment(HttpServletRequest request, @Valid @RequestBody OtcPaymentRequest payload)
            throws ParseException, JsonProcessingException {
        if (!authService.isAuthenticated(request))
            //return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        if (payload.getPymt_method() == null)
            payload.setPymt_method("");
        if (payload.getCollection_slip() == null && payload.getPymt_method().toLowerCase().equals("otc")) {
            log.error("Exception in " + this.getClass().toString()
                    + " otcPayment, collection_slip is null but payment method is 'OTC'!");
            return APIResponse.SystemRuleViolationExternal();
        }
        if (payload.getCollection_slip() == null)
            payload.setCollection_slip("");
        if (payload.getPayment_item_details() == null) {
            log.error("Exception in " + this.getClass().toString() + " otcPayment, Payment_item_details slip is null!");
            return APIResponse.SystemRuleViolationExternal();
        } else if (payload.getPayment_item_details().size() == 0) {
            log.error(
                    "Exception in " + this.getClass().toString() + " otcPayment, Payment_item_details slip is empty!");
            return APIResponse.SystemRuleViolationExternal();
        }

        for (OTCPymtItemDet item : payload.getPayment_item_details()) {
            if (item.getDisc_amt() == null)
                item.setDisc_amt(new BigDecimal(0));
            // if(item.getEmail_flag() == null)
            // item.setEmail_flag(0);
            if ((item.getTxn_type() == null || item.getTxn_type().isEmpty())
                    && item.getItem_desc().toLowerCase().contains("annual declaration")) {
                log.error("Exception in " + this.getClass().toString()
                        + " otcPayment, an item's txn_type is null but item_desc is 'Annual Declaration'!");
                return APIResponse.SystemRuleViolationExternal();
            }
            if (item.getCalendar_yr() == null && item.getItem_desc().toLowerCase().contains("annual declaration")) {
                log.error("Exception in " + this.getClass().toString()
                        + " otcPayment, an item's calender_yr is null but item_desc is 'Annual Declaration'!");
                return APIResponse.SystemRuleViolationExternal();
            }
        }

        int statusCode = otcSvc.sourceSystemSubmittedOrder(payload, authService.getClientIP(request),
                authService.getLoginUserName());

        ExtAudit extAudit = new ExtAudit();
        extAudit.setI_module_nm("otcPayment");
        extAudit.setI_direction("Incoming");
        extAudit.setI_rms_batch_no(payload.getOrn_no());
        String jsonBody = objectMapper.writeValueAsString(payload);
        extAudit.setI_request_body(jsonBody);

        if (statusCode == -403) { // unknown state code or unsupported MTT order_status
            externalAudit(extAudit, "Error: System Rule Violation");
            log.error("OTC Payment Error! StatusCode: " + statusCode);
            return APIResponse.SystemRuleViolationExternal();
        }
        if (statusCode <= 0) {
            externalAudit(extAudit, "Error: Internal Server Error");
            log.error("OTC Payment Error! StatusCode: " + statusCode);
            return APIResponse.InternalServerErrorExternal();
        }
        // statusCode == -1 // Cannot insert/update a MTT
        // statusCode == -2 // Bad MTT order_status
        // statusCode == -3 // Cannot find MFT record
        // statusCode == -4 // Cannot insert/update a MTT Item
        // statusCode == -5 // Not similar payment method found in existing record! (OTC
        // != Online)
        // statusCode == 1 // Insert records for MTT &| MTT Items successful
        // statusCode == 2 // Update records for MTT &| MTT Items successful

        return APIResponse.SuccessResponseExternal(Collections.singletonMap("data", Collections.emptyList()));
    }

    // ss start

    private void externalAudit(ExtAudit paramAudit, String Msg) {

        try {
            ExtAudit extAudit = paramAudit;
            extAudit.setI_response_body(Msg);
            commonSvc.sp_insextaudit(extAudit);
        } catch (Exception e) {
            log.error("Error: " + e.getMessage() + ", "
                    + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
        }

    }

    @PostMapping(value = "/submitBillingPayment")
    public void rms_submitBillingPayment(@RequestParam("billing_no") String billing_no,
            @RequestParam("ss_return_url") String ss_return_url, HttpServletResponse response, HttpSession session)
            throws IOException {

        SubmitBilPymtStatus submitBilPymtStatus = new SubmitBilPymtStatus();

        submitBilPymtStatus = onlinePaymentService.sp_checksubmitbilpaymentstatus(billing_no);

        if (submitBilPymtStatus.getCount() == 0) {
            response.sendRedirect(ss_return_url + "?orn_no=" + billing_no + "&err_msg=" + "failed");
        } else {

            if (submitBilPymtStatus.getOrder_status().equals("ES") ||
                    submitBilPymtStatus.getOrder_status().equals("PP")) {
                response.sendRedirect(onlinePortalURL + "/payment-page?pr=" + billing_no);

            }

            else {
                response.sendRedirect(ss_return_url + "?orn_no=" + billing_no + "&pymt_status="
                        + submitBilPymtStatus.getOrder_status_nm() + "&rcpt_no=" + submitBilPymtStatus.getRcpt_no()
                        + "&rcpt_dt=" + submitBilPymtStatus.getRcpt_dt());
            }
        }

        // response.sendRedirect(backPortalURL + "/bibss-customer-id-validation?ss_cd="
        // + ss_cd + "&callbackurl=" + callbackurl);

        // If something went wrong, you might want to send an error status or message to
        // the client.
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.getWriter().write("An internal error occurred");
        return;

    }
    // end

    private void updateBill(String ornNo) throws JsonProcessingException {

        bSvc.sp_updatebillstatuspaid(ornNo, authService.getLoginUserName());
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

    private void updateAccrual(List<OnlinePaymentItem> paymentItems, MTTRCPT rcpt) {
        for (OnlinePaymentItem item : paymentItems) {
            // call ricp function

            if (item.getCp_no() != "" && item.getCp_no() != null) {
                Integer result = ricpSvc.updateRICPCollected(
                        new SubmitRICPCanRequest(item.getEntity_type(), item.getEntity_no(), item.getCp_no(), "CE",
                                item.getMtt_item_id()),
                        // new SubmitRICPCanRequest(item.getEntity_type(),
                        // item.getEntity_nm(),item.getCp_no(), "CE"),
                        "CPP", "CA",
                        authService.getLoginUserName().equals("Anonymous") ? "system"
                                : authService.getLoginUserName());
            }

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

    @PostMapping(value = "/getrmsfee")
    public ResponseEntity<ApiResponse<BigDecimal>> sp_getrmsfee(HttpServletRequest request,
            @RequestBody OnlinePaymentItem getRequest) {

        BigDecimal result = BigDecimal.ZERO;

        if (!authService.isAuthenticated(request)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        result = onlinePaymentService.sp_getrmsfee(getRequest);

        return APIResponse.SuccessResponse(result);
    }
}
