package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OTCReceiptCancellationOrderInfoDetails {
    
    private BigInteger mtt_id;
    private String ss_cd;
    private String orn_no;
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
    private String coll_slip_no;

}
