package com.maven.rms.models.payload.responses;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.maven.rms.models.RMSUser;

import lombok.Getter;

@Getter
@JsonPropertyOrder({"ID","RoleID"})
public class UAMResponseUserRoles {
	private String ID;
	private String RoleID;
	
	public UAMResponseUserRoles(String ID, String RoleID) {
		this.ID = ID;
		this.RoleID = RoleID;
	}
	/*
	public String getID() {
		return ID;
	}
	public String getRoleID() {
		return RoleID;
	}
	*/
	@JsonSetter("ID")
	public void setID(String ID) {
		this.ID = ID;
	}
	@JsonSetter("RoleID")
	public void setRoleID(String RoleID) {
		this.RoleID = RoleID;
	}
	public static List<UAMResponseUserRoles> load(List<RMSUser> users) {
		List<UAMResponseUserRoles> list = new ArrayList<>();
		
		users.iterator().forEachRemaining((user) -> {
			StringBuilder concatRoleNames = new StringBuilder();
			if(user.getIsInternalUser() == 1 && user.getStatus().equals("A")) {
				user.getRoles().iterator().forEachRemaining(
						(role) -> {if(role.getStatus().equals("A") && user.getStatus().equals("A"))
									concatRoleNames.append(role.getRole().getRoleNmEn() + ",");});
			
				list.add(new UAMResponseUserRoles(user.getSsm4uuserrefno(), concatRoleNames.length() > 0 ?
						concatRoleNames.substring(0, concatRoleNames.length()-1) : ""));
			}
		});
		return list;
	}
	
}
