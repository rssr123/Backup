package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MTTListingDetReq {
	private Integer i_page;
	private Integer i_size;
	private String i_ss_cd;
    private String i_orn_no;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Singapore")
	private Date i_orn_dt_fr;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss",  timezone = "Asia/Singapore")
	private Date i_orn_dt_to;
	private BigDecimal i_total_amt;
	private String i_order_status;
    private String i_rcpt_no;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Singapore")
	private Date i_rcpt_dt_fr;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Singapore")
	private Date i_rcpt_dt_to;
	private String i_rms_type;
    
    // public MTTListingDetReq(Integer i_page, Integer i_size, String i_ss_cd,
    //         String i_orn_no, Date i_orn_dt_fr, Date i_orn_dt_to, BigDecimal i_total_amt, 
    //         String i_order_status, String i_rcpt_no, Date i_rcpt_dt_fr, Date i_rcpt_dt_to) {
	// 	this.i_page = i_page;
	// 	this.i_size = i_size;
	// 	this.i_ss_cd = i_ss_cd;
	// 	this.i_orn_no = i_orn_no;
	// 	this.i_orn_dt_fr = i_orn_dt_fr;
	// 	this.i_orn_dt_to = i_orn_dt_to;
	// 	this.i_total_amt = i_total_amt;
	// 	this.i_order_status = i_order_status;
	// 	this.i_rcpt_no = i_rcpt_no;
	// 	this.i_rcpt_dt_fr = i_rcpt_dt_fr;
	// 	this.i_rcpt_dt_to = i_rcpt_dt_to;
	// }
    
    // public MTTListingDetReq(String i_ss_cd, String i_orn_no, BigDecimal i_total_amt, String i_order_status, 
    //         String i_rcpt_no) {
	// 	this.i_ss_cd = i_ss_cd;
	// 	this.i_orn_no = i_orn_no;
	// 	this.i_total_amt = i_total_amt;
	// 	this.i_order_status = i_order_status;
	// 	this.i_rcpt_no = i_rcpt_no;
    // }

	// public Integer getI_page() {
	// 	return i_page;
	// }

	// public void setI_page(Integer i_page) {
	// 	this.i_page = i_page;
	// }

	// public Integer getI_size() {
	// 	return i_size;
	// }

	// public void setI_size(Integer i_size) {
	// 	this.i_size = i_size;
	// }

	// public String getI_ss_cd() {
	// 	return i_ss_cd;
	// }

	// public void setI_ss_cd(String i_ss_cd) {
	// 	this.i_ss_cd = i_ss_cd;
	// }

	// public String getI_orn_no() {
	// 	return i_orn_no;
	// }

	// public void setI_orn_no(String i_orn_no) {
	// 	this.i_orn_no = i_orn_no;
	// }

	// public Date getI_orn_dt_fr() {
	// 	return i_orn_dt_fr;
	// }

	// public void setI_orn_dt_fr(Date i_orn_dt_fr) {
	// 	this.i_orn_dt_fr = i_orn_dt_fr;
	// }

	// public Date getI_orn_dt_to() {
	// 	return i_orn_dt_to;
	// }

	// public void setI_orn_dt_to(Date i_orn_dt_to) {
	// 	this.i_orn_dt_to = i_orn_dt_to;
	// }

	// public BigDecimal getI_total_amt() {
	// 	return i_total_amt;
	// }

	// public void setI_total_amt(BigDecimal i_total_amt) {
	// 	this.i_total_amt = i_total_amt;
	// }

	// public String getI_order_status() {
	// 	return i_order_status;
	// }

	// public void setI_order_status(String i_order_status) {
	// 	this.i_order_status = i_order_status;
	// }

	// public String getI_rcpt_no() {
	// 	return i_rcpt_no;
	// }

	// public void setI_rcpt_no(String i_rcpt_no) {
	// 	this.i_rcpt_no = i_rcpt_no;
	// }

	// public Date getI_rcpt_dt_fr() {
	// 	return i_rcpt_dt_fr;
	// }

	// public void setI_rcpt_dt_fr(Date i_rcpt_dt_fr) {
	// 	this.i_rcpt_dt_fr = i_rcpt_dt_fr;
	// }

	// public Date getI_rcpt_dt_to() {
	// 	return i_rcpt_dt_to;
	// }

	// public void setI_rcpt_dt_to(Date i_rcpt_dt_to) {
	// 	this.i_rcpt_dt_to = i_rcpt_dt_to;
	// }
}
