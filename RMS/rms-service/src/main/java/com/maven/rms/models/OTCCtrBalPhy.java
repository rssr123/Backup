package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCCtrBalPhy {
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