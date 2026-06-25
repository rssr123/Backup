package com.maven.rms.models;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingTypeCode {
    
    private Integer bltc_id;
    private String bt_cd;
    private String bt_ty;
    private String bt_desc;
    private String class_id;
    private String ss_cd;
    private Integer bltc_item_id;
    private Integer mft_pk;
    private String mft_id;
    private Integer dps_mft_pk;
    private String dps_mft_id;
    private String fee_detail_nm_e;
    private BigDecimal unit_fee;
    private Integer tax_cd_id;
    private BigDecimal tax_pct;
    private String ss_nm;
    private Integer total;

}
