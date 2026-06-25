package com.maven.rms.models;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MTTItemDetails {
    private String fee_detail_id;
    private String item_ref_no;
    private String item_desc;
    private Integer qty;
    private BigDecimal unit_fee;
    private BigDecimal gross_amt;
    private String grant_cd;
    private BigDecimal disc_amt;
    private BigDecimal tax_pct;
    private BigDecimal tax_amt;
    private BigDecimal net_amt;
    private String entity_type;
    private String entity_no;
    private String entity_nm;
    private String  cp_no;
    private Integer cp_tier;
    private BigDecimal cp_tier_amt;
    private BigDecimal cp_tier_discpct;
    private String dps_id;
    private String dps_task;
    private String pymt_case;
    private String location;
    private String lit_item_ref;
    private String txn_type;
    private Integer calendar_yr;
    
}
