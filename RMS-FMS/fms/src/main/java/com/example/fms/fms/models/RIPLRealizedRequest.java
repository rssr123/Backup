package com.example.fms.fms.models;

import java.sql.Date;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.fms.fms.config.Constants;

public class RIPLRealizedRequest {
    
    private String txn_type;
    private String entity_type;
    private String entity_no;
    private String calendar_yr;
    private String rcpt_no;

    public String getRcpt_no() {
        return rcpt_no;
    }
    public void setRcpt_no(String rcpt_no) {
        this.rcpt_no = rcpt_no;
    }
    public String getTxn_type() {
        return txn_type;
    }
    public void setTxn_type(String txn_type) {
        this.txn_type = txn_type;
    }
    public String getEntity_type() {
        return entity_type;
    }
    public void setEntity_type(String entity_type) {
        this.entity_type = entity_type;
    }
    public String getEntity_no() {
        return entity_no;
    }
    public void setEntity_no(String entity_no) {
        this.entity_no = entity_no;
    }
    public String getCalendar_yr() {
        return calendar_yr;
    }
    public void setCalendar_yr(String calendar_yr) {
        this.calendar_yr = calendar_yr;
    }
}
