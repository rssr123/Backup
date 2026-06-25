package com.maven.rms.models.payload.responses;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;

import lombok.Getter;

@Getter
@JsonPropertyOrder({"status","message"})
public class UAMResponseResponse {
	private String status;
	private String message;
	
	public UAMResponseResponse(String status, String message) {
		this.status = status;
		this.message = message;
	}
	/*
	public String getStatus() {
		return status;
	}
	public String getMessage() {
		return message;
	}
	*/
	@JsonSetter("status")
	public void setStatus(String status) {
		this.status = status;
	}
	@JsonSetter("message")
	public void setMessage(String message) {
		this.message = message;
	}
}
