package com.maven.rms.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.maven.rms.models.Billing.Billing;
import com.maven.rms.models.Email;
import com.maven.rms.models.FMSARIModel;
import com.maven.rms.models.FMSCRMemo;
import com.maven.rms.models.FMSDRMemo;
import com.maven.rms.models.MFT;
import com.maven.rms.models.MFTRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.maven.rms.models.BillingStatus;
import com.maven.rms.models.BillingStatusRequest;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.RMSUser;
import com.maven.rms.models.payload.requests.BillingRegistrationIncoming;
import com.maven.rms.repositories.BillingRepository;
import com.maven.rms.repositories.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillingService {
	private final BillingRepository bRepo;
	private final UserRepository userRepo;

	@Autowired
	private MTTService mttSvc;
	@Autowired
	private MFTService mftSvc;
	@Autowired
	private EmailService emailSvc;
	@Autowired
	private FMSARIService ariSvc;
	@Autowired
	private FMSCRMemoService crSvc;
	@Autowired
	private FMSDRMemoService drSvc;

	@Value("${rms.application.onlinePortalURL}")
	private String onlinePortalUrl;
	// @Value("${rms.application.onlinePortalURL}")
	// private String publicPortalUrl;

	public BillingService(BillingRepository bRepo, UserRepository userRepo) {
		this.bRepo = bRepo;
		this.userRepo = userRepo;
	}

	public HashMap<String, List<Object>> sp_getbillingregistrationtypecodelist() {
		return bRepo.sp_getbillingregistrationtypecodelist();
	}

	public String sp_getfreerunnofullBilling() {
		return bRepo.sp_getfreerunnofullBilling();
	}

	public List<List<String>> sp_getusersbyrole(String userRoleEn) {
		List<List<String>> users = new ArrayList<List<String>>();
		for (RMSUser user : this.userRepo.sp_getusersbyrole(userRoleEn).orElse(null)) {
			List<String> info = new ArrayList<String>();
			info.add(user.getSsm4uuserrefno());
			info.add(user.getNm());
			info.add(user.getEmail());
			users.add(info);
		}
		return users;
	}

	public Integer sp_checkbillreginfo(Map<String, String> paramCheckList) {
		return bRepo.sp_checkbillreginfo(paramCheckList);
	}

	public String sp_getandreservebillrunno(int reserveBillingNoCount) {
		return bRepo.sp_getandreservebillrunno(reserveBillingNoCount);
	}

	public Integer sp_updatebillstatuspaid(String billingNo, String username) throws JsonProcessingException {
		// log.error(billingNo + " - " + username + " - sp_updatebillstatuspaid func
		// called!");
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("billingNo", billingNo);
		data.put("amt", new BigDecimal(0));
		data.put("adj", false);
		data.put("adjData", null);
		
		Integer statusCode = creditBill(data);
		log.error(statusCode.toString() + " - " + billingNo + " - " + username
				+ " - sp_updatebillstatuspaid func called!");

		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString() + "sp_updatebillstatuspaid func - "
					+ "status code: " + statusCode.toString() + "!");

			Integer statusCode2 = bRepo.sp_insbilhistbybillno(billingNo, "system", "Failed to generate credit memo!",
					"R");
			if (statusCode2 < 1)
				log.error("Exception in " + this.getClass().toString()
						+ "sp_insbilhistbybillno @ sp_updatebillstatuspaid func - "
						+ "status code: " + statusCode2.toString() + "!");

		}

		return statusCode > 0 ? bRepo.sp_updatebillstatuspaid(billingNo, username) : statusCode;
	}

	public Integer insNewBilReg(BillingRegistrationIncoming payload, String username)
			throws SerialException, SQLException {
		Integer bilWfId = bRepo.sp_insnewbilreg(payload, username);

		if (bilWfId < 1) {
			log.error("Exception in " + this.getClass().toString() + "insNewBilReg func - "
					+ "sp_insnewbilreg status code: " + bilWfId.toString() + "!");
			return bilWfId;
		}

		Integer statusCode = 0;
		for (Map<String, Object> bItems : payload.getI_billing_items()) {
			// if((Integer)bItems.get("qty") == 0)
			// continue;
			try {
				statusCode = bRepo.sp_insnewbilregitem(bilWfId, null, bItems, username);
			} catch (EmptyResultDataAccessException e) {
				log.error("Exception in " + this.getClass().toString() + "insNewBilReg func - "
						+ "sp_insnewbilregitem - item: " + (String) bItems.get("desc") + " - "
						+ "status code: " + statusCode.toString() + "!");
				bRepo.sp_rollbacknewbilreg(bilWfId);
				return statusCode;
			}

			if (statusCode < 1) {
				log.error("Exception in " + this.getClass().toString() + "insNewBilReg func - "
						+ "sp_insnewbilregitem - item: " + (String) bItems.get("desc") + " - "
						+ "status code: " + statusCode.toString() + "!");
				bRepo.sp_rollbacknewbilreg(bilWfId);
				return statusCode;
			}
		}

		if (payload.getI_billing_method().equals("L") || payload.getI_billing_method().equals("A")) {
			for (Map<String, Object> bItems : payload.getI_billing_issuance_list()) {
				DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
						Locale.ENGLISH);
				Map<String, Object> childBill = new HashMap<String, Object>();
				childBill.put("i_bil_wf_id", bilWfId);
				childBill.put("i_bil_id", null);
				childBill.put("i_bil_child_date",
						LocalDateTime.parse((String) bItems.get("billing_date"), inputFormatter));
				childBill.put("i_billing_no", (String) bItems.get("billing_no"));
				childBill.put("i_created_by", username);
				try {
					statusCode = bRepo.sp_insnewbilregchild(childBill);
				} catch (EmptyResultDataAccessException e) {
					log.error("Exception in " + this.getClass().toString() + "insNewBilReg func - "
							+ "sp_insnewbilregchild - item: " + (String) bItems.get("billing_no") + " - "
							+ "status code: " + statusCode.toString() + "!");
					bRepo.sp_rollbacknewbilreg(bilWfId);
					return statusCode;
				}
				if (statusCode < 1) {
					log.error("Exception in " + this.getClass().toString() + "insNewBilReg func - "
							+ "sp_insnewbilregchild - item: " + (String) bItems.get("billing_no") + " - "
							+ "status code: " + statusCode.toString() + "!");
					bRepo.sp_rollbacknewbilreg(bilWfId);
					return statusCode;
				}
			}
			try {
				bRepo.sp_insnewbilregdoc(bilWfId, payload.getI_loa_document(), username);
			} catch (EmptyResultDataAccessException e) {
				log.error("Exception in " + this.getClass().toString() + "insNewBilReg func - "
						+ "sp_insnewbilregdoc - item: " + (String) payload.getI_loa_document().get("i_file_nm")
						+ " (LOA/AGMT doc) - "
						+ "status code: " + statusCode.toString() + "!");
				bRepo.sp_rollbacknewbilreg(bilWfId);
				return statusCode;
			}
			if (statusCode < 1) {
				log.error("Exception in " + this.getClass().toString() + "insNewBilReg func - "
						+ "sp_insnewbilregdoc - item: " + (String) payload.getI_loa_document().get("i_file_nm")
						+ " (LOA/AGMT doc) - "
						+ "status code: " + statusCode.toString() + "!");
				bRepo.sp_rollbacknewbilreg(bilWfId);
				return statusCode;
			}
		}

		if (payload.getI_supporting_documents() != null && payload.getI_supporting_documents().size() > 0)
			for (Map<String, Object> bItems : payload.getI_supporting_documents()) {
				try {
					statusCode = bRepo.sp_insnewbilregdoc(bilWfId, bItems, username);
				} catch (EmptyResultDataAccessException e) {
					log.error("Exception in " + this.getClass().toString() + "insNewBilReg func - "
							+ "sp_insnewbilregdoc - item: " + (String) bItems.get("i_file_nm") + " - "
							+ "status code: " + statusCode.toString() + "!");
					bRepo.sp_rollbacknewbilreg(bilWfId);
					return statusCode;
				}
				if (statusCode < 1) {
					log.error("Exception in " + this.getClass().toString() + "insNewBilReg func - "
							+ "sp_insnewbilregdoc - item: " + (String) bItems.get("i_file_nm") + " - "
							+ "status code: " + statusCode.toString() + "!");
					bRepo.sp_rollbacknewbilreg(bilWfId);
					return statusCode;
				}
			}
		return statusCode;
	}

	public List<Map<String, Object>> sp_getbilhist(String billingNo, int page, int size) {
		return bRepo.sp_getbilhist(billingNo, page, size);
	}

	public List<Map<String, Object>> sp_getbilitems(String billingNo, int page, int size, String bilItemStatus) {
		return bRepo.sp_getbilitems(billingNo, page, size, bilItemStatus);
	}

	public List<Map<String, Object>> sp_getbilchildren(String billingNo, int page, int size) {
		return bRepo.sp_getbilchildren(billingNo, page, size);
	}

	public List<Map<String, Object>> sp_getbildoc(String billingNo, int page, int size) {
		return bRepo.sp_getbildoc(billingNo, page, size);
	}

	public String sp_getbildocblob(Integer bilDocId) throws SQLException, IOException {
		Blob data = bRepo.sp_getbildocblob(bilDocId);
		byte[] bytes = data.getBytes(1, (int) data.length());
		data.free();
		return Base64.getEncoder().encodeToString(bytes);
	}

	public String sp_getbilchildimgblob(Integer bilChildId) throws SQLException, IOException {
		byte[] bytes = bRepo.sp_getbilchildimgblob(bilChildId);
		return Base64.getEncoder().encodeToString(bytes);
	}

	public Integer sp_rejectunapprovedbilreg(String billingNo, String username, String remark) {
		return bRepo.sp_rejectunapprovedbilreg(billingNo, username, remark);
	}

	public Integer sp_cancelunapprovedbilreg(String billingNo, String username, String remark) {
		return bRepo.sp_cancelunapprovedbilreg(billingNo, username, remark);
	}

	public Integer sp_queryunapprovedbilreg(Map<String, Object> payload, String username)
			throws SerialException, SQLException {

		List<Map<String, Object>> i_supporting_documents = (List<Map<String, Object>>) payload.get("i_supporting_documents");
		Integer statusCode = 0;
		
		if (i_supporting_documents != null && i_supporting_documents.size() > 0) {
			Map<String, Object> request = new HashMap<String, Object>();
			request.put("billing_no", (String) payload.get("i_billing_no"));
			request.put("page", 1);
			request.put("size", Integer.MAX_VALUE);
			request.put("bil_item_status", "A");
			request.put("more_info", false);

			Billing bill = sp_getbill(request);
			if (bill == null) {
				log.error("Exception in " + this.getClass().toString()
						+ "sp_queryunapprovedbilreg func - sp_getbill failed!");
				return -1;
			}
			
			Integer bilWfId = bill.getBil_wf_id();
			
			for (Map<String, Object> bItems : i_supporting_documents) {
				try {
					statusCode = bRepo.sp_insnewbilregdoc(bilWfId, bItems, username);
				} catch (EmptyResultDataAccessException e) {
					log.error("Exception in " + this.getClass().toString() + "sp_queryunapprovedbilreg func - "
							+ "sp_insnewbilregdoc - item: " + (String) bItems.get("i_file_nm") + " - "
							+ "status code: " + statusCode.toString() + "!");
					bRepo.sp_rollbacknewbilreg(bilWfId);
					return statusCode;
				}
				if (statusCode < 1) {
					log.error("Exception in " + this.getClass().toString() + "sp_queryunapprovedbilreg func - "
							+ "sp_insnewbilregdoc - item: " + (String) bItems.get("i_file_nm") + " - "
							+ "status code: " + statusCode.toString() + "!");
					bRepo.sp_rollbacknewbilreg(bilWfId);
					return statusCode;
				}
			}
		}
		return bRepo.sp_queryunapprovedbilreg((String) payload.get("i_billing_no"), username, (String) payload.get("i_remark"));
	}

	public Integer updBilWFItem(Map<String, Object> payload, String username) {
		String billingNo = (String) payload.get("i_billing_no");
		List<Map<String, Object>> items = (List<Map<String, Object>>) payload.get("i_billing_items");
		Integer statusCode = 0;
		for (Map<String, Object> item : items) {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("billing_no", billingNo);
			data.put("price", item.get("unit_fee"));
			data.put("qty", item.get("qty"));
			data.put("taxc", item.get("tax_amt"));
			data.put("total", item.get("final_amt"));
			data.put("mftPk", item.get("mft_pk"));

			statusCode = bRepo.sp_updbilwfitem(data, username);
			if (statusCode < 1) {
				log.error("Exception in " + this.getClass().toString()
						+ "updBilWFItem func - sp_updbilwfitem failed! Code: " + statusCode.toString());
				return statusCode;
			}
		}
		return statusCode;
	}

	public Integer updBilWFDetails(Map<String, Object> payload, String username) {
		String billingNo = (String) payload.get("i_billing_no");
		Map<String, Object> item = (Map<String, Object>) payload.get("i_billing_info");
		item.put("i_billing_no", billingNo);
		Integer statusCode = bRepo.sp_updbilwfDet(item, username);
		if (statusCode < 1)
			log.error("Exception in " + this.getClass().toString()
					+ "updBilWFItem func - sp_updbilwfitem failed! Code: " + statusCode.toString());

		return statusCode;
	}

	public Integer sp_approvebilreg(String billingNo, String username, String remark) {
		return bRepo.sp_approvebilreg(billingNo, username, remark);
	}

	public Integer sp_confirmnewbill(String billingNo, String username) {
		return bRepo.sp_confirmnewbill(billingNo, username);
	}

	public Map<String, Object> sp_getbilllisting(Map<String, Object> request) {
		return bRepo.sp_getbilllisting(request);
	}

	public List<String> sp_getapprovedbilltoissue() {
		return bRepo.sp_getapprovedbilltoissue();
	}
	
	public Integer debugARI(String billingNo) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("billing_no", billingNo);
		request.put("page", 1);
		request.put("size", Integer.MAX_VALUE);
		request.put("bil_item_status", "A");
		request.put("more_info", true);

		Billing bill = sp_getbill(request);
		if (bill == null) {
			log.error("Exception in " + this.getClass().toString()
					+ "confirmBill func - sp_getbill failed!");
			return -1;
		}
		
		StringBuilder bilChildNoList = new StringBuilder();
		Map<String, Object> iss = null; // = bRepo.sp_getbilchild(billingNo);
		for (Map<String, Object> child : bill.getBilling_list()) {
			if (child.get("bil_no").equals(billingNo)) {
				iss = child;
				bilChildNoList.append(child.get("bil_no"));
				break;
			}
			bilChildNoList.append(child.get("bil_no") + ",");
		}
		if (iss == null || iss.get("bil_child_date") == null) {
			iss = bRepo.sp_getbilchild(billingNo);

			if (iss == null) {
				log.error("Exception in " + this.getClass().toString()
						+ "confirmBill func - bil_child object is null in bill! List of children: "
						+ bilChildNoList.toString());
				return -2;
			} else if (iss.get("bil_child_date") == null) {
				log.error("Exception in " + this.getClass().toString()
						+ "confirmBill func - bil_child key 'bil_child_date' item is null! List of children: "
						+ bilChildNoList.toString());
				return -2;
			}
		}

		BigDecimal ttl = new BigDecimal(0);
		for (Map<String, Object> item : bill.getBilling_items())
			ttl = ttl.add((BigDecimal) item.get("final_amt"));

		//if (((Integer) iss.get("billing_img")) == 0) {
			List<FMSARIModel> invoices = ariSvc.sp_getfmsaribybilchildid((Integer) iss.get("bil_child_id"));
			BigInteger hid = BigInteger
					.valueOf(ariSvc.sp_getfmsarihidbyaribodybilchildid((Integer) iss.get("bil_child_id")));

			if (invoices.size() < 1) {
				FMSARIModel ari = new FMSARIModel();
				ari.setType("Invoice");
				//ari.setLink_branch("00000");
				ari.setCust(bill.getCust_id());
				ari.setDesc(bill.getBilling_desc());
				ari.setAttr_ext_sys("RMS");
				ari.setGeneratePDF(1);
				ari.setCreated_by("system");
				ari.setAmt(ttl);
				ari.setInv_dt(LocalDateTime.now());

				if (hid == null || hid.compareTo(BigInteger.ZERO) < 1) {
					log.error("[debugARI] Inserting " + billingNo + " items into fmsari_h.");	
					hid = ariSvc.sp_insfmsari_h(ari);
				}

				if (hid == null || hid.compareTo(BigInteger.ZERO) < 1) {
					log.error("Exception in " + this.getClass().toString()
							+ "confirmBill func - sp_insfmsmtth failed!");
					return -3;
				}

				invoices = new ArrayList<FMSARIModel>();
				log.error("[debugARI] Inserting " + bill.getBilling_items().size() + " items into fmsmttb.");
				for (Map<String, Object> item : bill.getBilling_items()) {
					if(((Integer) item.get("qty") == 0) || ((BigDecimal) item.get("unit_fee")).compareTo(BigDecimal.ZERO) == 0)
						continue;
					ari = new FMSARIModel();
					ari.setType("Invoice");
					//ari.setLink_branch("HQ");

					ari.setCust(bill.getCust_id());
					ari.setDesc(bill.getBilling_desc());
					ari.setAttr_ext_sys("RMS");
					ari.setDiscAmt(new BigDecimal(0));
					ari.setPg_pymt_method("ANY");
					ari.setGeneratePDF(1);

					ari.setCust(bill.getCust_id());
					ari.setCust_nm(bill.getCust_nm());
					ari.setEnt_nm(bill.getEnt_nm());
					ari.setEnt_no(bill.getEnt_no());
					ari.setEnt_ty(bill.getEnt_ty());
					ari.setBil_child_id(((Integer) iss.get("bil_child_id")));
					ari.setPg_pymt_amt((BigDecimal) item.get("final_amt"));
					ari.setQty((Integer) item.get("qty"));
					ari.setItem_desc((String) item.get("mft_desc_en"));
					ari.setUnit_fee((BigDecimal) item.get("unit_fee"));
					ari.setGross_amt((BigDecimal) item.get("final_amt"));
					ari.setTax_amt((BigDecimal) item.get("tax_amt"));
					ari.setNet_amt((BigDecimal) item.get("final_amt"));
					ari.setFee_detail_pk(BigInteger.valueOf((Integer) item.get("mft_pk")));

					log.error("[debugARI] Inserting mft_pk:" + ((Integer)item.get("mft_pk")) + " into fmsmttb.");
					BigInteger insMttBStatus = ariSvc.sp_insfmsmttb(ari, hid, 0);
					if (insMttBStatus == null || hid.compareTo(BigInteger.ZERO) < 1) {
						log.error("Exception in " + this.getClass().toString()
								+ "confirmBill func - sp_insfmsmttb failed!");
						Integer statusCode = ariSvc.sp_deactivatefmsaribyarihid(hid.intValue());
						if (statusCode < 1)
							log.error("Exception in " + this.getClass().toString() + " (" + statusCode.toString() + ") "
									+ "confirmBill func - sp_insfmsmttb @ sp_deactivatefmsaribyarihid failed!");

						return -4;
					}
					invoices.add(ari);
				}
				Integer statusCode = ariSvc.sp_deactivatefmsaribyarihid(hid.intValue());
				if (statusCode < 1) {
					log.error("Exception in " + this.getClass().toString() + " (" + statusCode.toString() + ") "
							+ "confirmBill func - sp_deactivatefmsaribyarihid failed!");
					return -5;
				}
			}
		//}
		return 1;
	}
	
	public Integer debugARIResponse(String billingNo) {
			Map<String, Object> request = new HashMap<String, Object>();
			request.put("billing_no", billingNo);
			request.put("page", 1);
			request.put("size", 1);
			request.put("bil_item_status", "A");
			request.put("more_info", true);

			Billing bill = sp_getbill(request);
			if (bill == null) {
				log.error("Exception in " + this.getClass().toString()
						+ "confirmBill func - sp_getbill failed!");
				return -1;
			}
			StringBuilder bilChildNoList = new StringBuilder();
			Map<String, Object> iss = null; // = bRepo.sp_getbilchild(billingNo);
			for (Map<String, Object> child : bill.getBilling_list()) {
				if (child.get("bil_no").equals(billingNo)) {
					iss = child;
					bilChildNoList.append(child.get("bil_no"));
					break;
				}
				bilChildNoList.append(child.get("bil_no") + ",");
			}
			if (iss == null || iss.get("bil_child_date") == null) {
				iss = bRepo.sp_getbilchild(billingNo);

				if (iss == null) {
					log.error("Exception in " + this.getClass().toString()
							+ "confirmBill func - bil_child object is null in bill! List of children: "
							+ bilChildNoList.toString());
					return -2;
				} else if (iss.get("bil_child_date") == null) {
					log.error("Exception in " + this.getClass().toString()
							+ "confirmBill func - bil_child key 'bil_child_date' item is null! List of children: "
							+ bilChildNoList.toString());
					return -2;
				}
			}
			BigInteger hid = BigInteger.valueOf(ariSvc.sp_getfmsarihidbyaribodybilchildid((Integer) iss.get("bil_child_id")));
			ariSvc.generateStringBody(ariSvc.sp_getfmsaribybilchildid((Integer) iss.get("bil_child_id")));
			String apiStatusCode = ariSvc.sp_getfmsarirespcodebyarihid(hid.intValue());

			// AR Invoice failed!
			if (apiStatusCode == null || !apiStatusCode.equals("200")) {
				log.error("Exception in " + this.getClass().toString() + " AR Invoice Response Code (" + apiStatusCode + ") "
				 		+ "confirmBill func - generateStringBody - fms_api_ari - Bad Response!");
				return -6;
			}
			return 1;
	}

	public Integer confirmBill(String billingNo, String username, boolean isOnce) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("billing_no", billingNo);
		request.put("page", 1);
		request.put("size", Integer.MAX_VALUE);
		request.put("bil_item_status", "A");
		request.put("more_info", true);

		Billing bill = sp_getbill(request);
		if (bill == null) {
			log.error("Exception in " + this.getClass().toString()
					+ "confirmBill func - sp_getbill failed!");
			return -1;
		}
		StringBuilder bilChildNoList = new StringBuilder();
		Map<String, Object> iss = null; // = bRepo.sp_getbilchild(billingNo);
		for (Map<String, Object> child : bill.getBilling_list()) {
			if (child.get("bil_no").equals(billingNo)) {
				iss = child;
				bilChildNoList.append(child.get("bil_no"));
				break;
			}
			bilChildNoList.append(child.get("bil_no") + ",");
		}
		if (iss == null || iss.get("bil_child_date") == null) {
			// bilChildNoList.setLength(bilChildNoList.length() - 1);
			/*
			 * log.error("Exception in " + this.getClass().toString()
			 * +
			 * "confirmBill func - can't find/malformed bil_child in bill! List of children: "
			 * + bilChildNoList.toString());
			 */

			// Try again:
			iss = bRepo.sp_getbilchild(billingNo);

			if (iss == null) {
				log.error("Exception in " + this.getClass().toString()
						+ "confirmBill func - bil_child object is null in bill! List of children: "
						+ bilChildNoList.toString());
				return -2;
			} else if (iss.get("bil_child_date") == null) {
				log.error("Exception in " + this.getClass().toString()
						+ "confirmBill func - bil_child key 'bil_child_date' item is null! List of children: "
						+ bilChildNoList.toString());
				return -2;
			}
		}

		BigDecimal ttl = new BigDecimal(0);
		for (Map<String, Object> item : bill.getBilling_items())
			ttl = ttl.add((BigDecimal) item.get("final_amt"));

		// String apiStatusCode =
		// ariSvc.sp_getfmsarirespcodebybilchildid((Integer)iss.get("bil_child_id"));
		// if(apiStatusCode == null || !apiStatusCode.equals("200")) {
		if (((Integer) iss.get("billing_img")) == 0) {
			List<FMSARIModel> invoices = ariSvc.sp_getfmsaribybilchildid((Integer) iss.get("bil_child_id"));
			BigInteger hid = BigInteger
					.valueOf(ariSvc.sp_getfmsarihidbyaribodybilchildid((Integer) iss.get("bil_child_id")));

			if (invoices.size() < 1) {
				FMSARIModel ari = new FMSARIModel();
				ari.setType("Invoice");
				//ari.setLink_branch("00000");
				ari.setCust(bill.getCust_id());
				ari.setDesc(bill.getBilling_desc());
				ari.setAttr_ext_sys("RMS");
				ari.setGeneratePDF(1);
				ari.setCreated_by(username);
				ari.setAmt(ttl);

				if (hid == null || hid.compareTo(BigInteger.ZERO) < 1)
					hid = ariSvc.sp_insfmsari_h(ari);

				if (hid == null || hid.compareTo(BigInteger.ZERO) < 1) {
					log.error("Exception in " + this.getClass().toString()
							+ "confirmBill func - sp_insfmsmtth failed!");
					return -3;
				}

				invoices = new ArrayList<FMSARIModel>();
				for (Map<String, Object> item : bill.getBilling_items()) {
					if(((Integer) item.get("qty") == 0) || ((BigDecimal) item.get("unit_fee")).compareTo(BigDecimal.ZERO) == 0)
						continue;
					ari = new FMSARIModel();
					ari.setType("Invoice");
					//ari.setLink_branch("HQ");

					ari.setCust(bill.getCust_id());
					ari.setDesc(bill.getBilling_desc());
					ari.setAttr_ext_sys("RMS");
					ari.setDiscAmt(new BigDecimal(0));
					ari.setPg_pymt_method("ANY");
					ari.setGeneratePDF(1);

					ari.setCust(bill.getCust_id());
					ari.setCust_nm(bill.getCust_nm());
					ari.setEnt_nm(bill.getEnt_nm());
					ari.setEnt_no(bill.getEnt_no());
					ari.setEnt_ty(bill.getEnt_ty());
					ari.setBil_child_id(((Integer) iss.get("bil_child_id")));
					ari.setPg_pymt_amt((BigDecimal) item.get("final_amt"));
					ari.setQty((Integer) item.get("qty"));
					ari.setItem_desc((String) item.get("mft_desc_en"));
					ari.setUnit_fee((BigDecimal) item.get("unit_fee"));
					ari.setGross_amt((BigDecimal) item.get("final_amt"));
					ari.setTax_amt((BigDecimal) item.get("tax_amt"));
					ari.setNet_amt((BigDecimal) item.get("final_amt"));
					ari.setFee_detail_pk(BigInteger.valueOf((Integer) item.get("mft_pk")));

					BigInteger insMttBStatus = ariSvc.sp_insfmsmttb(ari, hid, 0);
					if (insMttBStatus == null || hid.compareTo(BigInteger.ZERO) < 1) {
						log.error("Exception in " + this.getClass().toString()
								+ "confirmBill func - sp_insfmsmttb failed!");
						Integer statusCode = ariSvc.sp_deactivatefmsaribyarihid(hid.intValue());
						if (statusCode < 1)
							log.error("Exception in " + this.getClass().toString() + " (" + statusCode.toString() + ") "
									+ "confirmBill func - sp_insfmsmttb @ sp_deactivatefmsaribyarihid failed!");

						return -4;
					}
					invoices.add(ari);
				}
				Integer statusCode = ariSvc.sp_deactivatefmsaribyarihid(hid.intValue());
				if (statusCode < 1) {
					log.error("Exception in " + this.getClass().toString() + " (" + statusCode.toString() + ") "
							+ "confirmBill func - sp_deactivatefmsaribyarihid failed!");
					return -5;
				}
			}
			ariSvc.generateStringBody(ariSvc.sp_getfmsaribybilchildid((Integer) iss.get("bil_child_id")));
			String apiStatusCode = ariSvc.sp_getfmsarirespcodebyarihid(hid.intValue());

			// AR Invoice failed!
			if (apiStatusCode == null || !apiStatusCode.equals("200")) {
				// log.error("Exception in " + this.getClass().toString() + " AR Invoice Response Code (" + apiStatusCode
				// 		+ ") "
				// 		+ "confirmBill func - generateStringBody - fms_api_ari - Bad Response!");
				return -6;
			}
		}

		iss = bRepo.sp_getbilchild(billingNo);
		if (((Integer) iss.get("billing_img")) == 0) {
			log.error("Exception in " + this.getClass().toString()
					+ "confirmBill func - detected no billing image for " + billingNo + "!");
			return -7;
		}

		// If AR Invoice API Success:
		Integer statusCode = bRepo.sp_confirmnewbill(billingNo, username);
		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString() + " (" + statusCode.toString() + ") "
					+ "confirmBill func - sp_confirmnewbill failed!");
			return -8;
		}
		String period = "";
		String redirect = onlinePortalUrl + "/payment-page?pr=" + billingNo;

		if (!isOnce) {
			LocalDateTime issuanceDate = (LocalDateTime) iss.get("bil_child_date");
			String month = (issuanceDate.getMonth().toString().toLowerCase());
			period = month.substring(0, 1).toUpperCase() + month.substring(1)
					+ " " + Integer.toString(issuanceDate.getYear());
		}

		OnlinePayment mtt = mttSvc.getMttFromOrderNo(billingNo).orElse(null);
		if (mtt == null) {
			log.error("Exception in " + this.getClass().toString()
					+ "confirmBill func - getMttFromOrderNo failed!");
			return -9;
		}

		StringBuilder body = new StringBuilder("Entity Name: " + bill.getEnt_nm()
				+ "<br>Billing No.: " + billingNo);
		if (!isOnce)
			body.append("<br>Billing Period: " + period);
		body.append("<br>Billing Amount: RM" + mtt.getTotal_amt().toString()
				+ "<br>Billing Description: " + bill.getBilling_desc()
				+ "<br><br>Dear Sir/Madam,<br>"
				+ "Tuan/Puan,<br><br>"
				+ "We wish to inform you that a billing payment is pending in our system. Please review and complete the transactions using the payment link provided below within 30 days from this e-mail issuance."
				+ "<br>Kami ingin memaklumkan bahawa terdapat pembayaran bil yang masih tertunggak dalam sistem kami. Kami memohon Tuan/Puan untuk melakukan semakan dan menyelesaikan transaksi menggunakan pautan pembayaran yang disediakan di bawah dalam tempoh 30 hari daripada tarikh e-mel dikeluarkan."
				+ "<br><br><a href='" + redirect + "'>CLICK HERE</a> to proceed with the payment.<br>"
				+ "<a href='" + redirect + "'>KLIK DI SINI</a> untuk tujuan pemprosesan pembayaran.<br><br>"
				+ "For further information or to access other services, you may also visit our RMS Public Portal: <a href='"
				+ onlinePortalUrl + "'>RMS Public Portal link.</a>"
				+ "<br>Untuk maklumat lanjut atau untuk mengakses perkhidmatan lain, anda juga boleh melayari Portal Awam RMS kami: <a href='"
				+ onlinePortalUrl + "'>Pautan Portal Awam RMS.</a>"
				+ "<br>**PLEASE IGNORE THIS EMAIL IF YOUR PAYMENT HAS ALREADY BEEN PROCESSED***"
				+ "<br>***MOHON ABAIKAN EMEIL INI SEKIRANYA PEMBAYARAN TELAH DILAKUKAN***"
				+ "<br>Thank you for using our services"
				+ "<br>Terima kasih kerana menggunakan perkhidmatan kami."
				+ "<br><br><br><br>"
				+ "[THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL]<br>");

		try {
			byte[] fileBytes = bRepo.sp_getbilchildimgblob((Integer) iss.get("bil_child_id"));
			File img = new File(System.getProperty("java.io.tmpdir"), billingNo + "_img.pdf");

			try (FileOutputStream fileOuputStream = new FileOutputStream(img)) {
				fileOuputStream.write(fileBytes);
				fileOuputStream.flush();
				fileOuputStream.close();
				emailSvc.saveEmailDets(
						new Email("Notification", mtt.getCust_email(), "", "", "PENDING PAYMENT", body.toString(),
								billingNo, img));
			} catch (Exception e) {
				log.error("Exception in " + this.getClass().toString()
						+ "byte[] to File func failed! Switching to normal email...");
				emailSvc.saveEmailDets(
						new Email("Notification", mtt.getCust_email(), "", "", "PENDING PAYMENT", body.toString(),
								billingNo, null));
			}
		} catch (Exception e) {
			log.error("Exception in " + this.getClass().toString()
					+ "sp_getbilchildimgblob / createTempFile failed! Switching to normal email...", e);
			emailSvc.saveEmailDets(
					new Email("Notification", mtt.getCust_email(), "", "", "PENDING PAYMENT", body.toString(),
							billingNo, null));
		}

		return 1;
	}

	public Map<String, Object> sp_getbilllistingcanadj(Map<String, Object> request) {
		return bRepo.sp_getbilllistingcanadj(request);
	}

	public Map<String, Object> sp_getcancelbillist(Map<String, Object> request) {
		return bRepo.sp_getcancelbillist(request);
	}

	public Billing sp_getbill(Map<String, Object> request) {
		return bRepo.sp_getbill(request);
	}

	public Integer sp_checkbilstatusvalid(String billingNo) {
		return bRepo.sp_checkbilstatusvalid(billingNo);
	}

	public Integer bilCanReq(String billingNo, String username, String remark) throws JsonProcessingException {
		Integer statusCode = bRepo.sp_cancelbilreq(billingNo, username, remark);
		return statusCode == 1 ? sp_approvebilcan(billingNo, username, remark) : statusCode;
	}

	public Integer updQryBilCan(String billingNo, String username, String remark) {
		return bRepo.sp_querybilcan(billingNo, username, remark);
	}

	public Integer rejBilCanReq(String billingNo, String username, String remark) {
		return bRepo.sp_rejectbilcan(billingNo, username, remark);
	}

	public Integer sp_approvebilcan(String billingNo, String username, String remark) throws JsonProcessingException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("billingNo", billingNo);
		data.put("amt", new BigDecimal(0));
		data.put("adj", false);
		data.put("adjData", null);
		Integer statusCode = creditBill(data);

		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString() + "sp_approvebilcan func - "
					+ "status code: " + statusCode.toString() + "!");
			Integer statusCode2 = bRepo.sp_insbilhistbybillno(billingNo, "system", "Failed to generate credit memo!",
					"R");
			if (statusCode2 < 1)
				log.error("Exception in " + this.getClass().toString()
						+ "sp_insbilhistbybillno @ sp_approvebilcan func - "
						+ "status code: " + statusCode2.toString() + "!");

		}
		
		return statusCode > 0 ? bRepo.sp_approvebilcan(billingNo, username, remark) : statusCode;
	}

	public Integer adjBilReq(List<Map<String, Object>> adjData, Map<String, Object> param)
			throws JsonProcessingException {
		String billingNo = (String) param.get("billing_no");
		String username = (String) param.get("username");
		String remarks = (String) param.get("remarks");
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("billing_no", billingNo);
		request.put("page", 1);
		request.put("size", 1);
		request.put("bil_item_status", "A");
		request.put("more_info", false);

		Billing bill = sp_getbill(request);
		Integer bilWfId = bill.getBil_wf_id();
		Integer bilId = bill.getBil_id();

		Integer statusCode = 0;

		for (Map<String, Object> bItems : adjData) {
			bItems.put("status", "WF-AN");
			try {
				statusCode = bRepo.sp_insnewbilregitem(bilWfId, bilId, bItems, username);
			} catch (EmptyResultDataAccessException e) {
				log.error("Exception in " + this.getClass().toString() + " adjBilReq func - "
						+ "sp_insnewbilregitem - item: " + (String) bItems.get("desc") + " - "
						+ "status code: " + statusCode.toString() + "!");
				bRepo.sp_rollbackadjbilreq(bilWfId, bilId);
				return statusCode;
			}
			if (statusCode < 1) {
				log.error("Exception in " + this.getClass().toString() + " adjBilReq func - "
						+ "sp_insnewbilregitem - item: " + (String) bItems.get("desc") + " - "
						+ "status code: " + statusCode.toString() + "!");
				bRepo.sp_rollbackadjbilreq(bilWfId, bilId);
				return statusCode;
			}
		}
		statusCode = bRepo.sp_adjbilreq(billingNo, username, remarks);
		return statusCode == 1 ? apprBilAdjReq(billingNo, username, remarks) : statusCode;
	}

	public Integer updQryBilAdj(String billingNo, String username, String remark) {
		return bRepo.sp_querybiladj(billingNo, username, remark);
	}

	public Integer rejBilAdjReq(String billingNo, String username, String remark) {
		return bRepo.sp_rejectbiladj(billingNo, username, remark);
	}

	public Integer apprBilAdjReq(String billingNo, String username, String remark) throws JsonProcessingException {
		BigDecimal totalAmt = new BigDecimal(0);
		List<Map<String, Object>> newData = bRepo.sp_latestDifferenceAftAdj(billingNo);
		for (Map<String, Object> item : newData)
			totalAmt = totalAmt.add((BigDecimal) item.get("final_amount"));

		Map<String, Object> iss = bRepo.sp_getbilchild(billingNo);
		
		Integer statusCode = 0;
		
		if(((String)iss.get("bil_status")).equals("U")) { //When payment is pending, add credit/debit
			if (totalAmt.compareTo(BigDecimal.ZERO) > 0) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("billingNo", billingNo);
				data.put("amt", totalAmt);
				data.put("adj", true);
				data.put("adjData", newData);
				data.put("username", username);
				data.put("remark", remark);
				
				statusCode = debitBill(data);
				if (statusCode < 1) {
					log.error("Exception in " + this.getClass().toString() + " apprBilAdjReq debit func - "
							+ "status code: " + statusCode.toString() + "!");
					Integer statusCode2 = bRepo.sp_insbilhistbybillno(billingNo, "system", "Failed to generate debit memo!",
							"R");
					if (statusCode2 < 1)
						log.error("Exception in " + this.getClass().toString()
								+ " sp_insbilhistbybillno @ apprBilAdjReq debit func - "
								+ "status code: " + statusCode2.toString() + "!");
				}
				return statusCode;
			} else if (totalAmt.compareTo(BigDecimal.ZERO) < 0) {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("billingNo", billingNo);
				data.put("amt", totalAmt);
				data.put("adj", true);
				data.put("adjData", newData);
				data.put("username", username);
				data.put("remark", remark);
				
				statusCode = creditBill(data);
				if (statusCode < 1) {
					log.error("Exception in " + this.getClass().toString() + "apprBilAdjReq credit func - "
							+ "status code: " + statusCode.toString() + "!");
					Integer statusCode2 = bRepo.sp_insbilhistbybillno(billingNo, "system",
							"Failed to generate credit memo!", "R");
					if (statusCode2 < 1)
						log.error("Exception in " + this.getClass().toString()
								+ "sp_insbilhistbybillno @ apprBilAdjReq credit func - "
								+ "status code: " + statusCode2.toString() + "!");
				}
				return statusCode;
			} else
				statusCode = 1;
		}
		else statusCode = 2;
		
		return statusCode > 0 ? bRepo.sp_approvebiladj(billingNo, username, remark) : statusCode;
	}

	public Integer sp_getbilmethod(String billingNo) {
		return bRepo.sp_getbilmethod(billingNo);
	}

	public Integer sp_getexistloa(String loaRef) { // LOA Ref that exists in both rms_wf_bil and rms_bil table
		return bRepo.sp_getexistloa(loaRef);
	}

	public Integer sp_getregisteredloa(String loaRef) { // Only LOA that is in rms_bil table
		return bRepo.sp_getregisteredloa(loaRef);
	}

	// billing issuance by source system start
	public List<BillingStatus> sp_getbillingstatus(BillingStatusRequest bilRequest) {
		List<BillingStatus> result = Collections.emptyList();
		List<Object[]> objects = bRepo.sp_getbillingstatus(bilRequest);
		result = convertToGetBillingStatus(objects);
		return result;
	}

	private List<BillingStatus> convertToGetBillingStatus(List<Object[]> objects) {
		List<BillingStatus> bilStatusList = new ArrayList<>();
		for (Object[] obj : objects) {
			BillingStatus bilStatus = new BillingStatus();
			bilStatus.setBilling_no((String) obj[0]);
			bilStatus.setBilling_status((String) obj[1]);
			bilStatusList.add(bilStatus);
		}
		return bilStatusList;
	}

	// billing issuance by source system end

	public Integer creditBill(Map<String, Object> data) throws JsonProcessingException {
		// FMS API (Credit API for full amount if got unpaid child) goes here
		// return 0; // if AR Invoice API fail
		String billingNo = (String) data.get("billingNo");
		BigDecimal amt = (BigDecimal) data.get("amt");
		Boolean adj = data.get("adj") == null ? false : (Boolean) data.get("adj");
		List<Map<String, Object>> adjData = (List<Map<String, Object>>) data.get("adjData");
		String username = (String) data.get("username");
		String remark = (String) data.get("remark");
		
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("billing_no", billingNo);
		request.put("page", 1);
		request.put("size", Integer.MAX_VALUE);
		request.put("bil_item_status", adj ? "WF-AN" : "A");
		request.put("more_info", true);

		Billing bill = sp_getbill(request);
		if (bill == null) {
			log.error("Exception in " + this.getClass().toString()
					+ "creditBill func - sp_getbill failed!");
			return -1;
		}

		if (!adj)
			for (Map<String, Object> items : bill.getBilling_items())
				amt = amt.add((BigDecimal) items.get("final_amt"));		

		Integer statusCode = 0;
		List<BigInteger> suc = new ArrayList<BigInteger>();
		for (Map<String, Object> issuance : bill.getBilling_list()) {
			if (!issuance.get("bil_status").equals("U"))
				continue;
			List<FMSARIModel> ariList = ariSvc.sp_getfmsaribybilchildid((Integer) issuance.get("bil_child_id"));
			if (ariList.size() < 1) {
				log.error("Exception in " + this.getClass().toString()
						+ "creditBill func - sp_getfmsaribybilchildid returning empty, no ARI detected for this unpaid bill!");
				return -4;
			}
			List<FMSCRMemo> memos = new ArrayList<FMSCRMemo>();
			for (Map<String, Object> item : bill.getBilling_items()) {
				Map<String, Object> adjItem = null;
				if(adj && adjData != null)
					for(Map<String, Object> i : adjData) {
						if(((Integer)i.get("mft_pk")).equals((Integer) item.get("mft_pk"))) {
							adjItem = i;
							break;
						}
					}
				FMSCRMemo memo = new FMSCRMemo();
				memo.setCust(bill.getCust_id());
				memo.setType("Credit Memo");
				//memo.setLink_branch("HQ");
				memo.setPg_pymt_amt(amt);
				memo.setDesc("Credit Note Batch");
				memo.setAttr_ext_sys("RMS");
				memo.setFms_ref_no(ariList.get(0).getFms_ref_no());
				memo.setAcct_nm("RMS PG MID Account ID"); // Look in rms_fms_acct table
				memo.setDoc_ty("Invoice");
				memo.setGenPdf(adj ? 1 : 0);
				memo.setBil_child_id((Integer) issuance.get("bil_child_id"));

				memo.setPg_pymt_method("ANY");
				memo.setQty(adj && adjItem != null ? Math.abs(((Integer) adjItem.get("qty"))) : (Integer) item.get("qty"));
				memo.setItem_desc((String) item.get("mft_desc_en"));
				memo.setUnit_fee((BigDecimal) item.get("unit_fee"));
				memo.setEnt_nm(bill.getEnt_nm());
				memo.setEnt_no(bill.getEnt_no());
				memo.setEnt_ty(bill.getEnt_ty());
				memo.setGross_amt(amt);

				MFTRequest mftR = new MFTRequest();
				mftR.setI_fee_detail_pk((Integer) item.get("mft_pk"));
				MFT mft = mftSvc.sp_getmftByPK(mftR);
				if (mft == null) {
					log.error("Exception in " + this.getClass().toString()
							+ "creditBill func - sp_getmftByPK failed! mftp_pk = "
							+ Integer.toString((Integer) item.get("mft_pk")));
					for (BigInteger hid : suc) {
						statusCode = crSvc.sp_rollbackfmscrmemohbh(hid);
						if (statusCode < 1)
							log.error("Exception in " + this.getClass().toString()
									+ "creditBill func - sp_rollbackfmscrmemohbh @ sp_getmftByPK failed with code "
									+ Integer.toString(statusCode) + " !");
					}
					return -2;
				}
				memo.setFee_detail_id(mft.getFee_detail_id());
				memo.setTax_amt((BigDecimal) item.get("tax_amt"));
				memo.setCust_nm(bill.getCust_nm());
				//memo.setBranch("00000");
				//memo.setSub_acct("000000");

				memo.setCoa1(mft.getLedger_cd());
				memos.add(memo);
			}
			statusCode = crSvc.newCrMemo(memos);
			if (statusCode < 1) {
				log.error("Exception in " + this.getClass().toString()
						+ "creditBill func - newBillingCrmemo failed with code " + Integer.toString(statusCode) + " !");
				for (BigInteger hid : suc) {
					statusCode = crSvc.sp_rollbackfmscrmemohbh(hid);
					if (statusCode < 1)
						log.error("Exception in " + this.getClass().toString()
								+ "creditBill func - sp_rollbackfmscrmemohbh @ newBillingCrmemo failed with code "
								+ Integer.toString(statusCode) + " !");
				}
				return -3;
			}
			suc.add(BigInteger.valueOf(statusCode));
		}
		if(adj) {
			statusCode = bRepo.sp_approvebiladj(billingNo, username, remark);
			if(statusCode < 1) {
				log.error("Exception in " + this.getClass().toString()
						+ "creditBill func - sp_approvebiladj @ newBillingCrmemo failed with code "
						+ Integer.toString(statusCode) + " !");
				return statusCode;
			}
			for (BigInteger hid : suc)
				statusCode = crSvc.crMemoCallAPI(hid);
		}

		return 1;
	}

	public Integer debitBill(Map<String, Object> data) throws JsonProcessingException {
		// FMS API (Debit API for full amount if got unpaid child) goes here
		// return 0; // if AR Invoice API fail
		String billingNo = (String) data.get("billingNo");
		BigDecimal amt = (BigDecimal) data.get("amt");
		Boolean adj = data.get("adj") == null ? false : (Boolean) data.get("adj");
		List<Map<String, Object>> adjData = (List<Map<String, Object>>) data.get("adjData");
		String username = (String) data.get("username");
		String remark = (String) data.get("remark");
		
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("billing_no", billingNo);
		request.put("page", 1);
		request.put("size", Integer.MAX_VALUE);
		request.put("bil_item_status", adj ? "WF-AN" : "A");
		request.put("more_info", true);

		Billing bill = sp_getbill(request);
		if (bill == null) {
			log.error("Exception in " + this.getClass().toString()
					+ "debitBill func - sp_getbill failed!");
			return -1;
		}

		if (!adj)
			for (Map<String, Object> items : bill.getBilling_items())
				amt = amt.add((BigDecimal) items.get("final_amt"));

		Integer statusCode = 0;
		List<BigInteger> suc = new ArrayList<BigInteger>();
		for (Map<String, Object> issuance : bill.getBilling_list()) {
			if (!issuance.get("bil_status").equals("U"))
				continue;
			List<FMSARIModel> ariList = ariSvc.sp_getfmsaribybilchildid((Integer) issuance.get("bil_child_id"));
			if (ariList.size() < 1) {
				log.error("Exception in " + this.getClass().toString()
						+ "debitBill func - sp_getfmsaribybilchildid returning empty, no ARI detected for this unpaid bill!");
				return -4;
			}
			List<FMSDRMemo> memos = new ArrayList<FMSDRMemo>();
			for (Map<String, Object> item : bill.getBilling_items()) {
				Map<String, Object> adjItem = null;
				if(adj && adjData != null)
					for(Map<String, Object> i : adjData) {
						if(((Integer)i.get("mft_pk")).equals((Integer) item.get("mft_pk"))) {
							adjItem = i;
							break;
						}
						else
							System.out.println("cont. search");
					}
				FMSDRMemo memo = new FMSDRMemo();
				memo.setCust(bill.getCust_id());
				memo.setType("Debit Memo");
				//memo.setLink_branch("HQ");

				memo.setPg_pymt_amt(amt);
				memo.setDesc("Debit Note Batch");
				memo.setAttr_ext_sys("RMS");
				memo.setFms_ref_no(ariList.get(0).getFms_ref_no());
				memo.setAcct_nm("RMS PG MID Account ID"); // Look in rms_fms_acct table
				memo.setDoc_ty("Invoice");
				memo.setGenPdf(adj ? 1 : 0);
				memo.setBil_child_id((Integer) issuance.get("bil_child_id"));

				memo.setPg_pymt_method("ANY");
				memo.setQty(adj && adjItem != null ? Math.abs(((Integer) adjItem.get("qty"))) : (Integer) item.get("qty"));
				memo.setItem_desc((String) item.get("mft_desc_en"));
				memo.setUnit_fee((BigDecimal) item.get("unit_fee"));
				memo.setEnt_nm(bill.getEnt_nm());
				memo.setEnt_no(bill.getEnt_no());
				memo.setEnt_ty(bill.getEnt_ty());
				memo.setGross_amt(amt);

				MFTRequest mftR = new MFTRequest();
				mftR.setI_fee_detail_pk((Integer) item.get("mft_pk"));
				MFT mft = mftSvc.sp_getmftByPK(mftR);
				if (mft == null) {
					log.error("Exception in " + this.getClass().toString()
							+ "debitBill func - sp_getmftByPK failed! mftp_pk = "
							+ Integer.toString((Integer) item.get("mft_pk")));
					for (BigInteger hid : suc) {
						statusCode = drSvc.sp_rollbackfmsdrmemohbh(hid);
						if (statusCode < 1)
							log.error("Exception in " + this.getClass().toString()
									+ "debitBill func - sp_rollbackfmscrmemohbh @ sp_getmftByPK failed with code "
									+ Integer.toString(statusCode) + " !");
					}
					return -2;
				}
				memo.setFee_detail_id(mft.getFee_detail_id());
				memo.setTax_amt((BigDecimal) item.get("tax_amt"));
				memo.setCust_nm(bill.getCust_nm());
				//memo.setBranch("00000");
				//memo.setSub_acct("000000");

				memo.setCoa1(mft.getLedger_cd());
				memos.add(memo);
			}
			statusCode = drSvc.newDrMemo(memos);
			if (statusCode < 1) {
				log.error("Exception in " + this.getClass().toString()
						+ "debitBill func - newBillingDrmemo failed with code " + Integer.toString(statusCode) + " !");
				for (BigInteger hid : suc) {
					statusCode = drSvc.sp_rollbackfmsdrmemohbh(hid);
					if (statusCode < 1)
						log.error("Exception in " + this.getClass().toString()
								+ "debitBill func - sp_rollbackfmscrmemohbh @ newBillingDrmemo failed with code "
								+ Integer.toString(statusCode) + " !");
				}
				return -3;
			}

			suc.add(BigInteger.valueOf(statusCode));
		}
		if(adj) {
			statusCode = bRepo.sp_approvebiladj(billingNo, username, remark);
			if(statusCode < 1) {
				log.error("Exception in " + this.getClass().toString()
						+ "debitBill func - sp_approvebiladj @ newBillingDrmemo failed with code "
						+ Integer.toString(statusCode) + " !");
				return statusCode;
			}
			for (BigInteger hid : suc)
				statusCode = drSvc.drMemoCallAPI(hid);
		}

		return 1;
	}

	public Integer sp_insbilhistbybillno(String billNo, String username, String remark, String msgType) {
		return bRepo.sp_insbilhistbybillno(billNo, username, remark, msgType);
	}

	public Integer retriggerBillPayment(String billingNo, String username) {
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("billing_no", billingNo);
		request.put("page", 1);
		request.put("size", 1);
		request.put("bil_item_status", "A");
		request.put("more_info", true);

		Billing bill = sp_getbill(request);
		if (bill == null) {
			log.error("Exception in " + this.getClass().toString()
					+ "retriggerBillPayment func - sp_getbill failed!");
			return -1;
		}
		StringBuilder bilChildNoList = new StringBuilder();
		Map<String, Object> iss = null; // = bRepo.sp_getbilchild(billingNo);
		for (Map<String, Object> child : bill.getBilling_list()) {
			if (child.get("bil_no").equals(billingNo)) {
				iss = child;
				break;
			}
			bilChildNoList.append(child.get("bil_no") + ",");
		}
		if (iss == null || iss.get("bil_child_date") == null) {
			bilChildNoList.setLength(bilChildNoList.length() - 1);
			log.error("Exception in " + this.getClass().toString()
					+ "retriggerBillPayment func - can't find/malformed bil_child in bill! List of children: "
					+ bilChildNoList.toString());
			return -2;
		}
		BigDecimal totalAmt = new BigDecimal(0);
		for (Map<String, Object> item : bill.getBilling_items())
			totalAmt = totalAmt.add((BigDecimal) item.get("final_amt"));

		String period = "";
		String redirect = onlinePortalUrl + "/payment-page?pr=" + billingNo;
		Boolean isOnce = bill.getBilling_mthd().equals("O");

		if (!isOnce) {
			LocalDateTime issuanceDate = (LocalDateTime) iss.get("bil_child_date");
			String month = (issuanceDate.getMonth().toString().toLowerCase());
			period = month.substring(0, 1).toUpperCase() + month.substring(1)
					+ " " + Integer.toString(issuanceDate.getYear());
		}

		Integer statusCode = mttSvc.sp_refreshMTTEmailExpDt(billingNo, username);
		if (statusCode == null || statusCode < 1) {
			log.error("Exception in " + this.getClass().toString()
					+ "retriggerBillPayment func - sp_refreshMTTEmailExpDt failed!");
			return -3;
		}

		StringBuilder body = new StringBuilder("Entity Name: " + bill.getEnt_nm()
				+ "<br>Billing No.: " + billingNo);
		if (!isOnce)
			body.append("<br>Billing Period: " + period);
		body.append("<br>Billing Amount: RM" + totalAmt.toString()
				+ "<br>Billing Description: " + bill.getBilling_desc()
				+ "<br><br>Dear Sir/Madam,<br>"
				+ "Tuan/Puan,<br><br>"
				+ "We wish to inform you that a billing payment is pending in our system. Please review and complete the transactions using the payment link provided below within 30 days from this e-mail issuance."
				+ "<br>Kami ingin memaklumkan bahawa terdapat pembayaran bil yang masih tertunggak dalam sistem kami. Kami memohon Tuan/Puan untuk melakukan semakan dan menyelesaikan transaksi menggunakan pautan pembayaran yang disediakan di bawah dalam tempoh 30 hari daripada tarikh e-mel dikeluarkan."
				+ "<br><br><a href='" + redirect + "'>CLICK HERE</a> to proceed with the payment.<br>"
				+ "<a href='" + redirect + "'>KLIK DI SINI</a> untuk tujuan pemprosesan pembayaran.<br><br>"
				+ "For further information or to access other services, you may also visit our RMS Public Portal: <a href='"
				+ onlinePortalUrl + "'>RMS Public Portal link.</a>"
				+ "<br>Untuk maklumat lanjut atau untuk mengakses perkhidmatan lain, anda juga boleh melayari Portal Awam RMS kami: <a href='"
				+ onlinePortalUrl + "'>Pautan Portal Awam RMS.</a>"
				+ "<br>**PLEASE IGNORE THIS EMAIL IF YOUR PAYMENT HAS ALREADY BEEN PROCESSED***"
				+ "<br>***MOHON ABAIKAN EMEIL INI SEKIRANYA PEMBAYARAN TELAH DILAKUKAN***"
				+ "<br>Thank you for using our services"
				+ "<br>Terima kasih kerana menggunakan perkhidmatan kami."
				+ "<br><br><br><br>"
				+ "[THIS IS AN AUTOMATED MESSAGE - PLEASE DO NOT REPLY DIRECTLY TO THIS EMAIL]<br>");

		try {
			byte[] fileBytes = bRepo.sp_getbilchildimgblob((Integer) iss.get("bil_child_id"));
			File img = new File(System.getProperty("java.io.tmpdir"), billingNo + "_img.pdf");

			try (FileOutputStream fileOuputStream = new FileOutputStream(img)) {
				fileOuputStream.write(fileBytes);
				fileOuputStream.flush();
				fileOuputStream.close();
				emailSvc.saveEmailDets(
						new Email("Notification", bill.getCust_email(), "", "", "PENDING PAYMENT", body.toString(),
								billingNo, img));
			} catch (Exception e) {
				log.error("Exception in " + this.getClass().toString()
						+ "byte[] to File func failed! Switching to normal email...");
				emailSvc.saveEmailDets(
						new Email("Notification", bill.getCust_email(), "", "", "PENDING PAYMENT", body.toString(),
								billingNo, null));
			}

		} catch (Exception e) {
			log.error("Exception in " + this.getClass().toString()
					+ "sp_getbilchildimgblob / createTempFile failed! Switching to normal email...", e);
			emailSvc.saveEmailDets(
					new Email("Notification", bill.getCust_email(), "", "", "PENDING PAYMENT", body.toString(),
							billingNo, null));
		}

		statusCode = bRepo.sp_insbilhistbybillno(billingNo, username,
				"Bill " + billingNo + " payment request retriggered!", "R");
		if (statusCode < 1) {
			log.error("Exception in " + this.getClass().toString()
					+ "sp_insbilhistbybillno @ retriggerBillPayment func - "
					+ "status code: " + (statusCode != null ? statusCode.toString() : "null") + "!");
			return -4;
		}

		return 1;
	}
}
