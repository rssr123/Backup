package com.maven.rms.models;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class OTCReceiptCancellationPymtItem {
    private String item_desc;
    private Integer qty;
    private BigDecimal unit_fee;
    private BigDecimal tax_pct;
    private BigDecimal tax_amt;
    private String grant_cd;
    private BigDecimal disc_amt;
    private BigDecimal gross_amt;
    private BigDecimal net_amt;
    private Integer total;
}
