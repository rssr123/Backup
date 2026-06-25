package com.maven.rms.controllers;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.rowset.serial.SerialException;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.models.BillingStatus;
import com.maven.rms.models.BillingStatusRequest;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.Billing.Billing;
import com.maven.rms.models.payload.requests.BillingRegistrationIncoming;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.BillingService;
import com.maven.rms.services.CommonService;
import com.maven.rms.utils.APIResponse;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/billing/v1")
@Slf4j
public class BillingController {

	@Autowired
	private AuthService authService;
	@Autowired
	private BillingService bSvc;

	@Value("${rms.application.backPortalURL}")
	private String backPortalURL;

	// @Autowired
	// private UAMService uamSvc;

	@Autowired
	private CommonService commonSvc;

	@Autowired
	private ObjectMapper objectMapper;

	public BillingController() {
		log.info("BillingController has started");
	}

	@GetMapping(value = "/bcdlistreg")
	public ResponseEntity<?> billingCdListReg(HttpServletRequest request) throws IOException {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		return APIResponse.SuccessResponseExternal(bSvc.sp_getbillingregistrationtypecodelist());
	}

	private void externalAudit(ExtAudit paramAudit, String Msg) {

		try {
			ExtAudit extAudit = paramAudit;
			// Get current datetime
			String responseDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			String responseBodyWithTime = "Response Time: " + responseDateTime + " | Body: " + Msg;
			extAudit.setI_response_body(responseBodyWithTime);
			commonSvc.sp_insextaudit(extAudit);
		} catch (Exception e) {
			log.error("Error: " + e.getMessage() + ", "
					+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
		}

	}

	@GetMapping(value = "/bcdrunnoreg")
	public ResponseEntity<?> billingCdRunnoReg(HttpServletRequest request) throws IOException {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		return APIResponse.SuccessResponseExternal(bSvc.sp_getfreerunnofullBilling());
	}

	@GetMapping(value = "/bcdapproverlistreg")
	public ResponseEntity<?> billingCdApproverListReg(HttpServletRequest request) throws IOException {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		return APIResponse.SuccessResponseExternal(bSvc.sp_getusersbyrole("BRANCHMANAGER"));
	}

	@PostMapping(value = "/debugupdatepaidbill", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> debugupdatepaidbill(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if (!payload.containsKey("i_bil_no") || !payload.containsKey("secret") || CollectionUtils.size(payload) != 2)
			return APIResponse.InvalidFormatExternal();
		if (!payload.get("secret").equals("debugging_the_billing_update_function!"))
			return APIResponse.InvalidFormatExternal();

		Integer result = bSvc.sp_updatebillstatuspaid((String) payload.get("i_bil_no"), authService.getLoginUserName());
		return APIResponse.SuccessResponse(result);
	}
	@PostMapping(value = "/debugupdatebillaritables", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> debugupdatebillari(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if (!payload.containsKey("i_bil_no") || !payload.containsKey("secret") || CollectionUtils.size(payload) != 2)
			return APIResponse.InvalidFormatExternal();
		if (!payload.get("secret").equals("debugging_the_billing_ari_function!"))
			return APIResponse.InvalidFormatExternal();
		
		Integer result = bSvc.debugARI((String) payload.get("i_bil_no"));
		return APIResponse.SuccessResponse(result);
	}
	@PostMapping(value = "/debuglaunchari", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> debuglaunchari(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if (!payload.containsKey("i_bil_no") || !payload.containsKey("secret") || CollectionUtils.size(payload) != 2)
			return APIResponse.InvalidFormatExternal();
		if (!payload.get("secret").equals("debugging_the_billing_ari_function!"))
			return APIResponse.InvalidFormatExternal();

		Integer result = bSvc.debugARIResponse((String) payload.get("i_bil_no"));
		return APIResponse.SuccessResponse(result);
	}

	@PostMapping(value = "/newbill", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> newBill(HttpServletRequest request,
			@Valid @RequestBody BillingRegistrationIncoming payload) throws IOException, SerialException, SQLException {

		if (!authService.isAuthenticated(request) || authService.getLoginUserName().toLowerCase().equals("anonymous"))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		Map<String, String> checklist = new HashMap<String, String>();
		checklist.put("i_bt_code", payload.getI_bt_code());
		checklist.put("i_state", payload.getI_state());
		checklist.put("i_entity_type", payload.getI_entity_type());
		checklist.put("i_sscode", payload.getI_sscode());

		Integer statusCode = bSvc.sp_checkbillreginfo(checklist);

		if (statusCode < 0) {
			int storeCode = statusCode;
			String issue = "";
			if (storeCode < -7) {
				issue += "SourceSystem: SSCode ";
				statusCode += 8;
			}
			if (storeCode < -3) {
				if (!issue.isEmpty())
					issue += "| ";
				issue += "Billing: Billing Code ";
				statusCode += 4;
			}
			if (storeCode < -1) {
				if (!issue.isEmpty())
					issue += "| ";
				issue += "Parameter: State Code ";
				statusCode += 2;
			}
			if (storeCode < 0) {
				if (!issue.isEmpty())
					issue += "| ";
				issue += "Parameter: Entity Type ";
			}

			issue += "Error.(i_sscode:" + payload.getI_bt_code() + "|i_bt_code:" + payload.getI_bt_code()
					+ "|i_state" + payload.getI_state() + "|i_entity_type:" + payload.getI_entity_type() + ")";

			log.error("Exception in " + this.getClass().toString() + "newBill func - Error code: "
					+ statusCode.toString() + " - " + issue);
			return APIResponse.InvalidFormatExternal();
		}

		if (payload.getI_billing_method().equals("L") || payload.getI_billing_method().equals("A")) {
			if (payload.getI_loa_ref_no() == null || payload.getI_loa_ref_no().isEmpty()
					|| payload.getI_date_range() == null || payload.getI_date_range().size() == 0
					|| payload.getI_billing_issuance_list() == null || payload.getI_billing_issuance_list().size() == 0
					|| payload.getI_loa_document() == null || payload.getI_loa_document().size() == 0) {
				log.error("Exception in " + this.getClass().toString() + "newBill func - missing LOA attribute(s)!");
				return APIResponse.InvalidFormatExternal();
			}

			if (payload.getI_billing_method().equals("A")) {
				if (payload.getI_agmt_ref_no() == null || payload.getI_agmt_ref_no().isEmpty()) {
					log.error("Exception in " + this.getClass().toString()
							+ "newBill func - missing Agreement attribute(s)!");
					return APIResponse.InvalidFormatExternal();
				}
			}
		}
		String billPrepend = "BIL" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		// Get the real billing no that's free
		String billNo = bSvc.sp_getandreservebillrunno(payload.getI_billing_cnt());
		Integer runningNo = Integer.parseInt(billNo.split(billPrepend)[1]);

		// Reset the billing no in both obj root and issuance list (if exists)
		payload.setI_billing_no(billNo);
		if (payload.getI_billing_method().equals("L") || payload.getI_billing_method().equals("A")) {
			for (Map<String, Object> issuance : payload.getI_billing_issuance_list()) {
				if (((String) issuance.get("billing_no")).equals(billNo))
					break;
				issuance.put("billing_no", billPrepend + String.format("%06d", runningNo));
				runningNo += 1;
			}

		}
		statusCode = bSvc.insNewBilReg(payload, authService.getLoginUserName());

		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString() + "newBill func - insNewBilReg failed!");
			return APIResponse.InternalServerErrorExternal();
		}
		// RMSUser approver = uamSvc.findUserByUsername(bill.req_name).orElse(null);

		// Send notification to SSM here //send notification to target (bill approver)
		// ssmApi.notifyUser(bil.bill_no, approver.email, "New Bill detected", "A new
		// bill is awaiting your review!");

		return APIResponse.SuccessResponseExternal(billNo);
	}

	@PostMapping(value = "/getbildocblob")
	public ResponseEntity<ApiResponse<String>> sp_getunapprovedbilregdocblob(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if (!payload.containsKey("i_doc_id") || CollectionUtils.size(payload) != 1)
			return APIResponse.InvalidFormatExternal();

		String result = bSvc.sp_getbildocblob((Integer) payload.get("i_doc_id"));

		if (result.isEmpty()) {
			log.error("Exception in " + this.getClass().toString()
					+ "sp_getunapprovedbilregdocblob func - blob is empty!");
			return APIResponse.NoDataFound();
		}
		return APIResponse.SuccessResponse(result);
	}

	@PostMapping(value = "/getbilchildimgblob")
	public ResponseEntity<ApiResponse<String>> sp_getbilchildimgblob(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if (!payload.containsKey("i_child_id") || CollectionUtils.size(payload) != 1)
			return APIResponse.InvalidFormatExternal();

		String result = bSvc.sp_getbilchildimgblob((Integer) payload.get("i_child_id"));

		if (result.isEmpty()) {
			log.error("Exception in " + this.getClass().toString() + "sp_getbilchildimgblob func - blob is empty!");
			return APIResponse.NoDataFound();
		}
		return APIResponse.SuccessResponse(result);
	}

	@PostMapping(value = "/getbildoc")
	public ResponseEntity<?> sp_getbildoc(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_page")
				|| !payload.containsKey("i_size")) && CollectionUtils.size(payload) != 3)
			return APIResponse.InvalidFormatExternal();

		return APIResponse.SuccessResponseExternal(bSvc.sp_getbildoc((String) payload.get("i_billing_no"),
				(Integer) payload.get("i_page"),
				(Integer) payload.get("i_size")));
	}

	@PostMapping(value = "/getbilchild")
	public ResponseEntity<?> sp_getbilchildren(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_page")
				|| !payload.containsKey("i_size")) && CollectionUtils.size(payload) != 3)
			return APIResponse.InvalidFormatExternal();

		return APIResponse.SuccessResponseExternal(bSvc.sp_getbilchildren((String) payload.get("i_billing_no"),
				(Integer) payload.get("i_page"),
				(Integer) payload.get("i_size")));
	}

	@PostMapping(value = "/getbilitems")
	public ResponseEntity<?> sp_getbilitems(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_page")
				|| !payload.containsKey("i_size") || !payload.containsKey("i_bil_item_status"))
				&& CollectionUtils.size(payload) != 4)
			return APIResponse.InvalidFormatExternal();

		return APIResponse.SuccessResponseExternal(bSvc.sp_getbilitems((String) payload.get("i_billing_no"),
				(Integer) payload.get("i_page"),
				(Integer) payload.get("i_size"),
				(String) payload.get("i_bil_item_status")));
	}

	@PostMapping(value = "/getbilhist")
	public ResponseEntity<?> sp_getbilhist(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_page")
				|| !payload.containsKey("i_size")) && CollectionUtils.size(payload) != 3)
			return APIResponse.InvalidFormatExternal();

		return APIResponse.SuccessResponseExternal(bSvc.sp_getbilhist((String) payload.get("i_billing_no"),
				(Integer) payload.get("i_page"),
				(Integer) payload.get("i_size")));
	}

	@PostMapping(value = "/rejectbillwf", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> rejectBillRegWf(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_remark"))
				&& CollectionUtils.size(payload) != 2)
			return APIResponse.InvalidFormatExternal();

		Integer statusCode = bSvc.sp_rejectunapprovedbilreg((String) payload.get("i_billing_no"),
				authService.getLoginUserName(), (String) payload.get("i_remark"));

		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString()
					+ "rejectBillRegWf func - sp_rejectunapprovedbilreg failed!");
			return APIResponse.InternalServerErrorExternal();
		}
		// Send notification to SSM here //send notification to target (owner of bill)
		// ssmApi.notifyUser(bil.bill_no, bill.created_by.email, "Bill rejected", "Your
		// bill has been rejected.");
		return APIResponse.SuccessResponseExternal((String) payload.get("i_billing_no"));
	}

	@PostMapping(value = "/querybillwf", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> queryBillRegWf(HttpServletRequest request, @Valid @RequestBody Map<String, Object> payload) 
			throws IOException, SerialException, SQLException {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		
		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_remark"))
				&& CollectionUtils.size(payload) > 1 && CollectionUtils.size(payload) < 5)
			return APIResponse.InvalidFormatExternal();

		Integer statusCode = 0;
		if (payload.containsKey("i_billing_items")) {
			statusCode = bSvc.updBilWFItem(payload, authService.getLoginUserName());
			if (statusCode < 1) {
				log.error("Exception in " + this.getClass().toString()
						+ "queryBillRegWf func - updBilWFItem failed! Code: " + statusCode.toString());
				return APIResponse.InternalServerErrorExternal();
			}
		}

		if (payload.containsKey("i_billing_info")) {
			statusCode = bSvc.updBilWFDetails(payload, authService.getLoginUserName());
			if (statusCode < 1) {
				log.error("Exception in " + this.getClass().toString()
						+ "queryBillRegWf func - updBilWFItem failed! Code: " + statusCode.toString());
				return APIResponse.InternalServerErrorExternal();
			}
		}

		statusCode = bSvc.sp_queryunapprovedbilreg(payload, authService.getLoginUserName());

		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString()
					+ "queryBillRegWf func - sp_queryunapprovedbilreg failed! Code: " + statusCode.toString());
			return APIResponse.InternalServerErrorExternal();
		}
		// Send notification to SSM here //send notification to target (req_name/owner
		// of bill)
		// ssmApi.notifyUser(bil.bill_no, bill.created_by.email/bill.req_name, "Bill
		// Query Reply/Request", "Further details requested / Bill updated by owner.");
		return APIResponse.SuccessResponseExternal((String) payload.get("i_billing_no"));
	}

	@PostMapping(value = "/aprovebillwf", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> approveBillRegWf(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws IOException, SQLException {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_remark"))
				&& CollectionUtils.size(payload) != 2)
			return APIResponse.InvalidFormatExternal();

		Integer statusCode = bSvc.sp_approvebilreg((String) payload.get("i_billing_no"), authService.getLoginUserName(),
				(String) payload.get("i_remark"));
		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString()
					+ " approveBillRegWf func - sp_approvebilreg failed! Code: " + statusCode.toString());
			return APIResponse.InternalServerErrorExternal();
		}

		if (statusCode == 2) // 2 is billing mthd = 'once', so immediately confirm the bill (make ARI Invoice
								// and send email for payment req)
			statusCode = bSvc.confirmBill((String) payload.get("i_billing_no"), authService.getLoginUserName(), true);
		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString()
					+ " approveBillRegWf func - confirmBill failed! Code: " + statusCode.toString());
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("ari_failed_code", statusCode.toString());
			return APIResponse.InternalServerErrorExternal(data);
		}

		// Send notification to SSM here //send notification to target (owner of bill)
		// ssmApi.notifyUser(bil.bill_no, bill.created_by.email, "Bill WF Approved",
		// "Your new bill has been approved.");

		return APIResponse.SuccessResponseExternal((String) payload.get("i_billing_no"));
	}

	@PostMapping(value = "/cancelbillwf", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> cancelBillRegWf(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_remark"))
				&& CollectionUtils.size(payload) != 2)
			return APIResponse.InvalidFormatExternal();

		Integer statusCode = bSvc.sp_cancelunapprovedbilreg((String) payload.get("i_billing_no"),
				authService.getLoginUserName(), (String) payload.get("i_remark"));

		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString()
					+ "cancelBillRegWf func - sp_cancelunapprovedbilreg failed!");
			return APIResponse.InternalServerErrorExternal();
		}
		// Send notification to SSM here //send notification to target (owner of bill)
		// ssmApi.notifyUser(bil.bill_no, bill.created_by.email, "Bill Cancelled", "Your
		// bill has been cancelled.");
		return APIResponse.SuccessResponseExternal((String) payload.get("i_billing_no"));
	}

	@PostMapping(value = "/getbillist", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getBillRegWfListing(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_page") || !payload.containsKey("i_size") || !payload.containsKey("i_ent_nm")
				|| !payload.containsKey("i_ent_no") || !payload.containsKey("i_ss_cd")
				|| !payload.containsKey("i_receipt_no")
				|| !payload.containsKey("i_billing_mthd") || !payload.containsKey("i_bil_wf_status")
				|| !payload.containsKey("i_dt_start")
				|| !payload.containsKey("i_dt_end") || !payload.containsKey("i_b_type")
				|| !payload.containsKey("i_billing_no"))
				&& !(CollectionUtils.size(payload) > 11 && CollectionUtils.size(payload) < 14))
			return APIResponse.InvalidFormatExternal();

		if (authService.isInternalUser() == 0 || payload.containsKey("i_user_only"))
			payload.put("i_created_by", authService.getLoginUserName());

		return APIResponse.SuccessResponseExternal(bSvc.sp_getbilllisting(payload));
	}

	@PostMapping(value = "/getbillcanadjlist", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getBillCanAdjListing(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_page") || !payload.containsKey("i_size") || !payload.containsKey("i_ent_nm")
				|| !payload.containsKey("i_ent_no") || !payload.containsKey("i_ss_cd")
				|| !payload.containsKey("i_receipt_no")
				|| !payload.containsKey("i_billing_mthd") || !payload.containsKey("i_bil_wf_status")
				|| !payload.containsKey("i_dt_start")
				|| !payload.containsKey("i_dt_end") || !payload.containsKey("i_b_type")
				|| !payload.containsKey("i_billing_no")
				|| !payload.containsKey("i_cust_id") || !payload.containsKey("i_orn_no")
				|| !payload.containsKey("i_ent_ty"))
				&& !(CollectionUtils.size(payload) > 14 && CollectionUtils.size(payload) < 17))
			return APIResponse.InvalidFormatExternal();

		if (authService.isInternalUser() == 0 || payload.containsKey("i_user_only"))
			payload.put("i_created_by", authService.getLoginUserName());

		return APIResponse.SuccessResponseExternal(bSvc.sp_getbilllistingcanadj(payload));
	}

	@PostMapping(value = "/getcancelbillist", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getCancelledBillist(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));

		if ((!payload.containsKey("i_page") || !payload.containsKey("i_size") || !payload.containsKey("i_billing_no")
				|| !payload.containsKey("i_cust_id") || !payload.containsKey("i_bil_wf_status")
				|| !payload.containsKey("i_dt_start") || !payload.containsKey("i_dt_end"))
				&& !(CollectionUtils.size(payload) > 6 && CollectionUtils.size(payload) < 9))
			return APIResponse.InvalidFormatExternal();

		if (authService.isInternalUser() == 0 || payload.containsKey("i_user_only"))
			payload.put("i_created_by", authService.getLoginUserName());

		return APIResponse.SuccessResponseExternal(bSvc.sp_getcancelbillist(payload));
	}

	@PostMapping(value = "/getbill", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getBill(HttpServletRequest request, @Valid @RequestBody Map<String, Object> payload) {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_page")
				|| !payload.containsKey("i_size") || !payload.containsKey("i_bil_item_status")
				|| !payload.containsKey("i_bil_details_flag"))
				&& (CollectionUtils.size(payload) > 4
						&& CollectionUtils.size(payload) < 7))
			return APIResponse.InvalidFormatExternal();

		Map<String, Object> dataEntry = new HashMap<String, Object>();
		dataEntry.put("billing_no", (String) payload.get("i_billing_no"));
		dataEntry.put("page", (Integer) payload.get("i_page"));
		dataEntry.put("size", (Integer) payload.get("i_size"));
		dataEntry.put("bil_item_status", (String) payload.get("i_bil_item_status"));
		dataEntry.put("more_info", (Boolean) payload.get("i_bil_details_flag"));
		Billing data = bSvc.sp_getbill(dataEntry);

		// Scrub sensitive data:
		data.setBil_wf_id(0);
		data.setBil_id(0);

		if ((authService.isInternalUser() == 0 && !authService.getLoginUserName().equals(data.getCreated_by()))
				|| (payload.containsKey("i_user_only") && !authService.getLoginUserName().equals(data.getCreated_by())))
			//return APIResponse.NoPermission(Collections.singletonMap("status", "UNAUTHORIZED"));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		return APIResponse.SuccessResponseExternal(data);
	}

	@PostMapping(value = "/validbilcanadj", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> checkValBilForCanAdj(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no")) && CollectionUtils.size(payload) != 1)
			return APIResponse.InvalidFormatExternal();

		Integer bilId = bSvc.sp_checkbilstatusvalid((String) payload.get("i_billing_no"));
		if (bilId > 0)
			return APIResponse.SuccessResponseExternal("true");

		return APIResponse.SuccessResponseExternal("false");

	}

	@PostMapping(value = "/reqbillcan", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> reqBillCan(HttpServletRequest request, @Valid @RequestBody Map<String, Object> payload)
			throws JsonProcessingException {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_remark"))
				&& CollectionUtils.size(payload) != 2)
			return APIResponse.InvalidFormatExternal();

		Integer statusCode = bSvc.bilCanReq((String) payload.get("i_billing_no"), authService.getLoginUserName(),
				(String) payload.get("i_remark"));

		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString() + "reqBillCan func - bilCanReq failed! Code: "
					+ statusCode.toString());
			return APIResponse.InternalServerErrorExternal();
		}
		// Send notification to SSM here //send notification to target (owner of bill)
		/*
		 * if(statusCode == 1)
		 * ssmApi.notifyUser(bil.bill_no, bill.created_by.email,
		 * "Bill Cancellation Request", "Your bill has been cancelled.");
		 * else if(statusCode == 2)
		 * ssmApi.notifyUser(bil.bill_no, bill.created_by.email,
		 * "Bill Cancellation Request",
		 * "Your bill cancellation request has been logged.");
		 */

		return APIResponse.SuccessResponseExternal((String) payload.get("i_billing_no"));
	}

	@PostMapping(value = "/rejectbillcan", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> rejectBillCan(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_remark"))
				&& CollectionUtils.size(payload) != 2)
			return APIResponse.InvalidFormatExternal();

		Integer statusCode = bSvc.rejBilCanReq((String) payload.get("i_billing_no"), authService.getLoginUserName(),
				(String) payload.get("i_remark"));

		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString() + "rejectBillCan func - rejBilCanReq failed! Code: "
					+ statusCode.toString());
			return APIResponse.InternalServerErrorExternal();
		}
		// Send notification to SSM here //send notification to target (owner of bill)
		// ssmApi.notifyUser(bil.bill_no, bill.created_by.email, "Bill Cancellation
		// rejected", "Your bill cancellation request has been rejected.");
		return APIResponse.SuccessResponseExternal((String) payload.get("i_billing_no"));
	}

	@PostMapping(value = "/querybillcan", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> queryBillCan(HttpServletRequest request, @Valid @RequestBody Map<String, Object> payload) {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_remark"))
				&& CollectionUtils.size(payload) != 2)
			return APIResponse.InvalidFormatExternal();

		Integer statusCode = bSvc.updQryBilCan((String) payload.get("i_billing_no"), authService.getLoginUserName(),
				(String) payload.get("i_remark"));

		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString() + "queryBillCan func - updQryBilCan failed! Code: "
					+ statusCode.toString());
			return APIResponse.InternalServerErrorExternal();
		}
		// Send notification to SSM here //send notification to target (req_name/owner
		// of bill)
		// ssmApi.notifyUser(bil.bill_no, bill.created_by.email/bill.req_name, "Bill
		// Cancellation Query Reply/Request", "Further details requested / Bill updated
		// by owner.");
		return APIResponse.SuccessResponseExternal((String) payload.get("i_billing_no"));
	}

	@PostMapping(value = "/aprovebillcan", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> approveBillCan(HttpServletRequest request, @Valid @RequestBody Map<String, Object> payload)
			throws JsonProcessingException {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_remark"))
				&& CollectionUtils.size(payload) != 2)
			return APIResponse.InvalidFormatExternal();

		Integer statusCode = bSvc.sp_approvebilcan((String) payload.get("i_billing_no"), authService.getLoginUserName(),
				(String) payload.get("i_remark"));
		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString()
					+ "approveBillCan func - sp_approvebilcan failed! Code: " + statusCode.toString());
			return APIResponse.InternalServerErrorExternal();
		}
		// Send notification to SSM here //send notification to target (owner of bill)
		// ssmApi.notifyUser(bil.bill_no, bill.created_by.email, "Bill Cancellation
		// Approved", "Your bill cancellation request has been approved.");
		return APIResponse.SuccessResponseExternal((String) payload.get("i_billing_no"));
	}

	@PostMapping(value = "/reqbilladj", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> reqBillAdj(HttpServletRequest request, @Valid @RequestBody Map<String, Object> payload)
			throws JsonProcessingException {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_remark")
				|| !payload.containsKey("i_billing_items")) && CollectionUtils.size(payload) != 3)
			return APIResponse.InvalidFormatExternal();

		Map<String, Object> param = new HashMap<String, Object>();
		List<Map<String, Object>> adjData = (List<Map<String, Object>>) payload.get("i_billing_items");

		param.put("billing_no", (String) payload.get("i_billing_no"));
		param.put("username", authService.getLoginUserName());
		param.put("remarks", (String) payload.get("i_remark"));

		Integer statusCode = bSvc.adjBilReq(adjData, param);

		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString() + "reqBillAdj func - adjBilReq failed! Code: "
					+ statusCode.toString());
			return APIResponse.InternalServerErrorExternal();
		}
		// Send notification to SSM here //send notification to target (owner of bill)
		/*
		 * if(statusCode == 1)
		 * ssmApi.notifyUser(bil.bill_no, bill.created_by.email,
		 * "Bill Adjustment Request", "Your bill has been adjusted.");
		 * else if(statusCode == 2)
		 * ssmApi.notifyUser(bil.bill_no, bill.created_by.email,
		 * "Bill Adjustment Request", "Your bill adjustment request has been logged.");
		 */
		return APIResponse.SuccessResponseExternal((String) payload.get("i_billing_no"));
	}

	@PostMapping(value = "/rejectbilladj", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> rejectBillAdj(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_remark"))
				&& CollectionUtils.size(payload) != 2)
			return APIResponse.InvalidFormatExternal();

		Integer statusCode = bSvc.rejBilAdjReq((String) payload.get("i_billing_no"), authService.getLoginUserName(),
				(String) payload.get("i_remark"));

		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString() + "rejectBillAdj func - rejBilAdjReq failed! Code: "
					+ statusCode.toString());
			return APIResponse.InternalServerErrorExternal();
		}
		// Send notification to SSM here //send notification to target (owner of bill)
		// ssmApi.notifyUser(bil.bill_no, bill.created_by.email, "Bill Adjustment
		// rejected", "Your bill adjustment has been rejected.");

		return APIResponse.SuccessResponseExternal((String) payload.get("i_billing_no"));
	}

	@PostMapping(value = "/querybilladj", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> queryBillAdj(HttpServletRequest request, @Valid @RequestBody Map<String, Object> payload) {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_remark"))
				&& CollectionUtils.size(payload) != 2)
			return APIResponse.InvalidFormatExternal();

		Integer statusCode = bSvc.updQryBilAdj((String) payload.get("i_billing_no"), authService.getLoginUserName(),
				(String) payload.get("i_remark"));

		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString() + "queryBillAdj func - updQryBilAdj failed! Code: "
					+ statusCode.toString());
			return APIResponse.InternalServerErrorExternal();
		}
		// Send notification to SSM here //send notification to target (req_name/owner
		// of bill)
		// ssmApi.notifyUser(bil.bill_no, bill.created_by.email/bill.req_name, "Bill
		// Adjustment Query Reply/Request", "Further details requested / Bill updated by
		// owner.");
		return APIResponse.SuccessResponseExternal((String) payload.get("i_billing_no"));
	}

	@PostMapping(value = "/aprovebilladj", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> approveBillAdj(HttpServletRequest request, @Valid @RequestBody Map<String, Object> payload)
			throws JsonProcessingException {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no") || !payload.containsKey("i_remark"))
				&& CollectionUtils.size(payload) != 2)
			return APIResponse.InvalidFormatExternal();

		Integer statusCode = bSvc.apprBilAdjReq((String) payload.get("i_billing_no"), authService.getLoginUserName(),
				(String) payload.get("i_remark"));
		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString()
					+ "approveBillAdj func - apprBilAdjReq failed! Code: " + statusCode.toString());
			return APIResponse.InternalServerErrorExternal();
		}

		// Send notification to SSM here //send notification to target (owner of bill)
		// ssmApi.notifyUser(bil.bill_no, bill.created_by.email, "Bill Adjustment
		// Approved", "Your bill adjustment request has been approved.");
		return APIResponse.SuccessResponseExternal((String) payload.get("i_billing_no"));
	}

	@PostMapping(value = "/getexistsloa")
	public ResponseEntity<?> getLOAExists(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_loa_ref_no")) && CollectionUtils.size(payload) != 1)
			return APIResponse.InvalidFormatExternal();

		return APIResponse
				.SuccessResponseExternal(Integer.toString(bSvc.sp_getexistloa((String) payload.get("i_loa_ref_no"))));
	}

	@PostMapping(value = "/getregisteredloa")
	public ResponseEntity<?> getLOARegistered(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_loa_ref_no")) && CollectionUtils.size(payload) != 1)
			return APIResponse.InvalidFormatExternal();

		return APIResponse.SuccessResponseExternal(
				Integer.toString(bSvc.sp_getregisteredloa((String) payload.get("i_loa_ref_no"))));
	}

	@PostMapping(value = "/refreshbillingpayment")
	public ResponseEntity<?> refreshBillingPayment(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_billing_no")) && CollectionUtils.size(payload) != 1)
			return APIResponse.InvalidFormatExternal();

		return APIResponse.SuccessResponseExternal(Integer.toString(
				bSvc.retriggerBillPayment((String) payload.get("i_billing_no"), authService.getLoginUserName())));
	}

	// billing issuance by source system start

	@PostMapping(value = "/getBillingStatus")
	public ResponseEntity<ApiResponse<List<BillingStatus>>> sp_getbillingstatus(HttpServletRequest request,
			@Valid @RequestBody BillingStatusRequest bilRequest) throws SQLException, JsonProcessingException {

		ExtAudit extAudit = new ExtAudit();
		extAudit.setI_module_nm("getBillingStatus");
		extAudit.setI_direction("Incoming");
		extAudit.setI_rms_batch_no(null);
		// Get current datetime
		String requestDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		String jsonBody = objectMapper.writeValueAsString(bilRequest);
		// Combine datetime and body
		String requestBodyWithTime = "Request Time: " + requestDateTime + " | Body: " + jsonBody;
		extAudit.setI_request_body(requestBodyWithTime);

		List<BillingStatus> result = Collections.emptyList();

		if (!authService.isAuthenticated(request)) {
			externalAudit(extAudit, "No Permission");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		result = bSvc.sp_getbillingstatus(bilRequest);

		if (result.isEmpty()) {
			externalAudit(extAudit, "NoDataFoundExternal: Logic Handling at sp_getbillingstatus, need check sp");
			return APIResponse.NoDataFoundExternal();
		}

		externalAudit(extAudit, "Success");

		return APIResponse.SuccessResponseExternal(result);
	}

	@PostMapping(value = "/submitBilling")
	public void rms_billingIssuanceBySSPage(@RequestParam("ss_cd") String ss_cd,
			@RequestParam("callbackurl") String callbackurl, HttpServletResponse response, HttpSession session)
			throws IOException {

		// response.sendRedirect(
		// backPortalURL + "/bibss-customer-id-validation?ss_cd=" + ss_cd +
		// "&callbackurl=" + callbackurl);
		
		String redirectUrl = backPortalURL + "/bibss-customer-id-validation" +
				"?ss_cd=" + URLEncoder.encode(ss_cd, "UTF-8") +
				"&callbackurl=" + URLEncoder.encode(callbackurl, "UTF-8");

		response.sendRedirect(redirectUrl);

		// If something went wrong, you might want to send an error status or message to
		// the client.
		response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		response.getWriter().write("An internal error occurred");
		return;
	}

	// billing issuance by source system end
}
