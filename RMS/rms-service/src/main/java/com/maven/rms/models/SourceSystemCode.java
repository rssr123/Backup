package com.maven.rms.models;

import java.util.Date;
import java.math.BigInteger;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_ss")
public class SourceSystemCode {
    

    @Id
    private BigInteger ss_id;
    private String ss_cd;
    private String ss_nm;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy HH:mm:ss")
    private Date dt_modified;
    private String modified_by;
    private String status;
    private String status_en;
    private String status_bm;
    private Integer total;
    
    // public BigInteger getSs_id() {
    //     return ss_id;
    // }
    // public void setSs_id(BigInteger ss_id) {
    //     this.ss_id = ss_id;
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

  
    

    
    
    

    

}
