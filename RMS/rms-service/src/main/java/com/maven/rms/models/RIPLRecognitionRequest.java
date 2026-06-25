package com.maven.rms.models;

import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RIPLRecognitionRequest {
    
    @NotNull(message = "txn_type is required.")
    @Size(min = 1, max = 5, message="txn_type is required.")
    private String txn_type;
    @NotNull(message = "entity_type is required.")
    @Size(min = 1, max = 1, message="entity_type is required.")
    private String entity_type;
    @NotNull(message = "entity_no is required.")
    @Size(min = 1, max = 40, message="entity_no is required.")
    private String entity_no;
    @NotNull(message = "calendar_yr is required.")
    @Size(min = 1, max = 5, message="calendar_yr is required.")
    private String calendar_yr;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    // @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "d/M/yyyy", timezone = "Asia/Singapore")
    @NotNull(message = "dt_due is required.")
    @Size(min = 1, max = 10, message="dt_due invalid format.")
    private String dt_due;
    // @NotNull(message = "ripl_ctype is required.")
    // @Size(min = 1, max = 10, message="ripl_ctype is required.")
    private String ripl_ctype;
    private String created_by;
    private String modified_by;

    // 2025-06-18 Added by Geo
    private String ss_cd;
    
}
