package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RICPAudit {
	private Integer i_ricp_id;
	private Date i_dt_txn;
	private String i_action_type;
	private String i_cp_no;
	private String i_entity_type;
	private String i_entity_no;
	private BigDecimal i_accr_amt_b4;
	private BigDecimal i_accr_amt_af;
	private String i_status_b4;
	private String i_status_af;
	private String i_created_by;
	private String i_modified_by;
	private String i_status;
	
	public RICPAudit(Integer i_ricp_id, Date i_dt_txn, String i_action_type, String i_cp_no,
    		String i_entity_type, String i_entity_no, BigDecimal i_accr_amt_b4, BigDecimal i_accr_amt_af,
    		String i_status_b4, String i_status_af, String i_created_by, String i_modified_by, String i_status) {
		this.i_ricp_id = i_ricp_id;
		this.i_dt_txn = i_dt_txn;
		this.i_action_type = i_action_type;
		this.i_cp_no = i_cp_no;
		this.i_entity_type = i_entity_type;
		this.i_entity_no = i_entity_no;
		this.i_accr_amt_b4 = i_accr_amt_b4;
		this.i_accr_amt_af = i_accr_amt_af;
		this.i_status_b4 = i_status_b4;
		this.i_status_af = i_status_af;
		this.i_created_by = i_created_by;
		this.i_modified_by = i_modified_by;
		this.i_status = i_status;
	}

	// public Integer getI_ricp_id() {
	// 	return i_ricp_id;
	// }

	// public void setI_ricp_id(Integer i_ricp_id) {
	// 	this.i_ricp_id = i_ricp_id;
	// }

	// public Date getI_dt_txn() {
	// 	return i_dt_txn;
	// }

	// public void setI_dt_txn(Date i_dt_txn) {
	// 	this.i_dt_txn = i_dt_txn;
	// }

	// public String getI_action_type() {
	// 	return i_action_type;
	// }

	// public void setI_action_type(String i_action_type) {
	// 	this.i_action_type = i_action_type;
	// }

	// public String getI_cp_no() {
	// 	return i_cp_no;
	// }

	// public void setI_cp_no(String i_cp_no) {
	// 	this.i_cp_no = i_cp_no;
	// }

	// public String getI_entity_type() {
	// 	return i_entity_type;
	// }

	// public void setI_entity_type(String i_entity_type) {
	// 	this.i_entity_type = i_entity_type;
	// }

	// public String getI_entity_no() {
	// 	return i_entity_no;
	// }

	// public void setI_entity_no(String i_entity_no) {
	// 	this.i_entity_no = i_entity_no;
	// }

	// public BigDecimal getI_accr_amt_b4() {
	// 	return i_accr_amt_b4;
	// }

	// public void setI_accr_amt_b4(BigDecimal i_accr_amt_b4) {
	// 	this.i_accr_amt_b4 = i_accr_amt_b4;
	// }

	// public BigDecimal getI_accr_amt_af() {
	// 	return i_accr_amt_af;
	// }

	// public void setI_accr_amt_af(BigDecimal i_accr_amt_af) {
	// 	this.i_accr_amt_af = i_accr_amt_af;
	// }

	// public String getI_status_b4() {
	// 	return i_status_b4;
	// }

	// public void setI_status_b4(String i_status_b4) {
	// 	this.i_status_b4 = i_status_b4;
	// }

	// public String getI_status_af() {
	// 	return i_status_af;
	// }

	// public void setI_status_af(String i_status_af) {
	// 	this.i_status_af = i_status_af;
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
}
