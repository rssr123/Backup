package com.maven.rms.models;

import java.math.BigInteger;
import java.util.Date;

import javax.persistence.criteria.CriteriaBuilder.In;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.maven.rms.config.Constants;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCReceiptCancellationHistoryDetailsAudit {
    
    private BigInteger mtt_id;
    private Integer otc_id;
    private String action;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_action;
    private String bcm_desc;
    private String counter_id;
    private String hist_status;
    private String status;
    private String justification;
    private String rc_type;
    private String others;
    private String remark;
    private String performed_by;
    private String performed_by_nm;
    private String assigned_to;
    private String assigned_to_nm;
    private Integer total;

}
