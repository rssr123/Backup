package com.maven.rms.models.payload.requests;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OtcPaymentRequest {
	@NotNull(message = "ss_cd is required.")
    @Size(min = 1, max = 10, message="ss_cd size error.")
	private String ss_cd;
	private String pymt_method;
	private String collection_slip;
	@NotNull(message = "orn_no is required.")
    @Size(min = 1, max = 20, message="orn_no size error.")
	private String orn_no;
	@NotNull(message = "orn_dt is required.")
    @Size(min = 1, max = 24, message="orn_dt size error.")
	private String orn_dt;
	@NotNull(message = "cust_nm is required.")
    @Size(min = 1, max = 255, message="cust_nm size error.")
	private String cust_nm;
	@NotNull(message = "cust_addr_1 is required.")
    @Size(min = 1, max = 150, message="cust_addr_1 size error.")
	private String cust_addr_1;
	@NotNull(message = "cust_addr_2 is required.")
    @Size(min = 1, max = 150, message="cust_addr_2 size error.")
	private String cust_addr_2;
	// @NotNull(message = "cust_addr_3 is required.")
    // @Size(min = 1, max = 150, message="cust_addr_3 size error.")
	private String cust_addr_3;
	@NotNull(message = "cust_postcode is required.")
    @Size(min = 1, max = 15, message="cust_postcode size error.")
	private String cust_postcode;
	@NotNull(message = "cust_city is required.")
    @Size(min = 1, max = 50, message="cust_city size error.")
	private String cust_city;
	@NotNull(message = "cust_state is required.")
    @Size(min = 1, max = 50, message="cust_state size error.")
	private String cust_state;
	@NotNull(message = "cust_email is required.")
    @Size(min = 1, max = 150, message="cust_email size error.")
	private String cust_email;
	@NotNull(message = "cust_phone is required.")
    @Size(min = 1, max = 15, message="cust_phone size error.")
	private String cust_phone;
	@NotNull(message = "total_amt is required.")
	private BigDecimal total_amt;
	@NotNull(message = "ss_return_url is required.")
    @Size(min = 1, max = 2000, message="ss_return_url size error.")
	private String ss_return_url;
	private Integer email_flag;
	private List<OTCPymtItemDet> payment_item_details;
	
	public OtcPaymentRequest() {}
}
