package com.maven.rms.models;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import com.maven.rms.config.Constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_fee_group")
public class FeeGrp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Integer fee_grp_id;
    private String ss_cd;
    private Integer ss_fee_grp_id;
    private String fee_grp_nm_en;
    private String fee_grp_nm_bm;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dtCreated;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dtModified;
    private String createdBy;
    private String modifiedBy;
    private String status;
    private String status_en;
    private String status_bm;

    // public String getStatus_en() {
    //     return status_en;
    // }

    // public void setStatus_en(String status_en) {
    //     this.status_en = status_en;
    // }

    // public String getStatus_bm() {
    //     return status_bm;
    // }

    // public void setStatus_bm(String status_bm) {
    //     this.status_bm = status_bm;
    // }

    private Integer total;

    // public Integer getTotal() {
    //     return total;
    // }

    // public void setTotal(Integer total) {
    //     this.total = total;
    // }

    // public Integer getFee_grp_id() {
    //     return fee_grp_id;
    // }

    // public void setFee_grp_id(Integer fee_grp_id) {
    //     this.fee_grp_id = fee_grp_id;
    // }

    // public String getFee_grp_nm_en() {
    //     return fee_grp_nm_en;
    // }

    // public void setFee_grp_nm_en(String fee_grp_nm_en) {
    //     this.fee_grp_nm_en = fee_grp_nm_en;
    // }

    // public String getFee_grp_nm_bm() {
    //     return fee_grp_nm_bm;
    // }

    // public void setFee_grp_nm_bm(String fee_grp_nm_bm) {
    //     this.fee_grp_nm_bm = fee_grp_nm_bm;
    // }

    // public Date getDtCreated() {
    //     return dtCreated;
    // }

    // public void setDtCreated(Date dtCreated) {
    //     this.dtCreated = dtCreated;
    // }

    // public Date getDtModified() {
    //     return dtModified;
    // }

    // public void setDtModified(Date dtModified) {
    //     this.dtModified = dtModified;
    // }

    // public String getCreatedBy() {
    //     return createdBy;
    // }

    // public void setCreatedBy(String createdBy) {
    //     this.createdBy = createdBy;
    // }

    // public String getModifiedBy() {
    //     return modifiedBy;
    // }

    // public void setModifiedBy(String modifiedBy) {
    //     this.modifiedBy = modifiedBy;
    // }

    // public String getStatus() {
    //     return status;
    // }

    // public void setStatus(String status) {
    //     this.status = status;
    // }

}
