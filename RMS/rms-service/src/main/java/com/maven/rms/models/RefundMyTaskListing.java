package com.maven.rms.models;

import java.math.BigInteger;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundMyTaskListing {
    
    private BigInteger rtt_wf_id;
    private String refund_ty;
    private String rtt_app_no;
    private String requested_by;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_requested;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_pick;
    private String rtt_status;
    private String created_by;
    private String approved_by;
    private String task_id;
    private Integer total;

}
