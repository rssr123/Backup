package com.maven.rms.models;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourtOrder {
    private Integer cc_case_id;
    private Integer cc_case_a_id;
    private Integer cc_cs_item_id;
    private String task_no;
    private String task_status;
    private String pymt_status;
    private String txn_ty;
    private String attr_case_no;
    private String assign_to;
    private BigDecimal pymt_amt;
    private Integer total;
}
