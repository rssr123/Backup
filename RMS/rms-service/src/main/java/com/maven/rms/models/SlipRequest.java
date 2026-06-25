package com.maven.rms.models;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Data
public class SlipRequest {
    private Integer rttWfId;
    private String rttAppNo;
    private String slipNo;
    private String custNm;
    private String entNo;
    private String custPhone;
    private String custEmail;
    private String rmsType;
    private String refundTy;
    private String custState;
    private String refundReason;
    private String rcptNo;
    private String ornNo;
    private String txnId;
    private BigDecimal refundAmt;
    private String RttStatus;
    private Timestamp rcptdate;
    private String approvedBy;
}


