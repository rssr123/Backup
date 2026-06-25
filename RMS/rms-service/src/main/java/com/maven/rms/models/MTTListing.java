package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MTTListing {

    private String ss_cd;
    private String orn_no;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date orn_dt;
    private BigDecimal total_amt;
    private String order_status;
    private String rcpt_no;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date rcpt_dt;
    private Integer total;
    private Integer mtt_id;
    private String rms_type;
    
}
