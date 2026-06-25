package com.maven.rms.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
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

import com.maven.rms.models.ReportRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.reports.PaymentCollectionReportGenerator;
import com.maven.rms.utils.reports.ReportReconAndAccountGenerator;
import com.maven.rms.utils.reports.ReportUTLGenerator;


@RestController
@RequestMapping("/api/reportutl/v1")
@Slf4j
public class ReportUTLController {
	//private static final Logger logger = LoggerFactory.getLogger(ReportController.class);
	
	@Autowired
	private AuthService authService;
	
    @Autowired
    private ReportUTLGenerator rutlG;

	
	public ReportUTLController() {
		log.info("ReportController services is started");
	}

    //ut listing report
	@PostMapping(value = "/ut-listing", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void dailyCollectionListingReport(HttpServletResponse response, HttpServletRequest request,
										@Valid @RequestBody Map<String, Object> dailyload) throws IOException, JRException, SQLException{
		try {
			if (!authService.isAuthenticated(request))
				return;
		}catch (NumberFormatException e) {
			return;
		} catch (Exception e) {
			return;   
		} 
		
		
        if(!dailyload.containsKey("i_orn_no") || 
            !dailyload.containsKey("i_sub_criteria")|| 
            !dailyload.containsKey("i_rcpt_no") || 
            !dailyload.containsKey("i_pg_txn_id") ||
            !dailyload.containsKey("i_stmt_no"))
            return;

		String ornNo = ((String)dailyload.get("i_orn_no"));
        String subCriteria = ((String)dailyload.get("i_sub_criteria"));
        String rcptNo = ((String)dailyload.get("i_rcpt_no"));
        String pgTxnId = ((String)dailyload.get("i_pg_txn_id"));
        String stmtNo = ((String)dailyload.get("i_stmt_no"));
		String fileExt = ((String)dailyload.get("i_report_format"));
		String contentType = fileExt.equals("pdf") ? "application/pdf" :
			fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
			fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
			"application/octet-stream";
	
		String fileName = "Unmatched Transaction Listing - " +  "." + fileExt;
		
		response.setContentType(contentType);
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
		
		
		
		File report = rutlG.generateReport(new ReportRequest(
				fileName, (String)dailyload.get("i_orn_no"),
		        (String)dailyload.get("i_sub_criteria"),
		        (String)dailyload.get("i_rcpt_no"),
		        (String)dailyload.get("i_pg_txn_id"),
		        (String)dailyload.get("i_stmt_no"),
		        fileExt));
		
		IOUtils.copy(new FileInputStream(report), response.getOutputStream());
		response.flushBuffer();
	}
	
}
