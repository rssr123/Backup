package com.maven.rms.models;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_rpt_ricp_age")
public class RICPAgingReportRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer rpt_ricp_age_id;
    
    private LocalDateTime p_dt_req;
    private Integer p_exp_status;
    private Integer p_can_v_status;
    private String p_ent_ty;
    private String p_ent_nm;
    private LocalDateTime p_dt_iss_fr;
    private LocalDateTime p_dt_iss_to;
    private LocalDateTime p_dt_rcpt_fr;	//payment date range
    private LocalDateTime p_dt_rcpt_to;
    private LocalDateTime p_dt_exp_fr;
    private LocalDateTime p_dt_exp_to;
    private LocalDateTime p_dt_wo_fr;
    private LocalDateTime p_dt_wo_to;
    private LocalDateTime p_dt_can_fr;
    private LocalDateTime p_dt_can_to;
    private LocalDateTime p_dt_void_fr;
    private LocalDateTime p_dt_void_to;
    private LocalDateTime dt_created;
    private LocalDateTime dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    private String p_email;
    private String p_file_type;
    private Integer p_file_size;
    private String p_file_nm;
    private String p_batch_no;
    private String p_fms_ref_no;
    private String task_id;
    
    public RICPAgingReportRequest(LocalDateTime p_dt_req, LocalDateTime p_dt_iss_fr, LocalDateTime p_dt_iss_to
    								, Integer p_exp_status, Integer p_can_v_status, String created_by, String p_file_type) {
    	this.p_dt_req = p_dt_req;
    	this.p_dt_iss_fr = p_dt_iss_fr;
    	this.p_dt_iss_to = p_dt_iss_to;
    	this.p_exp_status = p_exp_status;
    	this.p_can_v_status = p_can_v_status;
    	this.status = "P";
    	this.created_by = created_by;
    	this.modified_by = created_by;
    	this.p_file_type = p_file_type;
    }
    
	// public Integer getRpt_ricp_age_id() {
	// 	return rpt_ricp_age_id;
	// }
	// public void setRpt_ricp_age_id(Integer rpt_ricp_age_id) {
	// 	this.rpt_ricp_age_id = rpt_ricp_age_id;
	// }
	// public LocalDateTime getP_dt_req() {
	// 	return p_dt_req;
	// }
	// public void setP_dt_req(LocalDateTime p_dt_req) {
	// 	this.p_dt_req = p_dt_req;
	// }
	// public Integer getP_exp_status() {
	// 	return p_exp_status;
	// }
	// public void setP_exp_status(Integer p_exp_status) {
	// 	this.p_exp_status = p_exp_status;
	// }
	// public Integer getP_can_v_status() {
	// 	return p_can_v_status;
	// }
	// public void setP_can_v_status(Integer p_can_v_status) {
	// 	this.p_can_v_status = p_can_v_status;
	// }
	// public String getP_ent_ty() {
	// 	return p_ent_ty;
	// }
	// public void setP_ent_ty(String p_ent_ty) {
	// 	this.p_ent_ty = p_ent_ty;
	// }
	// public String getP_ent_nm() {
	// 	return p_ent_nm;
	// }
	// public void setP_ent_nm(String p_ent_nm) {
	// 	this.p_ent_nm = p_ent_nm;
	// }
	// public LocalDateTime getP_dt_iss_fr() {
	// 	return p_dt_iss_fr;
	// }
	// public void setP_dt_iss_fr(LocalDateTime p_dt_iss_fr) {
	// 	this.p_dt_iss_fr = p_dt_iss_fr;
	// }
	// public LocalDateTime getP_dt_iss_to() {
	// 	return p_dt_iss_to;
	// }
	// public void setP_dt_iss_to(LocalDateTime p_dt_iss_to) {
	// 	this.p_dt_iss_to = p_dt_iss_to;
	// }
	// public LocalDateTime getP_dt_exp_fr() {
	// 	return p_dt_exp_fr;
	// }
	// public void setP_dt_exp_fr(LocalDateTime p_dt_exp_fr) {
	// 	this.p_dt_exp_fr = p_dt_exp_fr;
	// }
	// public LocalDateTime getP_dt_exp_to() {
	// 	return p_dt_exp_to;
	// }
	// public void setP_dt_exp_to(LocalDateTime p_dt_exp_to) {
	// 	this.p_dt_exp_to = p_dt_exp_to;
	// }
	// public LocalDateTime getP_dt_wo_fr() {
	// 	return p_dt_wo_fr;
	// }
	// public void setP_dt_wo_fr(LocalDateTime p_dt_wo_fr) {
	// 	this.p_dt_wo_fr = p_dt_wo_fr;
	// }
	// public LocalDateTime getP_dt_wo_to() {
	// 	return p_dt_wo_to;
	// }
	// public void setP_dt_wo_to(LocalDateTime p_dt_wo_to) {
	// 	this.p_dt_wo_to = p_dt_wo_to;
	// }
	// public LocalDateTime getP_dt_can_fr() {
	// 	return p_dt_can_fr;
	// }
	// public void setP_dt_can_fr(LocalDateTime p_dt_can_fr) {
	// 	this.p_dt_can_fr = p_dt_can_fr;
	// }
	// public LocalDateTime getP_dt_can_to() {
	// 	return p_dt_can_to;
	// }
	// public void setP_dt_can_to(LocalDateTime p_dt_can_to) {
	// 	this.p_dt_can_to = p_dt_can_to;
	// }
	// public LocalDateTime getP_dt_void_fr() {
	// 	return p_dt_void_fr;
	// }
	// public void setP_dt_void_fr(LocalDateTime p_dt_void_fr) {
	// 	this.p_dt_void_fr = p_dt_void_fr;
	// }
	// public LocalDateTime getP_dt_void_to() {
	// 	return p_dt_void_to;
	// }
	// public void setP_dt_void_to(LocalDateTime p_dt_void_to) {
	// 	this.p_dt_void_to = p_dt_void_to;
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
	// public String getP_email() {
	// 	return p_email;
	// }
	// public void setP_email(String p_email) {
	// 	this.p_email = p_email;
	// }
	// public LocalDateTime getP_dt_rcpt_fr() {
	// 	return p_dt_rcpt_fr;
	// }
	// public void setP_dt_rcpt_fr(LocalDateTime p_dt_rcpt_fr) {
	// 	this.p_dt_rcpt_fr = p_dt_rcpt_fr;
	// }
	// public LocalDateTime getP_dt_rcpt_to() {
	// 	return p_dt_rcpt_to;
	// }
	// public void setP_dt_rcpt_to(LocalDateTime p_dt_rcpt_to) {
	// 	this.p_dt_rcpt_to = p_dt_rcpt_to;
	// }
	// public String getP_file_type() {
	// 	return p_file_type;
	// }
	// public void setP_file_type(String p_file_type) {
	// 	this.p_file_type = p_file_type;
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

	// public String getTask_id() {
	// 	return task_id;
	// }

	// public void setTask_id(String task_id) {
	// 	this.task_id = task_id;
	// }
}
