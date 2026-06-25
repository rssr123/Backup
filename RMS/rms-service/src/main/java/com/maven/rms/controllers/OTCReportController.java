package com.maven.rms.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
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

import com.maven.rms.services.AuthService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.ControllersEnum;
import com.maven.rms.models.payload.responses.ApiResponse;

import com.maven.rms.models.OTCReportRequest;
import com.maven.rms.utils.reports.CounterCollectionReportGenerator;
import com.maven.rms.utils.reports.DailyBalancingReportGenerator;
import com.maven.rms.utils.reports.MasterBalancingReportGenerator;
import com.maven.rms.utils.reports.OTCCollectionReportGenerator;
import com.maven.rms.utils.reports.OTCCollectionPlusReportGenerator;
import com.maven.rms.utils.reports.OTCReceiptCancellationReportGenerator;
import com.maven.rms.utils.reports.OTCReturnedChequeReport;
import com.maven.rms.utils.reports.BankInSlipReportGenerator;

@RestController
@RequestMapping("/api/otcreport/v1")
@Slf4j
public class OTCReportController {
    
    @Autowired
	private AuthService authService;

	@Autowired
	private CounterCollectionReportGenerator counterCollectionReportGenerator;

	@Autowired
	private DailyBalancingReportGenerator dailyBalancingReportGenerator;

	@Autowired
	private MasterBalancingReportGenerator masterBalancingReportGenerator;

	@Autowired
	private OTCCollectionReportGenerator otccollectionReportGenerator;

	@Autowired
	private OTCCollectionPlusReportGenerator otccollectionPlusReportGenerator;

	@Autowired
	private OTCReceiptCancellationReportGenerator otcreceiptCancellationReportGenerator;

	@Autowired
	private OTCReturnedChequeReport otcreturnedChequeReport;

	@Autowired
	private BankInSlipReportGenerator bankInSlipReportGenerator;

    @Value("${jasper.reports.directory}")
	private String report_directory;

	public OTCReportController() {
		log.info("ReportController services is started");
	}

    	@PostMapping(value = "/countercollection", consumes = MediaType.APPLICATION_JSON_VALUE)
		public void counterCollectionReport(HttpServletResponse response, HttpServletRequest request,
											@Valid @RequestBody Map<String, Object> payload) 
											throws IOException, JRException, SQLException {

			if (!authService.isAuthenticated(request)) {
				return;
			}

			if (!payload.containsKey("i_report_format")
				|| !payload.containsKey("i_date_from") 
				|| !payload.containsKey("i_date_to")
				) {
				return;
			}

			Object reportFormatObj = payload.get("i_report_format");
			String fileExt = reportFormatObj instanceof String ? (String) reportFormatObj : "pdf";

			String contentType = fileExt.equals("pdf") ? "application/pdf" :
								fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								"application/octet-stream";

			Object dateFromObj = payload.get("i_date_from");
			String startDateString = (dateFromObj instanceof String) ? ((String) dateFromObj).substring(0, 10).replace("-", "") : "";
			
			Object dateToObj = payload.get("i_date_to");
			String endDateString = (dateToObj instanceof String) ? ((String) dateToObj).substring(0, 10).replace("-", "") : "";

			// String fileName = "CounterCollection" 
			// 				+ "-" + startDateString + "-" + endDateString
			// 				+ "-" + ((String) payload.getOrDefault("i_branch_code", ""))
			// 				+ "-" + ((String) payload.getOrDefault("i_payment_mode", ""))
			// 				+ "." + fileExt;

			String branchCode = (payload.get("i_branch_code") != null) ? payload.get("i_branch_code").toString() : "";
			String paymentMode = (payload.get("i_payment_mode") != null) ? payload.get("i_payment_mode").toString() : "";
			
			String fileName = "CounterCollection"
					+ "-" + startDateString
					+ "-" + endDateString
					+ (branchCode.isEmpty() ? "" : "-" + branchCode)
					+ (paymentMode.isEmpty() ? "" : "-" + paymentMode)
					+ "." + fileExt;
						

			response.setContentType(contentType);
			response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
			response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

			File report = counterCollectionReportGenerator.generateReport(new OTCReportRequest(
					fileName, 
					fileExt, 
					(String) dateFromObj, 
					(String) dateToObj, 
					payload.containsKey("i_branch_code") ? (String) payload.get("i_branch_code") : null,
					payload.containsKey("i_payment_mode") ? (String) payload.get("i_payment_mode") : null
			));

			IOUtils.copy(new FileInputStream(report), response.getOutputStream());
			response.flushBuffer();
		}

		@PostMapping(value = "/dailybalancing", consumes = MediaType.APPLICATION_JSON_VALUE)
		public void dailyBalancingReportGenerator(HttpServletResponse response, HttpServletRequest request,
											@Valid @RequestBody Map<String, Object> payload) 
											throws IOException, JRException, SQLException {

			if (!authService.isAuthenticated(request)) {
				return;
			}

			if (!payload.containsKey("i_report_format")
				|| !payload.containsKey("i_date_from") 
				|| !payload.containsKey("i_date_to")
				) {
				return;
			}

			Object reportFormatObj = payload.get("i_report_format");
			String fileExt = reportFormatObj instanceof String ? (String) reportFormatObj : "pdf";

			String contentType = fileExt.equals("pdf") ? "application/pdf" :
								fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								"application/octet-stream";

			Object dateFromObj = payload.get("i_date_from");
			String startDateString = (dateFromObj instanceof String) ? ((String) dateFromObj).substring(0, 10).replace("-", "") : "";
			
			Object dateToObj = payload.get("i_date_to");
			String endDateString = (dateToObj instanceof String) ? ((String) dateToObj).substring(0, 10).replace("-", "") : "";

			// String fileName = "DailyBalancing" 
			// 				+ "-" + startDateString + "-" + endDateString
			// 				+ "-" + ((String) payload.getOrDefault("i_branch_code", ""))
			// 				+ "." + fileExt;

			String branchCode = (payload.get("i_branch_code") != null) ? payload.get("i_branch_code").toString() : "";

			String fileName = "DailyBalancing"
					+ "-" + startDateString
					+ "-" + endDateString
					+ (branchCode.isEmpty() ? "" : "-" + branchCode)
					+ "." + fileExt;
			

			response.setContentType(contentType);
			response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
			response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

			File report = dailyBalancingReportGenerator.generateReport(new OTCReportRequest(
					fileName, 
					fileExt, 
					(String) dateFromObj, 
					(String) dateToObj, 
					payload.containsKey("i_branch_code") ? (String) payload.get("i_branch_code") : null
			));

			IOUtils.copy(new FileInputStream(report), response.getOutputStream());
			response.flushBuffer();
		}

		@PostMapping(value = "/masterbalancing", consumes = MediaType.APPLICATION_JSON_VALUE)
		public void masterBalancingReport(HttpServletResponse response, HttpServletRequest request,
											@Valid @RequestBody Map<String, Object> payload) 
											throws IOException, JRException, SQLException {

			if (!authService.isAuthenticated(request)) {
				return;
			}

			if (!payload.containsKey("i_report_format")
				|| !payload.containsKey("i_date_from") 
				|| !payload.containsKey("i_date_to")
				) {
				return;
			}

			Object reportFormatObj = payload.get("i_report_format");
			String fileExt = reportFormatObj instanceof String ? (String) reportFormatObj : "pdf";

			String contentType = fileExt.equals("pdf") ? "application/pdf" :
								fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								"application/octet-stream";

			Object dateFromObj = payload.get("i_date_from");
			String startDateString = (dateFromObj instanceof String) ? ((String) dateFromObj).substring(0, 10).replace("-", "") : "";
			
			Object dateToObj = payload.get("i_date_to");
			String endDateString = (dateToObj instanceof String) ? ((String) dateToObj).substring(0, 10).replace("-", "") : "";

			// String fileName = "MasterBalancing" 
			// 				+ "-" + startDateString + "-" + endDateString
			// 				+ "-" + ((String) payload.getOrDefault("i_branch_code", ""))
			// 				+ "." + fileExt;

			String branchCode = (payload.get("i_branch_code") != null) ? payload.get("i_branch_code").toString() : "";

			String fileName = "MasterBalancing"
					+ "-" + startDateString
					+ "-" + endDateString
					+ (branchCode.isEmpty() ? "" : "-" + branchCode)
					+ "." + fileExt;
			

			response.setContentType(contentType);
			response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
			response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

			File report = masterBalancingReportGenerator.generateReport(new OTCReportRequest(
					fileName, 
					fileExt, 
					(String) dateFromObj, 
					(String) dateToObj, 
					payload.containsKey("i_branch_code") ? (String) payload.get("i_branch_code") : null
			));

			IOUtils.copy(new FileInputStream(report), response.getOutputStream());
			response.flushBuffer();
		}

		@PostMapping(value = "/otccollection", consumes = MediaType.APPLICATION_JSON_VALUE)
		public void otcCollectionReport(HttpServletResponse response, HttpServletRequest request,
											@Valid @RequestBody Map<String, Object> payload) 
											throws IOException, JRException, SQLException {

			if (!authService.isAuthenticated(request)) {
				return;
			}

			if (!payload.containsKey("i_report_format")
				|| !payload.containsKey("i_date_from") 
				|| !payload.containsKey("i_date_to")
				) {
				return;
			}

			Object reportFormatObj = payload.get("i_report_format");
			String fileExt = reportFormatObj instanceof String ? (String) reportFormatObj : "pdf";

			String contentType = fileExt.equals("pdf") ? "application/pdf" :
								fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								"application/octet-stream";

			Object dateFromObj = payload.get("i_date_from");
			String startDateString = (dateFromObj instanceof String) ? ((String) dateFromObj).substring(0, 10).replace("-", "") : "";
			
			Object dateToObj = payload.get("i_date_to");
			String endDateString = (dateToObj instanceof String) ? ((String) dateToObj).substring(0, 10).replace("-", "") : "";

			String fileName = "OTCCollection" 
							+ "-" + startDateString + "-" + endDateString
							+ "." + fileExt;

			response.setContentType(contentType);
			response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
			response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

			File report = otccollectionReportGenerator.generateReport(new OTCReportRequest(
					fileName, 
					fileExt, 
					(String) dateFromObj, 
					(String) dateToObj
			));

			IOUtils.copy(new FileInputStream(report), response.getOutputStream());
			response.flushBuffer();
		}

		@PostMapping(value = "/otccollectionplus", consumes = MediaType.APPLICATION_JSON_VALUE)
		public void otccollectionPlusReport(HttpServletResponse response, HttpServletRequest request,
											@Valid @RequestBody Map<String, Object> payload) 
											throws IOException, JRException, SQLException {

			if (!authService.isAuthenticated(request)) {
				return;
			}

			if (!payload.containsKey("i_report_format")
				|| !payload.containsKey("i_date_from") 
				|| !payload.containsKey("i_date_to")
				) {
				return;
			}

			Object reportFormatObj = payload.get("i_report_format");
			String fileExt = reportFormatObj instanceof String ? (String) reportFormatObj : "pdf";

			String contentType = fileExt.equals("pdf") ? "application/pdf" :
								fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								"application/octet-stream";

			Object dateFromObj = payload.get("i_date_from");
			String startDateString = (dateFromObj instanceof String) ? ((String) dateFromObj).substring(0, 10).replace("-", "") : "";
			
			Object dateToObj = payload.get("i_date_to");
			String endDateString = (dateToObj instanceof String) ? ((String) dateToObj).substring(0, 10).replace("-", "") : "";

			// String fileName = "OTCCollectionPlus" 
			// 				+ "-" + startDateString + "-" + endDateString
			// 				+ "-" + ((String) payload.getOrDefault("i_fee_detail_id", ""))
			// 				+ "-" + ((String) payload.getOrDefault("i_ledger_code", ""))
			// 				+ "." + fileExt;

			String feeDetailId = (payload.get("i_fee_detail_id") != null) ? payload.get("i_fee_detail_id").toString() : "";
			String ledgerCode = (payload.get("i_ledger_code") != null) ? payload.get("i_ledger_code").toString() : "";
			
			String fileName = "OTCCollectionPlus"
					+ "-" + startDateString
					+ "-" + endDateString
					+ (feeDetailId.isEmpty() ? "" : "-" + feeDetailId)
					+ (ledgerCode.isEmpty() ? "" : "-" + ledgerCode)
					+ "." + fileExt;
						

			response.setContentType(contentType);
			response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
			response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

			File report = otccollectionPlusReportGenerator.generateReport(new OTCReportRequest(
					fileName, 
					fileExt, 
					(String) dateFromObj, 
					(String) dateToObj, 
					payload.containsKey("i_fee_detail_id") ? (String) payload.get("i_fee_detail_id") : null,
					payload.containsKey("i_ledger_code") ? (String) payload.get("i_ledger_code") : null
			));

			IOUtils.copy(new FileInputStream(report), response.getOutputStream());
			response.flushBuffer();
		}

		@PostMapping(value = "/otcreceiptcancellation", consumes = MediaType.APPLICATION_JSON_VALUE)
		public void otcreceiptCancellationReport(HttpServletResponse response, HttpServletRequest request,
											@Valid @RequestBody Map<String, Object> payload) 
											throws IOException, JRException, SQLException {

			if (!authService.isAuthenticated(request)) {
				return;
			}

			if (!payload.containsKey("i_report_format")
				|| !payload.containsKey("i_date_from") 
				|| !payload.containsKey("i_date_to")
				) {
				return;
			}

			Object reportFormatObj = payload.get("i_report_format");
			String fileExt = reportFormatObj instanceof String ? (String) reportFormatObj : "pdf";

			String contentType = fileExt.equals("pdf") ? "application/pdf" :
								fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								"application/octet-stream";

			Object dateFromObj = payload.get("i_date_from");
			String startDateString = (dateFromObj instanceof String) ? ((String) dateFromObj).substring(0, 10).replace("-", "") : "";
			
			Object dateToObj = payload.get("i_date_to");
			String endDateString = (dateToObj instanceof String) ? ((String) dateToObj).substring(0, 10).replace("-", "") : "";

			// String fileName = "OTCReceiptCancellation" 
			// 				+ "-" + startDateString + "-" + endDateString
			// 				+ "-" + ((String) payload.getOrDefault("i_branch_code", ""))
			// 				+ "-" + ((String) payload.getOrDefault("i_payment_mode", ""))
			// 				+ "." + fileExt;

			String branchCode = (payload.get("i_branch_code") != null) ? payload.get("i_branch_code").toString() : "";
			String paymentMode = (payload.get("i_payment_mode") != null) ? payload.get("i_payment_mode").toString() : "";
			
			String fileName = "OTCReceiptCancellation"
					+ "-" + startDateString
					+ "-" + endDateString
					+ (branchCode.isEmpty() ? "" : "-" + branchCode)
					+ (paymentMode.isEmpty() ? "" : "-" + paymentMode)
					+ "." + fileExt;
						

			response.setContentType(contentType);
			response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
			response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

			File report = otcreceiptCancellationReportGenerator.generateReport(new OTCReportRequest(
					fileName, 
					fileExt, 
					(String) dateFromObj, 
					(String) dateToObj, 
					payload.containsKey("i_branch_code") ? (String) payload.get("i_branch_code") : null,
					payload.containsKey("i_payment_mode") ? (String) payload.get("i_payment_mode") : null
			));

			IOUtils.copy(new FileInputStream(report), response.getOutputStream());
			response.flushBuffer();
		}

		@PostMapping(value = "/otcreturnedcheque", consumes = MediaType.APPLICATION_JSON_VALUE)
		public void otcreturnedChequeReport(HttpServletResponse response, HttpServletRequest request,
											@Valid @RequestBody Map<String, Object> payload) 
											throws IOException, JRException, SQLException {

			if (!authService.isAuthenticated(request)) {
				return;
			}

			if (!payload.containsKey("i_report_format")
				|| !payload.containsKey("i_date_from") 
				|| !payload.containsKey("i_date_to")
				) {
				return;
			}

			Object reportFormatObj = payload.get("i_report_format");
			String fileExt = reportFormatObj instanceof String ? (String) reportFormatObj : "pdf";

			String contentType = fileExt.equals("pdf") ? "application/pdf" :
								fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								"application/octet-stream";

			Object dateFromObj = payload.get("i_date_from");
			String startDateString = (dateFromObj instanceof String) ? ((String) dateFromObj).substring(0, 10).replace("-", "") : "";
			
			Object dateToObj = payload.get("i_date_to");
			String endDateString = (dateToObj instanceof String) ? ((String) dateToObj).substring(0, 10).replace("-", "") : "";

			String fileName = "OTCReturnedCheque" 
							+ "-" + startDateString + "-" + endDateString
							+ "." + fileExt;

			response.setContentType(contentType);
			response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
			response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

			File report = otcreturnedChequeReport.generateReport(new OTCReportRequest(
					fileName, 
					fileExt, 
					(String) dateFromObj, 
					(String) dateToObj, 
					payload.containsKey("i_branch_code") ? (String) payload.get("i_branch_code") : null,
					payload.containsKey("i_receipt_no") ? (String) payload.get("i_receipt_no") : null,
					payload.containsKey("i_cheque_no") ? (String) payload.get("i_cheque_no") : null,
					payload.containsKey("i_customer_name") ? (String) payload.get("i_customer_name") : null,
					payload.containsKey("i_entity_no") ? (String) payload.get("i_entity_no") : null,
					payload.containsKey("i_non_billing_no") ? (String) payload.get("i_non_billing_no") : null,
					payload.containsKey("i_non_billing_status") ? (String) payload.get("i_non_billing_status") : null
			));

			IOUtils.copy(new FileInputStream(report), response.getOutputStream());
			response.flushBuffer();
		}

		@PostMapping(value = "/bankinslip", consumes = MediaType.APPLICATION_JSON_VALUE)
		public void bankInSlipReport(HttpServletResponse response, HttpServletRequest request,
											@Valid @RequestBody Map<String, Object> payload) 
											throws IOException, JRException, SQLException {

			if (!authService.isAuthenticated(request)) {
				return;
			}

			if (!payload.containsKey("i_report_format")
				|| !payload.containsKey("i_date_from") 
				|| !payload.containsKey("i_date_to")
				) {
				return;
			}

			Object reportFormatObj = payload.get("i_report_format");
			String fileExt = reportFormatObj instanceof String ? (String) reportFormatObj : "pdf";

			String contentType = fileExt.equals("pdf") ? "application/pdf" :
								fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								"application/octet-stream";

			Object dateFromObj = payload.get("i_date_from");
			String startDateString = (dateFromObj instanceof String) ? ((String) dateFromObj).substring(0, 10).replace("-", "") : "";
			
			Object dateToObj = payload.get("i_date_to");
			String endDateString = (dateToObj instanceof String) ? ((String) dateToObj).substring(0, 10).replace("-", "") : "";

			// String fileName = "BankInSlip" 
			// 				+ "-" + startDateString + "-" + endDateString
			// 				+ "-" + ((String) payload.getOrDefault("i_branch_code", ""))
			// 				+ "-" + ((String) payload.getOrDefault("i_bank_in_slip_no", ""))
			// 				+ "." + fileExt;

			String branchCode = (payload.get("i_branch_code") != null) ? payload.get("i_branch_code").toString() : "";
			String bankInSlipNo = (payload.get("i_bank_in_slip_no") != null) ? payload.get("i_bank_in_slip_no").toString() : "";
			
			String fileName = "BankInSlip"
					+ "-" + startDateString
					+ "-" + endDateString
					+ (branchCode.isEmpty() ? "" : "-" + branchCode)
					+ (bankInSlipNo.isEmpty() ? "" : "-" + bankInSlipNo)
					+ "." + fileExt;
						

			response.setContentType(contentType);
			response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
			response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

			File report = bankInSlipReportGenerator.generateReport(new OTCReportRequest(
					fileName, 
					fileExt, 
					(String) dateFromObj, 
					(String) dateToObj, 
					payload.containsKey("i_branch_code") ? (String) payload.get("i_branch_code") : null,
					payload.containsKey("i_bank_in_slip_no") ? (String) payload.get("i_bank_in_slip_no") : null
			));

			IOUtils.copy(new FileInputStream(report), response.getOutputStream());
			response.flushBuffer();
		}

}
