package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingReportRequest {
    private String fileName;
	private String fileType;
	private String reportType;

    private String dateStart;
    private String dateEnd;
    private String billingCategory;
    private String paymentStatus;
    private String billingNo;
    private String entityCustomerId; 
    private String classIdSelection;
    private String billingStatus;

	public BillingReportRequest(String iFileName, String iFileType, String iDateStart, String iDateEnd, String iBillingCategory, String iPaymentStatus, String iBillingNo, String iEntityCustomerId) {
        this.fileName = iFileName;
        this.fileType = iFileType;
        this.dateStart = iDateStart;
        this.dateEnd = iDateEnd;
        this.billingCategory = iBillingCategory;
        this.paymentStatus = iPaymentStatus;
        this.billingNo = iBillingNo;
        this.entityCustomerId = iEntityCustomerId;
    }

    public BillingReportRequest(String iFileName, String iFileType, String iDateStart, String iDateEnd, String iClassIdSelection, String iEntityCustomerId) {
        this.fileName = iFileName;
        this.fileType = iFileType;
        this.dateStart = iDateStart;
        this.dateEnd = iDateEnd;
        this.classIdSelection = iClassIdSelection;
        this.entityCustomerId = iEntityCustomerId;
    }

    public BillingReportRequest(String iFileName, String iFileType, String iDateStart, String iDateEnd, String iClassIdSelection, String iEntityCustomerId, String iPaymentStatus) {
        this.fileName = iFileName;
        this.fileType = iFileType;
        this.dateStart = iDateStart;
        this.dateEnd = iDateEnd;
        this.classIdSelection = iClassIdSelection;
        this.entityCustomerId = iEntityCustomerId;
        this.paymentStatus = iPaymentStatus;
    }

    public BillingReportRequest(String iFileName, String iFileType, String iDateStart, String iDateEnd, String iBillingCategory) {
        this.fileName = iFileName;
        this.fileType = iFileType;
        this.dateStart = iDateStart;
        this.dateEnd = iDateEnd;
        this.billingCategory = iBillingCategory;
    }

}
