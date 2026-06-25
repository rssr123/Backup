package com.maven.rms.models;

import javax.persistence.Column;
import java.util.Date;

import javax.persistence.Id;

import java.math.BigInteger;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

@Entity(name = "rms_role")
@Table(name = "rms_role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
	private BigInteger roleId;
    @Column(name = "role_nm_en")
	private String roleNmEn;
    @Column(name = "role_nm_bm")
	private String roleNmBm;
    @Column(name = "role_desc")
	private String roleDesc;
    @Column(name = "role_special")
	private int roleSpecial;
    @Column(name = "role_owner")
	private String roleOwner;
    @Column(name = "dt_created")
	private Date dtCreated;
    @Column(name = "dt_modified")
	private Date dtModified;
    @Column(name = "created_by")
	private String createdBy;
    @Column(name = "modified_by")
	private String modifiedBy;
	private String status;	
	
	public BigInteger getRoleId() {
		return roleId;
	}

	public void setRoleId(BigInteger roleId) {
		this.roleId = roleId;
	}

	public String getRoleNmEn() {
		return roleNmEn;
	}
	
	public void setRoleNmEn(String roleNmEn) {
		this.roleNmEn = roleNmEn;
	}

	public String getRoleNmBm() {
		return roleNmBm;
	}

	public void setRoleNmBm(String roleNmBm) {
		this.roleNmBm = roleNmBm;
	}

	public String getRoleDesc() {
		return roleDesc;
	}

	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}

	public int getRoleSpecial() {
		return roleSpecial;
	}

	public void setRoleSpecial(int roleSpecial) {
		this.roleSpecial = roleSpecial;
	}

	public String getRoleOwner() {
		return roleOwner;
	}

	public void setRoleOwner(String roleOwner) {
		this.roleOwner = roleOwner;
	}

	public Date getDtCreated() {
		return dtCreated;
	}

	public void setDtCreated(Date dtCreated) {
		this.dtCreated = dtCreated;
	}

	public Date getDtModified() {
		return dtModified;
	}

	public void setDtModified(Date dtModified) {
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
