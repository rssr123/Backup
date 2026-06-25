package com.maven.rms.models;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RRPaymentItems {
    
    // NVARCHAR(20) AS mtt_id,
    // NVARCHAR(100) AS item_desc,
    // INTEGER AS qty,
    // DECIMAL(16, 2) AS net_amt,
    // DECIMAL(16, 2) AS tax_amt,
    // NVARCHAR(20) AS grant_cd,
    // DECIMAL(16, 2) AS disc_amt,
    // DECIMAL(16, 2) AS gross_amt,
    // DECIMAL(16, 2) AS gross_amt_total;


    private String mtt_id;
    private String item_desc;
    private Integer qty;
    private BigDecimal net_amt;
    private BigDecimal tax_amt;
    private String grant_cd;
    private BigDecimal disc_amt;
    private BigDecimal gross_amt;
    private BigDecimal gross_amt_total;


}
