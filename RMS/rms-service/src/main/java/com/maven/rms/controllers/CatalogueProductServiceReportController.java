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
import com.maven.rms.models.CatalogueProductServiceRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.utils.reports.CatalogueProductServiceReportGenerator;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;

@RestController
@RequestMapping("api/cpsr/v1")
@Slf4j
public class CatalogueProductServiceReportController {
    @Autowired
    private AuthService authService;

    @Autowired
    private CatalogueProductServiceReportGenerator catalogueProductServiceReportGenerator;

    @Value("${jasper.reports.directory}")
    private String report_directory;

    public CatalogueProductServiceReportController() {
        log.info("ReportController services is started");
    }

    @PostMapping(value = "/prodserviceReport", consumes = MediaType.APPLICATION_JSON_VALUE)
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

        // String fileName = "CatalogueProductServiceReport"
        //         + "-" + startDateString + "-" + endDateString
        //         + "-" + ((String) payload.getOrDefault("i_type_of_service", ""))
        //         + "-" + ((String) payload.getOrDefault("i_payment_status", ""))
        //         + "-" + ((String) payload.getOrDefault("i_entity_no", ""))
        //         + "." + fileExt;

        String serviceType = (payload.get("i_type_of_service") != null) ? payload.get("i_type_of_service").toString() : "";
        String paymentStatus = (payload.get("i_payment_status") != null) ? payload.get("i_payment_status").toString() : "";
        String entityNo = (payload.get("i_entity_no") != null) ? payload.get("i_entity_no").toString() : "";
        
        String fileName = "CatalogueProductServiceReport"
                + "-" + startDateString
                + "-" + endDateString
                + (serviceType.isEmpty() ? "" : "-" + serviceType)
                + (paymentStatus.isEmpty() ? "" : "-" + paymentStatus)
                + (entityNo.isEmpty() ? "" : "-" + entityNo)
                + "." + fileExt;
        

        response.setContentType(contentType);
        response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);

        File report = catalogueProductServiceReportGenerator.generateReport(new CatalogueProductServiceRequest(
                fileName,
                fileExt,
                (String) dateFromObj,
                (String) dateToObj,
                payload.containsKey("i_type_of_service") ? (String) payload.get("i_type_of_service") : null,
                payload.containsKey("i_payment_status") ? (String) payload.get("i_payment_status") : null,
                payload.containsKey("i_entity_no") ? (String) payload.get("i_entity_no") : null
        ));

        IOUtils.copy(new FileInputStream(report), response.getOutputStream());
        response.flushBuffer();
    }
}
