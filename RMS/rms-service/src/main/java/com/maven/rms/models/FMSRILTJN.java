package com.maven.rms.models;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSRILTJN {

    private Integer rilt_a_id;
    private Integer rilt_id;
    private BigDecimal lit_amt_bf;
    private BigDecimal lit_amt_af;
    private Date dt_txn;
    private String status;

    private Integer i_rilt_a_id;
    private Integer i_rilt_id;
    private BigDecimal i_lit_amt_bf;
    private BigDecimal i_lit_amt_af;
    private Date i_dt_txn;
    private String i_status;

}