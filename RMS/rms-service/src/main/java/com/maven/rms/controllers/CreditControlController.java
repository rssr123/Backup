package com.maven.rms.controllers;

import java.time.LocalDate;
import java.time.ZoneId;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.collections4.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.CreditControlPaidInvoiceRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.models.CreditControlCase;
import com.maven.rms.models.CreditControlReminderRequest;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.CommonService;
import com.maven.rms.services.CreditControllerService;
import com.maven.rms.services.NotificationService;
import com.maven.rms.services.CreditControlCaseService;
import com.maven.rms.services.CreditControlService;
import com.maven.rms.utils.APIResponse;
import lombok.extern.slf4j.Slf4j;

@Valid
@RestController
@RequestMapping("/api/cc/v1")
@Slf4j
public class CreditControlController {

	@Autowired
	private AuthService authService;
	@Autowired
	private CreditControllerService ccSvc;
	@Autowired
	private CreditControlCaseService cc2Service;
	@Autowired
	private CreditControlService ccService;
	@Autowired
	private NotificationService notificationSvc;

	@Autowired
	private CommonService commonSvc;

	@Autowired
	private ObjectMapper objectMapper;

	@PostMapping(value = "/submitCaseReminder")
	public ResponseEntity<?> sp_insccrmd(HttpServletRequest request,
			@Valid @RequestBody CreditControlReminderRequest ccRmdRequest) throws JsonProcessingException {

		ExtAudit extAudit = new ExtAudit();
		extAudit.setI_module_nm("submitCaseReminder");
		extAudit.setI_direction("Incoming");
		extAudit.setI_rms_batch_no(null);
		String jsonBody = objectMapper.writeValueAsString(ccRmdRequest);
		extAudit.setI_request_body(jsonBody);

		Integer result = 0;

		if (!authService.isAuthenticated(request)) {
			externalAudit(extAudit, "No Permission");
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		if (ccRmdRequest.getReminder_cnt() < 1 || ccRmdRequest.getReminder_cnt() > 3) {
			externalAudit(extAudit, "SystemRuleViolationExternal: Reminder Count < 1 or > 3");
			return APIResponse.SystemRuleViolationExternal();
		}

		result = ccService.sp_insccrmd(ccRmdRequest);

		if (result == -1) { // no need error log because user might key in wrong fms_ari_ref_no
			externalAudit(extAudit, "NoDataFoundExternal: Result:-1 Logic Handling at sp_insccrmd, need check sp");
			return APIResponse.NoDataFoundExternal();
		} else if (result == -2) {
			externalAudit(extAudit,
					"SystemRuleViolationExternal: Result:-2 Logic Handling at sp_insccrmd, need check sp");
			return APIResponse.SystemRuleViolationExternal();
		} else if (result == -3) {
			externalAudit(extAudit,
					"SystemRuleViolationExternal: Result:-3  Logic Handling at sp_insccrmd, need check sp");
			return APIResponse.SystemRuleViolationExternal();
		} else if (result == -4) {
			externalAudit(extAudit,
					"InternalServerErrorExternal: Result:-4  Logic Handling at sp_insccrmd, need check sp");
			log.error("Exception in " + this.getClass().toString()
					+ ":submitCaseReminder method, failed to insert Case Reminder data. Code: " + result.toString());
			return APIResponse.InternalServerErrorExternal();
		} else if (result == -5) {
			externalAudit(extAudit,
					"InternalServerErrorExternal: Result:-5  Logic Handling at sp_insccrmd, need check sp");
			log.error("Exception in " + this.getClass().toString()
					+ ":submitCaseReminder method, failed to update Case data. Code: " + result.toString());
			return APIResponse.InternalServerErrorExternal();
		} else if (result == -6) {
			externalAudit(extAudit,
					"InternalServerErrorExternal: Result:-6  Logic Handling at sp_insccrmd, need check sp");
			log.error("Exception in " + this.getClass().toString()
					+ ":submitCaseReminder method, failed to insert Case History data. Code: " + result.toString());
			return APIResponse.InternalServerErrorExternal();
		} else if (result == 0) {
			externalAudit(extAudit,
					"InternalServerErrorExternal: Result: 0 Logic Handling at sp_insccrmd, need check sp");
			log.error("Exception in " + this.getClass().toString()
					+ ":submitCaseReminder method, failed to submit Case Reminder. Code: " + result.toString());
			return APIResponse.InternalServerErrorExternal();
		}

		externalAudit(extAudit, "Success");

		return APIResponse.SuccessResponseExternal(Collections.singletonMap("data", Collections.emptyList()));
	}

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

	@PostMapping(value = "/getcreditcontrolcase", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getCCCase(HttpServletRequest request, @Valid @RequestBody Map<String, Object> payload) {
		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_task_no") || !payload.containsKey("i_page")
				|| !payload.containsKey("i_size")) && CollectionUtils.size(payload) != 3)
			return APIResponse.InvalidFormatExternal();

		Map<String, Object> dataEntry = new HashMap<String, Object>();
		dataEntry.put("task_no", (String) payload.get("i_task_no"));
		dataEntry.put("page", (Integer) payload.get("i_page"));
		dataEntry.put("size", (Integer) payload.get("i_size"));
		dataEntry.put("skipFK", false);
		CreditControlCase c = ccSvc.sp_getcccase(dataEntry);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("pymt_attr_doc_no", c.getPaymentInformation().getPymt_attr_doc_no());
		data.put("invoice_desc", c.getInvoiceInformation().getInvoice_desc());
		data.put("task_status", c.getTask_status());
		data.put("dt_assigned", c.getDt_assigned());
		data.put("cust_nm", c.getCustomerInformation().getCust_nm());
		data.put("cust_email", c.getCustomerInformation().getCust_email());
		data.put("cust_phone", c.getCustomerInformation().getCust_phone());
		data.put("cust_addr1", c.getCustomerInformation().getCust_addr_1());
		data.put("cust_addr2", c.getCustomerInformation().getCust_addr_2());
		data.put("cust_addr3", c.getCustomerInformation().getCust_addr_3());
		data.put("cust_postcode", c.getCustomerInformation().getCust_postcode());
		data.put("cust_city", c.getCustomerInformation().getCust_city());
		data.put("cust_state", c.getCustomerInformation().getCust_state());
		data.put("attr_case_no", c.getInvoiceInformation().getAttr_case_no());
		data.put("dt_created", c.getDt_created());
		data.put("fms_ari_ref_no", c.getInvoiceInformation().getFms_ari_ref_no());
		data.put("pymt_status", c.getPaymentInformation().getPymt_status());
		data.put("txn_ty", c.getPaymentInformation().getTxn_ty());
		data.put("ref_no_txn", c.getPaymentInformation().getRef_no_txn());
		data.put("rcpt_no", c.getPaymentInformation().getRcpt_no());
		data.put("pymt_amt", c.getInvoiceInformation().getCur_doc_bal());
		data.put("reminders", c.getReminders());
		data.put("payment_items", c.getItemInformation());
		data.put("history", c.getHistory());
		data.put("documents_list", c.getDocuments_list());
		data.put("reminders_size", c.getReminders_size());
		data.put("payment_items_size", c.getPayment_items_size());
		data.put("history_size", c.getHistory_size());
		data.put("documents_size", c.getDocuments_size());
		// data.put("parent", c);

		/*
		 * //If wrong status return bad access
		 * if((authService.isInternalUser() == 0 &&
		 * !authService.getLoginUserName().equals(data.getCreated_by()))
		 * || (payload.containsKey("i_user_only") &&
		 * !authService.getLoginUserName().equals(data.getCreated_by())))
		 * return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		 */

		return APIResponse.SuccessResponseExternal(data);
	}

	// Submit new credit control case
	@PostMapping(value = "/submitCase")
	public ResponseEntity<ApiResponse<String>> submitCase(
			HttpServletRequest request,
			@Valid @RequestBody CreditControlCase bodyRequest) throws JsonProcessingException {

		ExtAudit extAudit = new ExtAudit();
		extAudit.setI_module_nm("submitCase");
		extAudit.setI_direction("Incoming");
		extAudit.setI_rms_batch_no(null);
		String jsonBody = objectMapper.writeValueAsString(bodyRequest);
		extAudit.setI_request_body(jsonBody);

		if (!authService.isAuthenticated(request)) {
			externalAudit(extAudit, "No Permission");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		Integer result = 0;

		if (bodyRequest != null) {
			String desc = bodyRequest.getInvoiceInformation().getInvoice_desc().trim();
			String pymt_status = bodyRequest.getPaymentInformation().getPymt_status().trim();

			if (!desc.equals("Billing") && !desc.equals("Non-Billing") && !desc.equals("e-BSK Loan")) {
				externalAudit(extAudit, "InvalidFormatExternal: Desc only Accept Billing/Non-Billing/e-BSK Loan");
				return APIResponse.InvalidFormatExternal();
			}

			if (!pymt_status.equals("Paid") && !pymt_status.equals("Unpaid")) {
				externalAudit(extAudit, "InvalidFormatExternal: payment status can only be Paid/UnPaid");
				return APIResponse.InvalidFormatExternal();
			}

			result = cc2Service.sp_inscccase(
					bodyRequest);
		}

		if (result == 0) {
			externalAudit(extAudit, "NoDataFoundExternal: Logic Handling at sp_inscccase, need check sp");
			return APIResponse.NoDataFoundExternal();
		} else if (result == -2) {
			externalAudit(extAudit, "DuplicateDataExternal: Logic Handling at sp_inscccase, need check sp");
			return APIResponse.DuplicateDataExternal();

		} else if (result > 0) {
			externalAudit(extAudit, "Success");
			return APIResponse.SuccessResponseExternal("");
		} else {
			externalAudit(extAudit, "InvalidFormatExternal: Logic Handling at sp_inscccase, need check sp");
			return APIResponse.InvalidFormatExternal();
		}
	}

	@PostMapping(value = "/getcccdocblob")
	public ResponseEntity<ApiResponse<String>> sp_getCCCdocblob(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if (!payload.containsKey("i_doc_id") || CollectionUtils.size(payload) != 1)
			return APIResponse.InvalidFormatExternal();

		String result = ccSvc.sp_getCCdocblob((Integer) payload.get("i_doc_id"));

		if (result.isEmpty()) {
			log.error("Exception in " + this.getClass().toString() + "sp_getccdocblob func - blob is empty!");
			return APIResponse.NoDataFoundExternal();
		}
		return APIResponse.SuccessResponse(result);
	}

	@PostMapping(value = "/getccchist")
	public ResponseEntity<?> sp_getCCCaseHist(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_task_no") || !payload.containsKey("i_page")
				|| !payload.containsKey("i_size")) && CollectionUtils.size(payload) != 3)
			return APIResponse.InvalidFormatExternal();

		return APIResponse.SuccessResponseExternal(ccSvc.sp_getcccasehist((String) payload.get("i_task_no"),
				(Integer) payload.get("i_page"),
				(Integer) payload.get("i_size")).get("history"));
	}	
	
	@GetMapping(value = "/smerolelist")
	public ResponseEntity<?> getSMERoleList(HttpServletRequest request) throws IOException {

		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		List<String> roles = new ArrayList<String>();
		roles.add("Finance Senior Manager");
		roles.add("Finance HOD");
		roles.add("Legal");
		roles.addAll(ccSvc.sp_getsmerolelist());
		
		return APIResponse.SuccessResponseExternal(roles);
	}

	@PostMapping(value = "/updccc")
	public ResponseEntity<?> updCCCase(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_task_no") || !payload.containsKey("i_remark"))
				&& !(CollectionUtils.size(payload) > 1 && CollectionUtils.size(payload) < 6))
			return APIResponse.InvalidFormatExternal();

		String rawGrp = payload.get("i_group") != null ? (String) payload.get("i_group") : "";
		
		if(rawGrp.equals("Finance Senior Manager"))
			rawGrp = "FINANACESENIORMANAGER";
		else if(rawGrp.equals("Finance HOD"))
			rawGrp = "FINANCEHOD";
		else if(rawGrp.equals("Legal"))
			rawGrp = "LEGAL";
		
		String remark = (String) payload.get("i_remark");
		Map<String, Object> dataEntry = new HashMap<String, Object>();
		dataEntry.put("task_no", (String) payload.get("i_task_no"));
		dataEntry.put("page", 1);
		dataEntry.put("size", 1);
		dataEntry.put("skipFK", false);
		CreditControlCase data = ccSvc.sp_getcccase(dataEntry);

		Integer status = 0;

		if (payload.containsKey("i_supporting_documents")) {
			status = ccSvc.insertSupportingCCCDocuments(data.getCc_case_id(),
					(List<Map<String, Object>>) payload.get("i_supporting_documents"),
					authService.getLoginUserName());
			if (status < 1) {
				log.error("Exception in " + this.getClass().toString()
						+ "updCCCase func - insertSupportingCCCDocuments - Bad statuscode: ["
						+ Integer.toString(status) + "]");
				status = ccSvc.sp_inscccasehist(data.getCc_case_id(), authService.getLoginUserName(),
						"Uploading documents failed!", "R");
				if (status < 1)
					log.error("Exception in " + this.getClass().toString()
							+ "updCCCase func - insertSupportingCCCDocuments > sp_inscccasehist "
							+ "- Bad statuscode: [" + Integer.toString(status) + "]");

				return APIResponse.InvalidFormatExternal();
			}
			status = 0;
		}
		String taskAction = data.getTask_status();
		if (taskAction.equals("IP") || taskAction.equals("RQ") || taskAction.equals("CMI") || taskAction.equals("DMI") || taskAction.equals("I")) {
			if (!payload.containsKey("i_action")) {
				log.error("Exception in " + this.getClass().toString()
						+ "updCCCase func - Pending Decision - Missing i_action variable from json!");
				return APIResponse.InvalidFormatExternal();
			}

			String action = (String) payload.get("i_action");
			if ((action.equals("assign") && payload.containsKey("i_group"))) {
				String newTaskStatus = null;
				String group = "";
				switch (rawGrp) {
					case "FINANACESENIORMANAGER":
						newTaskStatus = "PFSM";
						group = "Finance Senior Manager";
						break;
					case "FINANCEHOD":
						newTaskStatus = "PFH";
						group = "Finance HOD";
						break;
					case "LEGAL":
						newTaskStatus = "PL";
						group = "Legal Team";
						break;
					case "":
						break;
					default:
						newTaskStatus = "PSSS";
						group = rawGrp;
				}
				if (newTaskStatus == null) {
					log.error("Exception in " + this.getClass().toString()
							+ "updCCCase func - Assignment - Bad group: [" + (String) payload.get("i_group") + "]!");
					return APIResponse.InvalidFormatExternal();
				}

				status = ccSvc.updCaseTaskStatus(data.getTask_no(), newTaskStatus, rawGrp, authService.getLoginUserName(),
						"[Require input from " + group + "]\n\n" + remark, "R");

				if (status < 1) {
					log.error("Exception in " + this.getClass().toString()
							+ "updCCCase func - Assignment > updCaseTaskStatus - Bad statuscode: ["
							+ Integer.toString(status) + "]");
					return APIResponse.InvalidFormatExternal();
				}
			} else if (action.equals("debit") && payload.containsKey("i_amount")) {
				status = ccSvc.invokeCreditControlCaseDebitMemo(data, new BigDecimal((String) payload.get("i_amount")));
				if (status < 1) {
					log.error("Exception in " + this.getClass().toString()
							+ "updCCCase func - invokeCreditControlCaseDebitMemo - Bad statuscode: ["
							+ Integer.toString(status) + "]");
					status = ccSvc.sp_inscccasehist(data.getCc_case_id(), authService.getLoginUserName(),
							"[FMS Debit Memo API Failed!]\n\n" + remark, "R");
					if (status < 1)
						log.error("Exception in " + this.getClass().toString()
								+ "updCCCase func - invokeCreditControlCaseDebitMemo > sp_inscccasehist "
								+ "- Bad statuscode: [" + Integer.toString(status) + "]");

					return APIResponse.InvalidFormatExternal();
				}
				status = ccSvc.updCaseTaskStatus(data.getTask_no(), "DMI", rawGrp, authService.getLoginUserName(),
						"[Debit Memo of RM "
								+ (String.format("%,.2f", Double.parseDouble((String) payload.get("i_amount"))))
								+ " issued]\n\n" + remark,
						"R");

				if (status < 1) {
					log.error("Exception in " + this.getClass().toString()
							+ "updCCCase func - invokeCreditControlCaseDebitMemo > updCaseTaskStatus - Bad statuscode: ["
							+ Integer.toString(status) + "]");
					return APIResponse.InvalidFormatExternal();
				}
			} else if (action.equals("credit") && payload.containsKey("i_amount")) {
				status = ccSvc.invokeCreditControlCaseCreditMemo(data,
						new BigDecimal((String) payload.get("i_amount")));
				if (status < 1) {
					log.error("Exception in " + this.getClass().toString()
							+ "updCCCase func - invokeCreditControlCaseCreditMemo - Bad statuscode: ["
							+ Integer.toString(status) + "]");
					status = ccSvc.sp_inscccasehist(data.getCc_case_id(), authService.getLoginUserName(),
							"[FMS Credit Memo API Failed!]\n\n" + remark, "R");
					if (status < 1)
						log.error("Exception in " + this.getClass().toString()
								+ "updCCCase func - invokeCreditControlCaseCreditMemo > sp_inscccasehist "
								+ "- Bad statuscode: [" + Integer.toString(status) + "]");

					return APIResponse.InvalidFormatExternal();
				}
				status = ccSvc.updCaseTaskStatus(data.getTask_no(), "CMI", rawGrp, authService.getLoginUserName(),
						"[Credit Memo of RM "
								+ (String.format("%,.2f", Double.parseDouble((String) payload.get("i_amount"))))
								+ " issued]\n\n" + remark,
						"R");

				if (status < 1) {
					log.error("Exception in " + this.getClass().toString()
							+ "updCCCase func - invokeCreditControlCaseCreditMemo > updCaseTaskStatus - Bad statuscode: ["
							+ Integer.toString(status) + "]");
					return APIResponse.InvalidFormatExternal();
				}
			} else if (action.equals("impair")) {
				/*
				 * status = ccSvc.invokeCreditControlCaseImpair(data);
				 * if(status < 1) {
				 * log.error("Exception in " + this.getClass().toString()
				 * + "updCCCase func - invokeCreditControlCaseImpair - Bad statuscode: ["
				 * + Integer.toString(status) + "]");
				 * status = ccSvc.sp_inscccasehist(data.getCc_case_id(),
				 * authService.getLoginUserName(),
				 * "[FMS Impair API Failed!]\n\n" + remark, "R");
				 * if(status < 1)
				 * log.error("Exception in " + this.getClass().toString()
				 * + "updCCCase func - invokeCreditControlCaseImpair > sp_inscccasehist "
				 * + "- Bad statuscode: [" + Integer.toString(status) + "]");
				 * 
				 * return APIResponse.InvalidFormatExternal();
				 * }
				 */
				status = ccSvc.updCaseTaskStatus(data.getTask_no(), "I", rawGrp, authService.getLoginUserName(),
						"[Impair triggered]\n\n" + remark, "R");

				if (status < 1) {
					log.error("Exception in " + this.getClass().toString()
							+ "updCCCase func - invokeCreditControlCaseImpair > updCaseTaskStatus - Bad statuscode: ["
							+ Integer.toString(status) + "]");
					return APIResponse.InvalidFormatExternal();
				}
			} else if (action.equals("writeoff")) {
				status = ccSvc.invokeCreditControlCaseWriteOff(data);
				if (status < 1) {
					log.error("Exception in " + this.getClass().toString()
							+ " updCCCase func - invokeCreditControlCaseWriteOff - Bad statuscode: ["
							+ Integer.toString(status) + "]");
					status = ccSvc.sp_inscccasehist(data.getCc_case_id(), authService.getLoginUserName(),
							"[FMS Impair API Failed!]\n\n" + remark, "R");
					if (status < 1)
						log.error("Exception in " + this.getClass().toString()
								+ " updCCCase func - invokeCreditControlCaseWriteOff > sp_inscccasehist "
								+ "- Bad statuscode: [" + Integer.toString(status) + "]");

					return APIResponse.InvalidFormatExternal();
				}
				status = ccSvc.updCaseTaskStatus(data.getTask_no(), "WO", rawGrp, authService.getLoginUserName(),
						"[Write Off triggered]\n\n" + remark, "R");

				if (status < 1) {
					log.error("Exception in " + this.getClass().toString()
							+ " updCCCase func - invokeCreditControlCaseWriteOff > updCaseTaskStatus - Bad statuscode: ["
							+ Integer.toString(status) + "]");
					return APIResponse.InvalidFormatExternal();
				}
			} else if (action.equals("close")) {
				boolean paid = data.getPaymentInformation().getPymt_status().equals("P");
				if (paid)
					status = ccSvc.updCaseTaskStatus(data.getTask_no(), "C", rawGrp, authService.getLoginUserName(),
							"[Case Closed]\n\n" + remark, "R");
				else
					status = ccSvc.sp_inscccasehist(data.getCc_case_id(), authService.getLoginUserName(),
							"[Failed to Close Case - Payment Status Unpaid!]\n\n" + remark, "R");

				if (status < 1) {
					log.error("Exception in " + this.getClass().toString()
							+ " updCCCase func - Action > Close > "
							+ (paid ? "updCaseTaskStatus" : "sp_inscccasehist")
							+ " - Bad statuscode: [" + Integer.toString(status) + "]");
					return APIResponse.InvalidFormatExternal();
				}
			} else {
				log.error("Exception in " + this.getClass().toString()
						+ "updCCCase func - Pending Decision - Bad action: [" + action + "]");
				return APIResponse.InvalidFormatExternal();
			}
		} else if (taskAction.equals("PFSM") || taskAction.equals("PFH") || taskAction.equals("PSSS")) {
			status = ccSvc.updCaseTaskStatus(data.getTask_no(), "RQ", rawGrp, authService.getLoginUserName(),
					remark, "R");

			if (status < 1) {
				log.error("Exception in " + this.getClass().toString()
						+ "updCCCase func - Reply Query > updCaseTaskStatus - Bad statuscode: ["
						+ Integer.toString(status) + "]");
				return APIResponse.InvalidFormatExternal();
			}
		} else if (taskAction.equals("PL") || taskAction.equals("PCO")) {
			if (!payload.containsKey("i_action"))
				return APIResponse.InvalidFormatExternal();
			Boolean action = (Boolean) payload.get("i_action");
			if (taskAction.equals("PL"))
				status = action ? ccSvc.updCaseTaskStatus(data.getTask_no(), "PCO", rawGrp, authService.getLoginUserName(),
						"[Court Order Required]\n\n" + remark, "R")
						: ccSvc.updCaseTaskStatus(data.getTask_no(), "RQ", rawGrp, authService.getLoginUserName(),
								remark, "R");
			else if (taskAction.equals("PCO"))
				status = action ? ccSvc.updCaseTaskStatus(data.getTask_no(), "RQ", rawGrp, authService.getLoginUserName(),
						remark, "R")
						: ccSvc.updCaseTaskStatus(data.getTask_no(), "PCO", rawGrp, authService.getLoginUserName(),
								"[Court Order Required]\n\n" + remark, "R");
			if (status < 1) {
				log.error("Exception in " + this.getClass().toString()
						+ "updCCCase func - Legal/Court > updCaseTaskStatus - Bad statuscode: ["
						+ Integer.toString(status) + "] flags=taskStatus:" + taskAction + ",action:" + action);
				return APIResponse.InvalidFormatExternal();
			}
		}
		return APIResponse.SuccessResponseExternal("true");
	}

	@PostMapping(value = "/getassignedtaskcount")
	public ResponseEntity<ApiResponse<Integer>> getcccassignedtaskcount(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if (!payload.containsKey("i_assigned_to") || CollectionUtils.size(payload) != 1)
			return APIResponse.InvalidFormatExternal();

		Integer result = ccSvc.sp_getcccassignedtaskactivetaskcount((String) payload.get("i_assigned_to"));

		if (result == null || result <= 0) {
			log.debug("Exception in " + this.getClass().toString() + "getcccassignedtaskcount func - no cases!");
			return APIResponse.NoDataFoundExternal();
		}
		notificationSvc.sendNotificationUpdate();
		return APIResponse.SuccessResponse(result);
	}

	@PostMapping(value = "/getcreatedtaskcount")
	public ResponseEntity<ApiResponse<Integer>> getccccreatedtaskcount(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if (!payload.containsKey("i_created_by") || CollectionUtils.size(payload) != 1)
			return APIResponse.InvalidFormatExternal();

		Integer result = ccSvc.sp_getccccreatedtaskactivetaskcount((String) payload.get("i_created_by"));

		if (result == null || result <= 0) {
			log.debug("Exception in " + this.getClass().toString() + "getccccreatedtaskcount func - no cases!");
			return APIResponse.NoDataFoundExternal();
		}
		notificationSvc.sendNotificationUpdate();
		return APIResponse.SuccessResponse(result);
	}

	@PostMapping(value = "/tasklist")
	public ResponseEntity<?> getTaskList(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws SQLException, IOException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if ((!payload.containsKey("i_task_mode") || !payload.containsKey("i_page")
				|| !payload.containsKey("i_size")) &&
				!(CollectionUtils.size(payload) > 2 && CollectionUtils.size(payload) < 9))
			return APIResponse.InvalidFormatExternal();

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("i_task_mode", (String) payload.get("i_task_mode"));
		data.put("i_page", (Integer) payload.get("i_page"));
		data.put("i_size", (Integer) payload.get("i_size"));
		data.put("username", authService.getLoginUserName());
		data.put("i_task_id", (String) payload.get("i_task_id"));
		data.put("i_task_status", (String) payload.get("i_task_status"));
		data.put("i_payment_status", (String) payload.get("i_payment_status"));
		data.put("i_txn_type", (String) payload.get("i_txn_type"));
		data.put("i_case_no", (String) payload.get("i_case_no"));

		return APIResponse.SuccessResponseExternal(ccSvc.sp_getccctaskslisting(data));
	}

	@PostMapping(value = "/submitPaidInvoice")
	public ResponseEntity<?> sp_updcccasestatus(HttpServletRequest request,
			@Valid @RequestBody CreditControlPaidInvoiceRequest ccPaidInvRequest) throws JsonProcessingException {

		ExtAudit extAudit = new ExtAudit();
		extAudit.setI_module_nm("submitPaidInvoice");
		extAudit.setI_direction("Incoming");
		extAudit.setI_rms_batch_no(null);
		String jsonBody = objectMapper.writeValueAsString(ccPaidInvRequest);
		extAudit.setI_request_body(jsonBody);
		Integer result = 0;

		if (!authService.isAuthenticated(request)) {
			externalAudit(extAudit, "No Permission");
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		result = ccService.sp_updcccasestatus(ccPaidInvRequest);

		if (result == -1) { // no need error log because user might key in wrong fms_ari_ref_no
			externalAudit(extAudit,
					"NoDataFoundExternal: Result: -1 Logic Handling at sp_updcccasestatus, need check sp");
			return APIResponse.NoDataFoundExternal();
		} else if (result == -2) {
			externalAudit(extAudit,
					"DuplicateDataExternal: Result: -2 Logic Handling at sp_updcccasestatus, need check sp");
			return APIResponse.DuplicateDataExternal();
		} else if (result == -3) {
			externalAudit(extAudit,
					"InternalServerErrorExternal: Result: -3  Logic Handling at sp_updcccasestatus, need check sp");
			log.error("Exception in " + this.getClass().toString()
					+ ":submitPaidInvoice method, failed to update Paid Invoice. Code: " + result.toString());
			return APIResponse.InternalServerErrorExternal();
		} else if (result == -4) {
			externalAudit(extAudit,
					"InternalServerErrorExternal: Result: -4  Logic Handling at sp_updcccasestatus, need check sp");
			log.error("Exception in " + this.getClass().toString()
					+ ":submitPaidInvoice method, failed to insert Case History data. Code: " + result.toString());
			return APIResponse.InternalServerErrorExternal();
		} else if (result == 0) {
			externalAudit(extAudit,
					"InternalServerErrorExternal: Result: 0  Logic Handling at sp_updcccasestatus, need check sp");
			log.error("Exception in " + this.getClass().toString()
					+ ":submitPaidInvoice method, failed to submit Paid Invoice. Code: " + result.toString());
			return APIResponse.InternalServerErrorExternal();
		}

		externalAudit(extAudit, "Success");

		return APIResponse.SuccessResponseExternal(Collections.singletonMap("data", Collections.emptyList()));
	}
}
