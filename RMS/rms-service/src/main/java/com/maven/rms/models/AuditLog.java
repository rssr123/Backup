package com.maven.rms.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer audit_log_id;
    private String actor;
    private String request_url;
    private String module;
    private String request_msg;
    private String action;
    private String page_url;
    private String remark;
    private String source;
    private String response_msg;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_modified;
    private String modified_by;
    private String created_by;
    private String status;
    
    // public Integer getAudit_log_id() {
    //     return audit_log_id;
    // }
    // public void setAudit_log_id(Integer audit_log_id) {
    //     this.audit_log_id = audit_log_id;
    // }
    // public String getActor() {
    //     return actor;
    // }
    // public void setActor(String actor) {
    //     this.actor = actor;
    // }
    // public String getRequest_url() {
    //     return request_url;
    // }
    // public void setRequest_url(String request_url) {
    //     this.request_url = request_url;
    // }
    // public String getModule() {
    //     return module;
    // }
    // public void setModule(String module) {
    //     this.module = module;
    // }
    // public String getRequest_msg() {
    //     return request_msg;
    // }
    // public void setRequest_msg(String request_msg) {
    //     this.request_msg = request_msg;
    // }
    // public String getAction() {
    //     return action;
    // }
    // public void setAction(String action) {
    //     this.action = action;
    // }
    // public String getPage_url() {
    //     return page_url;
    // }
    // public void setPage_url(String page_url) {
    //     this.page_url = page_url;
    // }
    // public String getRemark() {
    //     return remark;
    // }
    // public void setRemark(String remark) {
    //     this.remark = remark;
    // }
    // public String getSource() {
    //     return source;
    // }
    // public void setSource(String source) {
    //     this.source = source;
    // }
    // public String getResponse_msg() {
    //     return response_msg;
    // }
    // public void setResponse_msg(String response_msg) {
    //     this.response_msg = response_msg;
    // }
    // public Date getDt_created() {
    //     return dt_created;
    // }
    // public void setDt_created(Date dt_created) {
    //     this.dt_created = dt_created;
    // }
    // public Date getDt_modified() {
    //     return dt_modified;
    // }
    // public void setDt_modified(Date dt_modified) {
    //     this.dt_modified = dt_modified;
    // }
    // public String getModified_by() {
    //     return modified_by;
    // }
    // public void setModified_by(String modified_by) {
    //     this.modified_by = modified_by;
    // }
    // public String getCreated_by() {
    //     return created_by;
    // }
    // public void setCreated_by(String created_by) {
    //     this.created_by = created_by;
    // }
    // public String getStatus() {
    //     return status;
    // }
    // public void setStatus(String status) {
    //     this.status = status;
    // }

    
}
