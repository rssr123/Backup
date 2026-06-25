package com.maven.rms.utils;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.xml.bind.DatatypeConverter;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.eGHLPaymentAPIRequest;
import com.maven.rms.services.CommonService;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

@Service
@Slf4j
public class EGHLPostUtility {
	// private static final Logger logger =
	// LoggerFactory.getLogger(EGHLPostUtility.class);

    @Autowired
    private CommonService commonSvc;

	@Value("${eghl.payment.url}")
	private String eGHLPaymentURL;
	@Value("${eghl.payment.password}")
	private String eGHLPassword;

	public String hashStringWithSHA256(String input) {
		try {
			// Create a MessageDigest instance for SHA-256
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			// Update the digest with the input string bytes
			md.update(input.getBytes());

			// Get the hashed bytes
			byte[] hashedBytes = md.digest();

			// Convert the hashed bytes to a hexadecimal representation
			String hashedString = DatatypeConverter.printHexBinary(hashedBytes).toLowerCase();

			return hashedString;
		} catch (NoSuchAlgorithmException e) {
			// Handle the exception or log an error
			log.error("Exception in " + EGHLPostUtility.class.getClass().toString(), e);
			return null;
		}
	}

	public Response eGHLPaymentAPI(eGHLPaymentAPIRequest req) {
		/*
		 * String MediaTypeString = "application/x-www-form-urlencoded";
		 * 
		 * Merchant ID: sit
		 * Merchant Password: sit12345
		 * Test Cards
		 * VISA: 4444333322221111
		 * MasterCard: 5555444433332222
		 * Expiry Date: any value
		 * CVV2: any value
		 * Txn Amount
		 * 0.14 – simulate failed
		 * 0.53 – simulate no response from gateway
		 * Other amount – approved
		 * 
		 * hashvalue = Password + ServiceID + PaymentID + Amount + CurrencyCode
		 * 
		 * String transactionType = "QUERY";
		 * String pymtMethod = "ANY";
		 * String serviceID = "sit";
		 * String paymentID = "VICKYTANROY23092203";
		 * String amount = "1.00";
		 * String currencyCode = "MYR";
		 */

		String hashValue = hashStringWithSHA256(
				eGHLPassword + req.getServiceID() + req.getPaymentID() + req.getAmount() + req.getCurrencyCode());

		String bodyString = "TransactionType=" + req.getTransactionType()
				+ "&PymtMethod=" + req.getPymtMethod()
				+ "&ServiceID=" + req.getServiceID()
				+ "&PaymentID=" + req.getPaymentID()
				+ "&Amount=" + req.getAmount()
				+ "&CurrencyCode=" + req.getCurrencyCode()
				+ "&HashValue=" + hashValue;

		// System.out.println(bodyString);

		OkHttpClient client = new OkHttpClient().newBuilder().build();

		MediaType mediaType = MediaType.parse(req.getMdType());
		RequestBody body = RequestBody.create(mediaType, bodyString);
		log.debug("BodyString:-------------\n" + bodyString + "\n-------------");

		log.debug("Target URL:-------------\n" + eGHLPaymentURL + "\n-------------");
		Request request = new Request.Builder()
				.url(eGHLPaymentURL)
				.method("POST", body)
				.addHeader("Content-Type", req.getMdType())
				.build();
		String requestDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

		try {
			Response resp = client.newCall(request).execute();

            try {            	
                ExtAudit extAudit = new ExtAudit();
                extAudit.setI_module_nm("EGHLPostUtility");
                extAudit.setI_request_body("Request Time: " + requestDateTime +  " | Body: " + bodyString);

				// Get current datetime
				String responseDateTime = LocalDateTime.now()
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				String responseBodyWithTime = "Response Time: " + responseDateTime + " | Body: " +
						(resp != null && resp.body() != null
								? (resp.toString() + " || "
										+ (resp.peekBody(Long.MAX_VALUE).string() != null
												? resp.peekBody(Long.MAX_VALUE).string()
												: "No Response"))
								: "No Response");
				
                extAudit.setI_response_body(responseBodyWithTime);
                extAudit.setI_rms_batch_no(null);
                extAudit.setI_direction("Outgoing");
                extAudit.setI_remark("MediaType:" + req.getMdType());
                commonSvc.sp_insextaudit(extAudit);
            } catch (Exception e) {
                log.error("Error in sp_insextaudit for eGHLPaymentAPI: " + e.getMessage() + ", "
                        + (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
            }
            
			return resp;
		} catch (IOException e) {
			log.error("EGHL Query Exception: " + eGHLPaymentURL + "Body: " + bodyString, e);
		}
		return null;
	}

}
