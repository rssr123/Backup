package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

@Getter
@Setter

public class RefundWFList {
    private int rtt_wf_id;
    private int rtt_wf_hist_id;
    private String rtt_app_no;
    private Date dt_process;
    private String rcpt_no;
    private String rcpt_date;
    private String orn_no;
    private String txn_id;
    private BigDecimal refund_amt;
    private String ent_no;
    private String ent_nm;
    private String cust_email;
    private String sme_email;
    private int appeal_cnt;
    private String rtt_status;
    private String pickup_by;
    private Date date_pick;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    private Date dt_requested;
    private Date dt_approved;
    private String refund_ty;
    private String requested_by;
    private String branch_cd;
    private String msg;
    private String refund_cd;
    private String assign_to;
    private String refund_reason;

    // for the form 
    private BigDecimal rcpt_amt;
    private String identity_type;
    private String identity_number;
    private String bank_account_no;
    private String bank_account_name;
    private String bank_account_type;
    private String bank_holder_name;
    private String billing_address_1;
    private String billing_address_2;
    private String billing_address_3;
    private String city;
    private String postcode;
    private String state;
    private String rec_email;
    private String cust_nm;
    private String cust_phone;
    private String ent_ty;
    
    private List<RefundDoc> uploadedFiles;
    private List<PaymentItemDetails> payment_item_details;


    @Override
    public String toString() {
        return "RefundWFList{" +
                "rtt_wf_id=" + rtt_wf_id +
                ", rcpt_no='" + rcpt_no + '\'' +
                ", rcpt_date='" + rcpt_date + '\'' +
                ", orn_no='" + orn_no + '\'' +
                ", txn_id='" + txn_id + '\'' +
                ", ent_no='" + ent_no + '\'' +
                ", ent_nm='" + ent_nm + '\'' +
                ", cust_email='" + cust_email + '\'' +
                ", sme_email='" + sme_email + '\'' +
                ", msg='" + msg + '\'' +
                ", rtt_status='" + rtt_status + '\'' +
                ", refund_ty='" + refund_ty + '\'' +
                ", assign_to='" + assign_to + '\'' +
                ", refund_reason='" + refund_reason + '\'' +
                ", bank_account_no='" + bank_account_no + '\'' +
                ", bank_holder_name='" + bank_holder_name + '\'' +
                ", payment_item_details=" + payment_item_details +
                ", uploadedFiles=" + uploadedFiles +
                '}';
    }
}
