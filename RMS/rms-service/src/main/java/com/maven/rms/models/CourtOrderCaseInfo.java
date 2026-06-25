package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourtOrderCaseInfo {
    
    private Integer cc_case_id;
    private Integer cc_case_a_id;
    private Integer cc_cs_item_id;
    private String cust_nm;
    private String cust_email;
    private String cust_phone;
    private String cust_addr_1;
    private String cust_addr_2;
    private String cust_addr_3;
    private String cust_postcode;
    private String cust_city;
    private String cust_state;
    private String attr_case_no;
    private Date dt_assigned;
    private String fms_ari_ref_no;
    private String pymt_status;
    private String txn_ty;
    private String ref_no_txn;
    private String cn_ref_no;
    private String rcpt_no;
    private BigDecimal pymt_amt;
    private String task_no;
    private String invoice_desc;
    private String task_status;

}
