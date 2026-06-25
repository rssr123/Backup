package com.example.fms.fms.models.payload.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UAMRequestRequest {
	
	private String Name;
	private String ID;
	private String Email;
	private String Status;
	private String Role;
	
	public UAMRequestRequest() {

	}
	public UAMRequestRequest(String Name) {
		this.Name = Name;
	}
	
	public String getName() {
		return Name;
	}
	@JsonProperty("Name")
	public void setName(String Name) {
		this.Name = Name;
	}
	public String getID() {
		return ID;
	}
	@JsonProperty("ID")
	public void setID(String ID) {
		this.ID = ID;
	}
	public String getEmail() {
		return Email;
	}
	@JsonProperty("Email")
	public void setEmail(String Email) {
		this.Email = Email;
	}
	public String getStatus() {
		return Status;
	}
	@JsonProperty("Status")
	public void setStatus(String Status) {
		this.Status = Status;
	}
	public String getRole() {
		return Role;
	}
	@JsonProperty("Role")
	public void setRole(String Role) {
		this.Role = Role;
	}

}
