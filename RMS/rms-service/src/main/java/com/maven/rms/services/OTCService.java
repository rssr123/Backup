package com.maven.rms.services;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maven.rms.models.MFT;
import com.maven.rms.models.MFTRequest;
import com.maven.rms.models.OTCCheckInRequest;
import com.maven.rms.models.OnlinePayment;
import com.maven.rms.models.OnlinePaymentItem;
import com.maven.rms.models.Param;
import com.maven.rms.models.payload.requests.OTCPymtItemDet;
import com.maven.rms.models.payload.requests.OtcPaymentRequest;
import com.maven.rms.repositories.OTCRepository;
import com.maven.rms.utils.RMSLogger;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class OTCService {
	private final OTCRepository otcRepo;
	
	@Autowired
    private MTTService mttSvc;
	@Autowired
    private OnlinePaymentService oPSvc;
	@Autowired
	private MFTService mftSvc;
	@Autowired
	private CommonService cSvc;
	
	public OTCService(OTCRepository otcRepo) {
		this.otcRepo = otcRepo;
	}
	
	public int sourceSystemSubmittedOrder(OtcPaymentRequest payload, String ipAdd, String username) {
		List<Param> states = cSvc.sp_getparam(1, Integer.MAX_VALUE, "", "State");
		boolean stateExists = false;
		for(Param state : states) {
			//if(state.getNm_en().equals(payload.getCust_state().toUpperCase())) {
			if(state.getParam_cd().equals(payload.getCust_state().toUpperCase())) {
				stateExists = true;
				break;
			}
		}
		if(!stateExists)
			return -403;
		
		OnlinePayment order = mttSvc.getMttFromOrderNo(payload.getOrn_no()).orElse(null);
		if(order != null) {
			if(!order.getRms_type().toLowerCase().equals(payload.getPymt_method().toLowerCase())) {
				log.error("Bad order status detected for ORN:" + order.getOrnNo() + " - " + order.getOrder_status());
				return -5;
			}
			if(order.getOrder_status().equals("P") || order.getOrder_status().equals("OE") || order.getOrder_status().equals("V"))
				return -2;
			
			//Scenario: update data
			order.setSs_cd(payload.getSs_cd());
			order.setRms_type(payload.getPymt_method());
			order.setColl_slip_no(payload.getCollection_slip());
			order.setOrnDt(LocalDateTime.parse(payload.getOrn_dt(), DateTimeFormatter.ISO_DATE_TIME));
			order.setCust_nm(payload.getCust_nm());
			order.setCust_addr_1(payload.getCust_addr_1());
			order.setCust_addr_2(payload.getCust_addr_2());
			order.setCust_addr_3(payload.getCust_addr_3());
			order.setCust_postcode(payload.getCust_postcode());
			order.setCust_city(payload.getCust_city());
			order.setCust_state(payload.getCust_state());
			order.setCust_email(payload.getCust_email());
			order.setCust_phone(payload.getCust_phone());
			order.setTotal_amt(payload.getTotal_amt());
			order.setSs_return_url(payload.getSs_return_url());
			order.setCust_ip(ipAdd);
			order.setModified_by(username);
			order.setDt_modified(LocalDateTime.now());
		
			List<OnlinePaymentItem> itemList = mttSvc.getListOfItems(order.getMttId());
			for(OTCPymtItemDet item : payload.getPayment_item_details()) {
				boolean mttItemUpdated = false;
				for(OnlinePaymentItem mttItem : itemList) {
					if(mttItem.getFee_detail_id().equals(item.getFee_detail_id()) 
							&& mttItem.getItem_ref_no().equals(item.getItem_ref_no())) {
						mttItem.setItem_desc(item.getItem_desc());
						mttItem.setLine_no(item.getLine_no());
						mttItem.setQty(item.getQty());
						mttItem.setUnit_fee(item.getUnit_fee());
						mttItem.setGross_amt(item.getGross_amt());
						mttItem.setTax_pct(item.getTax_pct());
						mttItem.setTax_amt(item.getTax_amt());
						mttItem.setNet_amt(item.getNet_amt());
						mttItem.setEntity_type(item.getEntity_type());
						mttItem.setEntity_no(item.getEntity_no());
						mttItem.setEntity_nm(item.getEntity_nm());
						//mttItem.setEmail_flag(item.getEmail_flag());
						mttItem.setGrant_cd(item.getGrant_cd());
						mttItem.setDisc_amt(item.getDisc_amt());
						mttItem.setDps_id(item.getDps_id());
						mttItem.setDps_task(item.getDps_task());
						mttItem.setPymt_case(item.getPymt_case());
						mttItem.setLocation(item.getLocation());
						mttItem.setLit_item_ref(item.getLit_item_ref());
						mttItem.setTxn_type(item.getTxn_type());
						mttItem.setCalendar_yr(item.getCalendar_yr());
						mttItem.setModified_by(username);
						mttItem.setDt_modified(LocalDateTime.now());
						mttItemUpdated = true;
						break;
					}
				}
				if(!mttItemUpdated) {
					MFTRequest rq = new MFTRequest();
					rq.setI_fee_detail_id(item.getFee_detail_id());
					MFT feeInfo = mftSvc.sp_getmftByID(rq);
					if(feeInfo == null) {
						log.error("Cannot find MFT record with fee detail id: " + item.getFee_detail_id() + "! Cancelling transaction...");
						return -3;
					}
					OnlinePaymentItem mttItem = new OnlinePaymentItem();
					mttItem.setMtt_id(order.getMttId());
					mttItem.setFee_detail_pk(feeInfo.getFee_detail_pk());
					mttItem.setFee_detail_id(item.getFee_detail_id());
					mttItem.setItem_ref_no(item.getItem_ref_no());
					mttItem.setItem_desc(item.getItem_desc());
					mttItem.setLine_no(item.getLine_no());
					mttItem.setQty(item.getQty());
					mttItem.setUnit_fee(item.getUnit_fee());
					mttItem.setGross_amt(item.getGross_amt());
					mttItem.setTax_pct(item.getTax_pct());
					mttItem.setTax_amt(item.getTax_amt());
					mttItem.setNet_amt(item.getNet_amt());
					mttItem.setEntity_type(item.getEntity_type());
					mttItem.setEntity_no(item.getEntity_no());
					mttItem.setEntity_nm(item.getEntity_nm());
					//mttItem.setEmail_flag(item.getEmail_flag());
					mttItem.setGrant_cd(item.getGrant_cd());
					mttItem.setDisc_amt(item.getDisc_amt());
					mttItem.setDps_id(item.getDps_id());
					mttItem.setDps_task(item.getDps_task());
					mttItem.setPymt_case(item.getPymt_case());
					mttItem.setLocation(item.getLocation());
					mttItem.setLit_item_ref(item.getLit_item_ref());
					mttItem.setTxn_type(item.getTxn_type());
					mttItem.setCalendar_yr(item.getCalendar_yr());
					mttItem.setModified_by(username);
					mttItem.setDt_modified(LocalDateTime.now());
					mttItem.setCreated_by(username);
					mttItem.setDt_created(LocalDateTime.now());
					mttItem.setStatus("A");
					itemList.add(mttItem);
				}
			}
			List<OnlinePaymentItem> successfulMttItems = new ArrayList<OnlinePaymentItem>();
			for (OnlinePaymentItem mttItem : itemList) {
				OnlinePaymentItem tmp = mttSvc.saveMTTItem(mttItem);
				if(tmp == null) {
					log.error("Failed to save mttItemId: " + Integer.toString(mttItem.getMtt_item_id()) 
							+ " / Entity No: " + mttItem.getEntity_no() + " / Entity Nm" + mttItem.getEntity_nm());
					for (OnlinePaymentItem toDelete : successfulMttItems) {
						Integer deleteStatus = mttSvc.deleteMTTItem(toDelete);
						if(deleteStatus <= 0)
							log.error("Failed to delete mttitem! mttItemId: " + Integer.toString(toDelete.getMtt_item_id()) 
							+ " / Entity No: " + toDelete.getEntity_no() + " / Entity Nm" + toDelete.getEntity_nm());
					}
					return -4;
				}
				successfulMttItems.add(tmp);
			}
			
			order = oPSvc.saveMTT(order);
			
			//If mtt or any mttItem has problem inserting, it will return -1
			return order == null ? -1 : 2;
		}
		//Scenario: create new mtt and mttitems here and set status (cannot find order_no in table)
		order = new OnlinePayment();
		order.setOrnNo(payload.getOrn_no());
		order.setSs_cd(payload.getSs_cd());
		order.setRms_type(payload.getPymt_method());
		order.setColl_slip_no(payload.getCollection_slip());
		order.setOrnDt(LocalDateTime.parse(payload.getOrn_dt(), DateTimeFormatter.ISO_DATE_TIME));
		order.setCust_nm(payload.getCust_nm());
		order.setCust_addr_1(payload.getCust_addr_1());
		order.setCust_addr_2(payload.getCust_addr_2());
		order.setCust_addr_3(payload.getCust_addr_3());
		order.setCust_postcode(payload.getCust_postcode());
		order.setCust_city(payload.getCust_city());
		order.setCust_state(payload.getCust_state());
		order.setCust_email(payload.getCust_email());
		order.setCust_phone(payload.getCust_phone());
		order.setTotal_amt(payload.getTotal_amt());
		order.setSs_return_url(payload.getSs_return_url());
		order.setCust_ip(ipAdd);
		order.setModified_by(username);
		order.setCreated_by(username);
		order.setDt_created(LocalDateTime.now());
		order.setDt_modified(LocalDateTime.now());
		order.setOrder_status(payload.getPymt_method().toUpperCase().equals("OTC") ? "PO" : "PIP");
		order.setDt_otc_expiry(payload.getPymt_method().toUpperCase().equals("OTC") ? LocalDateTime.now().plusDays(14) : null);
		
		List<OnlinePaymentItem> itemList = new ArrayList<OnlinePaymentItem>();
		for(OTCPymtItemDet item : payload.getPayment_item_details()) {
			MFTRequest rq = new MFTRequest();
			rq.setI_fee_detail_id(item.getFee_detail_id());
			MFT feeInfo = mftSvc.sp_getmftByID(rq);
			if(feeInfo == null) {
				log.error("Cannot find MFT record with fee detail id: " + item.getFee_detail_id() + "! Cancelling transaction...");
				return -3;
			}
			OnlinePaymentItem mttItem = new OnlinePaymentItem();
			mttItem.setFee_detail_pk(feeInfo.getFee_detail_pk());
			mttItem.setFee_detail_id(item.getFee_detail_id());
			mttItem.setItem_ref_no(item.getItem_ref_no());
			mttItem.setItem_desc(item.getItem_desc());
			mttItem.setLine_no(item.getLine_no());
			mttItem.setQty(item.getQty());
			mttItem.setUnit_fee(item.getUnit_fee());
			mttItem.setGross_amt(item.getGross_amt());
			mttItem.setTax_pct(item.getTax_pct());
			mttItem.setTax_amt(item.getTax_amt());
			mttItem.setNet_amt(item.getNet_amt());
			mttItem.setEntity_type(item.getEntity_type());
			mttItem.setEntity_no(item.getEntity_no());
			mttItem.setEntity_nm(item.getEntity_nm());
			//mttItem.setEmail_flag(item.getEmail_flag());
			mttItem.setGrant_cd(item.getGrant_cd());
			mttItem.setDisc_amt(item.getDisc_amt());
			mttItem.setDps_id(item.getDps_id());
			mttItem.setDps_task(item.getDps_task());
			mttItem.setPymt_case(item.getPymt_case());
			mttItem.setLocation(item.getLocation());
			mttItem.setLit_item_ref(item.getLit_item_ref());
			mttItem.setTxn_type(item.getTxn_type());
			mttItem.setCalendar_yr(item.getCalendar_yr());
			mttItem.setModified_by(username);
			mttItem.setDt_modified(LocalDateTime.now());
			mttItem.setCreated_by(username);
			mttItem.setDt_created(LocalDateTime.now());
			mttItem.setStatus("A");
			itemList.add(mttItem);
		}
		
		order = oPSvc.saveMTT(order);
		
		if(order == null)
			return -1;
		
		List<OnlinePaymentItem> successfulMttItems = new ArrayList<OnlinePaymentItem>();
		for (OnlinePaymentItem mttItem : itemList) {
			mttItem.setMtt_id(order.getMttId());
			OnlinePaymentItem tmp = mttSvc.saveMTTItem(mttItem);
			if(tmp == null) {
				log.error("Failed to save mttItemId: " + Integer.toString(mttItem.getMtt_item_id()) 
						+ " / Entity No: " + mttItem.getEntity_no() + " / Entity Nm" + mttItem.getEntity_nm());
				for (OnlinePaymentItem toDelete : successfulMttItems) {
					Integer deleteStatus = mttSvc.deleteMTTItem(toDelete);
					if(deleteStatus <= 0)
						log.error("Failed to delete mttitem! mttItemId: " + Integer.toString(toDelete.getMtt_item_id()) 
						+ " / Entity No: " + toDelete.getEntity_no() + " / Entity Nm" + toDelete.getEntity_nm());
				}
				return -4;
			}
			successfulMttItems.add(tmp);
		}
		return	1;
	}

	public int otcCheckIn(OTCCheckInRequest req) {
		return otcRepo.sp_insotccheckin(req);
	}
	
	public int otcCheckOut(String counter_id, String user_id , String session_id) {
		return otcRepo.sp_insotccheckout(counter_id, user_id, session_id);
	}
	
	public Map<String, Map<String, Object>> otcCheckInList(String username){
		return otcRepo.sp_getotccheckinlisting(username);
	}
	
	public Map<String, Object> otcCheckInStatus(String session_id, String user_id) {
		return otcRepo.sp_getotccheckedincounter(session_id, user_id);
	}	
	
	public Map<String, String> sp_getotccheckedininfo(String counter_id){
		return otcRepo.sp_getotccheckedininfo(counter_id);
	}
	
	public Map<String, String> sp_getotccheckedinuserinfo(String user_id){
		return otcRepo.sp_getotccheckedinuserinfo(user_id);
	}

	//shceduler
	public List<HashMap<String, String>> sp_getOtcOpenCtr(){
		return otcRepo.sp_getOtcOpenCtr();
	}

	//scheduler
	public List<HashMap<String, String>> sp_getotccheckout(){
		return otcRepo.sp_getotccheckout();
	}
}
