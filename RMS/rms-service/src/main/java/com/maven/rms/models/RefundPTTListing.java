package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
  @JsonFormat(
    shape   = JsonFormat.Shape.STRING,
    pattern = "yyyy-MM-dd HH:mm:ss"
  )
public class RefundPTTListing {
    
    private String orn_no;
    private LocalDateTime  orn_dt;
    private BigDecimal total_amt;
    private String order_status;
    private String rcpt_no;
    private Integer total;
    private Integer mtt_id;
    private String ent_nm;
    private String ent_no;
    private String txn_id;
    private String rms_type;
    private String rtt_status;

}
