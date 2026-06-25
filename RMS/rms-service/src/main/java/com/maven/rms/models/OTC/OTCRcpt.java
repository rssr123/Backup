package com.maven.rms.models.OTC;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCRcpt {
    private Integer otc_rcpt_id;
    private Integer otc_id;
    private String rcptNo;
    private Date rcpt_dt;
    private String rcpt_status;
    private Integer rcpt_reprint;
    private Integer is_uploaded;
    private String ver_id;
    private String ssdocref_id;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    private String file_nm;
    private String remark;
    
}
