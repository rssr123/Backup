package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MFTWFRequest {

	private Integer i_page;
	private Integer i_size;
	private BigInteger i_wf_id;
	private String i_fee_detail_id;
	private Integer i_fee_grp_id;
	private String i_fee_detail_nm_e;
	private String i_fee_detail_nm_b;
	private BigDecimal i_fee_amt;
	private Date i_promo_startdt;
	private Date i_promo_enddt;
	private BigDecimal i_promo_fee;
	private Integer i_allow_otc;
	private String i_ll_parent_id;
	private Integer i_ll_start_day;
	private Integer i_ll_start_mth;
	private Integer i_ll_end_day;
	private Integer i_ll_end_mth;
	private String i_ledger_cd;
	private String i_ss_cd;
	private String i_created_by;
	private String i_modified_by;
	private String i_status;
	private Date i_effective_date;
	private String i_remark;
	private String i_assign_to;
	private String i_action;
	private String i_created_by_nm;
	private String i_modified_by_nm;
	private Date i_dt_modified_fr;
	private Date i_dt_modified_to;
	private Date i_dt_created_fr;
	private Date i_dt_created_to;
	private Date i_dt_effective_fr;
	private Date i_dt_effective_to;
	private String i_wf_is_in_prg;
	private Integer i_fee_detail_pk;
	private Integer i_tax_cd_id;
	private String i_r_fee_det_nm;
	private BigDecimal i_r_fee_amt;
	private String i_r_ss_cd;
	private Date i_r_promo_startdt;
	private Date i_r_promo_enddt;
	private Integer i_r_ll_required;
	private String i_r_add_notes;
	private String i_mft_status;
	private BigDecimal i_r_promo_fee;

	private Integer i_ispub;

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

	// public BigInteger getI_wf_id() {
	// 	return i_wf_id;
	// }

	// public void setI_wf_id(BigInteger i_wf_id) {
	// 	this.i_wf_id = i_wf_id;
	// }

	// public String getI_fee_detail_id() {
	// 	return i_fee_detail_id;
	// }

	// public void setI_fee_detail_id(String i_fee_detail_id) {
	// 	this.i_fee_detail_id = i_fee_detail_id;
	// }

	// public Integer getI_fee_grp_id() {
	// 	return i_fee_grp_id;
	// }

	// public void setI_fee_grp_id(Integer i_fee_grp_id) {
	// 	this.i_fee_grp_id = i_fee_grp_id;
	// }

	// public String getI_fee_detail_nm_e() {
	// 	return i_fee_detail_nm_e;
	// }

	// public void setI_fee_detail_nm_e(String i_fee_detail_nm_e) {
	// 	this.i_fee_detail_nm_e = i_fee_detail_nm_e;
	// }

	// public String getI_fee_detail_nm_b() {
	// 	return i_fee_detail_nm_b;
	// }

	// public void setI_fee_detail_nm_b(String i_fee_detail_nm_b) {
	// 	this.i_fee_detail_nm_b = i_fee_detail_nm_b;
	// }

	// public BigDecimal getI_fee_amt() {
	// 	return i_fee_amt;
	// }

	// public void setI_fee_amt(BigDecimal i_fee_amt) {
	// 	this.i_fee_amt = i_fee_amt;
	// }

	// public Date getI_promo_startdt() {
	// 	return i_promo_startdt;
	// }

	// public void setI_promo_startdt(Date i_promo_startdt) {
	// 	this.i_promo_startdt = i_promo_startdt;
	// }

	// public Date getI_promo_enddt() {
	// 	return i_promo_enddt;
	// }

	// public void setI_promo_enddt(Date i_promo_enddt) {
	// 	this.i_promo_enddt = i_promo_enddt;
	// }

	// public BigDecimal getI_promo_fee() {
	// 	return i_promo_fee;
	// }

	// public void setI_promo_fee(BigDecimal i_promo_fee) {
	// 	this.i_promo_fee = i_promo_fee;
	// }

	// public Integer getI_allow_otc() {
	// 	return i_allow_otc;
	// }

	// public void setI_allow_otc(Integer i_allow_otc) {
	// 	this.i_allow_otc = i_allow_otc;
	// }

	// public String getI_ll_parent_id() {
	// 	return i_ll_parent_id;
	// }

	// public void setI_ll_parent_id(String i_ll_parent_id) {
	// 	this.i_ll_parent_id = i_ll_parent_id;
	// }

	// public Integer getI_ll_start_day() {
	// 	return i_ll_start_day;
	// }

	// public void setI_ll_start_day(Integer i_ll_start_day) {
	// 	this.i_ll_start_day = i_ll_start_day;
	// }

	// public Integer getI_ll_start_mth() {
	// 	return i_ll_start_mth;
	// }

	// public void setI_ll_start_mth(Integer i_ll_start_mth) {
	// 	this.i_ll_start_mth = i_ll_start_mth;
	// }

	// public Integer getI_ll_end_day() {
	// 	return i_ll_end_day;
	// }

	// public void setI_ll_end_day(Integer i_ll_end_day) {
	// 	this.i_ll_end_day = i_ll_end_day;
	// }

	// public Integer getI_ll_end_mth() {
	// 	return i_ll_end_mth;
	// }

	// public void setI_ll_end_mth(Integer i_ll_end_mth) {
	// 	this.i_ll_end_mth = i_ll_end_mth;
	// }

	// public String getI_ledger_cd() {
	// 	return i_ledger_cd;
	// }

	// public void setI_ledger_cd(String i_ledger_cd) {
	// 	this.i_ledger_cd = i_ledger_cd;
	// }

	// public String getI_ss_cd() {
	// 	return i_ss_cd;
	// }

	// public void setI_ss_cd(String i_ss_cd) {
	// 	this.i_ss_cd = i_ss_cd;
	// }

	// public String getI_created_by() {
	// 	return i_created_by;
	// }

	// public void setI_created_by(String i_created_by) {
	// 	this.i_created_by = i_created_by;
	// }

	// public String getI_modified_by() {
	// 	return i_modified_by;
	// }

	// public void setI_modified_by(String i_modified_by) {
	// 	this.i_modified_by = i_modified_by;
	// }

	// public String getI_status() {
	// 	return i_status;
	// }

	// public void setI_status(String i_status) {
	// 	this.i_status = i_status;
	// }

	// public Date getI_effective_date() {
	// 	return i_effective_date;
	// }

	// public void setI_effective_date(Date i_effective_date) {
	// 	this.i_effective_date = i_effective_date;
	// }

	// public String getI_remark() {
	// 	return i_remark;
	// }

	// public void setI_remark(String i_remark) {
	// 	this.i_remark = i_remark;
	// }

	// public String getI_assign_to() {
	// 	return i_assign_to;
	// }

	// public void setI_assign_to(String i_assign_to) {
	// 	this.i_assign_to = i_assign_to;
	// }

	// public String getI_action() {
	// 	return i_action;
	// }

	// public void setI_action(String i_action) {
	// 	this.i_action = i_action;
	// }

	// public String getI_modified_by_nm() {
	// 	return i_modified_by_nm;
	// }

	// public void setI_modified_by_nm(String i_modified_by_nm) {
	// 	this.i_modified_by_nm = i_modified_by_nm;
	// }

	// public Date getI_dt_modified_fr() {
	// 	return i_dt_modified_fr;
	// }

	// public void setI_dt_modified_fr(Date i_dt_modified_fr) {
	// 	this.i_dt_modified_fr = i_dt_modified_fr;
	// }

	// public Date getI_dt_modified_to() {
	// 	return i_dt_modified_to;
	// }

	// public void setI_dt_modified_to(Date i_dt_modified_to) {
	// 	this.i_dt_modified_to = i_dt_modified_to;
	// }

	// public Date getI_dt_created_fr() {
	// 	return i_dt_created_fr;
	// }

	// public void setI_dt_created_fr(Date i_dt_created_fr) {
	// 	this.i_dt_created_fr = i_dt_created_fr;
	// }

	// public Date getI_dt_created_to() {
	// 	return i_dt_created_to;
	// }

	// public void setI_dt_created_to(Date i_dt_created_to) {
	// 	this.i_dt_created_to = i_dt_created_to;
	// }

	// public Date getI_dt_effective_fr() {
	// 	return i_dt_effective_fr;
	// }

	// public void setI_dt_effective_fr(Date i_dt_effective_fr) {
	// 	this.i_dt_effective_fr = i_dt_effective_fr;
	// }

	// public Date getI_dt_effective_to() {
	// 	return i_dt_effective_to;
	// }

	// public void setI_dt_effective_to(Date i_dt_effective_to) {
	// 	this.i_dt_effective_to = i_dt_effective_to;
	// }

	// public String getI_wf_is_in_prg() {
	// 	return i_wf_is_in_prg;
	// }

	// public void setI_wf_is_in_prg(String i_wf_is_in_prg) {
	// 	this.i_wf_is_in_prg = i_wf_is_in_prg;
	// }

	// public Integer getI_fee_detail_pk() {
	// 	return i_fee_detail_pk;
	// }

	// public void setI_fee_detail_pk(Integer i_fee_detail_pk) {
	// 	this.i_fee_detail_pk = i_fee_detail_pk;
	// }

	// public Integer getI_tax_cd_id() {
	// 	return i_tax_cd_id;
	// }

	// public void setI_tax_cd_id(Integer i_tax_cd_id) {
	// 	this.i_tax_cd_id = i_tax_cd_id;
	// }

	// public String getI_r_fee_det_nm() {
	// 	return i_r_fee_det_nm;
	// }

	// public void setI_r_fee_det_nm(String i_r_fee_det_nm) {
	// 	this.i_r_fee_det_nm = i_r_fee_det_nm;
	// }

	// public BigDecimal getI_r_fee_amt() {
	// 	return i_r_fee_amt;
	// }

	// public void setI_r_fee_amt(BigDecimal i_r_fee_amt) {
	// 	this.i_r_fee_amt = i_r_fee_amt;
	// }

	// public String getI_r_ss_cd() {
	// 	return i_r_ss_cd;
	// }

	// public void setI_r_ss_cd(String i_r_ss_cd) {
	// 	this.i_r_ss_cd = i_r_ss_cd;
	// }

	// public Date getI_r_promo_startdt() {
	// 	return i_r_promo_startdt;
	// }

	// public void setI_r_promo_startdt(Date i_r_promo_startdt) {
	// 	this.i_r_promo_startdt = i_r_promo_startdt;
	// }

	// public Date getI_r_promo_enddt() {
	// 	return i_r_promo_enddt;
	// }

	// public void setI_r_promo_enddt(Date i_r_promo_enddt) {
	// 	this.i_r_promo_enddt = i_r_promo_enddt;
	// }

	// public Integer getI_r_ll_required() {
	// 	return i_r_ll_required;
	// }

	// public void setI_r_ll_required(Integer i_r_ll_required) {
	// 	this.i_r_ll_required = i_r_ll_required;
	// }

	// public String getI_r_add_notes() {
	// 	return i_r_add_notes;
	// }

	// public void setI_r_add_notes(String i_r_add_notes) {
	// 	this.i_r_add_notes = i_r_add_notes;
	// }

	// public String getI_mft_status() {
	// 	return i_mft_status;
	// }

	// public void setI_mft_status(String i_mft_status) {
	// 	this.i_mft_status = i_mft_status;
	// }

	// public BigDecimal getI_r_promo_fee() {
	// 	return i_r_promo_fee;
	// }

	// public void setI_r_promo_fee(BigDecimal i_r_promo_fee) {
	// 	this.i_r_promo_fee = i_r_promo_fee;
	// }

	
	public MFTWFRequest() {
	}

	// constructor for sp_getmftwf
	public MFTWFRequest(Integer i_page, Integer i_size, BigInteger i_wf_id, Integer i_fee_detail_pk,
			String i_fee_detail_id, String i_assign_to, String i_status, String i_created_by, String i_modified_by, String i_modified_by_nm,
			Date i_dt_modified_fr, Date i_dt_modified_to, Date i_dt_created_fr, Date i_dt_created_to,
			Date i_dt_effective_fr, Date i_dt_effective_to, String i_ss_cd, String i_wf_is_in_prg) {
		this.i_page = i_page;
		this.i_size = i_size;
		this.i_wf_id = i_wf_id;
		this.i_fee_detail_pk = i_fee_detail_pk;
		this.i_fee_detail_id = i_fee_detail_id;
		this.i_assign_to = i_assign_to;
		this.i_status = i_status;
		this.i_created_by = i_created_by;
		this.i_modified_by = i_modified_by;
		this.i_modified_by_nm = i_modified_by_nm;
		this.i_dt_modified_fr = i_dt_modified_fr;
		this.i_dt_modified_to = i_dt_modified_to;
		this.i_dt_created_fr = i_dt_created_fr;
		this.i_dt_created_to = i_dt_created_to;
		this.i_dt_effective_fr = i_dt_effective_fr;
		this.i_dt_effective_to = i_dt_effective_to;
		this.i_ss_cd = i_ss_cd;
		this.i_wf_is_in_prg = i_wf_is_in_prg;

	}

}
