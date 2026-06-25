package com.maven.rms.models;


import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingIssuanceBySSListOfIssuance {
    
    private Date bil_child_date;
    private String bil_child_status;
    private Integer bil_wf_id;
    private String bil_no;
    private String bil_status;
    private Integer total;

}
