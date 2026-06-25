package com.maven.rms.models;


import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceSystemCodeRequest {

  
    private Integer i_page;
    private Integer i_size;
    private BigInteger i_ss_id;
    private String i_ss_cd;
    private String i_ss_nm;
    private String i_modified_by;
    private Date i_dt_modified_fr;
    private Date i_dt_modified_to;
    private String i_status;
    
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

    // public BigInteger getI_ss_id() {
    //     return i_ss_id;
    // }

    // public void setI_ss_id(BigInteger i_ss_id) {
    //     this.i_ss_id = i_ss_id;
    // }

    // public String getI_ss_cd() {
    //     return i_ss_cd;
    // }

    // public void setI_ss_cd(String i_ss_cd) {
    //     this.i_ss_cd = i_ss_cd;
    // }

    // public String getI_ss_nm() {
    //     return i_ss_nm;
    // }

    // public void setI_ss_nm(String i_ss_nm) {
    //     this.i_ss_nm = i_ss_nm;
    // }

    // public String getI_modified_by() {
    //     return i_modified_by;
    // }

    // public void setI_modified_by(String i_modified_by) {
    //     this.i_modified_by = i_modified_by;
    // }

    // public Date getI_dt_modified_fr() {
    //     return i_dt_modified_fr;
    // }

    // public void setI_dt_modified_fr(Date i_dt_modified_fr) {
    //     this.i_dt_modified_fr = i_dt_modified_fr;
    // }

    // public Date getI_dt_modified_to() {
    //     return i_dt_modified_to;
    // }

    // public void setI_dt_modified_to(Date i_dt_modified_to) {
    //     this.i_dt_modified_to = i_dt_modified_to;
    // }

    // public String getI_status() {
    //     return i_status;
    // }

    // public void setI_status(String i_status) {
    //     this.i_status = i_status;
    // }



    
    
}
