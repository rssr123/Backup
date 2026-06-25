package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_mft_wf")
public class MFTWF {
    
    @Id
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger wf_id;
    private Integer fee_detail_pk;
    private String fee_detail_id;
    private Integer fee_grp_id;
    private String fee_grp_nm_en;
    private String fee_grp_nm_bm;
    private String fee_detail_nm_e;
    private String fee_detail_nm_b;
    private BigDecimal fee_amt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy", timezone = "Asia/Singapore")
    private Date promo_startdt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy", timezone = "Asia/Singapore")
    private Date promo_enddt;
    private BigDecimal promo_fee;
    private Integer tax_cd_id;
    private String tax_cd;
    private Integer allow_otc;
    private String ll_parent_id;
    private Integer ll_start_day;
    private Integer ll_start_mth;
    private Integer ll_end_day;
    private Integer ll_end_mth;
    private String ledger_cd;
    private String ss_cd;
    private String ss_nm;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy", timezone = "Asia/Singapore")
    private Date effective_date;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_modified;
    private String created_by;
    private String created_by_nm;
    private String modified_by;
    private String modified_by_nm;
    private String status;
    private String status_en;
    private String status_bm;
    private String assign_to;
    private String assign_to_nm;
    private String action;
    private String r_fee_det_nm;
    private BigDecimal r_fee_amt;
    private String r_ss_cd;
     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy", timezone = "Asia/Singapore")
    private Date r_promo_startdt;
     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy", timezone = "Asia/Singapore")
    private Date r_promo_enddt;
    private Integer r_ll_required;
    private String r_add_notes;
    private String mft_status;
    private BigDecimal r_promo_fee;
    private String task_id;
    private Integer is_pub;
    private Integer total;

    // public BigInteger getWf_id() {
    //     return wf_id;
    // }
    // public void setWf_id(BigInteger wf_id) {
    //     this.wf_id = wf_id;
    // }
    // public Integer getFee_detail_pk() {
    //     return fee_detail_pk;
    // }
    // public void setFee_detail_pk(Integer fee_detail_pk) {
    //     this.fee_detail_pk = fee_detail_pk;
    // }
    // public String getFee_detail_id() {
    //     return fee_detail_id;
    // }
    // public void setFee_detail_id(String fee_detail_id) {
    //     this.fee_detail_id = fee_detail_id;
    // }
    // public Integer getFee_grp_id() {
    //     return fee_grp_id;
    // }
    // public void setFee_grp_id(Integer fee_grp_id) {
    //     this.fee_grp_id = fee_grp_id;
    // }
    // public String getFee_grp_nm_en() {
    //     return fee_grp_nm_en;
    // }
    // public void setFee_grp_nm_en(String fee_grp_nm_en) {
    //     this.fee_grp_nm_en = fee_grp_nm_en;
    // }
    // public String getFee_grp_nm_bm() {
    //     return fee_grp_nm_bm;
    // }
    // public void setFee_grp_nm_bm(String fee_grp_nm_bm) {
    //     this.fee_grp_nm_bm = fee_grp_nm_bm;
    // }
    // public String getFee_detail_nm_e() {
    //     return fee_detail_nm_e;
    // }
    // public void setFee_detail_nm_e(String fee_detail_nm_e) {
    //     this.fee_detail_nm_e = fee_detail_nm_e;
    // }
    // public String getFee_detail_nm_b() {
    //     return fee_detail_nm_b;
    // }
    // public void setFee_detail_nm_b(String fee_detail_nm_b) {
    //     this.fee_detail_nm_b = fee_detail_nm_b;
    // }
    // public BigDecimal getFee_amt() {
    //     return fee_amt;
    // }
    // public void setFee_amt(BigDecimal fee_amt) {
    //     this.fee_amt = fee_amt;
    // }
    // public Date getPromo_startdt() {
    //     return promo_startdt;
    // }
    // public void setPromo_startdt(Date promo_startdt) {
    //     this.promo_startdt = promo_startdt;
    // }
    // public Date getPromo_enddt() {
    //     return promo_enddt;
    // }
    // public void setPromo_enddt(Date promo_enddt) {
    //     this.promo_enddt = promo_enddt;
    // }
    // public BigDecimal getPromo_fee() {
    //     return promo_fee;
    // }
    // public void setPromo_fee(BigDecimal promo_fee) {
    //     this.promo_fee = promo_fee;
    // }
    // public Integer getTax_cd_id() {
    //     return tax_cd_id;
    // }
    // public void setTax_cd_id(Integer tax_cd_id) {
    //     this.tax_cd_id = tax_cd_id;
    // }
    // public String getTax_cd() {
    //     return tax_cd;
    // }
    // public void setTax_cd(String tax_cd) {
    //     this.tax_cd = tax_cd;
    // }
    // public Integer getAllow_otc() {
    //     return allow_otc;
    // }
    // public void setAllow_otc(Integer allow_otc) {
    //     this.allow_otc = allow_otc;
    // }
    // public String getLl_parent_id() {
    //     return ll_parent_id;
    // }
    // public void setLl_parent_id(String ll_parent_id) {
    //     this.ll_parent_id = ll_parent_id;
    // }
    // public Integer getLl_start_day() {
    //     return ll_start_day;
    // }
    // public void setLl_start_day(Integer ll_start_day) {
    //     this.ll_start_day = ll_start_day;
    // }
    // public Integer getLl_start_mth() {
    //     return ll_start_mth;
    // }
    // public void setLl_start_mth(Integer ll_start_mth) {
    //     this.ll_start_mth = ll_start_mth;
    // }
    // public Integer getLl_end_day() {
    //     return ll_end_day;
    // }
    // public void setLl_end_day(Integer ll_end_day) {
    //     this.ll_end_day = ll_end_day;
    // }
    // public Integer getLl_end_mth() {
    //     return ll_end_mth;
    // }
    // public void setLl_end_mth(Integer ll_end_mth) {
    //     this.ll_end_mth = ll_end_mth;
    // }
    // public String getLedger_cd() {
    //     return ledger_cd;
    // }
    // public void setLedger_cd(String ledger_cd) {
    //     this.ledger_cd = ledger_cd;
    // }
    // public String getSs_cd() {
    //     return ss_cd;
    // }
    // public void setSs_cd(String ss_cd) {
    //     this.ss_cd = ss_cd;
    // }
    // public String getSs_nm() {
    //     return ss_nm;
    // }
    // public void setSs_nm(String ss_nm) {
    //     this.ss_nm = ss_nm;
    // }
    // public Date getEffective_date() {
    //     return effective_date;
    // }
    // public void setEffective_date(Date effective_date) {
    //     this.effective_date = effective_date;
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
    // public String getCreated_by_nm() {
    //     return created_by_nm;
    // }
    // public void setCreated_by_nm(String created_by_nm) {
    //     this.created_by_nm = created_by_nm;
    // }
    // public String getModified_by() {
    //     return modified_by;
    // }
    // public void setModified_by(String modified_by) {
    //     this.modified_by = modified_by;
    // }
    // public String getModified_by_nm() {
    //     return modified_by_nm;
    // }
    // public void setModified_by_nm(String modified_by_nm) {
    //     this.modified_by_nm = modified_by_nm;
    // }
    // public String getStatus() {
    //     return status;
    // }
    // public void setStatus(String status) {
    //     this.status = status;
    // }
    // public String getStatus_en() {
    //     return status_en;
    // }
    // public void setStatus_en(String status_en) {
    //     this.status_en = status_en;
    // }
    // public String getStatus_bm() {
    //     return status_bm;
    // }
    // public void setStatus_bm(String status_bm) {
    //     this.status_bm = status_bm;
    // }
    // public String getAssign_to() {
    //     return assign_to;
    // }
    // public void setAssign_to(String assign_to) {
    //     this.assign_to = assign_to;
    // }
    // public String getAssign_to_nm() {
    //     return assign_to_nm;
    // }
    // public void setAssign_to_nm(String assign_to_nm) {
    //     this.assign_to_nm = assign_to_nm;
    // }
    // public String getAction() {
    //     return action;
    // }
    // public void setAction(String action) {
    //     this.action = action;
    // }
    // public String getR_fee_det_nm() {
    //     return r_fee_det_nm;
    // }
    // public void setR_fee_det_nm(String r_fee_det_nm) {
    //     this.r_fee_det_nm = r_fee_det_nm;
    // }
    // public BigDecimal getR_fee_amt() {
    //     return r_fee_amt;
    // }
    // public void setR_fee_amt(BigDecimal r_fee_amt) {
    //     this.r_fee_amt = r_fee_amt;
    // }
    // public String getR_ss_cd() {
    //     return r_ss_cd;
    // }
    // public void setR_ss_cd(String r_ss_cd) {
    //     this.r_ss_cd = r_ss_cd;
    // }
    // public Date getR_promo_startdt() {
    //     return r_promo_startdt;
    // }
    // public void setR_promo_startdt(Date r_promo_startdt) {
    //     this.r_promo_startdt = r_promo_startdt;
    // }
    // public Date getR_promo_enddt() {
    //     return r_promo_enddt;
    // }
    // public void setR_promo_enddt(Date r_promo_enddt) {
    //     this.r_promo_enddt = r_promo_enddt;
    // }
    // public Integer getR_ll_required() {
    //     return r_ll_required;
    // }
    // public void setR_ll_required(Integer r_ll_required) {
    //     this.r_ll_required = r_ll_required;
    // }
    // public String getR_add_notes() {
    //     return r_add_notes;
    // }
    // public void setR_add_notes(String r_add_notes) {
    //     this.r_add_notes = r_add_notes;
    // }
    // public String getMft_status() {
    //     return mft_status;
    // }
    // public void setMft_status(String mft_status) {
    //     this.mft_status = mft_status;
    // }
    // public BigDecimal getR_promo_fee() {
    //     return r_promo_fee;
    // }
    // public void setR_promo_fee(BigDecimal r_promo_fee) {
    //     this.r_promo_fee = r_promo_fee;
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
