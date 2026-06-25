package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParamRequest {
    

    private Integer i_page;
    private Integer i_size;
    private String i_param_cd;
    private String i_param_grp_nm;
    
    // public Integer getI_page() {
    //     return i_page;
    // }
    // public void setI_page(Integer i_page) {
    //     this.i_page = i_page;
    // }
    // public Integer getI_size() {
    //     return i_size;
    // }
    // public void setI_size(Integer i_size) {
    //     this.i_size = i_size;
    // }
    // public String getI_param_cd() {
    //     return i_param_cd;
    // }
    // public void setI_param_cd(String i_param_cd) {
    //     this.i_param_cd = i_param_cd;
    // }
    // public String getI_param_grp_nm() {
    //     return i_param_grp_nm;
    // }
    // public void setI_param_grp_nm(String i_param_grp_nm) {
    //     this.i_param_grp_nm = i_param_grp_nm;
    // }
    public ParamRequest(Integer i_page, Integer i_size, String i_param_cd, String i_param_grp_nm) {
        this.i_page = i_page;
        this.i_size = i_size;
        this.i_param_cd = i_param_cd;
        this.i_param_grp_nm = i_param_grp_nm;
    }

    
 














}
