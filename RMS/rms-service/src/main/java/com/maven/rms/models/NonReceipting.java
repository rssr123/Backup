package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonReceipting {
    private Integer ag_sale_id;
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
    private Integer total_trx_no;
    private Integer batch_size;
    private Integer batch_cnt;
    private String task_id;
    private String task_status;
    private Date dt_created;
    private Date dt_modified;
    private String status;
    private Date dt_upload;
    private String settle_status;
    private String remarks;
    private Integer total;

    private BigDecimal discrepancy_amt;
}
