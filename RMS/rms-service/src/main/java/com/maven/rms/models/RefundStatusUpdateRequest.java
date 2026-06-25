package com.maven.rms.models;

public class RefundStatusUpdateRequest {
    
    private String app_no;
    private String reject_reason;

    // Constructor
    public RefundStatusUpdateRequest(String app_no, String reject_reason) {
        this.app_no = app_no;
        this.reject_reason = reject_reason;
    }

    // Getters and Setters
    public String getApp_no() {
        return app_no;
    }

    public void setApp_no(String app_no) {
        this.app_no = app_no;
    }

    public String getReject_reason() {
        return reject_reason;
    }

    public void setReject_reason(String reject_reason) {
        this.reject_reason = reject_reason;
    }

    @Override
    public String toString() {
        return "RefundStatusUpdateRequest{" +
               "app_no='" + app_no + '\'' +
               ", reject_reason='" + reject_reason + '\'' +
               '}';
    }
}
