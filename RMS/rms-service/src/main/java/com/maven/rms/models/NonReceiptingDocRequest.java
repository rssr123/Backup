package com.maven.rms.models;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonReceiptingDocRequest {
    private Integer i_ag_sale_id;
    private String i_ag_type;
    private String i_file_name;
    private String i_file_content;
    private String i_file_type;
    private Integer i_file_size_kb;
    private String i_created_by;
    private String i_modified_by;
    private String i_settle_status;
    private String i_task_status;
    private String i_remark;
    private String i_stmt_no;
    private BigDecimal i_discrepancy_amt;
}
