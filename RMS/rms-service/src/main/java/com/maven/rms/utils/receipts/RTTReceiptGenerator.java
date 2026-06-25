// package com.maven.rms.utils.receipts;

// import java.io.ByteArrayOutputStream;
// import java.io.File;
// import java.io.FileOutputStream;
// import java.io.IOException;
// import java.io.InputStream;
// import java.io.OutputStream;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// // import org.slf4j.Logger;
// // import org.slf4j.LoggerFactory;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.ui.jasperreports.JasperReportsUtils;

// import com.maven.rms.models.ParamRequest;
// import com.maven.rms.models.RTT.RTTCollectionReceiptingRequest;
// import com.maven.rms.models.RTT.RTTCollectionReceiptRequest;
// import com.maven.rms.services.StoreProcedureService;

// import net.sf.jasperreports.engine.JREmptyDataSource;
// import net.sf.jasperreports.engine.JRException;
// import net.sf.jasperreports.engine.JasperCompileManager;
// import net.sf.jasperreports.engine.JasperFillManager;
// import net.sf.jasperreports.engine.JasperPrint;
// import net.sf.jasperreports.engine.JasperReport;
// import net.sf.jasperreports.engine.design.JasperDesign;
// import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
// import net.sf.jasperreports.engine.xml.JRXmlLoader;
// import net.sf.jasperreports.export.SimpleExporterInput;
// import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
// import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

// @Service
// @Slf4j
// public class RTTReceiptGenerator {
// 	private static final String logo_path = "/jasper/images/logo-ssm.png";
// 	private final String receipt_template_path = "/jasper/SSM_OTC_Receipt.jrxml";
// 	private String rcpt_directory;
	
// 	@Autowired
// 	private StoreProcedureService spService;

// 	public RTTReceiptGenerator(@Value("${jasper.rcpt.directory}") String rcpt_directory) {
// 		this.rcpt_directory = rcpt_directory;
// 		log.info("Starting MTTPGReceiptGeneratorService...");
// 	}
	
// 	public File generateReceipt(RTTCollectionReceiptRequest r) throws IOException {
// 		if(r.getType().equals("pdf")) {
// 			File pdfFile = new File(rcpt_directory + "SSM-Receipt-" + r.getRTT().getRcptNo() + ".pdf");
			
// 			try(FileOutputStream pos = new FileOutputStream(pdfFile)){
// 				final JasperReport report = loadTemplate();
//                 final Map<String, Object> parameters = receiptParameters(r.getRTT());
// 				JasperReportsUtils.renderAsPdf(report, parameters, new JREmptyDataSource(1), pos);
// 				return pdfFile;
// 			}
// 			catch (final Exception e){
// 			    log.error("Exception in " + this.getClass().toString(), e);
// 				throw new IllegalArgumentException("Failed to generate the PDF receipt!");
// 			}
// 		}
// 		else if(r.getType().equals("xlsx"))
//             return generateXLSX(r.getRTT().getRcptNo(), receiptParameters(r.getRTT()));
// 		else
// 			return null;
// 	}

//     private Map<String, Object> receiptParameters(RTTCollectionReceiptingRequest rtt) {
// 		final Map<String, Object> parameters = new HashMap<>();
// 		parameters.put("logo", getClass().getResourceAsStream(logo_path));
// 		// parameters.put("pG",  pG);
// 		parameters.put("rmsRTT", rtt);
//         System.out.println(rtt.getRcptNo());

// 		return parameters;
// 	}

// 	private JasperReport loadTemplate() throws JRException {
// 		final InputStream reportInputStream = getClass().getResourceAsStream(receipt_template_path);
// 		final JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);
// 		return JasperCompileManager.compileReport(jasperDesign);
// 	}
	
// 	private File generateXLSX(String rcptNo, Map<String, Object> receiptParams) throws IOException{
// 		File xlsxFile = new File(rcpt_directory + "SSM-Receipt-" + rcptNo + ".xlsx");
		
// 		try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
// 				OutputStream fileOutputStream = new FileOutputStream(xlsxFile)){
// 			JasperReport report = loadTemplate();
// 			Map<String, Object> parameters = receiptParams;
// 			JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
// 			SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration();
// 			configuration.setOnePagePerSheet(true);
// 			configuration.setIgnoreGraphics(false);
			
// 			JRXlsxExporter jrXlsxExporter = new JRXlsxExporter();
// 			jrXlsxExporter.setExporterInput(new SimpleExporterInput(jasperPrint));
// 			jrXlsxExporter.setExporterOutput(new SimpleOutputStreamExporterOutput(byteArrayOutputStream));
// 			jrXlsxExporter.setConfiguration(configuration);
// 		    jrXlsxExporter.exportReport();
// 		    byteArrayOutputStream.writeTo(fileOutputStream);
// 			return xlsxFile;
// 		}
// 		catch (final Exception e){
// 		    log.error("Exception in " + this.getClass().toString(), e);
// 			throw new IllegalArgumentException("Failed to generate the XLSX File!");
// 		}
// 	}
	
// }
