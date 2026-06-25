package com.maven.rms.models;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillingIssuanceBySSBillingItemDetails {
    
    private Integer bil_id;
    private Integer mft_pk;
    private BigDecimal unit_fee;
    private Integer qty;
    private BigDecimal tax_pct;
    private BigDecimal tax_amt;
    private BigDecimal final_amt;
    private String created_by;
    private String modified_by;
    private String status;
    private Integer bil_wf_id;


}
