package com.maven.rms.models;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeeGrpRequest {
    private Integer i_page;
    private Integer i_size;
    private Long i_fee_grp_id;
    private String i_ss_cd;
    private Integer i_ss_fee_grp_id;
    private String i_fee_grp_nm_en;
    private String i_fee_grp_nm_bm;
    private String i_modified_by;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date i_dt_modified_fr;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date i_dt_modified_to;
    private String i_status;
    private String i_created_by;

    // public String getI_created_by() {
    //     return i_created_by;
    // }

    // public void setI_created_by(String i_created_by) {
    //     this.i_created_by = i_created_by;
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

    // public Long getI_fee_grp_id() {
    //     return i_fee_grp_id;
    // }

    // public void setI_fee_grp_id(Long i_fee_grp_id) {
    //     this.i_fee_grp_id = i_fee_grp_id;
    // }

    // public String getI_fee_grp_nm_en() {
    //     return i_fee_grp_nm_en;
    // }

    // public void setI_fee_grp_nm_en(String i_fee_grp_nm_en) {
    //     this.i_fee_grp_nm_en = i_fee_grp_nm_en;
    // }

    // public String getI_fee_grp_nm_bm() {
    //     return i_fee_grp_nm_bm;
    // }

    // public void setI_fee_grp_nm_bm(String i_fee_grp_nm_bm) {
    //     this.i_fee_grp_nm_bm = i_fee_grp_nm_bm;
    // }

    // public String getI_modified_by() {
    //     return i_modified_by;
    // }

    // public void setI_modified_by(String i_modified_by) {
    //     this.i_modified_by = i_modified_by;
    // }

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

    // public String getI_status() {
    //     return i_status;
    // }

    // public void setI_status(String i_status) {
    //     this.i_status = i_status;
    // }

}
