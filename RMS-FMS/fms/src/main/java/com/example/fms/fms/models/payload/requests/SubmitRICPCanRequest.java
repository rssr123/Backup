package com.example.fms.fms.models.payload.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SubmitRICPCanRequest {

	private String entity_type;
	private String entity_no;
	private String cp_no;
	private String Status;
	
	public SubmitRICPCanRequest() {}
	
	public String getEntity_type() {
		return entity_type;
	}
	public void setEntity_type(String entity_type) {
		this.entity_type = entity_type;
	}
	public String getEntity_no() {
		return entity_no;
	}
	public void setEntity_no(String entity_no) {
		this.entity_no = entity_no;
	}
	public String getCp_no() {
		return cp_no;
	}
	public void setCp_no(String cp_no) {
		this.cp_no = cp_no;
	}
	public String getStatus() {
		return Status;
	}
	@JsonProperty("Status")
	public void setStatus(String Status) {
		this.Status = Status;
	}
}
