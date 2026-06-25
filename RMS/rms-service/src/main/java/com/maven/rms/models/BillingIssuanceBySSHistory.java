package com.maven.rms.models;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingIssuanceBySSHistory {
    
    private String action;
    private String bil_wf_status;
    private String pickup_by;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_pick;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_created;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String msg;
    private Integer total;

}
