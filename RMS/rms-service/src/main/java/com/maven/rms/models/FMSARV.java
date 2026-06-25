package com.maven.rms.models;

import java.math.BigInteger;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSARV { 

    // sp_getfmsrcbank
    // private BigInteger rc_bank_id;
    // private BigInteger rc_pg_id;
    // private BigDecimal credit;
    // private Date dt_txn;
    // private BigDecimal mdr_amt;
    // private BigInteger mtt_pg_id;
    // private String acct_cd;

    // sp_getfmsarr
    // private BigInteger arr_hid;
    private String type;
    // private String branch;
    // private Date dt_sent;
    // private String cash_acct;
    // private BigDecimal pymt_amt;
    // private String pymt_method;
    // private String cust;
    private String rms_batch_no;
    private String fms_ref_no;
    // private String doc_type_b;
    // private BigDecimal amt;
    // private String doc_type_c;
    // private String entity_type;
    // private String offset_subacct;

    // sp_updfmsarr
    private String resp_attr_ext_sys;
    private String resp_co;
    private String resp_status;
    private String resp_msg;
    private Date resp_dt;

    //additionals
    private String  rcpt_no;

    //getfmsarv
    private BigInteger fms_arv_id;
    private String ref_no;
    private String arv_type;
    private String arv_hold;
    private String arv_reason;
    private String ext_sys;
    private String status;
    private String message;
    private Date fms_date;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    
}
