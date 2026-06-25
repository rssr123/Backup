package com.maven.rms.controllers.RTT;

import com.maven.rms.models.RTT.RTTSlipRequest;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.RTT.RTTSlipService;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("api/refund/v1/")
@RequiredArgsConstructor
public class RTTSlipController {
    private static final Logger logger = LoggerFactory.getLogger(RTTSlipController.class);
    private final RTTSlipService rttslipService;

    @Autowired
    private AuthService authService;

    @PostMapping("/PDFGenerator")
    public ResponseEntity<String> generateSlipPDF(@RequestBody RTTSlipRequest rttslipRequest, HttpServletRequest request) {
        try {
            if (!authService.isAuthenticated(request)) {
                return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).build();
            }
            rttslipService.generateSlipPDF(rttslipRequest);
            return ResponseEntity.ok("PDF generated successfully and saved to the download folder.");
        } catch (Exception e) {
            logger.error("Error generating slip PDF", e);
            return ResponseEntity.status(HttpStatus.SC_INTERNAL_SERVER_ERROR).body("Error generating slip PDF");
        }
    }
}