package com.maven.rms.utils.reports;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.maven.rms.models.RefundStatusRequest;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleCsvExporterConfiguration;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleWriterExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Service
@Slf4j
public class RefundStatusDetailedReportGenerator {

    private final String report = "/jasper/Refund_Status_Detailed_Report.jrxml";
	private String report_directory;

	@Autowired
	private DataSource ds;

    public RefundStatusDetailedReportGenerator(@Value("${jasper.reports.directory}") String report_directory) {
        this.report_directory = report_directory;
        log.info("Starting RefundStatusDetailedReportGenerator...");
    }

    public File generateReport(RefundStatusRequest r) throws IOException, JRException, SQLException {
		File report = new File(report_directory + r.getFileName());

            return 
            r.getFileType().equals("pdf") ? generatePDFReport(report, loadTemplate(r.getReportType()), reportParameters(r)) :
            r.getFileType().equals("xlsx") ? generateXLSXReport(report, loadTemplate(r.getReportType()), reportParameters(r)) :
            r.getFileType().equals("xls") ? generateXLSXReport(report, loadTemplate(r.getReportType()), reportParameters(r)) : 
            r.getFileType().equals("csv") ? generateCSVReport(report, loadTemplate(r.getReportType()), reportParameters(r)) : null;
    }

    public File generatePDFReport(File file, JasperReport report, Map<String, Object> reportParameters) throws IOException, JRException, SQLException {
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, reportParameters, ds.getConnection());
        JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
        return file;
    }

    public File generateXLSXReport(File file, JasperReport report, Map<String, Object> reportParameters) throws IOException, JRException, SQLException {
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            OutputStream fileOutputStream = new FileOutputStream(file)){
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, reportParameters, ds.getConnection());
            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
            configuration.setOnePagePerSheet(true);
            configuration.setIgnoreGraphics(false);
            
            JRXlsxExporter jrXlsxExporter = new JRXlsxExporter();
            jrXlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
            jrXlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
            jrXlsxExporter.setConfiguration(configuration);
            jrXlsxExporter.exportReport();
            byteArrayOutputStream.writeTo(fileOutputStream);
            return file;
        }
    }

    public File generateCSVReport(File file, JasperReport report, Map<String, Object> reportParameters) throws IOException, JRException, SQLException {
        JasperPrint jasperPrint = JasperFillManager.fillReport(report, reportParameters, ds.getConnection());
        SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
        configuration.setWriteBOM(Boolean.TRUE);
        configuration.setRecordDelimiter("\r\n");

        JRCsvExporter jrCsvExporter = new JRCsvExporter();
        jrCsvExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        jrCsvExporter.setExporterOutput(new SimpleWriterExporterOutput(file));
        jrCsvExporter.setConfiguration(configuration);
        jrCsvExporter.exportReport();
        return file;
    }

    private JasperReport loadTemplate(String reportType) throws JRException {
        InputStream reportInputStream;

        reportInputStream = getClass().getResourceAsStream(report);

        JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);
        return JasperCompileManager.compileReport(jasperDesign);
    }

    private Map<String, Object> reportParameters(RefundStatusRequest request) {
        final Map<String, Object> parameters = new HashMap<>();

        parameters.put("datestart",  request.getDateStart());
        parameters.put("dateend", request.getDateEnd());
        parameters.put("refundstatus", request.getRefundStatus());
        parameters.put("refundtype", request.getRefundType());

        return parameters;
    }
}



