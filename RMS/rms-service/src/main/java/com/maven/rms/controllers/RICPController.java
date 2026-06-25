package com.maven.rms.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.models.CreditControlReminderRequest;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.RICP;
import com.maven.rms.models.RICPList;
import com.maven.rms.models.RICPRPRequest;
import com.maven.rms.models.RICPRequest;
import com.maven.rms.models.payload.requests.PaidRICPRequest;
import com.maven.rms.models.payload.requests.SubmitRICPCanRequest;
import com.maven.rms.models.payload.requests.SubmitRICPIssRequest;
import com.maven.rms.services.RICPService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.RMSLogger;

import javax.servlet.http.HttpServletRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.CommonService;

@RestController
@RequestMapping("/api/ricp/v1")
@Slf4j
public class RICPController {
	private static final Logger logger = LoggerFactory.getLogger(RICPController.class);

	@Autowired
	private RICPService ricpSvc;

	@Autowired
	private AuthService authService;

	@Autowired
	private CommonService commonSvc;

	@Autowired
	private ObjectMapper objectMapper;

	public RICPController() {
		logger.info("RICPController has started....");
	}

	// @Secured("ROLE_REQUESTER")
	@PostMapping(value = "/submitricpissuance", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> submitRICPIssuance(HttpServletRequest request,
			@Valid @RequestBody SubmitRICPIssRequest payload) throws ParseException, JsonProcessingException {

		ExtAudit extAudit = new ExtAudit();
		extAudit.setI_module_nm("submitricpissuance");
		extAudit.setI_direction("Incoming");
		extAudit.setI_rms_batch_no(null);
		String jsonBody = objectMapper.writeValueAsString(payload);
		extAudit.setI_request_body(jsonBody);

		if (!authService.isAuthenticated(request)) {
			externalAudit(extAudit, "No Permission");
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		if (payload.getCp_sub_sect_id() == null)
			payload.setCp_sub_sect_id("");

		if (payload.getCp_sub_sect_id() == null)
			payload.setCp_sub_sect_id("");

		int successId = 0;
		Date issDate = new SimpleDateFormat("yyyy-MM-dd").parse(payload.getDt_issuance());
		Date expiryDate = new SimpleDateFormat("yyyy-MM-dd").parse(payload.getDt_expiry());
		// Date writeOffDate = new
		// SimpleDateFormat("yyyy-MM-dd").parse(payload.getDt_expiry());
		// writeOffDate.setMonth(writeOffDate.getMonth() + ricpSvc.getWriteOffMonths());
		// LocalDateTime ldt = LocalDateTime.ofInstant(writeOffDate.toInstant(),
		// ZoneId.systemDefault());
		// writeOffDate =
		// Date.from(ldt.minusDays(1).atZone(ZoneId.systemDefault()).toInstant());

		// new RICP object has status automatically set as CA
		RICP newRICPRec = new RICP(payload.getEntity_type(), payload.getEntity_no(), payload.getCp_no(),
				payload.getCp_act_id(), payload.getCp_sect_id(), payload.getCp_sub_sect_id(),
				issDate, expiryDate, payload.getCp_amt(), payload.getAccr_amt(), payload.getCp_tier_lvl(),
				payload.getCp_tier_amt(), null,
				authService.getLoginUserName().equals("Anonymous") ? "system" : authService.getLoginUserName());

		successId = ricpSvc.issueRCIP(newRICPRec);

		if (successId == -1) {
			logger.error("Exception in " + this.getClass().toString()
					+ ":submitRICPIssuance method, failed to insert duplicate RICP data.");

			externalAudit(extAudit, "DuplicateDataExternal: Logic Handling at issueRCIP, need check sp");

			return APIResponse.DuplicateDataExternal();
		} else if (successId < -1 || successId == 0) {
			logger.error("Exception in " + this.getClass().toString()
					+ ":submitRICPIssuance method, failed to insert RICP data.");
			externalAudit(extAudit, "InternalServerErrorExternal: Logic Handling at issueRCIP, need check sp");
			// return APIResponse.InvalidFormat(); //InternalServerError();
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

	// @Secured("ROLE_REQUESTER")
	@PostMapping(value = "/submitricpcancelvoid", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> submitRICPCancelVoid(HttpServletRequest request,
			@Valid @RequestBody SubmitRICPCanRequest payload) throws ParseException, JsonProcessingException {

		ExtAudit extAudit = new ExtAudit();
		extAudit.setI_module_nm("submitricpcancelvoid");
		extAudit.setI_direction("Incoming");
		extAudit.setI_rms_batch_no(null);
		String jsonBody = objectMapper.writeValueAsString(payload);
		extAudit.setI_request_body(jsonBody);

		if (!authService.isAuthenticated(request)) {
			externalAudit(extAudit, "No Permission");
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		int successId = 0;
		if (payload.getStatus().equals("V") || payload.getStatus().equals("C"))
			successId = ricpSvc.updateRICPCollected(payload, payload.getStatus().equals("V") ? "CPV" : "CPC", "CA",
					authService.getLoginUserName().equals("Anonymous") ? "system" : authService.getLoginUserName());
		else {
			logger.error("Exception in " + this.getClass().toString()
					+ " submitRICPCancelVoid, payload status invalid value: " + payload.getStatus());
			externalAudit(extAudit, "InvalidFormatExternal: Logic Handling at updateRICPCollected, need check sp");
			return APIResponse.InvalidFormatExternal(); // InternalServerError();
		}

		if (successId <= 0) {
			logger.error("Exception in " + this.getClass().toString()
					+ ": submitRICPCancelVoid method, failed to update RICP data.");
			// return APIResponse.NoDataFound(ControllersEnum.RICP_CONTROLLER);
			// //InternalServerError();

			if (successId == -401) {
				externalAudit(extAudit, "NoDataFoundExternal: Logic Handling at updateRICPCollected, need check sp");
				return APIResponse.NoDataFoundExternal();
			}

			externalAudit(extAudit,
					"InternalServerErrorExternal: Logic Handling at updateRICPCollected, need check sp");
			return APIResponse.InternalServerErrorExternal();
		}

		externalAudit(extAudit, "Success");

		return APIResponse.SuccessResponseExternal(Collections.singletonMap("data", Collections.emptyList()));
	}

	// @Secured("ROLE_USER")
	@PostMapping(value = "/getricp")
	public ResponseEntity<?> getRICP(HttpServletRequest request, @RequestBody RICPRequest RICPRequest)
			throws ParseException {
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));

		RICPList ricpList = ricpSvc.sp_getricp(RICPRequest);

		if (ricpList.getRicpList().isEmpty())
			// return APIResponse.NoDataFound(ControllersEnum.RICP_CONTROLLER);
			return APIResponse.InternalServerErrorExternal();

	        return APIResponse.SuccessResponseExternal(ricpList);
	}
	
	/*
	//@Secured("ROLE_REQUESTER")
	@PostMapping(value = "/paidricp", 
	consumes = MediaType.APPLICATION_JSON_VALUE, 
	produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> paidRICPCollect(HttpServletRequest request, @Valid @RequestBody PaidRICPRequest payload) throws ParseException{
		if (!authService.isAuthenticated(request))
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));

		int successId = 0;
		if(!payload.getCp_no().isEmpty())
			successId = ricpSvc.updateRICPCollected(new SubmitRICPCanRequest(payload.getEntity_type(), payload.getEntity_no()
					, payload.getCp_no(), "CE", payload.getMtt_item_id()), "CPP", "CA"
					, authService.getLoginUserName().toLowerCase().equals("anonymous") ? "system" : authService.getLoginUserName());
		else{
			logger.error("Exception in " + this.getClass().toString() + " paidRICPCollect");
			//return APIResponse.InvalidFormat();	//InternalServerError();
			return APIResponse.InternalServerErrorExternal();
		}

		if (successId <= 0) {
			logger.error("Exception in " + this.getClass().toString() + ": paidRICPCollect method, failed to update RICP data.");
			//return APIResponse.NoDataFound(ControllersEnum.RICP_CONTROLLER);	//InternalServerError();
			return APIResponse.InternalServerErrorExternal();
		}
		return APIResponse.SuccessResponseExternal(Collections.singletonMap("data", Collections.emptyList()));

	*/

	@PostMapping(value = "/submitagentricprp")
	public ResponseEntity<?> sp_updricprp(HttpServletRequest request,
			@Valid @RequestBody RICPRPRequest ricprpRequestRequest) throws JsonProcessingException {

		ExtAudit extAudit = new ExtAudit();
		extAudit.setI_module_nm("submitagentricprp");
		extAudit.setI_direction("Incoming");
		extAudit.setI_rms_batch_no(null);
		String jsonBody = objectMapper.writeValueAsString(ricprpRequestRequest);
		extAudit.setI_request_body(jsonBody);

		Integer result = 0;

		if (!authService.isAuthenticated(request)) {
			externalAudit(extAudit, "No Permission");
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		result = ricpSvc.sp_updricprp(ricprpRequestRequest);

		if (result == -1) {
			externalAudit(extAudit, "NoDataFoundExternal: Logic Handling at sp_updricprp, need check sp");
			return APIResponse.NoDataFoundExternal();
		} else if (result < -1 || result == 0) {
			externalAudit(extAudit, "InternalServerErrorExternal: Logic Handling at sp_updricprp, need check sp");
			RMSLogger.error("Exception in " + this.getClass().toString()
					+ ":submitAgentRICPRealizedPayment method, failed to update RICP data. Code: " + result.toString());
			return APIResponse.InternalServerErrorExternal();
		}
		externalAudit(extAudit, "Success");

		return APIResponse.SuccessResponseExternal(Collections.singletonMap("data", Collections.emptyList()));

	}
}
