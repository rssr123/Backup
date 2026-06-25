package com.maven.rms.scheduler.services;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.maven.rms.models.ReceiptRequest;
import com.maven.rms.models.Email;
import com.maven.rms.models.IdamanAPIUpload;
import com.maven.rms.models.IdamanAPIUploadReq;
import com.maven.rms.models.MTTPG;
import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.repositories.IOnlinePaymentRepository;
import com.maven.rms.repositories.MTTPGRepository;
import com.maven.rms.repositories.MTTRCPTRepository;
import com.maven.rms.repositories.IOnlinePaymentItemRepository;
import com.maven.rms.services.EmailService;
import com.maven.rms.services.IdamanAPIUploadService;
import com.maven.rms.services.MTTService;
import com.maven.rms.services.RICPService;
import com.maven.rms.models.eGHLPaymentAPIRequest;
import com.maven.rms.models.payload.requests.SubmitRICPCanRequest;
import com.maven.rms.utils.EGHLPostUtility;
import com.maven.rms.utils.receipts.MTTPGReceiptGenerator;
import org.apache.commons.collections4.CollectionUtils;

import okhttp3.Response;
/*
@EnableAsync
@EnableTransactionManagement
@Service
@Slf4j
public class AsyncPaymentClearanceService {
	
	private static final Logger logger = LoggerFactory.getLogger(AsyncPaymentClearanceService.class);
	
	private final MTTPGRepository mttPgRepository;
	private final MTTRCPTRepository rcptRepo;
	private final IOnlinePaymentRepository mttRepo;
	private final IOnlinePaymentItemRepository itemListRepo;
	
	@Autowired
	private EGHLPostUtility postUtils;
	@Autowired
	private MTTPGReceiptGenerator receiptGenerator;
	@Autowired
	private EmailService emailer;
	@Autowired
	private RICPService ricpSvc;
	@Autowired
	private IdamanAPIUploadService idamanUS;
	@Autowired
	private MTTService mttSvc;
	@Lazy
	@Autowired
	private AsyncPaymentClearanceService apcs;
	//@Autowired
	//private SSM4UAPI ssmApi;
	
	public AsyncPaymentClearanceService(MTTPGRepository mttPgRepository, MTTRCPTRepository rcptRepo
			, IOnlinePaymentRepository mttRepo, IOnlinePaymentItemRepository itemListRepo) {
		this.mttPgRepository = mttPgRepository;
		this.rcptRepo = rcptRepo;
		this.mttRepo = mttRepo;
		this.itemListRepo = itemListRepo;
		this.postUtils = new EGHLPostUtility();
	}

	@Async("paymentClearanceExecutor")
	public CompletableFuture<Void> processRecord(MTTPG pG){
		long start = System.currentTimeMillis();
		Response resp = postUtils.eGHLPaymentAPI(new eGHLPaymentAPIRequest("application/x-www-form-urlencoded", "QUERY"//pG.getPgTxnType()
				, pG.getPgPymtMethod(), pG.getPgServiceId(), pG.getPgPymtId()
				, String.format("%.2f", pG.getPgPymtAmt().doubleValue()), pG.getPgCurrCd()));

		if(resp == null) {
			//updateSchLog(schLogId, "Encountered null response when querying eGHL for MTTPG ID: " + Long.toString(pG.getMttPgId()), true);
			logger.error("Encountered null response when querying eGHL for MTTPG ID: " + Long.toString(pG.getMttPgId()));
			throw new CompletionException(new IllegalArgumentException(
					"MTTPG ID: " + Long.toString(pG.getMttPgId()) 
					+ " The argument cannot be null."));
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
				//updateSchLog(schLogId, "MTTPG ID: " + Long.toString(pG.getMttPgId()) + ", cannot find the string 'TxnExists' in response body. " + "Check error log for the response body.", true);
				log.error("Encountered malformed response when querying eGHL for MTTPG ID: " + Long.toString(pG.getMttPgId()));
				throw new CompletionException(new IllegalArgumentException(
						"MTTPG ID: " + Long.toString(pG.getMttPgId()) 
						+ "The response body does not contain the correct parameters: " + respBody));
			}
			//log.error("Response body data: " + respBody);

		} catch (IOException e) {
			throw new CompletionException(new IOException(
					"ResponseBody exception", e));
		}
		
		pG.setPgTxnExsts(Integer.parseInt(responseMap.get("TxnExists")));
		pG.setPgTxnStatus(Integer.parseInt(responseMap.get("TxnStatus")));
		pG.setPgQueryDesc(responseMap.get("QueryDesc"));
		pG.setPgTxnType(responseMap.get("TransactionType"));
		pG.setPgPymtMethod(responseMap.get("PymtMethod"));
		pG.setPgServiceId(responseMap.get("ServiceID"));
		pG.setPgPymtId(responseMap.get("PaymentID")); 	//Should be exactly same. Won't change JiC
		//pG.setPgPymtAmt(Double.valueOf(Double.parseDouble(responseMap.get("Amount"))).longValue());
		pG.setPgPymtAmt(new BigDecimal(responseMap.get("Amount")));
		pG.setPgCurrCd(responseMap.get("CurrencyCode"));
		pG.setPgHashValue(responseMap.get("HashValue"));
		
		if(!pG.getPgQueryDesc().equals("Invalid Service ID")) {
			//pG.setPgRefundAmt(Double.valueOf(Double.parseDouble(responseMap.get("TotalRefundAmount"))).longValue());
			pG.setPgRefundAmt(new BigDecimal(responseMap.get("TotalRefundAmount")));
			pG.setPgHashValue2(responseMap.get("HashValue2"));
		}
		
		if(pG.getPgQueryDesc().equals("Transaction exists")) {
			pG.setPgTxnId(responseMap.get("TxnID"));
			pG.setPgAuthCd(responseMap.get("AuthCode"));
			pG.setPgBankRefNo(responseMap.get("BankRefNo"));
			pG.setPgTxnMsg(responseMap.get("TxnMessage"));
			pG.setPgSessionId(responseMap.get("SessionID"));
			pG.setPgIssuingBank(responseMap.get("IssuingBank"));
			pG.setPgTokenType(responseMap.get("TokenType"));
			pG.setPgToken(responseMap.get("Token"));
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			pG.setPgRespTime(LocalDateTime.parse(responseMap.get("RespTime").trim(), formatter));
			pG.setPgCardNoMask(responseMap.get("CardNoMask"));
			pG.setPgCardHolder(responseMap.get("CardHolder"));
			pG.setPgCardType(responseMap.get("CardType"));
			pG.setPgCardExp(responseMap.get("CardExp") != null ? responseMap.get("CardExp").substring(0, 6) : null);	//Has trailing space and newline
		}
		
		pG.setDtModified(LocalDateTime.now());
		pG.setModifiedBy("system");

		if(pG.getPgTxnStatus() == 0) {
			//Reload in the OnlinePayment object that was not loaded in lazy loading
			OnlinePayment mtt = mttRepo.getOnlinePaymentByMttId(pG.getRmsMTT().getMttId()).orElse(null);
			if(mtt == null)
				//logger.error("MTTPG ID: " + Long.toString(pG.getMttPgId()) + "The object (OnlinePayment) cannot be found in the repo!");
				throw new CompletionException(new IllegalArgumentException(
						"MTTPG ID: " + Long.toString(pG.getMttPgId()) 
						+ "The object (OnlinePayment) cannot be found in the repo!"));
			
			MTTRCPT rcpt = getExistingReceipt(pG.getRmsMTT().getMttId()).orElse(null);
			if (rcpt == null)
				rcpt = mttSvc.sp_insertReceipt(pG.getPgPymtId(), "system");
				//rcpt = saveMTTRcpt(new MTTRCPT(pG, mtt.getOrnNo(), pG.getPgRespTime()));
				//rcpt = saveMTTRcpt(new MTTRCPT(pG, rcptRepo.sp_getrunno(), pG.getPgRespTime()));
			
			if(rcpt == null) 
				throw new CompletionException(new IllegalArgumentException(
						"MTTPG ID: " + Long.toString(pG.getMttPgId()) 
						+ " The receipt failed to save in the repo properly!"));
			
			List<OnlinePaymentItem> itemList = getListOfItems(pG.getRmsMTT().getMttId());
			// if(itemList.size() == 0) {
			if(CollectionUtils.size(itemList) == 0) {
				//logger.error("MTTPG ID: " + Long.toString(pG.getMttPgId()) + " The item list (OnlinePaymentItem) is empty!");
				throw new CompletionException(new IllegalArgumentException(
						"MTTPG ID: "+ Long.toString(pG.getMttPgId()) 
						+ " The item list (OnlinePaymentItem) is empty!"));
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
				/*
				emailer.sendMailWithAttachment(mtt.getCust_email(), "", ""
						, "PAYMENT SUCCESSFUL – RECEIPT ATTACHED"
						, body , pdfRcpt, false);
				*//*
				//log.error("Debug AsyncPymtClearance - can send email: MTTPG Id=" + Long.toString(pG.getMttPgId()) + ",ornNo:" + mtt.getOrnNo());
				emailer.saveEmailDets(new Email("Receipt"
						, mtt.getCust_email(), "", "", "PAYMENT SUCCESSFUL - RECEIPT ATTACHED", body, pdfRcpt));
			
				//Update RICP Compound to collected here
				for(OnlinePaymentItem item : itemList) {
					int checkSucc = ricpSvc.updateRICPCollected(new SubmitRICPCanRequest(item.getEntity_type(), item.getEntity_no(), item.getCp_no(), "CE", item.getMtt_item_id()), "CPP", "CA", "system"); //Where CE is Collected, CPP is Compound Paid and CA is Collectable
					
					if(checkSucc == -401)
						log.info("mtt_item_id: " + item.getMtt_item_id().toString() + " does not have RICP information.");
					else if(checkSucc < 1)
						log.error("Exception in " + this.getClass().toString() + " adding the audit record returned with error. Row number from insert is: " + Integer.toString(checkSucc));
				}
				//Send notification to SSM here
				//ssmApi.notifyUser(mtt.getCust_nm(), mtt.getCust_email(), "Email queued", "Your receipt has been sent to your email.");
	
				//Upload to IDAMAN here if detected have never uploaded before
				if(rcpt.getIsUploaded() == 0) {
					// try {
						String rcptUUID = "RMS-" + UUID.randomUUID().toString();
						byte[] bytes = Files.readAllBytes(pdfRcpt.toPath());
						String base64EncFile = Base64.getEncoder().encodeToString(bytes);
						
						List<IdamanAPIUpload> idaResp = idamanUS.idaman_api_uploadDoc(new IdamanAPIUploadReq("RMS", rcpt.getRcptNo(), "RMSReceipt", 
								new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
								"", "", "", "", "", "", rcptUUID, mtt.getOrnNo(), "", "", 
								"", "", "", "", base64EncFile, pdfRcpt.getName()));
						
						// if(idaResp == null || idaResp.equals(null) || idaResp.size() == 0) 
						if(idaResp == null || idaResp.equals(null) || CollectionUtils.size(idaResp) == 0)
							//logger.error("Exception in " + this.getClass().toString() + ": no data found in parsed response list! " + "The receipt failed to save properly when uploading to IDAMAN!");
							//logger.debug(endString);
							throw new CompletionException(new IllegalArgumentException(
								"MTTPG ID: " + Long.toString(pG.getMttPgId()) 
								+ " - no data found in parsed response list! "
								+ "The receipt failed to save properly when uploading to IDAMAN!"));
						
						
						String verID = idaResp.get(0).getVerid();
						if(verID != null && !verID.equals("")) {
							rcpt.setRcptUUID(rcptUUID);
							rcpt.setVersionId(idaResp.get(0).getVerid());
							rcpt.setIsUploaded(1);
							rcpt = saveMTTRcpt(rcpt);
							if(rcpt == null) 
								//logger.error("MTTPG ID: " + Long.toString(pG.getMttPgId()) + "The receipt failed to save properly in the repo after uploading to IDAMAN!");
								throw new CompletionException(new IllegalArgumentException(
									"MTTPG ID: " + Long.toString(pG.getMttPgId()) 
									+ " - The receipt failed to save properly in the repo after uploading to IDAMAN!"));
							
						}
						else 
							//logger.error("MTTPG ID: "  + Long.toString(pG.getMttPgId()) + " - The receipt failed to save properly in the repo after uploading to IDAMAN!"+ "Cannot get the version ID. Return data desc: " + idaResp.get(0).getDesc());
							throw new CompletionException(new IllegalArgumentException(
								"MTTPG ID: " + Long.toString(pG.getMttPgId()) 
								+ " - The receipt failed to save properly in the repo after uploading to IDAMAN!"
								+ "Cannot get the version ID. Return data desc: " + idaResp.get(0).getDesc()));
						
							
					// } catch (IOException e) {
					// 	//logger.error("Exception in " + this.getClass().toString() + " Cannot generate base64 string for PDF!", e);
					// 	throw new CompletionException(new IllegalArgumentException(
					// 			"MTTPG ID: " + Long.toString(pG.getMttPgId()) 
					// 			+ "Exception in " + this.getClass().toString() + " Cannot generate base64 string for PDF!", e));
					// } catch (Exception e1) {
					// 	//logger.error("Exception in " + this.getClass().toString(), e1);
					// 	throw new CompletionException(new IllegalArgumentException(
					// 			"MTTPG ID: " + Long.toString(pG.getMttPgId()) 
					// 			+ "Exception in " + this.getClass().toString(), e1));
					// }
					
				}

			} catch (IOException e) {
				throw new CompletionException(new IOException(
						"Receipt Generation exception", e));
			}
			//if(mtt.getOrnNo().contains("BIL"))
				//bSvc.sp_updatebillstatuspaid(mtt.getOrnNo(), "system");
		}

		apcs.updateMTTPG(pG);
		//apcs.updateLatestOrderStatus(pG.getRmsMTT().getMttId());
		
		
		long end = System.currentTimeMillis();
		String endString = "Thread '" + Thread.currentThread().getName() 
				+ "' took a Total time of: " + Long.toString(end-start)
				+ " milliseconds. Processed PG ID: " + Long.toString(pG.getMttPgId());
		log.warn(endString);
		return CompletableFuture.completedFuture(null);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateMTTPG(MTTPG pG) {
		if(pG.getRmsMTT() != null) {
			if(pG.getPgTxnType() == null)
				pG.setPgTxnType("");
			if(pG.getPgPymtMethod() == null)
				pG.setPgPymtMethod("");
			if(pG.getPgServiceId() == null)
				pG.setPgServiceId("");
			if(pG.getPgPymtId() == null)
				pG.setPgPymtId("");
			if(pG.getPgPymtDesc() == null)
				pG.setPgPymtDesc("");
			if(pG.getPgCurrCd() == null)
				pG.setPgCurrCd("");
			if(pG.getPgLangCd() == null)
				pG.setPgLangCd("");
			if(pG.getPgTxnId() == null)
				pG.setPgTxnId("");			
			if(pG.getPgIssuingBank() == null)
				pG.setPgIssuingBank("");
			if(pG.getPgAuthCd() == null)
				pG.setPgAuthCd("");
			if(pG.getPgHashValue() == null)
				pG.setPgHashValue("");
			if(pG.getPgHashValue2() == null)
				pG.setPgHashValue2("");
			if(pG.getPgQhashValue() == null)
				pG.setPgQhashValue("");
			if(pG.getPgQueryDesc() == null)
				pG.setPgQueryDesc("");
			if(pG.getPgSessionId() == null)
				pG.setPgSessionId("");
			if(pG.getPgTokenType() == null)
				pG.setPgTokenType("");
			if(pG.getPgToken() == null)
				pG.setPgToken("");
			if(pG.getPgCardNoMask() == null)
				pG.setPgCardNoMask("");
			if(pG.getPgCardHolder() == null)
				pG.setPgCardHolder("");
			if(pG.getPgCardType() == null)
				pG.setPgCardType("");
			if(pG.getPgCardExp() == null)
				pG.setPgCardExp("");
			
			//if(pG.getPgTxnStatus() == 0) log.error("Debug AsyncPymtClearance - presaving pG: MTTPG Id=" + Long.toString(pG.getMttPgId()));

			pG = mttPgRepository.save(pG);

			//if(pG.getPgTxnStatus() == 0) log.error("Debug AsyncPymtClearance - post-saved pG: MTTPG Id=" + Long.toString(pG.getMttPgId()));

			mttPgRepository.sp_updateLatestOrderStatus(pG.getRmsMTT().getMttId());
		}
		else
			log.error("Cannot find parent RMS-MTT object from RMS-MTT-PG ID-" + Long.toString(pG.getMttPgId()));
		
		
		log.debug("updateMTTPG(MTTPG) from " + this.getClass().toString() + " method called from Thread: " 
			+ Thread.currentThread().getName());
	}
		
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateLatestOrderStatus(int mttID) {
		// try {
			mttPgRepository.sp_updateLatestOrderStatus(mttID);
		// }catch (Exception e) {
		//     log.error("Exception in " + this.getClass().toString(), e);
		// }
	}
	
	@Transactional(readOnly=true)
	public Optional<MTTRCPT> getExistingReceipt(int rmsMTTId){
		return rcptRepo.sp_getRcptByMttId(rmsMTTId);
	}
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public MTTRCPT saveMTTRcpt(MTTRCPT rcpt) {
		// try {
			rcptRepo.save(rcpt);
			return rcpt;
		// }catch (Exception e) {
		//     log.error("Exception in " + this.getClass().toString(), e);
		//     return null;
		// }
	}
	
	@Transactional(readOnly=true)
	public List<OnlinePaymentItem> getListOfItems(int rmsMTTId){
		return itemListRepo.sp_getmttitem(rmsMTTId);
	}
	
}*/
