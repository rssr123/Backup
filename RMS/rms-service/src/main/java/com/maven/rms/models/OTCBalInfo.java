package com.maven.rms.models;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCBalInfo {
    
    private String branch_cd;
    private Date dt_bal;
    private String bal_type;
    private Integer no_of_counters;
    private Integer no_of_txn;
    private BigDecimal total;
    private BigDecimal total_emv;
    private BigDecimal total_phy;
    private BigDecimal total_cash;
    private BigDecimal total_che;
    private BigDecimal total_bd;
    private BigDecimal total_mo;
    private Integer no_of_rcpt_can;

    private String status;

}
