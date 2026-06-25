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
public class RIPLAgingReportGenerator {
    //private static final Logger logger = LoggerFactory.getLogger(PaymentCollectionReportGenerator.class);
    private final String report_template_path = "/jasper";
    private final String ripl_report = report_template_path + "/RIPL_Aging.jrxml";
    private String report_directory;

    @Autowired
	private DataSource ds;

	@Value("${spring.mail.username}")
    private String from;

    public RIPLAgingReportGenerator(@Value("${jasper.reports.directory}") String report_directory){
        this.report_directory = report_directory;
        log.info("Starting ReportReconAndAccountGenerator...");
    }

    public File generateRIPLAgingReport(ReportRequest r) throws IOException, JRException, SQLException{
		File report = new File(report_directory + r.getFileName());

		// try {
            

		return r.getFileType().equals("pdf") ? generatePDFReport(report, loadTemplateRIPLAging(), reportRIPLAgingParameters(r)) :
			r.getFileType().equals("xlsx") ? generateXLSXReport(report, loadTemplateRIPLAging(), reportRIPLAgingParameters(r)) :
		  	r.getFileType().equals("xls") ? generateXLSXReport(report, loadTemplateRIPLAging(), reportRIPLAgingParameters(r)) : 
		   r.getFileType().equals("csv") ? generateCSVReport(report, loadTemplateRIPLAging(), reportRIPLAgingParameters(r)) : null;
		// } catch (IOException e) {	
		// log.error("Exception in " + this.getClass().toString(), e);
		// } catch (JRException e) {
		// log.error("Exception in " + this.getClass().toString(), e);
		// }
		// return null;
		}

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
			// configuration.setOnePagePerSheet(true);
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

    private JasperReport loadTemplateRIPLAging() throws JRException {
        InputStream reportInputStream;
        reportInputStream = getClass().getResourceAsStream(ripl_report);	
        JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);
        return JasperCompileManager.compileReport(jasperDesign);
    }

    private Map<String, Object> reportRIPLAgingParameters(ReportRequest r) {
		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("createddate", r.getCreateddate());
		parameters.put("duedatestart", r.getDuedatestart());
		parameters.put("duedateend", r.getDuedateend());
		parameters.put("impairstatus", r.getImpairstatus());
		parameters.put("writeoffstatus", r.getWriteoffstatus());
		parameters.put("entitytype", r.getEntitytype());
		parameters.put("entitynm", r.getEntitynm());
		parameters.put("receiptdatestart", r.getReceiptdatestart());
		parameters.put("receiptdateend", r.getReceiptdateend());
		parameters.put("impairdatestart", r.getImpairdatestart());
		parameters.put("impairdateend", r.getImpairdateend());
		parameters.put("writeoffdatestart", r.getWriteoffdatestart());
		parameters.put("writeoffdateend", r.getWriteoffdateend());
		parameters.put("email", r.getEmail());
	
															
		return parameters;
	}
    
}
