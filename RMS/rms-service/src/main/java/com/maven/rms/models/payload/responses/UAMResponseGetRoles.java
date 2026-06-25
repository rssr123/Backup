package com.maven.rms.models.payload.responses;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.maven.rms.models.Role;

import lombok.Getter;

@Getter
@JsonPropertyOrder({"RoleID","RoleName","RoleDescription","RoleSpecial","RoleOwner"})
public class UAMResponseGetRoles {
	private String RoleID;
	private String RoleName;
	private String RoleDescription;
	private String RoleSpecial;
	private String RoleOwner;
	
	public UAMResponseGetRoles(BigInteger RoleID, String RoleName, String RoleDesc, int RoleSpecial, String RoleOwner) {
		this.RoleID = RoleID.toString();
		this.RoleName = RoleName;
		this.RoleDescription = RoleDesc;
		this.RoleSpecial = RoleSpecial > 0 ? "TRUE" : "FALSE";
		this.RoleOwner = RoleOwner;
	}
	/*
	public String getRoleID() {
		return RoleID;
	}
	public String getRoleName() {
		return RoleName;
	}
	public String getRoleDescription() {
		return RoleDescription;
	}
	public String getRoleSpecial() {
		return RoleSpecial;
	}
	public String getRoleOwner() {
		return RoleOwner;
	}
	*/
	@JsonSetter("RoleID")
	public void setRoleID(String roleID) {
		RoleID = roleID;
	}
	@JsonSetter("RoleName")
	public void setRoleName(String roleName) {
		RoleName = roleName;
	}
	@JsonSetter("RoleDescription")
	public void setRoleDescription(String roleDescription) {
		RoleDescription = roleDescription;
	}
	@JsonSetter("RoleSpecial")
	public void setRoleSpecial(String roleSpecial) {
		RoleSpecial = roleSpecial;
	}
	@JsonSetter("RoleOwner")
	public void setRoleOwner(String roleOwner) {
		RoleOwner = roleOwner;
	}
	
	public static List<UAMResponseGetRoles> load(List<Role> roles){
		List<UAMResponseGetRoles> list = new ArrayList<>();
		
		roles.iterator().forEachRemaining((role) -> list.add(
			new UAMResponseGetRoles(role.getRoleId(), role.getRoleNmEn(), role.getRoleDesc()
									, role.getRoleSpecial(), role.getRoleOwner())));
		return list;
	}
}
