package com.maven.rms.models.payload.requests;
import java.math.BigDecimal;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class OTCPymtItemDet {
	@NotNull(message = "fee_detail_id is required.")
    @Size(min = 1, max = 10, message="fee_detail_id size error.")
	private String fee_detail_id;
	@NotNull(message = "item_ref_no is required.")
    @Size(min = 1, max = 30, message="item_ref_no size error.")
	private String item_ref_no;
	@NotNull(message = "item_desc is required.")
    @Size(min = 1, max = 150, message="item_desc size error.")
	private String item_desc;
	@NotNull(message = "line_no is required.")
	private Integer line_no;
	@NotNull(message = "qty is required.")
	private Integer qty;
	@NotNull(message = "unit_fee is required.")
	private BigDecimal unit_fee;
	@NotNull(message = "gross_amt is required.")
	private BigDecimal gross_amt;
	private String grant_cd;
	private BigDecimal disc_amt;
	@NotNull(message = "tax_pct is required.")
	private BigDecimal tax_pct;
	@NotNull(message = "tax_amt is required.")
	private BigDecimal tax_amt;
	@NotNull(message = "net_amt is required.")
	private BigDecimal net_amt;
	@NotNull(message = "entity_type is required.")
    @Size(min = 1, max = 1, message="entity_type size error.")
	private String entity_type;
	@NotNull(message = "entity_no is required.")
    @Size(min = 1, max = 20, message="entity_no size error.")
	private String entity_no;
	@NotNull(message = "entity_nm is required.")
    @Size(min = 1, max = 100, message="entity_nm size error.")
	private String entity_nm;
	private String cp_no;
	private Integer cp_tier;
	private BigDecimal cp_tier_amt;
	private BigDecimal cp_tier_disc_pct;
	private String pymt_case;
	// private Integer email_flag;
	private String location;
	private String lit_item_ref;
	private String txn_type;
	private Integer calendar_yr;
	private String dps_id;
	private String dps_task;
    
    public OTCPymtItemDet() {}
}
