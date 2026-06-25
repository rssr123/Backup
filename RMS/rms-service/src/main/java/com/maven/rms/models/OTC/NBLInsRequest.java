package com.maven.rms.models.OTC;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NBLInsRequest {

    private String i_cust_id;
    private String i_cust_nm;
    private String i_cust_email;
    private String i_cust_phone;
    private String i_cust_addr_1;
    private String i_cust_addr_2;
    private String i_cust_addr_3;
    private String i_ent_nm;
    private String i_ent_no;
    private String i_ent_ty;
    private String i_created_by;
    private String i_modified_by;
    private String i_req_name;
    private String i_req_email;
    private String i_non_bil_no;
    private String i_non_bil_desc;
    private String i_ret_che_no;
    private double i_total_bil_amt;
    private String i_remark;
    private String i_bil_status;
    private String i_fms_admin_email;
    private String i_fms_admin_nm;
    private String i_cust_postcode;
    private String i_cust_city;
    private String i_cust_state;
    private String i_void_reason;
    // private Integer i_mtt_id;
    private Integer i_otc_body_id;
    private String i_counter_id;
    private BigDecimal i_che_amt;
    private Date i_dt_email_expiry;
    private String i_bt_cd;
    private String i_che_id;
    private String i_payer_nm;
    private String i_payer_email;
}