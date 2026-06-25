package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceProvider {
    private Integer ag_bil;
    // private String entity_nm;
    private String profile_nm;
    private String ag_bil_no;
    private String cust_email;
    private BigDecimal total_amt_payable;
    private Date date_collection;
    private String pymt_status;
    private Date dt_pymt;
    private Date date_email_sent;
    private String order_status;
    private Integer total;
    
}
