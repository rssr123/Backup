package com.maven.rms.models;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentItemDetails {
    private Integer fee_detail_pk;
    private String fee_detail_id;
    private String item_ref_no;
    private String item_desc;
    private Integer line_no;
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
    private String cp_no;
    private Integer cp_tier;
    private BigDecimal cp_tier_amt;
    private BigDecimal cp_tier_disc_pct;
    private String dps_id;
    private String dps_task;
    private String pymt_case;
    private String location;
    private String lit_item_ref;
    private String txn_type;
    private Integer calendar_yr;
    private Integer rtt_item_id;

    @Override
    public String toString() {
        return "PaymentItemDetails{" +
                "fee_detail_pk=" + fee_detail_pk + '\'' +
                ", fee_detail_id='" + fee_detail_id + '\'' +
                ", item_ref_no='" + item_ref_no + '\'' +
                ", item_desc='" + item_desc + '\'' +
                ", line_no=" + line_no +
                ", qty=" + qty +
                ", unit_fee=" + unit_fee +
                ", gross_amt=" + gross_amt +
                ", grant_cd='" + grant_cd + '\'' +
                ", disc_amt=" + disc_amt +
                ", tax_pct=" + tax_pct +
                ", tax_amt=" + tax_amt +
                ", net_amt=" + net_amt +
                ", entity_type='" + entity_type + '\'' +
                ", entity_no='" + entity_no + '\'' +
                ", entity_nm='" + entity_nm + '\'' +
                ", cp_no='" + cp_no + '\'' +
                ", cp_tier=" + cp_tier +
                ", cp_tier_amt=" + cp_tier_amt +
                ", cp_tier_disc_pct=" + cp_tier_disc_pct +
                ", txn_type=" + txn_type +
                ", calendar_yr=" + calendar_yr +
                ", dps_id='" + dps_id + '\'' +
                ", dps_task='" + dps_task + '\'' +
                ", pymt_case='" + pymt_case + '\'' +
                ", location='" + location + '\'' +
                ", lit_item_ref='" + lit_item_ref + '\'' +
                ", rtt_item_id=" + rtt_item_id +
                '}';
    }
}
