package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CCTaskListReq {
    private Integer i_page;
    private Integer i_size;
    private String i_task_id;
    private String i_task_status;
    private String i_payment_status;
    private String i_txn_type;
    private String i_case_no;

    private String i_assign_to;
    private String i_cc_case_id;
    private String i_cust_nm;
}
