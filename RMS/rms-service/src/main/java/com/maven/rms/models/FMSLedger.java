package com.maven.rms.models;

import java.math.BigInteger;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rms_fms_ledger")
public class FMSLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private BigInteger fms_ledger_id;
    private String fms_detail_id;
    private String fms_detail_nm_en;
    private String fms_ledger_cd;
    private String fms_cd;
    private Integer total;
    private String found;
    private Integer mft_total;

    // public Integer getMft_total() {
    //     return mft_total;
    // }

    // public void setMft_total(Integer mft_total) {
    //     this.mft_total = mft_total;
    // }

    // public String getFound() {
    //     return found;
    // }

    // public void setFound(String found) {
    //     this.found = found;
    // }

    // public BigInteger getFms_ledger_id() {
    //     return fms_ledger_id;
    // }

    // public void setFms_ledger_id(BigInteger fms_ledger_id) {
    //     this.fms_ledger_id = fms_ledger_id;
    // }

    // public String getFms_detail_id() {
    //     return fms_detail_id;
    // }

    // public void setFms_detail_id(String fms_detail_id) {
    //     this.fms_detail_id = fms_detail_id;
    // }

    // public String getFms_detail_nm_en() {
    //     return fms_detail_nm_en;
    // }

    // public void setFms_detail_nm_en(String fms_detail_nm_en) {
    //     this.fms_detail_nm_en = fms_detail_nm_en;
    // }

    // public String getFms_ledger_cd() {
    //     return fms_ledger_cd;
    // }

    // public void setFms_ledger_cd(String fms_ledger_cd) {
    //     this.fms_ledger_cd = fms_ledger_cd;
    // }

    // public String getFms_cd() {
    //     return fms_cd;
    // }

    // public void setFms_cd(String fms_cd) {
    //     this.fms_cd = fms_cd;
    // }

    // public Integer getTotal() {
    //     return total;
    // }

    // public void setTotal(Integer total) {
    //     this.total = total;
    // }

}
