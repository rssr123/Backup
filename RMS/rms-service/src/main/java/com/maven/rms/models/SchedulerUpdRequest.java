package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulerUpdRequest {
    private Integer i_chain_group;
    private String i_sch_status;
    private String i_function_nm;
    
}
