package com.maven.rms.models;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PGDetailListingResponse {
    private String dt_txn;
    private String txn_id;
    private String txn_type;
    private String txn_cd;
    private String found_in_rms;
    private String sub_criteria;
    private BigDecimal txn_amt;
    private BigDecimal mdr_amt;
    private BigDecimal sst_amt;
    private BigDecimal net_amt;
    private Integer total;
    
    // public String getDt_txn() {
    //     return dt_txn;
    // }
    // public void setDt_txn(String dt_txn) {
    //     this.dt_txn = dt_txn;
    // }
    // public String getTxn_id() {
    //     return txn_id;
    // }
    // public void setTxn_id(String txn_id) {
    //     this.txn_id = txn_id;
    // }
    // public String getTxn_type() {
    //     return txn_type;
    // }
    // public void setTxn_type(String txn_type) {
    //     this.txn_type = txn_type;
    // }
    // public String getTxn_cd() {
    //     return txn_cd;
    // }
    // public void setTxn_cd(String txn_cd) {
    //     this.txn_cd = txn_cd;
    // }
    // public String getFound_in_rms() {
    //     return found_in_rms;
    // }
    // public void setFound_in_rms(String found_in_rms) {
    //     this.found_in_rms = found_in_rms;
    // }
    // public String getSub_criteria() {
    //     return sub_criteria;
    // }
    // public void setSub_criteria(String sub_criteria) {
    //     this.sub_criteria = sub_criteria;
    // }
    // public BigDecimal getTxn_amt() {
    //     return txn_amt;
    // }
    // public void setTxn_amt(BigDecimal txn_amt) {
    //     this.txn_amt = txn_amt;
    // }
    // public BigDecimal getMdr_amt() {
    //     return mdr_amt;
    // }
    // public void setMdr_amt(BigDecimal mdr_amt) {
    //     this.mdr_amt = mdr_amt;
    // }
    // public BigDecimal getSst_amt() {
    //     return sst_amt;
    // }
    // public void setSst_amt(BigDecimal sst_amt) {
    //     this.sst_amt = sst_amt;
    // }
    // public BigDecimal getNet_amt() {
    //     return net_amt;
    // }
    // public void setNet_amt(BigDecimal net_amt) {
    //     this.net_amt = net_amt;
    // }
    // public Integer getTotal() {
    //     return total;
    // }
    // public void setTotal(Integer total) {
    //     this.total = total;
    // }
}
