package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSARIModel{ 

    // sp_getfmsmtt
    private BigInteger mtt_pg_id;           // int8 from Informix
    private BigDecimal pg_pymt_amt;             // decimal(16,2) from Informix
    private int qty;                        // int from Informix
    private String item_desc;               // nvarchar(100) from Informix
    private BigDecimal unit_fee;                // decimal(16,2) from Informix
    private String rcpt_no;          // nvarchar(100) from Informix
    private String cust_nm;          // nvarchar(100) from Informix
    private BigDecimal gross_amt;    // decimal(16,2) from Informix
    private String fee_detail_id;     // nvarchar(100) from Informix
    private BigInteger fee_detail_pk;     // cannot use fee_detail_id as it is not unique
    private String pg_pymt_method;    // nvarchar(100) from Informix
    private BigDecimal tax_amt;      // decimal(16,2) from Informix
    private String customer;        // nvarchar(100) from Informix
    private String item_ref_no;       // nvarchar(100) from Informix
    private String cp_no;          // nvarchar(100) from Informix
    private BigDecimal net_amt;      // decimal(16,2) from Informix

    
    private String type;            // nvarchar(100) from Informix
    private String link_branch;      // nvarchar(100) from Informix
    private BigDecimal amt;         // decimal(16,2) from Informix
    private String cust;            // nvarchar(100) from Informix
    private String rms_batch_no;      // nvarchar(100) from Informix
    private Date dt_sent;            // date from Informix
    private String desc;            // nvarchar(255) from Informix
    private String attr_ext_sys;      // nvarchar(100) from Informix
    // 241010: added by Wei Ern based on latest ISD
    private Integer lineNbr;
    // End
    private String coa1;            // nvarchar(100) from Informix
    private String coa2;            // nvarchar(100) from Informix
    private String branch;          // nvarchar(100) from Informix
    private String sub_acct;         // nvarchar(100) from Informix
    private String txn_desc;         // nvarchar(100) from Informix
    private BigDecimal unit_price;   // decimal(16,2) from Informix
    private String payee_info;       // nvarchar(100) from Informix
    private String ent_nm;           // nvarchar(100) from Informix
    private String ent_no;           // nvarchar(100) from Informix
    private String ent_ty;           // nvarchar(100) from Informix
    private BigDecimal item_amt;     // decimal(16,2) from Informix
    private String pymt_mode;        // nvarchar(100) from Informix
    private BigDecimal item_tax_amt;  // decimal(16,2) from Informix

    // 241010: added by Wei Ern based on latest ISD
    private BigDecimal discAmt;
    private String depositID;
    private String depositTask;
    // End

    private String resp_attr_ext_sys;
    private String fms_ref_no;
    private String resp_co;
    private String resp_status;
    private String resp_msg;
    private String resp_dt;

    private String Status;
    private String AttributeEXTSYSTEM;
    private String Message;
    private String ReferenceNbr;
    private String CustomerOder;
    private String CustomerOrder;
    private String Date;
    
	private LocalDateTime dt_created;
	private LocalDateTime dt_modified;
	private String created_by;
	private String modified_by;
	
	private Integer bil_child_id;
	private Integer generatePDF;
	
	private String document;
	
	private LocalDateTime inv_dt;
    private String lit_item_ref;
}
