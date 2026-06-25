package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCBalancingRequest {
    //Common Request
    private BigInteger id;
    private BigInteger otc_id;
    private String ssm4uuserrefno;
    private String detail_type;
    private String n_detail_type;
    
    //Cash Request
    private String param_cd;
    private Integer quantity;

    //Cheque Request
    private BigDecimal che_amt;
    private Date che_date;
    private String che_bank_nm;
    private String che_payer_nm;
    private String che_ba_acct_no;
    private String che_no;

    //Bank Draft Request
    private BigDecimal bd_amt;
    private Date bd_date;
    private String bd_no;
    private String bd_bank_nm;

    //Money Order Request
    private BigDecimal mo_amt;
    private Date mo_date;
    private String mo_rm_no;
    private String mo_payer_nm;
    private String mo_id_no;
    private String mo_contact_no;

    //Balancing Status
    private String counter_id;
    private String bal_status;
    private String bal_type;
    private BigDecimal total_emv_amt;
    private BigDecimal total_phy_amt;
    private BigDecimal total_collection;

    //Daily Balancing Status
    private String branch_code;
    private Date bal_date;

    private Integer i_page;
    private Integer i_size;
}
