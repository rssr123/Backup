package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MTTPGDetails {
    
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT)
    private Date pymt_submit_dt;
    private String pg_pymt_method;;
    private String pg_pymt_id;
    private String pg_pymt_desc;
    private BigDecimal pg_pymt_amt;
    private String pg_curr_cd ;
    private BigDecimal pg_tax_amt;
    private BigDecimal pg_b4tax_amt;
    private String pg_txn_id;
    private String pg_txn_status;

    // public Date getPymt_submit_dt() {
    //     return pymt_submit_dt;
    // }

    // public void setPymt_submit_dt(Date pymt_submit_dt) {
    //     this.pymt_submit_dt = pymt_submit_dt;
    // }

    // public String getPg_pymt_method() {
    //     return pg_pymt_method;
    // }

    // public void setPg_pymt_method(String pg_pymt_method) {
    //     this.pg_pymt_method = pg_pymt_method;
    // }

    // public String getPg_pymt_id() {
    //     return pg_pymt_id;
    // }

    // public void setPg_pymt_id(String pg_pymt_id) {
    //     this.pg_pymt_id = pg_pymt_id;
    // }

    // public String getPg_pymt_desc() {
    //     return pg_pymt_desc;
    // }

    // public void setPg_pymt_desc(String pg_pymt_desc) {
    //     this.pg_pymt_desc = pg_pymt_desc;
    // }
    
    // public BigDecimal getPg_pymt_amt() {
    //     return pg_pymt_amt;
    // }

    // public void setPg_pymt_amt(BigDecimal pg_pymt_amt) {
    //     this.pg_pymt_amt = pg_pymt_amt;
    // }

    // public String getPg_curr_cd() {
    //     return pg_curr_cd;
    // }

    // public void setPg_curr_cd(String pg_curr_cd) {
    //     this.pg_curr_cd = pg_curr_cd;
    // }

    // public BigDecimal getPg_tax_amt() {
    //     return pg_tax_amt;
    // }

    // public void setPg_tax_amt(BigDecimal pg_tax_amt) {
    //     this.pg_tax_amt = pg_tax_amt;
    // }
    // public BigDecimal getPg_b4tax_amt() {
    //     return pg_b4tax_amt;
    // }

    // public void setPg_b4tax_amt(BigDecimal pg_b4tax_amt) {
    //     this.pg_b4tax_amt = pg_b4tax_amt;
    // }

    // public String getPg_txn_id() {
    //     return pg_txn_id;
    // }

    // public void setPg_txn_id(String pg_txn_id) {
    //     this.pg_txn_id = pg_txn_id;
    // }

    // public String getPg_txn_status() {
    //     return pg_txn_status;
    // }

    // public void setPg_txn_status(String pg_txn_status) {
    //     this.pg_txn_status = pg_txn_status;
    // }
    
}
