package com.maven.rms.models;

import java.math.BigInteger;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RolePermissionGetRequest {

    private BigInteger i_role_perm_id;
    private Integer i_role_id;
    private Integer i_perm_id;
    private Integer i_is_allow;
    private String i_status;

    // public BigInteger getI_role_perm_id() {
    //     return i_role_perm_id;
    // }

    // public void setI_role_perm_id(BigInteger i_role_perm_id) {
    //     this.i_role_perm_id = i_role_perm_id;
    // }

    // public Integer getI_role_id() {
    //     return i_role_id;
    // }

    // public void setI_role_id(Integer i_role_id) {
    //     this.i_role_id = i_role_id;
    // }

    // public Integer getI_perm_id() {
    //     return i_perm_id;
    // }

    // public void setI_perm_id(Integer i_perm_id) {
    //     this.i_perm_id = i_perm_id;
    // }

    // public Integer getI_is_allow() {
    //     return i_is_allow;
    // }

    // public void setI_is_allow(Integer i_is_allow) {
    //     this.i_is_allow = i_is_allow;
    // }

    // public String getI_status() {
    //     return i_status;
    // }

    // public void setI_status(String i_status) {
    //     this.i_status = i_status;
    // }

}
