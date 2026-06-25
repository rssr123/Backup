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
import com.maven.rms.models.BillingReportRequest;
import com.maven.rms.models.SummaryBillingReportRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.utils.reports.BillingReportGenerator;
import com.maven.rms.utils.reports.DetailedBilBillingTypeReportGenerator;
import com.maven.rms.utils.reports.DetailedBilClassIdReportGenerator;
import com.maven.rms.utils.reports.SummaryBillingReportGenerator;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;

@RestController
@RequestMapping("api/blr/v1")
@Slf4j
public class BillingReportController {

    @Autowired
    private AuthService authService;

    @Autowired
    private BillingReportGenerator billingReportGenerator;

    @Autowired
    private SummaryBillingReportGenerator summaryBillingReportGenerator;

    @Autowired
    private DetailedBilClassIdReportGenerator detailedBilClassIdReportGenerator;

    @Autowired
    private DetailedBilBillingTypeReportGenerator detailedBilBillingTypeReportGenerator;

    @Value("${jasper.reports.directory}")
    private String report_directory;

    public BillingReportController() {
        log.info("ReportController services is started");
    }

    @PostMapping(value = "/billingReport", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void billingReport(HttpServletResponse response, HttpServletRequest request,
                              @Valid @RequestBody Map<String, Object> payload)
            throws IOException, JRException, SQLException {

        if (!authService.isAuthenticated(request)) {
            return;
        }

        if (!payload.containsKey("i_report_format")
                || !payload.containsKey("i_date_range_start")
                || !payload.containsKey("i_date_range_end")) {
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

        // String fileName = "BillingReport"
        //         + "-" + startDateString + "-" + endDateString
        //         + "-" + ((String) payload.getOrDefault("i_billing_category", ""))
        //         + "-" + ((String) payload.getOrDefault("i_payment_status", ""))
        //         + "-" + ((String) payload.getOrDefault("i_billing_no", ""))
        //         + "-" + ((String) payload.getOrDefault("i_entity_customer_id", ""))
        //         + "." + fileExt;

        String category = (payload.get("i_billing_category") != null) ? payload.get("i_billing_category").toString() : "";
        String paymentStatus = (payload.get("i_payment_status") != null) ? payload.get("i_payment_status").toString() : "";
        String billingNo = (payload.get("i_billing_no") != null) ? payload.get("i_billing_no").toString() : "";
        String customerId = (payload.get("i_entity_customer_id") != null) ? payload.get("i_entity_customer_id").toString() : "";
        
        String fileName = "BillingReport"
                + "-" + startDateString
                + "-" + endDateString
                + (category.isEmpty() ? "" : "-" + category)
                + (paymentStatus.isEmpty() ? "" : "-" + paymentStatus)
                + (billingNo.isEmpty() ? "" : "-" + billingNo)
                + (customerId.isEmpty() ? "" : "-" + customerId)
                + "." + fileExt;
        

        response.setContentType(contentType);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        File report = billingReportGenerator.generateReport(new BillingReportRequest(
                fileName,
                fileExt,
                (String) dateFromObj,
                (String) dateToObj,
                payload.containsKey("i_billing_category") ? (String) payload.get("i_billing_category") : null,
                payload.containsKey("i_payment_status") ? (String) payload.get("i_payment_status") : null,
                payload.containsKey("i_billing_no") ? (String) payload.get("i_billing_no") : null,
                payload.containsKey("i_entity_customer_id") ? (String) payload.get("i_entity_customer_id") : null
        ));

        IOUtils.copy(new FileInputStream(report), response.getOutputStream());
        response.flushBuffer();
    }


    @PostMapping(value = "/summaryBillingReport", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void summaryBillingReport(HttpServletResponse response, HttpServletRequest request,
                              @Valid @RequestBody Map<String, Object> payload)
            throws IOException, JRException, SQLException {

        if (!authService.isAuthenticated(request)) {
            return;
        }

        if (!payload.containsKey("i_report_format")
                || !payload.containsKey("i_date_range_start")
                || !payload.containsKey("i_date_range_end")) {
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

        // String fileName = "SummaryBillingReport"
        //         + "-" + startDateString + "-" + endDateString
        //         + "-" + ((String) payload.getOrDefault("i_class_id_selection", ""))
        //         + "-" + ((String) payload.getOrDefault("i_entity_customer_id", ""))
        //         + "." + fileExt;
        String classId = (payload.get("i_class_id_selection") != null) ? payload.get("i_class_id_selection").toString() : "";
        String customerId = (payload.get("i_entity_customer_id") != null) ? payload.get("i_entity_customer_id").toString() : "";
        String billingStatus = (payload.get("i_billing_status") != null) ? payload.get("i_billing_status").toString() : "";

        String fileName = "SummaryBillingReport"
                + "-" + startDateString
                + "-" + endDateString
                + (classId.isEmpty() ? "" : "-" + classId)
                + (customerId.isEmpty() ? "" : "-" + customerId)
                + (billingStatus.isEmpty() ? "" : "-" + billingStatus)
                + "." + fileExt;
        
        response.setContentType(contentType);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        File report = summaryBillingReportGenerator.generateReport(new SummaryBillingReportRequest(
                fileName,
                fileExt,
                (String) dateFromObj,
                (String) dateToObj,
                payload.containsKey("i_class_id_selection") ? (String) payload.get("i_class_id_selection") : null,
                payload.containsKey("i_entity_customer_id") ? (String) payload.get("i_entity_customer_id") : null,
                payload.containsKey("i_billing_status") ? (String) payload.get("i_billing_status") : null
        ));

        IOUtils.copy(new FileInputStream(report), response.getOutputStream());
        response.flushBuffer();
    }




    @PostMapping(value = "/dblclassidreport", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void detailedBilClassIdReport(HttpServletResponse response, HttpServletRequest request,
                              @Valid @RequestBody Map<String, Object> payload)
            throws IOException, JRException, SQLException {

        if (!authService.isAuthenticated(request)) {
            return;
        }

        if (!payload.containsKey("i_report_format")
                || !payload.containsKey("i_date_range_start")
                || !payload.containsKey("i_date_range_end")) {
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

        // String fileName = "DetailedBillingClassIdReport"
        //         + "-" + startDateString + "-" + endDateString
        //         + "-" + ((String) payload.getOrDefault("i_class_id_selection", ""))
        //         + "-" + ((String) payload.getOrDefault("i_entity_customer_id", ""))
        //         + "-" + ((String) payload.getOrDefault("i_payment_status", ""))
        //         + "." + fileExt;

        String classId = (payload.get("i_class_id_selection") != null) ? payload.get("i_class_id_selection").toString() : "";
        String customerId = (payload.get("i_entity_customer_id") != null) ? payload.get("i_entity_customer_id").toString() : "";
        String paymentStatus = (payload.get("i_payment_status") != null) ? payload.get("i_payment_status").toString() : "";
        
        String fileName = "DetailedBillingClassIdReport"
                + "-" + startDateString
                + "-" + endDateString
                + (classId.isEmpty() ? "" : "-" + classId)
                + (customerId.isEmpty() ? "" : "-" + customerId)
                + (paymentStatus.isEmpty() ? "" : "-" + paymentStatus)
                + "." + fileExt;
        

        response.setContentType(contentType);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        File report = detailedBilClassIdReportGenerator.generateReport(new BillingReportRequest(
                fileName,
                fileExt,
                (String) dateFromObj,
                (String) dateToObj,
                payload.containsKey("i_class_id_selection") ? (String) payload.get("i_class_id_selection") : null,
                payload.containsKey("i_entity_customer_id") ? (String) payload.get("i_entity_customer_id") : null,
                payload.containsKey("i_payment_status") ? (String) payload.get("i_payment_status") : null
        ));

        IOUtils.copy(new FileInputStream(report), response.getOutputStream());
        response.flushBuffer();
    }


    @PostMapping(value = "/dbltypereport", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void detailedBilBillingTypeReport(HttpServletResponse response, HttpServletRequest request,
                              @Valid @RequestBody Map<String, Object> payload)
            throws IOException, JRException, SQLException {

        if (!authService.isAuthenticated(request)) {
            return;
        }

        if (!payload.containsKey("i_report_format")
                || !payload.containsKey("i_date_range_start")
                || !payload.containsKey("i_date_range_end")) {
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

        String fileName = "DetailedBilBillingTypeReport"
                + "-" + startDateString + "-" + endDateString
                + "-" + ((String) payload.getOrDefault("i_billing_category", ""))
                + "." + fileExt;

        response.setContentType(contentType);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        File report = detailedBilBillingTypeReportGenerator.generateReport(new BillingReportRequest(
                fileName,
                fileExt,
                (String) dateFromObj,
                (String) dateToObj,
                payload.containsKey("i_billing_category") ? (String) payload.get("i_billing_category") : null
        ));

        IOUtils.copy(new FileInputStream(report), response.getOutputStream());
        response.flushBuffer();
    }
}
