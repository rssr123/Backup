package com.maven.rms.models;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Dashboard {
    private Integer receiptYear;
    private Integer receiptMonth;
    private Integer receiptDate;
    private Integer count_rcpt_mtt;
    private Integer count_rcpt_otc;

    private String ss_cd;
    private BigDecimal revenue;

    private String refund_status;
    private Integer count_refund;
    
}
