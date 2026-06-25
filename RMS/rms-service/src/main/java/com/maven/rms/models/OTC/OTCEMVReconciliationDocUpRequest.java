package com.maven.rms.models.OTC;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCEMVReconciliationDocUpRequest {
    private String i_dt_balancing;
    private String i_file_nm;
    private String i_file_content;
    private String i_file_type;
    private Integer i_file_size;
    private Integer i_dr_count;
    private BigDecimal i_dr_amt;
    private Integer i_cr_count;
    private BigDecimal i_cr_amt;
    private BigDecimal i_total;
    private String i_created_by;
    private String i_modified_by;
    private String i_status;
    private BigInteger i_rc_emv_id;
}
