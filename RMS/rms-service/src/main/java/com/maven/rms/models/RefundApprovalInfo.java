package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundApprovalInfo {
    private String task_id;
    private Integer rtt_wf_id;
    private String refund_ty;
    private String orn_no;
    private String rtt_status;
    private String rtt_app_no;
    private Date dt_created;
    private String ss_cd;
    private String rms_tpye;
    private String requested_by;
    private Date dt_requested;
    private Date date_pick;
    private String msg;
    private Date dt_approved;
    private String pickup_by;
    private String approved_by;
    private Integer mtt_id;
    private String refund_cd;
    private String refund_reason;
    private String status_param_nm;
}
