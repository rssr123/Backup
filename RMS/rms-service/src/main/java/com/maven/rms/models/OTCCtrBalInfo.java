package com.maven.rms.models;
import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCCtrBalInfo {
    
    private String counter_id;
    private String check_in;
    private String user_id;
    private String branch_cd;
    private Integer orders_paid;
    private BigDecimal total;
    private BigDecimal total_emv;
    private BigDecimal total_phy;
    private BigDecimal total_col;
    private BigDecimal total_che;
    private BigDecimal total_mo;
    private BigDecimal total_bd;
    private String status;

    private BigInteger otc_counter_id;

}
