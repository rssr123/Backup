package com.maven.rms.models.OTC;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCEMVReconciliationRC {
    private String branch_cd;
    private String coll_slip_no;
    private String orn_no;
    private String rcpt_no;
    private BigDecimal amount;
    private String payment_mode;
    private String requested_by;
    private String approved_by;
    private String reason;

    private Integer mtt_id;
    private Integer otc_id;
    private Integer otc_counter_id;
    private String counter_id; 
    private String otc_pymt_mode;
}
