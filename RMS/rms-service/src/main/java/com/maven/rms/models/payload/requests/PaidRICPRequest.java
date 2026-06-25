package com.maven.rms.models.payload.requests;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaidRICPRequest {
	private String entity_type;
	private String entity_no;
	private String cp_no;
	private Integer mtt_item_id;
	
	public PaidRICPRequest() {}

	/*
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
	*/
}
