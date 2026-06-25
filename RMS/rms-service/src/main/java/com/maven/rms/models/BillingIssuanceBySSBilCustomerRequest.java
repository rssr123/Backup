package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingIssuanceBySSBilCustomerRequest {
    
    private String i_cust_id;
    private String i_cust_nm;
    private String i_cust_email;
    private String i_cust_phone;
    private String i_cust_addr1;
    private String i_cust_addr2;
    private String i_cust_addr3;
    private String i_cust_postcode;
    private String i_cust_city;
    private String i_cust_state;
    private String i_ent_nm;
    private String i_ent_no;
    private String i_ent_ty;
    private String i_created_by;
    private String i_modified_by;
    private String i_status;
    private Integer i_bltc_id;
    private String i_req_name;
    private String i_req_email;
    private String i_ss_cd;
    private String i_billing_no;
    private String i_billing_desc;
    private String i_action;
    private BigDecimal i_dps_amt;
    private Integer i_billing_cnt;
    private String i_billing_freq;
    private String i_loa_id;
    private Date i_dt_loa_start;
    private Date i_dt_loa_end;
    private String i_agm_id;
    private Date i_dt_agm_start;
    private Date i_dt_agm_end;
    private String i_bil_wf_status;
    private String i_pickup_by;
    private Date i_dt_pick;
    private String i_billing_mthd; 
    // private String i_order_summary;
    private String i_msg;
    private String i_msg_type;
    private List<BillingIssuanceBySSBillingItemDetails> i_billingItemDetails;
    private List<BillingIssuanceBySSBillingChildDetails> i_billingChildDetails;
    // private PaymentRequest i_paymentRequest;
    
}
