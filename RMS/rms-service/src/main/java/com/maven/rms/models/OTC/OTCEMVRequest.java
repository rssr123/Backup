package com.maven.rms.models.OTC;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class OTCEMVRequest {
    private String i_resp_cd; // Response code
    private String i_card_no; // Card number
    private String i_dt_expiry; // Expiry date
    private String i_status_cd; // Status code
    private String i_approval_cd; // Approval code
    private String i_rrn; // Retrieval Reference Number
    private String i_trans_trace; // Transaction trace
    private String i_batch_no; // Batch number
    private String i_host_no; // Host number
    private String i_t_id; // Terminal ID
    private String i_mer_id; // Merchant ID
    private String i_aid; // Application ID
    private String i_tc; // Transaction Certificate
    private String i_cardholder_nm; // Cardholder name
    private String i_card_ty; // Card type
    private String i_prtnr_txn_id; // Partner transaction ID
    private String i_apay_txn_id; // Alternative payment transaction ID
    private String i_cust_id; // Customer ID
    private BigDecimal i_amt; // Amount
    private String i_add_data; // Additional data
    private String i_created_by; // Created by
    private String i_modified_by; // Modified by
}
