package com.maven.rms.models;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.maven.rms.services.EmailService;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Getter
@Setter
@Entity
@Table(name = "rms_email", 
uniqueConstraints = { 
		@UniqueConstraint(columnNames = "email_id")})
public class Email {	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="email_id")
	private Long emailId;
	
	@Column(name="email_type")
	private String emailType;		//May be: Receipt, Notification, Error, etc....
	
	private String to;
	private String cc;
	private String bcc;
	private String subject;
	private String body;
	
	@Column(name="retry_cnt")
	private int retryCnt;
	
	@Column(name="dt_created")
	private LocalDateTime dtCreated;
	
	@Column(name="dt_modified")
	private LocalDateTime dtModified;
	
	@Column(name="created_by")
	private String createdBy;
	
	@Column(name="modified_by")
	private String modifiedBy;
	
	private String status;
	
	private String ref_info;
	
	@Column(name="atchmt_pth")
	private String attachmentPath;
	
	public Email() {}
	
	public Email(String emailType, String to, String cc, String bcc, 
			String subject, String body, File attachment) {
		this.emailType = emailType;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.subject = subject;
		this.body = body;
		this.createdBy = "system";
		this.modifiedBy = this.createdBy;
		this.dtCreated = LocalDateTime.now();
		this.dtModified = this.dtCreated;
		this.retryCnt = 0;
		//P=Pending, S=Sent, F=Failed, D=Deleted
		this.status = "P";
		this.attachmentPath = attachment != null ? attachment.getAbsolutePath() : null;
	}
	
	public Email(String emailType, String to, String cc, String bcc, 
			String subject, String body, String ref_info, File attachment) {
		this.emailType = emailType;
		this.to = to;
		this.cc = cc;
		this.bcc = bcc;
		this.subject = subject;
		this.body = body;
		this.createdBy = "system";
		this.modifiedBy = this.createdBy;
		this.dtCreated = LocalDateTime.now();
		this.dtModified = this.dtCreated;
		this.retryCnt = 0;
		//P=Pending, S=Sent, F=Failed, D=Deleted
		this.status = "P";
		this.ref_info = ref_info;
		this.attachmentPath = attachment != null ? attachment.getAbsolutePath() : null;
	}

}
