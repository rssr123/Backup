package com.maven.rms.models;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingIssuanceBySSListofBilItems {
    
    private Integer mft_pk;
    private BigDecimal unit_fee;
    private Integer qty;
    private BigDecimal tax_pct;
    private BigDecimal tax_amt;
    private BigDecimal final_amt;
    private String fee_detail_id;
    private String fee_detail_nm_e;
    private Integer total;

}
