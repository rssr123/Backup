package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_tax_code")
public class TaxCd {

    @Id
    private String tax_cd;
    private BigInteger tax_cd_id;
    private String tax_cd_nm_en;
    private String tax_cd_nm_bm;
    private BigDecimal tax_pct;
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd MMM yyyy HH:mm:ss")
    private Date dtModified;
    private String modifiedBy;
    private String status;
    private String status_en;
    private String status_bm;
    private Integer total;

    // public String getTax_cd() {
    //     return tax_cd;
    // }

    // public void setTax_cd(String tax_cd) {
    //     this.tax_cd = tax_cd;
    // }

    // public BigInteger getTax_cd_id() {
    //     return tax_cd_id;
    // }

    // public void setTax_cd_id(BigInteger tax_cd_id) {
    //     this.tax_cd_id = tax_cd_id;
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

    // public Date getDtModified() {
    //     return dtModified;
    // }

    // public void setDtModified(Date dtModified) {
    //     this.dtModified = dtModified;
    // }

    // public String getModifiedBy() {
    //     return modifiedBy;
    // }

    // public void setModifiedBy(String modifiedBy) {
    //     this.modifiedBy = modifiedBy;
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
