package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SummaryBillingReportRequest {
    private String fileName;
	private String fileType;
	private String reportType;

    private String dateStart;
    private String dateEnd;
    private String billingCategory;
    private String entityCustomerId; 
    private String classIdSelection;
    private String billingStatus;

	

    public SummaryBillingReportRequest(String iFileName, String iFileType, String iDateStart, String iDateEnd, String iClassIdSelection, String iEntityCustomerId, String iBillingStatus) {
        this.fileName = iFileName;
        this.fileType = iFileType;
        this.dateStart = iDateStart;
        this.dateEnd = iDateEnd;
        this.classIdSelection = iClassIdSelection;
        this.entityCustomerId = iEntityCustomerId;
        this.billingStatus = iBillingStatus;
    }

   

}
