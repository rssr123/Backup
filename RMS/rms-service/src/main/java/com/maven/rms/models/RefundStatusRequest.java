package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundStatusRequest {

    private String fileName;
	private String fileType;
	private String reportType;

    private String dateStart;
    private String dateEnd;
    private String refundStatus;
    private String refundType;
    private String division;
    private String refundNo; 

	public RefundStatusRequest(String iFileName, String iFileType, String iDateStart, String iDateEnd, String iRefundStatus, String iRefundType) {
        this.fileName = iFileName;
        this.fileType = iFileType;
        this.dateStart = iDateStart;
        this.dateEnd = iDateEnd;
        this.refundStatus = iRefundStatus;
        this.refundType = iRefundType;
}

    public RefundStatusRequest(String iFileName, String iFileType, String iDateStart, String iDateEnd, String iRefundStatus, String iRefundType, String iDivision) {
        this.fileName = iFileName;
        this.fileType = iFileType;
        this.dateStart = iDateStart;
        this.dateEnd = iDateEnd;
        this.refundStatus = iRefundStatus;
        this.refundType = iRefundType;
        this.division = iDivision;
    }

    public RefundStatusRequest(String iFileName, String iFileType, String iDateStart, String iDateEnd, String iRefundStatus, String iRefundType, String iDivision, String iRefundNo) {
        this.fileName = iFileName;
        this.fileType = iFileType;
        this.dateStart = iDateStart;
        this.dateEnd = iDateEnd;
        this.refundStatus = iRefundStatus;
        this.refundType = iRefundType;
        this.division = iDivision;
        this.refundNo = iRefundNo; 
    }

}



