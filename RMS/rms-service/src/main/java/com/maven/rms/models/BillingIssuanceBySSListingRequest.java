package com.maven.rms.models;


import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingIssuanceBySSListingRequest {
    
    private Integer i_page;
    private Integer i_size;
    private String i_ent_nm;
    private String i_ent_no;
    private String i_ss_cd;
    private String i_rcpt_no;
    private String i_billing_mthd;
    private String i_bil_wf_status;
    private Date i_dt_created_fr;
    private Date i_dt_created_to;
    private String i_bt_ty;
    private String i_billing_no;

}
