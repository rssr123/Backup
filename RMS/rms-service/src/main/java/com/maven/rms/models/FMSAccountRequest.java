package com.maven.rms.models;

import java.sql.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSAccountRequest {
    private Integer i_page;
    private Integer i_size;
    private Integer i_fms_acct_id;
    private String i_acct_nm;
    private String i_acct_type;
    private String i_acct_cd;
    private String i_created_by;
    private Date i_dt_created;
    private String i_modified_by;
    private Date i_dt_modified;
    private String i_status;
    private Date i_dt_modified_fr; // Start date for range
    private Date i_dt_modified_to; // End date for range
   
    // public Date getI_dt_modified_fr() {
    //     return i_dt_modified_fr;
    // }
    // public void setI_dt_modified_fr(Date i_dt_modified_fr) {
    //     this.i_dt_modified_fr = i_dt_modified_fr;
    // }
    // public Date getI_dt_modified_to() {
    //     return i_dt_modified_to;
    // }
    // public void setI_dt_modified_to(Date i_dt_modified_to) {
    //     this.i_dt_modified_to = i_dt_modified_to;
    // }
    // public Integer getI_fms_acct_id() {
    //     return i_fms_acct_id;
    // }
    // public void setI_fms_acct_id(Integer i_fms_acct_id) {
    //     this.i_fms_acct_id = i_fms_acct_id;
    // }
    // public String getI_created_by() {
    //     return i_created_by;
    // }
    // public void setI_created_by(String i_created_by) {
    //     this.i_created_by = i_created_by;
    // }
    // public Date getI_dt_created() {
    //     return i_dt_created;
    // }
    // public void setI_dt_created(Date i_dt_created) {
    //     this.i_dt_created = i_dt_created;
    // }
    // public String getI_status() {
    //     return i_status;
    // }
    // public void setI_status(String i_status) {
    //     this.i_status = i_status;
    // }
    // public Integer getI_page() {
    //     return i_page;
    // }
    // public void setI_page(Integer i_page) {
    //     this.i_page = i_page;
    // }
    // public Integer getI_size() {
    //     return i_size;
    // }
    // public void setI_size(Integer i_size) {
    //     this.i_size = i_size;
    // }
    // public String getI_acct_nm() {
    //     return i_acct_nm;
    // }
    // public void setI_acct_nm(String i_acct_nm) {
    //     this.i_acct_nm = i_acct_nm;
    // }
    // public String getI_acct_type() {
    //     return i_acct_type;
    // }
    // public void setI_acct_type(String i_acct_type) {
    //     this.i_acct_type = i_acct_type;
    // }
    // public String getI_acct_cd() {
    //     return i_acct_cd;
    // }
    // public void setI_acct_cd(String i_acct_cd) {
    //     this.i_acct_cd = i_acct_cd;
    // }
    // public String getI_modified_by() {
    //     return i_modified_by;
    // }
    // public void setI_modified_by(String i_modified_by) {
    //     this.i_modified_by = i_modified_by;
    // }
    // public Date getI_dt_modified() {
    //     return i_dt_modified;
    // }
    // public void setI_dt_modified(Date i_dt_modified) {
    //     this.i_dt_modified = i_dt_modified;
    // }
    
}
