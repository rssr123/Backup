package com.maven.rms.controllers;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.IOUtils;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maven.rms.models.IdamanAPIDownload;
import com.maven.rms.models.IdamanAPIDownloadRequest;
import com.maven.rms.models.MTTPG;
import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.models.ReceiptRequest;
import com.maven.rms.models.payload.responses.ApiResponse;
import com.maven.rms.services.AuthService;
import com.maven.rms.services.IdamanAPIDownloadService;
import com.maven.rms.services.MTTService;
import com.maven.rms.utils.APIResponse;
import com.maven.rms.utils.receipts.MTTPGCancelledReceiptGenerator;
import com.maven.rms.utils.receipts.MTTPGReceiptGenerator;
import com.maven.rms.utils.receipts.MTTPGWatermarkedReceiptGenerator;

@RestController
@RequestMapping("/api/receipt/v1")
@Slf4j
public class ReceiptController {
	//private static final Logger logger = LoggerFactory.getLogger(ReceiptController.class);
	
	@Autowired
	private AuthService authService;
	@Autowired
	private MTTService mttService;
	@Autowired
    private IdamanAPIDownloadService idamanDS;
	
	@Autowired
	private MTTPGReceiptGenerator receiptGenerator;
	/*@Autowired
	private MTTPGCancelledReceiptGenerator receiptGenerator2;
	@Autowired
	private MTTPGWatermarkedReceiptGenerator receiptGenerator3;*/

	@Value("${jasper.rcpt.directory}")
	private String rcpt_directory;

	public ReceiptController() {
		log.info("ReceiptController services is started");
	}
	
	@PostMapping(value = "/dl_rcpt", consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiResponse<String>> dlRcptWithOrnNo(HttpServletResponse response, HttpServletRequest request,
										@Valid @RequestBody Map<String, Object> payload) throws IOException, JRException, SQLException{
		if (!authService.isAuthenticated(request))
			//return APIResponse.NoPermission("");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		if(!payload.containsKey("i_orn_no"))
			return APIResponse.InvalidFormatExternal();

		String fileName = "SSM-Receipt-" + ((String)payload.get("i_orn_no")) + ".pdf";
		
		File rcptFile = new File(rcpt_directory + fileName);
		
		//Check if exist in share
		if(rcptFile.exists()) {
			byte[] fileBytes = Files.readAllBytes(rcptFile.toPath());
	        String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);
	        return APIResponse.SuccessResponse(base64Encoded);
		}
		
		MTTRCPT rcpt = mttService.getExistingReceipt((String)payload.get("i_orn_no")).orElse(null);
		
		//Check if uploaded in Idaman
		if(rcpt != null && rcpt.getIsUploaded() > 1){
			try {	
				List<IdamanAPIDownload> data = idamanDS.idaman_api_downloadDoc(
	                new IdamanAPIDownloadRequest(rcpt.getRcptNo(), rcpt.getVersionId(), rcpt.getRcptUUID()));
	
				if (data.size() > 0)
					return APIResponse.SuccessResponse(data.get(0).getFile_content());
			}catch(Exception e) {} //Handle exceptions and allow code to continue in case there is unknown error
		}
		
		//In case there is mtt_rcpt record, but no file in share or Idaman, generate file and return it
		if(rcpt != null) {	
			OnlinePayment mtt = mttService.getMttFromOrderNo((String)payload.get("i_orn_no")).orElse(null);
			List<OnlinePaymentItem> itemList = mtt != null ? mttService.getListOfItems(mtt.getMttId()) : Collections.emptyList();
			MTTPG pG =  mttService.getMttPgById(rcpt.getMttPG().getMttPgId()).orElse(null);
			
			if(mtt != null && pG != null && CollectionUtils.size(itemList) > 0) {			
				rcptFile = receiptGenerator.generateReceipt(new ReceiptRequest(pG, mtt, rcpt, itemList, "pdf"));
	
				byte[] fileBytes = Files.readAllBytes(rcptFile.toPath());
		        String base64Encoded = Base64.getEncoder().encodeToString(fileBytes);
		        return APIResponse.SuccessResponse(base64Encoded);		
			}
		}

		return APIResponse.NoDataFoundExternal();
	}
	
	/*
	@PostMapping(value = "/test_rcpt", consumes = MediaType.APPLICATION_JSON_VALUE)
	public void testRcptWithOrnNo(HttpServletResponse response, HttpServletRequest request,
										@Valid @RequestBody Map<String, Object> payload) throws IOException, JRException, SQLException{
		if (!authService.isAuthenticated(request))
			return;

		if(!payload.containsKey("i_orn_no"))
			return;

		if(!payload.containsKey("i_type"))
			return;

		if(!payload.containsKey("secret") && !payload.get("secret").equals("debugging_the_receipt"))
			return;

		String fileName = "Debug-SSM-Receipt-" + ((String)payload.get("i_orn_no")) + ".pdf";
		File pdfRcpt = new File(rcpt_directory + fileName);
		
		OnlinePayment mtt = mttService.getMttFromOrderNo((String)payload.get("i_orn_no")).orElse(null);
		if(mtt == null) {
			System.out.println("Missing MTT");
			return;
		}
		System.out.println("MTTID: " + Integer.toString(mtt.getMttId()));
		List<OnlinePaymentItem> itemList = mttService.getListOfItems(mtt.getMttId());
		if(CollectionUtils.size(itemList) == 0) {
			System.out.println("Missing MTTITEMS");
			return;
		}
		MTTRCPT rcpt = mttService.getExistingReceipt(mtt.getMttId()).orElse(null);
		if(rcpt == null) {
			System.out.println("Missing MTTRCPT");
			return;
		}
		MTTPG pG =  mttService.getMttPgById(rcpt.getMttPG().getMttPgId()).orElse(null);
		if(pG == null) {
			System.out.println("Missing MTTPG");
			return;
		}
		
		InputStream in = getClass().getResourceAsStream("/fonts/msyh.ttf");
		System.out.println(in != null ? "Font found!" : "Font not found!");
				
		try {
			if(((String)payload.get("i_type")).equals("copy"))
				pdfRcpt = receiptGenerator3.generateReceipt(new ReceiptRequest(pG, mtt, rcpt, itemList, "pdf"),2);
			else if(((String)payload.get("i_type")).equals("cancel"))
				pdfRcpt = receiptGenerator2.generateReceipt(new ReceiptRequest(pG, mtt, rcpt, itemList, "pdf"));
			else
				pdfRcpt = receiptGenerator.generateReceipt(new ReceiptRequest(pG, mtt, rcpt, itemList, "pdf"));
		} catch (IOException e) {
			System.err.println("Exception in " + this.getClass().toString());
			System.out.println(e);
		}
		
		response.setContentType("application/pdf");
		response.addHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
		response.addHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, HttpHeaders.CONTENT_DISPOSITION);
		
		IOUtils.copy(new FileInputStream(pdfRcpt), response.getOutputStream());
		response.flushBuffer();
		
		pdfRcpt.delete();
	}
	*/
}
