package com.maven.rms.models;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitBillingChild {
    
    private String bil_no;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_FORMAT, timezone = "Asia/Singapore")
    private Date bil_child_date;
    private String bil_status;
    private String bil_status_nm;
    private List<SubmitBillingItem> bil_item;


    
}
