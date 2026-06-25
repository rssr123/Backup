package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSDeferredIncome {
    private BigInteger audit_id;
    private BigInteger di_id;
    private Date dt_txn;
    private BigDecimal bal_di_amt_af;
    private BigDecimal unit_fee;
    private String action_type;

    // public BigInteger getAudit_id() {
    //     return audit_id;
    // }
    // public void setAudit_id(BigInteger audit_id) {
    //     this.audit_id = audit_id;
    // }
    // public BigInteger getDi_id() {
    //     return di_id;
    // }
    // public void setDi_id(BigInteger di_id) {
    //     this.di_id = di_id;
    // }
    // public Date getDt_txn() {
    //     return dt_txn;
    // }
    // public void setDt_txn(Date dt_txn) {
    //     this.dt_txn = dt_txn;
    // }
    // public BigDecimal getBal_di_amt_af() {
    //     return bal_di_amt_af;
    // }
    // public void setBal_di_amt_af(BigDecimal bal_di_amt_af) {
    //     this.bal_di_amt_af = bal_di_amt_af;
    // }
    // public BigDecimal getUnit_fee() {
    //     return unit_fee;
    // }
    // public void setUnit_fee(BigDecimal unit_fee) {
    //     this.unit_fee = unit_fee;
    // }
    // public String getAction_type() {
    //     return action_type;
    // }
    // public void setAction_type(String action_type) {
    //     this.action_type = action_type;
    // }
}
