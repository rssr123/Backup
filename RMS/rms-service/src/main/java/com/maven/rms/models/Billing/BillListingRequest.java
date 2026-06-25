package com.maven.rms.models.Billing;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillListingRequest {
    private Integer i_page;
    private Integer i_size;
    private Date i_dt_created_fr;
    private Date i_dt_created_to;
    private String i_billing_status;
    private String i_bil_no;
    private String i_cust_id;
}
