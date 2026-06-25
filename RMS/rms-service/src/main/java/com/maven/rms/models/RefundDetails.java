package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundDetails {
    private Integer mtt_id;
    private String rms_type;
    private String ss_cd;
    private String orn_no;
    private String txn_id;
    private String ent_nm;
    private String ent_type; 
    private String ent_no;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT)
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
}


