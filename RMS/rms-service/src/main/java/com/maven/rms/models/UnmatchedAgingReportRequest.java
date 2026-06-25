package com.maven.rms.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_rpt_um_age")
public class UnmatchedAgingReportRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rpt_um_age_id;
    
    private LocalDateTime p_dt_req;
	private String p_recon_status;
    private String p_txn_id;
    private String p_rcpt_no;
    private String p_stmt_no;
	private LocalDateTime p_dt_stmt_fr;
	private LocalDateTime p_dt_stmt_to;
	private Integer p_dup;
	private String p_email;
	
	private String p_file_type;
    private String status;
	
	@Column(name="dt_created")
    private LocalDateTime dtCreated;
	
	@Column(name="dt_modified")
    private LocalDateTime dtModified;
	
    private String created_by;
    private String modified_by;
    
    private Integer p_file_size;
    private String p_file_nm;
    private String p_batch_no;
    private String  p_fms_ref_no;
    
    private String task_id;
    
    public UnmatchedAgingReportRequest(LocalDateTime requestDate, String reconStatus, String pgTxnId, String mttRcptNo, String rcPgStmtNo
    		, LocalDateTime stmtDtFr, LocalDateTime stmtDtTo, Integer duplicateFlag, String email, String fileType, String createdBy) {
    	super();
    	this.p_dt_req = requestDate;
    	this.p_recon_status = reconStatus;
    	this.p_txn_id = pgTxnId;
    	this.p_rcpt_no = mttRcptNo;
    	this.p_stmt_no = rcPgStmtNo;
    	this.p_dt_stmt_fr = stmtDtFr;
    	this.p_dt_stmt_to = stmtDtTo;
    	this.p_dup = duplicateFlag;
    	this.p_email = email;
    	this.p_file_type = fileType;
    	
    	this.status = "P";	//P=Pending (default) | I=In-Progress | S=Success | F=Failed | C=Canceled

    	this.created_by = createdBy;
    	this.modified_by = createdBy;    	
    }
    
	// public Integer getRpt_um_age_id() {
	// 	return rpt_um_age_id;
	// }
	// public void setRpt_um_age_id(Integer rpt_um_age_id) {
	// 	this.rpt_um_age_id = rpt_um_age_id;
	// }
	// public LocalDateTime getP_dt_req() {
	// 	return p_dt_req;
	// }
	// public void setP_dt_req(LocalDateTime p_dt_req) {
	// 	this.p_dt_req = p_dt_req;
	// }
	// public String getP_txn_id() {
	// 	return p_txn_id;
	// }
	// public void setP_txn_id(String p_txn_id) {
	// 	this.p_txn_id = p_txn_id;
	// }
	// public String getP_rcpt_no() {
	// 	return p_rcpt_no;
	// }
	// public void setP_rcpt_no(String p_rcpt_no) {
	// 	this.p_rcpt_no = p_rcpt_no;
	// }
	// public String getP_stmt_no() {
	// 	return p_stmt_no;
	// }
	// public void setP_stmt_no(String p_stmt_no) {
	// 	this.p_stmt_no = p_stmt_no;
	// }
	// public String getP_recon_status() {
	// 	return p_recon_status;
	// }
	// public void setP_recon_status(String p_recon_status) {
	// 	this.p_recon_status = p_recon_status;
	// }
	// public LocalDateTime getP_dt_stmt_fr() {
	// 	return p_dt_stmt_fr;
	// }
	// public void setP_dt_stmt_fr(LocalDateTime p_dt_stmt_fr) {
	// 	this.p_dt_stmt_fr = p_dt_stmt_fr;
	// }
	// public LocalDateTime getP_dt_stmt_to() {
	// 	return p_dt_stmt_to;
	// }
	// public void setP_dt_stmt_to(LocalDateTime p_dt_stmt_to) {
	// 	this.p_dt_stmt_to = p_dt_stmt_to;
	// }
	// public String getP_file_type() {
	// 	return p_file_type;
	// }
	// public void setP_file_type(String p_file_type) {
	// 	this.p_file_type = p_file_type;
	// }
	// public Integer getP_dup() {
	// 	return p_dup;
	// }
	// public void setP_dup(Integer p_dup) {
	// 	this.p_dup = p_dup;
	// }
	// public String getP_email() {
	// 	return p_email;
	// }
	// public void setP_email(String p_email) {
	// 	this.p_email = p_email;
	// }
	// public String getStatus() {
	// 	return status;
	// }
	// public void setStatus(String status) {
	// 	this.status = status;
	// }
	// public LocalDateTime getDtCreated() {
	// 	return dtCreated;
	// }
	// public void setDtCreated(LocalDateTime dtCreated) {
	// 	this.dtCreated = dtCreated;
	// }
	// public LocalDateTime getDtModified() {
	// 	return dtModified;
	// }
	// public void setDtModified(LocalDateTime dtModified) {
	// 	this.dtModified = dtModified;
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

	// public Integer getP_file_size() {
	// 	return p_file_size;
	// }

	// public void setP_file_size(Integer p_file_size) {
	// 	this.p_file_size = p_file_size;
	// }

	// public String getP_file_nm() {
	// 	return p_file_nm;
	// }

	// public void setP_file_nm(String p_file_nm) {
	// 	this.p_file_nm = p_file_nm;
	// }

	// public String getTask_id() {
	// 	return task_id;
	// }

	// public void setTask_id(String task_id) {
	// 	this.task_id = task_id;
	// }

	// public String getP_batch_no() {
	// 	return p_batch_no;
	// }

	// public void setP_batch_no(String p_batch_no) {
	// 	this.p_batch_no = p_batch_no;
	// }

	// public String getP_fms_ref_no() {
	// 	return p_fms_ref_no;
	// }

	// public void setP_fms_ref_no(String p_fms_ref_no) {
	// 	this.p_fms_ref_no = p_fms_ref_no;
	// }
}
