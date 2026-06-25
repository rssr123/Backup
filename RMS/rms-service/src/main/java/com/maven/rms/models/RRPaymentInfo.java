package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// @Entity(name = "rms_otc")
public class RRPaymentInfo {



    private String mtt_id;
    private String otc_id;
    private String payer_email;
    private String otc_pymt_mode;
    private BigDecimal cash_amt;
    private BigDecimal cash_amt_total;
    private String otc_che_id;
    private String che_status;
    private String che_bank_nm;
    private String che_no;
    private Date che_date;
    private String che_ba_acct_no;
    private BigDecimal che_amt;
    private BigDecimal che_amt_total;


}
