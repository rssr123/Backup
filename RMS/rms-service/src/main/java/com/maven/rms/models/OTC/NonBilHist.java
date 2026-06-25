package com.maven.rms.models.OTC;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonBilHist {
    private Integer non_bil_a_id;
    private Integer non_bil_id;
    private String req_name;
    private String req_email;
    private String non_bil_no;
    private String non_bil_desc;
    private String ret_che_no;
    private BigDecimal total_bil_amt;
    private String remark;
    private String bil_status;
    private String fms_admin_email;
    private String fms_admin_nm;
    private LocalDateTime dt_created;
    private LocalDateTime dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    private String bil_action;
    private String performed_by;
    private Integer otc_counter_id;
    private Integer otc_body_id;
    private Integer total;
}
