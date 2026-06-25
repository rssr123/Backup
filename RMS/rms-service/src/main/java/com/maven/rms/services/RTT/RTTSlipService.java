package com.maven.rms.services.RTT;

import com.maven.rms.models.RTT.RTTSlipRequest;
import com.maven.rms.repositories.RTT.RTTSlipRepository;
import lombok.RequiredArgsConstructor;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RTTSlipService {
    private static final Logger logger = LoggerFactory.getLogger(RTTSlipService.class);
    private final RTTSlipRepository rttslipRepository;

    public void generateSlipPDF(RTTSlipRequest rttslipRequest) throws Exception {
        try {
            RTTSlipRequest slipData = rttslipRepository.getSlipData(rttslipRequest);

            // Compile the Jasper report from .jrxml to .jasper
            JasperReport jasperReport = JasperCompileManager.compileReport("src/main/resources/SSM_RTT_Slip.jrxml");

            // Parameters for the report
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("rmsRTT", slipData);
            parameters.put("transactionId", rttslipRequest.getTxnId());

            // Fill the report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, new JREmptyDataSource());

            // Export the report to a PDF file
            String downloadPath = Paths.get(System.getProperty("user.home"), "Downloads", "slip.pdf").toString();
            try (FileOutputStream outputStream = new FileOutputStream(downloadPath)) {
                JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            }
        } catch (Exception e) {
            logger.error("Error generating slip PDF", e);
            throw new Exception("Error generating slip PDF", e);
        }
    }
}
