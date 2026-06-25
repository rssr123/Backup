package com.maven.rms.models;

import java.math.BigInteger;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MyTaskPublicTask {
    
    private String task_id;
    private String task_desc;
    private String requested_by;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private LocalDateTime dt_requested;
    private String pickup_by;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private LocalDateTime dt_pick;
    private String task_status;
    private String origin_table;
    private BigInteger pk;
    private Integer total;
    
}
