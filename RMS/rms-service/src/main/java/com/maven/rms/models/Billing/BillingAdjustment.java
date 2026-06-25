package com.maven.rms.models.Billing;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingAdjustment {
    private Integer bil_id;
    private Integer bltc_id;
    private Integer bilcust_id;
    private String req_name;
    private String req_email;
    private String ss_cd;
    private String billing_no;
    private String billing_desc;
    private String action;
    private BigDecimal dps_amt;
    private Integer billing_cnt;
    private String billing_freq;
    private String loa_id;
    private Date dt_loa_start;
    private Date dt_loa_end;
    private String agm_id;
    private Date dt_agm_start;
    private Date dt_agm_end;
    private String bil_wf_status;
    private String pickup_by;
    private Date dt_pick;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    // Columns from rms_bilcust
    private String cust_id;
    private String cust_nm;
    private String cust_email;
    private String cust_phone;
    private String cust_addr1;
    private String cust_addr2;
    private String cust_addr3;
    private String cust_postcode;
    private String cust_city;
    private String cust_state;
    private String ent_nm;
    private String ent_no;
    private String ent_ty;
    private String bt_cd;
    private String bt_desc;
    private BigDecimal amount;
    private String bill_type;
    private Integer total;
}
