package com.maven.rms.models;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_rpt_di_age")
public class DeferredIncomeAging {
    
    @Id
    private BigInteger rpt_di_age_id;
    private Date p_dt_req;
    private Integer p_tmn_status;
    private String p_ent_ty;
    private String p_ent_nm;
    private String p_txn_ty;
    private String p_status;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date p_dt_exp_fr;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date p_dt_exp_to;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date p_dt_eff_fr;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date p_dt_eff_to;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date p_dt_app_fr;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date p_dt_app_to;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date p_dt_tmn_fr;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date p_dt_tmn_to;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_modified;
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
    private Integer total;
    // public BigInteger getRpt_di_age_id() {
    //     return rpt_di_age_id;
    // }
    // public void setRpt_di_age_id(BigInteger rpt_di_age_id) {
    //     this.rpt_di_age_id = rpt_di_age_id;
    // }
    // public Date getP_dt_req() {
    //     return p_dt_req;
    // }
    // public void setP_dt_req(Date p_dt_req) {
    //     this.p_dt_req = p_dt_req;
    // }
    // public Integer getP_tmn_status() {
    //     return p_tmn_status;
    // }
    // public void setP_tmn_status(Integer p_tmn_status) {
    //     this.p_tmn_status = p_tmn_status;
    // }
    // public String getP_ent_ty() {
    //     return p_ent_ty;
    // }
    // public void setP_ent_ty(String p_ent_ty) {
    //     this.p_ent_ty = p_ent_ty;
    // }
    // public String getP_ent_nm() {
    //     return p_ent_nm;
    // }
    // public void setP_ent_nm(String p_ent_nm) {
    //     this.p_ent_nm = p_ent_nm;
    // }
    // public String getP_txn_ty() {
    //     return p_txn_ty;
    // }
    // public void setP_txn_ty(String p_txn_ty) {
    //     this.p_txn_ty = p_txn_ty;
    // }
    // public String getP_status() {
    //     return p_status;
    // }
    // public void setP_status(String p_status) {
    //     this.p_status = p_status;
    // }
    // public Date getP_dt_exp_fr() {
    //     return p_dt_exp_fr;
    // }
    // public void setP_dt_exp_fr(Date p_dt_exp_fr) {
    //     this.p_dt_exp_fr = p_dt_exp_fr;
    // }
    // public Date getP_dt_exp_to() {
    //     return p_dt_exp_to;
    // }
    // public void setP_dt_exp_to(Date p_dt_exp_to) {
    //     this.p_dt_exp_to = p_dt_exp_to;
    // }
    // public Date getP_dt_eff_fr() {
    //     return p_dt_eff_fr;
    // }
    // public void setP_dt_eff_fr(Date p_dt_eff_fr) {
    //     this.p_dt_eff_fr = p_dt_eff_fr;
    // }
    // public Date getP_dt_eff_to() {
    //     return p_dt_eff_to;
    // }
    // public void setP_dt_eff_to(Date p_dt_eff_to) {
    //     this.p_dt_eff_to = p_dt_eff_to;
    // }
    // public Date getP_dt_app_fr() {
    //     return p_dt_app_fr;
    // }
    // public void setP_dt_app_fr(Date p_dt_app_fr) {
    //     this.p_dt_app_fr = p_dt_app_fr;
    // }
    // public Date getP_dt_app_to() {
    //     return p_dt_app_to;
    // }
    // public void setP_dt_app_to(Date p_dt_app_to) {
    //     this.p_dt_app_to = p_dt_app_to;
    // }
    // public Date getP_dt_tmn_fr() {
    //     return p_dt_tmn_fr;
    // }
    // public void setP_dt_tmn_fr(Date p_dt_tmn_fr) {
    //     this.p_dt_tmn_fr = p_dt_tmn_fr;
    // }
    // public Date getP_dt_tmn_to() {
    //     return p_dt_tmn_to;
    // }
    // public void setP_dt_tmn_to(Date p_dt_tmn_to) {
    //     this.p_dt_tmn_to = p_dt_tmn_to;
    // }
    // public Date getDt_created() {
    //     return dt_created;
    // }
    // public void setDt_created(Date dt_created) {
    //     this.dt_created = dt_created;
    // }
    // public Date getDt_modified() {
    //     return dt_modified;
    // }
    // public void setDt_modified(Date dt_modified) {
    //     this.dt_modified = dt_modified;
    // }
    // public String getCreated_by() {
    //     return created_by;
    // }
    // public void setCreated_by(String created_by) {
    //     this.created_by = created_by;
    // }
    // public String getModified_by() {
    //     return modified_by;
    // }
    // public void setModified_by(String modified_by) {
    //     this.modified_by = modified_by;
    // }
    // public String getStatus() {
    //     return status;
    // }
    // public void setStatus(String status) {
    //     this.status = status;
    // }
    // public String getP_email() {
    //     return p_email;
    // }
    // public void setP_email(String p_email) {
    //     this.p_email = p_email;
    // }
    // public String getP_file_type() {
    //     return p_file_type;
    // }
    // public void setP_file_type(String p_file_type) {
    //     this.p_file_type = p_file_type;
    // }
    // public Integer getP_file_size() {
    //     return p_file_size;
    // }
    // public void setP_file_size(Integer p_file_size) {
    //     this.p_file_size = p_file_size;
    // }
    // public String getP_file_nm() {
    //     return p_file_nm;
    // }
    // public void setP_file_nm(String p_file_nm) {
    //     this.p_file_nm = p_file_nm;
    // }
    // public String getP_batch_no() {
    //     return p_batch_no;
    // }
    // public void setP_batch_no(String p_batch_no) {
    //     this.p_batch_no = p_batch_no;
    // }
    // public String getP_fms_ref_no() {
    //     return p_fms_ref_no;
    // }
    // public void setP_fms_ref_no(String p_fms_ref_no) {
    //     this.p_fms_ref_no = p_fms_ref_no;
    // }
    // public String getTask_id() {
    //     return task_id;
    // }
    // public void setTask_id(String task_id) {
    //     this.task_id = task_id;
    // }
    // public Integer getTotal() {
    //     return total;
    // }
    // public void setTotal(Integer total) {
    //     this.total = total;
    // }
    




}
