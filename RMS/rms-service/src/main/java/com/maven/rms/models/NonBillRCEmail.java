package com.maven.rms.models;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonBillRCEmail {
    private String non_bil_id;
    private String mtt_id;
    private String orn_no;
    private String payer_email;
    private String fms_admin_email;
    private String req_name;
    private String non_bil_no;
    private BigDecimal total_bil_amt;
    private String ret_che_no;
    private String reason;
    private String order_status;
    private String che_bank_nm;
    
}
