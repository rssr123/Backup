package com.maven.rms.models.OTC;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCEMVReconciliationStatusRequest {
    private BigInteger i_rc_emv_id;
    private String i_dt_balancing;
    private String i_dt_created;
    private String i_dt_modified;
    private String i_created_by;
    private String i_modified_by;
    private String i_status;
    private String i_rc_emv_status;

    private String i_date_from;
    private String i_date_to;
}
