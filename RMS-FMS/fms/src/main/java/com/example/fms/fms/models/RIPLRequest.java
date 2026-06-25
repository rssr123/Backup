package com.example.fms.fms.models;

import java.math.BigInteger;

public class RIPLRequest {
    private Integer i_page;
    private Integer i_size;
    private BigInteger i_ripl_id;
    private String i_txn_type;
    private String i_entity_type;
    private String i_entity_no;
    private String i_calendar_yr;
    private String i_dt_due;
    private String i_ripl_ctype;
    private String i_status;

    public Integer getI_page() {
        return i_page;
    }
    public void setI_page(Integer i_page) {
        this.i_page = i_page;
    }
    public Integer getI_size() {
        return i_size;
    }
    public void setI_size(Integer i_size) {
        this.i_size = i_size;
    }
    public BigInteger getI_ripl_id() {
        return i_ripl_id;
    }
    public void setI_ripl_id(BigInteger i_ripl_id) {
        this.i_ripl_id = i_ripl_id;
    }
    
    public String getI_txn_type() {
        return i_txn_type;
    }
    public void setI_txn_type(String i_txn_type) {
        this.i_txn_type = i_txn_type;
    }
    public String getI_entity_type() {
        return i_entity_type;
    }
    public void setI_entity_type(String i_entity_type) {
        this.i_entity_type = i_entity_type;
    }
    public String getI_entity_no() {
        return i_entity_no;
    }
    public void setI_entity_no(String i_entity_no) {
        this.i_entity_no = i_entity_no;
    }
    public String getI_calendar_yr() {
        return i_calendar_yr;
    }
    public void setI_calendar_yr(String i_calendar_yr) {
        this.i_calendar_yr = i_calendar_yr;
    }
    public String getI_dt_due() {
        return i_dt_due;
    }
    public void setI_dt_due(String i_dt_due) {
        this.i_dt_due = i_dt_due;
    }
    public String getI_ripl_ctype() {
        return i_ripl_ctype;
    }
    public void setI_ripl_ctype(String i_ripl_ctype) {
        this.i_ripl_ctype = i_ripl_ctype;
    }
    public String getI_status() {
        return i_status;
    }
    public void setI_status(String i_status) {
        this.i_status = i_status;
    }
}
