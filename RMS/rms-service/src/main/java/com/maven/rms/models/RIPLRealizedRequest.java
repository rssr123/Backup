package com.maven.rms.models;

import java.sql.Date;
import java.text.SimpleDateFormat;

import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RIPLRealizedRequest {
    
    @NotNull(message = "txn_type is required.")
    @Size(min = 1, max = 5, message="txn_type is required.")    
    private String txn_type;
    @NotNull(message = "entity_type is required.")
    @Size(min = 1, max = 1, message="entity_type is required.")
    private String entity_type;
    @NotNull(message = "entity_no is required.")
    @Size(min = 1, max = 40, message="entity_no is required.")
    private String entity_no;
    @NotNull(message = "calendar_yr is required.")
    @Size(min = 1, max = 4, message="calendar_yr is required.")
    private String calendar_yr;
    @NotNull(message = "rcpt_no is required.")
    @Size(min = 1, max = 20, message="rcpt_no is required.")
    private String rcpt_no;
    private String modified_by;

    // public String getRcpt_no() {
    //     return rcpt_no;
    // }
    // public void setRcpt_no(String rcpt_no) {
    //     this.rcpt_no = rcpt_no;
    // }
    // public String getTxn_type() {
    //     return txn_type;
    // }
    // public void setTxn_type(String txn_type) {
    //     this.txn_type = txn_type;
    // }
    // public String getEntity_type() {
    //     return entity_type;
    // }
    // public void setEntity_type(String entity_type) {
    //     this.entity_type = entity_type;
    // }
    // public String getEntity_no() {
    //     return entity_no;
    // }
    // public void setEntity_no(String entity_no) {
    //     this.entity_no = entity_no;
    // }
    // public String getCalendar_yr() {
    //     return calendar_yr;
    // }
    // public void setCalendar_yr(String calendar_yr) {
    //     this.calendar_yr = calendar_yr;
    // }
    // public String getModified_by() {
    //     return modified_by;
    // }
    // public void setModified_by(String modified_by) {
    //     this.modified_by = modified_by;
    // }
    
}
