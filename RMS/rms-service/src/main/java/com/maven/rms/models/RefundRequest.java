package com.maven.rms.models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class RefundRequest {
    private String rcpt_no;        
    private Timestamp rcpt_date;  
    private String orn_no;        
    private String txn_id;
    private BigDecimal refund_amt;        
    //private List<ItemRefund> item_ref_no;  
    private String cust_email;   
    private String sme_email;
    private String created_by;
    private String modified_by;
    private String remark;
    private String appeal_reason;
    // private String a_id;
    // private String batch_no;
    // private String trans_trace;
    private List<PaymentItemDetails> payment_item_details;
    private Integer mtt_id; // Added for refund payment items retrieval
    private Integer rtt_wf_id; // Added for refund workflow ID
}