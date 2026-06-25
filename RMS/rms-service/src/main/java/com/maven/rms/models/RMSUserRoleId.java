package com.maven.rms.models;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class RMSUserRoleId implements Serializable {
	
	@Column(name = "ssm4uuserrefno")
    private String ssm4uuserrefno;
 
    @Column(name = "role_id")
    private BigInteger roleId;
    
    private RMSUserRoleId() {}
    
    public RMSUserRoleId(String ssm4uuserrefno, BigInteger roleId) {
    	this.ssm4uuserrefno = ssm4uuserrefno;
    	this.roleId = roleId;
    }
    
	public String getSsm4uuserrefno() {
		return ssm4uuserrefno;
	}

	public void setSsm4uuserrefno(String ssm4uuserrefno) {
		this.ssm4uuserrefno = ssm4uuserrefno;
	}

	public BigInteger getRoleId() {
		return roleId;
	}

	public void setRoleId(BigInteger roleId) {
		this.roleId = roleId;
	}
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
 
        if (o == null || getClass() != o.getClass())
            return false;
        RMSUserRoleId that = (RMSUserRoleId) o;
        return Objects.equals(ssm4uuserrefno, that.ssm4uuserrefno) &&
               Objects.equals(roleId, that.roleId);
    }
 
    @Override
    public int hashCode() {
        return Objects.hash(ssm4uuserrefno, roleId);
    }
}
