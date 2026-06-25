package com.maven.rms.models;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemRefund {
    private String item_ref_no;
    private BigDecimal refund_amt;
}