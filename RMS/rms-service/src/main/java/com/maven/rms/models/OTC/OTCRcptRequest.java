package com.maven.rms.models.OTC;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCRcptRequest {
    
    private Integer i_otc_id;
    private String i_rcpt_no;
    private Date i_rcpt_dt;
    private String i_rcpt_status;  // You can add more fields as needed
    private Integer i_rcpt_reprint;
    private Integer i_is_uploaded;
    private String i_ver_id;
    private String i_ssdocref_id;
    private String i_created_by;
    private String i_modified_by;
    private String i_file_nm;
    private String i_remark;
    
}
