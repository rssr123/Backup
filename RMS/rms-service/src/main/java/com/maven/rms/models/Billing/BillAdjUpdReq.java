package com.maven.rms.models.Billing;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BillAdjUpdReq {
    private Integer i_bil_id;
    private String i_modified_by;
    private Integer i_bil_item_id;
    private BigDecimal i_unit_fee;
    private BigDecimal i_tax_amt;
    private BigDecimal i_final_amt;
    private Integer i_qty;
}