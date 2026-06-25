package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceProviderEmail {
    private Integer ag_bil;
    private String entity_nm;
    private String ag_bil_no;
    private BigDecimal total_amt_payable;
    private String cust_email;
    
}
