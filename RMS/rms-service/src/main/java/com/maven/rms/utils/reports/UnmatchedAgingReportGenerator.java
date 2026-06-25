package com.maven.rms.utils.reports;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
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
import com.maven.rms.models.UnmatchedAgingReportRequest;

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
public class UnmatchedAgingReportGenerator {
	//private static final Logger logger = LoggerFactory.getLogger(UnmatchedAgingReportGenerator.class);
	private final String report_template_path = "/jasper/Unmatched_Aging_Report.jrxml";
	private String report_directory;
	
	@Autowired
	private DataSource ds;
	
	public UnmatchedAgingReportGenerator(@Value("${jasper.reports.directory}") String report_directory){
		this.report_directory = report_directory;
		log.info("Starting UnmatchedAgingReportGenerator...");
	}
	
	public File generateReport(ReportRequest r) throws IOException, JRException, SQLException{
		File report = new File(report_directory + r.getFileName());
		
		// try {
			return r.getUarReq().getP_file_type().equals("pdf") ? generatePDFReport(report, loadTemplate(), reportParameters(r.getUarReq())) :
				r.getUarReq().getP_file_type().equals("xlsx") ? generateXLSXReport(report, loadTemplate(), reportParameters(r.getUarReq())) :
				r.getUarReq().getP_file_type().equals("xls") ? generateXLSXReport(report, loadTemplate(), reportParameters(r.getUarReq())) : 
				r.getUarReq().getP_file_type().equals("csv") ? generateCSVReport(report, loadTemplate(), reportParameters(r.getUarReq())) : null;
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
			JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
			return file;
		
		// catch (final Exception e){
		//     log.error("Exception in " + this.getClass().toString(), e);
		// 	throw new IllegalArgumentException("Failed to generate the PDF report!");
		// }
	}
	
	public File generateXLSXReport(File file, JasperReport report, Map<String, Object> reportParameters) throws IOException, JRException, SQLException {
		try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				OutputStream fileOutputStream = new FileOutputStream(file)){
			JasperPrint jasperPrint = JasperFillManager.fillReport(report, reportParameters, ds.getConnection());
			SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
			configuration.setOnePagePerSheet(false);
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
			SimpleCsvExporterConfiguration configuration = new SimpleCsvExporterConfiguration();
			configuration.setWriteBOM(Boolean.TRUE);
			configuration.setRecordDelimiter("\n");
			
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
	
	private JasperReport loadTemplate() throws JRException {
		InputStream reportInputStream;
		reportInputStream = getClass().getResourceAsStream(report_template_path);
				JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);
		return JasperCompileManager.compileReport(jasperDesign);
	}
	
	private Map<String, Object> reportParameters(UnmatchedAgingReportRequest req) {
		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("asOfDate", req.getP_dt_req().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		parameters.put("rcptNo", req.getP_rcpt_no());
		parameters.put("pgTxnId", req.getP_txn_id());
		parameters.put("stmtNo", req.getP_stmt_no());
		parameters.put("matchRec", req.getP_recon_status());
		parameters.put("pgDtStmtFr", req.getP_dt_stmt_fr() != null ? req.getP_dt_stmt_fr().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "");
		parameters.put("pgDtStmtTo", req.getP_dt_stmt_to() != null ? req.getP_dt_stmt_to().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "");
		parameters.put("showDupTrans", req.getP_dup() == 0 ? "f" : "t");
		
		return parameters;
	}
}
