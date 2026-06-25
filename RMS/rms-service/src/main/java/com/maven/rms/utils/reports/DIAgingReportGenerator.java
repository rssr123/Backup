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
public class DIAgingReportGenerator {
    //private static final Logger logger = LoggerFactory.getLogger(PaymentCollectionReportGenerator.class);
    private final String report_template_path = "/jasper";
    private final String di_report = report_template_path + "/Deferred_Income_Aging.jrxml";
    private String report_directory;

    @Autowired
	private DataSource ds;

	@Value("${spring.mail.username}")
    private String from;

    public DIAgingReportGenerator(@Value("${jasper.reports.directory}") String report_directory){
        this.report_directory = report_directory;
        log.info("Starting ReportReconAndAccountGenerator...");
    }

    public File generateDIAgingReport(ReportRequest r) throws IOException, JRException, SQLException{
		File report = new File(report_directory + r.getFileName());

		// try {
            

		return r.getFileType().equals("pdf") ? generatePDFReport(report, loadTemplateDIAging(), reportDIAgingParameters(r)) :
			r.getFileType().equals("xlsx") ? generateXLSXReport(report, loadTemplateDIAging(), reportDIAgingParameters(r)) :
		  	r.getFileType().equals("xls") ? generateXLSXReport(report, loadTemplateDIAging(), reportDIAgingParameters(r)) : 
		   r.getFileType().equals("csv") ? generateCSVReport(report, loadTemplateDIAging(), reportDIAgingParameters(r)) : null;
		} 
		// catch (IOException e) {	
		// log.error("Exception in " + this.getClass().toString(), e);
		// } catch (JRException e) {
		// log.error("Exception in " + this.getClass().toString(), e);
		// }
		// return null;
		// }

	public File generatePDFReport(File file, JasperReport report, Map<String, Object> reportParameters) throws IOException, JRException, SQLException {
		// try{
			JasperPrint jasperPrint = JasperFillManager.fillReport(report, reportParameters, ds.getConnection());
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
			SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
			configuration.setOnePagePerSheet(true);
			configuration.setIgnoreGraphics(false);
			configuration.setRemoveEmptySpaceBetweenRows(true);
			
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

    private JasperReport loadTemplateDIAging() throws JRException {
        InputStream reportInputStream;
        reportInputStream = getClass().getResourceAsStream(di_report);	
        JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);
        return JasperCompileManager.compileReport(jasperDesign);
    }

    private Map<String, Object> reportDIAgingParameters(ReportRequest r) {
		

		// log.error("receiptdateend parameters: " + r.getReceiptdateend());
		// log.error("effectivedatestart parameters: " + r.getEffectivedatestart());
		// log.error("effectivedateend parameters: " + r.getEffectivedateend());


		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("receiptdateend",  r.getReceiptdateend());
		parameters.put("effectivedatestart",  r.getEffectivedatestart());
		parameters.put("effectivedateend",  r.getEffectivedateend());
		parameters.put("distatus", r.getDistatus());
		parameters.put("inctermstatus",  r.getInctermstatus());
		parameters.put("entitytype",  r.getEntitytype());
		parameters.put("entitynm",  r.getEntitynm());
		parameters.put("txntype",  r.getTxntype());
		parameters.put("expirydatestart",  r.getExpirydatestart());
		parameters.put("expirydateend",  r.getExpirydateend());
		parameters.put("approvaldatestart",  r.getApprovaldatestart());
		parameters.put("approvaldateend",  r.getApprovaldateend());
		parameters.put("terminationdatestart",  r.getTerminationdatestart());
		parameters.put("terminationdateend",  r.getTerminationdateend());
		parameters.put("batchno",  r.getBatchno());
		parameters.put("fmsrefno",  r.getFmsrefno());
		parameters.put("email",  r.getEmail());
															
		return parameters;
	}
    
}
