package com.maven.rms.models;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourtOrderPymtInfo {
    

    private Integer cc_cs_item_id;
    private Integer cc_case_id;
    private String ref_no_txn;
    private String txn_item_desc;
    private Integer cn_qty;
    private BigDecimal cn_unit_price;
    private BigDecimal cn_disc_amt;
    private BigDecimal cn_amt;
    private BigDecimal cn_amt_total;
    
}
