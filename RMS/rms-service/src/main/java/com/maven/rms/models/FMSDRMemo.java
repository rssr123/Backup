package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSDRMemo {

    // sp_getfmsrcpgtxn
    private BigInteger rc_pg_id;
    private String pg_pymt_method;
    private BigInteger mtt_pg_id;
    private Integer qty;
    private String item_desc;
    private BigDecimal unit_fee;
    // private String entity_nm;
    // private String entity_no;
    // private String entity_type;
    private BigDecimal gross_amt;
    private String fee_detail_id;
    private BigDecimal tax_amt;
    private String rcpt_no;
    private String cust_nm;
    private BigDecimal pg_pymt_amt;

    // sp_getfmsdrmemo
    private BigInteger drmemo_hid;
    private String type;
    private String link_branch;
    private BigDecimal amt;
    private String cust;
    private String rms_batch_no;
    private Date dt_sent;
    private String desc;
    private String attr_ext_sys;
    private String fms_ref_no;
    private String doc_ty;
    private String acct;
    private String branch;
    private Integer qty_drmemo;
    private String sub_acct;
    private String txn_desc;
    private BigDecimal unit_price;
    // private String rcpt_no_drmemo;
    // private String payee_info;
    private String ent_nm;
    private String ent_no;
    private String ent_ty;
    // private BigDecimal item_amt;
    // private BigDecimal pymt;
    // private BigDecimal item_tax_amt;
    private String coa1;
    private String coa2;

    // 241010: Added 2 new fields
    private String depositID;
    private String depositTask;

    // 2025-04-22: Added new fields
    private Boolean hold;
    private Integer line_nbr;
    private BigDecimal discount_amt;

    // sp_updfmsdrmemo
    private String resp_attr_ext_sys;
    private String resp_co;
    private String resp_status;
    private String resp_msg;
    private Date resp_dt;

    private BigInteger rc_pgtxn_id;
    
    private String acct_nm;
    private Integer genPdf;
    private Integer bil_child_id;

    private String h_fms_ref_no;

    private BigInteger crmemo_hid;
}
