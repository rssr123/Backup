package com.maven.rms.models;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulerCustReq {
    private String i_start_job;
    private String i_end_job;

    private String i_function_nm;

    private String i_chain_name;
}
