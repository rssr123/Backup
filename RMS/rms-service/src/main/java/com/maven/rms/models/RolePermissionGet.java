package com.maven.rms.models;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolePermissionGet {

    private Integer perm_id;
    private Integer is_allow;
    private String status;
    private Integer total;


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

    // public String getStatus() {
    //     return status;
    // }

    // public void setStatus(String status) {
    //     this.status = status;
    // }

    // public Integer getTotal() {
    //     return total;
    // }

    // public void setTotal(Integer total) {
    //     this.total = total;
    // }
}