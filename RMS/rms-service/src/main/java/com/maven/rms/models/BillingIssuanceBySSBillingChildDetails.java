package com.maven.rms.models;


import java.util.Date;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class BillingIssuanceBySSBillingChildDetails {
    
    private Integer bil_id;
    private Date bil_child_date;
    private String bil_child_status;
    private String created_by;
    private String modified_by;
    private String status;
    private Integer bil_wf_id;
    private String bil_no;
    private String bil_status;


}
