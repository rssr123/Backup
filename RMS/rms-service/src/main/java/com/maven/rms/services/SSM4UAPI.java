package com.maven.rms.services;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.cc.cielo.authgen.TokenGenerator;
import com.maven.rms.models.ExtAudit;
import com.maven.rms.models.RMSUser;

@Service
@Slf4j
public class SSM4UAPI {
	// private static final Logger logger = LoggerFactory.getLogger(SSM4UAPI.class);

	@Autowired
	private CommonService commonSvc;

	@Value("${ssm4u.authgen.username}")
	private String authGenUsername;
	@Value("${ssm4u.authgen.password}")
	private String authGenPassword;
	@Value("${ssm4u.authgen.clientid}")
	private String authGenClientId;
	@Value("${ssm4u.authgen.get.user.profile.url}")
	private String ssm4uGetUserProfileUrl;
	@Value("${ssm4u.authgen.notify.user.url}")
	private String ssm4uNotifyUserUrl;
	@Value("${ssm4u.authgen.default.custRefNo}")
	private String defaultAuthGenCustRefNo;

	public SSM4UAPI() {
	}

	public okhttp3.Response getUserProfile(RMSUser user, String emailUsername, String ucustRefNo) {
		String tk = new TokenGenerator().generateTokenForAuth(authGenUsername, authGenPassword);
		String custRefNo = user != null ? user.getSsm4uuserrefno()
				: ucustRefNo != null ? ucustRefNo : defaultAuthGenCustRefNo;
		String jsonEmail = user != null ? user.getEmail() : emailUsername;

		DateTimeFormatter iso8601Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String custReqDate = LocalDateTime.now().format(iso8601Formatter);

		/*
		 * String jsonString =
		 * "{\"header\": {\"customerId\": \"RMS\",\"customerReferenceNo\": \""
		 * + custRefNo + "\",\"customerRequestDate\": \"" + custReqDate + "\"}"
		 * + ",\"request\": {\"searchType\": \"EMAIL\",\"searchRefNo\": \"" + jsonEmail
		 * + "\"}}";
		 */
		String jsonString = "{\"searchType\": \"EMAIL\",\"searchRefNo\": \"" + jsonEmail + "\"}";

		log.info("getUserProfile request jsonString: " + jsonString);

		okhttp3.OkHttpClient client = new okhttp3.OkHttpClient().newBuilder().build();
		okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8");
		okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, jsonString);

		okhttp3.Request request = new okhttp3.Request.Builder()
				.url(ssm4uGetUserProfileUrl)
				.method("POST", body)
				.addHeader("Content-Type", "application/json; charset=utf-8")
				// .addHeader("Authorization", tk)
				.addHeader("X-IBM-Client-Id", authGenClientId)
				.build();
		okhttp3.Response resp = null;
		// Reattempt connection 5 times.
		for (int i = 1; i < 6; i++) {
			try {
				// Get current datetime
				String requestDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
				String requestBodyWithTime = "Request Time: " + requestDateTime + " | Body: " + jsonString;
				resp = client.newCall(request).execute();

				try {
					ExtAudit extAudit = new ExtAudit();
					extAudit.setI_module_nm("SSM4uGetUserProfile");
					extAudit.setI_request_body(requestBodyWithTime);

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
					extAudit.setI_remark(null);
					commonSvc.sp_insextaudit(extAudit);
				} catch (Exception e) {
					log.error("Error in sp_insextaudit for SSM4uGetUserProfile: " + e.getMessage() + ", "
							+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
				}

				int status = resp.code();
				if (status == 200 || status == 404)
					break;

			} catch (IOException e) {
				log.error("Exception in " + this.getClass().toString() + ", attempt "
						+ Integer.toString(i) + "...\nPost Data: " + jsonString, e);

			}
		}

		return resp;
	}

	public void notifyUser(String custRefNo, String emailUsername, String subject, String content)
			throws IOException, IllegalArgumentException {
		String tk = new TokenGenerator().generateTokenForAuth(authGenUsername, authGenPassword);
		DateTimeFormatter iso8601Formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		String custReqDate = LocalDateTime.now().format(iso8601Formatter);

		/*
		 * String jsonString = "{\"header\": {\"customerId\": \"RMS\","
		 * + "\"customerReferenceNo\": \"" + custRefNo + "\","
		 * + "\"customerRequestDate\": \"" + custReqDate + "\"}"
		 * + ",\"request\": {\"searchType\": \"EMAIL\","
		 * + "\"searchRefNo\": \"" + emailUsername + "\","
		 * + "\"sourceSystemId\": \"RMS\","
		 * + "\"subject\": \"" + subject + "\","
		 * + "\"content\": \"" + content + "\"}}";
		 */
		String jsonString = "{\"searchType\": \"EMAIL\", \"searchRefNo\": \"" + emailUsername + "\","
				+ "\"searchRefNo\": \"" + emailUsername + "\","
				+ "\"sourceSystemId\": \"RMS\","
				+ "\"subject\": \"" + subject + "\","
				+ "\"content\": \"" + content + "\"}";

		okhttp3.OkHttpClient client = new okhttp3.OkHttpClient().newBuilder().build();
		okhttp3.MediaType mediaType = okhttp3.MediaType.parse("application/json; charset=utf-8");
		okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, jsonString);

		okhttp3.Request request = new okhttp3.Request.Builder()
				.url(ssm4uNotifyUserUrl)
				.method("POST", body)
				.addHeader("Content-Type", "application/json; charset=utf-8")
				// .addHeader("Authorization", tk))
				.addHeader("X-IBM-Client-Id", authGenClientId)
				.build();
		okhttp3.Response resp = null;
		// Reattempt connection 5 times.
		for (int i = 1; i < 6; i++) {
			// try {
			resp = client.newCall(request).execute();

			try {
				ExtAudit extAudit = new ExtAudit();
				extAudit.setI_module_nm("SSM4uNotifyUser");
				extAudit.setI_request_body(jsonString);
				extAudit.setI_response_body(
						resp != null && resp.body() != null
								? resp.toString() + " || " + resp.peekBody(Long.MAX_VALUE).string()
								: null);
				extAudit.setI_rms_batch_no(null);
				extAudit.setI_direction("Outgoing");
				extAudit.setI_remark(null);
				commonSvc.sp_insextaudit(extAudit);
			} catch (Exception e) {
				log.error("Error in sp_insextaudit for SSM4uNotifyUser: " + e.getMessage() + ", "
						+ (e.getCause() != null ? e.getCause().getMessage() : "No cause"), e);
			}

			int status = resp.code();
			if (status == 200 || status == 404)
				break;

			// } catch (IOException e) {
			// log.error("Exception in " + this.getClass().toString() + ", attempt "
			// + Integer.toString(i) + "...\nPost Data: " + jsonString, e);

			// }
		}
		if (resp != null) {
			// try {
			String dataBody = resp.body().string().replace("\"", "'").replace("\\", "");
			log.debug("DEBUG: notifyUser\n--------\n" + dataBody + "\n--------");
			if (!dataBody.contains("'response':{'status':'00'}")) {
				log.error("Could not find the valid fields from the return JSON from 'notifyUser'. ", dataBody);
				throw new IllegalArgumentException(
						"Return data doesn't have the correct fields!\nReturn data: " + dataBody);
			}
			// } catch (IOException e) {
			// log.error("Exception in " + this.getClass().toString(), e);
			// } catch (IllegalArgumentException e) {
			// log.error("Exception in " + this.getClass().toString(), e);
			// }
		}
	}

}
