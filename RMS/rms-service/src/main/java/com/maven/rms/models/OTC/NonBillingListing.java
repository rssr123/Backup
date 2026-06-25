package com.maven.rms.models.OTC;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonBillingListing {
    // Columns from rms_nonbil
    private Integer non_bil_id;
    private String req_name;
    private String req_email;
    private String non_bil_no;
    private String non_bil_desc;
    private String ret_che_no;
    private BigDecimal total_bil_amt;
    private String remark;
    private String bil_status;
    private String fms_admin_email;
    private String fms_admin_nm;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    private String bil_action;
    private Date dt_action;
    private String performed_by;
    private Integer otc_counter_id;
    private Integer otc_body_id;

    // Columns from rms_nonbil_cust
    private Integer non_bilcust_id;
    private String cust_id;
    private String cust_nm;
    private String cust_email;
    private String cust_phone;
    private String cust_addr_1;
    private String cust_addr_2;
    private String cust_addr_3;
    private String ent_nm;
    private String ent_no;
    private String ent_ty;
    private String cust_postcode;
    private String cust_city;
    private String cust_state;
    private String che_no;
    private String che_id;
    private Integer total;
}
