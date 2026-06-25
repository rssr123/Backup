package com.maven.rms.models.OTC;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCEMVReconciliationStatus {
    private BigInteger rc_emv_id;
    private Date dt_balancing;
    private Date dt_created;
    private Date dt_modified;
    private String created_by;
    private String modified_by;
    private String status;
    private String rc_emv_status;
}
