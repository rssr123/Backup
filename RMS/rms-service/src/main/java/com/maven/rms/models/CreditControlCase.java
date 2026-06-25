package com.maven.rms.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditControlCase {
	private Integer cc_case_id;
	private Integer cc_cust_id;
	private BigDecimal txn_total_amt;
	private String pick_up;
	private String assign_to;
	private LocalDateTime dt_pickup;
	private LocalDateTime dt_assigned;
	private BigDecimal decision_amt;
	private String task_status;
	private String task_no;
	private LocalDateTime dt_created;
	private LocalDateTime dt_modified;
	private String created_by;
	private String modified_by;
	private String inv_ty;
	private String status;

    @JsonProperty("InvoiceInformation")
    private InvoiceInformation InvoiceInformation;
    @JsonProperty("CustomerInformation")
    private CustomerInformation CustomerInformation;
    @JsonProperty("PaymentInformation")
    private PaymentInformation PaymentInformation;
    @JsonProperty("CreditMemoInformation")
    private CreditMemoInformation CreditMemoInformation;
    @JsonProperty("DebitMemoInformation")
    private DebitMemoInformation DebitMemoInformation;
    @JsonProperty("ItemInformation")
    private List<ItemInformation> itemInformation; 
    
	private List<CreditControlCaseRmd> reminders;
	private List<Map<String, Object>> history;
	private List<Map<String, Object>> documents_list;
	
	private Integer reminders_size;
	private Integer payment_items_size;
	private Integer history_size;
	private Integer documents_size;
	
    // Invoice Information
    @Getter
    @Setter
    public static class InvoiceInformation{
        private String type;
        private String fms_ari_ref_no;
        private String inv_cust_id;
        private BigDecimal cur_doc_bal;
        private Date dt_dunning;
        private int lv_dunning;
        private String attr_case_no;
        @JsonProperty("Invoice_desc")
        private String Invoice_desc;
    }

    // Customer Information
    @Getter
    @Setter
    public static class CustomerInformation{
        private String cust_nm;
        private String cust_id_ty;
        private String cust_id_no;
        private String cust_addr_1;
        private String cust_addr_2;
        private String cust_addr_3;
        private String cust_postcode;
        private String cust_city;
        private String cust_state;
        private String cust_country;
        private String cust_email;
        private String cust_phone;
    }

    // Payment Information
    @Getter
    @Setter
    public static class PaymentInformation {
        private String pymt_ty;
        private String pymt_ref_no;
        private Date pymt_dt_application;
        private String pymt_ref;
        private String pymt_attr_doc_no;
        private String pymt_attr_doc_ty;
        private BigDecimal pymt_amt;
        private String pymt_status;
        private String txn_ty;
        private String ref_no_txn;
        private String rcpt_no;
    }
 
    // Credit Memo Information
    @Getter
    @Setter
    public static class CreditMemoInformation {
        private String cn_type;
        private String cn_ref_no;
        private String cn_cust_orn;
        private BigDecimal cn_amt;
        private String cn_desc;
        private String cn_branch;
        private String cn_coa1;
        private String cn_coa2;
        private String cn_acct;
        private String cn_sub_acct;
        private Integer cn_qty;
        private BigDecimal cn_unit_price;
        private BigDecimal cn_disc_amt;
    }

    // Debit Memo Information
    @Getter
    @Setter
    public static class DebitMemoInformation{
        private String dn_type;
        private String dn_ref_no;
        private String dn_cust_orn;
        private BigDecimal dn_amt;
        private String dn_desc;
        private String dn_branch;
        private String dn_coa1;
        private String dn_coa2;
        private String dn_acct;
        private String dn_sub_acct;
        private Integer dn_qty;
        private BigDecimal dn_unit_price;
        private BigDecimal dn_disc_amt;
    }

    // Item Information
    @Getter
    @Setter
    public static class ItemInformation{
    	public Integer cc_cs_item_id;
    	public Integer cc_case_id;
        private String coa1;
        private String coa2;
        private String sub_acct;
        private Integer qty;
        private BigDecimal unit_price;
        private BigDecimal disc_amt;
        private String txn_item_ref;
        private String txn_item_desc;
    	public LocalDateTime dt_created;
    	public LocalDateTime dt_modified;
    	public String created_by;
    	public String modified_by;
    	public String status;
    	
    	public ItemInformation() {}
    }
}
