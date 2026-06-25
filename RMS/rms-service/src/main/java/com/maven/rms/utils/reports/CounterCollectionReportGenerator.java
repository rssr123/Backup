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

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

import com.maven.rms.models.OTCReportRequest;

@Service
@Slf4j
public class CounterCollectionReportGenerator {
    private final String report = "/jasper/Counter_Collection.jrxml";
    // private final String report_template_path = "/jasper/Counter_Collection-(";
	// private final String report1 = report_template_path + "1).jrxml";
	// private final String report2 = report_template_path + "2).jrxml";
	// private final String report3 = report_template_path + "3).jrxml";
	private String report_directory;
	
	@Autowired
	private DataSource ds;
	
	public CounterCollectionReportGenerator(@Value("${jasper.reports.directory}") String report_directory){
		this.report_directory = report_directory;
		log.info("Starting CounterCollectionReportGenerator...");
	}
	
	public File generateReport(OTCReportRequest r) throws IOException, JRException, SQLException{
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
		
		// if (reportType.equals("type1"))
		// 	reportInputStream = getClass().getResourceAsStream(report1);
		// else if (reportType.equals("type2"))
		// 	reportInputStream = getClass().getResourceAsStream(report2);
		// else if (reportType.equals("type3"))
		// 	reportInputStream = getClass().getResourceAsStream(report3);
		// else
		// 	throw new JRException("UNKNOWN REPORT TYPE: " + reportType + "!");

        reportInputStream = getClass().getResourceAsStream(report);
		
		JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);
		return JasperCompileManager.compileReport(jasperDesign);
	}
	
	private Map<String, Object> reportParameters(OTCReportRequest request) {
		final Map<String, Object> parameters = new HashMap<>();

		parameters.put("datefrom",  request.getDateFrom());
		parameters.put("dateto", request.getDateTo());
		
		if(request.getField1() != null)
			parameters.put("branchcode", request.getField1());

        if(request.getField2() != null)
            parameters.put("paymentmode", request.getField2());
		
		return parameters;
	}
}
