package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class OTCReceiptCancellationMyTaskListingRequest {
    
    private Integer i_page;
    private Integer i_size;
    private Integer i_otc_rc_id;
    private Integer i_otc_id;
    private Integer i_rc_type;
    private String i_rc_status;
    private String i_task_id;
    private String i_counter_id;
    private String i_requested_by;
    private String i_requested_by_nm;
    private String i_approved_by;
    private String i_approved_by_nm;
    private Date i_date_requested_fr;
    private Date i_date_requested_to;
    private String i_assigned_to;
    private String i_assigned_to_nm;

}
