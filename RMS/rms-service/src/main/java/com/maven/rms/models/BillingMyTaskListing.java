package com.maven.rms.models;

import java.math.BigInteger;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingMyTaskListing {
    
    private BigInteger bil_wf_id;
    private BigInteger bil_id;
    private String requested_by;
    private String billing_desc;
    private String bil_wf_status;
    private String assigned_to;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private LocalDateTime dt_requested;
    private String created_by;
    private String task_id;
    private Integer total;

}
