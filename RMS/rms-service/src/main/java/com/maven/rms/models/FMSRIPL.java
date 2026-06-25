package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSRIPL {
    private BigInteger audit_id;
    private BigInteger ripl_id;
    private Date dt_txn;
    private BigDecimal accr_amt_af;
    private String action_type;
    
    // public BigInteger getAudit_id() {
    //     return audit_id;
    // }
    // public void setAudit_id(BigInteger audit_id) {
    //     this.audit_id = audit_id;
    // }
    // public BigInteger getRipl_id() {
    //     return ripl_id;
    // }
    // public void setRipl_id(BigInteger ripl_id) {
    //     this.ripl_id = ripl_id;
    // }
    // public Date getDt_txn() {
    //     return dt_txn;
    // }
    // public void setDt_txn(Date dt_txn) {
    //     this.dt_txn = dt_txn;
    // }
    // public BigDecimal getAccr_amt_af() {
    //     return accr_amt_af;
    // }
    // public void setAccr_amt_af(BigDecimal accr_amt_af) {
    //     this.accr_amt_af = accr_amt_af;
    // }
    // public String getAction_type() {
    //     return action_type;
    // }
    // public void setAction_type(String action_type) {
    //     this.action_type = action_type;
    // }
    
}
