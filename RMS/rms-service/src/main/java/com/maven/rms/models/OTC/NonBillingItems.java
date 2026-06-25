package com.maven.rms.models.OTC;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NonBillingItems {
    private Integer non_bilitem_id;
    private Integer non_bil_id;
    private Integer mft_pk;
    private String fee_detail_nm_e;
    private BigDecimal unit_fee;
    private Integer qty;
    private BigDecimal tax_pct;
    private BigDecimal tax_amt;
    private BigDecimal item_total_amt;
    private String item_ref_no;
    private Integer total;
    
}
