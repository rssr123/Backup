package com.maven.rms.models.Billing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Billing {
	private Integer bil_id;
	private Integer bil_wf_id;
	private Integer bltc_id;
	private Integer bilcust_id;
	private String req_name;
	private String req_email;
	private String ss_cd;
	private String billing_no;
	private String billing_desc;
	private String action;
	private BigDecimal dps_amt;
	private Integer billing_cnt;
	private String billing_freq;
	private String loa_id;
	private LocalDateTime dt_loa_start;
	private LocalDateTime dt_loa_end;
	private String agm_id;
	private LocalDateTime dt_agm_start;
	private LocalDateTime dt_agm_end;
	private String bil_wf_status;
	private String pickup_by;
	private LocalDateTime dt_pick;
	private String billing_mthd;
	private LocalDateTime dt_created;
	private LocalDateTime dt_modified;
	private String created_by;
	private String modified_by;
	private String status;
	
	private String cust_id;
	private String cust_nm;
	private String cust_email;
	private String cust_phone;
	private String cust_addr1;
	private String cust_addr2;
	private String cust_addr3;
	private String cust_postcode;
	private String cust_city;
	private String cust_state;
	private String ent_nm;
	private String ent_no;
	private String ent_ty;
	private String billing_origin;

	private List<Map<String, Object>> history;
	private List<Map<String, Object>> billing_items;
	private List<Map<String, Object>> billing_list;
	private List<Map<String, Object>> documents_list;
	
	private Integer history_size;
	private Integer items_size;
	private Integer issuance_size;
	private Integer documents_size;
	
	private Integer has_query;
	
	public Billing() {}
}
