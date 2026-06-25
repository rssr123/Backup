package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceProviderProfileRequest {

   
    private Integer i_page;
    private Integer i_size;
    private String i_profile_nm;
    private String i_cust_nm;
    private String i_cust_addr_1;
    private String i_cust_addr_2;
    private String i_cust_addr_3;
    private String i_cust_postcode;
    private String i_cust_city;
    private String i_cust_state;
    private String i_cust_email;
    private String i_cust_phone;
    private String i_fee_detail_id;
    private String i_entity_type;
    private String i_entity_no;
    private String i_entity_nm;
    private String i_status;
    private String i_created_by;
    private String i_modified_by;
    private String i_ag_pf_id;

    
}
