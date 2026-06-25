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

import com.maven.rms.models.ReportRequest;

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
public class PaymentCollectionReportGenerator {
	//private static final Logger logger = LoggerFactory.getLogger(PaymentCollectionReportGenerator.class);
	private final String report_template_path = "/jasper/Payment_Collection-(";
	private final String ss_report = report_template_path + "Source_System).jrxml";
	private final String fd_report = report_template_path + "FD).jrxml";
	private final String md_report = report_template_path + "Pymt_Md).jrxml";
	private String report_directory;
	
	@Autowired
	private DataSource ds;
	
	public PaymentCollectionReportGenerator(@Value("${jasper.reports.directory}") String report_directory){
		this.report_directory = report_directory;
		log.info("Starting PaymentCollectionReportGenerator...");
	}
	
	public File generateReport(ReportRequest r) throws IOException, JRException, SQLException{
		File report = new File(report_directory + r.getFileName());

		// try {
			return r.getFileType().equals("pdf") ? generatePDFReport(report, loadTemplate(r.getReportType()), reportParameters(r)) :
				r.getFileType().equals("xlsx") ? generateXLSXReport(report, loadTemplate(r.getReportType()), reportParameters(r)) :
				r.getFileType().equals("xls") ? generateXLSXReport(report, loadTemplate(r.getReportType()), reportParameters(r)) : 
				r.getFileType().equals("csv") ? generateCSVReport(report, loadTemplate(r.getReportType()), reportParameters(r)) : null;
		// } catch (IOException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// } catch (JRException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// }
		// return null;
	}
	
	public File generatePDFReport(File file, JasperReport report, Map<String, Object> reportParameters) throws IOException, JRException, SQLException {
		// try{
			JasperPrint jasperPrint = JasperFillManager.fillReport(report, reportParameters, ds.getConnection());
			if(jasperPrint.getPages().size() == 0)
				return null;

			JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
			
			return file;
		// }
		// catch (final Exception e){
		//     log.error("Exception in " + this.getClass().toString(), e);
		// 	throw new IllegalArgumentException("Failed to generate the PDF report!");
		// }
	}
	
	public File generateXLSXReport(File file, JasperReport report, Map<String, Object> reportParameters) throws IOException, JRException, SQLException {
		try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				OutputStream fileOutputStream = new FileOutputStream(file)){
			JasperPrint jasperPrint = JasperFillManager.fillReport(report, reportParameters, ds.getConnection());
			if(jasperPrint.getPages().size() == 0)
				return null;
			
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
		// catch (final Exception e){
		//     log.error("Exception in " + this.getClass().toString(), e);
		// 	throw new IllegalArgumentException("Failed to generate the XLSX File!");
		// }
	}
	
	public File generateCSVReport(File file, JasperReport report, Map<String, Object> reportParameters) throws IOException, JRException, SQLException {
		// try{
			JasperPrint jasperPrint = JasperFillManager.fillReport(report, reportParameters, ds.getConnection());
			if(jasperPrint.getPages().size() == 0)
				return null;
			
			SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
			configuration.setWriteBOM(Boolean.TRUE);
			configuration.setRecordDelimiter("\r\n");
			
			JRCsvExporter jrCsvExporter = new JRCsvExporter();
			jrCsvExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
			jrCsvExporter.setExporterOutput(new SimpleWriterExporterOutput(file));
			jrCsvExporter.setConfiguration(configuration);
			jrCsvExporter.exportReport();
			
			return file;
		// }
		// catch (final Exception e){
		//     log.error("Exception in " + this.getClass().toString(), e);
		// 	throw new IllegalArgumentException("Failed to generate the CSV File!");
		// }
	}
	
	private JasperReport loadTemplate(String reportType) throws JRException {
		InputStream reportInputStream;
		
		if(reportType.equals("Payment Mode"))
			reportInputStream = getClass().getResourceAsStream(md_report);
		else if(reportType.equals("Source System"))
			reportInputStream = getClass().getResourceAsStream(ss_report);
		else if(reportType.equals("Fee Detail ID"))
			reportInputStream = getClass().getResourceAsStream(fd_report);
		else
			throw new JRException("UNKNOWN REPORT TYPE: " + reportType + "!");
		
		JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);
		return JasperCompileManager.compileReport(jasperDesign);
	}
	
	private Map<String, Object> reportParameters(ReportRequest r) {
		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("dateend",  r.getDateEnd());
		parameters.put("datestart", r.getDateStart());
		
		if(r.getPaymentMode() != null)
			parameters.put("paymentmode", r.getPaymentMode());
		
		return parameters;
	}
}
