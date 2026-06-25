package com.maven.rms.models;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingIssuanceBySSListing {
    

    private String cust_id;
    private String ent_nm;
    private String ent_no;
    private String billing_no;
    private Integer bil_id;
    private BigDecimal amount;
    private String billing_method;
    private String bil_wf_status;
    private String rcpt_no;
    private String req_name;
    private String bil_child_status;
    private Integer total;
  

}
