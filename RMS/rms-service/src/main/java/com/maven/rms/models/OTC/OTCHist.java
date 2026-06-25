package com.maven.rms.models.OTC;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

@Getter
@Setter
public class OTCHist {
    private Integer otc_id;
    private String action;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_action;
    private String otc_status;
    private String counter_id;
    private String act_by;
    private String branch;
    private String justification;
    private String remark;
    private String others;
    private Integer total;
    
}
