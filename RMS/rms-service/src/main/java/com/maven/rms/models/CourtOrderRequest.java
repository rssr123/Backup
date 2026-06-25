package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourtOrderRequest {
    private Integer i_page;
    private Integer i_size;
    private String i_task_no;
    private String i_task_status;
    private String i_pymt_status;
    private String i_txn_ty;
    private String i_attr_case_no;
    private String i_assign_to;
    private BigDecimal i_pymt_amt;
    private Integer i_cc_case_id;
    private Integer i_cc_case_a_id;
    private Integer i_cc_cs_item_id;


    
}
