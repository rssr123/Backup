package com.maven.rms.models;

public class GetRefundInfo {
    private String appNo;
    private String appStatus;
    private String appMsg;
    private String appRejectedReason;
    private String slipNo;
    private byte[] file;
    private String fileBase64;

    // Getters and Setters
    public String getAppNo() {
        return appNo;
    }

    public void setAppNo(String appNo) {
        this.appNo = appNo;
    }

    public String getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(String appStatus) {
        this.appStatus = appStatus;
    }

    public String getAppMsg() {
        return appMsg;
    }

    public void setAppMsg(String appMsg) {
        this.appMsg = appMsg;
    }

    public String getAppRejectedReason() {
        return appRejectedReason;
    }

    public void setAppRejectedReason(String appRejectedReason) {
        this.appRejectedReason = appRejectedReason;
    }

    public String getSlipNo() {
        return slipNo;
    }

    public void setSlipNo(String slipNo) {
        this.slipNo = slipNo;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileBase64() {
        return fileBase64;
    }

    public void setFileBase64(String fileBase64) {
        this.fileBase64 = fileBase64;
    }
}