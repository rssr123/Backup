package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class OTCReceiptCancellationRequest {
    


    private Integer i_otc_id;
    private String i_justication;
    private Integer i_rc_type;
    private String i_rc_status;
    private String i_counter_id;
    private String i_requested_by;
    private String i_requester_id;
    private String i_others;
    private String i_approved_by;
    private String i_approver_id;
    private String i_remark;
    private String i_created_by;
    private String i_modified_by;
    private String i_status;
    private String i_assigned_to;
    private String i_action;
    private Integer i_otc_counter_id;

}
