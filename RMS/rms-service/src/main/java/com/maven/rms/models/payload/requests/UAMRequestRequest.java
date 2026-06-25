package com.maven.rms.models.payload.requests;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class UAMRequestRequest {
	
	private String Name;
	private String ID;
	private String Email;
	private String Status;
	private String Role;
	
	public UAMRequestRequest() {}
	
	public UAMRequestRequest(String Name) {
		this.Name = Name;
	}
	/*
	public String getName() {
		return Name;
	}
	public String getID() {
		return ID;
	}
	public String getEmail() {
		return Email;
	}
	public String getStatus() {
		return Status;
	}
	public String getRole() {
		return Role;
	}
	*/
	@JsonProperty("Name")
	public void setName(String Name) {
		this.Name = Name;
	}
	@JsonProperty("ID")
	public void setID(String ID) {
		this.ID = ID;
	}
	@JsonProperty("Email")
	public void setEmail(String Email) {
		this.Email = Email;
	}
	@JsonProperty("Status")
	public void setStatus(String Status) {
		this.Status = Status;
	}
	@JsonProperty("Role")
	public void setRole(String Role) {
		this.Role = Role;
	}

	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name=").append(Name);
        sb.append(", ID=").append(ID);
        sb.append(", Email=").append(Email);
        sb.append(", Status=").append(Status);
        sb.append(", Role=").append(Role);
        return sb.toString();
    }

}
