package com.maven.rms.models;
import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SchedulerSeq {
    private Integer count_group;
    private Integer chain_group;
    private String chain_name;
    private Integer job_list_id;
    private String job_name;
    private String function_nm;
    private Integer sequence;
    private String frequency;
    private String status;
    private String scheduler_status;
    private Integer total;
}
