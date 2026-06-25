package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCCtrBalRMS {
    
    private Integer i_page;
    private Integer i_size;
    private String counter_id;

    private String col_slip_no;
    private String orn_no;
    private BigDecimal gtotal;
    private String otc_pymt_mode;
    private BigDecimal emv_amt;
    private BigDecimal cash_amt;
    private BigDecimal che_amt;
    private BigDecimal bd_amt;
    private BigDecimal mo_amt;
    private Integer total;

    private BigInteger otc_counter_id;
}
