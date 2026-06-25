package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCBankInSlip {
    //Info
    private String BankInSlipNo;
    private String branch_cd;
    private Date dt_bal;
    private String completed_by;
    private Date dt_completed;
    private BigDecimal total;
    private BigDecimal gtotal_cash;
    private Integer no_che;
    private BigDecimal gtotal_che;
    private Integer no_bd;
    private BigDecimal gtotal_bd;
    private Integer no_mo;
    private BigDecimal gtotal_mo;

    //Cash
    private String param_cd;
    private String denomination;
    private Integer quantity;
    private BigDecimal amount;
    private BigDecimal total_cash;

    //Physical
    private String counter_id;
    
    private BigDecimal total_cash_amt;
    private String col_slip_no;
    private String orn_no;
    private String che_bank_nm;
    private String che_payer_nm;
    private String che_ba_acct_no;
    private String che_no;
    private Date che_date;
    private BigDecimal che_amt;
    private String bd_bank_nm;
    private String bd_no;
    private Date bd_date;
    private BigDecimal bd_amt;
    private String mo_rm_no;
    private Date mo_date;
    private String mo_payer_nm;
    private String mo_id_no;
    private String mo_contact_no;
    private BigDecimal mo_amt;
    private String detail_type;

    private BigInteger id;
    private BigInteger otc_id;

    private BigInteger otc_counter_id;
}
