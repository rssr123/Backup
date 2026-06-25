package com.maven.rms.models;

import java.math.BigDecimal;
import java.sql.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSJournal {
    //Request
    private String branch_id;
    private String desc;
    private String ledger_id;
    private String module;
    private Date dt_txn;
    private String attr_ext_ref_no;
    private String attr_ext_sys;
    private String acct1; 
    private String branch1; 
    private BigDecimal credit1; 
    private BigDecimal debit1; 
    private String sub_acct1; 
    private String txn_desc1;
    private String acct2; 
    private String branch2; 
    private BigDecimal credit2; 
    private BigDecimal debit2; 
    private String sub_acct2; 
    private String txn_desc2;

    //Response
    private String AttributeEXTSYSTEM;
    private String BatchNbr;
    private String ExtRefNbr;
    private String Status;
    private String Message;
    private String Date;

}
