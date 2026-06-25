package com.maven.rms.models;

import lombok.Data;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Data
public class RefundChargebackRequest {
    private Integer rttWfId;
    private String ornNo;
    private String txnId;
    private Date dt_requested; // Changed to Date
    private String requested_by;
    private String rttStatus;
    private String pg_email;
}
