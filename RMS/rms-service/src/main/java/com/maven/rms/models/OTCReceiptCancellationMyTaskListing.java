package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OTCReceiptCancellationMyTaskListing {
    
    private Integer otc_rc_id;
    private Integer otc_id;
    private String justication;
    private Integer rc_type;
    private String rc_status;
    private String task_id;
    private String counter_id;
    private String requested_by;
    private String requested_by_nm;
     @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date date_requested;
    private String approved_by;
    private String approved_by_nm;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_approved;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_modified;
    private String created_by;
    private String created_by_nm;
    private String modified_by;
    private String modified_by_nm;
    private String assigned_to;
    private String assigned_to_nm;
    private Integer mtt_id;
    private String otc_pymt_mode;
    private String task_description;
    private String status;
    private Integer total;

}
