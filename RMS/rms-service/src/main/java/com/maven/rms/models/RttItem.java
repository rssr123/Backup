package com.maven.rms.models;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RttItem {
    private String item_desc;
    private Integer qty;
    private BigDecimal unit_fee;
    private BigDecimal tax_pct;
    private BigDecimal tax_amt;
    private String grant_cd;
    private BigDecimal disc_amt;
    private BigDecimal refund_amt;
    private BigDecimal total_refund_amt;
    private Integer rtt_item_id;
    private Integer rtt_wf_id;  
    private String item_ref_no;
    private BigDecimal net_amt;
    private String entity_no;
    private String entity_nm;
    private String entity_type;
    private BigDecimal gross_amt;
}
