package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequest {
	private String fileName;
	private String reportType;
	private String dateEnd;
	private String dateStart;
	private String fileType;
	private String paymentMode;
	
	private String settDate;
	private String ornNo;
	private Integer subCriteria;
	private String rcptNo;
	private String pgTransId;
	private String stmtNo;
	private Integer checkDuplicate;
	private String reportName;
	private String pgPaymentID;
	private Integer pgTransStatus;
	private String pgTransStatusMsg;
	private String pgTxnId;
	private String subCriteria2;
	private String transDesc;
	private String effectivedatestart;
	private String effectivedateend;
	private String distatus;
	private Integer inctermstatus;
	private String txntype;
	private String expirydatestart;
	private String expirydateend;
	private String approvaldatestart;
	private String approvaldateend;
	private String terminationdatestart;
	private String terminationdateend;
	private String batchno;
	private String fmsrefno;
	
	private RICPAgingReportRequest ricpReq;
	
	private String createddate;
	private String duedatestart;
	private String duedateend;
	private Integer impairstatus;
	private Integer writeoffstatus;
	private String entitytype;
	private String entitynm;
	private String receiptdatestart;
	private String receiptdateend;
	private String impairdatestart;
	private String impairdateend;
	private String writeoffdatestart;
	private String writeoffdateend;
	private String email;
	
	private UnmatchedAgingReportRequest uarReq;

	//For PaymentCollectionReportGenerator
	public ReportRequest(String fileName, String reportType, String dateEnd, String dateStart
			, String fileType, String paymentMode) {
		this.fileName = fileName;
		this.reportType = reportType;
		this.dateEnd = dateEnd;
		this.dateStart = dateStart;
		this.fileType = fileType;
		this.paymentMode = paymentMode;
		
	}
	
	//For ReportReconAndAccountGenerator
	public ReportRequest(String fileName, String settDate, String ornNo, Integer subCriteria, 
			String rcptNo, String pgTransId, String stmtNo, Integer checkDuplicate, 
			String reportName, String fileType) {
		this.fileName = fileName;
		this.settDate = settDate;
		this.ornNo = ornNo;
		this.subCriteria = subCriteria;
		this.rcptNo = rcptNo;
		this.pgTransId = pgTransId;
		this.stmtNo = stmtNo;
		this.checkDuplicate = checkDuplicate;
		this.reportName = reportName;
		this.fileType = fileType;
	}
	
	//For ReportReconAndAccountGenerator
	public ReportRequest(String fileName, String dateStart, String dateEnd, String ornNo, 
			String pgPaymentID, String pgTransId, Integer pgTransStatus, String pgTransStatusMsg, 
			String rcptNo, String stmtNo, String reportName, String fileType) {
		this.fileName = fileName;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		this.ornNo = ornNo;
		this.pgPaymentID = pgPaymentID;
		this.pgTransId = pgTransId;
		this.pgTransStatus = pgTransStatus;
		this.pgTransStatusMsg = pgTransStatusMsg;
		this.rcptNo = rcptNo;
		this.stmtNo = stmtNo;
		this.reportName = reportName;
		this.fileType = fileType;
	}
	
	//For ReportReconAndAccountGenerator
	public ReportRequest(String fileName, String dateStart, String dateEnd, String ornNo, 
			String pgPaymentID, String pgTransId, String rcptNo, String stmtNo, 
			String reportName, String fileType) {
		this.fileName = fileName;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		this.ornNo = ornNo;
		this.pgPaymentID = pgPaymentID;
		this.pgTransId = pgTransId;		
		this.rcptNo = rcptNo;
		this.stmtNo = stmtNo;
		this.reportName = reportName;
		this.fileType = fileType;
	}
	
	//For ReportReconAndAccountGenerator //reportName and fileType to be added separately after instantiation
	public ReportRequest(String fileName, String dateStart, String dateEnd, String stmtNo, 
			String transDesc) {
		this.fileName = fileName;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
		this.stmtNo = stmtNo;
		this.transDesc = transDesc;
	}
	
	//For ReportReconAndAccountGenerator
	public ReportRequest(String fileName, String receiptdateend, String effectivedatestart, String effectivedateend, 
			String distatus, Integer inctermstatus, String entitytype, String entitynm, 
			String txntype, String expirydatestart, String expirydateend, String approvaldatestart, 
			String approvaldateend, String terminationdatestart, String terminationdateend, String batchno, 
			String fmsrefno, String email, String fileType) {
		this.fileName = fileName;
		this.receiptdateend = receiptdateend;
		this.effectivedatestart = effectivedatestart;
		this.effectivedateend = effectivedateend;
		this.distatus = distatus;
		this.inctermstatus = inctermstatus;
		this.entitytype = entitytype;
		this.entitynm = entitynm;
		this.txntype = txntype;
		this.expirydatestart = expirydatestart;
		this.expirydateend = expirydateend;
		this.approvaldatestart = approvaldatestart;
		this.approvaldateend = approvaldateend;
		this.terminationdatestart = terminationdatestart;
		this.terminationdateend = terminationdateend;
		this.batchno = batchno;
		this.fmsrefno = fmsrefno;
		this.email = email;
		this.fileType = fileType;
	}
	
	//For ReportUTLGenerator
	public ReportRequest(String fileName, String ornNo, String subCriteria2, String rcptNo, 
    		String pgTxnId, String stmtNo, String fileType) {
		this.fileName = fileName;
		this.ornNo = ornNo;
		this.subCriteria2 = subCriteria2;
		this.rcptNo = rcptNo;
		this.pgTxnId = pgTxnId;
		this.stmtNo = stmtNo;
		this.fileType = fileType;
	}
	
	//For RICPAgingReportGenerator
	public ReportRequest(String fileName, RICPAgingReportRequest ricpReq) {
		this.fileName = fileName;
		this.ricpReq = ricpReq;
	}
	
	//For RIPLAgingReportGenerator
	public ReportRequest(String fileName, String createddate, String duedatestart, String duedateend, 
			Integer impairstatus, Integer writeoffstatus, String entitytype, String entitynm, 
			String receiptdatestart, String receiptdateend, String impairdatestart, String impairdateend, 
			String writeoffdatestart, String writeoffdateend, String email, String fileType) {
		this.fileName = fileName;
		this.createddate = createddate;
		this.duedatestart = duedatestart;
		this.duedateend = duedateend;
		this.impairstatus = impairstatus;
		this.writeoffstatus = writeoffstatus;
		this.entitytype = entitytype;
		this.entitynm = entitynm;
		this.receiptdatestart = receiptdatestart;
		this.receiptdateend = receiptdateend;
		this.impairdatestart = impairdatestart;
		this.impairdateend = impairdateend;
		this.writeoffdatestart = writeoffdatestart;
		this.writeoffdateend = writeoffdateend;
		this.email = email;
		this.fileType = fileType;
	}

	
	//For UnmatchedAgingReportGenerator
	public ReportRequest(String fileName, UnmatchedAgingReportRequest uarReq) {
		this.fileName = fileName;
		this.uarReq = uarReq;
	}

	// public String getFileName() {
	// 	return fileName;
	// }

	// public void setFileName(String fileName) {
	// 	this.fileName = fileName;
	// }

	// public String getReportType() {
	// 	return reportType;
	// }

	// public void setReportType(String reportType) {
	// 	this.reportType = reportType;
	// }

	// public String getDateEnd() {
	// 	return dateEnd;
	// }

	// public void setDateEnd(String dateEnd) {
	// 	this.dateEnd = dateEnd;
	// }

	// public String getDateStart() {
	// 	return dateStart;
	// }

	// public void setDateStart(String dateStart) {
	// 	this.dateStart = dateStart;
	// }

	// public String getFileType() {
	// 	return fileType;
	// }

	// public void setFileType(String fileType) {
	// 	this.fileType = fileType;
	// }

	// public String getPaymentMode() {
	// 	return paymentMode;
	// }

	// public void setPaymentMode(String paymentMode) {
	// 	this.paymentMode = paymentMode;
	// }

	// public String getSettDate() {
	// 	return settDate;
	// }

	// public void setSettDate(String settDate) {
	// 	this.settDate = settDate;
	// }

	// public String getOrnNo() {
	// 	return ornNo;
	// }

	// public void setOrnNo(String ornNo) {
	// 	this.ornNo = ornNo;
	// }

	// public Integer getSubCriteria() {
	// 	return subCriteria;
	// }

	// public void setSubCriteria(Integer subCriteria) {
	// 	this.subCriteria = subCriteria;
	// }

	// public String getRcptNo() {
	// 	return rcptNo;
	// }

	// public void setRcptNo(String rcptNo) {
	// 	this.rcptNo = rcptNo;
	// }

	// public String getPgTransId() {
	// 	return pgTransId;
	// }

	// public void setPgTransId(String pgTransId) {
	// 	this.pgTransId = pgTransId;
	// }

	// public String getStmtNo() {
	// 	return stmtNo;
	// }

	// public void setStmtNo(String stmtNo) {
	// 	this.stmtNo = stmtNo;
	// }

	// public Integer getCheckDuplicate() {
	// 	return checkDuplicate;
	// }

	// public void setCheckDuplicate(Integer checkDuplicate) {
	// 	this.checkDuplicate = checkDuplicate;
	// }

	// public String getReportName() {
	// 	return reportName;
	// }

	// public void setReportName(String reportName) {
	// 	this.reportName = reportName;
	// }

	// public String getPgPaymentID() {
	// 	return pgPaymentID;
	// }

	// public void setPgPaymentID(String pgPaymentID) {
	// 	this.pgPaymentID = pgPaymentID;
	// }

	// public Integer getPgTransStatus() {
	// 	return pgTransStatus;
	// }

	// public void setPgTransStatus(Integer pgTransStatus) {
	// 	this.pgTransStatus = pgTransStatus;
	// }

	// public String getPgTransStatusMsg() {
	// 	return pgTransStatusMsg;
	// }

	// public void setPgTransStatusMsg(String pgTransStatusMsg) {
	// 	this.pgTransStatusMsg = pgTransStatusMsg;
	// }

	// public String getPgTxnId() {
	// 	return pgTxnId;
	// }

	// public void setPgTxnId(String pgTxnId) {
	// 	this.pgTxnId = pgTxnId;
	// }

	// public String getSubCriteria2() {
	// 	return subCriteria2;
	// }

	// public void setSubCriteria2(String subCriteria2) {
	// 	this.subCriteria2 = subCriteria2;
	// }

	// public RICPAgingReportRequest getRicpReq() {
	// 	return ricpReq;
	// }

	// public void setRicpReq(RICPAgingReportRequest ricpReq) {
	// 	this.ricpReq = ricpReq;
	// }

	// public String getCreateddate() {
	// 	return createddate;
	// }

	// public void setCreateddate(String createddate) {
	// 	this.createddate = createddate;
	// }

	// public String getDuedatestart() {
	// 	return duedatestart;
	// }

	// public void setDuedatestart(String duedatestart) {
	// 	this.duedatestart = duedatestart;
	// }

	// public String getDuedateend() {
	// 	return duedateend;
	// }

	// public void setDuedateend(String duedateend) {
	// 	this.duedateend = duedateend;
	// }

	// public Integer getImpairstatus() {
	// 	return impairstatus;
	// }

	// public void setImpairstatus(Integer impairstatus) {
	// 	this.impairstatus = impairstatus;
	// }

	// public Integer getWriteoffstatus() {
	// 	return writeoffstatus;
	// }

	// public void setWriteoffstatus(Integer writeoffstatus) {
	// 	this.writeoffstatus = writeoffstatus;
	// }

	// public String getEntitytype() {
	// 	return entitytype;
	// }

	// public void setEntitytype(String entitytype) {
	// 	this.entitytype = entitytype;
	// }

	// public String getEntitynm() {
	// 	return entitynm;
	// }

	// public void setEntitynm(String entitynm) {
	// 	this.entitynm = entitynm;
	// }

	// public String getReceiptdatestart() {
	// 	return receiptdatestart;
	// }

	// public void setReceiptdatestart(String receiptdatestart) {
	// 	this.receiptdatestart = receiptdatestart;
	// }

	// public String getReceiptdateend() {
	// 	return receiptdateend;
	// }

	// public void setReceiptdateend(String receiptdateend) {
	// 	this.receiptdateend = receiptdateend;
	// }

	// public String getImpairdatestart() {
	// 	return impairdatestart;
	// }

	// public void setImpairdatestart(String impairdatestart) {
	// 	this.impairdatestart = impairdatestart;
	// }

	// public String getImpairdateend() {
	// 	return impairdateend;
	// }

	// public void setImpairdateend(String impairdateend) {
	// 	this.impairdateend = impairdateend;
	// }

	// public String getWriteoffdatestart() {
	// 	return writeoffdatestart;
	// }

	// public void setWriteoffdatestart(String writeoffdatestart) {
	// 	this.writeoffdatestart = writeoffdatestart;
	// }

	// public String getWriteoffdateend() {
	// 	return writeoffdateend;
	// }

	// public void setWriteoffdateend(String writeoffdateend) {
	// 	this.writeoffdateend = writeoffdateend;
	// }

	// public String getEmail() {
	// 	return email;
	// }

	// public void setEmail(String email) {
	// 	this.email = email;
	// }

	// public UnmatchedAgingReportRequest getUarReq() {
	// 	return uarReq;
	// }

	// public void setUarReq(UnmatchedAgingReportRequest uarReq) {
	// 	this.uarReq = uarReq;
	// }

	// public String getTransDesc() {
	// 	return transDesc;
	// }

	// public void setTransDesc(String transDesc) {
	// 	this.transDesc = transDesc;
	// }

	// public String getEffectivedatestart() {
	// 	return effectivedatestart;
	// }

	// public void setEffectivedatestart(String effectivedatestart) {
	// 	this.effectivedatestart = effectivedatestart;
	// }

	// public String getEffectivedateend() {
	// 	return effectivedateend;
	// }

	// public void setEffectivedateend(String effectivedateend) {
	// 	this.effectivedateend = effectivedateend;
	// }

	// public String getDistatus() {
	// 	return distatus;
	// }

	// public void setDistatus(String distatus) {
	// 	this.distatus = distatus;
	// }

	// public Integer getInctermstatus() {
	// 	return inctermstatus;
	// }

	// public void setInctermstatus(Integer inctermstatus) {
	// 	this.inctermstatus = inctermstatus;
	// }

	// public String getTxntype() {
	// 	return txntype;
	// }

	// public void setTxntype(String txntype) {
	// 	this.txntype = txntype;
	// }

	// public String getExpirydatestart() {
	// 	return expirydatestart;
	// }

	// public void setExpirydatestart(String expirydatestart) {
	// 	this.expirydatestart = expirydatestart;
	// }

	// public String getExpirydateend() {
	// 	return expirydateend;
	// }

	// public void setExpirydateend(String expirydateend) {
	// 	this.expirydateend = expirydateend;
	// }

	// public String getApprovaldatestart() {
	// 	return approvaldatestart;
	// }

	// public void setApprovaldatestart(String approvaldatestart) {
	// 	this.approvaldatestart = approvaldatestart;
	// }

	// public String getApprovaldateend() {
	// 	return approvaldateend;
	// }

	// public void setApprovaldateend(String approvaldateend) {
	// 	this.approvaldateend = approvaldateend;
	// }

	// public String getTerminationdatestart() {
	// 	return terminationdatestart;
	// }

	// public void setTerminationdatestart(String terminationdatestart) {
	// 	this.terminationdatestart = terminationdatestart;
	// }

	// public String getTerminationdateend() {
	// 	return terminationdateend;
	// }

	// public void setTerminationdateend(String terminationdateend) {
	// 	this.terminationdateend = terminationdateend;
	// }

	// public String getBatchno() {
	// 	return batchno;
	// }

	// public void setBatchno(String batchno) {
	// 	this.batchno = batchno;
	// }

	// public String getFmsrefno() {
	// 	return fmsrefno;
	// }

	// public void setFmsrefno(String fmsrefno) {
	// 	this.fmsrefno = fmsrefno;
	// }
}
