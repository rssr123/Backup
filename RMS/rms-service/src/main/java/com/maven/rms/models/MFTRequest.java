package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MFTRequest {

    private Integer i_page;
    private Integer i_size;
    private Integer i_fee_detail_pk;
    private String i_fee_detail_id;
    private Integer i_fee_grp_id;
    private BigDecimal i_unit_fee_fr;
    private BigDecimal i_unit_fee_to;
    private String i_ss_cd;
    private String i_tax_cd;
    private Date i_dt_modified_fr;
    private Date i_dt_modified_to;
    private String i_modified_by;
    private String i_status;
    
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
    // public Integer getI_fee_detail_pk() {
    //     return i_fee_detail_pk;
    // }
    // public void setI_fee_detail_pk(Integer i_fee_detail_pk) {
    //     this.i_fee_detail_pk = i_fee_detail_pk;
    // }
    // public String getI_fee_detail_id() {
    //     return i_fee_detail_id;
    // }
    // public void setI_fee_detail_id(String i_fee_detail_id) {
    //     this.i_fee_detail_id = i_fee_detail_id;
    // }
    // public Integer getI_fee_grp_id() {
    //     return i_fee_grp_id;
    // }
    // public void setI_fee_grp_id(Integer i_fee_grp_id) {
    //     this.i_fee_grp_id = i_fee_grp_id;
    // }
    // public BigDecimal getI_unit_fee_fr() {
    //     return i_unit_fee_fr;
    // }
    // public void setI_unit_fee_fr(BigDecimal i_unit_fee_fr) {
    //     this.i_unit_fee_fr = i_unit_fee_fr;
    // }
    // public BigDecimal getI_unit_fee_to() {
    //     return i_unit_fee_to;
    // }
    // public void setI_unit_fee_to(BigDecimal i_unit_fee_to) {
    //     this.i_unit_fee_to = i_unit_fee_to;
    // }
    // public String getI_ss_cd() {
    //     return i_ss_cd;
    // }
    // public void setI_ss_cd(String i_ss_cd) {
    //     this.i_ss_cd = i_ss_cd;
    // }
    // public String getI_tax_cd() {
    //     return i_tax_cd;
    // }
    // public void setI_tax_cd(String i_tax_cd) {
    //     this.i_tax_cd = i_tax_cd;
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
    // public String getI_modified_by() {
    //     return i_modified_by;
    // }
    // public void setI_modified_by(String i_modified_by) {
    //     this.i_modified_by = i_modified_by;
    // }
    // public String getI_status() {
    //     return i_status;
    // }
    // public void setI_status(String i_status) {
    //     this.i_status = i_status;
    // }
   
}
