package com.maven.rms.models;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RRHistoryTable {

   
    private String mtt_id;
    private String action;
    private Date dt_action;
    private String otc_status;
    private String counter_id;
    private String act_by;
    private String status;


    
}
