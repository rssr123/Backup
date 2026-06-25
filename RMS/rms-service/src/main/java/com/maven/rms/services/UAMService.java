package com.maven.rms.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maven.rms.interfaces.IUAMService;
import com.maven.rms.models.Role;
import com.maven.rms.models.RMSUser;
import com.maven.rms.models.RMSUserRole;
import com.maven.rms.repositories.RoleRepository;
import com.maven.rms.repositories.UserRepository;

@Service
@Slf4j
public class UAMService implements IUAMService {

	//private static final Logger logger = LoggerFactory.getLogger(UAMService.class);
	private final RoleRepository roleRepository;
	private final UserRepository userRepository;

	public UAMService(RoleRepository roleRepository, UserRepository userRepository) {
		this.roleRepository = roleRepository;
		this.userRepository = userRepository;
	}

	@Transactional(readOnly=true)
	@Override
	public List<Role> sp_getRole() {
		List<Role> result = Collections.emptyList();
		// try {
			result = roleRepository.findAll();
		// } catch (Exception e) {
		// 	log.error("Exception in " + this.getClass().toString(), e);
		// }
		return result;
	}

	@Override
	public String sp_createAccount(RMSUser user, Set<String> strRoles) {
		RMSUser repoUser = userRepository.findRMSUserBySsm4uuserrefno(user.getSsm4uuserrefno()).orElse(null);
		if (repoUser != null) {
			for(RMSUserRole r : repoUser.getRoles())
				r.setStatus("A");
			repoUser.setDtModified(LocalDateTime.now());
		}
		else
			repoUser = user;
		repoUser.setStatus("A");
		//getRoles(strRoles).forEach(repoUser::addRole);

		Set<Role> rolesToAdd = getRoles(strRoles);
		Set<Role> rolesToRemove = new HashSet<Role>();
		
		for(RMSUserRole r : repoUser.getRoles()) {
			if(rolesToAdd.contains(r.getRole()) && r.getStatus().equals("A"))
				rolesToAdd.remove(r.getRole());
			else
				rolesToRemove.add(r.getRole());
		}
		
		rolesToRemove.forEach(repoUser::removeRole);
		rolesToAdd.forEach(repoUser::addRole);
		
		return persistData(repoUser);
	}

	@Override
	public String sp_updateAccount(RMSUser user, Set<String> strRoles) {
		RMSUser repoUser = userRepository.findRMSUserBySsm4uuserrefno(user.getSsm4uuserrefno()).orElse(null);
		if(repoUser == null)
			return "Failed to find user in repo";
		repoUser.setDtModified(LocalDateTime.now());
		repoUser.setModifiedBy(user.getCreatedBy());
		repoUser.setNm(user.getNm());
		repoUser.setEmail(user.getEmail());
		repoUser.setStatus(user.getStatus());
		//repoUser.setRoles(getRoles(strRoles));
		Set<Role> rolesToAdd = getRoles(strRoles);
		Set<Role> rolesToRemove = new HashSet<Role>();
		
		for(RMSUserRole r : repoUser.getRoles()) {
			if(rolesToAdd.contains(r.getRole()) && r.getStatus().equals("A"))
				rolesToAdd.remove(r.getRole());
			else
				rolesToRemove.add(r.getRole());
		}
		
		rolesToRemove.forEach(repoUser::removeRole);
		rolesToAdd.forEach(repoUser::addRole);
		repoUser.setDtModified(LocalDateTime.now());
		return persistData(repoUser).equals("Successfully saved user.") ? 
					"Successfully updated user." : "Failed to update user.";
	}
	
	@Transactional
	@Override
	public String persistData(RMSUser user) {
		try {
			user = userRepository.save(user);
			return "Successfully saved user.";
		 } catch (Exception e) {
		 	log.error("Failed to save/modify user.", e);
		 	return "Failed to save/modify user.";
		 }
	}
	
	@Transactional(readOnly=true)
	public Set<Role> getRoles(Set<String> strRoles){
		Set<Role> roles = new HashSet<>();
		if (strRoles == null || strRoles.size() == 0) 
			throw(new RuntimeException("Error: strRoles list empty."));
		
		strRoles.forEach(roleName -> {
			if(!roleName.equals("")) {
				Role role = roleRepository.findRoleByRoleNmEn(roleName)
						.orElseThrow(() -> new RuntimeException("Error: Role '" + roleName + "'  not found. "
																+ "Set size: " + Integer.toString(strRoles.size())));
				roles.add(role);
			}
		});
		return roles;
	}

	@Transactional
	@Override
	public String sp_deleteAccount(String username, String modifiedBy) {
		RMSUser repoUser = userRepository.findRMSUserBySsm4uuserrefno(username).orElse(null);
		
		if(repoUser == null)
			return "Failed to find user in repo";
		if(repoUser.getStatus().equals("D"))
			return "Already set to 'D'";
		
		log.info("Setting user (" + repoUser.getSsm4uuserrefno() + ") status: '" + repoUser.getSsm4uuserrefno() + "' to 'D'...");
		
		repoUser.setModifiedBy(modifiedBy);
		repoUser.setStatus("D");
		for(RMSUserRole r : repoUser.getRoles()) {
			r.setStatus("D");
			r.setDtModified(LocalDateTime.now());
		}
		repoUser.setDtModified(LocalDateTime.now());
		userRepository.save(repoUser);
		
		log.info("Records for user '" + username + "' set to 'D' in database!");
		
		return persistData(repoUser).equals("Successfully saved user.") ?
				"Successfully deleted user." : "Failed to delete user.";
	}

	@Override
	public List<RMSUser> sp_getUsers(String username) {
		if (username.toLowerCase().equals("all"))
			return userRepository.findAll();
		RMSUser user = userRepository.findRMSUserBySsm4uuserrefno(username).orElse(null);
		return user == null ? Collections.emptyList() : new ArrayList<>(Arrays.asList(user));
	}

	public Optional<RMSUser> findUserByEmail(String email) {
		return userRepository.findRMSUserByEmail(email);
	}
	
	public Optional<RMSUser> findUserByUsername(String username){
		return userRepository.findRMSUserBySsm4uuserrefno(username);
	}
	
	public UserRepository returnUserRepo() {
		return userRepository;
	}
	
	public Integer sp_updatessm4uuserrefno(String username, String email) {
		return userRepository.sp_updatessm4uuserrefno(username, email);
	}
}
