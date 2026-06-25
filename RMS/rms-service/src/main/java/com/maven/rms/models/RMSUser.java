package com.maven.rms.models;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;


@Entity(name = "rms_user")
@Table(name = "rms_user", 
uniqueConstraints = { 
		@UniqueConstraint(columnNames = "ssm4uuserrefno"),
		@UniqueConstraint(columnNames = "nm"),
		@UniqueConstraint(columnNames = "email") })
public class RMSUser implements Serializable {
	@Id
	@Size(max=25)
	private String ssm4uuserrefno;
	private String nm;
	private String email;
	@Column(name = "dt_created")
	private LocalDateTime dtCreated;
	@Column(name = "dt_modified")
	private LocalDateTime dtModified;
	@Column(name = "created_by")
	private String createdBy;
	@Column(name = "modified_by")
	private String modifiedBy;
	private String status;
	@Column(name = "is_internal_user")
	private Integer isInternalUser;
	@Column(name = "user_status")
	private String userVerificationStatus;

	@Column(name = "session_id")
	private String sessionId;
	//Eager required for streaming user roles
	/*
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "rms_user_role", 
	joinColumns = @JoinColumn(name = "ssm4uuserrefno"), 
	inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();
	*/
	
	@OneToMany(mappedBy = "user",
	        cascade = CascadeType.ALL,
	        orphanRemoval = true, fetch = FetchType.EAGER)
	private Set<RMSUserRole> roles = new HashSet<RMSUserRole>();
	
	public RMSUser() {}

	public RMSUser(String ID, String name, String email, String status, String createdBy, Integer isInternalUser) {
		this.ssm4uuserrefno = ID.trim();
		this.nm = name.trim();
		this.email = email.trim();
		this.status = status;
		this.dtCreated = LocalDateTime.now();
		this.dtModified = dtCreated;
		this.createdBy = createdBy;
		this.modifiedBy = createdBy;
		this.isInternalUser = isInternalUser;
	}

	public void addRole(Role role) {
		for (Iterator<RMSUserRole> i = roles.iterator();
				i.hasNext();) {
			RMSUserRole u = i.next();
			
			if(u.getRole().equals(role) &&  u.getUser().equals(this)) {
				if(!u.getStatus().equals("A")) {
					u.setDtModified(LocalDateTime.now());
					u.setStatus("A");
				}
				return;
			}
		}
		RMSUserRole userRole = new RMSUserRole(this, role);
		roles.add(userRole);
	}
	
	public void removeRole(Role role) {
		for (Iterator<RMSUserRole> i = roles.iterator();
				i.hasNext();) {
			RMSUserRole u = i.next();
			
			if(u.getRole().equals(role) &&  u.getUser().equals(this)) {
				//i.remove();
				//u.setRole(null);
				//u.setUser(null);
				u.setDtModified(LocalDateTime.now());
				u.setStatus("D");
			}
		}
	}
	
	public String getSsm4uuserrefno() {
		return ssm4uuserrefno;
	}

	public void setSsm4uuserrefno(String ssm4uuserrefno) {
		this.ssm4uuserrefno = ssm4uuserrefno.trim();
	}

	public String getNm() {
		return nm;
	}

	public void setNm(String nm) {
		this.nm = nm.trim();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email.trim();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Set<RMSUserRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<RMSUserRole> roles) {
		this.roles = roles;
	}

	public LocalDateTime getDtCreated() {
		return dtCreated;
	}

	public void setDtCreated(LocalDateTime dtCreated) {
		this.dtCreated = dtCreated;
	}

	public LocalDateTime getDtModified() {
		return dtModified;
	}

	public void setDtModified(LocalDateTime dtModified) {
		this.dtModified = dtModified;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public String getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(String modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Integer getIsInternalUser() {
		return isInternalUser;
	}

	public void setIsInternalUser(Integer isInternalUser) {
		this.isInternalUser = isInternalUser;
	}

	public String getUserVerificationStatus() {
		return userVerificationStatus;
	}

	public void setUserVerificationStatus(String userVerificationStatus) {
		this.userVerificationStatus = userVerificationStatus;
	}
	
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	
	public String getSessionId() {
		return this.sessionId;
	}
	
	public boolean isLoggedIn(String sessionId) {
		//SSID will remain the same for the same browser session, so there should be seamless connectivity if a cluster is down
		return this.sessionId.equals(sessionId);
	}
}