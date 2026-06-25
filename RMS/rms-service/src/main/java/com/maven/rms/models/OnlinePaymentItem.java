package com.maven.rms.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity(name = "rms_mtt_item")
public class OnlinePaymentItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer mtt_item_id;
    private Integer mtt_id;
    private Integer fee_detail_pk;
    private Integer line_no;
    private String item_desc;
    private Integer qty;
    private BigDecimal unit_fee;
    private BigDecimal tax_amt;
    private BigDecimal disc_amt;
    private BigDecimal gross_amt;
    
    private String fee_detail_id;
    private String item_ref_no;
    private String grant_cd;
    private BigDecimal tax_pct;
    private BigDecimal net_amt;
    private String entity_type;
    private String entity_no;
    private String entity_nm;
    private String cp_no;
    private Integer cp_tier;
    private BigDecimal cp_tier_amt;
    private BigDecimal cp_tier_discpct;
    //private Integer email_flag;
    private String dps_id;
    private String dps_task;
    private String pymt_case;
    private String location;
    private String lit_item_ref;
    private String txn_type;
    private Integer calendar_yr;
    private String modified_by;
    private String created_by;
    private LocalDateTime dt_created;
    private LocalDateTime dt_modified;
    private String status;
    
    public OnlinePaymentItem() {}
    
    public OnlinePaymentItem(MFT mft) {
    	this.fee_detail_pk = mft.getFee_detail_pk();
    	this.fee_detail_id = mft.getFee_detail_id();
    	this.item_desc = mft.getFee_detail_nm_e();
    	this.line_no = 1;
    	this.qty = 0;
    	this.unit_fee = mft.getUnit_fee();
    	this.tax_amt = new BigDecimal(0);
    	this.disc_amt = new BigDecimal(0);
    	this.gross_amt = new BigDecimal(0);
    	this.item_ref_no = this.line_no.toString();
    	this.tax_pct = new BigDecimal(0);
    	this.net_amt = new BigDecimal(0);
    	this.entity_type = "";
    	this.entity_no = "";
    	this.entity_nm = "";
    	
    	this.dt_created = LocalDateTime.now();
    	this.dt_modified = this.dt_created;
    	this.status = "A";
    }
    
    // public Integer getMtt_item_id() {
    //     return mtt_item_id;
    // }

    // public void setMtt_item_id(Integer mtt_item_id) {
    //     this.mtt_item_id = mtt_item_id;
    // }
    
    // public Integer getLine_no() {
    //     return line_no;
    // }

    // public void setLine_no(Integer line_no) {
    //     this.line_no = line_no;
    // }

    // public String getItem_desc() {
    //     return item_desc;
    // }

    // public void setItem_desc(String item_desc) {
    //     this.item_desc = item_desc;
    // }

    // public Integer getQty() {
    //     return qty;
    // }

    // public void setQty(Integer qty) {
    //     this.qty = qty;
    // }

    // public BigDecimal getUnit_fee() {
    //     return unit_fee;
    // }

    // public void setUnit_fee(BigDecimal unit_fee) {
    //     this.unit_fee = unit_fee;
    // }

    // public BigDecimal getTax_amt() {
    //     return tax_amt;
    // }

    // public void setTax_amt(BigDecimal tax_amt) {
    //     this.tax_amt = tax_amt;
    // }

    // public BigDecimal getDisc_amt() {
    //     return disc_amt;
    // }

    // public void setDisc_amt(BigDecimal disc_amt) {
    //     this.disc_amt = disc_amt;
    // }

    // public BigDecimal getGross_amt() {
    //     return gross_amt;
    // }

    // public void setGross_amt(BigDecimal gross_amt) {
    //     this.gross_amt = gross_amt;
    // }

	// public String getFee_detail_id() {
	// 	return fee_detail_id;
	// }

	// public void setFee_detail_id(String fee_detail_id) {
	// 	this.fee_detail_id = fee_detail_id;
	// }

	// public String getItem_ref_no() {
	// 	return item_ref_no;
	// }

	// public void setItem_ref_no(String item_ref_no) {
	// 	this.item_ref_no = item_ref_no;
	// }

	// public String getGrant_cd() {
	// 	return grant_cd;
	// }

	// public void setGrant_cd(String grant_cd) {
	// 	this.grant_cd = grant_cd;
	// }

	// public BigDecimal getTax_pct() {
	// 	return tax_pct;
	// }

	// public void setTax_pct(BigDecimal tax_pct) {
	// 	this.tax_pct = tax_pct;
	// }

	// public BigDecimal getNet_amt() {
	// 	return net_amt;
	// }

	// public void setNet_amt(BigDecimal net_amt) {
	// 	this.net_amt = net_amt;
	// }

	// public String getEntity_type() {
	// 	return entity_type;
	// }

	// public void setEntity_type(String entity_type) {
	// 	this.entity_type = entity_type;
	// }

	// public String getEntity_no() {
	// 	return entity_no;
	// }

	// public void setEntity_no(String entity_no) {
	// 	this.entity_no = entity_no;
	// }

	// public String getEntity_nm() {
	// 	return entity_nm;
	// }

	// public void setEntity_nm(String entity_nm) {
	// 	this.entity_nm = entity_nm;
	// }

	// public String getCp_no() {
	// 	return cp_no;
	// }

	// public void setCp_no(String cp_no) {
	// 	this.cp_no = cp_no;
	// }

	// public Integer getCp_tier() {
	// 	return cp_tier;
	// }

	// public void setCp_tier(Integer cp_tier) {
	// 	this.cp_tier = cp_tier;
	// }

	// public BigDecimal getCp_tier_amt() {
	// 	return cp_tier_amt;
	// }

	// public void setCp_tier_amt(BigDecimal cp_tier_amt) {
	// 	this.cp_tier_amt = cp_tier_amt;
	// }

	// public BigDecimal getCp_tier_discpct() {
	// 	return cp_tier_discpct;
	// }

	// public void setCp_tier_discpct(BigDecimal cp_tier_discpct) {
	// 	this.cp_tier_discpct = cp_tier_discpct;
	// }
    
}