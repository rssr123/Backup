package com.maven.rms.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditControlCaseRmd {
	private Integer cc_rmd_id;
	private Integer reminder_cnt;
	private LocalDateTime reminder_dt;
	private String reminder_email_content;
	private LocalDateTime reminder_received_date;
	private LocalDateTime dt_created;
	private LocalDateTime dt_modified;
	private String created_by;
	private String modified_by;
	private String status;
	
	public CreditControlCaseRmd() {}
}
