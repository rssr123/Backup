package com.maven.rms.models.OTC;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NBLItem {
    private String bt_cd;
    private String bt_ty;
    private String bt_desc;
    private String class_id;
    private String ss_cd;
    private Integer mft_pk;
    private String mft_id;
    private Integer dps_mft_pk;
    private String dps_mft_id;
    private Integer fee_detail_pk;
    private String fee_detail_id;
    private String fee_detail_nm_e;
    private String fee_detail_nm_b;
    private BigDecimal unit_fee;
    private BigDecimal tax_pct;
}
