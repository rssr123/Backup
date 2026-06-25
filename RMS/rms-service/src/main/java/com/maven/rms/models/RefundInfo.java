package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class RefundInfo {
    private int rtt_id;
    private String refund_slip_no;
    private String requested_by;
    private Date dt_process;
    private int appeal_cnt;
    private String rtt_status;
    private String rtt_app_no;

}
