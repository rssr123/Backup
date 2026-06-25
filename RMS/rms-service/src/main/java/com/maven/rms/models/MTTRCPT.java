package com.maven.rms.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rms_mtt_rcpt", 
uniqueConstraints = { 
	@UniqueConstraint(columnNames = "mtt_rcpt_id")})
	public class MTTRCPT {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="mtt_rcpt_id")
	private Integer mttRcptID;
	
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mtt_id", referencedColumnName = "mtt_id")
	private OnlinePayment rmsMTT;
	
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mtt_pg_id", referencedColumnName = "mtt_pg_id")
    private MTTPG mttPG;
    
    @Size(max=20)
	@Column(name="rcpt_no")
    private String rcptNo;
    
	@Column(name="rcpt_dt")
	private LocalDateTime rcptDt;
	
	@Size(max=10)
	@Column(name="rcpt_status")
	private String rcptStatus;
	
	@Column(name="rcpt_reprint")
	private Integer rcptReprint;
	
	@Column(name="is_uploaded")
	private Integer isUploaded;
	
	@Column(name="dt_created")
    private LocalDateTime dtCreated;
	
	@Column(name="dt_modified")
    private LocalDateTime dtModified;
    
	@Size(max=25)
	@Column(name="created_by")
	private String createdBy;
	
	@Size(max=255)
	@Column(name="modified_by")
	private String modifiedBy;
	
	@Size(max=40)
	@Column(name="ssdocref_id")
	private String rcptUUID;
	
	@Size(max=36)
	@Column(name="ver_id")
	private String versionId;

	public MTTRCPT() {}
	
	public MTTRCPT(MTTPG mttPG, String rcptNo, LocalDateTime rcptDt) {
		this.mttPG = mttPG;
		this.rmsMTT = this.mttPG.getRmsMTT();
		this.rcptNo = rcptNo;
		this.rcptDt = rcptDt;
		this.rcptStatus = "A";
		this.rcptReprint = 0;
		this.isUploaded = 0;
		this.dtCreated = LocalDateTime.now();
		this.dtModified = this.dtCreated;
		this.createdBy = "system";
		this.modifiedBy = this.createdBy;
	}
		
	// public Integer getMttRcptID() {
	// 	return mttRcptID;
	// }
	// public void setMttRcptID(Integer mttRcptID) {
	// 	this.mttRcptID = mttRcptID;
	// }
	// public OnlinePayment getRmsMTT() {
	// 	return rmsMTT;
	// }
	// public void setRmsMTT(OnlinePayment rmsMTT) {
	// 	this.rmsMTT = rmsMTT;
	// }
	// public MTTPG getMttPG() {
	// 	return mttPG;
	// }
	// public void setMttPG(MTTPG mttPG) {
	// 	this.mttPG = mttPG;
	// }
	// public String getRcptNo() {
	// 	return rcptNo;
	// }
	// public void setRcptNo(String rcptNo) {
	// 	this.rcptNo = rcptNo;
	// }
	// public LocalDateTime getRcptDt() {
	// 	return rcptDt;
	// }
	// public void setRcptDt(LocalDateTime rcptDt) {
	// 	this.rcptDt = rcptDt;
	// }
	// public String getRcptStatus() {
	// 	return rcptStatus;
	// }
	// public void setRcptStatus(String rcptStatus) {
	// 	this.rcptStatus = rcptStatus;
	// }
	// public Integer getRcptReprint() {
	// 	return rcptReprint;
	// }
	// public void setRcptReprint(Integer rcptReprint) {
	// 	this.rcptReprint = rcptReprint;
	// }
	// public Integer getIsUploaded() {
	// 	return isUploaded;
	// }
	// public void setIsUploaded(Integer isUploaded) {
	// 	this.isUploaded = isUploaded;
	// }
	// public LocalDateTime getDtCreated() {
	// 	return dtCreated;
	// }
	// public void setDtCreated(LocalDateTime dtCreated) {
	// 	this.dtCreated = dtCreated;
	// }
	// public LocalDateTime getDtModified() {
	// 	return dtModified;
	// }
	// public void setDtModified(LocalDateTime dtModified) {
	// 	this.dtModified = dtModified;
	// }
	// public String getCreatedBy() {
	// 	return createdBy;
	// }
	// public void setCreatedBy(String createdBy) {
	// 	this.createdBy = createdBy;
	// }
	// public String getModifiedBy() {
	// 	return modifiedBy;
	// }
	// public void setModifiedBy(String modifiedBy) {
	// 	this.modifiedBy = modifiedBy;
	// }

	// public String getRcptUUID() {
	// 	return rcptUUID;
	// }

	// public void setRcptUUID(String rcptUUID) {
	// 	this.rcptUUID = rcptUUID;
	// }

	// public String getVersionId() {
	// 	return versionId;
	// }

	// public void setVersionId(String versionId) {
	// 	this.versionId = versionId;
	// }
}
