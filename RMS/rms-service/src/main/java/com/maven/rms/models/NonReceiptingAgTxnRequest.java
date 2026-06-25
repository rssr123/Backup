package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonReceiptingAgTxnRequest {
    private Integer i_page;
    private Integer i_size;
    private Integer i_ag_sale_id;
    private String i_stmt_no;
    private String i_txn_ref;
    private String i_acct_no;
    private String i_brn_chn;
    private String i_txn_desc;
    private String i_credit;
    private String i_posting_date;
}
