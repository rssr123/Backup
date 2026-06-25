package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter

public class RefundHist {
    private int rtt_wf_hist_id;
    private String action;
    private String rtt_status;
    private Date dt_action;
    private String requested_by;
    private String pickup_by;
    private String msg;
    private Integer total;
    private String assign_to;
    private String modified_by;
    private String modified_by_nm;
}
