package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceProviderEmailMtt {

    private String mtt_id;
    private String entity_name;
    private String orn_no;
    private BigDecimal total_amount;
    private String profile_name;
    private String cust_email;
    private String ag_bil;
    
}
