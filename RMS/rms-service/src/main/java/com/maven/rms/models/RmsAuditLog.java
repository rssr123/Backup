package com.maven.rms.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rms_audit_log")
public class RmsAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_log_id")
    private Long auditLogId;

    @Column(nullable = false)
    private String actor;

    @Column(name = "request_url", nullable = false)
    private String requestUrl;

    @Column(nullable = false)
    private String module;

    @Column(name = "request_msg", nullable = false)
    @Lob // Consider using @Lob for large text data
    private String requestMsg;

    @Column(nullable = false)
    private String action;

    @Column(name = "page_url", nullable = false)
    private String pageUrl;

    @Column(nullable = false)
    private String remark;

    @Column(nullable = false)
    private String source;

    @Column(name = "response_msg", nullable = false)
    @Lob // Consider using @Lob for large text data
    private String responseMsg;

    @Column(name = "dt_created", nullable = false)
    private LocalDateTime dtCreated;

    @Column(name = "dt_modified", nullable = false)
    private LocalDateTime dtModified;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(nullable = false)
    private String status;

    // Constructors, Getters, and Setters...

    // public Long getAuditLogId() {
    //     return auditLogId;
    // }

    // public void setAuditLogId(Long auditLogId) {
    //     this.auditLogId = auditLogId;
    // }

    // public String getActor() {
    //     return actor;
    // }

    // public void setActor(String actor) {
    //     this.actor = actor;
    // }

    // public String getRequestUrl() {
    //     return requestUrl;
    // }

    // public void setRequestUrl(String requestUrl) {
    //     this.requestUrl = requestUrl;
    // }

    // public String getModule() {
    //     return module;
    // }

    // public void setModule(String module) {
    //     this.module = module;
    // }

    // public String getRequestMsg() {
    //     return requestMsg;
    // }

    // public void setRequestMsg(String requestMsg) {
    //     this.requestMsg = requestMsg;
    // }

    // public String getAction() {
    //     return action;
    // }

    // public void setAction(String action) {
    //     this.action = action;
    // }

    // public String getPageUrl() {
    //     return pageUrl;
    // }

    // public void setPageUrl(String pageUrl) {
    //     this.pageUrl = pageUrl;
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

    // public String getResponseMsg() {
    //     return responseMsg;
    // }

    // public void setResponseMsg(String responseMsg) {
    //     this.responseMsg = responseMsg;
    // }

    // public LocalDateTime getDtCreated() {
    //     return dtCreated;
    // }

    // public void setDtCreated(LocalDateTime dtCreated) {
    //     this.dtCreated = dtCreated;
    // }

    // public LocalDateTime getDtModified() {
    //     return dtModified;
    // }

    // public void setDtModified(LocalDateTime dtModified) {
    //     this.dtModified = dtModified;
    // }

    // public String getCreatedBy() {
    //     return createdBy;
    // }

    // public void setCreatedBy(String createdBy) {
    //     this.createdBy = createdBy;
    // }

    // public String getModifiedBy() {
    //     return modifiedBy;
    // }

    // public void setModifiedBy(String modifiedBy) {
    //     this.modifiedBy = modifiedBy;
    // }

    // public String getStatus() {
    //     return status;
    // }

    // public void setStatus(String status) {
    //     this.status = status;
    // }

    // Default constructor is needed by JPA
    public RmsAuditLog() {
    }

}