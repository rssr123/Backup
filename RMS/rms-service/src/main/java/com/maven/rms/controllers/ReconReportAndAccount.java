package com.maven.rms.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.DateFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder.In;
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

import com.maven.rms.models.DeferredIncomeAging;
import com.maven.rms.models.DeferredIncomeAgingRequest;
import com.maven.rms.models.RiplAging;
import com.maven.rms.models.RIPLAgingRequest;
import com.maven.rms.models.MFTWF;
import com.maven.rms.models.MFTWFRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.models.ReportRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.DIAgingRepService;
import com.maven.rms.services.RIPLAgingRepService;
import com.maven.rms.services.StoreProcedureService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.reports.PaymentCollectionReportGenerator;
import com.maven.rms.utils.reports.RIPLAgingReportGenerator;
import com.maven.rms.utils.reports.ReportReconAndAccountGenerator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import com.maven.rms.utils.ControllersEnum;

@RestController
@RequestMapping("/api/rrareport/v1")
@Slf4j
public class ReconReportAndAccount {

	//private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

	@Autowired
	private AuthService authService;

	@Autowired
	private ReportReconAndAccountGenerator rrAG;

	@Autowired
	private RIPLAgingReportGenerator riplAG;

	@Autowired
	private StoreProcedureService spService;

	@Autowired
	private DIAgingRepService diAgingRepSvc;

	@Autowired
	private RIPLAgingRepService riplAgingService;

	@Value("${jasper.reports.directory}")
	private String report_directory;

	// @Autowired
	// private DeferredIncomeAging deferredIncomeAging;

	public ReconReportAndAccount() {
		log.info("ReconReportAndAccountController services is started");
	}

	// daily collection listing report
	@PostMapping(value = "/dly_col_lst_report", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void dailyCollectionListingReport(HttpServletResponse response, HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> dailyload) throws IOException, JRException, SQLException {
		// try {
			if (!authService.isAuthenticated(request))
				return;
		// } catch (NumberFormatException e) {
		// 	return;
		// } catch (Exception e) {
		// 	return;
		// }

		if (!dailyload.containsKey("i_start_date") || !dailyload.containsKey("i_end_date")
				|| !dailyload.containsKey("i_orn_no") || !dailyload.containsKey("i_pg_pymt_id")
				|| !dailyload.containsKey("i_pg_txn_id")
				|| !dailyload.containsKey("i_pg_txn_status") || !dailyload.containsKey("i_rcpt_no")
				|| !dailyload.containsKey("i_stmt_no")
				|| !dailyload.containsKey("i_pg_txn_msg") || !dailyload.containsKey("i_report_name")
				|| !dailyload.containsKey("i_report_format"))
			return;

		String fileExt = ((String) dailyload.get("i_report_format"));
		String contentType = fileExt.equals("pdf") ? "application/pdf"
				: fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
						: fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
								: "application/octet-stream";

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate startDateToConvert = LocalDate.parse((String) dailyload.get("i_start_date"), inputFormatter);
		LocalDate endDateToConvert = LocalDate.parse((String) dailyload.get("i_end_date"), inputFormatter);

		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
		String startDateString = startDateToConvert.format(outputFormatter);
		String endDateString = endDateToConvert.format(outputFormatter);

		// String fileName = "Daily Collection Listing - "
		// 		+ startDateString + " - " + endDateString + "." + fileExt;

		String fileName = "Daily Collection Listing - "
				+ startDateString  + "." + fileExt;

		response.setContentType(contentType);
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

		File report = rrAG.generateDailyColLstReport(new ReportRequest(fileName, (String) dailyload.get("i_start_date"),
				(String) dailyload.get("i_end_date"), (String) dailyload.get("i_orn_no"),
				(String) dailyload.get("i_pg_pymt_id"), (String) dailyload.get("i_pg_txn_id"),
				(Integer) dailyload.get("i_pg_txn_status"),
				(String) dailyload.get("i_pg_txn_msg"), (String) dailyload.get("i_rcpt_no"),
				(String) dailyload.get("i_stmt_no"), (String) dailyload.get("i_report_name"), fileExt));

		IOUtils.copy(new FileInputStream(report), response.getOutputStream());
		response.flushBuffer();
	}

	// daily collection listing report
	@PostMapping(value = "/un_trans_lst_report", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void generatUnmatchedTransListReport(HttpServletResponse response, HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> dailyload) throws IOException, JRException, SQLException {
		// try {
			if (!authService.isAuthenticated(request))
				return;
		// } catch (NumberFormatException e) {
		// 	return;
		// } catch (Exception e) {
		// 	return;
		// }

		if (!dailyload.containsKey("i_settlement_date") ||
				!dailyload.containsKey("i_orn_no") ||
				!dailyload.containsKey("i_sub_criteria") ||
				!dailyload.containsKey("i_rcpt_no") ||
				!dailyload.containsKey("i_pg_txn_id") ||
				!dailyload.containsKey("i_stmt_no") ||
				!dailyload.containsKey("i_check_duplicate") ||
				!dailyload.containsKey("i_report_name") ||
				!dailyload.containsKey("i_report_format"))
			return;

		String fileExt = ((String) dailyload.get("i_report_format"));
		String contentType = fileExt.equals("pdf") ? "application/pdf"
				: fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
						: fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
								: "application/octet-stream";

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate settlementDateToConvert = LocalDate.parse((String) dailyload.get("i_settlement_date"),
				inputFormatter);

		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
		String settlementDateString = settlementDateToConvert.format(outputFormatter);

		String fileName = "Unmatched Transaction Listing - "
				+ settlementDateString + "." + fileExt;

		response.setContentType(contentType);
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

		File report = rrAG.generatUnmatchedTransListReport(new ReportRequest(
				fileName,
				(String) dailyload.get("i_settlement_date"),
				(String) dailyload.get("i_orn_no"),
				(Integer) dailyload.get("i_sub_criteria"),
				(String) dailyload.get("i_rcpt_no"),
				(String) dailyload.get("i_pg_txn_id"),
				(String) dailyload.get("i_stmt_no"),
				(Integer) dailyload.get("i_check_duplicate"),
				(String) dailyload.get("i_report_name"),
				fileExt));

		IOUtils.copy(new FileInputStream(report), response.getOutputStream());
		response.flushBuffer();
	}

	// matched transaction listing report
	@PostMapping(value = "/mat_trans_lst_report", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void matchedTransactionListingReport(HttpServletResponse response, HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> matload) throws IOException, JRException, SQLException {
		// try {
			if (!authService.isAuthenticated(request))
				return;
		// } catch (NumberFormatException e) {
		// 	return;
		// } catch (Exception e) {
		// 	return;
		// }

		if (!matload.containsKey("i_start_date") || !matload.containsKey("i_end_date")
				|| !matload.containsKey("i_orn_no") || !matload.containsKey("i_pg_pymt_id")
				|| !matload.containsKey("i_pg_txn_id")
				|| !matload.containsKey("i_rcpt_no") || !matload.containsKey("i_stmt_no")
				|| !matload.containsKey("i_report_name") || !matload.containsKey("i_report_format"))
			return;

		String fileExt = ((String) matload.get("i_report_format"));
		String contentType = fileExt.equals("pdf") ? "application/pdf"
				: fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
						: fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
								: "application/octet-stream";

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate startDateToConvert = LocalDate.parse((String) matload.get("i_start_date"), inputFormatter);
		LocalDate endDateToConvert = LocalDate.parse((String) matload.get("i_end_date"), inputFormatter);

		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
		String startDateString = startDateToConvert.format(outputFormatter);
		String endDateString = endDateToConvert.format(outputFormatter);

		String fileName = "Matched Transaction Listing - "
				+ startDateString + " - " + endDateString + "." + fileExt;

		response.setContentType(contentType);
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

		File report = rrAG.generateMatTransLstReport(
				new ReportRequest(fileName, (String) matload.get("i_start_date"), (String) matload.get("i_end_date"),
						(String) matload.get("i_orn_no"), (String) matload.get("i_pg_pymt_id"),
						(String) matload.get("i_pg_txn_id"), (String) matload.get("i_rcpt_no"),
						(String) matload.get("i_stmt_no"), (String) matload.get("i_report_name"), fileExt));

		IOUtils.copy(new FileInputStream(report), response.getOutputStream());
		response.flushBuffer();
	}

	// PG Settlement/Disbursement listing report
	@PostMapping(value = "/pg_set_dis_lst_report", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void pgSettlementDisbursementListingReport(HttpServletResponse response, HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> pgload) throws IOException, JRException, SQLException {
		// try {
			if (!authService.isAuthenticated(request))
				return;
		// } catch (NumberFormatException e) {
		// 	return;
		// } catch (Exception e) {
		// 	return;
		// }

		if (!pgload.containsKey("i_start_date") || !pgload.containsKey("i_end_date") || !pgload.containsKey("i_stmt_no")
				|| !pgload.containsKey("i_txn_desc") || !pgload.containsKey("i_report_name")
				|| !pgload.containsKey("i_report_format"))
			return;

		String fileExt = ((String) pgload.get("i_report_format"));
		String contentType = fileExt.equals("pdf") ? "application/pdf"
				: fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
						: fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
								: "application/octet-stream";

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate startDateToConvert = LocalDate.parse((String) pgload.get("i_start_date"), inputFormatter);
		LocalDate endDateToConvert = LocalDate.parse((String) pgload.get("i_end_date"), inputFormatter);

		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
		String startDateString = startDateToConvert.format(outputFormatter);
		String endDateString = endDateToConvert.format(outputFormatter);

		String fileName = "PG Settlement Disbursement Listing - "
				+ startDateString + " - " + endDateString + "." + fileExt;

		response.setContentType(contentType);
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

		ReportRequest rq = new ReportRequest(fileName, (String) pgload.get("i_start_date"),
				(String) pgload.get("i_end_date"), (String) pgload.get("i_stmt_no"), (String) pgload.get("i_txn_desc"));
		rq.setReportName((String) pgload.get("i_report_name"));
		rq.setFileType(fileExt);
		File report = rrAG.generatePGSetDisLstReport(rq);

		IOUtils.copy(new FileInputStream(report), response.getOutputStream());
		response.flushBuffer();
	}

	// Insert DI aging report
	// @Secured("ROLE_USER")
	@PostMapping(value = "/insertdiagingrpt")
	public ResponseEntity<ApiResponse<BigInteger>> sp_insdiagingrpt(HttpServletRequest request,
			@RequestBody DeferredIncomeAgingRequest DIRequest) {

		BigInteger result = BigInteger.ZERO;
		String email = null;

		// try {
			if (!authService.isAuthenticated(request)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			if (DIRequest.getI_email_ntfn() == 1) {
				email = authService.getUserEmail();
			} else {
				email = null;
			}

			result = diAgingRepSvc.sp_insdiagingrpt(
					DIRequest,
					email,
					authService.getLoginUserName(), // created_by
					authService.getLoginUserName() // modified_by
			);

			if (result.compareTo(BigInteger.ZERO) <= 0) {
				return APIResponse.InternalServerError();
			}
			return APIResponse.SuccessResponse(result);

		// } catch (NumberFormatException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InvalidFormat();

		// } catch (Exception e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InternalServerError();

		// }
	}

	// Get DI aging listing report
	// @Secured("ROLE_USER")
	@PostMapping(value = "/getdiaginglistingrpt")
	public ResponseEntity<ApiResponse<List<DeferredIncomeAging>>> sp_getdiaginglistingrpt(HttpServletRequest request,
			@RequestBody DeferredIncomeAgingRequest DIRequest) {

		List<DeferredIncomeAging> result = Collections.emptyList();

		// try {
			if (!authService.isAuthenticated(request)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			result = diAgingRepSvc.sp_getdiaginglistingrpt(
					DIRequest);

			if (result.isEmpty()) {
				return APIResponse.NoDataFound(ControllersEnum.RECON_REPORT_AND_ACCOUNT);
			}

			return APIResponse.SuccessResponse(result);

		// } catch (NumberFormatException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InvalidFormat();

		// } catch (Exception e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InternalServerError();

		// }
	}

	// Update DI aging report
	// @Secured("ROLE_USER")
	@PostMapping(value = "/updatediagingrpt")
	public ResponseEntity<ApiResponse<Integer>> sp_upddiagingrpt(HttpServletRequest request,
			@RequestBody DeferredIncomeAgingRequest DIRequest) {

			Integer result = 0;

			// try {
				if (!authService.isAuthenticated(request)) {
					return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
				}


           result = diAgingRepSvc.sp_upddiagingrpt(
            DIRequest, authService.getLoginUserName() //modified_by
           );

			if (result <= 0) {
				return APIResponse.InternalServerError();
			}

			return APIResponse.SuccessResponse(result);

		// } catch (NumberFormatException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InvalidFormat();

		// } catch (Exception e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InternalServerError();

		// }
	}

	// Get DI aging single row
	// @Secured("ROLE_USER")
	@PostMapping(value = "/getdiagingrpt")
	public ResponseEntity<ApiResponse<List<DeferredIncomeAging>>> sp_getdiagingrpt(HttpServletRequest request,
			@RequestBody DeferredIncomeAgingRequest DIRequest) {

		List<DeferredIncomeAging> result = Collections.emptyList();

		// try {
			if (!authService.isAuthenticated(request)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			result = diAgingRepSvc.sp_getdiagingrpt(
					DIRequest.getI_rpt_di_age_id());

			if (result.isEmpty()) {
				return APIResponse.NoDataFound(ControllersEnum.RECON_REPORT_AND_ACCOUNT);
			}

			return APIResponse.SuccessResponse(result);

		// } catch (NumberFormatException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InvalidFormat();

		// } catch (Exception e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InternalServerError();

		// }
	}

	// generate di aging report
	@PostMapping(value = "/generatediagingrpt", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void diAgingReport(HttpServletResponse response, HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> diload) throws IOException, JRException, SQLException {
		// try {
			if (!authService.isAuthenticated(request))
				return;
		// } catch (NumberFormatException e) {
		// 	return;
		// } catch (Exception e) {
		// 	return;
		// }

		if (!diload.containsKey("i_rpt_di_age_id"))
			return;

		Integer intValue = (Integer) diload.get("i_rpt_di_age_id");
		BigInteger bigIntValue = BigInteger.valueOf(intValue);

		List<DeferredIncomeAging> diAgingList = diAgingRepSvc.sp_getdiagingrpt(bigIntValue);

		String p_dt_req = null;
		Integer p_tmn_status = null;
		String p_ent_ty = null;
		String p_ent_nm = null;
		String p_txn_ty = null;
		String p_status = null;
		String p_dt_exp_fr = null;
		String p_dt_exp_to = null;
		String p_dt_eff_fr = null;
		String p_dt_eff_to = null;
		String p_dt_app_fr = null;
		String p_dt_app_to = null;
		String p_dt_tmn_fr = null;
		String p_dt_tmn_to = null;
		String dt_created = null;
		String dt_modified = null;
		String created_by = null;
		String modified_by = null;
		String status = null;
		String p_email = null;
		String p_file_type = null;
		String p_file_nm = null;
		String p_batch_no = null;
		String p_fms_ref_no = null;

		if (!diAgingList.isEmpty()) {

			if (diAgingList.get(0).getP_dt_req() != null) {
				p_dt_req = convertDateTime(diAgingList.get(0).getP_dt_req().toString());
			}

			p_tmn_status = diAgingList.get(0).getP_tmn_status();
			p_ent_ty = diAgingList.get(0).getP_ent_ty();
			p_ent_nm = diAgingList.get(0).getP_ent_nm();
			p_txn_ty = diAgingList.get(0).getP_txn_ty();
			p_status = diAgingList.get(0).getP_status();

			if (diAgingList.get(0).getP_dt_exp_fr() != null) {
				p_dt_exp_fr = convertDateTime(diAgingList.get(0).getP_dt_exp_fr().toString());
			}

			if (diAgingList.get(0).getP_dt_exp_to() != null) {
				p_dt_exp_to = convertDateTime(diAgingList.get(0).getP_dt_exp_to().toString());
			}

			if (diAgingList.get(0).getP_dt_eff_fr() != null) {
				p_dt_eff_fr = convertDateTime(diAgingList.get(0).getP_dt_eff_fr().toString());
			}

			if (diAgingList.get(0).getP_dt_eff_to() != null) {
				p_dt_eff_to = convertDateTime(diAgingList.get(0).getP_dt_eff_to().toString());
			}

			if (diAgingList.get(0).getP_dt_app_fr() != null) {
				p_dt_app_fr = convertDateTime(diAgingList.get(0).getP_dt_app_fr().toString());
			}

			if (diAgingList.get(0).getP_dt_app_to() != null) {
				p_dt_app_to = convertDateTime(diAgingList.get(0).getP_dt_app_to().toString());
			}

			if (diAgingList.get(0).getP_dt_tmn_fr() != null) {
				p_dt_tmn_fr = convertDateTime(diAgingList.get(0).getP_dt_tmn_fr().toString());
			}

			if (diAgingList.get(0).getP_dt_tmn_to() != null) {
				p_dt_tmn_to = convertDateTime(diAgingList.get(0).getP_dt_tmn_to().toString());
			}

			if (diAgingList.get(0).getDt_created() != null) {
				dt_created = convertDateTime(diAgingList.get(0).getDt_created().toString());
			}

			if (diAgingList.get(0).getDt_modified() != null) {
				dt_modified = convertDateTime(diAgingList.get(0).getDt_modified().toString());
			}

			created_by = diAgingList.get(0).getCreated_by();
			modified_by = diAgingList.get(0).getModified_by();
			status = diAgingList.get(0).getStatus();
			p_email = diAgingList.get(0).getP_email();
			p_file_type = diAgingList.get(0).getP_file_type();
			p_file_nm = diAgingList.get(0).getP_file_nm();
			p_batch_no = diAgingList.get(0).getP_batch_no();
			p_fms_ref_no = diAgingList.get(0).getP_fms_ref_no();

		}

		String fileExt = p_file_type;
		String contentType = fileExt.equals("pdf") ? "application/pdf"
				: fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
						: fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
								: "application/octet-stream";

		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmm");
		String formattedDateTime = now.format(formatter);
		String fileName = "DI_Aging_" + formattedDateTime + "." + fileExt;
		// String fileName = p_file_nm;

		response.setContentType(contentType);
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

		File report = rrAG.generateDIAgingReport(
				new ReportRequest(fileName, p_dt_req, p_dt_eff_fr, p_dt_eff_to, p_status, p_tmn_status,
						p_ent_ty, p_ent_nm, p_txn_ty, p_dt_exp_fr, p_dt_exp_to, p_dt_app_fr, p_dt_app_to, p_dt_tmn_fr,
						p_dt_tmn_to, p_batch_no,
						p_fms_ref_no, p_email, fileExt));

		IOUtils.copy(new FileInputStream(report), response.getOutputStream());
		response.flushBuffer();
	}

	// get DI aging queue listing report to check if status pending or in progress
	// exist
	// @Secured("ROLE_USER")
	@PostMapping(value = "/getdiagequeuerpt")
	public ResponseEntity<ApiResponse<Integer>> sp_getdiagequeuerpt(HttpServletRequest request) {

		Integer result = -1;

		// try {
			if (!authService.isAuthenticated(request)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			result = diAgingRepSvc.sp_getdiagequeuerpt();

			if (result < 0) {
				return APIResponse.InternalServerError();
			}

			return APIResponse.SuccessResponse(result);

		// } catch (NumberFormatException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InvalidFormat();

		// } catch (Exception e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InternalServerError();

		// }
	}

	// // get pending or in progress DI aging report
	// // @Secured("ROLE_USER")
	// @PostMapping(value = "/getpendingdiagingrpt")
	// public ResponseEntity<ApiResponse<List<DeferredIncomeAging>>> sp_getpendingdiagingrpt(HttpServletRequest request) {

	// 	List<DeferredIncomeAging> result = Collections.emptyList();

	// 	// try {
	// 		if (!authService.isAuthenticated(request)) {
	// 			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
	// 		}

	// 		result = diAgingRepSvc.sp_getpendingdiagingrpt();

	// 		if (result.isEmpty()) {
	// 			return APIResponse.NoDataFound(ControllersEnum.RECON_REPORT_AND_ACCOUNT);
	// 		}

	// 		return APIResponse.SuccessResponse(result);
	// }

		// get pending or in progress DI aging report
	// @Secured("ROLE_USER")

	@PostMapping(value = "/downloaddiagingrpt", consumes = MediaType.APPLICATION_JSON_VALUE)
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


	public static String convertDateTime(String dateTime) {
		if (dateTime == null || dateTime.isEmpty()) {
			return null;
		}
		// try {
			// Define the input and output formatters
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
			DateTimeFormatter alternativeInputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			// Parse the input datetime string to LocalDateTime
			LocalDateTime localDateTime;
			try {
				localDateTime = LocalDateTime.parse(dateTime, inputFormatter);
			} catch (DateTimeParseException e) {
				// Attempt to parse using the alternative formatter
				localDateTime = LocalDateTime.parse(dateTime, alternativeInputFormatter);
			}

			// Format the LocalDateTime to the desired output format
			return localDateTime.format(outputFormatter);
		// } catch (DateTimeParseException e) {
		// 	// Handle the exception according to your need
		// 	e.printStackTrace();
		// 	return null;
		// }
	}

	// get DI aging pending record using id to know if it still in pending status
	// @Secured("ROLE_USER")
	@PostMapping(value = "/getpendingdiagingrptbyid")
	public ResponseEntity<ApiResponse<Integer>> sp_getpendingdiagingrptbyid(HttpServletRequest request,
			@RequestBody DeferredIncomeAgingRequest DIRequest) {

		Integer result = -1;

		// try {
			if (!authService.isAuthenticated(request)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			result = diAgingRepSvc.sp_getpendingdiagingrptbyid(
					DIRequest.getI_rpt_di_age_id());

			if (result < 0) {
				return APIResponse.InternalServerError();
			}

			return APIResponse.SuccessResponse(result);

		// } catch (NumberFormatException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InvalidFormat();

		// } catch (Exception e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InternalServerError();

		// }
	}

	// Insert RIPL aging report
	// @Secured("ROLE_USER")
	@PostMapping(value = "/insertriplagingrpt")
	public ResponseEntity<ApiResponse<BigInteger>> sp_insriplagingrpt(HttpServletRequest request,
			@RequestBody RIPLAgingRequest RIPLRequest) {

		BigInteger result = BigInteger.ZERO;
		String email = null;

		// try {
			if (!authService.isAuthenticated(request)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			if (RIPLRequest.getI_email_ntfn() == 1) {
				email = authService.getUserEmail();
			} else {
				email = null;
			}

			result = riplAgingService.sp_insriplagingrpt(
					RIPLRequest,
					email,
					authService.getLoginUserName(), // created_by
					authService.getLoginUserName() // modified_by
			);

			if (result.compareTo(BigInteger.ZERO) <= 0) {
				return APIResponse.InternalServerError();
			}
			return APIResponse.SuccessResponse(result);

		// } catch (NumberFormatException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InvalidFormat();

		// } catch (Exception e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InternalServerError();

		// }
	}

	// Get RIPL aging listing report
	// @Secured("ROLE_USER")
	@PostMapping(value = "/getriplaginglistingrpt")
	public ResponseEntity<ApiResponse<List<RiplAging>>> sp_getriplaginglistingrpt(HttpServletRequest request,
			@RequestBody RIPLAgingRequest RIPLRequest) {

		List<RiplAging> result = Collections.emptyList();

		// try {
			if (!authService.isAuthenticated(request)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			result = riplAgingService.sp_getriplaginglistingrpt(
					RIPLRequest);

			if (result.isEmpty()) {
				return APIResponse.NoDataFound(ControllersEnum.RECON_REPORT_AND_ACCOUNT);
			}

			return APIResponse.SuccessResponse(result);

		// } catch (NumberFormatException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InvalidFormat();

		// } catch (Exception e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InternalServerError();

		// }
	}

	// Update RIPL aging report
	// @Secured("ROLE_USER")
	@PostMapping(value = "/updateriplagingrpt")
	public ResponseEntity<ApiResponse<Integer>> sp_updriplagingrpt(HttpServletRequest request,
			@RequestBody RIPLAgingRequest RIPLRequest) {

		Integer result = 0;

		// try {
			if (!authService.isAuthenticated(request)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			RIPLRequest.setI_modified_by(authService.getLoginUserName());

			// result = spService.sp_updriplagingrpt(
			// RIPLRequest.getI_rpt_ripl_age_id(),
			// RIPLRequest.getI_status(),
			// RIPLRequest.getI_p_file_size(),
			// RIPLRequest.getI_p_file_nm(),
			// authService.getLoginUserName() //modified_by
			// );

			result = riplAgingService.sp_updriplagingrpt(RIPLRequest);

			if (result <= 0) {
				return APIResponse.InternalServerError();
			}

			return APIResponse.SuccessResponse(result);

		// } catch (NumberFormatException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InvalidFormat();

		// } catch (Exception e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InternalServerError();

		// }
	}

	// Get RIPL aging single row
	// @Secured("ROLE_USER")
	@PostMapping(value = "/getriplagingrpt")
	public ResponseEntity<ApiResponse<List<RiplAging>>> sp_getriplagingrpt(HttpServletRequest request,
			@RequestBody RIPLAgingRequest RIPLRequest) {

		List<RiplAging> result = Collections.emptyList();

		// try {
			if (!authService.isAuthenticated(request)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			result = riplAgingService.sp_getriplagingrpt(
					RIPLRequest.getI_rpt_ripl_age_id());

			if (result.isEmpty()) {
				return APIResponse.NoDataFound(ControllersEnum.RECON_REPORT_AND_ACCOUNT);
			}

			return APIResponse.SuccessResponse(result);

		// } catch (NumberFormatException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InvalidFormat();

		// } catch (Exception e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InternalServerError();

		// }
	}

	// generate ripl aging report
	@PostMapping(value = "/generateriplagingrpt", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void riplAgingReport(HttpServletResponse response, HttpServletRequest request,
			@Valid @RequestBody Map<String, Object> riplload) throws IOException, JRException, SQLException {
		// try {
			if (!authService.isAuthenticated(request))
				return;
		// } catch (NumberFormatException e) {
		// 	return;
		// } catch (Exception e) {
		// 	return;
		// }

		if (!riplload.containsKey("i_rpt_ripl_age_id"))
			return;

		Integer intValue = (Integer) riplload.get("i_rpt_ripl_age_id");
		BigInteger bigIntValue = BigInteger.valueOf(intValue);

		List<RiplAging> riplAgingList = riplAgingService.sp_getriplagingrpt(bigIntValue);

		String p_dt_req = null;
		Integer p_imp_status = null;
		Integer p_exp_status = null;
		String p_ent_ty = null;
		String p_ent_nm = null;
		String p_dt_due_fr = null;
		String p_dt_due_to = null;
		String p_dt_rcpt_fr = null;
		String p_dt_rcpt_to = null;
		String p_dt_imp_fr = null;
		String p_dt_imp_to = null;
		String p_dt_wo_fr = null;
		String p_dt_wo_to = null;
		String dt_created = null;
		String dt_modified = null;
		String created_by = null;
		String modified_by = null;
		String status = null;
		String p_email = null;
		String p_file_type = null;
		String p_file_nm = null;

		if (!riplAgingList.isEmpty()) {

			if (riplAgingList.get(0).getP_dt_req() != null) {
				p_dt_req = convertDateTime(riplAgingList.get(0).getP_dt_req().toString());
			}

			p_imp_status = riplAgingList.get(0).getP_imp_status();
			p_exp_status = riplAgingList.get(0).getP_exp_status();
			p_ent_ty = riplAgingList.get(0).getP_ent_ty();
			p_ent_nm = riplAgingList.get(0).getP_ent_nm();

			if (riplAgingList.get(0).getP_dt_due_fr() != null) {
				p_dt_due_fr = convertDateTime(riplAgingList.get(0).getP_dt_due_fr().toString());
			}

			if (riplAgingList.get(0).getP_dt_due_to() != null) {
				p_dt_due_to = convertDateTime(riplAgingList.get(0).getP_dt_due_to().toString());
			}

			if (riplAgingList.get(0).getP_dt_rcpt_fr() != null) {
				p_dt_rcpt_fr = convertDateTime(riplAgingList.get(0).getP_dt_rcpt_fr().toString());
			}

			if (riplAgingList.get(0).getP_dt_rcpt_to() != null) {
				p_dt_rcpt_to = convertDateTime(riplAgingList.get(0).getP_dt_rcpt_to().toString());
			}

			if (riplAgingList.get(0).getP_dt_imp_fr() != null) {
				p_dt_imp_fr = convertDateTime(riplAgingList.get(0).getP_dt_imp_fr().toString());
			}

			if (riplAgingList.get(0).getP_dt_imp_to() != null) {
				p_dt_imp_to = convertDateTime(riplAgingList.get(0).getP_dt_imp_to().toString());
			}

			if (riplAgingList.get(0).getP_dt_wo_fr() != null) {
				p_dt_wo_fr = convertDateTime(riplAgingList.get(0).getP_dt_wo_fr().toString());
			}

			if (riplAgingList.get(0).getP_dt_wo_to() != null) {
				p_dt_wo_to = convertDateTime(riplAgingList.get(0).getP_dt_wo_to().toString());
			}

			if (riplAgingList.get(0).getDt_created() != null) {
				dt_created = convertDateTime(riplAgingList.get(0).getDt_created().toString());
			}

			if (riplAgingList.get(0).getDt_modified() != null) {
				dt_modified = convertDateTime(riplAgingList.get(0).getDt_modified().toString());
			}

			created_by = riplAgingList.get(0).getCreated_by();
			modified_by = riplAgingList.get(0).getModified_by();
			status = riplAgingList.get(0).getStatus();
			p_email = riplAgingList.get(0).getP_email();
			p_file_type = riplAgingList.get(0).getP_file_type();
			p_file_nm = riplAgingList.get(0).getP_file_nm();

		} // re-check the code

		String fileExt = p_file_type;
		String contentType = fileExt.equals("pdf") ? "application/pdf"
				: fileExt.equals("xlsx") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
						: fileExt.equals("xls") ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
								: "application/octet-stream";

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate createdDateConvert = LocalDate.parse(p_dt_req, inputFormatter);

		// DateTimeFormatter outputFormatter =
		// DateTimeFormatter.ofPattern("dd-MMM-yyyy");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		String createdDateString = createdDateConvert.format(outputFormatter);

		// String fileName = "RIPL_Aging_" + createdDateString + "." + fileExt;
		String fileName = p_file_nm;

		response.setContentType(contentType);
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

		File report = riplAG.generateRIPLAgingReport(
				new ReportRequest(fileName, p_dt_req, p_dt_due_fr, p_dt_due_to, p_imp_status, p_exp_status,
						p_ent_ty, p_ent_nm, p_dt_rcpt_fr, p_dt_rcpt_to, p_dt_imp_fr, p_dt_imp_to, p_dt_wo_fr,
						p_dt_wo_to, p_email, fileExt));

		IOUtils.copy(new FileInputStream(report), response.getOutputStream());
		response.flushBuffer();
	}

	// get RIPL aging queue listing report to check if status pending or in progress
	// exist
	// @Secured("ROLE_USER")
	@PostMapping(value = "/getriplagequeuerpt")
	public ResponseEntity<ApiResponse<Integer>> sp_getriplagequeuerpt(HttpServletRequest request) {

		Integer result = -1;

		// try {
			if (!authService.isAuthenticated(request)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			result = riplAgingService.sp_getriplagequeuerpt();

			if (result < 0) {
				return APIResponse.InternalServerError();
			}

			return APIResponse.SuccessResponse(result);

		// } catch (NumberFormatException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InvalidFormat();

		// } catch (Exception e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InternalServerError();

		// }
	}

	// get pending or in progress RIPL aging report
	// @Secured("ROLE_USER")
	@PostMapping(value = "/getpendingriplagingrpt")
	public ResponseEntity<ApiResponse<List<RiplAging>>> sp_getpendingriplagingrpt(HttpServletRequest request) {

		List<RiplAging> result = Collections.emptyList();

		// try {
			if (!authService.isAuthenticated(request)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			result = riplAgingService.sp_getpendingriplagingrpt();

			if (result.isEmpty()) {
				return APIResponse.NoDataFound(ControllersEnum.RECON_REPORT_AND_ACCOUNT);
			}

			return APIResponse.SuccessResponse(result);

		// } catch (NumberFormatException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InvalidFormat();

		// } catch (Exception e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InternalServerError();

		// }
	}

	// get DI aging pending record using id to know if it still in pending status
	// @Secured("ROLE_USER")
	@PostMapping(value = "/getpendingriplagingrptbyid")
	public ResponseEntity<ApiResponse<Integer>> sp_getpendingriplagingrptbyid(HttpServletRequest request,
			@RequestBody RIPLAgingRequest RIPLRequest) {

		Integer result = -1;

		// try {
			if (!authService.isAuthenticated(request)) {
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
			}

			result = riplAgingService.sp_getpendingriplagingrptbyid(
					RIPLRequest.getI_rpt_ripl_age_id());

			if (result < 0) {
				return APIResponse.InternalServerError();
			}

			return APIResponse.SuccessResponse(result);

		// } catch (NumberFormatException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InvalidFormat();

		// } catch (Exception e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// 	return APIResponse.InternalServerError();

		// }
	}
}
