package com.maven.rms.services;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.RollbackException;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.maven.rms.models.ReceiptRequest;
import com.informix.lang.Decimal;
import com.maven.rms.interfaces.IMTTService;
import com.maven.rms.models.Email;
import com.maven.rms.models.EmailPP;
import com.maven.rms.models.GHLPayment;
import com.maven.rms.models.GHLPaymentResponse;
import com.maven.rms.models.IdamanAPIDownload;
import com.maven.rms.models.IdamanAPIDownloadRequest;
import com.maven.rms.models.IdamanAPIUpload;
import com.maven.rms.models.IdamanAPIUploadReq;
import com.maven.rms.models.MTTPG;
import com.maven.rms.models.MTTRCPT;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.models.RICP;
import com.maven.rms.models.eGHLPaymentAPIRequest;
import com.maven.rms.models.payload.requests.SubmitRICPCanRequest;
import com.maven.rms.repositories.IOnlinePaymentRepository;
import com.maven.rms.repositories.MTTPGRepository;
import com.maven.rms.repositories.MTTRCPTRepository;
import com.maven.rms.repositories.MTTRepository;
import com.maven.rms.repositories.IOnlinePaymentItemRepository;
import com.maven.rms.utils.EGHLPostUtility;
import com.maven.rms.utils.receipts.MTTPGReceiptGenerator;
import org.apache.commons.collections4.CollectionUtils;

import okhttp3.Response;

@Service
@Slf4j
public class MTTService implements IMTTService {
	// private static final Logger logger =
	// LoggerFactory.getLogger(MTTService.class);

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
	private IdamanAPIDownloadService idamanDS;
	@Autowired
	private IdamanAPIUploadService idamanUS;
	@Autowired
	private AuthService authSvc;
	@Autowired
	private MTTRepository mttRepository;

	public MTTService(MTTPGRepository mttPgRepository, MTTRCPTRepository rcptRepo, IOnlinePaymentRepository mttRepo,
			IOnlinePaymentItemRepository itemListRepo, MTTRepository mttRepository) {
		this.mttPgRepository = mttPgRepository;
		this.rcptRepo = rcptRepo;
		this.mttRepo = mttRepo;
		this.itemListRepo = itemListRepo;
		this.mttRepository = mttRepository;
	}

	public List<Map<String, Object>> requeryOrderStatus(String orderNo, String username) throws IOException {
		int count = getOrnNoCount(orderNo);
		if(count != 1)
			return Collections.singletonList(
					Collections.singletonMap("Error", (count > 1 ? (count + " duplicates found for Order No. " + orderNo) :
						"Cannot find OnlinePayment record from Order No. ") + orderNo));
		
		OnlinePayment mtt = getMttFromOrderNo(orderNo).orElse(null);
		if (mtt == null) {
			log.debug("Exception in " + this.getClass().toString()
					+ ": Cannot find OnlinePayment record from Order No. " + orderNo);
			return Collections.singletonList(
					Collections.singletonMap("Error", "Cannot find OnlinePayment record from Order No. " + orderNo));
		}

		Map<String, Object> item = new HashMap<>();
		item.put("orn_no", mtt.getOrnNo());
		item.put("pymt_status", mtt.getOrder_status());
		item.put("rcpt_no", null);
		item.put("rcpt_dt", null);
		item.put("rcpt_status", null);

		List<MTTPG> pGs = getAllMttPgsByMttId(mtt.getMttId());
		// if(pGs.size() < 1) {
		if (CollectionUtils.size(pGs) < 1) {
			log.debug("Exception in " + this.getClass().toString()
					+ ": Cannot find any MTTPG record from Order No. " + orderNo);
			item.put("Error", "Cannot find any MTTPG record from Order No. " + orderNo);
			return Collections.singletonList(item);
		}

		for (MTTPG pG : pGs) {
			Response resp = postUtils
					.eGHLPaymentAPI(new eGHLPaymentAPIRequest("application/x-www-form-urlencoded", "QUERY" // pG.getPgTxnType()
							, pG.getPgPymtMethod(), pG.getPgServiceId(), pG.getPgPymtId(),
							String.format("%.2f", pG.getPgPymtAmt().doubleValue()), pG.getPgCurrCd()));
			if (resp == null) {
				log.error("Encountered null response when querying eGHL for MTTPG ID: " + Long.toString(pG.getMttPgId()));
				continue;
			}

			Map<String, String> responseMap = new HashMap<>();
			String respBody;
			try {
				respBody = resp.body().string();
			} catch (IOException e) {
				log.error("Exception in " + this.getClass().toString(), e);
				continue;
			}
			log.debug("Response body data: " + respBody);

			String[] respArr = respBody.split("&");

			for (String key : respArr) {
				if ((!key.endsWith("=")) || key.endsWith("==")) {
					String[] dataS;
					try {
						dataS = java.net.URLDecoder.decode(key, StandardCharsets.UTF_8.name()).split("=", 2);
					} catch (UnsupportedEncodingException e) {
						log.error("Exception in " + this.getClass().toString(), e);
						continue;
					}
					responseMap.put(dataS[0], dataS[1]);
				}
			}
			if (!respBody.contains("TxnExists=")) {
				log.error("Encountered malformed response when querying eGHL for MTTPG ID: "
						+ Long.toString(pG.getMttPgId())
						+ "\n" + respBody);
				continue;
			}
			pG.setPgTxnExsts(Integer.parseInt(responseMap.get("TxnExists")));
			pG.setPgTxnStatus(Integer.parseInt(responseMap.get("TxnStatus")));
			pG.setPgQueryDesc(responseMap.get("QueryDesc"));
			pG.setPgTxnType(responseMap.get("TransactionType"));
			pG.setPgPymtMethod(responseMap.get("PymtMethod"));
			pG.setPgServiceId(responseMap.get("ServiceID"));
			pG.setPgPymtId(responseMap.get("PaymentID")); // Should be exactly same. Won't change JiC
			// pG.setPgPymtAmt(Double.valueOf(Double.parseDouble(responseMap.get("Amount"))).longValue());
			pG.setPgPymtAmt(new BigDecimal(responseMap.get("Amount")));
			pG.setPgCurrCd(responseMap.get("CurrencyCode"));
			pG.setPgHashValue(responseMap.get("HashValue"));

			if (!pG.getPgQueryDesc().equals("Invalid Service ID")) {
				// pG.setPgRefundAmt(Double.valueOf(Double.parseDouble(responseMap.get("TotalRefundAmount"))).longValue());
				pG.setPgRefundAmt(new BigDecimal(responseMap.get("TotalRefundAmount")));
				pG.setPgHashValue2(responseMap.get("HashValue2"));
			}

			//if (pG.getPgTxnStatus() == 0) {	//(pG.getPgQueryDesc().equals("Transaction exists")) {
				pG.setPgTxnId(responseMap.get("TxnID"));
				pG.setPgAuthCd(responseMap.get("AuthCode"));
				pG.setPgBankRefNo(responseMap.get("BankRefNo"));
				pG.setPgTxnMsg(responseMap.get("TxnMessage"));
				pG.setPgSessionId(responseMap.get("SessionID"));
				pG.setPgIssuingBank(responseMap.get("IssuingBank"));
				pG.setPgTokenType(responseMap.get("TokenType"));
				pG.setPgToken(responseMap.get("Token"));
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
				if(responseMap.get("RespTime") != null && !responseMap.get("RespTime").equals(null) && responseMap.get("RespTime").length() > 0)
					pG.setPgRespTime(LocalDateTime.parse(responseMap.get("RespTime").trim(), formatter));
				pG.setPgCardNoMask(responseMap.get("CardNoMask"));
				pG.setPgCardHolder(responseMap.get("CardHolder"));
				pG.setPgCardType(responseMap.get("CardType"));
				pG.setPgCardExp(responseMap.get("CardExp") != null ? responseMap.get("CardExp").substring(0, 6) : null); // Has trailing space and newline
			//}

			pG.setDtModified(LocalDateTime.now());
			pG.setModifiedBy(authSvc.getLoginUserName());

			item.put("pg_txn_exists", pG.getPgTxnExsts());
			item.put("pg_query_desc", pG.getPgQueryDesc());

			if (pG.getPgTxnStatus() == 0) {
				count = getRcptCount(mtt.getMttId());
				if(count > 1) {
					log.error("Error:" + count + " duplicates found for Rcpt with MttID: " + mtt.getMttId());
					continue;
				}
				Boolean isFirstReceipt = false;
				MTTRCPT rcpt = getExistingReceipt(mtt.getMttId()).orElse(null);
				
				if (rcpt == null) {
					rcpt = sp_insertReceipt(pG.getPgPymtId(), username);
					isFirstReceipt = true;
				}

				if (rcpt.getIsUploaded() == 1) {
					log.warn("MTTPG ID: " + Long.toString(pG.getMttPgId())
							+ "The receipt has already been uploaded!");
					updateMTTPG(pG);
					updateLatestOrderStatus(pG.getRmsMTT().getMttId());
					continue;
				}

				List<OnlinePaymentItem> itemList = getListOfItems(pG.getRmsMTT().getMttId());
				// if(itemList.size() == 0) {
				if (CollectionUtils.size(itemList) == 0) {
					log.debug("MTTPG ID: " + Long.toString(pG.getMttPgId())
							+ "The item list (OnlinePaymentItem) is empty!");
					updateMTTPG(pG);
					updateLatestOrderStatus(pG.getRmsMTT().getMttId());
					continue;
				}

				if(isFirstReceipt) {
					File pdfRcpt;
					try {
						pdfRcpt = receiptGenerator.generateReceipt(new ReceiptRequest(pG, mtt, rcpt, itemList, "pdf"));
					} catch (Exception e) {
						log.error("Exception in " + this.getClass().toString(), e);
						updateMTTPG(pG);
						updateLatestOrderStatus(pG.getRmsMTT().getMttId());
						continue;
					}
	
					// UploadDoc() to IDAMAN and set MTTRCPT rcpt isUploaded = 1 (remember to save!)
	
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
							+ "DO NOT REPLY DIRECTLY TO THIS EMAIL] <br>";
	
					emailer.saveEmailDets(new Email("Receipt", mtt.getCust_email(), "", "",
						"PAYMENT SUCCESSFUL - RECEIPT ATTACHED", body, mtt.getOrnNo().toUpperCase(), pdfRcpt));
				}
				for (OnlinePaymentItem pItem : itemList) {
					if(pItem.getEntity_type() == null || pItem.getEntity_type().isEmpty()
						|| pItem.getEntity_no() == null || pItem.getEntity_no().isEmpty()
						|| pItem.getCp_no() == null || pItem.getCp_no().isEmpty())
						continue;
					int checkSucc = ricpSvc.updateRICPCollected(new SubmitRICPCanRequest(pItem.getEntity_type(),
							pItem.getEntity_no(), pItem.getCp_no(), "CE", pItem.getMtt_item_id()), "CPP", "CA", username); // Where CE is
																									// Collected, CPP is
																									// Compound Paid and
																									// CA is Collectable
					if(checkSucc == -401)
						log.info("mtt_item_id: " + pItem.getMtt_item_id().toString() + " does not have RICP information.");
					else if(checkSucc < 1)
						log.error("Exception in " + this.getClass().toString() + " adding the audit record returned with error. Row number from insert is: " + Integer.toString(checkSucc));
				
				}

				item.put("rcpt_no", rcpt.getRcptNo());
				item.put("rcpt_dt", rcpt.getRcptDt());
				item.put("rcpt_status", rcpt.getRcptStatus());

				// Send notification to SSM here
				// ssmApi.notifyUser(mtt.getCust_nm(), mtt.getCust_email(), "Email queued",
				// "Your receipt has been sent to your email.");

				// Upload to IDAMAN here
				/*try {
					String rcptUUID = rcpt.getRcptUUID() != null ? rcpt.getRcptUUID() : "RMS-" + UUID.randomUUID().toString();
					byte[] bytes = Files.readAllBytes(pdfRcpt.toPath());
					String base64EncFile = Base64.getEncoder().encodeToString(bytes);

					List<IdamanAPIUpload> idaResp = idamanUS.idaman_api_uploadDoc(new IdamanAPIUploadReq(
							"RMS", rcpt.getRcptNo(), "RMSReceipt",
							new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
							"", "", "", "", "", "", rcptUUID, mtt.getOrnNo(), "", "",
							"", "", "", "", base64EncFile, pdfRcpt.getName()));

					if (idaResp == null || idaResp.equals(null) || CollectionUtils.size(idaResp) == 0) {
						log.error("Exception in " + this.getClass().toString()
								+ ": no data found in parsed response list!");
						item.put("Error", "The receipt failed to save properly when uploading to IDAMAN!");
						return Collections.singletonList(item);
					}
	
					String verID = idaResp.get(0).getVerid();
					if (verID != null && !verID.equals("")) {
						rcpt.setRcptUUID(rcptUUID);
						rcpt.setVersionId(idaResp.get(0).getVerid());
						rcpt.setIsUploaded(1);
						rcpt = saveMTTRcpt(rcpt);
						if (rcpt == null)
							log.error("MTTPG ID: " + Long.toString(pG.getMttPgId())
									+ "The receipt failed to save properly after uploading to IDAMAN!");
					} else
						log.error("MTTPG ID: " + Long.toString(pG.getMttPgId())
								+ "The receipt failed to save properly after uploading to IDAMAN!"
								+ "Cannot get the version ID. Return data desc: " + idaResp.get(0).getDesc());

				} catch (IOException e) {
					log.error("Exception in " + this.getClass().toString() + " Cannot generate base64 string for PDF!",
							e);
				} catch (Exception e1) {
					log.error("Exception in " + this.getClass().toString(), e1);
				}*/
			}
			updateMTTPG(pG);
			updateLatestOrderStatus(pG.getRmsMTT().getMttId());
		}

		// reload mtt item to get latest order_status
		mtt = getMttFromOrderNo(orderNo).orElse(null);
		item.put("pymt_status", mtt.getOrder_status());
		
		if(item.get("rcpt_no") == null || item.get("rcpt_dt") == null) {
			MTTRCPT rcpt = getExistingReceipt(mtt.getMttId()).orElse(null);
			if(rcpt != null) {
				item.put("rcpt_no", rcpt.getRcptNo());
				item.put("rcpt_dt", rcpt.getRcptDt());
				item.put("rcpt_status", rcpt.getRcptStatus());
			}
		}
		
		return Collections.singletonList(item);
	}

	public List<Map<String, Object>> getOrderStatus(String orderNo) {
		int count = getOrnNoCount(orderNo);
		if(count != 1)
			return Collections.singletonList(
					Collections.singletonMap("Error", (count > 1 ? (count + " duplicates found for Order No. " + orderNo) :
						"Cannot find OnlinePayment record from Order No. ") + orderNo));
		OnlinePayment mtt = getMttFromOrderNo(orderNo).orElse(null);
		if (mtt == null) {
			log.debug("Exception in " + this.getClass().toString()
					+ ": Cannot find OnlinePayment record from Order No. " + orderNo);
			return Collections.singletonList(Collections.singletonMap("Error", 
					"Cannot find OnlinePayment record from Order No. " + orderNo));
		}

		// List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = new HashMap<>();
		item.put("orn_no", mtt.getOrnNo());
		item.put("pymt_status", mtt.getOrder_status());
		item.put("rcpt_no", "");
		item.put("rcpt_dt", "");
		item.put("rcpt_status", "");
		item.put("pg_txn_exists", 0);
		item.put("pg_query_desc", "");

		count = getRcptCount(mtt.getMttId());
		if(count > 1)
			return Collections.singletonList(Collections.singletonMap("Error", 
					count + " duplicates found for Rcpt with MttID: " + mtt.getMttId()));
		MTTRCPT rcpt = getExistingReceipt(mtt.getMttId()).orElse(null);
		if (rcpt != null) {
			MTTPG pG = getMttPgById(rcpt.getMttPG().getMttPgId()).orElse(null);
			if (pG == null) {
				log.debug("Exception in " + this.getClass().toString() + ": Cannot find MTTPG record from receipt ID: "
						+ Long.toString(rcpt.getMttRcptID()));
				return Collections.singletonList(item);
				//return Collections.singletonList(Collections.singletonMap("Error",
				//		"Cannot find MTTPG record from receipt ID: " + Long.toString(rcpt.getMttRcptID())));
			}

			item.put("rcpt_no", rcpt.getRcptNo());
			item.put("rcpt_dt", rcpt.getRcptDt());
			item.put("rcpt_status", rcpt.getRcptStatus());
			item.put("pg_txn_exists", pG.getPgTxnExsts());
			item.put("pg_query_desc", pG.getPgQueryDesc());
			return Collections.singletonList(item);
		}

		MTTPG pG = getFirstMttPgByPymtDtAndMttId(mtt.getMttId()).orElse(null);
		if (pG == null) {
			log.debug("Exception in " + this.getClass().toString() + ": Cannot find MTTPG record from mtt ID: "
					+ Long.toString(mtt.getMttId()));
			item.put("pg_txn_exists", 1);
			return Collections.singletonList(item);
			//return Collections.singletonList(Collections.singletonMap("Error",
			//		"Cannot find MTTPG record from mtt ID: " + Long.toString(mtt.getMttId())));
		}

		item.put("pg_txn_exists", pG.getPgTxnExsts());
		item.put("pg_query_desc", pG.getPgQueryDesc());
		return Collections.singletonList(item);
	}

	public List<Map<String, Object>> getReceiptDetails(String orderNo, String rcptNo) {
		// Check order: MTT -> RCPT(if MTT=null) -> MTT(extracted from RCPT) -> MTTItems
		// -> RCPT(skip if invoked previously)
		if (orderNo.isEmpty() && rcptNo.isEmpty()) {
			log.debug("Exception in " + this.getClass().toString() + ": Both fields orderNo & rcptNo are empty!");
			return Collections
					.singletonList(Collections.singletonMap("Error", "Both fields orderNo & rcptNo are empty!"));
		}

		int count = getOrnNoCount(orderNo);
		if(count > 1)
			return Collections.singletonList(Collections.singletonMap("Error", 
					count + " duplicates found for Order No. " + orderNo));
		
		Boolean rcptDone = false;
		Map<String, Object> item = new HashMap<>();
		OnlinePayment mtt = getMttFromOrderNo(orderNo).orElse(null);
		if (mtt == null) { // Will return null if orderNo is empty! Add case for if rcptNo exists...

			count = getRcptCount(rcptNo);
			if(count != 1)
				return Collections.singletonList(Collections.singletonMap("Error", 
						(count > 1 ? (count + " duplicates found for Rcpt No. " + rcptNo) 
								: "Cannot find Receipt record for ") + rcptNo));
			
			MTTRCPT rcpt = getExistingReceipt(rcptNo).orElse(null);
			if (rcpt == null) {
				log.debug("Exception in " + this.getClass().toString()
						+ ": Cannot find OnlinePayment record from Order No. " + orderNo
						+ " nor Receipt record from either rcptNo: " + rcptNo);
				item.put("Error", "Cannot find either MTT nor Receipt");
				return Collections.singletonList(item);
			}
			mtt = getMttFromMttId(rcpt.getRmsMTT().getMttId()).orElse(null); // Reload to bypass lazy load
			if (mtt == null) {
				log.debug("Exception in " + this.getClass().toString()
						+ ": Cannot find OnlinePayment record from MTTRCPT ID: " + rcpt.getMttRcptID().toString());
				return Collections.singletonList(Collections.singletonMap("Error",
						"Cannot find OnlinePayment record from MTTRCPT ID: " + rcpt.getMttRcptID().toString()));
			}
			item.put("rcpt_no", rcpt.getRcptNo());
			item.put("rcpt_dt", rcpt.getRcptDt());
			item.put("rcpt_status", rcpt.getRcptStatus());
			rcptDone = true;
		}
		item.put("orn_no", mtt.getOrnNo());
		item.put("orn_dt", mtt.getOrnDt().toString());
		item.put("cust_nm", mtt.getCust_nm());
		item.put("cust_addr_1", mtt.getCust_addr_1());
		item.put("cust_addr_2", mtt.getCust_addr_2());
		item.put("cust_addr_3", mtt.getCust_addr_3());
		item.put("cust_postcode", mtt.getCust_postcode());
		item.put("cust_city", mtt.getCust_city());
		item.put("cust_state", mtt.getCust_state());
		item.put("cust_email", mtt.getCust_email());
		item.put("cust_phone", mtt.getCust_phone());
		item.put("total_amt", mtt.getTotal_amt());
		item.put("pymt_status", mtt.getOrder_status());

		List<Map<String, Object>> mttItems = new ArrayList<Map<String, Object>>();

		getListOfItems(mtt.getMttId()).forEach(i -> {
			Map<String, Object> mttItem = new HashMap<>();
			mttItem.put("fee_detail_id", i.getFee_detail_id());
			mttItem.put("item_ref_no", i.getItem_ref_no());
			mttItem.put("item_desc", i.getItem_desc());
			mttItem.put("line_no", i.getLine_no());
			mttItem.put("qty", i.getQty());
			mttItem.put("unit_fee", i.getUnit_fee());
			mttItem.put("gross_amt", i.getGross_amt());
			mttItem.put("grant_cd", i.getGrant_cd());
			mttItem.put("disc_amt", i.getDisc_amt());
			mttItem.put("tax_pct", i.getTax_pct());
			mttItem.put("tax_amt", i.getTax_amt());
			mttItem.put("net_amt", i.getNet_amt());
			mttItem.put("entity_type", i.getEntity_type());
			mttItem.put("entity_no", i.getEntity_no());
			mttItem.put("entity_nm", i.getEntity_nm());
			mttItem.put("cp_no", i.getCp_no() == null ? "" : i.getCp_no());
			mttItem.put("cp_tier", i.getCp_tier() == null ? 0 : i.getCp_tier());
			mttItem.put("cp_tier_amt", i.getCp_tier_amt() == null ? 0 : i.getCp_tier_amt());
			mttItem.put("cp_tier_disc_pct", i.getCp_tier_discpct() == null ? 0 : i.getCp_tier_discpct());
			mttItems.add(mttItem);
		});
		item.put("mtt_items", mttItems);

		if (!rcptDone) {
			count = getRcptCount(rcptNo);
			if(count != 1)
				return Collections.singletonList(Collections.singletonMap("Error", 
						(count > 1 ? (count + " duplicates found for Rcpt No. " + rcptNo) 
								: "Cannot find Receipt record for ") + rcptNo));
			
			MTTRCPT rcpt = getExistingReceipt(rcptNo).orElse(getExistingReceipt(mtt.getMttId()).orElse(null));
			if (rcpt == null) {
				log.debug("Exception in " + this.getClass().toString()
						+ ": Cannot find Receipt record from either rcptNo: " + rcptNo
						+ "or MTTId: " + Long.toString(mtt.getMttId()));
				item.put("Error", "Cannot find receipt");
				return Collections.singletonList(item);
			}
			item.put("rcpt_no", rcpt.getRcptNo());
			item.put("rcpt_dt", rcpt.getRcptDt());
			item.put("rcpt_status", rcpt.getRcptStatus());
		}
		return Collections.singletonList(item);
	}

	public List<Map<String, Object>> getReceipt(String orderNo, String rcptNo) throws IOException {
		// Check order: MTT -> RCPT(if MTT=null) -> MTT(extracted from RCPT) -> MTTItems
		// -> RCPT(skip if invoked previously)
		if (orderNo.isEmpty() && rcptNo.isEmpty()) {
			log.debug("Exception in " + this.getClass().toString() + ": Both fields orderNo & rcptNo are empty!");
			return Collections
					.singletonList(Collections.singletonMap("Error", "Both fields orderNo & rcptNo are empty!"));
		}
		Boolean rcptDone = false;
		Map<String, Object> item = new HashMap<>();
		int count = 0;
		
		MTTRCPT rcpt = null;
		OnlinePayment mtt = null;

		if (!orderNo.isEmpty()) {
			count = getOrnNoCount(orderNo);
			if(count != 1)
				return Collections.singletonList(
						Collections.singletonMap("Error", (count > 1 ? (count + " duplicates found for Order No. " + orderNo) :
							"Cannot find OnlinePayment record from Order No. ") + orderNo));
			mtt = getMttFromOrderNo(orderNo).orElse(null);

		}

		if (mtt == null) {
			count = getRcptCount(rcptNo);
			if(count != 1)
				return Collections.singletonList(Collections.singletonMap("Error", 
						(count > 1 ? (count + " duplicates found for Rcpt No. " + rcptNo) 
								: "Cannot find Receipt record for ") + rcptNo));
			rcpt = getExistingReceipt(rcptNo).orElse(null);
			if (rcpt == null) {
				log.debug("Exception in " + this.getClass().toString()
						+ ": Cannot find OnlinePayment record from Order No. " + orderNo
						+ " nor Receipt record from either rcptNo: " + rcptNo);
				item.put("Error", "Cannot find either MTT nor Receipt");
				return Collections.singletonList(item);
			}
			mtt = getMttFromMttId(rcpt.getRmsMTT().getMttId()).orElse(null); // Reload to bypass lazy load
			if (mtt == null) {
				log.debug("Exception in " + this.getClass().toString()
						+ ": Cannot find OnlinePayment record from MTTRCPT ID: " + rcpt.getMttRcptID().toString());
				return Collections.singletonList(Collections.singletonMap("Error",
						"Cannot find OnlinePayment record from MTTRCPT ID: " + rcpt.getMttRcptID().toString()));
			}
			rcptDone = true;
		}
		item.put("orn_no", mtt.getOrnNo());

		if (!rcptDone) {
			count = getRcptCount(rcptNo);
			if(count > 1)
				return Collections.singletonList(Collections.singletonMap("Error", 
						count + " duplicates found for Rcpt No. " + rcptNo));
			rcpt = getExistingReceipt(rcptNo).orElse(getExistingReceipt(mtt.getMttId()).orElse(null));
		}

		if (rcpt == null) {
			log.debug("Exception in " + this.getClass().toString() + ": Cannot find Receipt record from either rcptNo: "
					+ rcptNo
					+ "or MTTId: " + Long.toString(mtt.getMttId()));
			item.put("Error", "Cannot find receipt");
			return Collections.singletonList(item);
		} else {
			// here need to check receipt order is it same with param order, if not same
			// return error
			String rcptOrn = rcpt.getRmsMTT().getOrnNo();
			if (!orderNo.isEmpty() && !rcptOrn.equals(orderNo)) {
				log.error("Exception in " + this.getClass().toString()
						+ ": Rcpt No Order No: "
						+ rcptOrn
						+ " and param order not match" + orderNo);
				item.put("Error", "Cannot find receipt");
				return Collections.singletonList(item);

			}

			// here need to check order receipt is it same with param receipt, if not same
			// return error
			String orderRcpt = rcpt.getRcptNo();
			if (!rcptNo.isEmpty() && !orderRcpt.equals(rcptNo)) {
				log.error("Exception in " + this.getClass().toString()
						+ ": Order Receipt No: "
						+ orderRcpt
						+ " and param receipt not match" + rcptNo);
				item.put("Error", "Cannot find receipt");
				return Collections.singletonList(item);
			}
		}

		item.put("rcpt_no", rcpt.getRcptNo());

		// List<IdamanAPIDownload> data =
		// idamanDS.idaman_api_downloadDoc(rcpt.getRcptNo(), rcpt.getVersionId(),
		// rcpt.getRcptUUID());
		List<IdamanAPIDownload> data = idamanDS.idaman_api_downloadDoc(
				new IdamanAPIDownloadRequest(rcpt.getRcptNo(), rcpt.getVersionId(), rcpt.getRcptUUID()));
		// if(data == null || data.equals(null) || data.size() == 0) {
		if (data == null || data.equals(null) || CollectionUtils.size(data) == 0) {
			log.error("Exception in " + this.getClass().toString() + ": no data found in parsed response list!");
			item.put("Error", "Cannot find idaman PDF");
			return Collections.singletonList(item);
		}

		String fileString = data.get(0).getFile_content();
		if (fileString != null && !fileString.equals("")) {
			item.put("file_type", data.get(0).getFile_nm().substring(data.get(0).getFile_nm().lastIndexOf(".") + 1));
			item.put("file_data", fileString);
		} else {
			log.error("Exception in " + this.getClass().toString() + ": Cannot get base64 encoded string"
					+ ", unknown error. Desc: " + data.get(0).getDesc());
			item.put("Error", "Cannot grab PDF from IDAMAN.");
			/*
			 * //Will generate receipt using this code (just for debugging)
			 * File pdfRcpt;
			 * try {
			 * MTTPG pG =
			 * getMttPgById(rcpt.getMttPG().getMttPgId()).orElse(rcpt.getMttPG());
			 * pdfRcpt = receiptGenerator.generateReceipt(new ReceiptRequest(pG, mtt, rcpt,
			 * getListOfItems(pG.getRmsMTT().getMttId()), "pdf"));
			 * item.put("file_type",
			 * pdfRcpt.getName().substring(pdfRcpt.getName().lastIndexOf(".") + 1));
			 * byte[] bytes = Files.readAllBytes(pdfRcpt.toPath());
			 * String base64EncFile = Base64.getEncoder().encodeToString(bytes);
			 * item.put("file_data", base64EncFile);
			 * } catch (IOException e) {
			 * log.error("Exception in " + this.getClass().toString(), e);
			 * }
			 */
		}

		return Collections.singletonList(item);
	}

	// @Transactional
	//@Transactional(rollbackFor = Exception.class)
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void updateMTTPG(MTTPG pG) {
		if (pG.getRmsMTT() != null) {
			if(pG.getPgTxnType() == null)
				pG.setPgTxnType("");
			else if(pG.getPgTxnType().length() > 7)
				pG.setPgTxnType(pG.getPgTxnType().substring(0, 7));
			if(pG.getPgPymtMethod() == null)
				pG.setPgPymtMethod("");
			else if(pG.getPgPymtMethod().length() > 3)
				pG.setPgPymtMethod(pG.getPgPymtMethod().substring(0, 3));
			if(pG.getPgServiceId() == null)
				pG.setPgServiceId("");
			else if(pG.getPgServiceId().length() > 3)
				pG.setPgServiceId(pG.getPgServiceId().substring(0, 3));
			if(pG.getPgPymtId() == null || pG.getPgPymtId().toLowerCase().equals("null"))
				pG.setPgPymtId(null);
			else if(pG.getPgPymtId().length() > 20)
				pG.setPgPymtId(pG.getPgPymtId().substring(0, 20));
			if(pG.getPgPymtDesc() == null)
				pG.setPgPymtDesc("");
			else if(pG.getPgPymtDesc().length() > 100)
				pG.setPgPymtDesc(pG.getPgPymtDesc().substring(0, 100));
			if(pG.getPgCurrCd() == null)
				pG.setPgCurrCd("");
			else if(pG.getPgCurrCd().length() > 3)
				pG.setPgCurrCd(pG.getPgCurrCd().substring(0, 3));
			if(pG.getPgLangCd() == null)
				pG.setPgLangCd("");
			else if(pG.getPgLangCd().length() > 2)
				pG.setPgLangCd(pG.getPgLangCd().substring(0, 2));
			if(pG.getPgTxnId() == null)
				pG.setPgTxnId("");			
			else if(pG.getPgTxnId().length() > 30)
				pG.setPgTxnId(pG.getPgTxnId().substring(0, 30));
			if(pG.getPgIssuingBank() == null)
				pG.setPgIssuingBank("");
			else if(pG.getPgIssuingBank().length() > 30)
				pG.setPgIssuingBank(pG.getPgIssuingBank().substring(0, 30));
			if(pG.getPgAuthCd() == null)
				pG.setPgAuthCd("");
			else if(pG.getPgAuthCd().length() > 12)
				pG.setPgAuthCd(pG.getPgAuthCd().substring(0, 12));
			if(pG.getPgHashValue() == null)
				pG.setPgHashValue("");
			else if(pG.getPgHashValue().length() > 100)
				pG.setPgHashValue(pG.getPgHashValue().substring(0, 100));
			if(pG.getPgHashValue2() == null)
				pG.setPgHashValue2("");
			else if(pG.getPgHashValue2().length() > 100)
				pG.setPgHashValue2(pG.getPgHashValue2().substring(0, 100));
			if(pG.getPgQhashValue() == null)
				pG.setPgQhashValue("");
			else if(pG.getPgQhashValue().length() > 100)
				pG.setPgQhashValue(pG.getPgQhashValue().substring(0, 100));
			if(pG.getPgQueryDesc() == null)
				pG.setPgQueryDesc("");
			else if(pG.getPgQueryDesc().length() > 255)
				pG.setPgQueryDesc(pG.getPgQueryDesc().substring(0, 255));
			if(pG.getPgSessionId() == null)
				pG.setPgSessionId("");
			else if(pG.getPgSessionId().length() > 255)
				pG.setPgSessionId(pG.getPgSessionId().substring(0, 255));
			if(pG.getPgTokenType() == null)
				pG.setPgTokenType("");
			else if(pG.getPgTokenType().length() > 3)
				pG.setPgTokenType(pG.getPgTokenType().substring(0, 3));
			if(pG.getPgToken() == null)
				pG.setPgToken("");
			else if(pG.getPgToken().length() > 50)
				pG.setPgToken(pG.getPgToken().substring(0, 50));
			if(pG.getPgCardNoMask() == null)
				pG.setPgCardNoMask("");
			else if(pG.getPgCardNoMask().length() > 19)
				pG.setPgCardNoMask(pG.getPgCardNoMask().substring(0, 19));
			if(pG.getPgCardHolder() == null)
				pG.setPgCardHolder("");
			else if(pG.getPgCardHolder().length() > 30)
				pG.setPgCardHolder(pG.getPgCardHolder().substring(0, 30));
			if(pG.getPgCardType() == null)
				pG.setPgCardType("");
			else if(pG.getPgCardType().length() > 10)
				pG.setPgCardType(pG.getPgCardType().substring(0, 10));
			if(pG.getPgCardExp() == null)
				pG.setPgCardExp("");
			else if(pG.getPgCardExp().length() > 6)
				pG.setPgCardExp(pG.getPgCardExp().substring(0, 6));

		    int attempts = 0;
		    while (true) {
		        try {
					mttPgRepository.save(pG);
		            break; // success
		        } catch (RollbackException | TransactionSystemException ex) {
		            if (++attempts >= 5) {
		                Throwable root = NestedExceptionUtils.getRootCause(ex);
		                if (root == null) {
		                    root = ex;
		                }
		                log.error("[MTTService] Failed to update MTTPG (ID:" + pG.getMttPgId() 
		                			+ ")\nRoot cause: ", root);
		            	return;
		            }
		            try {
		                Thread.sleep(200L * attempts); // simple backoff
		            } catch (InterruptedException ignored) {}
		        }
		    }
		}
		else
			log.error("Cannot find parent RMS-MTT object from RMS-MTT-PG ID-" + Long.toString(pG.getMttPgId()));
		log.debug("updateMTTPG(MTTPG) from " + this.getClass().toString() + " method called from Thread: "
				+ Thread.currentThread().getName());
	}
	
	public String getMttPgConfig(String paymentExpiryConfigName) {
		return mttPgRepository.getConfig(paymentExpiryConfigName);
	}

	@Transactional(readOnly=true)
	public List<MTTPG> getListOfMTTPG(boolean isNotExpiredMTTPGList, int payment_Date_Expiry_Limit_Days){
		return isNotExpiredMTTPGList ? mttPgRepository.sp_getMTTPGToCheck()			//findMTTPGByPgTxnExsts(2)
				: mttPgRepository.sp_getExpiredMTTPG(payment_Date_Expiry_Limit_Days, "expire");
	}
	
	// @Transactional
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public MTTRCPT saveMTTRcpt(MTTRCPT rcpt) {
		try {
			rcptRepo.save(rcpt);
			return rcpt;
		} catch (Exception e) {
			log.error("Exception in " + this.getClass().toString(), e);
			return null;
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public OnlinePaymentItem saveMTTItem(OnlinePaymentItem item) {
		try {
			itemListRepo.save(item);
			return item;
		} catch (Exception e) {
			log.error("Exception in " + this.getClass().toString(), e);
			return null;
		}
	}

	@Transactional(rollbackFor = Exception.class)
	public Integer deleteMTTItem(OnlinePaymentItem item) {
		try {
			itemListRepo.save(item);
			return 1;
		} catch (Exception e) {
			log.error("Exception in " + this.getClass().toString(), e);
			return 0;
		}
	}

	// @Transactional
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public void updateLatestOrderStatus(int mttID) {
		try {
			mttPgRepository.sp_updateLatestOrderStatus(mttID);
		} catch (Exception e) {
			log.error("Exception in " + this.getClass().toString(), e);
		}
	}

	// @Transactional
	@Transactional(rollbackFor = Exception.class)
	public String getReceiptRunningNo() {
		try {
			return rcptRepo.sp_getrunno();
		} catch (Exception e) {
			log.error("Exception in " + this.getClass().toString(), e);
			return "";
		}
	}

	// @Transactional(readOnly=true)
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public List<OnlinePaymentItem> getListOfItems(int rmsMTTId) {
		return itemListRepo.sp_getmttitem(rmsMTTId);
	}
	
	// @Transactional(readOnly=true)
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public List<MTTPG> getAllMttPgsByMttId(int mttId) {
		return mttPgRepository.findAllByrmsMTT_mttId(mttId);
	}

	// @Transactional(readOnly=true)
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public Optional<MTTRCPT> getExistingReceipt(int rmsMTTId) {
		return rcptRepo.sp_getRcptByMttId(rmsMTTId);
	}

	// @Transactional(readOnly=true)
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public Optional<MTTRCPT> getExistingReceipt(String rcptNo) {
		return rcptRepo.sp_getRcptByRcptNo(rcptNo);
	}

	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public int getRcptCount(String rcptNo) {
		Integer count = rcptRepo.sp_checkRcptDuplicate(rcptNo);
		return count == null ? 0 : count;
	}	
	
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public int getRcptCount(int mttId) {
		Integer count = rcptRepo.sp_checkRcptDuplicate(mttId);
		return count == null ? 0 : count;
	}	
	
	// @Transactional(readOnly=true)
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public Optional<OnlinePayment> getMttFromOrderNo(String orderNo) {
		//return mttRepo.getOnlinePaymentByOrnNo(orderNo);
		return mttRepo.sp_getOneMTT(orderNo);
	}
	
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public int getOrnNoCount(String orderNo) {
		Integer count = mttRepo.sp_checkOrnDuplicate(orderNo);
		return count == null ? 0 : count;
	}	

	// @Transactional(readOnly=true)
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public Optional<OnlinePayment> getMttFromMttId(Integer mttId) {
		return mttRepo.getOnlinePaymentByMttId(mttId);
	}

	// @Transactional(readOnly=true)
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public Optional<MTTPG> getMttPgById(Long mttPgId) {
		return mttPgRepository.findMTTPGByMttPgId(mttPgId);
	}

	// @Transactional(readOnly=true)
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public Optional<MTTPG> getFirstMttPgByPymtDtAndMttId(int mttId) {
		return mttPgRepository.findFirstByrmsMTT_mttIdOrderByPymtSubmitDtDesc(mttId);
	}

	
	@Override
	public List<OnlinePaymentItem> sp_getMTTItem(Integer mttId) {
		List<OnlinePaymentItem> result = Collections.emptyList();

		try {

			result = convertToOnlinePaymentItemList(mttRepository.sp_getMTTItem(mttId));
		} catch (NumberFormatException e) {

			log.error("Exception in " + this.getClass().toString(), e);

		} catch (Exception e) {

			log.error("Exception in " + this.getClass().toString(), e);

		} finally {

		}

		return result;
	}

	@Override
	public Integer sp_updateMTT(String ornNo, String billingNm, String custAddr1, String custAddr2, String custAddr3,
			String custPostCode, String custCity, String custState) {
		Integer result = 0;

		try {

			result = mttRepository.sp_updateMTT(ornNo, billingNm, custAddr1, custAddr2, custAddr3,
					custPostCode, custCity, custState);
		} catch (NumberFormatException e) {

			log.error("Exception in " + this.getClass().toString(), e);

		} catch (Exception e) {

			log.error("Exception in " + this.getClass().toString(), e);

		} finally {

		}

		return result;
	}

	@Override
	public BigInteger sp_updateMTTStatus(String ornNo, String created_by, String modified_by) {
		BigInteger result = BigInteger.ZERO;

		try {

			result = mttRepository.sp_updateMTTStatus(ornNo, created_by, modified_by);

		} catch (NumberFormatException e) {

			log.error("Exception in " + this.getClass().toString(), e);

		} catch (Exception e) {

			log.error("Exception in " + this.getClass().toString(), e);

		} finally {

		}

		return result;
	}

	@Override
	public Integer sp_checkLatestOrderStatus2(String ornNo, BigDecimal totalAmt) {
		// TODO Auto-generated method stub
		Integer result = -1;

		try {

			result = mttRepository.sp_checkLatestOrderStatus2(ornNo, totalAmt);

		} catch (NumberFormatException e) {

			log.error("Exception in " + this.getClass().toString(), e);

		} catch (Exception e) {

			log.error("Exception in " + this.getClass().toString(), e);

		} finally {

		}

		return result;
	}

	@Override
	public String sp_checkLatestOrderStatus(String ornNo) {
		// TODO Auto-generated method stub
		String result = "";

		try {

			result = mttRepository.sp_checkLatestOrderStatus(ornNo);
			if (result == "") {
				result = "empty";
			}

		} catch (NumberFormatException e) {

			log.error("Exception in " + this.getClass().toString(), e);

		} catch (Exception e) {

			log.error("Exception in " + this.getClass().toString(), e);

		} finally {

		}

		return result;
	}

	private List<OnlinePaymentItem> convertToOnlinePaymentItemList(List<Object[]> objects) {
		List<OnlinePaymentItem> onlinePaymentItems = new ArrayList<>();

		for (Object[] obj : objects) {
			// Extract values from obj array and cast them to their respective types
			Integer mtt_item_id = (Integer) obj[0];
			Integer mtt_id = (Integer) obj[1];
			Integer fee_detail_pk = (Integer) obj[2];
			Integer line_no = (Integer) obj[3];
			String item_desc = (String) obj[4];
			Integer qty = (Integer) obj[5];
			BigDecimal unit_fee = (BigDecimal) obj[6];
			BigDecimal tax_amt = (BigDecimal) obj[7];
			BigDecimal disc_amt = (BigDecimal) obj[8];
			BigDecimal gross_amt = (BigDecimal) obj[9];
			String fee_detail_id = (String) obj[10];
			String item_ref_no = (String) obj[11];
			String grant_cd = (String) obj[12];
			BigDecimal tax_pct = (BigDecimal) obj[13];
			BigDecimal net_amt = (BigDecimal) obj[14];
			String entity_type = (String) obj[15];
			String entity_no = (String) obj[16];
			String entity_nm = (String) obj[17];
			String cp_no = (String) obj[18];
			Integer cp_tier = (Integer) obj[19];
			BigDecimal cp_tier_amt = (BigDecimal) obj[20];
			BigDecimal cp_tier_discpct = (BigDecimal) obj[21];
			String dps_id = (String) obj[22];
			String dps_task = (String) obj[23];
			String pymt_case = (String) obj[24];
			String location = (String) obj[25];
			String lit_item_ref = (String) obj[26];
			String txn_type = (String) obj[27];
			Integer calendar_yr = (Integer) obj[28];

			// Create a new OnlinePaymentItem instance using the extracted values
			OnlinePaymentItem onlinePaymentItem = new OnlinePaymentItem();
			onlinePaymentItem.setMtt_item_id(mtt_item_id);
			onlinePaymentItem.setMtt_id(mtt_id);
			onlinePaymentItem.setFee_detail_pk(fee_detail_pk);
			onlinePaymentItem.setLine_no(line_no);
			onlinePaymentItem.setItem_desc(item_desc);
			onlinePaymentItem.setQty(qty);
			onlinePaymentItem.setUnit_fee(unit_fee);
			onlinePaymentItem.setTax_amt(tax_amt);
			onlinePaymentItem.setDisc_amt(disc_amt);
			onlinePaymentItem.setGross_amt(gross_amt);
			onlinePaymentItem.setFee_detail_id(fee_detail_id);
			onlinePaymentItem.setItem_ref_no(item_ref_no);
			onlinePaymentItem.setGrant_cd(grant_cd);
			onlinePaymentItem.setTax_pct(tax_pct);
			onlinePaymentItem.setNet_amt(net_amt);
			onlinePaymentItem.setEntity_type(entity_type);
			onlinePaymentItem.setEntity_no(entity_no);
			onlinePaymentItem.setEntity_nm(entity_nm);
			onlinePaymentItem.setCp_no(cp_no);
			onlinePaymentItem.setCp_tier(cp_tier);
			onlinePaymentItem.setCp_tier_amt(cp_tier_amt);
			onlinePaymentItem.setCp_tier_discpct(cp_tier_discpct);
			onlinePaymentItem.setDps_id(dps_id);
			onlinePaymentItem.setDps_task(dps_task);
			onlinePaymentItem.setPymt_case(pymt_case);
			onlinePaymentItem.setLocation(location);
			onlinePaymentItem.setLit_item_ref(lit_item_ref);
			onlinePaymentItem.setTxn_type(txn_type);
			onlinePaymentItem.setCalendar_yr(calendar_yr);

			// Add the OnlinePaymentItem instance to the onlinePaymentItems list
			onlinePaymentItems.add(onlinePaymentItem);
		}
		return onlinePaymentItems;
	}

	public GHLPayment sp_insertPayment(Integer mttID, String pymtMethod, String serviceID,
			BigDecimal pymtAmt, String langCd, String usernameC,
			String usernameM) {

		// TODO Auto-generated method stub
		GHLPayment result = new GHLPayment();
		// Integer result=0;

		try {

			result = convertToGHLPayment(mttRepository.sp_insertPayment(mttID, pymtMethod, serviceID,
					pymtAmt, langCd, usernameC, usernameM));

		} catch (NumberFormatException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

		}

		return result;
	}

	@Override
	public Integer sp_updatePayment(GHLPaymentResponse ghlResponse, String usernameM) {

		// TODO Auto-generated method stub
		// GHLPayment result = new GHLPayment();
		Integer result = 0;

		// 1219: Fixed for cancel button as resp time will not be returned by GHL
		try {

			if (ghlResponse.getRespTime() != null) {
				// Decode the response time
				String decodedInput = URLDecoder.decode(ghlResponse.getRespTime(), "UTF-8");
				ghlResponse.setRespTime(decodedInput);
			}

			// String decodedInput = URLDecoder.decode(ghlResponse.getRespTime(), "UTF-8");

			// Define a formatter for the output date format
			// DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd
			// HH:mm:ss.SSS");

			// Parse the input date string
			// LocalDateTime inputDateTime = LocalDateTime.parse(decodedInput,
			// DateTimeFormatter.ofPattern("yyyy-MM-dd+HH:mm:ss"));

			// Format the parsed date and time as per the desired format
			// ghlResponse.setRespTime(decodedInput);

			result = mttRepository.sp_updatePayment(ghlResponse, usernameM);

		} catch (NumberFormatException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

		}

		return result;
	}

	@Override
	public Integer sp_checkPaymentRcpt(String ornNo) {

		// TODO Auto-generated method stub
		// GHLPayment result = new GHLPayment();
		Integer result = 0;

		try {

			result = mttRepository.sp_checkPaymentRcpt(ornNo);

		} catch (NumberFormatException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

		}

		return result;
	}

	@Override
	public MTTRCPT sp_insertReceipt(String paymentId, String username) {

		MTTRCPT result = new MTTRCPT();
		// Integer result=0;

		try {

			result = convertToMTTRCPT(mttRepository.sp_insertReceipt(paymentId, username));

		} catch (NumberFormatException e) {

			e.printStackTrace();

		} catch (Exception e) {

			e.printStackTrace();

		} finally {

		}

		return result;
	}

	// @Transactional(readOnly = true)
	@Transactional(readOnly = true, rollbackFor = Exception.class)
	public MTTRCPT convertToMTTRCPT(Object[] obj) {
		MTTRCPT mttrcpt = new MTTRCPT();

		// Extract values from the obj and cast them to their respective types
		// Assuming the order and types match your MTTRCPT class
		// BigInteger mttRcptID = (BigInteger) obj[0];
		// OnlinePayment rmsMTT = (OnlinePayment) obj[1]; // Assuming OnlinePayment is
		// the correct type
		// MTTPG mttPG = (MTTPG) obj[2]; // Assuming MTTPG is the correct type
		String rcptNo = (String) obj[3];
		// LocalDateTime rcptDt = (LocalDateTime) obj[4];
		// String rcptStatus = (String) obj[5];
		// Integer rcptReprint = (Integer) obj[6];
		// Integer isUploaded = (Integer) obj[7];
		// LocalDateTime dtCreated = (LocalDateTime) obj[8];
		// LocalDateTime dtModified = (LocalDateTime) obj[9];
		// String createdBy = (String) obj[10];
		// String modifiedBy = (String) obj[11];

		// Set the values in the MTTRCPT instance
		mttrcpt = rcptRepo.sp_getRcptByRcptNo(rcptNo).orElse(null);

		return mttrcpt;
	}

	private GHLPayment convertToGHLPayment(Object[] obj) {
		GHLPayment ghLPayment = new GHLPayment();

		// Extract values from the obj and cast them to their respective types
		// Assuming the order and types match your GHLPayment class
		String pymtId = (String) obj[0];
		String transactionType = (String) obj[1];
		String pymtMethod = (String) obj[2];
		String serviceId = (String) obj[3];
		String ordNo = (String) obj[4];
		String pymtDesc = (String) obj[5];
		String returnUrl = (String) obj[6];
		BigDecimal amt = (BigDecimal) obj[7];
		String currCd = (String) obj[8];
		String custIp = (String) obj[9];
		String custNm = (String) obj[10];
		String custPh = (String) obj[11];
		String hashValue = (String) obj[12];
		Integer pageTimeout = Integer.parseInt((String) obj[13]);
		String custEmail = (String) obj[14];

		// Set the values in the GHLPayment instance
		ghLPayment.setTransaction_type(transactionType);
		ghLPayment.setPymt_method(pymtMethod);
		ghLPayment.setService_id(serviceId);
		ghLPayment.setPymt_id(pymtId);
		ghLPayment.setOrd_no(ordNo);
		ghLPayment.setPymt_desc(pymtDesc);
		ghLPayment.setReturn_url(returnUrl);
		ghLPayment.setAmt(amt);
		ghLPayment.setCurr_cd(currCd);
		ghLPayment.setCust_ip(custIp);
		ghLPayment.setCust_nm(custNm);
		ghLPayment.setCust_ph(custPh);
		ghLPayment.setHash_value(hashValue);
		ghLPayment.setPage_timeout(pageTimeout);
		ghLPayment.setCust_email(custEmail);
		ghLPayment.setApproved_url(returnUrl);
		ghLPayment.setUnapproved_url(returnUrl);

		return ghLPayment;
	}

	@Override
	public Integer sp_updateMTTRcpt(Integer mttRcptID, String verID, String ssDocRefID) {
		Integer result = 0;
		try {
			result = mttRepository.sp_updateMTTRcpt(mttRcptID, verID, ssDocRefID);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	// weiern add
	@Override
	public Integer sp_checkornno(String ornno) {
		Integer result = 0;
		try {
			result = mttRepository.sp_checkornno(ornno);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public Integer sp_checktxn(String ornno, String pymt_id) {
		Integer result = 0;
		try {
			result = mttRepository.sp_checktxn(ornno, pymt_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	@Override
	public String sp_checktxnid(String ornno, String pymt_id) {
		String result = "";
		try {
			result = mttRepository.sp_checktxnid(ornno, pymt_id);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public Integer sp_updatemttordstatus(String ornNo, String orderStatus, String username) {
		return mttRepository.sp_updatemttordstatus(ornNo, orderStatus, username);
	}

	// shceduler
	@Override
	public List<EmailPP> sp_getemailpp(Integer mttID) {
		List<EmailPP> result = Collections.emptyList();

		List<Object[]> objects = mttRepository.sp_getemailpp(mttID);
		result = convertEmailPPList(objects);

		return result;
	}

	private List<EmailPP> convertEmailPPList(List<Object[]> objects) {
		List<EmailPP> emailPPList = new ArrayList<>();

		for (Object[] obj : objects) {
			EmailPP emailPP = new EmailPP();

			emailPP.setMtt_id((String) obj[0]);
			emailPP.setCust_email((String) obj[1]);
			emailPP.setOrn_no((String) obj[2]);
			emailPP.setTotal_amt((BigDecimal) obj[3]);
			emailPP.setOrder_status((String) obj[4]);
			emailPP.setEntity_nm((String) obj[5]);
			emailPP.setEmail_flag((Integer) obj[6]);

			emailPPList.add(emailPP);
		}

		return emailPPList;
	}

	public Integer sp_refreshMTTEmailExpDt(String ornNo, String username) {
		return mttRepository.sp_refreshMTTEmailExpDt(ornNo, username);
	}

	public MTTRCPT sp_getmttrcptinfo_v2(String orn_no) {

		MTTRCPT result = new MTTRCPT();

		try {

			result = convertToMTTRCPT(mttRepository.sp_getmttrcptinfo_v2(orn_no));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}
}
