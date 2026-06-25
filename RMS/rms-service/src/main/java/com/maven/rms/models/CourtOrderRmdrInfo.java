package com.maven.rms.models;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourtOrderRmdrInfo {

    private Integer cc_case_id;
    private Integer reminder_cnt;
    private Date reminder_dt;
    private Date reminder_received_date;
    private String reminder_email_content;
    
}
