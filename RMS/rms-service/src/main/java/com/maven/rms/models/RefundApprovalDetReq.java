package com.maven.rms.models;

import java.math.BigDecimal;

import com.informix.lang.Decimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefundApprovalDetReq {
    private Integer i_page;
    private Integer i_size;
    private Integer i_rtt_wf_id;
    private String i_rtt_app_no;
    private String i_task_id;
    private String i_refund_cd;
    private String i_rtt_status;
    private String i_msg;
    private String i_modified_by;
    private String i_pickup_by;
    private String i_assign_to;
    private String i_refund_reason;
    private String i_orn_no;
    private BigDecimal i_refund_amt;
    private String i_refund_type;

}
