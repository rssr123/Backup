package com.maven.rms.models;

import java.time.LocalDateTime;
import javax.persistence.Column;
import java.util.Date;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "rms_role_perm")
public class RolePermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_perm_id")
	private BigInteger role_perm_id;
    @Column(name = "role_id")
	private Integer role_id;
    @Column(name = "perm_id")
	private Integer perm_id;
    @Column(name = "is_allow")
	private Integer is_allow;
    @Column(name = "dt_created")
	private Date dt_created;
    @Column(name = "dt_modified")
	private Date dt_modified;
    @Column(name = "created_by")
	private String created_by;
    @Column(name = "modified_by")
	private String modified_by;
	private String status;

    // public BigInteger getRole_perm_id() {
    //     return role_perm_id;
    // }

    // public void setRole_perm_id(BigInteger role_perm_id) {
    //     this.role_perm_id = role_perm_id;
    // }

    // public Integer getRole_id() {
    //     return role_id;
    // }

    // public void setRole_id(Integer role_id) {
    //     this.role_id = role_id;
    // }

    // public Integer getPerm_id() {
    //     return perm_id;
    // }

    // public void setPerm_id(Integer perm_id) {
    //     this.perm_id = perm_id;
    // }

    // public Integer getIs_allow() {
    //     return is_allow;
    // }

    // public void setIs_allow(Integer is_allow) {
    //     this.is_allow = is_allow;
    // }

    // public Date getDt_created() {
    //     return dt_created;
    // }

    // public void setDt_created(Date dt_created) {
    //     this.dt_created = dt_created;
    // }

    // public Date getDt_modified() {
    //     return dt_modified;
    // }

    // public void setDt_modified(Date dt_modified) {
    //     this.dt_modified = dt_modified;
    // }

    // public String getCreated_by() {
    //     return created_by;
    // }

    // public void setCreated_by(String created_by) {
    //     this.created_by = created_by;
    // }

    // public String getModified_by() {
    //     return modified_by;
    // }
    // public void setModified_by(String modified_by) {
    //     this.modified_by = modified_by;
    // }

    // public String getStatus() {
    //     return status;
    // }

    // public void setStatus(String status) {
    //     this.status = status;
    // }
	
}