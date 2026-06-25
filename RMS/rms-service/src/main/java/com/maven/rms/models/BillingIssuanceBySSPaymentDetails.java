package com.maven.rms.models;


import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingIssuanceBySSPaymentDetails {
    
    private Integer bil_id;
    private String billing_no;
    private String billing_desc;
    private String bil_wf_status;
    private Integer bilcust_id;
    private String cust_id;
    private String cust_nm;
    private String cust_email;
    private String cust_phone;
    private String cust_addr1;
    private String cust_addr2;
    private String cust_addr3;
    private String cust_postcode;
    private String cust_city;
    private String cust_state;
    private String ent_nm;
    private String ent_no;
    private String ent_ty;
    private Integer bil_child_id;
    private Date bil_child_date;
    private String bil_child_status;
    private String bil_no;


}
