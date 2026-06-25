package com.maven.rms.models;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AgentDeferredIncome {

    private String rms_type;
    private String ss_cd;
    private String orn_no;
    private Date orn_dt;
    private String cust_ip;
    private String cust_nm;
    private String cust_addr_1;
    private String cust_addr_2;
    private String cust_addr_3;
    private String cust_postcode;
    private String cust_city;
    private String cust_state;
    private String cust_email;
    private String cust_phone;
    private BigDecimal total_amt;
    private String order_status;

    private List<AgentDetailDeferredIncome> payment_item_details;
}
