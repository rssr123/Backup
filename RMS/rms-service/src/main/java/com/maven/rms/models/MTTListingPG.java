package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MTTListingPG {

    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT)
    private Date pymt_submit_dt;
    private String pg_pymt_id; 
    private BigDecimal pg_pymt_amt;
    private String pg_txn_status;
    private Integer mtt_pg_id;

    // public Integer getMtt_pg_id() {
    //     return mtt_pg_id;
    // }
    // public void setMtt_pg_id(Integer mtt_pg_id) {
    //     this.mtt_pg_id = mtt_pg_id;
    // }
    // public Date getPymt_submit_dt() {
    //     return pymt_submit_dt;
    // }
    // public void setPymt_submit_dt(Date pymt_submit_dt) {
    //     this.pymt_submit_dt = pymt_submit_dt;
    // }
    // public String getPg_pymt_id() {
    //     return pg_pymt_id;
    // }
    // public void setPg_pymt_id(String pg_pymt_id) {
    //     this.pg_pymt_id = pg_pymt_id;
    // }
    // public BigDecimal getPg_pymt_amt() {
    //     return pg_pymt_amt;
    // }
    // public void setPg_pymt_amt(BigDecimal pg_pymt_amt) {
    //     this.pg_pymt_amt = pg_pymt_amt;
    // }
    // public String getPg_txn_status() {
    //     return pg_txn_status;
    // }
    // public void setPg_txn_status(String pg_txn_status) {
    //     this.pg_txn_status = pg_txn_status;
    // }

}
