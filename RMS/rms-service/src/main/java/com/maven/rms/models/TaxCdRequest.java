package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaxCdRequest {
    private Integer i_page;
    private Integer i_size;
    private Long i_tax_cd_id;
    private String i_tax_cd;
    private String i_tax_cd_nm_en;
    private String i_tax_cd_nm_bm;
    private String i_created_by;
    private String i_modified_by;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date i_dt_modified_fr;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Date i_dt_modified_to;
    private String i_status;
    private BigDecimal i_tax_pct;

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

    // public Long getI_tax_cd_id() {
    //     return i_tax_cd_id;
    // }

    // public void setI_tax_cd_id(Long i_tax_cd_id) {
    //     this.i_tax_cd_id = i_tax_cd_id;
    // }

    // public String getI_tax_cd() {
    //     return i_tax_cd;
    // }

    // public void setI_tax_cd(String i_tax_cd) {
    //     this.i_tax_cd = i_tax_cd;
    // }

    // public String getI_tax_cd_nm_en() {
    //     return i_tax_cd_nm_en;
    // }

    // public void setI_tax_cd_nm_en(String i_tax_cd_nm_en) {
    //     this.i_tax_cd_nm_en = i_tax_cd_nm_en;
    // }

    // public String getI_tax_cd_nm_bm() {
    //     return i_tax_cd_nm_bm;
    // }

    // public void setI_tax_cd_nm_bm(String i_tax_cd_nm_bm) {
    //     this.i_tax_cd_nm_bm = i_tax_cd_nm_bm;
    // }

    // public String getI_created_by() {
    //     return i_created_by;
    // }

    // public void setI_created_by(String i_created_by) {
    //     this.i_created_by = i_created_by;
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

    // public BigDecimal getI_tax_pct() {
    //     return i_tax_pct;
    // }

    // public void setI_tax_pct(BigDecimal i_tax_pct) {
    //     this.i_tax_pct = i_tax_pct;
    // }
}
