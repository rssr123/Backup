package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSARR {

    // sp_getfmsrcbank
    private BigInteger rc_bank_id;
    private BigInteger rc_pg_id;
    private BigDecimal credit;
    private Date dt_txn;
    private BigDecimal mdr_amt;
    private BigInteger mtt_pg_id;
    private String acct_cd;

    // sp_getfmsarr
    private BigInteger arr_hid;
    private String type;
    private String branch;
    private Date dt_sent;
    private String cash_acct;
    private BigDecimal pymt_amt;
    private String pymt_method;
    private String cust;
    private String rms_batch_no;
    private String fms_ref_no;
    private String doc_type_b;
    private BigDecimal amt;
    private String doc_type_c;
    private String entity_type;
    private String offset_subacct;

    // 2025-04-22 Added new fields
    private String description;
    private String attribute_sys_name;
    private String acct_id;
    private BigDecimal acct_paid;

    // 2025-05-29 Added new fields
    private String attribute_doc_no;
    private BigDecimal amt_paid;

    // sp_updfmsarr
    private String resp_attr_ext_sys;
    private String resp_co;
    private String resp_status;
    private String resp_msg;
    private Date resp_dt;
    private String payment_ref;

    private Integer flag; // 0: Insert, 1: Update


    // sp_insfmsarrnonrmsrecon
    private String i_cust_id;
    private String i_cash_acct;
    private BigDecimal i_h_amt;
    private String i_fms_ref_no;
    private BigDecimal i_b_amt;
    private BigDecimal i_c_amt;

}
