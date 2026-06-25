package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// @Entity(name = "rms_otc")
public class RROrderInfo {


    private String mtt_id;
    private String ss_cd ;
    private String coll_slip_no;
    private String orn_no;
    private String cust_nm;
    private String cust_phone;
    private String cust_email;
    private String cust_addr_1;
    private String cust_addr_2;
    private String cust_addr_3;
    private String cust_postcode;
    private String cust_city;
    private String cust_state;
    private String order_status;

}
