package com.maven.rms.models;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailPP {

    

    private String mtt_id;
    private String cust_email;
    private String orn_no;
    private BigDecimal total_amt;
    private String order_status;
    private String entity_nm;
    private Integer email_flag;
    
}
