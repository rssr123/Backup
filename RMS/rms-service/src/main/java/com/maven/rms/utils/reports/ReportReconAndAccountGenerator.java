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
public class ReportReconAndAccountGenerator {
    //private static final Logger logger = LoggerFactory.getLogger(PaymentCollectionReportGenerator.class);
	private final String report_template_path = "/jasper";
    private final String daily_report = report_template_path + "/Daily_Collection_Listing.jrxml";
	private final String matched_report = report_template_path + "/Matched_Transaction_Listing.jrxml";
	private final String pg_report = report_template_path + "/PG_Settlement_Disbursement_Listing.jrxml";
	private final String di_report = report_template_path + "/Deferred_Income_Aging.jrxml";
	private final String utl_report = report_template_path + "/Unmatched_Transaction_Listing.jrxml";
    private String report_directory;

	@Autowired
	private DataSource ds;

	@Value("${spring.mail.username}")
    private String from;

    public ReportReconAndAccountGenerator(@Value("${jasper.reports.directory}") String report_directory){
        this.report_directory = report_directory;
        log.info("Starting ReportReconAndAccountGenerator...");
    }

	public File generatUnmatchedTransListReport(ReportRequest r) throws IOException, JRException, SQLException{
		File report = new File(report_directory + r.getFileName());

		// try {

			return r.getFileType().equals("pdf") ? generatePDFReport(report, loadTemplate(r.getReportName()), reportUnmatchedTransListParameters(r)) :
				r.getFileType().equals("xlsx") ? generateXLSXReport(report, loadTemplate(r.getReportName()), reportUnmatchedTransListParameters(r)) :
				r.getFileType().equals("xls") ? generateXLSXReport(report, loadTemplate(r.getReportName()), reportUnmatchedTransListParameters(r)) : 
				r.getFileType().equals("csv") ? generateCSVReport(report, loadTemplate(r.getReportName()), reportUnmatchedTransListParameters(r)) : null;
		// } catch (IOException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// } catch (JRException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// }
		// return null;
	}

    public File generateDailyColLstReport(ReportRequest r) throws IOException, JRException, SQLException{
		File report = new File(report_directory + r.getFileName());

		// try {

			// if(fileType.equals("pdf")){
			// 	sendPDFReport(report, loadTemplate(reportName), reportDailyColLstParameters(dateStart, dateEnd, ornNo, pgPaymentID, pgTransID, pgTransStatus, pgTransStatusMsg, receiptNo, statementNo, reportName));
			// }
			return r.getFileType().equals("pdf") ? generatePDFReport(report, loadTemplate(r.getReportName()), reportDailyColLstParameters(r)) :
				r.getFileType().equals("xlsx") ? generateXLSXReport(report, loadTemplate(r.getReportName()), reportDailyColLstParameters(r)) :
				r.getFileType().equals("xls") ? generateXLSXReport(report, loadTemplate(r.getReportName()), reportDailyColLstParameters(r)) : 
				r.getFileType().equals("csv") ? generateCSVReport(report, loadTemplate(r.getReportName()), reportDailyColLstParameters(r)) : null;
		// } catch (IOException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// } catch (JRException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// }
		// return null;
	}

	public File generateMatTransLstReport(ReportRequest r) throws IOException, JRException, SQLException{
		File report = new File(report_directory + r.getFileName());

		// try {
			return r.getFileType().equals("pdf") ? generatePDFReport(report, loadTemplate(r.getReportName()), reportMatTransLstParameters(r)) :
				r.getFileType().equals("xlsx") ? generateXLSXReport(report, loadTemplate(r.getReportName()), reportMatTransLstParameters(r)) :
				r.getFileType().equals("xls") ? generateXLSXReport(report, loadTemplate(r.getReportName()), reportMatTransLstParameters(r)) : 
				r.getFileType().equals("csv") ? generateCSVReport(report, loadTemplate(r.getReportName()), reportMatTransLstParameters(r)) : null;
		// } catch (IOException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// } catch (JRException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// }
		// return null;
	}
	
	public File generatePGSetDisLstReport(ReportRequest r) throws IOException, JRException, SQLException{
		File report = new File(report_directory + r.getFileName());

		// try {
			
			return r.getFileType().equals("pdf") ? generatePDFReport(report, loadTemplate(r.getReportName()), reportPGSetDisLstParameters(r)) :
				r.getFileType().equals("xlsx") ? generateXLSXReport(report, loadTemplate(r.getReportName()), reportPGSetDisLstParameters(r)) :
				r.getFileType().equals("xls") ? generateXLSXReport(report, loadTemplate(r.getReportName()), reportPGSetDisLstParameters(r)) : 
				r.getFileType().equals("csv") ? generateCSVReport(report, loadTemplate(r.getReportName()), reportPGSetDisLstParameters(r)) : null;
		// } catch (IOException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// } catch (JRException e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// }
		// return null;
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
		 try{
			JasperPrint jasperPrint = JasperFillManager.fillReport(report, reportParameters, ds.getConnection());
			JasperExportManager.exportReportToPdfFile(jasperPrint, file.getAbsolutePath());
			return file;
		}
		catch (final Exception e){
		    log.error(" Time out is here Exception in " + this.getClass().toString(), e);
			throw new IllegalArgumentException("Failed to generate the PDF report!");
		}
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

    private JasperReport loadTemplate(String reportName) throws JRException {
		InputStream reportInputStream;
		
		if(reportName.equals("dly_col_lst_report"))
			reportInputStream = getClass().getResourceAsStream(daily_report);
		else if(reportName.equals("mat_trans_lst_report"))
			reportInputStream = getClass().getResourceAsStream(matched_report);
		else if(reportName.equals("pg_set_dis_lst_report"))
			reportInputStream = getClass().getResourceAsStream(pg_report);
			else if(reportName.equals("un_trans_lst_report"))
			reportInputStream = getClass().getResourceAsStream(utl_report);
		else
			throw new JRException("UNKNOWN REPORT TYPE: " + reportName + "!");
			
		JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);
		return JasperCompileManager.compileReport(jasperDesign);
        }

	private JasperReport loadTemplateDIAging() throws JRException {
		InputStream reportInputStream;
		reportInputStream = getClass().getResourceAsStream(di_report);	
		JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);
		return JasperCompileManager.compileReport(jasperDesign);
	}

	//Unmatched Transaction Listing
	private Map<String, Object> reportUnmatchedTransListParameters(ReportRequest r) {

		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("settlementdate",  r.getSettDate());
		parameters.put("orderreferenceno",  r.getOrnNo());
		parameters.put("subcriteria",  r.getSubCriteria());
		parameters.put("receiptnumber",  r.getRcptNo());
		parameters.put("pgtransactionid",  r.getPgTransId());
		parameters.put("statementNo",  r.getStmtNo());
		parameters.put("checkduplicate",  r.getCheckDuplicate());
		

		return parameters;
     	}
	
	private Map<String, Object> reportDailyColLstParameters(ReportRequest r) {

		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("datestart",  r.getDateStart());
		parameters.put("dateend",  r.getDateEnd());
		parameters.put("ornno",  r.getOrnNo());
		parameters.put("pgpymtid",  r.getPgPaymentID());
		parameters.put("pgtxnid",  r.getPgTransId());
		parameters.put("pgtxnstatus",  r.getPgTransStatus());
		parameters.put("pgtxnmsg",  r.getPgTransStatusMsg());
		parameters.put("rcptno",  r.getRcptNo());
		parameters.put("stmtno",  r.getStmtNo());

		return parameters;
     	}

	//Matched Transaction Listing
	private Map<String, Object> reportMatTransLstParameters(ReportRequest r) {

		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("datestart",  r.getDateStart());
		parameters.put("dateend",  r.getDateEnd());
		parameters.put("ornno",  r.getOrnNo());
		parameters.put("pgpymtid",  r.getPgPaymentID());
		parameters.put("pgtxnid",  r.getPgTransId());
		parameters.put("rcptno",  r.getRcptNo());
		parameters.put("stmtno",  r.getStmtNo());

		return parameters;
		}
	
	//PG Settlement Disbursement Listing
	private Map<String, Object> reportPGSetDisLstParameters(ReportRequest r) {
		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("datestart",  r.getDateStart());
		parameters.put("dateend",  r.getDateEnd());
		parameters.put("stmtno",  r.getStmtNo());
		parameters.put("txndesc",  r.getTransDesc());

		return parameters;
	}

    //DI Aging
	private Map<String, Object> reportDIAgingParameters(ReportRequest r) {
		
		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("receiptdateend",  r.getReceiptdateend());
		parameters.put("effectivedatestart",  r.getReceiptdatestart());
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
