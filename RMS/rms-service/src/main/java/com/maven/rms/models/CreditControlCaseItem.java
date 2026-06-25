package com.maven.rms.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditControlCaseItem {
	public Integer cc_cs_item_id;
	public Integer cc_case_id;
	public String txn_item_ref;
	public String txn_item_desc;
	private String coa1;
	private String coa2;
	private String sub_acct;
	private Integer qty;
	private BigDecimal unit_price;
	private BigDecimal disc_amt;
	public LocalDateTime dt_created;
	public LocalDateTime dt_modified;
	public String created_by;
	public String modified_by;
	public String status;
	
	public CreditControlCaseItem() {}
}
