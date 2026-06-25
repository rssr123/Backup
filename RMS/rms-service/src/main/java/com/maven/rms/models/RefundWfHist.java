package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_rtt_wf_hist")
public class RefundWfHist {
    @Id
    private BigInteger rtt_wf_hist_id;
    private String pickup_by;
    private LocalDateTime date_pick;
    private String created_by;
    private LocalDateTime dt_created;
    private String modified_by;
    private LocalDateTime dt_modified;
    private BigDecimal refund_total_amt;
    private String action;
    private LocalDateTime dt_action;



    
}
