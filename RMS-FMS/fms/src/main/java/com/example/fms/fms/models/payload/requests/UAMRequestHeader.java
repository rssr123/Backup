package com.example.fms.fms.models.payload.requests;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

@JsonPropertyOrder({"customerId","customerReferenceNo","customerRequestDate"})
public class UAMRequestHeader {
	private String customerId;
	private String customerReferenceNo;
	private LocalDateTime customerRequestDate;
	
	public UAMRequestHeader() {
		
	}
	@JsonGetter("customerId")
	public String getCustomerId() {
		return customerId;
	}
	@JsonSetter("customerId")
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}
	@JsonGetter("customerReferenceNo")
	public String getCustomerReferenceNo() {
		return customerReferenceNo;
	}
	@JsonSetter("customerReferenceNo")
	public void setCustomerReferenceNo(String customerReferenceNo) {
		this.customerReferenceNo = customerReferenceNo;
	}
	@JsonGetter("customerRequestDate")
	public LocalDateTime getCustomerRequestDate() {
		return customerRequestDate;
	}
	@JsonSetter("customerRequestDate")
	public void setCustomerRequestDate(LocalDateTime customerRequestDate) {
		this.customerRequestDate = customerRequestDate;
	}
}
