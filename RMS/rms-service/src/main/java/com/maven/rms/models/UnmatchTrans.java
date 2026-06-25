package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_unmatch_txn")
public class UnmatchTrans {
    @Id
    private String period;
    private BigDecimal in;
    private BigDecimal out;
    private BigDecimal variance;
    private String periodbalance;
    private Date dummydate;

    

    // public String getPeriod() {
    //     return period;
    // }

    // public void setPeriod(String period) {
    //     this.period = period;
    // }

    // public BigDecimal getIn() {
    //     return in;
    // }

    // public void setIn(BigDecimal in) {
    //     this.in = in;
    // }

    // public BigDecimal getOut() {
    //     return out;
    // }

    // public void setOut(BigDecimal out) {
    //     this.out = out;
    // }

    // public BigDecimal getVariance() {
    //     return variance;
    // }

    // public void setVariance(BigDecimal variance) {
    //     this.variance = variance;
    // }

    // public String getPeriodbalance() {
    //     return periodbalance;
    // }

    // public void setPeriodbalance(String periodbalance) {
    //     this.periodbalance = periodbalance;
    // }

    // public Date getDummydate() {
    //     return dummydate;
    // }

    // public void setDummydate(Date dummydate) {
    //     this.dummydate = dummydate;
    // }

}
