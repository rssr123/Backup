package com.maven.rms.models;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BranchCodeRequest {
    private Integer i_page;
    private Integer i_size;
    private String i_code;
    private String i_bcm_ty;
    private String i_bcm_desc;
    private Date i_dt_modified_fr;
    private Date i_dt_modified_to;
    private String i_modified_by;
    private String i_status;
}
