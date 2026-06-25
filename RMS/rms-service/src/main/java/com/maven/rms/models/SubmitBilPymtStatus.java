package com.maven.rms.models;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitBilPymtStatus {
    
    private Integer count;
    private String order_status;
    private String order_status_nm;
    private String rcpt_no;
    private Date rcpt_dt;

}
