package com.maven.rms.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FMSARIImmediateRequest {
    private String i_rms_batch_no;
    private Integer i_otc_cash_s_id;
    private Integer i_otc_mo_s_id;
    private Integer i_otc_emv_s_id;
    private Integer i_otc_che_id;
    private Integer i_otc_bd_id;
    private Integer i_non_bil_id;
}
