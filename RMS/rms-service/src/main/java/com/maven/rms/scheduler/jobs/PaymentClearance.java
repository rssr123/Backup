package com.maven.rms.scheduler.jobs;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.maven.rms.models.Email;
import com.maven.rms.models.IdamanAPIUpload;
import com.maven.rms.models.IdamanAPIUploadReq;
import com.maven.rms.models.MTTPG;
import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.models.ReceiptRequest;
import com.maven.rms.models.SchedulerLog;
import com.maven.rms.models.eGHLPaymentAPIRequest;
import com.maven.rms.models.payload.requests.SubmitRICPCanRequest;
import com.maven.rms.scheduler.services.SchedulerLogService;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.IdamanAPIUploadService;
import com.maven.rms.services.MTTService;
import com.maven.rms.services.RICPService;
import com.maven.rms.utils.EGHLPostUtility;
import com.maven.rms.utils.receipts.MTTPGReceiptGenerator;

@DisallowConcurrentExecution
@Component
@Slf4j
public class PaymentClearance implements Job{
	//private static final Logger logger = LoggerFactory.getLogger(PaymentClearance.class);
	public Integer payment_Date_Expiry_Limit_Days;

	@Autowired
	public SchedulerLogService schLogSvc;
	@Autowired
	public MTTPGReceiptGenerator receiptGenerator;
	@Autowired
	public EmailService emailer;
	@Autowired
	public RICPService ricpSvc;
	@Autowired
	public IdamanAPIUploadService idamanUS;
	@Autowired
	public MTTService mttSvc;
	@Autowired
	public EGHLPostUtility postUtils;

	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException{
		String configVal = mttSvc.getMttPgConfig("Payment_Date_Expiry_Limit_Days");
		int tmp = NumberUtils.toInt(configVal, 0);

		//If config is unloadable, default to 7 days
		if (tmp == 0) {
			log.error("Configuration for '" + "Payment_Date_Expiry_Limit_Days" + "' is invalid! Value: '" 
							+ configVal + "', resetting value to backup value: 7 days.");
			tmp = 7;
		}
		this.payment_Date_Expiry_Limit_Days = tmp;
		
		try {
			executePaymentClearanceJob(true);
		} catch (InterruptedException | ExecutionException e) {
		    log.error("Exception in " + this.getClass().toString(), e);
		} catch (IOException e) {
		    log.error("Exception in " + this.getClass().toString(), e);
			e.printStackTrace();
		}
	}
	
	public void executePaymentClearanceJob(boolean isNotExpiredMTTPGList) 
			throws InterruptedException, ExecutionException, IOException {
		List<MTTPG> listOfPGs = mttSvc.getListOfMTTPG(isNotExpiredMTTPGList, payment_Date_Expiry_Limit_Days);
		SchedulerLog newLog = new SchedulerLog("Payment Clearance - " + (isNotExpiredMTTPGList ? "Pending Payment" : "Expired"),
												"This job is called from thread: " + Thread.currentThread().getName(),
												CollectionUtils.size(listOfPGs));
		List<String>pgIds = new ArrayList<String>();
		newLog = schLogSvc.saveNewScheduleLog(newLog);
		
		if(newLog == null || newLog.equals(null))
			throw new IllegalArgumentException("The scheduler log failed to update!");

		log.info("Print list of Ids: This is called from thread: " + Thread.currentThread().getName());
		log.info("List size is: " + Integer.toString(CollectionUtils.size(listOfPGs)));
		
		int succ = 0;
		for(MTTPG pg : listOfPGs) {
			if(processRecord(pg))
				succ++;
			else
				pgIds.add(Long.toString(pg.getMttPgId()));
		}
		int fail = CollectionUtils.size(listOfPGs) - succ;
		newLog.setSuccessTxn(succ);
		newLog.setFailTxn(fail);
		newLog.setDtModified(LocalDateTime.now());
		
		String msg = "Failed PGIdList:" + String.join(",", pgIds);
		if(msg.length() > 500)
			msg = msg.substring(0, 497) + "...";
		newLog.setMessage(msg);
		
		log.debug("Succ: " + Integer.toString(succ) + " || Fail: " + Integer.toString(fail));
		schLogSvc.saveNewScheduleLog(newLog);
	}
		
	public Boolean processRecord(MTTPG pG){
		long start = System.currentTimeMillis();
		Response resp = postUtils.eGHLPaymentAPI(new eGHLPaymentAPIRequest("application/x-www-form-urlencoded", "QUERY"//pG.getPgTxnType()
				, pG.getPgPymtMethod(), pG.getPgServiceId(), pG.getPgPymtId()
				, String.format("%.2f", pG.getPgPymtAmt().doubleValue()), pG.getPgCurrCd()));

		if(resp == null) {
			log.error("[PaymentClearance] Encountered null response when querying eGHL for MTTPG ID: " + Long.toString(pG.getMttPgId()));
			//log.error("MTTPG ID: " + Long.toString(pG.getMttPgId()) + " The 'resp' argument cannot be null.");
			return false;
		}
		
		Map<String, String> responseMap = new HashMap<>();
		try {
			String respBody = resp.body().string();
			String[] respArr = respBody.split("&");
			
			for(String key : respArr) {
				if((!key.endsWith("=")) || key.endsWith("==")) {
					String[] data = java.net.URLDecoder.decode(key, StandardCharsets.UTF_8.name()).split("=", 2);
					responseMap.put(data[0], data[1]);
				}
			}
			
			if(!respBody.contains("TxnExists=")) {
				log.error("[PaymentClearance] Encountered malformed response when querying eGHL for MTTPG ID: " 
						+ Long.toString(pG.getMttPgId()) + "The response body does not contain the correct parameters: " + respBody);
				return false;
			}

		} catch (IOException e) {
			log.error("[PaymentClearance] ResponseBody exception", e);
			return false;
		}
		
		pG.setPgTxnExsts(Integer.parseInt(responseMap.get("TxnExists")));
		pG.setPgTxnStatus(Integer.parseInt(responseMap.get("TxnStatus")));
		pG.setPgQueryDesc(responseMap.get("QueryDesc"));
		pG.setPgTxnType(responseMap.get("TransactionType"));
		pG.setPgPymtMethod(responseMap.get("PymtMethod"));
		pG.setPgServiceId(responseMap.get("ServiceID"));
		pG.setPgPymtId(responseMap.get("PaymentID")); 	//Should be exactly same. Won't change JiC
		pG.setPgPymtAmt(new BigDecimal(responseMap.get("Amount")));
		pG.setPgCurrCd(responseMap.get("CurrencyCode"));
		pG.setPgHashValue(responseMap.get("HashValue"));
		
		if(!pG.getPgQueryDesc().equals("Invalid Service ID")) {
			pG.setPgRefundAmt(new BigDecimal(responseMap.get("TotalRefundAmount")));
			pG.setPgHashValue2(responseMap.get("HashValue2"));
		}
		
		//if(pG.getPgTxnStatus() == 0){	//pG.getPgQueryDesc().equals("Transaction exists")) {
			pG.setPgTxnId(responseMap.get("TxnID"));
			pG.setPgAuthCd(responseMap.get("AuthCode"));
			pG.setPgBankRefNo(responseMap.get("BankRefNo"));
			pG.setPgTxnMsg(responseMap.get("TxnMessage"));
			pG.setPgSessionId(responseMap.get("SessionID"));
			pG.setPgIssuingBank(responseMap.get("IssuingBank")== null ? "" : responseMap.get("IssuingBank"));
			pG.setPgTokenType(responseMap.get("TokenType"));
			pG.setPgToken(responseMap.get("Token"));
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			if(responseMap.get("RespTime") != null && !responseMap.get("RespTime").equals(null) && responseMap.get("RespTime").length() > 0)
				pG.setPgRespTime(LocalDateTime.parse(responseMap.get("RespTime").trim(), formatter));
			pG.setPgCardNoMask(responseMap.get("CardNoMask"));
			pG.setPgCardHolder(responseMap.get("CardHolder"));
			pG.setPgCardType(responseMap.get("CardType"));
			pG.setPgCardExp(responseMap.get("CardExp") != null ? responseMap.get("CardExp").substring(0, 6) : null);	//Has trailing space and newline
		//}
		
		pG.setDtModified(LocalDateTime.now());
		pG.setModifiedBy("system");

		if(pG.getPgTxnStatus() == 0) {
			//Reload in the OnlinePayment object that was not loaded in lazy loading
			OnlinePayment mtt = mttSvc.getMttFromMttId(pG.getRmsMTT().getMttId()).orElse(null);
			if(mtt == null) {
				log.error("[PaymentClearance] MTTPG ID: " + Long.toString(pG.getMttPgId()) 
					+ "The object (OnlinePayment) cannot be found in the repo!");
				return false;
			}
			
			boolean isGenNow = false;
			MTTRCPT rcpt = mttSvc.getExistingReceipt(pG.getRmsMTT().getMttId()).orElse(null);
			if (rcpt == null) {
				rcpt = mttSvc.sp_insertReceipt(pG.getPgPymtId(), "system");
				isGenNow = true;
			}
			if(rcpt == null) {
				log.error("[PaymentClearance] MTTPG ID: " + Long.toString(pG.getMttPgId()) 
					+ " The receipt failed to save in the repo properly!");
				return false;
			}
			
			List<OnlinePaymentItem> itemList = mttSvc.getListOfItems(pG.getRmsMTT().getMttId());
			if(CollectionUtils.size(itemList) == 0) {
				log.error("[PaymentClearance] MTTPG ID: "+ Long.toString(pG.getMttPgId()) 
					+ " The item list (OnlinePaymentItem) is empty!");
				return false;
			}
			
			try {
				File pdfRcpt = receiptGenerator.generateReceipt(new ReceiptRequest(pG, mtt, rcpt, itemList, "pdf"));
							
				String body = "Entity Name: " + mtt.getCust_nm()
						+ "<br>Receipt No: " + rcpt.getRcptNo().toUpperCase()
						+ "<br>Order Reference No.: " + mtt.getOrnNo().toUpperCase()
						+ "<br>Total Amount Paid: RM" + String.format("%.2f", pG.getPgPymtAmt().doubleValue())
						+ "<br><br>Dear Sir/Madam,<br>We are pleased to inform you that your "
						+ "online payment has been successfully processed. An official payment receipt "
						+ "has been generated for your records. Please find the attached receipt for "
						+ "your reference.<br>Thank you for using our services.<br><br><br>Tuan/Puan,<br>"
						+ "Dengan hormatnya, kami berbesar hati ingin memaklumkan bahawa pembayaran "
						+ "dalam talian anda telah berjaya diproses. Bersama-sama ini disertakan resit "
						+ "pembayaran untuk perhatian pihak Tuan/Puan selanjutnya.\r<br>Terima kasih kerana"
						+ " menggunakan perkhidmatan kami.<br><br><br><br>[THIS IS AN AUTOMATED MESSAGE - PLEASE "
						+ "DO NOT REPLY DIRECTLY TO THIS EMAIL]<br>";
				if(isGenNow)
					emailer.saveEmailDets(new Email("Receipt"
						, mtt.getCust_email(), "", "", "PAYMENT SUCCESSFUL - RECEIPT ATTACHED", body, pdfRcpt));
			
				//Update RICP Compound to collected here
				for(OnlinePaymentItem item : itemList) {
					if(item.getEntity_type() == null || item.getEntity_type().isEmpty()
							|| item.getEntity_no() == null || item.getEntity_no().isEmpty()
							|| item.getCp_no() == null || item.getCp_no().isEmpty())
							continue;
					int checkSucc = ricpSvc.updateRICPCollected(new SubmitRICPCanRequest(item.getEntity_type(), item.getEntity_no(), item.getCp_no(), "CE", item.getMtt_item_id()), "CPP", "CA", "system"); //Where CE is Collected, CPP is Compound Paid and CA is Collectable
					
					if(checkSucc == -401)
						log.info("[PaymentClearance] mtt_item_id: " + item.getMtt_item_id().toString() + " does not have RICP information.");
					else if(checkSucc < 1)
						log.error("[PaymentClearance] Exception in " + this.getClass().toString() + " adding the audit record returned with error. Row number from insert is: " + Integer.toString(checkSucc));
				}
				//Send notification to SSM here
				//ssmApi.notifyUser(mtt.getCust_nm(), mtt.getCust_email(), "Email queued", "Your receipt has been sent to your email.");
	
				//Upload to IDAMAN here if detected have never uploaded before
				/*if(rcpt.getIsUploaded() == 0) {
					// try {
						String rcptUUID = rcpt.getRcptUUID() != null ? rcpt.getRcptUUID() : "RMS-" + UUID.randomUUID().toString();
						byte[] bytes = Files.readAllBytes(pdfRcpt.toPath());
						String base64EncFile = Base64.getEncoder().encodeToString(bytes);
		
						List<IdamanAPIUpload> idaResp = idamanUS.idaman_api_uploadDoc(new IdamanAPIUploadReq("RMS", rcpt.getRcptNo(), "RMSReceipt", 
								new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
								"", "", "", "", "", "", rcptUUID, mtt.getOrnNo(), "", "", 
								"", "", "", "", base64EncFile, pdfRcpt.getName()));
						
						// if(idaResp == null || idaResp.equals(null) || idaResp.size() == 0) 
						if(idaResp == null || idaResp.equals(null) || CollectionUtils.size(idaResp) == 0) {
							log.error("[PaymentClearance] MTTPG ID: " + Long.toString(pG.getMttPgId()) 
								+ " - no data found in parsed response list! "
								+ "The receipt failed to save properly when uploading to IDAMAN!");
							//return false;
						}
						
						String verID = idaResp.get(0).getVerid();
						if(verID != null && !verID.equals("")) {
							rcpt.setRcptUUID(rcptUUID);
							rcpt.setVersionId(idaResp.get(0).getVerid());
							rcpt.setIsUploaded(1);
							rcpt = mttSvc.saveMTTRcpt(rcpt);
							if(rcpt == null) {
								log.error("[PaymentClearance] MTTPG ID: " + Long.toString(pG.getMttPgId()) 
								+ " - The receipt failed to save properly in the repo after uploading to IDAMAN!");
								return false;
							}
						}
						else {
							//logger.error("MTTPG ID: "  + Long.toString(pG.getMttPgId()) + " - The receipt failed to save properly in the repo after uploading to IDAMAN!"+ "Cannot get the version ID. Return data desc: " + idaResp.get(0).getDesc());
							log.error("[PaymentClearance] MTTPG ID: " + Long.toString(pG.getMttPgId()) 
							+ " - The receipt failed to save properly in the repo after uploading to IDAMAN!"
								+ "Cannot get the version ID. Return data desc: " + idaResp.get(0).getDesc());
							return false;
						}					
				}*/

			} catch (Exception e) {
				log.error("[PaymentClearance] Receipt Generation exception", e);
				return false;		
			}
		}
		try {
			mttSvc.updateMTTPG(pG);
		}catch(Exception e) {
			log.error("[PaymentClearance] Could not save data for "+ pG.toString(), e);
			return false;
		}
		mttSvc.updateLatestOrderStatus(pG.getRmsMTT().getMttId());
		
		long end = System.currentTimeMillis();
		String endString = "[PaymentClearance] MTTPG ID:" + Long.toString(pG.getMttPgId()) 
				+ " took a Total time of: " + Long.toString(end-start)
				+ " milliseconds.";
		log.info(endString);
		return true;
	}
}
