package com.maven.rms.models;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class eGHLPaymentAPIRequest {
	private String mdType;
	private String transactionType;
	private String pymtMethod;
	private String serviceID;
	private String paymentID;
	private String amount;
	private String currencyCode;
	
	public eGHLPaymentAPIRequest(String mdType, String transactionType, String pymtMethod, String serviceID
			, String paymentID, String amount, String currencyCode) {
		this.mdType = mdType;
		this.transactionType = transactionType;
		this.pymtMethod = pymtMethod;
		this.serviceID = serviceID;
		this.paymentID = paymentID;
		this.amount = amount;
		this.currencyCode = currencyCode;
	}
	
	// public String getMdType() {
	// 	return mdType;
	// }
	// public void setMdType(String mdType) {
	// 	this.mdType = mdType;
	// }
	// public String getTransactionType() {
	// 	return transactionType;
	// }
	// public void setTransactionType(String transactionType) {
	// 	this.transactionType = transactionType;
	// }
	// public String getPymtMethod() {
	// 	return pymtMethod;
	// }
	// public void setPymtMethod(String pymtMethod) {
	// 	this.pymtMethod = pymtMethod;
	// }
	// public String getServiceID() {
	// 	return serviceID;
	// }
	// public void setServiceID(String serviceID) {
	// 	this.serviceID = serviceID;
	// }
	// public String getPaymentID() {
	// 	return paymentID;
	// }
	// public void setPaymentID(String paymentID) {
	// 	this.paymentID = paymentID;
	// }
	// public String getAmount() {
	// 	return amount;
	// }
	// public void setAmount(String amount) {
	// 	this.amount = amount;
	// }
	// public String getCurrencyCode() {
	// 	return currencyCode;
	// }
	// public void setCurrencyCode(String currencyCode) {
	// 	this.currencyCode = currencyCode;
	// }
}
