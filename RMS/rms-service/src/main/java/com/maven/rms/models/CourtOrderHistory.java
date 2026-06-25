package com.maven.rms.models;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourtOrderHistory {



    private Integer cc_case_id;
    private Integer cc_msg_id;
    private Integer cc_case_a_id;
    private String msg;
    private String msg_type;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String pick_up;
    private String assign_to;
    private String task_status;
}
