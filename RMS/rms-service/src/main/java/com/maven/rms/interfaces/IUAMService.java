package com.maven.rms.interfaces;

import java.util.List;
import java.util.Set;

import com.maven.rms.models.Role;
import com.maven.rms.models.RMSUser;

public interface IUAMService {
	List<Role> sp_getRole();
	String sp_createAccount(RMSUser user, Set<String> strRoles);
	String sp_updateAccount(RMSUser user, Set<String> strRoles);
	String persistData(RMSUser user);
	Set<Role> getRoles(Set<String> strRoles);
	String sp_deleteAccount(String username, String modifiedBy);
	List<RMSUser> sp_getUsers(String username);
}
