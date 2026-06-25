package com.maven.rms.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OTCReceiptCancellationTaskAndReqInfoApproval {
    
    private Integer otc_rc_id;
    private Integer otc_id;
    private String justication;
    private String others;
    private Integer rc_type;
    private String rc_status;
    private String task_id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date date_assigned;
    private String counter_id;
    private String requested_by;
    private String requested_by_nm;
    private String requester_id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date date_requested;
    private String approved_by;
    private String approved_by_nm;
    private String approver_id;
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
    private Integer mtt_id;
    private Integer otc_counter_id;
    private String nm_en;

}
