package com.maven.rms.models;
import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTCBalCash {
    private String param_cd;
    private String denomination;
    private Integer quantity;
    private BigDecimal total;
}
