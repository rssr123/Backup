package com.maven.rms.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.compress.utils.IOUtils;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.maven.rms.models.RICPAgingReportRequest;
import com.maven.rms.models.UnmatchedAgingReportRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.models.ReportRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.RICPAgRepReqService;
import com.maven.rms.services.UnAgRepReqService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.reports.PaymentCollectionReportGenerator;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/report/v1")
@Slf4j
public class ReportController {
	//private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
	
	@Autowired
	private AuthService authService;
	@Autowired
	private PaymentCollectionReportGenerator pcrG;
	@Autowired
	private UnAgRepReqService uarrSvc;
	@Autowired
	private RICPAgRepReqService rarRSvc;
	
	@Value("${jasper.reports.directory}")
	private String report_directory;

	public ReportController() {
		log.info("ReportController services is started");
	}
	
	@PostMapping(value = "/pymt_col_report", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void paymentCollectionReport(HttpServletResponse response, HttpServletRequest request,
										@Valid @RequestBody Map<String, Object> payload) throws IOException, JRException, SQLException{
		// try {
			if (!authService.isAuthenticated(request))
				return;
		// }catch (NumberFormatException e) {
		// 	return;
		// } catch (Exception e) {
		// 	return;   
		// } 
		
		if(!payload.containsKey("i_start_date") || !payload.containsKey("i_end_date")
			|| !payload.containsKey("i_report_type") || !payload.containsKey("i_report_format"))
			return;

		int startMonth = Integer.parseInt(((String)payload.get("i_start_date")).substring(5,7));
		int endMonth = Integer.parseInt(((String)payload.get("i_end_date")).substring(5,7));
		String fileExt = ((String)payload.get("i_report_format"));
		String contentType = fileExt.equals("pdf") ? "application/pdf" :
			fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
			fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
			"application/octet-stream";
		String reportType = ((String)payload.get("i_report_type")).equals("pymt_col_pymt_md") ? "Payment Mode" :
			((String)payload.get("i_report_type")).equals("pymt_col_s_s") ? "Source System" :
			((String)payload.get("i_report_type")).equals("pymt_col_fee_dt_id") ? "Fee Detail ID" : 
			((String)payload.get("i_report_type"));
		String startMonthString = new DateFormatSymbols().getMonths()[startMonth-1];
		String endMonthString = new DateFormatSymbols().getMonths()[endMonth-1];
		
		String fileName = "Payment Collection - " + reportType + " " 
							+ startMonthString + " - " + endMonthString + " " 
							+ ((String)payload.get("i_start_date")).substring(0,4) + "." + fileExt;
		File report = pcrG.generateReport(new ReportRequest(fileName, reportType, (String)payload.get("i_end_date"),
				(String)payload.get("i_start_date"), fileExt, payload.containsKey("i_pymt_mthd") 
				? (String)payload.get("i_pymt_mthd") : null));
		
		if(report != null) {
			response.setContentType(contentType);
			response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
			response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
							
			IOUtils.copy(new FileInputStream(report), response.getOutputStream());
			response.flushBuffer();
		}
		else {
			response.setContentType("application/json");
		    response.setCharacterEncoding("UTF-8");
		    
			Map<String, String> data = new HashMap<String, String>();
			data.put("status", "401");	//No Data Found
		    
		    response.getWriter().write(new Gson().toJson(data));
		    response.getWriter().flush();
		    response.getWriter().close();
		}
			
	}

	@PostMapping(value = "/aging/download_report", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void downloadAgingReport(HttpServletResponse response, HttpServletRequest request,
	@Valid @RequestBody Map<String, Object> payload) throws IOException{
		// try {
			if (!authService.isAuthenticated(request))
				return;
		// }catch (NumberFormatException e) {
		// 	return;
		// } catch (Exception e) {
		// 	return;   
		// } 
		
		if(!payload.containsKey("i_file_name") || !payload.containsKey("i_report_format"))
			return;
		
		String fileExt = ((String)payload.get("i_report_format"));
		String contentType = fileExt.equals("pdf") ? "application/pdf" :
			fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
			fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
			"application/octet-stream";

		String fileName = (String)payload.get("i_file_name");
		
		File report = new File(report_directory + fileName);

		response.setContentType(contentType);
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
		
		IOUtils.copy(new FileInputStream(report), response.getOutputStream());
		response.flushBuffer();
	}
	
	@PostMapping(value = "/unmatched_aging/listing", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<Object>> listingUnmatchedAgingReport(HttpServletRequest request, 
			@Valid @RequestBody Map<String, Object> payload) throws IOException{
		// try {
			if (!authService.isAuthenticated(request))
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		// }catch (NumberFormatException e) {
		// 	return APIResponse.InvalidFormat();
		// } catch (Exception e) {
		// 	return APIResponse.InternalServerError();   
		// } 
		
		if(!payload.containsKey("i_page") || !payload.containsKey("i_size"))
			APIResponse.InvalidFormat();
		
		List<UnmatchedAgingReportRequest> result = uarrSvc.getListOfRequests((Integer)payload.get("i_page")
				, (Integer)payload.get("i_size"));
 
        if (result.isEmpty()) 
            return APIResponse.NoDataFound(ControllersEnum.REPORT_CONTROLLER);
        
        Collections.reverse(result);
        Map<String, Object> data = new HashMap<>();
        data.put("total", uarrSvc.getTotalRecordsFromUmAgeRReq());
        data.put("data", result);

        return APIResponse.SuccessResponse(data);
	}

	@PostMapping(value = "/unmatched_aging/req_report", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<Object>> requestUnmatchedAgingReport(HttpServletRequest request, 
			@Valid @RequestBody Map<String, Object> payload) throws IOException{
		// try {
			if (!authService.isAuthenticated(request))
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		// }catch (NumberFormatException e) {
		// 	return APIResponse.InvalidFormat();
		// } catch (Exception e) {
		// 	return APIResponse.InternalServerError();   
		// } 
		
		if(!payload.containsKey("i_req_date") || !payload.containsKey("i_recon_status")
			|| !payload.containsKey("i_pg_txn_id") || !payload.containsKey("i_rcpt_no")
			|| !payload.containsKey("i_rc_pg_stmt_no") || !payload.containsKey("i_stmt_dt_fr")
			|| !payload.containsKey("i_stmt_dt_to") || !payload.containsKey("i_dup_flag")
			|| !payload.containsKey("i_email") || !payload.containsKey("i_file_type"))
			return APIResponse.InvalidFormat();
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
		        .appendPattern("yyyy-MM-dd[ HH:mm:ss]")
		        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
		        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
		        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
		        .toFormatter();
		
		int returnCode = uarrSvc.addJobToTable(new UnmatchedAgingReportRequest(
				LocalDateTime.parse((String)payload.get("i_req_date"), formatter)
				, (String)payload.get("i_recon_status")
				, (String)payload.get("i_pg_txn_id")
				, (String)payload.get("i_rcpt_no")
				, (String)payload.get("i_rc_pg_stmt_no")
				, ((String)payload.get("i_stmt_dt_fr")).isEmpty() ? null : LocalDateTime.parse((String)payload.get("i_stmt_dt_fr"), formatter)
				, ((String)payload.get("i_stmt_dt_to")).isEmpty() ? null : LocalDateTime.parse((String)payload.get("i_stmt_dt_to"), formatter)
				, (Integer)payload.get("i_dup_flag")
				, (Integer)payload.get("i_email") == 1 ? authService.getUserEmail() : ""
				, (String)payload.get("i_file_type")
				, authService.getLoginUserName()));
		
		return APIResponse.SuccessResponse(returnCode);
	}
	
	@PostMapping(value = "/unmatched_aging/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<Object>> cancelUnmatchedAgingReport(HttpServletRequest request, 
			@Valid @RequestBody Map<String, Object> payload) throws IOException{
		// try {
			if (!authService.isAuthenticated(request))
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		// }catch (NumberFormatException e) {
		// 	return APIResponse.InvalidFormat();
		// } catch (Exception e) {
		// 	return APIResponse.InternalServerError();   
		// } 
		
		if(!payload.containsKey("i_req_id"))
			return APIResponse.InvalidFormat();
		
		int code = 0;
		UnmatchedAgingReportRequest rq = uarrSvc.getTask((Integer)payload.get("i_req_id"));
		if (rq != null){
			rq.setStatus("C");
			code = uarrSvc.updateStatus(rq);
		}
		
		return APIResponse.SuccessResponse(code);
	}
	
	@PostMapping(value = "/ricp_aging/listing", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<Object>> listingRICPAgingReport(HttpServletRequest request, 
			@Valid @RequestBody Map<String, Object> payload) throws IOException{
		// try {
			if (!authService.isAuthenticated(request))
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		// }catch (NumberFormatException e) {
		// 	return APIResponse.InvalidFormat();
		// } catch (Exception e) {
		// 	return APIResponse.InternalServerError();   
		// } 
		
		if(!payload.containsKey("i_page") || !payload.containsKey("i_size"))
			APIResponse.InvalidFormat();
		
		List<RICPAgingReportRequest> result = rarRSvc.getListOfRequests((Integer)payload.get("i_page")
				, (Integer)payload.get("i_size"));
 
        if (result.isEmpty()) 
            return APIResponse.NoDataFound(ControllersEnum.REPORT_CONTROLLER);
        
        Collections.reverse(result);
        Map<String, Object> data = new HashMap<>();
        data.put("total", rarRSvc.getTotalRecordsFromRICPAgeRReq());
        data.put("data", result);

        return APIResponse.SuccessResponse(data);
	}
	
	@PostMapping(value = "/ricp_aging/req_report", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<Object>> requestRICPAgingReport(HttpServletRequest request, 
			@Valid @RequestBody Map<String, Object> payload) throws IOException{
		// try {
			if (!authService.isAuthenticated(request))
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		// }catch (NumberFormatException e) {
		// 	return APIResponse.InvalidFormat();
		// } catch (Exception e) {
		// 	return APIResponse.InternalServerError();   
		// } 
		
		if(!payload.containsKey("i_req_date") || !payload.containsKey("i_dt_iss_fr")
			|| !payload.containsKey("i_dt_iss_to") || !payload.containsKey("i_exp_status")
			|| !payload.containsKey("i_can_v_status") || !payload.containsKey("i_dt_rcpt_fr")
			|| !payload.containsKey("i_dt_rcpt_to") || !payload.containsKey("i_dt_exp_fr")
			|| !payload.containsKey("i_dt_exp_to") || !payload.containsKey("i_dt_wo_fr")
			|| !payload.containsKey("i_dt_wo_to") || !payload.containsKey("i_dt_can_fr")
			|| !payload.containsKey("i_dt_can_to") || !payload.containsKey("i_dt_void_fr")
			|| !payload.containsKey("i_dt_void_to") || !payload.containsKey("i_ent_ty")
			|| !payload.containsKey("i_ent_nm") || !payload.containsKey("i_email")
			|| !payload.containsKey("i_file_type"))
			return APIResponse.InvalidFormat();
		DateTimeFormatter formatter = new DateTimeFormatterBuilder()
		        .appendPattern("yyyy-MM-dd[ HH:mm:ss]")
		        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
		        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
		        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
		        .toFormatter();
		
		RICPAgingReportRequest req = new RICPAgingReportRequest(
				LocalDateTime.parse((String)payload.get("i_req_date"), formatter)
				, LocalDateTime.parse((String)payload.get("i_dt_iss_fr"), formatter)
				, LocalDateTime.parse((String)payload.get("i_dt_iss_to"), formatter)
				, (Integer)payload.get("i_exp_status")
				, (Integer)payload.get("i_can_v_status")
				, authService.getLoginUserName()
				, (String)payload.get("i_file_type"));
		req.setP_ent_ty((String)payload.get("i_ent_ty"));
		req.setP_ent_nm((String)payload.get("i_ent_nm"));
		req.setP_dt_rcpt_fr(((String)payload.get("i_dt_rcpt_fr")).isEmpty() ? null : LocalDateTime.parse((String)payload.get("i_dt_rcpt_fr"), formatter));
		req.setP_dt_rcpt_to(((String)payload.get("i_dt_rcpt_to")).isEmpty() ? null : LocalDateTime.parse((String)payload.get("i_dt_rcpt_to"), formatter));
		req.setP_dt_exp_fr(((String)payload.get("i_dt_exp_fr")).isEmpty() ? null : LocalDateTime.parse((String)payload.get("i_dt_exp_fr"), formatter));
		req.setP_dt_exp_to(((String)payload.get("i_dt_exp_to")).isEmpty() ? null : LocalDateTime.parse((String)payload.get("i_dt_exp_to"), formatter));
		req.setP_dt_wo_fr(((String)payload.get("i_dt_wo_fr")).isEmpty() ? null : LocalDateTime.parse((String)payload.get("i_dt_wo_fr"), formatter));
		req.setP_dt_wo_to(((String)payload.get("i_dt_wo_to")).isEmpty() ? null : LocalDateTime.parse((String)payload.get("i_dt_wo_to"), formatter));
		req.setP_dt_can_fr(((String)payload.get("i_dt_can_fr")).isEmpty() ? null : LocalDateTime.parse((String)payload.get("i_dt_can_fr"), formatter));
		req.setP_dt_can_to(((String)payload.get("i_dt_can_to")).isEmpty() ? null : LocalDateTime.parse((String)payload.get("i_dt_can_to"), formatter));
		req.setP_dt_void_fr(((String)payload.get("i_dt_void_fr")).isEmpty() ? null : LocalDateTime.parse((String)payload.get("i_dt_void_fr"), formatter));
		req.setP_dt_void_to(((String)payload.get("i_dt_void_to")).isEmpty() ? null : LocalDateTime.parse((String)payload.get("i_dt_void_to"), formatter));
		req.setP_email((Integer)payload.get("i_email") == 1 ? authService.getUserEmail() : "");
		
		int returnCode = rarRSvc.addJobToTable(req);
		
		return APIResponse.SuccessResponse(returnCode);
	}
	
	@PostMapping(value = "/ricp_aging/cancel", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<Object>> cancelRICPAgingReport(HttpServletRequest request, 
			@Valid @RequestBody Map<String, Object> payload) throws IOException{
		// try {
			if (!authService.isAuthenticated(request))
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		// }catch (NumberFormatException e) {
		// 	return APIResponse.InvalidFormat();
		// } catch (Exception e) {
		// 	return APIResponse.InternalServerError();   
		// } 
		
		if(!payload.containsKey("i_req_id"))
			return APIResponse.InvalidFormat();
		
		int code = 0;
		RICPAgingReportRequest rq = rarRSvc.getTask((Integer)payload.get("i_req_id"));
		if (rq != null){
			rq.setStatus("C");
			code = rarRSvc.updateStatus(rq);
		}
		
		return APIResponse.SuccessResponse(code);
	}
}
