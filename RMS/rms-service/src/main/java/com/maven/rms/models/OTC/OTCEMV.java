package com.maven.rms.models.OTC;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class OTCEMV {
    private String resp_cd; // EMV response code
    private String card_no; // EMV Card number
    private String dt_expiry; // EMV Card expiry date
    private String status_cd; // EMV Status code
    private String approval_cd; // EMV Approval code
    private String rrn; // EMV Retrieval reference number
    private String trans_trace; // EMV Transaction trace
    private String batch_no; // EMV Batch number
    private String host_no; // EMV Host number
    private String t_id; // Terminal ID
    private String mer_id; // Merchant ID
    private String aid; // Application ID
    private String tc; // Transaction Cryptogram
    private String cardholder_nm; // Cardholder name
    private String card_ty; // Card type
    private String prtnr_txn_id; // Partner Transaction ID
    private String apay_txn_id; // AliPay Transaction ID
    private String cust_id; // AliPay Customer ID
    private BigDecimal amt; // Amount
    private String add_data; // Additional data
    private Date dt_created; // Date Created
    private Date dt_modified; // Date Modified
    private String created_by; // User who created the record
    private String modified_by; // User who modified the record
    private String status; // Status (A = Active, D = Deactivated)
}
