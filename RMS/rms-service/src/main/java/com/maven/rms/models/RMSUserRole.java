package com.maven.rms.models;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

@Entity(name = "RMSUserRole")
@Table(name = "rms_user_role")
public class RMSUserRole implements Serializable{
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id")
	private BigInteger userRoleId;
    
	@Embedded
	private RMSUserRoleId id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("ssm4uuserrefno")
	@JoinColumn(name = "ssm4uuserrefno")
	private RMSUser user;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@MapsId("roleId")
	@JoinColumn(name = "role_id")
	private Role role;

	@Column(name = "dt_created")
	private LocalDateTime dtCreated;
	@Column(name = "dt_modified")
	private LocalDateTime dtModified;
	@Column(name = "created_by")
	private String createdBy;
	@Column(name = "modified_by")
	private String modifiedBy;
	private String status;
	
	private RMSUserRole() {}
	
	public RMSUserRole(RMSUser user, Role role) {
		this.user = user;
		this.role = role;
		this.id = new RMSUserRoleId(user.getSsm4uuserrefno(), role.getRoleId());
		this.dtCreated = LocalDateTime.now();
		this.dtModified = this.dtCreated;
		this.createdBy = user.getCreatedBy();
		this.modifiedBy = user.getModifiedBy();
		this.status = user.getStatus();
	}
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
 
        return this.id.equals(((RMSUserRole)o).getId());
    }
 
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }
    
	public BigInteger getUserRoleId() {
		return userRoleId;
	}

	public void setUserRoleId(BigInteger userRoleId) {
		this.userRoleId = userRoleId;
	}

	public RMSUserRoleId getId() {
		return id;
	}

	public void setId(RMSUserRoleId id) {
		this.id = id;
	}

	public RMSUser getUser() {
		return user;
	}

	public void setUser(RMSUser user) {
		this.user = user;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
