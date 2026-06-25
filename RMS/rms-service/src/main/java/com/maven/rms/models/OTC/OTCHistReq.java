package com.maven.rms.models.OTC;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class OTCHistReq {
    private Integer i_mtt_id;
    private Integer i_otc_id;
    private String i_action;
    private Date i_dt_action;
    private String i_otc_status;
    private String i_counter_id;
    private String i_act_by;
    private String i_created_by;
    private String i_modified_by;
    
    
}
