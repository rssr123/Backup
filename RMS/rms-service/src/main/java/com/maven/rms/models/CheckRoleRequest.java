package com.maven.rms.models;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckRoleRequest {

    private String i_username;
    private String i_perm_cd;
    
    // public String getI_username() {
    //     return i_username;
    // }
    // public void setI_username(String i_username) {
    //     this.i_username = i_username;
    // }
    // public String getI_perm_cd() {
    //     return i_perm_cd;
    // }
    // public void setI_perm_cd(String i_perm_cd) {
    //     this.i_perm_cd = i_perm_cd;
    // }
    
}
