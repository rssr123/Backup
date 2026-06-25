package com.maven.rms.models;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCReceiptCancellationListingRequest {
    
    private Integer i_page;
    private Integer i_size;
    private String i_rcpt_no;
    private String i_orn_no;
    private String i_cust_nm;
    private Integer i_otc_counter_id;
}
