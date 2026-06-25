package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCCheckInRequest {
	private String sessionId;
	private String user_id;
	private String user_email;
	private String counter_id;
	private String branch_cd;
	
	public OTCCheckInRequest(String sessionId, String user_id, String user_email, String counter_id, String branch_cd) {
		this.sessionId = sessionId;
		this.user_id = user_id;
		this.user_email = user_email;
		this.counter_id = counter_id;
		this.branch_cd = branch_cd;
	}
}
