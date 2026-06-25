package com.maven.rms.controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
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
import com.maven.rms.models.EmailPP;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.CommonService;
import com.maven.rms.services.MTTService;
import com.maven.rms.utils.APIResponse;
import org.apache.commons.collections4.CollectionUtils;

@RestController
@RequestMapping("/api/mtt/v1")
@Slf4j
public class MTTController {
	// private static final Logger logger =
	// LoggerFactory.getLogger(MTTController.class);

	@Autowired
	private MTTService mttService;
	@Autowired
	private AuthService authService;
	@Autowired
	private CommonService commonSvc;

	@Autowired
	private ObjectMapper objectMapper;

	public MTTController() {
		log.info("MTTController services is started");
	}

	// @Secured("ROLE_REQUESTER")
	@PostMapping(value = "/requeryorderstatus", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> requeryOrderStatus(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws IOException, JsonProcessingException {
		// return requeryOrderStatus_v1(request, payload);

		// 20251006, this will fix connection leak issue
		return requeryOrderStatus_v2(request, payload);
	}

	public ResponseEntity<?> requeryOrderStatus_v1(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws IOException, JsonProcessingException {

		ExtAudit extAudit = new ExtAudit();
		extAudit.setI_module_nm("requeryorderstatus");
		extAudit.setI_direction("Incoming");
		extAudit.setI_rms_batch_no(null);

		// Get current datetime
		String requestDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		String jsonBody = objectMapper.writeValueAsString(payload);

		String requestBodyWithTime = "Request Time: " + requestDateTime + " | Body: " + jsonBody;
		extAudit.setI_request_body(requestBodyWithTime);

		if (!authService.isAuthenticated(request)) {
			try {

				// Get current datetime
				String responseDateTime = LocalDateTime.now()
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				String responseBodyWithTime = "Response Time: " + responseDateTime + " | Body: NoPermission";
				extAudit.setI_response_body(responseBodyWithTime);

				commonSvc.sp_insextaudit(extAudit);
			} catch (Exception e) {
				log.error("Error in sp_insextaudit for requeryorderstatus: " + e.getMessage() + ", "
						+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
			}

			// return APIResponse.NoPermission(Collections.singletonMap("data",
			// Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		// if(!payload.containsKey("orn_no") || payload.size() != 1)
		if (!payload.containsKey("orn_no") || CollectionUtils.size(payload) != 1) {
			try {

				// Get current datetime
				String responseDateTime = LocalDateTime.now()
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				String responseBodyWithTime = "Response Time: " + responseDateTime + " | Body: Key orn_no not found";
				extAudit.setI_response_body(responseBodyWithTime);
				commonSvc.sp_insextaudit(extAudit);
			} catch (Exception e) {
				log.error("Error in sp_insextaudit for requeryorderstatus: " + e.getMessage() + ", "
						+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
			}
			return APIResponse.InvalidFormatExternal();
		}
		if (!(payload.get("orn_no") instanceof String))
			return APIResponse.InvalidFormatExternal();
		List<Map<String, Object>> data = mttService.requeryOrderStatus((String) payload.get("orn_no"),
				authService.getLoginUserName());

		if (data.get(0).containsKey("Error")) {
			String errorMsg = (String) data.get(0).get("Error");
			if (errorMsg.contains("find"))
				log.debug("Detected error when invoking requeryOrderStatus in MTTController.class: "
						+ data.get(0).get("Error"));
			else if (errorMsg.contains("duplicate"))
				log.debug("Detected error when invoking requeryOrderStatus in MTTController.class: "
						+ data.get(0).get("Error"));
			else
				log.error("Detected error when invoking requeryOrderStatus in MTTController.class: "
						+ data.get(0).get("Error"));
			// return APIResponse.InternalServerErrorExternal();
			// errorMsg.contains("find") ? APIResponse.NoDataFoundExternal() :
			// APIResponse.InternalServerErrorExternal();

			try {
				String ErrorMsg = errorMsg.contains("find")
						? "No Data Found: " + errorMsg
						: errorMsg.contains("duplicate")
								? "Duplicate Data found: " + errorMsg
								: "Internal Server Error:" + errorMsg;

				// Get current datetime
				String responseDateTime = LocalDateTime.now()
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				String responseBodyWithTime = "Response Time: " + responseDateTime + " | Body: " + ErrorMsg;
				extAudit.setI_response_body(responseBodyWithTime);

				commonSvc.sp_insextaudit(extAudit);
			} catch (Exception e) {
				log.error("Error in sp_insextaudit for requeryOrderStatus: " + e.getMessage() + ", "
						+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
			}

			// 241126: changes based on request (weiern)
			// 250831: added duplicate check (Brian)
			return errorMsg.contains("find") ? APIResponse.NoDataFoundExternal()
					: errorMsg.contains("duplicate") ? APIResponse.DuplicateDataExternal()
							: APIResponse.InternalServerErrorExternal();
		}

		try {

			// Get current datetime
			String responseDateTime = LocalDateTime.now()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			String responseBodyWithTime = "Response Time: " + responseDateTime + " | Body: Success";
			extAudit.setI_response_body(responseBodyWithTime);

			commonSvc.sp_insextaudit(extAudit);
		} catch (Exception e) {
			log.error("Error in sp_insextaudit for requeryOrderStatus: " + e.getMessage() + ", "
					+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
		}

		return APIResponse.SuccessResponseExternal(data);
	}

	public ResponseEntity<?> requeryOrderStatus_v2(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws IOException, JsonProcessingException {

		// Initialize audit object
		ExtAudit extAudit = new ExtAudit();
		extAudit.setI_module_nm("requeryorderstatus");
		extAudit.setI_direction("Incoming");
		extAudit.setI_rms_batch_no(null);

		// Capture request timestamp and body
		String requestDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
		String jsonBody = objectMapper.writeValueAsString(payload);
		extAudit.setI_request_body("Request Time: " + requestDateTime + " | Body: " + jsonBody);

		try {
			// ✅ Step 1: Authentication check (no DB connection)
			if (!authService.isAuthenticated(request)) {
				return buildAuditResponse(extAudit, "NoPermission",
						APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList())));
			}

			// ✅ Step 2: Validate payload structure
			if (!payload.containsKey("orn_no") || CollectionUtils.size(payload) != 1) {
				return buildAuditResponse(extAudit, "Key orn_no not found",
						APIResponse.InvalidFormatExternal());
			}

			// ✅ Step 3: Validate payload type
			if (!(payload.get("orn_no") instanceof String)) {
				return buildAuditResponse(extAudit, "Invalid orn_no type",
						APIResponse.InvalidFormatExternal());
			}

			// ✅ Step 4: Execute main business logic
			String ornNo = (String) payload.get("orn_no");
			String username = authService.getLoginUserName();

			List<Map<String, Object>> data = mttService.requeryOrderStatus(ornNo, username);
			// ✅ All DB connections released here

			// ✅ Step 5: Handle error responses from service
			if (!data.isEmpty() && data.get(0).containsKey("Error")) {
				String errorMsg = (String) data.get(0).get("Error");

				// Log based on error type
				if (errorMsg.contains("find")) {
					log.debug("No data found in requeryOrderStatus for orn_no {}: {}", ornNo, errorMsg);
				} else if (errorMsg.contains("duplicate")) {
					log.debug("Duplicate data in requeryOrderStatus for orn_no {}: {}", ornNo, errorMsg);
				} else {
					log.error("Error in requeryOrderStatus for orn_no {}: {}", ornNo, errorMsg);
				}

				// Build error message for audit
				String auditErrorMsg = errorMsg.contains("find")
						? "No Data Found: " + errorMsg
						: errorMsg.contains("duplicate")
								? "Duplicate Data found: " + errorMsg
								: "Internal Server Error: " + errorMsg;

				// Return appropriate error response with audit
				ResponseEntity<?> errorResponse = errorMsg.contains("find")
						? APIResponse.NoDataFoundExternal()
						: errorMsg.contains("duplicate")
								? APIResponse.DuplicateDataExternal()
								: APIResponse.InternalServerErrorExternal();

				return buildAuditResponse(extAudit, auditErrorMsg, errorResponse);
			}

			// ✅ Step 6: Success response
			return buildAuditResponse(extAudit, "Success",
					APIResponse.SuccessResponseExternal(data));

		} catch (IOException e) {
			// ✅ Handle IO exceptions (e.g., external API calls)
			log.error("IOException in requeryOrderStatus for orn_no {}: {}",
					payload.get("orn_no"), e.getMessage(), e);
			return buildAuditResponse(extAudit, "IO Error: " + e.getMessage(),
					APIResponse.InternalServerErrorExternal());

		} catch (IllegalArgumentException e) {
			// ✅ Handle validation exceptions from service
			log.error("Validation error in requeryOrderStatus for orn_no {}: {}",
					payload.get("orn_no"), e.getMessage(), e);
			return buildAuditResponse(extAudit, "Validation Error: " + e.getMessage(),
					APIResponse.InvalidFormatExternal());

		} catch (Exception e) {
			// ✅ Handle unexpected exceptions
			log.error("Unexpected error in requeryOrderStatus for orn_no {}: {}",
					payload.get("orn_no"), e.getMessage(), e);
			return buildAuditResponse(extAudit, "Error: " + e.getMessage(),
					APIResponse.InternalServerErrorExternal());
		}
	}

	/**
	 * Helper method to build audit response with timestamp
	 * ✅ Centralizes audit logging - single DB connection, immediately returned
	 * 
	 * @param extAudit        Audit object to log
	 * @param responseMessage Response message to log
	 * @param response        Response entity to return
	 * @return ResponseEntity with audit logged
	 */
	private ResponseEntity<?> buildAuditResponse(ExtAudit extAudit, String responseMessage,
			ResponseEntity<?> response) {
		try {
			// Add response timestamp
			String responseDateTime = LocalDateTime.now()
					.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
			String responseBodyWithTime = "Response Time: " + responseDateTime + " | Body: " + responseMessage;
			extAudit.setI_response_body(responseBodyWithTime);

			// ✅ Insert audit log (gets 1 connection, inserts, releases immediately)
			commonSvc.sp_insextaudit(extAudit);

		} catch (Exception e) {
			// Don't fail the request if audit logging fails
			log.error("Error in sp_insextaudit for {}: {} - {}",
					extAudit.getI_module_nm(),
					e.getMessage(),
					(e.getCause() != null ? e.getCause().getMessage() : "No cause"),
					e);
		}

		return response;
	}

	// @Secured("ROLE_REQUESTER")
	@PostMapping(value = "/getorderstatus", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getOrderStatus(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws JsonProcessingException {

		ExtAudit extAudit = new ExtAudit();
		extAudit.setI_module_nm("getorderstatus");
		extAudit.setI_direction("Incoming");
		extAudit.setI_rms_batch_no(null);
		String jsonBody = objectMapper.writeValueAsString(payload);
		extAudit.setI_request_body(jsonBody);

		if (!authService.isAuthenticated(request)) {
			try {

				extAudit.setI_response_body("NoPermission");
				commonSvc.sp_insextaudit(extAudit);
			} catch (Exception e) {
				log.error("Error in sp_insextaudit for getorderstatus: " + e.getMessage() + ", "
						+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
			}

			// return APIResponse.NoPermission(Collections.singletonMap("data",
			// Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
		// if(!payload.containsKey("orn_no") || payload.size() != 1)
		if (!payload.containsKey("orn_no") || CollectionUtils.size(payload) != 1) {
			try {

				extAudit.setI_response_body("Key orn_no not found");
				commonSvc.sp_insextaudit(extAudit);
			} catch (Exception e) {
				log.error("Error in sp_insextaudit for getorderstatus: " + e.getMessage() + ", "
						+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
			}

			return APIResponse.InvalidFormatExternal();
		}
		if (!(payload.get("orn_no") instanceof String))
			return APIResponse.InvalidFormatExternal();
		List<Map<String, Object>> data = mttService.getOrderStatus((String) payload.get("orn_no"));

		if (data.get(0).containsKey("Error")) {
			String errorMsg = (String) data.get(0).get("Error");
			if (errorMsg.contains("find"))
				log.debug("Detected error when invoking getOrderStatus in MTTController.class: "
						+ data.get(0).get("Error"));
			else if (errorMsg.contains("duplicate"))
				log.debug("Detected error when invoking getOrderStatus in MTTController.class: "
						+ data.get(0).get("Error"));
			else
				log.error("Detected error when invoking getOrderStatus in MTTController.class: "
						+ data.get(0).get("Error"));
			// return APIResponse.InternalServerErrorExternal();

			try {

				extAudit.setI_response_body(errorMsg);
				commonSvc.sp_insextaudit(extAudit);
			} catch (Exception e) {
				log.error("Error in sp_insextaudit for getorderstatus: " + e.getMessage() + ", "
						+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
			}

			// 241204: fixed based on request for no data found
			// 250831: added duplicate check (Brian)
			return errorMsg.contains("find") ? APIResponse.NoDataFoundExternal()
					: errorMsg.contains("duplicate") ? APIResponse.DuplicateDataExternal()
							: APIResponse.InternalServerErrorExternal();
		}

		try {

			extAudit.setI_response_body("Success");
			commonSvc.sp_insextaudit(extAudit);
		} catch (Exception e) {
			log.error("Error in sp_insextaudit for getorderstatus: " + e.getMessage() + ", "
					+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
		}

		return APIResponse.SuccessResponseExternal(data);
	}

	// @Secured("ROLE_REQUESTER")
	@PostMapping(value = "/getreceiptdetails", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getReceiptDetails(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws JsonProcessingException {

		ExtAudit extAudit = new ExtAudit();
		extAudit.setI_module_nm("getreceiptdetails");
		extAudit.setI_direction("Incoming");
		extAudit.setI_rms_batch_no(null);
		String jsonBody = objectMapper.writeValueAsString(payload);
		extAudit.setI_request_body(jsonBody);

		if (!authService.isAuthenticated(request)) {
			try {

				extAudit.setI_response_body("NoPermission");
				commonSvc.sp_insextaudit(extAudit);
			} catch (Exception e) {
				log.error("Error in sp_insextaudit for getreceiptdetails: " + e.getMessage() + ", "
						+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
			}

			// return APIResponse.NoPermission(Collections.singletonMap("data",
			// Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		// if(!(payload.containsKey("orn_no") && payload.containsKey("rpt_no")) ||
		// payload.size() != 2)
		if (!(payload.containsKey("orn_no") && payload.containsKey("rpt_no")) || CollectionUtils.size(payload) != 2) {
			try {

				extAudit.setI_response_body("Key is missing, expected orn_no or rpt_no");
				commonSvc.sp_insextaudit(extAudit);
			} catch (Exception e) {
				log.error("Error in sp_insextaudit for getreceiptdetails: " + e.getMessage() + ", "
						+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
			}
			return APIResponse.InvalidFormatExternal();
		}

		if (!((payload.get("orn_no") instanceof String) && (payload.get("rpt_no") instanceof String)))
			return APIResponse.InvalidFormatExternal();

		List<Map<String, Object>> data = mttService.getReceiptDetails((String) payload.get("orn_no"),
				(String) payload.get("rpt_no"));

		if (data.get(0).containsKey("Error")) {
			String errorMsg = (String) data.get(0).get("Error");
			if (errorMsg.contains("find"))
				log.debug("Detected error when invoking getReceiptDetails in MTTController.class: "
						+ data.get(0).get("Error"));
			else if (errorMsg.contains("duplicate"))
				log.debug("Detected error when invoking getReceiptDetails in MTTController.class: "
						+ data.get(0).get("Error"));
			else
				log.error("Detected error when invoking getReceiptDetails in MTTController.class: "
						+ data.get(0).get("Error"));

			try {

				String ErrorMsg = errorMsg.contains("find")
						? "No Data Found: " + errorMsg
						: errorMsg.contains("field")
								? "Invalid Format: " + errorMsg
								: errorMsg.contains("duplicate")
										? "Duplicate Data: " + errorMsg
										: "Internal Server Error:" + errorMsg;
				extAudit.setI_response_body(ErrorMsg);
				commonSvc.sp_insextaudit(extAudit);
			} catch (Exception e) {
				log.error("Error in sp_insextaudit for getreceiptdetails: " + e.getMessage() + ", "
						+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
			}

			return errorMsg.contains("find") ? APIResponse.NoDataFoundExternal()
					: errorMsg.contains("field") ? APIResponse.InvalidFormatExternal()
							: errorMsg.contains("duplicate") ? APIResponse.DuplicateDataExternal()
									: APIResponse.InternalServerErrorExternal();

		}

		try {

			extAudit.setI_response_body("Success");
			commonSvc.sp_insextaudit(extAudit);
		} catch (Exception e) {
			log.error("Error in sp_insextaudit for getreceiptdetails: " + e.getMessage() + ", "
					+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
		}

		return APIResponse.SuccessResponseExternal(data);
	}

	// @Secured("ROLE_REQUESTER")
	@PostMapping(value = "/getreceipt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getReceipt(HttpServletRequest request, @Valid @RequestBody Map<String, Object> payload)
			throws IOException {
		// return getReceipt_v1(request, payload);

		// 20251105, this will fix Gson/Idaman API error handling
		return getReceipt_v2(request, payload);
	}

	public ResponseEntity<?> getReceipt_v1(HttpServletRequest request, Map<String, Object> payload)
			throws IOException {
		ExtAudit extAudit = new ExtAudit();
		extAudit.setI_module_nm("getreceipt");
		extAudit.setI_direction("Incoming");
		extAudit.setI_rms_batch_no(null);
		String jsonBody = objectMapper.writeValueAsString(payload);
		extAudit.setI_request_body(jsonBody);

		if (!authService.isAuthenticated(request)) {
			try {
				extAudit.setI_response_body("NoPermission");
				commonSvc.sp_insextaudit(extAudit);
			} catch (Exception e) {
				log.error("Error in sp_insextaudit for getreceipt: " + e.getMessage() + ", "
						+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
			}
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		if (!(payload.containsKey("orn_no") && payload.containsKey("rpt_no")) || CollectionUtils.size(payload) != 2) {
			try {
				extAudit.setI_response_body("Key is missing, expected orn_no or rpt_no");
				commonSvc.sp_insextaudit(extAudit);
			} catch (Exception e) {
				log.error("Error in sp_insextaudit for getreceipt: " + e.getMessage() + ", "
						+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
			}
			return APIResponse.InvalidFormatExternal();
		}

		if (!((payload.get("orn_no") instanceof String) && (payload.get("rpt_no") instanceof String)))
			return APIResponse.InvalidFormatExternal();

		List<Map<String, Object>> data = mttService.getReceipt((String) payload.get("orn_no"),
				(String) payload.get("rpt_no"));

		if (data.get(0).containsKey("Error")) {
			String errorMsg = (String) data.get(0).get("Error");
			if (errorMsg.contains("find"))
				log.debug("Detected error when invoking getReceipt in MTTController.class: "
						+ data.get(0).get("Error"));
			else if (errorMsg.contains("duplicate"))
				log.debug("Detected error when invoking getReceipt in MTTController.class: "
						+ data.get(0).get("Error"));
			else
				log.error("Detected error when invoking getReceipt in MTTController.class: "
						+ data.get(0).get("Error"));
			try {
				String ErrorMsg = errorMsg.contains("find") || errorMsg.contains("grab")
						? "No Data Found: " + errorMsg
						: errorMsg.contains("field") ? "Invalid Format: " + errorMsg
								: errorMsg.contains("duplicate") ? "Duplicate data: " + errorMsg
										: "Internal Server Error:" + errorMsg;
				extAudit.setI_response_body(ErrorMsg);
				commonSvc.sp_insextaudit(extAudit);
			} catch (Exception e) {
				log.error("Error in sp_insextaudit for getreceipt: " + e.getMessage() + ", "
						+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
			}
			return errorMsg.contains("find") || errorMsg.contains("grab") ? APIResponse.NoDataFoundExternal()
					: errorMsg.contains("field") ? APIResponse.InvalidFormatExternal()
							: errorMsg.contains("duplicate") ? APIResponse.DuplicateDataExternal()
									: APIResponse.InternalServerErrorExternal();
		}

		try {
			extAudit.setI_response_body("Success");
			commonSvc.sp_insextaudit(extAudit);
		} catch (Exception e) {
			log.error("Error in sp_insextaudit for getreceipt: " + e.getMessage() + ", "
					+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
		}

		return APIResponse.SuccessResponseExternal(data);
	}

	public ResponseEntity<?> getReceipt_v2(HttpServletRequest request, Map<String, Object> payload)
			throws IOException {

		// Initialize audit object
		ExtAudit extAudit = new ExtAudit();
		extAudit.setI_module_nm("getreceipt");
		extAudit.setI_direction("Incoming");
		extAudit.setI_rms_batch_no(null);
		String jsonBody = objectMapper.writeValueAsString(payload);
		extAudit.setI_request_body(jsonBody);

		try {
			// ✅ Step 1: Authentication check
			if (!authService.isAuthenticated(request)) {
				return buildAuditResponse(extAudit, "NoPermission",
						ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
			}

			// ✅ Step 2: Validate payload structure
			if (!(payload.containsKey("orn_no") && payload.containsKey("rpt_no"))
					|| CollectionUtils.size(payload) != 2) {
				return buildAuditResponse(extAudit, "Key is missing, expected orn_no or rpt_no",
						APIResponse.InvalidFormatExternal());
			}

			// ✅ Step 3: Validate payload types
			if (!((payload.get("orn_no") instanceof String) && (payload.get("rpt_no") instanceof String))) {
				return buildAuditResponse(extAudit, "Invalid payload types",
						APIResponse.InvalidFormatExternal());
			}

			// ✅ Step 4: Execute main business logic (Gson error can occur here)
			String ornNo = (String) payload.get("orn_no");
			String rptNo = (String) payload.get("rpt_no");

			List<Map<String, Object>> data = mttService.getReceipt(ornNo, rptNo);

			// ✅ Step 5: Handle error responses from service
			if (!data.isEmpty() && data.get(0).containsKey("Error")) {
				String errorMsg = (String) data.get(0).get("Error");

				// Log based on error type
				if (errorMsg.contains("find") || errorMsg.contains("grab")) {
					log.debug("No data found in getReceipt for orn_no {} rpt_no {}: {}", ornNo, rptNo, errorMsg);
				} else if (errorMsg.contains("duplicate")) {
					log.debug("Duplicate data in getReceipt for orn_no {} rpt_no {}: {}", ornNo, rptNo, errorMsg);
				} else {
					log.error("Error in getReceipt for orn_no {} rpt_no {}: {}", ornNo, rptNo, errorMsg);
				}

				// Build error message for audit
				String auditErrorMsg = errorMsg.contains("find") || errorMsg.contains("grab")
						? "No Data Found: " + errorMsg
						: errorMsg.contains("field")
								? "Invalid Format: " + errorMsg
								: errorMsg.contains("duplicate")
										? "Duplicate data: " + errorMsg
										: "Internal Server Error: " + errorMsg;

				// Return appropriate error response with audit
				ResponseEntity<?> errorResponse = errorMsg.contains("find") || errorMsg.contains("grab")
						? APIResponse.NoDataFoundExternal()
						: errorMsg.contains("field")
								? APIResponse.InvalidFormatExternal()
								: errorMsg.contains("duplicate")
										? APIResponse.DuplicateDataExternal()
										: APIResponse.InternalServerErrorExternal();

				return buildAuditResponse(extAudit, auditErrorMsg, errorResponse);
			}

			// ✅ Step 6: Success response
			return buildAuditResponse(extAudit, "Success",
					APIResponse.SuccessResponseExternal(data));

		} catch (com.google.gson.JsonSyntaxException e) {
			// ✅ CONFIRMED: Gson parsing errors → InternalServerErrorExternal
			log.error("JSON parsing error in getReceipt for orn_no {} rpt_no {}: API returned invalid JSON - {}",
					payload.get("orn_no"), payload.get("rpt_no"), e.getMessage(), e);
			return buildAuditResponse(extAudit,
					"API Response Error: Invalid JSON format received from external service",
					APIResponse.InternalServerErrorExternal());

		} catch (IOException e) {
			// ✅ CONFIRMED: IO exceptions (Idaman API failures) →
			// InternalServerErrorExternal
			log.error("IOException in getReceipt for orn_no {} rpt_no {}: {}",
					payload.get("orn_no"), payload.get("rpt_no"), e.getMessage(), e);
			return buildAuditResponse(extAudit, "IO Error: " + e.getMessage(),
					APIResponse.InternalServerErrorExternal());

		} catch (Exception e) {
			// ✅ CONFIRMED: All unexpected exceptions → InternalServerErrorExternal
			log.error("Unexpected error in getReceipt for orn_no {} rpt_no {}: {}",
					payload.get("orn_no"), payload.get("rpt_no"), e.getMessage(), e);
			return buildAuditResponse(extAudit, "Error: " + e.getMessage(),
					APIResponse.InternalServerErrorExternal());
		}
	}

	// scheduler
	@PostMapping(value = "/getemailpp")
	public ResponseEntity<ApiResponse<List<EmailPP>>> getemailpp(
			HttpServletRequest request,
			@RequestBody Map<String, Object> requestBody) {

		if (!authService.isAuthenticated(request)) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		// Extract the value and cast it to Integer
		Integer mttID = (Integer) requestBody.get("mttID");

		if (mttID == null) {
			return ResponseEntity.badRequest().build();
		}

		List<EmailPP> result = mttService.sp_getemailpp(mttID);

		if (result.isEmpty()) {
			return APIResponse.InternalServerError();
		}
		return APIResponse.SuccessResponse(result);
	}

}
