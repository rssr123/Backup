package com.maven.rms.controllers;

import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.RMSUser;
import com.maven.rms.models.payload.requests.UAMRequestPayload;
import com.maven.rms.models.payload.requests.UAMRequestRequest;
import com.maven.rms.models.payload.responses.UAMResponseResponse;
import com.maven.rms.models.payload.responses.UAMResponseUserRoles;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.CommonService;
import com.maven.rms.services.SSM4UAPI;
import com.maven.rms.services.UAMService;
import com.maven.rms.utils.APIResponse;

import lombok.extern.slf4j.Slf4j;

import com.maven.rms.models.payload.responses.UAMResponseGetRoles;
import com.maven.rms.models.payload.responses.UAMResponsePayload;

@Slf4j
@RestController
@RequestMapping("/api/uam/v1")
public class UAMController {
	//private static final log log = logFactory.getlog(UAMController.class);
	
	@Autowired
	private UAMService uamService;
	@Autowired
    private AuthService authService;
	@Autowired
	private SSM4UAPI ssmApi;    
	
	@Autowired
	private CommonService commonSvc;

	private ObjectMapper objectMapper = new ObjectMapper();

	/*
	 * For Future header authentication
	 * 
	 * @Autowired
	 * private HttpServletRequest request;
	 */

	public UAMController() {
		log.info("UAMController services is started");
	}

	// @Secured("ROLE_REQUESTER")
	@PostMapping(value = "/getRoles", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> GetRoles(HttpServletRequest request, @Valid @RequestBody UAMRequestPayload jsonPost) {
		try {
			if (!authService.isAuthenticated(request))
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}catch (NumberFormatException e) {
			log.error("Exception in " + this.getClass().toString(), e);
			logRequestResponse("getRoles", jsonPost, "Exception in UAMController getRoles " + this.getClass().toString(), "");
			return APIResponse.InvalidFormat();
		} catch (Exception e) {
			log.error("Exception in " + this.getClass().toString(), e);
			logRequestResponse("getRoles", jsonPost, "Exception in UAMController getRoles " + this.getClass().toString(), "");
			return APIResponse.InternalServerErrorExternal();   
		} 
		
		UAMResponsePayload resp = new UAMResponsePayload(jsonPost.getHeader());
		resp.setRoles(UAMResponseGetRoles.load(uamService.sp_getRole()));

		logRequestResponse("getRoles", jsonPost, resp.getRoles(), "");
		return ResponseEntity.ok(resp);
	}

	// @Secured({"ROLE_SUPERADMIN", "ROLE_ADMIN"})
	@PostMapping(value = "/createAccount", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> createAccount(HttpServletRequest request, @Valid @RequestBody UAMRequestPayload jsonPost) {
		try {
			if (!authService.isAuthenticated(request))
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}catch (NumberFormatException e) {
			log.error("Exception in " + this.getClass().toString(), e);
			logRequestResponse("createAccount", jsonPost, "Exception in UAMController createAccount " + this.getClass().toString(), "");
			return APIResponse.InvalidFormat();
		} catch (Exception e) {
			log.error("Exception in " + this.getClass().toString(), e);
			logRequestResponse("createAccount", jsonPost, "Exception in UAMController createAccount " + this.getClass().toString(), "");
			return APIResponse.InternalServerErrorExternal();   
		} 

		UAMRequestRequest reqData = jsonPost.getRequest();
		UAMResponsePayload resp = new UAMResponsePayload(jsonPost.getHeader());    	
		
		//Set default to GENERAL_USER if no role is given
		//String roles = reqData.getRole() == null || reqData.getRole().length() == 0 ? "GENERAL_USER" : reqData.getRole();
		
		if(reqData.getRole().length() < 1){
			logRequestResponse("createAccount", jsonPost, "Invalid Format", "");
			return APIResponse.InvalidFormat();
		}

		
		String ssm4uRefNo = reqData.getID().length() > 0 ? reqData.getID().replace(" ", "_") : null;
		String name = reqData.getName();
		
		if(ssm4uRefNo == (null)) {
			okhttp3.Response respo = ssmApi.getUserProfile(null, reqData.getEmail(), null);

			String dataBody = "";
			if (resp != null) {
				try {
					dataBody = respo.body().string().replace("\"", "'").replace("\\", "");
					if (!dataBody.contains("userStatus")) {
						log.error("Could not find the valid fields from the return JSON from 'getUserProfile'. "
						+ "Possibly returned error. Return data doesn't have the correct fields!\nReturn data: " + dataBody,
								dataBody);

						logRequestResponse("createAccount", jsonPost, "No Data Found", "");
			            return APIResponse.NoDataFoundExternal();
					}
					
					if(dataBody.contains("name':'"))
						name = dataBody.split("name':'")[1].split("'")[0];
					if(dataBody.contains("ssm4uUserRefNo':'")){
						ssm4uRefNo = dataBody.split("ssm4uUserRefNo':'")[1].split("'")[0];
					}
				} catch (Exception e) {
					log.error("Exception in " + this.getClass().toString(), e);
				}
			}
		}
		
		//Replace whitespace with underscore for IDs if spaces exist
		String insertStatus = uamService.sp_createAccount(
				new RMSUser(ssm4uRefNo, name, reqData.getEmail(), reqData.getStatus(), "system", 1)
				, new HashSet<>(Arrays.asList(reqData.getRole().split(","))));
				//, new HashSet<>(Arrays.asList(roles.split(","))));
		
		if(!insertStatus.equals("Successfully saved user.")) {
			log.error("Detected error when invoking UAMController 'createAccount' method: " + insertStatus);
			logRequestResponse("createAccount", jsonPost, "500 Server Error", "");
			resp.setResponse(new UAMResponseResponse("500", "Server Error"));
		}
		else
			resp.setResponse(new UAMResponseResponse("200", "Success"));
		
		logRequestResponse("createAccount", jsonPost, resp.getResponse(), "");
		return ResponseEntity.ok(resp);
	}

	// @Secured({"ROLE_SUPERADMIN", "ROLE_ADMIN"})
	@PostMapping(value = "/updateAccount", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> updateAccount(HttpServletRequest request, @Valid @RequestBody UAMRequestPayload jsonPost) {
		try {
			if (!authService.isAuthenticated(request))
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}catch (NumberFormatException e) {
			log.error("Exception in " + this.getClass().toString(), e);
			logRequestResponse("updateAccount", jsonPost, "Invalid Format", "");
			return APIResponse.InvalidFormat();
		} catch (Exception e) {
			log.error("Exception in " + this.getClass().toString(), e);
			logRequestResponse("updateAccount", jsonPost, "Internal Server Error", "");
			return APIResponse.InternalServerErrorExternal();   
		} 
		
		UAMRequestRequest reqData = jsonPost.getRequest();
		UAMResponsePayload resp = new UAMResponsePayload(jsonPost.getHeader());
		
		if(reqData.getRole().length() < 1){
			logRequestResponse("updateAccount", jsonPost, "Invalid Format", "");
			return APIResponse.InvalidFormat();
		}


		String updateStatus = uamService.sp_updateAccount(
				new RMSUser(reqData.getID(), reqData.getName(),reqData.getEmail(), reqData.getStatus(), "system", 1)
				, new HashSet<>(Arrays.asList(reqData.getRole().split(","))));
		
		if(!updateStatus.equals("Successfully updated user.")) {
			log.error("Detected error when invoking UAMController 'updateAccount' method: " + updateStatus);
			logRequestResponse("updateAccount", jsonPost, "500 Server Error", "");
			resp.setResponse(new UAMResponseResponse("500", "Server Error"));
		}
		else
			resp.setResponse(new UAMResponseResponse("200", "Success"));
		
		logRequestResponse("updateAccount", jsonPost, resp.getResponse(), "");
		return ResponseEntity.ok(resp);
	}

	//@Secured({ "ROLE_SUPERADMIN", "ROLE_ADMIN" })
	@PostMapping(value = "/deleteAccount", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> deleteAccount(HttpServletRequest request, @Valid @RequestBody UAMRequestPayload jsonPost) {
		try {
			if (!authService.isAuthenticated(request))
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}catch (NumberFormatException e) {
			log.error("Exception in " + this.getClass().toString(), e);
			logRequestResponse("deleteAccount", jsonPost, "Invalid Format", "");
			return APIResponse.InvalidFormat();
		} catch (Exception e) {
			log.error("Exception in " + this.getClass().toString(), e);
			logRequestResponse("deleteAccount", jsonPost, "Internal Server Error", "");
			return APIResponse.InternalServerErrorExternal();   
		} 
		
		UAMRequestRequest reqData = jsonPost.getRequest();
		UAMResponsePayload resp = new UAMResponsePayload(jsonPost.getHeader());

		String deleteStatus = uamService.sp_deleteAccount(reqData.getID(), "system");
		if(!deleteStatus.equals("Successfully deleted user.")) {
			log.error("Detected error when invoking UAMController 'deleteAccount' method: " + deleteStatus);
			logRequestResponse("deleteAccount", jsonPost, "500 Server Error", "");
			resp.setResponse(new UAMResponseResponse("500", "Server Error"));
		}
		else
			resp.setResponse(new UAMResponseResponse("200", "Success"));
		
		logRequestResponse("deleteAccount", jsonPost, resp.getResponse(), "");
		return ResponseEntity.ok(resp);
	}

	//@Secured("ROLE_REQUESTER")
	@PostMapping(value = "/getUserRoles", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> getUserRoles(HttpServletRequest request, @Valid @RequestBody UAMRequestPayload jsonPost) {		
		try {
			if (!authService.isAuthenticated(request))
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}catch (NumberFormatException e) {
			log.error("Exception in " + this.getClass().toString(), e);
			logRequestResponse("getUserRoles", jsonPost, "Invalid Format", "");
			return APIResponse.InvalidFormat();
		} catch (Exception e) {
			log.error("Exception in " + this.getClass().toString(), e);
			logRequestResponse("getUserRoles", jsonPost, "Internal Server Error", "");
			return APIResponse.InternalServerErrorExternal();   
		} 
		
		UAMRequestRequest reqData = jsonPost.getRequest();
		UAMResponsePayload resp = new UAMResponsePayload(jsonPost.getHeader());
		resp.setUserRoles(UAMResponseUserRoles.load(uamService.sp_getUsers(reqData.getID())));

		logRequestResponse("getUserRoles", jsonPost, resp.getUserRoles(), "");

		return ResponseEntity.ok(resp);
	}

	private void logRequestResponse(String methodName, Object request, Object response, String batchNo) {
		try {
			ExtAudit extAudit = new ExtAudit();
			extAudit.setI_module_nm("UAM_" + methodName);
			extAudit.setI_request_body(request.toString());
			// extAudit.setI_request_body(objectMapper.writeValueAsString(request));
			extAudit.setI_response_body(response != null ? objectMapper.writeValueAsString(response) : "");
			extAudit.setI_direction("Incoming");
			extAudit.setI_rms_batch_no(batchNo);
			
			commonSvc.sp_insextaudit(extAudit);
		} catch (Exception e) {
			log.error("Error in sp_insextaudit for UAM " + methodName + ": " + e.getMessage() + ", "
					+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
		}
	}
}


