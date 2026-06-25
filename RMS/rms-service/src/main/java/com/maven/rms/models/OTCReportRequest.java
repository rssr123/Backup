package com.maven.rms.models;

// import java.math.BigDecimal;
// import java.math.BigInteger;
import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCReportRequest {
    private String fileName;
	private String fileType;
	private String reportType;

    private String dateFrom;
    private String dateTo;
	private String field1; // branch code
	private String field2; // payment mode
	private String field3;
	private String field4;
	private String field5;
	private String field6;
	private String field7;

	// Counter Collection, OTC Collection Plus, OTC Receipt Cancellation, Bank-In Slip
	public OTCReportRequest(String iFileName, String iFileType, String iDateFrom, String iDateTo, String iField1, String iField2) {
		this.fileName = iFileName;
		this.fileType = iFileType;
		this.dateFrom = iDateFrom;
		this.dateTo = iDateTo;
		this.field1 = iField1;
		this.field2 = iField2;
	}

	// Daily Balancing, Master Balancing
	public OTCReportRequest(String iFileName, String iFileType, String iDateFrom, String iDateTo, String iField1) {
		this.fileName = iFileName;
		this.fileType = iFileType;
		this.dateFrom = iDateFrom;
		this.dateTo = iDateTo;
		this.field1 = iField1;
	}

	// OTC Collection
	public OTCReportRequest(String iFileName, String iFileType, String iDateFrom, String iDateTo) {
		this.fileName = iFileName;
		this.fileType = iFileType;
		this.dateFrom = iDateFrom;
		this.dateTo = iDateTo;
	}

	// OTC Returned Cheque
	public OTCReportRequest(String iFileName, String iFileType, String iDateFrom, String iDateTo, 
		String iField1, String iField2, String iField3, String iField4,
		String iField5, String iField6, String iField7) {
		this.fileName = iFileName;
		this.fileType = iFileType;
		this.dateFrom = iDateFrom;
		this.dateTo = iDateTo;
		this.field1 = iField1;
		this.field2 = iField2;
		this.field3 = iField3;
		this.field4 = iField4;
		this.field5 = iField5;
		this.field6 = iField6;
		this.field7 = iField7;
	}

}
