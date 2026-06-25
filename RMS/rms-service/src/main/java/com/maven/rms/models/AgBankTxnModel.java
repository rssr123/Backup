package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgBankTxnModel {
    private String acct_no;
    private String acct_type;
    private String acct_nm;
    private Date dt_fr;
    private Date dt_to;
    private Integer total_debit;
    private Integer total_credit;
    private BigDecimal begin_bal;
    private BigDecimal end_bal;
    private Date dt_txn;
    private Date dt_posting;
    private String txn_desc;
    private String txn_ref;
    private BigDecimal debit;
    private BigDecimal credit;
    private String source_cd;
    private String teller_id;
    private String brn_chn;
    private String txn_cd;
    private BigDecimal end_bal2;
    private String virtual_acct;
    private String txn_desc2;
    private String txn_desc3;
    private String txn_desc4;
    private Date dt_expiry;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    private Integer total;
}
