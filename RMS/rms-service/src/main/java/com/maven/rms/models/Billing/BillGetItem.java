package com.maven.rms.models.Billing;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillGetItem {
    private Integer bil_item_id;
    private Integer bil_id;
    private Integer mft_pk;
    private String fee_detail_nm_e;
    private BigDecimal unit_fee;
    private Integer qty;
    private BigDecimal tax_pct;
    private BigDecimal tax_amt;
    private BigDecimal final_amt;
    private Integer bil_wf_id;
    private Integer total;
}
