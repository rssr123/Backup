package com.maven.rms.controllers;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.OTCCheckInRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.OTCService;
import com.maven.rms.utils.APIResponse;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/otc/v1")
@Slf4j
public class OTCController {
	@Autowired
	private AuthService authService;
	@Autowired
	private OTCService otcSvc;

	public OTCController() {
		log.info("OTCController has started");
	}

	@GetMapping(value = "/checkinlist")
	public ResponseEntity<?> checkInOtcListing(HttpServletRequest request) throws IOException {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		return APIResponse.SuccessResponseExternal(otcSvc.otcCheckInList(authService.getLoginUserName()));
	}
	
	/*@GetMapping(value = "/checkinstatus", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> checkInOtcCounterStatus(HttpServletRequest request) throws IOException {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		
		String returnUUID = "";
		HttpSession ses = request.getSession(false);
    	if(ses != null) {
    		returnUUID = (String) ses.getAttribute("otc_session_id");
    		if(returnUUID == null || returnUUID.isEmpty()) {
    			log.debug("Detected error when invoking checkInOtcCounterStatus in OTCController.class: "
    					+ "Cannot find otc_session_id attribute in session!");
    			return APIResponse.InternalServerErrorExternal();
    		}
    	}
    	else {
			log.error("Detected error when invoking checkInOtcCounterStatus in OTCController.class: "
					+ "Cannot find session!");
			return APIResponse.InternalServerErrorExternal();
    	}
		return APIResponse.SuccessResponseExternal(otcSvc.otcCheckInStatus(returnUUID, authService.getLoginUserName()));
	}*/
	
	@PostMapping(value = "/checkinstatus", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> checkInOtcCounterStatus(HttpServletRequest request, @Valid @RequestBody Map<String, Object> payload) throws IOException {
 
		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
 
		if(!payload.containsKey("i_session_id") || CollectionUtils.size(payload) != 1)
			return APIResponse.InvalidFormatExternal();
 
		if(!(payload.get("i_session_id") instanceof String || payload.get("i_session_id") == null))
			return APIResponse.InvalidFormatExternal();
		
		return APIResponse.SuccessResponseExternal(otcSvc.otcCheckInStatus((String)payload.get("i_session_id"), 
													authService.getLoginUserName()));
	}

	@PostMapping(value = "/checkin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> checkInOtcCounter(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws IOException {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if (!payload.containsKey("i_counter_id") || !payload.containsKey("i_branch_cd")
				|| CollectionUtils.size(payload) != 2)
			return APIResponse.InvalidFormatExternal();

		if (!(payload.get("i_counter_id") instanceof String) || !(payload.get("i_branch_cd") instanceof String))
			return APIResponse.InvalidFormatExternal();
		String returnUUID = java.util.UUID.randomUUID().toString();//(String)request.getAttribute("javax.servlet.request.ssl_session_id");
		/*HttpSession ses = request.getSession(false);
    	if(ses != null) 
    		ses.setAttribute("otc_session_id", returnUUID);
    	else {
			log.error("Detected error when invoking checkInOtcCounter in OTCController.class: "
					+ "Cannot find the session to inject the otc_session_id!");
			return APIResponse.InternalServerErrorExternal();
    	}*/
    		
		int statusCode = otcSvc.otcCheckIn(new OTCCheckInRequest(returnUUID, // request.getSession().getId(),
				authService.getLoginUserName(), authService.getUserEmail(),
				(String) payload.get("i_counter_id"), (String) payload.get("i_branch_cd")));

		if (statusCode == -1) {
			log.error("Detected error when invoking checkInOtcCounter in OTCController.class: "
					+ "Unknown Branch Code");
			return APIResponse.InternalServerErrorExternal();
		} else if (statusCode == -2) {
			log.error("Detected error when invoking checkInOtcCounter in OTCController.class: "
					+ "Unknown Counter ID");
			return APIResponse.InternalServerErrorExternal();
		} else if (statusCode == -3) {
			log.error("Detected error when invoking checkInOtcCounter in OTCController.class: "
					+ "Unknown Branch Code AND Counter ID");
			return APIResponse.InternalServerErrorExternal();
		} else if (statusCode == -4) {
			log.debug("Detected error when invoking checkInOtcCounter in OTCController.class: "
					+ "Counter is already OPEN!");
			return APIResponse
					.InternalServerErrorExternal(otcSvc.sp_getotccheckedininfo((String) payload.get("i_counter_id")));
		} else if (statusCode == -5) {
			log.debug("Detected error when invoking checkInOtcCounter in OTCController.class: "
					+ "User is already checked in!");
			return APIResponse
					.InternalServerErrorExternal(otcSvc.sp_getotccheckedinuserinfo(authService.getLoginUserName()));
		} else if (statusCode == -6) {
			log.debug("Detected error when invoking checkInOtcCounter in OTCController.class: "
					+ "Check in blocked due to Branch Manager Balancing Complete / In-Progress or Daily Balancing In-Progress!");
			Map<String, String> map = new HashMap<String, String>();
			map.put("check_in_blocked", Integer.toString(statusCode));
			return APIResponse.InternalServerErrorExternal(map);
		} else if (statusCode <= 0) {
			log.error("Detected error when invoking checkInOtcCounter in OTCController.class: "
					+ "Insert error! Status code is: " + Integer.toString(statusCode));
			return APIResponse.InternalServerErrorExternal();
		}
		
		return APIResponse.SuccessResponseExternal(returnUUID);
	}

	@PostMapping(value = "/checkout", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> checkOutOtcCounter(HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> payload) throws IOException {

		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission(Collections.singletonMap("data", Collections.emptyList()));
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

		if((!payload.containsKey("i_counter_id") && !payload.containsKey("i_session_id")) || CollectionUtils.size(payload) != 2)
			return APIResponse.InvalidFormatExternal();
		if(!(payload.get("i_counter_id") instanceof String) && !(payload.get("i_session_id") instanceof String))
			return APIResponse.InvalidFormatExternal();
		
		/*String returnUUID = "";
		HttpSession ses = request.getSession(false);
    	if(ses != null) {
    		returnUUID = (String) ses.getAttribute("otc_session_id");
    		if(returnUUID == null || returnUUID.isEmpty()) {
    			log.debug("Detected error when invoking checkOutOtcCounter in OTCController.class: "
    					+ "Cannot find otc_session_id attribute in session!");
    			return APIResponse.InternalServerErrorExternal();
    		}
    	}
    	else {
			log.error("Detected error when invoking checkOutOtcCounter in OTCController.class: "
					+ "Cannot find session!");
			return APIResponse.InternalServerErrorExternal();
    	}*/
    		
		int statusCode = otcSvc.otcCheckOut((String) payload.get("i_counter_id"), 
				authService.getLoginUserName(),(String)payload.get("i_session_id"));

		if (statusCode == 0) {
			log.error("Detected error when invoking checkOutOtcCounter in OTCController.class: "
					+ "Cannot find open counter!");
			return APIResponse.InternalServerErrorExternal();
		} else if (statusCode < 0) {
			log.error("Detected error when invoking checkOutOtcCounter in OTCController.class: "
					+ "Insert error! Status code is: " + Integer.toString(statusCode));
			return APIResponse.InternalServerErrorExternal();
		}
		return APIResponse.SuccessResponseExternal(statusCode == 2 ? "checkout" : "counterbalancing");
	}

	// scheduler

	@PostMapping(value = "/open-counter", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getOtcOpenCtr() {
		try {
			List<HashMap<String, String>> result = otcSvc.sp_getOtcOpenCtr();
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return APIResponse.InternalServerErrorExternal();
		}
	}

	// scheduler
	@PostMapping(value = "/otccheckout", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getotccheckout() {
		try {
			List<HashMap<String, String>> result = otcSvc.sp_getotccheckout();
			return ResponseEntity.ok(result);
		} catch (Exception e) {
			return APIResponse.InternalServerErrorExternal();
		}
	}

}
