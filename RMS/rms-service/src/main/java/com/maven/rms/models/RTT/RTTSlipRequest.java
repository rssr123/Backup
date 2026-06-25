package com.maven.rms.models.RTT;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class RTTSlipRequest {
    private Integer rttWfId;
    private String rttAppNo;
    private String slipNo;
    private String custNm;
    private String entNo;
    private String custPhone;
    private String custEmail;
    private String dtCreated;
    private String rmsType;
    private String refundTy;
    private String custState;
    private String refundReason;
    private String rcptNo;
    private String ornNo;
    private String txnId;
    private BigDecimal refundAmt;
}


