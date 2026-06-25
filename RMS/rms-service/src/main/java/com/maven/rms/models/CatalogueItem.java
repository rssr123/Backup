package com.maven.rms.models;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CatalogueItem {
    private String feeDetailNmE;
    private String ssCd;
    private BigDecimal unitFee;
    private String feeDetailId;
    private BigDecimal taxPct;
    private Integer total;
}
