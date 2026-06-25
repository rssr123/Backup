package com.maven.rms.models;


import java.math.BigInteger;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleRequest {
    private Integer i_page;

    private Integer i_size;

    private BigInteger i_r_id;

    private String i_r_role_nm_en;

    private String i_r_role_nm_bm;

    private String i_created_by;

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

    // public BigInteger getI_r_id() {
    //     return i_r_id;
    // }

    // public void setI_r_id(BigInteger i_r_id) {
    //     this.i_r_id = i_r_id;
    // }

    // public String getI_r_role_nm_en() {
    //     return i_r_role_nm_en;
    // }

    // public void setI_r_role_nm_en(String i_r_role_nm_en) {
    //     this.i_r_role_nm_en = i_r_role_nm_en;
    // }

    // public String getI_r_role_nm_bm() {
    //     return i_r_role_nm_bm;
    // }

    // public void setI_r_role_nm_bm(String i_r_role_nm_bm) {
    //     this.i_r_role_nm_bm = i_r_role_nm_bm;
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

    // public String getI_created_by() {
    //     return i_created_by;
    // }

    // public void setI_created_by(String i_created_by) {
    //     this.i_created_by = i_created_by;
    // }
}
