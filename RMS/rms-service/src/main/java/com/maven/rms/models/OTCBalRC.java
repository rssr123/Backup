package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCBalRC {
    
    private String coll_slip_no;
    private String orn_no;
    private String rcpt_no;
    private BigDecimal totalAmount;
    private String otc_pymt_mode;
    private String requested_by;
    private String approved_by;
    private String remark;
    private BigInteger mtt_id;
    private Integer otc_id;
    private Integer otc_counter_id;
    private String counter_id;
}
