package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
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
@Entity(name = "rms_mft_wfdoc")
public class MFTWFDoc {
    

    @Id
     private BigInteger wfdoc_id;
     private String file_nm;
     private String file_content;
     private String file_type;
     private Integer file_size_kb;
     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
     private Date dt_created;
     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
     private Date dt_modified;
     private String created_by;
     private String modified_by;
     private Integer total;
    // public BigInteger getWfdoc_id() {
    //     return wfdoc_id;
    // }
    // public void setWfdoc_id(BigInteger wfdoc_id) {
    //     this.wfdoc_id = wfdoc_id;
    // }
    // public String getFile_nm() {
    //     return file_nm;
    // }
    // public void setFile_nm(String file_nm) {
    //     this.file_nm = file_nm;
    // }
    // public String getFile_content() {
    //     return file_content;
    // }
    // public void setFile_content(String file_content) {
    //     this.file_content = file_content;
    // }
    // public String getFile_type() {
    //     return file_type;
    // }
    // public void setFile_type(String file_type) {
    //     this.file_type = file_type;
    // }
    // public Integer getFile_size_kb() {
    //     return file_size_kb;
    // }
    // public void setFile_size_kb(Integer file_size_kb) {
    //     this.file_size_kb = file_size_kb;
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
    // public Integer getTotal() {
    //     return total;
    // }
    // public void setTotal(Integer total) {
    //     this.total = total;
    // }
   
   
   
    
}
