package com.maven.rms.models.payload.requests;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitRICPCanRequest {

	@NotNull(message = "entity_type is required.")
    @Size(min = 1, max = 1, message="entity_type is required.")
	private String entity_type;
	@NotNull(message = "entity_no is required.")
    @Size(min = 1, max = 40, message="entity_no is required.")
	private String entity_no;
	@NotNull(message = "cp_no is required.")
    @Size(min = 1, max = 20, message="cp_no is required.")
	private String cp_no;
	@NotNull(message = "Status is required.")
    @Size(min = 1, max = 10, message="Status is required.")
	private String status;
	private Integer mtt_item_id;
	
	public SubmitRICPCanRequest() {}
	
	public SubmitRICPCanRequest(String entity_type, String entity_no, String cp_no, String status, Integer mtt_item_id) {
		this.entity_type = entity_type;
		this.entity_no = entity_no;
		this.cp_no = cp_no;
		this.status = status;
		this.mtt_item_id = mtt_item_id;
	}
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
	public String getStatus() {
		return Status;
	}
	@JsonProperty("status")
	public void setStatus(String status) {
		this.status = status;
	}
	*/
}
