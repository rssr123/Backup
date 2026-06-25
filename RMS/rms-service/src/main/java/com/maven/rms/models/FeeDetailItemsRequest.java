package com.maven.rms.models;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;
import javax.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FeeDetailItemsRequest {

    private String fee_detail_id;
    private Integer fee_grp_id;
    private String ss_cd;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT_YEAR_MONTH_DAY, timezone = "Asia/Singapore")
    private LocalDateTime last_sync_dt;
    @NotNull(message = "exclude_deleted is required.")
    private Integer exclude_deleted;
    // public String getFee_detail_id() {
    // return fee_detail_id;
    // }
    // public void setFee_detail_id(String fee_detail_id) {
    // this.fee_detail_id = fee_detail_id;
    // }
    // public Integer getFee_grp_id() {
    // return fee_grp_id;
    // }
    // public void setFee_grp_id(Integer fee_grp_id) {
    // this.fee_grp_id = fee_grp_id;
    // }
    // public String getSs_cd() {
    // return ss_cd;
    // }
    // public void setSs_cd(String ss_cd) {
    // this.ss_cd = ss_cd;
    // }
    // public LocalDateTime getLast_sync_dt() {
    // return last_sync_dt;
    // }
    // public void setLast_sync_dt(LocalDateTime last_sync_dt) {
    // this.last_sync_dt = last_sync_dt;
    // }
    // public Integer getExclude_deleted() {
    // return exclude_deleted;
    // }
    // public void setExclude_deleted(Integer exclude_deleted) {
    // this.exclude_deleted = exclude_deleted;
    // }

}
