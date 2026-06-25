package com.maven.rms.models;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DashboardInit {
    private Integer revenue_year;
    private BigDecimal revenue;
    private String ss_cd;
    private BigDecimal revenue_by_ss;
    private String payment_method;
    private BigDecimal revenue_by_pm;
    private Integer rcpt_year;
    private Integer count_rcpt_mtt;
    private Integer count_rcpt_otc;
    private String refund_status;
    private Integer refund_count;
}
