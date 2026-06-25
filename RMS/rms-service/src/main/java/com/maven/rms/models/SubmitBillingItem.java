package com.maven.rms.models;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SubmitBillingItem {
    
    private String item_desc;
    private BigDecimal unit_fee;
    private Integer qty;
    private BigDecimal final_amt;

}
