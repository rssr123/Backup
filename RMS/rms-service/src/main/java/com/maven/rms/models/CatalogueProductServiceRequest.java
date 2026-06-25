package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatalogueProductServiceRequest {
    private String fileName;
	private String fileType;
	private String reportType;

    private String dateStart;
    private String dateEnd;
    private String typeOfService;
    private String paymentStatus;
    private String entityNo;
    private String entityCustomerId; 

	public CatalogueProductServiceRequest(String iFileName, String iFileType, String iDateStart, String iDateEnd, String iTypeOfService, String iPaymentStatus, String iEntityNo) {
        this.fileName = iFileName;
        this.fileType = iFileType;
        this.dateStart = iDateStart;
        this.dateEnd = iDateEnd;
        this.typeOfService = iTypeOfService;
        this.paymentStatus = iPaymentStatus;
        this.entityNo = iEntityNo;
    }  
}
