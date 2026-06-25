package com.maven.rms.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_ricp")
public class RICP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ricp_id;
    private String txn_type;
    private String entity_type;
    private String entity_no;
    private String calendar_yr;
    private String cp_no;
    private String cp_act_id;
    private String cp_sect_id;
    private String cp_sub_sect_id;
    private Date dt_issuance;
    private Date dt_expiry;
    private Date dt_void;
    private Date dt_cancel;
    private Date dt_writeoff;
    private BigDecimal cp_amt;
    private BigDecimal accr_amt;
    private LocalDateTime dt_created;
    private LocalDateTime dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    private Integer cp_tier;
    private BigDecimal cp_tier_amt; 
    
    
	//@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy HH:mm:ss[.SSS]")
    public RICP() {}
    
    public RICP(Integer ricp_id, String entity_type, String entity_no, String cp_no, 
    		Date dt_issuance, Date dt_expiry, BigDecimal cp_amt, BigDecimal accr_amt,
    		Integer cp_tier, BigDecimal cp_tier_amt, String status) {
    	this.ricp_id = ricp_id;
    	this.entity_type = entity_type;
    	this.entity_no = entity_no;
    	this.cp_no = cp_no;
    	this.dt_issuance = dt_issuance;
    	this.dt_expiry = dt_expiry;
    	this.cp_amt = cp_amt;
    	this.accr_amt = accr_amt;
    	this.cp_tier = cp_tier;
    	this.cp_tier_amt = cp_tier_amt;
    	this.status = status;
    }
    
    public RICP(String entity_type, String entity_no, String cp_no, String cp_act_id, String cp_sect_id,
    		String cp_sub_sect_id, Date dt_issuance, Date dt_expiry, BigDecimal cp_amt, BigDecimal accr_amt,
    		Integer cp_tier, BigDecimal cp_tier_amt, Date dt_writeoff, String username) {
    	this.entity_type = entity_type;
    	this.entity_no = entity_no;
    	this.cp_no = cp_no;
    	this.cp_act_id = cp_act_id;
    	this.cp_sect_id = cp_sect_id;
    	this.cp_sub_sect_id = cp_sub_sect_id;
    	this.dt_issuance = dt_issuance;
    	this.dt_expiry = dt_expiry;
    	this.cp_amt = cp_amt;
    	this.accr_amt = accr_amt;
    	this.cp_tier = cp_tier;
    	this.cp_tier_amt = cp_tier_amt;
    	this.dt_writeoff = dt_writeoff;
		

    	this.txn_type = "CP";
    	this.calendar_yr = Integer.toString(Year.now().getValue());
    	this.dt_void = null;
    	this.dt_cancel = null;
    	this.dt_created = LocalDateTime.now();
    	this.dt_modified = this.dt_created;
    	this.status = "CA"; //Set as collectable
    	this.created_by = username;
    	this.modified_by = username;
    }

	// public Integer getRicp_id() {
	// 	return ricp_id;
	// }
	// public void setRicp_id(Integer ricp_id) {
	// 	this.ricp_id = ricp_id;
	// }
	// public String getTxn_type() {
	// 	return txn_type;
	// }
	// public void setTxn_type(String txn_type) {
	// 	this.txn_type = txn_type;
	// }
	// public String getEntity_type() {
	// 	return entity_type;
	// }
	// public void setEntity_type(String entity_type) {
	// 	this.entity_type = entity_type;
	// }
	// public String getEntity_no() {
	// 	return entity_no;
	// }
	// public void setEntity_no(String entity_no) {
	// 	this.entity_no = entity_no;
	// }
	// public String getCalendar_yr() {
	// 	return calendar_yr;
	// }
	// public void setCalendar_yr(String calendar_yr) {
	// 	this.calendar_yr = calendar_yr;
	// }
	// public String getCp_no() {
	// 	return cp_no;
	// }
	// public void setCp_no(String cp_no) {
	// 	this.cp_no = cp_no;
	// }
	// public String getCp_act_id() {
	// 	return cp_act_id;
	// }
	// public void setCp_act_id(String cp_act_id) {
	// 	this.cp_act_id = cp_act_id;
	// }
	// public String getCp_sect_id() {
	// 	return cp_sect_id;
	// }
	// public void setCp_sect_id(String cp_sect_id) {
	// 	this.cp_sect_id = cp_sect_id;
	// }
	// public String getCp_sub_sect_id() {
	// 	return cp_sub_sect_id;
	// }
	// public void setCp_sub_sect_id(String cp_sub_sect_id) {
	// 	this.cp_sub_sect_id = cp_sub_sect_id;
	// }
	// public Date getDt_issuance() {
	// 	return dt_issuance;
	// }
	// public void setDt_issuance(Date dt_issuance) {
	// 	this.dt_issuance = dt_issuance;
	// }
	// public Date getDt_expiry() {
	// 	return dt_expiry;
	// }
	// public void setDt_expiry(Date dt_expiry) {
	// 	this.dt_expiry = dt_expiry;
	// }
	// public Date getDt_void() {
	// 	return dt_void;
	// }
	// public void setDt_void(Date dt_void) {
	// 	this.dt_void = dt_void;
	// }
	// public Date getDt_cancel() {
	// 	return dt_cancel;
	// }
	// public void setDt_cancel(Date dt_cancel) {
	// 	this.dt_cancel = dt_cancel;
	// }
	// public Date getDt_writeoff() {
	// 	return dt_writeoff;
	// }
	// public void setDt_writeoff(Date dt_writeoff) {
	// 	this.dt_writeoff = dt_writeoff;
	// }
	// public BigDecimal getCp_amt() {
	// 	return cp_amt;
	// }
	// public void setCp_amt(BigDecimal cp_amt) {
	// 	this.cp_amt = cp_amt;
	// }
	// public BigDecimal getAccr_amt() {
	// 	return accr_amt;
	// }
	// public void setAccr_amt(BigDecimal accr_amt) {
	// 	this.accr_amt = accr_amt;
	// }
	// public LocalDateTime getDt_created() {
	// 	return dt_created;
	// }
	// public void setDt_created(LocalDateTime dt_created) {
	// 	this.dt_created = dt_created;
	// }
	// public LocalDateTime getDt_modified() {
	// 	return dt_modified;
	// }
	// public void setDt_modified(LocalDateTime dt_modified) {
	// 	this.dt_modified = dt_modified;
	// }
	// public String getCreated_by() {
	// 	return created_by;
	// }
	// public void setCreated_by(String created_by) {
	// 	this.created_by = created_by;
	// }
	// public String getModified_by() {
	// 	return modified_by;
	// }
	// public void setModified_by(String modified_by) {
	// 	this.modified_by = modified_by;
	// }
	// public String getStatus() {
	// 	return status;
	// }
	// public void setStatus(String status) {
	// 	this.status = status;
	// }
	// public Integer getCp_tier() {
	// 	return cp_tier;
	// }
	// public void setCp_tier(Integer cp_tier) {
	// 	this.cp_tier = cp_tier;
	// }
	// public BigDecimal getCp_tier_amt() {
	// 	return cp_tier_amt;
	// }
	// public void setCp_tier_amt(BigDecimal cp_tier_amt) {
	// 	this.cp_tier_amt = cp_tier_amt;
	// }	
}
