package com.maven.rms.models;

import java.time.LocalDateTime;
import javax.persistence.Column;
import java.util.Date;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.joda.time.DateTime;

@Entity
@Table(name = "rms_user_role")
public class UserRole {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(name = "user_role_id")
	// private Integer userRoleId;

    @Column(name = "ssm4uuserrefno")
	private String userRef;

	private String name;

    private String email;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole_nm_en() {
        return role_nm_en;
    }

    public void setRole_nm_en(String role_nm_en) {
        this.role_nm_en = role_nm_en;
    }

    public Integer getTotalRoles() {
        return totalRoles;
    }

    public void setTotalRoles(Integer totalRoles) {
        this.totalRoles = totalRoles;
    }

    @Column(name = "role_id")
	private Integer roleId;

    // @Column(name = "dt_created")
	// private LocalDateTime dtCreated;

    // @Column(name = "dt_modified")
	// private LocalDateTime dtModified;

    @Column(name = "created_by")
	private String createdBy;

    @Column(name = "modified_by")
	private String modifiedBy;

	private String status;

    private String role_nm_en;

    private Integer totalRoles;

    // public Integer getUserRoleId() {
    //     return userRoleId;
    // }

    public String getUserRef() {
        return userRef;
    }

    public Integer getRoleId() {
        return roleId;
    }

    // public LocalDateTime getDtCreated() {
    //     return dtCreated;
    // }

    // public LocalDateTime getDtModified() {
    //     return dtModified;
    // }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public String getStatus() {
        return status;
    }

    // public void setUserRoleId(Integer userRoleId) {
    //     this.userRoleId = userRoleId;
    // }

    public void setUserRef(String userRef) {
        this.userRef = userRef;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    // public void setDtCreated(LocalDateTime dtCreated) {
    //     this.dtCreated = dtCreated;
    // }

    // public void setDtModified(LocalDateTime dtModified) {
    //     this.dtModified = dtModified;
    // }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

