package com.maven.rms.models;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCMasterBal {
    //Dropdown
    private String branch_code;
    private Date bal_date;

    //Validation
    private Integer result;

    //Listing
    private String daily_bal_status;
    private String user_id;
    private Integer no_of_counters;
    private String check_in;
    private BigDecimal total_amt;
    private Integer total;
}