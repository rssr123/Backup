package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PGReconExcelFile {
    
    private BigInteger rc_pg_id;
    private String dt_txn;
    private String txn_id;
    private String txn_type;
    private String txn_cd;
    private BigDecimal txn_amt;
    private BigDecimal mdr_amt;
    private BigDecimal sst_amt;
    private BigDecimal net_amt;

    // getters and setters
    // public BigInteger getRc_pg_id() {
    //     return rc_pg_id;
    // }

    // public void setRc_pg_id(BigInteger rc_pg_id) {
    //     this.rc_pg_id = rc_pg_id;
    // }

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
}