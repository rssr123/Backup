package com.maven.rms.models;

import java.util.Date;

import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSLedgerDocWithoutFile {

    private String i_fee_detail_id;
    private String i_fee_detail_nm_en;
    private String i_fms_ledger_cd;
    private String i_createdBy;
    private String i_modifiedBy;
    private String file_nm;
    private String file_type;
    private Integer file_size_kb;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private Integer total;

    // public String getFile_nm() {
    //     return file_nm;
    // }

    // public void setFile_nm(String file_nm) {
    //     this.file_nm = file_nm;
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

    // public String getI_fee_detail_id() {
    //     return i_fee_detail_id;
    // }

    // public void setI_fee_detail_id(String i_fee_detail_id) {
    //     this.i_fee_detail_id = i_fee_detail_id;
    // }

    // public String getI_fee_detail_nm_en() {
    //     return i_fee_detail_nm_en;
    // }

    // public void setI_fee_detail_nm_en(String i_fee_detail_nm_en) {
    //     this.i_fee_detail_nm_en = i_fee_detail_nm_en;
    // }

    // public String getI_fms_ledger_cd() {
    //     return i_fms_ledger_cd;
    // }

    // public void setI_fms_ledger_cd(String i_fms_ledger_cd) {
    //     this.i_fms_ledger_cd = i_fms_ledger_cd;
    // }

    // public String getI_createdBy() {
    //     return i_createdBy;
    // }

    // public void setI_createdBy(String i_createdBy) {
    //     this.i_createdBy = i_createdBy;
    // }

    // public String getI_modifiedBy() {
    //     return i_modifiedBy;
    // }

    // public void setI_modifiedBy(String i_modifiedBy) {
    //     this.i_modifiedBy = i_modifiedBy;
    // }

}
