package com.maven.rms.models;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuditLogRequest {
    private Integer i_audit_log_id;
    private String i_actor;
    private String i_request_url;
    private String i_module;
    private String i_request_msg;
    private String i_action;
    private String i_page_url;
    private String i_remark;
    private String i_source;
    private String i_response_msg;
    private Date i_dt_created;
    private Date i_dt_modified;
    private String i_modified_by;
    private String i_created_by;
    private String i_status;
    
    // public Integer getI_audit_log_id() {
    //     return i_audit_log_id;
    // }
    // public void setI_audit_log_id(Integer i_audit_log_id) {
    //     this.i_audit_log_id = i_audit_log_id;
    // }
    // public String getI_actor() {
    //     return i_actor;
    // }
    // public void setI_actor(String i_actor) {
    //     this.i_actor = i_actor;
    // }
    // public String getI_request_url() {
    //     return i_request_url;
    // }
    // public void setI_request_url(String i_request_url) {
    //     this.i_request_url = i_request_url;
    // }
    // public String getI_module() {
    //     return i_module;
    // }
    // public void setI_module(String i_module) {
    //     this.i_module = i_module;
    // }
    // public String getI_request_msg() {
    //     return i_request_msg;
    // }
    // public void setI_request_msg(String i_request_msg) {
    //     this.i_request_msg = i_request_msg;
    // }
    // public String getI_action() {
    //     return i_action;
    // }
    // public void setI_action(String i_action) {
    //     this.i_action = i_action;
    // }
    // public String getI_page_url() {
    //     return i_page_url;
    // }
    // public void setI_page_url(String i_page_url) {
    //     this.i_page_url = i_page_url;
    // }
    // public String getI_remark() {
    //     return i_remark;
    // }
    // public void setI_remark(String i_remark) {
    //     this.i_remark = i_remark;
    // }
    // public String getI_source() {
    //     return i_source;
    // }
    // public void setI_source(String i_source) {
    //     this.i_source = i_source;
    // }
    // public String getI_response_msg() {
    //     return i_response_msg;
    // }
    // public void setI_response_msg(String i_response_msg) {
    //     this.i_response_msg = i_response_msg;
    // }
    // public Date getI_dt_created() {
    //     return i_dt_created;
    // }
    // public void setI_dt_created(Date i_dt_created) {
    //     this.i_dt_created = i_dt_created;
    // }
    // public Date getI_dt_modified() {
    //     return i_dt_modified;
    // }
    // public void setI_dt_modified(Date i_dt_modified) {
    //     this.i_dt_modified = i_dt_modified;
    // }
    // public String getI_modified_by() {
    //     return i_modified_by;
    // }
    // public void setI_modified_by(String i_modified_by) {
    //     this.i_modified_by = i_modified_by;
    // }
    // public String getI_created_by() {
    //     return i_created_by;
    // }
    // public void setI_created_by(String i_created_by) {
    //     this.i_created_by = i_created_by;
    // }
    // public String getI_status() {
    //     return i_status;
    // }
    // public void setI_status(String i_status) {
    //     this.i_status = i_status;
    // }
    
}
