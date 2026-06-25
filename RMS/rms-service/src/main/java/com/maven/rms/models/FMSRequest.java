package com.maven.rms.models;

import java.math.BigDecimal;
import java.sql.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSRequest {
    private Integer i_page;
    private Integer i_size;
    private Integer i_fms_id;
    private String i_fms_cd;
    private String i_modified_by;
    private Date i_dt_modified_fr;
    private Date i_dt_modified_to;
    private String i_status;
    private Integer i_is_active;

    // public Integer getI_fms_id() {
    //     return i_fms_id;
    // }

    // public void setI_fms_id(Integer i_fms_id) {
    //     this.i_fms_id = i_fms_id;
    // }

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

    // public String getI_fms_cd() {
    //     return i_fms_cd;
    // }

    // public void setI_fms_cd(String i_fms_cd) {
    //     this.i_fms_cd = i_fms_cd;
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

    //  public Integer getI_is_active() {
    //     return i_is_active;
    // }

    // public void setI_is_active(Integer i_is_active) {
    //     this.i_is_active = i_is_active;
    // }
}
