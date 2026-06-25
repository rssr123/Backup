package com.maven.rms.models.OTC;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCEMVReconciliation {
    private String branch_cd;
    private String bal_status;
    private Integer emv_settlement_count;
    private BigDecimal emv_amt;
    private Integer total;
}
