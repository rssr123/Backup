package com.example.fms.fms.models;

import java.sql.Date;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.example.fms.fms.config.Constants;

public class RIPLRecognitionRequest {
    
    private String txn_type;
    private String entity_type;
    private String entity_no;
    private String calendar_yr;
    //@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d/M/yyyy", timezone = "Asia/Singapore")
    private String dt_due;
    private String ripl_ctype;

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
    public String getDt_due() {
        return dt_due;
    }
    public void setDt_due(String dt_due) {
        this.dt_due = dt_due;
    }
    public String getRipl_ctype() {
        return ripl_ctype;
    }
    public void setRipl_ctype(String ripl_ctype) {
        this.ripl_ctype = ripl_ctype;
    }
}
