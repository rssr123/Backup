package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ServiceProviderRequest {
    
    private Integer i_page;
    private Integer i_size;
    // private String i_entity_nm;
    private String i_profile_nm;
    private String i_cust_email;
    private BigDecimal i_total_amt_payable;
    private Date i_date_collection_fr;
    private Date i_date_collection_to;
    private String i_pymt_status;
    private Date i_dt_pymt_fr;
    private Date i_dt_pymt_to;
    private Date i_date_email_sent_fr;
    private Date i_date_email_sent_to;
    private Integer i_ag_bil;
    private String i_mtt_id;
    private  String i_ag_bil_no;

}
