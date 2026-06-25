package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgBankTxn {
    private Integer i_ag_sale_id;
    private Integer i_ag_doc_id;
    private String i_acct_no;
    private String i_acct_type;
    private String i_acct_nm;
    private Date i_dt_fr;
    private Date i_dt_to;
    private BigDecimal i_total_debit;
    private BigDecimal i_total_credit;
    private String i_begin_bal;
    private String i_end_bal;
    private String i_dt_txn;
    private String i_dt_posting;
    private String i_txn_desc;
    private String i_txn_ref;
    private String i_debit;
    private String i_credit;
    private String i_source_cd;
    private String i_teller_id;
    private String i_brn_chn;
    private String i_txn_cd;
    private String i_end_bal2;
    private String i_virtual_acct;
    private String i_txn_desc2;
    private String i_txn_desc3;
    private String i_txn_desc4;
    private Date i_dt_expiry;
    
}
