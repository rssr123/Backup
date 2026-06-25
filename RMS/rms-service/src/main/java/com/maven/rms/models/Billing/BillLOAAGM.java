package com.maven.rms.models.Billing;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillLOAAGM {
    private Integer bil_child_id;
    private Date bil_child_date;
    private String bil_child_status;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    private Integer bil_wf_id;
    private String bil_no;
    private Integer bil_id;
    private String bil_status;
    private Integer total;
}
