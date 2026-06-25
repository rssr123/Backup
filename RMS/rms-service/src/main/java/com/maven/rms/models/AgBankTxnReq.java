package com.maven.rms.models;


import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgBankTxnReq {
    private Integer i_ag_sale_id;
    private String i_stmt_no;
    private Integer i_ag_doc_id;
    private String i_acct_no;
    private String i_acct_type;
    private String i_acct_nm;
    private String i_dt_fr;
    private String i_dt_to;
    private String i_total_debit;
    private String i_total_credit;
    private String i_begin_bal;
    private String i_end_bal;
    private String i_dt_txn;
    private String i_dt_posting;
    private String i_time_posting;
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
    private String i_dt_expiry;
    private String i_time_txn;

    private Integer i_page;
    private Integer i_size;
    private String i_file_nm;
    private String i_created_by;
    private String i_dt_created;

    private String i_cash_acct;
    private String i_cn_cust_id;
    private String i_dn_cust_id;
    private String i_fms_ari_ref_no;
    private BigDecimal i_ari_total_amt;
    private BigDecimal i_mdr_total_amt;
    private BigDecimal i_discrepancy_amt;
    private Integer i_is_first;
    private Integer i_arr_hid;
    private BigDecimal i_header_amt;
    private Integer i_cndn_hid;
    private BigDecimal i_total_net_amt;
    
}
