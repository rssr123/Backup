package com.maven.rms.models.payload.requests;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BillingRegistrationIncoming {
	@NotNull(message = "i_bt_code is required.")
    @Size(min = 1, max = 10, message="i_bt_code size error.")
	private String i_bt_code;
	
	@NotNull(message = "i_entity_type is required.")
    @Size(min = 1, max = 10, message="i_entity_type size error.")
	private String i_entity_type;
	
	@NotNull(message = "i_entity_no is required.")
    //@Size(min = 12, max = 40, message="i_entity_no size error.")
	@Size(min = 1, max = 40, message="i_entity_no size error.")
	private String i_entity_no;
	
	@NotNull(message = "i_cust_id is required.")
    //@Size(min = 10, max = 255, message="i_cust_id size error.")
    @Size(min = 1, max = 255, message="i_cust_id size error.")
	private String i_cust_id;
	
	@NotNull(message = "i_cus_name is required.")
    //@Size(min = 4, max = 255, message="i_cus_name size error.")
	@Size(min = 1, max = 255, message="i_cus_name size error.")
	private String i_cus_name;
	
	@NotNull(message = "i_cus_email is required.")
    //@Size(min = 5, max = 255, message="i_cus_email size error.")
    @Size(min = 1, max = 255, message="i_cus_email size error.")
	private String i_cus_email;
	
	@NotNull(message = "i_cus_phno is required.")
    @Size(min = 10, max = 16, message="i_cus_phno size error.")
	private String i_cus_phno;
	
	@NotNull(message = "i_cus_add1 is required.")
    @Size(min = 1, max = 150, message="i_cus_add1 size error.")
	private String i_cus_add1;
	
	@NotNull(message = "i_cus_add2 is required.")
    @Size(min = 1, max = 150, message="i_cus_add2 size error.")
	private String i_cus_add2;
	
	@NotNull(message = "i_cus_add3 is required.")
    @Size(min = 0, max = 150, message="i_cus_add3 size error.")
	private String i_cus_add3;
	
	@NotNull(message = "i_postcode is required.")
    @Size(min = 4, max = 10, message="i_postcode size error.")
	private String i_postcode;
	
	@NotNull(message = "i_city is required.")
    @Size(min = 1, max = 150, message="i_city size error.")
	private String i_city;
	
	@NotNull(message = "i_state is required.")
    @Size(min = 1, max = 10, message="i_state size error.")
	private String i_state;
	
	@NotNull(message = "i_entity_name is required.")
    @Size(min = 1, max = 400, message="i_entity_name size error.")
	private String i_entity_name;
	
	@NotNull(message = "i_sscode is required.")
    @Size(min = 1, max = 10, message="i_sscode size error.")
	private String i_sscode;
	
	@NotNull(message = "i_billing_no is required.")
    @Size(min = 1, max = 255, message="i_billing_no size error.")
	private String i_billing_no;
	
	@NotNull(message = "i_billing_method is required.")
    @Size(min = 1, max = 10, message="i_billing_method size error.")
	private String i_billing_method;
	
	@NotNull(message = "i_billing_desc is required.")
    @Size(min = 1, max = 255, message="i_billing_desc size error.")
	private String i_billing_desc;
	
	@NotNull(message = "i_requester_name is required.")
    @Size(min = 1, max = 255, message="i_requester_name size error.")
	private String i_requester_name;
	
	@NotNull(message = "i_requester_email is required.")
    //@Size(min = 5, max = 255, message="i_requester_email size error.")
	@Size(min = 1, max = 255, message="i_requester_email size error.")
	private String i_requester_email;
	
	@NotNull(message = "i_remarks is required.")
	private String i_remarks;
	
	@NotNull(message = "i_billing_cnt is required.")
	private Integer i_billing_cnt;
	
    @Size(min = 1, max = 255, message="i_billing_freq size error.")
	@NotNull(message = "i_billing_freq is required.")
	private String i_billing_freq;
    
	@NotNull(message = "i_billing_items is required.")
	private List<Map<String, Object>> i_billing_items;
	
	@NotNull(message = "i_billing_items_total_cost is required.")
	private BigDecimal i_billing_items_total_cost;

	private List<Map<String, Object>> i_supporting_documents;
	private String i_loa_ref_no;
	private List<LocalDateTime> i_date_range;
	private List<Map<String, Object>> i_billing_issuance_list;
	private Map<String, Object> i_loa_document;
	private String i_agmt_ref_no;
}
