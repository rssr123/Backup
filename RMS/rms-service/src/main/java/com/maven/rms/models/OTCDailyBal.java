package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCDailyBal {
    
    //Dropdown
    private String branch_code;
    private Date bal_date;

    //Validation
    private Integer result;
    private String bal_status;
    private String bal_type;

    //Listing
    private BigInteger otc_counter_id;
    private String counter_id;
    private String user_id;
    private String counter_bal_status;
    private String check_in;
    private String check_out;
    private BigDecimal total_amt;
    private Integer total;

}