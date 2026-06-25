package com.example.fms.fms.models.payload.requests;

import java.math.BigDecimal;

public class SubmitRICPIssRequest {
	private String entity_type;
	private String entity_no;
	private String cp_no;
	private String cp_act_id;
	private String cp_sect_id;
	private String cp_sub_sect_id;
	private String dt_issuance;
	private String dt_expiry;
	private BigDecimal cp_amt;
	private BigDecimal accr_amt;
	private Integer cp_tier_lvl;
	private BigDecimal cp_tier_amt;
	
	public SubmitRICPIssRequest() {}

	public String getEntity_type() {
		return entity_type;
	}
	public void setEntity_type(String entity_type) {
		this.entity_type = entity_type;
	}
	public String getEntity_no() {
		return entity_no;
	}
	public void setEntity_no(String entity_no) {
		this.entity_no = entity_no;
	}
	public String getCp_no() {
		return cp_no;
	}
	public void setCp_no(String cp_no) {
		this.cp_no = cp_no;
	}
	public String getCp_act_id() {
		return cp_act_id;
	}
	public void setCp_act_id(String cp_act_id) {
		this.cp_act_id = cp_act_id;
	}
	public String getCp_sect_id() {
		return cp_sect_id;
	}
	public void setCp_sect_id(String cp_sect_id) {
		this.cp_sect_id = cp_sect_id;
	}
	public String getCp_sub_sect_id() {
		return cp_sub_sect_id;
	}
	public void setCp_sub_sect_id(String cp_sub_sect_id) {
		this.cp_sub_sect_id = cp_sub_sect_id;
	}
	public String getDt_issuance() {
		return dt_issuance;
	}
	public void setDt_issuance(String dt_issuance) {
		this.dt_issuance = dt_issuance;
	}
	public String getDt_expiry() {
		return dt_expiry;
	}
	public void setDt_expiry(String dt_expiry) {
		this.dt_expiry = dt_expiry;
	}
	public BigDecimal getCp_amt() {
		return cp_amt;
	}
	public void setCp_amt(BigDecimal cp_amt) {
		this.cp_amt = cp_amt;
	}
	public BigDecimal getAccr_amt() {
		return accr_amt;
	}
	public void setAccr_amt(BigDecimal accr_amt) {
		this.accr_amt = accr_amt;
	}
	public Integer getCp_tier_lvl() {
		return cp_tier_lvl;
	}
	public void setCp_tier_lvl(Integer cp_tier_lvl) {
		this.cp_tier_lvl = cp_tier_lvl;
	}
	public BigDecimal getCp_tier_amt() {
		return cp_tier_amt;
	}
	public void setCp_tier_amt(BigDecimal cp_tier_amt) {
		this.cp_tier_amt = cp_tier_amt;
	}
}
