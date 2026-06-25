package com.maven.rms.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.maven.rms.models.RefundStatusRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.utils.reports.RefundAgingReportGenerator;
import com.maven.rms.utils.reports.RefundStatusDetailedReportGenerator;
import com.maven.rms.utils.reports.RefundSummaryStatusReportGenerator;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;

@RestController
@RequestMapping("api/rs/v1")
@Slf4j
public class RefundStatusController {
    @Autowired
	private AuthService authService;

    @Autowired
    private RefundStatusDetailedReportGenerator refundStatusDetailedReportGenerator;

    @Autowired
    private RefundSummaryStatusReportGenerator refundSummaryStatusReportGenerator;

    @Autowired
    private RefundAgingReportGenerator refundAgingReportGenerator;

    @Value("${jasper.reports.directory}")
	private String report_directory;

	public RefundStatusController() {
		log.info("ReportController services is started");
	}

    	@PostMapping(value = "/generateRefundStatusReport", consumes = MediaType.APPLICATION_JSON_VALUE)
		public void refundStatusReport(HttpServletResponse response, HttpServletRequest request,
									  @Valid @RequestBody Map<String, Object> payload) 
								      throws IOException, JRException, SQLException {

			if (!authService.isAuthenticated(request)) {
				return;
			}

			if (!payload.containsKey("i_report_format")
				|| !payload.containsKey("i_date_range_start") 
				|| !payload.containsKey("i_date_range_end")
				) {
				return;
			}

			Object reportFormatObj = payload.get("i_report_format");
			String fileExt = reportFormatObj instanceof String ? (String) reportFormatObj : "pdf";

			String contentType = fileExt.equals("pdf") ? "application/pdf" :
								fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								"application/octet-stream";

			Object dateFromObj = payload.get("i_date_range_start");
			String startDateString = (dateFromObj instanceof String) ? ((String) dateFromObj).substring(0, 10).replace("-", "") : "";
			
			Object dateToObj = payload.get("i_date_range_end");
			String endDateString = (dateToObj instanceof String) ? ((String) dateToObj).substring(0, 10).replace("-", "") : "";

			// String fileName = "RefundStatusDetailed" 
			// 				+ "-" + startDateString + "-" + endDateString
			// 				+ "-" + ((String) payload.getOrDefault("i_refund_status", ""))
			// 				+ "-" + ((String) payload.getOrDefault("i_refund_ty", ""))
            //                 + "-" + ((String) payload.getOrDefault("i_division", ""))
			// 				+ "." + fileExt;

			String status = (payload.get("i_refund_status") != null) ? payload.get("i_refund_status").toString() : "";
			String type = (payload.get("i_refund_ty") != null) ? payload.get("i_refund_ty").toString() : "";

			String fileName = "RefundStatusDetailed"
        		+ "-" + startDateString
        		+ "-" + endDateString
        		+ (status.isEmpty() ? "" : "-" + status)
        		+ (type.isEmpty() ? "" : "-" + type)
        		+ "." + fileExt;

			response.setContentType(contentType);
			response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
			response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

			File report = refundStatusDetailedReportGenerator.generateReport(new RefundStatusRequest(
					fileName, 
					fileExt, 
					(String) dateFromObj, 
					(String) dateToObj, 
					payload.containsKey("i_refund_status") ? (String) payload.get("i_refund_status") : null,
					payload.containsKey("i_refund_ty") ? (String) payload.get("i_refund_ty") : null
			));

			IOUtils.copy(new FileInputStream(report), response.getOutputStream());
			response.flushBuffer();
		}

    	@PostMapping(value = "/refundSummaryStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
		public void refundSummaryReport(HttpServletResponse response, HttpServletRequest request,
									  @Valid @RequestBody Map<String, Object> payload) 
								      throws IOException, JRException, SQLException {

			if (!authService.isAuthenticated(request)) {
				return;
			}

			if (!payload.containsKey("i_report_format")
				|| !payload.containsKey("i_date_range_start") 
				|| !payload.containsKey("i_date_range_end")
				) {
				return;
			}

			Object reportFormatObj = payload.get("i_report_format");
			String fileExt = reportFormatObj instanceof String ? (String) reportFormatObj : "pdf";

			String contentType = fileExt.equals("pdf") ? "application/pdf" :
								fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								"application/octet-stream";

			Object dateFromObj = payload.get("i_date_range_start");
			String startDateString = (dateFromObj instanceof String) ? ((String) dateFromObj).substring(0, 10).replace("-", "") : "";
			
			Object dateToObj = payload.get("i_date_range_end");
			String endDateString = (dateToObj instanceof String) ? ((String) dateToObj).substring(0, 10).replace("-", "") : "";

			// String fileName = "RefundSummaryStatus" 
			// 				+ "-" + startDateString + "-" + endDateString
			// 				+ "-" + ((String) payload.getOrDefault("i_refund_status", ""))
			// 				+ "-" + ((String) payload.getOrDefault("i_refund_ty", ""))
            //                 + "-" + ((String) payload.getOrDefault("i_division", ""))
			// 				+ "." + fileExt;

			String status = (payload.get("i_refund_status") != null) ? payload.get("i_refund_status").toString() : "";
			String type = (payload.get("i_refund_ty") != null) ? payload.get("i_refund_ty").toString() : "";
			String division = (payload.get("i_division") != null) ? payload.get("i_division").toString() : "";
			
			String fileName = "RefundSummaryStatus"
					+ "-" + startDateString
					+ "-" + endDateString
					+ (status.isEmpty() ? "" : "-" + status)
					+ (type.isEmpty() ? "" : "-" + type)
					+ (division.isEmpty() ? "" : "-" + division)
					+ "." + fileExt;
						

			response.setContentType(contentType);
			response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
			response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

			File report = refundSummaryStatusReportGenerator.generateReport(new RefundStatusRequest(
					fileName, 
					fileExt, 
					(String) dateFromObj, 
					(String) dateToObj, 
					payload.containsKey("i_refund_status") ? (String) payload.get("i_refund_status") : null,
					payload.containsKey("i_refund_ty") ? (String) payload.get("i_refund_ty") : null,
                    payload.containsKey("i_division") ? (String) payload.get("i_division") : null
			));

			IOUtils.copy(new FileInputStream(report), response.getOutputStream());
			response.flushBuffer();
		}

        @PostMapping(value = "/refundAging", consumes = MediaType.APPLICATION_JSON_VALUE)
		public void refundAgingReport(HttpServletResponse response, HttpServletRequest request,
									  @Valid @RequestBody Map<String, Object> payload) 
								      throws IOException, JRException, SQLException {

			if (!authService.isAuthenticated(request)) {
				return;
			}

			if (!payload.containsKey("i_report_format")
				|| !payload.containsKey("i_date_range_start") 
				|| !payload.containsKey("i_date_range_end")
				) {
				return;
			}

			Object reportFormatObj = payload.get("i_report_format");
			String fileExt = reportFormatObj instanceof String ? (String) reportFormatObj : "pdf";

			String contentType = fileExt.equals("pdf") ? "application/pdf" :
								fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" :
								"application/octet-stream";

			Object dateFromObj = payload.get("i_date_range_start");
			String startDateString = (dateFromObj instanceof String) ? ((String) dateFromObj).substring(0, 10).replace("-", "") : "";
			
			Object dateToObj = payload.get("i_date_range_end");
			String endDateString = (dateToObj instanceof String) ? ((String) dateToObj).substring(0, 10).replace("-", "") : "";

			// String fileName = "RefundAging" 
			// 				+ "-" + startDateString + "-" + endDateString
			// 				+ "-" + ((String) payload.getOrDefault("i_refund_status", ""))
			// 				+ "-" + ((String) payload.getOrDefault("i_refund_ty", ""))
            //                 + "-" + ((String) payload.getOrDefault("i_refund_no", ""))
			// 				+ "." + fileExt;

			String status = (payload.get("i_refund_status") != null) ? payload.get("i_refund_status").toString() : "";
			String type = (payload.get("i_refund_ty") != null) ? payload.get("i_refund_ty").toString() : "";
			String number = (payload.get("i_refund_no") != null) ? payload.get("i_refund_no").toString() : "";

			String fileName = "RefundAging"
        			+ "-" + startDateString
        			+ "-" + endDateString
        			+ (status.isEmpty() ? "" : "-" + status)
        			+ (type.isEmpty() ? "" : "-" + type)
        			+ (number.isEmpty() ? "" : "-" + number)
        			+ "." + fileExt;

			response.setContentType(contentType);
			response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
			response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

			File report = refundAgingReportGenerator.generateReport(new RefundStatusRequest(
					fileName, 
					fileExt, 
					(String) dateFromObj, 
					(String) dateToObj, 
					payload.containsKey("i_refund_status") ? (String) payload.get("i_refund_status") : null,
					payload.containsKey("i_refund_ty") ? (String) payload.get("i_refund_ty") : null,
                    payload.containsKey("i_refund_no") ? (String) payload.get("i_refund_no") : null
			));

			IOUtils.copy(new FileInputStream(report), response.getOutputStream());
			response.flushBuffer();
		}
}

