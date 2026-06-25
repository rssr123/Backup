package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingIssuanceBySSBillingMethod {
    
    private String billing_mthd;
    private String bil_no;
    private String ent_nm;
    private Date bil_child_date;
    private BigDecimal sum_amt;
    private String bt_desc;
    private String billing_desc;


}
