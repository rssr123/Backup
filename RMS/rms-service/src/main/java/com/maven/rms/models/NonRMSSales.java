package com.maven.rms.models;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonRMSSales {
    private String ss_cd;
    private String cn_cust_id;
    private String dn_cust_id;
    private String cash_acct;
    private String merchant_id;
    private String stmt_no;
    private String fms_ari_ref_no;
    private BigDecimal ari_total_amt;
    private BigDecimal mdr_total_amt;
    private BigDecimal total_net_amt;
    private Date dt_settlement;
    private int total_trx_no;
    private int batch_size;
    private int batch_cnt;
}
