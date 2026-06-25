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
public class OTCReceiptCancellationHistoryDetails {
    
    private BigInteger mtt_id;
    private Integer otc_id;
    private String action;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Constants.DATE_TIME_FORMAT, timezone = "Asia/Singapore")
    private Date dt_action;
    private String otc_status;
    private String counter_id;
    private String act_by;
    private String nm_en;
    private Integer total;







}
