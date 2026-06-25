package com.maven.rms.models;

import java.util.Date;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BillingClassRequest {
    private Integer i_page;
    private Integer i_size;
    private Integer i_blcm_id;
    private String i_class_id;
    private String i_class_desc;
    private Date i_dt_modified_fr;
    private Date i_dt_modified_to;
    private String i_modified_by;
    private String i_status;
}
