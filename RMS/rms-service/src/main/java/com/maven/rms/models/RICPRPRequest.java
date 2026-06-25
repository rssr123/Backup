package com.maven.rms.models;

import java.math.BigDecimal;
import java.sql.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RICPRPRequest {
    

    @NotNull(message = "cp_no is required.")
    private String cp_no;
    @NotNull(message = "cp_tier is required.")
    private Integer cp_tier;
    @NotNull(message = "cp_tier_amt is required.")
    private BigDecimal cp_tier_amt;
    @NotNull(message = "cp_tier_disc_pct is required.")
    private BigDecimal cp_tier_disc_pct;
    @NotNull(message = "accr_amt is required.")
    private BigDecimal accr_amt;
    @NotNull(message = "dt_collection is required.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT, timezone = "Asia/Singapore")
    private Date dt_collection;






}
