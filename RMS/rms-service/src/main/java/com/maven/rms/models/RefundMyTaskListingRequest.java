package com.maven.rms.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundMyTaskListingRequest {
    
    private Integer i_page;
    private Integer i_size;
    private String i_username;
    private String i_user_role;
    private String i_my_task_mode;
    private String i_task_id;
    private String i_task_desc;
    private String i_rtt_app_no;
    private String i_requested_by;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT, timezone = "Asia/Singapore")
    private Date i_dt_requested;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT, timezone = "Asia/Singapore")
    private Date i_dt_pick;
    private String i_rtt_status;
    private String i_assigned_to;
    private String i_created_by;

}
