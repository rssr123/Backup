package com.maven.rms.models;

import java.math.BigDecimal;
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
@Entity(name = "rms_mft_wfhist")
public class MFTWFHistory {
    
    @Id
    //private Integer wf_id;

    private String action;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_activity;
    private String act_by;
    private String assign_to;
    private String remark;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String status_en;
    private String status_bm;
    private Integer total;
    private String assign_to_nm;
    private String status;
    private String act_by_nm;
    // public String getAction() {
    //     return action;
    // }
    // public void setAction(String action) {
    //     this.action = action;
    // }
    // public Date getDt_activity() {
    //     return dt_activity;
    // }
    // public void setDt_activity(Date dt_activity) {
    //     this.dt_activity = dt_activity;
    // }
    // public String getAct_by() {
    //     return act_by;
    // }
    // public void setAct_by(String act_by) {
    //     this.act_by = act_by;
    // }
    // public String getAssign_to() {
    //     return assign_to;
    // }
    // public void setAssign_to(String assign_to) {
    //     this.assign_to = assign_to;
    // }
    // public String getRemark() {
    //     return remark;
    // }
    // public void setRemark(String remark) {
    //     this.remark = remark;
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
    // public Integer getTotal() {
    //     return total;
    // }
    // public void setTotal(Integer total) {
    //     this.total = total;
    // }
    // public String getAssign_to_nm() {
    //     return assign_to_nm;
    // }
    // public void setAssign_to_nm(String assign_to_nm) {
    //     this.assign_to_nm = assign_to_nm;
    // }
    // public String getStatus() {
    //     return status;
    // }
    // public void setStatus(String status) {
    //     this.status = status;
    // }

    
   
    
    
   
    
   
    

   


}

