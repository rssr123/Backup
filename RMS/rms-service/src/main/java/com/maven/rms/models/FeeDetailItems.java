package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import com.maven.rms.config.Constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeeDetailItems {
    

    @Id
    private String fee_detail_id;
    private Integer fee_grp_id;
    private String fee_detail_nm_en;
    private String fee_detail_nm_bm;
    private BigDecimal unit_fee;
     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT, timezone = "Asia/Singapore")
    private Date promo_startdt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT, timezone = "Asia/Singapore")
    private Date promo_enddt;
    private BigDecimal promo_fee;
    private String tax_cd;
    private Integer allow_otc;
    private String ll_parent_id;
    private Integer ll_start_day;
    private Integer ll_start_mth;
    private Integer ll_end_day;
    private Integer ll_end_mth;
    private String ledger_cd;
    private String ss_cd;
    private String fee_grp_nm_en;
    private String fee_grp_nm_bm;
    private String tax_cd_nm_en;
    private String tax_cd_nm_bm; 
    private BigDecimal tax_pct;
    private String status;
    
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
    // public String getFee_detail_nm_en() {
    //     return fee_detail_nm_en;
    // }
    // public void setFee_detail_nm_en(String fee_detail_nm_en) {
    //     this.fee_detail_nm_en = fee_detail_nm_en;
    // }
    // public String getFee_detail_nm_bm() {
    //     return fee_detail_nm_bm;
    // }
    // public void setFee_detail_nm_bm(String fee_detail_nm_bm) {
    //     this.fee_detail_nm_bm = fee_detail_nm_bm;
    // }
    // public BigDecimal getUnit_fee() {
    //     return unit_fee;
    // }
    // public void setUnit_fee(BigDecimal unit_fee) {
    //     this.unit_fee = unit_fee;
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
    // public String getTax_cd_nm_en() {
    //     return tax_cd_nm_en;
    // }
    // public void setTax_cd_nm_en(String tax_cd_nm_en) {
    //     this.tax_cd_nm_en = tax_cd_nm_en;
    // }
    // public String getTax_cd_nm_bm() {
    //     return tax_cd_nm_bm;
    // }
    // public void setTax_cd_nm_bm(String tax_cd_nm_bm) {
    //     this.tax_cd_nm_bm = tax_cd_nm_bm;
    // }
    // public BigDecimal getTax_pct() {
    //     return tax_pct;
    // }
    // public void setTax_pct(BigDecimal tax_pct) {
    //     this.tax_pct = tax_pct;
    // }
    // public String getStatus() {
    //     return status;
    // }
    // public void setStatus(String status) {
    //     this.status = status;
    // }
 



}
