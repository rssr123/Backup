package com.maven.rms.models;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingItemRequest {
    private Integer i_page;
    private Integer i_size;
    private String i_billing_no;
    private String i_cust_email;   
}
