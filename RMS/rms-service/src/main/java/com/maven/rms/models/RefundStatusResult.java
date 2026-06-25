package com.maven.rms.models;

public class RefundStatusResult {
    private String status;
    private String refundTy;
    
    // Default constructor
    public RefundStatusResult() {
    }
    
    // Constructor with parameters
    public RefundStatusResult(String status, String refundTy) {
        this.status = status;
        this.refundTy = refundTy;
    }
    
    // Getters
    public String getStatus() {
        return status;
    }
    
    public String getRefundTy() {
        return refundTy;
    }
    
    // Setters
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setRefundTy(String refundTy) {
        this.refundTy = refundTy;
    }
    
    @Override
    public String toString() {
        return "RefundStatusResult{" +
                "status='" + status + '\'' +
                ", refundTy='" + refundTy + '\'' +
                '}';
    }
}