package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundStatus {
    private Integer no;
    private String refund_slip_no;
    private Date dt_process;
    private Date dt_requested;
    private String requested_by;
    private String id_no;
    private String refund_ty;
    private String ss_cd;
    private String rtt_app_no;
    private String orn_no;
    private String rcpt_no;
    private String rms_type;
    private Date pymt_submit_dt;
    private String ent_nm;
    private String ent_no;
    private String branch;
    private BigDecimal refund_amt;
    private String msg;
    private String rtt_status;
    private String approved_by;
    private Date dt_approved;
    private String fms_p_vou_no;
}
