package com.maven.rms.models;

import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CCTaskList {
    private Integer cc_case_id;
    private String task_id;
    private String task_status;
    private String assign_to;
    private String pick_up;
    private String pymt_status;
    private String txn_ty;
    private String attr_case_no;
    private Integer reminder_cnt;
    private Date reminder_dt;
    private BigDecimal txn_total_amt;
    private String cust_nm;
    private Integer total;
}
