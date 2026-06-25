package com.maven.rms.models.OTC;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCollectionReceiptRequest {
	// private MTTPG pG;
	private OTCPaymentDone otc;
	private OTCRcpt rcpt;
	private List<OTCCollectionReceiptingPymtItem> itemList;
	private String type;
	
	// // For OTCReceiptGenerator
	// public OTCollectionReceiptingRequest(MTTPG pG, OTCPayment otc, OTCRcpt rcpt, List<OTCCollectionReceiptingPymtItem> itemList, String type) {
	// 	// this.pG = pG;
	// 	this.otc = otc;
	// 	this.rcpt = rcpt;
	// 	this.itemList = itemList;
	// 	this.type = type;
	// }

	// For OTCReceiptGenerator
	public OTCollectionReceiptRequest(OTCPaymentDone otc, OTCRcpt rcpt, List<OTCCollectionReceiptingPymtItem> itemList, String type) {
		// this.pG = pG;
		this.otc = otc;
		this.rcpt = rcpt;
		this.itemList = itemList;
		this.type = type;
	}

}
