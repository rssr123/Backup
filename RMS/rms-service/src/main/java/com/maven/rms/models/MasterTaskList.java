package com.maven.rms.models;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MasterTaskList {
    
    @Id
    private Integer wf_id;
    private Integer fee_detail_pk;
    private String fee_detail_nm_e;
    private String fee_detail_nm_b;
    private Date effective_date;
    private Date dt_modified;
    private String modified_by;
    private String status;
    private String assign_to;
    private Integer total;
    // public Integer getWf_id() {
    //     return wf_id;
    // }
    // public void setWf_id(Integer wf_id) {
    //     this.wf_id = wf_id;
    // }
    // public Integer getFee_detail_pk() {
    //     return fee_detail_pk;
    // }
    // public void setFee_detail_pk(Integer fee_detail_pk) {
    //     this.fee_detail_pk = fee_detail_pk;
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
    // public Date getEffective_date() {
    //     return effective_date;
    // }
    // public void setEffective_date(Date effective_date) {
    //     this.effective_date = effective_date;
    // }
    // public Date getDt_modified() {
    //     return dt_modified;
    // }
    // public void setDt_modified(Date dt_modified) {
    //     this.dt_modified = dt_modified;
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
    // public String getAssign_to() {
    //     return assign_to;
    // }
    // public void setAssign_to(String assign_to) {
    //     this.assign_to = assign_to;
    // }
    // public Integer getTotal() {
    //     return total;
    // }
    // public void setTotal(Integer total) {
    //     this.total = total;
    // }
    
   
  

    

}






