package com.maven.rms.models;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReceiptRequest {
	private MTTPG pG;
	private OnlinePayment mtt;
	private MTTRCPT rcpt;
	private List<OnlinePaymentItem> itemList;
	private String type;
	
	//For MTTPGReceiptGenerator
	public ReceiptRequest(MTTPG pG, OnlinePayment mtt, MTTRCPT rcpt, List<OnlinePaymentItem> itemList, String type) {
		this.pG = pG;
		this.mtt = mtt;
		this.rcpt = rcpt;
		this.itemList = itemList;
		this.type = type;
	}

	// public MTTPG getpG() {
	// 	return pG;
	// }

	// public void setpG(MTTPG pG) {
	// 	this.pG = pG;
	// }

	// public OnlinePayment getMtt() {
	// 	return mtt;
	// }

	// public void setMtt(OnlinePayment mtt) {
	// 	this.mtt = mtt;
	// }

	// public MTTRCPT getRcpt() {
	// 	return rcpt;
	// }

	// public void setRcpt(MTTRCPT rcpt) {
	// 	this.rcpt = rcpt;
	// }

	// public List<OnlinePaymentItem> getItemList() {
	// 	return itemList;
	// }

	// public void setItemList(List<OnlinePaymentItem> itemList) {
	// 	this.itemList = itemList;
	// }

	// public String getType() {
	// 	return type;
	// }

	// public void setType(String type) {
	// 	this.type = type;
	// }
}
