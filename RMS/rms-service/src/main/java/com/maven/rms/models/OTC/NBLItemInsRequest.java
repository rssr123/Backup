package com.maven.rms.models.OTC;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NBLItemInsRequest {
    // private Integer i_non_bil_id;
    private Integer i_mft_pk;
    private BigDecimal i_unit_fee;
    private Integer i_quantity;
    private BigDecimal i_tax_pct;
    private BigDecimal i_tax_amt;
    private BigDecimal i_item_total_amt;
    private String i_created_by;
    private String i_modified_by;
    private String i_non_bil_no;
    private Integer i_line_nbr;
    private Integer i_mtt_id;
    private Integer i_non_bil_id;
}