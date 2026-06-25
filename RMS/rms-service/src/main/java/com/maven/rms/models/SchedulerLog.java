package com.maven.rms.models;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;

import com.maven.rms.utils.ServerInetUtils;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rms_sch_log", 
uniqueConstraints = { 
		@UniqueConstraint(columnNames = "sch_log_id")})
public class SchedulerLog {
	

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="sch_log_id")
	private Long schLogId;
	
	@Size(max=255)
	@Column(name="sch_nm")
	private String schNm;
	
	@Size(max=500)
	@Column(name="message")
	private String message;
	
	@Column(name="total_txn")
	private int totalTxn;
	
	@Column(name="success_txn")
	private int successTxn;
	
	@Column(name="fail_txn")
	private int failTxn;
	
	@Column(name="dt_created")
	private LocalDateTime dtCreated;
	
	@Column(name="dt_modified")
	private LocalDateTime dtModified;
	
	@Size(max=25)
	@Column(name="created_by")
	private String createdBy;
	
	@Size(max=25)
	@Column(name="modified_by")
	private String modifiedBy;
	
	@Size(max=10)
	@Column(name="status")
	private String status;
	
	@Size(max=50)
	@Column(name="server_ip")
	private String serverIp;
	
	public SchedulerLog() {}
	
	public SchedulerLog(String schNm, String message, int totalTxn) {
		this.schNm = schNm;
		this.message = message;
		this.totalTxn = totalTxn;
		this.successTxn = 0;
		this.failTxn = 0;
		this.dtCreated = LocalDateTime.now();
		this.dtModified = this.dtCreated;
		this.createdBy = "system";
		this.modifiedBy = "system";
		this.status = "A";
	}

}