package com.maven.rms.utils.receipts;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.ui.jasperreports.JasperReportsUtils;

import com.maven.rms.models.MTTPG;
import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.models.Param;
import com.maven.rms.models.ParamRequest;
import com.maven.rms.models.ReceiptRequest;
import com.maven.rms.services.StoreProcedureService;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@Service
@Slf4j
public class MTTPGReceiptGenerator {
	// private static final Logger logger =
	// LoggerFactory.getLogger(MTTPGReceiptGenerator.class);
	private static final String logo_path = "/jasper/images/logo-ssm.png";
	private final String receipt_template_path = "/jasper/SSM_Receipt.jrxml";
	private String rcpt_directory;

	@Autowired
	private StoreProcedureService spService;

	public MTTPGReceiptGenerator(@Value("${jasper.rcpt.directory}") String rcpt_directory) {
		this.rcpt_directory = rcpt_directory;
		log.info("Starting MTTPGReceiptGeneratorService...");
	}

	public File generateReceipt(ReceiptRequest r) throws IOException {
		File dir = new File(rcpt_directory);
		if (!dir.exists()) {
		    boolean created = dir.mkdirs();
		    if (!created) {
		        log.error("[MTTPGReceiptGenerator] Failed to create receipt directory: " + dir.getAbsolutePath());
		        throw new IOException("[MTTPGReceiptGenerator] Cannot create receipt directory: " + dir.getAbsolutePath());
		    }
		}
		
		int attempts = 0;
		String safeRcptNo = r.getRcpt().getRcptNo().replaceAll("[^A-Za-z0-9_-]", "_");
		
		while (true) {
		if (r.getType().equals("pdf")) {
			
			File file = new File(rcpt_directory + "SSM-Receipt-" + safeRcptNo + ".pdf");
			
			try (FileOutputStream pos = new FileOutputStream(file)) {
				final JasperReport report = loadTemplate();
				final Map<String, Object> parameters = receiptParameters(r.getPG(), r.getMtt(), r.getRcpt(),
						r.getItemList());
				// final JRBeanCollectionDataSource dataSource = new
				// JRBeanCollectionDataSource(Collections.singletonList("Receipt"));
				JasperReportsUtils.renderAsPdf(report, parameters, new JREmptyDataSource(1), pos);
				return file;
			} catch (Exception e) {
		        if (++attempts >= 3) {
		            log.error("[MTTPGReceiptGenerator] Failed to create PDF after " + attempts + " attempts for " + file.getAbsolutePath() + " | Parent exists: " + file.getParentFile().exists(), e);
					throw new IllegalArgumentException("[MTTPGReceiptGenerator] Failed to generate the PDF receipt", e);
		        }
		        log.warn("[MTTPGReceiptGenerator] Retrying file write (attempt " + attempts + ") for " + file.getName() + " | Parent exists: " + file.getParentFile().exists(), e);
		        try { Thread.sleep(200L * attempts); } catch (InterruptedException ignored) {}
			}
			
		} else if (r.getType().equals("xlsx")) {
			File file = new File(rcpt_directory + "SSM-Receipt-" + safeRcptNo + ".xlsx");
			Map<String, Object> receiptParams = receiptParameters(r.getPG(), r.getMtt(), r.getRcpt(), r.getItemList());
			
			try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
					OutputStream fileOutputStream = new FileOutputStream(file)) {
				JasperReport report = loadTemplate();
				Map<String, Object> parameters = receiptParams;
				JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, new JREmptyDataSource());
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
			} catch (Exception e) {
		        if (++attempts >= 3) {
		            log.error("[MTTPGReceiptGenerator] Failed to create PDF after " + attempts + " attempts for " + file.getAbsolutePath() + " | Parent exists: " + file.getParentFile().exists(), e);
					throw new IllegalArgumentException("[MTTPGReceiptGenerator] Failed to generate the XLSX receipt", e);
		        }
		        log.warn("[MTTPGReceiptGenerator] Retrying file write (attempt " + attempts + ") for " + file.getName() + " | Parent exists: " + file.getParentFile().exists(), e);
		        try { Thread.sleep(200L * attempts); } catch (InterruptedException ignored) {}
			}
		}
		else
			break;
		}
		return null;
	}
	
	
	private Map<String, Object> receiptParameters(MTTPG pG, OnlinePayment mtt, MTTRCPT rcpt,
			List<OnlinePaymentItem> itemList) {
		for (OnlinePaymentItem item : itemList)
			if (item.getGrant_cd() != null)
				if (item.getGrant_cd().length() > 0)
					item.setItem_desc(item.getItem_desc() + "\nIncentive: " + item.getGrant_cd());

		final Map<String, Object> parameters = new HashMap<>();
		parameters.put("logo", getClass().getResourceAsStream(logo_path));
		parameters.put("pG", pG);
		parameters.put("rmsMTT", mtt);
		parameters.put("rmsMttRcpt", rcpt);
		parameters.put("rmsMTTItems", itemList);
		// parameters.put("custState", (spService.sp_getparam(1, 1, mtt.getCust_state(),
		// "State")
		// .get(0).getNm_en()));
		List<Param> state = spService.sp_getparam(new ParamRequest(1, 1, mtt.getCust_state(), "State"));
		parameters.put("custState", state.size() > 0 ? state.get(0).getNm_en() : mtt.getCust_state());

		return parameters;
	}

	private JasperReport loadTemplate() throws JRException {
		final InputStream reportInputStream = getClass().getResourceAsStream(receipt_template_path);
		final JasperDesign jasperDesign = JRXmlLoader.load(reportInputStream);
		return JasperCompileManager.compileReport(jasperDesign);
	}

}
