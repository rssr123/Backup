package com.maven.rms.models.payload.responses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.maven.rms.models.payload.requests.UAMRequestHeader;

import lombok.Getter;

@Getter
@JsonInclude(Include.NON_NULL)
@JsonPropertyOrder({"Header","Response","Roles","UserRoles"})
public class UAMResponsePayload {
	
	private UAMRequestHeader Header;
	private UAMResponseResponse Response;
	private List<UAMResponseGetRoles> Roles;
	private List<UAMResponseUserRoles> UserRoles;
	
	public UAMResponsePayload(UAMRequestHeader Header){
		this.Header = Header;
	}
	public UAMResponsePayload(UAMRequestHeader Header, UAMResponseResponse Response){
		this.Header = Header;
		this.Response = Response;
	}
	/*
	public UAMRequestHeader getHeader() {
		return Header;
	}
	public UAMResponseResponse getResponse() {
		return Response;
	}
	public List<UAMResponseGetRoles> getRoles() {
		return Roles;
	}
	public List<UAMResponseUserRoles> getUserRoles() {
		return UserRoles;
	}
	*/
	@JsonSetter("Header")
	public void setHeader(UAMRequestHeader Header) {
		this.Header = Header;
	}
	@JsonSetter("Response")
	public void setResponse(UAMResponseResponse Response) {
		this.Response = Response;
	}
	@JsonSetter("Role")
	public void setRoles(List<UAMResponseGetRoles> roles) {
		Roles = roles;
	}
	@JsonSetter("UserRole")
	public void setUserRoles(List<UAMResponseUserRoles> userRoles) {
		UserRoles = userRoles;
	}
}
