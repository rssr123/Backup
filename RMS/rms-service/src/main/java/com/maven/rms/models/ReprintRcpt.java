package com.maven.rms.models;

import java.math.BigDecimal;
import java.math.BigInteger;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
// @Entity(name = "rms_otc")
public class ReprintRcpt {

    // @Id
    private Integer mtt_id;
    private String rcpt_no;
    private String orn_no;
    private String cust_nm;
    private BigInteger otc_id;
    private String otc_pymt_mode;
    private BigDecimal total_amt;
    private Integer otc_counter_id;
    private String counter_id;
    private String branch_cd;
    private Integer otc_rcpt_id;
    private Integer otc_rc_rp_id;
    private Integer total;
    


}
