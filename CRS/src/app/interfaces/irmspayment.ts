export interface Irmspayment {
    ss_cd: string;
    orn_no: string;
    orn_dt: string;
    cust_nm: string;
    cust_addr_1: string;
    cust_addr_2: string;
    cust_addr_3: string;
    cust_postcode: string;
    cust_city: string;
    cust_state: string;
    cust_email: string;
    cust_phone: string;
    total_amt: string;
    ss_return_url: string;
    ss_callback_url: string;
    email_flag: string;
    payment_item_details: PaymentItemDetail[];
}

export interface PaymentItemDetail {
    fee_detail_id: string;
    item_ref_no: string;
    item_desc: string;
    line_no: string;
    qty: string;
    unit_fee: string;
    gross_amt: string;
    grant_cd: string;
    disc_amt: string;
    tax_pct: string;
    tax_amt: string;
    net_amt: string;
    entity_type: string;
    entity_no: string;
    entity_nm: string;
    cp_no: string;
    cp_tier: string;
    cp_tier_amt: string;
    cp_tier_disc_pct: string;
    dps_id: string;
    dps_task: string;
    pymt_case: string;
    location:string;
    lit_item_ref: string;
    txn_type: string;
    calender_yr: string;
  }
