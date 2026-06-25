package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCCtrBalCol {
    private Integer i_page;
    private Integer i_size;
    private String counter_id;
    
    private String col_slip_no;
    private String orn_no;
    private String trans_trace;
    private String batch_no;
    private String host_no;
    private String t_id;
    private BigDecimal amount;
    private Integer total;

    private BigInteger otc_counter_id;
}
